package org.chartsy.main.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerListener;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyListener;
import java.awt.event.InputMethodListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.font.LineMetrics;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.AncestorListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.LabelUI;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

/**
 *
 * @author viorel.gheba
 */
public class HtmlRendererImpl extends JLabel implements HtmlRenderer.Renderer
{
    private static final Rectangle bounds = new Rectangle();
    private static final boolean swingRendering = Boolean.getBoolean("nb.useSwingHtmlRendering"); //NOI18N
    private static final Insets EMPTY_INSETS = new Insets(0, 0, 0, 0);
    enum Type {UNKNOWN, TREE, LIST, TABLE}

    //For experimentation - holding the graphics object may be the source of some
    //strange painting problems on Apple
    private static boolean noCacheGraphics = Boolean.getBoolean("nb.renderer.nocache"); //NOI18N
    private static Reference<Graphics> scratchGraphics = null;
    private boolean centered = false;
    private boolean parentFocused = false;
    private Boolean html = null;
    private int indent = 0;
    private Border border = null;
    private boolean selected = false;
    private boolean leadSelection = false;
    private Dimension prefSize = null;
    private Type type = Type.UNKNOWN;
    private int renderStyle = HtmlRenderer.STYLE_CLIP;
    private boolean enabled = true;

    /** Restore the renderer to a pristine state */
    public void reset() {
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
        setFont(UIManager.getFont("controlFont")); //NOI18N
        setIconTextGap(3);
        setEnabled(true);
        border = null;

        //Defensively ensure the insets haven't been messed with
        EMPTY_INSETS.top = 0;
        EMPTY_INSETS.left = 0;
        EMPTY_INSETS.right = 0;
        EMPTY_INSETS.bottom = 0;
    }

    public Component getTableCellRendererComponent(
        JTable table, Object value, boolean selected, boolean leadSelection, int row, int column
    ) {
        reset();
        configureFrom(value, table, selected, leadSelection);
        type = Type.TABLE;

        if (swingRendering && selected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
            setOpaque(true);
        }

        return this;
    }

    public Component getTreeCellRendererComponent(
        JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean leadSelection
    ) {
        reset();
        configureFrom(value, tree, selected, leadSelection);
        type = Type.TREE;

        if (swingRendering && selected) {
            if (HtmlLabelUI.isGTK()) {
                setBackground(HtmlLabelUI.getBackgroundFor(this));
                setForeground(HtmlLabelUI.getForegroundFor(this));
            }
            setOpaque(true);
        }

        return this;
    }

