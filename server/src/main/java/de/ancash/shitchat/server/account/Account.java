package de.ancash.shitchat.server.account;

import static de.ancash.shitchat.ShitChatKeys.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import de.ancash.shitchat.ShitChatImage;
import de.ancash.shitchat.channel.AbstractChannel;
import de.ancash.shitchat.channel.ChannelType;
import de.ancash.shitchat.channel.DirectChannel;
import de.ancash.shitchat.channel.GroupChannel;
import de.ancash.shitchat.packet.user.RequestType;
import de.ancash.shitchat.server.channel.ChannelRegistry;
import de.ancash.shitchat.server.client.Client;
import de.ancash.shitchat.user.FriendList;
import de.ancash.shitchat.user.FullUser;
import de.ancash.shitchat.user.MessageList;
import de.ancash.shitchat.user.User;

public class Account {

	private final File file;
	private String username;
	private String email;
	private UUID uid;
	private File profilePicFile;
	private final Map<RequestType, Set<UUID>> incomingReqs = new HashMap<>();
	private final Map<RequestType, Set<UUID>> outgoingReqs = new HashMap<>();
	private final Map<RequestType, Set<UUID>> acceptedReqs = new HashMap<>();
	private final Map<ChannelType, Set<UUID>> channels = new HashMap<>();
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

	synchronized Session newSession(Client client) {
		Session s = new Session(UUID.randomUUID(), this, client);
		sessions.put(s.getSessionId(), s);
		return s;
	}

	public synchronized Set<Session> getAllSessions() {
		return new HashSet<>(sessions.values());
	}

	synchronized Session removeSession(UUID sid) {
		return sessions.remove(sid);
	}

	public synchronized Set<DirectChannel> getDirectChannels(ChannelRegistry cr) {
		return channels.get(ChannelType.DIRECT).stream()
				.map(i -> (DirectChannel) cr.getChannelById(i, ChannelType.DIRECT)).filter(ch -> ch != null)
				.collect(Collectors.toSet());
	}

	public synchronized Set<GroupChannel> getGroupChannels(ChannelRegistry cr) {
		return channels.get(ChannelType.DIRECT).stream()
				.map(i -> (GroupChannel) cr.getChannelById(i, ChannelType.DIRECT)).filter(ch -> ch != null)
				.collect(Collectors.toSet());
	}

	public synchronized DirectChannel getDirectChannel(ChannelRegistry cr, UUID id) {
		return (DirectChannel) cr.getChannelById(id, ChannelType.DIRECT);
	}

	public synchronized User toUser() {
		return new User(uid, username, getProfilePic());
	}

	public synchronized FullUser toFullUser(AccountRegistry registry) {
		return new FullUser(uid, username, getProfilePic(),
				new FriendList(getUser(incomingReqs.get(RequestType.FRIEND), registry),
						getUser(outgoingReqs.get(RequestType.FRIEND), registry),
						getUser(acceptedReqs.get(RequestType.FRIEND), registry)),
				new MessageList(getUser(incomingReqs.get(RequestType.MESSAGE), registry),
						getUser(outgoingReqs.get(RequestType.MESSAGE), registry),
						getUser(acceptedReqs.get(RequestType.MESSAGE), registry)));
	}

	private synchronized Set<User> getUser(Set<UUID> ids, AccountRegistry registry) {
		return ids.stream().map(registry::getAccount).filter(a -> a != null).map(Account::toUser).filter(a -> a != null)
				.collect(Collectors.toSet());
	}

	public synchronized Session getSession(UUID id) {
		return sessions.get(id);
	}

	public synchronized boolean hasSessions() {
		return !sessions.isEmpty();
	}

	public synchronized int countSessions() {
		return sessions.size();
	}

	@SuppressWarnings("nls")
	public synchronized ShitChatImage getProfilePic() {
		try {
			return new ShitChatImage(profilePicFile.exists() ? Files.readAllBytes(profilePicFile.toPath()) : null);
		} catch (IOException e) {
			System.err.println("could not get image at " + profilePicFile);
			e.printStackTrace();
			return new ShitChatImage(null);
		}
	}

	public synchronized long getLastAccess() {
		return lastAccess;
	}

	public synchronized Account updateLastAccess() {
		lastAccess = System.currentTimeMillis();
		return this;
	}

	public synchronized byte[] getPassword() {
		return pass;
	}

