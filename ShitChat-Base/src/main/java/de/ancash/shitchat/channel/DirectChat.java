package de.ancash.shitchat.channel;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

public class DirectChat extends AbstractChannel implements Serializable {

	private static final long serialVersionUID = -6883606748860448005L;

	public DirectChat(UUID id, Collection<UUID> users) {
		super(id, Type.DIRECT, users);
	}
}
