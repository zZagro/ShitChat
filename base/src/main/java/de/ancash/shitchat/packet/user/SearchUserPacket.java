package de.ancash.shitchat.packet.user;

import java.util.UUID;

import de.ancash.shitchat.packet.SessionedPacket;

public class SearchUserPacket extends SessionedPacket {

	private static final long serialVersionUID = -4951530663682877936L;

	private final String user;

	public SearchUserPacket(UUID sessionId, String user) {
		super(sessionId);
		this.user = user;
	}

	public String getUser() {
		return user;
	}
}
