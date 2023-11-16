package de.ancash.shitchat.channel;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import de.ancash.shitchat.message.AbstractMessage;

public abstract class AbstractChannel implements Serializable {

	private static final long serialVersionUID = 7201251742694922686L;

	public static final int CACHED_MESSAGES = 100;

	protected final UUID id;
	protected final ChannelType channelType;
	protected final Set<UUID> users;
	protected final LinkedList<AbstractMessage> messages = new LinkedList<>();
	protected long lastAccess = System.currentTimeMillis();

	public AbstractChannel(UUID id, ChannelType channelType, Collection<UUID> users) {
		this.id = id;
		this.channelType = channelType;
		this.users = new HashSet<>(users);
	}

	public AbstractChannel updateLastAccess() {
		lastAccess = System.currentTimeMillis();
		return this;
	}

	public void addMessage(AbstractMessage message) {
		messages.add(message);
		Collections.sort(messages, (a, b) -> Long.compare(a.getTimestamp(), b.getTimestamp()));
		if (messages.size() > CACHED_MESSAGES)
			messages.poll();
		updateLastAccess();
	}

	public int countMessages() {
		return messages.size();
	}

	public AbstractMessage getMessage(int i) {
		return messages.get(i);
	}

	public List<AbstractMessage> getMessages() {
		return Collections.unmodifiableList(messages);
	}

	public UUID getChannelId() {
		return id;
	}

	public ChannelType getChannelType() {
		return channelType;
	}

	public long getLastAccess() {
		return lastAccess;
	}

	public Set<UUID> getUsers() {
		return Collections.unmodifiableSet(users);
	}
}
