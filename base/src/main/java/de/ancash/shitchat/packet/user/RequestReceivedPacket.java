package de.ancash.shitchat.packet.user;

import java.util.UUID;

import de.ancash.shitchat.packet.SessionedPacket;
import de.ancash.shitchat.user.FullUser;
import de.ancash.shitchat.user.User;

public class RequestReceivedPacket extends SessionedPacket {

	private static final long serialVersionUID = -1174866331156010537L;

	private final User sender;
	private final FullUser full;
	private final RequestType type;

	public RequestReceivedPacket(UUID sessionId, User sender, FullUser full, RequestType type) {
		super(sessionId);
		this.sender = sender;
		this.full = full;
		this.type = type;
	}

	public FullUser getNewFullTarget() {
		return full;
	}

	public User getSender() {
		return sender;
	}

	public RequestType getType() {
		return type;
	}
}
