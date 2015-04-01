package quickblacklist.handlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;
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
				QuickBlacklist.logger.log(Level.ERROR, "Failed to create config for Quick Blacklist!");
				e.printStackTrace();
				return;
			}
		}
		
		Configuration config = new Configuration(file, true);
		
		config.load();
		
		QBL_Settings.enableBlacklist = config.get("Main", "Enable Blacklisting", true).getBoolean(true);
		QBL_Settings.creativeBlacklist = config.get("Main", "Blacklist in Creative", true).getBoolean(true);
		QBL_Settings.scanRate = config.get("Main", "Blacklist Scan Interval", 1).getInt(1);
		QBL_Settings.dropInstead = config.get("Main", "Drop Instead Of Delete", false).getBoolean(false);
		
		QBL_Settings.blacklist = new ArrayList<String>(Arrays.asList(config.get("Blacklist", "Blacklisted Items (ID or ID:META)", new String[0]).getStringList()));
		
		config.save();
	}
}
