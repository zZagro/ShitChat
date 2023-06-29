package de.ancash.shitchat.packet.user;

import java.util.UUID;

import de.ancash.shitchat.packet.SessionedPacket;
import de.ancash.shitchat.user.FullUser;
import de.ancash.shitchat.user.User;

public class FriendRequestReceivedPacket extends SessionedPacket {

	private static final long serialVersionUID = -1174866331156010537L;

	private final User sender;
	private final FullUser full;

	public FriendRequestReceivedPacket(UUID sessionId, User sender, FullUser full) {
		super(sessionId);
		this.sender = sender;
		this.full = full;
	}

	public FullUser getNewFullTarget() {
		return full;
	}

	public User getSender() {
		return sender;
	}
}
