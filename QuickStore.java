package info.wubsy.itemrestrict;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.MainPlayerInventory;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;

import java.util.Optional;


public class QuickStore {
	final private ItemRestrict plugin;
	private final ConfigStore cStore;
	
	public QuickStore( ItemRestrict plugin ) {
		this.plugin = plugin;
		this.cStore = plugin.cStore;
	}

	public void notifyBan(final Player player, final ItemStack item) {
		player.sendMessage(Text.of("Banned: " + item.getItem().getName()));
	}
	
	public void notifyBan(final Player player, final BlockState block) {
		player.sendMessage(Text.of("Banned: " + block.getName()));
	}
	
	public void scanChunk( final Chunk chunk ) {
		if( cStore.getBanListSize( ActionType.World ) == 0 ) return;
		//at this point it should be ok, if it is just reading.
		//final Task.Builder scheduler = Task.builder();
		//scheduler.async( plugin, new Runnable() {
                        //@Override
			//public void run() {
				Vector3i vector3 = chunk.getBiomeMax();

				final int yMax = vector3.getY();

				BlockState block;
				for( int x = 0; x < 16; x++ ) {
					for( int z = 0; z < 16; z++ ) {
						for( int y = 0; y < yMax; y++ ) {
							block = chunk.getBlock( x, y, z );
							if( cStore.isBannable( null, block, ActionType.World ) ) {
								final BlockState clearBlock = block;
								Location location = chunk.getLocation(x, y, z);
										location.setBlockType(BlockTypes.AIR);
									}

							}
						}
					}
				}



	
	public void itemUnequip( final Player player, final int itemSlotX, final int itemSlotY ) {
		
		int invSpace = 0;
		int freeSlotX = 0;
		int freeSlotY = 0;

		Inventory inventory = player.getInventory();
		MainPlayerInventory mainInv = ((MainPlayerInventory)inventory);
		Optional<ItemStack> item = mainInv.getGrid().getSlot(itemSlotX, itemSlotY).get().peek();
		Optional<Slot> slot = mainInv.getGrid().getSlot(itemSlotX, itemSlotY);
		slot.get().clear();

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
			mainInv.set( itemSlotX, itemSlotY, null );
			mainInv.set( freeSlotX, freeSlotY, item.get() );
			notifyBan( player, item.get() );
		}
		else {
			mainInv.set(itemSlotX, itemSlotY, null );
			//player.getWorld().createEntity(


			Optional<EntityType> itemToDrop = Sponge.getRegistry().getType(EntityType.class, item.get().getType().getName());
			player.getWorld().createEntity( itemToDrop.get(), player.getLocation().getBlockPosition() );
			player.sendMessage(Text.of("You are not allowed to equip this item and your internal inventory is full." ));
		}
	}
	
	public void scanInventory( final Player player ) {
		if( cStore.getBanListSize( ActionType.Ownership ) == 0 && cStore.getBanListSize( ActionType.Equip ) == 0 ) return;
		//no need, only makes servers susceptible for crashes 
		//ItemRestrict.server.getScheduler().runTaskAsynchronously( plugin, new Runnable() {
			//public void run() {
				Inventory inventory = player.getInventory();
				MainPlayerInventory mainInv = ((MainPlayerInventory)inventory);
				ItemStack itemStack = inventory.peek().get();
				if ( cStore.getBanListSize( ActionType.Ownership ) != 0 ) {
					for (int j = 0; j <= mainInv.getColumns(); j++) {
						for (int k = 0; k <= mainInv.getColumns(); k++) {
							if (cStore.isBannable(player, itemStack, ActionType.Ownership))
								mainInv.set(j, k, null);
							//notifyBan( player, items[i] );
						}
					}
				}

				
				if ( cStore.getBanListSize( ActionType.Equip ) != 0 ) {

					for (int j = 0; j <= mainInv.getColumns(); j++) {
						for (int k = 0; k <= mainInv.getColumns(); k++) {
							Optional<ItemStack> item = mainInv.getGrid().getSlot(j, k).get().peek();
							if (cStore.isBannable(player, item.get(), ActionType.Equip)) {
								itemUnequip(player, j, k);
							}
						}
					}
				}
			}

	public void flashItem( final Player player ) {
		final Optional<ItemStack> item = player.getItemInHand(HandTypes.MAIN_HAND);
		player.setItemInHand( HandTypes.MAIN_HAND, null );

				player.setItemInHand(HandTypes.MAIN_HAND, item.get() );

		}
	}
