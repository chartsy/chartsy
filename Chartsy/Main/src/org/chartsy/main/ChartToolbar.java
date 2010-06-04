package org.chartsy.main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.AbstractBorder;
import org.chartsy.main.utils.MainActions;
import org.chartsy.main.utils.SerialVersion;

/**
 *
 * @author Administrator
 */
public class ChartToolbar extends JToolBar implements Serializable
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    private ChartFrame chartFrame;
    private SymbolChanger symbolChanger;

    public ChartToolbar(ChartFrame frame)
    {
        super("ChartToolbar", JToolBar.HORIZONTAL);
        chartFrame = frame;
        initComponents();
        setFloatable(false);
        setBorder(new BottomBorder());
        addMouseListener(new ToolbarOptions(this));
    }

    protected void initComponents()
    {
        // Symbol Changer Toolbar
        symbolChanger = new SymbolChanger(chartFrame);
        add(symbolChanger);

        AbstractButton button;

        // Zoom In Button
        button = new JButton(MainActions.zoomIn(chartFrame));
        setButtonSettings(button, "Zoom In");
        add(button);

        // Zoom Out Button
        button = new JButton(MainActions.zoomOut(chartFrame));
        setButtonSettings(button, "Zoom Out");
        add(button);

        // Intervals Button
        button = new JButton(MainActions.timeMenu(chartFrame));
        setButtonSettings(button, "Select Time");
        add(button);

        // Charts Button
        button = new JButton(MainActions.chartMenu(chartFrame));
        setButtonSettings(button, "Select Chart");
        add(button);

        // Indicators Button
        button = new JButton(MainActions.addIndicators(chartFrame));
        setButtonSettings(button, "Add Indicators");
        add(button);

        // Overlays Button
        button = new JButton(MainActions.addOverlays(chartFrame));
        setButtonSettings(button, "Add Overlays");
        add(button);

        // Annotations Button
        button = new JButton(MainActions.annotationMenu(chartFrame));
        setButtonSettings(button, "Annotations");
        add(button);

        // Marker Button
        button = new JToggleButton(MainActions.markerAction(chartFrame));
        setButtonSettings(button, "Marker");
        button.setSelected(true);
        add(button);

        // Export Image Button
        button = new JButton(MainActions.exportImage(chartFrame));
        setButtonSettings(button, "Export Image");
        add(button);

        // Print Button
        button = new JButton(MainActions.printChart(chartFrame));
        setButtonSettings(button, "Print");
        add(button);

        // Properties Button
        button = new JButton(MainActions.chartSettings(chartFrame));
        setButtonSettings(button, "Chart Settings");
        add(button);
    }


    private void setButtonSettings(AbstractButton button, String tooltip)
    {
        button.setVerticalAlignment(SwingConstants.TOP);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setMargin(new Insets(6,6,6,6));
        button.setBorderPainted(false);
        button.setToolTipText(tooltip);
        if (!chartFrame.getChartProperties().getToolbarShowLabels())
            button.setText("");
    }

    public void updateToolbar()
    {
        symbolChanger.updateToolbar();
    }

    public JPopupMenu getToolbarMenu()
    {
        JPopupMenu popup = new JPopupMenu();
        JCheckBoxMenuItem item;

        item = new JCheckBoxMenuItem(new AbstractAction("Small Toolbar Icons")
        {
            public void actionPerformed(ActionEvent e)
            {
                boolean b = chartFrame.getChartProperties().getToolbarSmallIcons();
                chartFrame.getChartProperties().setToolbarSmallIcons(!b);
                updateToolbar();
            }
        });
        item.setMargin(new Insets(0,0,0,0));
        item.setState(chartFrame.getChartProperties().getToolbarSmallIcons());
        popup.add(item);

        item = new JCheckBoxMenuItem(new AbstractAction("Show Labels")
        {
            public void actionPerformed(ActionEvent e)
            {
                boolean b = chartFrame.getChartProperties().getToolbarShowLabels();
                chartFrame.getChartProperties().setToolbarShowLabels(!b);
                updateToolbar();
            }
        });
        item.setMargin(new Insets(0,0,0,0));
        item.setState(chartFrame.getChartProperties().getToolbarShowLabels());
        popup.add(item);

        return popup;
    }

    public static class ToolbarOptions extends MouseAdapter
    {

        private ChartToolbar toolbar;

        public ToolbarOptions(ChartToolbar bar)
        {
            toolbar = bar;
        }

        @Override
        public void mousePressed(MouseEvent e)
        {
            if (e.getButton() == MouseEvent.BUTTON3)
            {
                toolbar.getToolbarMenu().show(toolbar, e.getX(), e.getY());
            }
        }

    }

    class BottomBorder extends AbstractBorder implements Serializable
    {

        private static final long serialVersionUID = SerialVersion.APPVERSION;

        protected Color color = new Color(0x898c95);
        protected int thickness = 1;
        protected int gap = 1;

        public BottomBorder()
        {}

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
        {
            Color old = g.getColor();
            g.setColor(color);
            for (int i = 0; i < thickness; i++)
                g.drawLine(x, y + height - i - 1, x + width, y + height - i - 1);
            g.setColor(old);
        }

        @Override
        public Insets getBorderInsets(Component c) 
        { return new Insets(0, 0, gap, 0); }

        @Override
        public Insets getBorderInsets(Component c, Insets insets)
        {
            insets.left = 0;
            insets.top = 0;
            insets.right = 0;
            insets.bottom = gap;
            return insets;
        }

        @Override
        public boolean isBorderOpaque() 
        { return false; }

    }

}
