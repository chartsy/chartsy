package org.chartsy.main.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.ErrorManager;
import org.openide.awt.Notification;
import org.openide.awt.NotificationDisplayer;

/**
 *
 * @author Viorel
 */
public final class NotifyUtil
{

	private NotifyUtil()
	{
	}

	public static void show(String title, String message, MessageType type,
		ActionListener actionListener, boolean clear)
	{
        Notification n = (Notification) NotificationDisplayer.getDefault()
			.notify(title, type.getIcon(), message, actionListener);
        if (clear == true)
            n.clear();
    }

	public static void show(String title, final String message,
		final MessageType type, boolean clear)
	{
        ActionListener actionListener = new ActionListener()
		{
            @Override public void actionPerformed(ActionEvent e)
			{
                MessageUtil.show(message, type);
            }
        };
        show(title, message, type, actionListener, clear);
    }

	public static void info(String title, String message, boolean clear)
	{
        show(title, message, MessageType.INFO, clear);
    }

	public static void error(String title, String message, boolean clear)
	{
        show(title, message, MessageType.ERROR, clear);
    }

	public static void error(String title, final String message,
		final Throwable exception , boolean clear)
	{
        ActionListener actionListener = new ActionListener()
		{
            @Override public void actionPerformed(ActionEvent e)
			{
                ErrorManager.getDefault().notify(exception);
            }
        };
        show(title, message, MessageType.ERROR, actionListener, clear);
    }

	public static void warn(String title, String message, boolean clear)
	{
        show(title, message, MessageType.WARNING, clear);
    }

	public static void plain(String title, String message, boolean clear)
	{
        show(title, message, MessageType.PLAIN, clear);
    }

}

