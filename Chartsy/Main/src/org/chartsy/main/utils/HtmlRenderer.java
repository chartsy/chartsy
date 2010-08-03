package org.chartsy.main.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.LineMetrics;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.openide.util.Utilities;

/**
 *
 * @author Viorel
 */
public final class HtmlRenderer
{
	
    private static HtmlRendererImpl LABEL = null;
    private static Stack<Color> colorStack = new Stack<Color>();

    public static final int STYLE_CLIP = 0;
    public static final int STYLE_TRUNCATE = 1;
    private static final int STYLE_WORDWRAP = 2;

    private static final boolean STRICT_HTML = Boolean.getBoolean("netbeans.lwhtml.strict"); //NOI18N
    private static Set<String> badStrings = null;

    private static final Logger LOG = Logger.getLogger(HtmlRenderer.class.getName());

    private static final Object[] entities = new Object[]
	{
            new char[] { 'g', 't' },
			new char[] { 'l', 't' },
            new char[] { 'q', 'u', 'o', 't' },
			new char[] { 'a', 'm', 'p' },
            new char[] { 'l', 's', 'q', 'u', 'o' },
            new char[] { 'r', 's', 'q', 'u', 'o' },
            new char[] { 'l', 'd', 'q', 'u', 'o' },
            new char[] { 'r', 'd', 'q', 'u', 'o' },
            new char[] { 'n', 'd', 'a', 's', 'h' },
            new char[] { 'm', 'd', 'a', 's', 'h' },
            new char[] { 'n', 'e' },
            new char[] { 'l', 'e' },
            new char[] { 'g', 'e' },
            new char[] { 'c', 'o', 'p', 'y' },
            new char[] { 'r', 'e', 'g' },
            new char[] { 't', 'r', 'a', 'd', 'e' },
            new char[] { 'n', 'b', 's', 'p'}
        };

    private static final char[] entitySubstitutions = new char[] 
	{'>', '<', '"', '&', 8216, 8217, 8220, 8221, 8211, 8212, 8800, 8804, 8805, 169, 174, 8482, ' '};
    
	private HtmlRenderer()
	{}

    public static final Renderer createRenderer()
	{
        return new HtmlRendererImpl();
    }

    public static final JLabel createLabel()
	{
        return new HtmlRendererImpl();
    }

    public static double renderPlainString
		(String s, Graphics g, int x, int y, int w, int h, Font f,
		Color defaultColor, int style, boolean paint)
	{
        if ((style < 0) || (style > 1))
            throw new IllegalArgumentException("Unknown rendering mode: " + style);

        return _renderPlainString(s, g, x, y, w, h, f, defaultColor, style, paint);
    }

    private static double _renderPlainString
		(String s, Graphics g, int x, int y, int w, int h, Font f,
		Color foreground, int style, boolean paint)
	{
        if (f == null)
		{
            f = UIManager.getFont("controlFont");
            if (f == null)
			{
                int fs = 11;
                Object cfs = UIManager.get("customFontSize");

                if (cfs instanceof Integer)
                    fs = ((Integer) cfs).intValue();

                f = new Font("Dialog", Font.PLAIN, fs);
            }
        }

        FontMetrics fm = g.getFontMetrics(f);
        int wid;
        if (Utilities.isMac())
            wid = fm.stringWidth(s);
		else
            wid = (int)fm.getStringBounds(s, g).getWidth();

        if (paint)
		{
            g.setColor(foreground);
            g.setFont(f);

            if ((wid <= w) || (style == STYLE_CLIP))
			{
                g.drawString(s, x, y);
            } 
			else
			{
                char[] chars = s.toCharArray();

                if (chars.length == 0)
                    return 0;

                double chWidth = wid / chars.length;
                int estCharsToPaint = new Double(w / chWidth).intValue();
                if( estCharsToPaint > chars.length )
                    estCharsToPaint = chars.length;

                while( estCharsToPaint > 3 )
				{
                    if( estCharsToPaint < chars.length )
                        Arrays.fill(chars, estCharsToPaint - 3, estCharsToPaint, '.');
                    int  newWidth;
                    if (Utilities.isMac()) 
                        newWidth = fm.stringWidth(new String(chars, 0, estCharsToPaint));
                    else
                        newWidth = (int)fm.getStringBounds(chars, 0, estCharsToPaint, g).getWidth();

                    if( newWidth <= w )
                        break;

                    estCharsToPaint--;
                }

                if (style == STYLE_TRUNCATE)
				{
                    int length = estCharsToPaint;
                    if (length <= 0)
                        return 0;

                    if (paint)
					{
                        if (length > 3)
                            g.drawChars(chars, 0, length, x, y);
                        else
						{
                            Shape shape = g.getClip();
                            if (shape != null)
							{
                                if (s != null)
								{
                                    Area area = new Area(shape);
                                    area.intersect(new Area(new Rectangle(x, y, w, h)));
                                    g.setClip(area);
                                } 
								else
								{
                                    g.setClip(new Rectangle(x, y, w, h));
                                }
                            }

                            g.drawString("...", x, y);
                            if (shape != null)
                                g.setClip(shape);
                        }
                    }
                }
            }
        }

        return wid;
    }
	
