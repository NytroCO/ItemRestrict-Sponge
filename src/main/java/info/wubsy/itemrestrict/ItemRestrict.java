package info.wubsy.itemrestrict;

import com.google.inject.Inject;
import info.wubsy.itemrestrict.config.ConfigLoader;
import info.wubsy.itemrestrict.config.IresConfig;
import info.wubsy.itemrestrict.config.MessagesConfig;
import ninja.leaping.configurate.objectmapping.GuiceObjectMapperFactory;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

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
		getLogger().info("[ItemRestrict] ItemRestrict Started.");
		server = getServer();
		getLogger().info("Setting up config...");
		cfgLoader = new ConfigLoader(this);
		if (cfgLoader.loadConfig()) iresConfig = cfgLoader.getIresConfig();
		if (cfgLoader.loadMessages()) messages = cfgLoader.getMessagesConfig();
	}

	@Listener
	public void onInit(GameInitializationEvent event) {
		getLogger().info("[ItemRestrict] Initialized.");
		registerCommands();
	}

	public static CommandSpec getCommand() {
		CommandSpec command = CommandSpec.builder()
				.description(Text.of("Configure item restrictions"))
				.permission("ires.command.admin")
				.arguments(
						GenericArguments.flags()
								.permissionFlag("itemrestrict.command.admin.hand", "h", "-hand")
								.permissionFlag("itemrestrict.command.admin.ban", "b", "-ban")
								.permissionFlag("itemrestrict.command.admin.unban", "u", "-unban")
								.buildWith(GenericArguments.none())
				)
				.executor(new ItemRestrictCmd(ItemRestrict.instance))
				.build();
		return command;
	}

	private static void registerCommands() {
		Sponge.getCommandManager().register(ItemRestrict.instance, ItemRestrict.getCommand(), "ires");
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