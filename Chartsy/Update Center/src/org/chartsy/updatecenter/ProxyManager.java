package org.chartsy.updatecenter;

import java.util.prefs.Preferences;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;

/**
 *
 * @author Viorel
 */
public final class ProxyManager
{

    private static final int NO_PROXY = 0;
    private static final int DEFAULT_PROXY = 1;
    private static final int MANUAL_PROXY = 2;

    private static ProxyManager instance;
    private static Preferences preferences
                = NbPreferences.root().node("/org/netbeans/core");

    public static ProxyManager manager()
    {
        if (instance == null)
            instance = new ProxyManager();
        return instance;
    }

    private ProxyManager()
    {}

    private String getKey(String key)
    {
        return NbBundle.getMessage(ProxyManager.class, "KEY_Value", key);
    }

    private boolean useProxy()
    {
        return preferences.getInt(
                NbBundle.getMessage(ProxyManager.class, getKey("Type")),
                DEFAULT_PROXY)
                == MANUAL_PROXY;
    }

    private String proxyHost()
    {
        return preferences.get(
                NbBundle.getMessage(ProxyManager.class, getKey("HttpHost")), "");
    }

    private int proxyPort()
    {
        return preferences.getInt(
                NbBundle.getMessage(ProxyManager.class, getKey("HttpPort")), 0);
    }

    private boolean useProxyAuth()
    {
        return preferences.getBoolean(
                NbBundle.getMessage(ProxyManager.class, getKey("UseAuth")), false);
    }

    private String proxyUsername()
    {
        return preferences.get(
                NbBundle.getMessage(ProxyManager.class, getKey("Username")), "");
    }

    private String proxyPassword()
    {
        return preferences.get(
                NbBundle.getMessage(ProxyManager.class, getKey("Password")), "");
    }

    public HttpClient httpClient()
    {
        HttpClient client = new HttpClient();

        if (useProxy())
        {
            if (proxyHost().hashCode() != "".hashCode()
                    && proxyPort() != 0)
                client.getHostConfiguration().setProxy(proxyHost(), proxyPort());

            if (useProxyAuth())
            {
                if (proxyUsername().hashCode() != "".hashCode()
                        && proxyPassword().hashCode() != "".hashCode())
                {
                    Credentials credentials
                            = new UsernamePasswordCredentials(
                            proxyUsername(), proxyPassword());
                    AuthScope authScope
                            = new AuthScope(proxyHost(), proxyPort());
                    client.getState().setProxyCredentials(authScope, credentials);
                }
            }
        }

        return client;
    }

}
