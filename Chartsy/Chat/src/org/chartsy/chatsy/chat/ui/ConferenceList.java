package org.chartsy.chatsy.chat.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.component.tabbedPane.AbstractTab;
import org.chartsy.chatsy.chat.util.TaskEngine;

/**
 *
 * @author Viorel
 */
public class ConferenceList extends AbstractTab
{

	private List<String> hostedRooms = new ArrayList<String>();
	private JPanel listPanel;
	private JScrollPane hostedRoomsListScrollPane;
	private TimerTask refeshTask;

	public ConferenceList()
	{
		setName("Conferences");
	}

	@Override
	protected void buildContent()
	{
		listPanel = new JPanel();
		listPanel.setLayout(new LayoutManager()
		{
			public void addLayoutComponent(String name, Component comp)
			{
			}
			public void removeLayoutComponent(Component comp)
			{
			}
			public Dimension preferredLayoutSize(Container parent)
			{
				return new Dimension(0, 0);
			}
			public Dimension minimumLayoutSize(Container parent)
			{
				return new Dimension(0, 0);
			}
			public void layoutContainer(Container parent)
			{
				Insets insets = parent.getInsets();
				int w = parent.getWidth() - insets.left - insets.right;
				int h = parent.getHeight() - insets.top - insets.bottom;
				int x = insets.left;
				int y = insets.top;

				for (Component component : parent.getComponents())
				{
					component.setBounds(x, y, w, 42);
					y += 42;
				}
			}
		});
		listPanel.setOpaque(true);
		listPanel.setBackground(Color.WHITE);

		hostedRoomsListScrollPane = new JScrollPane(listPanel);
		hostedRoomsListScrollPane.getViewport().setOpaque(true);
		hostedRoomsListScrollPane.getViewport().setBackground(Color.WHITE);
		hostedRoomsListScrollPane.setAutoscrolls(true);
		hostedRoomsListScrollPane.setBorder(BorderFactory.createEmptyBorder());
		hostedRoomsListScrollPane.getVerticalScrollBar().setBlockIncrement(50);
		hostedRoomsListScrollPane.getVerticalScrollBar().setUnitIncrement(20);
		add(hostedRoomsListScrollPane, BorderLayout.CENTER);

		try
		{
			Collection<HostedRoom> rooms = MultiUserChat.getHostedRooms(ChatsyManager.getConnection(), "conference.chat.mrswing.com");
			Iterator<HostedRoom> iterator = rooms.iterator();
			while (iterator.hasNext())
			{
				HostedRoom hostedRoom = iterator.next();
				if (!hostedRooms.contains(hostedRoom.getJid()) && !hostedRoom.getJid().startsWith("symbol_conference"))
				{
					hostedRooms.add(hostedRoom.getJid());
					ConferenceItem conferenceItem = new ConferenceItem(hostedRoom.getName(), hostedRoom.getJid());
					listPanel.add(conferenceItem);
				}
			}
		}
		catch (XMPPException ex)
		{
		}

		refeshTask = new TimerTask()
		{
			@Override public void run()
			{
				try
				{
					Collection<HostedRoom> rooms = MultiUserChat.getHostedRooms(ChatsyManager.getConnection(), "conference.chat.mrswing.com");
					Iterator<HostedRoom> iterator = rooms.iterator();
					while (iterator.hasNext())
					{
						HostedRoom hostedRoom = iterator.next();
						if (!hostedRooms.contains(hostedRoom.getJid()) && !hostedRoom.getJid().startsWith("symbol_conference"))
						{
							hostedRooms.add(hostedRoom.getJid());
							ConferenceItem conferenceItem = new ConferenceItem(hostedRoom.getName(), hostedRoom.getJid());
							listPanel.add(conferenceItem);
						}
					}
				}
				catch (XMPPException ex)
				{
				}
				listPanel.validate();
				listPanel.repaint();
			}
		};
		TaskEngine.getInstance().scheduleAtFixedRate(refeshTask, 2000, 2000);
	}

}
