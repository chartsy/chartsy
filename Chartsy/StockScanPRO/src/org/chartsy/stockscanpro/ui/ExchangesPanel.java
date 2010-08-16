package org.chartsy.stockscanpro.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.openide.util.NbBundle;

/**
 *
 * @author Viorel
 */
public class ExchangesPanel extends JPanel
{

	private ExchangePopup exchangePopup;
	private JLabel exchangeLbl;
	private JButton exchangeBtn;

	public ExchangesPanel()
	{
		super(new FlowLayout(FlowLayout.LEADING));
		setOpaque(false);
		
		initComponents();
	}

	private void initComponents()
	{
		exchangePopup = new ExchangePopup();

		exchangeLbl = new JLabel(
			NbBundle.getMessage(ExchangesPanel.class, "Exchange_Lbl",
			exchangePopup.getSelectedExchanges()));
		exchangeLbl.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		exchangeLbl.setOpaque(false);
		exchangeLbl.setFont(getExtraBoldFont(exchangeLbl.getFont()));
		exchangePopup.setExchangeListener(exchangeLbl);

		exchangeBtn = new JButton(
			NbBundle.getMessage(ExchangesPanel.class, "Exchange_Btn"));
		exchangeBtn.setOpaque(false);
		exchangeBtn.setBorder(BorderFactory.createEmptyBorder(1, 11, 1, 1));
		exchangeBtn.setMargin(new Insets(0, 0, 0, 0));
		exchangeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		exchangeBtn.setHorizontalAlignment(SwingConstants.CENTER);
		exchangeBtn.setBorderPainted(false);
        exchangeBtn.setFocusPainted(false);
        exchangeBtn.setContentAreaFilled(false);
        exchangeBtn.setRolloverEnabled(true);
        exchangeBtn.setFocusable(true);
        exchangeBtn.setFont(getExtraBoldFont(exchangeBtn.getFont()));
        exchangeBtn.setForeground(new Color(0x203673));
		exchangeBtn.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                exchangePopup.show(exchangeBtn, 11, exchangeBtn.getHeight());
            }
        });
		exchangeBtn.addMouseListener(new MouseAdapter()
		{
			public @Override void mouseEntered(MouseEvent e)
			{
				exchangeBtn.setForeground(new Color(0x284693));
				exchangeBtn.setFont(getUnderlineFont(exchangeBtn.getFont()));
			}
			public @Override void mouseExited(MouseEvent e)
			{
				exchangeBtn.setForeground(new Color(0x203673));
				exchangeBtn.setFont(getNonUnderlineFont(exchangeBtn.getFont()));
			}
		});

		add(exchangeLbl);
		add(exchangeBtn);
	}

	public Object[] getSelectedExchanges()
	{
		return exchangePopup.getSelectedItems();
	}

	private Font getExtraBoldFont(Font font)
	{
		Map attr = font.getAttributes();
		attr.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_EXTRABOLD);
		return font.deriveFont(attr);
	}

	private Font getUnderlineFont(Font font)
	{
		Map attr = font.getAttributes();
		attr.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		return font.deriveFont(attr);
	}

	private Font getNonUnderlineFont(Font font)
	{
		Map attr = font.getAttributes();
		attr.put(TextAttribute.UNDERLINE, -1);
		return font.deriveFont(attr);
	}

}
