package org.chartsy.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import org.chartsy.main.managers.UpdaterManager;
import org.openide.windows.WindowManager;

/**
 *
 * @author viorel.gheba
 */
public class SelectDataProvider extends javax.swing.JDialog {

    public SelectDataProvider(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        parent.setIconImage(WindowManager.getDefault().getMainWindow().getIconImage());
        setTitle("Select a Data Provider");
        initComponents();
        initForm();
    }

    protected void initForm() {
        Vector updaters = UpdaterManager.getDefault().getUpdaters();
        for (Object obj : updaters) {
            lstDataProvider.addItem(obj);
        }
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        btnOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Object obj = lstDataProvider.getSelectedItem();
                UpdaterManager.getDefault().setActiveUpdater(obj);
                setVisible(false);
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTop = new javax.swing.JLabel();
        lblDataProvider = new javax.swing.JLabel();
        lstDataProvider = new javax.swing.JComboBox();
        btnCancel = new javax.swing.JButton();
        btnOk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        lblTop.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTop.setText(org.openide.util.NbBundle.getMessage(SelectDataProvider.class, "SelectDataProvider.lblTop.text")); // NOI18N

        lblDataProvider.setText(org.openide.util.NbBundle.getMessage(SelectDataProvider.class, "SelectDataProvider.lblDataProvider.text")); // NOI18N

        btnCancel.setText(org.openide.util.NbBundle.getMessage(SelectDataProvider.class, "SelectDataProvider.btnCancel.text")); // NOI18N

        btnOk.setText(org.openide.util.NbBundle.getMessage(SelectDataProvider.class, "SelectDataProvider.btnOk.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblTop, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(lblDataProvider)
                        .add(18, 18, 18)
                        .add(lstDataProvider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 195, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(btnOk)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(btnCancel)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(lblTop)
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblDataProvider)
                    .add(lstDataProvider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnCancel)
                    .add(btnOk))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                SelectDataProvider dialog = new SelectDataProvider(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnOk;
    private javax.swing.JLabel lblDataProvider;
    private javax.swing.JLabel lblTop;
    private javax.swing.JComboBox lstDataProvider;
    // End of variables declaration//GEN-END:variables

}
