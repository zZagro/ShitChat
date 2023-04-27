package de.ancash.shitchat.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.validator.routines.EmailValidator;

@SuppressWarnings("nls")
public final class AuthenticationUtil {

	private static final int salts = 16;
	private static SecretKeyFactory f;
	
	static {
		try {
			f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public static byte[] hashPassword(char[] c) {
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[salts];
		random.nextBytes(salt);
		KeySpec spec = new PBEKeySpec(c, salt, 65536, 128);
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
