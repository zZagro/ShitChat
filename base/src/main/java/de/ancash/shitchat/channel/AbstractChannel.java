package de.ancash.shitchat.channel;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractChannel {

	protected final UUID id;
	protected final Type type;
	protected final Set<UUID> users;

	public AbstractChannel(UUID id, Type type, Collection<UUID> users) {
		this.id = id;
		this.type = type;
		this.users = Collections.unmodifiableSet(new HashSet<>(users));
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
}
