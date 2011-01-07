package org.chartsy.chatsy.chat.component.tabbedPane;

import java.awt.BorderLayout;
import javax.swing.JPanel;

/**
 *
 * @author Viorel
 */
public abstract class AbstractTab extends JPanel
{

	protected boolean initialized = false;

	public AbstractTab()
	{
		super(new BorderLayout());
		setOpaque(false);

	}

	public void addNotify()
	{
		super.addNotify();
		if (!initialized)
		{
			buildContent();
			initialized = true;
		}
	}

	protected abstract void buildContent();

}
