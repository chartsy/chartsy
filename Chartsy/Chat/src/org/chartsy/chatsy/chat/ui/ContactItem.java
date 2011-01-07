package org.chartsy.chatsy.chat.ui;

import org.jivesoftware.smack.packet.DefaultPacketExtension;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence;
import org.chartsy.chatsy.chat.ChatManager;
import org.chartsy.chatsy.chat.PresenceManager;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.util.GraphicUtils;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.MalformedURLException;
import java.net.URL;
import org.chartsy.chatsy.chat.util.FontLoader;
import org.openide.util.ImageUtilities;

public final class ContactItem extends JPanel implements MouseListener
{

	private ContactItemInfo itemInfo;
	private AvatarLabel avatarLabel;
	
    private String nickname;
    private String alias;
    private String fullyQualifiedJID;
    private String status;
    private String groupName;

	private Icon icon;

    private Presence presence;

    private int fontSize = 12;
    private int iconSize = 32;

	private boolean mouseOver = false;
	private boolean selected = false;
	boolean isOffline = true;

    public ContactItem(String alias, String nickname, String fullyQualifiedJID)
	{
        setLayout(new BorderLayout());
		setOpaque(true);
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		setFocusable(true);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        avatarLabel = new AvatarLabel();
		itemInfo = new ContactItemInfo();

		add(avatarLabel, BorderLayout.WEST);
		add(itemInfo, BorderLayout.CENTER);

        this.alias = alias;
        this.nickname = nickname;
        this.fullyQualifiedJID = fullyQualifiedJID;
		setName(fullyQualifiedJID);

        setDisplayName();
		presence = PresenceManager.INVISIBLE;
		updatePresenceIcon(presence);
		updateAvatar();
		updateAvatarInSideIcon();

		addMouseListener((MouseListener)this);
    }

    public String getDisplayName()
	{
    	final String displayName;
		if (alias != null) 
			displayName = alias;
		else if (nickname != null)
			displayName = nickname;
		else
			displayName = getJID();
		
		if (displayName != null) 
			return displayName;
		else
			return "";
	}

	public String getNickname()
	{
		return nickname;
	}
	
    public void setNickname(String nickname)
	{
        this.nickname = nickname;
        if (alias == null) 
        	setDisplayName();
    }

	public String getAlias()
	{
		return alias;
	}
    
    public void setAlias(String alias)
	{
    	this.alias = alias;
    	setDisplayName();
    }

    private void setDisplayName()
	{
    	final String displayName = getDisplayName();
		itemInfo.setNickname(displayName);
    }
    
    public String getJID()
	{
        return fullyQualifiedJID;
    }

    public Icon getIcon()
	{
        return icon;
    }

    public void setIcon(Icon icon)
	{
        this.icon = icon;
		itemInfo.getDisplayNameLabel().setIcon(icon);
    }

    public String getStatus()
	{
        return status;
    }

    public void setStatus(String status)
	{
        this.status = status;
    }

    public String getGroupName()
	{
        return groupName;
    }

    public void setGroupName(String groupName)
	{
        this.groupName = groupName;
    }

	public boolean isOffline()
	{
		return isOffline;
	}

	public void setOffline(boolean offline)
	{
		isOffline = offline;
		setVisible(!offline);
	}

    public JLabel getNicknameLabel()
	{
        return itemInfo.getDisplayNameLabel();
    }

    public JLabel getDescriptionLabel()
	{
        return itemInfo.getDescriptionLabel();
    }

    public Presence getPresence()
	{
        return presence;
    }

    public void setPresence(Presence presence)
	{
        this.presence = presence;
        final PacketExtension packetExtension = presence.getExtension("x", "vcard-temp:x:update");
        if (packetExtension != null)
		{
            DefaultPacketExtension o = (DefaultPacketExtension) packetExtension;
            String hash = o.getValue("photo");
            if (hash != null)
			{
				updateAvatar();
                updateAvatarInSideIcon();
            }
        }
        updatePresenceIcon(presence);
    }

    public URL getAvatarURL() throws MalformedURLException
	{
        return ChatsyManager.getVCardManager().getAvatarURL(getJID());
    }

    private void updateAvatar()
	{
        ChatsyManager.getVCardManager().addToQueue(getJID());
    }

