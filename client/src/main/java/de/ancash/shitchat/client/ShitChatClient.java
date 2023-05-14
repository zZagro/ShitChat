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

	public static void main(String[] args) throws InterruptedException {
		ShitChatClient client = new ShitChatClient("denzo.algoholics.eu", 25565);
		if (client.connect()) {
			System.out.println("connected");
			System.out.println(client.login("joe@gmail.com",
					AuthenticationUtil.hashPassword("joe@gmail.com", "password".toCharArray())));
			Thread.sleep(5000);
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
		checkConnected();
		return sid;
	}

	public void disconnect() {
		client.onDisconnect(new IllegalStateException());
	}

	public User getUser() {
		checkConnected();
		return user;
	}

	public PacketFuture sendShitChatPacket(ShitChatPacket packet) {
		checkConnected();
		return sendShitChatPacket0(packet);
	}

	@SuppressWarnings("nls")
	protected void checkConnected() {
		if (state != State.CONNECTED)
			throw new IllegalStateException("not connected");
	}

	protected PacketFuture sendShitChatPacket0(ShitChatPacket packet) {
		Packet p = packet.toPacket();
		new Thread(() -> client.write(p)).start();
		return new PacketFuture(p, null);
	}

	protected PacketFuture sendPacket0(Packet packet) {
		client.write(packet);
		return new PacketFuture(packet, null);
	}

	public boolean isConnected() {
		return client != null && client.isConnected() && client.isConnectionValid();
	}

	public Optional<String> login(String email, byte[] pass) {
		if (state != State.CONNECTING || !isConnected())
			return Optional.of(ShitChatPlaceholder.CANNOT_AUTH);
		state = State.AUTHENTICATING;
		return authenticate(sendShitChatPacket0(new LoginPacket(email, pass)));
	}

	public Optional<String> signUp(String email, byte[] pass, String user) {
		if (state != State.CONNECTING || !isConnected())
			return Optional.of(ShitChatPlaceholder.CANNOT_AUTH);
		state = State.AUTHENTICATING;
		return authenticate(sendShitChatPacket0(new SignUpPacket(email, pass, user)));
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
			Logger.getGlobal().info("on connect" + client + " " + event.getClient());
		}
	}

	public enum State {
		DISCONNECTED, CONNECTING, AUTHENTICATING, CONNECTED;
	}
}
