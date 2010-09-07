package org.chartsy.main.welcome.content;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Viorel
 */
public abstract class LinkButton extends JButton
	implements Constants, MouseListener, ActionListener, FocusListener
{

	private String label;
	private boolean underline = false;
	final ImageIcon BULLET = ImageUtilities.loadImageIcon(BULLET_ICON, false);
	private final Color defaultForeground;
	
	public LinkButton(String label, boolean showBullet)
	{
		this(label, showBullet, LINK_COLOR);
	}
	
	public LinkButton(String label, boolean showBullet, Color foreground)
	{
		super(label);
		this.label = label;
		this.defaultForeground = foreground;
		setForeground(defaultForeground);
		setFont(LINK_FONT);
		setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		setHorizontalAlignment(SwingConstants.LEFT);
		setVerticalAlignment(SwingConstants.CENTER);
		if (showBullet)
            setIcon(BULLET);
		setFocusable(true);
		setMargin(new Insets(0, 0, 0, 0));
		setBorderPainted(false);
		setFocusPainted(false);
		setRolloverEnabled(false);
		setContentAreaFilled(false);

		addMouseListener((MouseListener)this);
		addActionListener((ActionListener)this);
		addFocusListener((FocusListener)this);
	}

	public void mouseClicked(MouseEvent e)
	{}

	public void mousePressed(MouseEvent e)
	{}

	public void mouseReleased(MouseEvent e)
	{}

	public void mouseEntered(MouseEvent e)
	{
		setText("<html><u>"+label+"</u></html>");
		if (isEnabled())
			onMouseEntered();
	}

	public void mouseExited(MouseEvent e)
	{
		setText("<html>"+label+"</html>");
		if (isEnabled())
			onMouseExited();
	}

	public void actionPerformed(ActionEvent e)
	{
		if (isEnabled())
			onActionPerformed();
	}
	
	public void focusGained(FocusEvent e)
	{
		Rectangle rect = getBounds();
		rect.grow(0, FONT_SIZE);
		scrollRectToVisible(rect);
	}

	public void focusLost(FocusEvent e)
	{}

	protected void onActionPerformed()
	{}

	protected void onMouseExited()
	{}

    protected void onMouseEntered()
	{}

    @Override public void paint(Graphics g) {
        super.paint(g);
        if (underline && isEnabled())
		{
            Font f = getFont();
            FontMetrics fm = getFontMetrics(f);
            int iconWidth = 0;
            if (null != getIcon())
			{
                iconWidth = getIcon().getIconWidth() + getIconTextGap();
            }
            int x1 = iconWidth;
            int y1 = fm.getHeight();
            int x2 = fm.stringWidth(getText()) + iconWidth;
            if (getText().length() > 0)
                g.drawLine(x1, y1, x2, y1);
        }
    }

	protected boolean isVisited()
	{
		return false;
	}

}
