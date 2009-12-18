package org.chartsy.main;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.chartsy.main.utils.XMLUtils;
import org.openide.windows.WindowManager;

/**
 *
 * @author viorelgheba
 */
public class RegisterDialog extends javax.swing.JDialog {

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
                    try {
                        Desktop.getDesktop().browse(new URI(e.getURL().toString()));
                    } catch (IOException io) {
                        io.printStackTrace();
                    } catch (URISyntaxException uri) {
                        uri.printStackTrace();
                    }
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblUser = new javax.swing.JLabel();
        lblPass = new javax.swing.JLabel();
        txtUser = new javax.swing.JTextField();
        txtPass = new javax.swing.JPasswordField();
        btnRegister = new javax.swing.JButton();
        btnLater = new javax.swing.JButton();
        lblResponce = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        editor = new javax.swing.JEditorPane();
        btnClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(RegisterDialog.class, "RegisterDialog.title")); // NOI18N
        setAlwaysOnTop(true);
        setLocationByPlatform(true);
        setName("RegisterDialog"); // NOI18N
        setResizable(false);

        lblUser.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        lblUser.setText(org.openide.util.NbBundle.getMessage(RegisterDialog.class, "RegisterDialog.lblUser.text")); // NOI18N

        lblPass.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        lblPass.setText(org.openide.util.NbBundle.getMessage(RegisterDialog.class, "RegisterDialog.lblPass.text")); // NOI18N

        txtUser.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        txtUser.setText(org.openide.util.NbBundle.getMessage(RegisterDialog.class, "RegisterDialog.txtUser.text")); // NOI18N

        txtPass.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        txtPass.setText(org.openide.util.NbBundle.getMessage(RegisterDialog.class, "RegisterDialog.txtPass.text")); // NOI18N

        btnRegister.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        btnRegister.setText(org.openide.util.NbBundle.getMessage(RegisterDialog.class, "RegisterDialog.btnRegister.text")); // NOI18N
        btnRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegisterActionPerformed(evt);
            }
        });

        btnLater.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        btnLater.setText(org.openide.util.NbBundle.getMessage(RegisterDialog.class, "RegisterDialog.btnLater.text")); // NOI18N
        btnLater.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLaterActionPerformed(evt);
            }
        });

        lblResponce.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        lblResponce.setText(org.openide.util.NbBundle.getMessage(RegisterDialog.class, "RegisterDialog.lblResponce.text")); // NOI18N
        lblResponce.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane1.setViewportBorder(null);

        editor.setBorder(null);
        editor.setContentType(org.openide.util.NbBundle.getMessage(RegisterDialog.class, "RegisterDialog.editor.contentType")); // NOI18N
        editor.setEditable(false);
        editor.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        editor.setText(org.openide.util.NbBundle.getMessage(RegisterDialog.class, "RegisterDialog.editor.text")); // NOI18N
        editor.setRequestFocusEnabled(false);
        jScrollPane1.setViewportView(editor);

        btnClose.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        btnClose.setText(org.openide.util.NbBundle.getMessage(RegisterDialog.class, "RegisterDialog.btnClose.text")); // NOI18N
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                            .addComponent(lblResponce, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblUser)
                                    .addComponent(lblPass))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtPass, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                                    .addComponent(txtUser, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(btnClose)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRegister)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnLater)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUser)
                    .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPass))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblResponce, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClose)
                    .addComponent(btnRegister)
                    .addComponent(btnLater))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLaterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLaterActionPerformed
        setVisible(false);
    }//GEN-LAST:event_btnLaterActionPerformed

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

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        setVisible(false);
    }//GEN-LAST:event_btnCloseActionPerformed

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
