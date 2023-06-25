package de.ancash.shitchat.packet.profile;

import java.util.UUID;

import de.ancash.shitchat.packet.SessionedPacket;

public class PasswordChangePacket extends SessionedPacket implements IProfilePacket {

	private static final long serialVersionUID = -8785640453975804471L;

	private final byte[] newPass;
	private final byte[] oldPass;

	public PasswordChangePacket(UUID sessionId, byte[] oldPass, byte[] newPass) {
		super(sessionId);
		this.newPass = newPass;
		this.oldPass = oldPass;
	}

	public byte[] getOldPass() {
		return oldPass;
	}

	public byte[] getNewPass() {
		return newPass;
	}
}
