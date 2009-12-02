package org.chartsy.main.intro;

import java.awt.BorderLayout;
import java.io.IOException;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 *
 * @author viorel.gheba
 */
public final class WebBrowser extends JPanel implements HyperlinkListener {

    public JEditorPane window_edp;
    public JScrollPane window_scrl;
    private static final String LINK = "http://www.chartsy.org/index.php?option=com_startpage&view=startpage";

    public WebBrowser() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                System.out.println("Starting");
                setLayout(new BorderLayout());
                try {
                    System.out.println("Aquiring URL");
                    window_edp = new JEditorPane();
                    window_edp.setEditable(false);
                    window_edp.setPage(LINK);

                    window_scrl = new JScrollPane(window_edp, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                    add(window_scrl, BorderLayout.CENTER);
                } catch (IOException e) {
                    System.err.println( "can't find URL" );
                }
            }
        });
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
