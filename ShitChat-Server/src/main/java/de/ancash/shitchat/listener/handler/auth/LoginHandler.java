package de.ancash.shitchat.listener.handler.auth;

import java.io.IOException;
import java.util.Arrays;

import de.ancash.shitchat.ShitChatPlaceholder;
import de.ancash.shitchat.account.Account;
import de.ancash.shitchat.account.AccountRegistry;
import de.ancash.shitchat.packet.auth.AuthFailedPacket;
import de.ancash.shitchat.packet.auth.AuthSuccessPacket;
import de.ancash.shitchat.packet.auth.LoginPacket;
import de.ancash.shitchat.user.User;
import de.ancash.sockets.async.client.AbstractAsyncClient;

public class LoginHandler {

	private final AccountRegistry registry;

	public LoginHandler(AccountRegistry registry) {
		this.registry = registry;
	}

	@SuppressWarnings("nls")
	public void login(AbstractAsyncClient cl, LoginPacket login) {
		if (!registry.exists(login.getEmail())) {
			cl.putWrite(new AuthFailedPacket(ShitChatPlaceholder.ACCOUNT_NONEXISTENT).toBytes());
			return;
		}
		Account acc = registry.getAccount(registry.getUIdByEmail(login.getEmail()));
		if (!Arrays.equals(acc.getPassword(), login.getPassword())) {
			cl.putWrite(new AuthFailedPacket(ShitChatPlaceholder.WRONG_PASSWORD).toBytes());
			return;
		}
		try {
			User user = acc.toUser();
			cl.putWrite(new AuthSuccessPacket(registry.newSession(acc).getSessionId(), user).toBytes());
		} catch (IOException e) {
			System.err.println("Could not get User of " + login.getEmail());
			e.printStackTrace();
			cl.putWrite(new AuthFailedPacket(ShitChatPlaceholder.INTERNAL_ERROR).toBytes());
			return;
		}
	}
}
