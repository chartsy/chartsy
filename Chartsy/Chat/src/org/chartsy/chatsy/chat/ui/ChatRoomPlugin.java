package org.chartsy.chatsy.chat.ui;

import javax.swing.Icon;
import javax.swing.JComponent;

public interface ChatRoomPlugin
{

    void setChatRoom(ChatRoom room);
    void tabSelected();
    String getTabTitle();
    Icon getTabIcon();
    String getTabToolTip();
    JComponent getGUI();
	
}
