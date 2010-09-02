package org.chartsy.welcome.content;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author viorel.gheba
 */
public class Feed {

    final String title;
    final String link;
    final String description;
    final String language;
    final List<FeedMessage> entries = new ArrayList<FeedMessage>();

    public Feed(String title, String link, String description, String language) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.language = language;
    }

    public List<FeedMessage> getMessages() { return entries; }
    public String getTitle() { return title; }
    public String getLink() { return link; }
    public String getDescription() { return description; }
    public String getLanguage() { return language; }

    public String toString() { return "Feed [description=" + description + ", language=" + language + ", link=" + link + ", title=" + title + "]"; }

}
