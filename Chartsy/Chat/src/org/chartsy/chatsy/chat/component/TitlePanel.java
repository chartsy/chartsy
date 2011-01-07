package org.chartsy.chatsy.chat.component;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.geom.AffineTransform;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public final class TitlePanel extends JPanel
{
	
    private final JLabel titleLabel = new JLabel();
    private final WrappedLabel descriptionLabel = new WrappedLabel();
    private final JLabel iconLabel = new JLabel();
    private final GridBagLayout gridBagLayout = new GridBagLayout();

    public TitlePanel(String title, String description, Icon icon, boolean showDescription)
	{
		if (icon != null)
			iconLabel.setIcon(icon);
        setTitle(title);
        setDescription(description);
        setLayout(gridBagLayout);
        descriptionLabel.setBackground(Color.white);

        if (showDescription)
		{
            add(iconLabel, new GridBagConstraints(2, 0, 1, 2, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
            add(descriptionLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 9, 5, 5), 0, 0));
            add(titleLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
            setBackground(Color.white);

            titleLabel.setFont(new Font("Dialog", Font.BOLD, 11));
            descriptionLabel.setFont(new Font("Dialog", 0, 10));
        }
        else
		{
            final JPanel panel = new ImagePanel();
            panel.setBorder(BorderFactory.createEtchedBorder());

            panel.setLayout(new GridBagLayout());
            panel.add(titleLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
            panel.add(iconLabel, new GridBagConstraints(2, 0, 1, 2, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

            titleLabel.setVerticalTextPosition(JLabel.CENTER);
            titleLabel.setFont(new Font("Dialog", Font.BOLD, 14));
            titleLabel.setForeground(Color.black);
            descriptionLabel.setFont(new Font("Dialog", 0, 10));
            add(panel, new GridBagConstraints(0, 0, 1, 0, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 2, 2), 0, 0));
        }
    }

    public final void setIcon(Icon icon)
	{
        titleLabel.setIcon(icon);
    }

    public final void setTitle(String title)
	{
        titleLabel.setText(title);
    }

    public final void setDescription(String desc)
	{
        descriptionLabel.setText(desc);
    }

    public class ImagePanel extends JPanel 
	{

        final ImageIcon icons = null;

        public ImagePanel()
		{
        }

        public ImagePanel(LayoutManager layout)
		{
            super(layout);
        }

        public void paintComponent(Graphics g)
		{
			if (icons != null)
			{
				Image backgroundImage = icons.getImage();
				double scaleX = getWidth() / (double)backgroundImage.getWidth(null);
				double scaleY = getHeight() / (double)backgroundImage.getHeight(null);
				AffineTransform xform = AffineTransform.getScaleInstance(scaleX, scaleY);
				((Graphics2D)g).drawImage(backgroundImage, xform, this);
			}
        }

    }

}
