package de.ancash.shitchat.packet;

import java.io.Serializable;

import de.ancash.sockets.packet.Packet;

public abstract class ShitChatPacket implements Serializable {

	private static final long serialVersionUID = -3838949048838257013L;

	public static final short BASE_HEADER = 12321;

	public Packet toPacket() {
		Packet packet = new Packet(BASE_HEADER);
		packet.setSerializable(this);
		packet.isClientTarget(false);
		return packet;
	}
}
