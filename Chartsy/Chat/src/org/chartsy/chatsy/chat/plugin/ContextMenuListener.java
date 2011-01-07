package org.chartsy.chatsy.chat.plugin;

import javax.swing.JPopupMenu;
import java.awt.event.MouseEvent;

public interface ContextMenuListener
{

    void poppingUp(Object object, JPopupMenu popup);
    void poppingDown(JPopupMenu popup);
    boolean handleDefaultAction(MouseEvent e);

}

 
