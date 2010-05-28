package org.chartsy.main.intro.content;

import org.openide.util.NbBundle;

/**
 *
 * @author viorel.gheba
 */
public class BundleSupport {

    private static final String BUNDLE_NAME = "org.chartsy.main.intro.Bundle";
    private static final String LABEL_PREFIX = "LBL_";
    private static final String CONTENT_PREFIX = "CONTENT_";
    private static final String URL_PREFIX = "URL_";

    private BundleSupport() {}

    public static String getLabel(String s) {
        return NbBundle.getBundle(BUNDLE_NAME).getString((new StringBuilder()).append(LABEL_PREFIX).append(s).toString());
    }

    public static String getContent(String s) {
        return NbBundle.getBundle(BUNDLE_NAME).getString((new StringBuilder()).append(CONTENT_PREFIX).append(s).toString());
    }

    public static String getURL(String s) {
        return NbBundle.getBundle(BUNDLE_NAME).getString((new StringBuilder()).append(URL_PREFIX).append(s).toString());
    }

}
