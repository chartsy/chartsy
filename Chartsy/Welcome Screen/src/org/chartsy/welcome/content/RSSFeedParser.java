package org.chartsy.welcome.content;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.chartsy.main.managers.ProxyManager;
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

    URL url;

    public RSSFeedParser(String feedUrl) {
        try { this.url = new URL(feedUrl); }
        catch (MalformedURLException e) {}
    }

    public Feed readFeed() {
        Feed feed = null;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(
				ProxyManager.getDefault().inputStreamGET(url.toString()));
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

                nodeList = channel.getChildNodes();
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Element item = (Element) nodeList.item(i);

                    if (item.getTagName().equals(ITEM)) {
                        NodeList itemNodeList = item.getChildNodes();
                        FeedMessage feedMessage = new FeedMessage();
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
            e.printStackTrace();
        }

        return feed;
    }

}
