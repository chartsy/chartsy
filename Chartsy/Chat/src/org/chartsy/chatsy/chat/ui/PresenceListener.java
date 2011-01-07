package org.chartsy.chatsy.chat.ui;

import org.jivesoftware.smack.packet.Presence;

public interface PresenceListener
{

    void presenceChanged(Presence presence);

}
