package info.wubsy.itemrestrict;

import info.wubsy.itemrestrict.config.ConfigLoader;
import info.wubsy.itemrestrict.config.IresConfig;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigStore {

    private final ItemRestrict plugin;
    private IresConfig config;
    private ConfigLoader loader;


    //private final File configDir;

    private ConfigurationLoader<CommentedConfigurationNode> configLoader;

    // Cache config values
    private List<String> worldList;
    private final Map<ActionType, List<String>> bans = new HashMap<ActionType, List<String>>();

    public ConfigStore(ItemRestrict plugin) {
        this.plugin = plugin;
        if (!plugin.getConfigDir().exists()) {
            plugin.getConfigDir().mkdirs();
        }
        // Force reload plugin
        loader.loadConfig();
    }

        public void reloadConfig() {
            // Config operations
            loader.loadConfig();

            // Config variables
            config = plugin.getIresConfig();
            List<String> worldList = loader.getWorldBans();

            for(final ActionType type : ActionType.values())
            {
                if(!bans.containsKey(type.name()))
                {
                    bans.put(type, new ArrayList<String>());
                }

                final List<String> values = bans.get(type);
                values.clear();
                for (int i = 0; i <= loader.getBanList().size(); i++) {
                    values.addAll(toLowerCase(loader.getBanList().get(0)));
                    plugin.getLogger().debug("Added " + loader.getBanList().get(i).get(0) + "to banned list in memory");
                }
            }
        }

        public boolean hasPermission(CommandSource src, String node, boolean allowConsole) {
            if (src instanceof Player) {
                if (!src.hasPermission(node)) {
                    src.sendMessage(Text.of("Insufficient permissions"));
                    return false;
                }
            } else {
                if (!allowConsole) {
                    src.sendMessage(Text.of("This is only a player command"));
                }
                return false;
            }

            return true;
        }

        public boolean isEnabledWorld(World world) {
            return worldList.contains("All") || worldList.contains(world.getName());
        }

        public boolean isBanned(BlockState block, ActionType actionType) {
            boolean banned = isBanned(getConfigString(block), actionType);
            if (!banned) {
                banned = isBanned(getConfigStringParent(block), actionType);
            }
            return banned;
        }

        public boolean isBanned(ItemStack item, ActionType actionType) {
            boolean banned = isBanned(getConfigString(item), actionType);
            if (!banned) {
                banned = isBanned(getConfigStringParent(item), actionType);
            }
            return banned;
        }

        private boolean isBanned(final String configString, final ActionType actionType)
        {
            return bans.containsKey(actionType) && bans.get(actionType).contains(configString.toLowerCase());
        }

        public boolean isBannable(Player player, ItemStack item, ActionType actionType) {
            // Check null
            if (item == null) {
                return false;
            }

            // Player checks
            if (player != null) {
                // Check world
                if (!isEnabledWorld(player.getWorld())) {
                    return false;
                }

                // Check exclude permission
                if (player.hasPermission("ItemRestrict.bypass." + getActionTypeString(actionType) + "." + getConfigString(item))) {
                    return false;
                }

                // Check exclude parent permission
                if (player.hasPermission("ItemRestrict.bypass." + getActionTypeString(actionType) + "." + getConfigStringParent(item))) {
                    return false;
                }
            }

            // Check ban list
            return isBanned(item, actionType);
        }

        public boolean isBannable(Player player, BlockState block, ActionType actionType) {
            // Check null
            if (block == null) {
                return false;
            }

            // Player checks
            if (player != null) {
                // Check world
                if (!isEnabledWorld(player.getWorld())) {
                    return false;
                }

                // Check exclude permission
                if (player.hasPermission("ItemRestrict.bypass." + getActionTypeString(actionType) + "." + getConfigString(block))) {
                    return false;
                }

                // Check exclude parent permission
                if (player.hasPermission("ItemRestrict.bypass." + getActionTypeString(actionType) + "." + getConfigStringParent(block))) {
                    return false;
                }
            }

            // Check ban list
            return isBanned(block, actionType);
        }

    private String getActionTypeString(ActionType actionType) {
        // Select proper string
        switch (actionType) {
            case Usage:
                return "Usage";
            case Equip:
                return "Equip";
            case Crafting:
                return "Crafting";
            case Ownership:
                return "Ownership";
            case World:
                return "World";
            default:
                // Should never reach here if all enum cases covered
                ItemRestrict.logger.debug("Unknown ActionType detected: {0}", actionType.toString());
                return "";
        }
    }

    /** \brief Checks if a string validates as configuration string
     * 
     * Checks if a string validates as configuration string.
     * A configuration string consists of a string followed by an optional hyphen '-' and a number
     * Example: WOOL-5
     * 
     * @param config string to check
     * @return true if strConfig is a configuration string, false otherwise
     */
    private boolean isConfigString(final String config)
    {
    	final int dashIndex = config.indexOf('-');
    	final String name = (dashIndex < 0 ? config : config.substring(0, dashIndex));
    	final String dataValue = (dashIndex < 0 ? "0" : config.substring(dashIndex + 1));

    	// TODO (k4su: 07.11.16): eventually replace by regex
    	try
    	{
    		Integer.parseInt(dataValue);
    	}
    	catch(final NumberFormatException exception)
    	{
    		return false;
    	}

    	return (name.length() > 0);
    }



    @SuppressWarnings("deprecation")
    private String getConfigStringParent(BlockState block) {
        // Config version string of block id 
        return ("" + block.getType().getId()).toLowerCase();
    }
    
     @SuppressWarnings("deprecation")
    private String getConfigString(BlockState block) {
        // Config version string of item id and data value
        
        return ("" + block.getType().toString() + "-" + block.getType().getName()).toLowerCase();
    }
    
    @SuppressWarnings("deprecation")
    private String getConfigString(ItemStack item) {
        // Config version string of item id and data value
        ItemType matData = item.getItem();
        return ("" + item.getType().toString() + "-" + matData.getName()).toLowerCase();
    }

    private String getConfigStringParent(ItemStack item) {
        // Config version string of item id and data value
        return ("" + item.getType().toString()).toLowerCase();
    }

    public int getScanFrequencyOnPlayerJoin() { // Lame
        return config.event.onPlayerJoin;
    }

    public double getScanFrequencyOnChunkLoad() {
        return config.event.onChunkLoad;
    }

    /** \brief get the size of bans for the actiontype
     * 
     * @param actionType actiontype to get banlength
     * @return amount of bans for xActionType
     */
    public int getBanListSize(final ActionType actionType)
    {
    	if(bans.containsKey(actionType))
    	{
    		return bans.get(actionType).size();
    	}
    	else
    	{
    		ItemRestrict.logger.debug( "Unknown ActionType detected: {0}", actionType.name());
    		return 0;
    	}
    }

    /** \brief add a ban for a specified usage
     * 
     * @param src         src who called the command
     * @param actionType      usage type for the ban
     * @param configString  name of the item (incl. data after dash '-')
     */
    public void addBan(final CommandSource src, final ActionType actionType, final String configString) {
        // Check valid actionType
        if (actionType == null) {
            src.sendMessage(Text.of("Invalid ban type. Valid ban types: Usage, Equip, Crafting, Ownership, World"));
            return;
        }

        // Check valid config string
        if (!isConfigString(configString)) {
            src.sendMessage(Text.of(configString + "  is not a valid item"));
            return;
        }

        IresConfig newConf = new IresConfig();
        if(bans.containsKey(actionType))
        {
			final List<String> typeBans = bans.get(actionType);
			// only add if not yet added
			if(!typeBans.contains(configString))
			{
				typeBans.add(configString.toLowerCase());
			}
			switch(actionType)
            {
                case Equip:
                    newConf.bans.equipBlacklist.add(configString);
                case Usage:
                    newConf.bans.usageBlacklist.add(configString);
                case World:
                    newConf.bans.worldBlacklist.add(configString);
                case Crafting:
                    newConf.bans.craftBlacklist.add(configString);
                case Ownership:
                    newConf.bans.ownershipBlacklist.add(configString);
                default:
                    plugin.getLogger().debug("[ItemRestrictSponge] YIKES! SOMETHING WENT WRONG ADDING BAN!");
                    break;
            }
        }
        else
        {
            // Should never reach here if all enum cases covered
            ItemRestrict.logger.debug("Unknown ActionType detected: {0}", actionType.toString());
            return;
        }
        loader.saveConfig(newConf);
        src.sendMessage(Text.of("Item Banned"));
    }

    /** \brief remove a ban for a specified usage
     * 
     * @param src          src who called the command
     * @param actionType      usage type for the ban
     * @param configString  name of the item (incl. data after dash '-')
     */
    public void removeBan(final CommandSource src, final ActionType actionType, final String configString) {
        // Check valid actionType
        IresConfig newConf = new IresConfig();
        if (actionType == null) {
            src.sendMessage(Text.of("Invalid ban type. Valid ban types: Usage, Equip, Crafting, Ownership, World"));
            return;
        }

        // Check valid config string
        if (!isConfigString(configString)) {
            src.sendMessage(Text.of(configString + " is not a valid item"));
            return;
        }

        if(bans.containsKey(actionType))
        {

			//final List<String> typeBans = bans.get(actionType);
            switch(actionType)
            {
                case Equip:
                    newConf.bans.equipBlacklist.remove(configString.toLowerCase());
                case Usage:
                    newConf.bans.usageBlacklist.remove(configString.toLowerCase());
                case World:
                    newConf.bans.worldBlacklist.remove(configString.toLowerCase());
                case Crafting:
                    newConf.bans.craftBlacklist.remove(configString.toLowerCase());
                case Ownership:
                    newConf.bans.ownershipBlacklist.remove(configString.toLowerCase());
                default:
                    plugin.getLogger().debug("[ItemRestrictSponge] YIKES! SOMETHING WENT WRONG REMOVING BAN!");
                    break;
            }

        }
        else
        {
            // Should never reach here if all enum cases covered
            ItemRestrict.logger.debug( "Unknown ActionType detected: {0}", actionType.toString());
            return;
        }
        loader.saveConfig(newConf);
        src.sendMessage(Text.of("Item Unbanned"));
    }

    /** \brief Converts eventual ids stored in the banlists to their material type */
    public void convert()
    {
    	// convert the bans
    	for(final List<String> banList : bans.values())
    	{
    		final int banCount = banList.size();
    		for(int i = 0; i < banCount; ++i)
    		{
    			final String value = banList.get(i);
    			final String itemTypeString = value.toString();
                ItemType itemType = ItemTypes.NONE; // Not gonna lie, this probably won't work
    			for (int j = 0; j <= ItemTypes.class.getClasses().length; j++) {
    			    if (value.contains(ItemTypes.class.getClasses()[j].toString())) {
                        itemType = ItemType.class.getEnumConstants()[j]; // Not gonna lie, this probably won't work

                    }

                }

    			final int data = getDataForString(value);

    			if(data >= 0 && itemType != null)
    			{
    				if(data > 0)
    				{
						banList.set(i, itemType.getName().toLowerCase() + '-' + data);
					}
					else
					{
						banList.set(i, itemType.getName().toLowerCase());
					}
				}
    		}
    	}
    	//saveBans();
    }

    /** \brief converts the keys as ids in a section to materialname + id */
    /* This is useless in the modern day of named items
    private void convertSection(final ConfigurationSection section)
    {
    	if(section != null)
    	{
    		for(final String key : section.getKeys(false))
    		{
    			final Material material = getMaterialForString(key);
    			final int data = getDataForString(key);

    			final String value = section.getString(key);

    			// set new value with materialname
    			if(material != null && data >= 0)
    			{
    				if(data > 0)
    				{
    					section.set(material.name().toLowerCase() + '-' + data, value);
    				}
    				else
    				{
    					section.set(material.name().toLowerCase(), value);
    				}
    				// clear old value
    				section.set(key, null);
    			}
    		}
    	}
    }
    */

    /** @return Material for an id or null if no material was found */
    /*
    private ItemType getMaterialForString(final String value)
    {
		final int dashIndex = value.indexOf('-');

		int id = -1;
		if(dashIndex == -1)  // no data value, e.g. >>pink<< wool
		{
			try
			{
				id = Integer.parseInt(value);
			}
			catch(final NumberFormatException e)
			{
				ItemRestrict.logger.log(Level.WARNING, "Skipping: " + value);
				return null;
			}
		}
		else
		{
			try
			{
				id = Integer.parseInt(value.substring(0, dashIndex));
			}
			catch(final NumberFormatException e)
			{
				ItemRestrict.logger.log(Level.WARNING, "Skipping: " + value);
				return null;
			}
		}

		return ItemTypes.
    }
    */
    private int getDataForString(final String value)
    {
    	final int dashIndex = value.indexOf('-');

    	if(dashIndex == -1)  // no data value, e.g. >>pink<< wool
    	{
    		return -1;
    	}
    	else
    	{
    		try
    		{
    			return Integer.parseInt(value.substring(dashIndex + 1));
    		}
    		catch(final NumberFormatException e)
    		{
    			ItemRestrict.logger.debug( "Skipping: " + value);
    			return -1;
    		}
    	}
    }

    /** \brief saves the bans to the configfile */
    public void saveBans(IresConfig newConfig)
    {
        loader.saveConfig(newConfig);
    }

    public void getInformationInHand(CommandSource src) {
        if(src instanceof Player){
            Player player = (Player) src;
            src.sendMessage(Text.of(getConfigString(player.getItemInHand(HandTypes.MAIN_HAND).get())));
        }else{
            src.sendMessage(Text.of("you need to be a player to get this information"));
        }
    }

    private List<String> toLowerCase(List<String> stringList) {
        for(int i = 0; i<stringList.size(); i++){
            stringList.set(i, stringList.get(i).toLowerCase());
        }
        return stringList;
    }
}
