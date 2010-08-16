package org.chartsy.main;

import java.util.Collections;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.text.AbstractDocument;
import org.chartsy.main.chart.Chart;
import org.chartsy.main.data.DataProvider;
import org.chartsy.main.data.Exchange;
import org.chartsy.main.data.Stock;
import org.chartsy.main.managers.ChartManager;
import org.chartsy.main.managers.DataProviderManager;
import org.chartsy.main.utils.UppercaseDocumentFilter;
import org.chartsy.main.utils.autocomplete.StockAutoCompleter;
import org.openide.windows.WindowManager;

/**
 *
 * @author viorel.gheba
 */
public class NewChartDialog extends javax.swing.JDialog {

    private static final String defaultDataProvider = "MrSwing";
	private StockAutoCompleter completer = null;

    private Stock stock = null;
    private DataProvider dataProvider = null;
    private Chart chart = null;
   
    public NewChartDialog(java.awt.Frame parent, boolean modal)
    {
        super(parent, modal);
        setTitle("New Chart");
        initComponents();
        parent.setIconImage(WindowManager.getDefault().getMainWindow().getIconImage());
        getRootPane().setDefaultButton(btnNewChart);
        initForm();
    }


    private void initForm()
    {
        dataProvider = DataProviderManager.getDefault().getDataProvider(defaultDataProvider);
        List<String> dataProviders = DataProviderManager.getDefault().getDataProviders();
        Collections.sort(dataProviders);
        
        lstDataProvider.setMaximumRowCount(dataProviders.size());
        for (String s : dataProviders)
            lstDataProvider.addItem(s);
        lstDataProvider.setSelectedItem(defaultDataProvider);

        List<String> charts = ChartManager.getDefault().getCharts();
        Collections.sort(charts);
        lstChart.setMaximumRowCount(charts.size());
        for (String s : charts)
            lstChart.addItem(s);

        ((AbstractDocument) txtSymbol.getDocument()).setDocumentFilter(new UppercaseDocumentFilter());
		completer = new StockAutoCompleter(txtSymbol);
		completer.setDataProvider(dataProvider);
    }

    public Stock getStock() 
    { 
		return stock;
	}

    public DataProvider getDataProvider() 
    { 
		return dataProvider;
	}

    public Chart getChart() 
    { 
		return chart;
	}

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        lblSymbol = new javax.swing.JLabel();
        lblChart = new javax.swing.JLabel();
        lblExchange = new javax.swing.JLabel();
        txtSymbol = new javax.swing.JTextField();
        lstChart = new javax.swing.JComboBox();
        lstExchange = new javax.swing.JComboBox();
        lblLogo = new javax.swing.JLabel();
        lstDataProvider = new javax.swing.JComboBox();
        lblDataProvider = new javax.swing.JLabel();
        btnCancel = new javax.swing.JButton();
        btnNewChart = new javax.swing.JButton();
        autocompleteLbl = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        mainPanel.setMinimumSize(new java.awt.Dimension(400, 168));

        lblSymbol.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblSymbol.setText(org.openide.util.NbBundle.getMessage(NewChartDialog.class, "NewChartDialog.lblSymbol.text")); // NOI18N

        lblChart.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblChart.setText(org.openide.util.NbBundle.getMessage(NewChartDialog.class, "NewChartDialog.lblChart.text")); // NOI18N

        lblExchange.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblExchange.setText(org.openide.util.NbBundle.getMessage(NewChartDialog.class, "NewChartDialog.lblExchange.text")); // NOI18N

        txtSymbol.setColumns(6);
        txtSymbol.setText(org.openide.util.NbBundle.getMessage(NewChartDialog.class, "NewChartDialog.txtSymbol.text")); // NOI18N
        txtSymbol.setToolTipText(org.openide.util.NbBundle.getMessage(NewChartDialog.class, "NewChartDialog.txtSymbol.toolTipText")); // NOI18N

