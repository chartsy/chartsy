package org.chartsy.chatsy.chat.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.chartsy.chatsy.chat.util.FontLoader;
import org.jivesoftware.smack.XMPPException;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.ui.conferences.ConferenceUtils;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Viorel
 */
public class ConferenceItem extends JPanel
{

	private String conferenceName;
	private String conferenceJID;
	private int occupants = 0;
	private JLabel nameLabel;
	private boolean mouseOver = false;
	private boolean focused = false;

	public ConferenceItem(String name, String jid)
	{
		super(new BorderLayout());
		setOpaque(true);
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		setFocusable(true);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		this.conferenceName = name;
		this.conferenceJID = jid;
		try
		{
			this.occupants = ConferenceUtils.getNumberOfOccupants(conferenceJID);
		}
		catch (XMPPException ex)
		{
		}

		nameLabel = new JLabel(conferenceName);
		nameLabel.setOpaque(false);
		nameLabel.setHorizontalAlignment(JLabel.LEFT);
		nameLabel.setVerticalAlignment(JLabel.CENTER);
		nameLabel.setFont(FontLoader.getDroidFont(Font.PLAIN, 13));
		nameLabel.setForeground(Color.decode("0x111111"));
		nameLabel.setIcon(ImageUtilities.loadImageIcon("org/chartsy/chatsy/resources/conference.png", true));
		nameLabel.setIconTextGap(10);
		add(nameLabel, BorderLayout.CENTER);

		addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				requestFocus();
				if (e.getClickCount() == 2)
					joinRoom(conferenceJID, conferenceName);
			}
			public void mouseEntered(MouseEvent e)
			{
				mouseOver = true;
				repaint();
			}
			public void mouseExited(MouseEvent e)
			{
				mouseOver = false;
				repaint();
			}
		});
		
		addFocusListener(new FocusAdapter()
		{
			public void focusGained(FocusEvent e)
			{
				focused = true;
				repaint();
			}
			public void focusLost(FocusEvent e)
			{
				focused = false;
				repaint();
			}
		});
	}

	private void joinRoom(String roomJID, String roomDescription)
	{
		ChatRoom chatRoom = ChatsyManager.getChatManager().getChatContainer().getChatRoom(roomJID);
		if (chatRoom == null)
			ConferenceUtils.joinConferenceOnSeperateThread(roomDescription, roomJID, null);
	}

	public String getConferenceName()
	{
		return conferenceName;
	}

	public String getConferenceJID()
	{
		return conferenceJID;
	}

	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		Graphics2D graphics2D = (Graphics2D) g;
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		
		if (mouseOver && !focused)
		{
			Color color = Color.decode("0x0298db");
			Color bgColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 100);
			graphics2D.setColor(bgColor);
			graphics2D.fill(new Rectangle(0, 0, getWidth(), getHeight()-1));
		}

		if (focused)
		{
			Color color = Color.decode("0x0298db");
			Color bgColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 200);
			graphics2D.setColor(bgColor);
			graphics2D.fill(new Rectangle(0, 0, getWidth(), getHeight()-1));
		}

		GradientPaint gradientPaint;
		gradientPaint = new GradientPaint(0, getHeight()-1, Color.decode("0xefefef"), getWidth()/2, getHeight()-1, Color.decode("0xadaaad"));
		graphics2D.setPaint(gradientPaint);
		graphics2D.drawLine(0, getHeight()-1, getWidth()/2, getHeight()-1);
		gradientPaint = new GradientPaint(getWidth()/2, getHeight()-1, Color.decode("0xadaaad"), getWidth(), getHeight()-1, Color.decode("0xefefef"));
		graphics2D.setPaint(gradientPaint);
		graphics2D.drawLine(getWidth()/2, getHeight()-1, getWidth(), getHeight()-1);
	}

}
