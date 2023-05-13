package de.ancash.shitchat.message;

import java.io.StringReader;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import de.ancash.shitchat.channel.AbstractChat;
import de.ancash.shitchat.user.User;

@SuppressWarnings("nls")
public abstract class AbstractMessage {

	protected static final String TYPE_KEY = "type";
	protected static final String CHANNEL_ID_KEY = "channel";
	protected static final String SENDER_ID_KEY = "sender";
	protected static final String TIMESTAMP_KEY = "stamp";

	protected final UUID channel;
	protected final UUID sender;
	protected final long millis;
	protected final Type type;

	public AbstractMessage(AbstractChat channel, User sender, long millis, Type type) {
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

	protected JsonObjectBuilder serializeBase() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add(TYPE_KEY, type.name());
		builder.add(CHANNEL_ID_KEY, channel.toString());
		builder.add(SENDER_ID_KEY, sender.toString());
		builder.add(TIMESTAMP_KEY, millis);
		return builder;
	}

	public abstract String serialize();

	public static AbstractMessage deserialize(String json) {
		JsonObject object = Json.createReader(new StringReader(json)).readObject();
		switch (Type.valueOf(object.getString(TYPE_KEY))) {
		case STRING:
			return StringMessage.deserialize(json);
		default:
			throw new IllegalArgumentException(Type.valueOf(object.getString(TYPE_KEY)).name());
		}
	}

	public enum Type {
		STRING;
	}
}
