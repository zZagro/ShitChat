package de.ancash.shitchat.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.management.RuntimeErrorException;

import de.ancash.shitchat.account.Account;

public class Session {
	
	private static final Map<UUID, Session> sessions = new HashMap<UUID, Session>();
	private static final Map<Integer, List<Session>> hashedSessions = new HashMap<>();
	
	@SuppressWarnings("nls")
	public static synchronized Session newSession(Account acc) {
		Session s = new Session(UUID.randomUUID(), acc);
		if(sessions.containsKey(s.getSessionId()))
			throw new RuntimeErrorException(new Error("duplicate sid"));
		sessions.put(s.getSessionId(), s);
		hashedSessions.computeIfAbsent(s.hashCode(), k -> new ArrayList<>());
		hashedSessions.get(s.hashCode()).add(s);
		return s;
	}
	
	private final UUID id;
	
	private Session(UUID id, Account acc) {
		this.id = id;
	}
	
	public UUID getSessionId() {
		return id;
	}
}
