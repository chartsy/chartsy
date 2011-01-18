package org.chartsy.updatecenter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.chartsy.updatecenter.xml.Downloads;
import org.chartsy.updatecenter.xml.UpdatesXmlParser;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.windows.WindowManager;

/**
 *
 * @author Viorel
 */
public final class VersionChecker implements Runnable
{

    private static VersionChecker instance = null;

    public boolean newVersionFound = false;
    public String installerVersion = null;
    public Downloads downloads;

    public RequestProcessor.Task task;

    private DownloadManager manager;

    public static VersionChecker checker()
    {
        if (instance == null)
            instance = new VersionChecker();
        return instance;
    }

    private VersionChecker()
    {
        manager = new DownloadManager(new JFrame(), true);
        task = Installer.PROCESSOR.post(this, 5000);
        task.addTaskListener(new TaskListener()
        {
            public void taskFinished(Task task)
            {
                if (newVersionFound)
                {
                    if (DownloadManager.canDownload(downloads.getFeatures()))
                    {
                        if (downloads.getInstaller(DownloadManager.getOS()) != null)
                        {
                            final Download download = new Download(
                                    downloads.getInstaller(DownloadManager.getOS()).getUrl());
                            SwingUtilities.invokeLater(new Runnable()
                            {
                                public void run()
                                {
                                    manager.setLocationRelativeTo(
                                            WindowManager.getDefault().getMainWindow());
                                    manager.actionAdd(download);
                                    manager.setVisible(true);
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    public void run()
    {
        newVersionFound = false;
        installerVersion = "";

        HttpClient client = ProxyManager.manager().httpClient();
        HttpMethod method = new GetMethod(
                NbBundle.getMessage(VersionChecker.class, "URL_XMLVersionChecker"));

        try
        {
            client.executeMethod(method);
            downloads = UpdatesXmlParser.getDownloads(method.getResponseBodyAsString());
            newVersionFound = (downloads.getNbVersion().hashCode() !=
                    NbBundle.getMessage(
                    VersionChecker.class, "VER_NetBeans").hashCode()
                    && downloads.getVersion().hashCode() !=
                    NbBundle.getMessage(
                    VersionChecker.class, "VER_Chartsy").hashCode());
            installerVersion = downloads.getVersion();
            method.releaseConnection();
        }
        catch (IOException ex)
        {
            Logger.getLogger(VersionChecker.class.getPackage().getName())
                    .log(Level.SEVERE, "", ex);
        }
    }

}
