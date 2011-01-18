package org.chartsy.updatecenter.xml;

/**
 *
 * @author Viorel
 */
public class Installer
{

    private String filename;
    private String url;
    private String os;

    public Installer()
    {
        filename = "";
        url = "";
        os = "";
    }

    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    public String getFilename()
    {
        return filename;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getUrl()
    {
        return url;
    }

    public void setOS(String os)
    {
        this.os = os;
    }

    public String getOS()
    {
        return os;
    }

}
