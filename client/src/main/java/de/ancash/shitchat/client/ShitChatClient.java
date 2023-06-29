package de.ancash.shitchat.client;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import de.ancash.datastructures.tuples.Duplet;
import de.ancash.ithread.IThreadPoolExecutor;
import de.ancash.libs.org.bukkit.event.EventHandler;
import de.ancash.libs.org.bukkit.event.EventManager;
import de.ancash.libs.org.bukkit.event.Listener;
import de.ancash.shitchat.packet.ShitChatPacket;
import de.ancash.shitchat.user.User;
import de.ancash.shitchat.util.AuthenticationUtil;
import de.ancash.sockets.async.impl.packet.client.AsyncPacketClient;
import de.ancash.sockets.async.impl.packet.client.AsyncPacketClientFactory;
import de.ancash.sockets.events.ClientConnectEvent;
import de.ancash.sockets.events.ClientDisconnectEvent;
import de.ancash.sockets.events.ClientPacketReceiveEvent;
import de.ancash.sockets.packet.Packet;
import de.ancash.sockets.packet.PacketFuture;

public abstract class ShitChatClient implements Listener {

	@SuppressWarnings("nls")
	public static void main(String[] args) throws InterruptedException {
		ShitChatClient client = new ShitChatClient("denzo.algoholics.eu", 25565) {
//			ShitChatClient client = new ShitChatClient("localhost", 12345) {

			@Override
			@EventHandler
			public void onClientDisconnect(ClientDisconnectEvent event) {
				super.onClientDisconnect(event);
			}

			@Override
			@EventHandler
			public void onClientConnect(ClientConnectEvent event) {
				super.onClientConnect(event);
			}

			@Override
			@EventHandler
			public void onPacket(ClientPacketReceiveEvent event) {
				super.onPacket(event);
			}

			@Override
			public void onAuthSuccess() {
				System.out.println("auth success");
			}

			@Override
			public void onUserNameChange(User user) {
				System.out.println("user name change: " + user.getUsername());
			}

			@Override
			public void onUserNameChangeFailed(String reason) {
				System.out.println("user name change fail: " + reason);
			}

			@Override
			public void onAuthenticationFailed(String reason) {
				System.out.println("auth failed: " + reason);
			}

			@Override
			public void onConnectFailed() {
				System.out.println("connect failed");
			}

			@Override
			public void onDisconnect() {
				System.out.println("disconnect");
			}

			@Override
			public void onConnect() {
				System.out.println("connect");
			}

			@Override
			public void onPPChangeFailed(String reason) {
				System.out.println("on pp change failed: " + reason);
			}

			@Override
			public void onPPChange(User user) {
				System.out.println("on pp change");
			}

			@Override
			public void onChangePasswordFailed(String reason) {
				System.out.println("pwd change fail: " + reason);
			}

			@Override
			public void onChangePassword() {
				System.out.println("pwd change");
			}

			@Override
			public void onSearchUser(List<User> found) {
				System.out.println("found users: " + found);
			}

			@Override
			public void onSearchUserFailed(String reason) {
				System.out.println("search user failed: " + reason);
			}

		};
		if (client.connect()) {
			System.out.println("connected");
			System.out.println(client.login("joe@gmail.com",
					AuthenticationUtil.hashPassword("joe@gmail.com", "pwd".toCharArray())));
//			System.out.println(client.changeUserName("joe mama"));
			List<User> searched = client.searchUser("").getFirst().get();
			for (User u : searched)
				System.out.println(u.getUserId() + ": " + u.getUsername());
//			client.changePassword(AuthenticationUtil.hashPassword("joe@gmail.com", "password".toCharArray()),
//					AuthenticationUtil.hashPassword("joe@gmail.com", "pwd".toCharArray()));
			Thread.sleep(2000);
			client.disconnect();
		} else {
			System.err.println("not connected");
		}
	}

	private static final AsyncPacketClientFactory factory = new AsyncPacketClientFactory();
	private IThreadPoolExecutor pool;

	volatile AsyncPacketClient client;
	private final String address;
	private final int port;
	volatile State state = State.DISCONNECTED;
	volatile UUID sid;
	volatile User user;
	final AuthHandler authHandler = new AuthHandler(this);
	final ProfileEditHandler profileEditHandler = new ProfileEditHandler(this);
	final UserSearchHandler searchUser = new UserSearchHandler(this);
	String email;
	final Logger logger = Logger.getGlobal();

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

