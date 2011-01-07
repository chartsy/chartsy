package org.chartsy.chatsy.chat.ui.conferences;

import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.component.TitlePanel;
import org.chartsy.chatsy.chat.util.SwingWorker;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import org.openide.util.NbPreferences;

final class JoinConferenceRoomDialog extends JPanel
{
    
    private JLabel roomNameLabel = new JLabel("Room Name");
    private JLabel nicknameLabel = new JLabel("Nickname");
    private JLabel passwordLabel = new JLabel("Password");
    private JPasswordField passwordField = new JPasswordField();
    private JTextField nicknameField = new JTextField();
    private GridBagLayout gridBagLayout1 = new GridBagLayout();
    private JLabel roomNameDescription = new JLabel();

    public JoinConferenceRoomDialog()
	{
        setLayout(gridBagLayout1);
        add(nicknameField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 0, 0));
        add(passwordField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 0, 0));
        add(passwordLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
        add(nicknameLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
        add(roomNameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
        add(roomNameDescription, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 0, 0));
        add(new JLabel(), new GridBagConstraints(0, 3, 2, 1, 0.0, 1.0, 
			GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
			new Insets(5, 5, 5, 5), 0, 0));
    }

    public void joinRoom(final String roomJID, final String roomName)
	{
        nicknameField.setText(NbPreferences.root().node("/org/chartsy/chat").get("nickname", ""));
        passwordField.setVisible(false);
        passwordLabel.setVisible(false);
        roomNameDescription.setText(roomName);

        final JOptionPane pane;
        TitlePanel titlePanel;
        titlePanel = new TitlePanel(
			"Join conference room",
			"Specify information for conference room", null, true);

        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        Object[] options = {"Join", "Cancel"};
        pane = new JOptionPane(
			this,
			JOptionPane.PLAIN_MESSAGE,
			JOptionPane.OK_CANCEL_OPTION,
			ChatsyManager.getApplicationImage(),
			options,
			options[0]);
		
        mainPanel.add(pane, BorderLayout.CENTER);

        final JOptionPane p = new JOptionPane();
        final JDialog dlg = p.createDialog(new JFrame(), "Conference rooms");
        dlg.setModal(false);

        dlg.pack();
        dlg.setSize(350, 250);
        dlg.setResizable(true);
        dlg.setContentPane(mainPanel);
        dlg.setLocationRelativeTo(ChatsyManager.getMainWindow());

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
                else if ("Join".equals(value))
				{
                    pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                    dlg.dispose();
                    ConferenceUtils.joinConferenceOnSeperateThread(roomName, roomJID, null);
                }
            }
        };

        pane.addPropertyChangeListener(changeListener);
        dlg.setVisible(true);
        dlg.toFront();
        dlg.requestFocus();

        SwingWorker worker = new SwingWorker()
		{
            boolean requiresPassword;

            public Object construct()
			{
                requiresPassword = ConferenceUtils.isPasswordRequired(roomJID);
                return requiresPassword;
            }

            public void finished()
			{
                passwordField.setVisible(requiresPassword);
                passwordLabel.setVisible(requiresPassword);
            }
        };
        worker.start();
    }
	
}
