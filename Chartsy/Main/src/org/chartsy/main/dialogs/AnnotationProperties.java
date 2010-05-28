package org.chartsy.main.dialogs;

import org.chartsy.main.chart.Annotation;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.nodes.Node;
import org.openide.windows.WindowManager;

/**
 *
 * @author viorel.gheba
 */
public class AnnotationProperties extends javax.swing.JDialog
{

    public AnnotationProperties(java.awt.Frame parent, boolean modal)
    {
        super(parent, modal);
        initComponents();
        parent.setIconImage(WindowManager.getDefault().getMainWindow().getIconImage());
    }

    public void initializeForm(Annotation annotation)
    {
        PropertySheet prop = new PropertySheet();
        prop.setNodes(new Node[] {annotation.getNode()});
        setContentPane(prop);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(AnnotationProperties.class, "AnnotationProperties.title_1")); // NOI18N
        setResizable(false);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 507, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 251, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AnnotationProperties dialog = new AnnotationProperties(new javax.swing.JFrame(), true);
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
