package de.ancash.shitchat.server.account;

import java.util.UUID;
import java.util.function.Consumer;

public class Session {

	private Account acc;
	private final UUID id;
	private final long start = System.currentTimeMillis();
	private Consumer<Session> onExit;

	Session(UUID id, Account acc) {
		this.id = id;
		this.acc = acc;
	}

	Session setOnExit(Consumer<Session> c) {
		this.onExit = c;
		return this;
	}

	public synchronized void exit() {
		onExit.accept(this);
		acc = null;
		onExit = null;
	}

	public synchronized boolean isValid() {
		return acc != null && onExit != null;
	}

	public Account getAccount() {
		return acc.updateLastAccess();
	}

	public Session updateLastAccess() {
		acc.updateLastAccess();
		return this;
	}

	public long getStart() {
		return start;
	}

	public UUID getSessionId() {
		return id;
	}
}
