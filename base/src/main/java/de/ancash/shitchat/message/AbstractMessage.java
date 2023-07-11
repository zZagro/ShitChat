package de.ancash.shitchat.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.simpleyaml.configuration.serialization.ConfigurationSerializable;

import de.ancash.shitchat.channel.AbstractChannel;
import de.ancash.shitchat.user.User;

@SuppressWarnings("nls")
public abstract class AbstractMessage implements Serializable, ConfigurationSerializable {

	private static final long serialVersionUID = 5816473586212316568L;
	protected static final String TYPE_KEY = "type";
	protected static final String CHANNEL_ID_KEY = "channel";
	protected static final String SENDER_ID_KEY = "sender";
	protected static final String TIMESTAMP_KEY = "stamp";

	protected final UUID channel;
	protected final UUID sender;
	protected final long millis;
	protected final Type type;

	public AbstractMessage(AbstractChannel channel, User sender, long millis, Type type) {
		this(channel.getChannelId(), sender.getUserId(), millis, type);
	}

	public AbstractMessage(UUID channel, UUID sender, long millis, Type type) {
		this.channel = channel;
		this.sender = sender;
		this.millis = millis;
		this.type = type;
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

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put(CHANNEL_ID_KEY, channel.toString());
		map.put(SENDER_ID_KEY, sender.toString());
		map.put(TIMESTAMP_KEY, String.valueOf(millis));
		map.put(TYPE_KEY, type.name());
		return map;
	}

	public static AbstractMessage deserialize(Map<String, Object> map) {
		switch (Type.valueOf(String.valueOf(map.get(TYPE_KEY)))) {
		case STRING:
			return StringMessage.deserialize(map);
		default:
			throw new IllegalArgumentException(map.toString());
		}
	}

	public enum Type {
		STRING;
	}
}
