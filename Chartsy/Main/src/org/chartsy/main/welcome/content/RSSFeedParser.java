package org.chartsy.main.welcome.content;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.chartsy.main.managers.ProxyManager;
import org.chartsy.main.utils.NotifyUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author viorel.gheba
 */
public class RSSFeedParser {

    static final String TITLE = "title";
    static final String DESCRIPTION = "description";
    static final String CHANNEL = "channel";
    static final String LANGUAGE = "language";
    static final String LINK = "link";
    static final String ITEM = "item";
    static final String PUB_DATE = "pubDate";
    static final String GUID = "guid";

    String url;
	String name;
	List<FeedListener> listeners = new ArrayList<FeedListener>();

    public RSSFeedParser(String feedUrl, String feedName) {
		this.name = feedName;
        this.url = feedUrl;
    }

    public void readFeed() {
		org.chartsy.chatsy.chat.util.SwingWorker worker
			= new org.chartsy.chatsy.chat.util.SwingWorker()
		{
			@Override public Object construct()
			{
				Feed feed = null;

				try {
					InputStream stream = ProxyManager.getDefault().inputStreamGET(url);
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					DocumentBuilder builder = factory.newDocumentBuilder();
					Document document = builder.parse(stream);
					document.normalizeDocument();

					if (document != null) {
						Element rss = (Element) document.getElementsByTagName("rss").item(0);
						Element channel = (Element) rss.getElementsByTagName(CHANNEL).item(0);

						String title = "";
						String link = "";
						String desc = "";
						String lang = "";

						NodeList nodeList = channel.getChildNodes();
						for (int i = 0; i < nodeList.getLength(); i++) {
							Element element = (Element) nodeList.item(i);
							if (element.getTagName().equals(TITLE)) title = element.getTextContent();
							if (element.getTagName().equals(LINK)) link = element.getTextContent();
							if (element.getTagName().equals(DESCRIPTION)) desc = element.getTextContent();
							if (element.getTagName().equals(LANGUAGE)) lang = element.getTextContent();
							if (element.getTagName().equals(ITEM)) break;
						}

						feed = new Feed(title, link, desc, lang);
						feed.setFeedName(name);

						nodeList = channel.getChildNodes();
						for (int i = 0; i < nodeList.getLength(); i++) {
							Element item = (Element) nodeList.item(i);

							if (item.getTagName().equals(ITEM)) {
								FeedMessage feedMessage = new FeedMessage();

								NodeList itemNodeList = item.getChildNodes();
								for (int j = 0; j < itemNodeList.getLength(); j++) {
									Element element = (Element) itemNodeList.item(j);

									if (element.getTagName().equals(TITLE)) feedMessage.setTitle(element.getTextContent());
									if (element.getTagName().equals(GUID)) feedMessage.setGuid(element.getTextContent());
									if (element.getTagName().equals(LINK)) feedMessage.setLink(element.getTextContent());
									if (element.getTagName().equals(DESCRIPTION)) feedMessage.setDescription(element.getTextContent());
									if (element.getTagName().equals(PUB_DATE)) feedMessage.setPubDate(element.getTextContent());
								}

								feed.getMessages().add(feedMessage);
							}
						}
					}
				}
				catch (Exception e)
				{
					NotifyUtil.error("RSS parse error", "Could not parse " + name + " RSS Feed", false);
					//Logger.getLogger(getClass().getName()).log(Level.SEVERE, "", e);
				}

				return feed;
			}

			@Override public void finished()
			{
				Feed feed = (Feed) get();
				fireFeedFinished(feed);
			}
		};
        worker.start();
    }

	public void addFeedListener(FeedListener listener)
	{
		listeners.add(listener);
	}

	public void removeFeedListener(FeedListener listener)
	{
		listeners.remove(listener);
	}

	public void fireFeedFinished(Feed feed)
	{
		if (feed != null)
			for (FeedListener listener : listeners)
				listener.fireFeedParsed(new FeedEvent(feed));
	}

}
