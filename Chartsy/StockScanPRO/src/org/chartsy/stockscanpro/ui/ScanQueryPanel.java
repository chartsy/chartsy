package org.chartsy.stockscanpro.ui;

import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import org.netbeans.editor.EditorUI;
import org.netbeans.editor.Utilities;
import org.openide.util.NbBundle;

/**
 *
 * @author Viorel
 */
public class ScanQueryPanel extends JPanel
{

	private JLabel queryLbl;
	private JEditorPane queryTxt;

	public ScanQueryPanel()
	{
		super(new SpringLayout());
		setOpaque(false);

		initComponents();
	}

	private void initComponents()
	{
		queryLbl = new JLabel(
			NbBundle.getMessage(ScanQueryPanel.class, "Query_Lbl"),
			JLabel.LEFT);
		queryLbl.setOpaque(false);
		add(queryLbl);

		queryTxt = new JEditorPane();
		queryTxt.setContentType("text/x-scan");
		EditorUI editorUI = Utilities.getEditorUI(queryTxt);
		JComponent mainComp = null;
		if (editorUI != null)
			mainComp = editorUI.getExtComponent();
		if (mainComp == null)
			mainComp = new javax.swing.JScrollPane(queryTxt);
		mainComp.setPreferredSize(new Dimension(520, 200));
		queryLbl.setLabelFor(queryTxt);
		add(mainComp);

		SpringUtilities.makeCompactGrid(this,
			2, 1, 
			5, 5,
			5, 5);
	}

	public void setQuery(String query)
	{
		queryTxt.setText(query);
	}

	public String getQuery()
	{
		return queryTxt.getText();
	}

}
