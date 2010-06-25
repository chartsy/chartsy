package org.chartsy.stockscanpro.ui;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Viorel
 */
public class QueryPanel extends JPanel
{

    public JPanel buttonContainer;
    public JPanel exchangeContainer;
    public JPanel formContainer;

    public QueryPanel()
    {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder());

        buttonContainer = new JPanel();
        initButtonContainerUI();

        exchangeContainer = new JPanel();
        initExchangeContainerUI();

        formContainer = new JPanel();
        initFormContainerUI();

        add(buttonContainer);
        add(exchangeContainer);
        add(formContainer);
    }

    private void initButtonContainerUI()
    {
        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.X_AXIS));
        buttonContainer.setOpaque(false);

        JButton button;
        buttonContainer.add(button = new JButton("Filter Builder"));
        buttonContainer.add(button = new JButton("Load Scan"));
        buttonContainer.add(button = new JButton("Save Scan"));
    }

    private void initExchangeContainerUI()
    {
        exchangeContainer.setLayout(new BoxLayout(exchangeContainer, BoxLayout.X_AXIS));
        exchangeContainer.setOpaque(false);

        exchangeContainer.add(new JLabel("n exchange(s) are selected."));
        exchangeContainer.add(new JButton("Change..."));
    }

    private void initFormContainerUI()
    {
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setOpaque(false);

        formContainer.add(new JLabel("Scan title"));
    }

}
