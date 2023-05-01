package de.ancash.shitchat;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;

import de.ancash.shitchat.util.ImageUtil;

public class ShitChatImage implements Serializable{

	private static final long serialVersionUID = 5811895656791893313L;
	
	private final byte[] img;
	private final String type;
	private transient BufferedImage bimg;
	
	public ShitChatImage(BufferedImage img, String type) throws IOException {
		this(ImageUtil.serialize(img, type), type);
	}
	
	public ShitChatImage(byte[] img, String type) {
		this.img = img;
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
	public byte[] getImage() {
		return img;
	}
	
	public BufferedImage deserialize() throws IOException {
		if(bimg != null)
			return bimg;
		bimg = ImageUtil.deserialize(img);
		return bimg;
	}
	
	public static enum Type{
		CUSTOM, DEFAULT;
	}
}
