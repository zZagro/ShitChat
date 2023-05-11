package de.ancash.shitchat.packet.auth;

import de.ancash.shitchat.packet.ShitChatPacket;
import de.ancash.shitchat.util.AuthenticationUtil;
import de.ancash.sockets.packet.Packet;

public abstract class AuthenticationPacket extends ShitChatPacket {

	private static final long serialVersionUID = -7272743600674162088L;

	protected final String email;
	protected final byte[] pass;

	@SuppressWarnings("nls")
	public AuthenticationPacket(String email, byte[] pass) {
		if (!AuthenticationUtil.isEmailValid(email))
			throw new IllegalArgumentException(String.format("invalid email '%s'", email));
		this.email = email;
		this.pass = pass;
	}

	@Override
	public Packet toPacket() {
		Packet p = super.toPacket();
		p.setAwaitResponse(true);
		return p;
	}

	public String getEmail() {
		return email;
	}

	public byte[] getPassword() {
		return pass;
	}
}
