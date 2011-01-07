package org.chartsy.chatsy.chat.component.renderer;


import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import java.awt.Color;
import java.awt.Component;

public class JPanelRenderer extends JPanel implements ListCellRenderer
{

    public JPanelRenderer()
	{
        setOpaque(true);
		setBackground(Color.WHITE);
    }

    public Component getListCellRendererComponent
		(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
        JPanel panel = (JPanel) value;
        if (isSelected)
            panel.setBackground(Color.decode("0x0298db"));
        else
            panel.setBackground(list.getBackground());
        return panel;
    }
	
}

