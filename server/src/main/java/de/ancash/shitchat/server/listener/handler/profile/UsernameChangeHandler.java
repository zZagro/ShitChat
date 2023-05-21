package de.ancash.shitchat.server.listener.handler.profile;

import de.ancash.shitchat.ShitChatPlaceholder;
import de.ancash.shitchat.packet.profile.UsernameChangePacket;
import de.ancash.shitchat.packet.profile.UsernameChangeResultPacket;
import de.ancash.shitchat.server.account.Account;
import de.ancash.shitchat.server.account.AccountRegistry;
import de.ancash.shitchat.server.client.Client;
import de.ancash.sockets.packet.Packet;

public class UsernameChangeHandler {

	private final AccountRegistry registry;

	public UsernameChangeHandler(AccountRegistry registry) {
		this.registry = registry;
	}

	public void changeUsername(Client client, UsernameChangePacket ucp, Packet packet) {
		System.out.println("user name change packet!");
		if (!client.getSID().equals(ucp.getSessionId()) || !registry.isSessionValid(ucp.getSessionId()))
			packet.setSerializable(
					new UsernameChangeResultPacket(ucp.getSessionId(), null, ShitChatPlaceholder.INVALID_SESSION));
		else if (registry.isUsernameUsed(ucp.getNewUserName()))
			packet.setSerializable(new UsernameChangeResultPacket(ucp.getSessionId(), null,
					ShitChatPlaceholder.USERNAME_ALREADY_EXISTS));
		else {
			Account acc = registry.updateUsername(ucp.getSessionId(), ucp.getNewUserName());
			if (acc == null) {
				packet.setSerializable(
						new UsernameChangeResultPacket(ucp.getSessionId(), null, ShitChatPlaceholder.INTERNAL_ERROR));
			} else {
				packet.setSerializable(new UsernameChangeResultPacket(ucp.getSessionId(), acc.toUser(), null));
			}
		}
		client.putWrite(packet.toBytes());
		System.out.println(packet.getTimeStamp());
		return;
	}
}
