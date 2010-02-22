package org.chartsy.main.intro;

import java.awt.BorderLayout;
import java.io.ObjectStreamException;
import java.io.Serializable;
import org.chartsy.main.intro.ui.StartPageContent;
//import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author viorel.gheba
 */
//@ConvertAsProperties(dtd = "-//org.chartsy.main.intro//Welcome//EN",
//autostore = false)
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

    public int getPersistenceType() { return TopComponent.PERSISTENCE_ALWAYS; }
    public String preferredID() { return PREFERRED_ID; }
    public void componentOpened() {}
    public void componentClosed() {}

    void writeProperties(java.util.Properties p) { p.setProperty("version", "1.0"); }
    Object readProperties(java.util.Properties p) {
        if (instance == null) instance = this;
        instance.readPropertiesImpl(p);
        return instance;
    }
    private void readPropertiesImpl(java.util.Properties p) { String version = p.getProperty("version"); }

    protected Object writeReplace() throws ObjectStreamException { return new WelcomeHelper(); }

    final static class WelcomeHelper implements Serializable {
        private static final long serialVersionUID = 101L;
        public Object readResolve() { return WelcomePage.getDefault(); }
    }

}
