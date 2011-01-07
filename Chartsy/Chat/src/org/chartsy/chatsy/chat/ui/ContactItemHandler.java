package org.chartsy.chatsy.chat.ui;

import org.jivesoftware.smack.packet.Presence;
import javax.swing.Icon;

public interface ContactItemHandler
{

    boolean handlePresence(ContactItem item, Presence presence);
    Icon getIcon(String jid);
    Icon getTabIcon(Presence presence);
    boolean handleDoubleClick(ContactItem item);
	
}
