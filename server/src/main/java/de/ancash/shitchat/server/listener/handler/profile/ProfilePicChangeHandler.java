package de.ancash.shitchat.server.listener.handler.profile;

import de.ancash.shitchat.packet.profile.ProfilePicChangePacket;
import de.ancash.shitchat.server.ShitChatServer;
import de.ancash.sockets.async.client.AbstractAsyncClient;

public class ProfilePicChangeHandler {

	private final ShitChatServer server;

	public ProfilePicChangeHandler(ShitChatServer server) {
		this.server = server;
	}

	public void changeProfilePic(AbstractAsyncClient client, ProfilePicChangePacket packet) {

	}
}
