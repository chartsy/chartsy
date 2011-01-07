package org.chartsy.chatsy.chat.search;

import org.chartsy.chatsy.Chatsy;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.component.IconTextField;
import org.chartsy.chatsy.chat.util.SwingWorker;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import javax.swing.JTextField;

public class SearchService extends JPanel
{

    private JTextField findField;
    private boolean newSearch;
    private Searchable activeSearchable;

    public SearchService()
	{
        setLayout(new GridBagLayout());
        findField = new JTextField();
        final JLabel findLabel = new JLabel();
		findLabel.setText("Find");
		
        if (Chatsy.isMac())
            add(findField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, 
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 30), 0, 0));
        else
            add(findField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, 
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));

        if (ChatsyManager.getConnection().isSecureConnection())
		{
            final JLabel lockLabel = new JLabel();
            lockLabel.setHorizontalTextPosition(JLabel.LEFT);
            if (Chatsy.isMac())
                add(lockLabel, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, 
					GridBagConstraints.WEST, GridBagConstraints.NONE,
					new Insets(5, 0, 5, 15), 0, 0));
            else 
                add(lockLabel, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, 
					GridBagConstraints.WEST, GridBagConstraints.NONE,
					new Insets(5, 0, 5, 5), 0, 0));
        }

        findField.addKeyListener(new KeyListener()
		{
			public void keyTyped(KeyEvent e)
			{
            }
            public void keyPressed(KeyEvent e)
			{
                if (e.getKeyChar() == KeyEvent.VK_ENTER)
				{
                    SwingWorker worker = new SwingWorker()
					{
                        public Object construct()
						{
                            activeSearchable.search(findField.getText());
                            return true;
                        }
                        public void finished()
						{
                            findField.setText("");
                        }
                    };
                    worker.start();
                }
            }
            public void keyReleased(KeyEvent e)
			{
            }
        });

        findField.addMouseListener(new MouseAdapter()
		{
            public void mouseClicked(MouseEvent e)
			{
                if (newSearch)
				{
                    findField.setText("");
                    findField.setForeground((Color) UIManager.get("TextField.foreground"));
                    newSearch = false;
                }
            }
        });

        findField.addMouseListener(new MouseAdapter()
		{
            public void mouseClicked(MouseEvent e)
			{
                Collection<Searchable> searchables = ChatsyManager.getSearchManager().getSearchServices();
                if (searchables.size() <= 1)
                    return;
                final JPopupMenu popup = new JPopupMenu();
                for (final Searchable searchable : searchables)
				{
                    Action action = new AbstractAction()
					{
                        public void actionPerformed(ActionEvent e)
						{
                            setActiveSearchService(searchable);
                        }
                    };
                    action.putValue(Action.SMALL_ICON, searchable.getIcon());
                    action.putValue(Action.NAME, searchable.getName());
                    popup.add(action);
                }
                popup.show(findField, 0, findField.getHeight());
            }
        });
    }

    public void setActiveSearchService(final Searchable searchable)
	{
        this.activeSearchable = searchable;
        newSearch = true;
        findField.requestFocus();
        findField.setForeground((Color) UIManager.get("TextField.lightforeground"));
        findField.setText(searchable.getDefaultText());
        findField.setToolTipText(searchable.getToolTip());

        findField.addFocusListener(new FocusListener()
		{
            public void focusGained(FocusEvent e)
			{
                findField.setText("");
            }
            public void focusLost(FocusEvent e)
			{
                findField.setForeground((Color) UIManager.get("TextField.lightforeground"));
                findField.setText(searchable.getDefaultText());
            }
        });
    }

    protected JTextField getFindField()
	{
        return findField;
    }

}
