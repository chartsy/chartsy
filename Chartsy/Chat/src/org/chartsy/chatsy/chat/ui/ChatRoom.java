package org.chartsy.chatsy.chat.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.chartsy.chatsy.chat.ChatAreaSendField;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.component.RolloverButton;
import org.chartsy.chatsy.chat.plugin.ContextMenuListener;
import org.chartsy.chatsy.chat.util.GraphicUtils;
import org.openide.util.NbPreferences;

public abstract class ChatRoom extends JPanel
	implements ActionListener, PacketListener, DocumentListener, ConnectionListener, FocusListener, ContextMenuListener
{

	private final JPanel chatPanel;
    private final JSplitPane splitPane;
    private JSplitPane verticalSplit;

    private final JLabel notificationLabel;
    private final TranscriptWindow transcriptWindow;
    private final ChatAreaSendField chatAreaButton;
    private final ChatToolBar toolbar;
    private final JScrollPane textScroller;
    private final JPanel bottomPanel;
    private final JPanel editorBar;
    private JPanel chatWindowPanel;

    private int unreadMessageCount;
    private boolean mousePressed;
    private List<ChatRoomClosingListener> closingListeners = new CopyOnWriteArrayList<ChatRoomClosingListener>();
    private final List<String> packetIDList;
    private final List<MessageListener> messageListeners;
    private List<Message> transcript;
    private MouseAdapter transcriptWindowMouseListener;
    private KeyAdapter chatEditorKeyListener;

    protected ChatRoom()
	{
        chatPanel = new JPanel(new GridBagLayout());
        transcriptWindow = new TranscriptWindow();
        splitPane = new JSplitPane();
        packetIDList = new ArrayList<String>();
        notificationLabel = new JLabel();
        toolbar = new ChatToolBar();
        bottomPanel = new JPanel();

        messageListeners = new ArrayList<MessageListener>();
        transcript = new ArrayList<Message>();
        editorBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));

        transcriptWindowMouseListener = new MouseAdapter()
		{
            public void mouseClicked(MouseEvent e)
			{
                getChatInputEditor().requestFocus();
            }
            public void mouseReleased(MouseEvent e)
			{
                mousePressed = false;
                if (transcriptWindow.getSelectedText() == null)
                    getChatInputEditor().requestFocus();
            }
            public void mousePressed(MouseEvent e)
			{
                mousePressed = true;
            }
        };
        transcriptWindow.addMouseListener(transcriptWindowMouseListener);
		transcriptWindow.setBackground(Color.white);
        chatAreaButton = new ChatAreaSendField("Send");

        textScroller = new JScrollPane(transcriptWindow);
        textScroller.setBackground(transcriptWindow.getBackground());
        textScroller.getViewport().setBackground(Color.white);
		textScroller.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.lightGray));

        getChatInputEditor().setSelectedTextColor(Color.decode("0xffffff"));
        getChatInputEditor().setSelectionColor(Color.decode("0x336699"));
        init();
        getSplitPane().setRightComponent(null);
        getTranscriptWindow().addContextMenuListener(this);

        ChatsyManager.getConnection().addConnectionListener(this);
        addFocusListener(this);
    }

    private void init()
	{
        setLayout(new GridBagLayout());
		setBackground(Color.white);

        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setOneTouchExpandable(false);

        verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        add(verticalSplit, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, 
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0), 0, 0));
        verticalSplit.setBorder(BorderFactory.createEmptyBorder());
        verticalSplit.setOneTouchExpandable(false);
        verticalSplit.setTopComponent(splitPane);
        textScroller.setAutoscrolls(true);
        textScroller.getVerticalScrollBar().setBlockIncrement(50);
        textScroller.getVerticalScrollBar().setUnitIncrement(20);

        chatWindowPanel = new JPanel();
        chatWindowPanel.setLayout(new GridBagLayout());
        chatWindowPanel.add(textScroller, new GridBagConstraints(0, 10, 1, 1, 1.0, 1.0, 
			GridBagConstraints.WEST, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0), 0, 0));
        chatWindowPanel.setOpaque(false);
		
		chatPanel.add(chatWindowPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, 
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 5, 0, 5), 0, 0));
        splitPane.setLeftComponent(chatPanel);

        editorBar.setOpaque(false);
        chatPanel.setOpaque(false);

        bottomPanel.setOpaque(false);
        splitPane.setOpaque(false);
        bottomPanel.setLayout(new GridBagLayout());
        bottomPanel.add(chatAreaButton, new GridBagConstraints(0, 1, 5, 1, 1.0, 1.0, 
			GridBagConstraints.WEST, GridBagConstraints.BOTH,
			new Insets(0, 5, 5, 5), 0, 15));
        bottomPanel.add(editorBar, new GridBagConstraints(0, 0, 5, 1, 0.0, 0.0,
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(0, 5, 0, 5), 0, 0));

        bottomPanel.setBorder(BorderFactory.createEmptyBorder());
        verticalSplit.setOpaque(false);

        verticalSplit.setBottomComponent(bottomPanel);
        verticalSplit.setResizeWeight(1.0);
        verticalSplit.setDividerSize(2);

        chatAreaButton.getButton().addActionListener(this);
        getChatInputEditor().getDocument().addDocumentListener(this);
        chatEditorKeyListener = new KeyAdapter()
		{
            public void keyPressed(KeyEvent e)
			{
                checkForEnter(e);
            }
        };
        getChatInputEditor().addKeyListener(chatEditorKeyListener);
        getChatInputEditor().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ctrl F4"), "closeTheRoom");
        getChatInputEditor().getActionMap().put("closeTheRoom", new AbstractAction("closeTheRoom")
		{
			public void actionPerformed(ActionEvent evt)
			{
                closeChatRoom();
            }
        });
    }

    public void actionPerformed(ActionEvent e)
	{
        sendMessage();
        getChatInputEditor().clear();
        chatAreaButton.getButton().setEnabled(false);
    }

    protected abstract void sendMessage();
    protected abstract void sendMessage(String text);
    public abstract void sendMessage(Message message);

    public String getNickname()
	{
        return NbPreferences.root().node("/org/chartsy/chat").get("nickname", "");
    }

    public void insertMessage(Message message)
	{
        ChatsyManager.getChatManager().filterIncomingMessage(this, message);
        ChatsyManager.getChatManager().fireGlobalMessageReceievedListeners(this, message);
        addToTranscript(message, true);
        fireMessageReceived(message);
    }

    public void addToTranscript(Message message, boolean updateDate)
	{
        final Message newMessage = new Message();
        newMessage.setTo(message.getTo());
        newMessage.setFrom(message.getFrom());
        newMessage.setBody(message.getBody()); 
        newMessage.setProperty("date", new Date());
        transcript.add(newMessage);
        if (updateDate && transcriptWindow.getLastUpdated() != null)
            notificationLabel.setText("las msg received at "
				+ ChatsyManager.DATE_SECOND_FORMATTER.format(transcriptWindow.getLastUpdated()));
        scrollToBottom();
    }

    public void addToTranscript(String to, String from, String body, Date date)
	{
        final Message newMessage = new Message();
        newMessage.setTo(to);
        newMessage.setFrom(from);
        newMessage.setBody(body);
        newMessage.setProperty("date", date);
        transcript.add(newMessage);
    }

    public void scrollToBottom()
	{
        if (mousePressed)
            return;

        int lengthOfChat = transcriptWindow.getDocument().getLength();
        transcriptWindow.setCaretPosition(lengthOfChat);

        try
		{
            JScrollBar scrollBar = textScroller.getVerticalScrollBar();
            scrollBar.setValue(scrollBar.getMaximum());
        }
        catch (Exception e)
		{
        }
    }

    protected void checkForText(DocumentEvent e)
	{
        final int length = e.getDocument().getLength();
        if (length > 0)
            chatAreaButton.getButton().setEnabled(true);
        else
            chatAreaButton.getButton().setEnabled(false);
    }

    public void positionCursor()
	{
        getChatInputEditor().setCaretPosition(getChatInputEditor().getCaretPosition());
        chatAreaButton.getChatInputArea().requestFocusInWindow();
    }

    public abstract void leaveChatRoom();

    public void processPacket(Packet packet)
	{
    }

    public ChatInputEditor getChatInputEditor()
	{
        return chatAreaButton.getChatInputArea();
    }

    public TranscriptWindow getTranscriptWindow()
	{
        return transcriptWindow;
    }

    private void checkForEnter(KeyEvent e)
	{
        final KeyStroke keyStroke = KeyStroke.getKeyStroke(e.getKeyCode(), e.getModifiers());
        if (!keyStroke.equals(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.SHIFT_DOWN_MASK)) 
			&& e.getKeyChar() == KeyEvent.VK_ENTER)
		{
            e.consume();
            sendMessage();
            getChatInputEditor().setText("");
            getChatInputEditor().setCaretPosition(0);
        }
        else if (keyStroke.equals(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.SHIFT_DOWN_MASK)))
		{
            final Document document = getChatInputEditor().getDocument();
            try
			{
                document.insertString(getChatInputEditor().getCaretPosition(), "\n", null);
                getChatInputEditor().requestFocusInWindow();
                chatAreaButton.getButton().setEnabled(true);
            }
            catch (BadLocationException badLoc)
			{
            }
        }
    }

    public void addMessageListener(MessageListener listener)
	{
        messageListeners.add(listener);
    }

    public void removeMessageListener(MessageListener listener)
	{
        messageListeners.remove(listener);
    }

    private void fireMessageReceived(Message message)
	{
        for (MessageListener messageListener : messageListeners)
            messageListener.messageReceived(this, message);
    }

    protected void fireMessageSent(Message message)
	{
        for (MessageListener messageListener : messageListeners)
            messageListener.messageSent(this, message);
    }

    public List<Message> getTranscripts()
	{
        return transcript;
    }

    public void disableToolbar()
	{
        final int count = editorBar.getComponentCount();
        for (int i = 0; i < count; i++)
		{
            final Object o = editorBar.getComponent(i);
            if (o instanceof RolloverButton)
			{
                final RolloverButton rb = (RolloverButton)o;
                rb.setEnabled(false);
            }
        }
    }

    public void enableToolbar()
	{
        final int count = editorBar.getComponentCount();
        for (int i = 0; i < count; i++)
		{
            final Object o = editorBar.getComponent(i);
            if (o instanceof RolloverButton)
			{
                final RolloverButton rb = (RolloverButton)o;
                rb.setEnabled(true);
            }
        }
    }

    public void removeUpdate(DocumentEvent event)
	{
        checkForText(event);
    }

    public void changedUpdate(DocumentEvent docEvent)
	{
    }

    public JSplitPane getSplitPane()
	{
        return splitPane;
    }

    public JPanel getChatPanel()
	{
        return chatPanel;
    }

    public void closeChatRoom()
	{
        fireClosingListeners();
        getTranscriptWindow().removeContextMenuListener(this);
        getTranscriptWindow().removeMouseListener(transcriptWindowMouseListener);
        getChatInputEditor().removeKeyListener(chatEditorKeyListener);
        textScroller.getViewport().remove(transcriptWindow);
        ChatsyManager.getConnection().removeConnectionListener(this);

        packetIDList.clear();
        messageListeners.clear();
        getChatInputEditor().close();

        getChatInputEditor().getActionMap().remove("closeTheRoom");
        chatAreaButton.getButton().removeActionListener(this);
        bottomPanel.remove(chatAreaButton);
    }

    public abstract Icon getTabIcon();
    public abstract String getRoomname();
    public abstract String getTabTitle();
    public abstract String getRoomTitle();
    public abstract Message.Type getChatType();
    public abstract boolean isActive();
    
	public JLabel getNotificationLabel()
	{
        return notificationLabel;
    }

    public void addPacketID(String packetID)
	{
        packetIDList.add(packetID);
    }

    public boolean packetIDExists(String packetID)
	{
        return packetIDList.contains(packetID);
    }

    public ChatRoom getChatRoom()
	{
        return this;
    }

    public ChatToolBar getToolBar()
	{
        return toolbar;
    }


    public void insertUpdate(DocumentEvent e)
	{
        checkForText(e);
    }

    public void saveTranscript()
	{
    }

    public JPanel getSendFieldToolbar()
	{
        return editorBar;
    }

    public class ChatToolBar extends JPanel
	{

		private JPanel buttonPanel;

        public ChatToolBar()
		{
            buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 0));
            setLayout(new GridBagLayout());
            buttonPanel.setOpaque(false);
            add(buttonPanel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            setOpaque(false);
        }

        public void addChatRoomButton(ChatRoomButton button)
		{
            buttonPanel.add(button);
            Component[] comps = buttonPanel.getComponents();
            final int no = comps != null ? comps.length : 0;

            final List<Component> buttons = new ArrayList<Component>();
            for (int i = 0; i < no; i++)
			{
                try
				{
                    Component component = comps[i];
                    if (component instanceof JButton)
                        buttons.add(component);
                }
                catch (NullPointerException e)
				{
                }
            }
            GraphicUtils.makeSameSize((JComponent[])buttons.toArray(new JComponent[buttons.size()]));
        }

        public void removeChatRoomButton(ChatRoomButton button)
		{
            buttonPanel.remove(button);
        }
		
    }

    public int getUnreadMessageCount()
	{
        return unreadMessageCount;
    }

    public void increaseUnreadMessageCount()
	{
        unreadMessageCount++;
    }

    public void clearUnreadMessageCount()
	{
        unreadMessageCount = 0;
    }

    public JPanel getBottomPanel()
	{
        return bottomPanel;
    }

    public JPanel getChatWindowPanel()
	{
        return chatWindowPanel;
    }

    public JPanel getEditorBar()
	{
        return editorBar;
    }

    public void addClosingListener(ChatRoomClosingListener listener)
	{
        closingListeners.add(listener);
    }

    public void removeClosingListener(ChatRoomClosingListener listener)
	{
        closingListeners.remove(listener);
    }

    private void fireClosingListeners()
	{
        for (ChatRoomClosingListener chatRoomClosingListener : closingListeners)
		{
            chatRoomClosingListener.closing();
            removeClosingListener(chatRoomClosingListener);
        }
    }

    public JScrollPane getScrollPaneForTranscriptWindow()
	{
        return textScroller;
    }

    public JButton getSendButton()
	{
        return chatAreaButton.getButton();
    }

    public JSplitPane getVerticalSlipPane()
	{
        return verticalSplit;
    }

    public void focusGained(FocusEvent focusEvent)
	{
        validate();
        invalidate();
        repaint();
    }

    public void poppingUp(Object component, JPopupMenu popup)
	{
    }

    public void poppingDown(JPopupMenu popup)
	{
    }

    public boolean handleDefaultAction(MouseEvent e)
	{
        return false;
    }

    public void focusLost(FocusEvent focusEvent)
	{
    }

    public abstract long getLastActivity();

    public void connectionClosed()
	{
    }

    public void connectionClosedOnError(Exception e)
	{
    }

    public void reconnectingIn(int seconds)
	{
    }

    public void reconnectionSuccessful()
	{
    }

    public void reconnectionFailed(Exception e)
	{
    }
	
}


