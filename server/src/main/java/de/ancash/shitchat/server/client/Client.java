package de.ancash.shitchat.server.client;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.UUID;

import de.ancash.sockets.async.impl.packet.server.AsyncPacketServerClient;
import de.ancash.sockets.async.server.AbstractAsyncServer;

public class Client extends AsyncPacketServerClient {

	protected UUID sid;

	public Client(AbstractAsyncServer asyncIOServer, AsynchronousSocketChannel asyncSocket, int queueSize,
			int readBufSize, int writeBufSize) throws IOException {
		super(asyncIOServer, asyncSocket, queueSize, readBufSize, writeBufSize);
		setMaxPacketSize(1024 * 1024 * 16);
	}

	public void setSID(UUID sid) {
		this.sid = sid;
	}

	public UUID getSID() {
		return sid;
	}
}