    public static double renderString
		(String s, Graphics g, int x, int y, int w, int h, Font f,
		Color defaultColor, int style, boolean paint)
	{
        switch (style)
		{
			case STYLE_CLIP:
			case STYLE_TRUNCATE:
				break;
			default:
				throw new IllegalArgumentException("Unknown rendering mode: " + style);
        }

        if (s.startsWith("<html") || s.startsWith("<HTML")) 
            return _renderHTML(s, 6, g, x, y, w, h, f, defaultColor, style, paint, null);
        else
            return renderPlainString(s, g, x, y, w, h, f, defaultColor, style, paint);
    }
	
    public static double renderHTML
		(String s, Graphics g, int x, int y, int w, int h, Font f,
		Color defaultColor, int style, boolean paint)
	{
        if ((style < 0) || (style > 1)) 
            throw new IllegalArgumentException("Unknown rendering mode: " + style);

        return _renderHTML(s, 0, g, x, y, w, h, f, defaultColor, style, paint, null);
    }

    static double _renderHTML
		(String s, int pos, Graphics g, int x, int y, int w, int h, Font f,
		Color defaultColor, int style, boolean paint, Color background)
	{
        if (f == null)
		{
            f = UIManager.getFont("controlFont");
            if (f == null)
			{
                int fs = 11;
                Object cfs = UIManager.get("customFontSize");

                if (cfs instanceof Integer)
                    fs = ((Integer) cfs).intValue();

                f = new Font("Dialog", Font.PLAIN, fs);
            }
        }

        Stack<Color> _colorStack = SwingUtilities.isEventDispatchThread() 
			? HtmlRenderer.colorStack
			: new Stack<Color>();

        g.setColor(defaultColor);
        g.setFont(f);

        char[] chars = s.toCharArray();
        int origX = x;
        boolean done = false;
        boolean inTag = false;
        boolean inClosingTag = false;
        boolean strikethrough = false;
        boolean underline = false;
        boolean bold = false;
        boolean italic = false;
        boolean truncated = false;
        double widthPainted = 0;
        double heightPainted = 0;
        boolean lastWasWhitespace = false;
        double lastHeight = 0;

        double dotWidth = 0;

        if (style == STYLE_TRUNCATE) 
            dotWidth = g.getFontMetrics().charWidth('.');
		
        _colorStack.clear();

        while (!done)
		{
            if (pos == s.length())
                return widthPainted;

            try
			{
                inTag |= (chars[pos] == '<');
            } 
			catch (ArrayIndexOutOfBoundsException e)
			{
                ArrayIndexOutOfBoundsException aib 
					= new ArrayIndexOutOfBoundsException
					("HTML rendering failed at position " + pos
					+ " in String \"" + s + "\".  Please report this "
					+ "at http://www.netbeans.org");

                if (STRICT_HTML) 
                    throw aib;
                else
				{
                    Logger.getLogger(HtmlRenderer.class.getName()).log(Level.WARNING, null, aib);
                    return renderPlainString(s, g, x, y, w, h, f, defaultColor, style, paint);
                }
            }

            inClosingTag = inTag 
				&& ((pos + 1) < chars.length)
				&& (chars[pos + 1] == '/');

            if (truncated)
			{
                g.setColor(defaultColor);
                g.setFont(f);

                if (paint)
                    g.drawString("...", x, y);
                done = true;
            } 
			else if (inTag)
			{
                pos++;
                int tagEnd = pos;
                done = tagEnd >= (chars.length - 1);

				while (!done
					&& (chars[tagEnd] != '>'))
				{
                    done = tagEnd == (chars.length - 1);
                    tagEnd++;
                }

                if (done)
				{
                    throwBadHTML("Matching '>' not found", pos, chars);
                    break;
                }

                if (inClosingTag)
				{
                    pos++;
                    switch (chars[pos])
					{
						case 'P':
						case 'p':
						case 'H':
						case 'h':
							break;
							
						case 'B':
						case 'b':
							if ((chars[pos + 1] == 'r')
								|| (chars[pos + 1] == 'R'))
								break;

							if (!bold)
								throwBadHTML("Closing bold tag w/o " +
									"opening bold tag", pos, chars);

							if (italic)
								g.setFont(deriveFont(f, Font.ITALIC));
							else
								g.setFont(deriveFont(f, Font.PLAIN));

							bold = false;
							break;

						case 'E':
						case 'e':
						case 'I':
						case 'i':
							if (bold)
								g.setFont(deriveFont(f, Font.BOLD));
							else
								g.setFont(deriveFont(f, Font.PLAIN));

							if (!italic)
								throwBadHTML("Closing italics tag w/o"
									 +"opening italics tag", pos, chars);

							italic = false;
							break;

						case 'S':
						case 's':

							switch (chars[pos + 1])
							{
								case 'T':
								case 't':
									if (italic)
										g.setFont(deriveFont(f, Font.ITALIC));
									else
										g.setFont(deriveFont(f, Font.PLAIN));

									bold = false;
									break;

								case '>':
									strikethrough = false;
									break;
							}
							break;

                    case 'U':
                    case 'u':
                        underline = false;
                        break;

                    case 'F':
                    case 'f':
                        if (_colorStack.isEmpty())
                            g.setColor(defaultColor);
                        else
                            g.setColor(_colorStack.pop());
                        break;

                    default:
                        throwBadHTML("Malformed or unsupported HTML",
                            pos, chars);
                    }
                } 
				else
				{
                    switch (chars[pos])
					{
						case 'B':
						case 'b':
							switch (chars[pos + 1])
							{
								case 'R':
								case 'r':

								if (style == STYLE_WORDWRAP)
								{
									x = origX;
									int lineHeight = g.getFontMetrics().getHeight();
									y += lineHeight;
									heightPainted += lineHeight;
									widthPainted = 0;
								}
								break;

							case '>':
								bold = true;
								if (italic)
									g.setFont(deriveFont(f, Font.BOLD | Font.ITALIC));
								else
									g.setFont(deriveFont(f, Font.BOLD));
								break;
							}

							break;

						case 'e':
						case 'E':
						case 'I':
						case 'i':
							italic = true;

							if (bold)
								g.setFont(deriveFont(f, Font.ITALIC | Font.BOLD));
							else
								g.setFont(deriveFont(f, Font.ITALIC));
							break;

						case 'S':
						case 's':

							switch (chars[pos + 1])
							{
								case '>':
									strikethrough = true;
									break;

								case 'T':
								case 't':
									bold = true;

									if (italic)
										g.setFont(deriveFont(f, Font.BOLD | Font.ITALIC));
									else
										g.setFont(deriveFont(f, Font.BOLD));
									break;
							}
							
							break;

						case 'U':
						case 'u':
							underline = true;
							break;

						case 'f':
						case 'F':
							Color c = findColor(chars, pos, tagEnd);
							_colorStack.push(g.getColor());

							if (background != null)
								c = HtmlLabelUI.ensureContrastingColor(c, background);

							g.setColor(c);
							break;

						case 'P':
						case 'p':
							if (style == STYLE_WORDWRAP)
							{
								x = origX;
								int lineHeight = g.getFontMetrics().getHeight();
								y += (lineHeight + (lineHeight / 2));
								heightPainted = y + lineHeight;
								widthPainted = 0;
							}
							break;

						case 'H':
						case 'h':
							if (pos == 1)
								break;
							else {
								throwBadHTML("Malformed or unsupported HTML",
									pos, chars);
								break;
							}

						default:
							throwBadHTML("Malformed or unsupported HTML",
								pos, chars);
                    }
                }

                pos = tagEnd + (done ? 0 : 1);
                inTag = false;
            } 
			else
			{
                if (lastWasWhitespace)
				{
                    while ((pos < (s.length() - 1))
						&& Character.isWhitespace(chars[pos]))
                        pos++;

                    if (pos == (chars.length - 1))
                        return (style != STYLE_WORDWRAP) 
							? widthPainted
							: heightPainted;
                }

                boolean isAmp = false;
                boolean nextLtIsEntity = false;
                int nextTag = chars.length - 1;

                if ((chars[pos] == '&'))
				{
                    boolean inEntity = pos != (chars.length - 1);
                    if (inEntity)
					{
                        int newPos = substEntity(chars, pos + 1);
                        inEntity = newPos != -1;
                        if (inEntity)
						{
                            pos = newPos;
                            isAmp = chars[pos] == '&';
                            nextLtIsEntity = chars[pos] == '<';
                        } 
						else
						{
                            nextLtIsEntity = false;
                            isAmp = true;
                        }
                    }
                } 
				else
				{
                    nextLtIsEntity = false;
                }

                for (int i = pos; i < chars.length; i++)
				{
                    if ((chars[i] == '<' && !nextLtIsEntity) 
						|| (chars[i] == '&' && !isAmp && i != chars.length - 1))
					{
                        nextTag = i - 1;
                        break;
                    }

                    isAmp = false;
                    nextLtIsEntity = false;
                }

                FontMetrics fm = g.getFontMetrics();
                Rectangle2D r = fm.getStringBounds(chars, pos, nextTag + 1, g);
                if (Utilities.isMac()) 
                    r.setRect(r.getX(), r.getY(), 
						(double)fm.stringWidth(new String(chars, pos, nextTag - pos + 1)),
						r.getHeight());

                lastHeight = r.getHeight();
                int length = (nextTag + 1) - pos;
                boolean goToNextRow = false;
                boolean brutalWrap = false;
                double chWidth;

                if (truncated) 
                    chWidth = dotWidth;
                else
				{
                    chWidth = r.getWidth() / (nextTag+1 - pos);
                    if ((chWidth == Double.POSITIVE_INFINITY)
						|| (chWidth == Double.NEGATIVE_INFINITY)) 
                        chWidth = fm.getMaxAdvance();
                }

                if (((style != STYLE_CLIP) 
					&& ((style == STYLE_TRUNCATE)
					&& ((widthPainted + r.getWidth()) > w)))
					|| ((style == STYLE_WORDWRAP)
					&& ((widthPainted + r.getWidth()) > w)))
				{
                    if (chWidth > 3)
					{
                        double pixelsOff = (widthPainted + (r.getWidth() + 5)) - w;
                        double estCharsOver = pixelsOff / chWidth;
                        if (style == STYLE_TRUNCATE)
						{
                            int charsToPaint = Math.round(Math.round(Math.ceil((w - widthPainted) / chWidth)));
                            int startPeriodsPos = (pos + charsToPaint) - 3;

                            if (startPeriodsPos >= chars.length)
                                startPeriodsPos = chars.length - 4;

							length = (startPeriodsPos - pos);
                            if (length < 0) 
                                length = 0;

                            r = fm.getStringBounds(chars, pos, pos + length, g);
                            if (Utilities.isMac()) 
                                r.setRect(r.getX(), r.getY(), (double)fm.stringWidth(new String(chars, pos, length)), r.getHeight());
                            truncated = true;
                        } 
						else
						{
                            goToNextRow = true;
                            int lastChar = new Double(nextTag - estCharsOver).intValue();
                            brutalWrap = x == 0;

                            for (int i = lastChar; i > pos; i--)
							{
                                lastChar--;
                                if (Character.isWhitespace(chars[i]))
								{
                                    length = (lastChar - pos) + 1;
                                    brutalWrap = false;
                                    break;
                                }
                            }

                            if ((lastChar <= pos)
								&& (length > estCharsOver)
								&& !brutalWrap)
							{
                                x = origX;
                                y += r.getHeight();
                                heightPainted += r.getHeight();

                                boolean boundsChanged = false;

                                while (!done
									&& Character.isWhitespace(chars[pos])
									&& (pos < nextTag))
								{
                                    pos++;
                                    boundsChanged = true;
                                    done = pos == (chars.length - 1);
                                }

                                if (pos == nextTag) 
                                    lastWasWhitespace = true;

                                if (boundsChanged)
								{
                                    r = fm.getStringBounds(chars, pos, nextTag + 1, g);
                                    if (Utilities.isMac()) 
                                        r.setRect(r.getX(), r.getY(), (double)fm.stringWidth(new String(chars, pos, nextTag - pos + 1)), r.getHeight());
                                }

                                goToNextRow = false;
                                widthPainted = 0;

                                if (chars[pos - 1 + length] == '<')
                                    length--;
                            } 
							else if (brutalWrap)
							{
                                length = (new Double((w - widthPainted) / chWidth)).intValue();
                                if ((pos + length) > nextTag) 
                                    length = (nextTag - pos);
                                goToNextRow = true;
                            }
                        }
                    }
                }

                if (!done)
				{
                    if (paint)
                        g.drawChars(chars, pos, length, x, y);

                    if (strikethrough || underline)
					{
                        LineMetrics lm = fm.getLineMetrics(chars, pos, length - 1, g);
                        int lineWidth = new Double(x + r.getWidth()).intValue();

                        if (paint)
						{
                            if (strikethrough)
							{
                                int stPos = Math.round(lm.getStrikethroughOffset()) +
                                    g.getFont().getBaselineFor(chars[pos]) + 1;
                                g.drawLine(x, y + stPos, lineWidth, y + stPos);
                            }

                            if (underline)
							{
                                int stPos = Math.round(lm.getUnderlineOffset()) +
                                    g.getFont().getBaselineFor(chars[pos]) + 1;
                                g.drawLine(x, y + stPos, lineWidth, y + stPos);
                            }
                        }
                    }

                    if (goToNextRow)
					{
                        x = origX;
                        y += r.getHeight();
                        heightPainted += r.getHeight();
                        widthPainted = 0;
                        pos += (length);

                        while ((pos < chars.length) 
							&& (Character.isWhitespace(chars[pos]))
							&& (chars[pos] != '<'))
                            pos++;

                        lastWasWhitespace = true;
                        done |= (pos >= chars.length);
                    } 
					else
					{
                        x += r.getWidth();
                        widthPainted += r.getWidth();
                        lastWasWhitespace = Character.isWhitespace(chars[nextTag]);
                        pos = nextTag + 1;
                    }

                    done |= (nextTag == chars.length);
                }
            }
        }

        if (style != STYLE_WORDWRAP) 
            return widthPainted;
        else
            return heightPainted + lastHeight;
    }

