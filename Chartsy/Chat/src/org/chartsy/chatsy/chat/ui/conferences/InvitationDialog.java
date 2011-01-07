package org.chartsy.chatsy.chat.ui.conferences;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.StringUtils;
import org.chartsy.chatsy.chat.ChatManager;
import org.chartsy.chatsy.chat.ChatNotFoundException;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.component.TitlePanel;
import org.chartsy.chatsy.chat.ui.ChatRoom;
import org.chartsy.chatsy.chat.ui.RosterPickList;
import org.chartsy.chatsy.chat.ui.rooms.GroupChatRoom;
import org.chartsy.chatsy.chat.util.ModelUtil;
import org.chartsy.chatsy.chat.util.SwingWorker;
import org.chartsy.chatsy.chat.util.log.Log;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

final class InvitationDialog extends JPanel
{
    
    private JLabel roomsLabel = new JLabel("Room");
    private JTextField roomsField = new JTextField();

    private JLabel messageLabel = new JLabel("Message");
    private JTextField messageField = new JTextField("Please join me in a conference");

    private JLabel inviteLabel = new JLabel("Invited users");


    private DefaultListModel invitedUsers = new DefaultListModel();
    private JList invitedUserList = new JList(invitedUsers);

    private JDialog dlg;

    private GridBagLayout gridBagLayout1 = new GridBagLayout();

    public InvitationDialog() {
        setLayout(gridBagLayout1);

        add(roomsLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
        add(roomsField, new GridBagConstraints(1, 0, 3, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 0, 0));
        add(messageLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
        add(messageField, new GridBagConstraints(1, 1, 3, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 0, 0));

        JLabel jidLabel = new JLabel("Add JID");
        final JTextField jidField = new JTextField();
        JButton addJIDButton = new JButton("Add");
        JButton browseButton = new JButton("Browse");

        add(jidLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 0, 0));
        add(jidField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 0, 0));
        add(addJIDButton, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
        add(browseButton, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));

        addJIDButton.addActionListener(new ActionListener()
		{
            public void actionPerformed(ActionEvent actionEvent)
			{
                String jid = jidField.getText();
                String server = StringUtils.parseBareAddress(jid);
                if (server == null || server.indexOf("@") == -1)
				{
                    JOptionPane.showMessageDialog(
						dlg,
						"Enter a valid Jabber ID",
						"Error",
						JOptionPane.ERROR_MESSAGE);
                    jidField.setText("");
                    jidField.requestFocus();
                }
                else
				{
                    if (!invitedUsers.contains(jid))
                        invitedUsers.addElement(jid);
                    jidField.setText("");
                }
            }
        });

        browseButton.addActionListener(new ActionListener()
		{
            public void actionPerformed(ActionEvent actionEvent)
			{
                RosterPickList browser = new RosterPickList();
                Collection<String> col = browser.showRoster(dlg);
                for (String aCol : col)
				{
                    String jid = aCol;
                    if (!invitedUsers.contains(jid))
                        invitedUsers.addElement(jid);
                }
            }
        });

        add(inviteLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 0, 0));
        add(new JScrollPane(invitedUserList), new GridBagConstraints(1, 3, 3, 1, 1.0, 1.0, 
			GridBagConstraints.WEST, GridBagConstraints.BOTH,
			new Insets(5, 5, 5, 5), 0, 0));

