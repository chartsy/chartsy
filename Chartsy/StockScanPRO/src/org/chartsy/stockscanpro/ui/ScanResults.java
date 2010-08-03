package org.chartsy.stockscanpro.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import org.openide.util.NbBundle;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Viorel
 */
public class ScanResults extends TopComponent
{

	private static ScanResults instance;
	private static final String PREFERRED_ID = "ScanResults";

	private JLabel resultsLbl;
	private ScanResultList resultList;

	private String resultsNr;
	private String[] results;

	public ScanResults()
	{
		setName(NbBundle.getMessage(ScanSaver.class, "CTL_ScanSaver"));
		setDisplayName(NbBundle.getMessage(ScanSaver.class, "CTL_ScanSaver"));
		setToolTipText(NbBundle.getMessage(ScanSaver.class, "HINT_ScanSaver"));

		putClientProperty("TopComponentAllowDockAnywhere", Boolean.TRUE);
		putClientProperty(TopComponent.PROP_UNDOCKING_DISABLED, Boolean.TRUE);
		putClientProperty(TopComponent.PROP_MAXIMIZATION_DISABLED, Boolean.TRUE);

		initComponents();
	}

	public void setTitle(String exchange)
	{
		setName(exchange);
		setDisplayName(exchange);
		setToolTipText(exchange);
	}

	private void initComponents()
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		resultsLbl = new JLabel();
		resultsLbl.setOpaque(false);
		resultsLbl.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		resultsLbl.setFont(getExtraBoldFont(resultsLbl.getFont()));
		resultsLbl.setPreferredSize(new Dimension(600, 50));
		resultsLbl.setVerticalAlignment(SwingConstants.CENTER);
		resultsLbl.setHorizontalAlignment(SwingConstants.CENTER);
		add(resultsLbl);

		resultList = new ScanResultList();
		JScrollPane scrollPane = new JScrollPane(resultList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(600, 550));
		add(scrollPane);
	}

	private Font getExtraBoldFont(Font font)
	{
		Map attr = font.getAttributes();
		attr.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_EXTRABOLD);
		return font.deriveFont(attr);
	}

	private void initContent()
	{
		if (resultsNr != null)
			resultsLbl.setText(resultsNr);
		if (results != null)
			resultList.setListData(results);
	}

	public static synchronized ScanResults getDefault()
	{
		if (instance == null)
			instance = new ScanResults();
		return instance;
	}

	public static synchronized ScanResults findInstance()
	{
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null)
		{
            Logger.getLogger(ScanResults.class.getName()).warning(
				"Cannot find " + PREFERRED_ID + " component. It will not be "
				+ "located properly in the window system.");
            return getDefault();
        }
        if (win instanceof ScanResults)
		{
            return (ScanResults) win;
        }

        Logger.getLogger(ScanResults.class.getName()).warning(
			"There seem to be multiple components with the '"
			+ PREFERRED_ID + "' ID. That is a potential source of errors and "
			+ "unexpected behavior.");
        return getDefault();
    }

	public @Override void open()
	{
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		Mode mode = WindowManager.getDefault().findMode("undockedEditor");
		if (mode != null)
		{
			mode.setBounds(new Rectangle((dim.width - 600) / 2, (dim.height - 600) / 2, 600, 600));
			mode.dockInto(this);
			initContent();
			super.open();
		}
	}

	public int getPersistenceType()
	{
        return TopComponent.PERSISTENCE_NEVER;
    }

	protected String preferredID()
	{
        return PREFERRED_ID;
    }

	public void setResponce(String responce)
	{
		StringTokenizer tokenizer = new StringTokenizer(responce, "\n");
		resultsNr = tokenizer.nextToken();

		List<String> list = new ArrayList<String>();
		while (tokenizer.hasMoreTokens())
			list.add(tokenizer.nextToken());
		results = list.toArray(new String[list.size()]);
	}

}
