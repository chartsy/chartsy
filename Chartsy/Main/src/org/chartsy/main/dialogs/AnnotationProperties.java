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

    public AnnotationProperties(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setTitle("Annotations Properties");
        parent.setIconImage(WindowManager.getDefault().getMainWindow().getIconImage());
    }

    public void initializeForm(final ChartFrame chartFrame) {
        this.color.setBackground(chartFrame.getChartProperties().getAnnotationColor());
        this.color.addMouseListener(new MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                getLabelColor(color, "Choose Annotation Color");
            }
        });
        this.lineStyle.setSelectedItem(chartFrame.getChartProperties().getAnnotationStroke());
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
        cf.getChartProperties().setAnnotationStroke((Stroke) lineStyle.getSelectedItem());
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

        color.setText(org.openide.util.NbBundle.getMessage(AnnotationProperties.class, "AnnotationProperties.color.text")); // NOI18N
        color.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        color.setOpaque(true);
        color.setPreferredSize(new java.awt.Dimension(20, 20));

        btnOk.setText(org.openide.util.NbBundle.getMessage(AnnotationProperties.class, "AnnotationProperties.btnOk.text")); // NOI18N

        btnCancel.setText(org.openide.util.NbBundle.getMessage(AnnotationProperties.class, "AnnotationProperties.btnCancel.text")); // NOI18N

        lineStyle.setPreferredSize(new java.awt.Dimension(100, 20));

        org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblStyle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(btnOk)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnCancel))
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(lineStyle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(color, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(12, 12, 12)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .add(22, 22, 22)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(lblColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(color, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblStyle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lineStyle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnCancel)
                    .add(btnOk))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
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
    private final org.chartsy.main.utils.StrokeComboBox lineStyle = new org.chartsy.main.utils.StrokeComboBox();
    private javax.swing.JPanel mainPanel;
    // End of variables declaration//GEN-END:variables

}
