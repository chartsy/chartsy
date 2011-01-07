package org.chartsy.chatsy.chatimpl.profile;

import org.jivesoftware.smack.PacketInterceptor;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.ui.ContactItem;
import org.chartsy.chatsy.chat.util.Base64;
import org.chartsy.chatsy.chat.util.GraphicUtils;
import org.chartsy.chatsy.chat.util.ModelUtil;
import org.chartsy.chatsy.chat.util.SwingWorker;
import org.chartsy.chatsy.chat.util.TaskEngine;
import org.chartsy.chatsy.chat.util.log.Log;
import org.chartsy.chatsy.chatimpl.plugin.manager.Enterprise;
import org.chartsy.chatsy.chatimpl.profile.ext.JabberAvatarExtension;
import org.chartsy.chatsy.chatimpl.profile.ext.VCardUpdateExtension;
import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

public class VCardManager
{

	private static final Logger LOG = Logger.getLogger(VCardManager.class.getName());
    private VCard personalVCard;
    private Map<String, VCard> vcards = new HashMap<String, VCard>();
    private boolean vcardLoaded;
    private File imageFile;
    private final VCardEditor editor;
    private File vcardStorageDirectory;
    final MXParser parser;
    private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();
    private File contactsDir;
    private List<VCardListener> listeners = new ArrayList<VCardListener>();

    public VCardManager()
	{
        parser = new MXParser();
        try
		{
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        }
        catch (XmlPullParserException e)
		{
            LOG.log(Level.SEVERE, "", e);
        }

        imageFile = new File(ChatsyManager.getUserDirectory(), "personal.png");
        personalVCard = new VCard();
        vcardStorageDirectory = new File(ChatsyManager.getUserDirectory(), "vcards");
		if (!vcardStorageDirectory.exists())
			vcardStorageDirectory.mkdirs();
        contactsDir = new File(ChatsyManager.getUserDirectory(), "contacts");
		if (!contactsDir.exists())
			contactsDir.mkdirs();

        initializeUI();
		
        PacketFilter presenceFilter = new PacketTypeFilter(Presence.class);
        ChatsyManager.getConnection().addPacketInterceptor(new PacketInterceptor()
		{
            public void interceptPacket(Packet packet)
			{
                Presence newPresence = (Presence)packet;
                VCardUpdateExtension update = new VCardUpdateExtension();
                JabberAvatarExtension jax = new JabberAvatarExtension();

                PacketExtension updateExt = newPresence.getExtension(update.getElementName(), update.getNamespace());
                PacketExtension jabberExt = newPresence.getExtension(jax.getElementName(), jax.getNamespace());

                if (updateExt != null)
                    newPresence.removeExtension(updateExt);
                if (jabberExt != null)
                    newPresence.removeExtension(jabberExt);

                if (personalVCard != null)
				{
                    byte[] bytes = personalVCard.getAvatar();
                    if (bytes != null && bytes.length > 0)
					{
                        update.setPhotoHash(personalVCard.getAvatarHash());
                        jax.setPhotoHash(personalVCard.getAvatarHash());
                        newPresence.addExtension(update);
                        newPresence.addExtension(jax);
                    }
                }
            }
        }, presenceFilter);

        editor = new VCardEditor();
        startQueueListener();
    }

    private void startQueueListener()
	{
        final Runnable queueListener = new Runnable()
		{
            public void run()
			{
                while (true)
				{
                    try
					{
                        String jid = queue.take();
                        reloadVCard(jid);
                    }
                    catch (InterruptedException e)
					{
                        e.printStackTrace();
                        break;
                    }
                }
            }
        };
        TaskEngine.getInstance().submit(queueListener);
    }

    public void addToQueue(String jid)
	{
        if (!queue.contains(jid))
            queue.add(jid);
    }

    private void initializeUI()
	{
        boolean enabled = Enterprise.containsFeature(Enterprise.VCARD_FEATURE);
        if (!enabled) 
            return;
    }

