package org.chartsy.chatsy.chatimpl.plugin.manager;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.util.log.Log;

import java.util.Iterator;

public class Enterprise
{

	private static Enterprise instance;
    public static final String MUC_FEATURE = "muc";
    public static final String VCARD_FEATURE = "vcard";
    private static DiscoverInfo featureInfo;
    private boolean chatsyManagerInstalled = true;

	public static Enterprise getInstance()
	{
		if (instance == null)
			instance = new Enterprise();
		return instance;
	}

    private Enterprise()
	{
        populateFeatureSet();
    }

    public boolean isChatsyManagerInstalled()
	{
        return chatsyManagerInstalled;
    }

    public static boolean containsFeature(String feature)
	{
        if (featureInfo == null)
            return true;
        return featureInfo.containsFeature(feature);
    }

    private void populateFeatureSet()
	{
        final ServiceDiscoveryManager disco = ServiceDiscoveryManager.getInstanceFor(ChatsyManager.getConnection());
        final DiscoverItems items = ChatsyManager.getSessionManager().getDiscoveredItems();
        Iterator<DiscoverItems.Item> iter = items.getItems();
        while (iter.hasNext())
		{
            DiscoverItems.Item item = iter.next();
            String entity = item.getEntityID();
            if (entity != null)
			{
                if (entity.startsWith("manager."))
				{
                    chatsyManagerInstalled = true;
                    try
					{
                        featureInfo = disco.discoverInfo(item.getEntityID());
                    }
                    catch (XMPPException e)
					{
                        Log.error("Error while retrieving feature list for ChatsyManager.", e);
                    }
                }
            }
        }
    }
	
}
