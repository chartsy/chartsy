package org.chartsy.main.dialogs;

import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.icons.IconUtils;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.nodes.Node;

/**
 *
 * @author viorel.gheba
 */
public class ChartSettings extends javax.swing.JDialog {

    public ChartSettings(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        try { parent.setIconImage(IconUtils.getDefault().getImage16("icon")); }
        catch(Exception e) {}
    }

    public void initializeForm(final ChartFrame chartFrame) {
        PropertySheet prop = new PropertySheet();
        prop.setNodes(new Node[] {chartFrame.getNode()});
        setContentPane(prop);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.title")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 651, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 522, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ChartSettings dialog = new ChartSettings(new javax.swing.JFrame(), true);
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
    // End of variables declaration//GEN-END:variables

}
