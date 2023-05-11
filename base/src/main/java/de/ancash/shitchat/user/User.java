package de.ancash.shitchat.user;

import java.io.Serializable;
import java.util.UUID;

import de.ancash.shitchat.ShitChatImage;

public class User implements Serializable {

	private static final long serialVersionUID = 2606950742075748380L;

	private final UUID id;
	private final String name;
	private final ShitChatImage img;

	public User(UUID id, String name, ShitChatImage img) {
		this.id = id;
		this.name = name;
		this.img = img;
	}

	public ShitChatImage getProfilePic() {
		return img;
	}

	public String getName() {
		return name;
	}

	public UUID getUId() {
		return id;
	}
}
