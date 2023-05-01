package de.ancash.shitchat.listener.handler.auth;

import de.ancash.shitchat.ShitChatServer;
import de.ancash.shitchat.packet.auth.SignInPacket;
import de.ancash.sockets.async.client.AbstractAsyncClient;

public class SignInHandler {
	
	private final ShitChatServer server;
	
	public SignInHandler(ShitChatServer server) {
		this.server = server;
	}
	
	public void signIn(AbstractAsyncClient client, SignInPacket packet) {
		
	}
}
