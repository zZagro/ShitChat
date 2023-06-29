package de.ancash.shitchat.packet.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import de.ancash.shitchat.packet.SessionedPacket;
import de.ancash.shitchat.user.User;

public class SearchUserResultPacket extends SessionedPacket {

	private static final long serialVersionUID = -7625892045434007892L;

	private final List<User> found;
	private final String failReason;

	public SearchUserResultPacket(UUID sessionId, List<User> found, String failReason) {
		super(sessionId);
		this.failReason = failReason;
		this.found = new ArrayList<>(found);
	}

	public boolean wasSuccessful() {
		return failReason == null;
	}

	public String getReason() {
		return failReason;
	}

	public List<User> getFoundUser() {
		return Collections.unmodifiableList(found);
	}
}