    public Component getListCellRendererComponent(
        JList list, Object value, int index, boolean selected, boolean leadSelection
    ) {
        reset();
        configureFrom(value, list, selected, leadSelection);
        type = Type.LIST;

        if (swingRendering && selected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
            setOpaque(true);
        }

        // ##93658: In GTK we have to paint borders in combo boxes
        if (HtmlLabelUI.isGTK()) {
            if (index == -1) {
                Color borderC = UIManager.getColor("controlShadow");
                borderC = borderC == null ? Color.GRAY : borderC;
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(borderC),
                        BorderFactory.createEmptyBorder(3, 2, 3, 2)));
            } else {
                setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            }
        }

        return this;
    }

    /** Generic code to set properties appropriately from any of the renderer
     * fetching methods */
    private void configureFrom(Object value, JComponent target, boolean selected, boolean leadSelection) {
        if (value == null) {
            value = "";
        }

        setText((value == null) ? "" : value.toString());

        setSelected(selected);

        if (selected) {
            setParentFocused(checkFocused(target));
        } else {
            setParentFocused(false);
        }

        setEnabled(target.isEnabled());

        setLeadSelection(leadSelection);

        setFont(target.getFont());
    }

    private boolean checkFocused(JComponent c) {
        Component focused = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
        boolean result = c == focused;

        if (!result) {
            result = c.isAncestorOf(focused);
        }

        return result;
    }

    public @Override void addNotify() {
        if (swingRendering) {
            super.addNotify();
        }
    }

    public @Override void removeNotify() {
        if (swingRendering) {
            super.removeNotify();
        }
    }

    public void setSelected(boolean val) {
        selected = val;
    }

    public void setParentFocused(boolean val) {
        parentFocused = val;
    }

    public void setLeadSelection(boolean val) {
        leadSelection = val;
    }

    public void setCentered(boolean val) {
        centered = val;

        if (val) {
            setIconTextGap(5);
        }

        if (swingRendering) {
            if (val) {
                setVerticalTextPosition(JLabel.BOTTOM);
                setHorizontalAlignment(JLabel.CENTER);
                setHorizontalTextPosition(JLabel.CENTER);
            } else {
                setVerticalTextPosition(JLabel.CENTER);
                setHorizontalAlignment(JLabel.LEADING);
                setHorizontalTextPosition(JLabel.TRAILING);
            }
        }
    }

    public void setIndent(int pixels) {
        this.indent = pixels;
    }

    public void setHtml(boolean val) {
        Boolean wasHtml = html;
        String txt = getText();
        html = val ? Boolean.TRUE : Boolean.FALSE;

        if (swingRendering && (html != wasHtml)) {
            //Ensure label UI gets updated and builds its little document tree...
            firePropertyChange("text", txt, getText()); //NOI18N
        }
    }

    public void setRenderStyle(int style) {
        renderStyle = style;
    }

    int getRenderStyle() {
        return renderStyle;
    }

    boolean isLeadSelection() {
        return leadSelection;
    }

    boolean isCentered() {
        return centered;
    }

    boolean isParentFocused() {
        return parentFocused;
    }

    boolean isHtml() {
        if (html == null) {
            String s = getText();
            html = checkHtml(s);
        }

        return html.booleanValue();
    }

    private Boolean checkHtml(String s) {
        Boolean result;

        if (s == null) {
            result = Boolean.FALSE;
        } else if (s.startsWith("<html") || s.startsWith("<HTML")) { //NOI18N
            result = Boolean.TRUE;
        } else {
            result = Boolean.FALSE;
        }

        return result;
    }

    boolean isSelected() {
        return selected;
    }

    int getIndent() {
        return indent;
    }

    Type getType() {
        return type;
    }

    public @Override Dimension getPreferredSize() {
        if (!swingRendering) {
            if (prefSize == null) {
                prefSize = getUI().getPreferredSize(this);
            }

            return prefSize;
        } else {
            return super.getPreferredSize();
        }
    }

    /**
     * Overridden for the case that we're running with the lightweight html renderer disabled, to convert
     * any less-than-legal html to legal html for purposes of Swing's html rendering.
     *
     * @return The text - unless the renderer is disabled, this just return super.getText()
     */
    public @Override String getText() {
        String result = super.getText();

        if (swingRendering && Boolean.TRUE.equals(html)) {
            //Standard swing rendering needs an opening HTML tag to function, so make sure there is
            //one if we're not using HtmlLabelUI
            result = ensureHtmlTags(result);
        } else if (swingRendering && (html == null)) {
            //Cannot call isHtml() here, it will create an endless loop, so manually check the HTML status
            html = checkHtml(super.getText());

            if (Boolean.TRUE.equals(html)) {
                result = ensureHtmlTags(result);
            }
        }

        return result;
    }

    /**
     * Converts our extended html syntax (allowing UIManager color keys and omitting opening html tags
     * into standard html.  Only called if the lightweight html renderer is disabled and we're running with
     * a standard JLabel UI
     *
     * @param s The string that is the text of the label
     * @return The same string converted to standard HTML Swing's rendering infrastructure will know what to do
     *         with
     */
    private String ensureHtmlTags(String s) {
        s = ensureLegalFontColorTags(s);

        if (!s.startsWith("<HTML") && !s.startsWith("<html")) { //NOI18N
            s = "<html>" + s + "</html>"; //NOI18N
        }

        return s;
    }

    /**
     * Converts extended UI manager color tags into legal html in the case that we're using swing rendering
     *
     * @param s string to convert if it has questionable font tags
     * @return The converted string
     */
    private static String ensureLegalFontColorTags(String s) {
        String check = s.toUpperCase();
        int start = 0;
        int fidx = check.indexOf("<FONT", start); //NOI18N
        StringBuffer sb = null;

        if ((fidx != -1) && (fidx <= s.length())) {
            while ((fidx != -1) && (fidx <= s.length())) {
                int cidx = check.indexOf("COLOR", start); //NOI18N
                int tagEnd = check.indexOf('>', start); //NOI18N
                start = tagEnd + 1;

                if (tagEnd == -1) {
                    break;
                }

                if (cidx != -1) {
                    if (cidx < tagEnd) {
                        //we have a font color tag
                        int eidx = check.indexOf('=', cidx); //NOI18N

                        if (eidx != -1) {
                            int bangIdx = check.indexOf('!', eidx); //NOI18N

                            if ((bangIdx != -1) && (bangIdx < tagEnd)) {
                                int colorStart = bangIdx + 1;
                                int colorEnd = tagEnd;

                                for (int i = colorStart; i < tagEnd; i++) {
                                    char c = s.charAt(i);

                                    if (!Character.isLetter(c)) {
                                        colorEnd = i;

                                        break;
                                    }
                                }

                                if (sb == null) {
                                    sb = new StringBuffer(s);
                                }

                                String colorString = s.substring(colorStart, colorEnd);
                                String converted = convertToStandardColor(colorString);
                                sb.replace(bangIdx, colorEnd, converted);
                                s = sb.toString();
                                check = s.toUpperCase();
                            }
                        }
                    }
                }

                fidx = check.indexOf("<FONT", start); //NOI18N
                start = fidx;
            }
        }

        if (sb != null) {
            return sb.toString();
        } else {
            return s;
        }
    }

    /**
     * Creates a standard html #nnnnnn string from a string representing a UIManager key.  If the color is not found,
     * black will be used.  Only used if the lightweight html renderer is disabled.
     *
     * @param colorString  A string found after a ! character in a color definition, which needs to be converted to
     *        standard HTML
     * @return A hex number string
     */
    private static String convertToStandardColor(String colorString) {
        Color c = UIManager.getColor(colorString);

        if (c == null) {
            c = Color.BLACK;
        }

        StringBuffer sb = new StringBuffer(7);
        sb.append('#');
        sb.append(hexString(c.getRed()));
        sb.append(hexString(c.getGreen()));
        sb.append(hexString(c.getBlue()));

        return sb.toString();
    }

    /**
     * Gets a hex string for an integer.  Ensures the result is always two characters long, which is not
     * true of Integer.toHexString().
     *
     * @param r an integer < 255
     * @return a 2 character hexadecimal string
     */
    private static String hexString(int r) {
        String s = Integer.toHexString(r);

        if (s.length() == 1) {
            s = '0' + s;
        }

        return s;
    }

    /** Overridden to do nothing under normal circumstances.  If the boolean flag to <strong>not</strong> use the
     * internal HTML renderer is in effect, this will fire changes normally */
    protected @Override final void firePropertyChange(String name, Object old, Object nue) {
        if (swingRendering) {
            if ("text".equals(name) && isHtml()) {
                //Force in the HTML tags so the UI will set up swing HTML rendering appropriately
                nue = getText();
            }

            super.firePropertyChange(name, old, nue);
        }
    }

    public @Override Border getBorder() {
        Border result;

        if ((indent != 0) && swingRendering) {
            result = BorderFactory.createEmptyBorder(0, indent, 0, 0);
        } else {
            result = border;
        }

        return result;
    }

    public @Override void setBorder(Border b) {
        Border old = border;
        border = b;

        if (swingRendering) {
            firePropertyChange("border", old, b);
        }
    }

    public @Override Insets getInsets() {
        Insets result;

        //Call getBorder(), not just read the field - if swingRendering, the border will be constructed, and the
        //insets are what will make the indent property work;  HtmlLabelUI doesn't need this, it just reads the
        //insets property, but BasicLabelUI and its ilk do
        Border b = getBorder();

        if (b == null) {
            result = EMPTY_INSETS;
        } else {
            result = b.getBorderInsets(this);
        }

        return result;
    }

    public @Override void setEnabled(boolean b) {
        //OptimizeIt shows about 12Ms overhead calling back to Component.enable(), so avoid it if possible
        enabled = b;

        if (swingRendering) {
            super.setEnabled(b);
        }
    }

    public @Override boolean isEnabled() {
        return enabled;
    }

    public @Override void updateUI() {
        if (swingRendering) {
            super.updateUI();
        } else {
            setUI(HtmlLabelUI.createUI(this));
        }
    }

    /** Overridden to produce a graphics object even when isDisplayable() is
     * false, so that calls to getPreferredSize() will return accurate
     * dimensions (presuming the font and text are set correctly) even when
     * not onscreen. */
    public @Override Graphics getGraphics() {
        Graphics result = null;

        if (isDisplayable()) {
            result = super.getGraphics();
        }

        if (result == null) {
            result = scratchGraphics();
        }

        return result;
    }

    /** Fetch a scratch graphics object for calculating preferred sizes while
     * offscreen */
    private static final Graphics scratchGraphics() {
        Graphics result = null;

        if (scratchGraphics != null) {
            result = scratchGraphics.get();

            if (result != null) {
                result.setClip(null); //just in case somebody did something nasty
            }
        }

        if (result == null) {
            result = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(1, 1).getGraphics();

            if (!noCacheGraphics) {
                scratchGraphics = new SoftReference<Graphics>(result);
            }
        }

        return result;
    }

    public @Override void setBounds(int x, int y, int w, int h) {
        if (swingRendering) {
            super.setBounds(x, y, w, h);
        }

        bounds.setBounds(x, y, w, h);
    }

    @Deprecated
    public @Override void reshape(int x, int y, int w, int h) {
        if (swingRendering) {
            super.reshape(x, y, w, h);
        }
    }

    public @Override int getWidth() {
        return bounds.width;
    }

    public @Override int getHeight() {
        return bounds.height;
    }

    public @Override Point getLocation() {
        return bounds.getLocation();
    }

    /** Overridden to do nothing for performance reasons */
    public @Override void validate() {
        //do nothing
    }

    /** Overridden to do nothing for performance reasons */
    public @Override void repaint(long tm, int x, int y, int w, int h) {
        //do nothing
    }

    /** Overridden to do nothing for performance reasons */
    public @Override void repaint() {
        //do nothing
    }

    /** Overridden to do nothing for performance reasons */
    public @Override void invalidate() {
        //do nothing
    }

    /** Overridden to do nothing for performance reasons */
    public @Override void revalidate() {
        //do nothing
    }

    /** Overridden to do nothing for performance reasons */
    public @Override void addAncestorListener(AncestorListener l) {
        if (swingRendering) {
            super.addAncestorListener(l);
        }
    }

    /** Overridden to do nothing for performance reasons */
    public @Override void addComponentListener(ComponentListener l) {
        if (swingRendering) {
            super.addComponentListener(l);
        }
    }

    /** Overridden to do nothing for performance reasons */
    public @Override void addContainerListener(ContainerListener l) {
        if (swingRendering) {
            super.addContainerListener(l);
        }
    }

    /** Overridden to do nothing for performance reasons */
    public @Override void addHierarchyListener(HierarchyListener l) {
        if (swingRendering) {
            super.addHierarchyListener(l);
        }
    }

    /** Overridden to do nothing for performance reasons */
    public @Override void addHierarchyBoundsListener(HierarchyBoundsListener l) {
        if (swingRendering) {
            super.addHierarchyBoundsListener(l);
        }
    }

    /** Overridden to do nothing for performance reasons */
    public @Override void addInputMethodListener(InputMethodListener l) {
        if (swingRendering) {
            super.addInputMethodListener(l);
        }
    }

    /** Overridden to do nothing for performance reasons */
    public @Override void addFocusListener(FocusListener fl) {
        if (swingRendering) {
            super.addFocusListener(fl);
        }
    }

    /** Overridden to do nothing for performance reasons */
    public @Override void addMouseListener(MouseListener ml) {
        if (swingRendering) {
            super.addMouseListener(ml);
        }
    }

    /** Overridden to do nothing for performance reasons */
    public @Override void addMouseWheelListener(MouseWheelListener ml) {
        if (swingRendering) {
            super.addMouseWheelListener(ml);
        }
    }

    /** Overridden to do nothing for performance reasons */
    public @Override void addMouseMotionListener(MouseMotionListener ml) {
        if (swingRendering) {
            super.addMouseMotionListener(ml);
        }
    }

    /** Overridden to do nothing for performance reasons */
    public @Override void addVetoableChangeListener(VetoableChangeListener vl) {
        if (swingRendering) {
            super.addVetoableChangeListener(vl);
        }
    }

    /** Overridden to do nothing for performance reasons, unless using standard swing rendering */
    public @Override void addPropertyChangeListener(String s, PropertyChangeListener l) {
        if (swingRendering) {
            super.addPropertyChangeListener(s, l);
        }
    }

    public @Override void addPropertyChangeListener(PropertyChangeListener l) {
        if (swingRendering) {
            super.addPropertyChangeListener(l);
        }
    }
}

final class HtmlRenderer {
    private static HtmlRendererImpl LABEL = null;

    /** Stack object used during HTML rendering to hold previous colors in
     * the case of nested color entries. */
    private static Stack<Color> colorStack = new Stack<Color>();

    /**
     * Constant used by {@link #renderString renderString}, {@link #renderPlainString renderPlainString},
     * {@link #renderHTML renderHTML}, and {@link Renderer#setRenderStyle}
     * if painting should simply be cut off at the boundary of the cooordinates passed.
     */
    public static final int STYLE_CLIP = 0;

    /**
     * Constant used by {@link #renderString renderString}, {@link #renderPlainString renderPlainString},
     * {@link #renderHTML renderHTML}, and {@link Renderer#setRenderStyle} if
     * painting should produce an ellipsis (...)
     * if the text would overlap the boundary of the coordinates passed.
     */
    public static final int STYLE_TRUNCATE = 1;

    /**
     * Constant used by {@link #renderString renderString}, {@link #renderPlainString renderPlainString},
     * {@link #renderHTML renderHTML}, and {@link Renderer#setRenderStyle}
     * if painting should word wrap the text.  In
     * this case, the return value of any of the above methods will be the
     * height, rather than width painted.
     */
    private static final int STYLE_WORDWRAP = 2;

