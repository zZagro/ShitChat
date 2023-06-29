package de.ancash.shitchat.server.listener.handler.auth;

import java.util.Arrays;

import de.ancash.misc.ReflectionUtils;
import de.ancash.shitchat.ShitChatPlaceholder;
import de.ancash.shitchat.packet.auth.AuthFailedPacket;
import de.ancash.shitchat.packet.auth.AuthSuccessPacket;
import de.ancash.shitchat.packet.auth.LoginPacket;
import de.ancash.shitchat.server.account.Account;
import de.ancash.shitchat.server.account.AccountRegistry;
import de.ancash.shitchat.server.account.Session;
import de.ancash.shitchat.server.client.Client;
import de.ancash.shitchat.user.FullUser;
import de.ancash.sockets.packet.Packet;

public class LoginHandler {

	private final AccountRegistry registry;

	public LoginHandler(AccountRegistry registry) {
		this.registry = registry;
	}

	@SuppressWarnings("nls")
	public void login(Client cl, LoginPacket login, Packet packet) {
		System.out.println("login: " + ReflectionUtils.toStringRec(login, true));
		if (cl.getSID() != null && registry.isSessionValid(cl.getSID())) {
			System.out.println(cl.getRemoteAddress() + " tried to login with valid session");
			packet.setSerializable(new AuthFailedPacket(ShitChatPlaceholder.INVALID_SESSION));
		} else if (!registry.isEmailUsed(login.getEmail())) {
			System.out.println(cl.getRemoteAddress() + " tried to login with invalid email");
			packet.setSerializable(new AuthFailedPacket(ShitChatPlaceholder.ACCOUNT_NONEXISTENT));
		} else {
			Account acc = registry.getAccount(registry.getUIdByEmail(login.getEmail()));
			if (!Arrays.equals(acc.getPassword(), login.getPassword())) {
				System.out.println(cl.getRemoteAddress() + " tried to login with wrong pwd");
				packet.setSerializable(new AuthFailedPacket(ShitChatPlaceholder.WRONG_PASSWORD));
			} else {
				FullUser user = acc.toFullUser(registry);
				Session s = registry.newSession(acc);
				packet.setSerializable(new AuthSuccessPacket(s.getSessionId(), user));
				cl.setSID(s.getSessionId());
				System.out.println(cl.getRemoteAddress() + " login successful");
			}
		}
		cl.putWrite(packet.toBytes());
	}
}
