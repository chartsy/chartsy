package org.chartsy.updatecenter;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

/**
 *
 * @author Viorel
 */
public class DownloadManager extends javax.swing.JDialog implements Observer
{

    public DownloadManager(java.awt.Frame parent, boolean modal)
    {
        super(parent, modal);
        initComponents();
        initGUI();
        setIconImage(WindowManager.getDefault().getMainWindow().getIconImage());
        setSize(800, 600);
    }

    private void initGUI()
    {
        model = new DownloadTableModel();
        table = new JTable(model);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                tableSelectionChanged();
            }
        });
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ProgressRenderer renderer = new ProgressRenderer(0, 100);
        renderer.setStringPainted(true);
        table.setDefaultRenderer(JProgressBar.class, renderer);

        table.setRowHeight((int) renderer.getPreferredSize().getHeight());

        JPanel downloadsPanel = new JPanel();
        downloadsPanel.setBorder(BorderFactory.createTitledBorder("Downloads"));
        downloadsPanel.setLayout(new BorderLayout());
        downloadsPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel();

        pauseBtn = new JButton("Pause");
        pauseBtn.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                actionPause();
            }
        });
        pauseBtn.setEnabled(false);
        buttonsPanel.add(pauseBtn);

        resumeBtn = new JButton("Resume");
        resumeBtn.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                actionResume();
            }
        });
        resumeBtn.setEnabled(false);
        buttonsPanel.add(resumeBtn);

        cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                actionCancel();
            }
        });
        cancelBtn.setEnabled(false);
        buttonsPanel.add(cancelBtn);

        clearBtn = new JButton("Clear");
        clearBtn.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                actionClear();
            }
        });
        clearBtn.setEnabled(false);
        buttonsPanel.add(clearBtn);

        exeBtn = new JButton("Open");
        exeBtn.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                actionExe();
            }
        });
        exeBtn.setEnabled(false);
        buttonsPanel.add(exeBtn);

        String folder = System.getProperty("user.home")
                + File.separator
                + "Chartsy"
                + File.separator
                + "downloads";
        
        JLabel label = new JLabel(
                "<html>After the download is completed, select the file then "+
                "click open to start the installer.<br>If the installer does "+
                "not start, go to this folder "+folder+" and launch it."+
                "<br>Note: For mac users, go to this folder " + folder + " and "+
                "launch the installer.</html>", JLabel.CENTER);

        getContentPane().setLayout(new SpringLayout());
        getContentPane().add(downloadsPanel);
        getContentPane().add(label);
        getContentPane().add(buttonsPanel);

        SpringUtilities.makeCompactGrid(getContentPane(),
                3, 1,
                5, 5,
                5, 5);
    }

    private void tableSelectionChanged()
    {
        if (selectedDownload != null)
            selectedDownload.deleteObserver(DownloadManager.this);

        if (!clearing)
        {
            selectedDownload = model.getDownload(table.getSelectedRow());
            selectedDownload.addObserver(DownloadManager.this);
            updateButtons();
        }
    }

    public void actionAdd(Download download)
    {
        model.addDownload(download);
        updateButtons();
    }

    private void actionPause()
    {
        selectedDownload.pause();
        updateButtons();
    }

    private void actionResume()
    {
        selectedDownload.resume();
        updateButtons();
    }

    private void actionCancel()
    {
        selectedDownload.cancel();
        updateButtons();
    }

    private void actionClear()
    {
        clearing = true;
        model.clearDownload(table.getSelectedRow());
        clearing = false;
        selectedDownload = null;
        updateButtons();
    }

    private void actionExe()
    {
        if (selectedDownload != null)
        {
            int status = selectedDownload.getStatus();
            if (status == Download.COMPLETE)
            {
                try
                {
                    Process process = null;
                    if (isWindows())
                    {
                        process = Runtime.getRuntime().exec(
                            selectedDownload.getFilePath(
                            selectedDownload.getUrl()));
                    }
                    else if (isUnix())
                    {
                        process = Runtime.getRuntime().exec("sh " +
                            selectedDownload.getFilePath(
                            selectedDownload.getUrl()));
                    }
                }
                catch (IOException ex)
                {}
            }
        }
    }

    private void updateButtons()
    {
        if (selectedDownload != null)
        {
            int status = selectedDownload.getStatus();
            switch (status)
            {
                case Download.DOWNLOADING:
                    pauseBtn.setEnabled(true);
                    resumeBtn.setEnabled(false);
                    cancelBtn.setEnabled(true);
                    clearBtn.setEnabled(false);
                    exeBtn.setEnabled(false);
                    break;
                case Download.PAUSED:
                    pauseBtn.setEnabled(false);
                    resumeBtn.setEnabled(true);
                    cancelBtn.setEnabled(true);
                    clearBtn.setEnabled(false);
                    exeBtn.setEnabled(false);
                    break;
                case Download.ERROR:
                    pauseBtn.setEnabled(false);
                    resumeBtn.setEnabled(true);
                    cancelBtn.setEnabled(false);
                    clearBtn.setEnabled(true);
                    exeBtn.setEnabled(false);
                    break;
                default:
                    pauseBtn.setEnabled(false);
                    resumeBtn.setEnabled(false);
                    cancelBtn.setEnabled(false);
                    clearBtn.setEnabled(true);
                    exeBtn.setEnabled(true);
            }
        }
        else
        {
            pauseBtn.setEnabled(false);
            resumeBtn.setEnabled(false);
            cancelBtn.setEnabled(false);
            clearBtn.setEnabled(false);
        }
    }

    public void update(Observable o, Object arg)
    {
        if (selectedDownload != null
                && selectedDownload.equals(o))
            updateButtons();
    }

    public static boolean canDownload(String features)
    {
        NotifyDescriptor descriptor = new NotifyDescriptor.Confirmation(
                NbBundle.getMessage(DownloadManager.class, "MSG_OpenDownloadManager", features),
                "Open Download Manager",
                NotifyDescriptor.YES_NO_OPTION);
        Object retval = DialogDisplayer.getDefault().notify(descriptor);
        if (retval.equals(NotifyDescriptor.YES_OPTION))
            return true;
        else
            return false;
    }

    public static String getOS()
    {
        if (isWindows())
            return "windows";
        else if (isMac())
            return "macos";
        else
            return "linux";
    }

    public static String getExtension()
    {
        if (isWindows())
            return ".exe";
        else if (isMac())
            return ".dmg";
        else
            return ".sh";
    }

    public static boolean isWindows()
    {
        return (OS.indexOf("win") >= 0);
    }

    public static boolean isMac()
    {
        return (OS.indexOf("mac") >= 0);
    }

    public static boolean isUnix()
    {
        return (OS.indexOf("nix") >= 0
                || OS.indexOf("nux") >= 0);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(DownloadManager.class, "DownloadManager.title")); // NOI18N
        setIconImage(null);
        setLocationByPlatform(true);
        setResizable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[])
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                DownloadManager dialog
                        = new DownloadManager(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter()
                {
                    public void windowClosing(java.awt.event.WindowEvent e)
                    {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    private static String OS
            = System.getProperty("os.name").toLowerCase();

    private DownloadTableModel model;
    private JTable table;
    private JButton pauseBtn, resumeBtn, cancelBtn, clearBtn, exeBtn;

    private Download selectedDownload;
    private boolean clearing;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