    /** System property to cause exceptions to be thrown when unparsable
     * html is encountered */
    private static final boolean STRICT_HTML = Boolean.getBoolean("netbeans.lwhtml.strict"); //NOI18N

    /** Cache for strings which have produced errors, so we don't post an
     * error message more than once */
    private static Set<String> badStrings = null;

    private static Logger LOG = Logger.getLogger(HtmlRenderer.class.getName());

    /** Definitions for a limited subset of SGML character entities */
    private static final Object[] entities = new Object[] {
            new char[] { 'g', 't' }, new char[] { 'l', 't' }, //NOI18N
            new char[] { 'q', 'u', 'o', 't' }, new char[] { 'a', 'm', 'p' }, //NOI18N
            new char[] { 'l', 's', 'q', 'u', 'o' }, //NOI18N
            new char[] { 'r', 's', 'q', 'u', 'o' }, //NOI18N
            new char[] { 'l', 'd', 'q', 'u', 'o' }, //NOI18N
            new char[] { 'r', 'd', 'q', 'u', 'o' }, //NOI18N
            new char[] { 'n', 'd', 'a', 's', 'h' }, //NOI18N
            new char[] { 'm', 'd', 'a', 's', 'h' }, //NOI18N
            new char[] { 'n', 'e' }, //NOI18N
            new char[] { 'l', 'e' }, //NOI18N
            new char[] { 'g', 'e' }, //NOI18N
            new char[] { 'c', 'o', 'p', 'y' }, //NOI18N
            new char[] { 'r', 'e', 'g' }, //NOI18N
            new char[] { 't', 'r', 'a', 'd', 'e' }, //NOI18N
            new char[] { 'n', 'b', 's', 'p' //NOI18N
            }
        }; //NOI18N

    /** Mappings for the array of SGML character entities to characters */
    private static final char[] entitySubstitutions = new char[] {
            '>', '<', '"', '&', 8216, 8217, 8220, 8221, 8211, 8212, 8800, 8804, 8805, //NOI18N
            169, 174, 8482, ' '
        };
    private HtmlRenderer() {
        //do nothing
    }

    /**
     * Returns an instance of Renderer which may be used as a table/tree/list cell renderer.
     * This method must be called on the AWT event thread.  If you <strong>know</strong> you will
     * be passing it legal HTML (legal as documented here), call {@link Renderer#setHtml setHtml(true)} on the
     * result of this call <strong>after calling getNNNCellRenderer</strong> to provide this hint.
     *
     * @return A cell renderer that can render HTML.
     */
    public static final Renderer createRenderer() {
        return new HtmlRendererImpl();
    }

    /**
     * For HTML rendering jobs outside of trees/lists/tables, returns a JLabel which will paint its text using
     * the lightweight HTML renderer.  The result of this call will implement {@link Renderer}.
     * <strong>Do not add the result of this call to the AWT hierarchy</strong>.  It is not a general purpose <code>JLabel</code>, and
     * will not behave correctly.  Use the result of this call to paint or to measure text.  Example:
     * <pre>
     * private final JLabel label = HtmlRenderer.createLabel();
     *
     * public void paint(Graphics g) {
     *    // background/whatever painting code here...
     *    label.setText(someHtmlText);
     *    label.paint(g);
     * }
     * </pre>
     *
     *
     * @return a label which can render a subset of HTML very quickly
     */
    public static final JLabel createLabel() {
        return new HtmlRendererImpl();
    }

    /**
     * Render a string to a graphics instance, using the same API as {@link #renderHTML renderHTML}.
     * Can render a string using JLabel-style ellipsis (...) in the case that
     * it will not fit in the passed rectangle, if the style parameter is
     * {@link #STYLE_CLIP}. Returns the width in pixels successfully painted.
     * <strong>This method is not thread-safe and should not be called off
     * the AWT thread.</strong>
     *
     * @see #renderHTML
     */
    public static double renderPlainString(
        String s, Graphics g, int x, int y, int w, int h, Font f, Color defaultColor, int style, boolean paint
    ) {
        //per Jarda's request, keep the word wrapping code but don't expose it.
        if ((style < 0) || (style > 1)) {
            throw new IllegalArgumentException("Unknown rendering mode: " + style); //NOI18N
        }

        return _renderPlainString(s, g, x, y, w, h, f, defaultColor, style, paint);
    }

    private static double _renderPlainString(
        String s, Graphics g, int x, int y, int w, int h, Font f, Color foreground, int style, boolean paint
    ) {
        if (f == null) {
            f = UIManager.getFont("controlFont"); //NOI18N

            if (f == null) {
                int fs = 11;
                Object cfs = UIManager.get("customFontSize"); //NOI18N

                if (cfs instanceof Integer) {
                    fs = ((Integer) cfs).intValue();
                }

                f = new Font("Dialog", Font.PLAIN, fs); //NOI18N
            }
        }

        FontMetrics fm = g.getFontMetrics(f);
//        Rectangle2D r = fm.getStringBounds(s, g);
        int wid;
        if (Utilities.isMac()) {
            // #54257 - on macosx + chinese/japanese fonts, the getStringBounds() method returns bad value
            wid = fm.stringWidth(s);
        } else {
            wid = (int)fm.getStringBounds(s, g).getWidth();
        }

        if (paint) {
            g.setColor(foreground);
            g.setFont(f);

            if ((wid <= w) || (style == STYLE_CLIP)) {
                g.drawString(s, x, y);
            } else {
                char[] chars = s.toCharArray();

                if (chars.length == 0) {
                    return 0;
                }

                double chWidth = wid / chars.length;
                int estCharsToPaint = new Double(w / chWidth).intValue();
                if( estCharsToPaint > chars.length )
                    estCharsToPaint = chars.length;
                //let's correct the estimate now
                while( estCharsToPaint > 3 ) {
                    if( estCharsToPaint < chars.length )
                        Arrays.fill(chars, estCharsToPaint - 3, estCharsToPaint, '.'); //NOI18N
                    int  newWidth;
                    if (Utilities.isMac()) {
                        // #54257 - on macosx + chinese/japanese fonts, the getStringBounds() method returns bad value
                        newWidth = fm.stringWidth(new String(chars, 0, estCharsToPaint));
                    } else {
                        newWidth = (int)fm.getStringBounds(chars, 0, estCharsToPaint, g).getWidth();
                    }
                    if( newWidth <= w )
                        break;
                    estCharsToPaint--;
                }

                if (style == STYLE_TRUNCATE) {
                    int length = estCharsToPaint;

                    if (length <= 0) {
                        return 0;
                    }

                    if (paint) {
                        if (length > 3) {
                            g.drawChars(chars, 0, length, x, y);
                        } else {
                            Shape shape = g.getClip();
                            // clip only if clipping is supported
                            if (shape != null) {
                                if (s != null) {
                                    Area area = new Area(shape);
                                    area.intersect(new Area(new Rectangle(x, y, w, h)));
                                    g.setClip(area);
                                } else {
                                    g.setClip(new Rectangle(x, y, w, h));
                                }
                            }

                            g.drawString("...", x, y);
                            if (shape != null) {
                                g.setClip(shape);
                            }
                        }
                    }
                } else {
                    //TODO implement plaintext word wrap if we want to support it at some point
                }
            }
        }

        return wid;
    }

    /**
     * Render a string to a graphics context, using HTML markup if the string
     * begins with <code>&lt;html&gt;</code>.  Delegates to {@link #renderPlainString renderPlainString}
     * or {@link #renderHTML renderHTML} as appropriate.  See the class documentation for
     * for details of the subset of HTML that is
     * supported.
     * @param s The string to render
     * @param g A graphics object into which the string should be drawn, or which should be
     * used for calculating the appropriate size
     * @param x The x coordinate to paint at.
     * @param y The y position at which to paint.  Note that this method does not calculate font
     * height/descent - this value should be the baseline for the line of text, not
     * the upper corner of the rectangle to paint in.
     * @param w The maximum width within which to paint.
     * @param h The maximum height within which to paint.
     * @param f The base font to be used for painting or calculating string width/height.
     * @param defaultColor The base color to use if no font color is specified as html tags
     * @param style The wrapping style to use, either {@link #STYLE_CLIP},
     * or {@link #STYLE_TRUNCATE}
     * @param paint True if actual painting should occur.  If false, this method will not actually
     * paint anything, only return a value representing the width/height needed to
     * paint the passed string.
     * @return The width in pixels required
     * to paint the complete string, or the passed parameter <code>w</code> if it is
     * smaller than the required width.
     */
    public static double renderString(
        String s, Graphics g, int x, int y, int w, int h, Font f, Color defaultColor, int style, boolean paint
    ) {
        switch (style) {
        case STYLE_CLIP:
        case STYLE_TRUNCATE:
            break;

        default:
            throw new IllegalArgumentException("Unknown rendering mode: " + style); //NOI18N
        }

        //        System.err.println ("rps: " + y + " " + s);
        if (s.startsWith("<html") || s.startsWith("<HTML")) { //NOI18N

            return _renderHTML(s, 6, g, x, y, w, h, f, defaultColor, style, paint, null);
        } else {
            return renderPlainString(s, g, x, y, w, h, f, defaultColor, style, paint);
        }
    }

