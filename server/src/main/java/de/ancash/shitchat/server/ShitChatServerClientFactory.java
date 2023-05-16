package de.ancash.shitchat.server;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;

import de.ancash.sockets.async.client.AbstractAsyncClientFactory;
import de.ancash.sockets.async.impl.packet.server.AsyncPacketServerClient;
import de.ancash.sockets.async.server.AbstractAsyncServer;

public class ShitChatServerClientFactory extends AbstractAsyncClientFactory<AsyncPacketServerClient> {

	@Override
	public AsyncPacketServerClient newInstance(AbstractAsyncServer asyncServer, AsynchronousSocketChannel socket,
			int queueSize, int readBufSize, int writeBufSize) throws IOException {
		AsyncPacketServerClient apsc = new AsyncPacketServerClient(asyncServer, socket, queueSize, readBufSize,
				writeBufSize);
		apsc.setMaxPacketSize(1024 * 1024 * 16);
		return apsc;
	}

	@Override
	public AsyncPacketServerClient newInstance(String address, int port, int queueSize, int readBufSize,
			int writeBufSize, int threads) throws IOException {
		throw new UnsupportedOperationException();
	}
}
