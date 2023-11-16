package de.ancash.shitchat.server.listener.handler.profile;

import de.ancash.shitchat.ShitChatPlaceholder;
import de.ancash.shitchat.packet.profile.ProfileChangeResultPacket;
import de.ancash.shitchat.packet.profile.UsernameChangePacket;
import de.ancash.shitchat.server.account.Account;
import de.ancash.shitchat.server.account.AccountRegistry;
import de.ancash.shitchat.server.channel.ChannelRegistry;
import de.ancash.shitchat.server.client.Client;
import de.ancash.shitchat.server.listener.handler.HandlerUtil;
import de.ancash.sockets.packet.Packet;

public class UsernameChangeHandler {

	private final AccountRegistry registry;
	private final ChannelRegistry cr;

	public UsernameChangeHandler(AccountRegistry registry, ChannelRegistry cr) {
		this.registry = registry;
		this.cr = cr;
	}

	@SuppressWarnings("nls")
	public void changeUsername(Client cl, UsernameChangePacket ucp, Packet packet) {
		if (!HandlerUtil.validateSID(registry, cl, ucp, packet)) {
			System.out.println(cl.getRemoteAddress() + " change username invalid sid");
			packet.setSerializable(
					new ProfileChangeResultPacket(ucp.getSessionId(), null, ShitChatPlaceholder.INVALID_SESSION));
			cl.putWrite(packet.toBytes());
			return;
		} else if (registry.isUsernameUsed(ucp.getNewUserName())) {
			System.out.println(cl.getRemoteAddress() + " change username username already used");
			packet.setSerializable(new ProfileChangeResultPacket(ucp.getSessionId(), null,
					ShitChatPlaceholder.USERNAME_ALREADY_EXISTS));
		} else {
			Account acc = registry.updateUsername(ucp.getSessionId(), ucp.getNewUserName());
			if (acc == null) {
				System.out.println(cl.getRemoteAddress() + " change username internal error");
				packet.setSerializable(
						new ProfileChangeResultPacket(ucp.getSessionId(), null, ShitChatPlaceholder.INTERNAL_ERROR));
			} else {
				System.out.println(cl.getRemoteAddress() + " change username successful");
				packet.setSerializable(
						new ProfileChangeResultPacket(ucp.getSessionId(), acc.toFullUser(registry, cr), null));
			}
		}
		cl.putWrite(packet.toBytes());
	}
}
