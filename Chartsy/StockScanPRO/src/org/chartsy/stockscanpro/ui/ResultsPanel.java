package org.chartsy.stockscanpro.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *
 * @author Viorel
 */
public class ResultsPanel extends JPanel
{

	private ResultTabbedPane tabbedPane;

	public ResultsPanel()
	{
		setLayout(new BorderLayout());
		setOpaque(false);

		tabbedPane = new ResultTabbedPane();
		tabbedPane.setOpaque(false);
		tabbedPane.setVisible(false);
		add(tabbedPane, BorderLayout.CENTER);
	}

	public void addTab(
		String exchange, String scanTitle, String scan, String responce)
	{
		if (!tabbedPane.isVisible())
			tabbedPane.setVisible(true);

		if (!responce.startsWith("Error:"))
		{
			ResultPanel resultPanel = new ResultPanel(scanTitle, scan);
			resultPanel.setResponce(responce);
			tabbedPane.addTab(exchange, resultPanel);
		}
		else
		{
			ErrorPanel errorPanel = new ErrorPanel(scanTitle, scan, responce);
			tabbedPane.addTab(exchange, errorPanel);
		}
	}

	class ResultTabbedPane extends JTabbedPane implements MouseListener
	{

		public ResultTabbedPane()
		{
			super();
			addMouseListener((MouseListener) this);
		}

		public @Override void addTab(String title, Component component)
		{
			this.addTab(title, component, null);
		}

		public void addTab(String title, Component component, Icon extraIcon)
		{
			super.addTab(title, new CloseTabIcon(extraIcon), component);
		}

		public @Override void removeTabAt(int index)
		{
			super.removeTabAt(index);
			if (getComponentCount() == 0)
				setVisible(false);
		}


		public void mouseClicked(MouseEvent e)
		{
			int tabNumber = getUI().tabForCoordinate(this, e.getX(), e.getY());
			if (tabNumber < 0)
				return;
			
			Rectangle rect = ((CloseTabIcon) getIconAt(tabNumber)).getBounds();
			if (rect.contains(e.getX(), e.getY()))
				this.removeTabAt(tabNumber);
		}

		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}

	}

	class CloseTabIcon implements Icon
	{

		private int x_pos;
		private int y_pos;
		private int width;
		private int height;
		private Icon fileIcon;

		public CloseTabIcon(Icon fileIcon)
		{
			this.fileIcon = fileIcon;
			width = 16;
			height = 16;
		}

		public void paintIcon(Component c, Graphics g, int x, int y)
		{
			this.x_pos = x;
			this.y_pos = y;
			int y_p = y + 2;

			if (fileIcon == null)
			{
				Color col = g.getColor();

				g.setColor(Color.black);
				g.drawLine(x + 1, y_p, x + 12, y_p);
				g.drawLine(x + 1, y_p + 13, x + 12, y_p + 13);
				g.drawLine(x, y_p + 1, x, y_p + 12);
				g.drawLine(x + 13, y_p + 1, x + 13, y_p + 12);
				g.drawLine(x + 3, y_p + 3, x + 10, y_p + 10);
				g.drawLine(x + 3, y_p + 4, x + 9, y_p + 10);
				g.drawLine(x + 4, y_p + 3, x + 10, y_p + 9);
				g.drawLine(x + 10, y_p + 3, x + 3, y_p + 10);
				g.drawLine(x + 10, y_p + 4, x + 4, y_p + 10);
				g.drawLine(x + 9, y_p + 3, x + 3, y_p + 9);
				g.setColor(col);
			}
			else
				fileIcon.paintIcon(c, g, x + width, y_p);
		}

		public int getIconWidth()
		{
			return width + (fileIcon != null ? fileIcon.getIconWidth() : 0);
		}

		public int getIconHeight()
		{
			return height;
		}

		public Rectangle getBounds()
		{
			return new Rectangle(x_pos, y_pos, width, height);
		}

	}

}
