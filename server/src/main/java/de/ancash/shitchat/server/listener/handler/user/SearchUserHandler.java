package de.ancash.shitchat.server.listener.handler.user;

import java.util.List;
import java.util.stream.Collectors;

import de.ancash.shitchat.ShitChatPlaceholder;
import de.ancash.shitchat.packet.profile.ProfileChangeResultPacket;
import de.ancash.shitchat.packet.user.SearchUserPacket;
import de.ancash.shitchat.packet.user.SearchUserResultPacket;
import de.ancash.shitchat.server.account.Account;
import de.ancash.shitchat.server.account.AccountRegistry;
import de.ancash.shitchat.server.client.Client;
import de.ancash.shitchat.server.listener.handler.HandlerUtil;
import de.ancash.sockets.packet.Packet;

public class SearchUserHandler {

	private final AccountRegistry registry;

	public SearchUserHandler(AccountRegistry registry) {
		this.registry = registry;
	}

	@SuppressWarnings("nls")
	public void searchUser(Client cl, SearchUserPacket ucp, Packet packet) {
		if (!HandlerUtil.validateSID(registry, cl, ucp, packet)) {
			System.out.println(cl.getRemoteAddress() + " change username invalid sid");
			packet.setSerializable(
					new ProfileChangeResultPacket(ucp.getSessionId(), null, ShitChatPlaceholder.INVALID_SESSION));
			cl.putWrite(packet.toBytes());
			return;
		}
		Account acc = registry.getSession(ucp.getSessionId()).getAccount();
		List<String> searched = registry.getAllUsernames().stream()
				.filter(s -> s.toLowerCase().startsWith(ucp.getUser().toLowerCase()) && !s.equals(acc.getUsername()))
				.sorted().collect(Collectors.toList());
		System.out.println(cl.getRemoteAddress() + " searched & found " + searched.size() + " users");
		searched = searched.subList(0, Math.min(10, searched.size()));
		packet.setSerializable(
				new SearchUserResultPacket(ucp.getSessionId(), searched.stream().map(registry::getUIDByUsername)
						.map(registry::getAccount).map(Account::toUser).collect(Collectors.toList()), null));
		cl.putWrite(packet.toBytes());
	}
}
