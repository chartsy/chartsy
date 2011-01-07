package org.chartsy.chatsy.chat.search;

import org.chartsy.chatsy.chat.util.SwingWorker;
import org.chartsy.chatsy.chatimpl.search.users.UserSearchService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SearchManager
{
    private List<Searchable> searchServices = new ArrayList<Searchable>();
    private SearchService ui;
    private static SearchManager singleton;
    private static final Object LOCK = new Object();

    public static SearchManager getInstance()
	{
        synchronized (LOCK)
		{
            if (null == singleton)
			{
                SearchManager controller = new SearchManager();
                singleton = controller;
                return controller;
            }
        }
        return singleton;
    }

    private SearchManager()
	{
        ui = new SearchService();
        SwingWorker worker = new SwingWorker()
		{
            UserSearchService searchWizard;
            public Object construct()
			{
                searchWizard = new UserSearchService();
                return searchWizard;
            }
            public void finished()
			{
                if (searchWizard.getSearchServices() != null)
				{
                    ui.setActiveSearchService(searchWizard);
                    addSearchService(searchWizard);
                }
            }
        };
        worker.start();
    }

    public void addSearchService(Searchable searchable)
	{
        searchServices.add(searchable);
    }

    public void removeSearchService(Searchable searchable)
	{
        searchServices.remove(searchable);
    }

    public Collection<Searchable> getSearchServices()
	{
        return searchServices;
    }

	public Collection<String> getSearchServicesNames()
	{
        List<String> names = new ArrayList<String>();
		for (Searchable searchable : searchServices)
			names.add(searchable.getName());
		return names;
    }

    public SearchService getSearchServiceUI()
	{
        return ui;
    }

}
