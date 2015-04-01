package quickblacklist.handlers;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import org.apache.logging.log4j.Level;
import quickblacklist.core.QBL_Settings;
import quickblacklist.core.QuickBlacklist;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EventHandler
{
	@SubscribeEvent
	public void onEntitySpawn(EntityJoinWorldEvent event)
	{
		if(!(event.entity instanceof EntityItem) || event.world.isRemote || QBL_Settings.dropInstead)
		{
			return;
		}
		
		EntityItem eItem = (EntityItem)event.entity;
		ItemStack item = eItem.getEntityItem();
		
		if(QBL_Settings.blacklist.contains("" + Item.itemRegistry.getNameForObject(item.getItem()) + ":" + item.getItemDamage()) || QBL_Settings.blacklist.contains("" + Item.itemRegistry.getNameForObject(item.getItem())))
		{
			QuickBlacklist.logger.log(Level.WARN, "Deleted item drop '" + item.getDisplayName() + "' at (" + eItem.posX + "," + eItem.posY + "," + eItem.posZ + ")");
			eItem.setDead();
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void onEntityInteract(EntityInteractEvent event)
	{
		if(event.entityPlayer.worldObj.isRemote)
		{
			return;
		}
		
		if(!QBL_Settings.enableBlacklist || (event.entityPlayer.capabilities.isCreativeMode && !QBL_Settings.creativeBlacklist))
		{
			return;
		}
		
		if(event.target != null && event.target instanceof IInventory)
		{
			IInventory chest = (IInventory)event.target;
			
			for(int i = 0; i < chest.getSizeInventory(); i++)
			{
				ItemStack cItem = chest.getStackInSlot(i);
				
				if(cItem != null && (QBL_Settings.blacklist.contains("" + Item.itemRegistry.getNameForObject(cItem.getItem()) + ":" + cItem.getItemDamage()) || QBL_Settings.blacklist.contains("" + Item.itemRegistry.getNameForObject(cItem.getItem()))))
				{
					if(!QBL_Settings.dropInstead)
					{
						QuickBlacklist.logger.log(Level.WARN, event.entityPlayer.getCommandSenderName() + " was flagged for accessing entity with item '" + cItem.getDisplayName() + "' in slot " + i);
						chest.setInventorySlotContents(i, null);
						chest.markDirty();
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if(event.entityPlayer.worldObj.isRemote)
		{
			return;
		}
		
		if(!QBL_Settings.enableBlacklist || (event.entityPlayer.capabilities.isCreativeMode && !QBL_Settings.creativeBlacklist))
		{
			return;
		}
		
		ItemStack item = event.entityPlayer.getHeldItem();
		
		if(item != null && (QBL_Settings.blacklist.contains("" + Item.itemRegistry.getNameForObject(item.getItem()) + ":" + item.getItemDamage()) || QBL_Settings.blacklist.contains("" + Item.itemRegistry.getNameForObject(item.getItem()))))
		{
			QuickBlacklist.logger.log(Level.WARN, event.entityPlayer.getCommandSenderName() + " was flagged for using item '" + item.getDisplayName() + "'");
			if(QBL_Settings.dropInstead)
			{
				event.entityPlayer.dropPlayerItemWithRandomChoice(item, false);
			}
			event.entityPlayer.setCurrentItemOrArmor(0, null);
			event.entityPlayer.inventory.markDirty();
			
			event.setCanceled(true);
		}
		
		if(event.action == Action.RIGHT_CLICK_BLOCK)
		{
			TileEntity tile = event.entityPlayer.worldObj.getTileEntity(event.x, event.y, event.z);
			
			if(tile != null && tile instanceof IInventory)
			{
				IInventory chest = (IInventory)tile;
				
				for(int i = 0; i < chest.getSizeInventory(); i++)
				{
					ItemStack cItem = chest.getStackInSlot(i);
					
					if(cItem != null && (QBL_Settings.blacklist.contains("" + Item.itemRegistry.getNameForObject(cItem.getItem()) + ":" + cItem.getItemDamage()) || QBL_Settings.blacklist.contains("" + Item.itemRegistry.getNameForObject(cItem.getItem()))))
					{
						if(!QBL_Settings.dropInstead)
						{
							QuickBlacklist.logger.log(Level.WARN, event.entityPlayer.getCommandSenderName() + " was flagged for accessing tile entity with item '" + item.getDisplayName() + "' in slot " + i);
							chest.setInventorySlotContents(i, null);
							chest.markDirty();
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event)
	{
		if(event.entityLiving.worldObj.isRemote)
		{
			return;
		}
		
		if(event.entityLiving instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)event.entityLiving;
			
			if(!QBL_Settings.enableBlacklist || (player.capabilities.isCreativeMode && !QBL_Settings.creativeBlacklist))
			{
				return;
			}
			
			int pass = player.getEntityData().getInteger("QBL_Pass");
			
			if(pass < QBL_Settings.scanRate)
			{
				player.getEntityData().setInteger("QBL_Pass", pass + 1);
				return;
			} else
			{
				player.getEntityData().setInteger("QBL_Pass", 0);
			}
			
			for(int i = 0; i < player.inventory.getSizeInventory(); i++)
			{
				ItemStack item = player.inventory.getStackInSlot(i);
				
				if(item != null && (QBL_Settings.blacklist.contains("" + Item.itemRegistry.getNameForObject(item.getItem()) + ":" + item.getItemDamage()) || QBL_Settings.blacklist.contains("" + Item.itemRegistry.getNameForObject(item.getItem()))))
				{
					QuickBlacklist.logger.log(Level.WARN, player.getCommandSenderName() + " was flagged for having item '" + item.getDisplayName() + "' in slot " + i);
					if(QBL_Settings.dropInstead)
					{
						player.dropPlayerItemWithRandomChoice(item, false);
					}
					player.inventory.setInventorySlotContents(i, null);
					player.inventory.markDirty();
					player.inventoryContainer.detectAndSendChanges();
				}
			}
			
			if(player.openContainer != null && player.openContainer != player.inventoryContainer)
			{
				for(int i = 0; i < player.openContainer.inventorySlots.size(); i++)
				{
					Slot slot = player.openContainer.getSlot(i);
					ItemStack item = slot == null? null : slot.getStack();
					
					if(item != null && (QBL_Settings.blacklist.contains("" + Item.itemRegistry.getNameForObject(item.getItem()) + ":" + item.getItemDamage()) || QBL_Settings.blacklist.contains("" + Item.itemRegistry.getNameForObject(item.getItem()))))
					{
						QuickBlacklist.logger.log(Level.WARN, player.getCommandSenderName() + " was flagged for accessing container with item '" + item.getDisplayName() + "' in slot " + i);
						if(QBL_Settings.dropInstead)
						{
							player.dropPlayerItemWithRandomChoice(item, false);
						}
						player.openContainer.putStackInSlot(i, null);
						slot.inventory.markDirty();
						player.openContainer.detectAndSendChanges();
					}
				}
			}
		}
	}
}
