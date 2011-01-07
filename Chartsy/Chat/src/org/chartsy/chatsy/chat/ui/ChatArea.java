package org.chartsy.chatsy.chat.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.plugin.ContextMenuListener;
import org.chartsy.chatsy.chat.util.BrowserLauncher;
import org.chartsy.chatsy.chat.util.ModelUtil;

public class ChatArea extends JTextPane implements MouseListener, MouseMotionListener, ActionListener
{

    public final SimpleAttributeSet styles = new SimpleAttributeSet();
    public static final Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);
    public static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
    private int fontSize = 12;
    private List<ContextMenuListener> contextMenuListener = new ArrayList<ContextMenuListener>();
    private JPopupMenu popup;

    private JMenuItem cutMenu;
    private JMenuItem copyMenu;
    private JMenuItem pasteMenu;
    private JMenuItem selectAll;

    private List<LinkInterceptor> interceptors = new ArrayList<LinkInterceptor>();
    protected boolean forceEmoticons = false;

    public ChatArea()
	{
        setFontSize(fontSize);
		setFont(new Font("Dialog", Font.PLAIN, fontSize));

		cutMenu = new JMenuItem("Cut");
        cutMenu.addActionListener((ActionListener)this);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("Ctrl x"), "cut");
        getActionMap().put("cut", new AbstractAction("cut")
		{
			public void actionPerformed(ActionEvent evt)
			{
                cutAction();
            }
        });

		copyMenu = new JMenuItem("Copy");
        copyMenu.addActionListener((ActionListener)this);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("Ctrl c"), "copy");
        getActionMap().put("copy", new AbstractAction("copy")
		{
			public void actionPerformed(ActionEvent evt)
			{
                ChatsyManager.setClipboard(getSelectedText());
            }
        });

		pasteMenu = new JMenuItem("Paste");
        pasteMenu.addActionListener((ActionListener)this);
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("Ctrl v"), "paste");
        getActionMap().put("paste", new AbstractAction("paste")
		{
			public void actionPerformed(ActionEvent evt)
			{
                pasteAction();
            }
        });

		selectAll = new JMenuItem("Select All");
        selectAll.addActionListener((ActionListener)this);
    }

    public void setText(String message)
	{
        setCursor(HAND_CURSOR);
        if (ModelUtil.hasLength(message))
		{
            try
			{
                insert(message);
            }
            catch (BadLocationException e)
			{
            }
        }
    }

    public void clear()
	{
        super.setText("");
        StyleConstants.setUnderline(styles, false);
        StyleConstants.setBold(styles, false);
        StyleConstants.setItalic(styles, false);
        setCharacterAttributes(styles, false);
    }

    public void insert(String text) throws BadLocationException
	{
        boolean bold = false;
        boolean italic = false;
        boolean underlined = false;

        final StringTokenizer tokenizer = new StringTokenizer(text, " \n \t", true);
        while (tokenizer.hasMoreTokens())
		{
            String textFound = tokenizer.nextToken();
            if ((textFound.startsWith("http://")
				|| textFound.startsWith("ftp://")
				|| textFound.startsWith("https://")
				|| textFound.startsWith("www."))
				&& textFound.indexOf(".") > 1)
                insertLink(textFound);
            else if (textFound.startsWith("\\\\")
				|| (textFound.indexOf("://") > 0
				&& textFound.indexOf(".") < 1))
                insertAddress(textFound);
            else 
                insertText(textFound);
        }

        StyleConstants.setBold(styles, bold);
        StyleConstants.setItalic(styles, italic);
        StyleConstants.setUnderline(styles, underlined);
    }

    public void insertText(String text) throws BadLocationException
	{
        final Document doc = getDocument();
        styles.removeAttribute("link");
        doc.insertString(doc.getLength(), text, styles);
    }

    public void insertText(String text, Color color) throws BadLocationException
	{
        final Document doc = getDocument();
        StyleConstants.setForeground(styles, color);
        doc.insertString(doc.getLength(), text, styles);
    }

    public void insertLink(String link) throws BadLocationException
	{
        final Document doc = getDocument();
		styles.addAttribute("link", link);
        StyleConstants.setForeground(styles, Color.decode("0x336699"));
        StyleConstants.setUnderline(styles, true);
        doc.insertString(doc.getLength(), link, styles);

        StyleConstants.setUnderline(styles, false);
        StyleConstants.setForeground(styles, Color.decode("0x111111"));
        styles.removeAttribute("link");
        setCharacterAttributes(styles, false);
    }
    
    public void insertAddress(String address) throws BadLocationException
	{
        final Document doc = getDocument();
        styles.addAttribute("link", address);

        StyleConstants.setForeground(styles, Color.decode("0x009900"));
        StyleConstants.setUnderline(styles, true);
        doc.insertString(doc.getLength(), address, styles);
		
        StyleConstants.setUnderline(styles, false);
        StyleConstants.setForeground(styles, Color.decode("0x111111"));
        styles.removeAttribute("link");
        setCharacterAttributes(styles, false);
    }

    public boolean insertImage(String imageKey)
	{
        return false;
    }

    public void insertHorizontalLine() {
        try
		{
            insertText("\n");
        }
        catch (BadLocationException e)
		{
        }
    }

    public void setBold()
	{
        final Element element = getStyledDocument().getCharacterElement(getCaretPosition() - 1);
        if (element != null)
		{
            AttributeSet as = element.getAttributes();
            boolean isBold = StyleConstants.isBold(as);
            StyleConstants.setBold(styles, !isBold);
            try
			{
                setCharacterAttributes(styles, true);
            }
            catch (Exception ex)
			{
            }
        }
    }

    public void setItalics()
	{
        final Element element = getStyledDocument().getCharacterElement(getCaretPosition() - 1);
        if (element != null)
		{
            AttributeSet as = element.getAttributes();
            boolean isItalic = StyleConstants.isItalic(as);
            StyleConstants.setItalic(styles, !isItalic);
            try
			{
                setCharacterAttributes(styles, true);
            }
            catch (Exception fontException)
			{
            }
        }
    }

    public void setUnderlined()
	{
        final Element element = getStyledDocument().getCharacterElement(getCaretPosition() - 1);
        if (element != null)
		{
            AttributeSet as = element.getAttributes();
            boolean isUnderlined = StyleConstants.isUnderline(as);
            StyleConstants.setUnderline(styles, !isUnderlined);
            try
			{
                setCharacterAttributes(styles, true);
            }
            catch (Exception underlineException)
			{
            }
        }
    }

    public void setFont(String font)
	{
        StyleConstants.setFontFamily(styles, font);
        try
		{
            setCharacterAttributes(styles, false);
        }
        catch (Exception fontException)
		{
        }
    }

    public void setFontSize(int size)
	{
        StyleConstants.setFontSize(styles, size);
        try
		{
            setCharacterAttributes(styles, false);
        }
        catch (Exception fontException)
		{
        }
        fontSize = size;
    }

    public void mouseClicked(MouseEvent e)
	{
        try
		{
            final int pos = viewToModel(e.getPoint());
            final Element element = getStyledDocument().getCharacterElement(pos);

            if (element != null)
			{
                final AttributeSet as = element.getAttributes();
                final Object o = as.getAttribute("link");

                if (o != null)
				{
                    try
					{
                        final String url = (String)o;
                        boolean handled = fireLinkInterceptors(e, url);
                        if (!handled)
                        	BrowserLauncher.openURL(url);
                    }
                    catch (Exception ioe)
					{
                    }
                }
            }
        }
        catch (Exception ex)
		{
        }
    }

    public void mousePressed(MouseEvent e)
	{
        if (e.isPopupTrigger())
            handlePopup(e);
    }

    public void mouseReleased(MouseEvent e)
	{
        if (e.isPopupTrigger())
            handlePopup(e);
    }

    public void mouseEntered(MouseEvent e)
	{
    }

    public void mouseExited(MouseEvent e)
	{
    }

    public void mouseDragged(MouseEvent e)
	{
    }

    public void mouseMoved(MouseEvent e)
	{
        checkForLink(e);
    }

    private void checkForLink(MouseEvent e)
	{
        try
		{
            final int pos = viewToModel(e.getPoint());
            final Element element = getStyledDocument().getCharacterElement(pos);

            if (element != null)
			{
                final AttributeSet as = element.getAttributes();
                final Object o = as.getAttribute("link");

                if (o != null)
                    setCursor(HAND_CURSOR);
                else
                    setCursor(DEFAULT_CURSOR);
            }
        }
        catch (Exception ex)
		{
        }
    }

    public String getMarkup()
	{
        final StringBuilder buf = new StringBuilder();
        final String text = getText();
        final StyledDocument doc = getStyledDocument();
        final Element rootElem = doc.getDefaultRootElement();

        if (text.trim().length() <= 0)
            return null;

        boolean endsInNewline = text.charAt(text.length() - 1) == '\n';
        for (int j = 0; j < rootElem.getElementCount(); j++)
		{
            final Element pElem = rootElem.getElement(j);

            for (int i = 0; i < pElem.getElementCount(); i++)
			{
                final Element e = pElem.getElement(i);
                final AttributeSet as = e.getAttributes();
                final boolean bold = StyleConstants.isBold(as);
                final boolean italic = StyleConstants.isItalic(as);
                final boolean underline = StyleConstants.isUnderline(as);
                int end = e.getEndOffset();

                if (end > text.length())
                    end = text.length();
                if (endsInNewline && end >= text.length() - 1)
                    end--;
                if (j == rootElem.getElementCount() - 1
                        && i == pElem.getElementCount() - 1)
                    end = text.length();

                final String current = text.substring(e.getStartOffset(), end);
                if (bold)
                    buf.append("[b]");
                if (italic)
                    buf.append("[i]");
                if (underline)
                    buf.append("[u]");

                final StringTokenizer tkn = new StringTokenizer(current, " ", true);
                while (tkn.hasMoreTokens())
				{
                    final String token = tkn.nextToken();
                    if (token.startsWith("http://") || token.startsWith("ftp://")
                            || token.startsWith("https://"))
                        buf.append("[url]").append(token).append("[/url]");
                    else if (token.startsWith("www"))
					{
                        buf.append("[url ");
                        buf.append("http://").append(token);
                        buf.append("]");
                        buf.append(token);
                        buf.append("[/url]");
                    }
                    else
                        buf.append(token);
                }

                if (underline)
                    buf.append("[/u]");
                if (italic)
                    buf.append("[/i]");
                if (bold)
                    buf.append("[/b]");
            }
        }

        return buf.toString();
    }

    private void handlePopup(MouseEvent e)
	{
        popup = new JPopupMenu();
        popup.add(cutMenu);
        popup.add(copyMenu);
        popup.add(pasteMenu);
        fireContextMenuListeners();
        popup.addSeparator();
        popup.add(selectAll);

        boolean textSelected = ModelUtil.hasLength(getSelectedText());
        String clipboard = ChatsyManager.getClipboard();
        cutMenu.setEnabled(textSelected && isEditable());
        copyMenu.setEnabled(textSelected);
        pasteMenu.setEnabled(ModelUtil.hasLength(clipboard) && isEditable());

        popup.show(this, e.getX(), e.getY());
    }

    public void addContextMenuListener(ContextMenuListener listener)
	{
        contextMenuListener.add(listener);
    }

    public void removeContextMenuListener(ContextMenuListener listener)
	{
        contextMenuListener.remove(listener);
    }

    private void fireContextMenuListeners()
	{
        for (ContextMenuListener listener : new ArrayList<ContextMenuListener>(contextMenuListener))
            listener.poppingUp(this, popup);
    }

    public void addLinkInterceptor(LinkInterceptor interceptor)
	{
        interceptors.add(interceptor);
    }

    public void removeLinkInterceptor(LinkInterceptor interceptor)
	{
        interceptors.remove(interceptor);
    }

    public boolean fireLinkInterceptors(MouseEvent event, String link)
	{
        for (LinkInterceptor linkInterceptor : new ArrayList<LinkInterceptor>(interceptors))
		{
            boolean handled = linkInterceptor.handleLink(event, link);
            if (handled)
                return true;
        }
        return false;
    }

    public void actionPerformed(ActionEvent e)
	{
        if (e.getSource() == cutMenu)
            cutAction();
        else if (e.getSource() == copyMenu)
            ChatsyManager.setClipboard(getSelectedText());
        else if (e.getSource() == pasteMenu)
            pasteAction();
        else if (e.getSource() == selectAll)
		{
            requestFocus();
            selectAll();
        }
    }

    private void cutAction()
	{
        String selectedText = getSelectedText();
        replaceSelection("");
        ChatsyManager.setClipboard(selectedText);
    }

    private void pasteAction()
	{
        String text = ChatsyManager.getClipboard();
        if (text != null)
            replaceSelection(text);
    }

    protected void releaseResources()
	{
        getActionMap().remove("copy");
        getActionMap().remove("cut");
        getActionMap().remove("paste");
    }

    public Boolean getForceEmoticons()
	{
        return forceEmoticons;
    }

    public void setForceEmoticons(Boolean forceEmoticons)
	{
        this.forceEmoticons = forceEmoticons;
    }
	
}