    public void viewProfile(final String jid, final JComponent parent)
	{
        final SwingWorker vcardThread = new SwingWorker()
		{
            VCard vcard = new VCard();
            public Object construct()
			{
                vcard = getVCard(jid);
                return vcard;
            }
            public void finished()
			{
                if (vcard.getError() == null || vcard != null)
                    editor.displayProfile(jid, vcard, parent);
            }
        };
        vcardThread.start();
    }

    public void viewFullProfile(final String jid, final JComponent parent)
	{
        final SwingWorker vcardThread = new SwingWorker()
		{
            VCard vcard = new VCard();
            public Object construct()
			{
                vcard = getVCard(jid);
                return vcard;
            }
            public void finished()
			{
                if (vcard.getError() == null || vcard != null)
                    editor.viewFullProfile(vcard, parent);
            }
        };
        vcardThread.start();
    }

    public VCard getVCard()
	{
        if (!vcardLoaded)
		{
            try
			{
                personalVCard.load(ChatsyManager.getConnection());
                byte[] bytes = personalVCard.getAvatar();
                if (bytes != null && bytes.length > 0)
				{
                    ImageIcon icon = new ImageIcon(bytes);
                    icon = VCardManager.scale(icon);
                    if (icon != null && icon.getIconWidth() != -1)
					{
                        BufferedImage image = GraphicUtils.convert(icon.getImage());
                        ImageIO.write(image, "PNG", imageFile);
                    }
                }
            }
            catch (Exception e)
			{
                Log.error(e);
            }
            vcardLoaded = true;
        }
        return personalVCard;
    }

    public static ImageIcon getAvatarIcon(VCard vcard)
	{
        byte[] bytes = vcard.getAvatar();
        if (bytes != null && bytes.length > 0)
		{
            ImageIcon icon = new ImageIcon(bytes);
            return GraphicUtils.scaleImageIcon(icon, 40, 40);
        }
        return null;
    }

    public VCard getVCard(String jid)
	{
        return getVCard(jid, true);
    }

    public VCard getVCardFromMemory(String jid)
	{
        if (vcards.containsKey(jid))
            return vcards.get(jid);

        VCard vcard = loadFromFileSystem(jid);
        if (vcard == null)
		{
            addToQueue(jid);
            vcard = new VCard();
            vcard.setJabberId(jid);
        }

        return vcard;
    }

    public VCard getVCard(String jid, boolean useCache)
	{
        jid = StringUtils.parseBareAddress(jid);
        if (!vcards.containsKey(jid) || !useCache)
		{
            VCard vcard = new VCard();
            try
			{
                VCard localVCard = loadFromFileSystem(jid);
                if (localVCard != null)
				{
                    localVCard.setJabberId(jid);
                    vcards.put(jid, localVCard);
                    return localVCard;
                }

                vcard.load(ChatsyManager.getConnection(), jid);
                vcard.setJabberId(jid);
                if (vcard.getNickName() != null && vcard.getNickName().length() > 0)
                {
                	ContactItem item = ChatsyManager.getWorkspace().getContactList().getContactItemByJID(jid);
					if (item != null)
						item.setNickname(vcard.getNickName());
                }
                vcards.put(jid, vcard);
            }
            catch (XMPPException e)
			{
                vcard.setJabberId(jid);
                vcard.setError(new XMPPError(XMPPError.Condition.conflict));
                vcards.put(jid, vcard);
            }
        }
        return vcards.get(jid);
    }

    public VCard reloadVCard(String jid)
	{
        jid = StringUtils.parseBareAddress(jid);
        VCard vcard = new VCard();
        try
		{
            vcard.load(ChatsyManager.getConnection(), jid);
            vcard.setJabberId(jid);
            if (vcard.getNickName() != null && vcard.getNickName().length() > 0)
            {
            	ContactItem item = ChatsyManager.getWorkspace().getContactList().getContactItemByJID(jid);
            	item.setNickname(vcard.getNickName());
            }
            vcards.put(jid, vcard);
        }
        catch (XMPPException e)
		{
            vcard.setError(new XMPPError(XMPPError.Condition.conflict));
            vcards.put(jid, vcard);
        }
        return vcards.get(jid);
    }

    public void addVCard(String jid, VCard vcard)
	{
        vcard.setJabberId(jid);
        vcards.put(jid, vcard);
    }

