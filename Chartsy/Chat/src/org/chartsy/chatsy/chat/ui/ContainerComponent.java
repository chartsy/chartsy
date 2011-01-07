package org.chartsy.chatsy.chat.ui;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

public interface ContainerComponent
{

    public abstract String getTabTitle();
    public abstract String getFrameTitle();
    public abstract ImageIcon getTabIcon();
    public abstract JComponent getGUI();
    public abstract String getToolTipDescription();
    public abstract boolean closing();
	
}
