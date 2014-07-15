package quickblacklist.handlers;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import net.minecraftforge.common.Configuration;
import quickblacklist.core.QBL_Settings;
import quickblacklist.core.QuickBlacklist;

public class ConfigHandler
{
	public static void LoadConfig(File file)
	{
		if(!file.exists())
		{
			try
			{
				file.createNewFile();
			} catch(IOException e)
			{
				QuickBlacklist.logger.log(Level.SEVERE, "Failed to create config for Quick Blacklist!");
				e.printStackTrace();
				return;
			}
		}
		
		Configuration config = new Configuration(file);
		
		config.load();
		
		QBL_Settings.enableBlacklist = config.get("Main", "Enable Blacklisting", true).getBoolean(true);
		QBL_Settings.creativeBlacklist = config.get("Main", "Blacklist in Creative", true).getBoolean(true);
		QBL_Settings.scanRate = config.get("Main", "Blacklist Scan Interval", 1).getInt(1);
		QBL_Settings.dropInstead = config.get("Main", "Drop Instead Of Delete", false).getBoolean(false);
		
		String[] blacklist = config.get("Blacklist", "Blacklisted Items (ID or ID:META)", new String[0]).getStringList();
		
		if(blacklist.length > 0)
		{
			for(int i = 0; i < blacklist.length; i++)
			{
				QBL_Settings.blacklist.add(blacklist[i].trim().replaceAll(" ", ""));
			}
			
			QuickBlacklist.logger.log(Level.INFO, "Blacklisted " + blacklist.length + " item(s)");
		}
		
		config.save();
	}
}
