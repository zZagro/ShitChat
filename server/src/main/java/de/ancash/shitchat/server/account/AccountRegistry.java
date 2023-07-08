package de.ancash.shitchat.server.account;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import de.ancash.misc.CustomReentrantReadWriteLock;
import de.ancash.shitchat.ShitChatImage;
import de.ancash.shitchat.ShitChatKeys;
import de.ancash.shitchat.server.ShitChatServer;
import de.ancash.shitchat.server.client.Client;

@SuppressWarnings("nls")
public class AccountRegistry extends Thread {

	private final File accountsDir = new File("data/accounts");
	private final ShitChatServer server;
	private final CustomReentrantReadWriteLock lock = new CustomReentrantReadWriteLock();
	private final ConcurrentHashMap<String, UUID> uidByUsername = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, UUID> uidByEmail = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<UUID, Account> accByUId = new ConcurrentHashMap<UUID, Account>();
	private final ConcurrentHashMap<UUID, Session> sesByUId = new ConcurrentHashMap<UUID, Session>();
	private final String dataFile = "data.yml";

	public AccountRegistry(ShitChatServer server) throws InvalidConfigurationException, IOException {
		this.server = server;
		if (!accountsDir.exists())
			accountsDir.mkdirs();
		loadAccounts();
	}

	public Set<String> getAllUsernames() {
		return lock.readLock(() -> new HashSet<>(uidByUsername.keySet()));
	}

	public UUID getUIDByUsername(String un) {
		return uidByUsername.get(un);
	}

	private void loadAccounts() throws InvalidConfigurationException, IOException {
		System.out.println("Loading usernames...");
		File[] accs = accountsDir.listFiles();
		if (accs != null && accs.length > 0) {
			for (File acc : accs) {
				File data = new File(acc.getPath() + "/" + dataFile);
				if (!data.exists()) {
					System.err.println("not udata file found in " + acc.getPath());
					continue;
				}
				YamlFile yml = new YamlFile(data);
				yml.load();
				uidByUsername.put(yml.getString(ShitChatKeys.USER_NAME),
						UUID.fromString(yml.getString(ShitChatKeys.UID)));
				uidByEmail.put(yml.getString(ShitChatKeys.USER_EMAIL),
						UUID.fromString(yml.getString(ShitChatKeys.UID)));
			}
		}
	}

	public Account updateUsername(UUID sessionId, String newUserName) {
		return lock.writeLock(() -> {
			if (isUsernameUsed(newUserName) || !isSessionValid(sessionId))
				return null;
			Account acc = sesByUId.get(sessionId).getAccount();
			if (!uidByUsername.get(acc.getUsername()).equals(acc.getId())) {
				System.err.println("very strange sync error");
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

	public Account updateProfilePic(UUID sessionId, byte[] newPp) {
		return lock.writeLock(() -> {
			if (!isSessionValid(sessionId))
				return null;
			Account acc = sesByUId.get(sessionId).getAccount();
			try {
				acc.setProfilePic(new ShitChatImage(newPp));
			} catch (IOException e) {
				System.err.println("Could not update pp for " + acc.getId() + ": " + e);
				e.printStackTrace();
				return null;
			}
			System.out.println("pp change: " + acc.getId());
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

	public boolean isUIDValid(UUID id) {
		return getDir(id).exists();
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

	public Session getSession(UUID sid) {
		return lock.readLock(() -> sesByUId.get(sid).updateLastAccess());
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
	}

	private void checkCacheTimeout() {
		Iterator<UUID> keys = accByUId.keySet().iterator();
		long now = System.currentTimeMillis();
		while (keys.hasNext()) {
			UUID cur = keys.next();
			Account acc = accByUId.get(cur);
			if (acc.getLastAccess() + TimeUnit.SECONDS.toMillis(10) < now && !acc.hasSessions()) {
				System.out.println("cache life time of " + acc.getEmail() + ":" + acc.getUsername() + " expired");
				keys.remove();
			}
		}
	}

	public Session newSession(Account acc, Client client) {
		Session s = acc.newSession(client).setOnExit(ses -> {
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
		return new File(accountsDir.getPath() + "/" + id.toString());
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
			File data = new File(String.join("//", dir.getPath(), dataFile));
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
			Account acc = new Account(new File(String.join("//", dir.getPath(), dataFile)));
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
