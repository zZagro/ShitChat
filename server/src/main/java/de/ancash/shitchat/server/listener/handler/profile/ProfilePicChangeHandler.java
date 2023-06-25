package de.ancash.shitchat.server.listener.handler.profile;

import de.ancash.shitchat.ShitChatPlaceholder;
import de.ancash.shitchat.packet.profile.ProfileChangeResultPacket;
import de.ancash.shitchat.packet.profile.ProfilePicChangePacket;
import de.ancash.shitchat.server.account.Account;
import de.ancash.shitchat.server.account.AccountRegistry;
import de.ancash.shitchat.server.client.Client;
import de.ancash.shitchat.server.listener.handler.HandlerUtil;
import de.ancash.sockets.packet.Packet;

public class ProfilePicChangeHandler {

	private final AccountRegistry registry;

	public ProfilePicChangeHandler(AccountRegistry registry) {
		this.registry = registry;
	}

	public void changeProfilePic(Client client, ProfilePicChangePacket ppcp, Packet packet) {
		System.out.println("pp change packet: " + client.getRemoteAddress());
		if (!HandlerUtil.validateSID(registry, client, ppcp, packet))
			return;
		Account acc = registry.updateProfilePic(ppcp.getSessionId(), ppcp.getImage().asBytes());
		if (acc == null) {
			packet.setSerializable(
					new ProfileChangeResultPacket(ppcp.getSessionId(), null, ShitChatPlaceholder.INTERNAL_ERROR));
		} else {
			packet.setSerializable(new ProfileChangeResultPacket(ppcp.getSessionId(), acc.toUser(), null));
		}
		client.putWrite(packet.toBytes());
	}
}
