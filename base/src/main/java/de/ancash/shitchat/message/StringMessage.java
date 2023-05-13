package de.ancash.shitchat.message;

import java.io.StringReader;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import de.ancash.libs.org.apache.commons.lang3.Validate;
import de.ancash.shitchat.channel.AbstractChat;
import de.ancash.shitchat.user.User;

@SuppressWarnings("nls")
public class StringMessage extends AbstractMessage {

	public static final int MAX_STRING_MESSAGE_LENGTH = 1000;
	public static final String MESSAGE_REGEX = "[a-zA-Z0-9]*";

	protected static final String MESSAGE_KEY = "msg";

	public static boolean isValid(String message) {
		for (String s : message.split("\n"))
			if (!s.matches(MESSAGE_REGEX))
				return false;
		return true;
	}

	private final String message;

	public StringMessage(AbstractChat channel, User sender, long millis, String message) {
		this(channel.getChannelId(), sender.getUserId(), millis, message);
	}

	public StringMessage(UUID channel, UUID sender, long millis, String message) {
		super(channel, sender, millis, Type.STRING);
		Validate.isTrue(message.length() <= MAX_STRING_MESSAGE_LENGTH, "message too long");
		Validate.isTrue(isValid(message), "invalid message, only " + MESSAGE_REGEX);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public boolean isMessageValid() {
		return isValid(message);
	}

	@Override
	public String serialize() {
		JsonObjectBuilder builder = serializeBase();
		builder.add(MESSAGE_KEY, message);
		return builder.build().toString();
	}

	public static StringMessage deserialize(String json) {
		JsonObject object = Json.createReader(new StringReader(json)).readObject();
		return new StringMessage(UUID.fromString(object.getString(CHANNEL_ID_KEY)),
				UUID.fromString(object.getString(SENDER_ID_KEY)), object.getJsonNumber(TIMESTAMP_KEY).longValue(),
				object.getString(MESSAGE_KEY));
	}
}
