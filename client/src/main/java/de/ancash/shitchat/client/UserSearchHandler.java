package de.ancash.shitchat.client;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import de.ancash.datastructures.tuples.Duplet;
import de.ancash.datastructures.tuples.Tuple;
import de.ancash.shitchat.ShitChatPlaceholder;
import de.ancash.shitchat.packet.user.SearchUserPacket;
import de.ancash.shitchat.packet.user.SearchUserResultPacket;
import de.ancash.shitchat.user.User;
import de.ancash.sockets.packet.PacketFuture;

public class UserSearchHandler {

	private final ShitChatClient client;

	public UserSearchHandler(ShitChatClient client) {
		this.client = client;
	}

	public Duplet<Optional<List<User>>, Optional<String>> searchUser(String name) {
		if (!client.isAuthenticated()) {
			client.onSearchUserFailed(ShitChatPlaceholder.NOT_AUTHENTICATED);
			return Tuple.of(Optional.empty(), Optional.of(ShitChatPlaceholder.NOT_AUTHENTICATED));
		}
		return searchUser(client.sendShitChatPacket0(new SearchUserPacket(client.sid, name), true));
	}

	private Duplet<Optional<List<User>>, Optional<String>> searchUser(PacketFuture future) {
		Optional<SearchUserResultPacket> result = future.get(30, TimeUnit.SECONDS);
		if (!result.isPresent()) {
			client.onSearchUserFailed(ShitChatPlaceholder.INTERNAL_ERROR);
			return Tuple.of(Optional.empty(), Optional.of(ShitChatPlaceholder.INTERNAL_ERROR));
		}
		SearchUserResultPacket surp = result.get();
		if (surp.wasSuccessful()) {
			client.onSearchUser(surp.getFoundUser());
			return Tuple.of(Optional.of(surp.getFoundUser()), Optional.empty());
		}
		client.onSearchUserFailed(surp.getReason());
		return Tuple.of(Optional.empty(), Optional.of(surp.getReason()));
	}
}
