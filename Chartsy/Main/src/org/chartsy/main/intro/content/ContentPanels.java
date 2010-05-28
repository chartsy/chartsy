package org.chartsy.main.intro.content;

import javax.swing.JPanel;
import org.chartsy.main.intro.ui.ForumPanel;
import org.chartsy.main.intro.ui.GetStartedPanel;
import org.chartsy.main.intro.ui.PluginsPanel;
import org.chartsy.main.intro.ui.WelcomePanel;

/**
 *
 * @author viorel.gheba
 */
public class ContentPanels {

    private ContentPanels() {}

    public static JPanel getPanel(String label) {
        String s = BundleSupport.getContent(label);
        if (s.equals(BundleSupport.getContent("Welcome"))) return new WelcomePanel();
        else if (s.equals(BundleSupport.getContent("GetStarted"))) return new GetStartedPanel();
        else if (s.equals(BundleSupport.getContent("Plugins"))) return new PluginsPanel();
        else return new ForumPanel();
    }

}
