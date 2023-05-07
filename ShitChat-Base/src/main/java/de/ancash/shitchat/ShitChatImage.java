package de.ancash.shitchat;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.Arrays;

public class ShitChatImage implements Serializable {

	private static final long serialVersionUID = 5811895656791893313L;

	private final byte[] img;

	public ShitChatImage(byte[] img) {
		this.img = img;
	}

	public ByteArrayInputStream asStream() {
		return new ByteArrayInputStream(img);
	}

	public byte[] asBytes() {
		return Arrays.copyOf(img, img.length);
	}
}
