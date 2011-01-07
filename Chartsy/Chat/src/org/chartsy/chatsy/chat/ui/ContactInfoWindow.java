package org.chartsy.chatsy.chat.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import org.chartsy.chatsy.chat.util.FontLoader;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.VCard;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.util.GraphicUtils;
import org.chartsy.chatsy.chat.util.ModelUtil;
import org.chartsy.chatsy.chat.util.log.Log;
import org.chartsy.chatsy.chatimpl.plugin.gateways.transports.Transport;
import org.chartsy.chatsy.chatimpl.plugin.gateways.transports.TransportUtils;
import org.openide.util.ImageUtilities;

public class ContactInfoWindow extends JPanel
{
	
	private final JLabel nicknameLabel = new JLabel();
    private final JTextArea statusLabel = new JTextArea();
    private final JLabel fullJIDLabel = new JLabel();
    private final JLabel avatarLabel = new JLabel();
    private final JLabel iconLabel = new JLabel();
    private final JLabel titleLabel = new JLabel();

    private ContactItem contactItem;
    private JWindow window = new JWindow();
    private static ContactInfoWindow singleton;
    private static final Object LOCK = new Object();

    public static ContactInfoWindow getInstance()
	{
        synchronized (LOCK)
		{
            if (null == singleton)
			{
                ContactInfoWindow controller = new ContactInfoWindow();
                singleton = controller;
                return controller;
            }
        }
        return singleton;
    }

