package org.chartsy.main.favorites;

import java.awt.BorderLayout;
import java.util.logging.Logger;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
//import org.openide.util.ImageUtilities;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

@ConvertAsProperties(dtd = "-//org.chartsy.main.favorites//Favorites//EN",
autostore = false)
public final class FavoritesTopComponent extends TopComponent implements ExplorerManager.Provider
{

    private static FavoritesTopComponent instance;
    private static final Logger LOG = Logger.getLogger(FavoritesTopComponent.class.getPackage().getName());
    //static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    private static final String PREFERRED_ID = "FavoritesTopComponent";

    private transient ExplorerManager explorerManager = new ExplorerManager();
    private BeanTreeView beanTreeView;

    public FavoritesTopComponent()
    {
        initComponents();
        setName(NbBundle.getMessage(FavoritesTopComponent.class, "CTL_FavoritesTopComponent"));
        setToolTipText(NbBundle.getMessage(FavoritesTopComponent.class, "HINT_FavoritesTopComponent"));
        //setIcon(ImageUtilities.loadImage(ICON_PATH, true));
        putClientProperty(TopComponent.PROP_CLOSING_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_DRAGGING_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_MAXIMIZATION_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_UNDOCKING_DISABLED, Boolean.TRUE);
        initializeUIElements();
    }

    private void initializeUIElements()
    {
        setLayout(new BorderLayout());
        beanTreeView = new BeanTreeView();
        add(beanTreeView, BorderLayout.CENTER);

        associateLookup(ExplorerUtils.createLookup(explorerManager, getActionMap()));
        explorerManager.setRootContext(new AbstractNode(new FolderChildren()));
        explorerManager.getRootContext().setDisplayName("Favorites");
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDoubleBuffered(true);
        setPreferredSize(new java.awt.Dimension(400, 200));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    public static synchronized FavoritesTopComponent getDefault()
    {
        if (instance == null)
        {
            instance = new FavoritesTopComponent();
        }
        return instance;
    }

    public static synchronized FavoritesTopComponent findInstance()
    {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null)
        {
            LOG.warning("Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof FavoritesTopComponent)
        {
            return (FavoritesTopComponent) win;
        }
        LOG.warning("There seem to be multiple components with the '" + PREFERRED_ID + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    public @Override int getPersistenceType()
    {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    public @Override void componentOpened()
    {
        // TODO add custom code on component opening
    }

    public @Override void componentClosed()
    {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p)
    {
        p.setProperty("version", "1.0");
    }

    Object readProperties(java.util.Properties p)
    {
        if (instance == null)
        {
            instance = this;
        }
        instance.readPropertiesImpl(p);
        return instance;
    }

    private void readPropertiesImpl(java.util.Properties p)
    {
        String version = p.getProperty("version");
    }

    protected @Override String preferredID() 
    { return PREFERRED_ID; }

    public ExplorerManager getExplorerManager() 
    { return explorerManager; }
    
}
