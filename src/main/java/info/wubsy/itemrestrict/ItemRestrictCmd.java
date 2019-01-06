package info.wubsy.itemrestrict;

import info.wubsy.itemrestrict.config.ConfigLoader;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import java.util.ArrayList;
import java.util.List;

//import org.spongepowered.api.plugin.Plugin;


public class ItemRestrictCmd implements CommandExecutor {

    private final ConfigStore cStore;
    private ConfigLoader cfgLoader;

    public ItemRestrictCmd(ItemRestrict plugin) {
         this.cStore = plugin.cStore;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (args.hasAny("h") || args.hasAny("b") || args.hasAny ("u")) {
            if (args.hasAny("h")) { //oh boy I don't know what to do
                if (src instanceof Player) {
                    cStore.getInformationInHand(src);
                    return CommandResult.success();
                }
                throw new CommandException(Text.of("Could not get hand data."));

            }
            if (args.hasAny("b")) {
                cStore.addBan(src, getActionType(args[1]), args[2]);
                return CommandResult.success();
            }
            if (args.hasAny("u")) {
                cStore.removeBan(src, getActionType(args[1]), args[2]);
                return CommandResult.success();
            }
        }
        return CommandResult.success();
    }

    private ActionType getActionType(String actionTypeString) {
    	for(final ActionType type : ActionType.values())
    	{
    		if(type.name().compareToIgnoreCase(actionTypeString) == 0)
    		{
    			return type;
    		}
    	}
    	return null;
    }

    private List<Text> getCommands() {
        List<Text> texts = new ArrayList<Text>();
        texts.add(Text.builder().onClick(TextActions.suggestCommand("/ires reload"))
                .onHover(TextActions.showText(Text.of("Reload ItemRestrict config")))
                .append(Text.of("/ires reload"))
                .build());
        return texts;
    }
}
