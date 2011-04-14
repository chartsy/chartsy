package org.chartsy.main.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.chartsy.main.ChartFrame;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbPreferences;
import org.openide.windows.WindowManager;

/**
 *
 * @author viorel.gheba
 */
public class ImageExporter {
    
    private static ImageExporter instance;
    private File defaultFolder = null;
    private JFileChooser fileChooser;
    private FileFilter jpegFilter;
    private FileFilter pngFilter;


    public static ImageExporter getDefault()
    {
        if (instance == null)
            instance = new ImageExporter();
        return instance;
    }

    private ImageExporter()
    {}

    public void export(ChartFrame chartFrame)
    {
        JComponent chartPanel = chartFrame.getMainPanel();
        if (chartPanel != null)
        {
            try
            {
                Preferences p = NbPreferences.root().node("/org/chartsy/imageexporter");
                String folder = p.get("default_folder", null);
                if (folder != null) defaultFolder = new File(folder);
                else defaultFolder = null;
                if (fileChooser == null) {
                    fileChooser = new JFileChooser();
                    fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
                    fileChooser.setDialogTitle("Export Image");
                    fileChooser.setAcceptAllFileFilterUsed(true);
                    fileChooser.setCurrentDirectory(defaultFolder);

                    jpegFilter = new FileFilter()
                    {
                        public boolean accept(File file)
                        {
                            String filename = file.getName();
                            return file.isDirectory() || filename.endsWith(".jpeg") || filename.endsWith(".jpg");
                        }
                        public String getDescription()
                        {
                            return "JPEG (*.jpeg *.jpg)";
                        }
                    };
                    fileChooser.addChoosableFileFilter(jpegFilter);

                    pngFilter = new FileFilter()
                    {
                        public boolean accept(File file)
                        {
                            String filename = file.getName();
                            return file.isDirectory() || filename.endsWith(".png");
                        }
                        public String getDescription()
                        {
                            return "PNG (*.png)";
                        }
                    };
                    fileChooser.addChoosableFileFilter(pngFilter);
                }

                fileChooser.setSelectedFile(new File(chartFrame.getChartData().getStock().getKey()));
                defaultFolder = fileChooser.getCurrentDirectory();
                p.put("default_folder", fileChooser.getCurrentDirectory().getAbsolutePath());

                if (fileChooser.showSaveDialog(WindowManager.getDefault().getMainWindow()) == JFileChooser.APPROVE_OPTION)
                {
                    File file = fileChooser.getSelectedFile();
                    String filename = file.getName();
                    BufferedImage image = chartFrame.getBufferedImage(chartPanel.getWidth(), chartPanel.getHeight());
                    FileFilter fileFilter = fileChooser.getFileFilter();

                    String format;
                    if (fileFilter == jpegFilter)
                    {
                        if (!filename.endsWith(".jpeg") && !filename.endsWith(".jpg"))
                            file = new File(file.getAbsolutePath() + ".jpg");
                        format = "jpg";
                    } 
                    else
                    {
                        if (!filename.endsWith(".png"))
                            file = new File(file.getAbsolutePath() + ".png");
                        format = "png";
                    }
                    
                    Object retval = NotifyDescriptor.YES_OPTION;
                    if (file.exists())
                    {
                        NotifyDescriptor d = new NotifyDescriptor.Confirmation(
                            "This file already exists. Do you want to overwrite it?",
                            "Overwrite",
                            NotifyDescriptor.YES_NO_OPTION);
                        retval = DialogDisplayer.getDefault().notify(d);
                    }

                    if (retval.equals(NotifyDescriptor.YES_OPTION))
                        ImageIO.write(image, format, file);
                    else
                        export(chartFrame);
                }
            } 
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
    }

    public File getExportedFile(ChartFrame chartFrame, Class resource) {
        File file = null;
        JComponent chartPanel = chartFrame.getMainPanel();
        if (chartPanel != null)
        {
            try
            {
                String format = "png";
                file = File.createTempFile("tmp_chart", format);
                BufferedImage image = chartFrame.getBufferedImage(chartPanel.getWidth(), chartPanel.getHeight());
                ImageIO.write(image, format, file);
            } catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
        return file;
    }

}
