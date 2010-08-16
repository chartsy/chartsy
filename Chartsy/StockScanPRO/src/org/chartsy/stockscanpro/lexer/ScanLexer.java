package org.chartsy.stockscanpro.lexer;

import java.util.regex.Pattern;
import org.chartsy.stockscanpro.lexer.api.ScanTokenId;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;

/**
 *
 * @author Viorel
 */
public class ScanLexer implements Lexer<ScanTokenId>
{

	private static final int EOF = LexerInput.EOF;

	private final LexerInput input;

	private final TokenFactory<ScanTokenId> tokenFactory;

	private boolean tradeValueFlag = false;

	public ScanLexer(LexerRestartInfo<ScanTokenId> info)
	{
		this.input = info.input();
		this.tokenFactory = info.tokenFactory();
		assert (info.state() == null); // never set to non-null value in state()
	}

	@Override
	public Object state()
	{
		return null; // always in default state after token recognition
	}

	@Override
	public Token<ScanTokenId> nextToken()
	{
		while (true)
		{
			int c = input.read();
			switch (c)
			{
				case '"': case '|': case '&': case '%': case '^': case '\'':
				case '!': case '~': case ';': case ':': case '?': case '{':
				case '}': case '@':
					return token(ScanTokenId.ERROR);

				case ',':
					return token(ScanTokenId.COMMA);

				case '=':
					if ((c = input.read()) == '=') // ==
						return token(ScanTokenId.ERROR);

					input.backup(1);
					return token(ScanTokenId.EQ);

				case '>':
					switch (c = input.read())
					{
						case '>': // >>
							switch (c = input.read())
							{
								case '>': // >>>
									if ((c = input.read()) == '=') // >>>=
										return token(ScanTokenId.ERROR);
									input.backup(1);
									return token(ScanTokenId.ERROR);

								case '=': // >>=
									return token(ScanTokenId.ERROR);
							}

							input.backup(1);
							return token(ScanTokenId.ERROR);

						case '=': // >=
							return token(ScanTokenId.GTEQ);
					}

					input.backup(1);
					return token(ScanTokenId.GT);

				case '<':
					switch (c = input.read())
					{
						case '<': // <<
							if ((c = input.read()) == '=') // <<=
								return token(ScanTokenId.ERROR);

							input.backup(1);
							return token(ScanTokenId.ERROR);

						case '=': // <=
							return token(ScanTokenId.LTEQ);
					}

					input.backup(1);
					return token(ScanTokenId.LT);

				case '+':
					return token(ScanTokenId.PLUS);

				case '-':
					return token(ScanTokenId.MINUS);

				case '*':
					return token(ScanTokenId.STAR);

				case '/':
					return token(ScanTokenId.SLASH);

				case '.':
					if ((c = input.read()) == '.')
						if ((c = input.read()) == '.') // ...
							return token(ScanTokenId.ERROR);
						else
							input.backup(2);
					else if ('0' <= c && c >= '9') // float literal
					{
						return finishNumberLiteral(c = input.read(), true);
					}
					else
						input.backup(1);

					return token(ScanTokenId.ERROR);

				case '(':
					return token(ScanTokenId.LPAREN);

				case ')':
					return token(ScanTokenId.RPAREN);

				case '[':
					return token(ScanTokenId.LBRACKET);

				case ']':
					return token(ScanTokenId.RBRACKET);

				case '0': // is a number
					c = input.read();
					return finishNumberLiteral(c, false);

				case '1': case '2': case '3': case '4': case '5':
				case '6': case '7': case '8': case '9':
					return finishNumberLiteral(c = input.read(), false);

				// Keywords and indicators lexing
				case 'a':
					switch (c = input.read())
					{
						case 'd': // adx
							if ((c = input.read()) == 'x')
								return parseParamsIndicator(ScanTokenId.ADX, false, 1);
							else
								return token(ScanTokenId.ERROR);

						case 'n': // and
							return ((c = input.read()) == 'd')
								? token(ScanTokenId.AND) : token(ScanTokenId.ERROR);

						case 't': // atr
							if ((c = input.read()) == 'r')
								return parseParamsIndicator(ScanTokenId.ATR, false, 1);
							else
								return token(ScanTokenId.ERROR);

						default:
							return token(ScanTokenId.ERROR);
					}

				case 'b':
					switch (c = input.read())
					{
						case 'o':
							switch (c = input.read())
							{
								case 'd':
									if ((c = input.read()) == 'y'
										&& (c = input.read()) == '_')

										switch (c = input.read())
										{
											case 'b': // body_bottom
												if ((c = input.read()) == 'o'
													&& (c = input.read()) == 't'
													&& (c = input.read()) == 't'
													&& (c = input.read()) == 'o'
													&& (c = input.read()) == 'm')
													return parseNoParamsIndicator(ScanTokenId.BODY_BOTTOM);
												else
													return token(ScanTokenId.ERROR);

											case 't': // body_top
												if ((c = input.read()) == 'o'
													&& (c = input.read()) == 'p')
													return parseNoParamsIndicator(ScanTokenId.BODY_TOP);
												else
													return token(ScanTokenId.ERROR);
										}
									break;

								case 'l':
									if ((c = input.read()) == 'l'
										&& (c = input.read()) == 'i'
										&& (c = input.read()) == 'n'
										&& (c = input.read()) == 'g'
										&& (c = input.read()) == 'e'
										&& (c = input.read()) == 'r'
										&& (c = input.read()) == '_')

										switch (c = input.read())
										{
											case 'l': // bollinger_lower
												if ((c = input.read()) == 'o'
													&& (c = input.read()) == 'w'
													&& (c = input.read()) == 'e'
													&& (c = input.read()) == 'r')
													return parseParamsIndicator(ScanTokenId.BOLLINGER_LOWER, true, 3);
												else
													return token(ScanTokenId.ERROR);

											case 'm': // bollinger_middle
												if ((c = input.read()) == 'i'
													&& (c = input.read()) == 'd'
													&& (c = input.read()) == 'd'
													&& (c = input.read()) == 'l'
													&& (c = input.read()) == 'e')
													return parseParamsIndicator(ScanTokenId.BOLLINGER_MIDDLE, true, 2);
												else
													return token(ScanTokenId.ERROR);

											case 'u': // bollinger_upper
												if ((c = input.read()) == 'p'
													&& (c = input.read()) == 'p'
													&& (c = input.read()) == 'e'
													&& (c = input.read()) == 'r')
													return parseParamsIndicator(ScanTokenId.BOLLINGER_UPPER, true, 3);
												else
													return token(ScanTokenId.ERROR);

											default:
												return token(ScanTokenId.ERROR);
										}
									break;

								default:
									return token(ScanTokenId.ERROR);
							}

						default:
							return token(ScanTokenId.ERROR);
					}

				case 'c':
					switch (c = input.read())
					{
						case 'c':
							if ((c = input.read()) == 'i')
							{
								if ((c = input.read()) == '_')
								{
									if ((c = input.read()) == 'e'
										&& (c = input.read()) == 'm'
										&& (c = input.read()) == 'a')
										return parseParamsIndicator(ScanTokenId.CCI_EMA, false, 2);
									else
										return token(ScanTokenId.ERROR);
								}

								input.backup(1);
								return parseParamsIndicator(ScanTokenId.CCI, false, 1);
							}
							break;

						case 'l': // close
							if ((c = input.read()) == 'o'
								&& (c = input.read()) == 's'
								&& (c = input.read()) == 'e')
							{
								if (tradeValueFlag)
								{
									tradeValueFlag = false;
									return token(ScanTokenId.CLOSE_VAR);
								}
								
								return parseNoParamsIndicator(ScanTokenId.CLOSE);
							}
							else
								return token(ScanTokenId.ERROR);

						default:
							return token(ScanTokenId.ERROR);
					}

				case 'e': // ema
					if ((c = input.read()) == 'm'
						&& (c = input.read()) == 'a')
						return parseParamsIndicator(ScanTokenId.EMA, true, 2);
					else
						return token(ScanTokenId.ERROR);

				case 'f': // force_index
					if ((c = input.read()) == 'o'
						&& (c = input.read()) == 'r'
						&& (c = input.read()) == 'c'
						&& (c = input.read()) == 'e'
						&& (c = input.read()) == '_'
						&& (c = input.read()) == 'i'
						&& (c = input.read()) == 'n'
						&& (c = input.read()) == 'd'
						&& (c = input.read()) == 'e'
						&& (c = input.read()) == 'x')
						return parseParamsIndicator(ScanTokenId.FORCE_INDEX, false, 1);
					else
						return token(ScanTokenId.ERROR);

				case 'h': // high
					if ((c = input.read()) == 'i'
						&& (c = input.read()) == 'g'
						&& (c = input.read()) == 'h')
					{
						if (tradeValueFlag)
						{
							tradeValueFlag = false;
							return token(ScanTokenId.HIGH_VAR);
						}

						return parseNoParamsIndicator(ScanTokenId.HIGH);
					}
					else
						return token(ScanTokenId.ERROR);

				case 'l': // low
					if ((c = input.read()) == 'o'
						&& (c = input.read()) == 'w')
					{
						if (tradeValueFlag)
						{
							tradeValueFlag = false;
							return token(ScanTokenId.LOW_VAR);
						}

						return parseNoParamsIndicator(ScanTokenId.LOW);
					}
					else
						return token(ScanTokenId.ERROR);

				case 'm':
					switch (c = input.read())
					{
						case 'a':
							switch (c = input.read())
							{
								case 'x': // max
									return parseParamsIndicator(ScanTokenId.MAX, true, 2);

								case 'c': // macd
									if ((c = input.read()) != 'd')
										return token(ScanTokenId.ERROR);

									if ((c = input.read()) == '_')
									{
										switch (c = input.read())
										{
											case 'h':
												if ((c = input.read()) == 'i'
													&& (c = input.read()) == 's'
													&& (c = input.read()) == 't'
													&& (c = input.read()) == 'o'
													&& (c = input.read()) == 'g'
													&& (c = input.read()) == 'r'
													&& (c = input.read()) == 'a'
													&& (c = input.read()) == 'm')
													return parseParamsIndicator(ScanTokenId.MACD_HISTOGRAM, false, 3);
												break;

											case 's':
												if ((c = input.read()) == 'i'
													&& (c = input.read()) == 'g'
													&& (c = input.read()) == 'n'
													&& (c = input.read()) == 'a'
													&& (c = input.read()) == 'l')
													return parseParamsIndicator(ScanTokenId.MACD_SIGNAL, false, 3);
												break;
										}
									}
									
									input.backup(1);
									return parseParamsIndicator(ScanTokenId.MACD, false, 2);
							}

						case 'd': // mdi
							if ((c = input.read()) == 'i')
								return parseParamsIndicator(ScanTokenId.MDI, false, 1);
							else
								return token(ScanTokenId.ERROR);

						case 'i': // min
							if ((c = input.read()) == 'n')
								return parseParamsIndicator(ScanTokenId.MIN, true, 2);
							else
								return token(ScanTokenId.ERROR);

						case 'o':
							if ((c = input.read()) == 'n'
								&& (c = input.read()) == 'e'
								&& (c = input.read()) == 'y'
								&& (c = input.read()) == '_'
								&& (c = input.read()) == 'f'
								&& (c = input.read()) == 'l'
								&& (c = input.read()) == 'o'
								&& (c = input.read()) == 'w')
							{
								if ((c = input.read()) == '_')
								{
									if ((c = input.read()) == 'a'
										&& (c = input.read()) == 'v'
										&& (c = input.read()) == 'g')
										return parseParamsIndicator(ScanTokenId.MONEY_FLOW_AVG, false, 1);
									else
										return token(ScanTokenId.ERROR);
								}

								input.backup(1);
								return parseNoParamsIndicator(ScanTokenId.MONEY_FLOW);
							}
							break;

						default:
							return token(ScanTokenId.ERROR);
					}

				case 'o':
					switch (c = input.read())
					{
						case 'p': // open
							if ((c = input.read()) == 'e'
								&& (c = input.read()) == 'n')
							{
								if (tradeValueFlag)
								{
									tradeValueFlag = false;
									return token(ScanTokenId.OPEN_VAR);
								}

								return parseNoParamsIndicator(ScanTokenId.OPEN);
							}
							else
								return token(ScanTokenId.ERROR);

						case 'r': // or
							return token(ScanTokenId.OR);

						case 'n': // on_balance_volume
							if ((c = input.read()) == '_'
								&& (c = input.read()) == 'b'
								&& (c = input.read()) == 'a'
								&& (c = input.read()) == 'l'
								&& (c = input.read()) == 'a'
								&& (c = input.read()) == 'n'
								&& (c = input.read()) == 'c'
								&& (c = input.read()) == 'e'
								&& (c = input.read()) == '_'
								&& (c = input.read()) == 'v'
								&& (c = input.read()) == 'o'
								&& (c = input.read()) == 'l'
								&& (c = input.read()) == 'u'
								&& (c = input.read()) == 'm'
								&& (c = input.read()) == 'e')
								return parseParamsIndicator(ScanTokenId.ON_BALANCE_VOLUME, false, 1);
							else
								return token(ScanTokenId.ERROR);

						default:
							return token(ScanTokenId.ERROR);
					}

				case 'p':
					switch (c = input.read())
					{
						case 'c': // pctr
							if ((c = input.read()) == 't'
								&& (c = input.read()) == 'r')
								return parseParamsIndicator(ScanTokenId.PCTR, false, 1);
							else
								return token(ScanTokenId.ERROR);

						case 'd': // pdi
							if ((c = input.read()) == 'i')
								return parseParamsIndicator(ScanTokenId.PDI, false, 1);
							else
								return token(ScanTokenId.ERROR);

						default:
							return token(ScanTokenId.ERROR);
					}

				case 'r': // rsi
					if ((c = input.read()) == 's'
						&& (c = input.read()) == 'i')
						return parseParamsIndicator(ScanTokenId.RSI, false, 1);
					else
						return token(ScanTokenId.ERROR);

				case 's':
					switch (c = input.read())
					{
						case 'm': // sma
							if ((c = input.read()) == 'a')
								return parseParamsIndicator(ScanTokenId.SMA, true, 2);
							else
								return token(ScanTokenId.ERROR);

						case 'p': // spread
							if ((c = input.read()) == 'r'
								&& (c = input.read()) == 'e'
								&& (c = input.read()) == 'a'
								&& (c = input.read()) == 'd')
							{
								if (tradeValueFlag)
								{
									tradeValueFlag = false;
									return token(ScanTokenId.SPREAD_VAR);
								}

								return parseNoParamsIndicator(ScanTokenId.SPREAD);
							}
							else
								return token(ScanTokenId.ERROR);

						case 't':
							if ((c = input.read()) == 'o'
								&& (c = input.read()) == '_')

								switch (c = input.read())
								{
									case 'd': // sto_d
										return parseParamsIndicator(ScanTokenId.STO_D, false, 3);

									case 'k': // sto_k
										return parseParamsIndicator(ScanTokenId.STO_K, false, 2);

									default:
										return token(ScanTokenId.ERROR);
								}
							break;

						default:
							return token(ScanTokenId.ERROR);
					}

				case 'u': // ultimate_oscillator
					if ((c = input.read()) == 'l'
						&& (c = input.read()) == 't'
						&& (c = input.read()) == 'i'
						&& (c = input.read()) == 'm'
						&& (c = input.read()) == 'a'
						&& (c = input.read()) == 't'
						&& (c = input.read()) == 'e'
						&& (c = input.read()) == '_'
						&& (c = input.read()) == 'o'
						&& (c = input.read()) == 's'
						&& (c = input.read()) == 'c'
						&& (c = input.read()) == 'i'
						&& (c = input.read()) == 'l'
						&& (c = input.read()) == 'l'
						&& (c = input.read()) == 'a'
						&& (c = input.read()) == 't'
						&& (c = input.read()) == 'o'
						&& (c = input.read()) == 'r')
						return parseNoParamsIndicator(ScanTokenId.ULTIMATE_OSCILLATOR);
					else
						return token(ScanTokenId.ERROR);

				case 'v': // volume
					if ((c = input.read()) == 'o'
						&& (c = input.read()) == 'l'
						&& (c = input.read()) == 'u'
						&& (c = input.read()) == 'm'
						&& (c = input.read()) == 'e')
					{
						if (tradeValueFlag)
						{
							tradeValueFlag = false;
							return token(ScanTokenId.VOLUME_VAR);
						}
						
						return parseNoParamsIndicator(ScanTokenId.VOLUME);
					}
					else
						return token(ScanTokenId.ERROR);

				// Rest of lowercase letters starting identifiers
				case 'd': case 'g': case 'i': case 'j': case 'k':
				case 'n': case 'q': case 't': case 'w': case 'x':
				case 'y': case 'z':
				// Uppercase letters starting identifiers
				case 'A': case 'B': case 'C': case 'D': case 'E':
                case 'F': case 'G': case 'H': case 'I': case 'J':
                case 'K': case 'L': case 'M': case 'N': case 'O':
                case 'P': case 'Q': case 'R': case 'S': case 'T':
                case 'U': case 'V': case 'W': case 'X': case 'Y':
                case 'Z':
				case '$':
					//return finishIdentifier();
					return token(ScanTokenId.ERROR);

				// All Character.isWhitespace(c) below 0x80 follow
				// ['\t' - '\r'] and [0x1c - ' ']
				case '\t': case '\n': case 0x0b: case '\f': case '\r':
				case 0x1c: case 0x1d: case 0x1e: case 0x1f:
					return finishWhitespace();
				case ' ':
					c = input.read();
					if (c == EOF || !Character.isWhitespace(c))
					{
						input.backup(1);
						return tokenFactory.getFlyweightToken(ScanTokenId.WHITESPACE, " ");
					}
					return finishWhitespace();

				case EOF:
					return null;

				default:
					if (c >= 0x80)
					{
						c = translateSurrogates(c);
						if (Character.isJavaIdentifierStart(c))
							return finishIdentifier();
						if (Character.isWhitespace(c))
							return finishWhitespace();
					}

					// Invalid char
					return token(ScanTokenId.ERROR);
			} // end of switch (c)
		} // end of while(true)
	}