	public synchronized boolean hasDirectChatTo(UUID to, ChannelRegistry registry) {
		return channels.get(ChannelType.DIRECT).stream().map(id -> registry.getChannelById(id, ChannelType.DIRECT))
				.map(AbstractChannel::getUsers).flatMap(Collection::stream).filter(uid -> uid != null && uid.equals(to))
				.findAny().isEmpty();
	}

	public synchronized UUID getDirectChannelTo(UUID to, ChannelRegistry registry) {
		for (UUID id : channels.get(ChannelType.DIRECT)) {
			DirectChannel ch = (DirectChannel) registry.getChannelById(id, ChannelType.DIRECT);
			if (ch.getUsers().contains(to))
				return id;
		}
		return null;
	}

	public synchronized void setPassword(byte[] pass) throws InvalidConfigurationException, IOException {
		yml.load();
		yml.set(USER_PASSWORD, IntStream.range(0, pass.length).boxed().map(i -> pass[i]).collect(Collectors.toList()));
		yml.save();
		loadFromFile();
	}

	public String getEmail() {
		return email;
	}

	public String getUsername() {
		return username;
	}

	public UUID getUserId() {
		return uid;
	}

	@Override
	public int hashCode() {
		return uid.hashCode();
	}

	public synchronized void setEmail(String email) throws InvalidConfigurationException, IOException {
		yml.load();
		yml.set(USER_EMAIL, email);
		yml.save();
		loadFromFile();
	}

	public synchronized void setUsername(String name) throws InvalidConfigurationException, IOException {
		yml.load();
		yml.set(USER_NAME, name);
		yml.save();
		loadFromFile();
	}

	public synchronized boolean isIncomingReq(UUID id, RequestType type) {
		return incomingReqs.get(type).contains(id);
	}

	public synchronized boolean isOutgoingReq(UUID id, RequestType type) {
		return outgoingReqs.get(type).contains(id);
	}

	public synchronized boolean isAcceptedReq(UUID id, RequestType type) {
		return acceptedReqs.get(type).contains(id);
	}

