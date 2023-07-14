package de.ancash.shitchat.server.listener.handler.user;

import de.ancash.shitchat.ShitChatPlaceholder;
import de.ancash.shitchat.packet.profile.ProfileChangeResultPacket;
import de.ancash.shitchat.packet.user.RequestPacket;
import de.ancash.shitchat.packet.user.RequestReceivedPacket;
import de.ancash.shitchat.packet.user.RequestResultPacket;
import de.ancash.shitchat.packet.user.RequestType;
import de.ancash.shitchat.server.account.Account;
import de.ancash.shitchat.server.account.AccountRegistry;
import de.ancash.shitchat.server.channel.ChannelRegistry;
import de.ancash.shitchat.server.client.Client;
import de.ancash.shitchat.server.listener.handler.HandlerUtil;
import de.ancash.sockets.packet.Packet;

public class RequestHandler {

	private final AccountRegistry registry;
	private final ChannelRegistry cr;

	public RequestHandler(AccountRegistry registry, ChannelRegistry cr) {
		this.registry = registry;
		this.cr = cr;
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
					rp.getType(), acc.toFullUser(registry, cr)));
		} else {
			Account target = registry.getAccount(rp.getTarget());
			if (acc.isIncomingReq(rp.getTarget(), rp.getType())) {
				acc.removeIncomingReq(rp.getTarget(), rp.getType());
				acc.addAcceptedReq(rp.getTarget(), rp.getType());
				target.removeOutgoingReq(acc.getUserId(), rp.getType());
				target.addAcceptedReq(acc.getUserId(), rp.getType());

				System.out.println(rp.getType() + " req match " + acc.getUserId() + " <=> " + rp.getTarget());
				if (rp.getType() == RequestType.MESSAGE) {
					System.out.println("opening direct channel for " + acc.getUserId() + " <=> " + rp.getTarget());
					cr.createDirectChannel(acc, target);
				}

				target.getAllSessions().forEach(s -> s.sendPacket(new RequestReceivedPacket(s.getSessionId(),
						acc.toUser(), target.toFullUser(registry, cr), rp.getType())));
				acc.getAllSessions().stream().filter(s -> !s.getSessionId().equals(rp.getSessionId()))
						.forEach(s -> s.sendPacket(
								new ProfileChangeResultPacket(s.getSessionId(), acc.toFullUser(registry, cr), null)));
				packet.setSerializable(
						new RequestResultPacket(rp.getSessionId(), null, rp.getType(), acc.toFullUser(registry, cr)));

			} else if (acc.isOutgoingReq(rp.getTarget(), rp.getType()))
				packet.setSerializable(new RequestResultPacket(rp.getSessionId(),
						ShitChatPlaceholder.REQUEST_ALREADY_EXISTS, rp.getType(), acc.toFullUser(registry, cr)));

			else if (acc.isAcceptedReq(rp.getTarget(), rp.getType()))
				packet.setSerializable(new RequestResultPacket(rp.getSessionId(),
						ShitChatPlaceholder.REQUEST_ALREADY_ACCEPTED, rp.getType(), acc.toFullUser(registry, cr)));

			else {
				if (!acc.addOutgoingReq(rp.getTarget(), rp.getType())
						|| !target.addIncomingReq(acc.getUserId(), rp.getType())) {
					packet.setSerializable(new RequestResultPacket(rp.getSessionId(),
							ShitChatPlaceholder.INTERNAL_ERROR, rp.getType(), acc.toFullUser(registry, cr)));
					acc.removeOutgoingReq(rp.getTarget(), rp.getType());
				} else {
					System.out.println(rp.getType() + " req " + acc.getUserId() + " => " + rp.getTarget());
					target.getAllSessions().forEach(s -> s.sendPacket(new RequestReceivedPacket(s.getSessionId(),
							acc.toUser(), target.toFullUser(registry, cr), rp.getType())));
					acc.getAllSessions().stream().filter(s -> !s.getSessionId().equals(rp.getSessionId()))
							.forEach(s -> s.sendPacket(new ProfileChangeResultPacket(s.getSessionId(),
									acc.toFullUser(registry, cr), null)));
					packet.setSerializable(new RequestResultPacket(rp.getSessionId(), null, rp.getType(),
							acc.toFullUser(registry, cr)));
				}
			}
		}

		cl.putWrite(packet.toBytes());
	}
}
