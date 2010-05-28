package org.chartsy.main;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import javax.swing.JComponent;

/**
 *
 * @author viorel.gheba
 */
public abstract class AbstractComponent extends JComponent implements Serializable
{

    private static final long serialVersionUID = 2L;

    protected transient Object obj = new Object();
    private transient BufferedImage img = null;
    private transient BufferedImage old = null;

    @Override
    protected void paintComponent(Graphics g)
    {
        if (img == null || img.getWidth() != getWidth() || img.getHeight() != getHeight()) {
            resetImage();
            paintAbstractComponent(g);
        } else {
            g.drawImage(img, 0, 0, this);
        }
    }

    public void resetImage() {
        if (img == null) { return; }
        old = img;
        img = null;
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (obj != null) {
            resetImage();
        }
    }

    protected abstract void paintAbstractComponent(Graphics g);

}
