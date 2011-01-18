package org.chartsy.updatecenter;

import java.io.File;
import java.io.IOException;
import org.openide.filesystems.FileUtil;
import org.openide.modules.ModuleInstall;
import org.openide.util.RequestProcessor;
import org.openide.windows.WindowManager;

public class Installer extends ModuleInstall
{

    public static final RequestProcessor PROCESSOR
            = new RequestProcessor("Chartsy Version Updater", 1);

    @Override
    public void restored()
    {
        WindowManager.getDefault().invokeWhenUIReady(new Runnable()
        {
            public void run()
            {
                String folder = System.getProperty("user.home") + File.separator + "Chartsy" + File.separator + "downloads";
                File file = new File(folder);
                if (!file.exists()) {
                    try { FileUtil.createFolder(file); }
                    catch (IOException ex) {}
                }
                VersionChecker.checker();
            }
        });
    }

}
