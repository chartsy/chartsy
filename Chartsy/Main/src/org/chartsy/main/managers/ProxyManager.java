package org.chartsy.main.managers;

import java.util.prefs.Preferences;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.openide.util.NbPreferences;

/**
 *
 * @author viorel.gheba
 */
public final class ProxyManager
{

    private static ProxyManager instance;

    private int proxyType = 1; // default value
    private String proxyHttpHost = "";
    private int proxyHttpPort = 0;
    private boolean useProxyAuthentication = false; // default value
    private String proxyAuthenticationUsername = "";
    private String proxyAuthenticationPassword = "";

    public static ProxyManager getDefault()
    {
        if (instance == null)
            instance = new ProxyManager();
        return instance;
    }

    private ProxyManager()
    {
        initialize();
    }

    public void initialize()
    {
        Preferences p = NbPreferences.root().node("/org/netbeans/core");
        if (p != null)
        proxyType = p.getInt(PROXY_TYPE_KEY, 1);
        if (proxyType == 2)
        {
            proxyHttpHost = p.get(PROXY_HTTP_HOST_KEY, "");
            proxyHttpPort = p.getInt(PROXY_HTTP_PORT_KEY, 0);
            useProxyAuthentication = p.getBoolean(PROXY_USE_AUTH_KEY, false);
            if (useProxyAuthentication)
            {
                proxyAuthenticationUsername = p.get(PROXY_USERNAME_KEY, "");
                proxyAuthenticationPassword = p.get(PROXY_PASSWORD_KEY, "");
            }
        }
    }

    public boolean useProxy()
    { return proxyType == 2; }

    public HttpClient getHttpClient()
    {
        initialize();
        if (proxyType == 2)
        {
            HttpClient client = new HttpClient();

            HostConfiguration config = client.getHostConfiguration();
            config.setProxy(proxyHttpHost, proxyHttpPort);
            
            if (useProxyAuthentication)
            {
                Credentials credentials = new UsernamePasswordCredentials(proxyAuthenticationUsername, proxyAuthenticationPassword);
                AuthScope authScope = new AuthScope(proxyHttpHost, proxyHttpPort);
                client.getState().setProxyCredentials(authScope, credentials);
            }

            return client;
        }
        else
        {
            return new HttpClient();
        }
    }

    private static final String PROXY_TYPE_KEY = "proxyType";
    private static final String PROXY_HTTP_HOST_KEY = "proxyHttpHost";
    private static final String PROXY_HTTP_PORT_KEY = "proxyHttpPort";
    private static final String PROXY_USE_AUTH_KEY = "useProxyAuthentication";
    private static final String PROXY_USERNAME_KEY = "proxyAuthenticationUsername";
    private static final String PROXY_PASSWORD_KEY = "proxyAuthenticationPassword";

}
