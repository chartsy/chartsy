package org.chartsy.welcome.ui;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.chartsy.welcome.content.Constants;
import org.openide.util.NbBundle;

/**
 *
 * @author Viorel
 */
public class Description extends JPanel implements Constants
{

	public Description()
	{
		super(new BorderLayout());
		setOpaque(false);

		JLabel desc = new JLabel(
			NbBundle.getMessage(Description.class, "DESC_Chartsy"));
		desc.setHorizontalAlignment(JLabel.LEFT);
		desc.setVerticalAlignment(JLabel.CENTER);
		desc.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		desc.setOpaque(false);
		desc.setFont(BUTTON_FONT);
		desc.setForeground(LINK_COLOR);
		add(desc, BorderLayout.CENTER);
	}

}
