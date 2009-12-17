package org.chartsy.main.utils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author viorelgheba
 */
public class URLChecker {

    public static final int INFORMATIONAL = 1;
    public static final int SUCCESSFUL = 2;
    public static final int REDIRECTION = 3;
    public static final int ERROR = 4;
    public static final int SERVER_ERROR = 5;

    private URLChecker() {}

    public static boolean checkURL(URL url) {
        boolean ok = false;
        try {
            URLConnection connection = url.openConnection();
            if (connection instanceof HttpURLConnection) {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.connect();
                int responce = httpConnection.getResponseCode() / 100;
                System.out.println("Responce = " + responce);
                switch (responce) {
                    case INFORMATIONAL:
                        ok = true;
                        break;
                    case SUCCESSFUL:
                        ok = true;
                        break;
                    case REDIRECTION:
                        ok = true;
                        break;
                    case ERROR:
                        ok = false;
                        break;
                    case SERVER_ERROR:
                        ok = false;
                        break;
                }
            }
        } catch (Exception e) {
            ok = false;
        }
        return ok;
    }

}
