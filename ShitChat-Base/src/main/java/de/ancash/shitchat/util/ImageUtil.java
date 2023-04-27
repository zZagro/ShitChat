package de.ancash.shitchat.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public final class ImageUtil {

	ImageUtil() {
	}
	
	public static byte[] serialize(BufferedImage img, String type) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ImageIO.write(img, type, bout);
		return bout.toByteArray();
	}
	
	public static BufferedImage deserialize(byte[] b) throws IOException {
		return ImageIO.read(new ByteArrayInputStream(b));
	}
}
