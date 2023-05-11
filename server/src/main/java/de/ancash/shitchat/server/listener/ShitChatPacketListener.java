package de.ancash.shitchat.server.listener;

import java.io.Serializable;

import de.ancash.libs.org.bukkit.event.EventHandler;
import de.ancash.libs.org.bukkit.event.Listener;
import de.ancash.shitchat.packet.SessionedPacket;
import de.ancash.shitchat.packet.ShitChatPacket;
import de.ancash.shitchat.packet.auth.AuthenticationPacket;
import de.ancash.shitchat.packet.auth.LoginPacket;
import de.ancash.shitchat.packet.auth.SignInPacket;
import de.ancash.shitchat.server.ShitChatServer;
import de.ancash.shitchat.server.listener.handler.auth.LoginHandler;
import de.ancash.shitchat.server.listener.handler.auth.SignInHandler;
import de.ancash.sockets.async.client.AbstractAsyncClient;
import de.ancash.sockets.events.ServerPacketReceiveEvent;
import de.ancash.sockets.packet.Packet;

public class ShitChatPacketListener implements Listener {

	@SuppressWarnings("unused")
	private final ShitChatServer server;
	private final LoginHandler loginHandler;
	private final SignInHandler signInHandler;

	public ShitChatPacketListener(ShitChatServer server) {
		this.server = server;
		loginHandler = new LoginHandler(server.getAccountRegistry());
		this.signInHandler = new SignInHandler(server.getAccountRegistry());
	}

	@SuppressWarnings("nls")
	@EventHandler
	public void onPacket(ServerPacketReceiveEvent event) {
		Packet p = event.getPacket();
		Serializable s = p.getSerializable();
		if (s == null || !(s instanceof ShitChatPacket))
			return;
		ShitChatPacket scp = (ShitChatPacket) s;

		if (scp instanceof AuthenticationPacket) {
			handleAuthPacket(event.getClient(), (AuthenticationPacket) scp, p);
		} else if (scp instanceof SessionedPacket) {
			handleSessionesPacket(event.getClient(), (SessionedPacket) scp);
		} else
			throw new IllegalStateException("unhandled packet of type " + scp.getClass());

	}

	private void handleAuthPacket(AbstractAsyncClient client, AuthenticationPacket scp, Packet packet) {
		if (scp instanceof LoginPacket)
			loginHandler.login(client, (LoginPacket) scp, packet);
		else if (scp instanceof SignInPacket)
			signInHandler.signIn(client, (SignInPacket) scp, packet);
	}

	private void handleSessionesPacket(AbstractAsyncClient client, SessionedPacket scp) {

	}
}