    public String toString()
	{
        return itemInfo.getNickname();
    }

    public void updatePresenceIcon(Presence presence)
	{
		getNicknameLabel().setForeground(Color.decode("0x111111"));
		getNicknameLabel().setFont(FontLoader.getDroidFont(Font.PLAIN, fontSize));

        ChatManager chatManager = ChatsyManager.getChatManager();
        boolean handled = chatManager.fireContactItemPresenceChanged(this, presence);
        if (handled) 
            return;

		if (presence.isAvailable())
		{
			String statusText = presence.getStatus();
			if (statusText == null)
			{
				setStatus(PresenceManager._AVAILABLE);
				setIcon(PresenceManager.getIconFromPresence(PresenceManager.AVAILABLE));
				setOffline(false);
				return;
			}
			
			if (statusText.equals(PresenceManager._AVAILABLE))
			{
				setStatusText(PresenceManager._AVAILABLE);
				setIcon(PresenceManager.getIconFromPresence(PresenceManager.AVAILABLE));
				setOffline(false);
			}
			else if (statusText.equals(PresenceManager._AWAY))
			{
				setStatusText(PresenceManager._AWAY);
				setIcon(PresenceManager.getIconFromPresence(PresenceManager.AWAY));
				setOffline(false);
			}
			else if (statusText.equals(PresenceManager._BUSY))
			{
				setStatusText(PresenceManager._BUSY);
				setIcon(PresenceManager.getIconFromPresence(PresenceManager.BUSY));
				setOffline(false);
			}
			else if (statusText.equals(PresenceManager._INVISIBLE))
			{
				setStatusText("");
				setIcon(PresenceManager.getIconFromPresence(PresenceManager.INVISIBLE));
				setOffline(true);
			}
		}
		else
		{
			getNicknameLabel().setForeground(Color.decode("0xeeeeee"));
			setStatusText("");
			setIcon(PresenceManager.getIconFromPresence(PresenceManager.INVISIBLE));
			setOffline(true);
		}
    }

    public void setStatusText(String status)
	{
        setStatus(status);
		getDescriptionLabel().setText(status);
    }

    public void setAvatarImage(Icon icon)
	{
        avatarLabel.setIcon(icon);
		avatarLabel.validate();
		avatarLabel.repaint();
    }

    public void showUserComingOnline()
	{
        getNicknameLabel().setFont(FontLoader.getDroidFont(Font.BOLD, fontSize));
        getNicknameLabel().setForeground(new Color(255, 128, 0));
    }

    public void showUserGoingOfflineOnline()
	{
        getNicknameLabel().setFont(FontLoader.getDroidFont(Font.BOLD, fontSize));
        getNicknameLabel().setForeground(Color.decode("0xcc0000"));
    }

    public void updateAvatarInSideIcon()
	{
        try
		{
            final URL url = getAvatarURL();
            if (url != null)
			{
				ImageIcon imageIcon = GraphicUtils.scale(new ImageIcon(url), iconSize, iconSize);
				setAvatarImage(imageIcon);
            }
			else
			{
				setAvatarImage(ImageUtilities.loadImageIcon("org/chartsy/chatsy/resources/default-avatar-32.png", true));
			}
        }
        catch (MalformedURLException e)
		{
			setAvatarImage(ImageUtilities.loadImageIcon("org/chartsy/chatsy/resources/default-avatar-32.png", true));
        }
    }

	public Dimension getPreferredSize()
	{
		Dimension dimension = super.getPreferredSize();
		return new Dimension(dimension.width, 42);
	}

	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		Graphics2D graphics2D = (Graphics2D) g;
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

