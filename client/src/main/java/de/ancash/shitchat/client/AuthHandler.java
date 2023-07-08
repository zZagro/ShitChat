package de.ancash.shitchat.client;

import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import de.ancash.shitchat.ShitChatPlaceholder;
import de.ancash.shitchat.client.ShitChatClient.State;
import de.ancash.shitchat.packet.auth.AuthFailedPacket;
import de.ancash.shitchat.packet.auth.AuthResultPacket;
import de.ancash.shitchat.packet.auth.AuthSuccessPacket;
import de.ancash.shitchat.packet.auth.LoginPacket;
import de.ancash.shitchat.packet.auth.SignUpPacket;
import de.ancash.sockets.packet.PacketFuture;

public class AuthHandler {

	private final ShitChatClient client;

	public AuthHandler(ShitChatClient client) {
		this.client = client;
	}

	@SuppressWarnings("nls")
	public Future<Optional<String>> login(String email, byte[] pass) {
		if (client.state != State.CONNECTING || !client.isConnected()) {
			client.onAuthenticationFailed(ShitChatPlaceholder.NOT_CONNECTED);
			return client.pool.submit(() -> Optional.of(ShitChatPlaceholder.NOT_CONNECTED));
		}
		client.state = State.AUTHENTICATING;
		client.email = email;
		client.logger.info("login");
		return client.pool.submit(() -> authenticate(client.sendShitChatPacket0(new LoginPacket(email, pass), true)));
	}

	@SuppressWarnings("nls")
	public Future<Optional<String>> signUp(String email, byte[] pass, String user) {
		if (client.state != State.CONNECTING || !client.isConnected()) {
			client.onAuthenticationFailed(ShitChatPlaceholder.NOT_CONNECTED);
			return client.pool.submit(() -> Optional.of(ShitChatPlaceholder.NOT_CONNECTED));
		}
		client.state = State.AUTHENTICATING;
		client.email = email;
		client.logger.info("sign up");
		return client.pool
				.submit(() -> authenticate(client.sendShitChatPacket0(new SignUpPacket(email, pass, user), true)));
	}

	@SuppressWarnings("nls")
	private Optional<String> authenticate(PacketFuture future) {
		Optional<AuthResultPacket> opt = null;
		opt = future.get(30, TimeUnit.SECONDS);

		if (!opt.isPresent()) {
			client.client.onDisconnect(new IllegalStateException("could not authorize"));
			client.onAuthenticationFailed(ShitChatPlaceholder.INTERNAL_ERROR);
			return Optional.of(ShitChatPlaceholder.INTERNAL_ERROR);
		}
		System.out.println((System.nanoTime() - future.getTimestamp()) / 1000D + " micros");
		AuthResultPacket result = (AuthResultPacket) opt.get();
		if (result instanceof AuthSuccessPacket) {
			onAuthSuccess((AuthSuccessPacket) result);
			client.onUserUpdated();
			return Optional.empty();
		} else {
			client.email = null;
			client.state = State.CONNECTING;
			client.onAuthenticationFailed(((AuthFailedPacket) opt.get()).getReason());
			return Optional.of(((AuthFailedPacket) opt.get()).getReason());
		}
	}

	private void onAuthSuccess(AuthSuccessPacket packet) {
		client.sid = packet.getSID();
		client.user = packet.getUser();
		client.state = State.CONNECTED;
		client.logUser();
		client.onAuthSuccess();
	}
}
