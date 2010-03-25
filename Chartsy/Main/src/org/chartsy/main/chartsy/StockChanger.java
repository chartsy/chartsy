package org.chartsy.main.chartsy;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.text.AbstractDocument;
import org.chartsy.main.utils.Stock;
import org.chartsy.main.utils.UppercaseDocumentFilter;

/**
 *
 * @author viorel.gheba
 */
public class StockChanger extends javax.swing.JPanel {

    private ChartFrame listener;

    public StockChanger() {
        initComponents();
        stock.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                int i = e.getKeyChar();
                if (i == 10) submit.doClick();
            }
        });
        ((AbstractDocument)stock.getDocument()).setDocumentFilter(new UppercaseDocumentFilter());
    }

    public void setListener(ChartFrame cf) { listener = cf; }
    public void setStock(String s) { stock.setText(s); }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        stock = new javax.swing.JTextField();
        submit = new javax.swing.JButton();

        setMaximumSize(new java.awt.Dimension(101, 53));

        stock.setText(org.openide.util.NbBundle.getMessage(StockChanger.class, "StockChanger.stock.text")); // NOI18N
        stock.setMaximumSize(new java.awt.Dimension(2147483647, 31));
        stock.setMinimumSize(new java.awt.Dimension(36, 31));
        stock.setPreferredSize(new java.awt.Dimension(36, 31));
        stock.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                stockMouseClicked(evt);
            }
        });

        submit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/chartsy/main/icons/submit.png"))); // NOI18N
        submit.setText(org.openide.util.NbBundle.getMessage(StockChanger.class, "StockChanger.submit.text")); // NOI18N
        submit.setToolTipText(org.openide.util.NbBundle.getMessage(StockChanger.class, "StockChanger.submit.toolTipText")); // NOI18N
        submit.setBorder(null);
        submit.setBorderPainted(false);
        submit.setContentAreaFilled(false);
        submit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(stock, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 44, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(submit)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(stock, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(submit))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void stockMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_stockMouseClicked
        listener.setFocus(false);
    }//GEN-LAST:event_stockMouseClicked

    private void submitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitActionPerformed
        if (!stock.getText().toUpperCase().equals(listener.getStock().getKey())) {
            listener.getTimer().cancel();
            String s = stock.getText().toUpperCase();
            Stock newStock;
            if (!s.contains(".")) newStock = listener.getUpdater().getStock(s, "");
            else {
                String[] strings = s.split(".");
                newStock = listener.getUpdater().getStock(strings[0], strings[1]);
            }
            listener.setStock(newStock);
            listener.setFocus(true);
            listener.setRestored(true);
            listener.refresh();
        }
    }//GEN-LAST:event_submitActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField stock;
    private javax.swing.JButton submit;
    // End of variables declaration//GEN-END:variables

}
