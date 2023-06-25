package de.ancash.shitchat.server;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import de.ancash.cli.CLI;
import de.ancash.ithread.IThreadPoolExecutor;
import de.ancash.libs.org.apache.commons.io.FileUtils;
import de.ancash.libs.org.bukkit.event.EventHandler;
import de.ancash.libs.org.bukkit.event.EventManager;
import de.ancash.libs.org.bukkit.event.Listener;
import de.ancash.misc.io.IFormatter;
import de.ancash.misc.io.LoggerUtils;
import de.ancash.shitchat.server.account.AccountRegistry;
import de.ancash.shitchat.server.client.Client;
import de.ancash.shitchat.server.client.ClientFactory;
import de.ancash.shitchat.server.listener.ShitChatPacketListener;
import de.ancash.sockets.async.impl.packet.server.AsyncPacketServer;
import de.ancash.sockets.events.ClientDisconnectEvent;

public class ShitChatServer implements Listener {

	@SuppressWarnings("unused")
	private static volatile ShitChatServer singleton;

	public static void main(String[] args) throws InvalidConfigurationException, IOException {
		singleton = new ShitChatServer();
	}

	@SuppressWarnings("nls")
	private final YamlFile config = new YamlFile(new File("config.yml"));
	private String address;
	private int port;
	private String defaultProfilePicFile;
	private int worker;
	private AsyncPacketServer server;
	private long cacheTimeout;
	private volatile boolean running = false;
	private final IThreadPoolExecutor pool = IThreadPoolExecutor.newCachedThreadPool();
	private final AccountRegistry accRegistry;
	private final ShitChatPacketListener listener;
	private final CLI cli = new CLI();

	@SuppressWarnings("nls")
	public ShitChatServer() throws InvalidConfigurationException, IOException {
		System.out.println("Starting ShitChat-Server...");
		IFormatter formatter = new IFormatter("[" + IFormatter.PART_DATE_TIME + "] " + "[" + IFormatter.THREAD_NAME
				+ "/" + IFormatter.COLOR + IFormatter.LEVEL + IFormatter.RESET + "] " + IFormatter.COLOR
				+ IFormatter.MESSAGE + IFormatter.RESET);
		LoggerUtils.setErr(Level.SEVERE, formatter);
		LoggerUtils.setOut(Level.INFO, formatter);
		LoggerUtils.setGlobalLogger(formatter);
		System.out.println("Logger set");
		accRegistry = new AccountRegistry(this);
		listener = new ShitChatPacketListener(this);
		loadConfig();
		start();
	}

	@SuppressWarnings("nls")
	private void start() throws IOException {
		EventManager.registerEvents(this, this);
		server = new AsyncPacketServer(address, port, worker);
		server.setAsyncClientFactory(new ClientFactory());
		running = true;
		System.out.println("Binding to " + address + ":" + port);
		server.start();
		System.out.println("Done");
		pool.submit(accRegistry);
		EventManager.registerEvents(listener, this);
		cli.onInput(this::onInput);
		cli.run();
	}

	@EventHandler
	public void onDisconnect(ClientDisconnectEvent event) {
		if (event.getClient() == null || !(event.getClient() instanceof Client))
			return;
		Client client = (Client) event.getClient();
		accRegistry.onDisconnect(client);
	}

	@SuppressWarnings("nls")
	private void onInput(String s) {
		if ("stop".equals(s.toLowerCase())) {
			running = false;
			pool.shutdownNow();
			try {
				Thread.sleep(2000);
				server.stop();
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
			Thread.currentThread().interrupt();
		}
	}

	@SuppressWarnings("nls")
	private void loadConfig() throws InvalidConfigurationException, IOException {
		System.out.println("Loading config...");
		if (!config.exists())
			FileUtils.copyInputStreamToFile(getClass().getClassLoader().getResourceAsStream("config.yml"),
					config.getConfigurationFile());
		de.ancash.misc.io.FileUtils.setMissingConfigurationSections(config,
				getClass().getClassLoader().getResourceAsStream("config.yml"));
		config.loadWithComments();
		defaultProfilePicFile = config.getString("defaultProfilePicFile");
		address = config.getString("server.address");
		port = config.getInt("server.port");
		worker = config.getInt("server.worker");
	}

	public AccountRegistry getAccountRegistry() {
		return accRegistry;
	}

	public boolean isRunning() {
		return running;
	}

	public String getDefaultProfilePicFile() {
		return defaultProfilePicFile;
	}

	public long getCacheTimeout() {
		return cacheTimeout;
	}
}
