package de.ancash.shitchat.channel;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

import de.ancash.shitchat.user.User;

public class GroupChannel extends AbstractChannel implements Serializable {

	private static final long serialVersionUID = -6883606748860448005L;

	public GroupChannel(UUID id, Collection<UUID> users) {
		super(id, ChannelType.GROUP, users);
	}

	public void addUser(User user) {
		super.users.add(user.getUserId());
	}

	public void removeUser(User user) {
		super.users.remove(user.getUserId());
	}
}