	@SuppressWarnings("nls")
	public synchronized boolean addOutgoingReq(UUID id, RequestType type) {
		if (outgoingReqs.get(type).contains(id) || acceptedReqs.get(type).contains(id))
			return false;
		outgoingReqs.get(type).add(id);
		try {
			yml.load();
			yml.set(getPath(OUTGOING_REQUESTS, type),
					outgoingReqs.get(type).stream().map(UUID::toString).collect(Collectors.toList()));
			yml.save();
			loadFromFile();
		} catch (IOException ex) {
			System.err.println("add outgoing m req");
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	@SuppressWarnings("nls")
	private String getPath(String base, RequestType type) {
		return String.join("-", base, type.name());
	}

	@SuppressWarnings("nls")
	public synchronized boolean addAcceptedReq(UUID id, RequestType type) {
		incomingReqs.get(type).remove(id);
		outgoingReqs.get(type).remove(id);
		if (!acceptedReqs.get(type).contains(id))
			acceptedReqs.get(type).add(id);
		try {
			yml.load();
			yml.set(getPath(OUTGOING_REQUESTS, type),
					outgoingReqs.get(type).stream().map(UUID::toString).collect(Collectors.toList()));
			yml.set(getPath(INCOMING_REQUESTS, type),
					incomingReqs.get(type).stream().map(UUID::toString).collect(Collectors.toList()));
			yml.set(getPath(ACCEPTED_REQUESTS, type),
					acceptedReqs.get(type).stream().map(UUID::toString).collect(Collectors.toList()));
			yml.save();
			loadFromFile();
		} catch (IOException ex) {
			System.err.println("add accepted m req");
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	@SuppressWarnings("nls")
	public synchronized void removeOutgoingReq(UUID target, RequestType type) {
		outgoingReqs.get(type).remove(target);
		try {
			yml.load();
			yml.set(getPath(OUTGOING_REQUESTS, type),
					outgoingReqs.get(type).stream().map(UUID::toString).collect(Collectors.toList()));
			yml.save();
			loadFromFile();
		} catch (IOException ex) {
			System.err.println("remove outgoing m req");
			ex.printStackTrace();
		}
	}

	@SuppressWarnings("nls")
	public synchronized void removeIncomingReq(UUID target, RequestType type) {
		incomingReqs.get(type).remove(target);
		try {
			yml.load();
			yml.set(getPath(INCOMING_REQUESTS, type),
					incomingReqs.get(type).stream().map(UUID::toString).collect(Collectors.toList()));
			yml.save();
			loadFromFile();
		} catch (IOException ex) {
			System.err.println("remove outgoing req ");
			ex.printStackTrace();
		}
	}

	@SuppressWarnings("nls")
	public synchronized boolean addIncomingReq(UUID id, RequestType type) {
		if (incomingReqs.get(type).contains(id) || acceptedReqs.get(type).contains(id))
			return false;
		incomingReqs.get(type).add(id);
		try {
			yml.load();
			yml.set(getPath(INCOMING_REQUESTS, type),
					incomingReqs.get(type).stream().map(UUID::toString).collect(Collectors.toList()));
			yml.save();
			loadFromFile();
		} catch (IOException ex) {
			System.err.println("add incoming m req");
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	@SuppressWarnings("nls")
	public synchronized void setProfilePic(ShitChatImage img) throws IOException {
		File to = new File(file.getParentFile().getPath() + "/" + uid + "-pp");
		to.delete();
		to.createNewFile();
		Files.write(to.toPath(), img.asBytes(), StandardOpenOption.APPEND);
		YamlFile yml = new YamlFile(file);
		yml.load();
		yml.set(PROFILE_PIC_FILE, to.getPath());
		yml.save();
		loadFromFile();
		System.out.println("wrote " + img.asBytes().length + " to " + to);
	}

	private synchronized void loadFromFile() throws InvalidConfigurationException, IOException {
		yml.load();
		username = yml.getString(USER_NAME);
		uid = UUID.fromString(yml.getString(UID));
		email = yml.getString(USER_EMAIL);
		incomingReqs.clear();
		outgoingReqs.clear();
		acceptedReqs.clear();
		channels.clear();
		incomingReqs.put(RequestType.FRIEND,
				Optional.ofNullable(yml.getStringList(getPath(INCOMING_REQUESTS, RequestType.FRIEND)))
						.orElse(new ArrayList<>()).stream().map(UUID::fromString).collect(Collectors.toSet()));
		outgoingReqs.put(RequestType.FRIEND,
				Optional.ofNullable(yml.getStringList(getPath(OUTGOING_REQUESTS, RequestType.FRIEND)))
						.orElse(new ArrayList<>()).stream().map(UUID::fromString).collect(Collectors.toSet()));
		acceptedReqs.put(RequestType.FRIEND,
				Optional.ofNullable(yml.getStringList(getPath(ACCEPTED_REQUESTS, RequestType.FRIEND)))
						.orElse(new ArrayList<>()).stream().map(UUID::fromString).collect(Collectors.toSet()));
		incomingReqs.put(RequestType.MESSAGE,
				Optional.ofNullable(yml.getStringList(getPath(INCOMING_REQUESTS, RequestType.MESSAGE)))
						.orElse(new ArrayList<>()).stream().map(UUID::fromString).collect(Collectors.toSet()));
		outgoingReqs.put(RequestType.MESSAGE,
				Optional.ofNullable(yml.getStringList(getPath(OUTGOING_REQUESTS, RequestType.MESSAGE)))
						.orElse(new ArrayList<>()).stream().map(UUID::fromString).collect(Collectors.toSet()));
		acceptedReqs.put(RequestType.MESSAGE,
				Optional.ofNullable(yml.getStringList(getPath(ACCEPTED_REQUESTS, RequestType.MESSAGE)))
						.orElse(new ArrayList<>()).stream().map(UUID::fromString).collect(Collectors.toSet()));

		channels.put(ChannelType.DIRECT, Optional.ofNullable(yml.getStringList(DIRECT_CHANNELS))
				.orElse(new ArrayList<>()).stream().map(UUID::fromString).collect(Collectors.toSet()));
		channels.put(ChannelType.GROUP, Optional.ofNullable(yml.getStringList(GROUP_CHANNELS)).orElse(new ArrayList<>())
				.stream().map(UUID::fromString).collect(Collectors.toSet()));

		List<Byte> temp = yml.getByteList(USER_PASSWORD);
		pass = new byte[temp.size()];
		profilePicFile = new File(yml.getString(PROFILE_PIC_FILE));
		IntStream.range(0, temp.size()).forEach(i -> pass[i] = temp.get(i));
		yml.save();
	}

	public synchronized void addChannel(UUID channelId, ChannelType type)
			throws InvalidConfigurationException, IOException {
		channels.get(type).add(channelId);
		yml.load();
		yml.set(GROUP_CHANNELS,
				channels.get(ChannelType.GROUP).stream().map(UUID::toString).collect(Collectors.toList()));
		yml.set(DIRECT_CHANNELS,
				channels.get(ChannelType.DIRECT).stream().map(UUID::toString).collect(Collectors.toList()));
		yml.save();
		loadFromFile();
	}
}
