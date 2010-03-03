package org.chartsy.main;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.prefs.Preferences;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.chartsy.main.utils.DesktopUtil;
import org.openide.util.NbPreferences;
import org.openide.windows.WindowManager;

/**
 *
 * @author viorel.gheba
 */
public class RegisterForm extends javax.swing.JDialog {

    public RegisterForm(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setTitle("Register");
        parent.setIconImage(WindowManager.getDefault().getMainWindow().getIconImage());
        getRootPane().setDefaultButton(btnRegister);
        initForm();
    }

    private void initForm() {
        lblTop.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                try { DesktopUtil.browse("http://www.mrswing.com/amember/signup.php"); }
                catch (Exception ex) {}
            }
            public void mouseEntered(MouseEvent e) { setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); }
            public void mouseExited(MouseEvent e) { setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlMain = new javax.swing.JPanel();
        lblTop = new javax.swing.JLabel();
        lblUsername = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        lblMessage = new javax.swing.JLabel();
        btnRegister = new javax.swing.JButton();
        btnRemind = new javax.swing.JButton();
        txtPassword = new javax.swing.JPasswordField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        lblTop.setText(org.openide.util.NbBundle.getMessage(RegisterForm.class, "RegisterForm.lblTop.text")); // NOI18N

        lblUsername.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblUsername.setText(org.openide.util.NbBundle.getMessage(RegisterForm.class, "RegisterForm.lblUsername.text")); // NOI18N

        txtUsername.setText(org.openide.util.NbBundle.getMessage(RegisterForm.class, "RegisterForm.txtUsername.text")); // NOI18N

        lblPassword.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblPassword.setText(org.openide.util.NbBundle.getMessage(RegisterForm.class, "RegisterForm.lblPassword.text")); // NOI18N

        lblMessage.setText(org.openide.util.NbBundle.getMessage(RegisterForm.class, "RegisterForm.lblMessage.text")); // NOI18N

        btnRegister.setText(org.openide.util.NbBundle.getMessage(RegisterForm.class, "RegisterForm.btnRegister.text")); // NOI18N
        btnRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegisterActionPerformed(evt);
            }
        });

        btnRemind.setText(org.openide.util.NbBundle.getMessage(RegisterForm.class, "RegisterForm.btnRemind.text")); // NOI18N
        btnRemind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemindActionPerformed(evt);
            }
        });

        txtPassword.setText(org.openide.util.NbBundle.getMessage(RegisterForm.class, "RegisterForm.txtPassword.text")); // NOI18N

        org.jdesktop.layout.GroupLayout pnlMainLayout = new org.jdesktop.layout.GroupLayout(pnlMain);
        pnlMain.setLayout(pnlMainLayout);
        pnlMainLayout.setHorizontalGroup(
            pnlMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlMainLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblMessage, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .add(lblTop, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .add(pnlMainLayout.createSequentialGroup()
                        .add(btnRegister)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(btnRemind))
                    .add(pnlMainLayout.createSequentialGroup()
                        .add(pnlMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblUsername)
                            .add(lblPassword))
                        .add(28, 28, 28)
                        .add(pnlMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(txtUsername, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, txtPassword, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE))))
                .addContainerGap())
        );
        pnlMainLayout.setVerticalGroup(
            pnlMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlMainLayout.createSequentialGroup()
                .addContainerGap()
                .add(lblTop, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(pnlMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblUsername)
                    .add(txtUsername, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(pnlMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblPassword)
                    .add(txtPassword, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(lblMessage, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 42, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(pnlMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnRegister)
                    .add(btnRemind))
                .addContainerGap(42, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlMain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlMain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRemindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemindActionPerformed
        setVisible(false);
    }//GEN-LAST:event_btnRemindActionPerformed

    private void btnRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegisterActionPerformed
        String username = txtUsername.getText();
        String pass = new String(txtPassword.getPassword());
        if (username.equals("")) {
            lblMessage.setText("<html>Username field is empty!</html>");
            return;
        }
        if (pass.equals("")) {
            lblMessage.setText("<html>Password field is empty!</html>");
            return;
        }
        String password = getMD5(pass);
        if (password == null) return;
        try {
            HttpClient client = new HttpClient();
            HttpMethod method = new PostMethod("http://www.chartsy.org/checkregistration.php?username=" + username + "&password=" + password);
            client.executeMethod(method);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
            if (bufferedReader != null) {
                String firstLine = bufferedReader.readLine();
                if (firstLine.equals("OK")) {
                    String name = bufferedReader.readLine();
                    Preferences p = NbPreferences.root().node("/org/chartsy/register");
                    p.put("registred", "true");
                    p.put("name", name);
                    p.put("date", String.valueOf(new Date().getTime()));
                    p.put("username", username);
                    p.put("password", pass);
                    lblMessage.setText(name + ", thank you for the registration.");
                    btnRegister.setVisible(false);
                    btnRemind.setText("Close");
                } else {
                    lblMessage.setText("Error, could not register. Check your username and password.");
                }
            }
            method.releaseConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }//GEN-LAST:event_btnRegisterActionPerformed

    private String getMD5(String password) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(password.getBytes());
            BigInteger hash = new BigInteger(1, md5.digest());
            return hash.toString();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                RegisterForm dialog = new RegisterForm(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnRegister;
    private javax.swing.JButton btnRemind;
    private javax.swing.JLabel lblMessage;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblTop;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables

}
