package de.ancash.shitchat.server.channel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import de.ancash.misc.CustomReentrantReadWriteLock;
import de.ancash.shitchat.channel.AbstractChannel;
import de.ancash.shitchat.channel.ChannelType;
import de.ancash.shitchat.channel.DirectChannel;
import de.ancash.shitchat.channel.GroupChannel;
import de.ancash.shitchat.message.AbstractMessage;
import de.ancash.shitchat.message.MessageType;
import de.ancash.shitchat.message.StringMessage;
import de.ancash.shitchat.packet.message.MessagePacket;
import de.ancash.shitchat.server.ShitChatServer;
import de.ancash.shitchat.server.account.Account;
import de.ancash.shitchat.server.account.AccountRegistry;

@SuppressWarnings("nls")
public class ChannelRegistry implements Runnable {

	private static final String CHANNEL_MESSAGES_FILE = "messages.yml";
	private static final String CHANNEL_USERS_FILE = "users.yml";
	private static final String CHANNEL_USERS_PROP = "users";
	private static final String CHANNEL_MESSAGES_PROP = "messages";
	private final File channelDir = new File("data/channels");
	private final ShitChatServer server;
	private final CustomReentrantReadWriteLock lock = new CustomReentrantReadWriteLock();
	private final ConcurrentHashMap<ChannelType, ConcurrentHashMap<UUID, AbstractChannel>> channelsById = new ConcurrentHashMap<ChannelType, ConcurrentHashMap<UUID, AbstractChannel>>();
	private final Set<AbstractChannel> saveQueue = Collections.synchronizedSet(new LinkedHashSet<AbstractChannel>());

	public ChannelRegistry(ShitChatServer server) throws InvalidConfigurationException, IOException {
		this.server = server;
		channelsById.put(ChannelType.DIRECT, new ConcurrentHashMap<UUID, AbstractChannel>());
		channelsById.put(ChannelType.GROUP, new ConcurrentHashMap<UUID, AbstractChannel>());
		if (!channelDir.exists())
			channelDir.mkdirs();
	}

	public AbstractChannel getChannelById(UUID id, ChannelType type) {
		AbstractChannel ch = lock.conditionalReadLock(() -> channelsById.get(type).containsKey(id), () -> null,
				() -> getChannelById0(id, type));
		if (ch != null)
			return ch;
		return lock.writeLock(() -> getChannelById0(id, type));
	}

	public AbstractChannel createGroupChannel() {
		return getChannelById(UUID.randomUUID(), ChannelType.GROUP);
	}

	public boolean writeToChannel(AccountRegistry ar, AbstractChannel channel, Account sender, String message) {
		if (!exists(channel.getChannelId(), channel.getChannelType()))
			return false;
		return lock.writeLock(() -> writeToChannel0(ar, channel, sender, message));
	}

	private boolean writeToChannel0(AccountRegistry ar, AbstractChannel channel, Account sender, String message) {
		if (!StringMessage.isValid(message) || message.length() > 2000)
			return false;
		AbstractMessage msg = new StringMessage(channel.getChannelId(), sender.getUserId(), System.currentTimeMillis(),
				message);
		channel.addMessage(msg);
		saveQueue.add(channel);
		channel.getUsers().stream().map(ar::getAccount).map(Account::getAllSessions).flatMap(Collection::stream)
				.forEach(s -> {
					s.sendPacket(new MessagePacket(s.getSessionId(), msg, MessageType.STRING));
					System.out.println(
							"published message to " + s.getAccount().getUserId() + " in " + channel.getChannelId());
				});
		return true;
	}

	public DirectChannel createDirectChannel(Account a, Account b) {
		return lock.writeLock(() -> {
			UUID id;
			if ((id = a.getDirectChannelTo(b.getUserId(), this)) != null) {
				System.out.println(
						"direct channel " + a.getUserId() + "<=>" + b.getUserId() + " already exists, using it");
				return (DirectChannel) getChannelById0(id, ChannelType.DIRECT);
			}
			id = UUID.randomUUID();
			try {
				File dir = new File(getDir(id, ChannelType.DIRECT));
				YamlFile usersFile = new YamlFile(new File(dir.getPath() + "/" + CHANNEL_USERS_FILE));
				usersFile.createNewFile(false);
				usersFile.load();
				if (!usersFile.isConfigurationSection(CHANNEL_USERS_PROP))
					usersFile.createSection(CHANNEL_USERS_PROP);
				ConfigurationSection user = usersFile.getConfigurationSection(CHANNEL_USERS_PROP);
				user.createSection(a.getUserId().toString());
				user.createSection(b.getUserId().toString());
				usersFile.save();
				new YamlFile(new File(dir.getPath() + "/" + CHANNEL_MESSAGES_FILE)).createNewFile(false);
				DirectChannel channel = new DirectChannel(id, a.getUserId(), b.getUserId());
				channelsById.get(ChannelType.DIRECT).put(id, channel);
				System.out.println("direct channel " + id + " for " + a.getUserId() + "<=>" + b.getUserId());
				a.addChannel(channel.getChannelId(), channel.getChannelType());
				b.addChannel(channel.getChannelId(), channel.getChannelType());
				return channel;
			} catch (IOException ex) {
				System.err.println("could not create direct channel " + id);
				ex.printStackTrace();
				return null;
			}
		});
	}

