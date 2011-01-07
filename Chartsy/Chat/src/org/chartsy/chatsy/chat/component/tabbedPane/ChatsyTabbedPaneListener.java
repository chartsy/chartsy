package org.chartsy.chatsy.chat.component.tabbedPane;

import java.awt.Component;

public interface ChatsyTabbedPaneListener
{

    void tabRemoved(ChatsyTab tab, Component component, int index);
    void tabAdded(ChatsyTab tab, Component component, int index);
    void tabSelected(ChatsyTab tab, Component component, int index);
    void allTabsRemoved();
    boolean canTabClose(ChatsyTab tab, Component component);

}