    private ContactInfoWindow()
	{
        setLayout(new GridBagLayout());       
        setBackground(Color.white);

        add(avatarLabel, new GridBagConstraints(0, 1, 1, 3, 0.0, 0.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
			new Insets(2, 2, 2, 2), 0, 0));
        add(iconLabel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(2, 2, 0, 2), 0, 0));
        add(nicknameLabel, new GridBagConstraints(2, 1, 1, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(2, 0, 0, 2), 0, 0));
        add(statusLabel, new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
			new Insets(0, 0, 0, 2), 0, 0));
        add(titleLabel, new GridBagConstraints(2, 3, 1, 1, 1.0, 0.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
			new Insets(0, 0, 2, 2), 0, 0));
        add(fullJIDLabel, new GridBagConstraints(0, 5, 4, 1, 1.0, 1.0, 
			GridBagConstraints.SOUTHWEST, GridBagConstraints.HORIZONTAL,
			new Insets(0, 2, 2, 2), 0, 0));

        nicknameLabel.setFont(FontLoader.getDroidFont(Font.BOLD, 12));
        statusLabel.setFont(FontLoader.getDroidFont(Font.PLAIN, 12));
        statusLabel.setForeground(Color.decode("0xaaaaaa"));
        statusLabel.setLineWrap(true);
        statusLabel.setWrapStyleWord(true);
        statusLabel.setEditable(false);
        statusLabel.setBorder(null);
        fullJIDLabel.setFont(FontLoader.getDroidFont(Font.PLAIN, 12));
        fullJIDLabel.setForeground(Color.decode("0xaaaaaa"));
        titleLabel.setFont(FontLoader.getDroidFont(Font.PLAIN, 11));
        titleLabel.setForeground(Color.decode("0xaaaaaa"));
        fullJIDLabel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.decode("0xeeeeee")));
        setBorder(BorderFactory.createLineBorder(Color.decode("0xeeeeee"), 1));
        window.getContentPane().add(this);

        final ContactList contactList = ChatsyManager.getWorkspace().getContactList();
        contactList.addContactListListener(new ContactListListener()
		{
            public void contactItemAdded(ContactItem item)
			{
            }
            public void contactItemRemoved(ContactItem item)
			{
            }
            public void contactGroupAdded(ContactGroup group)
			{
            }
            public void contactGroupRemoved(ContactGroup group)
			{
            }
            public void contactItemClicked(ContactItem item)
			{
                if (window != null)
                    window.dispose();
            }
            public void contactItemDoubleClicked(ContactItem item)
			{
                if (window != null)
                    window.dispose();
            }
        });
    }

    public void display(ContactGroup group, MouseEvent e)
	{
		Component component = group.getListPanel().getComponentAt(e.getPoint());
		if (!(component instanceof ContactItem))
			return;

		ContactItem item = (ContactItem) component;
		if (item == null || item.getJID() == null)
			return;
		if (getContactItem() != null && getContactItem() == item)
			return;

		iconLabel.setIcon(item.getIcon());
		Point point = item.getLocation();
		window.setFocusableWindowState(false);
		setContactItem(item);
		window.pack();

		Point mainWindowLocation = ChatsyManager.getMainWindow().getLocationOnScreen();
		Point listLocation = item.getLocationOnScreen();

		int x = (int)mainWindowLocation.getX() + ChatsyManager.getMainWindow().getWidth();
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if ((int)screenSize.getWidth() - getPreferredSize().getWidth() >= x)
		{
            int y = (int)listLocation.getY() + (int)point.getY();
            y = y - 5;
            window.setLocation(x, y);
            if (!window.isVisible())
                window.setVisible(true);
        }
		else
		{
			int y = (int)listLocation.getY() + (int)point.getY();
            y = y - 5;
            window.setLocation((int)mainWindowLocation.getX() - (int)getPreferredSize().getWidth(), y);
            if (!window.isVisible())
                window.setVisible(true);
		}
    }

    public void setContactItem(ContactItem contactItem)
	{
        this.contactItem = contactItem;
        if (contactItem == null)
            return;

        nicknameLabel.setText(contactItem.getDisplayName());

        String status = contactItem.getStatus();
        if (!ModelUtil.hasLength(status))
		{
            if (contactItem.getPresence() == null || contactItem.getPresence().getType() == Presence.Type.unavailable)
                status = "Offline";
            else
                status = "Online";
        }
        statusLabel.setText(status);

        Transport transport = TransportUtils.getTransport(StringUtils.parseServer(contactItem.getJID()));
        if (transport != null)
		{
            fullJIDLabel.setIcon(transport.getIcon());
            String name = StringUtils.parseName(contactItem.getJID());
            name = StringUtils.unescapeNode(name);
            fullJIDLabel.setText(transport.getName() + " - " + name);
        }
        else
		{
            String name = StringUtils.unescapeNode(contactItem.getJID());
            fullJIDLabel.setText(name);
            fullJIDLabel.setIcon(null);
        }

        avatarLabel.setBorder(null);
        try
		{
            URL avatarURL = contactItem.getAvatarURL();
            ImageIcon icon = null;
            if (avatarURL != null)
                icon = new ImageIcon(avatarURL);

            if (icon != null && icon.getIconHeight() > 1)
			{
                icon = GraphicUtils.scaleImageIcon(icon, 96, 96);
                avatarLabel.setIcon(icon);
            }
            else 
			{
				icon = ImageUtilities.loadImageIcon("org/chartsy/chatsy/resources/default-avatar-64.png", true);
                avatarLabel.setIcon(icon);
            }
            avatarLabel.setBorder(BorderFactory.createBevelBorder(0, Color.white, Color.lightGray));
        }
        catch (MalformedURLException e)
		{
            Log.error(e);
        }

        String title = "";
        VCard vcard = ChatsyManager.getVCardManager().getVCardFromMemory(StringUtils.parseBareAddress(contactItem.getJID()));
        if (vcard != null)
		{
            title = vcard.getField("TITLE");
            if (!ModelUtil.hasLength(title))
                title = "";
        }
        titleLabel.setText(title);
    }

    public ContactItem getContactItem()
	{
        return contactItem;
    }

    public void dispose()
	{
        window.setVisible(false);
        contactItem = null;
        window.dispose();
    }

    public Dimension getPreferredSize()
	{
        final Dimension size = super.getPreferredSize();
        size.width = 300;
        size.height = 125;
        return size;
    }

    public void mouseEntered(MouseEvent e)
	{
    }

    public void mouseExited(MouseEvent e)
	{
        Point point = e.getPoint();
        Dimension dim = window.getSize();

        int x = (int)point.getX();
        int y = (int)point.getY();

        boolean close = false;
        if (x < 0 || x >= dim.getWidth())
            close = true;
        if (y < 0 || y >= dim.getHeight())
            close = true;

        if (close)
		{
            window.setVisible(false);
            contactItem = null;
        }
    }

}
