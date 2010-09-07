package org.chartsy.main.welcome.ui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import org.chartsy.main.welcome.content.Constants;
import org.chartsy.main.welcome.content.Logo;
import org.chartsy.main.welcome.content.SpringUtilities;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Viorel
 */
public class Tutorials extends JPanel implements Constants
{

	public Tutorials()
	{
		super(new SpringLayout());
		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		initComponents();
	}

	private void initComponents()
	{
		JLabel label;

		label = new JLabel(ImageUtilities.loadImageIcon(TUTS_ICON, true));
		label.setHorizontalAlignment(JLabel.LEFT);
		label.setOpaque(false);
		label.setBorder(BorderFactory.createEmptyBorder());
		add(label);

		add(Logo.createLogoLink(TUT_VID_ICON, TUTS_URL));

		SpringUtilities.makeCompactGrid(this,
			2, 1,
			5, 5,
			5, 5);
	}

}
