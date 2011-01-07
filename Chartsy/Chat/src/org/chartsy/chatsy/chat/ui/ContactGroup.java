package org.chartsy.chatsy.chat.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import org.jivesoftware.smack.packet.Presence;
import org.chartsy.chatsy.chat.PresenceManager;
import org.chartsy.chatsy.chat.util.ModelUtil;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;

import javax.swing.JLabel;
import javax.swing.JPanel;
import org.chartsy.chatsy.chat.util.FontLoader;
import org.chartsy.chatsy.chat.ChatsyManager;

public class ContactGroup extends JPanel implements MouseListener
{

	private static final Logger LOG = Logger.getLogger(ContactGroup.class.getName());

	private List<String> contactItems = new ArrayList<String>();
	private Map<String, ContactItem> contactItemsMap = new HashMap<String, ContactItem>();
    private List<ContactGroupListener> listeners = new ArrayList<ContactGroupListener>();

    private String groupName;
	private GroupNamePanel groupNamePanel;
    private JPanel listPanel;
	private boolean expanded = true;

    public ContactGroup(String groupName)
	{
		super(new BorderLayout());
		setOpaque(false);
		setName(groupName);

		this.groupName = groupName;

		groupNamePanel = new GroupNamePanel();
		groupNamePanel.setGroupName(groupName);

		listPanel = new JPanel();
		listPanel.setOpaque(false);
		listPanel.setLayout(new LayoutManager()
		{
			public void addLayoutComponent(String name, Component comp)
			{
			}
			public void removeLayoutComponent(Component comp)
			{
			}
			public Dimension preferredLayoutSize(Container parent)
			{
				return new Dimension(0, 0);
			}
			public Dimension minimumLayoutSize(Container parent)
			{
				return new Dimension(0, 0);
			}
			public void layoutContainer(Container parent)
			{
				Insets insets = parent.getInsets();
				int w = parent.getWidth() - insets.left - insets.right;
				int x = 0;
				int y = 0;

				for (Component component : parent.getComponents())
				{
					if (component.isVisible())
					{
						component.setBounds(x, y, w, 42);
						y += 42;
					}
				}
			}
		});

		add(groupNamePanel, BorderLayout.NORTH);
		add(listPanel, BorderLayout.CENTER);

		groupNamePanel.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON1)
					toggleExpanded();
				else if (e.getButton() == MouseEvent.BUTTON3)
					fireContactGroupPopupEvent(e);
			}
		});
    }

	public int getPanelHeight()
	{
		int height = 25;
		if (expanded)
			for (int i = 0; i < getComponentCount(); i++)
				height += 42;
		return height;
	}

	public void toggleExpanded()
	{
		setExpanded(!expanded);
	}

	public void setExpanded(boolean expanded)
	{
		this.expanded = expanded;
		this.listPanel.setVisible(expanded);
	}

	public boolean isExpanded()
	{
		return expanded;
	}

	public boolean isEmpty()
	{
		Component[] components = listPanel.getComponents();
		for (Component component : components)
			if (component.isVisible())
				return false;
		return true;
	}

	public void setTitle(String title)
	{
		groupNamePanel.setGroupName(title);
	}

    public void addOfflineContactItem(final String alias, final String nickname, final String jid, final String status)
	{
       try 
	   {
		   EventQueue.invokeLater(new Runnable()
		   {
			   public void run()
			   {
				   final ContactItem offlineItem = new ContactItem(alias, nickname, jid);
				   offlineItem.setGroupName(getGroupName());
				   final Presence offlinePresence = PresenceManager.getPresence(jid);
				   offlineItem.setPresence(offlinePresence);
				   offlineItem.setIcon(PresenceManager.getIconFromPresence(offlinePresence));
				   if (ModelUtil.hasLength(status))
					   offlineItem.setStatusText(status);
				   offlineItem.setOffline(true);
				   contactItems.add(offlineItem.getJID());
				   contactItemsMap.put(offlineItem.getJID(), offlineItem);
				   insertOfflineContactItem(offlineItem);
			   }
		   });
       }
       catch(Exception ex)
	   {
		   LOG.log(Level.SEVERE, "", ex);
       }
    }

    public void insertOfflineContactItem(ContactItem offlineItem)
	{
		Collections.sort(contactItems);
		int index = contactItems.indexOf(offlineItem.getJID());
		listPanel.add(offlineItem, index);
    }

    public void removeOfflineContactItem(ContactItem item)
	{
        contactItems.remove(item.getJID());
		contactItemsMap.remove(item.getJID());
        removeContactItem(item);
    }

    public void removeOfflineContactItem(String jid)
	{
        for (Component component : listPanel.getComponents())
            if (component.getName().equals(jid))
                removeOfflineContactItem((ContactItem) component);
    }

    public void toggleOfflineVisibility(boolean show)
	{
		final List<ContactItem> items = getContactItems();
		for (ContactItem contactItem : items)
		{
			if (show)
				insertOfflineContactItem(contactItem);
		}
    }

    public void addContactItem(ContactItem item)
	{
		removeOfflineContactItem(item.getJID());

        item.setGroupName(getGroupName());
        contactItems.add(item.getJID());
		contactItemsMap.put(item.getJID(), item);

		List<String> tempItems = getContactItemsJIDs();
        Collections.sort(tempItems);
        int index = tempItems.indexOf(item.getJID());
        listPanel.add(item, index);
        fireContactItemAdded(item);
		updateTitle();
    }

    public void fireContactGroupUpdated()
	{
		listPanel.validate();
		listPanel.repaint();
		updateTitle();
    }

    public void addContactGroup(ContactGroup contactGroup)
	{
    }

    public void removeContactGroup(ContactGroup contactGroup)
	{
    }

    public void setPanelBackground(Color color)
	{
    }

    public ContactGroup getContactGroup(String groupName)
	{
        return null;
    }

    public void removeContactItem(ContactItem item)
	{
        contactItems.remove(item.getJID());
		contactItemsMap.remove(item.getJID());
        if (contactItems.isEmpty())
			setVisible(false);
        listPanel.remove(item);
        updateTitle();
        fireContactItemRemoved(item);
    }

    public ContactItem getContactItemByDisplayName(String displayName)
	{
        for (Component component : listPanel.getComponents())
            if (component instanceof ContactItem) {
				final ContactItem item = (ContactItem) component;
				if (item.getDisplayName().equals(displayName))
					return item;
			}
        return null;
    }

    public ContactItem getContactItemByJID(String bareJID)
	{
		return contactItemsMap.get(bareJID);
    }

    public List<ContactItem> getContactItems()
	{
        final List<ContactItem> list = new ArrayList<ContactItem>();
		for (Component component : listPanel.getComponents())
			if (component instanceof ContactItem)
				list.add((ContactItem)component);
        return list;
    }

    public String getGroupName()
	{
        return groupName;
    }

    public void mouseClicked(MouseEvent e)
	{
    }

    public void mouseEntered(MouseEvent e)
	{
    }

    public void mouseExited(MouseEvent e)
	{
    }

    public void mousePressed(MouseEvent e)
	{
    }

    public void mouseReleased(MouseEvent e)
	{
    }

    public void checkPopup(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON3)
		{
			final Collection<ContactItem> selectedItems = ChatsyManager.getChatManager().getSelectedContactItems();
			if (selectedItems.size() > 1)
				firePopupEvent(e, selectedItems);
			else if (selectedItems.size() == 1)
				firePopupEvent(e, (ContactItem)selectedItems.iterator().next());
		}
    }

    public void addContactGroupListener(ContactGroupListener listener)
	{
        listeners.add(listener);
    }

    public void removeContactGroupListener(ContactGroupListener listener)
	{
        listeners.remove(listener);
    }

	public void contactItemClicked(ContactItem item)
	{
		fireContactItemClicked(item);
	}

    private void fireContactItemClicked(ContactItem item)
	{
        for (ContactGroupListener contactGroupListener : new ArrayList<ContactGroupListener>(listeners)) 
            contactGroupListener.contactItemClicked(item);
    }

	public void contactItemDoubleClicked(ContactItem item)
	{
		fireContactItemDoubleClicked(item);
	}

    private void fireContactItemDoubleClicked(ContactItem item)
	{
        for (ContactGroupListener contactGroupListener : new ArrayList<ContactGroupListener>(listeners)) 
            contactGroupListener.contactItemDoubleClicked(item);
    }


    private void firePopupEvent(MouseEvent e, ContactItem item)
	{
        for (ContactGroupListener contactGroupListener : new ArrayList<ContactGroupListener>(listeners)) 
            contactGroupListener.showPopup(e, item);
    }

    private void firePopupEvent(MouseEvent e, Collection<ContactItem> items)
	{
        for (ContactGroupListener contactGroupListener : new ArrayList<ContactGroupListener>(listeners)) 
            contactGroupListener.showPopup(e, items);
    }

    private void fireContactGroupPopupEvent(MouseEvent e)
	{
        for (ContactGroupListener contactGroupListener : new ArrayList<ContactGroupListener>(listeners))
            contactGroupListener.contactGroupPopup(e, this);
    }

    private void fireContactItemAdded(ContactItem item)
	{
        for (ContactGroupListener contactGroupListener : new ArrayList<ContactGroupListener>(listeners)) 
            contactGroupListener.contactItemAdded(item);
    }

    private void fireContactItemRemoved(ContactItem item)
	{
        for (ContactGroupListener contactGroupListener : new ArrayList<ContactGroupListener>(listeners)) 
            contactGroupListener.contactItemRemoved(item);
    }

    private void updateTitle()
	{
        int count = 0;
        List<ContactItem> list = new ArrayList<ContactItem>(getContactItems());
        int size = list.size();
        for (int i = 0; i < size; i++)
		{
            ContactItem it = list.get(i);
            if (!it.isOffline())
                count++;
        }
        setTitle(getGroupTitle(groupName) + " (" + count + " " + "Online" + ")");
		setVisible(count == 0 ? false : true);
    }

    public void clearSelection()
	{
    }

    public void removeAllContacts()
	{
        for (ContactItem item : new ArrayList<ContactItem>(getContactItems()))
            removeContactItem(item);
        for (ContactItem item : getOfflineContacts())
            removeOfflineContactItem(item);
    }

    public boolean hasAvailableContacts()
	{
        for (ContactItem item : getContactItems()) 
            if (item.getPresence() != null) 
                return true;
        return false;
    }

    public Collection<ContactItem> getOfflineContacts()
	{
		final List<ContactItem> list = new ArrayList<ContactItem>();
		for (ContactItem contactItem : getContactItems())
			if (contactItem.isOffline())
				list.add(contactItem);
        return list;
    }

    final Comparator<ContactItem> itemComparator = new Comparator<ContactItem>()
	{
        public int compare(ContactItem item1, ContactItem item2)
		{
            return item1.getDisplayName().toLowerCase().compareTo(item2.getDisplayName().toLowerCase());
        }
    };

    public boolean isOfflineGroup()
	{
        return "Offline Group".equals(getGroupName());
    }

    public boolean isUnfiledGroup()
	{
        return "Unfiled".equals(getGroupName());
    }

    public String toString()
	{
        return getGroupName();
    }

    public boolean isSharedGroup()
	{
        return false;
    }

    protected void setSharedGroup(boolean sharedGroup)
	{
    }

    public List<ContactItem> getSelectedContacts()
	{
		return new ArrayList<ContactItem>();
    }

    public JPanel getContainerPanel()
	{
        return listPanel;
    }

    public Collection<ContactGroup> getContactGroups()
	{
        return new ArrayList<ContactGroup>();
    }

    public Dimension getPreferredSize()
	{
        final Dimension size = super.getPreferredSize();
        size.width = 0;
        return size;
    }

    public void setGroupName(String groupName)
	{
        this.groupName = groupName;
		this.setName(groupName);
		updateTitle();
    }

    public String getGroupTitle(String title)
	{
        int lastIndex = title.lastIndexOf("::");
        if (lastIndex != -1) 
            title = title.substring(lastIndex + 2);
        return title;
    }
    
    public boolean isSubGroup(String groupName)
	{
        return groupName.indexOf("::") != -1;
    }

    public boolean isSubGroup()
	{
        return isSubGroup(getGroupName());
    }

    public JPanel getListPanel()
	{
        return listPanel;
    }

	private List<String> getContactItemsJIDs()
	{
		return contactItems;
	}

	private static class GroupNamePanel extends JPanel
	{

		private JLabel groupName;
		private TexturePaint texturePaint;

		private GroupNamePanel()
		{
			super(new BorderLayout());
			setOpaque(false);
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			groupName = new JLabel();
			groupName.setOpaque(false);
			groupName.setFont(FontLoader.getDroidFont(Font.BOLD, 14));
			groupName.setForeground(Color.WHITE);
			groupName.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
			add(groupName, BorderLayout.CENTER);

			try
			{
				BufferedImage bufferedImage = ImageIO.read(getClass().getResource("/org/chartsy/chatsy/resources/contact-group-bg.png"));
				Rectangle rectangle = new Rectangle(0, 0, bufferedImage.getWidth(null), bufferedImage.getHeight(null));
				texturePaint = new TexturePaint(bufferedImage, rectangle);
			}
			catch (IOException ex)
			{
			}
		}

		public void setGroupName(String name)
		{
			groupName.setText(name);
		}

		public String getGroupName()
		{
			return groupName.getText();
		}

		public Dimension getPreferredSize()
		{
			final Dimension dimension = super.getPreferredSize();
			dimension.setSize(dimension.getWidth(), 25);
			return dimension;
		}

		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			Graphics2D graphics2D = (Graphics2D) g;
			graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			graphics2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
			graphics2D.setPaint(texturePaint);
			graphics2D.fill(new Rectangle(0, 0, getWidth(), getHeight()));
		}

	}

}