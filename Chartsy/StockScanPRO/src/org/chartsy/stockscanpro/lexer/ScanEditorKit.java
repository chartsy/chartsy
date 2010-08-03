package org.chartsy.stockscanpro.lexer;

import javax.swing.text.Document;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.SyntaxSupport;
import org.netbeans.editor.ext.ExtSyntaxSupport;
import org.netbeans.modules.editor.NbEditorKit;

/**
 *
 * @author Viorel
 */
public class ScanEditorKit extends NbEditorKit
{

	public ScanEditorKit()
	{
		super();
	}

	public static ScanEditorKit create()
	{
		return new ScanEditorKit();
	}

	@Override
	public String getContentType()
	{
		return "text/x-scan"; // NOI18N
	}

	@Override
	public Document createDefaultDocument()
	{
		return super.createDefaultDocument();
	}

	@Override
	public SyntaxSupport createSyntaxSupport(final BaseDocument bdoc)
	{
		return new ExtSyntaxSupport(bdoc) {};
	}

}