    private static Color findColor
		(final char[] ch, final int pos, final int tagEnd)
	{
        int colorPos = pos;
        boolean useUIManager = false;

        for (int i = pos; i < tagEnd; i++)
		{
            if (ch[i] == 'c') {
                colorPos = i + 6;

                if ((ch[colorPos] == '\'')
					|| (ch[colorPos] == '"'))
                    colorPos++;

                if (ch[colorPos] == '#')
				{
                    colorPos++;
                } 
				else if (ch[colorPos] == '!')
				{
                    useUIManager = true;
                    colorPos++;
                }

                break;
            }
        }

        if (colorPos == pos)
		{
            String out = "Could not find color identifier in font declaration";
            throwBadHTML(out, pos, ch);
        }

        String s;
        if (useUIManager)
		{
            int end = ch.length - 1;

            for (int i = colorPos; i < ch.length; i++)
			{
                if ((ch[i] == '"') || (ch[i] == '\''))
				{
                    end = i;
                    break;
                }
            }

            s = new String(ch, colorPos, end - colorPos);
        } 
		else
		{
            s = new String(ch, colorPos, 6);
        }

        Color result = null;

        if (useUIManager)
		{
            result = UIManager.getColor(s);
            if (result == null)
			{
                throwBadHTML("Could not resolve logical font declared in HTML: " + s,
                    pos, ch);
                result = UIManager.getColor("textText");
                if (result == null)
                    result = Color.BLACK;
            }
        } 
		else
		{
            try
			{
                int rgb = Integer.parseInt(s, 16);
                result = new Color(rgb);
            } 
			catch (NumberFormatException nfe)
			{
                throwBadHTML("Illegal hexadecimal color text: " + s +
                    " in HTML string", colorPos, ch);
            }
        }

        if (result == null)
		{
            throwBadHTML("Unresolvable html color: " + s +
				" in HTML string \n  ", pos, ch);
        }

        return result;
    }

