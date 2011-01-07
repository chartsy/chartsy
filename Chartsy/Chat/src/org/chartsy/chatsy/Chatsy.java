package org.chartsy.chatsy;

import java.io.File;

public final class Chatsy
{

    private static final String USER_CHATSY_HOME = System.getProperty("user.home") + File.separator + "Chartsy" + File.separator + "Chat";

    public Chatsy()
	{
    }

    public static boolean isWindows()
	{
        final String osName = System.getProperty("os.name").toLowerCase();
        return osName.startsWith("windows");
    }

    public static boolean isVista()
	{
        final String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("vista");
    }

    public static boolean isMac()
	{
        String lcOSName = System.getProperty("os.name").toLowerCase();
        return lcOSName.indexOf("mac") != -1;
    }

	public static boolean isLinux()
	{
        final String osName = System.getProperty("os.name").toLowerCase();
        return osName.startsWith("linux");
    }

    public static String getChatUserHome()
	{
        return USER_CHATSY_HOME;
    }

    public static boolean isCustomBuild()
	{
        return true;
    }

}
