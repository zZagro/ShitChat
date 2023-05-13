package de.ancash.shitchat.channel;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;

import de.ancash.shitchat.message.AbstractMessage;

public abstract class AbstractChat {

	public static final int CACHED_MESSAGES = 100;

	protected final UUID id;
	protected final Type type;
	protected final Set<UUID> users;
	protected final LinkedList<String> messages = new LinkedList<>();
	protected long lastAccess = System.currentTimeMillis();

	public AbstractChat(UUID id, Type type, Collection<UUID> users) {
		this.id = id;
		this.type = type;
		this.users = Collections.unmodifiableSet(new HashSet<>(users));
	}

	public void addMessage(AbstractMessage message) {
		messages.add(message.serialize());
		if (messages.size() > CACHED_MESSAGES)
			messages.poll();
		updateLastAccess();
	}

	public AbstractChat updateLastAccess() {
		lastAccess = System.currentTimeMillis();
		return this;
	}

	public UUID getChannelId() {
		return id;
	}

	public Type getChannelType() {
		return type;
	}

	public Set<UUID> getUsers() {
		return users;
	}

	public enum Type {
		GROUP, DIRECT;
	}

	public long getLastAccess() {
		return lastAccess;
	}
}
