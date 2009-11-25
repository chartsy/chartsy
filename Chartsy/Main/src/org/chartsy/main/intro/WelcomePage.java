package org.chartsy.main.intro;

import java.awt.BorderLayout;
import java.io.ObjectStreamException;
import java.io.Serializable;
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
        if (win == null) {
            return getDefault();
        }
        if (win instanceof WelcomePage) {
            return (WelcomePage) win;
        }
        return getDefault();
    }

    private WelcomePage() {
        setLayout(new BorderLayout());
        initComponents();
        setName("Welcome");
        setToolTipText("Welcome");
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    @Override
    public String preferredID() { return PREFERRED_ID; }

    private void initComponents() {
        WebBrowser web = new WebBrowser();
        add(web, BorderLayout.CENTER);
    }

    @Override
    protected Object writeReplace() throws ObjectStreamException {
        return new WelcomeHelper();
    }

    final static class WelcomeHelper implements Serializable {

        private static final long serialVersionUID = 101L;

        public Object readResolve() {
            return WelcomePage.getDefault();
        }

    }

}
