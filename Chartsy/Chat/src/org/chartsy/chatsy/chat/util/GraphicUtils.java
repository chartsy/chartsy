package org.chartsy.chatsy.chat.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Label;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;

import org.chartsy.chatsy.chat.util.log.Log;

public final class GraphicUtils
{
	
    private static final Insets HIGHLIGHT_INSETS = new Insets(1, 1, 1, 1);
    public static final Color SELECTION_COLOR = new java.awt.Color(166, 202, 240);
    public static final Color TOOLTIP_COLOR = new java.awt.Color(166, 202, 240);

    protected final static Component component = new Component() {};
    protected final static MediaTracker tracker = new MediaTracker(component);

    private static Map<String,Image> imageCache = new HashMap<String,Image>();

    public static final Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);
    public static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);

    private GraphicUtils()
	{
    }

    public static void centerWindowOnScreen(Window window)
	{
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final Dimension size = window.getSize();

        if (size.height > screenSize.height) 
            size.height = screenSize.height;
        if (size.width > screenSize.width) 
            size.width = screenSize.width;

        window.setLocation((screenSize.width - size.width) / 2,
            (screenSize.height - size.height) / 2);
    }

    public static void drawHighlightBorder(Graphics g, int x, int y,
		int width, int height, boolean raised,
		Color shadow, Color highlight)
	{
        final Color oldColor = g.getColor();
        g.translate(x, y);

        g.setColor(raised ? highlight : shadow);
        g.drawLine(0, 0, width - 2, 0);
        g.drawLine(0, 1, 0, height - 2);

        g.setColor(raised ? shadow : highlight);
        g.drawLine(width - 1, 0, width - 1, height - 1);
        g.drawLine(0, height - 1, width - 2, height - 1);

        g.translate(-x, -y);
        g.setColor(oldColor);
    }

    public static Insets getHighlightBorderInsets()
	{
        return HIGHLIGHT_INSETS;
    }

    public static ImageIcon createImageIcon(Image image)
	{
        if (image == null)
            return null;

        synchronized (tracker)
		{
            tracker.addImage(image, 0);
            try
			{
                tracker.waitForID(0, 0);
            }
            catch (InterruptedException e)
			{
                System.out.println("INTERRUPTED while loading Image");
            }
            tracker.removeImage(image, 0);
        }

        return new ImageIcon(image);
    }

    public static Point getPopupMenuShowPoint(JPopupMenu popup, MouseEvent event)
	{
        Component source = (Component)event.getSource();
        Point topLeftSource = source.getLocationOnScreen();
        Point ptRet = getPopupMenuShowPoint(popup,
            topLeftSource.x + event.getX(),
            topLeftSource.y + event.getY());
        ptRet.translate(-topLeftSource.x, -topLeftSource.y);
        return ptRet;
    }

    public static Point getPopupMenuShowPoint(JPopupMenu popup, int x, int y)
	{
        Dimension sizeMenu = popup.getPreferredSize();
        Point bottomRightMenu = new Point(x + sizeMenu.width, y + sizeMenu.height);

        Rectangle[] screensBounds = getScreenBounds();
        int n = screensBounds.length;
        for (int i = 0; i < n; i++)
		{
            Rectangle screenBounds = screensBounds[i];
            if (screenBounds.x <= x && x <= (screenBounds.x + screenBounds.width))
			{
                Dimension sizeScreen = screenBounds.getSize();
                sizeScreen.height -= 32;

                int xOffset = 0;
                if (bottomRightMenu.x > (screenBounds.x + sizeScreen.width))
                    xOffset = -sizeMenu.width;

                int yOffset = 0;
                if (bottomRightMenu.y > (screenBounds.y + sizeScreen.height))
                    yOffset = sizeScreen.height - bottomRightMenu.y;

                return new Point(x + xOffset, y + yOffset);
            }
        }

        return new Point(x, y);
    }

    public static void centerWindowOnComponent(Window window, Component over)
	{
        if ((over == null) || !over.isShowing())
		{
            centerWindowOnScreen(window);
            return;
        }

        Point parentLocation = over.getLocationOnScreen();
        Dimension parentSize = over.getSize();
        Dimension size = window.getSize();

        int x = parentLocation.x + (parentSize.width - size.width) / 2;
        int y = parentLocation.y + (parentSize.height - size.height) / 2;

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if (x + size.width > screenSize.width)
            x = screenSize.width - size.width;
        if (x < 0)
            x = 0;
        if (y + size.height > screenSize.height)
            y = screenSize.height - size.height;
        if (y < 0)
            y = 0;

        window.setLocation(x, y);
    }

    public static boolean isAncestorOfFocusedComponent(Component c)
	{
        if (c.hasFocus())
		{
            return true;
        }
        else
		{
            if (c instanceof Container)
			{
                Container cont = (Container)c;
                int n = cont.getComponentCount();
                for (int i = 0; i < n; i++)
				{
                    Component child = cont.getComponent(i);
                    if (isAncestorOfFocusedComponent(child))
                        return true;
                }
            }
        }
        return false;
    }

    public static Component getFocusableComponentOrChild(Component c)
	{
        return getFocusableComponentOrChild(c, false);
    }

    public static Component getFocusableComponentOrChild(Component c, boolean deepest)
	{
        if (c != null && c.isEnabled() && c.isVisible())
		{
            if (c instanceof Container)
			{
                Container cont = (Container)c;
                if (!deepest)
				{
                    if (c instanceof JComponent)
					{
                        JComponent jc = (JComponent)c;
                        if (jc.isRequestFocusEnabled())
                            return jc;
                    }
                }

                int n = cont.getComponentCount();
                for (int i = 0; i < n; i++)
				{
                    Component child = cont.getComponent(i);
                    Component focused = getFocusableComponentOrChild(child, deepest);
                    if (focused != null)
                        return focused;
                }

                if (c instanceof JComponent)
				{
                    if (deepest)
					{
                        JComponent jc = (JComponent)c;
                        if (jc.isRequestFocusEnabled())
                            return jc;
                    }
                }
                else
				{
                    return c;
                }
            }
        }

        return null;
    }

    public static Component focusComponentOrChild(Component c)
	{
        return focusComponentOrChild(c, false);
    }

    public static Component focusComponentOrChild(Component c, boolean deepest)
	{
        final Component focusable = getFocusableComponentOrChild(c, deepest);
        if (focusable != null)
            focusable.requestFocus();
        return focusable;
    }

    public static Image loadFromResource(String imageName, Class<?> cls)
	{
        try
		{
            final URL url = cls.getResource(imageName);
            if (url == null)
                return null;

            Image image = imageCache.get(url.toString());
            if (image == null)
			{
                image = Toolkit.getDefaultToolkit().createImage(url);
                imageCache.put(url.toString(), image);
            }

            return image;
        }
        catch (Exception e)
		{
            Log.error(e);
        }

        return null;
    }

    public static Rectangle[] getScreenBounds()
	{
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice[] screenDevices = graphicsEnvironment.getScreenDevices();
        Rectangle[] screenBounds = new Rectangle[screenDevices.length];
        for (int i = 0; i < screenDevices.length; i++)
		{
            GraphicsDevice screenDevice = screenDevices[i];
            final GraphicsConfiguration defaultConfiguration = screenDevice.getDefaultConfiguration();
            screenBounds[i] = defaultConfiguration.getBounds();
        }

        return screenBounds;
    }


    public static void makeSameSize(JComponent... comps)
	{
        if (comps.length == 0)
            return;

        int max = 0;
        for (JComponent comp1 : comps)
		{
            int w = comp1.getPreferredSize().width;
            max = w > max ? w : max;
        }

        Dimension dim = new Dimension(max, comps[0].getPreferredSize().height);
        for (JComponent comp : comps)
            comp.setPreferredSize(dim);
    }

    public static String toHTMLColor(Color c)
	{
        int color = c.getRGB();
        color |= 0xff000000;
        String s = Integer.toHexString(color);
        return s.substring(2);
    }

    public static String createToolTip(String text, int width)
	{
        final String htmlColor = toHTMLColor(TOOLTIP_COLOR);
        return "<html><table width=" + width + " bgColor="
			+ htmlColor + "><tr><td>" + text + "</td></tr></table></table>";
    }

    public static String createToolTip(String text)
	{
        final String htmlColor = toHTMLColor(TOOLTIP_COLOR);
        return "<html><table  bgColor=" + htmlColor + "><tr><td>"
			+ text + "</td></tr></table></table>";
    }

    public static String getHighlightedWords(String text, String query)
	{
        final StringTokenizer tkn = new StringTokenizer(query, " ");
        int tokenCount = tkn.countTokens();
        String[] words = new String[tokenCount];
        for (int j = 0; j < tokenCount; j++)
		{
            String queryWord = tkn.nextToken();
            words[j] = queryWord;
        }

        String highlightedWords;
        try
		{
            highlightedWords = StringUtils.highlightWords(text, words, "<font style=background-color:yellow;font-weight:bold;>", "</font>");
        }
        catch (Exception e)
		{
            highlightedWords = text;
        }

        return highlightedWords;
    }

    public static ImageIcon createShadowPicture(Image buf)
	{
        buf = removeTransparency(buf);

        BufferedImage splash;

        JLabel label = new JLabel();
        int width = buf.getWidth(null);
        int height = buf.getHeight(null);
        int extra = 4;

        splash = new BufferedImage(width + extra, height + extra, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D)splash.getGraphics();


        BufferedImage shadow = new BufferedImage(width + extra, height + extra, BufferedImage.TYPE_INT_ARGB);
        Graphics g = shadow.getGraphics();
        g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.3f));
        g.fillRoundRect(0, 0, width, height, 12, 12);

        g2.drawImage(shadow, getBlurOp(7), 0, 0);
        g2.drawImage(buf, 0, 0, label);
        return new ImageIcon(splash);
    }

    public static BufferedImage removeTransparency(Image image)
	{
        int w = image.getWidth(null);
        int h = image.getHeight(null);
        BufferedImage bi2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi2.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bi2;
    }

    public static Image toImage(BufferedImage bufferedImage)
	{
        return Toolkit.getDefaultToolkit().createImage(bufferedImage.getSource());
    }

    private static ConvolveOp getBlurOp(int size)
	{
        float[] data = new float[size * size];
        float value = 1 / (float)(size * size);
        for (int i = 0; i < data.length; i++) {
            data[i] = value;
        }
        return new ConvolveOp(new Kernel(size, size, data));
    }

    public static BufferedImage convert(Image im) throws InterruptedException, IOException
	{
        load(im);
        BufferedImage bi = new BufferedImage(im.getWidth(null), im.getHeight(null), BufferedImage.TYPE_INT_ARGB_PRE);        
        Graphics bg = bi.getGraphics();
        bg.drawImage(im, 0, 0, null);
        bg.dispose();
        return bi;
    }

    public static void load(Image image) throws InterruptedException, IOException
	{
        MediaTracker tracker = new MediaTracker(new Label());
        tracker.addImage(image, 0);
        tracker.waitForID(0);
        if (tracker.isErrorID(0))
            throw new IOException("error loading image");
    }

    public static byte[] getBytesFromImage(Image image)
	{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
		{
            ImageIO.write(convert(image), "PNG", baos);
        }
        catch (IOException e)
		{
            Log.error(e);
        }
        catch (InterruptedException e)
		{
            Log.error(e);
        }

        return baos.toByteArray();
    }

    public static ImageIcon scaleImageIcon(ImageIcon icon, int newHeight, int newWidth)
	{
        Image img = icon.getImage();
        int height = icon.getIconHeight();
        int width = icon.getIconWidth();

        if (height > newHeight)
            height = newHeight;
        if (width > newWidth)
            width = newWidth;
		
        img = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    public static ImageIcon scale(ImageIcon icon, int newHeight, int newWidth)
	{
        Image img = icon.getImage();
        img = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    public static Icon getIcon(File file)
	{
        try
		{
            sun.awt.shell.ShellFolder sf = sun.awt.shell.ShellFolder.getShellFolder(file);
            return new ImageIcon(sf.getIcon(true), sf.getFolderType());
        }
        catch (Exception e)
		{
            try
			{
                return new JFileChooser().getIcon(file);
            }
            catch (Exception e1)
			{
            }
        }
        return null;
    }
	
}
