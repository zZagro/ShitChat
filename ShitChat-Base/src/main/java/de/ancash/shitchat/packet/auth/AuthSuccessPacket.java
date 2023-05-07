package de.ancash.shitchat.packet.auth;

import java.util.UUID;

import de.ancash.shitchat.ShitChatImage;
import de.ancash.shitchat.packet.IImagePacket;
import de.ancash.shitchat.packet.ShitChatPacket;
import de.ancash.shitchat.user.User;

public class AuthSuccessPacket extends ShitChatPacket implements IImagePacket {

	private static final long serialVersionUID = 3272540097556748864L;

	private final UUID sId;
	private final User user;

	public AuthSuccessPacket(UUID sId, User user) {
		this.sId = sId;
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public UUID getSID() {
		return sId;
	}

	@Override
	public ShitChatImage getImage() {
		return user.getProfilePic();
	}
}
