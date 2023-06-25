package de.ancash.shitchat.packet.profile;

import java.util.UUID;

import de.ancash.shitchat.packet.SessionedPacket;
import de.ancash.shitchat.user.User;

public class ProfileChangeResultPacket extends SessionedPacket implements IProfilePacket {

	private static final long serialVersionUID = -8785640453975804471L;

	private final User newUser;
	private final String reason;

	public ProfileChangeResultPacket(UUID sessionId, User newUser, String reason) {
		super(sessionId);
		this.newUser = newUser;
		this.reason = reason;
	}

	public boolean wasSuccessful() {
		return newUser != null && reason == null;
	}

	public String getReason() {
		return reason;
	}

	public User getNewUser() {
		return newUser;
	}
}
