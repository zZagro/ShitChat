package de.ancash.shitchat.packet.user;

import java.util.UUID;

import de.ancash.shitchat.packet.SessionedPacket;

public class FriendRequestResultPacket extends SessionedPacket {

	private static final long serialVersionUID = 540743936934659391L;

	private final String failReason;

	public FriendRequestResultPacket(UUID sessionId, String failReason) {
		super(sessionId);
		this.failReason = failReason;
		;
	}

	public boolean wasSuccessful() {
		return failReason == null;
	}

	public String getReason() {
		return failReason;
	}
}
