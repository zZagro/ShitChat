package de.ancash.shitchat.packet.auth;

public class LoginPacket extends AuthenticationPacket {

	private static final long serialVersionUID = -7344764230466722586L;

	public LoginPacket(String email, byte[] pass) {
		super(email, pass);
	}

}
