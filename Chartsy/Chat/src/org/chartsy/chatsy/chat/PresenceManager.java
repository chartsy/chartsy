package org.chartsy.chatsy.chat;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.packet.Presence;
import javax.swing.Icon;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.ImageUtilities;

public class PresenceManager
{

	public static final String _AVAILABLE = "Available";
	public static final String _AWAY = "Away";
	public static final String _BUSY = "Busy";
	public static final String _INVISIBLE = "Invisible";

    private static final List<Presence> PRESENCES = new ArrayList<Presence>();
	public static final Presence AVAILABLE = new Presence(Presence.Type.available, _AVAILABLE, 1, Presence.Mode.available);
	public static final Presence AWAY = new Presence(Presence.Type.available, _AWAY, 0, Presence.Mode.away);
	public static final Presence BUSY = new Presence(Presence.Type.available, _BUSY, 0, Presence.Mode.dnd);
	public static final Presence INVISIBLE = new Presence(Presence.Type.unavailable, _INVISIBLE, 0, Presence.Mode.xa);

	public static final Icon AVAILABLE_ICON;
	public static final Icon AWAY_ICON;
	public static final Icon BUSY_ICON;
	public static final Icon INVISIBLE_ICON;

    static
	{
        PRESENCES.add(AVAILABLE);
        PRESENCES.add(AWAY);
        PRESENCES.add(BUSY);
		PRESENCES.add(INVISIBLE);

		AVAILABLE_ICON = ImageUtilities.loadImageIcon("org/chartsy/chatsy/resources/available.png", true);
		AWAY_ICON = ImageUtilities.loadImageIcon("org/chartsy/chatsy/resources/away.png", true);
		BUSY_ICON = ImageUtilities.loadImageIcon("org/chartsy/chatsy/resources/busy.png", true);
		INVISIBLE_ICON = ImageUtilities.loadImageIcon("org/chartsy/chatsy/resources/invisible.png", true);
    }

    private PresenceManager()
	{
    }

    public static boolean isOnline(String jid)
	{
        final Roster roster = ChatsyManager.getConnection().getRoster();
        Presence presence = roster.getPresence(jid);
        return presence.isAvailable();
    }

    public static boolean isAvailable(String jid)
	{
        final Roster roster = ChatsyManager.getConnection().getRoster();
        Presence presence = roster.getPresence(jid);
        return presence.isAvailable() && !presence.isAway();
    }

    public static boolean isAvailable(Presence presence)
	{
        return presence.isAvailable() && !presence.isAway();
    }

    public static Presence getPresence(String jid)
	{
        final Roster roster = ChatsyManager.getConnection().getRoster();
        return roster.getPresence(jid);
    }

    public static String getFullyQualifiedJID(String jid)
	{
        final Roster roster = ChatsyManager.getConnection().getRoster();
        Presence presence = roster.getPresence(jid);
        return presence.getFrom();
    }

    public static Icon getIconFromPresence(Presence presence)
	{
        if (!presence.isAvailable())
            return INVISIBLE_ICON;

		Presence.Mode presenceMode = presence.getMode();
        if (presenceMode == null)
            presenceMode = Presence.Mode.available;

        Icon icon = null;
        if (presenceMode.equals(Presence.Mode.available)) 
            icon = AVAILABLE_ICON;
        else if (presenceMode.equals(Presence.Mode.away)) 
            icon = AWAY_ICON;
        else if (presenceMode.equals(Presence.Mode.dnd)) 
            icon = BUSY_ICON;
        else if (presenceMode.equals(Presence.Mode.xa)) 
            icon = INVISIBLE_ICON;

        Icon handlerIcon = ChatsyManager.getChatManager().getTabIconForContactHandler(presence);
        if (handlerIcon != null) 
            icon = handlerIcon;

        return icon;
    }

    public static List<Presence> getPresences()
	{
        return PRESENCES;
    }
	
}