	private int translateSurrogates(int c)
	{
        if (Character.isHighSurrogate((char)c))
		{
            int lowSurr = input.read();
            if (lowSurr != EOF && Character.isLowSurrogate((char)lowSurr))
			{
                // c and lowSurr form the integer unicode char.
                c = Character.toCodePoint((char)c, (char)lowSurr);
            }
			else
			{
                // Otherwise it's error: Low surrogate does not follow the high one.
                // Leave the original character unchanged.
                // As the surrogates do not belong to any
                // specific unicode category the lexer should finally
                // categorize them as a lexical error.
                input.backup(1);
            }
        }
        return c;
    }

	private Token<ScanTokenId> finishWhitespace() {
        while (true)
		{
            int c = input.read();
            // There should be no surrogates possible for whitespace
            // so do not call translateSurrogates()
            if (c == EOF || !Character.isWhitespace(c))
			{
                input.backup(1);
                return tokenFactory.createToken(ScanTokenId.WHITESPACE);
            }
        }
    }

	private Token<ScanTokenId> finishIdentifier()
	{
        return finishIdentifier(input.read());
    }

    private Token<ScanTokenId> finishIdentifier(int c)
	{
        while (true)
		{
            if (c == EOF || !Character.isJavaIdentifierPart(c = translateSurrogates(c))) {
                // For surrogate 2 chars must be backed up
                input.backup((c >= Character.MIN_SUPPLEMENTARY_CODE_POINT) ? 2 : 1);
                return tokenFactory.createToken(ScanTokenId.IDENTIFIER);
            }
            c = input.read();
        }
    }

