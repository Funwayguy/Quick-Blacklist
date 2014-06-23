package quickblacklist.core.proxies;

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
		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}
}
