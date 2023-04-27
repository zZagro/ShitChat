package de.ancash.shitchat;

public class ShitChatServer {

	private static ShitChatServer singleton;
	
	public static void main(String[] args) {
		singleton = new ShitChatServer();
	}
	
	public static ShitChatServer getInstance() {
		return singleton;
	}
	
	public String getDefaultProfilePicFile() {
		return defaultProfilePicFile;
	}

	private final String defaultProfilePicFile = "settings/default-profile-pic.yml";
	

}
