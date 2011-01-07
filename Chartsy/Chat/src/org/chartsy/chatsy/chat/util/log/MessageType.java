package org.chartsy.chatsy.chat.util.log;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.openide.NotifyDescriptor;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Viorel
 */
public enum MessageType
{

	PLAIN		(NotifyDescriptor.PLAIN_MESSAGE, null),
	INFO		(NotifyDescriptor.INFORMATION_MESSAGE, "info.png"),
	QUESTION	(NotifyDescriptor.QUESTION_MESSAGE, null),
	ERROR		(NotifyDescriptor.ERROR_MESSAGE, "error.png"),
	WARNING		(NotifyDescriptor.WARNING_MESSAGE, "warning.png");

	private int notifyDescriptorType;
	private Icon icon;

	private MessageType(int notifyDescriptorType, String resourceName)
	{
		this.notifyDescriptorType = notifyDescriptorType;
		if (resourceName == null)
			icon = new ImageIcon();
		else
			icon = ImageUtilities.loadImageIcon("org/chartsy/chatsy/resources/" + resourceName, true);
	}

	int getNotifyDescriptorType()
	{
		return notifyDescriptorType;
	}

	Icon getIcon()
	{
		return icon;
	}

}
