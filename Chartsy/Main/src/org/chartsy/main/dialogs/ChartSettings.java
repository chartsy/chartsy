package org.chartsy.main.dialogs;

import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import javax.swing.JColorChooser;
import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.utils.DefaultTheme;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.windows.WindowManager;

/**
 *
 * @author viorel.gheba
 */
public class ChartSettings extends javax.swing.JDialog {

    private Font selectedFont;

    public ChartSettings(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        parent.setIconImage(WindowManager.getDefault().getMainWindow().getIconImage());
    }

    public void initializeForm(final ChartFrame cf) {
        axisColor.setBackground(cf.getChartProperties().getAxisColor());
        axisColor.addMouseListener(new MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                getLabelColor(axisColor, "Choose Axis Color");
            }
        });
        axisStyle.setSelectedItem(cf.getChartProperties().getAxisStroke());
        barWidth.setText(String.valueOf(cf.getChartProperties().getBarWidth()));
        borderColor.setBackground(cf.getChartProperties().getBarColor());
        borderColor.addMouseListener(new MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                getLabelColor(borderColor, "Choose Border Color");
            }
        });
        borderStyle.setSelectedItem(cf.getChartProperties().getBarStroke());
        upColor.setBackground(cf.getChartProperties().getBarUpColor());
        upColor.addMouseListener(new MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                getLabelColor(upColor, "Choose Up Color");
            }
        });
        downColor.setBackground(cf.getChartProperties().getBarDownColor());
        downColor.addMouseListener(new MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                getLabelColor(downColor, "Choose Down Color");
            }
        });
        hColor.setBackground(cf.getChartProperties().getGridHorizontalColor());
        hColor.addMouseListener(new MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                getLabelColor(hColor, "Choose Horizontal Grid Color");
            }
        });
        hStyle.setSelectedItem(cf.getChartProperties().getGridHorizontalStroke());
        hVisibility.setSelected(cf.getChartProperties().getGridHorizontalVisibility());
        vColor.setBackground(cf.getChartProperties().getGridVerticalColor());
        vColor.addMouseListener(new MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                getLabelColor(vColor, "Choose Vertical Grid Color");
            }
        });
        vStyle.setSelectedItem(cf.getChartProperties().getGridVerticalStroke());
        vVisibility.setSelected(cf.getChartProperties().getGridVerticalVisibility());
        backgroundColor.setBackground(cf.getChartProperties().getBackgroundColor());
        backgroundColor.addMouseListener(new MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                getLabelColor(backgroundColor, "Choose Background Color");
            }
        });
        selectedFont = cf.getChartProperties().getFont();
        fontValue.setText(getFontValue(cf.getChartProperties().getFont()));
        fontChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Font font = null;
                PropertyEditor pe = PropertyEditorManager.findEditor(Font.class);
                pe.setValue(selectedFont);
                DialogDescriptor dd = new DialogDescriptor(pe.getCustomEditor(), "Choose Font");
                DialogDisplayer.getDefault().createDialog(dd).setVisible(true);
                if (dd.getValue() == DialogDescriptor.OK_OPTION) {
                    try {
                        font = (Font) pe.getValue();
                    } catch (Exception ex) {
                        font = selectedFont;
                    }
                }
                if (font != null) {
                    selectedFont = font;
                    fontValue.setText(getFontValue(font));
                }
            }
        });
        fontColor.setBackground(cf.getChartProperties().getFontColor());
        fontColor.addMouseListener(new MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                getLabelColor(fontColor, "Choose Font Color");
            }
        });
        btnApply.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setSettings(cf);
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        btnOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setSettings(cf);
                setVisible(false);
            }
        });
        btnReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetSettings();
            }
        });
    }

    private void getLabelColor(javax.swing.JLabel label, String title) {
        Color color = JColorChooser.showDialog(this, title, label.getBackground());
        if (color != null) {
            label.setBackground(color);
        }
    }

    private String getFontValue(Font font) {
        final String[] style = {"Plain", "Bold", "Italic", "Bold Italic"};
        return font.getName() + ", " + style[font.getStyle()] + ", " + font.getSize();
    }

    private void setSettings(ChartFrame cf) {
        cf.getChartProperties().setAxisColor(this.axisColor.getBackground());
        cf.getChartProperties().setAxisStroke((Stroke) this.axisStyle.getSelectedItem());

        cf.getChartProperties().setBarWidth(Double.parseDouble(this.barWidth.getText()));
        cf.getChartProperties().setBarColor(this.borderColor.getBackground());
        cf.getChartProperties().setBarStroke((Stroke) this.borderStyle.getSelectedItem());
        cf.getChartProperties().setBarVisibility(this.borderVisibility.isSelected());
        cf.getChartProperties().setBarDownColor(this.downColor.getBackground());
        cf.getChartProperties().setBarDownVisibility(this.downVisibility.isSelected());
        cf.getChartProperties().setBarUpColor(this.upColor.getBackground());
        cf.getChartProperties().setBarUpVisibility(this.upVisibility.isSelected());

        cf.getChartProperties().setGridHorizontalColor(this.hColor.getBackground());
        cf.getChartProperties().setGridHorizontalStroke((Stroke) this.hStyle.getSelectedItem());
        cf.getChartProperties().setGridHorizontalVisibility(this.hVisibility.isSelected());
        cf.getChartProperties().setGridVerticalColor(this.vColor.getBackground());
        cf.getChartProperties().setGridVerticalStroke((Stroke) this.vStyle.getSelectedItem());
        cf.getChartProperties().setGridVerticalVisibility(this.vVisibility.isSelected());

        cf.getChartProperties().setBackgroundColor(this.backgroundColor.getBackground());
        cf.getChartProperties().setFont(this.selectedFont);
        cf.getChartProperties().setFontColor(this.fontColor.getBackground());
        cf.getChartPanel().repaint();
    }

    private void resetSettings() {
        axisColor.setBackground(DefaultTheme.AXIS_COLOR);
        axisStyle.setSelectedItem(DefaultTheme.AXIS_STROKE);

        barWidth.setText(String.valueOf(DefaultTheme.BAR_WIDTH));
        borderColor.setBackground(DefaultTheme.BAR_COLOR);
        borderStyle.setSelectedItem(DefaultTheme.BAR_STROKE);
        borderVisibility.setSelected(DefaultTheme.BAR_VISIBILITY);
        upColor.setBackground(DefaultTheme.BAR_UP_COLOR);
        upVisibility.setSelected(DefaultTheme.BAR_UP_VISIBILITY);
        downColor.setBackground(DefaultTheme.BAR_DOWN_COLOR);
        downVisibility.setSelected(DefaultTheme.BAR_DOWN_VISIBILITY);

        hColor.setBackground(DefaultTheme.HORIZONTAL_GRID_COLOR);
        hStyle.setSelectedItem(DefaultTheme.HORIZONTAL_GRID_STROKE);
        hVisibility.setSelected(DefaultTheme.HORIZONTAL_GRID_VISIBILITY);
        vColor.setBackground(DefaultTheme.VERTICAL_GRID_COLOR);
        vStyle.setSelectedItem(DefaultTheme.VERTICAL_GRID_STROKE);
        vVisibility.setSelected(DefaultTheme.VERTICAL_GRID_VISIBILITY);

        backgroundColor.setBackground(DefaultTheme.BACKGROUND_COLOR);
        selectedFont = DefaultTheme.FONT;
        fontValue.setText(getFontValue(DefaultTheme.FONT));
        fontColor.setBackground(DefaultTheme.FONT_COLOR);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        lblAxis = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        lblAxisColor = new javax.swing.JLabel();
        axisColor = new javax.swing.JLabel();
        lblAxisStyle = new javax.swing.JLabel();
        lblDataSeries = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        lblBarWidth = new javax.swing.JLabel();
        barWidth = new javax.swing.JTextField();
        lblBorderColor = new javax.swing.JLabel();
        borderColor = new javax.swing.JLabel();
        lblBorderStyle = new javax.swing.JLabel();
        lblUpColor = new javax.swing.JLabel();
        upColor = new javax.swing.JLabel();
        lblDownColor = new javax.swing.JLabel();
        downColor = new javax.swing.JLabel();
        lblGrid = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        lblHColor = new javax.swing.JLabel();
        lblHStyle = new javax.swing.JLabel();
        lblHVisibility = new javax.swing.JLabel();
        lblVColor = new javax.swing.JLabel();
        lblVStyle = new javax.swing.JLabel();
        lblVVisibility = new javax.swing.JLabel();
        hColor = new javax.swing.JLabel();
        hVisibility = new javax.swing.JCheckBox();
        vColor = new javax.swing.JLabel();
        vVisibility = new javax.swing.JCheckBox();
        lblWindow = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        lblBackground = new javax.swing.JLabel();
        lblFont = new javax.swing.JLabel();
        lblFontColor = new javax.swing.JLabel();
        backgroundColor = new javax.swing.JLabel();
        fontColor = new javax.swing.JLabel();
        fontChooser = new javax.swing.JButton();
        fontValue = new javax.swing.JLabel();
        btnCancel = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        btnApply = new javax.swing.JButton();
        btnOk = new javax.swing.JButton();
        lblBorderVisibility = new javax.swing.JLabel();
        borderVisibility = new javax.swing.JCheckBox();
        lblUpVisibility = new javax.swing.JLabel();
        upVisibility = new javax.swing.JCheckBox();
        lblDownVisibility = new javax.swing.JLabel();
        downVisibility = new javax.swing.JCheckBox();
        axisStyle = new org.chartsy.main.utils.StrokeComboBox();
        borderStyle = new org.chartsy.main.utils.StrokeComboBox();
        hStyle = new org.chartsy.main.utils.StrokeComboBox();
        vStyle = new org.chartsy.main.utils.StrokeComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));

        lblAxis.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblAxis.text")); // NOI18N

        lblAxisColor.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblAxisColor.text")); // NOI18N
        lblAxisColor.setPreferredSize(new java.awt.Dimension(52, 20));

        axisColor.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.axisColor.text")); // NOI18N
        axisColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        axisColor.setMaximumSize(new java.awt.Dimension(20, 20));
        axisColor.setMinimumSize(new java.awt.Dimension(20, 20));
        axisColor.setOpaque(true);
        axisColor.setPreferredSize(new java.awt.Dimension(20, 20));

        lblAxisStyle.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblAxisStyle.text")); // NOI18N

        lblDataSeries.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblDataSeries.text")); // NOI18N

        lblBarWidth.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblBarWidth.text")); // NOI18N

        barWidth.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.barWidth.text")); // NOI18N

        lblBorderColor.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblBorderColor.text")); // NOI18N
        lblBorderColor.setPreferredSize(new java.awt.Dimension(64, 20));

        borderColor.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.borderColor.text")); // NOI18N
        borderColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        borderColor.setMaximumSize(new java.awt.Dimension(20, 20));
        borderColor.setMinimumSize(new java.awt.Dimension(20, 20));
        borderColor.setOpaque(true);
        borderColor.setPreferredSize(new java.awt.Dimension(20, 20));

        lblBorderStyle.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblBorderStyle.text")); // NOI18N

        lblUpColor.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblUpColor.text")); // NOI18N
        lblUpColor.setPreferredSize(new java.awt.Dimension(45, 20));

        upColor.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.upColor.text")); // NOI18N
        upColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        upColor.setMaximumSize(new java.awt.Dimension(20, 20));
        upColor.setMinimumSize(new java.awt.Dimension(20, 20));
        upColor.setOpaque(true);
        upColor.setPreferredSize(new java.awt.Dimension(20, 20));

        lblDownColor.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblDownColor.text")); // NOI18N
        lblDownColor.setPreferredSize(new java.awt.Dimension(45, 20));

        downColor.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.downColor.text")); // NOI18N
        downColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        downColor.setMaximumSize(new java.awt.Dimension(20, 20));
        downColor.setMinimumSize(new java.awt.Dimension(20, 20));
        downColor.setOpaque(true);
        downColor.setPreferredSize(new java.awt.Dimension(20, 20));

        lblGrid.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblGrid.text")); // NOI18N

        lblHColor.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblHColor.text")); // NOI18N
        lblHColor.setPreferredSize(new java.awt.Dimension(102, 20));

        lblHStyle.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblHStyle.text")); // NOI18N

        lblHVisibility.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblHVisibility.text")); // NOI18N
        lblHVisibility.setPreferredSize(new java.awt.Dimension(114, 21));

        lblVColor.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblVColor.text")); // NOI18N
        lblVColor.setPreferredSize(new java.awt.Dimension(89, 20));

        lblVStyle.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblVStyle.text")); // NOI18N

        lblVVisibility.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblVVisibility.text")); // NOI18N
        lblVVisibility.setPreferredSize(new java.awt.Dimension(101, 21));

        hColor.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.hColor.text")); // NOI18N
        hColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        hColor.setMaximumSize(new java.awt.Dimension(20, 20));
        hColor.setMinimumSize(new java.awt.Dimension(20, 20));
        hColor.setOpaque(true);
        hColor.setPreferredSize(new java.awt.Dimension(20, 20));

        hVisibility.setBackground(new java.awt.Color(255, 255, 255));
        hVisibility.setSelected(true);
        hVisibility.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.hVisibility.text")); // NOI18N

        vColor.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.vColor.text")); // NOI18N
        vColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        vColor.setMaximumSize(new java.awt.Dimension(20, 20));
        vColor.setMinimumSize(new java.awt.Dimension(20, 20));
        vColor.setOpaque(true);
        vColor.setPreferredSize(new java.awt.Dimension(20, 20));

        vVisibility.setBackground(new java.awt.Color(255, 255, 255));
        vVisibility.setSelected(true);
        vVisibility.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.vVisibility.text")); // NOI18N

        lblWindow.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblWindow.text")); // NOI18N

        lblBackground.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblBackground.text")); // NOI18N
        lblBackground.setPreferredSize(new java.awt.Dimension(88, 20));

        lblFont.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblFont.text")); // NOI18N
        lblFont.setPreferredSize(new java.awt.Dimension(26, 23));

        lblFontColor.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblFontColor.text")); // NOI18N
        lblFontColor.setPreferredSize(new java.awt.Dimension(54, 20));

        backgroundColor.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.backgroundColor.text")); // NOI18N
        backgroundColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        backgroundColor.setMaximumSize(new java.awt.Dimension(20, 20));
        backgroundColor.setMinimumSize(new java.awt.Dimension(20, 20));
        backgroundColor.setOpaque(true);
        backgroundColor.setPreferredSize(new java.awt.Dimension(20, 20));

        fontColor.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.fontColor.text")); // NOI18N
        fontColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        fontColor.setMaximumSize(new java.awt.Dimension(20, 20));
        fontColor.setMinimumSize(new java.awt.Dimension(20, 20));
        fontColor.setOpaque(true);
        fontColor.setPreferredSize(new java.awt.Dimension(20, 20));

        fontChooser.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.fontChooser.text")); // NOI18N

        fontValue.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.fontValue.text")); // NOI18N
        fontValue.setPreferredSize(new java.awt.Dimension(49, 23));

        btnCancel.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.btnCancel.text")); // NOI18N

        btnReset.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.btnReset.text")); // NOI18N

        btnApply.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.btnApply.text")); // NOI18N

        btnOk.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.btnOk.text")); // NOI18N

        lblBorderVisibility.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblBorderVisibility.text")); // NOI18N
        lblBorderVisibility.setPreferredSize(new java.awt.Dimension(76, 21));

        borderVisibility.setBackground(new java.awt.Color(255, 255, 255));
        borderVisibility.setSelected(true);
        borderVisibility.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.borderVisibility.text")); // NOI18N

        lblUpVisibility.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblUpVisibility.text")); // NOI18N
        lblUpVisibility.setPreferredSize(new java.awt.Dimension(57, 21));

        upVisibility.setBackground(new java.awt.Color(255, 255, 255));
        upVisibility.setSelected(true);
        upVisibility.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.upVisibility.text")); // NOI18N

        lblDownVisibility.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblDownVisibility.text")); // NOI18N
        lblDownVisibility.setPreferredSize(new java.awt.Dimension(71, 21));

        downVisibility.setBackground(new java.awt.Color(255, 255, 255));
        downVisibility.setSelected(true);
        downVisibility.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.downVisibility.text")); // NOI18N

        axisStyle.setPreferredSize(new java.awt.Dimension(100, 20));

        borderStyle.setPreferredSize(new java.awt.Dimension(100, 20));

        hStyle.setMinimumSize(new java.awt.Dimension(100, 20));
        hStyle.setPreferredSize(new java.awt.Dimension(100, 20));

        vStyle.setPreferredSize(new java.awt.Dimension(100, 20));

        org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblAxis)
                            .add(mainPanelLayout.createSequentialGroup()
                                .add(10, 10, 10)
                                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(mainPanelLayout.createSequentialGroup()
                                        .add(lblAxisStyle)
                                        .add(18, 18, 18)
                                        .add(axisStyle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                    .add(mainPanelLayout.createSequentialGroup()
                                        .add(lblAxisColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(18, 18, 18)
                                        .add(axisColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                            .add(lblDataSeries)
                            .add(lblGrid)
                            .add(lblWindow)
                            .add(mainPanelLayout.createSequentialGroup()
                                .add(10, 10, 10)
                                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(mainPanelLayout.createSequentialGroup()
                                        .add(lblFont, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(80, 80, 80)
                                        .add(fontChooser, 0, 0, Short.MAX_VALUE))
                                    .add(mainPanelLayout.createSequentialGroup()
                                        .add(lblBackground, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(18, 18, 18)
                                        .add(backgroundColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                    .add(mainPanelLayout.createSequentialGroup()
                                        .add(lblFontColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(fontColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(fontValue, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 389, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(mainPanelLayout.createSequentialGroup()
                                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(lblBorderVisibility, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(lblBorderColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(lblBarWidth)
                                    .add(lblBorderStyle))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(borderColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(barWidth, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 69, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(borderVisibility)
                                    .add(borderStyle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .add(33, 33, 33)
                                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                        .add(lblDownColor, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(mainPanelLayout.createSequentialGroup()
                                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                            .add(lblDownVisibility, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                    .add(lblUpColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(lblUpVisibility, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(upVisibility)
                                    .add(downColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(downVisibility)
                                    .add(upColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                            .add(jSeparator4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 531, Short.MAX_VALUE)
                            .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 531, Short.MAX_VALUE)
                            .add(jSeparator2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 531, Short.MAX_VALUE)
                            .add(jSeparator3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 531, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, mainPanelLayout.createSequentialGroup()
                                .add(btnOk)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(btnApply)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(btnReset)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(btnCancel)))
                        .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(mainPanelLayout.createSequentialGroup()
                                .add(lblHColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(18, 18, 18)
                                .add(hColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(mainPanelLayout.createSequentialGroup()
                                .add(lblHStyle)
                                .add(18, 18, 18)
                                .add(hStyle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(mainPanelLayout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(lblHVisibility, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(hVisibility)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 60, Short.MAX_VALUE)
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(mainPanelLayout.createSequentialGroup()
                                .add(lblVVisibility, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(vVisibility))
                            .add(mainPanelLayout.createSequentialGroup()
                                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(lblVColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(lblVStyle))
                                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(mainPanelLayout.createSequentialGroup()
                                        .add(18, 18, 18)
                                        .add(vColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                    .add(mainPanelLayout.createSequentialGroup()
                                        .add(18, 18, 18)
                                        .add(vStyle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
                        .add(46, 46, 46))))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(lblAxis)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(axisColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblAxisColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(lblAxisStyle)
                        .add(18, 18, 18)
                        .add(lblDataSeries))
                    .add(axisStyle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblBarWidth)
                            .add(barWidth, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(12, 12, 12)
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(lblBorderColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(borderColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(lblUpColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(upColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(upVisibility)
                            .add(lblUpVisibility, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblBorderStyle)
                            .add(lblDownColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(9, 9, 9))
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(downColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED))
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(borderStyle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)))
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(borderVisibility)
                        .add(53, 53, 53)
                        .add(lblGrid))
                    .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(lblDownVisibility, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(downVisibility))
                    .add(lblBorderVisibility, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(lblHColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(hColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(8, 8, 8)
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblHStyle)
                            .add(hStyle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(hVisibility)
                            .add(lblHVisibility, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lblWindow))
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(lblVColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(vColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(8, 8, 8)
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblVStyle)
                            .add(vStyle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblVVisibility, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(vVisibility))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblBackground, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(backgroundColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblFont, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(fontChooser)
                        .add(fontValue, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblFontColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(fontColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 12, Short.MAX_VALUE)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnCancel)
                    .add(btnReset)
                    .add(btnApply)
                    .add(btnOk))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
    private javax.swing.JLabel axisColor;
    private org.chartsy.main.utils.StrokeComboBox axisStyle;
    private javax.swing.JLabel backgroundColor;
    private javax.swing.JTextField barWidth;
    private javax.swing.JLabel borderColor;
    private org.chartsy.main.utils.StrokeComboBox borderStyle;
    private javax.swing.JCheckBox borderVisibility;
    private javax.swing.JButton btnApply;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOk;
    private javax.swing.JButton btnReset;
    private javax.swing.JLabel downColor;
    private javax.swing.JCheckBox downVisibility;
    private javax.swing.JButton fontChooser;
    private javax.swing.JLabel fontColor;
    private javax.swing.JLabel fontValue;
    private javax.swing.JLabel hColor;
    private org.chartsy.main.utils.StrokeComboBox hStyle;
    private javax.swing.JCheckBox hVisibility;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JLabel lblAxis;
    private javax.swing.JLabel lblAxisColor;
    private javax.swing.JLabel lblAxisStyle;
    private javax.swing.JLabel lblBackground;
    private javax.swing.JLabel lblBarWidth;
    private javax.swing.JLabel lblBorderColor;
    private javax.swing.JLabel lblBorderStyle;
    private javax.swing.JLabel lblBorderVisibility;
    private javax.swing.JLabel lblDataSeries;
    private javax.swing.JLabel lblDownColor;
    private javax.swing.JLabel lblDownVisibility;
    private javax.swing.JLabel lblFont;
    private javax.swing.JLabel lblFontColor;
    private javax.swing.JLabel lblGrid;
    private javax.swing.JLabel lblHColor;
    private javax.swing.JLabel lblHStyle;
    private javax.swing.JLabel lblHVisibility;
    private javax.swing.JLabel lblUpColor;
    private javax.swing.JLabel lblUpVisibility;
    private javax.swing.JLabel lblVColor;
    private javax.swing.JLabel lblVStyle;
    private javax.swing.JLabel lblVVisibility;
    private javax.swing.JLabel lblWindow;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel upColor;
    private javax.swing.JCheckBox upVisibility;
    private javax.swing.JLabel vColor;
    private org.chartsy.main.utils.StrokeComboBox vStyle;
    private javax.swing.JCheckBox vVisibility;
    // End of variables declaration//GEN-END:variables

}
