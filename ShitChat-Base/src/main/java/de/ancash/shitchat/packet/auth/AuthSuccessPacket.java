package de.ancash.shitchat.packet.auth;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;

import de.ancash.shitchat.packet.ShitChatPacket;

public class AuthSuccessPacket extends ShitChatPacket{

	private static final long serialVersionUID = 3272540097556748864L;

	private final UUID sId;
	private final String user;
	private final byte[] pp;
	
	public AuthSuccessPacket(UUID sId, String user, byte[] pp) {
		this.sId = sId;
		this.pp = pp;
		this.user = user;
	}

	public BufferedImage getProfilePic() throws IOException {
		return ImageIO.read(new ByteArrayInputStream(pp));
	}
	
	public String getUserName() {
		return user;
	}
	
	public UUID getSID() {
		return sId;
	}
}
