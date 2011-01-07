package org.chartsy.chatsy.chat.ui;

import org.jdesktop.swingx.calendar.DateUtils;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.packet.DelayInformation;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.plugin.ContextMenuListener;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TranscriptWindow extends ChatArea implements ContextMenuListener
{

	private final SimpleDateFormat notificationDateFormatter;
    private final SimpleDateFormat messageDateFormatter;
    private final String notificationDateFormat = ((SimpleDateFormat)SimpleDateFormat.getDateInstance(SimpleDateFormat.FULL)).toPattern();
    private final String messageDateFormat = ((SimpleDateFormat)SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)).toPattern();
    private Date lastUpdated;
    private Font defaultFont = new Font("Dialog", Font.PLAIN, 12);
    private Date lastPost;
    
    public TranscriptWindow()
	{
        setEditable(false);
        addMouseListener((MouseListener)this);
        addMouseMotionListener((MouseMotionListener)this);
		addContextMenuListener((ContextMenuListener)this);
        setDragEnabled(true);

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("Ctrl c"), "copy");
        getActionMap().put("copy", new AbstractAction("copy")
		{
			public void actionPerformed(ActionEvent evt)
			{
                StringSelection stringSelection = new StringSelection(getSelectedText());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            }
        });
        notificationDateFormatter = new SimpleDateFormat(notificationDateFormat);
        messageDateFormatter = new SimpleDateFormat(messageDateFormat);
    }

    public void addComponent(Component component)
	{
        final StyledDocument doc = (StyledDocument)getDocument();
        Style style = doc.addStyle("StyleName", null);
        StyleConstants.setComponent(style, component);
        try
		{
            doc.insertString(doc.getLength(), "ignored text", style);
            doc.insertString(doc.getLength(), "\n", null);
        }
        catch (BadLocationException e)
		{
        }
    }

    public void insertMessage(String nickname, Message message, Color foreground)
	{
        insertMessage(nickname, message, foreground, Color.white);
    }
   
    public void insertMessage(String nickname, Message message, Color foreground, Color background)
	{
        for (TranscriptWindowInterceptor interceptor : ChatsyManager.getChatManager().getTranscriptWindowInterceptors())
		{
            boolean handled = interceptor.isMessageIntercepted(this, nickname, message);
            if (handled)
                return;
        }

        String body = message.getBody();
        try
		{
            DelayInformation inf = (DelayInformation)message.getExtension("x", "jabber:x:delay");
            if (inf != null)
                body = "(Offline) " + body;
			
            StyleConstants.setBold(styles, false);
            StyleConstants.setForeground(styles, foreground);
            StyleConstants.setBackground(styles, background);
            final Document doc = getDocument();
            styles.removeAttribute("link");

            StyleConstants.setFontSize(styles, defaultFont.getSize());
            doc.insertString(doc.getLength(), nickname + ": ", styles);
            StyleConstants.setBold(styles, false);

            StyleConstants.setForeground(styles, Color.black);
            setText(body);
            insertText("\n");
        }
        catch (BadLocationException e)
		{
        }
    }

    public void insertPrefixAndMessage(String prefix, String message, Color foreground)
	{
        try
		{
            StyleConstants.setBold(styles, false);
            StyleConstants.setForeground(styles, foreground);
            final Document doc = getDocument();
            styles.removeAttribute("link");

            StyleConstants.setFontSize(styles, defaultFont.getSize());
            if (prefix != null)
                doc.insertString(doc.getLength(), prefix + ": ", styles);

            StyleConstants.setBold(styles, false);
            StyleConstants.setForeground(styles, Color.black);
            setText(message);
            insertText("\n");
        }
        catch (BadLocationException e)
		{
        }
    }

    public synchronized void insertNotificationMessage(String message, Color foregroundColor)
	{
        try
		{
            StyleConstants.setBold(styles, false);
            StyleConstants.setForeground(styles, foregroundColor);
            final Document doc = getDocument();
            styles.removeAttribute("link");

            StyleConstants.setFontSize(styles, defaultFont.getSize());
            doc.insertString(doc.getLength(), "", styles);

            StyleConstants.setBackground(styles, Color.white);
            StyleConstants.setBold(styles, false);
            StyleConstants.setForeground(styles, foregroundColor);
            setText(message);
            insertText("\n");
            StyleConstants.setForeground(styles, Color.black);
        }
        catch (BadLocationException ex)
		{
        }
    }

    public synchronized void insertCustomText(String text, boolean bold, boolean underline, Color foreground)
	{
        try
		{
            StyleConstants.setBold(styles, true);
            StyleConstants.setForeground(styles, foreground);
            final Document doc = getDocument();
            styles.removeAttribute("link");

            StyleConstants.setFontSize(styles, defaultFont.getSize());
            doc.insertString(doc.getLength(), "", styles);

            StyleConstants.setBold(styles, bold);
            StyleConstants.setUnderline(styles, underline);
            StyleConstants.setForeground(styles, foreground);
            setText(text);
            insertText("\n");
            StyleConstants.setUnderline(styles, false);
            StyleConstants.setForeground(styles, Color.black);
        }
        catch (BadLocationException ex)
		{
        }
    }

    public Date getLastUpdated()
	{
        return lastUpdated;
    }

    public void insertHistoryMessage(String userid, String message, Date date)
	{
        try
		{
            String value;

            long lastPostTime = lastPost != null ? lastPost.getTime() : 0;
            long lastPostStartOfDay = DateUtils.startOfDayInMillis(lastPostTime);
            long newPostStartOfDay = DateUtils.startOfDayInMillis(date.getTime());

            int diff = DateUtils.getDaysDiff(lastPostStartOfDay, newPostStartOfDay);
            if (diff != 0)
                insertCustomText(notificationDateFormatter.format(date), true, true, Color.BLACK);

            value = "(" + messageDateFormatter.format(date) + ") ";
            value = value + userid + ": ";

            lastPost = date;

            StyleConstants.setBold(styles, false);
            StyleConstants.setForeground(styles, Color.BLACK);
            final Document doc = getDocument();
            styles.removeAttribute("link");

            StyleConstants.setFontSize(styles, defaultFont.getSize());
            doc.insertString(doc.getLength(), value, styles);

            StyleConstants.setBold(styles, false);
            StyleConstants.setForeground(styles, Color.gray);
            setText(message);
            StyleConstants.setForeground(styles, Color.BLACK);
            insertText("\n");
        }
        catch (BadLocationException ex)
		{
        }
    }

    public void showWindowDisabled()
	{
        final Document document = getDocument();
        final SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setForeground(attrs, Color.LIGHT_GRAY);

        final int length = document.getLength();
        StyledDocument styledDocument = getStyledDocument();
        styledDocument.setCharacterAttributes(0, length, attrs, false);
    }

    public void saveTranscript(String fileName, List<Message> transcript, String headerData)
	{
    }

    public void cleanup()
	{
        super.releaseResources();
        clear();
        removeMouseListener(this);
        removeMouseMotionListener(this);
        removeContextMenuListener(this);
        getActionMap().remove("copy");
    }

    public void setFont(Font font)
	{
        this.defaultFont = font;
    }

    public Font getFont()
	{
        return defaultFont;
    }

    public void poppingUp(final Object object, JPopupMenu popup)
	{
    }

    public void poppingDown(JPopupMenu popup)
	{
    }

    public boolean handleDefaultAction(MouseEvent e)
	{
        return false;
    }
	
}
