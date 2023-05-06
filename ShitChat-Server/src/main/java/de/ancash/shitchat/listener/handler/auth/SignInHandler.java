package de.ancash.shitchat.listener.handler.auth;

import java.io.IOException;

import de.ancash.shitchat.ShitChatPlaceholder;
import de.ancash.shitchat.account.Account;
import de.ancash.shitchat.account.AccountRegistry;
import de.ancash.shitchat.packet.auth.AuthFailedPacket;
import de.ancash.shitchat.packet.auth.AuthSuccessPacket;
import de.ancash.shitchat.packet.auth.SignInPacket;
import de.ancash.shitchat.user.User;
import de.ancash.sockets.async.client.AbstractAsyncClient;

public class SignInHandler {

	private final AccountRegistry registry;

	public SignInHandler(AccountRegistry registry) {
		this.registry = registry;
	}

	@SuppressWarnings("nls")
	public void signIn(AbstractAsyncClient cl, SignInPacket packet) {
		if (registry.exists(packet.getEmail())) {
			cl.putWrite(new AuthFailedPacket(ShitChatPlaceholder.ACCOUNT_ALREADY_EXISTS).toBytes());
			return;
		}
		Account acc = registry.createAccount(packet.getEmail(), packet.getUserName(), packet.getPassword());
		if (acc == null) {
			cl.putWrite(new AuthFailedPacket(ShitChatPlaceholder.INTERNAL_ERROR).toBytes());
			return;
		}
		try {
			User user = acc.toUser();
			cl.putWrite(new AuthSuccessPacket(registry.newSession(acc).getSessionId(), user).toBytes());
		} catch (IOException e) {
			System.err.println("could not get User of " + packet.getEmail());
			e.printStackTrace();
			cl.putWrite(new AuthFailedPacket(ShitChatPlaceholder.INTERNAL_ERROR).toBytes());
			return;
		}
	}
}
