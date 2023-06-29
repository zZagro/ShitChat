package de.ancash.shitchat.packet.user;

import java.util.UUID;

import de.ancash.shitchat.packet.SessionedPacket;

public class FriendRequestPacket extends SessionedPacket {

	private static final long serialVersionUID = 540743936934659391L;

	private final UUID target;

	public FriendRequestPacket(UUID sessionId, UUID target) {
		super(sessionId);
		this.target = target;
	}

	public UUID getTarget() {
		return target;
	}
}
