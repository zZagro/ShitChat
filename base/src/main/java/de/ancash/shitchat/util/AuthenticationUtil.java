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

	public static boolean isEmailValid(String e) {
		return EmailValidator.getInstance().isValid(e);
	}

	AuthenticationUtil() {
	}

}
