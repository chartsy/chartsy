package org.chartsy.main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;
import javax.swing.JComboBox;
import org.chartsy.main.managers.ChartManager;
import org.chartsy.main.managers.UpdaterManager;
import org.chartsy.main.updater.AbstractUpdater;
import org.openide.windows.WindowManager;

/**
 *
 * @author viorel.gheba
 */
public class NewChartDialog extends javax.swing.JDialog {

    private NewChart newChart;

    public NewChartDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setTitle("New Chart");
        parent.setIconImage(WindowManager.getDefault().getMainWindow().getIconImage());
        getRootPane().setDefaultButton(btnNewChart);
        initForm();
    }

    public void setListener(NewChart newChart) {
        this.newChart = newChart;
    }

    private void initForm() {
        lblLogo.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        lblLogo.setVerticalAlignment(javax.swing.JLabel.CENTER);

        Vector charts = ChartManager.getDefault().getCharts();
        for (Object obj : charts)
            lstChart.addItem(obj);

        Vector dataProviders = UpdaterManager.getDefault().getUpdaters();
        for (Object obj : dataProviders) {
            lstDataProvider.addItem(obj);
        }

        this.txtSymbol.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) { txtSymbol.setText(txtSymbol.getText().toUpperCase()); }
            public void keyPressed(KeyEvent e) { txtSymbol.setText(txtSymbol.getText().toUpperCase()); }
            public void keyReleased(KeyEvent e) { txtSymbol.setText(txtSymbol.getText().toUpperCase()); }
        });
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        mainPanel.setMinimumSize(new java.awt.Dimension(400, 168));

        lblSymbol.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblSymbol.setText(org.openide.util.NbBundle.getMessage(NewChartDialog.class, "NewChartDialog.lblSymbol.text")); // NOI18N

        lblChart.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblChart.setText(org.openide.util.NbBundle.getMessage(NewChartDialog.class, "NewChartDialog.lblChart.text")); // NOI18N

        lblExchange.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblExchange.setText(org.openide.util.NbBundle.getMessage(NewChartDialog.class, "NewChartDialog.lblExchange.text")); // NOI18N

        txtSymbol.setText(org.openide.util.NbBundle.getMessage(NewChartDialog.class, "NewChartDialog.txtSymbol.text")); // NOI18N

        lblLogo.setBackground(new java.awt.Color(255, 255, 255));
        lblLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/chartsy/main/icons/chartsy_component_logo.png"))); // NOI18N
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

        org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblLogo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
                    .add(lblChart)
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, mainPanelLayout.createSequentialGroup()
                                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(lblDataProvider)
                                    .add(lblExchange))
                                .add(20, 20, 20)
                                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(lstExchange, 0, 361, Short.MAX_VALUE)
                                    .add(lstChart, 0, 361, Short.MAX_VALUE)
                                    .add(lstDataProvider, 0, 361, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, mainPanelLayout.createSequentialGroup()
                                        .add(btnNewChart)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(btnCancel))))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, mainPanelLayout.createSequentialGroup()
                                .add(lblSymbol)
                                .add(56, 56, 56)
                                .add(txtSymbol, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 361, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .add(26, 26, 26)))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(lblLogo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                .add(18, 18, 18)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblSymbol)
                    .add(txtSymbol, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
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
        this.newChart.setSymbol("");
        this.newChart.setUpdater("");
        this.newChart.setExchange("");
        this.newChart.setChart("");
        setVisible(false);
}//GEN-LAST:event_btnCancelActionPerformed

    private void btnNewChartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewChartActionPerformed
        String symbol = txtSymbol.getText();
        AbstractUpdater au = UpdaterManager.getDefault().getUpdater((String) lstDataProvider.getSelectedItem());
        String exchange = (lstExchange.isVisible() ? au.getSufix(lstExchange.getSelectedItem()) : "");
        String chart = (String) lstChart.getSelectedItem();
        this.newChart.setSymbol(symbol);
        this.newChart.setUpdater((String) lstDataProvider.getSelectedItem());
        this.newChart.setExchange(exchange);
        this.newChart.setChart(chart);
        setVisible(false);
}//GEN-LAST:event_btnNewChartActionPerformed

    private void lstDataProviderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lstDataProviderActionPerformed
        JComboBox list = (JComboBox) evt.getSource();
        Object obj = list.getSelectedItem();
        Vector exchanges = UpdaterManager.getDefault().getUpdater(obj).getExchanges();
        for (Object item : exchanges)
            lstExchange.addItem(item);
        if (exchanges == null) {
            this.lblExchange.setVisible(false);
            this.lstExchange.setVisible(false);
        }
    }//GEN-LAST:event_lstDataProviderActionPerformed

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
