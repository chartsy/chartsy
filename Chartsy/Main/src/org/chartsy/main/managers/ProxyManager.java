package org.chartsy.main.managers;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.prefs.Preferences;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.openide.util.NbPreferences;

/**
 *
 * @author viorel.gheba
 */
public final class ProxyManager
{

    private static ProxyManager instance;
    private static Preferences corePreferences = NbPreferences.root().node("/org/netbeans/core");
    private HttpClient client;
    private boolean isOnline;

    public static ProxyManager getDefault()
    {
        if (instance == null)
        {
            instance = new ProxyManager();
        }
        return instance;
    }

    private ProxyManager()
    {
        setOnline(true);

		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
		System.setProperty ("org.apache.commons.logging.simplelog.showdatetime", "true");
		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "error");

        MultiThreadedHttpConnectionManager manager = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams params = new HttpConnectionManagerParams();
        params.setMaxTotalConnections(100);
        manager.setParams(params);

        client = new HttpClient(manager);
        client.getParams().setSoTimeout(10000);
        client.getParams().setParameter("http.connection.timeout", 30000);
        setProxy();
    }

    public HttpClient httpClient()
    {
        return client;
    }

    public String inputStringPOST(String url, NameValuePair[] query, NameValuePair[] request)
		throws IOException
    {
        String response = "";
        PostMethod method = new PostMethod(url);
        method.setQueryString(query);
        method.setRequestBody(request);

		int status = client.executeMethod(method);
		if (status != HttpStatus.SC_OK)
		{
			throw new IOException(method.getStatusText());
		} else
		{
			InputStream is = method.getResponseBodyAsStream();
			BufferedInputStream bis = new BufferedInputStream(is);

			String datastr = null;
			StringBuilder sb = new StringBuilder();
			byte[] bytes = new byte[8192]; // reading as chunk of 8192 bytes

			int count = bis.read(bytes);
			while (count != -1 && count <= 8192)
			{
				datastr = new String(bytes, 0, count);
				sb.append(datastr);
				count = bis.read(bytes);
			}

			bis.close();
			response = sb.toString();
		}
		
		method.releaseConnection();

        return response;
    }

    public InputStream inputStreamGET(String url)
		throws IOException
    {
        InputStream stream = null;
        GetMethod method = new GetMethod(url);

		int status = client.executeMethod(method);
		if (status != HttpStatus.SC_OK)
		{
			throw new IOException(method.getStatusText());
		} else
		{
			stream = new ByteArrayInputStream(method.getResponseBody());
		}
		
        return stream;
    }

    public BufferedReader bufferReaderGET(String url)
		throws IOException
    {
        InputStream stream = inputStreamGET(url);
        if (stream != null)
        {
            return new BufferedReader(new InputStreamReader(stream));
        }
        return null;
    }

    public InputStream inputStreamPOST(String url, NameValuePair[] query)
		throws IOException
    {
        InputStream stream = null;
        PostMethod method = new PostMethod(url);
        method.setRequestBody(query);

		int status = client.executeMethod(method);
		if (status != HttpStatus.SC_OK)
		{
			throw new IOException(method.getStatusText());
		} else
		{
			stream = new ByteArrayInputStream(method.getResponseBody());
		}

        return stream;
    }

    public BufferedReader bufferReaderPOST(String url, NameValuePair[] query)
		throws IOException
    {
        InputStream stream = inputStreamPOST(url, query);
        if (stream != null)
        {
            return new BufferedReader(new InputStreamReader(stream));
        }
        return null;
    }

    public void setProxy()
    {
        if (corePreferences.getInt(PROXY_TYPE_KEY, 1) == 2)
        {
            HostConfiguration config = client.getHostConfiguration();
            config.setProxy(
                    corePreferences.get(PROXY_HTTP_HOST_KEY, ""),
                    corePreferences.getInt(PROXY_HTTP_PORT_KEY, 0));
            if (corePreferences.getBoolean(PROXY_USE_AUTH_KEY, false))
            {
                Credentials credentials = new UsernamePasswordCredentials(
                        corePreferences.get(PROXY_USERNAME_KEY, ""),
                        corePreferences.get(PROXY_PASSWORD_KEY, ""));
                client.getState().setProxyCredentials(AuthScope.ANY, credentials);
            } else
            {
                client.getState().setProxyCredentials(AuthScope.ANY, null);
            }
        }
    }

    public HttpClient getHttpClient()
    {
        if (corePreferences.getInt(PROXY_TYPE_KEY, 1) == 2)
        {
            HttpClient httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
            httpClient.getParams().setSoTimeout(10000);
            httpClient.getParams().setParameter("http.connection.timeout", 30000);
            HostConfiguration config = httpClient.getHostConfiguration();
            config.setProxy(
                    corePreferences.get(PROXY_HTTP_HOST_KEY, ""),
                    corePreferences.getInt(PROXY_HTTP_PORT_KEY, 0));
            if (corePreferences.getBoolean(PROXY_USE_AUTH_KEY, false))
            {
                Credentials credentials = new UsernamePasswordCredentials(
                        corePreferences.get(PROXY_USERNAME_KEY, ""),
                        corePreferences.get(PROXY_PASSWORD_KEY, ""));
                AuthScope authScope = new AuthScope(
                        corePreferences.get(PROXY_HTTP_HOST_KEY, ""),
                        corePreferences.getInt(PROXY_HTTP_PORT_KEY, 0));
                httpClient.getState().setProxyCredentials(authScope, credentials);
            }
            return httpClient;
        } else
        {
            HttpClient httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
            httpClient.getParams().setSoTimeout(10000);
            httpClient.getParams().setParameter("http.connection.timeout", 30000);
            return httpClient;
        }
    }

    public void setOnline(boolean online)
    {
        this.isOnline = online;
    }

    public boolean isOnline()
    {
        return isOnline;
    }
	
    private static final String PROXY_TYPE_KEY = "proxyType";
    private static final String PROXY_HTTP_HOST_KEY = "proxyHttpHost";
    private static final String PROXY_HTTP_PORT_KEY = "proxyHttpPort";
    private static final String PROXY_USE_AUTH_KEY = "useProxyAuthentication";
    private static final String PROXY_USERNAME_KEY = "proxyAuthenticationUsername";
    private static final String PROXY_PASSWORD_KEY = "proxyAuthenticationPassword";
	
}
