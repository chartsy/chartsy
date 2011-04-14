package org.chartsy.main.managers;

import com.google.code.facebookapi.FacebookException;
import com.google.code.facebookapi.FacebookJsonRestClient;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.utils.DesktopUtil;
import org.chartsy.main.utils.ImageExporter;
import org.chartsy.main.utils.MessageType;
import org.chartsy.main.utils.NotifyUtil;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.NbPreferences;
import org.openide.windows.WindowManager;

/**
 *
 * @author Viorel
 */
public final class FacebookManager
{

    private static final String API_KEY = "549641ea0bc5bb4488383e263f286aa0";
    private static final String SECRET = "325d3e5ea84c3ba875a526f715b66265";

    private static FacebookManager instance;

    private boolean loggedIn = false;

    private String email = "";
    private String pass = "";
    private boolean autoLogin = false;

    private String sessionKey;
    private String sessionSecret;

    private PhotoCaptionPanel captionPanel;

    public static FacebookManager getDefault()
    {
        if (instance == null) {
            instance = new FacebookManager();
        }
        return instance;
    }

    private FacebookManager()
    {
        loggedIn = false;
        Preferences pref = NbPreferences.forModule(FacebookManager.class);
        email = pref.get("facebook_email", "");
        pass = pref.get("facebook_pass", "");
        autoLogin = pref.getBoolean("facebook_auto_login", false);
        if (autoLogin) {
            login();
        }
    }

    public void reinitialize()
    {
        Preferences pref = NbPreferences.forModule(FacebookManager.class);
        email = pref.get("facebook_email", "");
        pass = pref.get("facebook_pass", "");
        autoLogin = pref.getBoolean("facebook_auto_login", false);
        if (autoLogin) {
            login();
        }
    }

    public boolean isLoggedIn()
    {
        return loggedIn;
    }

    public void logout()
    {
        loggedIn = false;
    }

    public void login()
    {
        if (email.isEmpty() || pass.isEmpty()) {
            NotifyUtil.warn("Acount Data", "You need to specify and account e-mail and password.", false);
            return;
        }

        final ProgressHandle handle = ProgressHandleFactory.createHandle("Connecting to Facebook ...");

        final Runnable task = new Runnable() {
            @Override
            public void run()
            {
                handle.start();
                handle.switchToIndeterminate();

                FacebookJsonRestClient client = new FacebookJsonRestClient(API_KEY, SECRET);
                client.setIsDesktop(true);

                HttpURLConnection connection;
                List<String> cookies;

                try {
                    String token = client.auth_createToken();

                    String post_url = "http://www.facebook.com/login.php";
                    String get_url = "http://www.facebook.com/login.php"
                        + "?api_key=" + API_KEY
                        + "&v=1.0"
                        + "&auth_token=" + token;

                    HttpURLConnection.setFollowRedirects(true);

                    // get login
                    connection = (HttpURLConnection) new URL(get_url).openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2.3) Gecko/20100401");
                    connection.setRequestProperty("Host", "www.facebook.com");
                    connection.setRequestProperty("Accept-Charset", "UFT-8");
                    connection.connect();

                    cookies = connection.getHeaderFields().get("Set-Cookie");

                    // post login
                    connection = (HttpURLConnection) new URL(post_url).openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2.3) Gecko/20100401");
                    connection.setRequestProperty("Host", "www.facebook.com");
                    connection.setRequestProperty("Accept-Charset", "UFT-8");

                    if (cookies != null) {
                        for (String cookie : cookies) {
                            connection.addRequestProperty("Cookie", cookie.split(";", 2)[0]);
                        }
                    }

                    String params = "api_key=" + API_KEY
                        + "&auth_token=" + token
                        + "&email=" + URLEncoder.encode(email, "UTF-8")
                        + "&pass=" + URLEncoder.encode(pass, "UTF-8")
                        + "&v=1.0";

                    connection.setRequestProperty("Content-Length", Integer.toString(params.length()));
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setDoOutput(true);
                    connection.connect();

                    connection.getOutputStream().write(params.toString().getBytes("UTF-8"));
                    connection.getOutputStream().close();

                    cookies = connection.getHeaderFields().get("Set-Cookie");
                    if (cookies == null) {
                        ActionListener listener = new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                String url = "http://www.chartsy.org/facebook/";
                                DesktopUtil.browseAndWarn(url, null);
                            }
                        };
                        NotifyUtil.show(
                            "Application Permission",
                            "You need to grant permission first.",
                            MessageType.WARNING,
                            listener,
                            false);
                        connection.disconnect();
                        loggedIn = false;
                    } else {
                        sessionKey = client.auth_getSession(token);
                        sessionSecret = client.getSessionSecret();
                        loggedIn = true;
                    }

