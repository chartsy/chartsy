package org.chartsy.main.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.event.ChangeEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeListener;
import org.chartsy.main.utils.ComponentGenerator;
import org.chartsy.main.utils.Properties;
import org.chartsy.main.utils.PropertyItem;
import org.chartsy.main.utils.StrokeComboBox;

/**
 *
 * @author viorel.gheba
 */
public class PropertiesPanel extends JPanel {

    private Properties properties;
    private Properties initial;
    private GridBagConstraints c;
    private javax.swing.JDialog dialog;

    public PropertiesPanel(Properties p) {
        super(new GridBagLayout());
        properties = p;
        initial = p;
    }

    public void setDialog(javax.swing.JDialog d) { dialog = d; }
    public void setProperties(Properties properties) { this.properties = properties; }
    public Properties getProperties() { return this.properties; }

    public void initComponents() {
        setBackground(Color.WHITE);
        c = new GridBagConstraints();
        c.insets = new Insets(2, 2, 2, 2);
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.NORTH;
        
        for (int i = 0; i < this.properties.getItems(); i++) {
            final JLabel label = new JLabel(this.properties.getName(i));
            label.setName("lblProperty" + i);

            c.gridx = 0;
            c.gridy = i;
            add(label, c);

            this.addComponent(i, this);
        }

        final JButton reset = new JButton("Reset");
        reset.setName("Reset");
        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                reset();
            }
        });

        c.gridx = 1;
        c.gridy = this.properties.getItems();
        add(reset, c);
    }

    private void reset() { setProperties(initial); }

    private void addComponent(int i, JPanel panel) {
        final PropertyItem p = properties.getPropertyItem(i);
        final String component = p.getComponent();
        
        if (component.equals(ComponentGenerator.JTEXTFIELD)) {
            final JTextField comp = (JTextField) ComponentGenerator.getComponent(component);
            comp.setName(p.getName());
            comp.setText((String) p.getValue());
            comp.setPreferredSize(new Dimension(156, 20));
            comp.setBackground(Color.WHITE);
            comp.addKeyListener(new KeyListener() {
                public void keyTyped(KeyEvent e) {}
                public void keyPressed(KeyEvent e) {}
                public void keyReleased(KeyEvent e) {
                    p.setValue(comp.getText());
                    dialog.repaint();
                }
            });

            c.gridx = 1;
            c.gridy = i;

            panel.add(comp, c);
        } else if (component.equals(ComponentGenerator.JCOMBOBOX)) {
            final JComboBox comp = (JComboBox) ComponentGenerator.getComponent(component);
            comp.setName(p.getName());
            for (String s : p.getList()) {
                comp.addItem(s);
            }
            comp.setSelectedItem(p.getValue());
            comp.setPreferredSize(new Dimension(156, 20));
            comp.setBackground(Color.WHITE);
            comp.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    p.setValue(comp.getSelectedItem());
                    dialog.repaint();
                }
            });

            c.gridx = 1;
            c.gridy = i;

            panel.add(comp, c);
        } else if (component.equals(ComponentGenerator.JSTROKECOMBOBOX)) {
            final StrokeComboBox comp = (StrokeComboBox) ComponentGenerator.getComponent(component);
            comp.setName(p.getName());
            comp.setSelectedIndex(Integer.parseInt((String) p.getValue()));
            comp.setPreferredSize(new Dimension(156, 20));
            comp.setBackground(Color.WHITE);
            comp.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    p.setValue(Integer.toString(comp.getSelectedIndex()));
                    dialog.repaint();
                }
            });

            c.gridx = 1;
            c.gridy = i;

            panel.add(comp, c);
        } else if (component.equals(ComponentGenerator.JLABEL)) {
            final JLabel comp = (JLabel) ComponentGenerator.getComponent(component);
            comp.setName(p.getName());
            comp.setOpaque(true);
            comp.setBorder(BorderFactory.createLineBorder(Color.black));
            comp.setBackground((Color) p.getValue());
            comp.setPreferredSize(new Dimension(156, 20));
            comp.addMouseListener(new MouseListener() {
                public void mouseClicked(MouseEvent e) {
                    Color newColor = JColorChooser.showDialog(null, "Choose Color", comp.getBackground());
                    if (newColor != null) {
                        comp.setBackground(newColor);
                        p.setValue(newColor);
                        dialog.repaint();
                    }
                }
                public void mousePressed(MouseEvent e) {}
                public void mouseReleased(MouseEvent e) {}
                public void mouseEntered(MouseEvent e) {}
                public void mouseExited(MouseEvent e) {}
            });

            c.gridx = 1;
            c.gridy = i;

            panel.add(comp, c);
        } else if (component.equals(ComponentGenerator.JCHECKBOX)) {
            final JCheckBox comp = (JCheckBox) ComponentGenerator.getComponent(component);
            comp.setName(p.getName());
            comp.setText("");
            comp.setSelected((Boolean) p.getValue());
            comp.setPreferredSize(new Dimension(156, 20));
            comp.setBackground(Color.WHITE);
            comp.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    p.setValue(comp.isSelected());
                    dialog.repaint();
                }
            });

            c.gridx = 1;
            c.gridy = i;

            panel.add(comp, c);
        } else if (component.equals(ComponentGenerator.JSLIDER)) {
            final JSlider comp = (JSlider) ComponentGenerator.getComponent(component);
            comp.setName(p.getName());
            comp.setMinimum(0);
            comp.setMaximum(255);
            comp.setExtent(5);
            comp.setValue(Integer.parseInt((String) p.getValue()));
            comp.setPreferredSize(new Dimension(156, 20));
            comp.setBackground(Color.WHITE);
            comp.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    p.setValue(String.valueOf(comp.getValue()));
                    dialog.repaint();
                }
            });

            c.gridx = 1;
            c.gridy = i;

            panel.add(comp, c);
        }
    }

}
