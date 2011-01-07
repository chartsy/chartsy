package org.chartsy.chatsy.chatimpl.search.users;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.component.TitlePanel;
import org.chartsy.chatsy.chat.ui.DataFormUI;
import org.chartsy.chatsy.chat.util.ModelUtil;
import org.chartsy.chatsy.chat.util.SwingWorker;
import org.chartsy.chatsy.chat.util.log.Log;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class UserSearchForm extends JPanel
{

    private JComboBox servicesBox;
    private UserSearchManager searchManager;
    private Collection<String> searchServices;
    private CardLayout cardLayout = new CardLayout();
    private JPanel cardPanel = new JPanel();
    private TitlePanel titlePanel;
    private Map<String,SearchForm> serviceMap = new HashMap<String,SearchForm>();
    
    public UserSearchForm(Collection<String> searchServices)
	{
        setLayout(new GridBagLayout());
        cardPanel.setLayout(cardLayout);
        this.searchServices = searchServices;
        searchManager = new UserSearchManager(ChatsyManager.getConnection());
        addSearchServices();
        showService(getSearchService());
    }
    
    private void addSearchServices()
	{
        servicesBox = new JComboBox();    
        for (String searchService : searchServices)
		{
            String service = searchService;
            servicesBox.addItem(service);
        }
        
        titlePanel = new TitlePanel("", "", null, true);
        add(titlePanel, new GridBagConstraints(0, 0, 3, 1, 1.0, 0.0, 
			GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
			new Insets(0, 0, 0, 0), 0, 0));

        final JLabel serviceLabel = new JLabel("Search Services");
        add(serviceLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
        add(servicesBox, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 150, 0));

        final JButton addService = new JButton("Add Service");
        add(addService, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
        addService.addActionListener(new ActionListener()
		{
            public void actionPerformed(ActionEvent actionEvent) 
			{
				final String serviceName = JOptionPane.showInputDialog(
					getRootPane(),
					"Name of search service?",
					"Add search service",
					JOptionPane.QUESTION_MESSAGE);
                if (ModelUtil.hasLength(serviceName))
				{
                    SwingWorker findServiceThread = new SwingWorker()
					{
                        Form newForm;
                        public Object construct()
						{
                            try
							{
                                newForm = searchManager.getSearchForm(serviceName);
                            }
                            catch (XMPPException e)
							{
                            }
                            return newForm;
                        }

                        public void finished()
						{
                            if (newForm == null)
							{
                                JOptionPane.showMessageDialog(
									getGUI(),
									"Unable to contact search service",
									"Notification",
									JOptionPane.ERROR_MESSAGE);
                            }
                            else
							{
                                servicesBox.addItem(serviceName);
                                servicesBox.setSelectedItem(serviceName);
                            }
                            
                        }
                    };
                    findServiceThread.start();
                }
            }
        });

        servicesBox.addActionListener(new ActionListener()
		{
            public void actionPerformed(ActionEvent actionEvent)
			{
                SwingWorker worker = new SwingWorker()
				{
                    public Object construct()
					{
                        try
						{
                            Thread.sleep(50);
                        }
                        catch (Exception e)
						{
                            Log.error("Problem sleeping thread.", e);
                        }
                        return "ok";
                    }

                    public void finished()
					{
                        showService(getSearchService());
                    }
                };
                worker.start();
            }
        });

        add(cardPanel, new GridBagConstraints(0, 3, 3, 1, 1.0, 1.0, 
			GridBagConstraints.WEST, GridBagConstraints.BOTH,
			new Insets(5, 5, 5, 5), 0, 0));

		if (servicesBox.getItemCount() > 1)
			servicesBox.setSelectedIndex(0);
		else if (servicesBox.getItemCount() == 1)
		{
			serviceLabel.setVisible(false);
			addService.setVisible(false);
			servicesBox.setVisible(false);
		}
    }

    public void showService(String service)
	{
        if (serviceMap.containsKey(service))
		{
            cardLayout.show(cardPanel, service);
        }
        else
		{
            SearchForm searchForm = new SearchForm(service);
            cardPanel.add(searchForm, service);
            serviceMap.put(service, searchForm);
            cardLayout.show(cardPanel, service);
        }

        SearchForm searchForm = serviceMap.get(service);
        Form form = searchForm.getSearchForm();
        String description = form.getInstructions();
        titlePanel.setTitle("Buddy Search");
        titlePanel.setDescription(description);
    }

    public String getSearchService()
	{
        return (String)servicesBox.getSelectedItem();
    }

    public DataFormUI getQuestionForm()
	{
        SearchForm searchForm = serviceMap.get(getSearchService());
        return searchForm.getQuestionForm();
    }

    public void performSearch()
	{
        SearchForm searchForm = serviceMap.get(getSearchService());
        searchForm.performSearch();
    }

    public Component getGUI()
	{
        return this;
    }

}