    /**
     * Render a string as HTML using a fast, lightweight renderer supporting a limited
     * subset of HTML.  See class Javadoc for details.
     *
     * <P>
     * This method can also be used in non-painting mode to establish the space
     * necessary to paint a string.  This is accomplished by passing the value of the
     * <code>paint</code> argument as false.  The return value will be the required
     * width in pixels
     * to display the text.  Note that in order to retrieve an
     * accurate value, the argument for available width should be passed
     * as {@link Integer#MAX_VALUE} or an appropriate maximum size - otherwise
     * the return value will either be the passed maximum width or the required
     * width, whichever is smaller.  Also, the clip shape for the passed graphics
     * object should be null or a value larger than the maximum possible render size.
     * <P>
     * This method will log a warning if it encounters HTML markup it cannot
     * render.  To aid diagnostics, if NetBeans is run with the argument
     * <code>-J-Dnetbeans.lwhtml.strict=true</code> an exception will be thrown
     * when an attempt is made to render unsupported HTML.
     * @param s The string to render
     * @param g A graphics object into which the string should be drawn, or which should be
     * used for calculating the appropriate size
     * @param x The x coordinate to paint at.
     * @param y The y position at which to paint.  Note that this method does not calculate font
     * height/descent - this value should be the baseline for the line of text, not
     * the upper corner of the rectangle to paint in.
     * @param w The maximum width within which to paint.
     * @param h The maximum height within which to paint.
     * @param f The base font to be used for painting or calculating string width/height.
     * @param defaultColor The base color to use if no font color is specified as html tags
     * @param style The wrapping style to use, either {@link #STYLE_CLIP},
     * or {@link #STYLE_TRUNCATE}
     * @param paint True if actual painting should occur.  If false, this method will not actually
     * paint anything, only return a value representing the width/height needed to
     * paint the passed string.
     * @return The width in pixels required
     * to paint the complete string, or the passed parameter <code>w</code> if it is
     * smaller than the required width.
     */
    public static double renderHTML(
        String s, Graphics g, int x, int y, int w, int h, Font f, Color defaultColor, int style, boolean paint
    ) {
        //per Jarda's request, keep the word wrapping code but don't expose it.
        if ((style < 0) || (style > 1)) {
            throw new IllegalArgumentException("Unknown rendering mode: " + style); //NOI18N
        }

        return _renderHTML(s, 0, g, x, y, w, h, f, defaultColor, style, paint, null);
    }

