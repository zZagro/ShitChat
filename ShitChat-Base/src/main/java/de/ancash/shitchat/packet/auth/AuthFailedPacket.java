package de.ancash.shitchat.packet.auth;

import de.ancash.shitchat.packet.ShitChatPacket;

public class AuthFailedPacket extends ShitChatPacket {

	private static final long serialVersionUID = 3272540097556748864L;

	private final String reason;

	public AuthFailedPacket(String reason) {
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}
}
