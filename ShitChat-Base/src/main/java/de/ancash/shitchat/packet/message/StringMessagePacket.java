package de.ancash.shitchat.packet.message;

import java.util.UUID;

import de.ancash.shitchat.packet.SessionedShitChatPacket;

public class StringMessagePacket extends SessionedShitChatPacket{

	private static final long serialVersionUID = -7217684742933905281L;

	private final String message;
	private final UUID chat;
	
	public StringMessagePacket(UUID sessionId, String message, UUID chat) {
		super(sessionId);
		this.chat = chat;
		this.message = message;
	}

	public UUID getChatId() {
		return chat;
	}
	
	public String getMessage() {
		return message;
	}
}
