package org.chartsy.chatsy.chat.ui.status;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import org.chartsy.chatsy.chat.util.FontLoader;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.packet.VCard;
import org.chartsy.chatsy.chat.PresenceManager;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.ui.PresenceListener;
import org.chartsy.chatsy.chat.util.GraphicUtils;
import org.chartsy.chatsy.chat.util.ModelUtil;
import org.chartsy.chatsy.chat.util.SwingTimerTask;
import org.chartsy.chatsy.chat.util.SwingWorker;
import org.chartsy.chatsy.chat.util.TaskEngine;
import org.chartsy.chatsy.chatimpl.profile.VCardEditor;
import org.chartsy.chatsy.chatimpl.profile.VCardListener;
import org.chartsy.chatsy.chatimpl.profile.VCardManager;
import org.openide.util.ImageUtilities;
import org.openide.util.NbPreferences;

public class StatusBar extends JPanel implements VCardListener
{
	
	private static final long serialVersionUID = -4322806442034868526L;

	private List<StatusItem> statusList = new ArrayList<StatusItem>();

    private AvatarLabel imageLabel;
    private JLabel descriptiveLabel = new JLabel();
    private JLabel nicknameLabel = new JLabel();
	private StatusButton statusButton = new StatusButton();
    private Presence currentPresence;

