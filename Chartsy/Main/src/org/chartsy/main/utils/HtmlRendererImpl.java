package org.chartsy.main.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerListener;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyListener;
import java.awt.event.InputMethodListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.AncestorListener;

/**
 *
 * @author viorel.gheba
 */
public class HtmlRendererImpl extends JLabel implements HtmlRenderer.Renderer
{
    protected static final Rectangle bounds = new Rectangle();
    protected static final boolean swingRendering = true;
    protected static final Insets EMPTY_INSETS = new Insets(0, 0, 0, 0);
    public static enum Type {UNKNOWN, LIST};

    protected static boolean noCacheGraphics = Boolean.getBoolean("nb.renderer.nocache");
    protected static Reference<Graphics> scratchGraphics = null;
    protected boolean centered = false;
    protected boolean parentFocused = false;
    protected Boolean html = null;
    protected int indent = 0;
    protected Border border = null;
    protected boolean selected = false;
    protected boolean leadSelection = false;
    protected Dimension prefSize = null;
    public Type type = Type.UNKNOWN;
    protected int renderStyle = HtmlRenderer.STYLE_CLIP;
    protected boolean enabled = true;

    public void reset()
	{
        parentFocused = false;
        setCentered(false);
        html = null;
        indent = 0;
        border = null;
        setIcon(null);
        setOpaque(false);
        selected = false;
        leadSelection = false;
        prefSize = null;
        type = Type.UNKNOWN;
        renderStyle = HtmlRenderer.STYLE_CLIP;
        setFont(UIManager.getFont("controlFont"));
        setIconTextGap(3);
        setEnabled(true);
        border = null;

        EMPTY_INSETS.top = 0;
        EMPTY_INSETS.left = 0;
        EMPTY_INSETS.right = 0;
        EMPTY_INSETS.bottom = 0;
    }

