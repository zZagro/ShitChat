
package de.ancash.shitchat.server.account;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import de.ancash.misc.CustomReentrantReadWriteLock;
import de.ancash.shitchat.ShitChatKeys;
import de.ancash.shitchat.server.ShitChatServer;
import de.ancash.shitchat.server.client.Client;

@SuppressWarnings("nls")
public class AccountRegistry extends Thread {

	private final File accountsDir = new File("data/accounts");
	private final ShitChatServer server;
	private final CustomReentrantReadWriteLock lock = new CustomReentrantReadWriteLock();
	private final YamlFile accountLink = new YamlFile(new File("data/accounts.yml"));
	private final YamlFile usernames = new YamlFile(new File("data/usernames.yml"));
	private final ConcurrentHashMap<String, UUID> uidByUsername = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, UUID> uidByEmail = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<UUID, Account> accByUId = new ConcurrentHashMap<UUID, Account>();
	private final ConcurrentHashMap<UUID, Session> sesByUId = new ConcurrentHashMap<UUID, Session>();

	public AccountRegistry(ShitChatServer server) throws InvalidConfigurationException, IOException {
		this.server = server;
		if (!accountsDir.exists())
			accountsDir.mkdirs();
		usernames.createNewFile(false);
		loadUsernames();
		accountLink.createNewFile(false);
		loadAccounts();
	}

	private void loadAccounts() throws InvalidConfigurationException, IOException {
		System.out.println("Loading accounts");
		accountLink.load();
		accountLink.getKeys(false).forEach(k -> uidByEmail.put(accountLink.getString(k), UUID.fromString(k)));
		System.out.println(uidByEmail.size() + " accounts found");
	}

	private void loadUsernames() throws InvalidConfigurationException, IOException {
		System.out.println("Loading usernames...");
		usernames.load();
		usernames.getKeys(false).forEach(k -> {
			if (uidByUsername.containsKey(usernames.get(k)))
				System.err.println("Duplicate username: " + usernames.getString(k) + " (" + UUID.fromString(k) + ", "
						+ uidByUsername.get(usernames.getString(k)));
			uidByUsername.put(usernames.getString(k), UUID.fromString(k));
		});
		System.out.println(uidByUsername.size() + " usernames found!");
	}

	private void saveUsernames() throws InvalidConfigurationException, IOException {
		System.out.println("Saving usernames");
		usernames.createNewFile();
		usernames.load();
		uidByUsername.entrySet().forEach(e -> usernames.set(e.getValue().toString(), e.getKey()));
		usernames.save();
		System.out.println(uidByEmail.size() + " usernames saved");
	}

	private void saveAccounts() throws InvalidConfigurationException, IOException {
		System.out.println("Saving accounts");
		accountLink.createNewFile();
		accountLink.load();
		uidByEmail.entrySet().forEach(e -> accountLink.set(e.getValue().toString(), e.getKey()));
		accountLink.save();
		System.out.println(uidByEmail.size() + " accounts saved");
	}

	public Account updateUsername(UUID sessionId, String newUserName) {
		return lock.writeLock(() -> {
			if (isUsernameUsed(newUserName) || !isSessionValid(sessionId))
				return null;
			Account acc = sesByUId.get(sessionId).getAccount();
			System.out.println(
					uidByUsername.get(acc.getUsername()) + ": " + acc.getUsername() + ": " + uidByUsername.keySet());
			if (acc == null || !uidByUsername.get(acc.getUsername()).equals(acc.getId())) {
				System.err.println("very strange error");
				return null;
			}
			String old = acc.getUsername();
			try {
				acc.setUsername(newUserName);
			} catch (IOException e) {
				System.err.println("Could not update username for " + acc.getId() + ": " + newUserName);
				e.printStackTrace();
				return null;
			}
			uidByUsername.remove(old);
			uidByUsername.put(newUserName, acc.getId());
			System.out.println("username change: " + old + " -> " + newUserName);
			return acc;
		});
	}

	public UUID getUIdByEmail(String email) {
		return uidByEmail.get(email);
	}

	public boolean isEmailUsed(String email) {
		return uidByEmail.containsKey(email);
	}

	public boolean isUsernameUsed(String un) {
		return uidByUsername.containsKey(un);
	}

