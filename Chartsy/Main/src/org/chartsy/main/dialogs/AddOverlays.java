package org.chartsy.main.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.swing.ListSelectionModel;
import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.chartsy.chart.AbstractOverlay;
import org.chartsy.main.chartsy.chart.Overlay;
import org.chartsy.main.icons.IconUtils;
import org.chartsy.main.managers.OverlayManager;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.nodes.Node;

/**
 *
 * @author viorel.gheba
 */
public class AddOverlays extends javax.swing.JDialog {

    private ChartFrame parent;
    private Overlay[] initial;
    private Overlay[] selected;
    private AbstractOverlay[] unselected;
    private Vector<Object> selectedItems = new Vector<Object>();
    private Vector<Object> unselectedItems = new Vector<Object>();

    public AddOverlays(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        try { parent.setIconImage(IconUtils.getDefault().getImage16("icon")); }
        catch (Exception e) {}
    }

    public void setChartFrame(ChartFrame cf) { parent = cf; }

    public void initForm() {
        selected = new Overlay[0];
        unselected = new AbstractOverlay[0];

        unselected = OverlayManager.getDefault().getOverlays();
        selected = parent.getChartRenderer().getOverlays();
        initial = selected;

        setSelectedIndicators();
        setUnselectedIndicators();

        scrollPane.setLayout(new BorderLayout());
        scrollPane.setSize(324, 222);

        lstSelected.setListData(selectedItems);
        lstSelected.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstSelected.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                switch (e.getClickCount()) {
                    case 1:
                        btnAdd.setEnabled(false);
                        btnRemove.setEnabled(lstSelected.getLastVisibleIndex() != -1);
                        int index = lstSelected.locationToIndex(e.getPoint());
                        if (index != -1) {
                            Overlay ind = selected[index];
                            getPanel(ind);
                        }
                        break;
                    case 2:
                        btnRemove.doClick();
                        break;
                }
            }
        });

        lstUnselected.setListData(unselectedItems);
        lstUnselected.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstUnselected.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                switch (e.getClickCount()) {
                    case 1:
                        scrollPane.removeAll();
                        scrollPane.revalidate();
                        btnAdd.setEnabled(true);
                        btnRemove.setEnabled(false);
                        break;
                    case 2:
                        btnAdd.doClick();
                        break;
                }
            }
        });

        btnAdd.setEnabled(false);
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = lstUnselected.getSelectedIndex();
                if (index != -1) {
                    AbstractOverlay ai = unselected[index];
                    Overlay[] list = new Overlay[selected.length + 1];
                    for (int j = 0; j < selected.length; j++) {
                        list[j] = selected[j];
                    }
                    Overlay ovr = ai.newInstance();
                    list[selected.length] = ovr;
                    selected = list;
                    setSelectedIndicators();
                    lstSelected.setListData(selectedItems);
                    getPanel(ovr);
                }
            }
        });

        this.btnRemove.setEnabled(false);
        this.btnRemove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = lstSelected.getSelectedIndex();
                if (index != -1) {
                    if (selected.length == 1) {
                        Overlay[] list = new Overlay[0];
                        selected = list;
                    } else {
                        Overlay[] list = new Overlay[selected.length - 1];
                        int k = 0;
                        for (int j = 0; j < selected.length; j++) {
                            if (j != index) {
                                list[k] = selected[j];
                                k++;
                            }
                        }
                        selected = list;
                    }
                    setSelectedIndicators();
                    lstSelected.setListData(selectedItems);
                }
                scrollPane.removeAll();
                scrollPane.revalidate();
                repaint();
            }
        });

        this.btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selected = initial;
                setVisible(false);
            }
        });

        this.btnOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < selected.length; i++) {
                    selected[i].setDataset(parent.getChartRenderer().getMainDataset());
                    selected[i].calculate();
                }
                parent.getChartRenderer().setOverlays(selected);
                parent.getChartPanel().repaint();
                setVisible(false);
            }
        });

        this.btnApply.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < selected.length; i++) {
                    selected[i].setDataset(parent.getChartRenderer().getMainDataset());
                    selected[i].calculate();
                }
                parent.getChartRenderer().setOverlays(selected);
                parent.getChartPanel().repaint();
            }
        });
    }

    private void getPanel(Overlay o) {
        PropertySheet prop = new PropertySheet();
        prop.setNodes(new Node[] {o.getNode()});
        Dimension d = prop.getSize();
        prop.setPreferredSize(new Dimension(436, d.height));
        scrollPane.removeAll();
        scrollPane.add(prop, BorderLayout.CENTER);
        repaint();
    }

    public void setSelectedIndicators() {
        selectedItems.clear();
        for (Overlay ind : selected) {
            selectedItems.add(ind.getLabel());
        }
    }

    public void setUnselectedIndicators() {
        unselectedItems.clear();
        for (AbstractOverlay ind : unselected) {
            unselectedItems.add(ind.getName());
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        setSelectedIndicators();
        lstSelected.setListData(selectedItems);
    }

    @Override
    public void update(Graphics g) {
        super.update(g);
        repaint();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        lblIO = new javax.swing.JLabel();
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
        jSeparator1 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(AddOverlays.class, "AddOverlays.title")); // NOI18N
        setResizable(false);

        lblIO.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblIO.setText(org.openide.util.NbBundle.getMessage(AddOverlays.class, "AddOverlays.lblIO.text")); // NOI18N

        jScrollPane1.setViewportView(lstUnselected);

        lblSelected.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblSelected.setText(org.openide.util.NbBundle.getMessage(AddOverlays.class, "AddOverlays.lblSelected.text")); // NOI18N

        jScrollPane2.setViewportView(lstSelected);

        btnAdd.setText(org.openide.util.NbBundle.getMessage(AddOverlays.class, "AddOverlays.btnAdd.text")); // NOI18N

        btnRemove.setText(org.openide.util.NbBundle.getMessage(AddOverlays.class, "AddOverlays.btnRemove.text")); // NOI18N

        lblProperties.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblProperties.setText(org.openide.util.NbBundle.getMessage(AddOverlays.class, "AddOverlays.lblProperties.text")); // NOI18N

        btnCancel.setText(org.openide.util.NbBundle.getMessage(AddOverlays.class, "AddOverlays.btnCancel.text")); // NOI18N

        btnApply.setText(org.openide.util.NbBundle.getMessage(AddOverlays.class, "AddOverlays.btnApply.text")); // NOI18N

        btnOk.setText(org.openide.util.NbBundle.getMessage(AddOverlays.class, "AddOverlays.btnOk.text")); // NOI18N

        scrollPane.setBackground(new java.awt.Color(255, 255, 255));
        scrollPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 51)));
        scrollPane.setAutoscrolls(true);
        scrollPane.setMaximumSize(new java.awt.Dimension(436, 294));
        scrollPane.setMinimumSize(new java.awt.Dimension(436, 294));
        scrollPane.setPreferredSize(new java.awt.Dimension(436, 294));

        org.jdesktop.layout.GroupLayout scrollPaneLayout = new org.jdesktop.layout.GroupLayout(scrollPane);
        scrollPane.setLayout(scrollPaneLayout);
        scrollPaneLayout.setHorizontalGroup(
            scrollPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 435, Short.MAX_VALUE)
        );
        scrollPaneLayout.setVerticalGroup(
            scrollPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 292, Short.MAX_VALUE)
        );

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblIO, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                    .add(lblSelected, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 298, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 298, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(btnAdd)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnRemove)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, mainPanelLayout.createSequentialGroup()
                        .add(btnOk)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnApply)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnCancel))
                    .add(lblProperties, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, scrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 368, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(mainPanelLayout.createSequentialGroup()
                            .add(lblIO)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 129, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                            .add(lblSelected)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(18, 18, 18)
                            .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                .add(btnAdd)
                                .add(btnRemove))
                            .addContainerGap())
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, mainPanelLayout.createSequentialGroup()
                            .add(lblProperties)
                            .add(11, 11, 11)
                            .add(scrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(18, 18, 18)
                            .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                .add(btnCancel)
                                .add(btnApply)
                                .add(btnOk))
                            .add(82, 82, 82)))))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AddOverlays dialog = new AddOverlays(new javax.swing.JFrame(), true);
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
