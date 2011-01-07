package org.chartsy.chatsy.chat.ui.status;

import org.jivesoftware.smack.packet.Presence;

import javax.swing.Icon;
import javax.swing.JLabel;

public class StatusItem extends JLabel
{

    private Presence presence;

    public StatusItem(Presence presence, Icon icon)
	{
        this.presence = presence;
        setIcon(icon);
        setText(presence.getStatus());
    }

    public Presence getPresence()
	{
        return presence;
    }

}
