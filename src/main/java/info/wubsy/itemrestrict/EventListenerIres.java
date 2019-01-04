package info.wubsy.itemrestrict;


import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.CraftItemEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.chunk.LoadChunkEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.MainPlayerInventory;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Chunk;

import java.util.Optional;
import java.util.Random;

public class EventListenerIres{
	private final QuickStore qStore;
	private final ConfigStore cStore;
	private final Random rand;


	public EventListenerIres( ItemRestrict plugin ) {
		this.cStore = plugin.cStore;
		this.qStore = plugin.qStore;
		rand = new Random();
	}
	
	
	// Ordered by ban type, Block/Entity/Player
	
	// Usage Bans - Prevent item usage
	@Listener( order = Order.LAST )
	public void onBlockPlace(ChangeBlockEvent.Place event, @First Player player, @Getter("getTargetBlock") BlockState block, @Getter("getItem") ItemStack item) {
		// When a block is placed
		//BlockType block = event.getBlock();
		//ItemStack item = event.getItemInHand();

		// Check usage bannable and world
		if( cStore.isBannable( player, block, ActionType.Usage ) || cStore.isBannable( player, item, ActionType.Usage ) ) {
			// Cancel
			qStore.notifyBan( player, item );
			event.setCancelled( true );
		}
	}
	
	@Listener
	public void onEntityDamageByEntity(DamageEntityEvent event, @Root Entity damager) {
		// When an entity damages another entity
		//Entity damager = event.getDamager();
		Optional<Player> damageSource = event.getContext().get(EventContextKeys.PLAYER);
		damageSource.get().getClass();
		if( !( damager instanceof Player ) ) return;
		Player player = (Player) damager;
		Optional<ItemStack> item = player.getItemInHand(HandTypes.MAIN_HAND);

		// Check usage bannable and world
		if( !cStore.isBannable( player, item.get(), ActionType.Usage ) ) return;
		
		// Cancel
		qStore.notifyBan( player, item.get() );
		event.setCancelled( true );
	}

	@Listener
	public void onPlayerInteract(InteractBlockEvent.Secondary.MainHand event, @First Player player) {
		// When a player interacts with world

		ItemStack item = player.getItemInHand(HandTypes.MAIN_HAND).get();
		BlockState block = event.getTargetBlock().getState();

		// Check usage bannable and world
		if( cStore.isBannable( player, item, ActionType.Usage ) ) {
			qStore.notifyBan( player, item );
			event.setCancelled( true );
			qStore.flashItem( player );
		}
		else if( cStore.isBannable( player, block, ActionType.Usage ) ) {
			qStore.notifyBan( player, block );
			event.setCancelled( true );
		}
	}
	/*
	@Listener
	public void onPlayerInteractEntity( InteractEntityEvent.Primary.MainHand event ) {
		// When a player interacts with an entity
		Player player = event.getPlayer();
		ItemStack item = event.getPlayer().getItemInHand();

		// Check usage bannable and world
		if( !cStore.isBannable( player, item, ActionType.Usage ) ) return;
		
		// Cancel
		qStore.notifyBan( player, item );
		event.setCancelled( true );
	}
	*/
	// Crafting Bans - Prevent crafting when detected
	
	@Listener
	public void onItemCrafted( CraftItemEvent.Craft event, @First Player player, @Getter("getItem") ItemStack item) {
		// When an item is crafted
		//Player player = (Player) event.);
		//ItemStack item = event.getRecipe().getResult();

		// Check ownership bannable and world
		if( cStore.isBannable( player, item, ActionType.Ownership ) || cStore.isBannable( player, item, ActionType.Crafting ) ) {
			// Cancel
			qStore.notifyBan( player, item );
			event.setCancelled( true );
		}
	}
	
	// Ownership Bans - Remove item when detected
	
	@Listener
	public void onPlayerJoin( ClientConnectionEvent.Join event, @First Player player ) {
		// Perform random screening
		if( rand.nextDouble() > cStore.getScanFrequencyOnPlayerJoin() ) return;
		
		// When a player joins
		// Sike it should be able to get it

		// Scan inventory
		qStore.scanInventory( player );

		// Scan chunk
		qStore.scanChunk(player.getWorld().getChunkAtBlock(player.getLocation().getChunkPosition()).get());
	}
	
