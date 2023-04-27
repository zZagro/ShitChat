package de.ancash.shitchat.account;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.ancash.libs.org.simpleyaml.configuration.file.YamlFile;
import de.ancash.libs.org.simpleyaml.exceptions.InvalidConfigurationException;
import de.ancash.shitchat.ShitChatImage;
import de.ancash.shitchat.ShitChatServer;

import static de.ancash.shitchat.ShitChatKeys.*;

public class Account {
	
	private final File file;
	private String username;
	private String email;
	private final UUID uid;
	private final File profilePicFile;
	private String profilePicType;
	private byte[] pass;
	private final YamlFile yml;
	
	@SuppressWarnings("nls")
	public Account(File file) throws InvalidConfigurationException, IOException {
		this.file = file;
		this.uid = UUID.fromString(file.getName().split(".yml")[0]);
		this.profilePicFile = new File(file.getParentFile().getPath() + "/" + uid + "-pp");
		yml = new YamlFile(file);
		if(!profilePicFile.exists())
			profilePicFile.createNewFile();
		loadFromFile();
	}
	
	public ShitChatImage getProfilePic() throws IOException {
		return new ShitChatImage(Files.readAllBytes(profilePicFile.toPath()), profilePicType);
	}
	
	public byte[] getPassword() {
		return pass;
	}
	
	public void setPassword(byte[] pass) throws InvalidConfigurationException, IOException {
		yml.load();
		yml.set(USER_PASSWORD, IntStream.range(0, pass.length).boxed().map(i -> pass[i]).collect(Collectors.toList()));
		yml.save();
		loadFromFile();
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getUserName() {
		return username;
	}
	
	public UUID getId() {
		return uid;
	}
	
	@Override
	public int hashCode() {
		return uid.hashCode();
	}
	
	public void setEmail(String email) throws InvalidConfigurationException, IOException {
		yml.load();
		yml.set(USER_EMAIL, email);
		yml.save();
		loadFromFile();
	}
	
	public void setUserName(String name) throws InvalidConfigurationException, IOException {
		yml.load();
		yml.set(USER_NAME, name);
		yml.save();
		loadFromFile();
	}
	
	public void setProfilePic(ShitChatImage img) throws IOException {
		profilePicFile.delete();
		profilePicFile.createNewFile();
		Files.write(profilePicFile.toPath(), img.getImage(), StandardOpenOption.APPEND);
		YamlFile yml = new YamlFile(file);
		yml.load();
		yml.set(PROFILE_PIC_TYPE, img.getType());
		yml.set(PROFILE_PIC_FILE, profilePicFile.getPath());
		yml.save();
		loadFromFile();
	}
	
	@SuppressWarnings("nls")
	private void checkProperties(YamlFile yml) {
		if(!yml.contains(USER_NAME))
			yml.set(USER_NAME, "Joe");
		if(!yml.contains(USER_EMAIL))
			yml.set(USER_EMAIL, "joe@mama.com");
		if(!yml.contains(PROFILE_PIC_FILE))
			yml.set(PROFILE_PIC_FILE, ShitChatServer.getInstance().getDefaultProfilePicFile());
		if(!yml.contains(USER_PASSWORD))
			yml.set(USER_PASSWORD, Arrays.asList((byte) 0));
	}
	
	private void loadFromFile() throws InvalidConfigurationException, IOException {
		yml.load();
		checkProperties(yml);
		username = yml.getString(USER_NAME);
		email = yml.getString(USER_EMAIL);
		profilePicType = yml.getString(PROFILE_PIC_TYPE);
		List<Byte> temp = yml.getByteList(USER_PASSWORD);
		pass = new byte[temp.size()];
		IntStream.range(0, temp.size()).forEach(i -> pass[i] = temp.get(i));
		yml.save();
	}
}
