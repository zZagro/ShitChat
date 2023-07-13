package de.ancash.shitchat.message;

import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.simpleyaml.configuration.serialization.ConfigurationSerializable;

import de.ancash.libs.org.apache.commons.lang3.Validate;
import de.ancash.shitchat.channel.AbstractChannel;
import de.ancash.shitchat.user.User;

@SuppressWarnings("nls")
public class StringMessage extends AbstractMessage implements ConfigurationSerializable {

	private static final long serialVersionUID = -2469343375731176578L;
	public static final int MAX_STRING_MESSAGE_LENGTH = 1000;
	public static final Pattern MESSAGE_PATTERN = Pattern.compile("[\\x20-\\x7F]*");

	protected static final String MESSAGE_KEY = "msg";

//	public static void main(String[] args) throws IOException {
//		ConfigurationSerialization.registerClass(StringMessage.class);
//		YamlFile file = new YamlFile("test.yml");
//		file.createNewFile();
//		file.set("test", new StringMessage(UUID.randomUUID(), UUID.randomUUID(), System.currentTimeMillis(), "asda sdas das23 34 34 3"));
//		file.save();
//		file.load();
//		System.out.println(file.get("test"));
//		file.deleteFile();
//	}

	public static boolean isValid(String message) {
		return MESSAGE_PATTERN.matcher(message).matches();
	}

	private final String message;

	public StringMessage(AbstractChannel channel, User sender, long millis, String message) {
		this(channel.getChannelId(), sender.getUserId(), millis, message);
	}

	public StringMessage(UUID channel, UUID sender, long millis, String message) {
		super(channel, sender, millis, MessageType.STRING);
		Validate.isTrue(message.length() <= MAX_STRING_MESSAGE_LENGTH, "message too long");
		Validate.isTrue(isValid(message), "invalid message, only " + MESSAGE_PATTERN.pattern());
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public boolean isMessageValid() {
		return isValid(message);
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		map.put(MESSAGE_KEY, message);
		return map;
	}

	public static StringMessage deserialize(Map<String, Object> map) {
		return new StringMessage(UUID.fromString((String) map.get(CHANNEL_ID_KEY)),
				UUID.fromString((String) map.get(SENDER_ID_KEY)), (long) map.get(TIMESTAMP_KEY),
				(String) map.get(MESSAGE_KEY));
	}
}
