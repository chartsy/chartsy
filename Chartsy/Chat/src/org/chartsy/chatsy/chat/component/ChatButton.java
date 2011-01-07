package org.chartsy.chatsy.chat.component;

import org.chartsy.chatsy.chat.util.FontLoader;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

/**
 *
 * @author Viorel
 */
public class ChatButton extends JButton
{

	private static final String ICON = "/org/chartsy/chatsy/resources/btn.png";
	private static final String ICON_HOVER = "/org/chartsy/chatsy/resources/btnh.png";

	public ChatButton()
	{
		super();
		initComponent();
	}

	public ChatButton(String text)
	{
		super(text);
		initComponent();
	}

	public ChatButton(Icon icon)
	{
		super(icon);
		initComponent();
	}

	public ChatButton(Action action)
	{
		super(action);
		initComponent();
	}

	public ChatButton(String text, Icon icon)
	{
		super(text, icon);
		initComponent();
	}

	private void initComponent()
	{
		setOpaque(false);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		setHorizontalAlignment(SwingConstants.CENTER);
		setHorizontalTextPosition(SwingConstants.CENTER);
		setVerticalAlignment(SwingConstants.CENTER);
		setVerticalTextPosition(SwingConstants.CENTER);
		setFocusable(true);
		setBorderPainted(false);
		setFocusPainted(false);
		setRolloverEnabled(true);
		setContentAreaFilled(false);
		setIcon(new ImageIcon(getClass().getResource(ICON)));
		setRolloverIcon(new ImageIcon(getClass().getResource(ICON_HOVER)));
		setForeground(Color.decode("0x111111"));
		setFont(FontLoader.getDroidFont(Font.PLAIN, 14));
		setPreferredSize(new Dimension(149, 44));
	}

	protected void paintComponent(Graphics g)
	{
		Graphics2D graphics2D = (Graphics2D) g;
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

		super.paintComponent(graphics2D);
	}


	public Dimension getPreferredSize()
	{
		return new Dimension(149, 44);
	}

}
