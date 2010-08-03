package org.chartsy.stockscanpro.completion;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.net.URL;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.spi.editor.completion.CompletionDocumentation;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;
import org.openide.text.NbDocument;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author Viorel
 */
public abstract class AbstractCompletionItem implements CompletionItem
{

	public static KeywordItem getKeywordItem(String fixedName, int firstOffset, int lastOffset)
	{
		return new KeywordItem(fixedName, firstOffset, lastOffset);
	}

	public static TradeValueItem getTradeValueItem(String fixedName, int firstOffset, int lastOffset)
	{
		return new TradeValueItem(fixedName, firstOffset, lastOffset);
	}

	public static IndicatorItem getIndicatorItem(String fixedName, int firstOffset, int lastOffset,
		String kwd, String[] params, boolean hasTradeValue, boolean hasExtraParam)
	{
		return new IndicatorItem(fixedName, firstOffset, lastOffset, kwd, params, hasTradeValue, hasExtraParam);
	}

	private static IndicatorCompletionDocumentation getIndicatorCompletionDocumentation(String keyword)
	{
		return new IndicatorCompletionDocumentation(keyword);
	}

	private static final String ICON_URL = "org/chartsy/stockscanpro/resources/stock.png";

	protected String fixedName;
	protected int firstOffset;
	protected int lastOffset;
	protected String type;

	public static final String KEYWORD = "Keyword";
	public static final String INDICATOR = "Indicator";
	public static final String TRADE_VALUE = "TradeValue";

	public static final String COLOR_END = "</font>";
    public static final String BOLD = "<b>";
    public static final String BOLD_END = "</b>";
	public static final String ITALICS = "<i>";
    public static final String ITALICS_END = "</i>";

	public AbstractCompletionItem(String fixedName, int firstOffset, int lastOffset, String type)
	{
		this.fixedName = fixedName;
		this.firstOffset = firstOffset;
		this.lastOffset = lastOffset;
		this.type = type;
	}

	public void defaultAction(final JTextComponent component)
	{
		final StyledDocument doc = (StyledDocument) component.getDocument();

		class AtomicChange implements Runnable
		{
			public void run()
			{
				try
				{
					int caretOffset = component.getCaretPosition();
					int lenght = Math.max(0, lastOffset - firstOffset + 1);
					doc.remove(firstOffset, lenght);
					doc.insertString(firstOffset, getText(), null);
				}
				catch (BadLocationException e)
				{}
			}
		}

		try
		{
			NbDocument.runAtomicAsUser(doc, new AtomicChange());
		}
		catch (BadLocationException e)
		{}
		finally
		{
			Completion.get().hideAll();
		}
	}

	public void processKeyEvent(KeyEvent evt)
	{}

	public String getLeftHtmlText()
	{
		return null;
	}

	public String getRightHtmlText()
	{
		return ITALICS + getType() + ITALICS_END;
	}

	public String getKeyword()
	{
		return fixedName;
	}

	public String getType()
	{
		return this.type;
	}

	public ImageIcon getIcon()
	{
		return ImageUtilities.loadImageIcon(ICON_URL, true);
	}

	public int getPreferredWidth(Graphics g, Font defaultFont)
	{
		return CompletionUtilities.getPreferredWidth(getLeftHtmlText(), getRightHtmlText(), g, defaultFont);
	}

	public void render(Graphics g, Font defaultFont, Color defaultColor, Color backgroundColor, int width, int height, boolean selected)
	{
		CompletionUtilities.renderHtml(getIcon(), getLeftHtmlText(), getRightHtmlText(), g, defaultFont, defaultColor, width, height, selected);
	}

	public CompletionTask createDocumentationTask()
	{
		return null;
	}

	public CompletionTask createToolTipTask()
	{
		return null;
	}

	public boolean instantSubstitution(JTextComponent component)
	{
		return false;
	}

	public int getSortPriority()
	{
		return 0;
	}

	public CharSequence getSortText()
	{
		return fixedName;
	}

	public CharSequence getInsertPrefix()
	{
		return fixedName;
	}

	public String getText()
	{
		return fixedName;
	}

	public static class KeywordItem extends AbstractCompletionItem
	{

		private static final String KEYWORD_COLOR = "<font color=#4096ee>";
		private String leftText;

		public KeywordItem(String fixedName, int firstOffset, int lastOffset)
		{
			super(fixedName, firstOffset, lastOffset, KEYWORD);
		}

