package org.chartsy.main.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.chart.Overlay;
import org.chartsy.main.managers.OverlayManager;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.nodes.Node;
import org.openide.windows.WindowManager;

/**
 *
 * @author viorel.gheba
 */
public class Overlays extends javax.swing.JDialog
{

    private ChartFrame parent;
    private List<Overlay> initial;
    private List<Overlay> selected;
    private List<Overlay> unselected;

    public Overlays(java.awt.Frame parent, boolean modal)
    {
        super(parent, modal);
        initComponents();
        parent.setIconImage(WindowManager.getDefault().getMainWindow().getIconImage());
    }

    public void setChartFrame(ChartFrame cf) 
    { parent = cf; }

    public void initForm()
    {
        btnAdd.setEnabled(false);
        btnRemove.setEnabled(false);

        selected = new ArrayList<Overlay>();
        unselected = new ArrayList<Overlay>();

        unselected = OverlayManager.getDefault().getOverlaysList();
        selected = parent.getSplitPanel().getChartPanel().getOverlays();
        initial = selected;

        scrollPane.setEnabled(false);
        scrollPane.setLayout(new BorderLayout());
        scrollPane.setPreferredSize(new Dimension(549, 296));
        scrollPane.setMinimumSize(new Dimension(549, 296));

        lstSelected.setListData(getArray(selected, true));
        lstUnselected.setListData(getArray(unselected, false));
    }

    private void setPanel(Overlay o)
    {
        PropertySheet prop = new PropertySheet();
        prop.setNodes(new Node[] {o.getNode()});
        Dimension d = prop.getSize();
        prop.setPreferredSize(new Dimension(549, d.height));
        scrollPane.setEnabled(true);
        scrollPane.removeAll();
        scrollPane.add(prop, BorderLayout.CENTER);
        scrollPane.validate();
        validate();
        repaint();
    }

    private String[] getArray(List<Overlay> list, boolean label)
    {
        String[] array = new String[list.size()];
        for (int i = 0; i < list.size(); i++)
            array[i] = label == true ? list.get(i).getLabel() : list.get(i).getName();
        return array;
    }

    public @Override void paint(Graphics g)
    {
        super.paint(g);
        int index = lstSelected.getSelectedIndex();
        lstSelected.setListData(getArray(selected, true));
        lstSelected.setSelectedIndex(index);
    }

    public @Override void update(Graphics g)
    {
        super.update(g);
        repaint();
    }

    public @Override void setVisible(boolean b)
    {
        super.setVisible(b);
        if (!b)
            parent.componentFocused();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        lblIO = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstUnselected = new javax.swing.JList();
        lblSelected = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstSelected = new javax.swing.JList();
        btnAdd = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        lblProperties = new javax.swing.JLabel();
        btnCancel = new javax.swing.JButton();
        btnApply = new javax.swing.JButton();
        btnOk = new javax.swing.JButton();
        scrollPane = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(Overlays.class, "Overlays.title")); // NOI18N
        setResizable(false);

        lblIO.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblIO.setText(org.openide.util.NbBundle.getMessage(Overlays.class, "Overlays.lblIO.text")); // NOI18N

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        lstUnselected.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstUnselectedMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(lstUnselected);

        lblSelected.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblSelected.setText(org.openide.util.NbBundle.getMessage(Overlays.class, "Overlays.lblSelected.text")); // NOI18N

        lstSelected.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstSelectedMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(lstSelected);

