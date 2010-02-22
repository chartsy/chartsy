package org.chartsy.main.intro.feed;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

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

    final URL url;

    public RSSFeedParser(String feedUrl) {
        try { this.url = new URL(feedUrl); }
        catch (MalformedURLException e) { throw new RuntimeException(e); }
    }

    @SuppressWarnings("null")
    public Feed readFeed() {
        Feed feed = null;

        try {
            boolean isFeedHeader = true;

            String description = "";
            String title = "";
            String link = "";
            String language = "";
            String pubdate = "";
            String guid = "";

            // First create a new XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            // Setup a new eventReader
            InputStream in = read();
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
            // Read the XML document
            while (eventReader.hasNext()) {

                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    String s = event.asStartElement().getName().getLocalPart();

                    if (event.asStartElement().getName().getLocalPart().equals(ITEM)) {
                        if (isFeedHeader) {
                            isFeedHeader = false;
                            feed = new Feed(title, link, description, language);
                        }
                        event = eventReader.nextEvent();
                        continue;
                    }

                    if (event.asStartElement().getName().getLocalPart().equals(TITLE)) {
                            event = eventReader.nextEvent();
                            title = event.asCharacters().getData();
                            continue;
                    }
                    if (event.asStartElement().getName().getLocalPart().equals(DESCRIPTION)) {
                            event = eventReader.nextEvent();
                            description = event.asCharacters().getData();
                            continue;
                    }

                    if (event.asStartElement().getName().getLocalPart().equals(LINK)) {
                            event = eventReader.nextEvent();
                            link = event.asCharacters().getData();
                            continue;
                    }

                    if (event.asStartElement().getName().getLocalPart().equals(GUID)) {
                            event = eventReader.nextEvent();
                            guid = event.asCharacters().getData();
                            continue;
                    }
                    if (event.asStartElement().getName().getLocalPart().equals(LANGUAGE)) {
                            event = eventReader.nextEvent();
                            language = event.asCharacters().getData();
                            continue;
                    }
                    if (event.asStartElement().getName().getLocalPart().equals(PUB_DATE)) {
                            event = eventReader.nextEvent();
                            pubdate = event.asCharacters().getData();
                            continue;
                    }
                } else if (event.isEndElement()) {
                    String s = event.asEndElement().getName().getLocalPart();

                    if (event.asEndElement().getName().getLocalPart().equals(ITEM)) {
                        FeedMessage message = new FeedMessage();
                        message.setDescription(description);
                        message.setGuid(guid);
                        message.setLink(link);
                        message.setTitle(title);
                        message.setPubDate(pubdate);
                        feed.getMessages().add(message);
                        event = eventReader.nextEvent();
                        continue;
                    }
                }
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }

        return feed;
    }

    private InputStream read() {
        try { return url.openStream(); }
        catch (IOException e) { throw new RuntimeException(e); }
    }


}
