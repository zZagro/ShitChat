package de.ancash.shitchat.server.listener.handler.auth;

import java.io.IOException;
import java.util.Arrays;

import de.ancash.misc.ReflectionUtils;
import de.ancash.shitchat.ShitChatPlaceholder;
import de.ancash.shitchat.packet.auth.AuthFailedPacket;
import de.ancash.shitchat.packet.auth.AuthSuccessPacket;
import de.ancash.shitchat.packet.auth.LoginPacket;
import de.ancash.shitchat.server.account.Account;
import de.ancash.shitchat.server.account.AccountRegistry;
import de.ancash.shitchat.user.User;
import de.ancash.sockets.async.client.AbstractAsyncClient;
import de.ancash.sockets.packet.Packet;

public class LoginHandler {

	private final AccountRegistry registry;

	public LoginHandler(AccountRegistry registry) {
		this.registry = registry;
	}

	@SuppressWarnings("nls")
	public void login(AbstractAsyncClient cl, LoginPacket login, Packet packet) {
		System.out.println("login: " + ReflectionUtils.toStringRec(login, true));
		if (!registry.exists(login.getEmail())) {
			packet.setSerializable(new AuthFailedPacket(ShitChatPlaceholder.ACCOUNT_NONEXISTENT));
			cl.putWrite(packet.toBytes());
			return;
		}
		Account acc = registry.getAccount(registry.getUIdByEmail(login.getEmail()));
		if (!Arrays.equals(acc.getPassword(), login.getPassword())) {
			packet.setSerializable(new AuthFailedPacket(ShitChatPlaceholder.WRONG_PASSWORD));
			cl.putWrite(packet.toBytes());
			return;
		}
		try {
			User user = acc.toUser();
			packet.setSerializable(new AuthSuccessPacket(registry.newSession(acc).getSessionId(), user));
			cl.putWrite(packet.toBytes());
		} catch (IOException e) {
			System.err.println("Could not get User of " + login.getEmail());
			e.printStackTrace();
			packet.setSerializable(new AuthFailedPacket(ShitChatPlaceholder.INTERNAL_ERROR));
			cl.putWrite(packet.toBytes());
			return;
		}
	}
}
