package de.ancash.shitchat.account;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import de.ancash.libs.org.simpleyaml.configuration.file.YamlFile;
import de.ancash.libs.org.simpleyaml.exceptions.InvalidConfigurationException;
import de.ancash.misc.CustomReentrantReadWriteLock;
import de.ancash.shitchat.ShitChatServer;

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
		accountLink.load();
		accountLink.getKeys(false).forEach(k -> uidByEmail.put(k, UUID.fromString(accountLink.getString(k))));
		if(!accountsDir.exists())
			accountsDir.mkdirs();
	}

	@Override
	public void run() {

	}
	
	public Session newSession(Account acc) {
		return acc.newSession().setOnExit(ses -> {
			sesByUId.remove(ses.getSessionId());
			acc.removeSession(ses.getSessionId());
		});
	}

	public Account getAccount(UUID id) {
		Account a = lock.conditionalReadLock(() -> accByUId.containsKey(id), () -> null, () -> getAccount0(id));
		if(a != null)
			return a;
		return lock.writeLock(() -> getAccount0(id));
	}

	private Account getAccount0(UUID id) {
		if (accByUId.containsKey(id)) {
			return accByUId.get(id).updateLastAccess();
		}
		String[] split = id.toString().split("-");
		List<String> path = Arrays.asList(split);
		path.add(0, accountsDir.getPath());
		File dir = new File(String.join("//", path));
		dir.mkdirs();
		try {
			Account acc = new Account(new File(String.join("//", dir.getPath(), "data.yml")));
			accByUId.put(id, acc);
			return acc;
		} catch (IOException e) {
			throw new IllegalStateException("could not load account", e);
		}
	}
}
