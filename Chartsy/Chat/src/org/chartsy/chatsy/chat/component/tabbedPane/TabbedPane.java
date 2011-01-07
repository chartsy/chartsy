package org.chartsy.chatsy.chat.component.tabbedPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.chartsy.chatsy.chat.util.FontLoader;
import org.chartsy.chatsy.chat.Workspace;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Viorel
 */
public class TabbedPane extends JPanel
{

	private final JComponent[] tabs;
	private final TabButton[] buttons;
	private final JComponent tabHeader;
	private final JPanel tabContent;
	private boolean[] tabAdded;
	private int selTabIndex = -1;

	public TabbedPane(JComponent... tabs)
	{
		super(new BorderLayout());
		setOpaque(false);
		this.tabs = tabs;
		tabAdded = new boolean[tabs.length];
		Arrays.fill(tabAdded, false);

		ActionListener al = new ActionListener()
		{
            public void actionPerformed(ActionEvent e)
			{
                TabButton btn = (TabButton) e.getSource();
				btn.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				switchTab(btn.getTabIndex());
            }
        };

		buttons = new TabButton[tabs.length];
		for (int i = 0; i < buttons.length; i++)
		{
			if (tabs[i] != null)
			{
				buttons[i] = new TabButton(tabs[i].getName(), i);
				buttons[i].addActionListener(al);
			}
		}

		tabHeader = new TabHeader(buttons);
		add(tabHeader, BorderLayout.NORTH);

		tabContent = new TabContentPane();
		add(tabContent, BorderLayout.CENTER);
		switchTab(0);
	}

	private void switchTab(int tabIndex)
	{
		if (!tabAdded[tabIndex])
		{
			tabContent.add(tabs[tabIndex], new GridBagConstraints(tabIndex, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0,0,0,0), 0, 0) );
			tabAdded[tabIndex] = true;
		}
		if (selTabIndex >= 0)
			buttons[selTabIndex].setSelected(false);

		JComponent compToShow = tabs[tabIndex];
		JComponent compToHide = selTabIndex >= 0 ? tabs[selTabIndex] : null;
		selTabIndex = tabIndex;
		buttons[selTabIndex].setSelected(true);

		if (compToHide != null)
			compToHide.setVisible(false);

		compToShow.setVisible(true);
		compToShow.requestFocusInWindow();
		Workspace.getInstance().getCommandPanel().setSelectedTab(selTabIndex);
	}

	private static class TabButton extends JLabel
	{

		private boolean isSelected = false;
		private ActionListener actionListener;
		private final int tabIndex;
		private Image tabBackground;
		private Image tabSelectedBackground;

		public TabButton(String title, int index)
		{
			super(title);
			tabIndex = index;
			setOpaque(false);
			setHorizontalAlignment(JLabel.CENTER);
			setHorizontalTextPosition(JLabel.CENTER);
			setVerticalAlignment(JLabel.CENTER);
			setVerticalTextPosition(JLabel.CENTER);
			setFocusable(true);
			setFont(FontLoader.getDroidFont(Font.BOLD, 14));
			setForeground(isSelected ? Color.decode("0x111111") : Color.decode("0xffffff"));

			addKeyListener(new KeyAdapter()
			{
				public void keyPressed(KeyEvent e)
				{
					if (e.getKeyCode() == KeyEvent.VK_SPACE
						|| e.getKeyCode() == KeyEvent.VK_ENTER)
						if (actionListener != null)
							actionListener.actionPerformed(new ActionEvent(TabButton.this, 0, "clicked"));
				}
			});

			addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent e)
				{
					if (actionListener != null)
						actionListener.actionPerformed(new ActionEvent(TabButton.this, 0, "clicked"));
				}
				public void mouseEntered(MouseEvent e)
				{
					if (!isSelected)
					{
						setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					}
					else
					{
						setCursor(Cursor.getDefaultCursor());
					}
				}
				public void mouseExited(MouseEvent e)
				{
					setCursor(Cursor.getDefaultCursor());
				}
			});

			addFocusListener(new FocusAdapter()
			{
				public void focusGained(FocusEvent e)
				{
					// do something
				}
				public void focusLost(FocusEvent e)
				{
					// do something
				}
			});

			tabBackground = ImageUtilities.loadImage("org/chartsy/chatsy/resources/tabh.png", true);
			tabSelectedBackground = ImageUtilities.loadImage("org/chartsy/chatsy/resources/tab.png", true);
		}

		public void addActionListener(ActionListener l)
		{
			assert null == actionListener;
			this.actionListener = l;
		}

		public void setSelected(boolean sel)
		{
			this.isSelected = sel;
			setForeground(isSelected ? Color.decode("0x111111") : Color.decode("0xffffff"));
			setFocusable(!sel);
			if (getParent() != null)
				getParent().repaint();
		}

		public int getTabIndex()
		{
            return tabIndex;
        }

		public Dimension getPreferredSize()
		{
			final Dimension dimension = super.getPreferredSize();
			if (dimension.width < 163)
				dimension.setSize(163, dimension.getHeight());
			dimension.setSize(dimension.width, 30);
			return dimension;
		}

		protected void paintComponent(Graphics g)
		{
			Graphics2D graphics2D = (Graphics2D) g;
			graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			graphics2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

			int width = getWidth();
			if (isSelected)
				graphics2D.drawImage(tabSelectedBackground, width/2 - 82, 1, this);
			else
				graphics2D.drawImage(tabBackground, width/2 - 80, 1, this);
			super.paintComponent(graphics2D);
		}
		
	}

	private class TabHeader extends JPanel
	{

		private final TabButton[] buttons;
		private TexturePaint texturePaint;

		public TabHeader(TabButton ... buttons)
		{
			super(new GridLayout(1,0));
			setOpaque(false);
			this.buttons = buttons;
			for (TabButton btn : buttons)
				add(btn);

			try
			{
				BufferedImage bufferedImage = ImageIO.read(getClass().getResource("/org/chartsy/chatsy/resources/tabsb.png"));
				Rectangle rectangle = new Rectangle(0, 0, bufferedImage.getWidth(null), bufferedImage.getHeight(null));
				texturePaint = new TexturePaint(bufferedImage, rectangle);
			}
			catch (IOException ex)
			{
			}
		}

		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			Graphics2D graphics2D = (Graphics2D) g;
			graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			graphics2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
			graphics2D.setPaint(texturePaint);
			graphics2D.fill(new Rectangle(0, getHeight()-6, getWidth(), 6));
		}

		public Dimension getPreferredSize()
		{
			Dimension dimension = super.getPreferredSize();
			return new Dimension(dimension.width, 36);
		}
		
	}

}
