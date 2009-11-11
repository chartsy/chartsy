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
import org.chartsy.main.utils.StrokeComboBox;
import org.chartsy.main.utils.StrokeGenerator;
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
    }

    public void initializeForm(final ChartFrame cf) {
        setIconImage(WindowManager.getDefault().getMainWindow().getIconImage());

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

    private String getFontValue(Font font) {
        final String[] style = {"Plain", "Bold", "Italic", "Bold Italic"};
        return font.getName() + ", " + style[font.getStyle()] + ", " + font.getSize();
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

        lblAxis.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblAxis.text_1")); // NOI18N

        lblAxisColor.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblAxisColor.text_1")); // NOI18N
        lblAxisColor.setPreferredSize(new java.awt.Dimension(52, 20));

        axisColor.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.axisColor.text_1")); // NOI18N
        axisColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        axisColor.setMaximumSize(new java.awt.Dimension(20, 20));
        axisColor.setMinimumSize(new java.awt.Dimension(20, 20));
        axisColor.setOpaque(true);
        axisColor.setPreferredSize(new java.awt.Dimension(20, 20));

        lblAxisStyle.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblAxisStyle.text_1")); // NOI18N

        lblDataSeries.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblDataSeries.text_1")); // NOI18N

        lblBarWidth.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblBarWidth.text_1")); // NOI18N

        barWidth.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.barWidth.text_1")); // NOI18N

        lblBorderColor.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblBorderColor.text_1")); // NOI18N
        lblBorderColor.setPreferredSize(new java.awt.Dimension(64, 20));

        borderColor.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.borderColor.text_1")); // NOI18N
        borderColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        borderColor.setMaximumSize(new java.awt.Dimension(20, 20));
        borderColor.setMinimumSize(new java.awt.Dimension(20, 20));
        borderColor.setOpaque(true);
        borderColor.setPreferredSize(new java.awt.Dimension(20, 20));

        lblBorderStyle.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblBorderStyle.text_1")); // NOI18N

        lblUpColor.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblUpColor.text_1")); // NOI18N
        lblUpColor.setPreferredSize(new java.awt.Dimension(45, 20));

        upColor.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.upColor.text_1")); // NOI18N
        upColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        upColor.setMaximumSize(new java.awt.Dimension(20, 20));
        upColor.setMinimumSize(new java.awt.Dimension(20, 20));
        upColor.setOpaque(true);
        upColor.setPreferredSize(new java.awt.Dimension(20, 20));

        lblDownColor.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblDownColor.text_1")); // NOI18N
        lblDownColor.setPreferredSize(new java.awt.Dimension(45, 20));

        downColor.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.downColor.text_1")); // NOI18N
        downColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        downColor.setMaximumSize(new java.awt.Dimension(20, 20));
        downColor.setMinimumSize(new java.awt.Dimension(20, 20));
        downColor.setOpaque(true);
        downColor.setPreferredSize(new java.awt.Dimension(20, 20));

        lblGrid.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblGrid.text_1")); // NOI18N

        lblHColor.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblHColor.text_1")); // NOI18N
        lblHColor.setPreferredSize(new java.awt.Dimension(102, 20));

        lblHStyle.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblHStyle.text_1")); // NOI18N

        lblHVisibility.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblHVisibility.text_1")); // NOI18N
        lblHVisibility.setPreferredSize(new java.awt.Dimension(114, 21));

        lblVColor.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblVColor.text_1")); // NOI18N
        lblVColor.setPreferredSize(new java.awt.Dimension(89, 20));

        lblVStyle.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblVStyle.text_1")); // NOI18N

        lblVVisibility.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblVVisibility.text_1")); // NOI18N
        lblVVisibility.setPreferredSize(new java.awt.Dimension(101, 21));

        hColor.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.hColor.text_1")); // NOI18N
        hColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        hColor.setMaximumSize(new java.awt.Dimension(20, 20));
        hColor.setMinimumSize(new java.awt.Dimension(20, 20));
        hColor.setOpaque(true);
        hColor.setPreferredSize(new java.awt.Dimension(20, 20));

        hVisibility.setBackground(new java.awt.Color(255, 255, 255));
        hVisibility.setSelected(true);
        hVisibility.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.hVisibility.text_1")); // NOI18N

        vColor.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.vColor.text_1")); // NOI18N
        vColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        vColor.setMaximumSize(new java.awt.Dimension(20, 20));
        vColor.setMinimumSize(new java.awt.Dimension(20, 20));
        vColor.setOpaque(true);
        vColor.setPreferredSize(new java.awt.Dimension(20, 20));

        vVisibility.setBackground(new java.awt.Color(255, 255, 255));
        vVisibility.setSelected(true);
        vVisibility.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.vVisibility.text_1")); // NOI18N

        lblWindow.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblWindow.text_1")); // NOI18N

        lblBackground.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblBackground.text_1")); // NOI18N
        lblBackground.setPreferredSize(new java.awt.Dimension(88, 20));

        lblFont.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblFont.text_1")); // NOI18N
        lblFont.setPreferredSize(new java.awt.Dimension(26, 23));

        lblFontColor.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblFontColor.text_1")); // NOI18N
        lblFontColor.setPreferredSize(new java.awt.Dimension(54, 20));

        backgroundColor.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.backgroundColor.text_1")); // NOI18N
        backgroundColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        backgroundColor.setMaximumSize(new java.awt.Dimension(20, 20));
        backgroundColor.setMinimumSize(new java.awt.Dimension(20, 20));
        backgroundColor.setOpaque(true);
        backgroundColor.setPreferredSize(new java.awt.Dimension(20, 20));

        fontColor.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.fontColor.text_1")); // NOI18N
        fontColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        fontColor.setMaximumSize(new java.awt.Dimension(20, 20));
        fontColor.setMinimumSize(new java.awt.Dimension(20, 20));
        fontColor.setOpaque(true);
        fontColor.setPreferredSize(new java.awt.Dimension(20, 20));

        fontChooser.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.fontChooser.text")); // NOI18N

        fontValue.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.fontValue.text_1")); // NOI18N
        fontValue.setPreferredSize(new java.awt.Dimension(49, 23));

        btnCancel.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.btnCancel.text_1")); // NOI18N

        btnReset.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.btnReset.text_1")); // NOI18N

        btnApply.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.btnApply.text_1")); // NOI18N

        btnOk.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.btnOk.text_1")); // NOI18N

        lblBorderVisibility.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblBorderVisibility.text_1")); // NOI18N
        lblBorderVisibility.setPreferredSize(new java.awt.Dimension(76, 21));

        borderVisibility.setBackground(new java.awt.Color(255, 255, 255));
        borderVisibility.setSelected(true);
        borderVisibility.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.borderVisibility.text_1")); // NOI18N

        lblUpVisibility.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblUpVisibility.text_1")); // NOI18N
        lblUpVisibility.setPreferredSize(new java.awt.Dimension(57, 21));

        upVisibility.setBackground(new java.awt.Color(255, 255, 255));
        upVisibility.setSelected(true);
        upVisibility.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.upVisibility.text_1")); // NOI18N

        lblDownVisibility.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.lblDownVisibility.text_1")); // NOI18N
        lblDownVisibility.setPreferredSize(new java.awt.Dimension(71, 21));

        downVisibility.setBackground(new java.awt.Color(255, 255, 255));
        downVisibility.setSelected(true);
        downVisibility.setText(org.openide.util.NbBundle.getMessage(ChartSettings.class, "ChartSettings.downVisibility.text_1")); // NOI18N

        axisStyle.setPreferredSize(new java.awt.Dimension(100, 20));

        borderStyle.setPreferredSize(new java.awt.Dimension(100, 20));

        hStyle.setMinimumSize(new java.awt.Dimension(100, 20));
        hStyle.setPreferredSize(new java.awt.Dimension(100, 20));

        vStyle.setPreferredSize(new java.awt.Dimension(100, 20));

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblAxis)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(lblAxisStyle)
                                .addGap(18, 18, 18)
                                .addComponent(axisStyle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(lblAxisColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(axisColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(lblDataSeries)
                    .addComponent(lblGrid)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(lblHColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(hColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(lblHStyle)
                                .addGap(18, 18, 18)
                                .addComponent(hStyle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(lblHVisibility, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(hVisibility)))
                        .addGap(31, 31, 31)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(lblVVisibility, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(vVisibility))
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblVColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblVStyle))
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(mainPanelLayout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(vColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(mainPanelLayout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(vStyle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(183, 183, 183))
                    .addComponent(lblWindow)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(lblFont, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(80, 80, 80)
                                .addComponent(fontChooser, 0, 0, Short.MAX_VALUE))
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(lblBackground, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(backgroundColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(lblFontColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(fontColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fontValue, javax.swing.GroupLayout.PREFERRED_SIZE, 389, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblBorderVisibility, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblBorderColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblBarWidth)
                            .addComponent(lblBorderStyle))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(borderColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(barWidth, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(borderStyle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(borderVisibility))
                        .addGap(64, 64, 64)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(lblDownColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(mainPanelLayout.createSequentialGroup()
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(lblDownVisibility, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(lblUpColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblUpVisibility, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(upVisibility)
                            .addComponent(downColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(downVisibility)
                            .addComponent(upColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                            .addComponent(btnOk)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnApply)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnReset)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnCancel))))
                .addContainerGap(32, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblAxis)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(axisColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAxisColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(lblAxisStyle)
                        .addGap(18, 18, 18)
                        .addComponent(lblDataSeries))
                    .addComponent(axisStyle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblBarWidth)
                            .addComponent(barWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblBorderColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(borderColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblUpColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(upColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(upVisibility)
                            .addComponent(lblUpVisibility, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblBorderStyle)
                            .addComponent(lblDownColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(9, 9, 9))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(downColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(borderStyle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(borderVisibility)
                        .addGap(53, 53, 53)
                        .addComponent(lblGrid))
                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblDownVisibility, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(downVisibility))
                    .addComponent(lblBorderVisibility, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblHColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblVColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblHStyle)
                        .addComponent(lblVStyle))
                    .addComponent(hStyle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vStyle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblHVisibility, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hVisibility)
                    .addComponent(lblVVisibility, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vVisibility))
                .addGap(18, 18, 18)
                .addComponent(lblWindow)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblBackground, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(backgroundColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFont, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(fontChooser)
                        .addComponent(fontValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFontColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fontColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel)
                    .addComponent(btnReset)
                    .addComponent(btnApply)
                    .addComponent(btnOk))
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
