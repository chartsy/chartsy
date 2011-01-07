package org.chartsy.main;

import org.chartsy.main.chart.Chart;
import org.chartsy.main.chart.Indicator;
import org.chartsy.main.chart.Overlay;
import org.chartsy.main.data.Stock;
import org.chartsy.main.intervals.Interval;

/**
 *
 * @author Viorel
 */
public abstract class ChartFrameAdapter implements ChartFrameListener
{

	public void stockChanged(Stock newStock) {}
	public void intervalChanged(Interval newInterval) {}
	public void chartChanged(Chart newChart) {}
	public void datasetKeyChanged(String datasetKey) {}
	public void indicatorAdded(Indicator indicator) {}
	public void indicatorRemoved(Indicator indicator) {}
	public void overlayAdded(Overlay overlay) {}
	public void overlayRemoved(Overlay overlay) {}
	public double zoomIn(double barWidth) { return barWidth; }
	public double zoomOut(double barWidth) { return barWidth; }

}
