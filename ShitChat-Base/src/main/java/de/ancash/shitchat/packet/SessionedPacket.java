package de.ancash.shitchat.packet;

import java.util.UUID;

public abstract class SessionedPacket extends ShitChatPacket {

	private static final long serialVersionUID = -7548012341210260685L;

	protected final UUID sessionId;

	public SessionedPacket(UUID sessionId) {
		this.sessionId = sessionId;
	}

	public UUID getSessionId() {
		return sessionId;
	}
}