    public static ImageIcon scale(ImageIcon icon)
	{
        Image avatarImage = icon.getImage();
        if (icon.getIconHeight() > 64 || icon.getIconWidth() > 64)
            avatarImage = avatarImage.getScaledInstance(-1, 64, Image.SCALE_SMOOTH);
        return new ImageIcon(avatarImage);
    }

    public URL getAvatar(String jid)
	{
        if (jid != null && StringUtils.parseBareAddress(ChatsyManager.getSessionManager().getJID()).equals(StringUtils.parseBareAddress(jid)))
		{
            if (imageFile.exists())
			{
                try
				{
                    return imageFile.toURI().toURL();
                }
                catch (MalformedURLException e)
				{
                    Log.error(e);
                }
            }
            else
			{
                return getClass().getResource("/org/chartsy/chatsy/resources/default-avatar-64.png");
            }
        }

        ContactItem item = ChatsyManager.getWorkspace().getContactList().getContactItemByJID(jid);
        URL avatarURL = null;
        if (item != null)
		{
            try
			{
                avatarURL = item.getAvatarURL();
            }
            catch (MalformedURLException e)
			{
                Log.error(e);
            }
        }

        if (avatarURL == null)
		{
            return getClass().getResource("/org/chartsy/chatsy/resources/default-avatar-64.png");
        }

        return avatarURL;
    }

    public VCard searchPhoneNumber(String phoneNumber)
	{
        for (VCard vcard : vcards.values())
		{
            String homePhone = getNumbersFromPhone(vcard.getPhoneHome("VOICE"));
            String workPhone = getNumbersFromPhone(vcard.getPhoneWork("VOICE"));
            String cellPhone = getNumbersFromPhone(vcard.getPhoneWork("CELL"));

            String query = getNumbersFromPhone(phoneNumber);
            if ((homePhone != null && homePhone.contains(query)) ||
                (workPhone != null && workPhone.contains(query)) ||
                (cellPhone != null && cellPhone.contains(query)))
                return vcard;
        }

        return null;
    }

    public static String getNumbersFromPhone(String number)
	{
        if (number == null)
            return null;
        number = number.replace("-", "");
        number = number.replace("(", "");
        number = number.replace(")", "");
        number = number.replace(" ", "");
        if (number.startsWith("1"))
            number = number.substring(1);
        return number;
    }

    public void setPersonalVCard(VCard vcard)
	{
        this.personalVCard = vcard;
    }

    public URL getAvatarURL(String jid)
	{
        VCard vcard = getVCard(jid, true);
        if (vcard != null)
		{
            String hash = vcard.getAvatarHash();
            if (!ModelUtil.hasLength(hash))
                return null;

            final File avatarFile = new File(contactsDir, hash);
            try
			{
                return avatarFile.toURI().toURL();
            }
            catch (MalformedURLException e)
			{
                Log.error(e);
            }
        }
        return null;
    }

    private VCard loadFromFileSystem(String jid)
	{
        String fileName = Base64.encodeBytes(jid.getBytes());

        final File vcardFile = new File(vcardStorageDirectory, fileName);
        if (!vcardFile.exists())
            return null;

        try
		{
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(vcardFile), "UTF-8"));
            VCardProvider provider = new VCardProvider();
            parser.setInput(in);
            VCard vcard = (VCard)provider.parseIQ(parser);

            String timestamp = vcard.getField("timestamp");
            if (timestamp != null)
			{
                long time = Long.parseLong(timestamp);
                long now = System.currentTimeMillis();
                long hour = (1000 * 60) * 60;
                if (now - time > hour)
                    addToQueue(jid);
            }

            vcard.setJabberId(jid);
            vcards.put(jid, vcard);
            return vcard;
        }
        catch (Exception e)
		{
            Log.warning("Unable to load vCard for " + jid, e);
        }

        return null;
    }

    public void addVCardListener(VCardListener listener)
	{
        listeners.add(listener);
    }

    public void removeVCardListener(VCardListener listener)
	{
        listeners.remove(listener);
    }

    protected void notifyVCardListeners()
	{
        for (VCardListener listener : listeners)
		{
            listener.vcardChanged(personalVCard);
        }
    }

}
