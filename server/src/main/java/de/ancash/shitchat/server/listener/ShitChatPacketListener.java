package de.ancash.shitchat.server.listener;

import java.io.Serializable;

import de.ancash.libs.org.bukkit.event.EventHandler;
import de.ancash.libs.org.bukkit.event.Listener;
import de.ancash.shitchat.packet.SessionedPacket;
import de.ancash.shitchat.packet.ShitChatPacket;
import de.ancash.shitchat.packet.auth.AuthenticationPacket;
import de.ancash.shitchat.packet.auth.LoginPacket;
import de.ancash.shitchat.packet.auth.SignUpPacket;
import de.ancash.shitchat.packet.profile.PasswordChangePacket;
import de.ancash.shitchat.packet.profile.ProfilePicChangePacket;
import de.ancash.shitchat.packet.profile.UsernameChangePacket;
import de.ancash.shitchat.packet.user.RequestPacket;
import de.ancash.shitchat.packet.user.SearchUserPacket;
import de.ancash.shitchat.server.ShitChatServer;
import de.ancash.shitchat.server.client.Client;
import de.ancash.shitchat.server.listener.handler.auth.LoginHandler;
import de.ancash.shitchat.server.listener.handler.auth.SignUpHandler;
import de.ancash.shitchat.server.listener.handler.profile.PasswordChangeHandler;
import de.ancash.shitchat.server.listener.handler.profile.ProfilePicChangeHandler;
import de.ancash.shitchat.server.listener.handler.profile.UsernameChangeHandler;
import de.ancash.shitchat.server.listener.handler.user.RequestHandler;
import de.ancash.shitchat.server.listener.handler.user.SearchUserHandler;
import de.ancash.sockets.events.ServerPacketReceiveEvent;
import de.ancash.sockets.packet.Packet;

public class ShitChatPacketListener implements Listener {

	@SuppressWarnings("unused")
	private final ShitChatServer server;
	private final LoginHandler loginHandler;
	private final SignUpHandler signUpHandler;
	private final UsernameChangeHandler usernameChange;
	private final ProfilePicChangeHandler ppChange;
	private final PasswordChangeHandler pwdChange;
	private final SearchUserHandler searchUser;
	private final RequestHandler reqHandler;

	public ShitChatPacketListener(ShitChatServer server) {
		this.server = server;
		loginHandler = new LoginHandler(server.getAccountRegistry());
		this.signUpHandler = new SignUpHandler(server.getAccountRegistry());
		usernameChange = new UsernameChangeHandler(server.getAccountRegistry());
		ppChange = new ProfilePicChangeHandler(server.getAccountRegistry());
		pwdChange = new PasswordChangeHandler(server.getAccountRegistry());
		searchUser = new SearchUserHandler(server.getAccountRegistry());
		reqHandler = new RequestHandler(server.getAccountRegistry());
	}

	@SuppressWarnings("nls")
	@EventHandler
	public void onPacket(ServerPacketReceiveEvent event) {
		Packet p = event.getPacket();
		Serializable s = p.getSerializable();
		if (s == null || !(s instanceof ShitChatPacket))
			return;
		ShitChatPacket scp = (ShitChatPacket) s;
		Client client = (Client) event.getClient();
		if (scp instanceof AuthenticationPacket) {
			handleAuthPacket(client, (AuthenticationPacket) scp, p);
		} else if (scp instanceof SessionedPacket) {
			handleSessionedPacket(client, (SessionedPacket) scp, p);
		} else
			throw new IllegalStateException("unhandled packet of type " + scp.getClass());

	}

	private void handleAuthPacket(Client client, AuthenticationPacket scp, Packet packet) {
		if (scp instanceof LoginPacket)
			loginHandler.login(client, (LoginPacket) scp, packet);
		else if (scp instanceof SignUpPacket)
			signUpHandler.signUp(client, (SignUpPacket) scp, packet);
	}

	private void handleSessionedPacket(Client client, SessionedPacket scp, Packet packet) {
		if (scp instanceof UsernameChangePacket)
			usernameChange.changeUsername(client, (UsernameChangePacket) scp, packet);
		else if (scp instanceof ProfilePicChangePacket)
			ppChange.changeProfilePic(client, (ProfilePicChangePacket) scp, packet);
		else if (scp instanceof PasswordChangePacket)
			pwdChange.changePassword(client, (PasswordChangePacket) scp, packet);
		else if (scp instanceof SearchUserPacket)
			searchUser.searchUser(client, (SearchUserPacket) scp, packet);
		else if (scp instanceof RequestPacket)
			reqHandler.onRequest(client, (RequestPacket) scp, packet);
	}
}