	private Token<ScanTokenId> keywordOrIdentifier(ScanTokenId keywordId)
	{
        return keywordOrIdentifier(keywordId, input.read());
    }

	private Token<ScanTokenId> keywordOrIdentifier(ScanTokenId keywordId, int c)
	{
        // Check whether the given char is non-ident and if so then return keyword
        if (c == EOF || !Character.isJavaIdentifierPart(c = translateSurrogates(c)))
		{
            // For surrogate 2 chars must be backed up
            input.backup((c >= Character.MIN_SUPPLEMENTARY_CODE_POINT) ? 2 : 1);
            return token(keywordId);
        }
		else // c is identifier part
            return finishIdentifier();
    }

	private Token<ScanTokenId> finishNumberLiteral(int c, boolean inFraction) {
        while (true)
		{
            switch (c)
			{
                case '.':
                    if (!inFraction)
					{
                        inFraction = true;
                    }
					else // two dots in the literal
					{
                        return token(ScanTokenId.FLOAT_LITERAL_INVALID);
                    }
                    break;
                case 'l': case 'L': // 0l or 0L
                    return token(ScanTokenId.ERROR);
                case 'd': case 'D':
                    return token(ScanTokenId.ERROR);
                case 'f': case 'F':
                    return token(ScanTokenId.ERROR);
                case '0': case '1': case '2': case '3': case '4':
                case '5': case '6': case '7': case '8': case '9':
                    break;
                case 'e': case 'E': // exponent part
                    return token(ScanTokenId.ERROR);
                default:
                    input.backup(1);
                    return token(inFraction ? ScanTokenId.DOUBLE_LITERAL : ScanTokenId.INT_LITERAL);
            }
            c = input.read();
        }
    }

