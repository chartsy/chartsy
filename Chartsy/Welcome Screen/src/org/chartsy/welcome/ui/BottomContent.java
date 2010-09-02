package org.chartsy.welcome.ui;

import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.chartsy.main.managers.ProxyManager;
import org.chartsy.welcome.content.Constants;
import org.chartsy.welcome.content.DesktopUtil;
import org.openide.awt.StatusDisplayer;
import org.openide.util.NbBundle;

/**
 *
 * @author Viorel
 */
public final class BottomContent extends JPanel
	implements Constants, MouseListener, ActionListener
{

	private static final Logger LOG
		= Logger.getLogger(BottomContent.class.getPackage().getName());

	private String url = "";
    private Random random = new Random();
    private String cookieName;
    private String cookieValue;
    private String cookieDomain;
    private String cookiePath;
    private Date cookieExpires;
    private boolean cookieSecure;

	private JButton bottomLink;

	public BottomContent()
	{
		super(new FlowLayout(FlowLayout.CENTER));
        setOpaque(false);

        bottomLink = new JButton();
        bottomLink.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        bottomLink.setCursor(Cursor.getPredefinedCursor(12));
        bottomLink.setHorizontalAlignment(2);
        bottomLink.addMouseListener((MouseListener) this);
        bottomLink.setMargin(new Insets(0, 0, 0, 0));
        bottomLink.setBorderPainted(false);
        bottomLink.setFocusPainted(false);
        bottomLink.setContentAreaFilled(false);
        bottomLink.addActionListener((ActionListener) this);

        int i = getRandomNumber();
        Image image = getImageFromURL(getImageURL(i));
        if (image != null)
		{
            url = getURL(i);
            ImageIcon imageIcon = new ImageIcon(image);

            bottomLink.setIcon(imageIcon);
            bottomLink.setPressedIcon(imageIcon);
        }

        add(bottomLink);
	}

	private int getRandomNumber()
	{
        int n = 1000;
        int rand = random.nextInt(n);
        return Math.abs(rand);
    }

    private String getURL(int i)
	{
		return NbBundle.getMessage(BottomContent.class, "URL_BannerLink", Integer.toString(i));
    }

    private String getImageURL(int i)
	{
        return NbBundle.getMessage(BottomContent.class, "URL_BannerImageLink", Integer.toString(i));
    }

    @Override public void mouseClicked(MouseEvent e)
	{}

    @Override public void mousePressed(MouseEvent e)
	{}

    @Override public void mouseReleased(MouseEvent e)
	{}

    @Override public void mouseEntered(MouseEvent e)
	{
		StatusDisplayer.getDefault().setStatusText(url);
	}

    @Override public void mouseExited(MouseEvent e)
	{
		StatusDisplayer.getDefault().setStatusText("");
	}

    @Override public void actionPerformed(ActionEvent e)
	{
        try
		{
            bottomLink.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            HttpClient client = ProxyManager.getDefault().getHttpClient();
            Cookie cookie = new Cookie(cookieDomain, cookieName, cookieValue, cookiePath, cookieExpires, cookieSecure);
            client.getState().addCookie(cookie);
            HttpMethod method = new GetMethod(url.toString());
            method.setFollowRedirects(true);
            client.executeMethod(method);

			String uri = method.getURI().getHost() + (method.getURI().getPath().equals("/") ? "" : method.getURI().getPath());
			uri = (uri.startsWith("http://")) ? uri : "http://" + uri;
			DesktopUtil.browse(uri);

            method.setFollowRedirects(false);
            method.releaseConnection();
            bottomLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } 
		catch (Exception ex)
		{
            LOG.log(Level.SEVERE, "", ex);
        }
    }

    public Image getImageFromURL(String urlString)
	{
        try
		{
            HttpClient client = ProxyManager.getDefault().getHttpClient();
            HttpMethod method = new GetMethod(urlString);
            client.executeMethod(method);

            Cookie[] cookies = client.getState().getCookies();
            for (int i = 0; i < cookies.length; i++)
			{
                if (cookies[i].getName().equals("OAVARS[acaa7ff1]"))
				{
                    cookieName = cookies[i].getName();
                    cookieValue = cookies[i].getValue();
                    cookieDomain = cookies[i].getDomain();
                    cookiePath = cookies[i].getPath();
                    cookieExpires = cookies[i].getExpiryDate();
                    cookieSecure = cookies[i].getSecure();
                }
            }

            Image image = ImageIO.read(method.getResponseBodyAsStream());
            method.releaseConnection();
            return image;
        }
		catch (Exception ex)
		{
			LOG.log(Level.SEVERE, "", ex);
        }

        return null;
    }

}
