package de.ancash.shitchat.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FriendList implements Serializable {

	private static final long serialVersionUID = 1940819498281615178L;

	private final List<User> list;
	private final List<User> pending;

	public FriendList(List<User> list, List<User> pending) {
		this.list = new ArrayList<>(list);
		this.pending = new ArrayList<>(pending);
	}

	public List<User> getList() {
		return Collections.unmodifiableList(list);
	}

	public List<User> getPending() {
		return Collections.unmodifiableList(pending);
	}
}
