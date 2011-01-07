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
import javax.swing.JCheckBox;

/**
 *
 * @author Viorel
 */
public class ChatCheckBox extends JCheckBox
{

	private static final String ICON = "/org/chartsy/chatsy/resources/chb.png";
	private static final String ICON_SEL = "/org/chartsy/chatsy/resources/chbs.png";

	public ChatCheckBox()
	{ 
		this("");
	}

	public ChatCheckBox(String text)
	{
		this(text, false);
	}

	public ChatCheckBox(String text, boolean selected)
	{
		this(text, null, selected);
	}

	public ChatCheckBox(Icon icon)
	{
		this(icon, false);
	}

	public ChatCheckBox(Icon icon, boolean selected)
	{
		this("", icon, selected);
	}

	public ChatCheckBox(String text, Icon icon, boolean selected)
	{
		super(text, icon, selected);
		initComponent();
	}

	public ChatCheckBox(Action action)
	{
		super(action);
		initComponent();
	}

	public ChatCheckBox(String text, Icon icon)
	{
		super(text, icon);
	}

	private void initComponent()
	{
		setOpaque(false);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		setFocusable(true);
		setBorderPainted(false);
		setFocusPainted(false);
		setRolloverEnabled(false);
		setContentAreaFilled(false);
		setIcon(new ImageIcon(getClass().getResource(ICON)));
		setSelectedIcon(new ImageIcon(getClass().getResource(ICON_SEL)));
		setForeground(Color.decode("0xffffff"));
		setFont(FontLoader.getDroidFont(Font.PLAIN, 14));
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
		Dimension dimension = super.getPreferredSize();
		return new Dimension(dimension.width, 24);
	}

}
