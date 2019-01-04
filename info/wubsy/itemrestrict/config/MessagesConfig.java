package info.wubsy.itemrestrict.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
/**
 * Created by Wubsy on 12/25/2018.
 */

@ConfigSerializable
public class MessagesConfig {

    @Setting("Message Color")
    public TextColor messageColor = TextColors.LIGHT_PURPLE;

    @Setting("Prefix")
    public String prefix = "&5[ItemRestrict] ";

    @Setting("Banned Message")
    public String banMsg = "You are not permitted to have this item.";
}
