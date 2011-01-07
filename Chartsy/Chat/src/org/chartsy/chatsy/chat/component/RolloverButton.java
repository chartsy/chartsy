package org.chartsy.chatsy.chat.component;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

import javax.swing.SwingConstants;
import org.openide.util.ImageUtilities;

public class RolloverButton extends JButton
{

    public RolloverButton()
	{
        decorate();
    }

    public RolloverButton(String text)
	{
        super(text);
        decorate();
    }

    public RolloverButton(Action action)
	{
        super(action);
        decorate();
    }

    public RolloverButton(Icon icon)
	{
        super(icon);
        decorate();
    }

    public RolloverButton(String text, Icon icon)
	{
        super(text, icon);
        decorate();
    }

    private void decorate()
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
		setRolloverEnabled(false);
		setContentAreaFilled(false);
		setMargin(new Insets(6, 6, 6, 6));
		setForeground(Color.decode("0x111111"));
		setFont(new Font("Dialog", Font.BOLD, 14));
		setPreferredSize(new Dimension(27, 28));
    }

	@Override protected void paintComponent(Graphics g)
	{
		Image image = ImageUtilities.loadImage("org/chartsy/chatsy/resources/tbtn-bg.png", true);
		if (image != null)
			g.drawImage(image, 0, 0, this);
		super.paintComponent(g);
	}

	@Override public Dimension getPreferredSize()
	{
		return new Dimension(27, 28);
	}

}