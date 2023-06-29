package de.ancash.shitchat.server.listener.handler;

import de.ancash.shitchat.ShitChatPlaceholder;
import de.ancash.shitchat.packet.SessionedPacket;
import de.ancash.shitchat.packet.profile.ProfileChangeResultPacket;
import de.ancash.shitchat.server.account.AccountRegistry;
import de.ancash.shitchat.server.client.Client;
import de.ancash.sockets.packet.Packet;

public class HandlerUtil {

	@SuppressWarnings("nls")
	public static boolean validateSID(AccountRegistry registry, Client client, SessionedPacket sp, Packet packet) {
		if (!client.getSID().equals(sp.getSessionId()) || !registry.isSessionValid(sp.getSessionId())) {
			packet.setSerializable(
					new ProfileChangeResultPacket(sp.getSessionId(), null, ShitChatPlaceholder.INVALID_SESSION));
			client.putWrite(packet.toBytes());
			System.out.println("invalid sid: " + client.getRemoteAddress());
			return false;
		}
		return true;
	}

}
