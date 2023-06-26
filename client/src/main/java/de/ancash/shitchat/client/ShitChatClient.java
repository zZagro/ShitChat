package de.ancash.shitchat.client;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import de.ancash.ithread.IThreadPoolExecutor;
import de.ancash.libs.org.bukkit.event.EventHandler;
import de.ancash.libs.org.bukkit.event.EventManager;
import de.ancash.libs.org.bukkit.event.Listener;
import de.ancash.shitchat.ShitChatImage;
import de.ancash.shitchat.ShitChatPlaceholder;
import de.ancash.shitchat.packet.ShitChatPacket;
import de.ancash.shitchat.packet.auth.AuthFailedPacket;
import de.ancash.shitchat.packet.auth.AuthResultPacket;
import de.ancash.shitchat.packet.auth.AuthSuccessPacket;
import de.ancash.shitchat.packet.auth.LoginPacket;
import de.ancash.shitchat.packet.auth.SignUpPacket;
import de.ancash.shitchat.packet.profile.PasswordChangePacket;
import de.ancash.shitchat.packet.profile.ProfileChangeResultPacket;
import de.ancash.shitchat.packet.profile.ProfilePicChangePacket;
import de.ancash.shitchat.packet.profile.UsernameChangePacket;
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

		};
		if (client.connect()) {
			System.out.println("connected");
			System.out.println(client.login("joe@gmail.com",
					AuthenticationUtil.hashPassword("joe@gmail.com", "pwd".toCharArray())));
			System.out.println(client.changeUserName(UUID.randomUUID().toString()));

			System.out.println("user: " + client.getUser().getUsername());
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

	private volatile AsyncPacketClient client;
	private final String address;
	private final int port;
	private volatile State state = State.DISCONNECTED;
	private volatile UUID sid;
	private volatile User user;
	private String email;
	private final Logger logger = Logger.getGlobal();

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

	@SuppressWarnings("nls")
	public Optional<String> login(String email, byte[] pass) {
		if (state != State.CONNECTING || !isConnected()) {
			onAuthenticationFailed(ShitChatPlaceholder.NOT_CONNECTED);
			return Optional.of(ShitChatPlaceholder.NOT_CONNECTED);
		}
		state = State.AUTHENTICATING;
		this.email = email;
		logger.info("login");
		return authenticate(sendShitChatPacket0(new LoginPacket(email, pass), true));
	}

	@SuppressWarnings("nls")
	public Optional<String> signUp(String email, byte[] pass, String user) {
		if (state != State.CONNECTING || !isConnected()) {
			onAuthenticationFailed(ShitChatPlaceholder.NOT_CONNECTED);
			return Optional.of(ShitChatPlaceholder.NOT_CONNECTED);
		}
		state = State.AUTHENTICATING;
		this.email = email;
		logger.info("sign up");
		return authenticate(sendShitChatPacket0(new SignUpPacket(email, pass, user), true));
	}

	public boolean isAuthenticated() {
		return state == State.CONNECTED;
	}

	public Optional<String> changePassword(byte[] oldPass, byte[] newPass) {
		if (!isAuthenticated()) {
			onChangePasswordFailed(ShitChatPlaceholder.NOT_AUTHENTICATED);
			return Optional.of(ShitChatPlaceholder.NOT_AUTHENTICATED);
		}
		logger.info("change pwd");
		return changePassword(sendShitChatPacket0(new PasswordChangePacket(sid, oldPass, newPass), true));
	}

	private Optional<String> changePassword(PacketFuture future) {
		Optional<ProfileChangeResultPacket> result = future.get(30, TimeUnit.SECONDS);
		if (!result.isPresent()) {
			onChangePasswordFailed(ShitChatPlaceholder.INTERNAL_ERROR);
			return Optional.of(ShitChatPlaceholder.INTERNAL_ERROR);
		}
		ProfileChangeResultPacket r = result.get();
		if (r.wasSuccessful()) {
			user = r.getNewUser();
			onChangePassword();
			;
			return Optional.empty();
		}
		onChangePasswordFailed(r.getReason());
		return Optional.of(r.getReason());
	}

	public abstract void onChangePasswordFailed(String reason);

	public abstract void onChangePassword();

	public Optional<String> changePP(byte[] bb) {
		if (!isAuthenticated()) {
			onPPChangeFailed(ShitChatPlaceholder.NOT_AUTHENTICATED);
			return Optional.of(ShitChatPlaceholder.NOT_AUTHENTICATED);
		}
		logger.info("change pp");
		return changePP(sendShitChatPacket0(new ProfilePicChangePacket(sid, new ShitChatImage(bb)), true));
	}

	private Optional<String> changePP(PacketFuture future) {
		Optional<ProfileChangeResultPacket> result = future.get(30, TimeUnit.SECONDS);
		if (!result.isPresent()) {
			onPPChangeFailed(ShitChatPlaceholder.INTERNAL_ERROR);
			return Optional.of(ShitChatPlaceholder.INTERNAL_ERROR);
		}
		ProfileChangeResultPacket r = result.get();
		if (r.wasSuccessful()) {
			user = r.getNewUser();
			onPPChange(user);
			return Optional.empty();
		}
		onPPChangeFailed(r.getReason());
		return Optional.of(r.getReason());
	}

	public abstract void onPPChangeFailed(String reason);

	public abstract void onPPChange(User user);

	@SuppressWarnings("nls")
	public Optional<String> changeUserName(String nun) {
		if (!isAuthenticated()) {
			onUserNameChangeFailed(ShitChatPlaceholder.NOT_AUTHENTICATED);
			return Optional.of(ShitChatPlaceholder.NOT_AUTHENTICATED);
		}
		logger.info("change user name");
		return changeUserName(sendShitChatPacket0(new UsernameChangePacket(sid, nun), true));
	}

	private Optional<String> changeUserName(PacketFuture future) {
		Optional<ProfileChangeResultPacket> result = future.get(30, TimeUnit.SECONDS);
		if (!result.isPresent()) {
			onUserNameChangeFailed(ShitChatPlaceholder.INTERNAL_ERROR);
			return Optional.of(ShitChatPlaceholder.INTERNAL_ERROR);
		}
		ProfileChangeResultPacket r = result.get();
		if (r.wasSuccessful()) {
			user = r.getNewUser();
			onUserNameChange(user);
			return Optional.empty();
		}
		onUserNameChangeFailed(r.getReason());
		return Optional.of(r.getReason());
	}

	public abstract void onUserNameChange(User user);

	public abstract void onUserNameChangeFailed(String reason);

	@SuppressWarnings("nls")
	private Optional<String> authenticate(PacketFuture future) {
		Optional<AuthResultPacket> opt = null;
		opt = future.get(30, TimeUnit.SECONDS);

		if (!opt.isPresent()) {
			client.onDisconnect(new IllegalStateException("could not authorize"));
			onAuthenticationFailed(ShitChatPlaceholder.INTERNAL_ERROR);
			return Optional.of(ShitChatPlaceholder.INTERNAL_ERROR);
		}
		AuthResultPacket result = (AuthResultPacket) opt.get();
		if (result instanceof AuthSuccessPacket) {
			onAuthSuccess((AuthSuccessPacket) result);
			return Optional.empty();
		} else {
			email = null;
			state = State.CONNECTING;
			onAuthenticationFailed(((AuthFailedPacket) opt.get()).getReason());
			return Optional.of(((AuthFailedPacket) opt.get()).getReason());
		}
	}

	public abstract void onAuthenticationFailed(String reason);

	private void onAuthSuccess(AuthSuccessPacket packet) {
		sid = packet.getSID();
		user = packet.getUser();
		state = State.CONNECTED;
		logUser();
		onAuthSuccess();
	}

	public abstract void onAuthSuccess();

	@SuppressWarnings("nls")
	private void logUser() {
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
