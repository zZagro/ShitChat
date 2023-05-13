
package de.ancash.shitchat.server.channel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import de.ancash.libs.org.simpleyaml.configuration.file.YamlFile;
import de.ancash.libs.org.simpleyaml.exceptions.InvalidConfigurationException;
import de.ancash.misc.CustomReentrantReadWriteLock;
import de.ancash.shitchat.channel.AbstractChat;
import de.ancash.shitchat.channel.AbstractChat.Type;
import de.ancash.shitchat.channel.DirectChat;
import de.ancash.shitchat.channel.GroupChat;
import de.ancash.shitchat.message.AbstractMessage;
import de.ancash.shitchat.server.ShitChatServer;

@SuppressWarnings("nls")
public class ChannelRegistry extends Thread {

	private static final String CHAT_MESSAGES_FILE = "messages.yml";
	private static final String CHAT_USERS_FILE = "users.yml";
	private static final String CHAT_USERS_PROP = "users";
	private static final String CHAT_MESSAGES_PROP = "messages";
	private final File chatDir = new File("data/chats");
	private final ShitChatServer server;
	private final CustomReentrantReadWriteLock lock = new CustomReentrantReadWriteLock();
	private final HashMap<AbstractChat.Type, HashMap<UUID, AbstractChat>> chatsById = new HashMap<AbstractChat.Type, HashMap<UUID, AbstractChat>>();

	public ChannelRegistry(ShitChatServer server) throws InvalidConfigurationException, IOException {
		this.server = server;
		chatsById.put(Type.DIRECT, new HashMap<UUID, AbstractChat>());
		chatsById.put(Type.GROUP, new HashMap<UUID, AbstractChat>());
		if (!chatDir.exists())
			chatDir.mkdirs();
	}

	public AbstractChat getChatById(UUID id, Type type) {
		AbstractChat ch = lock.conditionalReadLock(() -> chatsById.get(type).containsKey(id), () -> null,
				() -> getChatById0(id, type));
		if (ch != null)
			return ch;
		return lock.writeLock(() -> getChatById0(id, type));
	}

	protected AbstractChat getChatById0(UUID id, Type type) {
		if (chatsById.get(type).containsKey(id))
			return chatsById.get(type).get(id).updateLastAccess();
		try {
			loadChat(new File(getDir(id, type)), type, id);
		} catch (IOException e) {
			System.err.println("could not load chat at " + getDir(id, type));
			e.printStackTrace();
			return null;
		}
		return getChatById0(id, type);
	}

	protected void loadChat(File dir, Type type, UUID id) throws IOException {
		System.out.println("loading chat in " + dir.getPath());
		YamlFile usersFile = new YamlFile(new File(String.join("\\", dir.getPath(), CHAT_USERS_FILE)));
		usersFile.createNewFile(false);
		usersFile.load();
		List<UUID> users = usersFile.getStringList(CHAT_USERS_PROP).stream().map(UUID::fromString)
				.collect(Collectors.toList());
		YamlFile messagesFile = new YamlFile(new File(String.join("\\", dir.getPath(), CHAT_MESSAGES_FILE)));
		messagesFile.createNewFile(false);
		messagesFile.load();
		AbstractChat chat = null;
		switch (type) {
		case DIRECT:
			chat = new DirectChat(id, users.get(0), users.get(1));
			break;
		case GROUP:
			chat = new GroupChat(id, users);
			break;
		default:
			throw new IllegalArgumentException(type.name());
		}
		messagesFile.getStringList(CHAT_MESSAGES_PROP).stream().map(AbstractMessage::deserialize)
				.forEach(chat::addMessage);
		chatsById.get(type).put(id, chat);
	}

	public boolean exists(UUID id) {
		return getChatById(id, Type.DIRECT) != null || getChatById(id, Type.GROUP) != null;
	}

	@Override
	public void run() {
		while (server.isRunning()) {

			lock.writeLock(() -> checkCacheTimeout());

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.err.println("interrupted " + e);
				return;
			}
		}
		lock.writeLock(() -> {

		});
	}

	private void checkCacheTimeout() {
		chatsById.values().forEach(e -> checkCacheTimeout(e.values()));
	}

	private void checkCacheTimeout(Collection<AbstractChat> channels) {
		Iterator<AbstractChat> chs = channels.iterator();
		long now = System.currentTimeMillis();
		while (chs.hasNext()) {
			AbstractChat cur = chs.next();
			if (cur.getLastAccess() + now < System.currentTimeMillis()) {
				System.out
						.println("cache life time of " + cur.getChannelId() + ":" + cur.getChannelType() + " expired");
				chs.remove();
			}
		}
	}

	public String getDir(UUID id, Type type) {
		String[] split = id.toString().split("-");
		List<String> path = new ArrayList<>(Arrays.asList(split));
		path.add(0, chatDir.getPath());
		path.add(1, type.name());
		return String.join("//", path);
	}

	public AbstractChat createChat(Type type) {
		return lock.writeLock(() -> {
			UUID id = UUID.randomUUID();
			File dir = new File(getDir(id, type));
			while (dir.exists()) {
				System.err.println("duplicate cid during chat creation found: " + id);
				id = UUID.randomUUID();
				dir = new File(getDir(id, type));
			}
			dir.mkdirs();
			return getChatById0(id, type);
		});
	}
}
