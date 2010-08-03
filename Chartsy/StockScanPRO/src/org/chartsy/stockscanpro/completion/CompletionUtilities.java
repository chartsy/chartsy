package org.chartsy.stockscanpro.completion;

import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.chartsy.stockscanpro.lexer.api.ScanTokenId;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.lexer.InputAttributes;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.LanguagePath;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Viorel
 */
public final class CompletionUtilities
{

	private CompletionUtilities()
	{}

	public static TokenSequence<ScanTokenId> getScanTokenSequence(final TokenHierarchy hierarchy, final int offset)
	{
        if (hierarchy != null)
		{
            TokenSequence<?> ts = hierarchy.tokenSequence();
            
			while(ts != null && (offset == 0 || ts.moveNext()))
			{
                ts.move(offset);

                if (ts.language() == ScanTokenId.language())
                    return (TokenSequence<ScanTokenId>)ts;

                if (!ts.moveNext() && !ts.movePrevious())
                    return null;

                ts = ts.embedded();
            }
        }
		
        return null;
    }

	public static boolean isScanContext(final JTextComponent component, final int offset)
	{
		Document doc = component.getDocument();
		if (doc instanceof AbstractDocument)
			((AbstractDocument)doc).readLock();

		try
		{
			if (doc.getLength() == 0 && "text/x-dialog-binding".equals(doc.getProperty("mimeType")))
			{
				InputAttributes attributes = (InputAttributes) doc.getProperty(InputAttributes.class);
				LanguagePath path = LanguagePath.get(MimeLookup.getLookup("text/x-dialog-binding").lookup(Language.class));
				Document d = (Document) attributes.getValue(path, "dialogBinding.document");
				if (d != null)
					return "text/x-scan".equals(NbEditorUtilities.getMimeType(d));
				FileObject fo = (FileObject)attributes.getValue(path, "dialogBinding.fileObject");
				return "text/x-scan".equals(fo.getMIMEType());
			}

			TokenSequence<ScanTokenId> ts = getScanTokenSequence(TokenHierarchy.get(doc), offset);

			if (ts == null)
				return false;

			if (!ts.moveNext() && !ts.movePrevious())
				return true;

			if (offset == ts.offset())
				return true;

			switch(ts.token().id())
			{
				case DOUBLE_LITERAL:
					if (ts.token().text().charAt(0) == '.')
						break;
				case FLOAT_LITERAL:
				case FLOAT_LITERAL_INVALID:
				case INT_LITERAL:
				case LONG_LITERAL:
					return false;
			}

			return true;
		}
		finally
		{
			if (doc instanceof AbstractDocument)
				((AbstractDocument) doc).readUnlock();
		}
	}

}
