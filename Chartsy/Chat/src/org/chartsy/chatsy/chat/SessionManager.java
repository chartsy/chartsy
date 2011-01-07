package org.chartsy.chatsy.chat;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.chartsy.chatsy.chat.ui.PresenceListener;
import org.chartsy.chatsy.chat.util.log.Log;
import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;
import org.jivesoftware.smack.Roster.SubscriptionMode;

public final class SessionManager implements ConnectionListener
{
	
    private XMPPConnection connection;
    private PrivateDataManager personalDataManager;
    private String serverAddress;
    private String username;
    private String password;
    private String JID;
    private List<PresenceListener> presenceListeners = new ArrayList<PresenceListener>();
    private String userBareAddress;
    private DiscoverItems discoverItems;

    public SessionManager()
	{
    }

    public void initializeSession(XMPPConnection connection, String username, String password)
	{
        this.connection = connection;
        this.username = username;
        this.password = password;
        this.userBareAddress = StringUtils.parseBareAddress(connection.getUser());
        personalDataManager = new PrivateDataManager(getConnection());
        discoverItems();
    }

    private void discoverItems()
	{
        ServiceDiscoveryManager disco = ServiceDiscoveryManager.getInstanceFor(ChatsyManager.getConnection());
        try
		{
            discoverItems = disco.discoverItems(ChatsyManager.getConnection().getServiceName());
        }
        catch (XMPPException e)
		{
            Log.error(e);
            discoverItems = new DiscoverItems();
        }
    }

    public XMPPConnection getConnection()
	{
        return connection;
    }

    public PrivateDataManager getPersonalDataManager()
	{
        return personalDataManager;
    }

    public String getServerAddress()
	{
        return serverAddress;
    }

    public void setServerAddress(String address)
	{
        this.serverAddress = address;
    }

    public void connectionClosedOnError(final Exception ex)
	{
        SwingUtilities.invokeLater(new Runnable()
		{
            public void run()
			{
                final Presence presence = new Presence(Presence.Type.unavailable);
                changePresence(presence);
                Log.debug("Connection closed on error.: " + ex.getMessage());
            }
        });
    }

    public void connectionClosed()
	{
    }

    public String getUsername()
	{
        return StringUtils.unescapeNode(username);
    }

    public String getPassword()
	{
        return password;
    }

    public void changePresence(Presence presence)
	{
        for (PresenceListener listener : new ArrayList<PresenceListener>(this.presenceListeners))
            listener.presenceChanged(presence);
        if (ChatsyManager.getConnection().isConnected())
            ChatsyManager.getConnection().sendPacket(presence);
    }

    public String getJID()
	{
        return JID;
    }

    public void setJID(String jid)
	{
        this.JID = jid;
    }

    public void addPresenceListener(PresenceListener listener)
	{
        presenceListeners.add(listener);
    }

    public void removePresenceListener(PresenceListener listener)
	{
        presenceListeners.remove(listener);
    }

    public String getBareAddress()
	{
        return userBareAddress;
    }

    public DiscoverItems getDiscoveredItems()
	{
        return discoverItems;
    }

    public void setConnection(XMPPConnection con)
	{
        this.connection = con;
		connection.getRoster().setSubscriptionMode(SubscriptionMode.accept_all);
    }

    public void reconnectingIn(int i)
	{
    }

    public void reconnectionSuccessful()
	{
    }

    public void reconnectionFailed(Exception exception)
	{
    }

}