    public Component getListCellRendererComponent
		(JList list, Object value, int index, boolean selected,
		boolean leadSelection)
	{
        reset();
        configureFrom(value, list, selected, leadSelection);
        type = Type.LIST;

        if (swingRendering && selected)
		{
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
            setOpaque(true);
        }

        if (HtmlLabelUI.isGTK())
		{
            if (index == -1)
			{
                Color borderC = UIManager.getColor("controlShadow");
                borderC = borderC == null ? Color.GRAY : borderC;
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(borderC),
                        BorderFactory.createEmptyBorder(3, 2, 3, 2)));
            } 
			else
			{
                setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            }
        }

        return this;
    }

    protected void configureFrom
		(Object value, JComponent target, boolean selected, boolean leadSelection)
	{
        if (value == null)
            value = "";

        setText((value == null) ? "" : value.toString());
        setSelected(selected);

        if (selected) 
            setParentFocused(checkFocused(target));
		else
            setParentFocused(false);

        setEnabled(target.isEnabled());
        setLeadSelection(leadSelection);
        setFont(target.getFont());
    }

    protected boolean checkFocused(JComponent c)
	{
        Component focused
			= KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
        boolean result = c == focused;

        if (!result)
            result = c.isAncestorOf(focused);

        return result;
    }

    public @Override void addNotify()
	{
        if (swingRendering)
            super.addNotify();
    }

    public @Override void removeNotify()
	{
        if (swingRendering)
            super.removeNotify();
    }

    public void setSelected(boolean val)
	{
        selected = val;
    }

    public void setParentFocused(boolean val)
	{
        parentFocused = val;
    }

    public void setLeadSelection(boolean val)
	{
        leadSelection = val;
    }

    public void setCentered(boolean val)
	{
        centered = val;

        if (val)
            setIconTextGap(5);

        if (swingRendering)
		{
            if (val)
			{
                setVerticalTextPosition(JLabel.BOTTOM);
                setHorizontalAlignment(JLabel.CENTER);
                setHorizontalTextPosition(JLabel.CENTER);
            } 
			else
			{
                setVerticalTextPosition(JLabel.CENTER);
                setHorizontalAlignment(JLabel.LEADING);
                setHorizontalTextPosition(JLabel.TRAILING);
            }
        }
    }

    public void setIndent(int pixels)
	{
        this.indent = pixels;
    }

    public void setHtml(boolean val)
	{
        Boolean wasHtml = html;
        String txt = getText();
        html = val ? Boolean.TRUE : Boolean.FALSE;

        if (swingRendering && (html != wasHtml))
            firePropertyChange("text", txt, getText());
    }

    public void setRenderStyle(int style)
	{
        renderStyle = style;
    }

    int getRenderStyle()
	{
        return renderStyle;
    }

    boolean isLeadSelection()
	{
        return leadSelection;
    }

    boolean isCentered()
	{
        return centered;
    }

    boolean isParentFocused()
	{
        return parentFocused;
    }

    boolean isHtml()
	{
        if (html == null)
		{
            String s = getText();
            html = checkHtml(s);
        }

        return html.booleanValue();
    }

    protected Boolean checkHtml(String s)
	{
        Boolean result;

        if (s == null)
            result = Boolean.FALSE;
		else if (s.startsWith("<html")
			|| s.startsWith("<HTML"))
            result = Boolean.TRUE;
        else
            result = Boolean.FALSE;

        return result;
    }

    boolean isSelected()
	{
        return selected;
    }

    int getIndent()
	{
        return indent;
    }

    Type getType()
	{
        return type;
    }

    public @Override Dimension getPreferredSize()
	{
        if (!swingRendering)
		{
            if (prefSize == null)
                prefSize = getUI().getPreferredSize(this);
            return prefSize;
        } 
		else
		{
            return super.getPreferredSize();
        }
    }

    public @Override String getText()
	{
        String result = super.getText();

        if (swingRendering
			&& Boolean.TRUE.equals(html))
            result = ensureHtmlTags(result);
        else if (swingRendering
			&& (html == null))
		{
            html = checkHtml(super.getText());
            if (Boolean.TRUE.equals(html))
                result = ensureHtmlTags(result);
        }

        return result;
    }

    protected String ensureHtmlTags(String s)
	{
        s = ensureLegalFontColorTags(s);
        if (!s.startsWith("<HTML") 
			&& !s.startsWith("<html"))
            s = "<html>" + s + "</html>";

        return s;
    }

    protected static String ensureLegalFontColorTags(String s)
	{
        String check = s.toUpperCase();
        int start = 0;
        int fidx = check.indexOf("<FONT", start);
        StringBuffer sb = null;

        if ((fidx != -1) 
			&& (fidx <= s.length()))
		{
            while ((fidx != -1) 
				&& (fidx <= s.length()))
			{
                int cidx = check.indexOf("COLOR", start);
                int tagEnd = check.indexOf('>', start);
                start = tagEnd + 1;

                if (tagEnd == -1)
                    break;

                if (cidx != -1)
				{
                    if (cidx < tagEnd)
					{
                        int eidx = check.indexOf('=', cidx);

                        if (eidx != -1)
						{
                            int bangIdx = check.indexOf('!', eidx);

                            if ((bangIdx != -1) && (bangIdx < tagEnd))
							{
                                int colorStart = bangIdx + 1;
                                int colorEnd = tagEnd;

                                for (int i = colorStart; i < tagEnd; i++)
								{
                                    char c = s.charAt(i);
                                    if (!Character.isLetter(c))
									{
                                        colorEnd = i;
                                        break;
                                    }
                                }

                                if (sb == null)
                                    sb = new StringBuffer(s);

                                String colorString = s.substring(colorStart, colorEnd);
                                String converted = convertToStandardColor(colorString);
                                sb.replace(bangIdx, colorEnd, converted);
                                s = sb.toString();
                                check = s.toUpperCase();
                            }
                        }
                    }
                }

                fidx = check.indexOf("<FONT", start);
                start = fidx;
            }
        }

        if (sb != null) 
            return sb.toString();
		else
            return s;
    }

    protected static String convertToStandardColor(String colorString)
	{
        Color c = UIManager.getColor(colorString);
        if (c == null)
            c = Color.BLACK;

		StringBuilder sb = new StringBuilder(7);
        sb.append('#');
        sb.append(hexString(c.getRed()));
        sb.append(hexString(c.getGreen()));
        sb.append(hexString(c.getBlue()));

        return sb.toString();
    }

    protected static String hexString(int r)
	{
        String s = Integer.toHexString(r);
        if (s.length() == 1)
            s = '0' + s;

        return s;
    }

    protected @Override final void firePropertyChange(String name, Object old, Object nue)
	{
        if (swingRendering)
		{
            if ("text".equals(name) && isHtml())
                nue = getText();
            super.firePropertyChange(name, old, nue);
        }
    }

    public @Override Border getBorder()
	{
        Border result;
        if ((indent != 0) && swingRendering)
            result = BorderFactory.createEmptyBorder(0, indent, 0, 0);
		else
            result = border;

        return result;
    }

    public @Override void setBorder(Border b)
	{
        Border old = border;
        border = b;
        if (swingRendering)
            firePropertyChange("border", old, b);
    }

    public @Override Insets getInsets()
	{
        Insets result;
        Border b = getBorder();
        if (b == null) 
            result = EMPTY_INSETS;
        else
            result = b.getBorderInsets(this);

        return result;
    }

    public @Override void setEnabled(boolean b)
	{
        enabled = b;
        if (swingRendering)
            super.setEnabled(b);
    }

    public @Override boolean isEnabled()
	{
        return enabled;
    }

    public @Override void updateUI()
	{
        if (swingRendering)
            super.updateUI();
        else
            setUI(HtmlLabelUI.createUI(this));
    }

    public @Override Graphics getGraphics()
	{
        Graphics result = null;
        if (isDisplayable())
            result = super.getGraphics();

        if (result == null)
            result = scratchGraphics();

        return result;
    }

    protected static final Graphics scratchGraphics()
	{
        Graphics result = null;
        if (scratchGraphics != null)
		{
            result = scratchGraphics.get();
            if (result != null)
                result.setClip(null);
        }

        if (result == null)
		{
            result = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(1, 1).getGraphics();
            if (!noCacheGraphics) 
                scratchGraphics = new SoftReference<Graphics>(result);
        }

        return result;
    }

    public @Override void setBounds(int x, int y, int w, int h)
	{
        if (swingRendering) 
            super.setBounds(x, y, w, h);
        bounds.setBounds(x, y, w, h);
    }

    @Deprecated
    public @Override void reshape(int x, int y, int w, int h)
	{
        if (swingRendering)
            super.reshape(x, y, w, h);
    }

    public @Override int getWidth()
	{
        return bounds.width;
    }

    public @Override int getHeight()
	{
        return bounds.height;
    }

    public @Override Point getLocation()
	{
        return bounds.getLocation();
    }

    public @Override void validate()
	{}

    public @Override void repaint(long tm, int x, int y, int w, int h)
	{}

    public @Override void repaint()
	{}

    public @Override void invalidate()
	{}

    public @Override void revalidate()
	{}

    public @Override void addAncestorListener(AncestorListener l)
	{
        if (swingRendering)
            super.addAncestorListener(l);
    }

    public @Override void addComponentListener(ComponentListener l)
	{
        if (swingRendering)
            super.addComponentListener(l);
    }

    public @Override void addContainerListener(ContainerListener l)
	{
        if (swingRendering) 
            super.addContainerListener(l);
    }

    public @Override void addHierarchyListener(HierarchyListener l)
	{
        if (swingRendering)
            super.addHierarchyListener(l);
    }

    public @Override void addHierarchyBoundsListener(HierarchyBoundsListener l)
	{
        if (swingRendering)
            super.addHierarchyBoundsListener(l);
    }

    public @Override void addInputMethodListener(InputMethodListener l)
	{
        if (swingRendering)
            super.addInputMethodListener(l);
    }

    public @Override void addFocusListener(FocusListener fl)
	{
        if (swingRendering) 
            super.addFocusListener(fl);
    }

    public @Override void addMouseListener(MouseListener ml)
	{
        if (swingRendering) 
            super.addMouseListener(ml);
    }

    public @Override void addMouseWheelListener(MouseWheelListener ml)
	{
        if (swingRendering) 
            super.addMouseWheelListener(ml);
    }

    public @Override void addMouseMotionListener(MouseMotionListener ml)
	{
        if (swingRendering)
            super.addMouseMotionListener(ml);
    }

    public @Override void addVetoableChangeListener(VetoableChangeListener vl)
	{
        if (swingRendering)
            super.addVetoableChangeListener(vl);
    }

    public @Override void addPropertyChangeListener(String s, PropertyChangeListener l)
	{
        if (swingRendering)
            super.addPropertyChangeListener(s, l);
    }

    public @Override void addPropertyChangeListener(PropertyChangeListener l)
	{
        if (swingRendering)
            super.addPropertyChangeListener(l);
    }
}