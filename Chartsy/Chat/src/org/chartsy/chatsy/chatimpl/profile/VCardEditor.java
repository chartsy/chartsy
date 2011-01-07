package org.chartsy.chatsy.chatimpl.profile;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.packet.VCard;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.component.TitlePanel;
import org.chartsy.chatsy.chat.ui.VCardViewer;
import org.chartsy.chatsy.chat.ui.status.StatusBar;
import org.chartsy.chatsy.chat.util.GraphicUtils;
import org.chartsy.chatsy.chat.util.ModelUtil;
import org.chartsy.chatsy.chat.util.log.Log;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.MalformedURLException;
import org.openide.util.ImageUtilities;

public class VCardEditor
{

	private final ImageIcon defaultAvatar = ImageUtilities.loadImageIcon("org/chartsy/chatsy/resources/default-avatar-64.png", true);
    private BusinessPanel businessPanel;
    private PersonalPanel personalPanel;
    private HomePanel homePanel;
    private AvatarPanel avatarPanel;
    private JLabel avatarLabel;

    public void editProfile(final VCard vCard, JComponent parent)
	{
        final JTabbedPane tabbedPane = new JTabbedPane();
        personalPanel = new PersonalPanel();
        personalPanel.showJID(false);
        tabbedPane.addTab("Primary", personalPanel);
        businessPanel = new BusinessPanel();
        tabbedPane.addTab("Work", businessPanel);
        homePanel = new HomePanel();
        tabbedPane.addTab("Home", homePanel);
        avatarPanel = new AvatarPanel();
        tabbedPane.addTab("Display Image", avatarPanel);
        buildUI(vCard);

        final JOptionPane pane;
        final JDialog dlg;
        TitlePanel titlePanel;
        ImageIcon icon = VCardManager.getAvatarIcon(vCard);
        if (icon == null)
            icon = defaultAvatar;
        titlePanel = new TitlePanel("Contact Details", "", icon, true);

        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        Object[] options = {"Save", "Cancel"};
        pane = new JOptionPane(
			tabbedPane,
			JOptionPane.PLAIN_MESSAGE,
			JOptionPane.OK_CANCEL_OPTION,
			ChatsyManager.getApplicationImage(),
			options,
			options[0]);
        mainPanel.add(pane, BorderLayout.CENTER);

        JOptionPane p = new JOptionPane();
        dlg = p.createDialog(parent, "Contact Details");
        dlg.setModal(false);

        dlg.pack();
        dlg.setSize(600, 400);
        dlg.setResizable(true);
        dlg.setContentPane(mainPanel);
        dlg.setLocationRelativeTo(parent);

        PropertyChangeListener changeListener = new PropertyChangeListener()
		{
            public void propertyChange(PropertyChangeEvent e)
			{
                String value = (String)pane.getValue();
                if ("Cancel".equals(value))
				{
                    pane.removePropertyChangeListener(this);
                    dlg.dispose();
                }
                else if ("Save".equals(value))
				{
                    pane.removePropertyChangeListener(this);
                    dlg.dispose();
                    saveVCard();
                }
            }
        };

        pane.addPropertyChangeListener(changeListener);
        avatarPanel.setParentDialog(dlg);
        dlg.setVisible(true);
        dlg.toFront();
        dlg.requestFocus();
        personalPanel.focus();
    }

    public void viewFullProfile(final VCard vCard, JComponent parent)
	{
        final JTabbedPane tabbedPane = new JTabbedPane();
        personalPanel = new PersonalPanel();
        personalPanel.allowEditing(false);
        personalPanel.showJID(false);
        tabbedPane.addTab("Primary", personalPanel);

        businessPanel = new BusinessPanel();
        businessPanel.allowEditing(false);
        tabbedPane.addTab("Work", businessPanel);

        homePanel = new HomePanel();
        homePanel.allowEditing(false);
        tabbedPane.addTab("Home", homePanel);

        avatarPanel = new AvatarPanel();
        avatarPanel.allowEditing(false);
        tabbedPane.addTab("Display Image", avatarPanel);

        buildUI(vCard);

        final JOptionPane pane;
        final JDialog dlg;

        TitlePanel titlePanel;

        ImageIcon icon = VCardManager.getAvatarIcon(vCard);
        if (icon == null)
            icon = defaultAvatar;

        titlePanel = new TitlePanel("Profile Information", "", icon, true);

        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        Object[] options = {"Close"};
        pane = new JOptionPane(
			tabbedPane,
			JOptionPane.PLAIN_MESSAGE,
			JOptionPane.OK_CANCEL_OPTION,
			ChatsyManager.getApplicationImage(),
			options,
			options[0]);
        mainPanel.add(pane, BorderLayout.CENTER);

        JOptionPane p = new JOptionPane();
        dlg = p.createDialog(parent, "Profile Information");
        dlg.setModal(false);

        dlg.pack();
        dlg.setSize(600, 400);
        dlg.setResizable(true);
        dlg.setContentPane(mainPanel);
        dlg.setLocationRelativeTo(parent);

        PropertyChangeListener changeListener = new PropertyChangeListener()
		{
            public void propertyChange(PropertyChangeEvent e)
			{
                Object o = pane.getValue();
                if (o instanceof Integer)
				{
                    pane.removePropertyChangeListener(this);
                    dlg.dispose();
                    return;
                }
                String value = (String)pane.getValue();
                if ("Close".equals(value))
				{
                    pane.removePropertyChangeListener(this);
                    dlg.dispose();
                }
            }
        };

        pane.addPropertyChangeListener(changeListener);

        dlg.setVisible(true);
        dlg.toFront();
        dlg.requestFocus();

        personalPanel.focus();
    }

