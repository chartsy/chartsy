package org.chartsy.chatsy.chat.ui.conferences;

import org.jdesktop.swingx.JXList;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.muc.Affiliate;
import org.jivesoftware.smackx.muc.InvitationRejectionListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.Occupant;
import org.jivesoftware.smackx.muc.UserStatusListener;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.chartsy.chatsy.chat.ChatManager;
import org.chartsy.chatsy.chat.PresenceManager;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.UserManager;
import org.chartsy.chatsy.chat.component.ImageTitlePanel;
import org.chartsy.chatsy.chat.ui.ChatRoom;
import org.chartsy.chatsy.chat.ui.ChatRoomListener;
import org.chartsy.chatsy.chat.ui.rooms.ChatRoomImpl;
import org.chartsy.chatsy.chat.ui.rooms.GroupChatRoom;
import org.chartsy.chatsy.chat.util.ModelUtil;
import org.chartsy.chatsy.chat.util.log.Log;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import org.openide.util.ImageUtilities;

public final class GroupChatParticipantList extends JPanel 
	implements ChatRoomListener
{

	private GroupChatRoom groupChatRoom;
	private final ImageTitlePanel agentInfoPanel;
	private ChatManager chatManager;
	private MultiUserChat chat;

	private final Map<String, String> userMap = new HashMap<String, String>();
	private UserManager userManager = ChatsyManager.getUserManager();
	private DefaultListModel model = new DefaultListModel();
	private JXList participantsList;
	private PacketListener listener = null;
	private Map<String, String> invitees = new HashMap<String, String>();
	private boolean allowNicknameChange = true;
	private DiscoverInfo roomInformation;
	private List<JLabel> users = new ArrayList<JLabel>();

	public GroupChatParticipantList()
	{
		setLayout(new GridBagLayout());
		chatManager = ChatsyManager.getChatManager();

		agentInfoPanel = new ImageTitlePanel("msg");
		participantsList = new JXList(model);
		participantsList.setCellRenderer(new ParticipantRenderer());

		this.setOpaque(false);
		this.setBackground(Color.white);

		participantsList.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				if (evt.getClickCount() == 2)
				{
					String selectedUser = getSelectedUser();
					startChat(groupChatRoom, userMap.get(selectedUser));
				}
			}
			public void mouseReleased(final MouseEvent evt)
			{
				if (evt.isPopupTrigger())
					checkPopup(evt);
			}
			public void mousePressed(final MouseEvent evt)
			{
				if (evt.isPopupTrigger())
					checkPopup(evt);
			}
		});

		JScrollPane scroller = new JScrollPane(participantsList);
		scroller.getVerticalScrollBar().setBlockIncrement(50);
		scroller.getVerticalScrollBar().setUnitIncrement(20);
		scroller.setBackground(Color.white);
		scroller.getViewport().setBackground(Color.white);
		add(scroller, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, 
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0), 0, 0));
	}

	public void setChatRoom(final ChatRoom chatRoom)
	{
		this.groupChatRoom = (GroupChatRoom) chatRoom;
		chatManager.addChatRoomListener(this);
		chat = groupChatRoom.getMultiUserChat();
		chat.addInvitationRejectionListener(new InvitationRejectionListener()
		{
			public void invitationDeclined(String jid, String message)
			{
				String nickname = userManager.getUserNicknameFromJID(jid);
				userHasLeft(chatRoom, nickname);
				chatRoom.getTranscriptWindow().insertNotificationMessage(
					nickname + " has rejected the invitation.",ChatManager.NOTIFICATION_COLOR);
			}
		});

		listener = new PacketListener()
		{
			public void processPacket(final Packet packet)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						Presence p = (Presence) packet;
						if (p.getError() != null)
						{
							if (p.getError().getCondition().equals(
								XMPPError.Condition.conflict.toString()))
								return;
						}
						final String userid = p.getFrom();

						String displayName = StringUtils.parseResource(userid);
						userMap.put(displayName, userid);

						if (p.getType() == Presence.Type.available)
						{
							addParticipant(userid, p);
							agentInfoPanel.setVisible(true);
						} 
						else
						{
							removeUser(displayName);
						}
					}
				});
			}
		};

		chat.addParticipantListener(listener);
		ServiceDiscoveryManager disco = ServiceDiscoveryManager.getInstanceFor(ChatsyManager.getConnection());
		try
		{
			roomInformation = disco.discoverInfo(chat.getRoom());
		} 
		catch (XMPPException e)
		{
			Log.debug("Unable to retrieve room informatino for " + chat.getRoom());
		}
	}

	public void chatRoomOpened(ChatRoom room)
	{
		if (room != groupChatRoom)
			return;

		chat.addUserStatusListener(new UserStatusListener()
		{
			public void kicked(String actor, String reason)
			{
			}
			public void voiceGranted()
			{
			}
			public void voiceRevoked()
			{
			}
			public void banned(String actor, String reason)
			{
			}
			public void membershipGranted()
			{
			}
			public void membershipRevoked()
			{
			}
			public void moderatorGranted()
			{
			}
			public void moderatorRevoked()
			{
			}
			public void ownershipGranted()
			{
			}
			public void ownershipRevoked()
			{
			}
			public void adminGranted()
			{
			}
			public void adminRevoked()
			{
			}
		});
	}

	public void chatRoomLeft(ChatRoom room)
	{
		if (this.groupChatRoom == room)
		{
			chatManager.removeChatRoomListener(this);
			agentInfoPanel.setVisible(false);
		}
	}

	public void chatRoomClosed(ChatRoom room)
	{
		if (this.groupChatRoom == room)
		{
			chatManager.removeChatRoomListener(this);
			chat.removeParticipantListener(listener);
		}
	}

	public void chatRoomActivated(ChatRoom room)
	{
	}

	public void userHasJoined(ChatRoom room, String userid)
	{
	}

	public void addInvitee(String jid, String message)
	{
		final UserManager userManager = ChatsyManager.getUserManager();
		String displayName = userManager.getUserNicknameFromJID(jid);
		groupChatRoom.getTranscriptWindow().insertNotificationMessage(
			displayName + " has been invited to join this room.", ChatManager.NOTIFICATION_COLOR);

		if (roomInformation != null
			&& !roomInformation.containsFeature("muc_nonanonymous"))
			return;

		final ImageIcon inviteIcon = null;
		addUser(inviteIcon, displayName);
		invitees.put(displayName, message);
	}

	private ImageIcon getImageIcon(String participantJID)
	{
		String displayName = StringUtils.parseResource(participantJID);
		ImageIcon icon = ImageUtilities.loadImageIcon("org/chartsy/chatsy/resources/available.png", true);
		icon.setDescription(displayName);
		return icon;
	}

	private void addParticipant(String participantJID, Presence presence)
	{
		for (String displayName : invitees.keySet())
		{
			String jid = ChatsyManager.getUserManager()
				.getJIDFromDisplayName(displayName);
			Occupant occ = chat.getOccupant(participantJID);
			if (occ != null)
			{
				String actualJID = occ.getJid();
				if (actualJID.equals(jid))
					removeUser(displayName);
			}
		}

		String nickname = StringUtils.parseResource(participantJID);
		if (!exists(nickname))
		{
			Icon icon;
			icon = PresenceManager.getIconFromPresence(presence);
			if (icon == null)
				icon = ImageUtilities.loadImageIcon("org/chartsy/chatsy/resources/available.png", true);
			addUser(icon, nickname);
		} 
		else
		{
			Icon icon = PresenceManager.getIconFromPresence(presence);
			if (icon == null)
				icon = ImageUtilities.loadImageIcon("org/chartsy/chatsy/resources/available.png", true);
			int index = getIndex(nickname);
			if (index != -1)
			{
				final JLabel userLabel = new JLabel(nickname, icon, JLabel.HORIZONTAL);
				model.setElementAt(userLabel, index);
			}
		}
	}

	public void userHasLeft(ChatRoom room, String userid)
	{
		if (room != groupChatRoom)
			return;
		int index = getIndex(userid);
		if (index != -1)
		{
			removeUser(userid);
			userMap.remove(userid);
		}
	}

	private boolean exists(String nickname)
	{
		for (int i = 0; i < model.getSize(); i++)
		{
			final JLabel userLabel = (JLabel) model.getElementAt(i);
			if (userLabel.getText().equals(nickname))
				return true;
		}
		return false;
	}

	private String getSelectedUser()
	{
		JLabel label = (JLabel) participantsList.getSelectedValue();
		if (label != null)
			return label.getText();
		return null;
	}

	private void startChat(ChatRoom groupChat, String groupJID)
	{
		String userNickname = StringUtils.parseResource(groupJID);
		String roomTitle = userNickname + " - "
				+ StringUtils.parseName(groupChat.getRoomname());
		String nicknameOfUser = StringUtils.parseResource(groupJID);
		String nickname = groupChat.getNickname();

		if (nicknameOfUser.equals(nickname))
			return;

		ChatRoom chatRoom = chatManager.getChatContainer().getChatRoom(groupJID);
		if (chatRoom == null)
		{
			chatRoom = new ChatRoomImpl(groupJID, nicknameOfUser, roomTitle);
			chatManager.getChatContainer().addChatRoom(chatRoom);
		}
		chatManager.getChatContainer().activateChatRoom(chatRoom);
	}

	public void tabSelected()
	{
	}

	public String getTabTitle()
	{
		return "Room information";
	}

	public Icon getTabIcon()
	{
		return null;
	}

	public String getTabToolTip()
	{
		return "Room information";
	}

	public JComponent getGUI()
	{
		return this;
	}

	private void kickUser(String nickname)
	{
		try
		{
			chat.kickParticipant(nickname, "kicked");
		} 
		catch (XMPPException e)
		{
			groupChatRoom.insertText(nickname + " kicked");
		}
	}

	private void banUser(String displayName)
	{
		try
		{
			Occupant occupant = chat.getOccupant(userMap.get(displayName));
			if (occupant != null)
			{
				String bareJID = StringUtils
						.parseBareAddress(occupant.getJid());
				chat.banUser(bareJID, "banned");
			}
		} 
		catch (XMPPException e)
		{
			Log.error(e);
		}
	}

	private void unbanUser(String jid)
	{
		try
		{
			chat.grantMembership(jid);
		} 
		catch (XMPPException e)
		{
			Log.error(e);
		}
	}

	private void grantVoice(String nickname)
	{
		try
		{
			chat.grantVoice(nickname);
		} 
		catch (XMPPException e)
		{
			Log.error(e);
		}
	}

	private void revokeVoice(String nickname)
	{
		try
		{
			chat.revokeVoice(nickname);
		} 
		catch (XMPPException e)
		{
			Log.error(e);
		}
	}

	private void grantModerator(String nickname)
	{
		try
		{
			chat.grantModerator(nickname);
		} 
		catch (XMPPException e)
		{
			Log.error(e);
		}
	}

	private void revokeModerator(String nickname)
	{
		try
		{
			chat.revokeModerator(nickname);
		} 
		catch (XMPPException e)
		{
			Log.error(e);
		}
	}

	public Dimension getPreferredSize()
	{
		final Dimension size = super.getPreferredSize();
		size.width = 150;
		return size;
	}

	private void checkPopup(MouseEvent evt)
	{
		Point p = evt.getPoint();
		final int index = participantsList.locationToIndex(p);

		final JPopupMenu popup = new JPopupMenu();

		if (index != -1)
		{
			participantsList.setSelectedIndex(index);
			final JLabel userLabel = (JLabel) model.getElementAt(index);
			final String selectedUser = userLabel.getText();
			final String groupJID = userMap.get(selectedUser);
			String groupJIDNickname = StringUtils.parseResource(groupJID);

			final String nickname = groupChatRoom.getNickname();
			final Occupant occupant = userManager.getOccupant(groupChatRoom,
					selectedUser);
			final boolean admin = ChatsyManager.getUserManager().isOwnerOrAdmin(
					groupChatRoom, chat.getNickname());
			final boolean moderator = ChatsyManager.getUserManager()
					.isModerator(groupChatRoom, chat.getNickname());

			final boolean userIsAdmin = userManager.isOwnerOrAdmin(occupant);
			final boolean userIsModerator = userManager.isModerator(occupant);
			boolean isMe = nickname.equals(groupJIDNickname);

			if (groupJIDNickname == null)
			{
				Action inviteAgainAction = new AbstractAction()
				{
					public void actionPerformed(ActionEvent actionEvent)
					{
						String message = invitees.get(selectedUser);
						String jid = userManager
								.getJIDFromDisplayName(selectedUser);
						chat.invite(jid, message);
					}
				};
				inviteAgainAction.putValue(Action.NAME, "Invite again");
				popup.add(inviteAgainAction);

				Action removeInvite = new AbstractAction()
				{
					public void actionPerformed(ActionEvent actionEvent)
					{
						int index = getIndex(selectedUser);
						if (index != -1)
							model.removeElementAt(index);
					}
				};
				removeInvite.putValue(Action.NAME, "Remove");
				popup.add(removeInvite);
				
				popup.show(participantsList, evt.getX(), evt.getY());
				return;
			}

			if (isMe)
			{
				Action changeNicknameAction = new AbstractAction() 
				{
					public void actionPerformed(ActionEvent actionEvent)
					{
						String newNickname = JOptionPane.showInputDialog(
							groupChatRoom,
							"New nickname:",
							"Change nickname",
							JOptionPane.QUESTION_MESSAGE);
						if (ModelUtil.hasLength(newNickname))
						{
							while (true)
							{
								newNickname = newNickname.trim();
								String nick = chat.getNickname();
								if (newNickname.equals(nick))
									return;
								try
								{
									chat.changeNickname(newNickname);
									break;
								} 
								catch (XMPPException e1)
								{
									newNickname = JOptionPane.showInputDialog(
										groupChatRoom,
										"Nickname in use, please specify another Nickname:",
										"Change nickname",
										JOptionPane.QUESTION_MESSAGE);
									if (!ModelUtil.hasLength(newNickname))
										break;
								}
							}
						}
					}
				};

				changeNicknameAction.putValue(Action.NAME, "Change nickname");
				if (allowNicknameChange)
					popup.add(changeNicknameAction);
			}

			Action chatAction = new AbstractAction()
			{
				public void actionPerformed(ActionEvent actionEvent)
				{
					String selectedUser = getSelectedUser();
					startChat(groupChatRoom, userMap.get(selectedUser));
				}
			};
			chatAction.putValue(Action.NAME, "Start a chat");
			if (!isMe)
				popup.add(chatAction);

			Action blockAction = new AbstractAction()
			{
				public void actionPerformed(ActionEvent e)
				{
					String user = getSelectedUser();
					ImageIcon icon;
					if (groupChatRoom.isBlocked(groupJID))
					{
						groupChatRoom.removeBlockedUser(groupJID);
						icon = getImageIcon(groupJID);
					} 
					else
					{
						groupChatRoom.addBlockedUser(groupJID);
						icon = null;
					}
					JLabel label = new JLabel(user, icon, JLabel.HORIZONTAL);
					model.setElementAt(label, index);
				}
			};
			blockAction.putValue(Action.NAME, "Block user");
			if (!isMe)
			{
				if (groupChatRoom.isBlocked(groupJID))
					blockAction.putValue(Action.NAME, "Unblock user");
				popup.add(blockAction);
			}

			Action kickAction = new AbstractAction()
			{
				public void actionPerformed(ActionEvent actionEvent)
				{
					kickUser(selectedUser);
				}
			};
			kickAction.putValue(Action.NAME, "Kick user");
			if (moderator && !userIsAdmin && !isMe)
				popup.add(kickAction);

			Action voiceAction = new AbstractAction()
			{
				public void actionPerformed(ActionEvent actionEvent)
				{
					if (userManager.hasVoice(groupChatRoom, selectedUser))
					{
						revokeVoice(selectedUser);
					} 
					else
					{
						grantVoice(selectedUser);
					}
				}
			};
			voiceAction.putValue(Action.NAME, "Voice");
			if (moderator && !userIsModerator && !isMe)
			{
				if (userManager.hasVoice(groupChatRoom, selectedUser))
				{
					voiceAction.putValue(Action.NAME, "Revoke voice");
				} 
				else
				{
					voiceAction.putValue(Action.NAME, "Grant voice");
				}
				popup.add(voiceAction);
			}

			Action banAction = new AbstractAction()
			{
				public void actionPerformed(ActionEvent actionEvent)
				{
					banUser(selectedUser);
				}
			};
			banAction.putValue(Action.NAME, "Ban user");
			if (admin && !userIsModerator && !isMe)
				popup.add(banAction);

			Action moderatorAction = new AbstractAction()
			{
				public void actionPerformed(ActionEvent actionEvent)
				{
					if (!userIsModerator)
					{
						grantModerator(selectedUser);
					} 
					else
					{
						revokeModerator(selectedUser);
					}
				}
			};
			if (admin && !userIsModerator)
			{
				moderatorAction.putValue(Action.NAME, "Grant moderator");
				popup.add(moderatorAction);
			} 
			else if (admin && userIsModerator && !isMe)
			{
				moderatorAction.putValue(Action.NAME, "Revoke moderator");
				popup.add(moderatorAction);
			}

			Action unbanAction = new AbstractAction()
			{
				public void actionPerformed(ActionEvent actionEvent)
				{
					String jid = ((JMenuItem) actionEvent.getSource()).getText();
					unbanUser(jid);
				}
			};
			if (admin)
			{
				JMenu unbanMenu = new JMenu("Unban");
				Iterator<Affiliate> bannedUsers = null;
				try
				{
					bannedUsers = chat.getOutcasts().iterator();
				} 
				catch (XMPPException e)
				{
					Log.error("Error loading all banned users", e);
				}
				while (bannedUsers != null && bannedUsers.hasNext())
				{
					Affiliate bannedUser = (Affiliate) bannedUsers.next();
					JMenuItem bannedItem = new JMenuItem(bannedUser.getJid());
					unbanMenu.add(bannedItem);
					bannedItem.addActionListener(unbanAction);
				}
				if (unbanMenu.getMenuComponentCount() > 0)
					popup.add(unbanMenu);
			}
		}

		Action inviteAction = new AbstractAction()
		{
			public void actionPerformed(ActionEvent actionEvent)
			{
				ConferenceUtils.inviteUsersToRoom(groupChatRoom.getConferenceService(), groupChatRoom.getRoomname(), null);
			}
		};
		inviteAction.putValue(Action.NAME, "Invite users");
		if (index != -1) 
			popup.addSeparator();
		popup.add(inviteAction);

		popup.show(participantsList, evt.getX(), evt.getY());
	}

	public void setNicknameChangeAllowed(boolean allowed)
	{
		allowNicknameChange = allowed;
	}

	public int getIndex(String name)
	{
		for (int i = 0; i < model.getSize(); i++)
		{
			JLabel label = (JLabel) model.getElementAt(i);
			if (label.getText().equals(name))
				return i;
		}
		return -1;
	}

	public synchronized void removeUser(String displayName)
	{
		try
		{
			for (int i = 0; i < users.size(); i++)
			{
				JLabel label = users.get(i);
				if (label.getText().equals(displayName))
				{
					users.remove(label);
					model.removeElement(label);
				}
			}
			for (int i = 0; i < model.size(); i++)
			{
				JLabel label = (JLabel) model.getElementAt(i);
				if (label.getText().equals(displayName))
				{
					users.remove(label);
					model.removeElement(label);
				}
			}
		} 
		catch (Exception e)
		{
			Log.error(e);
		}
	}

	public synchronized void addUser(Icon userIcon, String nickname)
	{
		try
		{
			final JLabel user = new JLabel(nickname, JLabel.HORIZONTAL);
			if (userIcon != null)
				user.setIcon(userIcon);
			users.add(user);
			Collections.sort(users, labelComp);
			final int index = users.indexOf(user);
			model.insertElementAt(user, index);
		} 
		catch (Exception e)
		{
			Log.error(e);
		}
	}

	final Comparator<JLabel> labelComp = new Comparator<JLabel>()
	{
		public int compare(JLabel item1, JLabel item2)
		{
			return item1.getText().toLowerCase().compareTo(item2.getText().toLowerCase());
		}
	};

	public class ParticipantRenderer extends JLabel implements ListCellRenderer
	{

		public ParticipantRenderer()
		{
			setOpaque(true);
		}

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			if (isSelected)
			{
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} 
			else
			{
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			JLabel label = (JLabel) value;
			setText(label.getText());
			setIcon(label.getIcon());
			return this;
		}

	}
	
}

