package org.chartsy.chatsy.chat.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jivesoftware.smackx.packet.VCard;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.UserManager;
import org.chartsy.chatsy.chat.util.GraphicUtils;
import org.chartsy.chatsy.chat.util.ModelUtil;
import org.chartsy.chatsy.chat.util.SwingWorker;
import org.chartsy.chatsy.chat.util.log.Log;
import org.openide.util.ImageUtilities;

public class VCardViewer extends JPanel
{

	private Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
    private Cursor LINK_CURSOR = new Cursor(Cursor.HAND_CURSOR);
    private final String jid;
    private final JLabel avatarImage;
    private String emailAddress = "";

    public VCardViewer(final String jid)
	{
        setLayout(new GridBagLayout());
        setOpaque(false);

        this.jid = jid;
        avatarImage = new JLabel();
        add(avatarImage, new GridBagConstraints(0, 0, 1, 3, 0.0, 1.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
			new Insets(5, 0, 5, 0), 0, 0));

        Image aImage = ImageUtilities.loadImage("org/chartsy/chatsy/resources/default-avatar-64.png", true);
        ImageIcon ico = new ImageIcon(aImage);
        avatarImage.setIcon(ico);

        final SwingWorker vcardLoader = new SwingWorker()
		{
            VCard vcard = null;
            public Object construct()
			{
                vcard = ChatsyManager.getVCardManager().getVCard(jid);
                return vcard;
            }
            public void finished()
			{
                if (vcard == null)
                    return;
                ImageIcon icon = null;
                byte[] bytes = vcard.getAvatar();
                if (bytes != null && bytes.length > 0)
				{
                    try
					{
                        icon = new ImageIcon(bytes);
                        Image aImage = icon.getImage();
                        aImage = aImage.getScaledInstance(-1, 48, Image.SCALE_SMOOTH);
                        icon = new ImageIcon(aImage);
                    }
                    catch (Exception e)
					{
                        Log.error(e);
                    }
                }
                else
				{
                    Image newImage = ImageUtilities.loadImage("org/chartsy/chatsy/resources/default-avatar-64.png", true);
					newImage = newImage.getScaledInstance(-1, 32, Image.SCALE_SMOOTH);
					icon = new ImageIcon(newImage);
                }

                if (icon != null && icon.getIconWidth() > 0)
				{
                    avatarImage.setIcon(icon);
                    avatarImage.setBorder(BorderFactory.createBevelBorder(0, Color.white, Color.lightGray));
                }

                vcard.setJabberId(jid);
                buildUI(vcard);
            }
        };
        vcardLoader.start();
    }

    private void buildUI(final VCard vcard)
	{
        avatarImage.addMouseListener(new MouseAdapter()
		{
            public void mouseClicked(MouseEvent mouseEvent)
			{
                if (mouseEvent.getClickCount() == 2)
                    ChatsyManager.getVCardManager().viewProfile(vcard.getJabberId(), avatarImage);
            }
        });

        String firstName = vcard.getFirstName();
        if (firstName == null)
            firstName = "";

        String lastName = vcard.getLastName();
        if (lastName == null)
            lastName = "";

        final JLabel usernameLabel = new JLabel();
        usernameLabel.setHorizontalTextPosition(JLabel.LEFT);
        usernameLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 15));

        usernameLabel.setForeground(Color.GRAY);
        if (ModelUtil.hasLength(firstName) && ModelUtil.hasLength(lastName))
            usernameLabel.setText(firstName + " " + lastName);
        else
		{
            String nickname = ChatsyManager.getUserManager().getUserNicknameFromJID(jid);
            usernameLabel.setText(UserManager.unescapeJID(nickname));
        }

        final Icon icon = ChatsyManager.getChatManager().getIconForContactHandler(vcard.getJabberId());
        if (icon != null)
            usernameLabel.setIcon(icon);

        add(usernameLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 0, 0), 0, 0));

        String title = vcard.getField("TITLE");
        if (ModelUtil.hasLength(title))
		{
            final JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 11));
            add(titleLabel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, 
				GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
				new Insets(0, 7, 0, 0), 0, 0));
        }

        if (ModelUtil.hasLength(vcard.getEmailHome()))
            emailAddress = vcard.getEmailHome();

        final Color linkColor = new Color(49, 89, 151);
        final String unselectedText = "<html><body><font color=" + GraphicUtils.toHTMLColor(linkColor) + "><u>" + emailAddress + "</u></font></body></html>";
        final String hoverText = "<html><body><font color=red><u>" + emailAddress + "</u></font></body></html>";
        final JLabel emailTime = new JLabel(unselectedText);
        emailTime.addMouseListener(new MouseAdapter()
		{
            public void mouseClicked(MouseEvent e)
			{
                startEmailClient(emailAddress);
            }
            public void mouseEntered(MouseEvent e)
			{
                emailTime.setText(hoverText);
                setCursor(LINK_CURSOR);
            }
            public void mouseExited(MouseEvent e)
			{
                emailTime.setText(unselectedText);
                setCursor(DEFAULT_CURSOR);
            }
        });
        add(emailTime, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
			new Insets(0, 7, 10, 0), 0, 0));

        final String jid = UserManager.unescapeJID(vcard.getJabberId());
        final JLabel jidLabel = new JLabel("<html><body>JID: <font color=" + GraphicUtils.toHTMLColor(linkColor) + "><u>" + jid + "</u></font></body></html>");
        jidLabel.setToolTipText("Click to copy jid to clipboard.");
        jidLabel.addMouseListener(new MouseAdapter()
		{
            public void mouseEntered(MouseEvent mouseEvent)
			{
                jidLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(MouseEvent mouseEvent)
			{
                jidLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
            public void mouseClicked(MouseEvent mouseEvent)
			{
                ChatsyManager.setClipboard(jid);
            }
        });
        add(jidLabel, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
			new Insets(0, 7, 2, 0), 0, 0));

        String homeNumber = vcard.getPhoneHome("VOICE");
        if (!ModelUtil.hasLength(homeNumber))
            homeNumber = "n/a";
        final JLabel homePhoneLabel = new JLabel("Home: " + homeNumber);
        add(homePhoneLabel, new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
			new Insets(0, 7, 2, 0), 0, 0));

        String workNumber = vcard.getPhoneWork("VOICE");
        if (!ModelUtil.hasLength(workNumber))
            workNumber = "n/a";
        final JLabel workPhoneLabel = new JLabel("Work: " + workNumber);
        add(workPhoneLabel, new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
			new Insets(0, 7, 2, 0), 0, 0));

        String cellNumber = vcard.getPhoneWork("CELL");
        if (!ModelUtil.hasLength(cellNumber))
            cellNumber = "n/a";
        final JLabel cellPhoneLabel = new JLabel("Cell: " + cellNumber);
        add(cellPhoneLabel, new GridBagConstraints(1, 6, 1, 1, 1.0, 0.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
			new Insets(0, 7, 2, 0), 0, 0));

        String company = vcard.getOrganization();
        final JLabel orgLabel = new JLabel("Company: " + company);
        add(orgLabel, new GridBagConstraints(1, 7, 1, 1, 1.0, 0.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
			new Insets(0, 7, 2, 0), 0, 0));
    }

    private void startEmailClient(String emailAddress)
	{
   		try
		{
			Desktop.getDesktop().mail(new URI("mailto:" + emailAddress));
		} 
		catch (IOException e)
		{
			Log.error("Can't open Mailer", e);
		} 
		catch (URISyntaxException e)
		{
			Log.error("URI Wrong", e);
		}
    }

}
