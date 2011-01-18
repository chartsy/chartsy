package org.chartsy.updatecenter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Observable;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 *
 * @author Viorel
 */
public class Download extends Observable implements Runnable
{

    private static final int MAX_BUFFER_SIZE = 1024;

    public static final String STATUSES[] =
    {
        "Downloading",
        "Paused",
        "Complete",
        "Cancelled",
        "Error"
    };

    public static final int DOWNLOADING = 0;
    public static final int PAUSED = 1;
    public static final int COMPLETE = 2;
    public static final int CANCELLED = 3;
    public static final int ERROR = 4;

    private String url;
    private String folder;
    private int size;
    private int downloaded;
    private int status;

    public Download(String url)
    {
        this.url = url;
        folder = System.getProperty("user.home")
                + File.separator
                + "Chartsy"
                + File.separator
                + "downloads";
        size = -1;
        downloaded = 0;
        status = DOWNLOADING;
        download();
    }

    public String getUrl()
    {
        return url;
    }

    public int getSize()
    {
        return size;
    }

    public float getProgress()
    {
        return ((float) downloaded/size) * 100;
    }

    public int getStatus()
    {
        return status;
    }

    public void pause()
    {
        status = PAUSED;
        stateChanged();
    }

    public void resume()
    {
        status = DOWNLOADING;
        stateChanged();
        download();
    }

    public void cancel()
    {
        status = CANCELLED;
        stateChanged();
    }

    private void error()
    {
        status = ERROR;
        stateChanged();
    }

    private void download()
    {
        Thread thread = new Thread(this);
        thread.start();
    }

    public String getFilePath(String url)
    {
        return folder + File.separator
                + url.substring(url.lastIndexOf('/') + 1);
    }

    public String getFileName(String url)
    {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    public void run()
    {
        RandomAccessFile file = null;
        InputStream stream = null;
        GetMethod method = null;

        try 
        {
            HttpClient client = ProxyManager.manager().httpClient();
            method = new GetMethod(url);

            method.setRequestHeader("Range", "bytes=" + downloaded + "-");

            int responce = client.executeMethod(method);

            if (responce / 100 != 2)
                error();

            Header header = method.getResponseHeader("Content-Length");
            int contentLength = Integer.parseInt(header.getValue());
            if (contentLength < 1)
                error();

            if (size == -1)
            {
                size = contentLength;
                stateChanged();
            }

            file = new RandomAccessFile(getFilePath(url), "rw");
            file.seek(downloaded);

            stream = method.getResponseBodyAsStream();
            while (status == DOWNLOADING)
            {
                byte buffer[];
                if (size - downloaded > MAX_BUFFER_SIZE)
                    buffer = new byte[MAX_BUFFER_SIZE];
                else
                    buffer = new byte[size - downloaded];

                int read = stream.read(buffer);
                if (read == -1)
                    break;

                file.write(buffer, 0, read);
                downloaded += read;
                stateChanged();
            }

            if (status == DOWNLOADING)
            {
                status = COMPLETE;
                stateChanged();
            }
        } 
        catch (IOException ex)
        {
            error();
        } 
        finally
        {
            // Close file.
            if (file != null)
            {
                try { file.close(); }
                catch (Exception e) {}
            }

            // Close connection to server.
            if (stream != null)
            {
                try { stream.close(); }
                catch (Exception e) {}
            }

            if (method != null)
            {
                method.releaseConnection();
            }
        }
    }

    private void stateChanged()
    {
        setChanged();
        notifyObservers();
    }

}
