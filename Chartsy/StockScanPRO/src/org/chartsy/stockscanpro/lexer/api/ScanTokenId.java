package org.chartsy.stockscanpro.lexer.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import org.chartsy.stockscanpro.lexer.ScanLexer;
import org.netbeans.api.lexer.InputAttributes;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.LanguagePath;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.spi.lexer.LanguageEmbedding;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 *
 * @author Viorel
 */
public enum ScanTokenId implements TokenId
{

	ERROR							(null, "error"),
	IDENTIFIER						(null, "identifier"),

	AND								("and", "keyword"),
	OR								("or", "keyword"),

	OPEN_VAR						("open", "literal"),
	CLOSE_VAR						("close", "literal"),
	HIGH_VAR						("high", "literal"),
	LOW_VAR							("low", "literal"),
	VOLUME_VAR						("volume", "literal"),
	SPREAD_VAR						("spread", "literal"),

	// Price
	OPEN							("open", "indicator"),
	CLOSE							("close", "indicator"),
	HIGH							("high", "indicator"),
	LOW								("low", "indicator"),
	VOLUME							("volume", "indicator"),
	SPREAD							("spread", "indicator"),
	BODY_TOP						("body_top", "indicator"),
	BODY_BOTTOM						("body_bottom", "indicator"),
	MIN								("min", "indicator"),
	MAX								("max", "indicator"),

	// Bollinger Bands
	BOLLINGER_UPPER					("bollinger_upper", "indicator"),
	BOLLINGER_LOWER					("bollinger_lower", "indicator"),
	BOLLINGER_MIDDLE				("bollinger_middle", "indicator"),

	// Commodity Channel Index
	CCI								("cci", "indicator"),
	CCI_EMA							("cci_ema", "indicator"),

	// Directional Movement Index
	ADX								("adx", "indicator"),
	PDI								("pdi", "indicator"),
	MDI								("mdi", "indicator"),

	// Moving Average
	SMA								("sma", "indicator"),
	EMA								("ema", "indicator"),

	// Average True Range
	ATR								("atr", "indicator"),

	// Force Index
	FORCE_INDEX						("force_index", "indicator"),

	// MACD, Moving Average Convergence/Divergence
	MACD							("macd", "indicator"),
	MACD_SIGNAL						("macd_signal", "indicator"),
	MACD_HISTOGRAM					("macd_histogram", "indicator"),

	// Money Flow
	MONEY_FLOW						("money_flow", "indicator"),
	MONEY_FLOW_AVG					("money_flow_avg", "indicator"),

	// On Balance Volume
	ON_BALANCE_VOLUME				("on_balance_volume", "indicator"),

	// Relative Strength Index
	RSI								("rsi", "indicator"),

	// Full Stochastics
	STO_K							("sto_k", "indicator"),
	STO_D							("sto_d", "indicator"),

	// Ultimate Oscillator
	ULTIMATE_OSCILLATOR				("ultimate_oscillator", "indicator"),

	// Williams %R
	PCTR							("pctr", "indicator"),

	INT_LITERAL						(null, "number"),
    LONG_LITERAL					(null, "number"),
    FLOAT_LITERAL					(null, "number"),
    DOUBLE_LITERAL					(null, "number"),

	LPAREN							("(", "separator"),
	RPAREN							(")", "separator"),
	LBRACKET						("[", "separator"),
	RBRACKET						("]", "separator"),
	COMMA							(",", "separator"),
	US								("_", "separator"),

	EQ								("=", "operator"),
	GT								(">", "operator"),
	LT								("<", "operator"),
	LTEQ							("<=", "operator"),
	GTEQ							(">=", "operator"),
	PLUS							("+", "operator"),
	MINUS							("-", "operator"),
	STAR							("*", "operator"),
	SLASH							("/", "operator"),

	WHITESPACE						(null, "whitespace"),
	FLOAT_LITERAL_INVALID			(null, "number");

	private final String fixedText;
	private final String primaryCategory;

