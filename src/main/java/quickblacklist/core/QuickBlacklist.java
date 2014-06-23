package quickblacklist.core;

import java.util.logging.Logger;
import quickblacklist.core.proxies.QBL_CommonProxy;
import quickblacklist.handlers.ConfigHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = QBL_Settings.ID, name = QBL_Settings.Name, version = QBL_Settings.Version)
@NetworkMod(clientSideRequired = false, serverSideRequired = true, channels = {QBL_Settings.Channel})

public class QuickBlacklist
{
	@Instance(QBL_Settings.ID)
	public static QuickBlacklist instance;
	
	@SidedProxy(clientSide = QBL_Settings.Proxy + ".QBL_ClientProxy", serverSide = QBL_Settings.Proxy + ".QBL_CommonProxy")
	public static QBL_CommonProxy proxy;
	
	public static Logger logger;
	
	@EventHandler
	public static void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();
		logger.setParent(FMLLog.getLogger());
		proxy.registerEventHandlers();
		ConfigHandler.LoadConfig(event.getSuggestedConfigurationFile());
	}
	
	@EventHandler
	public static void init(FMLInitializationEvent event)
	{
	}
	
	@EventHandler
	public static void postInit(FMLPostInitializationEvent event)
	{
	}
}