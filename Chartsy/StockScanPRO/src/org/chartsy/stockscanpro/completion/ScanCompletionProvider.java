package org.chartsy.stockscanpro.completion;

import java.util.Set;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.chartsy.stockscanpro.lexer.api.ScanTokenId;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;

/**
 *
 * @author Viorel
 */
public class ScanCompletionProvider implements CompletionProvider
{

	public ScanCompletionProvider()
	{}

	@Override
	public CompletionTask createTask(int i, JTextComponent jTextComponent)
	{
        return new AsyncCompletionTask(new AsyncCompletionQuery()
		{
            @Override
            protected void query(CompletionResultSet completionResultSet, Document document, int caretOffset)
			{
				final StyledDocument bDoc = (StyledDocument) document;

				TokenHierarchy<StyledDocument> th = TokenHierarchy.get(bDoc);
				TokenSequence<ScanTokenId> ts = th.tokenSequence(ScanTokenId.language());

				ts.move(caretOffset == 0 ? 0 : caretOffset - 1);
				ts.moveNext();

				Token<ScanTokenId> token = ts.token();
				String possibleKeyword = token.text().toString();

				int firstOffset = ts.offset();
				int prefixLength = caretOffset - ts.offset();
				String prefix = prefixLength > 0 ? possibleKeyword.substring(0, prefixLength) : possibleKeyword;
				int lastOffset = bDoc.getLength() - 1;
				if (ts.moveNext())
					lastOffset = ts.offset() - 1;

				// keywords
				Set<ScanTokenId> kwds = ScanTokenId.language().tokenCategoryMembers("keyword");
				for (ScanTokenId kwd : kwds)
				{
					final String key = kwd.fixedText();
					if (!key.equals("") && key.startsWith(prefix))
						completionResultSet.addItem(AbstractCompletionItem.getKeywordItem(key, firstOffset, lastOffset));
				}

				// trade value
				Set<ScanTokenId> lits = ScanTokenId.language().tokenCategoryMembers("literal");
				for (ScanTokenId lit : lits)
				{
					final String key = lit.fixedText();
					if (!key.equals("") && key.startsWith(prefix))
						completionResultSet.addItem(AbstractCompletionItem.getTradeValueItem(key, firstOffset, lastOffset));
				}

				// indicators
				for (IndexedIndicator indicator : PredefinedIndicators.getIndicators())
				{
					final String key = indicator.toString();
					if (!key.equals("") && key.startsWith(prefix))
						completionResultSet.addItem(
							AbstractCompletionItem.getIndicatorItem(key, firstOffset, lastOffset,
								indicator.getFixedName(),
								indicator.getParams(),
								indicator.hasTradeValue(),
								indicator.hasExtraParam())
						);
				}

                completionResultSet.finish();

            }
        }, jTextComponent);
	}

	public int getAutoQueryTypes(JTextComponent component, String string)
	{
		if (string != null && string.length() > 0)
			if (CompletionUtilities.isScanContext(component, component.getSelectionStart() - 1))
				return COMPLETION_QUERY_TYPE;
		
		return 0;
	}

}