    public StatusBar()
	{
        setLayout(new GridBagLayout());
		setOpaque(false);

		imageLabel = new AvatarLabel();

        add(imageLabel, new GridBagConstraints(0, 0, 1, 4, 0.0, 0.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
			new Insets(2, 2, 2, 2), 0, 0));
        add(nicknameLabel, new GridBagConstraints(1, 0, 2, 2, 1.0, 0.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
			new Insets(10, 5, 0, 0), 0, 0));
        add(descriptiveLabel, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
			new Insets(0, 5, 0, 0), 0, 0));
		add(statusButton, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
			new Insets(10, 2, 0, 0), 0, 0));

        nicknameLabel.setToolTipText(ChatsyManager.getConnection().getUser());
        nicknameLabel.setFont(FontLoader.getDroidFont(Font.BOLD, 14));

        buildStatusItemList();
		setStatus(PresenceManager._AVAILABLE);
		currentPresence = new Presence(Presence.Type.available, PresenceManager._AVAILABLE, 1, Presence.Mode.available);

        ChatsyManager.getSessionManager().addPresenceListener(new PresenceListener()
		{
            public void presenceChanged(Presence presence)
			{
                changeAvailability(presence);
            }
        });

        imageLabel.addMouseListener(new MouseAdapter()
		{
            public void mouseClicked(MouseEvent mouseEvent)
			{
                if (mouseEvent.getClickCount() == 1)
				{
                    VCardManager vcardManager = ChatsyManager.getVCardManager();
                    VCardEditor editor = new VCardEditor();
                    editor.editProfile(vcardManager.getVCard(), ChatsyManager.getWorkspace());
                }
            }
        });

        final TimerTask task = new SwingTimerTask()
		{
            public void doRun()
			{
                ChatsyManager.getVCardManager().addVCardListener(ChatsyManager.getWorkspace().getStatusBar());
            }
        };
        TaskEngine.getInstance().schedule(task, 3000);
    }

    public void setAvatar(Icon icon)
	{
        imageLabel.setIcon(icon);
        invalidate();
        validateTree();
    }

    public void setNickname(String nickname)
	{
        nicknameLabel.setText(nickname);
    }

    public void setStatus(String status)
	{
		statusButton.setStatus(status);
    }

	public void setStatusIcon(Icon icon)
	{
		statusButton.setIcon(icon);
	}

    public void showPopup(MouseEvent e)
	{
		final StatusPopup popup = new StatusPopup();

        for (final StatusItem statusItem : statusList)
		{
            final Action statusAction = new AbstractAction()
			{
				public void actionPerformed(ActionEvent actionEvent)
				{
                    final String text = statusItem.getText();
                    final StatusItem si = getStatusItem(text);
                    if (si == null)
                        return;

                    SwingWorker worker = new SwingWorker()
					{
                        public Object construct()
						{
                            ChatsyManager.getSessionManager().changePresence(si.getPresence());
                            return "ok";
                        }
                        public void finished()
						{
                            setStatus(text);
                        }
                    };
                    worker.start();
                }
            };
            statusAction.putValue(Action.NAME, statusItem.getText());
            statusAction.putValue(Action.SMALL_ICON, statusItem.getIcon());
			popup.add(new StatusMenuItem(statusAction));
        }
		
		popup.show(statusButton, 0, statusButton.getHeight());
    }

    public void changeAvailability(final Presence presence)
	{
        if ((presence.getMode() == currentPresence.getMode())
			&& (presence.getType() == currentPresence.getType())
			&& (presence.getStatus().equals(currentPresence.getStatus())))
		{
            PacketExtension pe = presence.getExtension("x", "vcard-temp:x:update");
            if (pe != null) 
                loadVCard();
            return;
        }

        final Runnable changePresenceRunnable = new Runnable()
		{
            public void run()
			{
                currentPresence = presence;
                setStatus(presence.getStatus());
                Icon icon = PresenceManager.getIconFromPresence(presence);
                if (icon != null) 
					statusButton.setStatusIcon(icon);
            }
        };
        SwingUtilities.invokeLater(changePresenceRunnable);
    }

    private void buildStatusItemList()
	{
        for (Presence presence : PresenceManager.getPresences())
		{
            Icon icon = PresenceManager.getIconFromPresence(presence);
            StatusItem item = new StatusItem(presence, icon);
            statusList.add(item);
        }

		final Icon availableIcon = PresenceManager.AVAILABLE_ICON;
		statusButton.setStatusIcon(availableIcon);
		validate();
		repaint();
    }

    public Collection<StatusItem> getStatusList()
	{
        return statusList;
    }

    public Presence getPresence()
	{
        return currentPresence;
    }

    public StatusItem getStatusItem(String label)
	{
        for (StatusItem aStatusList : statusList) 
            if (aStatusList.getText().equals(label)) 
                return aStatusList;
        return null;
    }

    public void loadVCard()
	{
        final Runnable loadVCard = new Runnable()
		{
            public void run()
			{
                VCard vcard = ChatsyManager.getVCardManager().getVCard();
                updatVCardInformation(vcard);
            }
        };
        TaskEngine.getInstance().submit(loadVCard);
    }

    private void updatVCardInformation(final VCard vCard)
	{
        SwingUtilities.invokeLater(new Runnable()
		{
            public void run()
			{
                if (vCard.getError() == null)
				{
                    String firstName = vCard.getFirstName();
                    String lastName = vCard.getLastName();
                    String nickname = vCard.getNickName();
					
                    if (ModelUtil.hasLength(firstName) && ModelUtil.hasLength(lastName)) {
                        setNickname(firstName + " " + lastName);
						NbPreferences.root().node("/org/chartsy/chat").put("nickname", firstName + " " + lastName);
					}
                    else if (ModelUtil.hasLength(firstName)) {
                        setNickname(firstName);
						NbPreferences.root().node("/org/chartsy/chat").put("nickname", firstName);
					}
                    else if (ModelUtil.hasLength(nickname)) {
                        setNickname(nickname);
						NbPreferences.root().node("/org/chartsy/chat").put("nickname", nickname);
					}
                    else
					{
                        nickname = ChatsyManager.getSessionManager().getUsername();
                        setNickname(nickname);
						NbPreferences.root().node("/org/chartsy/chat").put("nickname", nickname);
                    }
                }
                else
				{
                    String nickname = ChatsyManager.getSessionManager().getUsername();
                    setNickname(nickname);
					NbPreferences.root().node("/org/chartsy/chat").put("nickname", nickname);
                    return;
                }

                byte[] avatarBytes = null;
                try
				{
                    avatarBytes = vCard.getAvatar();
                }
                catch (Exception e)
				{
                }

                if (avatarBytes != null 
					&& avatarBytes.length > 0)
				{
                    try
					{
                        ImageIcon avatarIcon = new ImageIcon(avatarBytes);
                        avatarIcon = VCardManager.scale(avatarIcon);
                        imageLabel.setIcon(GraphicUtils.scaleImageIcon(avatarIcon, 64, 64));
                        imageLabel.invalidate();
                        imageLabel.validate();
                        imageLabel.repaint();
                    }
                    catch (Exception e)
					{
                    }
                }
                else
				{
					ImageIcon icon = ImageUtilities.loadImageIcon("org/chartsy/chatsy/resources/default-avatar-64.png", true);
                    imageLabel.setIcon(icon);
                    imageLabel.invalidate();
                    imageLabel.validate();
                    imageLabel.repaint();
                }
            }
        });

    }

    public static Presence copyPresence(Presence presence)
	{
        return new Presence(presence.getType(), presence.getStatus(), presence.getPriority(), presence.getMode());
    }

    public JLabel getNicknameLabel()
	{
        return nicknameLabel;
    }

	private class StatusButton extends JButton
	{

		private Image backgroundImage;

		public StatusButton()
		{
			setOpaque(false);
			setBorderPainted(false);
			setRolloverEnabled(false);
			setFocusPainted(false);
			setContentAreaFilled(false);

			setPreferredSize(new Dimension(188, 25));
			setMinimumSize(new Dimension(188, 25));
			setMaximumSize(new Dimension(188, 25));

			setHorizontalAlignment(JButton.LEFT);
			setHorizontalTextPosition(JButton.RIGHT);
			setVerticalAlignment(JButton.CENTER);
			setVerticalTextPosition(JButton.CENTER);
			setIconTextGap(5);
			setMargin(new Insets(0, 10, 0, 5));
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			setFont(FontLoader.getDroidFont(Font.PLAIN, 12));
			setForeground(Color.decode("0x111111"));
			addMouseListener(new MouseAdapter()
			{
				public void mousePressed(MouseEvent e)
				{
					showPopup(e);
				}
			});
			backgroundImage = ImageUtilities.loadImage("org/chartsy/chatsy/resources/status-btn.png", true);
		}

		protected void paintComponent(Graphics g)
		{
			Graphics2D graphics2D = (Graphics2D) g;
			graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			graphics2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

			graphics2D.drawImage(backgroundImage, 0, 0, this);
			super.paintComponent(graphics2D);
		}


		public void setStatus(String status)
		{
			if (status == null)
				return;
			
			int length = status.length();
            String visualStatus = status;
            if (length > 30)
                visualStatus = status.substring(0, 27) + "...";
            setText(visualStatus);
            setToolTipText(status);
		}

		public void setStatusIcon(Icon icon)
		{
			setIcon(icon);
			this.repaint();
		}

	}

    public void setDescriptiveText(String text)
	{
        descriptiveLabel.setText(text);
    }

    public Dimension getPreferredSize()
	{
        Dimension dim = super.getPreferredSize();
        dim.width = 0;
        return dim;
    }

    public void vcardChanged(VCard vcard)
	{
        updatVCardInformation(vcard);
    }

	private static class AvatarLabel extends JLabel
	{

		private boolean mouseOver = false;
		private Image borderImage;

		private AvatarLabel()
		{
			super();
			setOpaque(false);
			setPreferredSize(new Dimension(70, 69));
			setMinimumSize(new Dimension(70, 69));
			setMaximumSize(new Dimension(70, 69));
			setHorizontalAlignment(JLabel.CENTER);
			setVerticalAlignment(JLabel.CENTER);
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			borderImage = ImageUtilities.loadImage("org/chartsy/chatsy/resources/avatar-border.png", true);

			addMouseListener(new MouseAdapter()
			{
				public void mouseEntered(MouseEvent e)
				{
					mouseOver = true;
					repaint();
				}
				public void mouseExited(MouseEvent e)
				{
					mouseOver = false;
					repaint();
				}
			});
		}

		protected void paintComponent(Graphics g)
		{
			Graphics2D graphics2D = (Graphics2D) g;
			graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			graphics2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

			super.paintComponent(graphics2D);
			if (mouseOver)
				graphics2D.drawImage(borderImage, 0, 0, this);
		}

	}

	private static class StatusPopup extends JPopupMenu
	{

		private StatusPopup()
		{
			super();
			setOpaque(true);
			setBorderPainted(false);
			setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			setBackground(Color.WHITE);
		}

		public Dimension getPreferredSize()
		{
			Dimension dimension = super.getPreferredSize();
			return new Dimension(188, dimension.height);
		}

	}

	private static class StatusMenuItem extends JMenuItem
	{

		private StatusMenuItem(Action action)
		{
			super(action);
			setOpaque(false);
			setFont(FontLoader.getDroidFont(Font.PLAIN, 12));
			setForeground(Color.decode("0x111111"));
			setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			setBorderPainted(false);
			setFocusPainted(false);
			setContentAreaFilled(false);
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

	}
	
}
