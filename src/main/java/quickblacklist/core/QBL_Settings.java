package quickblacklist.core;

import java.util.ArrayList;

public class QBL_Settings
{
	public static final String Version = "QBL_VER_NUM";
	public static final String ID = "QuickBlacklist";
	public static final String Channel = "QBL_CH";
	public static final String Name = "Quick Blacklist";
	public static final String Proxy = "quickblacklist.core.proxies";
	
	public static ArrayList<String> blacklist = new ArrayList<String>();
	
	public static boolean enableBlacklist = true;
	public static boolean creativeBlacklist = true;
	public static int scanRate = 1;
	public static boolean dropInstead = false;
}
