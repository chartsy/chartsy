package org.chartsy.chatsy.chatimpl.search.users;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.search.Searchable;
import org.chartsy.chatsy.chat.ui.DataFormUI;
import org.chartsy.chatsy.chat.util.GraphicUtils;
import org.chartsy.chatsy.chat.util.SwingWorker;
import org.chartsy.chatsy.chat.util.log.Log;

import javax.swing.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.openide.windows.WindowManager;

public class UserSearchService implements Searchable
{
	
    private Collection searchServices;

    public UserSearchService()
	{
        loadSearchServices();
    }

    public void search(final String query)
	{
        SwingWorker worker = new SwingWorker()
		{
            public Object construct()
			{
                if (searchServices == null)
                    loadSearchServices();
                return true;
            }
            public void finished()
			{
                processQuery(query);
            }
        };
        worker.start();
    }

    private void processQuery(String query)
	{
        if (searchServices == null)
		{
            JOptionPane.showMessageDialog(
				ChatsyManager.getMainWindow(),
				"Unable to contact search service",
				"Error",
				JOptionPane.ERROR_MESSAGE);
            return;
        }

        UserSearchForm searchForm;
        DataFormUI dataFormUI;
        try
		{
            searchForm = new UserSearchForm(searchServices);
            dataFormUI = searchForm.getQuestionForm();
        }
        catch (Exception e)
		{
            JOptionPane.showMessageDialog(
				ChatsyManager.getMainWindow(),
				"Unable to contact search service",
				"Error",
				JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField textField = (JTextField)dataFormUI.getComponent("search");
        if (textField != null)
		{
            textField.setText(query);
        }
        else
		{
            textField = (JTextField)dataFormUI.getComponent("last");
            if (textField != null) 
                textField.setText(query);
        }

        if (textField == null)
		{
            textField = (JTextField)dataFormUI.getComponent("userName");
            if (textField != null) 
                textField.setText(query);
        }

        if (textField != null)
            searchForm.performSearch();

        JFrame frame = new JFrame();
        frame.setIconImage(WindowManager.getDefault().getMainWindow().getIconImage());
        final JDialog dialog = new JDialog(frame, "Buddy Search", false);
        dialog.getContentPane().add(searchForm);
        dialog.pack();
        dialog.setSize(500, 500);

        GraphicUtils.centerWindowOnScreen(dialog);
        dialog.setVisible(true);
    }

    private void loadSearchServices()
	{
        try
		{
            searchServices = getServices();
        }
        catch (Exception e)
		{
            Log.error("Unable to load search services.", e);
        }
    }

    private Collection getServices() throws Exception
	{
        final Set<String> searchServicesSet = new HashSet<String>();
        ServiceDiscoveryManager discoManager = ServiceDiscoveryManager.getInstanceFor(ChatsyManager.getConnection());
        DiscoverItems items = ChatsyManager.getSessionManager().getDiscoveredItems();
        Iterator<DiscoverItems.Item> iter = items.getItems();
        while (iter.hasNext())
		{
            DiscoverItems.Item item = iter.next();
            try
			{
                DiscoverInfo info;
                try
				{
                    info = discoManager.discoverInfo(item.getEntityID());
                }
                catch (XMPPException e)
				{
                    continue;
                }

                if (info.containsFeature("jabber:iq:search"))
				{
                    for (Iterator<DiscoverInfo.Identity> identities = info.getIdentities(); identities.hasNext();)
					{
                        DiscoverInfo.Identity identity = identities.next();
                        if ("directory".equals(identity.getCategory()) && "user".equals(identity.getType()))
                            searchServicesSet.add(item.getEntityID());
                    }
                }
            }
            catch (Exception e)
			{
                break;
            }
        }
        return searchServicesSet;
    }

    public Collection getSearchServices()
	{
        return searchServices;
    }

    public String getToolTip()
	{
        return "Search for other people on the server";
    }

    public String getDefaultText()
	{
        return "Search for other people on the server";
    }

    public String getName()
	{
        return "Person search";
    }

    public Icon getIcon()
	{
        return null;
    }

}
