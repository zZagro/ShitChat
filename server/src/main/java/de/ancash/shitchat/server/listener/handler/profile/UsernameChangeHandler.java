package de.ancash.shitchat.server.listener.handler.profile;

import de.ancash.shitchat.ShitChatPlaceholder;
import de.ancash.shitchat.packet.profile.ProfileChangeResultPacket;
import de.ancash.shitchat.packet.profile.UsernameChangePacket;
import de.ancash.shitchat.server.account.Account;
import de.ancash.shitchat.server.account.AccountRegistry;
import de.ancash.shitchat.server.client.Client;
import de.ancash.shitchat.server.listener.handler.HandlerUtil;
import de.ancash.sockets.packet.Packet;

public class UsernameChangeHandler {

	private final AccountRegistry registry;

	public UsernameChangeHandler(AccountRegistry registry) {
		this.registry = registry;
	}

	@SuppressWarnings("nls")
	public void changeUsername(Client client, UsernameChangePacket ucp, Packet packet) {
		System.out.println("user name change packet: " + client.getRemoteAddress());
		if (!HandlerUtil.validateSID(registry, client, ucp, packet))
			return;
		else if (registry.isUsernameUsed(ucp.getNewUserName()))
			packet.setSerializable(new ProfileChangeResultPacket(ucp.getSessionId(), null,
					ShitChatPlaceholder.USERNAME_ALREADY_EXISTS));
		else {
			Account acc = registry.updateUsername(ucp.getSessionId(), ucp.getNewUserName());
			if (acc == null) {
				packet.setSerializable(
						new ProfileChangeResultPacket(ucp.getSessionId(), null, ShitChatPlaceholder.INTERNAL_ERROR));
			} else {
				packet.setSerializable(new ProfileChangeResultPacket(ucp.getSessionId(), acc.toUser(), null));
			}
		}
		client.putWrite(packet.toBytes());
	}
}
