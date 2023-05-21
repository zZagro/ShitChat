package de.ancash.shitchat.server.client;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;

import de.ancash.sockets.async.client.AbstractAsyncClientFactory;
import de.ancash.sockets.async.impl.packet.server.AsyncPacketServerClient;
import de.ancash.sockets.async.server.AbstractAsyncServer;

public class ClientFactory extends AbstractAsyncClientFactory<AsyncPacketServerClient> {

	@Override
	public Client newInstance(AbstractAsyncServer asyncServer, AsynchronousSocketChannel socket, int queueSize,
			int readBufSize, int writeBufSize) throws IOException {
		return new Client(asyncServer, socket, queueSize, readBufSize, writeBufSize);
	}

	@Override
	public Client newInstance(String address, int port, int queueSize, int readBufSize, int writeBufSize, int threads)
			throws IOException {
		throw new UnsupportedOperationException();
	}
}
