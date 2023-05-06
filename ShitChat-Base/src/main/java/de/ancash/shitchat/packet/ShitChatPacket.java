package de.ancash.shitchat.packet;

import de.ancash.sockets.packet.Packet;

public abstract class ShitChatPacket extends Packet {

	private static final long serialVersionUID = -3838949048838257013L;

	public static final short BASE_HEADER = 12321;

	public ShitChatPacket() {
		super(BASE_HEADER);
	}
}
