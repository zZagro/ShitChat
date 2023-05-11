package de.ancash.shitchat.server.account;

import static de.ancash.shitchat.ShitChatKeys.PROFILE_PIC_FILE;
import static de.ancash.shitchat.ShitChatKeys.UID;
import static de.ancash.shitchat.ShitChatKeys.USER_EMAIL;
import static de.ancash.shitchat.ShitChatKeys.USER_NAME;
import static de.ancash.shitchat.ShitChatKeys.USER_PASSWORD;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.ancash.libs.org.simpleyaml.configuration.file.YamlFile;
import de.ancash.libs.org.simpleyaml.exceptions.InvalidConfigurationException;
import de.ancash.shitchat.ShitChatImage;
import de.ancash.shitchat.user.User;

public class Account {

	private final File file;
	private String username;
	private String email;
	private UUID uid;
	private File profilePicFile;
	private byte[] pass;
	private final YamlFile yml;
	private long lastAccess = System.currentTimeMillis();
	private final ConcurrentHashMap<UUID, Session> sessions = new ConcurrentHashMap<>();

	@SuppressWarnings("nls")
	Account(File file) throws InvalidConfigurationException, IOException {
		this.file = file;
		this.profilePicFile = new File(file.getParentFile().getPath() + "/" + uid + "-pp");
		yml = new YamlFile(file);
		loadFromFile();
	}

	synchronized Session newSession() {
		Session s = new Session(UUID.randomUUID(), this);
		sessions.put(s.getSessionId(), s);
		return s;
	}

	Set<Session> getAllSessions() {
		return new HashSet<>(sessions.values());
	}

	Session removeSession(UUID sid) {
		return sessions.remove(sid);
	}

	public User toUser() throws IOException {
		return new User(uid, username, getProfilePic());
	}

	public Session getSession(UUID id) {
		return sessions.get(id);
	}

	public boolean hasSessions() {
		return !sessions.isEmpty();
	}

	public int countSessions() {
		return sessions.size();
	}

	public ShitChatImage getProfilePic() throws IOException {
		return new ShitChatImage(profilePicFile.exists() ? Files.readAllBytes(profilePicFile.toPath()) : null);
	}

	public long getLastAccess() {
		return lastAccess;
	}

	public Account updateLastAccess() {
		lastAccess = System.currentTimeMillis();
		return this;
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
		Files.write(profilePicFile.toPath(), img.asBytes(), StandardOpenOption.APPEND);
		YamlFile yml = new YamlFile(file);
		yml.load();
		yml.set(PROFILE_PIC_FILE, profilePicFile.getPath());
		yml.save();
		loadFromFile();
	}

	private void loadFromFile() throws InvalidConfigurationException, IOException {
		yml.load();
		username = yml.getString(USER_NAME);
		uid = UUID.fromString(yml.getString(UID));
		email = yml.getString(USER_EMAIL);
		List<Byte> temp = yml.getByteList(USER_PASSWORD);
		pass = new byte[temp.size()];
		profilePicFile = new File(yml.getString(PROFILE_PIC_FILE));
		IntStream.range(0, temp.size()).forEach(i -> pass[i] = temp.get(i));
		yml.save();
	}
}