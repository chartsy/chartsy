package org.chartsy.main.intro;

import java.awt.BorderLayout;
import org.openide.windows.TopComponent;

/**
 *
 * @author viorel.gheba
 */
public class WelcomePage extends TopComponent {

    private static WelcomePage instance;

    public static WelcomePage getDefault() {
        if (instance == null) instance = new WelcomePage();
        return instance;
    }

    private WelcomePage() {
        setLayout(new BorderLayout());
        initComponents();
        setDisplayName("Welcome");
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_NEVER;
    }

    @Override
    public String preferredID() {
        return "Welcome";
    }

    private void initComponents() {
        WebBrowser web = new WebBrowser();
        add(web, BorderLayout.CENTER);
    }

}
