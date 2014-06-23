package quickblacklist.handlers;

import quickblacklist.core.QBL_Settings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

public class EventHandler
{
	@ForgeSubscribe
	public void onLivingUpdate(LivingUpdateEvent event)
	{
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
						player.inventory.armorInventory[i] = null;
					}
				}
			}
		}
	}
}
