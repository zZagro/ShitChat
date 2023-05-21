package de.ancash.shitchat.packet.auth;

import de.ancash.shitchat.util.AuthenticationUtil;

public class SignUpPacket extends AuthenticationPacket {

	private static final long serialVersionUID = -785843351433745864L;

	private final String user;

	public SignUpPacket(String email, byte[] pass, String user) {
		super(email, pass);
		this.user = user;
	}

	public String getUsername() {
		return user;
	}

	public boolean isUsernameValid() {
		return AuthenticationUtil.isUsernameValid(user);
	}
}
