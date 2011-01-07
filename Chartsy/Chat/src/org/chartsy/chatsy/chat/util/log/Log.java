package org.chartsy.chatsy.chat.util.log;


import java.util.logging.Level;
import java.util.logging.Logger;

public class Log
{

    private static final Logger LOGGER;

    private Log()
	{
    }

    static
	{
		LOGGER = Logger.getAnonymousLogger();
    }

    public static void error(String message, Throwable ex)
	{
        LOGGER.log(Level.SEVERE, message, ex);
    }

    public static void error(Throwable ex)
	{
        LOGGER.log(Level.SEVERE, "", ex);
    }

    public static void warning(String message, Throwable ex)
	{
		LOGGER.warning(message);
        LOGGER.log(Level.SEVERE, "", ex);
    }

    public static void warning(String message)
	{
		LOGGER.warning(message);
    }

    public static void error(String message)
	{
        LOGGER.log(Level.SEVERE, message);
    }

    public static void debug(String message)
	{
		LOGGER.info(message);
    }

}
