package org.chartsy.stockscanpro.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.text.BreakIterator;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

/**
 *
 * @author Viorel
 */
public class ErrorPanel extends JPanel
{

	private String scanTitle = "";
	private String scan = "";
	private String error = "";

	private JLabel scanTitleLbl;
	private JLabel queryLbl;
	private JLabel errorLbl;

	public ErrorPanel
		(String scanTitle, String scan, String error)
	{
		super(new SpringLayout());
		setBackground(Color.white);

		this.scanTitle = scanTitle;
		this.scan = scan;
		this.error = "<html>" + error.replace("\n", "<br>") + "</html>";

		initComponents();
	}

	private void initComponents()
	{
		scanTitleLbl = new JLabel(scanTitle, JLabel.LEFT);
		add(scanTitleLbl);

		queryLbl = new JLabel("", JLabel.LEFT);
		add(queryLbl);

		errorLbl = new JLabel(error, JLabel.LEFT);
		errorLbl.setForeground(Color.red);
		add(errorLbl);

		SpringUtilities.makeCompactGrid(this,
			3, 1,	// rows, cols
			5, 5,	// initialX, initialY
			5, 5);	// xPad, yPad
	}
	
	protected @Override void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if (queryLbl.getText().equals(""))
			wrapedLabel(queryLbl, scan);
	}

	private void wrapedLabel(JLabel label, String text)
	{
		FontMetrics fm = label.getFontMetrics(label.getFont());
		Container container = label.getParent();
		int containerWidth = container.getWidth();

		BreakIterator boundary = BreakIterator.getWordInstance();
		boundary.setText(text);

		StringBuilder trial = new StringBuilder();
		StringBuilder real = new StringBuilder("<html>");

		int start = boundary.first();
		for (int end = boundary.next();
			end != BreakIterator.DONE;
			start = end, end = boundary.next())
		{
			String word = text.substring(start, end);
			trial.append(word);
			int trialWidth
				= SwingUtilities.computeStringWidth(fm, trial.toString());
			if (trialWidth > containerWidth)
			{
				trial = new StringBuilder(word);
				real.append("<br>");
			}
			real.append(word);
		}

		real.append("</html>");
		label.setText(real.toString());
	}

}
