package org.chartsy.main.intro.ui;

import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import org.chartsy.main.intro.content.Constants;

/**
 *
 * @author Viorel
 */
public class NoConnection extends JLabel implements Constants
{

    public NoConnection()
    {
        super();
        setOpaque(false);
        setFont(WELCOME_LABEL_FONT);
        setForeground(COLOR_TAB_CONTENT_BACKGROUND);
        setText("Please check your internet connection!");
        setToolTipText("Please check your internet connection!");
        setVerticalAlignment(SwingConstants.CENTER);
        setVerticalTextPosition(SwingConstants.CENTER);
        setHorizontalAlignment(SwingConstants.CENTER);
        setHorizontalTextPosition(SwingConstants.CENTER);
        setPreferredSize(new Dimension(1800, 74));
    }

}
