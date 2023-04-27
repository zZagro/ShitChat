package de.ancash.shitchat.packet.profile;

import java.util.UUID;

import de.ancash.shitchat.ShitChatImage;
import de.ancash.shitchat.packet.IImagePacket;
import de.ancash.shitchat.packet.SessionedShitChatPacket;

public class ProfilePicChangePacket extends SessionedShitChatPacket implements IImagePacket, IProfilePacket{

	private static final long serialVersionUID = 1492851281950656931L;

	private final ShitChatImage img;
	
	public ProfilePicChangePacket(UUID sessionId, ShitChatImage img) {
		super(sessionId);
		this.img = img;
	}

	@Override
	public ShitChatImage getImage() {
		return img;
	}
}
