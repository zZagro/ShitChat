package de.ancash.shitchat.packet.user;

import java.util.UUID;

import de.ancash.shitchat.packet.SessionedPacket;
import de.ancash.shitchat.user.FullUser;

public class RequestResultPacket extends SessionedPacket {

	private static final long serialVersionUID = 540743936934659391L;

	private final String failReason;
	private final RequestType type;
	private final FullUser user;

	public RequestResultPacket(UUID sessionId, String failReason, RequestType type, FullUser user) {
		super(sessionId);
		this.user = user;
		this.failReason = failReason;
		this.type = type;
	}

	public boolean wasSuccessful() {
		return failReason == null;
	}

	public String getReason() {
		return failReason;
	}

	public RequestType getType() {
		return type;
	}

	public FullUser getUser() {
		return user;
	}
}
