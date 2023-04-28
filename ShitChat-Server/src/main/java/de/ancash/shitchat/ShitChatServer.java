package de.ancash.shitchat;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import de.ancash.libs.org.apache.commons.io.FileUtils;
import de.ancash.libs.org.simpleyaml.configuration.file.YamlFile;
import de.ancash.libs.org.simpleyaml.exceptions.InvalidConfigurationException;
import de.ancash.loki.logger.PluginOutputFormatter;
import de.ancash.misc.io.IFormatter;
import de.ancash.misc.io.LoggerUtils;
import de.ancash.sockets.async.impl.packet.server.AsyncPacketServer;

public class ShitChatServer {

	private static ShitChatServer singleton;
	
	public static void main(String[] args) throws InvalidConfigurationException, IOException {
		singleton = new ShitChatServer();
	}
	
	public static ShitChatServer getInstance() {
		return singleton;
	}

	@SuppressWarnings("nls")
	private final YamlFile config = new YamlFile(new File("config.yml"));
	private String address;
	private int port;
	private String defaultProfilePicFile;
	private int worker;
	private AsyncPacketServer server;
	private long cacheTimeout;
	
	@SuppressWarnings("nls")
	public ShitChatServer() throws InvalidConfigurationException, IOException {
		System.out.println("Starting ShitChat-Server...");
		LoggerUtils.setErr(Level.INFO, new IFormatter("[" + IFormatter.PART_DATE_TIME + "] " + "["
				+ IFormatter.THREAD_NAME + "/" + IFormatter.COLOR + IFormatter.LEVEL + IFormatter.RESET + "] ["
				+ PluginOutputFormatter.PLUGIN_NAME + "] " + IFormatter.COLOR + IFormatter.MESSAGE + IFormatter.RESET));
		System.out.println("Logger set");
		loadConfig();
		start();
	}
	
	@SuppressWarnings("nls")
	private void start() throws IOException {
		System.out.println("Binding to " + address + ":" + port);
		server = new AsyncPacketServer(address, port, worker);
		server.start();
	}
	
	@SuppressWarnings("nls")
	private void loadConfig() throws InvalidConfigurationException, IOException {
		System.out.println("Loading config...");
		if(!config.exists())
			FileUtils.copyInputStreamToFile(getClass().getClassLoader().getResourceAsStream("config.yml"), config.getConfigurationFile());
		de.ancash.misc.io.FileUtils.setMissingConfigurationSections(config, getClass().getClassLoader().getResourceAsStream("config.yml"));
		config.loadWithComments();
		defaultProfilePicFile = config.getString("defaultProfilePicFile");
		address = config.getString("server.address");
		port = config.getInt("server.port");
		worker = config.getInt("server.worker");
	}
	
	public String getDefaultProfilePicFile() {
		return defaultProfilePicFile;
	}

	public long getCacheTimeout() {
		return cacheTimeout;
	}
}
