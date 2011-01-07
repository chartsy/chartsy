package org.chartsy.chatsy.chatimpl.profile;

import org.chartsy.chatsy.chat.util.GraphicUtils;
import org.chartsy.chatsy.chat.util.SwingWorker;
import org.chartsy.chatsy.chat.util.URLFileSystem;
import org.chartsy.chatsy.chat.util.log.Log;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

public class AvatarPanel extends JPanel implements ActionListener
{
	
    private JLabel avatar;
    private byte[] bytes;
    private File avatarFile;
    final JButton browseButton = new JButton("Browse");
    final JButton clearButton = new JButton("Clear");
    private FileDialog fileChooser;
    private Dialog dlg;

    public AvatarPanel()
	{
        setLayout(new GridBagLayout());
		setBackground(Color.WHITE);

        final JLabel photo = new JLabel("Avatar:");

        avatar = new JLabel();

        add(photo, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
        add(avatar, new GridBagConstraints(1, 0, 1, 2, 1.0, 1.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
        add(browseButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
        add(clearButton, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));

        browseButton.addActionListener(this);
        clearButton.addActionListener(new ActionListener()
		{
            public void actionPerformed(ActionEvent actionEvent)
			{
                avatar.setIcon(null);
                bytes = null;
                avatarFile = null;
                avatar.setBorder(null);
            }
        });
        avatar.setText("No avatar found");
        GraphicUtils.makeSameSize(browseButton, clearButton);
    }

    public void setEditable(boolean editable)
	{
        browseButton.setVisible(editable);
        clearButton.setVisible(editable);
    }

    public void setAvatar(ImageIcon icon)
	{
        avatar.setBorder(BorderFactory.createBevelBorder(0, Color.white, Color.lightGray));
        if (icon.getIconHeight() > 64 || icon.getIconWidth() > 64)
		{
            avatar.setIcon(new ImageIcon(icon.getImage().getScaledInstance(-1, 64, Image.SCALE_SMOOTH)));
        }
        else
		{
            avatar.setIcon(icon);
        }
        avatar.setText("");
    }

    public void setAvatarBytes(byte[] bytes)
	{
        this.bytes = bytes;
    }

    public byte[] getAvatarBytes()
	{
        return bytes;
    }

    public Icon getAvatar()
	{
        return avatar.getIcon();
    }

    public File getAvatarFile()
	{
        return avatarFile;
    }

    public void actionPerformed(ActionEvent e)
	{
        initFileChooser();
        fileChooser.setVisible(true);
        if (fileChooser.getDirectory() != null 
			&& fileChooser.getFile() != null)
		{
            File file = new File(fileChooser.getDirectory(), fileChooser.getFile());
            String suffix = URLFileSystem.getSuffix(file);
            if (suffix.toLowerCase().equals(".jpeg") 
				|| suffix.toLowerCase().equals(".gif")
				|| suffix.toLowerCase().equals(".jpg")
				|| suffix.toLowerCase().equals(".png"))
			{
                changeAvatar(file, this);
            }
            else
			{
                JOptionPane.showMessageDialog(
					this,
					"Please choose a valid image file.",
					"Error",
					JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void changeAvatar(final File selectedFile, final Component parent)
	{
        SwingWorker worker = new SwingWorker()
		{
            public Object construct()
			{
                try
				{
                    ImageIcon imageOnDisk = new ImageIcon(selectedFile.getCanonicalPath());
                    Image avatarImage = imageOnDisk.getImage();
                    if (avatarImage.getHeight(null) > 96 
						|| avatarImage.getWidth(null) > 96)
                        avatarImage = avatarImage.getScaledInstance(-1, 96, Image.SCALE_SMOOTH);
                    return avatarImage;
                }
                catch (IOException ex)
				{
                    Log.error(ex);
                }
                return null;
            }

            public void finished()
			{
                Image avatarImage = (Image)get();
                setAvatar(new ImageIcon(avatarImage));
                avatarFile = selectedFile;
            }
        };

        worker.start();
    }

    public class ImageFilter implements FilenameFilter 
	{

        public final String jpeg = "jpeg";
        public final String jpg = "jpg";
        public final String gif = "gif";
        public final String png = "png";

        public boolean accept(File f, String string)
		{
            if (f.isDirectory())
                return true;

            String extension = getExtension(f);
            if (extension != null)
			{
                if (extension.equals(gif) 
					|| extension.equals(jpeg)
					||extension.equals(jpg)
					|| extension.equals(png))
				{
                    return true;
                }
                else
				{
                    return false;
                }
            }

            return false;
        }

        public String getExtension(File f)
		{
            String ext = null;
            String s = f.getName();
            int i = s.lastIndexOf('.');
            if (i > 0 && i < s.length() - 1)
                ext = s.substring(i + 1).toLowerCase();
            return ext;
        }

        public String getDescription()
		{
            return "*.JPEG, *.GIF, *.PNG";
        }
		
    }

    public void allowEditing(boolean allowEditing)
	{
        Component[] comps = getComponents();
        if (comps != null)
		{
            final int no = comps.length;
            for (int i = 0; i < no; i++)
			{
                Component comp = comps[i];
                if (comp instanceof JTextField)
                    ((JTextField)comp).setEditable(allowEditing);
            }
        }
    }

    public void initFileChooser()
	{
        if (fileChooser == null)
		{
            fileChooser = new FileDialog(dlg, "Choose Avatar", FileDialog.LOAD);
            fileChooser.setFilenameFilter(new ImageFilter());
        }
    }

    public void setParentDialog(Dialog dialog)
	{
        this.dlg = dialog;
    }

}


