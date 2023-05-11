package de.ancash.shitchat.server.listener.handler.auth;

import java.io.IOException;

import de.ancash.misc.ReflectionUtils;
import de.ancash.shitchat.ShitChatPlaceholder;
import de.ancash.shitchat.packet.auth.AuthFailedPacket;
import de.ancash.shitchat.packet.auth.AuthSuccessPacket;
import de.ancash.shitchat.packet.auth.SignInPacket;
import de.ancash.shitchat.server.account.Account;
import de.ancash.shitchat.server.account.AccountRegistry;
import de.ancash.shitchat.user.User;
import de.ancash.sockets.async.client.AbstractAsyncClient;
import de.ancash.sockets.packet.Packet;

public class SignInHandler {

	private final AccountRegistry registry;

	public SignInHandler(AccountRegistry registry) {
		this.registry = registry;
	}

	@SuppressWarnings("nls")
	public void signIn(AbstractAsyncClient cl, SignInPacket sip, Packet packet) {
		System.out.println("signin: " + ReflectionUtils.toStringRec(packet, true));
		if (registry.exists(sip.getEmail())) {
			packet.setSerializable(new AuthFailedPacket(ShitChatPlaceholder.ACCOUNT_ALREADY_EXISTS));
			cl.putWrite(packet.toBytes());
			return;
		}
		Account acc = registry.createAccount(sip.getEmail(), sip.getUserName(), sip.getPassword());
		if (acc == null) {
			packet.setSerializable(new AuthFailedPacket(ShitChatPlaceholder.INTERNAL_ERROR));
			cl.putWrite(packet.toBytes());
			return;
		}
		try {
			User user = acc.toUser();
			packet.setSerializable(new AuthSuccessPacket(registry.newSession(acc).getSessionId(), user));
			cl.putWrite(packet.toBytes());
		} catch (IOException e) {
			System.err.println("could not get User of " + sip.getEmail());
			e.printStackTrace();
			packet.setSerializable(new AuthFailedPacket(ShitChatPlaceholder.INTERNAL_ERROR));
			cl.putWrite(packet.toBytes());
			return;
		}
	}
}
