package org.chartsy.main.dialogs;

import java.awt.Color;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import javax.swing.JColorChooser;
import org.chartsy.main.chartsy.ChartFrame;
import org.openide.windows.WindowManager;

/**
 *
 * @author viorel.gheba
 */
public class AnnotationProperties extends javax.swing.JDialog {

    /** Creates new form AnnotationProperties */
    public AnnotationProperties(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public void initializeForm(final ChartFrame chartFrame) {
        setIconImage(WindowManager.getDefault().getMainWindow().getIconImage());
        setTitle("Annotations Properties");

        this.color.setBackground(chartFrame.getChartProperties().getAnnotationColor());
        this.color.addMouseListener(new MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                getLabelColor(color, "Choose Annotation Color");
            }
        });
        this.lstStyle.setSelectedItem(chartFrame.getChartProperties().getAnnotationStroke());
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        btnOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setSettings(chartFrame);
                setVisible(false);
            }
        });
    }

    private void getLabelColor(javax.swing.JLabel label, String title) {
        Color c = JColorChooser.showDialog(this, title, label.getBackground());
        if (c != null) {
            label.setBackground(c);
        }
    }

    private void setSettings(ChartFrame cf) {
        cf.getChartProperties().setAnnotationColor(color.getBackground());
        cf.getChartProperties().setAnnotationStroke((Stroke) lstStyle.getSelectedItem());
        cf.getChartPanel().repaint();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        lblColor = new javax.swing.JLabel();
        lblStyle = new javax.swing.JLabel();
        btnOk = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));

        lblColor.setText(org.openide.util.NbBundle.getMessage(AnnotationProperties.class, "AnnotationProperties.lblColor.text")); // NOI18N
        lblColor.setPreferredSize(new java.awt.Dimension(29, 20));

        lblStyle.setText(org.openide.util.NbBundle.getMessage(AnnotationProperties.class, "AnnotationProperties.lblStyle.text")); // NOI18N
        lblStyle.setPreferredSize(new java.awt.Dimension(50, 20));

        lstStyle.setPreferredSize(new java.awt.Dimension(100, 20));

        color.setText(org.openide.util.NbBundle.getMessage(AnnotationProperties.class, "AnnotationProperties.color.text")); // NOI18N
        color.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        color.setOpaque(true);
        color.setPreferredSize(new java.awt.Dimension(20, 20));

        btnOk.setText(org.openide.util.NbBundle.getMessage(AnnotationProperties.class, "AnnotationProperties.btnOk.text")); // NOI18N

        btnCancel.setText(org.openide.util.NbBundle.getMessage(AnnotationProperties.class, "AnnotationProperties.btnCancel.text")); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblStyle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(btnOk)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancel))
                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(color, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lstStyle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(color, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lstStyle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStyle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel)
                    .addComponent(btnOk))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOk;
    private final javax.swing.JLabel color = new javax.swing.JLabel();
    private javax.swing.JLabel lblColor;
    private javax.swing.JLabel lblStyle;
    private final org.chartsy.main.utils.StrokeComboBox lstStyle = new org.chartsy.main.utils.StrokeComboBox();
    private javax.swing.JPanel mainPanel;
    // End of variables declaration//GEN-END:variables

}
