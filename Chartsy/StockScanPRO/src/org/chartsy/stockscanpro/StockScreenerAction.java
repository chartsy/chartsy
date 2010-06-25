package org.chartsy.stockscanpro;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class StockScreenerAction implements ActionListener
{

    public void actionPerformed(ActionEvent e)
    {
        StockScreenerComponent component = new StockScreenerComponent();
        component.open();
        component.requestActive();
    }
}
