package org.chartsy.stockscanpro.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

/**
 *
 * @author Viorel
 */
public class ResultPanel extends JPanel
{

	private String scanTitle = "";
	private String scan = "";
	
	private JLabel scanTitleLbl;
	private JLabel queryLbl;
	private JLabel resultsLbl;
	private ScanResultList resultList;

	private String resultsNr;
	private String[] results;

	public ResultPanel
		(String scanTitle, String scan)
	{
		super(new SpringLayout());
		setBackground(Color.white);

		this.scanTitle = scanTitle;
		this.scan = scan;

		initComponents();
	}

	private void initComponents()
	{
		scanTitleLbl = new JLabel(scanTitle, JLabel.LEFT);
		add(scanTitleLbl);

		queryLbl = new JLabel("", JLabel.LEFT);
		add(queryLbl);

		resultsLbl = new JLabel("", JLabel.LEFT);
		add(resultsLbl);

		resultList = new ScanResultList();
		JScrollPane scrollPane = new JScrollPane(
			resultList,
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane);

		SpringUtilities.makeCompactGrid(this,
			4, 1,	// rows, cols
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

	public void setResponce(String responce)
	{
		if (responce != null || !responce.isEmpty())
		{
			StringTokenizer tokenizer = new StringTokenizer(responce, "\n");
			if (tokenizer.hasMoreTokens())
				resultsNr = tokenizer.nextToken();

			List<String> list = new ArrayList<String>();
			while (tokenizer.hasMoreTokens())
				list.add(tokenizer.nextToken());
			results = list.toArray(new String[list.size()]);
		}
		initContent();
	}

	private void initContent()
	{
		if (resultsNr != null)
			resultsLbl.setText(resultsNr);
		if (results != null)
			resultList.setListData(results);
	}

	public String getScanTitle()
	{
		return scanTitle;
	}

	public String getScan()
	{
		return scan;
	}

}