		if (mouseOver && !selected)
		{
			Color color = Color.decode("0x0298db");
			Color bgColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 100);
			graphics2D.setColor(bgColor);
			graphics2D.fill(new Rectangle(0, 0, getWidth(), getHeight()-1));
		}

		if (selected)
		{
			Color color = Color.decode("0x0298db");
			Color bgColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 200);
			graphics2D.setColor(bgColor);
			graphics2D.fill(new Rectangle(0, 0, getWidth(), getHeight()-1));
		}

		GradientPaint gradientPaint;
		gradientPaint = new GradientPaint(0, getHeight()-1, Color.decode("0xefefef"), getWidth()/2, getHeight()-1, Color.decode("0xadaaad"));
		graphics2D.setPaint(gradientPaint);
		graphics2D.drawLine(0, getHeight()-1, getWidth()/2, getHeight()-1);
		gradientPaint = new GradientPaint(getWidth()/2, getHeight()-1, Color.decode("0xadaaad"), getWidth(), getHeight()-1, Color.decode("0xefefef"));
		graphics2D.setPaint(gradientPaint);
		graphics2D.drawLine(getWidth()/2, getHeight()-1, getWidth(), getHeight()-1);
	}

	public void setSelected(boolean flag)
	{
		selected = flag;
		repaint();
	}

	public void setMouseOver(boolean flag)
	{
		mouseOver = flag;
		repaint();
	}

	@Override public void mouseClicked(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1
			|| ChatsyManager.getWorkspace().getContactList().getSelectedUsers().size() == 1)
		{
			Component parent = getParent();
			if (parent != null)
			{
				if (!(parent.getParent() instanceof ContactGroup))
					return;

				ContactGroup contactGroup = (ContactGroup) parent.getParent();
				if (e.getClickCount() == 2)
					contactGroup.contactItemDoubleClicked(this);
				else if (e.getClickCount() == 1)
					contactGroup.contactItemClicked(this);
			}
		}
		if (e.getButton() == MouseEvent.BUTTON3)
		{
			checkPopup(e);
		}
	}

	@Override public void mousePressed(MouseEvent e)
	{
	}

	@Override public void mouseReleased(MouseEvent e)
	{
	}

	@Override public void mouseEntered(MouseEvent e)
	{
		setMouseOver(true);
	}

	@Override public void mouseExited(MouseEvent e)
	{
		setMouseOver(false);
	}

	private void checkPopup(MouseEvent e)
	{
		ContactGroup group = ChatsyManager.getWorkspace().getContactList().getContactGroup(groupName);
		if (group != null)
			group.checkPopup(e);
	}

	private static class ContactItemInfo extends JPanel
	{

		private JLabel displayNameLabel = new JLabel(" ");
		private JLabel descriptionLabel = new JLabel(" ");

		private ContactItemInfo()
		{
			super(new BorderLayout());
			setOpaque(false);
			setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

			displayNameLabel.setFont(FontLoader.getDroidFont(Font.PLAIN, 12));
			displayNameLabel.setForeground(Color.decode("0x111111"));
			displayNameLabel.setHorizontalAlignment(JLabel.LEFT);
			displayNameLabel.setHorizontalTextPosition(JLabel.LEFT);
			displayNameLabel.setVerticalAlignment(JLabel.CENTER);
			displayNameLabel.setVerticalTextPosition(JLabel.CENTER);
			displayNameLabel.setIconTextGap(5);

			descriptionLabel.setFont(FontLoader.getDroidFont(Font.PLAIN, 11));
			descriptionLabel.setForeground(Color.decode("0xbdbebd"));
			descriptionLabel.setHorizontalTextPosition(JLabel.LEFT);
			descriptionLabel.setHorizontalAlignment(JLabel.LEFT);

			add(displayNameLabel, BorderLayout.CENTER);
			add(descriptionLabel, BorderLayout.SOUTH);
		}

		public void setNickname(String nickname)
		{
			displayNameLabel.setText(nickname);
		}

		public String getNickname()
		{
			return displayNameLabel.getText();
		}

		public JLabel getDisplayNameLabel()
		{
			return displayNameLabel;
		}

		public void setStatus(String status)
		{
			descriptionLabel.setText(status);
		}

		public String getStatus()
		{
			return descriptionLabel.getText();
		}

		public JLabel getDescriptionLabel()
		{
			return descriptionLabel;
		}

	}

	private static class AvatarLabel extends JLabel
	{

		private AvatarLabel()
		{
			super();
			setOpaque(false);
			setPreferredSize(new Dimension(32, 32));
			setMinimumSize(new Dimension(32, 32));
			setMaximumSize(new Dimension(32, 32));
			setHorizontalAlignment(JLabel.CENTER);
			setVerticalAlignment(JLabel.CENTER);
		}

	}

}
