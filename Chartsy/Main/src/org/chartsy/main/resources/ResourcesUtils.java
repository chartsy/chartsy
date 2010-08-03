package org.chartsy.main.resources;

import java.awt.Image;
import javax.swing.ImageIcon;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author viorel.gheba
 */
public final class ResourcesUtils
{

    private ResourcesUtils()
	{}

    public static Image getImage(final String name)
    {
        return ImageUtilities.loadImage(
			NbBundle.getMessage(ResourcesUtils.class, "ICON_Template", name),
			true);
    }

    public static Image getImage16(final String name)
    {
        return ImageUtilities.loadImage(
			NbBundle.getMessage(ResourcesUtils.class, "ICON_Template_16", name),
			true);
    }

    public static Image getImage24(final String name)
    {
        return ImageUtilities.loadImage(
			NbBundle.getMessage(ResourcesUtils.class, "ICON_Template_24", name),
			true);
    }

    public static ImageIcon getIcon(final String name)
    {
        return ImageUtilities.loadImageIcon(
			NbBundle.getMessage(ResourcesUtils.class, "ICON_Template", name), 
			true);
    }

    public static ImageIcon getIcon16(final String name)
    {
        return ImageUtilities.loadImageIcon(
			NbBundle.getMessage(ResourcesUtils.class, "ICON_Template_16", name), 
			true);
    }

    public static ImageIcon getIcon24(final String name)
    {
        return ImageUtilities.loadImageIcon(
			NbBundle.getMessage(ResourcesUtils.class, "ICON_Template_24", name), 
			true);
    }

    public static ImageIcon getLogo()
    {
        return ImageUtilities.loadImageIcon(
			NbBundle.getMessage(ResourcesUtils.class, "ICON_Logo"), 
			true);
    }

	public static ImageIcon getFavoritesIcon()
	{
		return ImageUtilities.loadImageIcon(
			NbBundle.getMessage(ResourcesUtils.class, "ICON_Favorites"),
			true);
	}

	public static ImageIcon getFavoritesBigIcon()
	{
		return ImageUtilities.loadImageIcon(
			NbBundle.getMessage(ResourcesUtils.class, "ICON_BIG_Favorites"),
			true);
	}

	public static ImageIcon getImageIcon(String name, boolean small)
	{
		if (small)
			return ImageUtilities.loadImageIcon(
				NbBundle.getMessage(ResourcesUtils.class, "ICON_Template_16", name),
				true);
		else
			return ImageUtilities.loadImageIcon(
				NbBundle.getMessage(ResourcesUtils.class, "ICON_Template_24", name),
				true);
	}

}
