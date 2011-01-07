package org.chartsy.chatsy.chat.component.tabbedPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.BorderFactory;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.chartsy.chatsy.chat.util.FontLoader;

import org.chartsy.chatsy.chat.util.ModelUtil;
import org.chartsy.chatsy.chat.util.SwingWorker;
import org.chartsy.chatsy.chat.util.log.Log;
import org.openide.util.ImageUtilities;

public class ChatsyTabbedPane extends JPanel
{

	private static final String NAME = "ChatsyTabbedPane";
	private List<ChatsyTabbedPaneListener> listeners = new ArrayList<ChatsyTabbedPaneListener>();
	private JTabbedPane pane = null;
	private Icon closeInactiveButtonIcon;
	private Icon closeActiveButtonIcon;
	private boolean closeEnabled = false;
	private int dragTabIndex = -1;

	public static final Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);
	public static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);

	public ChatsyTabbedPane()
	{
		this(JTabbedPane.TOP);
	}

	public ChatsyTabbedPane(final int type)
	{
		setLayout(new BorderLayout());
		setOpaque(false);

		pane = new JTabbedPane(type);
		pane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
		add(pane);
		
		ChangeListener changeListener = new ChangeListener()
		{
			public void stateChanged(ChangeEvent changeEvent)
			{
				JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
				int index = sourceTabbedPane.getSelectedIndex();
				if (index >= 0) 
					fireTabSelected(getTabAt(index), getTabAt(index).getComponent(), index);
			}
		};
		pane.addChangeListener(changeListener);
		closeInactiveButtonIcon = ImageUtilities.loadImageIcon("org/chartsy/chatsy/resources/close_white.png", true);
		closeActiveButtonIcon = ImageUtilities.loadImageIcon("org/chartsy/chatsy/resources/close_dark.png", true);
	}
	
	public ChatsyTab getTabContainingComponent(Component component)
	{
		for (Component comp : pane.getComponents())
		{
			if (comp instanceof ChatsyTab)
			{
				ChatsyTab tab = (ChatsyTab) comp;
				if (tab.getComponent() == component)
					return tab;
			}
		}
		return null;
	}

	public ChatsyTab addTab(String title, Icon icon, final Component component)
	{
		return addTab(title, icon, component, null);
	}

	public ChatsyTab addTab(String title, Icon icon, final Component component, String tip)
	{
		final ChatsyTab chatsyTab = new ChatsyTab(this, component);
		TabPanel tabpanel = new TabPanel(chatsyTab, title, icon);
		pane.addTab(title, null, chatsyTab, tip);
		pane.setTabComponentAt(pane.getTabCount() - 1, tabpanel);
		fireTabAdded(chatsyTab, component, getTabPosition(chatsyTab));
		return chatsyTab;
	}

	public ChatsyTab getTabAt(int index)
	{
		return ((ChatsyTab) pane.getComponentAt(index));
	}

	public int getTabPosition(ChatsyTab tab)
	{
		return pane.indexOfComponent(tab);
	}

	public Component getComponentInTab(ChatsyTab tab)
	{
		return tab.getComponent();
	}

	public void setIconAt(int index, Icon icon)
	{
		Component com = pane.getTabComponentAt(index);
		if (com instanceof TabPanel)
		{
			TabPanel panel = (TabPanel) com;
			panel.setIcon(icon);
		}
	}

	public void setTitleAt(int index, String title)
	{
		if (index > 0)
		{
			Component com = pane.getTabComponentAt(index);
			if (com instanceof TabPanel)
			{
				TabPanel panel = (TabPanel) com;
				panel.setTitle(title);
			}
		}
	}

	public void setTitleColorAt(int index, Color color)
	{
		Component com = pane.getTabComponentAt(index);
		if (com instanceof TabPanel)
		{
			TabPanel panel = (TabPanel) com;
			panel.setTitleColor(color);
		}
	}

	public void setTitleBoldAt(int index, boolean bold)
	{
		Component com = pane.getTabComponentAt(index);
		if (com instanceof TabPanel)
		{
			TabPanel panel = (TabPanel) com;
			panel.setTitleBold(bold);
		}
	}

	public void setTitleFontAt(int index, Font font)
	{
		Component com = pane.getTabComponentAt(index);
		if (com instanceof TabPanel)
		{
			TabPanel panel = (TabPanel) com;
			panel.setTitleFont(font);
		}
	}

	public Font getDefaultFontAt(int index)
	{
		Component com = pane.getTabComponentAt(index);
		if (com instanceof TabPanel)
		{
			TabPanel panel = (TabPanel) com;
			return panel.getDefaultFont();
		}
		return null;
	}

	public String getTitleAt(int index)
	{
		return pane.getTitleAt(index);
	}

	public int getTabCount()
	{
		return pane.getTabCount();
	}

	public void setSelectedIndex(int index)
	{
		pane.setSelectedIndex(index);
	}

	public int indexOfComponent(Component component)
	{
		for (Component comp : pane.getComponents())
		{
			if (comp instanceof ChatsyTab)
			{
				ChatsyTab tab = (ChatsyTab) comp;
				if (tab.getComponent() == component)
					return pane.indexOfComponent(tab);
			}
		}
		return -1;
	}

	public Component getComponentAt(int index)
	{
		return ((ChatsyTab) pane.getComponentAt(index)).getComponent();
	}

	public Component getTabComponentAt(int index)
	{
		return pane.getTabComponentAt(index);
	}

	public Component getTabComponentAt(ChatsyTab tab)
	{
		return pane.getTabComponentAt(indexOfComponent(tab));
	}

	public Component getSelectedComponent()
	{
		if (pane.getSelectedComponent() instanceof ChatsyTab)
		{
			ChatsyTab tab = (ChatsyTab) pane.getSelectedComponent();
			return tab.getComponent();
		}
		return null;
	}

	public void removeTabAt(int index)
	{
		pane.remove(index);
	}

	public int getSelectedIndex()
	{
		return pane.getSelectedIndex();
	}

	public void setCloseButtonEnabled(boolean enable)
	{
		closeEnabled = enable;
	}

	public void addChatsyTabbedPaneListener(ChatsyTabbedPaneListener listener)
	{
		listeners.add(listener);
	}

	public void removeChatsyTabbedPaneListener(ChatsyTabbedPaneListener listener)
	{
		listeners.remove(listener);
	}

	public void fireTabAdded(ChatsyTab tab, Component component, int index)
	{
		final Iterator list = ModelUtil.reverseListIterator(listeners.listIterator());
		while (list.hasNext())
			((ChatsyTabbedPaneListener) list.next()).tabAdded(tab, component, index);
	}

	public JPanel getMainPanel()
	{
		return this;
	}

	public void removeComponent(Component comp)
	{
		int index = indexOfComponent(comp);
		if (index != -1) 
			removeTabAt(index);
	}

	public void fireTabRemoved(ChatsyTab tab, Component component, int index)
	{
		final Iterator list = ModelUtil.reverseListIterator(listeners.listIterator());
		while (list.hasNext())
			((ChatsyTabbedPaneListener) list.next()).tabRemoved(tab, component, index);
	}

	public void fireTabSelected(ChatsyTab tab, Component component, int index)
	{
		final Iterator list = ModelUtil.reverseListIterator(listeners.listIterator());
		while (list.hasNext())
			((ChatsyTabbedPaneListener) list.next()).tabSelected(tab, component, index);
	}

	public void allTabsClosed()
	{
		final Iterator list = ModelUtil.reverseListIterator(listeners.listIterator());
		while (list.hasNext()) 
			((ChatsyTabbedPaneListener) list.next()).allTabsRemoved();
	}

	public void close(ChatsyTab chatsyTab)
	{
		int closeTabNumber = pane.indexOfComponent(chatsyTab);
		pane.removeTabAt(closeTabNumber);
		fireTabRemoved(chatsyTab, chatsyTab.getComponent(), closeTabNumber);

		if (pane.getTabCount() == 0) 
			allTabsClosed();
	}

	private final class TabPanel extends JPanel
	{

		private final Font defaultFont = FontLoader.getDroidFont(Font.PLAIN, 11);
		private final Font tabFont = FontLoader.getDroidFont(Font.BOLD, 14);
		private final Color defaultColor = Color.decode("0x111111");
		private JLabel iconLabel;
		private JLabel titleLabel;

		public TabPanel(final ChatsyTab chatsyTab, String title, Icon icon)
		{
			setLayout(new BorderLayout());
			setOpaque(false);
			
			titleLabel = new JLabel(title);
			titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			titleLabel.setFont(defaultFont);
			titleLabel.setForeground(defaultColor);
			if (icon != null)
			{
				iconLabel = new JLabel(icon);
				add(iconLabel, BorderLayout.WEST);
			}
			add(titleLabel, BorderLayout.CENTER);
			
			if (closeEnabled)
			{
				final JLabel tabCloseButton = new JLabel(closeInactiveButtonIcon);
				tabCloseButton.setCursor(HAND_CURSOR);
				tabCloseButton.addMouseListener(new MouseAdapter()
				{
					public void mouseEntered(MouseEvent mouseEvent)
					{
						tabCloseButton.setIcon(closeActiveButtonIcon);
					}
					public void mouseExited(MouseEvent mouseEvent)
					{
						tabCloseButton.setIcon(closeInactiveButtonIcon);
					}
					public void mousePressed(MouseEvent mouseEvent)
					{
						final SwingWorker closeTimerThread = new SwingWorker()
						{
							public Object construct()
							{
								try
								{
									Thread.sleep(100);
								} 
								catch (InterruptedException e)
								{
									Log.error(e);
								}
								return true;
							}
							public void finished()
							{
								close(chatsyTab);
							}
						};
						closeTimerThread.start();
					}
				});
				add(tabCloseButton, BorderLayout.EAST);
			}
			else
			{
				setTitleFont(tabFont);
				if (iconLabel != null)
					iconLabel.setIcon(null);
			}
		}

		public Font getDefaultFont()
		{
			return defaultFont;
		}

		public Color getDefaultColor()
		{
			return defaultColor;
		}

		public void setIcon(Icon icon)
		{
			iconLabel.setIcon(icon);
		}

		public void setTitle(String title)
		{
			titleLabel.setText(title);
		}

		public void setTitleColor(Color color)
		{
			titleLabel.setForeground(color);
		}

		public void setTitleBold(boolean bold)
		{
			Font oldFont = titleLabel.getFont();
			Font newFont;
			if (bold)
				newFont = new Font(oldFont.getFontName(), Font.BOLD, oldFont.getSize());
			else
				newFont = new Font(oldFont.getFontName(), Font.PLAIN, oldFont.getSize());
			titleLabel.setFont(newFont);
		}

		public void setTitleFont(Font font)
		{
			titleLabel.setFont(font);
		}

	}

	public void enableDragAndDrop()
	{
		final DragSourceListener dragSourceListener = new DragSourceListener()
		{
			public void dragDropEnd(DragSourceDropEvent event)
			{
				dragTabIndex = -1;
			}
			public void dragEnter(DragSourceDragEvent event)
			{
				event.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);				
			}
			public void dragExit(DragSourceEvent event) 
			{
			}
			public void dragOver(DragSourceDragEvent event) 
			{
			}
			public void dropActionChanged(DragSourceDragEvent event) 
			{
			}
		};

	    final Transferable transferable = new Transferable()
		{
	    	private final DataFlavor FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, NAME);
			public Object getTransferData(DataFlavor flavor)
					throws UnsupportedFlavorException, IOException
			{
				return pane;
			}
			public DataFlavor[] getTransferDataFlavors()
			{
				DataFlavor[] f = new DataFlavor[1];
				f[0] = this.FLAVOR;
				return f;
			}
			public boolean isDataFlavorSupported(DataFlavor flavor)
			{
				return flavor.getHumanPresentableName().equals(NAME);
			}
	    };
	    
	    final DragGestureListener dragGestureListener = new DragGestureListener()
		{
			public void dragGestureRecognized(DragGestureEvent event)
			{
                dragTabIndex = pane.indexAtLocation(event.getDragOrigin().x, event.getDragOrigin().y);
                try
				{
                    event.startDrag(DragSource.DefaultMoveDrop, transferable, dragSourceListener);
                }
				catch(Exception ex)
				{
                }
			}

	    };
	    
	    final DropTargetListener dropTargetListener = new DropTargetListener()
		{
			public void dragEnter(DropTargetDragEvent event) 
			{
			}
			public void dragExit(DropTargetEvent event) 
			{
			}
			public void dragOver(DropTargetDragEvent event) 
			{
			}
			public void drop(DropTargetDropEvent event)
			{
				int dropTabIndex = getTargetTabIndex(event.getLocation());
	            moveTab(dragTabIndex,dropTabIndex);
			}
			public void dropActionChanged(DropTargetDragEvent event) 
			{
			}
	    };
		
	    DropTarget dropTarget = new DropTarget(pane, DnDConstants.ACTION_COPY_OR_MOVE, dropTargetListener, true);
	    DragSource dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer(pane, DnDConstants.ACTION_COPY_OR_MOVE, dragGestureListener);
	}
	
	
	private void moveTab(int prev, int next)
	{
		if (next < 0 || prev == next) 
			return;
		
		Component cmp = pane.getComponentAt(prev);
		Component tab = pane.getTabComponentAt(prev);
		String str = pane.getTitleAt(prev);
		Icon icon = pane.getIconAt(prev);
		String tip = pane.getToolTipTextAt(prev);
		boolean flg = pane.isEnabledAt(prev);
		int tgtindex = prev > next ? next : next - 1;
		pane.remove(prev);
		pane.insertTab(str, icon, cmp, tip, tgtindex);
		pane.setEnabledAt(tgtindex, flg);

		if (flg)
			pane.setSelectedIndex(tgtindex);
		pane.setTabComponentAt(tgtindex, tab);
	}
	
	private int getTargetTabIndex(Point point)
	{
		Point tabPt = SwingUtilities.convertPoint(pane, point, pane);
		boolean isTB = pane.getTabPlacement() == JTabbedPane.TOP 
			|| pane.getTabPlacement() == JTabbedPane.BOTTOM;
		for (int i = 0; i < getTabCount(); i++)
		{
			Rectangle r = pane.getBoundsAt(i);
			if(isTB)
				r.setRect(r.x-r.width/2, r.y,  r.width, r.height);
			else
				r.setRect(r.x, r.y-r.height/2, r.width, r.height);
			if (r.contains(tabPt))
				return i;
		}
		Rectangle r = pane.getBoundsAt(getTabCount()-1);
		if(isTB)
			r.setRect(r.x+r.width/2, r.y,  r.width, r.height);
		else
			r.setRect(r.x, r.y+r.height/2, r.width, r.height);
		return r.contains(tabPt)?getTabCount():-1;
	}

}