	public User getUser() {
		if (state != State.CONNECTED)
			return null;
		return user;
	}

	public String getEmail() {
		if (state != State.CONNECTED)
			return null;
		return email;
	}

	@SuppressWarnings("nls")
	public void disconnect() {
		client.onDisconnect(new IllegalStateException("client disconnect"));
	}

	protected PacketFuture sendShitChatPacket0(ShitChatPacket packet, boolean awaitResp) {
		Packet p = packet.toPacket();
		p.setAwaitResponse(awaitResp);
		pool.submit(() -> client.write(p));
		return new PacketFuture(p, null);
	}

	public boolean isConnected() {
		return client != null && client.isConnected() && client.isConnectionValid();
	}

	public Optional<String> login(String email, byte[] pass) {
		return authHandler.login(email, pass);
	}

	public Optional<String> signUp(String email, byte[] pass, String user) {
		return authHandler.signUp(email, pass, user);
	}

	public boolean isAuthenticated() {
		return state == State.CONNECTED;
	}

	public Optional<String> changePassword(byte[] oldPass, byte[] newPass) {
		return profileEditHandler.changePassword(oldPass, newPass);
	}

	public abstract void onChangePasswordFailed(String reason);

	public abstract void onChangePassword();

	public Optional<String> changePP(byte[] bb) {
		return profileEditHandler.changePP(bb);
	}

	public abstract void onPPChangeFailed(String reason);

	public abstract void onPPChange(User user);

	public Optional<String> changeUserName(String nun) {
		return profileEditHandler.changeUserName(nun);
	}

	public abstract void onUserNameChange(User user);

	public abstract void onUserNameChangeFailed(String reason);

	public abstract void onAuthenticationFailed(String reason);

	public abstract void onAuthSuccess();

	public Duplet<Optional<List<User>>, Optional<String>> searchUser(String name) {
		return searchUser.searchUser(name);
	}

	public abstract void onSearchUser(List<User> found);

	public abstract void onSearchUserFailed(String reason);

	@SuppressWarnings("nls")
	protected void logUser() {
		logger.info("sid: " + sid);
		logger.info("user: " + user.getUsername());
		logger.info("email: " + email);
	}

	@SuppressWarnings("nls")
	public synchronized boolean connect() {
		if (client != null)
			return false;
		pool = IThreadPoolExecutor.newFixedThreadPool(2);
		logger.info("connecting");
		state = State.CONNECTING;
		try {
			client = factory.newInstance(address, port, 4 * 1024, 4 * 1024, 1);
		} catch (IOException e) {
			state = State.DISCONNECTED;
			logger.warning("could not connect");
			logThrowable(e);
			onConnectFailed();
			return false;
		}

		while (!isConnected()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
			}
		}

		return true;
	}

	public abstract void onConnectFailed();

	private void logThrowable(Throwable th) {
		for (StackTraceElement e : th.getStackTrace())
			logger.warning(e.toString());
	}

	@EventHandler
	public void onPacket(ClientPacketReceiveEvent event) {
		if (event.getReceiver() == null || client == null || event.getReceiver().equals(client)) {

		}
	}

	@SuppressWarnings("nls")
	@EventHandler
	public void onClientDisconnect(ClientDisconnectEvent event) {
		if (event.getClient() == null || client == null || event.getClient().equals(client)) {
			state = State.DISCONNECTED;
			client = null;
			logger.warning("disconnected");
			if (event.getThrowable() != null)
				logThrowable(event.getThrowable());
			else
				logger.info("no reason supplied");
			pool.shutdownNow();
			pool = null;
			onDisconnect();
		}
	}

	public abstract void onDisconnect();

	@SuppressWarnings("nls")
	@EventHandler
	public void onClientConnect(ClientConnectEvent event) {
		if (event.getClient() == null || client == null || event.getClient().equals(client)) {
			state = State.CONNECTING;
			logger.info("connected to server");
			onConnect();
		}
	}

	public abstract void onConnect();

	public enum State {
		DISCONNECTED, CONNECTING, AUTHENTICATING, CONNECTED;
	}
}
