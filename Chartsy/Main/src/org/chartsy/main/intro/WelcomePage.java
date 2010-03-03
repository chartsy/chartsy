package org.chartsy.main.intro;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.Serializable;
import org.chartsy.main.intro.content.Constants;
import org.chartsy.main.intro.ui.StartPageContent;
import org.openide.util.ImageUtilities;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author viorel.gheba
 */
public class WelcomePage extends TopComponent implements Constants {

    private static WelcomePage instance;
    private static final String PREFERRED_ID = "Welcome";

    public static synchronized WelcomePage getDefault() {
        if (instance == null) instance = new WelcomePage();
        return instance;
    }

    public static synchronized WelcomePage findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) { return getDefault(); }
        if (win instanceof WelcomePage) { return (WelcomePage) win; }
        return getDefault();
    }

    private WelcomePage() {
        initComponents();
        
        putClientProperty(TopComponent.PROP_CLOSING_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_DRAGGING_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_UNDOCKING_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_MAXIMIZATION_DISABLED, Boolean.TRUE);

        setLayout(new BorderLayout());
        add(new StartPageContent(), BorderLayout.CENTER);
    }

    private void initComponents() {
        setName("Welcome");
        setToolTipText("Welcome");
        setDisplayName("Welcome");
        setHtmlDisplayName("Welcome");
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }
    @Override
    protected Object writeReplace() {
        return new ResolvableHelper();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(COLOR_CONTENT_BACKGROUND);
        g2.fillRect(0, 0, getWidth(), getHeight());

        Image image = ImageUtilities.loadImage(IMAGE_LIGHT_EFFECT, true);
        g2.drawImage(image, 0, 0, this);
    }



    final static class ResolvableHelper implements Serializable {

        private static final long serialVersionUID = 111L;

        private ResolvableHelper() {}

        public Object readResolve() {
            return WelcomePage.getDefault();
        }

    }

}