	private Token<ScanTokenId> token(ScanTokenId id)
	{
		String fixedText = id.fixedText();
		return (fixedText != null)
			? tokenFactory.getFlyweightToken(id, fixedText)
			: tokenFactory.createToken(id);
	}

	@Override
	public void release()
	{}

	private String readUntilNextOpenParenthesis()
	{
		StringBuilder sb = new StringBuilder();
		do
		{
			int c = input.read();
			if (c == EOF)
				return sb.toString();
			sb.append((char) c);
			if (c == '(')
				return sb.toString();
		}
		while (true);
	}

	private String readUntilNextParenthesis()
	{
		StringBuilder sb = new StringBuilder();
		int nParen = 1;
		do
		{
			int c = input.read();
			if (c == EOF)
				return sb.toString();
			sb.append((char) c);
			switch (c)
			{
				case '(':
					nParen++;
					break;
				case ')':
					nParen--;
					break;
			}
			if (nParen == 0)
				return sb.toString();
		}
		while (true);
	}

	private String readUntilNextBraket()
	{
		StringBuilder sb = new StringBuilder();
		int nBraket = 1;
		do
		{
			int c = input.read();
			if (c == EOF)
				return sb.toString();
			sb.append((char) c);
			switch (c)
			{
				case '[':
					nBraket++;
					break;
				case ']':
					nBraket--;
					break;
			}
			if (nBraket == 0)
				return sb.toString();
		}
		while (true);
	}

