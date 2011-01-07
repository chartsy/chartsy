package org.chartsy.main.welcome;

import org.chartsy.main.welcome.content.FeedListener;
import org.chartsy.main.welcome.content.RSSFeedParser;
import org.openide.util.NbBundle;

/**
 *
 * @author Viorel
 */
public class Feeds
{

	private static Feeds instance;

	private static RSSFeedParser randomPluginParser;
	private static RSSFeedParser latestNewsParser;
	private static RSSFeedParser forumParser;

	public static final String randomPluginFeed = "randomPlugin";
	public static final String latestNewsFeed = "latestNews";
	public static final String forumFeed = "forum";

	public static Feeds getDefault()
	{
		if (instance == null)
			instance = new Feeds();
		return instance;
	}

	private Feeds()
	{
		randomPluginParser = new RSSFeedParser(
			NbBundle.getMessage(Feeds.class, "URL_RandomPlugin"),
			randomPluginFeed);

		latestNewsParser = new RSSFeedParser(
			NbBundle.getMessage(Feeds.class, "URL_LatestNews"),
			latestNewsFeed);

		forumParser = new RSSFeedParser(
			NbBundle.getMessage(Feeds.class, "URL_Forum"),
			forumFeed);
	}

	public static void start()
	{
		randomPluginParser.readFeed();
		latestNewsParser.readFeed();
		forumParser.readFeed();
	}

	public static void addFeedListener(String feedName, FeedListener listener)
	{
		if (feedName.equals(randomPluginFeed))
			randomPluginParser.addFeedListener(listener);
		if (feedName.equals(latestNewsFeed))
			latestNewsParser.addFeedListener(listener);
		if (feedName.equals(forumFeed))
			forumParser.addFeedListener(listener);
	}

}
