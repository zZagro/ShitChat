package de.ancash.shitchat.listener;

import de.ancash.libs.org.bukkit.event.EventHandler;
import de.ancash.libs.org.bukkit.event.Listener;
import de.ancash.shitchat.ShitChatServer;
import de.ancash.shitchat.listener.handler.auth.LoginHandler;
import de.ancash.shitchat.listener.handler.auth.SignInHandler;
import de.ancash.shitchat.packet.SessionedShitChatPacket;
import de.ancash.shitchat.packet.ShitChatPacket;
import de.ancash.shitchat.packet.auth.AuthenticationPacket;
import de.ancash.shitchat.packet.auth.LoginPacket;
import de.ancash.shitchat.packet.auth.SignInPacket;
import de.ancash.sockets.async.client.AbstractAsyncClient;
import de.ancash.sockets.events.ServerPacketReceiveEvent;
import de.ancash.sockets.packet.Packet;

public class ShitChatPacketListener implements Listener {

	private final ShitChatServer server;
	private final LoginHandler loginHandler;
	private final SignInHandler signInHandler;

	public ShitChatPacketListener(ShitChatServer server) {
		this.server = server;
		loginHandler = new LoginHandler(server);
		this.signInHandler = new SignInHandler(this.server);
	}

	@EventHandler
	public void onPacket(ServerPacketReceiveEvent event) {
		Packet p = event.getPacket();
		if (!(p instanceof ShitChatPacket))
			return;
		ShitChatPacket scp = (ShitChatPacket) p;

		if (scp instanceof AuthenticationPacket) {
			handleAuthPacket(event.getClient(), (AuthenticationPacket) scp);
		} else if (scp instanceof SessionedShitChatPacket) {
			handleSessionesPacket(event.getClient(), (SessionedShitChatPacket) scp);
		} else
			throw new IllegalStateException("unhandled packet of type " + scp.getClass());

	}

	private void handleAuthPacket(AbstractAsyncClient client, AuthenticationPacket scp) {
		if (scp instanceof LoginPacket)
			loginHandler.login(client, (LoginPacket) scp);
		else if (scp instanceof SignInPacket)
			signInHandler.signIn(client, (SignInPacket) scp);
	}

	private void handleSessionesPacket(AbstractAsyncClient client, SessionedShitChatPacket scp) {

	}
}