	ScanTokenId(String fixedText, String primaryCategory)
	{
		this.fixedText = fixedText;
		this.primaryCategory = primaryCategory;
	}

	public String fixedText()
	{
		return this.fixedText;
	}

	public String primaryCategory()
	{
		return this.primaryCategory;
	}

	private static final Language<ScanTokenId> language = new LanguageHierarchy<ScanTokenId>()
	{

		@Override
        protected String mimeType()
		{
            return "text/x-scan";
        }

		@Override
        protected Collection<ScanTokenId> createTokenIds()
		{
            return EnumSet.allOf(ScanTokenId.class);
        }

        @Override
        protected Map<String,Collection<ScanTokenId>> createTokenCategories()
		{
            Map<String,Collection<ScanTokenId>> cats = new HashMap<String,Collection<ScanTokenId>>();

			// Additional literals being a lexical error
            cats.put("error", EnumSet.of(ScanTokenId.FLOAT_LITERAL_INVALID));

            // Literals category
            EnumSet<ScanTokenId> nums = EnumSet.of(
                ScanTokenId.INT_LITERAL,
                ScanTokenId.LONG_LITERAL,
                ScanTokenId.FLOAT_LITERAL,
                ScanTokenId.DOUBLE_LITERAL
            );
            cats.put("number", nums);

			EnumSet<ScanTokenId> kwds = EnumSet.of(
				ScanTokenId.AND,
				ScanTokenId.OR
			);
			cats.put("keyword", kwds);

			EnumSet<ScanTokenId> inds = EnumSet.of(
				ScanTokenId.ADX,
				ScanTokenId.ATR,
				ScanTokenId.BODY_BOTTOM,
				ScanTokenId.BODY_TOP,
				ScanTokenId.BOLLINGER_LOWER,
				ScanTokenId.BOLLINGER_MIDDLE,
				ScanTokenId.BOLLINGER_UPPER,
				ScanTokenId.CCI,
				ScanTokenId.CCI_EMA,
				ScanTokenId.CLOSE,
				ScanTokenId.EMA,
				ScanTokenId.FORCE_INDEX,
				ScanTokenId.HIGH,
				ScanTokenId.LOW,
				ScanTokenId.MACD,
				ScanTokenId.MACD_HISTOGRAM,
				ScanTokenId.MACD_SIGNAL,
				ScanTokenId.MAX,
				ScanTokenId.MDI,
				ScanTokenId.MIN,
				ScanTokenId.MONEY_FLOW,
				ScanTokenId.MONEY_FLOW_AVG,
                ScanTokenId.OPEN,
				ScanTokenId.ON_BALANCE_VOLUME,
				ScanTokenId.PCTR,
				ScanTokenId.PDI,
				ScanTokenId.RSI,
				ScanTokenId.SMA,
				ScanTokenId.SPREAD, 
				ScanTokenId.STO_D,
				ScanTokenId.STO_K,
				ScanTokenId.ULTIMATE_OSCILLATOR,
				ScanTokenId.VOLUME
            );
            cats.put("indicator", inds);

			EnumSet<ScanTokenId> lits = EnumSet.of(
				ScanTokenId.CLOSE_VAR,
				ScanTokenId.HIGH_VAR,
				ScanTokenId.LOW_VAR,
				ScanTokenId.OPEN_VAR,
				ScanTokenId.SPREAD_VAR,
				ScanTokenId.VOLUME_VAR
			);
			cats.put("literal", lits);

            return cats;
        }

        @Override
        protected Lexer<ScanTokenId> createLexer(LexerRestartInfo<ScanTokenId> info)
		{
            return new ScanLexer(info);
        }

        @Override
        protected LanguageEmbedding<?> embedding(
        Token<ScanTokenId> token, LanguagePath languagePath, InputAttributes inputAttributes)
		{
            return null; // No embedding
        }

	}.language();

	public static Language<ScanTokenId> language()
	{
		return language;
	}

}