    /** Implementation of HTML rendering */
    static double _renderHTML(
        String s, int pos, Graphics g, int x, int y, int w, int h, Font f, Color defaultColor, int style, boolean paint,
        Color background
    ) {
        //        System.err.println ("rhs: " + y + " " + s);
        if (f == null) {
            f = UIManager.getFont("controlFont"); //NOI18N

            if (f == null) {
                int fs = 11;
                Object cfs = UIManager.get("customFontSize"); //NOI18N

                if (cfs instanceof Integer) {
                    fs = ((Integer) cfs).intValue();
                }

                f = new Font("Dialog", Font.PLAIN, fs); //NOI18N
            }
        }

        //Thread safety - avoid allocating memory for the common case
        Stack<Color> _colorStack = SwingUtilities.isEventDispatchThread() ? HtmlRenderer.colorStack : new Stack<Color>();

        g.setColor(defaultColor);
        g.setFont(f);

        char[] chars = s.toCharArray();
        int origX = x;
        boolean done = false; //flag if rendering completed, either by finishing the string or running out of space
        boolean inTag = false; //flag if the current position is inside a tag, and the tag should be processed rather than rendering
        boolean inClosingTag = false; //flag if the current position is inside a closing tag
        boolean strikethrough = false; //flag if a strikethrough line should be painted
        boolean underline = false; //flag if an underline should be painted
        boolean bold = false; //flag if text is currently bold
        boolean italic = false; //flag if text is currently italic
        boolean truncated = false; //flag if the last possible character has been painted, and the next loop should paint "..." and return
        double widthPainted = 0; //the total width painted, for calculating needed space
        double heightPainted = 0; //the total height painted, for calculating needed space
        boolean lastWasWhitespace = false; //flag to skip additional whitespace if one whitespace char already painted
        double lastHeight = 0; //the last line height, for calculating total required height

        double dotWidth = 0;

        //Calculate the width of a . character if we may need to truncate
        if (style == STYLE_TRUNCATE) {
            dotWidth = g.getFontMetrics().charWidth('.'); //NOI18N
        }

        /* How this all works, for anyone maintaining this code (hopefully it will
          never need it):
          1. The string is converted to a char array
          2. Loop over the characters.  Variable pos is the current point.
            2a. See if we're in a tag by or'ing inTag with currChar == '<'
              If WE ARE IN A TAG:
               2a1: is it an opening tag?
                 If YES:
                   - Identify the tag, Configure the Graphics object with
                     the appropriate font, color, etc.  Set pos = the first
                     character after the tag
                 If NO (it's a closing tag)
                   - Identify the tag.  Reconfigure the Graphics object
                     with the state it should be in outside the tag
                     (reset the font if italic, pop a color off the stack, etc.)
            2b. If WE ARE NOT IN A TAG
               - Locate the next < or & character or the end of the string
               - Paint the characters using the Graphics object
               - Check underline and strikethrough tags, and paint line if
                 needed
            See if we're out of space, and do the right thing for the style
            (paint ..., give up or skip to the next line)
         */
        //Clear any junk left behind from a previous rendering loop
        _colorStack.clear();

        //Enter the painting loop
        while (!done) {
            if (pos == s.length()) {
                return widthPainted;
            }

            //see if we're in a tag
            try {
                inTag |= (chars[pos] == '<');
            } catch (ArrayIndexOutOfBoundsException e) {
                //Should there be any problem, give a meaningful enough
                //message to reproduce the problem
                ArrayIndexOutOfBoundsException aib = new ArrayIndexOutOfBoundsException(
                        "HTML rendering failed at position " + pos + " in String \"" //NOI18N
                         +s + "\".  Please report this at http://www.netbeans.org"
                    ); //NOI18N

                if (STRICT_HTML) {
                    throw aib;
                } else {
                    Logger.getLogger(HtmlRenderer.class.getName()).log(Level.WARNING, null, aib);

                    return renderPlainString(s, g, x, y, w, h, f, defaultColor, style, paint);
                }
            }

            inClosingTag = inTag && ((pos + 1) < chars.length) && (chars[pos + 1] == '/'); //NOI18N

            if (truncated) {
                //Then we've almost run out of space, time to print ... and quit
                g.setColor(defaultColor);
                g.setFont(f);

                if (paint) {
                    g.drawString("...", x, y); //NOI18N
                }

                done = true;
            } else if (inTag) {
                //If we're in a tag, don't paint, process it
                pos++;

                int tagEnd = pos;

                //#54237 - if done and end of string -> wrong html
                done = tagEnd >= (chars.length - 1);

                while (!done && (chars[tagEnd] != '>')) {
                    done = tagEnd == (chars.length - 1);
                    tagEnd++;
                }

                if (done) {
                    throwBadHTML("Matching '>' not found", pos, chars);
                    break;
                }

                if (inClosingTag) {
                    //Handle closing tags by resetting the Graphics object (font, etc.)
                    pos++;

                    switch (chars[pos]) {
                    case 'P': //NOI18N
                    case 'p': //NOI18N
                    case 'H': //NOI18N
                    case 'h':
                        break; //ignore html opening/closing tags

                    case 'B': //NOI18N
                    case 'b': //NOI18N

                        if ((chars[pos + 1] == 'r') || (chars[pos + 1] == 'R')) {
                            break;
                        }

                        if (!bold) {
                            throwBadHTML("Closing bold tag w/o " + //NOI18N
                                "opening bold tag", pos, chars
                            ); //NOI18N
                        }

                        if (italic) {
                            g.setFont(deriveFont(f, Font.ITALIC));
                        } else {
                            g.setFont(deriveFont(f, Font.PLAIN));
                        }

                        bold = false;

                        break;

                    case 'E': //NOI18N
                    case 'e': //em tag
                    case 'I': //NOI18N
                    case 'i': //NOI18N

                        if (bold) {
                            g.setFont(deriveFont(f, Font.BOLD));
                        } else {
                            g.setFont(deriveFont(f, Font.PLAIN));
                        }

                        if (!italic) {
                            throwBadHTML("Closing italics tag w/o" //NOI18N
                                 +"opening italics tag", pos, chars
                            ); //NOI18N
                        }

                        italic = false;

                        break;

                    case 'S': //NOI18N
                    case 's': //NOI18N

                        switch (chars[pos + 1]) {
                        case 'T': //NOI18N
                        case 't':

                            if (italic) { //NOI18N
                                g.setFont(deriveFont(f, Font.ITALIC));
                            } else {
                                g.setFont(deriveFont(f, Font.PLAIN));
                            }

                            bold = false;

                            break;

                        case '>': //NOI18N
                            strikethrough = false;

                            break;
                        }

                        break;

                    case 'U': //NOI18N
                    case 'u':
                        underline = false; //NOI18N

                        break;

                    case 'F': //NOI18N
                    case 'f': //NOI18N

                        if (_colorStack.isEmpty()) {
                            g.setColor(defaultColor);
                        } else {
                            g.setColor(_colorStack.pop());
                        }

                        break;

                    default:
                        throwBadHTML("Malformed or unsupported HTML", //NOI18N
                            pos, chars
                        );
                    }
                } else {
                    //Okay, we're in an opening tag.  See which one and configure the Graphics object
                    switch (chars[pos]) {
                    case 'B': //NOI18N
                    case 'b': //NOI18N

                        switch (chars[pos + 1]) {
                        case 'R': //NOI18N
                        case 'r': //NOI18N

                            if (style == STYLE_WORDWRAP) {
                                x = origX;

                                int lineHeight = g.getFontMetrics().getHeight();
                                y += lineHeight;
                                heightPainted += lineHeight;
                                widthPainted = 0;
                            }

                            break;

                        case '>':
                            bold = true;

                            if (italic) {
                                g.setFont(deriveFont(f, Font.BOLD | Font.ITALIC));
                            } else {
                                g.setFont(deriveFont(f, Font.BOLD));
                            }

                            break;
                        }

                        break;

                    case 'e': //NOI18N  //em tag
                    case 'E': //NOI18N
                    case 'I': //NOI18N
                    case 'i': //NOI18N
                        italic = true;

                        if (bold) {
                            g.setFont(deriveFont(f, Font.ITALIC | Font.BOLD));
                        } else {
                            g.setFont(deriveFont(f, Font.ITALIC));
                        }

                        break;

                    case 'S': //NOI18N
                    case 's': //NOI18N

                        switch (chars[pos + 1]) {
                        case '>':
                            strikethrough = true;

                            break;

                        case 'T':
                        case 't':
                            bold = true;

                            if (italic) {
                                g.setFont(deriveFont(f, Font.BOLD | Font.ITALIC));
                            } else {
                                g.setFont(deriveFont(f, Font.BOLD));
                            }

                            break;
                        }

                        break;

                    case 'U': //NOI18N
                    case 'u': //NOI18N
                        underline = true;

                        break;

                    case 'f': //NOI18N
                    case 'F': //NOI18N

                        Color c = findColor(chars, pos, tagEnd);
                        _colorStack.push(g.getColor());

                        if (background != null) {
                            c = HtmlLabelUI.ensureContrastingColor(c, background);
                        }

                        g.setColor(c);

                        break;

                    case 'P': //NOI18N
                    case 'p': //NOI18N

                        if (style == STYLE_WORDWRAP) {
                            x = origX;

                            int lineHeight = g.getFontMetrics().getHeight();
                            y += (lineHeight + (lineHeight / 2));
                            heightPainted = y + lineHeight;
                            widthPainted = 0;
                        }

                        break;

                    case 'H':
                    case 'h': //Just an opening HTML tag

                        if (pos == 1) {
                            break;
                        } else { // fallthrough warning
                            throwBadHTML("Malformed or unsupported HTML", pos, chars); //NOI18N
                            break;
                        }

                    default:
                        throwBadHTML("Malformed or unsupported HTML", pos, chars); //NOI18N
                    }
                }

                pos = tagEnd + (done ? 0 : 1);
                inTag = false;
            } else {
                //Okay, we're not in a tag, we need to paint
                if (lastWasWhitespace) {
                    //Skip multiple whitespace characters
                    while ((pos < (s.length() - 1)) && Character.isWhitespace(chars[pos])) {
                        pos++;
                    }

                    //Check strings terminating with multiple whitespace -
                    //otherwise could get an AIOOBE here
                    if (pos == (chars.length - 1)) {
                        return (style != STYLE_WORDWRAP) ? widthPainted : heightPainted;
                    }
                }

                //Flag to indicate if an ampersand entity was processed,
                //so the resulting & doesn't get treated as the beginning of
                //another entity (and loop endlessly)
                boolean isAmp = false;

                //Flag to indicate the next found < character really should
                //be painted (it came from an entity), it is not the beginning
                //of a tag
                boolean nextLtIsEntity = false;
                int nextTag = chars.length - 1;

                if ((chars[pos] == '&')) { //NOI18N

                    boolean inEntity = pos != (chars.length - 1);

                    if (inEntity) {
                        int newPos = substEntity(chars, pos + 1);
                        inEntity = newPos != -1;

                        if (inEntity) {
                            pos = newPos;
                            isAmp = chars[pos] == '&'; //NOI18N

                            nextLtIsEntity = chars[pos] == '<';
                        } else {
                            nextLtIsEntity = false;
                            isAmp = true;
                        }
                    }
                } else {
                    nextLtIsEntity = false;
                }

                for (int i = pos; i < chars.length; i++) {
                    if ((chars[i] == '<' && !nextLtIsEntity) || (chars[i] == '&' && !isAmp && i != chars.length - 1)) {
                        nextTag = i - 1;

                        break;
                    }

                    //Reset these flags so we don't skip all & or < chars for the rest of the string
                    isAmp = false;
                    nextLtIsEntity = false;
                }

                FontMetrics fm = g.getFontMetrics();

                //Get the bounds of the substring we'll paint
                Rectangle2D r = fm.getStringBounds(chars, pos, nextTag + 1, g);
                if (Utilities.isMac()) {
                    // #54257 - on macosx + chinese/japanese fonts, the getStringBounds() method returns bad value
                    r.setRect(r.getX(), r.getY(), (double)fm.stringWidth(new String(chars, pos, nextTag - pos + 1)), r.getHeight());
                }

                //Store the height, so we can add it if we're in word wrap mode,
                //to return the height painted
                lastHeight = r.getHeight();

                //Work out the length of this tag
                int length = (nextTag + 1) - pos;

                //Flag to be set to true if we run out of space
                boolean goToNextRow = false;

                //Flag that the current line is longer than the available width,
                //and should be wrapped without finding a word boundary
                boolean brutalWrap = false;

                //Work out the per-character avg width of the string, for estimating
                //when we'll be out of space and should start the ... in truncate
                //mode
                double chWidth;

                if (truncated) {
                    //if we're truncating, use the width of one dot from an
                    //ellipsis to get an accurate result for truncation
                    chWidth = dotWidth;
                } else {
                    //calculate an average character width
                    chWidth = r.getWidth() / (nextTag+1 - pos);

                    //can return this sometimes, so handle it
                    if ((chWidth == Double.POSITIVE_INFINITY) || (chWidth == Double.NEGATIVE_INFINITY)) {
                        chWidth = fm.getMaxAdvance();
                    }
                }

                if (
                    ((style != STYLE_CLIP) &&
                        ((style == STYLE_TRUNCATE) && ((widthPainted + r.getWidth()) > (w /*- (chWidth * 3)*/)))) ||
                        /** mkleint - commented out the "- (chWidth *3) because it makes no sense to strip the text and add dots when it fits exactly
                         * into the rendering rectangle.. with this condition we stripped even strings that came close to the limit..
                         **/
                        ((style == STYLE_WORDWRAP) && ((widthPainted + r.getWidth()) > w))
                ) {
                    if (chWidth > 3) {
                        double pixelsOff = (widthPainted + (r.getWidth() + 5)) - w;

                        double estCharsOver = pixelsOff / chWidth;

                        if (style == STYLE_TRUNCATE) {
                            int charsToPaint = Math.round(Math.round(Math.ceil((w - widthPainted) / chWidth)));

                            /*                            System.err.println("estCharsOver = " + estCharsOver);
                                                        System.err.println("Chars to paint " + charsToPaint + " chwidth = " + chWidth + " widthPainted " + widthPainted);
                                                        System.err.println("Width painted + width of tag: " + (widthPainted + r.getWidth()) + " available: " + w);
                             */
                            int startPeriodsPos = (pos + charsToPaint) - 3;

                            if (startPeriodsPos >= chars.length) {
                                startPeriodsPos = chars.length - 4;
                            }

                            length = (startPeriodsPos - pos);

                            if (length < 0) {
                                length = 0;
                            }

                            r = fm.getStringBounds(chars, pos, pos + length, g);
                            if (Utilities.isMac()) {
                                // #54257 - on macosx + chinese/japanese fonts, the getStringBounds() method returns bad value
                                r.setRect(r.getX(), r.getY(), (double)fm.stringWidth(new String(chars, pos, length)), r.getHeight());
                            }

                            //                            System.err.println("Truncated set to true at " + pos + " (" + chars[pos] + ")");
                            truncated = true;
                        } else {
                            //Word wrap mode
                            goToNextRow = true;

                            int lastChar = new Double(nextTag - estCharsOver).intValue();

                            //Unlike Swing's word wrap, which does not wrap on tag boundaries correctly, if we're out of space,
                            //we're out of space
                            brutalWrap = x == 0;

                            for (int i = lastChar; i > pos; i--) {
                                lastChar--;

                                if (Character.isWhitespace(chars[i])) {
                                    length = (lastChar - pos) + 1;
                                    brutalWrap = false;

                                    break;
                                }
                            }

                            if ((lastChar <= pos) && (length > estCharsOver) && !brutalWrap) {
                                x = origX;
                                y += r.getHeight();
                                heightPainted += r.getHeight();

                                boolean boundsChanged = false;

                                while (!done && Character.isWhitespace(chars[pos]) && (pos < nextTag)) {
                                    pos++;
                                    boundsChanged = true;
                                    done = pos == (chars.length - 1);
                                }

                                if (pos == nextTag) {
                                    lastWasWhitespace = true;
                                }

                                if (boundsChanged) {
                                    //recalculate the width we will add
                                    r = fm.getStringBounds(chars, pos, nextTag + 1, g);
                                    if (Utilities.isMac()) {
                                        // #54257 - on macosx + chinese/japanese fonts, the getStringBounds() method returns bad value
                                        r.setRect(r.getX(), r.getY(), (double)fm.stringWidth(new String(chars, pos, nextTag - pos + 1)), r.getHeight());
                                    }
                                }

                                goToNextRow = false;
                                widthPainted = 0;

                                if (chars[pos - 1 + length] == '<') {
                                    length--;
                                }
                            } else if (brutalWrap) {
                                //wrap without checking word boundaries
                                length = (new Double((w - widthPainted) / chWidth)).intValue();

                                if ((pos + length) > nextTag) {
                                    length = (nextTag - pos);
                                }

                                goToNextRow = true;
                            }
                        }
                    }
                }

                if (!done) {
                    if (paint) {
                        g.drawChars(chars, pos, length, x, y);
                    }

                    if (strikethrough || underline) {
                        LineMetrics lm = fm.getLineMetrics(chars, pos, length - 1, g);
                        int lineWidth = new Double(x + r.getWidth()).intValue();

                        if (paint) {
                            if (strikethrough) {
                                int stPos = Math.round(lm.getStrikethroughOffset()) +
                                    g.getFont().getBaselineFor(chars[pos]) + 1;

                                //PENDING - worth supporting with g.setStroke()? A one pixel line is most likely
                                //good enough
                                //int stThick = Math.round (lm.getStrikethroughThickness());
                                g.drawLine(x, y + stPos, lineWidth, y + stPos);
                            }

                            if (underline) {
                                int stPos = Math.round(lm.getUnderlineOffset()) +
                                    g.getFont().getBaselineFor(chars[pos]) + 1;

                                //PENDING - worth supporting with g.setStroke()? A one pixel line is most likely
                                //good enough
                                //int stThick = new Float (lm.getUnderlineThickness()).intValue();
                                g.drawLine(x, y + stPos, lineWidth, y + stPos);
                            }
                        }
                    }

                    if (goToNextRow) {
                        //if we're in word wrap mode and need to go to the next
                        //line, reconfigure the x and y coordinates
                        x = origX;
                        y += r.getHeight();
                        heightPainted += r.getHeight();
                        widthPainted = 0;
                        pos += (length);

                        //skip any leading whitespace
                        while ((pos < chars.length) && (Character.isWhitespace(chars[pos])) && (chars[pos] != '<')) {
                            pos++;
                        }

                        lastWasWhitespace = true;
                        done |= (pos >= chars.length);
                    } else {
                        x += r.getWidth();
                        widthPainted += r.getWidth();
                        lastWasWhitespace = Character.isWhitespace(chars[nextTag]);
                        pos = nextTag + 1;
                    }

                    done |= (nextTag == chars.length);
                }
            }
        }

        if (style != STYLE_WORDWRAP) {
            return widthPainted;
        } else {
            return heightPainted + lastHeight;
        }
    }

