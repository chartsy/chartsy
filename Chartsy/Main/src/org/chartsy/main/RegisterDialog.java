package org.chartsy.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.chartsy.main.utils.XMLUtils;
import org.openide.windows.WindowManager;

/**
 *
 * @author viorelgheba
 */
public class RegisterDialog extends javax.swing.JDialog {

    static final String[] browsers = { "firefox", "opera", "konqueror", "epiphany", "seamonkey", "galeon", "kazehakase", "mozilla", "netscape" };

    public RegisterDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        initForm();
        getRootPane().setDefaultButton(btnRegister);
        parent.setIconImage(WindowManager.getDefault().getMainWindow().getIconImage());
    }

    private void initForm() {
        btnClose.setVisible(false);
        editor.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    openURL(e.getURL().toString());
                }
            }
        });
    }

    private void openURL(String url) {
        String osName = System.getProperty("os.name");
        try {
            if (osName.startsWith("Max OS")) {
                Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
                Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[] {String.class});
                openURL.invoke(null, new Object[] {url});
            } else if (osName.startsWith("Windows")) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else {
                boolean found = false;
                for (String browser : browsers)
                    if (!found) {
                        found = Runtime.getRuntime().exec( new String[] {"which", browser}).waitFor() == 0;
                        if (found) Runtime.getRuntime().exec(new String[] {browser, url});
                    }
                if (!found) throw new Exception(Arrays.toString(browsers));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error attempting to launch web browser\n" + e.toString());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        editor = new javax.swing.JEditorPane();
        lblUser = new javax.swing.JLabel();
        txtUser = new javax.swing.JTextField();
        lblPass = new javax.swing.JLabel();
        txtPass = new javax.swing.JPasswordField();
        lblResponce = new javax.swing.JLabel();
        btnClose = new javax.swing.JButton();
        btnRegister = new javax.swing.JButton();
        btnLater = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        editor.setBackground(new java.awt.Color(240, 240, 240));
        editor.setBorder(null);
        editor.setContentType(org.openide.util.NbBundle.getMessage(RegisterDialog.class, "RegisterDialog.editor.contentType")); // NOI18N
        editor.setEditable(false);
        editor.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        editor.setText(org.openide.util.NbBundle.getMessage(RegisterDialog.class, "RegisterDialog.editor.text")); // NOI18N
        editor.setRequestFocusEnabled(false);
        jScrollPane1.setViewportView(editor);

        lblUser.setFont(new java.awt.Font("Dialog", 1, 11));
        lblUser.setText(org.openide.util.NbBundle.getMessage(RegisterDialog.class, "RegisterDialog.lblUser.text")); // NOI18N

        txtUser.setFont(new java.awt.Font("Dialog", 0, 11));
        txtUser.setText(org.openide.util.NbBundle.getMessage(RegisterDialog.class, "RegisterDialog.txtUser.text")); // NOI18N

        lblPass.setFont(new java.awt.Font("Dialog", 1, 11));
        lblPass.setText(org.openide.util.NbBundle.getMessage(RegisterDialog.class, "RegisterDialog.lblPass.text")); // NOI18N

        txtPass.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        txtPass.setText(org.openide.util.NbBundle.getMessage(RegisterDialog.class, "RegisterDialog.txtPass.text")); // NOI18N

        lblResponce.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        lblResponce.setText(org.openide.util.NbBundle.getMessage(RegisterDialog.class, "RegisterDialog.lblResponce.text")); // NOI18N
        lblResponce.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        btnClose.setFont(new java.awt.Font("Dialog", 0, 11));
        btnClose.setText(org.openide.util.NbBundle.getMessage(RegisterDialog.class, "RegisterDialog.btnClose.text")); // NOI18N
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        btnRegister.setFont(new java.awt.Font("Dialog", 0, 11));
        btnRegister.setText(org.openide.util.NbBundle.getMessage(RegisterDialog.class, "RegisterDialog.btnRegister.text")); // NOI18N
        btnRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegisterActionPerformed(evt);
            }
        });

        btnLater.setFont(new java.awt.Font("Dialog", 0, 11));
        btnLater.setText(org.openide.util.NbBundle.getMessage(RegisterDialog.class, "RegisterDialog.btnLater.text")); // NOI18N
        btnLater.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLaterActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblUser)
                            .add(lblPass))
                        .add(18, 18, 18)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(txtPass, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                            .add(txtUser, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)))
                    .add(lblResponce, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(btnClose)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnRegister)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnLater)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblUser)
                    .add(txtUser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtPass, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblPass))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(lblResponce, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnClose)
                    .add(btnRegister)
                    .add(btnLater))
                .addContainerGap(60, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        setVisible(false);
}//GEN-LAST:event_btnCloseActionPerformed

    private void btnRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegisterActionPerformed
        String user = txtUser.getText();
        String pass = new String(txtPass.getPassword());
        String password = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(new String(txtPass.getPassword()).getBytes());
            BigInteger hash = new BigInteger(1, md5.digest());
            password = hash.toString(16);
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        try {
            URL url = new URL("http://www.chartsy.org/checkregistration.php?username=" + user + "&password=" + password);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            if (br != null) {
                String firstLine = br.readLine();
                if (firstLine.equals("OK")) {
                    String name = br.readLine();
                    XMLUtils.register(name, user, pass);
                    lblResponce.setText(name + ", thank you for the registration.");
                    btnLater.setVisible(false);
                    btnRegister.setVisible(false);
                    btnClose.setVisible(true);
                } else {
                    lblResponce.setText("Error, could not register. Check your username and password.");
                }
            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException io) {
            io.printStackTrace();
        }
}//GEN-LAST:event_btnRegisterActionPerformed

    private void btnLaterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLaterActionPerformed
        setVisible(false);
}//GEN-LAST:event_btnLaterActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                RegisterDialog dialog = new RegisterDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnLater;
    private javax.swing.JButton btnRegister;
    private javax.swing.JEditorPane editor;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblPass;
    private javax.swing.JLabel lblResponce;
    private javax.swing.JLabel lblUser;
    private javax.swing.JPasswordField txtPass;
    private javax.swing.JTextField txtUser;
    // End of variables declaration//GEN-END:variables

}
