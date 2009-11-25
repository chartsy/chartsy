package org.chartsy.main.dialogs;

import java.util.LinkedHashMap;
import org.chartsy.main.icons.IconUtils;
import org.chartsy.main.managers.UpdaterManager;
import org.chartsy.main.updater.AbstractUpdater;
import org.chartsy.main.utils.Stock;

/**
 *
 * @author viorel.gheba
 */
public class LoaderDialog extends javax.swing.JDialog {

    public LoaderDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        try { parent.setIconImage(IconUtils.getDefault().getImage16("icon")); }
        catch (Exception e) {}
    }

    public void setLabelText(String text) {
        this.lblUpdate.setText(text);
    }

    public void update(final Stock stock, final String time, final AbstractUpdater abstractUpdater) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    UpdaterManager.getDefault().update(stock, time, abstractUpdater);
                } finally {
                    if (UpdaterManager.getDefault().isUpdated()) {
                        setVisible(false);
                        UpdaterManager.getDefault().setUpdate(false);
                    }
                }
            }
        });
        t.start();
    }

    public void update(final LinkedHashMap stocks, final AbstractUpdater abstractUpdater) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    UpdaterManager.getDefault().update(stocks, abstractUpdater);
                } finally {
                    if (UpdaterManager.getDefault().isUpdated()) {
                        setVisible(false);
                        UpdaterManager.getDefault().setUpdate(false);
                    }
                }
            }
        });
        t.start();
    }

    public void update(final Stock[] stocks, final AbstractUpdater abstractUpdater) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    UpdaterManager.getDefault().update(stocks, abstractUpdater);
                } finally {
                    if (UpdaterManager.getDefault().isUpdated()) {
                        setVisible(false);
                        UpdaterManager.getDefault().setUpdate(false);
                    }
                }
            }
        });
        t.start();
    }

    public void update(final Stock stock, final AbstractUpdater updater) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    UpdaterManager.getDefault().update(stock, updater);
                } finally {
                    if (UpdaterManager.getDefault().isUpdated()) {
                        setVisible(false);
                        UpdaterManager.getDefault().setUpdate(false);
                    }
                }
            }
        });
        t.start();
    }

    public void updateIntraday(final Stock stock, final String time, final AbstractUpdater abstractUpdater) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    UpdaterManager.getDefault().updateIntraDay(stock, time, abstractUpdater);
                } finally {
                    if (UpdaterManager.getDefault().isUpdated()) {
                        setVisible(false);
                        UpdaterManager.getDefault().setUpdate(false);
                    }
                }
            }
        });
        t.start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        lblUpdate = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(LoaderDialog.class, "LoaderDialog.title")); // NOI18N

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));

        lblLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/chartsy/main/icons/chartsy_component_logo.png"))); // NOI18N
        lblLogo.setText(org.openide.util.NbBundle.getMessage(LoaderDialog.class, "LoaderDialog.lblLogo.text")); // NOI18N
        lblLogo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        progressBar.setIndeterminate(true);

        lblUpdate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblUpdate.setText(org.openide.util.NbBundle.getMessage(LoaderDialog.class, "LoaderDialog.lblUpdate.text")); // NOI18N
        lblUpdate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lblLogo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 432, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(progressBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE)
                    .add(lblUpdate, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(lblLogo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(lblUpdate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                LoaderDialog dialog = new LoaderDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblUpdate;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JProgressBar progressBar;
    // End of variables declaration//GEN-END:variables

}
