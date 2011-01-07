package org.chartsy.chatsy.chat.component;

import org.chartsy.chatsy.chat.ui.ContactItem;
import org.chartsy.chatsy.chat.util.ModelUtil;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.ListCellRenderer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class JContactItemField extends JPanel
{

    private JTextField textField = new JTextField();
    private DefaultListModel model = new DefaultListModel();
    private JList list;
    private JWindow popup;
    private List<ContactItem> items;

    public JContactItemField(List<ContactItem> items)
	{
        setLayout(new BorderLayout());
        list = new JList(model)
		{
            public String getToolTipText(MouseEvent e)
			{
                int row = locationToIndex(e.getPoint());
                final ContactItem item = (ContactItem)getModel().getElementAt(row);
                if (item != null)
                    return item.getJID();
                return null;
            }
        };
        this.items = items;
        add(textField, BorderLayout.CENTER);

        textField.addKeyListener(new KeyAdapter()
		{
            public void keyReleased(KeyEvent keyEvent)
			{
                char ch = keyEvent.getKeyChar();
                if (validateChar(ch))
                    showPopupMenu();

                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER)
				{
                    int index = list.getSelectedIndex();
                    if (index >= 0)
					{
                        ContactItem selection = (ContactItem)list.getSelectedValue();
                        textField.setText(selection.getDisplayName());
                        popup.setVisible(false);
                    }
                }

                if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE)
                    popup.setVisible(false);
				
                dispatchEvent(keyEvent);
            }
            public void keyPressed(KeyEvent e)
			{
                if (isArrowKey(e))
                    list.dispatchEvent(e);
            }
        });

        textField.addFocusListener(new FocusListener()
		{
            public void focusGained(FocusEvent e)
			{
            }
            public void focusLost(FocusEvent e)
			{
                textField.requestFocusInWindow();
            }
        });

        popup = new JWindow();
        popup.getContentPane().add(new JScrollPane(list));
        popup.setAlwaysOnTop(true);

        list.setCellRenderer(new PopupRenderer());
        list.addMouseListener(new MouseAdapter()
		{
            public void mouseClicked(MouseEvent e)
			{
                if (e.getClickCount() == 2)
				{
                    int index = list.getSelectedIndex();
                    if (index >= 0)
					{
                        ContactItem selection = (ContactItem)list.getSelectedValue();
                        textField.setText(selection.getDisplayName());
                        popup.setVisible(false);
                    }
                }
            }
        });
    }

    public void dispose()
	{
        popup.dispose();
    }

    public void setItems(List<ContactItem> list)
	{
        this.items = list;
    }

    public JList getList()
	{
        return list;
    }

    private void showPopupMenu()
	{
        model.removeAllElements();
        String typedItem = textField.getText();
        final List<ContactItem> validItems = new ArrayList<ContactItem>();
        for (ContactItem contactItem : items)
		{
            String nickname = contactItem.getDisplayName().toLowerCase();
            if (nickname.startsWith(typedItem.toLowerCase()))
                validItems.add(contactItem);
        }

        if (validItems.size() > 0)
            for (final ContactItem label : validItems)
                model.addElement(label);

        if (!validItems.isEmpty() && !popup.isVisible())
		{
            popup.pack();
            popup.setSize(textField.getWidth(), 200);
            Point pt = textField.getLocationOnScreen();
            pt.translate(0, textField.getHeight());
            popup.setLocation(pt);
            popup.toFront();
            popup.setVisible(true);
        }

        if (validItems.size() > 0)
            list.setSelectedIndex(0);
    }

    public boolean validateChars(String text)
	{
        if (!ModelUtil.hasLength(text))
            return false;
        for (int i = 0; i < text.length(); i++)
		{
            char ch = text.charAt(i);
            if (!Character.isLetterOrDigit(ch)
				&& ch != '@'
				&& ch != '-'
				&& ch != '_'
				&& ch != '.'
				&& ch != ','
				&& ch != ' ')
                return false;
        }
        return true;
    }

    public boolean validateChar(char ch)
	{
        if (!Character.isLetterOrDigit(ch)
			&& ch != '@'
			&& ch != '-'
			&& ch != '_'
			&& ch != '.'
			&& ch != ','
			&& ch != ' '
			&& ch != KeyEvent.VK_BACK_SPACE
			&& ch != KeyEvent.CTRL_DOWN_MASK
			&& ch != KeyEvent.CTRL_MASK)
            return false;
        return true;
    }

    public boolean isArrowKey(KeyEvent e)
	{
        if (e.getKeyCode() == KeyEvent.VK_UP 
			|| e.getKeyCode() == KeyEvent.VK_DOWN)
            return true;
        return false;
    }

    public String getText()
	{
        return textField.getText();
    }

    public ContactItem getSelectedContactItem()
	{
        return (ContactItem)list.getSelectedValue();
    }

    public void setText(String text)
	{
        textField.setText(text);
    }

    public void focus()
	{
        textField.requestFocus();
    }

    public JTextField getTextField()
	{
        return textField;
    }

    public JWindow getPopup()
	{
        return popup;
    }

    class PopupRenderer extends JLabel implements ListCellRenderer
	{

        public PopupRenderer()
		{
            setOpaque(true);
            this.setHorizontalTextPosition(JLabel.RIGHT);
            this.setHorizontalAlignment(JLabel.LEFT);
        }

        public Component getListCellRendererComponent(JList list,
			Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
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

            ContactItem contactItem = (ContactItem)value;
            setText(contactItem.getDisplayName());
            if (contactItem.getIcon() != null)
                setIcon(contactItem.getIcon());
            setFont(contactItem.getNicknameLabel().getFont());
            setForeground(contactItem.getForeground());

            return this;
        }
    }

    public boolean canClose()
	{
        return !textField.hasFocus();
    }

}