		public String getLeftHtmlText()
		{
			if (leftText == null)
			{
                StringBuilder sb = new StringBuilder();
                sb.append(KEYWORD_COLOR);
                sb.append(BOLD);
                sb.append(fixedName);
                sb.append(BOLD_END);
                sb.append(COLOR_END);
                leftText = sb.toString();
            }
            return leftText;
		}

	}

	public static class TradeValueItem extends AbstractCompletionItem
	{

		private static final String TRADE_VALUE_COLOR = "<font color=#d15600>";
		private String leftText;

		public TradeValueItem(String fixedName, int firstOffset, int lastOffset)
		{
			super(fixedName, firstOffset, lastOffset, TRADE_VALUE);
		}

		public String getLeftHtmlText()
		{
			if (leftText == null)
			{
                StringBuilder sb = new StringBuilder();
                sb.append(TRADE_VALUE_COLOR);
                sb.append(BOLD);
                sb.append(fixedName);
                sb.append(BOLD_END);
                sb.append(COLOR_END);
                leftText = sb.toString();
            }
            return leftText;
		}

	}

	public static class IndicatorItem extends AbstractCompletionItem
	{

		private static final String INDICATOR_COLOR = "<font color=#356aa0>";
		private static final String TRADE_VALUE_COLOR = "<font color=#d15600>";
		private static final String NUMBER_COLOR = "<font color=#008c00>";
		private String leftText;

		private String kwd;
		private String[] params;
		private boolean hasTradeValue = false;
		private boolean hasExtraParam = false;

		public IndicatorItem(String fixedName, int firstOffset, int lastOffset,
			String kwd, String[] params,
			boolean hasTradeValue, boolean hasExtraParam)
		{
			super(fixedName, firstOffset, lastOffset, INDICATOR);
			this.kwd = kwd;
			this.params = params != null ? params : new String[] {};
			this.hasTradeValue = hasTradeValue;
			this.hasExtraParam = hasExtraParam;
		}

		public String getLeftHtmlText()
		{
			if (leftText == null)
			{
                StringBuilder sb = new StringBuilder();
                sb.append(INDICATOR_COLOR);
                sb.append(BOLD);
                sb.append(this.kwd);
                sb.append(BOLD_END);
                sb.append(COLOR_END);

				sb.append("(");
				if (params.length > 0)
				{
					for (int i = 0; i < params.length; i++)
					{
						sb.append(hasTradeValue ? (i == 0 ? TRADE_VALUE_COLOR : NUMBER_COLOR) : NUMBER_COLOR);
						sb.append(hasTradeValue ? (i == 0 ? BOLD : "") : "");
						sb.append(params[i]);
						sb.append(hasTradeValue ? (i == 0 ? BOLD_END : "") : "");
						sb.append(COLOR_END);

						if (i < params.length - 1)
							sb.append(", ");
					}
				}
				sb.append(")");

				if (hasExtraParam)
				{
					sb.append("[-");
					sb.append(NUMBER_COLOR);
					sb.append("1");
					sb.append(COLOR_END);
					sb.append("]");
				}
                leftText = sb.toString();
            }
            return leftText;
		}

		public String getText()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(this.kwd);

			sb.append("(");
			if (params.length > 0)
			{
				for (int i = 0; i < params.length; i++)
				{
					sb.append(params[i]);
					if (i < params.length - 1)
						sb.append(", ");
				}
			}
			sb.append(")");

			if (hasExtraParam)
				sb.append("[-1]");

			return sb.toString();
		}

		@Override
		public CompletionTask createDocumentationTask()
		{
			if (NbBundle.getMessage(AbstractCompletionItem.class, kwd + "_DESC") != null
				&& !NbBundle.getMessage(AbstractCompletionItem.class, kwd + "_DESC").equals(""))
			{
				return new AsyncCompletionTask(new AsyncCompletionQuery()
				{
					@Override
					protected void query(CompletionResultSet completionResultSet, Document document, int i)
					{
						completionResultSet.setDocumentation(getIndicatorCompletionDocumentation(kwd));
						completionResultSet.finish();
					}
				});
			}
			else
			{
				return null;
			}
		}

	}

	public static class IndicatorCompletionDocumentation implements CompletionDocumentation
	{

		private String keyword;

		public IndicatorCompletionDocumentation(String keyword)
		{
			this.keyword = keyword;
		}

		public String getText()
		{
			return NbBundle.getMessage(AbstractCompletionItem.class, keyword + "_DESC");
		}

		public URL getURL()
		{
			return null;
		}

		public CompletionDocumentation resolveLink(String string)
		{
			return null;
		}

		public Action getGotoSourceAction()
		{
			return null;
		}

	}
	
}
