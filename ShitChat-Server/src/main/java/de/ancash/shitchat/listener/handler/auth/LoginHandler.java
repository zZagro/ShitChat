package de.ancash.shitchat.listener.handler.auth;

import de.ancash.shitchat.ShitChatServer;
import de.ancash.shitchat.packet.auth.LoginPacket;
import de.ancash.sockets.async.client.AbstractAsyncClient;

public class LoginHandler {

	private final ShitChatServer server;
	
	public LoginHandler(ShitChatServer server) {
		this.server = server;
	}
	
	public void login(AbstractAsyncClient cl, LoginPacket login) {
		
	}
}
