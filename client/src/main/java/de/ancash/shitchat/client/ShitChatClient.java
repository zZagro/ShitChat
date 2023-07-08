package de.ancash.shitchat.client;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import de.ancash.datastructures.tuples.Duplet;
import de.ancash.ithread.IThreadPoolExecutor;
import de.ancash.libs.org.bukkit.event.EventHandler;
import de.ancash.libs.org.bukkit.event.EventManager;
import de.ancash.libs.org.bukkit.event.Listener;
import de.ancash.misc.ReflectionUtils;
import de.ancash.shitchat.packet.SessionedPacket;
import de.ancash.shitchat.packet.ShitChatPacket;
import de.ancash.shitchat.packet.profile.ProfileChangeResultPacket;
import de.ancash.shitchat.packet.user.RequestReceivedPacket;
import de.ancash.shitchat.packet.user.RequestType;
import de.ancash.shitchat.user.FullUser;
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
	public static void main(String[] args) throws InterruptedException, ExecutionException {
//		ShitChatClient client = new ShitChatClient("denzo.algoholics.eu", 25565) {
		ShitChatClient client = new ShitChatClient("localhost", 12345) {

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

			@Override
			public void onRequestSuccessful(UUID target, RequestType type) {
				System.out.println("on req suc: " + target + ": " + type);
			}

			@Override
			public void onRequestFailed(String reason, UUID target, RequestType type) {
				System.out.println("on req failed: " + reason);
			}

			@Override
			public void onUserUpdated() {
				System.out.println("user updated");
				System.out.println("fa: " + user.getFriendList().getAccepted());
				System.out.println("fi: " + user.getFriendList().getIncoming());
				System.out.println("fo: " + user.getFriendList().getOutgoing());
				System.out.println("ma: " + user.getMessageRequestList().getAccepted());
				System.out.println("mi: " + user.getMessageRequestList().getIncoming());
				System.out.println("mo: " + user.getMessageRequestList().getOutgoing());
			}

			@Override
			public void onRequestReceived(User who, RequestType type) {
				System.out.println(who.getUsername() + " sent " + type);
			}

		};
		if (client.connect()) {
			System.out.println("connected");
			System.out.println(
					client.login("joe@gmail.com", AuthenticationUtil.hashPassword("joe@gmail.com", "pwd".toCharArray()))
							.get());
//			System.out.println(client.changeUserName("joe mama"));
			List<User> searched = client.searchUser("").get().getFirst().get();
			for (User u : searched)
				System.out.println(u.getUserId() + ": " + u.getUsername());
//			client.changePassword(AuthenticationUtil.hashPassword("joe@gmail.com", "password".toCharArray()),
//					AuthenticationUtil.hashPassword("joe@gmail.com", "pwd".toCharArray()));
			System.out.println(client
					.sendRequest(UUID.fromString("532c1705-ead5-4abc-8064-3a925c1f705c"), RequestType.FRIEND).get());
			Thread.sleep(2000);
			client.disconnect();
		} else {
			System.err.println("not connected");
		}
	}

	private static final AsyncPacketClientFactory factory = new AsyncPacketClientFactory();
	IThreadPoolExecutor pool;

	volatile AsyncPacketClient client;
	private final String address;
	private final int port;
	volatile State state = State.DISCONNECTED;
	volatile UUID sid;
	volatile FullUser user;
	final AuthHandler authHandler = new AuthHandler(this);
	final ProfileEditHandler profileEditHandler = new ProfileEditHandler(this);
	final UserSearchHandler searchUser = new UserSearchHandler(this);
	final RequestHandler reqHandler = new RequestHandler(this);
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

	public Future<Optional<String>> login(String email, byte[] pass) {
		return authHandler.login(email, pass);
	}

	public Future<Optional<String>> signUp(String email, byte[] pass, String user) {
		return authHandler.signUp(email, pass, user);
	}

	public boolean isAuthenticated() {
		return state == State.CONNECTED;
	}

	public Future<Optional<String>> changePassword(byte[] oldPass, byte[] newPass) {
		return profileEditHandler.changePassword(oldPass, newPass);
	}

	public abstract void onChangePasswordFailed(String reason);

	public abstract void onChangePassword();

	public Future<Optional<String>> changePP(byte[] bb) {
		return profileEditHandler.changePP(bb);
	}

	public abstract void onPPChangeFailed(String reason);

	public abstract void onPPChange(User user);

	public Future<Optional<String>> changeUserName(String nun) {
		return profileEditHandler.changeUserName(nun);
	}

	public abstract void onUserNameChange(User user);

	public abstract void onUserNameChangeFailed(String reason);

	public abstract void onAuthenticationFailed(String reason);

	public abstract void onAuthSuccess();

	public Future<Duplet<Optional<List<User>>, Optional<String>>> searchUser(String name) {
		return searchUser.searchUser(name);
	}

	public abstract void onSearchUser(List<User> found);

	public abstract void onSearchUserFailed(String reason);

	public Future<Optional<String>> sendRequest(UUID target, RequestType type) {
		return reqHandler.sendRequest(target, type);
	}

	public abstract void onRequestSuccessful(UUID target, RequestType type);

	public abstract void onRequestFailed(String reason, UUID target, RequestType type);

	public abstract void onRequestReceived(User who, RequestType type);

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
		pool = IThreadPoolExecutor.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 2);
		logger.info("connecting");
		state = State.CONNECTING;
		try {
			client = factory.newInstance(address, port, 4 * 1024, 4 * 1024, Runtime.getRuntime().availableProcessors());
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

	public abstract void onUserUpdated();

	private void logThrowable(Throwable th) {
		for (StackTraceElement e : th.getStackTrace())
			logger.warning(e.toString());
	}

	@EventHandler
	public void onPacket(ClientPacketReceiveEvent event) {
		if (event.getReceiver() == null || client == null || event.getReceiver().equals(client)) {
			Packet packet = event.getPacket();
			Serializable s = packet.getSerializable();
			if (!(s instanceof ShitChatPacket))
				return;
			ShitChatPacket scp = (ShitChatPacket) s;
			if (scp instanceof SessionedPacket) {
				SessionedPacket sp = (SessionedPacket) scp;
				if (!sp.getSessionId().equals(sid)) {

					System.err.println("packet with invalid sid");
					ReflectionUtils.toStringRec(sp, true);
					return;
				}
				if (sp instanceof ProfileChangeResultPacket) {
					ProfileChangeResultPacket pcrp = (ProfileChangeResultPacket) sp;
					if (pcrp.getNewUser() != null && pcrp.getNewUser().getUserId().equals(user.getUserId())) {
						user = pcrp.getNewUser();
						onUserUpdated();
					}
				} else if (sp instanceof RequestReceivedPacket) {
					RequestReceivedPacket rrp = (RequestReceivedPacket) sp;
					if (rrp.getNewFullTarget() != null && rrp.getNewFullTarget().getUserId().equals(user.getUserId())) {
						user = rrp.getNewFullTarget();
						onUserUpdated();
						onRequestReceived(rrp.getSender(), rrp.getType());
					}
				}
			}
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