                    connection.disconnect();
                    handle.finish();
                } catch (FacebookException fex) {
                    handle.finish();
                    NotifyUtil.error("Login Error", fex.getMessage(), fex, false);
                } catch (IOException ioex) {
                    handle.finish();
                    NotifyUtil.error("Login Error", ioex.getMessage(), ioex, false);
                }
            }
        };

        WindowManager.getDefault().invokeWhenUIReady(task);
    }

    public void publishChart(final ChartFrame chartFrame, final String caption)
    {
        if (loggedIn) {
            final ProgressHandle handle = ProgressHandleFactory.createHandle("Uploading chart image to wall ...");

            final Runnable task = new Runnable()
            {
                @Override
                public void run()
                {
                    handle.start();
                    handle.switchToIndeterminate();

                    FacebookJsonRestClient client = new FacebookJsonRestClient(API_KEY, sessionSecret, sessionKey);

                    try {
                        File file = ImageExporter.getDefault().getExportedFile(chartFrame, getClass());
                        if (caption != null && !caption.isEmpty()) {
                            String text = caption;
                            text += "\n\n";
                            text += "Image generated by Chartsy.\nVisit us on www.chartsy.org.";
                            client.photos_upload(file, text);
                        } else {
                            String text = "Image generated by Chartsy.\nVisit us on www.chartsy.org.";
                            client.photos_upload(file, text);
                        }
                        NotifyUtil.info("Upload Done", "File uploaded successfully.", false);
                    } catch (FacebookException fex) {
                        NotifyUtil.error("Upload Error", fex.getMessage(), fex, false);
                    }

                    handle.finish();
                }
            };

            WindowManager.getDefault().invokeWhenUIReady(task);
        }
    }

    public PhotoCaptionPanel getPhotoCaptionPanel()
    {
        if (captionPanel == null)
        {
            captionPanel = new PhotoCaptionPanel();
        }
        captionPanel.reset();
        return captionPanel;
    }

    public String getPhotoCaption()
    {
        if (captionPanel == null)
        {
            captionPanel = new PhotoCaptionPanel();
            return "";
        }
        return captionPanel.getCaption();
    }

    public class PhotoCaptionPanel extends JPanel 
    {

        private JLabel captionLbl;
        private JTextPane captionTxt;
        private JScrollPane captionSp;

        public PhotoCaptionPanel()
        {
            super(new GridBagLayout());

            setBorder(new TitledBorder("Photo Caption:"));

            GridBagConstraints lastConstraints = new GridBagConstraints();
            lastConstraints.fill = GridBagConstraints.HORIZONTAL;
            lastConstraints.anchor = GridBagConstraints.NORTHWEST;
            lastConstraints.weightx = 1.0;
            lastConstraints.gridwidth = GridBagConstraints.REMAINDER;
            lastConstraints.insets = new Insets(5, 5, 5, 5);

            GridBagConstraints labelConstraints = (GridBagConstraints) lastConstraints.clone();
            labelConstraints.weightx = 0.0;
            labelConstraints.gridwidth = 1;

            captionLbl = new JLabel("Caption:");
            add(captionLbl, labelConstraints);

            captionTxt = new JTextPane();
            captionTxt.setPreferredSize(new Dimension(300, 200));

            captionSp = new JScrollPane(captionTxt, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            add(captionSp, lastConstraints);
        }

        public void reset()
        {
            captionTxt.setText("");
        }

        public String getCaption()
        {
            return captionTxt.getText();
        }
        
    }

}
