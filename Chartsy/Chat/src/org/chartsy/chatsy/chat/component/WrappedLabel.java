package org.chartsy.chatsy.chat.component;

import javax.swing.JTextArea;
import java.awt.Dimension;

public class WrappedLabel extends JTextArea
{

    public WrappedLabel()
	{
        this.setEditable(false);
        this.setWrapStyleWord(true);
        this.setLineWrap(true);
        this.setOpaque(false);
    }

    public Dimension getPreferredSize()
	{
        final Dimension size = super.getPreferredSize();
        size.width = 0;
        return size;
    }
	
}