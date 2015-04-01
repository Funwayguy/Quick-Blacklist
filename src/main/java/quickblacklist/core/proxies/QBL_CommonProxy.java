package quickblacklist.core.proxies;

import cpw.mods.fml.common.FMLCommonHandler;
import quickblacklist.handlers.EventHandler;
import net.minecraftforge.common.MinecraftForge;

public class QBL_CommonProxy
{
	public boolean isClient()
	{
		return false;
	}
	
	public void registerTickHandlers()
	{
	}
	
	public void registerEventHandlers()
	{
		EventHandler handler = new EventHandler();
		MinecraftForge.EVENT_BUS.register(handler);
		FMLCommonHandler.instance().bus().register(handler);
	}
}