    public void displayProfile(final String jid, VCard vcard, JComponent parent)
	{
        VCardViewer viewer = new VCardViewer(jid);
        final JFrame dlg = new JFrame(jid + "'s Profile");

        avatarLabel = new JLabel();
        avatarLabel.setHorizontalAlignment(JButton.RIGHT);
        avatarLabel.setBorder(BorderFactory.createBevelBorder(0, Color.white, Color.lightGray));
        
        Object[] options = {"View Full Profile", "Close"};
        final JOptionPane pane = new JOptionPane(
			viewer, JOptionPane.PLAIN_MESSAGE,
			JOptionPane.OK_CANCEL_OPTION,
			ChatsyManager.getApplicationImage(),
			options,
			options[0]);

        dlg.pack();
        dlg.setSize(350, 250);
        dlg.setResizable(true);
        dlg.setContentPane(pane);
        dlg.setLocationRelativeTo(parent);

        PropertyChangeListener changeListener = new PropertyChangeListener()
		{
            public void propertyChange(PropertyChangeEvent e)
			{
                if (pane.getValue() instanceof Integer)
				{
                    pane.removePropertyChangeListener(this);
                    dlg.dispose();
                    return;
                }
                String value = (String)pane.getValue();
                if ("Close".equals(value))
				{
                    pane.removePropertyChangeListener(this);
                    dlg.dispose();
                }
                else if ("View Full Profile".equals(value))
				{
                    pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                    ChatsyManager.getVCardManager().viewFullProfile(jid, pane);
                }
            }
        };

        pane.addPropertyChangeListener(changeListener);
        dlg.addKeyListener(new KeyAdapter()
		{
            public void keyPressed(KeyEvent keyEvent)
			{
                if (keyEvent.getKeyChar() == KeyEvent.VK_ESCAPE)
                    dlg.dispose();
            }
        });

        dlg.setVisible(true);
        dlg.toFront();
        dlg.requestFocus();
    }

    private void buildUI(VCard vcard)
	{
        personalPanel.setFirstName(vcard.getFirstName());
        personalPanel.setMiddleName(vcard.getMiddleName());
        personalPanel.setLastName(vcard.getLastName());
        personalPanel.setEmailAddress(vcard.getEmailHome());
        personalPanel.setNickname(vcard.getNickName());
        personalPanel.setJID(vcard.getJabberId());

        businessPanel.setCompany(vcard.getOrganization());
        businessPanel.setDepartment(vcard.getOrganizationUnit());
        businessPanel.setStreetAddress(vcard.getAddressFieldWork("STREET"));
        businessPanel.setCity(vcard.getAddressFieldWork("LOCALITY"));
        businessPanel.setState(vcard.getAddressFieldWork("REGION"));
        businessPanel.setZipCode(vcard.getAddressFieldWork("PCODE"));
        businessPanel.setCountry(vcard.getAddressFieldWork("CTRY"));
        businessPanel.setJobTitle(vcard.getField("TITLE"));
        businessPanel.setPhone(vcard.getPhoneWork("VOICE"));
        businessPanel.setFax(vcard.getPhoneWork("FAX"));
        businessPanel.setPager(vcard.getPhoneWork("PAGER"));
        businessPanel.setMobile(vcard.getPhoneWork("CELL"));
        businessPanel.setWebPage(vcard.getField("URL"));

        homePanel.setStreetAddress(vcard.getAddressFieldHome("STREET"));
        homePanel.setCity(vcard.getAddressFieldHome("LOCALITY"));
        homePanel.setState(vcard.getAddressFieldHome("REGION"));
        homePanel.setZipCode(vcard.getAddressFieldHome("PCODE"));
        homePanel.setCountry(vcard.getAddressFieldHome("CTRY"));
        homePanel.setPhone(vcard.getPhoneHome("VOICE"));
        homePanel.setFax(vcard.getPhoneHome("FAX"));
        homePanel.setPager(vcard.getPhoneHome("PAGER"));
        homePanel.setMobile(vcard.getPhoneHome("CELL"));

        byte[] bytes = vcard.getAvatar();
        if (bytes != null && bytes.length > 0)
		{
            ImageIcon icon = new ImageIcon(bytes);
            avatarPanel.setAvatar(icon);
            avatarPanel.setAvatarBytes(bytes);
            if (avatarLabel != null)
			{
                icon = GraphicUtils.scaleImageIcon(icon, 48, 48);
                avatarLabel.setIcon(icon);
            }
        }
    }

