package info.wubsy.itemrestrict.src.main.java.info.wubsy.itemrestrict;

import info.wubsy.itemrestrict.config.ConfigLoader;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
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
    public CommandResult execute(CommandSource src, CommandContext args) {
        if (args.hasAny("r") || args.hasAny("h") || args.hasAny("b") || args.hasAny ("u") || args.hasAny("c")) {
            if (args.hasAny("r")) {
                cfgLoader.loadConfig();
                src.sendMessage(Text.of("ItemRestrict Config Reloaded"));
                return CommandResult.success();
            }
            if (args.hasAny("h")) { //oh boy I don't know what to do
                cStore.getInformationInHand(src);
                return CommandResult.success();
            }
            if (args.hasAny("b")) {
                //cStore.addBan(src, getActionType(args[1]), args[2]);
                return CommandResult.success();
            }
            if (args.hasAny("u")) {
                //cStore.removeBan(src, getActionType(args[1]), args[2]);
                return CommandResult.success();
            }
            if (args.hasAny("c")) {
                cStore.convert();
                src.sendMessage(Text.of("Converted"));
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

    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .description(Text.of("sample text"))
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
                .executor(new ItemRestrictCmd(ItemRestrict.instance))
                .build();
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
