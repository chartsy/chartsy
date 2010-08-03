package org.chartsy.stockscanpro;

import org.chartsy.stockscanpro.ui.Content;
import java.awt.BorderLayout;
import java.util.logging.Logger;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Viorel
 */
public class StockScreenerComponent extends TopComponent
{

	private static StockScreenerComponent instance;
    private static final String PREFERRED_ID = "StockScreenerComponent";
    private static final Logger LOG = Logger.getLogger(StockScreenerComponent.class.getPackage().getName());

    public StockScreenerComponent()
    {
        setName(NbBundle.getMessage(StockScreenerComponent.class, "CTL_StockScreenerComponent"));
        setToolTipText(NbBundle.getMessage(StockScreenerComponent.class, "HINT_StockScreenerComponent"));
		setIcon(ImageUtilities.loadImage("org/chartsy/stockscanpro/resources/stock.png", true));
        
        putClientProperty(TopComponent.PROP_DRAGGING_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_MAXIMIZATION_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_UNDOCKING_DISABLED, Boolean.TRUE);

        setLayout(new BorderLayout());
        add(new Content(), BorderLayout.CENTER);
    }

    public @Override int getPersistenceType()
    {
        return TopComponent.PERSISTENCE_NEVER;
    }

    protected @Override String preferredID()
    {
        return PREFERRED_ID;
    }

	public static synchronized StockScreenerComponent getDefault()
	{
        if (instance == null)
            instance = new StockScreenerComponent();
        return instance;
    }

	public static synchronized StockScreenerComponent findInstance()
	{
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);

		if (win == null)
		{
            LOG.warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }

        if (win instanceof StockScreenerComponent)
		{
            return (StockScreenerComponent) win;
        }

        LOG.warning(
                "There seem to be multiple components with the '" + PREFERRED_ID
                + "' ID. That is a potential source of errors and unexpected behavior.");

        return getDefault();
    }

}
