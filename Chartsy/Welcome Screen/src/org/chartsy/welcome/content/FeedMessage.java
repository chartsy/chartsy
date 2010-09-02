package org.chartsy.welcome.content;

/**
 *
 * @author viorel.gheba
 */
public class FeedMessage
{

    String title;
    String description;
    String link;
    String guid;
    String pubDate;

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getLink()
    {
        return link;
    }

    public void setLink(String link)
    {
        this.link = link;
    }

    public String getGuid()
    {
        return guid;
    }

    public void setGuid(String guid)
    {
        this.guid = guid;
    }

    public String getPubDate()
    {
        return pubDate;
    }

    public void setPubDate(String pubDate)
    {
        this.pubDate = pubDate;
    }

    @Override
    public String toString()
    {
        return "FeedMessage [title=" + title + ", description=" + description + ", link=" + link + ", guid=" + guid + ", pubDate=" + pubDate + "]";
    }
}
