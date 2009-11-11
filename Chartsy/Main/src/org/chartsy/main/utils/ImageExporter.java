package org.chartsy.main.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.chartsy.main.chartsy.ChartPanel;
import org.chartsy.main.managers.LoggerManager;
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


    public static ImageExporter getDefault() {
        if (instance == null) instance = new ImageExporter();
        return instance;
    }

    private ImageExporter() {}

    public void export(ChartPanel chartPanel) {
        if (chartPanel != null) {
            try {
                if (fileChooser == null) {
                    fileChooser = new JFileChooser();
                    fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
                    fileChooser.setDialogTitle("Export Image");
                    fileChooser.setAcceptAllFileFilterUsed(true);
                    fileChooser.setCurrentDirectory(defaultFolder);

                    jpegFilter = new FileFilter() {
                        public boolean accept(File file) {
                            String filename = file.getName();
                            return file.isDirectory() || filename.endsWith(".jpeg") || filename.endsWith(".jpg");
                        }
                        public String getDescription() {
                            return "JPEG (*.jpeg *.jpg)";
                        }
                    };
                    fileChooser.addChoosableFileFilter(jpegFilter);

                    pngFilter = new FileFilter() {
                        public boolean accept(File file) {
                            String filename = file.getName();
                            return file.isDirectory() || filename.endsWith(".png");
                        }
                        public String getDescription() {
                            return "PNG (*.png)";
                        }
                    };
                    fileChooser.addChoosableFileFilter(pngFilter);
                }

                fileChooser.setSelectedFile(new File("Untitled"));
                defaultFolder = fileChooser.getCurrentDirectory();

                if (fileChooser.showSaveDialog(WindowManager.getDefault().getMainWindow()) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    String filename = file.getName();
                    BufferedImage image = chartPanel.getBufferedImage(chartPanel.getWidth(), chartPanel.getHeight());
                    FileFilter fileFilter = fileChooser.getFileFilter();

                    String format;
                    if (fileFilter == jpegFilter) {
                        if (!filename.endsWith(".jpeg") && !filename.endsWith(".jpg")) {
                            file = new File(file.getAbsolutePath() + ".jpg");
                        }
                        format = "jpg";
                    } else {
                        if (!filename.endsWith(".png")) {
                            file = new File(file.getAbsolutePath() + ".png");
                        }
                        format = "png";
                    }
                    ImageIO.write(image, format, file);
                }
            } catch (IOException ex) {
                LoggerManager.getDefault().log(ex);
            }
        }
    }

}
