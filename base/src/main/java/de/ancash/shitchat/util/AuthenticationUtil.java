package de.ancash.shitchat.util;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.validator.routines.EmailValidator;

@SuppressWarnings("nls")
public final class AuthenticationUtil {

	private static SecretKeyFactory f;
	public static int MAX_USERNAME_LENGTH = 32;

	static {
		try {
			f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}

	public static byte[] hashPassword(String email, char[] c) {
		KeySpec spec = new PBEKeySpec(c, email.getBytes(StandardCharsets.UTF_8), 65536, 256);
		try {
			return f.generateSecret(spec).getEncoded();
		} catch (InvalidKeySpecException e) {
			throw new IllegalStateException(e);
		}
	}

	public static boolean isUsernameValid(String n) {
		for (char c : n.toCharArray())
			if (c < 32 || c > 126)
				return false;
		return n.length() > 0 && n.length() <= MAX_USERNAME_LENGTH;
	}

	public static boolean isEmailValid(String e) {
		return EmailValidator.getInstance().isValid(e);
	}

	AuthenticationUtil() {
	}

}
