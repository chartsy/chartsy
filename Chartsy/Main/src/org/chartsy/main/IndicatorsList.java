package org.chartsy.main;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import org.chartsy.main.chart.Indicator;
import org.chartsy.main.utils.SerialVersion;

/**
 *
 * @author Viorel
 */
public class IndicatorsList extends JList
{

	private static final long serialVersionUID = SerialVersion.APPVERSION;

	private ChartFrame chartFrame;
	private ChartFrameAdapter chartFrameAdapter;
	private DefaultListModel listModel;
	private IndicatorsListRenderer listRenderer;

	public IndicatorsList(ChartFrame frame)
	{
		this.chartFrame = frame;
		this.listModel = new DefaultListModel();
		this.listRenderer = new IndicatorsListRenderer();

		setOpaque(false);
		setDoubleBuffered(true);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setBorder(BorderFactory.createEmptyBorder());
		setDragEnabled(false);
		setModel(listModel);
		setFixedCellHeight(Indicator.DEFAULT_HEIGHT);
		setCellRenderer(listRenderer);

		chartFrameAdapter = new ChartFrameAdapter()
		{

			@Override
			public void indicatorAdded(Indicator indicator)
			{
				int index = listModel.size();
				listModel.add(index, indicator);
				revalidate();
				repaint();
			}

			@Override
			public void indicatorRemoved(Indicator indicator)
			{
				listModel.removeElement(indicator);
				revalidate();
				repaint();
			}

		};
		chartFrame.addChartFrameListener(chartFrameAdapter);
	}

	public ChartFrame getChartFrame()
	{
		return chartFrame;
	}

	public int getIndicatorsCount()
	{
		return getModel().getSize();
	}

	class IndicatorsListModel extends AbstractListModel
	{

		private List<Indicator> indicators;

		public IndicatorsListModel()
		{
			indicators = Collections.synchronizedList(new ArrayList<Indicator>());
		}

		@Override
		public int getSize()
		{
			return indicators.size();
		}

		@Override
		public Object getElementAt(int index)
		{
			return indicators.get(index);
		}

	}

	class IndicatorsListRenderer extends JPanel implements ListCellRenderer
	{

		public IndicatorsListRenderer()
		{
			setOpaque(false);
		}

		@Override
		public Component getListCellRendererComponent
			(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			Indicator indicator = (Indicator) value;
			IndicatorPanel indicatorPanel = new IndicatorPanel(chartFrame, indicator);
			indicatorPanel.updateToolbox();
			int height = Indicator.DEFAULT_HEIGHT != indicator.getMaximizedHeight()
				? indicator.getMaximizedHeight()
				: Indicator.DEFAULT_HEIGHT;
			indicatorPanel.setMaximizedHeight(height);
			return indicatorPanel;
		}

	}

}
