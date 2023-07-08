package de.ancash.shitchat.client;

import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import de.ancash.shitchat.ShitChatImage;
import de.ancash.shitchat.ShitChatPlaceholder;
import de.ancash.shitchat.packet.profile.PasswordChangePacket;
import de.ancash.shitchat.packet.profile.ProfileChangeResultPacket;
import de.ancash.shitchat.packet.profile.ProfilePicChangePacket;
import de.ancash.shitchat.packet.profile.UsernameChangePacket;
import de.ancash.sockets.packet.PacketFuture;

public class ProfileEditHandler {

	private final ShitChatClient client;

	public ProfileEditHandler(ShitChatClient client) {
		this.client = client;
	}

	public Future<Optional<String>> changePP(byte[] bb) {
		if (!client.isAuthenticated()) {
			client.onPPChangeFailed(ShitChatPlaceholder.NOT_AUTHENTICATED);
			return client.pool.submit(() -> Optional.of(ShitChatPlaceholder.NOT_AUTHENTICATED));
		}
		return client.pool.submit(() -> changePP(
				client.sendShitChatPacket0(new ProfilePicChangePacket(client.sid, new ShitChatImage(bb)), true)));
	}

	private Optional<String> changePP(PacketFuture future) {
		Optional<ProfileChangeResultPacket> result = future.get(30, TimeUnit.SECONDS);
		if (!result.isPresent()) {
			client.onPPChangeFailed(ShitChatPlaceholder.INTERNAL_ERROR);
			return Optional.of(ShitChatPlaceholder.INTERNAL_ERROR);
		}
		System.out.println((System.nanoTime() - future.getTimestamp()) / 1000D + " micros");
		ProfileChangeResultPacket r = result.get();
		if (r.wasSuccessful()) {
			client.user = r.getNewUser();
			client.onPPChange(client.user);
			return Optional.empty();
		}
		client.onPPChangeFailed(r.getReason());
		return Optional.of(r.getReason());
	}

	public Future<Optional<String>> changePassword(byte[] oldPass, byte[] newPass) {
		if (!client.isAuthenticated()) {
			client.onChangePasswordFailed(ShitChatPlaceholder.NOT_AUTHENTICATED);
			return client.pool.submit(() -> Optional.of(ShitChatPlaceholder.NOT_AUTHENTICATED));
		}
		return client.pool.submit(() -> changePassword(
				client.sendShitChatPacket0(new PasswordChangePacket(client.sid, oldPass, newPass), true)));
	}

	private Optional<String> changePassword(PacketFuture future) {
		Optional<ProfileChangeResultPacket> result = future.get(30, TimeUnit.SECONDS);
		if (!result.isPresent()) {
			client.onChangePasswordFailed(ShitChatPlaceholder.INTERNAL_ERROR);
			return Optional.of(ShitChatPlaceholder.INTERNAL_ERROR);
		}
		System.out.println((System.nanoTime() - future.getTimestamp()) / 1000D + " micros");
		ProfileChangeResultPacket r = result.get();
		if (r.wasSuccessful()) {
			client.user = r.getNewUser();
			client.onChangePassword();
			return Optional.empty();
		}
		client.onChangePasswordFailed(r.getReason());
		return Optional.of(r.getReason());
	}

	public Future<Optional<String>> changeUserName(String nun) {
		if (!client.isAuthenticated()) {
			client.onUserNameChangeFailed(ShitChatPlaceholder.NOT_AUTHENTICATED);
			return client.pool.submit(() -> Optional.of(ShitChatPlaceholder.NOT_AUTHENTICATED));
		}
		return client.pool.submit(
				() -> changeUserName(client.sendShitChatPacket0(new UsernameChangePacket(client.sid, nun), true)));
	}

	private Optional<String> changeUserName(PacketFuture future) {
		Optional<ProfileChangeResultPacket> result = future.get(30, TimeUnit.SECONDS);
		if (!result.isPresent()) {
			client.onUserNameChangeFailed(ShitChatPlaceholder.INTERNAL_ERROR);
			return Optional.of(ShitChatPlaceholder.INTERNAL_ERROR);
		}
		System.out.println((System.nanoTime() - future.getTimestamp()) / 1000D + " micros");
		ProfileChangeResultPacket r = result.get();
		if (r.wasSuccessful()) {
			client.user = r.getNewUser();
			client.onUserNameChange(client.user);
			return Optional.empty();
		}
		client.onUserNameChangeFailed(r.getReason());
		return Optional.of(r.getReason());
	}
}
