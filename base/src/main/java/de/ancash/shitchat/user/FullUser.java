package de.ancash.shitchat.user;

import java.util.UUID;

import de.ancash.shitchat.ShitChatImage;

public class FullUser extends User {

	private static final long serialVersionUID = 5911967466056074123L;

	private final FriendList friendList;

	public FullUser(UUID id, String name, ShitChatImage img, FriendList friendList) {
		super(id, name, img);
		this.friendList = friendList;
	}

	public FriendList getFriendList() {
		return friendList;
	}
}