	@Listener
	public void onInventoryClick(ClickInventoryEvent.Primary event, @First Player player, @Getter("createStack") ItemStack clickItem , @Getter("getSourceInventory")
    Inventory inventory) {
        // When an item is clicked in the inv
        //ItemStack cursorItem = event.getContext().get;

        // Check usage bannable and world
        if (cStore.isBannable(player, clickItem, ActionType.Ownership)) {
            // Cancel and ban
            qStore.notifyBan(player, clickItem);
            event.setCancelled(true);
            player.setItemInHand(HandTypes.MAIN_HAND, null);
        }
    }
/*
	private boolean isPlaceInventory( ClickInventoryEvent.Drop action ) { // I have no idea what the bukkit dev was trying to do here
		switch( action ) {
		case PLACE_ALL:
		case PLACE_SOME:
		case PLACE_ONE:
			return true;
		default:
			return false;
		}

	}
*/
	@Listener
	public void onPlayerPickupItem( ChangeInventoryEvent.Pickup event, @Getter("getOriginalStack") ItemStack item ) {
		// When a player pickups
		Player player = event.getTargetInventory().query(Player.class);
		Slot slot = event.getTransactions().get(0).getSlot();
		SlotPos slotPos = slot.query(SlotPos.class);
		// Check ownership bannable and world
		if( cStore.isBannable( player, item, ActionType.Ownership ) ) {			
			// Cancel
			qStore.notifyBan( player, item );
			event.setCancelled( true );
		}
		else if ( cStore.isBannable( player, item, ActionType.Equip ) ) {
            int invSpace = 0;
            int freeSlotX = 0;
            int freeSlotY = 0;

            Inventory inventory = player.getInventory();
            MainPlayerInventory mainInv = ((MainPlayerInventory)inventory);

            //Optional<Slot> slot = mainInv.getGrid().getSlot(itemSlotX, itemSlotY);
            //slot.get().set(ItemStack.empty());

            for (int i = 9; i <= 36; i++) {
                for (int j = 0; j <= mainInv.getColumns(); j++) {
                    for (int k = 0; k <= mainInv.getColumns(); k++) {
                        if (mainInv.getGrid().getSlot(j, k) == null) {
                            invSpace = 1;
                            freeSlotX = j;
                            freeSlotY = k;
                            break;
                        }
                    }
                }
            }
            if ( invSpace == 1 ) {
                mainInv.set( slotPos, null );
                mainInv.set( freeSlotX, freeSlotY, item );
                player.sendMessage( Text.of("You are not allowed to possess " + item.getType().getName() ));

            }
			else {
				player.sendMessage( Text.of("You are not allowed to equip this item and your internal inventory is full." ));
				event.setCancelled( true );
			}
		}
	}
	
	@Listener
	public void onInventory ( InteractInventoryEvent.Close event, @First Player player) {
		// Scan inventory
		qStore.scanInventory( player );
	}
	/*
	@Listener
	public void onPlayerItemHeld(final ChangeInventoryEvent.Held event, @First Player player) {
	// Don´t do this, this only causes server crashes, what happens if another plugin accesses the slot during it gets deleted ?
		//ItemRestrict.server.getScheduler().runTaskAsynchronously( plugin, new Runnable() {
		//	public void run() {
				// When a player switches item in hand

				int slotId = event.getNewSlot();
				ItemStack item = player.getInventory().getItem( slotId );
				
				// Check ownership bannable and world
				if( item != null && cStore.isBannable( player, item, ActionType.Ownership ) ) {				
					// Ban
					qStore.notifyBan( player, item );
					player.getInventory().setItem( slotId, null );
				}
				else if( item != null && cStore.isBannable( player, item, ActionType.Equip ) ) {
					qStore.notifyBan( player, item );
					qStore.scanInventory( player );
					//qStore.itemUnequip( player, slotId );
					//player.getInventory().setItem( slotId, null );
					//player.getWorld().dropItemNaturally( player.getLocation(), item );
				}
			}
		//});
	//}
	*/
	// World Bans - Remove block when detected
	@Listener
	public void onChunkLoad( LoadChunkEvent event, @Getter("getTargetChunk") Chunk chunk ) {
		// Perform random screening
		if( rand.nextDouble() > cStore.getScanFrequencyOnChunkLoad() ) return;
        // Without async, this may be very bad
		// When a chunk loads
		// Scan chunk
		qStore.scanChunk( chunk );
	}
}
