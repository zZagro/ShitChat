package de.ancash.shitchat.server.listener.handler.profile;

import de.ancash.shitchat.packet.profile.UserNameChangePacket;
import de.ancash.shitchat.server.ShitChatServer;
import de.ancash.sockets.async.client.AbstractAsyncClient;

public class UserNameChangeHandler {

	private final ShitChatServer server;

	public UserNameChangeHandler(ShitChatServer server) {
		this.server = server;
	}

	public void changeProfilePic(AbstractAsyncClient client, UserNameChangePacket packet) {

	}
}
