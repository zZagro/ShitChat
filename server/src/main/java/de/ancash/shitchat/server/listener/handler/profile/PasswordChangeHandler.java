package de.ancash.shitchat.server.listener.handler.profile;

import java.io.IOException;
import java.util.Arrays;

import de.ancash.shitchat.ShitChatPlaceholder;
import de.ancash.shitchat.packet.profile.PasswordChangePacket;
import de.ancash.shitchat.packet.profile.ProfileChangeResultPacket;
import de.ancash.shitchat.server.account.Account;
import de.ancash.shitchat.server.account.AccountRegistry;
import de.ancash.shitchat.server.client.Client;
import de.ancash.shitchat.server.listener.handler.HandlerUtil;
import de.ancash.sockets.packet.Packet;

public class PasswordChangeHandler {

	private final AccountRegistry registry;

	public PasswordChangeHandler(AccountRegistry registry) {
		this.registry = registry;
	}

	@SuppressWarnings("nls")
	public void changePassword(Client cl, PasswordChangePacket ucp, Packet packet) {
		if (!HandlerUtil.validateSID(registry, cl, ucp, packet)) {
			System.out.println(cl.getRemoteAddress() + " change pwd invalid sid");
			packet.setSerializable(
					new ProfileChangeResultPacket(ucp.getSessionId(), null, ShitChatPlaceholder.INVALID_SESSION));
			cl.putWrite(packet.toBytes());
			return;
		}
		Account acc = registry.getSession(ucp.getSessionId()).getAccount();
		if (acc == null) {
			System.out.println(cl.getRemoteAddress() + " change pwd internal error");
			packet.setSerializable(
					new ProfileChangeResultPacket(ucp.getSessionId(), null, ShitChatPlaceholder.INTERNAL_ERROR));
		} else {
			byte[] realOld = acc.getPassword();
			if (!Arrays.equals(realOld, ucp.getOldPass()) || ucp.getNewPass().length != 32) {
				System.out.println(cl.getRemoteAddress() + " change pwd wrong password");
				packet.setSerializable(
						new ProfileChangeResultPacket(ucp.getSessionId(), null, ShitChatPlaceholder.WRONG_PASSWORD));
			} else {
				try {
					acc.setPassword(ucp.getNewPass());
					packet.setSerializable(
							new ProfileChangeResultPacket(ucp.getSessionId(), acc.toFullUser(registry), null));
					System.out.println(cl.getRemoteAddress() + " change pwd successful");
				} catch (IOException e) {
					packet.setSerializable(new ProfileChangeResultPacket(ucp.getSessionId(), null,
							ShitChatPlaceholder.INTERNAL_ERROR));
					System.err.println("could not change password of " + acc.getUserId());
					e.printStackTrace();
				}
			}
		}
		cl.putWrite(packet.toBytes());
	}
}
