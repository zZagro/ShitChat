package de.ancash.shitchat.server.account;

import java.util.UUID;
import java.util.function.Consumer;

import de.ancash.shitchat.packet.ShitChatPacket;
import de.ancash.shitchat.server.client.Client;

public class Session {

	private Account acc;
	private final UUID id;
	private final long start = System.currentTimeMillis();
	private Consumer<Session> onExit;
	private final Client client;

	Session(UUID id, Account acc, Client client) {
		this.id = id;
		this.client = client;
		this.acc = acc;
	}

	Session setOnExit(Consumer<Session> c) {
		this.onExit = c;
		return this;
	}

	synchronized void exit() {
		onExit.accept(this);
		acc = null;
		onExit = null;
	}

	public void sendPacket(ShitChatPacket packet) {
		client.putWrite(packet.toPacket().toBytes());
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

	public Client getClient() {
		return client;
	}
}
