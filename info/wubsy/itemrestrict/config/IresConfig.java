package info.wubsy.itemrestrict.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wubsy on 12/25/2018.
 */
@ConfigSerializable
public class IresConfig {

    @Setting("Worlds")
    public String Worlds = "All";

    /* Useless :D */
    @Setting("Scanner")
    public Double Scanner = 0.1;

    @Setting("event")
    public Event event = new Event();

    @ConfigSerializable
    public static class Event {

        @Setting("onPlayerJoin")
        public int onPlayerJoin = 1;

        @Setting("onChunkLoad")
        public double onChunkLoad = 0.1;

    }

    @Setting("Bans")
    public Bans bans = new Bans();

    @ConfigSerializable
    public static class Bans {

        @Setting("Usage")
        public List<String> usageBlacklist = new ArrayList<String>() {
            {
                add(BlockTypes.DARK_OAK_DOOR.getId());   // Not expected to be actually banned,
            }                                           // just an example
        };

        @Setting("Ownership")
        public List<String> ownershipBlacklist = new ArrayList<String>() {
            {
                add(BlockTypes.FIRE.getId()); //Good luck getting it unmodded as a normal player
            }
        };

        @Setting("Crafting")
        public List<String>  craftBlacklist = new ArrayList<String>() {
            {
                add(BlockTypes.TNT.getId()); // Tsk tsk...
            }
        };

        @Setting("World")
        public List<String>  worldBlacklist = new ArrayList<String>() {
            {
                add(BlockTypes.TNT.getId()); // Tsk tsk...
            }
        };

        @Setting("Equip")
        public List<String> equipBlacklist = new ArrayList<String>() {
            {
                add(EquipmentTypes.HEADWEAR.getId()); // This isn't TF2
            }
        };


    }



}
