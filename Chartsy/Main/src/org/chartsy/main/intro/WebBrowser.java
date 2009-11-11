package org.chartsy.main.intro;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.URL;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 *
 * @author viorel.gheba
 */
public class WebBrowser extends JPanel implements HyperlinkListener {

    public JPanel window_pnl;
    public JEditorPane window_edp;
    public JScrollPane window_scrl;

    public WebBrowser() {
        this.setLayout(new BorderLayout());
        try {
            URL url = new URL("http://www.chartsy.org/index.php?template=startpage");
            //this.window_edp = (url.openConnection().getContentLength() > 0 ? new JEditorPane("http://www.google.com") : new JEditorPane());
            this.window_edp = new JEditorPane("http://www.chartsy.org/index.php?template=startpage");
            this.window_edp.setContentType("text/html; charset=UTF-8");
            this.window_edp.setEditable(false);
            this.window_edp.addHyperlinkListener(this);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        this.window_pnl = new JPanel(new BorderLayout());
        this.window_scrl = new JScrollPane(this.window_edp);
        this.window_pnl.add(this.window_scrl);
        this.add(this.window_pnl, BorderLayout.CENTER);
    }

    public WebBrowser getWebBrowser() {
        return this;
    }

    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {
                window_edp.setPage(e.getURL());
                window_edp.repaint();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