    /** Parse a font color tag and return an appopriate java.awt.Color instance */
    private static Color findColor(final char[] ch, final int pos, final int tagEnd) {
        int colorPos = pos;
        boolean useUIManager = false;

        for (int i = pos; i < tagEnd; i++) {
            if (ch[i] == 'c') {
                colorPos = i + 6;

                if ((ch[colorPos] == '\'') || (ch[colorPos] == '"')) {
                    colorPos++;
                }

                //skip the leading # character
                if (ch[colorPos] == '#') {
                    colorPos++;
                } else if (ch[colorPos] == '!') {
                    useUIManager = true;
                    colorPos++;
                }

                break;
            }
        }

        if (colorPos == pos) {
            String out = "Could not find color identifier in font declaration"; //NOI18N
            throwBadHTML(out, pos, ch);
        }

        //Okay, we're now on the first character of the hex color definition
        String s;

        if (useUIManager) {
            int end = ch.length - 1;

            for (int i = colorPos; i < ch.length; i++) {
                if ((ch[i] == '"') || (ch[i] == '\'')) { //NOI18N
                    end = i;

                    break;
                }
            }

            s = new String(ch, colorPos, end - colorPos);
        } else {
            s = new String(ch, colorPos, 6);
        }

        Color result = null;

        if (useUIManager) {
            result = UIManager.getColor(s);

            //Not all look and feels will provide standard colors; handle it gracefully
            if (result == null) {
                throwBadHTML("Could not resolve logical font declared in HTML: " + s, //NOI18N
                    pos, ch
                );
                result = UIManager.getColor("textText"); //NOI18N

                //Avoid NPE in headless situation?
                if (result == null) {
                    result = Color.BLACK;
                }
            }
        } else {
            try {
                int rgb = Integer.parseInt(s, 16);
                result = new Color(rgb);
            } catch (NumberFormatException nfe) {
                throwBadHTML("Illegal hexadecimal color text: " + s + //NOI18N
                    " in HTML string", colorPos, ch
                ); //NOI18N
            }
        }

        if (result == null) {
            throwBadHTML("Unresolvable html color: " + s //NOI18N
                 +" in HTML string \n  ", pos, ch
            ); //NOI18N
        }

        return result;
    }

    /**
     * Workaround for Apple bug 3644261 - after using form editor, all boldface
     * fonts start showing up with incorrect metrics, such that all boldface
     * fonts in the entire IDE are displayed 12px below where they should be.
     * Embarrassing and awful.
     */
    private static final Font deriveFont(Font f, int style) {
        //      return f.deriveFont(style);
        // see #49973 for details.
        Font result = Utilities.isMac() ? new Font(f.getName(), style, f.getSize()) : f.deriveFont(style);

        return result;
    }

    /** Find an entity at the passed character position in the passed array.
     * If an entity is found, the trailing ; character will be substituted
     * with the resulting character, and the position of that character
     * in the array will be returned as the new position to render from,
     * causing the renderer to skip the intervening characters */
    private static final int substEntity(char[] ch, int pos) {
        //There are no 1 character entities, abort
        if (pos >= (ch.length - 2)) {
            return -1;
        }

        //if it's numeric, parse out the number
        if (ch[pos] == '#') { //NOI18N

            return substNumericEntity(ch, pos + 1);
        }

        //Okay, we've potentially got a named character entity. Try to find it.
        boolean match;

        for (int i = 0; i < entities.length; i++) {
            char[] c = (char[]) entities[i];
            match = true;

            if (c.length < (ch.length - pos)) {
                for (int j = 0; j < c.length; j++) {
                    match &= (c[j] == ch[j + pos]);
                }
            } else {
                match = false;
            }

            if (match) {
                //if it's a match, we still need the trailing ;
                if (ch[pos + c.length] == ';') { //NOI18N

                    //substitute the character referenced by the entity
                    ch[pos + c.length] = entitySubstitutions[i];

                    return pos + c.length;
                }
            }
        }

        return -1;
    }

    /** Finds a character defined as a numeric entity (e.g. &amp;#8222;)
     * and replaces the trailing ; with the referenced character, returning
     * the position of it so the renderer can continue from there.
     */
    private static final int substNumericEntity(char[] ch, int pos) {
        for (int i = pos; i < ch.length; i++) {
            if (ch[i] == ';') {
                try {
                    ch[i] = (char) Integer.parseInt(new String(ch, pos, i - pos));

                    return i;
                } catch (NumberFormatException nfe) {
                    throwBadHTML("Unparsable numeric entity: " + //NOI18N
                        new String(ch, pos, i - pos), pos, ch
                    ); //NOI18N
                }
            }
        }

        return -1;
    }

    /** Throw an exception for unsupported or bad html, indicating where the problem is
     * in the message  */
    private static void throwBadHTML(String msg, int pos, char[] chars) {
        char[] chh = new char[pos];
        Arrays.fill(chh, ' '); //NOI18N
        chh[pos - 1] = '^'; //NOI18N

        String out = msg + "\n  " + new String(chars) + "\n  " + new String(chh) + "\n Full HTML string:" +
            new String(chars); //NOI18N

        if (!STRICT_HTML) {
            if (LOG.isLoggable(Level.WARNING)) {
                if (badStrings == null) {
                    badStrings = new HashSet<String>();
                }

                if (!badStrings.contains(msg)) {
                    // bug, issue 38372 - log messages containing
                    //newlines are truncated - so for now we iterate the
                    //string we've just constructed
                    StringTokenizer tk = new StringTokenizer(out, "\n", false);

                    while (tk.hasMoreTokens()) {
                        LOG.warning(tk.nextToken());
                    }

                    badStrings.add(msg.intern());   // NOPMD
                }
            }
        } else {
            throw new IllegalArgumentException(out);
        }
    }

