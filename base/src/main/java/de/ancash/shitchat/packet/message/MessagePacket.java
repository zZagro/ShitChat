package de.ancash.shitchat.packet.message;

import java.util.Map;
import java.util.UUID;

import de.ancash.shitchat.message.AbstractMessage;
import de.ancash.shitchat.message.AbstractMessage.Type;
import de.ancash.shitchat.message.StringMessage;
import de.ancash.shitchat.packet.SessionedPacket;

public class MessagePacket extends SessionedPacket {

	private static final long serialVersionUID = -7217684742933905281L;

	private final Map<String, Object> message;
	private final String type;

	public MessagePacket(UUID sessionId, AbstractMessage message, Type type) {
		super(sessionId);
		this.message = message.serialize();
		this.type = type.name();
	}

	public Type getMessageType() {
		return Type.valueOf(type);
	}

	public AbstractMessage getMessage() {
		switch (getMessageType()) {
		case STRING:
			return StringMessage.deserialize(message);
		default:
			return null;
		}
	}
}
