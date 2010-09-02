package org.chartsy.welcome;

import org.chartsy.welcome.content.Feed;
import org.chartsy.welcome.content.RSSFeedParser;
import org.openide.util.NbBundle;

/**
 *
 * @author Viorel
 */
public class Feeds
{

	private static Feeds instance;

	private static Feed randomPlugin;
	private static Feed latestNews;
	private static Feed forum;

	public static Feeds getDefault()
	{
		if (instance == null)
			instance = new Feeds();
		return instance;
	}

	private Feeds()
	{}

	public static void start()
	{
		getDefault();

		randomPlugin = (new RSSFeedParser(NbBundle.getMessage(Feeds.class, "URL_RandomPlugin"))).readFeed();
		latestNews = (new RSSFeedParser(NbBundle.getMessage(Feeds.class, "URL_LatestNews"))).readFeed();
		forum = (new RSSFeedParser(NbBundle.getMessage(Feeds.class, "URL_Forum"))).readFeed();
	}

	public Feed getRandomPlugin()
	{
		return randomPlugin;
	}

	public Feed getLatestNews()
	{
		return latestNews;
	}

	public Feed getForum()
	{
		return forum;
	}

}
