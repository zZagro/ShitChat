package de.ancash.shitchat.channel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;

public class DirectChannel extends AbstractChannel implements Serializable {

	private static final long serialVersionUID = -6883606748860448005L;

	public DirectChannel(UUID id, UUID a, UUID b) {
		super(id, ChannelType.DIRECT, Arrays.asList(a, b));
	}
}
