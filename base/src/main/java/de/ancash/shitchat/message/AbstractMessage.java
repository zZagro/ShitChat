package de.ancash.shitchat.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.ancash.shitchat.channel.AbstractChannel;
import de.ancash.shitchat.user.User;

@SuppressWarnings("nls")
public abstract class AbstractMessage implements Serializable {

	private static final long serialVersionUID = 5816473586212316568L;
	protected static final String CHANNEL_ID_KEY = "channel";
	protected static final String SENDER_ID_KEY = "sender";
	protected static final String TIMESTAMP_KEY = "stamp";

	protected final UUID channel;
	protected final UUID sender;
	protected final long millis;
	protected final MessageType messageType;

	public AbstractMessage(AbstractChannel channel, User sender, long millis, MessageType messageType) {
		this(channel.getChannelId(), sender.getUserId(), millis, messageType);
	}

	public AbstractMessage(UUID channel, UUID sender, long millis, MessageType messageType) {
		this.channel = channel;
		this.sender = sender;
		this.millis = millis;
		this.messageType = messageType;
	}

	public UUID getChannelId() {
		return channel;
	}

	public UUID getSenderId() {
		return sender;
	}

	public long getTimestamp() {
		return millis;
	}

	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put(CHANNEL_ID_KEY, channel.toString());
		map.put(SENDER_ID_KEY, sender.toString());
		map.put(TIMESTAMP_KEY, millis);
		return map;
	}
}
