package org.chartsy.main.utils;

import java.io.File;
import javax.swing.filechooser.FileFilter;



/**
 *
 * @author viorel.gheba
 */
public class ExtensionFileFilter extends FileFilter {

    private String description;

    private String extension;

    public ExtensionFileFilter(final String description, final String extension) {
        this.description = description;
        this.extension = extension;
    }

    public boolean accept(final File file) {
        if (file.isDirectory()) {
            return true;
        }
        final String name = file.getName().toLowerCase();
        if (name.endsWith(this.extension)) {
            return true;
        }
        else {
            return false;
        }
    }

    public String getDescription() {
        return this.description;
    }

}