	public boolean isSessionValid(UUID session) {
		return lock.writeLock(() -> {
			if (sesByUId.containsKey(session)) {
				sesByUId.get(session).getAccount().updateLastAccess();
				return true;
			}
			System.out.println("invalid session: " + session + ", " + sesByUId.keySet());
			return false;
		});
	}

	@Override
	public void run() {
		while (server.isRunning()) {

			lock.writeLock(this::checkCacheTimeout);

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.err.println("interrupted " + e);
				return;
			}
		}
		lock.writeLock(() -> {
			try {
				saveAccounts();
				saveUsernames();
			} catch (IOException e) {
				System.err.println("could not save accounts");
				e.printStackTrace();
			}
		});
	}

	private void checkCacheTimeout() {
		Iterator<UUID> keys = accByUId.keySet().iterator();
		long now = System.currentTimeMillis();
		while (keys.hasNext()) {
			UUID cur = keys.next();
			Account acc = accByUId.get(cur);
			System.out.println(acc.getId() + " " + (acc.getLastAccess() + TimeUnit.MINUTES.toMillis(10) - now));
			if (acc.getLastAccess() + TimeUnit.SECONDS.toMillis(10) < now && !acc.hasSessions()) {
				System.out.println("cache life time of " + acc.getEmail() + ":" + acc.getUsername() + " expired");
				keys.remove();
			}
		}
	}

	public Session newSession(Account acc) {
		Session s = acc.newSession().setOnExit(ses -> {
			sesByUId.remove(ses.getSessionId());
			acc.removeSession(ses.getSessionId());
		});
		sesByUId.put(s.getSessionId(), s);
		return s;
	}

	public Account getAccount(UUID id) {
		Account a = lock.conditionalReadLock(() -> accByUId.containsKey(id), () -> null, () -> getAccount0(id));
		if (a != null)
			return a;
		return lock.writeLock(() -> getAccount0(id));
	}

	public File getDir(UUID id) {
		String[] split = id.toString().split("-");
		List<String> path = new ArrayList<>(Arrays.asList(split));
		path.add(0, accountsDir.getPath());
		return new File(String.join("//", path));
	}

	public Account createAccount(String email, String name, byte[] pass) {
		return lock.writeLock(() -> {
			if (isEmailUsed(email) || isUsernameUsed(name))
				return null;
			UUID id = UUID.randomUUID();
			File dir = getDir(id);
			while (dir.exists()) {
				System.err.println("duplicate uid during acc creation found: " + id);
				id = UUID.randomUUID();
				dir = getDir(id);
			}
			dir.mkdirs();
			File data = new File(String.join("//", dir.getPath(), "data.yml"));
			try {
				data.createNewFile();
				YamlFile yf = new YamlFile(data);
				yf.load();
				yf.set(ShitChatKeys.USER_EMAIL, email);
				yf.set(ShitChatKeys.USER_NAME, name);
				yf.set(ShitChatKeys.USER_PASSWORD,
						IntStream.range(0, pass.length).map(i -> pass[i]).boxed().collect(Collectors.toList()));
				yf.set(ShitChatKeys.PROFILE_PIC_FILE, server.getDefaultProfilePicFile());
				yf.set(ShitChatKeys.UID, id.toString());
				yf.set(ShitChatKeys.DIRECT_CHANNELS, new ArrayList<>());
				yf.set(ShitChatKeys.GROUP_CHANNELS, new ArrayList<>());
				yf.save();
				uidByEmail.put(email, id);
				uidByUsername.put(name, id);
			} catch (IOException e) {
				System.err.println("could not create/load new data file");
				e.printStackTrace();
				return null;
			}
			return getAccount0(id);
		});
	}

	private Account getAccount0(UUID id) {
		if (accByUId.containsKey(id)) {
			return accByUId.get(id).updateLastAccess();
		}
		File dir = getDir(id);
		if (!dir.exists()) {
			System.err.println("no files found for " + id);
			return null;
		}
		try {
			Account acc = new Account(new File(String.join("//", dir.getPath(), "data.yml")));
			accByUId.put(id, acc);
			return acc;
		} catch (IOException e) {
			System.err.println("could not load account at " + dir.getPath());
			e.printStackTrace();
			return null;
		}
	}

	public void onDisconnect(Client client) {
		if (client == null || client.getSID() == null)
			return;
		lock.writeLock(() -> {
			Session s = sesByUId.get(client.getSID());
			if (s == null)
				return;
			s.exit();
		});

	}
}
