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

	@SuppressWarnings("nls")
	public void changeProfilePic(Client cl, ProfilePicChangePacket ppcp, Packet packet) {
		if (!HandlerUtil.validateSID(registry, cl, ppcp, packet)) {
			System.out.println(cl.getRemoteAddress() + " change pp invalid sid");
			packet.setSerializable(
					new ProfileChangeResultPacket(ppcp.getSessionId(), null, ShitChatPlaceholder.INVALID_SESSION));
			cl.putWrite(packet.toBytes());
			return;
		}
		Account acc = registry.updateProfilePic(ppcp.getSessionId(), ppcp.getImage().asBytes());
		if (acc == null) {
			System.out.println(cl.getRemoteAddress() + " change pp internal error");
			packet.setSerializable(
					new ProfileChangeResultPacket(ppcp.getSessionId(), null, ShitChatPlaceholder.INTERNAL_ERROR));
		} else {
			System.out.println(cl.getRemoteAddress() + " change pp successful");
			packet.setSerializable(new ProfileChangeResultPacket(ppcp.getSessionId(), acc.toFullUser(registry), null));
		}
		cl.putWrite(packet.toBytes());
	}
}
