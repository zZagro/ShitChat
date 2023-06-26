package de.zagro.shitchat;

import android.content.Context;
import android.widget.Toast;

import de.ancash.libs.org.bukkit.event.EventHandler;
import de.ancash.shitchat.client.ShitChatClient;
import de.ancash.shitchat.user.User;
import de.ancash.sockets.events.ClientConnectEvent;
import de.ancash.sockets.events.ClientDisconnectEvent;
import de.ancash.sockets.events.ClientPacketReceiveEvent;

public class ShitChatManager extends ShitChatClient {

    public ShitChatManager(String address, int port) {
        super(address, port);
    }

    @Override
    public void onPPChangeFailed(String s) {

    }

    @Override
    public void onPPChange(User user) {

    }

    @Override
    public void onUserNameChange(User user) {

    }

    @Override
    public void onUserNameChangeFailed(String s) {

    }

    @Override
    public void onAuthenticationFailed(String s) {

    }

    @Override
    public void onAuthSuccess() {

    }

    @Override
    public void onConnectFailed() {

    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onConnect() {

    }

    @Override
    @EventHandler
    public void onClientDisconnect(ClientDisconnectEvent event) {
        super.onClientDisconnect(event);
    }

    @Override
    @EventHandler
    public void onClientConnect(ClientConnectEvent event) {
        super.onClientConnect(event);
    }

    @Override
    @EventHandler
    public void onPacket(ClientPacketReceiveEvent event) {
        super.onPacket(event);
    }
}
