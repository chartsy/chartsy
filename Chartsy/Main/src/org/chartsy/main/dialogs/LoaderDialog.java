package org.chartsy.main.dialogs;

import java.util.Hashtable;
import org.chartsy.main.managers.UpdaterManager;
import org.openide.windows.WindowManager;

/**
 *
 * @author viorel.gheba
 */
public class LoaderDialog extends javax.swing.JDialog {

    public LoaderDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setTitle("Updating");
        lblLogo.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        lblLogo.setVerticalAlignment(javax.swing.JLabel.CENTER);
        lblUpdate.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        lblUpdate.setVerticalAlignment(javax.swing.JLabel.CENTER);
        setIconImage(WindowManager.getDefault().getMainWindow().getIconImage());
    }

    public void update(final String symbol, final String time) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    UpdaterManager.getDefault().update(symbol, time);
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

    public void update(final Hashtable symbols) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    UpdaterManager.getDefault().update(symbols);
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

    public void update(final String[] symbols) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    UpdaterManager.getDefault().update(symbols);
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

    public void update(final String symbol) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    UpdaterManager.getDefault().update(symbol);
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

    public void updateIntraday(final String symbol, final String time) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    UpdaterManager.getDefault().updateIntraDay(symbol, time);
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
        progressBar = new javax.swing.JProgressBar();
        lblUpdate = new javax.swing.JLabel();
        lblLogo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));

        progressBar.setIndeterminate(true);

        lblUpdate.setText(org.openide.util.NbBundle.getMessage(LoaderDialog.class, "LoaderDialog.lblUpdate.text")); // NOI18N
        lblUpdate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/chartsy/main/icons/chartsy_component_logo.png"))); // NOI18N
        lblLogo.setText(org.openide.util.NbBundle.getMessage(LoaderDialog.class, "LoaderDialog.lblLogo.text")); // NOI18N
        lblLogo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblLogo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 432, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUpdate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE)
                    .addComponent(progressBar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