    private void saveVCard()
	{
        final VCard vcard = new VCard();

        vcard.setFirstName(personalPanel.getFirstName());
        vcard.setLastName(personalPanel.getLastName());
        vcard.setMiddleName(personalPanel.getMiddleName());
        vcard.setEmailHome(personalPanel.getEmailAddress());
        vcard.setNickName(personalPanel.getNickname());

        vcard.setOrganization(businessPanel.getCompany());
        vcard.setAddressFieldWork("STREET", businessPanel.getStreetAddress());
        vcard.setAddressFieldWork("LOCALITY", businessPanel.getCity());
        vcard.setAddressFieldWork("REGION", businessPanel.getState());
        vcard.setAddressFieldWork("PCODE", businessPanel.getZipCode());
        vcard.setAddressFieldWork("CTRY", businessPanel.getCountry());
        vcard.setField("TITLE", businessPanel.getJobTitle());
        vcard.setOrganizationUnit(businessPanel.getDepartment());
        vcard.setPhoneWork("VOICE", businessPanel.getPhone());
        vcard.setPhoneWork("FAX", businessPanel.getFax());
        vcard.setPhoneWork("PAGER", businessPanel.getPager());
        vcard.setPhoneWork("CELL", businessPanel.getMobile());
        vcard.setField("URL", businessPanel.getWebPage());

        vcard.setAddressFieldHome("STREET", homePanel.getStreetAddress());
        vcard.setAddressFieldHome("LOCALITY", homePanel.getCity());
        vcard.setAddressFieldHome("REGION", homePanel.getState());
        vcard.setAddressFieldHome("PCODE", homePanel.getZipCode());
        vcard.setAddressFieldHome("CTRY", homePanel.getCountry());
        vcard.setPhoneHome("VOICE", homePanel.getPhone());
        vcard.setPhoneHome("FAX", homePanel.getFax());
        vcard.setPhoneHome("PAGER", homePanel.getPager());
        vcard.setPhoneHome("CELL", homePanel.getMobile());

        final File avatarFile = avatarPanel.getAvatarFile();
        byte[] avatarBytes = avatarPanel.getAvatarBytes();

        if (avatarFile != null)
		{
            try
			{
                ImageIcon icon = new ImageIcon(avatarFile.toURI().toURL());
                Image image = icon.getImage();
                image = image.getScaledInstance(-1, 48, Image.SCALE_SMOOTH);
                avatarBytes = GraphicUtils.getBytesFromImage(image);
            }
            catch (MalformedURLException e)
			{
                Log.error("Unable to set avatar.", e);
            }
        }

        if (avatarBytes != null)
            vcard.setAvatar(avatarBytes);

        try
		{
            final VCardManager vcardManager = ChatsyManager.getVCardManager();
            vcardManager.setPersonalVCard(vcard);

            vcard.save(ChatsyManager.getConnection());

			if (avatarBytes != null)
			{
                Presence presence = ChatsyManager.getWorkspace().getStatusBar().getPresence();
                Presence newPresence = new Presence(presence.getType(), presence.getStatus(), presence.getPriority(), presence.getMode());
                ChatsyManager.getSessionManager().changePresence(newPresence);
                StatusBar statusBar = ChatsyManager.getWorkspace().getStatusBar();
                statusBar.setAvatar(GraphicUtils.scale(new ImageIcon(vcard.getAvatar()), 64, 64));
            }
            else
			{
                String firstName = vcard.getFirstName();
                String lastName = vcard.getLastName();
                StatusBar statusBar = ChatsyManager.getWorkspace().getStatusBar();
                if (ModelUtil.hasLength(firstName) && ModelUtil.hasLength(lastName))
                    statusBar.setNickname(firstName + " " + lastName);
                else if (ModelUtil.hasLength(firstName))
                    statusBar.setNickname(firstName);
                statusBar.setAvatar(null);
            }

            ChatsyManager.getVCardManager().notifyVCardListeners();
        }
        catch (XMPPException e)
		{
        }
    }


}


