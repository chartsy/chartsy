package org.chartsy.welcome.ui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import org.chartsy.welcome.content.Constants;
import org.chartsy.welcome.content.SpringUtilities;
import org.chartsy.welcome.content.WebLink;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Viorel
 */
public class FollowUs extends JPanel implements Constants
{

	public FollowUs()
	{
		super(new SpringLayout());
		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		initComponents();
	}

	private void initComponents()
	{
		JLabel label;

		label = new JLabel(ImageUtilities.loadImageIcon(FOLLOW_ICON, true));
		label.setHorizontalAlignment(JLabel.LEFT);
		label.setOpaque(false);
		label.setBorder(BorderFactory.createEmptyBorder());
		add(label);

		add(WebLink.createWebLink("twitter", true));
		add(WebLink.createWebLink("facebook", true));
		add(WebLink.createWebLink("youtube", true));

		SpringUtilities.makeCompactGrid(this,
			4, 1,
			5, 5,
			5, 5);
	}

}
