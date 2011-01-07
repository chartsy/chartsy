package org.chartsy.main.utils;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author Viorel
 */
public final class MessageUtil
{

	private MessageUtil()
	{
	}

	public static DialogDisplayer getDialogDisplayer()
	{
		return DialogDisplayer.getDefault();
	}

	public static void show(String message, MessageType messageType)
	{
		getDialogDisplayer().notify(new NotifyDescriptor.Message(
			message,
			messageType.getNotifyDescriptorType()));
	}

	public static void showException(String message, Throwable exception)
	{
		getDialogDisplayer().notify(new NotifyDescriptor.Exception(
			exception,
			message));
	}

	public static void info(String message)
	{
		show(message, MessageType.INFO);
	}

	public static void error(String message)
	{
		show(message, MessageType.ERROR);
	}

	public static void error(String message, Throwable exception)
	{
		showException(message, exception);
	}

	public static void question(String message)
	{
		show(message, MessageType.QUESTION);
	}

	public static void warn(String message)
	{
		show(message, MessageType.WARNING);
	}

	public static void plain(String message)
	{
		show(message, MessageType.PLAIN);
	}

}
