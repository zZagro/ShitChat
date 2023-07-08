package de.ancash.shitchat.client;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import de.ancash.shitchat.ShitChatPlaceholder;
import de.ancash.shitchat.packet.user.RequestPacket;
import de.ancash.shitchat.packet.user.RequestResultPacket;
import de.ancash.shitchat.packet.user.RequestType;
import de.ancash.sockets.packet.PacketFuture;

public class RequestHandler {

	private final ShitChatClient client;

	public RequestHandler(ShitChatClient client) {
		this.client = client;
	}

	public Future<Optional<String>> sendRequest(UUID target, RequestType type) {
		if (!client.isAuthenticated()) {
			client.onPPChangeFailed(ShitChatPlaceholder.NOT_AUTHENTICATED);
			return client.pool.submit(() -> Optional.of(ShitChatPlaceholder.NOT_AUTHENTICATED));
		} else
			return client.pool.submit(() -> sendRequest(
					client.sendShitChatPacket0(new RequestPacket(client.sid, target, type), true), target, type));
	}

	private Optional<String> sendRequest(PacketFuture future, UUID target, RequestType type) {
		Optional<RequestResultPacket> result = future.get(30, TimeUnit.SECONDS);
		if (!result.isPresent()) {
			client.onPPChangeFailed(ShitChatPlaceholder.INTERNAL_ERROR);
			return Optional.of(ShitChatPlaceholder.INTERNAL_ERROR);
		}
		System.out.println((System.nanoTime() - future.getTimestamp()) / 1000D + " micros");
		RequestResultPacket r = result.get();
		if (r.wasSuccessful()) {
			client.user = r.getUser();
			client.onRequestSuccessful(target, type);
			return Optional.empty();
		}
		client.onRequestFailed(r.getReason(), target, type);
		return Optional.of(r.getReason());
	}
}
