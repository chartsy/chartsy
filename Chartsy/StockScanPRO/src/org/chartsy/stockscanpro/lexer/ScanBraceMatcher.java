package org.chartsy.stockscanpro.lexer;

import java.util.List;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.chartsy.stockscanpro.lexer.api.ScanTokenId;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.spi.editor.bracesmatching.BracesMatcher;
import org.netbeans.spi.editor.bracesmatching.BracesMatcherFactory;
import org.netbeans.spi.editor.bracesmatching.MatcherContext;
import org.netbeans.spi.editor.bracesmatching.support.BracesMatcherSupport;

/**
 *
 * @author Viorel
 */
public final class ScanBraceMatcher implements BracesMatcher, BracesMatcherFactory
{

	private static final char [] PAIRS = new char [] { '(', ')', '[', ']' };
	private static final ScanTokenId [] PAIR_TOKEN_IDS
		= new ScanTokenId []
	{
        ScanTokenId.LPAREN, ScanTokenId.RPAREN,
        ScanTokenId.LBRACKET, ScanTokenId.RBRACKET
    };

	private final MatcherContext context;

	private int originOffset;
    private char originChar;
    private char matchingChar;
    private boolean backward;
    private List<TokenSequence<?>> sequences;

	public ScanBraceMatcher()
	{
        this(null);
    }

	private ScanBraceMatcher(MatcherContext context)
	{
        this.context = context;
    }

	public int[] findOrigin()
		throws BadLocationException, InterruptedException
	{
		((AbstractDocument) context.getDocument()).readLock();
		try
		{
			int [] origin = BracesMatcherSupport.findChar
				(
				context.getDocument(),
                context.getSearchOffset(),
                context.getLimitOffset(),
                PAIRS
				);

			if (origin != null)
			{
				originOffset = origin[0];
				originChar = PAIRS[origin[1]];
                matchingChar = PAIRS[origin[1] + origin[2]];
                backward = origin[2] < 0;

				TokenHierarchy<Document> th = TokenHierarchy.get(context.getDocument());
				sequences = getEmbeddedTokenSequences(th, originOffset, backward, ScanTokenId.language());

				if (!sequences.isEmpty())
				{
					TokenSequence<?> seq = sequences.get(sequences.size() - 1);
					seq.move(originOffset);
				}

				return new int [] { originOffset, originOffset + 1 };
			} else {
				return null;
			}
		}
		finally
		{
			((AbstractDocument) context.getDocument()).readUnlock();
		}
	}

	public int[] findMatches()
		throws InterruptedException, BadLocationException
	{
		((AbstractDocument) context.getDocument()).readLock();
		try
		{
			if (!sequences.isEmpty())
			{
				TokenSequence<?> seq = sequences.get(sequences.size() - 1);
				seq.move(originOffset);

				TokenHierarchy<Document> th = TokenHierarchy.get(context.getDocument());
				List<TokenSequence<?>> list;
				if (backward)
				{
					list = th.tokenSequenceList(seq.languagePath(), 0, originOffset);
				}
				else
				{
					list = th.tokenSequenceList(seq.languagePath(), originOffset + 1, context.getDocument().getLength());
				}

				ScanTokenId originId = getTokenId(originChar);
				ScanTokenId lookingForId = getTokenId(matchingChar);
				int counter = 0;

				for(TokenSequenceIterator tsi = new TokenSequenceIterator(list, backward); tsi.hasMore(); )
				{
					TokenSequence<?> sq = tsi.getSequence();

					if (originId == sq.token().id())
					{
						counter++;
					}
					else if (lookingForId == sq.token().id())
					{
						if (counter == 0)
							return new int [] { sq.offset(), sq.offset() + sq.token().length() };
						else
							counter--;
					}
				}
			}

			return null;
		}
		finally
		{
			((AbstractDocument) context.getDocument()).readUnlock();
		}
	}

	private ScanTokenId getTokenId(char ch)
	{
		for(int i = 0; i < PAIRS.length; i++)
		{
			if (PAIRS[i] == ch)
				return PAIR_TOKEN_IDS[i];
		}
		return null;
	}

	public static List<TokenSequence<?>> getEmbeddedTokenSequences(
		TokenHierarchy<?> th, int offset, boolean backwardBias,
		Language<?> language)
	{
		List<TokenSequence<?>> sequences
			= th.embeddedTokenSequences(offset, backwardBias);

		for (int i = sequences.size() - 1; i >= 0; i--)
		{
			TokenSequence<?> seq = sequences.get(i);
			if (seq.language() == language)
				break;
			else
				sequences.remove(i);
		}

		return sequences;
	}

	private static final class TokenSequenceIterator
	{

		private final List<TokenSequence<?>> list;
		private final boolean backward;

		private int index;

		public TokenSequenceIterator(List<TokenSequence<?>> list, boolean backward)
		{
			this.list = list;
            this.backward = backward;
            this.index = -1;
		}

		public boolean hasMore()
		{
			return backward ? hasPrevious() : hasNext();
		}

		public TokenSequence<?> getSequence()
		{
			assert index >= 0 && index < list.size() :
				"No sequence available, call hasMore() first.";

			return list.get(index);
		}

		private boolean hasPrevious()
		{
			boolean anotherSeq = false;

			if (index == -1)
			{
				index = list.size() - 1;
				anotherSeq = true;
			}

			for( ; index >= 0; index--)
			{
				TokenSequence<?> seq = list.get(index);
				if (anotherSeq)
					seq.moveEnd();

				if (seq.movePrevious())
					return true;

				anotherSeq = true;
			}

			return false;
		}

		private boolean hasNext()
		{
			boolean anotherSeq = false;

			if (index == -1)
			{
				index = 0;
				anotherSeq = true;
			}

			for( ; index < list.size(); index++)
			{
				TokenSequence<?> seq = list.get(index);
				if (anotherSeq)
					seq.moveStart();

				if (seq.moveNext())
					return true;

				anotherSeq = true;
			}

			return false;
		}

	}

	public BracesMatcher createMatcher(MatcherContext context)
	{
		return new ScanBraceMatcher(context);
	}

}
