package info.wubsy.itemrestrict.src.main.java.info.wubsy.itemrestrict;

import com.google.inject.Inject;
import info.wubsy.itemrestrict.config.ConfigLoader;
import info.wubsy.itemrestrict.config.IresConfig;
import info.wubsy.itemrestrict.config.MessagesConfig;
import ninja.leaping.configurate.objectmapping.GuiceObjectMapperFactory;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import java.io.File;

import static org.spongepowered.api.Sponge.getServer;

@Plugin(name = "ItemRestrictSponge", id = "itemrestrictsponge", version = "0.0.1", description = "A remake of the early bukkit version of itemrestrict")
public class ItemRestrict {
	public static ItemRestrict instance;

	// Create static instances
	public static Logger logger;
	public static Server server;

	// Create storage interfaces
	public ConfigStore cStore;
	public QuickStore qStore;

	private IresConfig iresConfig;
	private MessagesConfig messages;
	private ConfigLoader cfgLoader;

	private final GuiceObjectMapperFactory factory;
	private final File configDir;
	private final Game game;

	@Inject
	public ItemRestrict(Logger logger, Game game, @ConfigDir(sharedRoot = false) File configDir, GuiceObjectMapperFactory factory) {
		instance = this;
		ItemRestrict.logger = logger;
		this.game = game;
		this.configDir = configDir;
		this.factory = factory;
	}

	public MessagesConfig getMessagesCfg() {
		return messages;
	}

	public IresConfig getIresConfig() {
		return iresConfig;
	}
	@Listener
	public void onPreInit(GamePreInitializationEvent event) {
		server = getServer();
		getLogger().info("Setting up config...");
		cfgLoader = new ConfigLoader(this);
		if (cfgLoader.loadConfig()) iresConfig = cfgLoader.getIresConfig();
		if (cfgLoader.loadMessages()) messages = cfgLoader.getMessagesConfig();
	}
/*
	public static CommandSpec getCommand() {
		return CommandSpec.builder()
				.description(Text.of("Configure item restrictions"))
				.permission("ires.command.sampletext")
				.arguments(
						GenericArguments.flags()
								.permissionFlag("ires.command.sampletext.reload", "r")
								.permissionFlag("ires.command.sampletext.hand", "h")
								.permissionFlag("ires.command.sampletext.ban", "b")
								.permissionFlag("ires.command.sampletext.unban", "u")
								.permissionFlag("ires.command.sampletext.convert", "c")
								.buildWith(GenericArguments.none())
				)
				.build();
	}
*/
	private void registerCommands() {
		getLogger().info("Registering commands...");
		Sponge.getCommandManager().register(this, ItemRestrictCmd.getCommand(), "ires");
	}

	public File getConfigDir() {
		return configDir;
	}

	public Logger getLogger() {
		return logger;
	}

	public GuiceObjectMapperFactory getFactory() {
		return factory;
	}
}