package org.chartsy.chatsy.chatimpl.search.users;

import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.ReportedData;
import org.chartsy.chatsy.chat.ChatManager;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.component.Table;
import org.chartsy.chatsy.chat.ui.ChatContainer;
import org.chartsy.chatsy.chat.ui.ChatRoom;
import org.chartsy.chatsy.chat.ui.RosterDialog;
import org.chartsy.chatsy.chat.util.ModelUtil;
import org.chartsy.chatsy.chat.util.log.Log;
import org.chartsy.chatsy.chatimpl.profile.VCardManager;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UserSearchResults extends JPanel
{

    private UsersInfoTable resultsTable;

    public UserSearchResults()
	{
        setLayout(new BorderLayout());
    }

    public void showUsersFound(ReportedData data)
	{
        List<String> columnList = new ArrayList<String>();
        Iterator columns = data.getColumns();
        while (columns.hasNext())
		{
            ReportedData.Column column = (ReportedData.Column)columns.next();
            String label = column.getLabel();
            columnList.add(label);
        }

        if (resultsTable == null)
		{
            resultsTable = new UsersInfoTable(columnList.toArray(new String[columnList.size()]));
            final JScrollPane scrollPane = new JScrollPane(resultsTable);
            scrollPane.getViewport().setBackground(Color.white);
            add(scrollPane, BorderLayout.CENTER);

            resultsTable.addMouseListener(new MouseAdapter()
			{
                public void mouseClicked(MouseEvent e)
				{
                    if (e.getClickCount() == 2)
					{
                        int row = resultsTable.getSelectedRow();
                        openChatRoom(row);
                    }
                }

                public void mouseReleased(MouseEvent e)
				{
                    checkPopup(e);
                }

                public void mousePressed(MouseEvent e)
				{
                    checkPopup(e);
                }
            });
        }
        else
		{
            resultsTable.clearTable();
        }
        
        Iterator rows = data.getRows();
        List<String> modelList;
        while (rows.hasNext())
		{
            modelList = new ArrayList<String>();
            ReportedData.Row row = (ReportedData.Row)rows.next();
            for (int i = 0; i < resultsTable.getColumnCount(); i++)
			{
                String tableValue = (String)resultsTable.getTableHeader().getColumnModel().getColumn(i).getHeaderValue();
                Iterator columnIterator = data.getColumns();
                while (columnIterator.hasNext())
				{
                    ReportedData.Column column = (ReportedData.Column)columnIterator.next();
                    if (column.getLabel().equals(tableValue))
					{
                        tableValue = column.getVariable();
                        break;
                    }
                }

                String modelValue = getFirstValue(row, tableValue);
                modelList.add(modelValue);
            }

            resultsTable.getTableModel().addRow(modelList.toArray());

        }
    }

    private void checkPopup(MouseEvent e)
	{
        if (!e.isPopupTrigger())
            return;
        
        final int row = resultsTable.rowAtPoint(e.getPoint());
        final JPopupMenu menu = new JPopupMenu();
        Action addContactAction = new AbstractAction()
		{
            public void actionPerformed(ActionEvent e)
			{
                RosterDialog dialog = new RosterDialog();
                String jid = (String)resultsTable.getValueAt(row, 0);

                TableColumn column = null;
                try
				{
                    column = resultsTable.getColumn("Username");
                }
                catch (Exception ex)
				{
                    try
					{
                        column = resultsTable.getColumn("nick");
                    }
                    catch (Exception e1)
					{
                    }
                }
                if (column != null)
				{
                    int col = column.getModelIndex();
                    String nickname = (String)resultsTable.getValueAt(row, col);
                    if (!ModelUtil.hasLength(nickname))
                        nickname = StringUtils.parseName(jid);
                    dialog.setDefaultNickname(nickname);
                }

                dialog.setDefaultJID(jid);
                dialog.showRosterDialog();
            }
        };

        Action chatAction = new AbstractAction()
		{
            public void actionPerformed(ActionEvent e)
			{
                openChatRoom(row);
            }
        };

        Action profileAction = new AbstractAction()
		{
            public void actionPerformed(ActionEvent e)
			{
                VCardManager vcardSupport = ChatsyManager.getVCardManager();
                String jid = (String)resultsTable.getValueAt(row, 0);
                vcardSupport.viewProfile(jid, resultsTable);
            }
        };

		final JMenuItem addAsContact = new JMenuItem(addContactAction);
		addContactAction.putValue(Action.NAME, "Add as contact");
		menu.add(addAsContact);

        final JMenuItem chatMenu = new JMenuItem(chatAction);
        chatAction.putValue(Action.NAME, "Start Chat");
        menu.add(chatMenu);

        final JMenuItem viewProfileMenu = new JMenuItem(profileAction);
        profileAction.putValue(Action.NAME, "View profile");
        menu.add(viewProfileMenu);

        menu.show(resultsTable, e.getX(), e.getY());
    }

    private final class UsersInfoTable extends Table
	{
        UsersInfoTable(String[] headers)
		{
            super(headers);
            getColumnModel().setColumnMargin(0);
            setSelectionBackground(Table.SELECTION_COLOR);
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            setRowSelectionAllowed(true);
        }
    }

    public String getFirstValue(ReportedData.Row row, String key)
	{
        try
		{
            final Iterator rows = row.getValues(key);
            while (rows.hasNext())
                return (String)rows.next();
        }
        catch (Exception e)
		{
            Log.error("Error retrieving the first value.", e);
        }
        return null;
    }

    private void openChatRoom(int row)
	{
        String jid = (String)resultsTable.getValueAt(row, 0);
        String nickname = StringUtils.parseName(jid);

        TableColumn column;
        try
		{
            column = resultsTable.getColumn("nick");
            int col = column.getModelIndex();
            nickname = (String)resultsTable.getValueAt(row, col);
            if (!ModelUtil.hasLength(nickname))
                nickname = StringUtils.parseName(jid);
        }
        catch (Exception e1)
		{
        }

        ChatManager chatManager = ChatsyManager.getChatManager();
        ChatRoom chatRoom = chatManager.createChatRoom(jid, nickname, nickname);

        ChatContainer chatRooms = chatManager.getChatContainer();
        chatRooms.activateChatRoom(chatRoom);
    }

    public void clearTable()
	{
        if (resultsTable != null)
            resultsTable.clearTable();
    }

}