	protected AbstractChannel getChannelById0(UUID id, ChannelType type) {
		if (channelsById.get(type).containsKey(id))
			return channelsById.get(type).get(id).updateLastAccess();
		if (!exists(id, type))
			return null;
		try {
			loadChat(new File(getDir(id, type)), type, id);
		} catch (IOException e) {
			System.err.println("could not load chat at " + getDir(id, type));
			e.printStackTrace();
			return null;
		}
		getChannelById0(id, type);
		return channelsById.get(type).get(id);
	}

	@SuppressWarnings("unchecked")
	protected void loadChat(File dir, ChannelType type, UUID id) throws IOException {
		System.out.println("loading chat in " + dir.getPath());
		YamlFile usersFile = new YamlFile(new File(dir.getPath() + "/" + CHANNEL_USERS_FILE));
		if (!usersFile.exists())
			return;
		usersFile.createNewFile(false);
		usersFile.load();
		if (!usersFile.isConfigurationSection(CHANNEL_USERS_PROP))
			usersFile.createSection(CHANNEL_USERS_PROP);
		ConfigurationSection user = usersFile.getConfigurationSection(CHANNEL_USERS_PROP);
		List<UUID> users = user.getKeys(false).stream().map(UUID::fromString).collect(Collectors.toList());
		YamlFile messagesFile = new YamlFile(new File(dir.getPath() + "/" + CHANNEL_MESSAGES_FILE));
		messagesFile.createNewFile(false);
		messagesFile.load();
		AbstractChannel chat = null;
		switch (type) {
		case DIRECT:
			chat = new DirectChannel(id, users.get(0), users.get(1));
			break;
		case GROUP:
			chat = new GroupChannel(id, users);
			break;
		default:
			throw new IllegalArgumentException(type.name());
		}
		System.out.println("messages: " + messagesFile);
		Optional.ofNullable(((List<AbstractMessage>) messagesFile.getList(CHANNEL_MESSAGES_PROP)))
				.orElse(new ArrayList<>()).forEach(chat::addMessage);
		channelsById.get(type).put(id, chat);
	}

	public boolean exists(UUID id, ChannelType type) {
		return new File(getDir(id, type)).exists();
	}

	@Override
	public void run() {
		while (server.isRunning()) {

			lock.writeLock(() -> checkChannelsToSave());
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

	private void checkChannelsToSave() {
		Iterator<AbstractChannel> iter = saveQueue.iterator();
		long now = System.currentTimeMillis();
		while (iter.hasNext()) {
			AbstractChannel ch = iter.next();
			if (ch.getLastAccess() + TimeUnit.SECONDS.toMillis(10) < now) {
				long s = System.nanoTime();
				try {
					saveChannelToFile(ch);
					System.out.println("saving chanel" + ch.getChannelId() + ":" + ch.getChannelType() + "saved in "
							+ (System.nanoTime() - s) / 1000D + " micros");
				} catch (IOException e) {
					System.err.println("could not save " + ch);
					e.printStackTrace();
				}
				iter.remove();
			}
		}
	}

	private void checkCacheTimeout() {
		channelsById.values().forEach(e -> checkCacheTimeout(e.values()));
	}

	private void checkCacheTimeout(Collection<AbstractChannel> channels) {
		Iterator<AbstractChannel> chs = channels.iterator();
		long now = System.currentTimeMillis();
		while (chs.hasNext()) {
			AbstractChannel cur = chs.next();
			if (cur.getLastAccess() + TimeUnit.SECONDS.toMillis(60) < now) {
				long s = System.nanoTime();
				try {
					saveChannelToFile(cur);
					System.out.println("cache life time of channel " + cur.getChannelId() + ":" + cur.getChannelType()
							+ " expired, saved in " + (System.nanoTime() - s) / 1000D + " micros");
				} catch (IOException e) {
					System.err.println("could not save ");
					e.printStackTrace();
				}
				chs.remove();
			}
		}
	}

	private void saveChannelToFile(AbstractChannel channel) throws IOException {
		File dir = new File(getDir(channel.getChannelId(), channel.getChannelType()));
		YamlFile users = new YamlFile(new File(dir.getPath() + "/" + CHANNEL_USERS_FILE));
		users.createNewFile(false);
		users.load();
		ConfigurationSection cs = users.createSection(CHANNEL_USERS_PROP);
		channel.getUsers().stream().map(UUID::toString).forEach(cs::createSection);
		users.save();
		YamlFile messages = new YamlFile(new File(dir.getPath() + "/" + CHANNEL_MESSAGES_FILE));
		messages.createNewFile(false);
		messages.load();
		messages.set(CHANNEL_MESSAGES_PROP, channel.getMessages());
		messages.save();
	}

	public String getDir(UUID id, ChannelType type) {
		return channelDir.getPath() + "/" + type.name() + "/" + id;
	}
}
