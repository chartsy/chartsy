package org.chartsy.main.templates;

import java.util.ArrayList;
import org.chartsy.main.ChartProperties;
import org.chartsy.main.chart.Chart;
import org.chartsy.main.chart.Indicator;
import org.chartsy.main.chart.Overlay;
import org.chartsy.main.managers.TemplateManager;

/**
 *
 * @author Viorel
 */
public class Template
{

	private String name;
	private Chart chart;
	private ChartProperties chartProperties;
	private ArrayList<Overlay> overlays;
	private ArrayList<Indicator> indicators;

	public Template(String name)
	{
		this.name = name;
		this.chartProperties = new ChartProperties();
		this.overlays = new ArrayList<Overlay>();
		this.indicators = new ArrayList<Indicator>();
	}

	public String getName()
	{
		return name;
	}

	public void setChart(Chart chart)
	{
		if (chart == null)
			return;
		this.chart = chart;
	}

	public Chart getChart()
	{
		return chart;
	}

	public void setChartProperties(ChartProperties chartProperties)
	{
		if (chartProperties == null)
			return;
		this.chartProperties = chartProperties;
	}

	public ChartProperties getChartProperties()
	{
		return chartProperties;
	}

	public void addOverlay(Overlay overlay)
	{
		if (overlay == null)
			return;
		overlays.add(overlay);
	}

	public ArrayList<Overlay> getOverlays()
	{
		ArrayList<Overlay> list = new ArrayList<Overlay>();
		for (int i = 0; i < overlays.size(); i++)
		{
			Overlay instance = TemplateManager.getDefault().getOverlay(i);
			list.add(instance);
		}
		return list;
	}

	public void addIndicator(Indicator indicator)
	{
		if (indicator == null)
			return;
		indicators.add(indicator);
	}

	public ArrayList<Indicator> getIndicators()
	{
		ArrayList<Indicator> list = new ArrayList<Indicator>();
		for (Indicator indicator : indicators)
		{
			Indicator instance = indicator.newInstance();
			list.add(instance);
		}
		return list;
	}

}
