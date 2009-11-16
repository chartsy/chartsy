package org.chartsy.main;

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

        Vector exchanges = UpdaterManager.getDefault().getActiveUpdater().getExchanges();
        for (Object obj : exchanges)
            lstExchange.addItem(obj);
        if (exchanges == null) {
            this.lblExchange.setVisible(false);
            this.lstExchange.setVisible(false);
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
        lblLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/chartsy/main/icons/chartsy_component_logo.png"))); // NOI18N
        lblLogo.setText(org.openide.util.NbBundle.getMessage(NewChartDialog.class, "NewChartDialog.lblLogo.text")); // NOI18N
        lblLogo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblLogo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE)
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblSymbol)
                            .add(lblChart)
                            .add(lblExchange))
                        .add(32, 32, 32)
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lstChart, 0, 343, Short.MAX_VALUE)
                            .add(txtSymbol, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                            .add(lstExchange, 0, 343, Short.MAX_VALUE)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, mainPanelLayout.createSequentialGroup()
                        .add(btnNewChart)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnCancel)))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(lblLogo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                .add(18, 18, 18)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblSymbol)
                    .add(txtSymbol, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblChart)
                    .add(lstChart, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblExchange)
                    .add(lstExchange, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnCancel)
                    .add(btnNewChart))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 452, Short.MAX_VALUE)
            .add(mainPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 300, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, mainPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    /**
    *NewChartDialogrgs the command line arguments
    */
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