    /**
     * Interface aggregating table, tree, and list cell renderers.
     * @see #createRenderer
     * @see #createLabel
     */
    public interface Renderer extends TableCellRenderer, TreeCellRenderer, ListCellRenderer {
        /** Indicate that the component being rendered has keyboard focus.  NetBeans requires that a different
         * selection color be used depending on whether the view has focus.
         *
         * @param parentFocused Whether or not the focused selection color should be used
         */
        void setParentFocused(boolean parentFocused);

        /**
         * Indicate that the text should be painted centered below the icon.  This is primarily used
         * by org.openide.explorer.view.IconView
         *
         * @param centered Whether or not centered painting should be used.
         */
        void setCentered(boolean centered);

        /**
         * Set a number of pixels the icon and text should be indented.  Used by ChoiceView and ListView to
         * fake tree-style nesting.  This value has no effect if {@link #setCentered setCentered(true)} has been called.
         *
         * @param pixels The number of pixels to indent
         */
        void setIndent(int pixels);

        /**
         * Explicitly tell the renderer it is going to receive HTML markup, or it is not.  If the renderer should
         * check the string for opening HTML tags to determine this, don't call this method.  If you <strong>know</strong>
         * the string will be compliant HTML, it is preferable to call this method with true; if you want to intentionally
         * render HTML markup literally, call this method with false.
         *
         * @param val
         */
        void setHtml(boolean val);

        /**
         * Set the rendering style - this can be JLabel-style truncated-with-elipsis (...) text, or clipped text.
         * The default is {@link #STYLE_CLIP}.
         *
         * @param style The text style
         */
        void setRenderStyle(int style);

        /** Set the icon to be used for painting
         *
         * @param icon An icon or null
         */
        void setIcon(Icon icon);

        /** Clear any stale data from previous use by other components,
         * clearing the icon, text, disabled state and other customizations, returning the component
         * to its initialized state.  This is done automatically when get*CellRenderer() is called,
         * and to the shared instance when {@link #createLabel} is called.<p>
         * Users of the static {@link #createLabel} method may want to call this method if they
         * use the returned instance more than once without again calling {@link #createLabel}.
         */
        void reset();

        /** Set the text to be displayed.  Use this if the object being rendered's toString() does not
         * return a real user-displayable string, after calling get**CellRenderer().  Typically after calling
         * this one calls {@link #setHtml} if the text is known to either be or not be HTML markup.
         *
         * @param txt The text that should be displayed
         */
        void setText(String txt);

        /**
         * Convenience method to set the gap between the icon and text.
         *
         * @param gap an integer number of pixels
         */
        void setIconTextGap(int gap);
    }

}

class HtmlLabelUI extends LabelUI {

    /** System property to automatically turn on antialiasing for html strings */

    private static final boolean antialias = Boolean.getBoolean("nb.cellrenderer.antialiasing") // NOI18N
         ||Boolean.getBoolean("swing.aatext") // NOI18N
         ||(isGTK() && gtkShouldAntialias()) // NOI18N
         || isAqua();

    private static HtmlLabelUI uiInstance;

    private static int FIXED_HEIGHT;

    static {
        //Jesse mode
        String ht = System.getProperty("nb.cellrenderer.fixedheight"); //NOI18N

        if (ht != null) {
            try {
                FIXED_HEIGHT = Integer.parseInt(ht);
            } catch (Exception e) {
                //do nothing
            }
        }
    }

    private static Map<Object,Object> hintsMap;
    private static Color unfocusedSelBg;
    private static Color unfocusedSelFg;
    private static Boolean gtkAA;

    public static ComponentUI createUI(JComponent c) {
        assert c instanceof HtmlRendererImpl;

        if (uiInstance == null) {
            uiInstance = new HtmlLabelUI();
        }

        return uiInstance;
    }

    public @Override Dimension getPreferredSize(JComponent c) {
        return calcPreferredSize((HtmlRendererImpl) c);
    }

    /** Get the width of the text */
    private static int textWidth(String text, Graphics g, Font f, boolean html) {
        if (text != null) {
            if (html) {
                return Math.round(
                    Math.round(
                        Math.ceil(
                            HtmlRenderer.renderHTML(
                                text, g, 0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE, f, Color.BLACK,
                                HtmlRenderer.STYLE_CLIP, false
                            )
                        )
                    )
                );
            } else {
                return Math.round(
                    Math.round(
                        Math.ceil(
                            HtmlRenderer.renderPlainString(
                                text, g, 0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE, f, Color.BLACK,
                                HtmlRenderer.STYLE_CLIP, false
                            )
                        )
                    )
                );
            }
        } else {
            return 0;
        }
    }

    private Dimension calcPreferredSize(HtmlRendererImpl r) {
        Insets ins = r.getInsets();
        Dimension prefSize = new java.awt.Dimension(ins.left + ins.right, ins.top + ins.bottom);
        String text = r.getText();

        Graphics g = r.getGraphics();
        Icon icon = r.getIcon();

        if (text != null) {
            FontMetrics fm = g.getFontMetrics(r.getFont());
            prefSize.height += (fm.getMaxAscent() + fm.getMaxDescent());
        }

        if (icon != null) {
            if (r.isCentered()) {
                prefSize.height += (icon.getIconHeight() + r.getIconTextGap());
                prefSize.width += icon.getIconWidth();
            } else {
                prefSize.height = Math.max(icon.getIconHeight() + ins.top + ins.bottom, prefSize.height);
                prefSize.width += (icon.getIconWidth() + r.getIconTextGap());
            }
        }

        //Antialiasing affects the text metrics, so use it if needed when
        //calculating preferred size or the result here will be narrower
        //than the space actually needed
        ((Graphics2D) g).addRenderingHints(getHints());

        int textwidth = textWidth(text, g, r.getFont(), r.isHtml()) + 4;

        if (r.isCentered()) {
            prefSize.width = Math.max(prefSize.width, textwidth + ins.right + ins.left);
        } else {
            prefSize.width += (textwidth + r.getIndent());
        }

        if (FIXED_HEIGHT > 0) {
            prefSize.height = FIXED_HEIGHT;
        }

        return prefSize;
    }

