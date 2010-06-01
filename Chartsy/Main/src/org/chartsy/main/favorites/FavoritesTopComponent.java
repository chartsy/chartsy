package org.chartsy.main.favorites;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.logging.Logger;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
//import org.openide.util.ImageUtilities;
import org.netbeans.api.settings.ConvertAsProperties;

@ConvertAsProperties(dtd = "-//org.chartsy.main.favorites//Favorites//EN",
autostore = false)
public final class FavoritesTopComponent extends TopComponent
{

    private static FavoritesTopComponent instance;
    //static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    private static final String PREFERRED_ID = "FavoritesTopComponent";

    private FavoritesPanel mainPanel;

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
        mainPanel = new FavoritesPanel();
        add(mainPanel);

        setLayout(new LayoutManager()
        {
            public void addLayoutComponent(String name, Component comp)
            {}
            public void removeLayoutComponent(Component comp)
            {}
            public Dimension preferredLayoutSize(Container parent)
            {return new Dimension(0, 0);}
            public Dimension minimumLayoutSize(Container parent)
            {return new Dimension(0, 0);}
            public void layoutContainer(Container parent)
            {
                Insets insets = parent.getInsets();
                int width = parent.getWidth() - insets.left - insets.right;
                int height = parent.getHeight() - insets.top - insets.bottom;

                mainPanel.setBounds(insets.left, insets.top, width, height);
            }
        });
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

    public static synchronized FavoritesTopComponent getDefault() {
        if (instance == null) {
            instance = new FavoritesTopComponent();
        }
        return instance;
    }

    public static synchronized FavoritesTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(FavoritesTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof FavoritesTopComponent) {
            return (FavoritesTopComponent) win;
        }
        Logger.getLogger(FavoritesTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID
                + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    public @Override int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    public @Override void componentOpened() {
        // TODO add custom code on component opening
    }

    public @Override void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        p.setProperty("version", "1.0");
    }

    Object readProperties(java.util.Properties p) {
        if (instance == null) {
            instance = this;
        }
        instance.readPropertiesImpl(p);
        return instance;
    }

    private void readPropertiesImpl(java.util.Properties p) {
        String version = p.getProperty("version");
    }

    protected @Override String preferredID() {
        return PREFERRED_ID;
    }
}
