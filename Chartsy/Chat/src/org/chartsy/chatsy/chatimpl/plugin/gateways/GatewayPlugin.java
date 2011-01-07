package org.chartsy.chatsy.chatimpl.plugin.gateways;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.OrFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.packet.DiscoverItems.Item;
import org.chartsy.chatsy.chat.ChatManager;
import org.chartsy.chatsy.chat.PresenceManager;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.component.MessageDialog;
import org.chartsy.chatsy.chat.plugin.Plugin;
import org.chartsy.chatsy.chat.ui.*;
import org.chartsy.chatsy.chat.util.SwingWorker;
import org.chartsy.chatsy.chat.util.log.Log;
import org.chartsy.chatsy.chatimpl.plugin.gateways.transports.*;

import javax.swing.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GatewayPlugin implements Plugin, ContactItemHandler
{

    public static final String GATEWAY = "gateway";
    private Map<Transport, GatewayButton> uiMap = new HashMap<Transport, GatewayButton>();

    public void initialize()
	{
        ProviderManager.getInstance().addIQProvider(Gateway.ELEMENT_NAME, Gateway.NAMESPACE, new Gateway.Provider());
        SwingWorker thread = new SwingWorker()
		{
            public Object construct()
			{
                try
				{
                    Thread.sleep(5000);
                    populateTransports();
                }
                catch (Exception e)
				{
                    Log.error(e);
                    return false;
                }
                return true;
            }
            public void finished()
			{
                Boolean transportExists = (Boolean) get();
                if (!transportExists) 
                    return;

                if (TransportUtils.getTransports().size() > 0)
				{
                    final JPanel commandPanel = ChatsyManager.getWorkspace().getCommandPanel();
                    final JLabel dividerLabel = new JLabel("div");
                    commandPanel.add(dividerLabel);
                }

                for (final Transport transport : TransportUtils.getTransports()) 
                    addTransport(transport);
                registerPresenceListener();
            }
        };
        thread.start();
    }

    public void shutdown()
	{
    }

    public boolean canShutDown()
	{
        return false;
    }

    public void uninstall()
	{
    }

    private void populateTransports() throws Exception
	{
        DiscoverItems discoItems = ChatsyManager.getSessionManager().getDiscoveredItems();
        DiscoverItems.Item item;
        Iterator<DiscoverItems.Item> items = discoItems.getItems();
        while (items.hasNext())
		{
            item = (Item)items.next();
            String entityName = item.getEntityID();
            if (entityName != null)
			{
                if (entityName.startsWith("xmpp."))
				{
                    XMPPTransport xmppTransport = new XMPPTransport(item.getEntityID());
                    TransportUtils.addTransport(item.getEntityID(), xmppTransport);
                }
                else if (entityName.startsWith("sip."))
				{
                    SimpleTransport simpleTransport = new SimpleTransport(item.getEntityID());
                    TransportUtils.addTransport(item.getEntityID(), simpleTransport);
                }              
            }
        }

    }

    private void addTransport(final Transport transport)
	{
        final GatewayButton button = new GatewayButton(transport);
        uiMap.put(transport, button);
    }

    private void registerPresenceListener()
	{
        PacketFilter orFilter = new OrFilter(new PacketTypeFilter(Presence.class), new PacketTypeFilter(Message.class));
        ChatsyManager.getConnection().addPacketListener(new PacketListener()
		{
            public void processPacket(Packet packet)
			{
                if (packet instanceof Presence)
				{
                    Presence presence = (Presence)packet;
                    Transport transport = TransportUtils.getTransport(packet.getFrom());
                    if (transport != null)
					{
                        boolean registered = true;
                        if (presence.getType() == Presence.Type.unavailable) 
                            registered = false;
                        GatewayButton button = uiMap.get(transport);
                        button.signedIn(registered);
                    }
                }
                else if (packet instanceof Message)
				{
                    Message message = (Message)packet;
                    String from = message.getFrom();
                    boolean hasError = message.getType() == Message.Type.error;
                    String body = message.getBody();
                    if (from != null && hasError)
					{
                        Transport transport = TransportUtils.getTransport(from);
                        if (transport != null)
						{
                            String title = "Alert from " + transport.getName();
                            MessageDialog.showAlert(body, title, "Information", null);
                        }
                    }
                }
            }
        }, orFilter);

        ChatManager chatManager = ChatsyManager.getChatManager();
        chatManager.addContactItemHandler(this);

        final ContactList contactList = ChatsyManager.getWorkspace().getContactList();
        for (ContactGroup contactGroup : contactList.getContactGroups())
		{
            for (ContactItem contactItem : contactGroup.getContactItems())
			{
                Presence presence = contactItem.getPresence();
                if (presence.isAvailable())
				{
                    String domain = StringUtils.parseServer(presence.getFrom());
                    Transport transport = TransportUtils.getTransport(domain);
                    if (transport != null)
					{
                        handlePresence(contactItem, presence);
                        contactGroup.fireContactGroupUpdated();
                    }
                }
            }
        }

        ChatsyManager.getSessionManager().addPresenceListener(new PresenceListener()
		{
            public void presenceChanged(Presence presence)
			{
                for (Transport transport : TransportUtils.getTransports())
				{
                    GatewayButton button = uiMap.get(transport);
                    if (button.isLoggedIn())
					{
                        if (!presence.isAvailable()) 
                            return;
                        Presence p = new Presence(presence.getType(), presence.getStatus(), presence.getPriority(), presence.getMode());
                        p.setTo(transport.getServiceName());
                        ChatsyManager.getConnection().sendPacket(p);
                    }
                }
            }
        });
    }


    public boolean handlePresence(ContactItem item, Presence presence)
	{
        if (presence.isAvailable())
		{
            String domain = StringUtils.parseServer(presence.getFrom());
            Transport transport = TransportUtils.getTransport(domain);
            if (transport != null)
			{
                if (presence.getType() == Presence.Type.available) {
                    item.setAvatarImage(transport.getIcon());
                }
                else
				{
                    item.setAvatarImage(transport.getInactiveIcon());
                }
                return false;
            }
        }

        return false;
    }

    public boolean handleDoubleClick(ContactItem item)
	{
        return false;
    }

    public Icon getIcon(String jid)
	{
        String domain = StringUtils.parseServer(jid);
        Transport transport = TransportUtils.getTransport(domain);
        if (transport != null)
		{
            if (PresenceManager.isOnline(jid))
			{
                return transport.getIcon();
            }
            else
			{
                return transport.getInactiveIcon();
            }
        }
        return null;
    }

    public Icon getTabIcon(Presence presence)
	{
        return null;
    }
	
}
