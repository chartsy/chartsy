package org.chartsy.main.features;

import java.awt.Component;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CallableSystemAction;

/**
 *
 * @author Viorel
 */
public final class FeatureAction extends CallableSystemAction
{

	public void performAction()
	{
		// do nothing
	}

	public String getName()
	{
		return "Feature Banners";
	}

	public HelpCtx getHelpCtx()
	{
		return HelpCtx.DEFAULT_HELP;
	}

	public @Override Component getToolbarPresenter()
	{
		return FeaturesPanel.getDefault();
	}

	protected @Override boolean asynchronous()
	{
		return false;
	}

}
