package org.chartsy.chatsy.chatimpl.search.users;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.ui.DataFormUI;
import org.chartsy.chatsy.chat.util.SwingWorker;
import org.chartsy.chatsy.chat.util.log.Log;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import org.chartsy.chatsy.chat.util.StringUtils;

public class SearchForm extends JPanel
{
	
    private UserSearchResults searchResults;
    private DataFormUI questionForm;
    private UserSearchManager searchManager;
    private String serviceName;
    private Form searchForm;

    public SearchForm(String service)
	{
        this.serviceName = service;
        searchManager = new UserSearchManager(ChatsyManager.getConnection());
        setLayout(new GridBagLayout());
        try
		{
            searchForm = searchManager.getSearchForm(service);
        }
        catch (XMPPException e)
		{
            Log.error("Unable to load search services.", e);
            JOptionPane.showMessageDialog(
				ChatsyManager.getMainWindow(),
				"Unable to contact search service",
				"Notification",
				JOptionPane.ERROR_MESSAGE);
            return;
        }

        searchManager = new UserSearchManager(ChatsyManager.getConnection());
        questionForm = new DataFormUI(searchForm);
        questionForm.setBorder(BorderFactory.createTitledBorder("Search Form"));
        add(questionForm, new GridBagConstraints(0, 0, 3, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.BOTH,
			new Insets(5, 5, 5, 5), 0, 0));

        final JButton searchButton = new JButton("Search");
        add(searchButton, new GridBagConstraints(0, 1, 3, 1, 1.0, 0.0, 
			GridBagConstraints.EAST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
        searchButton.addActionListener(new ActionListener()
		{
            @Override public void actionPerformed(ActionEvent e)
			{
                performSearch();
            }
        });

        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        String enterString = StringUtils.keyStroke2String(enter);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(enterString), "enter");
        getActionMap().put("enter", new AbstractAction("enter")
		{
            @Override public void actionPerformed(ActionEvent evt)
			{
                performSearch();
            }
        });

        searchResults = new UserSearchResults();
        searchResults.setBorder(BorderFactory.createTitledBorder("Search Results"));
        add(searchResults, new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0, 
			GridBagConstraints.WEST, GridBagConstraints.BOTH,
			new Insets(5, 5, 5, 5), 0, 0));
    }

    public DataFormUI getQuestionForm()
	{
        return questionForm;
    }

    public Form getSearchForm()
	{
        return searchForm;
    }

    public void performSearch()
	{
        searchResults.clearTable();
        SwingWorker worker = new SwingWorker()
		{
            ReportedData data;
            @Override public Object construct()
			{
                try
				{
                    Form answerForm = questionForm.getFilledForm();
                    data = searchManager.getSearchResults(answerForm, serviceName);
                }
                catch (XMPPException e)
				{
                    Log.error("Unable to load search service.", e);
                }

                return data;
            }

            @Override public void finished()
			{
                if (data != null)
				{
                    searchResults.showUsersFound(data);
                    searchResults.invalidate();
                    searchResults.validate();
                    searchResults.repaint();
                }
                else
				{
                    JOptionPane.showMessageDialog(
						searchResults,
						"No search results were returned by the server",
						"Notification",
						JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.start();
    }
	
}
