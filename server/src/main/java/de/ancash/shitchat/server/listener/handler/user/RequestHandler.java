package de.ancash.shitchat.server.listener.handler.user;

import de.ancash.shitchat.ShitChatPlaceholder;
import de.ancash.shitchat.packet.profile.ProfileChangeResultPacket;
import de.ancash.shitchat.packet.user.RequestPacket;
import de.ancash.shitchat.packet.user.RequestReceivedPacket;
import de.ancash.shitchat.packet.user.RequestResultPacket;
import de.ancash.shitchat.server.account.Account;
import de.ancash.shitchat.server.account.AccountRegistry;
import de.ancash.shitchat.server.client.Client;
import de.ancash.shitchat.server.listener.handler.HandlerUtil;
import de.ancash.sockets.packet.Packet;

public class RequestHandler {

	private final AccountRegistry registry;

	public RequestHandler(AccountRegistry registry) {
		this.registry = registry;
	}

	@SuppressWarnings("nls")
	public synchronized void onRequest(Client cl, RequestPacket rp, Packet packet) {
		if (!HandlerUtil.validateSID(registry, cl, rp, packet)) {
			System.out.println(cl.getRemoteAddress() + " change username invalid sid");
			packet.setSerializable(new RequestResultPacket(rp.getSessionId(), ShitChatPlaceholder.INVALID_SESSION,
					rp.getType(), null));
			cl.putWrite(packet.toBytes());
			return;
		}
		Account acc = registry.getSession(rp.getSessionId()).getAccount();
		if (!registry.isUIDValid(rp.getTarget())) {
			packet.setSerializable(new RequestResultPacket(rp.getSessionId(), ShitChatPlaceholder.ACCOUNT_NONEXISTENT,
					rp.getType(), acc.toFullUser(registry)));
		} else {
			Account target = registry.getAccount(rp.getTarget());
			if (acc.isIncomingReq(rp.getTarget(), rp.getType())) {
				acc.removeIncomingReq(rp.getTarget(), rp.getType());
				acc.addAcceptedReq(rp.getTarget(), rp.getType());
				target.removeOutgoingReq(acc.getId(), rp.getType());
				target.addAcceptedReq(acc.getId(), rp.getType());
				target.getAllSessions().forEach(s -> s.sendPacket(new RequestReceivedPacket(s.getSessionId(),
						acc.toUser(), target.toFullUser(registry), rp.getType())));
				acc.getAllSessions().stream().filter(s -> !s.getSessionId().equals(rp.getSessionId())).forEach(s -> s
						.sendPacket(new ProfileChangeResultPacket(s.getSessionId(), acc.toFullUser(registry), null)));
				packet.setSerializable(
						new RequestResultPacket(rp.getSessionId(), null, rp.getType(), acc.toFullUser(registry)));
				System.out.println(rp.getType() + " req match " + acc.getId() + " <=> " + rp.getTarget());

			} else if (acc.isOutgoingReq(rp.getTarget(), rp.getType()))
				packet.setSerializable(new RequestResultPacket(rp.getSessionId(),
						ShitChatPlaceholder.REQUEST_ALREADY_EXISTS, rp.getType(), acc.toFullUser(registry)));

			else if (acc.isAcceptedReq(rp.getTarget(), rp.getType()))
				packet.setSerializable(new RequestResultPacket(rp.getSessionId(),
						ShitChatPlaceholder.REQUEST_ALREADY_ACCEPTED, rp.getType(), acc.toFullUser(registry)));

			else {
				if (!acc.addOutgoingReq(rp.getTarget(), rp.getType())
						|| !target.addIncomingReq(acc.getId(), rp.getType())) {
					packet.setSerializable(new RequestResultPacket(rp.getSessionId(),
							ShitChatPlaceholder.INTERNAL_ERROR, rp.getType(), acc.toFullUser(registry)));
					acc.removeOutgoingReq(rp.getTarget(), rp.getType());
				} else {
					System.out.println(rp.getType() + " req " + acc.getId() + " => " + rp.getTarget());
					target.getAllSessions().forEach(s -> s.sendPacket(new RequestReceivedPacket(s.getSessionId(),
							acc.toUser(), target.toFullUser(registry), rp.getType())));
					acc.getAllSessions().stream().filter(s -> !s.getSessionId().equals(rp.getSessionId()))
							.forEach(s -> s.sendPacket(
									new ProfileChangeResultPacket(s.getSessionId(), acc.toFullUser(registry), null)));
					packet.setSerializable(
							new RequestResultPacket(rp.getSessionId(), null, rp.getType(), acc.toFullUser(registry)));
				}
			}
		}

		cl.putWrite(packet.toBytes());
	}
}
