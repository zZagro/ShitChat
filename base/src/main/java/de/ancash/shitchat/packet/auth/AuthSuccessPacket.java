package de.ancash.shitchat.packet.auth;

import java.util.UUID;

import de.ancash.shitchat.ShitChatImage;
import de.ancash.shitchat.packet.IImagePacket;
import de.ancash.shitchat.user.FullUser;

public class AuthSuccessPacket extends AuthResultPacket implements IImagePacket {

	private static final long serialVersionUID = 3272540097556748864L;

	private final UUID sId;
	private final FullUser user;

	public AuthSuccessPacket(UUID sId, FullUser user) {
		this.sId = sId;
		this.user = user;
	}

	public FullUser getUser() {
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
