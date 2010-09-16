package org.chartsy.main.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.chartsy.main.templates.TemplateManagerPanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;

public final class TemplateManagerAction implements ActionListener
{

	public void actionPerformed(ActionEvent e)
	{
		TemplateManagerPanel panel = new TemplateManagerPanel();
		DialogDescriptor descriptor = new DialogDescriptor(panel, "Template Manager", true, null);
		descriptor.setMessageType(DialogDescriptor.PLAIN_MESSAGE);
		descriptor.setOptions(new Object[]
		{DialogDescriptor.CANCEL_OPTION});
		DialogDisplayer.getDefault().notify(descriptor);
	}
	
}
