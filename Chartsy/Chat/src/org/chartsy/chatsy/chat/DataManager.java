package org.chartsy.chatsy.chat;

import org.chartsy.chatsy.chat.plugin.MetadataListener;
import org.chartsy.chatsy.chat.ui.ChatRoom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataManager
{

    private static DataManager singleton;
    private static final Object LOCK = new Object();
	private List<MetadataListener> metadataListeners = new ArrayList<MetadataListener>();

    public static DataManager getInstance()
	{
        synchronized (LOCK)
		{
            if (null == singleton)
			{
                DataManager controller = new DataManager();
                singleton = controller;
                return controller;
            }
        }
        return singleton;
    }

    private DataManager()
	{
    }

    public void addMetadataListener(MetadataListener listener)
	{
        metadataListeners.add(listener);
    }

    public void removeMetadataListener(MetadataListener listener)
	{
        metadataListeners.remove(listener);
    }

    public void setMetadataForRoom(ChatRoom room, Map map)
	{
        for (MetadataListener listener : metadataListeners)
            listener.metadataAssociatedWithRoom(room, map);
    }
	
}