        invitedUserList.addMouseListener(new MouseAdapter()
		{
            public void mouseReleased(MouseEvent mouseEvent)
			{
                if (mouseEvent.isPopupTrigger())
                    showPopup(mouseEvent);
            }
            public void mousePressed(MouseEvent mouseEvent)
			{
                if (mouseEvent.isPopupTrigger())
                    showPopup(mouseEvent);
            }
        });
    }

    private void showPopup(MouseEvent e)
	{
        final JPopupMenu popup = new JPopupMenu();
        final int index = invitedUserList.locationToIndex(e.getPoint());

        Action removeAction = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
                invitedUsers.remove(index);
            }
        };
        removeAction.putValue(Action.NAME, "Remove");
        popup.add(removeAction);
        popup.show(invitedUserList, e.getX(), e.getY());
    }

    public void inviteUsersToRoom(final String serviceName, String roomName, Collection<String> jids)
	{
        roomsField.setText(roomName);
        JFrame parent = ChatsyManager.getChatManager().getChatContainer().getChatFrame();
        if (parent == null || !parent.isVisible())
            parent = new JFrame();

        if (jids != null)
            for (Object jid : jids) 
                invitedUsers.addElement(jid);

        final JOptionPane pane;
        TitlePanel titlePanel;
        titlePanel = new TitlePanel(
			"Invite To conference",
			"Invite users to a conference room", null, true);

        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        Object[] options = {"Invite", "Cancel"};
        pane = new JOptionPane(
			this,
			JOptionPane.PLAIN_MESSAGE,
			JOptionPane.OK_CANCEL_OPTION,
			ChatsyManager.getApplicationImage(),
			options,
			options[0]);
        mainPanel.add(pane, BorderLayout.CENTER);

        final JOptionPane p = new JOptionPane();
        dlg = p.createDialog(parent, "Conference rooms");
        dlg.setModal(false);
        dlg.pack();
        dlg.setSize(500, 450);
        dlg.setResizable(true);
        dlg.setContentPane(mainPanel);
        dlg.setLocationRelativeTo(parent);

        PropertyChangeListener changeListener = new PropertyChangeListener()
		{
            public void propertyChange(PropertyChangeEvent e)
			{
                String value = (String)pane.getValue();
                if ("Cancel".equals(value))
				{
                    pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                    dlg.dispose();
                }
                else if ("Invite".equals(value))
				{
                    final String roomTitle = roomsField.getText();
                    int size = invitedUserList.getModel().getSize();
                    if (size == 0)
					{
                        JOptionPane.showMessageDialog(
							dlg,
							"Specify users to join this conference room",
							"Error",
							JOptionPane.ERROR_MESSAGE);
                        pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                        return;
                    }

                    if (!ModelUtil.hasLength(roomTitle))
					{
                        JOptionPane.showMessageDialog(
							dlg,
							"No room to join",
							"Error",
							JOptionPane.ERROR_MESSAGE);
                        pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                        return;
                    }
					
                    String roomName = "";
                    ChatManager chatManager = ChatsyManager.getChatManager();
                    for (ChatRoom chatRoom : chatManager.getChatContainer().getChatRooms())
					{
                        if (chatRoom instanceof GroupChatRoom)
						{
                            GroupChatRoom groupRoom = (GroupChatRoom) chatRoom;
                            if (groupRoom.getRoomname().equals(roomTitle))
							{
                                roomName = groupRoom.getMultiUserChat().getRoom();
                                break;
                            }
                        }
                    }
                    String message = messageField.getText();
                    final String messageText = message != null ? message : "Please join me in a conference";
                    if (invitedUsers.getSize() > 0) 
                        invitedUserList.setSelectionInterval(0, invitedUsers.getSize() - 1);

                    GroupChatRoom chatRoom;
                    try
					{
                        chatRoom = ChatsyManager.getChatManager().getGroupChat(roomName);
                    }
                    catch (ChatNotFoundException ex)
					{
                        dlg.setVisible(false);
                        final List<String> jidList = new ArrayList<String>();
                        Object[] jids = invitedUserList.getSelectedValues();
                        final int no = jids != null ? jids.length : 0;
                        for (int i = 0; i < no; i++)
						{
                            try
							{
                                jidList.add((String)jids[i]);
                            }
                            catch (NullPointerException ee)
							{
                                Log.error(ee);
                            }
                        }

                        SwingWorker worker = new SwingWorker()
						{
                            public Object construct()
							{
                                try
								{
                                    Thread.sleep(15);
                                }
                                catch (InterruptedException e2)
								{
                                    Log.error(e2);
                                }
                                return "ok";
                            }

                            public void finished()
							{
                                try
								{
                                    ConferenceUtils.createPrivateConference(serviceName, messageText, roomTitle, jidList);
                                }
                                catch (XMPPException e2)
								{
                                    JOptionPane.showMessageDialog(
										pane,
										ConferenceUtils.getReason(e2),
										"Error",
										JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        };

                        worker.start();
                        pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                        return;
                    }

                    pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                    dlg.dispose();

                    Object[] values = invitedUserList.getSelectedValues();
                    final int no = values != null ? values.length : 0;
                    for (int i = 0; i < no; i++)
					{
                        String jid = (String)values[i];
                        chatRoom.getMultiUserChat().invite(jid, message != null ? message : "msg");
                        String nickname = ChatsyManager.getUserManager().getUserNicknameFromJID(jid);
                        chatRoom.getTranscriptWindow().insertNotificationMessage("Invited " + nickname, ChatManager.NOTIFICATION_COLOR);
                    }

                }
            }
        };

        pane.addPropertyChangeListener(changeListener);
        dlg.setVisible(true);
        dlg.toFront();
        dlg.requestFocus();
    }
	
}
