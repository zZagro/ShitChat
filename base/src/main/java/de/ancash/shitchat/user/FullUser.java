package de.ancash.shitchat.user;

import java.util.UUID;

import de.ancash.shitchat.ShitChatImage;

public class FullUser extends User {

	private static final long serialVersionUID = 5911967466056074123L;

	private final FriendList friendList;
	private final MessageList messageReqList;
	private final ChannelList channelList;

	public FullUser(UUID id, String name, ShitChatImage img, FriendList friendList, MessageList messageReqList,
			ChannelList channelList) {
		super(id, name, img);
		this.friendList = friendList;
		this.messageReqList = messageReqList;
		this.channelList = channelList;
	}

	public ChannelList getChannelList() {
		return channelList;
	}

	public FriendList getFriendList() {
		return friendList;
	}

	public MessageList getMessageRequestList() {
		return messageReqList;
	}
}
