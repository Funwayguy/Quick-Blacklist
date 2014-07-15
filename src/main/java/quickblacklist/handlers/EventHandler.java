package quickblacklist.handlers;

import quickblacklist.core.QBL_Settings;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

public class EventHandler
{
	@ForgeSubscribe
	public void onEntitySpawn(EntityJoinWorldEvent event)
	{
		if(!(event.entity instanceof EntityItem) || event.world.isRemote || !QBL_Settings.dropInstead)
		{
			return;
		}
		
		EntityItem eItem = (EntityItem)event.entity;
		ItemStack item = eItem.getEntityItem();
		
		if(QBL_Settings.blacklist.contains("" + item.itemID + ":" + item.getItemDamage()) || QBL_Settings.blacklist.contains("" + item.itemID))
		{
			eItem.setDead();
			event.setCanceled(true);
		}
	}
	
	@ForgeSubscribe
	public void onEntityInteract(EntityInteractEvent event)
	{
		if(event.isCanceled() || event.entityPlayer.worldObj.isRemote)
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
				
				if(cItem != null && (QBL_Settings.blacklist.contains("" + cItem.itemID + ":" + cItem.getItemDamage()) || QBL_Settings.blacklist.contains("" + cItem.itemID)))
				{
					if(!QBL_Settings.dropInstead)
					{
						chest.setInventorySlotContents(i, null);
					}
				}
			}
		}
	}
	
	@ForgeSubscribe
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if(event.isCanceled() || event.entityPlayer.worldObj.isRemote)
		{
			return;
		}
		
		if(!QBL_Settings.enableBlacklist || (event.entityPlayer.capabilities.isCreativeMode && !QBL_Settings.creativeBlacklist))
		{
			return;
		}
		
		ItemStack item = event.entityPlayer.getHeldItem();
		
		if(item != null && (QBL_Settings.blacklist.contains("" + item.itemID + ":" + item.getItemDamage()) || QBL_Settings.blacklist.contains("" + item.itemID)))
		{
			if(QBL_Settings.dropInstead)
			{
				event.entityPlayer.dropPlayerItem(item);
			}
			event.entityPlayer.setCurrentItemOrArmor(0, null);
			
			event.setCanceled(true);
		}
		
		if(event.action == Action.RIGHT_CLICK_BLOCK)
		{
			TileEntity tile = event.entityPlayer.worldObj.getBlockTileEntity(event.x, event.y, event.z);
			
			if(tile != null && tile instanceof IInventory)
			{
				IInventory chest = (IInventory)tile;
				
				for(int i = 0; i < chest.getSizeInventory(); i++)
				{
					ItemStack cItem = chest.getStackInSlot(i);
					
					if(cItem != null && (QBL_Settings.blacklist.contains("" + cItem.itemID + ":" + cItem.getItemDamage()) || QBL_Settings.blacklist.contains("" + cItem.itemID)))
					{
						if(!QBL_Settings.dropInstead)
						{
							chest.setInventorySlotContents(i, null);
						}
					}
				}
			}
		}
	}
	
	@ForgeSubscribe
	public void onLivingUpdate(LivingUpdateEvent event)
	{
		if(event.entityLiving.worldObj.isRemote)
		{
			return;
		}
		
		if(event.entityLiving instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)event.entityLiving;
			ItemStack[] mainInvo = player.inventory.mainInventory;
			ItemStack[] armorInvo = player.inventory.armorInventory;
			
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
			
			for(int i = 0; i < mainInvo.length; i++)
			{
				if(mainInvo[i] != null)
				{
					ItemStack item = mainInvo[i];
					if(QBL_Settings.blacklist.contains("" + item.itemID + ":" + item.getItemDamage()) || QBL_Settings.blacklist.contains("" + item.itemID))
					{
						if(QBL_Settings.dropInstead)
						{
							player.dropPlayerItem(item);
						}
						player.inventory.mainInventory[i] = null;
					}
				}
			}
			
			for(int i = 0; i < armorInvo.length; i++)
			{
				if(armorInvo[i] != null)
				{
					ItemStack item = armorInvo[i];
					if(QBL_Settings.blacklist.contains("" + item.itemID + ":" + item.getItemDamage()) || QBL_Settings.blacklist.contains("" + item.itemID))
					{
						if(QBL_Settings.dropInstead)
						{
							player.dropPlayerItem(item);
						}
						player.inventory.armorInventory[i] = null;
					}
				}
			}
		}
	}
}
