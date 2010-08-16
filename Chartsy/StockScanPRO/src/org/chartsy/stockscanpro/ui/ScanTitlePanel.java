package org.chartsy.stockscanpro.ui;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import org.openide.util.NbBundle;

/**
 *
 * @author Viorel
 */
public class ScanTitlePanel extends JPanel
{

	private JLabel scanTitleLbl;
	private JTextField scanTitleTxt;

	public ScanTitlePanel()
	{
		super(new SpringLayout());
		setOpaque(false);
		
		initComponents();
	}

	private void initComponents()
	{
		scanTitleLbl = new JLabel(
			NbBundle.getMessage(ScanTitlePanel.class, "ScanTitle_Lbl"),
			JLabel.LEFT);
		scanTitleLbl.setOpaque(false);
		add(scanTitleLbl);

		scanTitleTxt = new JTextField(50);
		scanTitleTxt.setText(
			NbBundle.getMessage(ScanTitlePanel.class, "ScanTitle_Txt"));
		scanTitleTxt.addFocusListener(new FocusListener()
		{
			public void focusGained(FocusEvent e)
			{
				if (scanTitleTxt.getText().equals(
					NbBundle.getMessage(QueryPanel.class, "ScanTitle_Txt")))
					scanTitleTxt.setText("");
			}
			public void focusLost(FocusEvent e)
			{
				if (scanTitleTxt.getText().equals(""))
					scanTitleTxt.setText(
						NbBundle.getMessage(QueryPanel.class, "ScanTitle_Txt"));
			}
		});
		scanTitleLbl.setLabelFor(scanTitleTxt);
		add(scanTitleTxt);

		SpringUtilities.makeCompactGrid(this,
			2, 1,
			5, 5,
			5, 5);
	}

	public void setScanTitle(String title)
	{
		scanTitleTxt.setText(title);
	}

	public String getScanTitle()
	{
		return scanTitleTxt.getText();
	}

}
