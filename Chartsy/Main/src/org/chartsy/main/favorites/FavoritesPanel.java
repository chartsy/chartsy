package org.chartsy.main.favorites;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JPanel;

/**
 *
 * @author Viorel
 */
public class FavoritesPanel extends JPanel
{

    //private JComponent toolbox;
    //private JButton settingsBtn;
    //private JButton refreshBtn;

    public FavoritesPanel()
    {
        super(new BorderLayout());
        setOpaque(true);
        setBackground(Color.WHITE);

        //toolbox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 5));
        //toolbox.setOpaque(false);
        //add(toolbox, BorderLayout.SOUTH);

        //settingsBtn = new JButton("Settings");
        //toolbox.add(settingsBtn);

        //refreshBtn = new JButton("Refresh");
        //toolbox.add(refreshBtn);


    }

}
