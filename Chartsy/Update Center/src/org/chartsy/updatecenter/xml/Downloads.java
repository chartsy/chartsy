package org.chartsy.updatecenter.xml;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Viorel
 */
public class Downloads
{

    private String version;
    private String nbVersion;
    private String features;
    private List<Installer> installers;
    
    public Downloads()
    {
        version = "";
        nbVersion = "";
        features = "";
        installers = new ArrayList<Installer>();
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getVersion()
    {
        return version;
    }

    public void setNbVersion(String nbVersion)
    {
        this.nbVersion = nbVersion;
    }

    public String getNbVersion()
    {
        return nbVersion;
    }

    public void setFeatures(String features)
    {
        this.features = features;
    }

    public String getFeatures()
    {
        return features;
    }

    public void addInstaller(Installer installer)
    {
        installers.add(installer);
    }

    public List<Installer> getInstallers()
    {
        return installers;
    }

    public Installer getInstaller(String os)
    {
        for (Installer installer : installers)
            if (installer.getOS().hashCode() == os.hashCode())
                return installer;
        return null;
    }

}
