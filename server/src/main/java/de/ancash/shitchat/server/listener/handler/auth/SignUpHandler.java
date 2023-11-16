package de.ancash.shitchat.server.listener.handler.auth;

import de.ancash.shitchat.ShitChatPlaceholder;
import de.ancash.shitchat.packet.auth.AuthFailedPacket;
import de.ancash.shitchat.packet.auth.AuthSuccessPacket;
import de.ancash.shitchat.packet.auth.SignUpPacket;
import de.ancash.shitchat.server.account.Account;
import de.ancash.shitchat.server.account.AccountRegistry;
import de.ancash.shitchat.server.account.Session;
import de.ancash.shitchat.server.channel.ChannelRegistry;
import de.ancash.shitchat.server.client.Client;
import de.ancash.shitchat.user.FullUser;
import de.ancash.sockets.packet.Packet;

public class SignUpHandler {

	private final AccountRegistry registry;
	private final ChannelRegistry cr;

	public SignUpHandler(AccountRegistry registry, ChannelRegistry cr) {
		this.registry = registry;
		this.cr = cr;
	}

	@SuppressWarnings("nls")
	public void signUp(Client cl, SignUpPacket sip, Packet packet) {
		if (cl.getSID() != null && registry.isSessionValid(cl.getSID())) {
			System.out.println(cl.getRemoteAddress() + " signup already valid session");
			packet.setSerializable(new AuthFailedPacket(ShitChatPlaceholder.INVALID_SESSION));
		} else if (!sip.isUsernameValid()) {
			System.out.println(cl.getRemoteAddress() + " signup invalid username");
			packet.setSerializable(new AuthFailedPacket(ShitChatPlaceholder.INVALID_USERNAME));
		} else if (registry.isEmailUsed(sip.getEmail())) {
			System.out.println(cl.getRemoteAddress() + " signup email already used");
			packet.setSerializable(new AuthFailedPacket(ShitChatPlaceholder.ACCOUNT_ALREADY_EXISTS));
		} else if (registry.isUsernameUsed(sip.getUsername())) {
			System.out.println(cl.getRemoteAddress() + " signup username already used");
			packet.setSerializable(new AuthFailedPacket(ShitChatPlaceholder.USERNAME_ALREADY_EXISTS));
		} else {
			Account acc = registry.createAccount(sip.getEmail(), sip.getUsername(), sip.getPassword());
			if (acc == null) {
				System.out.println(cl.getRemoteAddress() + " signup internal error");
				packet.setSerializable(new AuthFailedPacket(ShitChatPlaceholder.INTERNAL_ERROR));
				cl.putWrite(packet.toBytes());
				return;
			}
			FullUser user = acc.toFullUser(registry, cr);
			Session s = registry.newSession(acc, cl);
			packet.setSerializable(new AuthSuccessPacket(s.getSessionId(), user));
			cl.setSID(s.getSessionId());
			System.out.println(
					cl.getRemoteAddress() + " signup successful: " + user.getUsername() + "(" + user.getUserId() + ")");
		}
		cl.putWrite(packet.toBytes());
	}
}
