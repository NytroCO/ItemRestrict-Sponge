package info.wubsy.itemrestrict.src.main.java.info.wubsy.itemrestrict.config;

import com.google.common.reflect.TypeToken;
import info.wubsy.itemrestrict.src.main.java.info.wubsy.itemrestrict.ItemRestrict;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wubsy on 12/25/2018.
 */
public class ConfigLoader {

    private final ItemRestrict plugin;
    private IresConfig iresConfig;
    private ConfigurationLoader<CommentedConfigurationNode> iresLoader;

    private MessagesConfig messagesConfig;

    public ConfigLoader(ItemRestrict plugin) {
        this.plugin = plugin;
        if (!plugin.getConfigDir().exists()) {
            plugin.getConfigDir().mkdirs();
        }
    }

    public boolean loadConfig() {
        try {
            File file = new File(plugin.getConfigDir(), "ires.conf");
            if (!file.exists()) {
                file.createNewFile();
            }
            iresLoader = HoconConfigurationLoader.builder().setFile(file).build();
            CommentedConfigurationNode config = iresLoader.load(ConfigurationOptions.defaults().setObjectMapperFactory(plugin.getFactory()).setShouldCopyDefaults(true));
            iresConfig = config.getValue(TypeToken.of(IresConfig.class), new IresConfig());
            iresLoader.save(config);
            return true;
        } catch (Exception e) {
            plugin.getLogger().error("Config load error.", e);
            return false;
        }
    }

    public boolean loadMessages() {
        try {
            File file = new File(plugin.getConfigDir(), "messages.conf");
            if (!file.exists()) {
                file.createNewFile();
            }
            ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(file).build();
            CommentedConfigurationNode config = loader.load(ConfigurationOptions.defaults().setObjectMapperFactory(plugin.getFactory()).setShouldCopyDefaults(true));
            messagesConfig = config.getValue(TypeToken.of(MessagesConfig.class), new MessagesConfig());
            loader.save(config);
            return true;
        } catch (Exception e) {
            plugin.getLogger().error("Messages config load error.", e);
            return false;
        }
    }

    public void saveConfig(IresConfig newConfig) {
        try {
            File file = new File(plugin.getConfigDir(), "ires.conf");
            if (!file.exists()) {
                file.createNewFile();
            }
            CommentedConfigurationNode config = iresLoader.load(ConfigurationOptions.defaults().setObjectMapperFactory(plugin.getFactory()).setShouldCopyDefaults(true));
            config.setValue(TypeToken.of(IresConfig.class), newConfig);
            iresLoader.save(config);
        } catch (Exception e) {
            plugin.getLogger().error("Could not save config.", e);
        }
    }

    public ArrayList<ArrayList<String>> getBanList() { // Yikes
        IresConfig.Bans bans = getIresConfig().bans;
        ArrayList<ArrayList<String>> allBans = new ArrayList<ArrayList<String>>();
        ArrayList<String> craftBans = new ArrayList<String>(bans.craftBlacklist);
        ArrayList<String> equipBans = new ArrayList<String>(bans.equipBlacklist);
        ArrayList<String> ownBans = new ArrayList<String>(bans.ownershipBlacklist);
        ArrayList<String> useBans = new ArrayList<String>(bans.usageBlacklist);
        ArrayList<String> worldBans = new ArrayList<String>(bans.worldBlacklist);
        allBans.add(craftBans);
        allBans.add(equipBans);
        allBans.add(ownBans);
        allBans.add(useBans);
        allBans.add(worldBans);

        return allBans;
    }

    public List<String> getCraftBans() {
        IresConfig.Bans bans = getIresConfig().bans;
        return bans.craftBlacklist;
    }

    public List<String> getEquipBans() {
        IresConfig.Bans bans = getIresConfig().bans;
        return bans.equipBlacklist;
    }

    public List<String> getOwnBans() {
        IresConfig.Bans bans = getIresConfig().bans;
        return bans.ownershipBlacklist;
    }

    public List<String> getUseBans() {
        IresConfig.Bans bans = getIresConfig().bans;
        return bans.usageBlacklist;
    }

    public List<String> getWorldBans() {
        IresConfig.Bans bans = getIresConfig().bans;
        return bans.worldBlacklist;

    }

    public IresConfig getIresConfig() {
        return iresConfig;
    }

    public MessagesConfig getMessagesConfig() {
        return messagesConfig;
    }
}
