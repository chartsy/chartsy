package org.chartsy.chatsy.chat.ui;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;
import org.chartsy.chatsy.chat.PresenceManager;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.component.TitlePanel;
import org.chartsy.chatsy.chat.component.renderer.JPanelRenderer;
import org.chartsy.chatsy.chat.util.log.Log;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;

public class RosterPickList extends JPanel
{
	
	private DefaultListModel model = new DefaultListModel();
    private JList rosterList = new JList(model);

    public RosterPickList()
	{
        setLayout(new GridBagLayout());
        rosterList.setCellRenderer(new JPanelRenderer());
        JLabel rosterLabel = new JLabel("Available Users");
        this.add(rosterLabel,
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
			GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 0, 0));
        this.add(new JScrollPane(rosterList),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
			GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
			new Insets(5, 5, 5, 5), 0, 0));
    }

    public Collection<String> showRoster(JDialog parent)
	{
        final List<ContactItem> userList = new ArrayList<ContactItem>();
        final Roster roster = ChatsyManager.getConnection().getRoster();
        for (RosterEntry entry : roster.getEntries())
		{
            Presence presence = PresenceManager.getPresence(entry.getUser());
            if (presence.isAvailable())
			{
                ContactItem item = new ContactItem(entry.getName(), null, entry.getUser());
                item.setPresence(presence);
                userList.add(item);
            }
        }
        Collections.sort(userList, itemComparator);
        for (ContactItem item : userList)
            model.addElement(item);

        final JOptionPane pane;
        TitlePanel titlePanel;
        titlePanel = new TitlePanel("Roster", "Select one or more users in your roster", null, true);

        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        Object[] options = {"Ok", "Cancel"};
        pane = new JOptionPane(
			this,
			JOptionPane.PLAIN_MESSAGE,
			JOptionPane.OK_CANCEL_OPTION,
			ChatsyManager.getApplicationImage(),
			options,
			options[0]);
        mainPanel.add(pane, BorderLayout.CENTER);
        final JOptionPane p = new JOptionPane();
        final JDialog dlg = p.createDialog(parent, "Roster");
        dlg.setModal(true);

        dlg.pack();
        dlg.setSize(350, 450);
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
                    rosterList.clearSelection();
                    pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                    dlg.dispose();
                }
                else if ("Ok".equals(value))
				{
                    pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                    dlg.dispose();
                }
            }
        };
        pane.addPropertyChangeListener(changeListener);

        dlg.setVisible(true);
        dlg.toFront();
        dlg.requestFocus();

        List<String> selectedContacts = new ArrayList<String>();
        Object[] values = rosterList.getSelectedValues();
        final int no = values != null ? values.length : 0;
        for (int i = 0; i < no; i++)
		{
            try
			{
                ContactItem item = (ContactItem)values[i];
                selectedContacts.add(item.getJID());
            }
            catch (NullPointerException e)
			{
                Log.error(e);
            }
        }
        return selectedContacts;
    }

    final Comparator<ContactItem> itemComparator = new Comparator<ContactItem>()
	{
        public int compare(ContactItem item1, ContactItem item2)
		{
            String nickname1 = item1.getDisplayName();
            String nickname2 = item2.getDisplayName();
            if (nickname1 == null || nickname2 == null)
                return 0;
            return nickname1.toLowerCase().compareTo(nickname2.toLowerCase());
        }
    };

}