    @SuppressWarnings("unchecked")
    static final Map<?,?> getHints() {
        //XXX We REALLY need to put this in a graphics utils lib
        if (hintsMap == null) {
            //Thanks to Phil Race for making this possible
            hintsMap = (Map)(Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints")); //NOI18N
            if (hintsMap == null) {
                hintsMap = new HashMap<Object,Object>();
                if (antialias) {
                    hintsMap.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                }
            }
        }
        Map<?,?> ret = hintsMap;
        assert ret != null; // does this method need to be synchronized?
        return ret;
    }

    public @Override void update(Graphics g, JComponent c) {
        Color bg = getBackgroundFor((HtmlRendererImpl) c);
        HtmlRendererImpl h = (HtmlRendererImpl) c;

        if (bg != null) {
            int x = h.isSelected() ? ((h.getIcon() == null) ? 0 : (h.getIcon().getIconWidth() + h.getIconTextGap())) : 0;
            x += h.getIndent();
            g.setColor(bg);
            g.fillRect(x, 0, c.getWidth() - x, c.getHeight());
        }

        if (h.isLeadSelection()) {
            Color focus = UIManager.getColor("Tree.selectionBorderColor"); // NOI18N

            if ((focus == null) || focus.equals(bg)) {
                focus = Color.BLUE;
            }

            if (!isGTK() && !isAqua() && !isNimbus()) {
                int x = ((h.getIcon() == null) ? 0 : (h.getIcon().getIconWidth() + h.getIconTextGap()));
                g.setColor(focus);
                g.drawRect(x, 0, c.getWidth() - (x + 1), c.getHeight() - 1);
            }
        }

        paint(g, c);
    }

    public @Override void paint(Graphics g, JComponent c) {

        ((Graphics2D) g).addRenderingHints(getHints());

        HtmlRendererImpl r = (HtmlRendererImpl) c;

        if (r.isCentered()) {
            paintIconAndTextCentered(g, r);
        } else {
            paintIconAndText(g, r);
        }
    }

    /** Actually paint the icon and text using our own html rendering engine. */
    private void paintIconAndText(Graphics g, HtmlRendererImpl r) {
        Font f = r.getFont();
        g.setFont(f);

        FontMetrics fm = g.getFontMetrics();

        //Find out what height we need
        int txtH = fm.getMaxAscent() + fm.getMaxDescent();
        Insets ins = r.getInsets();

        //find out the available height less the insets
        int rHeight = r.getHeight();
        int availH = rHeight - (ins.top + ins.bottom);

        int txtY;

        if (availH >= txtH) {
            //Center the text if we have space
            txtY = (txtH + ins.top + ((availH / 2) - (txtH / 2))) - fm.getMaxDescent();
        } else if (r.getHeight() > txtH) {
            txtY = txtH + (rHeight - txtH) / 2 - fm.getMaxDescent();
        } else {
            //Okay, it's not going to fit, punt.
            txtY = fm.getMaxAscent();
        }

        int txtX = r.getIndent();

        Icon icon = r.getIcon();

        //Check the icon non-null and height (see TabData.NO_ICON for why)
        if ((icon != null) && (icon.getIconWidth() > 0) && (icon.getIconHeight() > 0)) {
            int iconY;

            if (availH > icon.getIconHeight()) {
                //add 2 to make sure icon top pixels are not cut off by outline
                iconY = ins.top + ((availH / 2) - (icon.getIconHeight() / 2)); // + 2;
            } else if (availH == icon.getIconHeight()) {
                //They're an exact match, make it 0
                iconY = 0;
            } else {
                //Won't fit; make the top visible and cut the rest off (option:
                //center it and clip it on top and bottom - probably even harder
                //to recognize that way, though)
                iconY = ins.top;
            }

            //add in the insets
            int iconX = ins.left + r.getIndent() + 1; //+1 to get it out of the way of the focus border

            try {
                //Diagnostic - the CPP module currently is constructing
                //some ImageIcon from a null image in Options.  So, catch it and at
                //least give a meaningful message that indicates what node
                //is the culprit
                icon.paintIcon(r, g, iconX, iconY);
            } catch (NullPointerException npe) {
                Exceptions.attachMessage(npe,
                                         "Probably an ImageIcon with a null source image: " +
                                         icon + " - " + r.getText()); //NOI18N
                Exceptions.printStackTrace(npe);
            }

            txtX = iconX + icon.getIconWidth() + r.getIconTextGap();
        } else {
            //If there's no icon, paint the text where the icon would start
            txtX += ins.left;
        }

        String text = r.getText();

        if (text == null) {
            //No text, we're done
            return;
        }

        //Get the available horizontal pixels for text
        int txtW = (icon != null)
            ? (r.getWidth() - (ins.left + ins.right + icon.getIconWidth() + r.getIconTextGap() + r.getIndent()))
            : (r.getWidth() - (ins.left + ins.right + r.getIndent()));

        Color background = getBackgroundFor(r);
        Color foreground = ensureContrastingColor(getForegroundFor(r), background);

        if (r.isHtml()) {
            HtmlRenderer._renderHTML(text, 0, g, txtX, txtY, txtW, txtH, f, foreground, r.getRenderStyle(), true, background);
        } else {
            HtmlRenderer.renderPlainString(text, g, txtX, txtY, txtW, txtH, f, foreground, r.getRenderStyle(), true);
        }
    }

    private void paintIconAndTextCentered(Graphics g, HtmlRendererImpl r) {
        Insets ins = r.getInsets();
        Icon ic = r.getIcon();
        int w = r.getWidth() - (ins.left + ins.right);
        int txtX = ins.left;
        int txtY = 0;

        if ((ic != null) && (ic.getIconWidth() > 0) && (ic.getIconHeight() > 0)) {
            int iconx = (w > ic.getIconWidth()) ? ((w / 2) - (ic.getIconWidth() / 2)) : txtX;
            int icony = 0;
            ic.paintIcon(r, g, iconx, icony);
            txtY += (ic.getIconHeight() + r.getIconTextGap());
        }

        int txtW = r.getPreferredSize().width;
        txtX = (txtW < r.getWidth()) ? ((r.getWidth() / 2) - (txtW / 2)) : 0;

        int txtH = r.getHeight() - txtY;

        Font f = r.getFont();
        g.setFont(f);

        FontMetrics fm = g.getFontMetrics(f);
        txtY += fm.getMaxAscent();

        Color background = getBackgroundFor(r);
        Color foreground = ensureContrastingColor(getForegroundFor(r), background);

        if (r.isHtml()) {
            HtmlRenderer._renderHTML(
                r.getText(), 0, g, txtX, txtY, txtW, txtH, f, foreground, r.getRenderStyle(), true, background
            );
        } else {
            HtmlRenderer.renderString(
                r.getText(), g, txtX, txtY, txtW, txtH, r.getFont(), foreground, r.getRenderStyle(), true
            );
        }
    }

    /*
    (int pos, String s, Graphics g, int x,
    int y, int w, int h, Font f, Color defaultColor, int style,
    boolean paint, Color background) {  */
    static Color ensureContrastingColor(Color fg, Color bg) {
        if (bg == null) {
            if (isNimbus()) {
                bg = Color.WHITE;
            } else {
                bg = UIManager.getColor("text"); //NOI18N

                if (bg == null) {
                    bg = Color.WHITE;
                }
            }
        }
        if (fg == null) {
            if (isNimbus()) {
                fg = Color.BLACK;
            } else {
                fg = UIManager.getColor("textText");
                if (fg == null) {
                    fg = Color.BLACK;
                }
            }
        }

        if (Color.BLACK.equals(fg) && Color.WHITE.equals(fg)) {
            return fg;
        }

        boolean replace = fg.equals(bg);
        int dif = 0;

        if (!replace) {
            dif = difference(fg, bg);
            replace = dif < 80;
        }

        if (replace) {
            int lum = luminance(bg);
            boolean darker = lum >= 128;

            if (darker) {
                fg = Color.BLACK;
            } else {
                fg = Color.WHITE;
            }
        }

        return fg;
    }

    private static int difference(Color a, Color b) {
        return Math.abs(luminance(a) - luminance(b));
    }

    private static int luminance(Color c) {
        return (299*c.getRed() + 587*c.getGreen() + 114*c.getBlue()) / 1000;
    }

    static Color getBackgroundFor(HtmlRendererImpl r) {
        if (r.isOpaque()) {
            return r.getBackground();
        }

        if (r.isSelected() && !r.isParentFocused() && !isGTK() && !isNimbus()) {
            return getUnfocusedSelectionBackground();
        }

        Color result = null;

        if (r.isSelected()) {
            switch (r.getType()) {
            case LIST:
                result = UIManager.getColor("List.selectionBackground"); //NOI18N

                if (result == null) { //GTK

                    //plaf library guarantees this one:
                    result = UIManager.getColor("Tree.selectionBackground"); //NOI18N
                }

                //System.err.println("  now " + result);
                break;

            case TABLE:
                result = UIManager.getColor("Table.selectionBackground"); //NOI18N

                break;

            case TREE:
                return UIManager.getColor("Tree.selectionBackground"); //NOI18N
            }

            return (result == null) ? r.getBackground() : result;
        }

        return null;
    }

    static Color getForegroundFor(HtmlRendererImpl r) {
        if (r.isSelected() && !r.isParentFocused() && !isGTK() && !isNimbus()) {
            return getUnfocusedSelectionForeground();
        }

        if (!r.isEnabled()) {
            return UIManager.getColor("textInactiveText"); //NOI18N
        }

        Color result = null;

        if (r.isSelected()) {
            switch (r.getType()) {
            case LIST:
                result = UIManager.getColor("List.selectionForeground"); //NOI18N
                break;
            case TABLE:
                result = UIManager.getColor("Table.selectionForeground"); //NOI18N
                break;
            case TREE:
                result = UIManager.getColor("Tree.selectionForeground"); //NOI18N
            }
        }

        return (result == null) ? r.getForeground() : result;
    }

    static boolean isAqua () {
        return "Aqua".equals(UIManager.getLookAndFeel().getID());
    }

    static boolean isGTK () {
        return "GTK".equals(UIManager.getLookAndFeel().getID());
    }

    static boolean isNimbus () {
        return "Nimbus".equals(UIManager.getLookAndFeel().getID());
    }

    /** Get the system-wide unfocused selection background color */
    private static Color getUnfocusedSelectionBackground() {
        if (unfocusedSelBg == null) {
            //allow theme/ui custom definition
            unfocusedSelBg = UIManager.getColor("nb.explorer.unfocusedSelBg"); //NOI18N

            if (unfocusedSelBg == null) {
                //try to get standard shadow color
                unfocusedSelBg = UIManager.getColor("controlShadow"); //NOI18N

                if (unfocusedSelBg == null) {
                    //Okay, the look and feel doesn't suport it, punt
                    unfocusedSelBg = Color.lightGray;
                }

                //Lighten it a bit because disabled text will use controlShadow/
                //gray
                if (!Color.WHITE.equals(unfocusedSelBg.brighter())) {
                    unfocusedSelBg = unfocusedSelBg.brighter();
                }
            }
        }

        return unfocusedSelBg;
    }

    /** Get the system-wide unfocused selection foreground color */
    private static Color getUnfocusedSelectionForeground() {
        if (unfocusedSelFg == null) {
            //allow theme/ui custom definition
            unfocusedSelFg = UIManager.getColor("nb.explorer.unfocusedSelFg"); //NOI18N

            if (unfocusedSelFg == null) {
                //try to get standard shadow color
                unfocusedSelFg = UIManager.getColor("textText"); //NOI18N

                if (unfocusedSelFg == null) {
                    //Okay, the look and feel doesn't suport it, punt
                    unfocusedSelFg = Color.BLACK;
                }
            }
        }

        return unfocusedSelFg;
    }

    public static final boolean gtkShouldAntialias() {
        if (gtkAA == null) {
            Object o = Toolkit.getDefaultToolkit().getDesktopProperty("gnome.Xft/Antialias"); //NOI18N
            gtkAA = new Integer(1).equals(o) ? Boolean.TRUE : Boolean.FALSE;
        }

        return gtkAA.booleanValue();
    }
}