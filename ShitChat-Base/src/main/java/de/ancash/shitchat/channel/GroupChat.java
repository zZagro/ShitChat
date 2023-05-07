package de.ancash.shitchat.channel;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

public class GroupChat extends AbstractChannel implements Serializable {

	private static final long serialVersionUID = -6883606748860448005L;

	public GroupChat(UUID id, Collection<UUID> users) {
		super(id, Type.GROUP, users);
	}

}
