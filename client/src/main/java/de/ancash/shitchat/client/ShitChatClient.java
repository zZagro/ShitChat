package de.ancash.shitchat.client;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import de.ancash.libs.org.bukkit.event.EventHandler;
import de.ancash.libs.org.bukkit.event.EventManager;
import de.ancash.libs.org.bukkit.event.Listener;
import de.ancash.misc.ReflectionUtils;
import de.ancash.shitchat.ShitChatPlaceholder;
import de.ancash.shitchat.packet.ShitChatPacket;
import de.ancash.shitchat.packet.auth.AuthFailedPacket;
import de.ancash.shitchat.packet.auth.AuthResultPacket;
import de.ancash.shitchat.packet.auth.AuthSuccessPacket;
import de.ancash.shitchat.packet.auth.LoginPacket;
import de.ancash.shitchat.packet.auth.SignUpPacket;
import de.ancash.shitchat.packet.profile.UsernameChangePacket;
import de.ancash.shitchat.packet.profile.UsernameChangeResultPacket;
import de.ancash.shitchat.user.User;
import de.ancash.shitchat.util.AuthenticationUtil;
import de.ancash.sockets.async.impl.packet.client.AsyncPacketClient;
import de.ancash.sockets.async.impl.packet.client.AsyncPacketClientFactory;
import de.ancash.sockets.events.ClientConnectEvent;
import de.ancash.sockets.events.ClientDisconnectEvent;
import de.ancash.sockets.events.ClientPacketReceiveEvent;
import de.ancash.sockets.packet.Packet;
import de.ancash.sockets.packet.PacketFuture;

public class ShitChatClient implements Listener {

	@SuppressWarnings("nls")
	public static void main(String[] args) throws InterruptedException {
		ShitChatClient client = new ShitChatClient("localhost", 12345);
		if (client.connect()) {
			System.out.println("connected");
			System.out.println(client.login("joe@gmail.com",
					AuthenticationUtil.hashPassword("joe@gmail.com", "password".toCharArray())));
			System.out.println(client.updateUserName(UUID.randomUUID().toString()));

			System.out.println("user: " + client.getUser().getUsername());
			Thread.sleep(2000);
			client.disconnect();
		} else {
			System.err.println("not connected");
		}
	}

	private static final AsyncPacketClientFactory factory = new AsyncPacketClientFactory();

	private volatile AsyncPacketClient client;
	private final String address;
	private final int port;
	private volatile State state = State.DISCONNECTED;
	private volatile UUID sid;
	private volatile User user;

	public ShitChatClient(String address, int port) {
		EventManager.registerEvents(this, this);
		this.address = address;
		this.port = port;

	}

	public State getState() {
		return state;
	}

	public UUID getSessionId() {
		if (state != State.CONNECTED)
			return null;
		return sid;
	}

	@SuppressWarnings("nls")
	public void disconnect() {
		client.onDisconnect(new IllegalStateException("client disconnect"));
	}

	public User getUser() {
		if (state != State.CONNECTED)
			return null;
		return user;
	}

	protected PacketFuture sendShitChatPacket0(ShitChatPacket packet, boolean awaitResp) {
		Packet p = packet.toPacket();
		p.setAwaitResponse(awaitResp);
		client.write(p);
		return new PacketFuture(p, null);
	}

	public boolean isConnected() {
		return client != null && client.isConnected() && client.isConnectionValid();
	}

	public Optional<String> login(String email, byte[] pass) {
		if (state != State.CONNECTING || !isConnected())
			return Optional.of(ShitChatPlaceholder.NOT_CONNECTED);
		state = State.AUTHENTICATING;
		return authenticate(sendShitChatPacket0(new LoginPacket(email, pass), true));
	}

	public Optional<String> signUp(String email, byte[] pass, String user) {
		if (state != State.CONNECTING || !isConnected())
			return Optional.of(ShitChatPlaceholder.NOT_CONNECTED);
		state = State.AUTHENTICATING;
		return authenticate(sendShitChatPacket0(new SignUpPacket(email, pass, user), true));
	}

	public boolean isAuthenticated() {
		return state == State.CONNECTED;
	}

	public Optional<String> updateUserName(String nun) {
		if (!isAuthenticated())
			return Optional.of(ShitChatPlaceholder.NOT_AUTHENTICATED);
		return updateUserName(sendShitChatPacket0(new UsernameChangePacket(sid, nun), true));
	}

	private Optional<String> updateUserName(PacketFuture future) {
		Optional<UsernameChangeResultPacket> result = future.get(30, TimeUnit.SECONDS);
		if (!result.isPresent())
			return Optional.of(ShitChatPlaceholder.INTERNAL_ERROR);
		UsernameChangeResultPacket r = result.get();
		if (r.wasSuccessful()) {
			user = r.getNewUser();
			return Optional.empty();
		}
		return Optional.of(r.getReason());
	}

	@SuppressWarnings("nls")
	private Optional<String> authenticate(PacketFuture future) {
		Optional<AuthResultPacket> opt = null;
		opt = future.get(30, TimeUnit.SECONDS);

		Logger.getGlobal().info(client + "");
		Logger.getGlobal().info(future + "");
		Logger.getGlobal().info(opt + "");
		if (!opt.isPresent()) {
			client.onDisconnect(new IllegalStateException("could not authorize"));
			return Optional.of(ShitChatPlaceholder.INTERNAL_ERROR);
		}
		AuthResultPacket result = (AuthResultPacket) opt.get();
		if (result instanceof AuthSuccessPacket) {
			onAuthSuccess((AuthSuccessPacket) result);
			return Optional.empty();
		} else {
			state = State.CONNECTING;
			return Optional.of(((AuthFailedPacket) opt.get()).getReason());
		}
	}

	private void onAuthSuccess(AuthSuccessPacket packet) {
		sid = packet.getSID();
		user = packet.getUser();
		state = State.CONNECTED;
		System.out.println("SID: " + sid);
		System.out.println("User: " + ReflectionUtils.toString(user, true));
		System.out.println("PP: " + ReflectionUtils.toString(user.getProfilePic(), true));
		System.out.println("PP-Size: " + user.getProfilePic().asBytes().length);
	}

	public synchronized boolean connect() {
		if (client != null)
			return false;
		state = State.CONNECTING;
		try {
			client = factory.newInstance(address, port, 10_000, 256 * 1024, 256 * 1024, 2);
		} catch (IOException e) {
			state = State.AUTHENTICATING;
			return false;
		}
		return true;
	}

	@EventHandler
	public void onPacket(ClientPacketReceiveEvent event) {
		if (event.getReceiver() == null || client == null || event.getReceiver().equals(client)) {

		}
	}

	@EventHandler
	public void onClientDisconnect(ClientDisconnectEvent event) {
		if (event.getClient() == null || client == null || event.getClient().equals(client)) {
			state = State.DISCONNECTED;
			client = null;
			Logger.getGlobal().info("on disconnect");
			if (event.getThrowable() != null)
				event.getThrowable().printStackTrace();
			else
				Logger.getGlobal().info("no throwable");
			Thread.dumpStack();
		}
	}

	@EventHandler
	public void onClientConnect(ClientConnectEvent event) {
		if (event.getClient() == null || client == null || event.getClient().equals(client)) {
			state = State.CONNECTING;
		}
	}

	public enum State {
		DISCONNECTED, CONNECTING, AUTHENTICATING, CONNECTED;
	}
}
