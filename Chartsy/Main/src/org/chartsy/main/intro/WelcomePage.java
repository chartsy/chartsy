package org.chartsy.main.intro;

import java.awt.BorderLayout;
import java.io.Serializable;
import org.chartsy.main.intro.ui.StartPageContent;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author viorel.gheba
 */
public class WelcomePage extends TopComponent {

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


    final static class ResolvableHelper implements Serializable {

        private static final long serialVersionUID = 111L;

        private ResolvableHelper() {}

        public Object readResolve() {
            return WelcomePage.getDefault();
        }

    }

}
