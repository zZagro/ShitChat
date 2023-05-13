package de.ancash.shitchat.channel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;

public class DirectChat extends AbstractChat implements Serializable {

	private static final long serialVersionUID = -6883606748860448005L;

	public DirectChat(UUID id, UUID a, UUID b) {
		super(id, Type.DIRECT, Arrays.asList(a, b));
	}
}
