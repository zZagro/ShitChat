package de.ancash.shitchat.listener.handler.profile;

import de.ancash.shitchat.ShitChatServer;
import de.ancash.shitchat.packet.profile.UserNameChangePacket;
import de.ancash.sockets.async.client.AbstractAsyncClient;

public class UserNameChangeHandler {

	private final ShitChatServer server;
	
	public UserNameChangeHandler(ShitChatServer server) {
		this.server = server;
	}
	
	public void changeProfilePic(AbstractAsyncClient client, UserNameChangePacket packet) {
		
	}
}
