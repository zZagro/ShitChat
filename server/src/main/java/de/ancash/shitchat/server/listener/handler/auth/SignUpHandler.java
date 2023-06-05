package de.ancash.shitchat.server.listener.handler.auth;

import de.ancash.misc.ReflectionUtils;
import de.ancash.shitchat.ShitChatPlaceholder;
import de.ancash.shitchat.packet.auth.AuthFailedPacket;
import de.ancash.shitchat.packet.auth.AuthSuccessPacket;
import de.ancash.shitchat.packet.auth.SignUpPacket;
import de.ancash.shitchat.server.account.Account;
import de.ancash.shitchat.server.account.AccountRegistry;
import de.ancash.shitchat.server.account.Session;
import de.ancash.shitchat.server.client.Client;
import de.ancash.shitchat.user.User;
import de.ancash.sockets.packet.Packet;

public class SignUpHandler {

	private final AccountRegistry registry;

	public SignUpHandler(AccountRegistry registry) {
		this.registry = registry;
	}

	@SuppressWarnings("nls")
	public void signUp(Client cl, SignUpPacket sip, Packet packet) {
		System.out.println("signin: " + ReflectionUtils.toStringRec(packet, true));
		if (cl.getSID() != null && registry.isSessionValid(cl.getSID())) {
			packet.setSerializable(new AuthFailedPacket(ShitChatPlaceholder.INVALID_SESSION));
		} else if (!sip.isUsernameValid()) {
			packet.setSerializable(new AuthFailedPacket(ShitChatPlaceholder.INVALID_USERNAME));
		} else if (registry.isEmailUsed(sip.getEmail())) {
			packet.setSerializable(new AuthFailedPacket(ShitChatPlaceholder.ACCOUNT_ALREADY_EXISTS));
		} else if (registry.isUsernameUsed(sip.getUsername())) {
			packet.setSerializable(new AuthFailedPacket(ShitChatPlaceholder.USERNAME_ALREADY_EXISTS));
		} else {
			Account acc = registry.createAccount(sip.getEmail(), sip.getUsername(), sip.getPassword());
			if (acc == null) {
				packet.setSerializable(new AuthFailedPacket(ShitChatPlaceholder.INTERNAL_ERROR));
				cl.putWrite(packet.toBytes());
				return;
			}
			User user = acc.toUser();
			Session s = registry.newSession(acc);
			packet.setSerializable(new AuthSuccessPacket(s.getSessionId(), user));
			cl.setSID(s.getSessionId());
		}
		cl.putWrite(packet.toBytes());
	}
}