    private static final Font deriveFont(Font f, int style)
	{
        Font result = Utilities.isMac() 
			? new Font(f.getName(), style, f.getSize())
			: f.deriveFont(style);
        return result;
    }

    private static final int substEntity(char[] ch, int pos)
	{
        if (pos >= (ch.length - 2))
            return -1;

        if (ch[pos] == '#') 
            return substNumericEntity(ch, pos + 1);

        boolean match;

        for (int i = 0; i < entities.length; i++)
		{
            char[] c = (char[]) entities[i];
            match = true;

            if (c.length < (ch.length - pos))
			{
                for (int j = 0; j < c.length; j++) 
                    match &= (c[j] == ch[j + pos]);
            } 
			else
			{
                match = false;
            }

            if (match)
			{
                if (ch[pos + c.length] == ';')
				{
                    ch[pos + c.length] = entitySubstitutions[i];
                    return pos + c.length;
                }
            }
        }

        return -1;
    }

    private static final int substNumericEntity(char[] ch, int pos)
	{
        for (int i = pos; i < ch.length; i++)
		{
            if (ch[i] == ';')
			{
                try
				{
                    ch[i] = (char) Integer.parseInt(new String(ch, pos, i - pos));
                    return i;
                } 
				catch (NumberFormatException nfe)
				{
                    throwBadHTML("Unparsable numeric entity: " +
                        new String(ch, pos, i - pos), pos, ch);
                }
            }
        }

        return -1;
    }

    private static void throwBadHTML(String msg, int pos, char[] chars)
	{
        char[] chh = new char[pos];
        Arrays.fill(chh, ' ');
        chh[pos - 1] = '^';

        String out = msg + "\n  " + new String(chars) + "\n  " 
			+ new String(chh) + "\n Full HTML string:" + new String(chars);

        if (!STRICT_HTML)
		{
            if (LOG.isLoggable(Level.WARNING))
			{
                if (badStrings == null)
                    badStrings = new HashSet<String>();

                if (!badStrings.contains(msg))
				{
                    StringTokenizer tk = new StringTokenizer(out, "\n", false);
                    while (tk.hasMoreTokens())
                        LOG.warning(tk.nextToken());
                    badStrings.add(msg.intern());
                }
            }
        } 
		else
		{
            throw new IllegalArgumentException(out);
        }
    }

    public interface Renderer extends ListCellRenderer
	{
        void setParentFocused(boolean parentFocused);
        void setCentered(boolean centered);
        void setIndent(int pixels);
        void setHtml(boolean val);
        void setRenderStyle(int style);
        void setIcon(Icon icon);
        void reset();
        void setText(String txt);
        void setIconTextGap(int gap);
    }

}
