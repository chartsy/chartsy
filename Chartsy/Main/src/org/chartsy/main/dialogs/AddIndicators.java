package org.chartsy.main.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.chartsy.chart.AbstractIndicator;
import org.chartsy.main.chartsy.chart.Indicator;
import org.chartsy.main.managers.IndicatorManager;
import org.openide.windows.WindowManager;

/**
 *
 * @author viorel.gheba
 */
public class AddIndicators extends javax.swing.JDialog {

    private ChartFrame parent;
    private Indicator[] initial;
    private Indicator[] selected;
    private AbstractIndicator[] unselected;
    private Vector<Object> selectedItems = new Vector<Object>();
    private Vector<Object> unselectedItems = new Vector<Object>();

    public AddIndicators(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setIconImage(WindowManager.getDefault().getMainWindow().getIconImage());
    }
    public void setChartFrame(ChartFrame cf) { parent = cf; }

    public void initForm() {
        selected = new Indicator[0];
        unselected = new AbstractIndicator[0];

        unselected = IndicatorManager.getDefault().getIndicators();
        selected = parent.getChartRenderer().getIndicators();
        initial = selected;

        setSelectedIndicators();
        setUnselectedIndicators();

        lblDescriptionValue.setText("");

        scrollPane.setLayout(new BorderLayout());
        scrollPane.setSize(324, 222);

        lstSelected.setListData(selectedItems);
        lstSelected.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstSelected.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                btnAdd.setEnabled(false);
                btnRemove.setEnabled(lstSelected.getLastVisibleIndex() != -1);
                int index = lstSelected.getSelectedIndex();
                if (index != -1) {
                    Indicator ind = selected[index];
                    lblDescription.setText(ind.getDescription());
                    getPanel(ind);
                }
            }
        });

        lstUnselected.setListData(unselectedItems);
        lstUnselected.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstUnselected.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                btnAdd.setEnabled(true);
                btnRemove.setEnabled(false);
                lblDescriptionValue.setText("");
            }
        });

        btnAdd.setEnabled(false);
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = lstUnselected.getSelectedIndex();
                if (index != -1) {
                    AbstractIndicator ai = unselected[index];
                    Indicator[] list = new Indicator[selected.length + 1];
                    for (int j = 0; j < selected.length; j++) {
                        list[j] = selected[j];
                    }
                    list[selected.length] = ai.newInstance();
                    selected = list;
                    setSelectedIndicators();
                    lstSelected.setListData(selectedItems);
                }
            }
        });

        this.btnRemove.setEnabled(false);
        this.btnRemove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = lstSelected.getSelectedIndex();
                if (index != -1) {
                    if (selected.length == 1) {
                        Indicator[] list = new Indicator[0];
                        selected = list;
                    } else {
                        Indicator[] list = new Indicator[selected.length - 1];
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
                    selected[i].setAreaIndex(i);
                }
                parent.getChartRenderer().setIndicators(selected);
                parent.getChartPanel().repaint();
                setVisible(false);
            }
        });

        this.btnApply.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Indicator[] list = parent.getChartRenderer().getIndicators();
                for (int i = 0; i < selected.length; i++) {
                    selected[i].setDataset(parent.getChartRenderer().getMainDataset());
                    selected[i].calculate();
                }
                parent.getChartRenderer().setIndicators(selected);
                parent.getChartPanel().repaint();
            }
        });
    }

    private void getPanel(Indicator i) {
        PropertiesPanel p = new PropertiesPanel(i.getProperties());
        p.initComponents();
        p.setDialog(this);
        JScrollPane jsp = new JScrollPane(p);
        jsp.setPreferredSize(new Dimension(324, 222));
        scrollPane.removeAll();
        scrollPane.add(jsp, BorderLayout.CENTER);
        scrollPane.revalidate();
    }

    public void setSelectedIndicators() {
        selectedItems.clear();
        for (Indicator ind : selected) {
            selectedItems.add(ind.getLabel());
        }
    }

    public void setUnselectedIndicators() {
        unselectedItems.clear();
        for (AbstractIndicator ind : unselected) {
            unselectedItems.add(ind.getName());
        }
    }

    public void paint(Graphics g) {
        super.paint(g);
        setSelectedIndicators();
        lstSelected.setListData(selectedItems);
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
        lblDescription = new javax.swing.JLabel();
        lblDescriptionValue = new javax.swing.JLabel();
        btnCancel = new javax.swing.JButton();
        btnApply = new javax.swing.JButton();
        btnOk = new javax.swing.JButton();
        scrollPane = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));

        lblIO.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblIO.setText(org.openide.util.NbBundle.getMessage(AddIndicators.class, "AddIndicators.lblIO.text")); // NOI18N

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jScrollPane1.setViewportView(lstUnselected);

        lblSelected.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblSelected.setText(org.openide.util.NbBundle.getMessage(AddIndicators.class, "AddIndicators.lblSelected.text")); // NOI18N

        jScrollPane2.setViewportView(lstSelected);

        btnAdd.setText(org.openide.util.NbBundle.getMessage(AddIndicators.class, "AddIndicators.btnAdd.text")); // NOI18N

        btnRemove.setText(org.openide.util.NbBundle.getMessage(AddIndicators.class, "AddIndicators.btnRemove.text")); // NOI18N

        lblProperties.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblProperties.setText(org.openide.util.NbBundle.getMessage(AddIndicators.class, "AddIndicators.lblProperties.text")); // NOI18N

        lblDescription.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblDescription.setText(org.openide.util.NbBundle.getMessage(AddIndicators.class, "AddIndicators.lblDescription.text")); // NOI18N

        lblDescriptionValue.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblDescriptionValue.setText(org.openide.util.NbBundle.getMessage(AddIndicators.class, "AddIndicators.lblDescriptionValue.text")); // NOI18N
        lblDescriptionValue.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        btnCancel.setText(org.openide.util.NbBundle.getMessage(AddIndicators.class, "AddIndicators.btnCancel.text")); // NOI18N

        btnApply.setText(org.openide.util.NbBundle.getMessage(AddIndicators.class, "AddIndicators.btnApply.text")); // NOI18N

        btnOk.setText(org.openide.util.NbBundle.getMessage(AddIndicators.class, "AddIndicators.btnOk.text")); // NOI18N

        scrollPane.setBackground(new java.awt.Color(255, 255, 255));
        scrollPane.setAutoscrolls(true);
        scrollPane.setMaximumSize(new java.awt.Dimension(324, 222));
        scrollPane.setMinimumSize(new java.awt.Dimension(324, 222));

        javax.swing.GroupLayout scrollPaneLayout = new javax.swing.GroupLayout(scrollPane);
        scrollPane.setLayout(scrollPaneLayout);
        scrollPaneLayout.setHorizontalGroup(
            scrollPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 417, Short.MAX_VALUE)
        );
        scrollPaneLayout.setVerticalGroup(
            scrollPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 222, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(lblIO, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(lblSelected, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(btnAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemove)
                        .addGap(176, 176, 176))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addComponent(btnOk)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnApply)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancel))
                    .addComponent(lblProperties, javax.swing.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
                    .addComponent(lblDescriptionValue, javax.swing.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
                    .addComponent(lblDescription)
                    .addComponent(scrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(lblProperties)
                        .addGap(11, 11, 11)
                        .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(11, 11, 11)
                        .addComponent(lblDescription)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblDescriptionValue, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnCancel)
                            .addComponent(btnApply)
                            .addComponent(btnOk)))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(lblIO)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblSelected)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnAdd)
                            .addComponent(btnRemove))))
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
                AddIndicators dialog = new AddIndicators(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblDescriptionValue;
    private javax.swing.JLabel lblIO;
    private javax.swing.JLabel lblProperties;
    private javax.swing.JLabel lblSelected;
    private javax.swing.JList lstSelected;
    private javax.swing.JList lstUnselected;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel scrollPane;
    // End of variables declaration//GEN-END:variables

}