        lstExchange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lstExchangeActionPerformed(evt);
            }
        });

        lblLogo.setBackground(new java.awt.Color(255, 255, 255));
        lblLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/chartsy/main/resources/logo.png"))); // NOI18N
        lblLogo.setText(org.openide.util.NbBundle.getMessage(NewChartDialog.class, "NewChartDialog.lblLogo.text")); // NOI18N
        lblLogo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        lstDataProvider.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lstDataProviderActionPerformed(evt);
            }
        });

        lblDataProvider.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblDataProvider.setText(org.openide.util.NbBundle.getMessage(NewChartDialog.class, "NewChartDialog.lblDataProvider.text")); // NOI18N

        btnCancel.setText(org.openide.util.NbBundle.getMessage(NewChartDialog.class, "NewChartDialog.btnCancel.text")); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnNewChart.setText(org.openide.util.NbBundle.getMessage(NewChartDialog.class, "NewChartDialog.btnNewChart.text")); // NOI18N
        btnNewChart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewChartActionPerformed(evt);
            }
        });

        autocompleteLbl.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        autocompleteLbl.setText(org.openide.util.NbBundle.getMessage(NewChartDialog.class, "NewChartDialog.autocompleteLbl.text")); // NOI18N

        org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblLogo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 489, Short.MAX_VALUE)
                            .add(mainPanelLayout.createSequentialGroup()
                                .add(lblSymbol)
                                .add(56, 56, 56)
                                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(autocompleteLbl)
                                    .add(txtSymbol, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 361, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .add(27, 27, 27)))
                        .addContainerGap())
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblChart)
                            .add(mainPanelLayout.createSequentialGroup()
                                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(lblDataProvider)
                                    .add(lblExchange))
                                .add(20, 20, 20)
                                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(lstExchange, 0, 362, Short.MAX_VALUE)
                                    .add(lstChart, 0, 362, Short.MAX_VALUE)
                                    .add(lstDataProvider, 0, 362, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, mainPanelLayout.createSequentialGroup()
                                        .add(btnNewChart)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(btnCancel)))))
                        .add(36, 36, 36))))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(lblLogo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                .add(18, 18, 18)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblSymbol)
                    .add(txtSymbol, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(3, 3, 3)
                .add(autocompleteLbl)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblDataProvider)
                    .add(lstDataProvider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblExchange)
                    .add(lstExchange, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblChart)
                    .add(lstChart, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnCancel)
                    .add(btnNewChart))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        stock = null;
        dataProvider = null;
        chart = null;
        setVisible(false);
}//GEN-LAST:event_btnCancelActionPerformed

    private void btnNewChartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewChartActionPerformed
        dataProvider = DataProviderManager.getDefault().getDataProvider((String) lstDataProvider.getSelectedItem());
        Exchange exchange = dataProvider.getExchanges()[lstExchange.getSelectedIndex()];
        stock = new Stock(txtSymbol.getText(), exchange.getSufix());
        chart = ChartManager.getDefault().getChart((String) lstChart.getSelectedItem());
        setVisible(false);
}//GEN-LAST:event_btnNewChartActionPerformed

    private void lstDataProviderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lstDataProviderActionPerformed
        JComboBox list = (JComboBox) evt.getSource();
        String provider = (String) list.getSelectedItem();
        lstExchange.removeAllItems();
        dataProvider = DataProviderManager.getDefault().getDataProvider(provider);
		if (completer != null)
			completer.setDataProvider(dataProvider);
        Exchange[] exchanges = dataProvider.getExchanges();
        if (exchanges == null || exchanges.length == 0)
        {
            lblExchange.setVisible(false);
            lstExchange.setVisible(false);
        }
        else
        {
            for (Exchange exchange : exchanges)
                lstExchange.addItem(exchange.getExchange());
        }
    }//GEN-LAST:event_lstDataProviderActionPerformed

    private void lstExchangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lstExchangeActionPerformed
        Object obj = lstDataProvider.getSelectedItem();
        if (obj != null)
        {
            DataProvider dp = DataProviderManager.getDefault().getDataProvider((String) obj);
            int i = lstExchange.getSelectedIndex();
            if (i != -1)
            {
                Exchange exchange = dp.getExchanges()[i];
                String symbol = txtSymbol.getText();

                if (symbol != null)
                {
                    symbol.trim();
                    String delimiter = ".";

                    if (symbol.contains(delimiter))
                    {
                        int index = symbol.indexOf(delimiter);
                        symbol = symbol.substring(0, index);
                    }

                    txtSymbol.setText(symbol + exchange.getSufix());
                }
            }
        }
    }//GEN-LAST:event_lstExchangeActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                NewChartDialog dialog = new NewChartDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel autocompleteLbl;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnNewChart;
    private javax.swing.JLabel lblChart;
    private javax.swing.JLabel lblDataProvider;
    private javax.swing.JLabel lblExchange;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblSymbol;
    private javax.swing.JComboBox lstChart;
    private javax.swing.JComboBox lstDataProvider;
    private javax.swing.JComboBox lstExchange;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTextField txtSymbol;
    // End of variables declaration//GEN-END:variables

}