	private Token<ScanTokenId> parseNoParamsIndicator(ScanTokenId tokenId)
	{
		if (input.read() == '('
			&& input.read() == ')')
		{
			if (input.read() == '[')
				if (!parseExtraArgument())
					return token(ScanTokenId.ERROR);

			input.backup(3);
			return token(tokenId);
		}
		else
		{
			input.backup(2);
			return token(ScanTokenId.ERROR);
		}
	}

	private Token<ScanTokenId> parseParamsIndicator(ScanTokenId tokenId, boolean hasTradeValue, int nParams)
	{
		String space = readUntilNextOpenParenthesis();
		
		if (space.endsWith("("))
		{
			String params = readUntilNextParenthesis();

			if (!parseParams(params, hasTradeValue, nParams))
				return token(ScanTokenId.ERROR);

			if (!params.endsWith(")"))
				return token(ScanTokenId.ERROR);

			if (input.read() == '[')
				if (!parseExtraArgument())
					return token(ScanTokenId.ERROR);

			input.backup(params.length() + space.length() + 1);
			tradeValueFlag = hasTradeValue;
			return token(tokenId);
		}
		else
		{
			input.backup(space.length());
			return token(ScanTokenId.ERROR);
		}
	}

	private boolean parseParams(String string, boolean hasTradeValue, int nParams)
	{
		String params = string;

		if (params.endsWith(")"))
			params = params.substring(0, params.length() - 1);

		params = params.replace(" ", "");
		
		String[] paramList = params.split(",");

		if (paramList.length != nParams)
			return false;

		if (hasTradeValue)
			if (!isTradeValue(paramList[0]))
				return false;

		for (int i = (hasTradeValue ? 1 : 0); i < nParams; i++)
			if (!isInteger(paramList[i]))
				return false;

		return true;
	}

	private boolean parseExtraArgument()
	{
		String string = readUntilNextBraket();

		if (!string.startsWith("-"))
			return false;

		if (!isNegativeInteger(string))
			return false;

		if (!string.endsWith("]"))
			return false;

		input.backup(string.length());
		
		return true;
	}

	private boolean isNegativeInteger(String text)
	{
		String number = text.substring(0, text.length() - 1);

		if (!number.startsWith("-"))
			return false;

		number = number.replace("-", "");

		if (number.equals("0"))
			return false;

		return number.matches("^\\d+$");
	}

	private boolean isInteger(String text)
	{
		if (text.contains(".") || text.contains(","))
			return false;

		return text.matches("^\\d+$");
	}

	private static Pattern TRADE_VALUE = Pattern.compile("open|close|high|low|volume|spread");
	private boolean isTradeValue(String string)
	{
		return TRADE_VALUE.matcher(string).matches();
	}

}
