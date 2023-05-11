package de.ancash.shitchat.server.account;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.ancash.libs.org.simpleyaml.configuration.file.YamlFile;
import de.ancash.libs.org.simpleyaml.exceptions.InvalidConfigurationException;
import de.ancash.misc.CustomReentrantReadWriteLock;
import de.ancash.shitchat.ShitChatKeys;
import de.ancash.shitchat.server.ShitChatServer;

@SuppressWarnings("nls")
public class AccountRegistry extends Thread {

	private final File accountsDir = new File("data/accounts");
	private final ShitChatServer server;
	private final CustomReentrantReadWriteLock lock = new CustomReentrantReadWriteLock();
	private final YamlFile accountLink = new YamlFile(new File("data/accounts.yml"));
	private final ConcurrentHashMap<String, UUID> uidByEmail = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<UUID, Account> accByUId = new ConcurrentHashMap<UUID, Account>();
	private final ConcurrentHashMap<UUID, Session> sesByUId = new ConcurrentHashMap<UUID, Session>();

	public AccountRegistry(ShitChatServer server) throws InvalidConfigurationException, IOException {
		this.server = server;
		if (!accountsDir.exists())
			accountsDir.mkdirs();
		accountLink.createNewFile(false);
		loadAccounts();
	}

	private void loadAccounts() throws InvalidConfigurationException, IOException {
		System.out.println("Loading accounts");
		accountLink.load();
		accountLink.getKeys(false)
				.forEach(k -> uidByEmail.put(formatEmail(k), UUID.fromString(accountLink.getString(k))));
		System.out.println(uidByEmail.size() + " accounts found");
	}

	private void saveAccounts() throws InvalidConfigurationException, IOException {
		System.out.println("Saving accounts");
		accountLink.createNewFile();
		accountLink.load();
		uidByEmail.entrySet().forEach(e -> accountLink.set(formatEmail(e.getKey()), e.getValue().toString()));
		accountLink.save();
		System.out.println(uidByEmail.size() + " accounts saved");
	}

	private String formatEmail(String e) {
		return e.replaceAll("\\.", toUnicode('.'));
	}

	private static String toUnicode(char ch) {
		return "{" + String.format("\\u%04x", (int) ch) + "}";
	}

	public UUID getUIdByEmail(String email) {
		return uidByEmail.get(formatEmail(email));
	}

	public boolean exists(String email) {
		return uidByEmail.containsKey(formatEmail(email))
				&& new File(String.join("//", getDir(getUIdByEmail(email)).getPath(), "data.yml")).exists();
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
			if (acc.getLastAccess() + now < System.currentTimeMillis()) {
				System.out.println("cache life time of " + acc.getEmail() + ":" + acc.getUserName() + " expired");
				Set<Session> sss = acc.getAllSessions();
				sss.forEach(Session::exit);
			}
		}
	}

	public Session newSession(Account acc) {
		Session s = acc.newSession().setOnExit(ses -> {
			sesByUId.remove(ses.getSessionId());
			acc.removeSession(ses.getSessionId());
		});
		sesByUId.put(acc.getId(), s);
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
			if (exists(email))
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
				yf.save();
				uidByEmail.put(formatEmail(email), id);
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
}