        btnAdd.setText(org.openide.util.NbBundle.getMessage(Overlays.class, "Overlays.btnAdd.text")); // NOI18N
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnRemove.setText(org.openide.util.NbBundle.getMessage(Overlays.class, "Overlays.btnRemove.text")); // NOI18N
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });

        lblProperties.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblProperties.setText(org.openide.util.NbBundle.getMessage(Overlays.class, "Overlays.lblProperties.text")); // NOI18N

        btnCancel.setText(org.openide.util.NbBundle.getMessage(Overlays.class, "Overlays.btnCancel.text")); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnApply.setText(org.openide.util.NbBundle.getMessage(Overlays.class, "Overlays.btnApply.text")); // NOI18N
        btnApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplyActionPerformed(evt);
            }
        });

        btnOk.setText(org.openide.util.NbBundle.getMessage(Overlays.class, "Overlays.btnOk.text")); // NOI18N
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkActionPerformed(evt);
            }
        });

        scrollPane.setBackground(new java.awt.Color(255, 255, 255));
        scrollPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        scrollPane.setAutoscrolls(true);
        scrollPane.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        org.jdesktop.layout.GroupLayout scrollPaneLayout = new org.jdesktop.layout.GroupLayout(scrollPane);
        scrollPane.setLayout(scrollPaneLayout);
        scrollPaneLayout.setHorizontalGroup(
            scrollPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 547, Short.MAX_VALUE)
        );
        scrollPaneLayout.setVerticalGroup(
            scrollPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 294, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(btnAdd)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnRemove))
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 298, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, lblSelected, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, lblIO, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)))
                .add(16, 16, 16)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(mainPanelLayout.createSequentialGroup()
                            .add(btnOk)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(btnApply)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(btnCancel))
                        .add(scrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(lblProperties, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 151, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(16, 16, 16))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(lblProperties)
                        .add(11, 11, 11)
                        .add(scrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(btnCancel)
                            .add(btnApply)
                            .add(btnOk)))
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(lblIO)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 129, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(lblSelected)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 7, Short.MAX_VALUE)
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(btnAdd)
                            .add(btnRemove))))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 897, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void lstUnselectedMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstUnselectedMouseClicked
        switch (evt.getClickCount()) {
            case 1:
                scrollPane.setEnabled(false);
                btnAdd.setEnabled(true);
                btnRemove.setEnabled(false);
                break;
            case 2:
                btnAdd.doClick();
                break;
        }
}//GEN-LAST:event_lstUnselectedMouseClicked

    private void lstSelectedMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstSelectedMouseClicked
        switch (evt.getClickCount()) {
            case 1:
                btnAdd.setEnabled(false);
                if (selected.size() > 0) btnRemove.setEnabled(true);
                else btnRemove.setEnabled(false);
                int i = lstSelected.getSelectedIndex();
                if (i != -1) {
                    setPanel(selected.get(i));
                }
                break;
            case 2:
                btnRemove.doClick();
                break;
        }
}//GEN-LAST:event_lstSelectedMouseClicked

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        int i = lstUnselected.getSelectedIndex();
        if (i != -1) {
            Overlay o = unselected.get(i).newInstance();
            selected.add(o);
            lstSelected.setListData(getArray(selected, true));
            setPanel(o);
        }
}//GEN-LAST:event_btnAddActionPerformed

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        int i = lstSelected.getSelectedIndex();
        if (i != -1) {
            selected.remove(i);
            if (selected.size() == 0) btnRemove.setEnabled(false);
            scrollPane.setEnabled(false);
            lstSelected.setListData(getArray(selected, true));
            validate();
            repaint();
        }
}//GEN-LAST:event_btnRemoveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        selected = initial;
        setVisible(false);
}//GEN-LAST:event_btnCancelActionPerformed

    private void btnApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyActionPerformed
        parent.getSplitPanel().getChartPanel().clearOverlays();
        if (selected.size() > 0) {
            for (int i = 0; i < selected.size(); i++) {
                Overlay o = selected.get(i);
                o.setLogarithmic(parent.getChartProperties().getAxisLogarithmicFlag());
                o.setDataset(parent.getChartData().getDataset(false));
                o.calculate();
                parent.getSplitPanel().getChartPanel().addOverlay(o);
            }
        }
        parent.revalidate();
        parent.repaint();
}//GEN-LAST:event_btnApplyActionPerformed

    private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
        parent.getSplitPanel().getChartPanel().clearOverlays();
        parent.getChartData().removeAllOverlaysDatasetListeners();
        if (selected.size() > 0) {
            for (int i = 0; i < selected.size(); i++) {
                Overlay o = selected.get(i);
                o.setLogarithmic(parent.getChartProperties().getAxisLogarithmicFlag());
                o.setDataset(parent.getChartData().getDataset(false));
                o.calculate();
                parent.getSplitPanel().getChartPanel().addOverlay(o);
            }
        }
        parent.revalidate();
        parent.repaint();
        setVisible(false);
}//GEN-LAST:event_btnOkActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Overlays dialog = new Overlays(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public @Override void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnApply;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOk;
    private javax.swing.JButton btnRemove;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblIO;
    private javax.swing.JLabel lblProperties;
    private javax.swing.JLabel lblSelected;
    private javax.swing.JList lstSelected;
    private javax.swing.JList lstUnselected;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel scrollPane;
    // End of variables declaration//GEN-END:variables

}
