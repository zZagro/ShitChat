package de.ancash.shitchat.user;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import de.ancash.shitchat.channel.AbstractChannel;
import de.ancash.shitchat.channel.ChannelType;
import de.ancash.shitchat.channel.DirectChannel;
import de.ancash.shitchat.channel.GroupChannel;

public class ChannelList implements Serializable {

	private static final long serialVersionUID = 2584028783999240449L;
	private final Map<ChannelType, Set<AbstractChannel>> channels;

	public ChannelList(Map<ChannelType, Set<AbstractChannel>> channels) {
		this.channels = channels;
	}

	public Set<DirectChannel> getDirectChannels() {
		return channels.get(ChannelType.DIRECT).stream().map(a -> (DirectChannel) a).collect(Collectors.toSet());
	}

	public Set<GroupChannel> getGroupChannels() {
		return channels.get(ChannelType.GROUP).stream().map(a -> (GroupChannel) a).collect(Collectors.toSet());
	}
}
