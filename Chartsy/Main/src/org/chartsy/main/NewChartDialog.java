package org.chartsy.main;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;
import org.chartsy.main.managers.ChartManager;
import org.chartsy.main.managers.UpdaterManager;
import org.openide.windows.WindowManager;

/**
 *
 * @author viorel.gheba
 */
public class NewChartDialog extends javax.swing.JDialog {

    private NewChart newChart;

    public NewChartDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle("New Chart");
        setBackground(Color.WHITE);
        setIconImage(WindowManager.getDefault().getMainWindow().getIconImage());
        initComponents();
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

        Vector exchanges = UpdaterManager.getDefault().getActiveUpdater().getExchanges();
        for (Object obj : exchanges)
            lstExchange.addItem(obj);
        if (exchanges == null) {
            this.lblExchange.setVisible(false);
            this.lstExchange.setVisible(false);
        }

        this.txtSymbol.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
                txtSymbol.setText(txtSymbol.getText().toUpperCase());
            }
            public void keyPressed(KeyEvent e) {
                txtSymbol.setText(txtSymbol.getText().toUpperCase());
            }
            public void keyReleased(KeyEvent e) {
                txtSymbol.setText(txtSymbol.getText().toUpperCase());
            }
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
        btnCancel = new javax.swing.JButton();
        btnNewChart = new javax.swing.JButton();
        lblLogo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel.setMinimumSize(new java.awt.Dimension(400, 168));

        lblSymbol.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblSymbol.setText(org.openide.util.NbBundle.getMessage(NewChartDialog.class, "NewChartDialog.lblSymbol.text")); // NOI18N

        lblChart.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblChart.setText(org.openide.util.NbBundle.getMessage(NewChartDialog.class, "NewChartDialog.lblChart.text")); // NOI18N

        lblExchange.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblExchange.setText(org.openide.util.NbBundle.getMessage(NewChartDialog.class, "NewChartDialog.lblExchange.text")); // NOI18N

        txtSymbol.setText(org.openide.util.NbBundle.getMessage(NewChartDialog.class, "NewChartDialog.txtSymbol.text")); // NOI18N

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

        lblLogo.setBackground(new java.awt.Color(255, 255, 255));
        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/chartsy/main/icons/chartsy_component_logo.png"))); // NOI18N
        lblLogo.setText(org.openide.util.NbBundle.getMessage(NewChartDialog.class, "NewChartDialog.lblLogo.text")); // NOI18N
        lblLogo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblLogo, javax.swing.GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblSymbol)
                            .addComponent(lblChart)
                            .addComponent(lblExchange))
                        .addGap(32, 32, 32)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lstChart, 0, 343, Short.MAX_VALUE)
                            .addComponent(txtSymbol, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                            .addComponent(lstExchange, 0, 343, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addComponent(btnNewChart)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancel)))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblLogo, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSymbol)
                    .addComponent(txtSymbol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblChart)
                    .addComponent(lstChart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblExchange)
                    .addComponent(lstExchange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel)
                    .addComponent(btnNewChart))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.newChart.setSymbol("");
        this.newChart.setChart("");
        setVisible(false);
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnNewChartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewChartActionPerformed
        String symbol = txtSymbol.getText() + (lstExchange.isVisible() ? UpdaterManager.getDefault().getActiveUpdater().getSufix(lstExchange.getSelectedItem()) : "");
        String chart = (String) lstChart.getSelectedItem();
        this.newChart.setSymbol(symbol);
        this.newChart.setChart(chart);
        setVisible(false);
    }//GEN-LAST:event_btnNewChartActionPerformed

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
    private javax.swing.JLabel lblExchange;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblSymbol;
    private javax.swing.JComboBox lstChart;
    private javax.swing.JComboBox lstExchange;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTextField txtSymbol;
    // End of variables declaration//GEN-END:variables

}
