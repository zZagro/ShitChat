package de.ancash.shitchat.packet.profile;

import java.util.UUID;

import de.ancash.shitchat.packet.SessionedPacket;
import de.ancash.shitchat.user.FullUser;

public class ProfileChangeResultPacket extends SessionedPacket implements IProfilePacket {

	private static final long serialVersionUID = -8785640453975804471L;

	private final FullUser newUser;
	private final String reason;

	public ProfileChangeResultPacket(UUID sessionId, FullUser newUser, String reason) {
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

	public FullUser getNewUser() {
		return newUser;
	}
}
