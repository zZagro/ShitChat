package de.ancash.shitchat.user;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MessageList implements Serializable {

	private static final long serialVersionUID = 5471266172424407801L;

	private final Set<User> incoming;
	private final Set<User> outgoing;
	private final Set<User> accepted;

	public MessageList(Set<User> incoming, Set<User> outgoing, Set<User> accepted) {
		this.incoming = new HashSet<>(incoming);
		this.outgoing = new HashSet<>(outgoing);
		this.accepted = new HashSet<>(accepted);
	}

	public Set<User> getIncoming() {
		return Collections.unmodifiableSet(incoming);
	}

	public Set<User> getOutgoing() {
		return Collections.unmodifiableSet(outgoing);
	}

	public Set<User> getAccepted() {
		return Collections.unmodifiableSet(accepted);
	}
}
