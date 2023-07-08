package de.ancash.shitchat.packet.user;

import java.util.UUID;

import de.ancash.shitchat.packet.SessionedPacket;

public class RequestPacket extends SessionedPacket {

	private static final long serialVersionUID = 540743936934659391L;

	private final UUID target;
	private final RequestType type;

	public RequestPacket(UUID sessionId, UUID target, RequestType type) {
		super(sessionId);
		this.target = target;
		this.type = type;
	}

	public UUID getTarget() {
		return target;
	}

	public RequestType getType() {
		return type;
	}
}
