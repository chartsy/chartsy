package org.chartsy.main.options;

import org.chartsy.main.managers.FacebookManager;
import org.openide.util.NbPreferences;

final class FacebookPanel extends javax.swing.JPanel {

    private final FacebookOptionsPanelController controller;

    public FacebookPanel(FacebookOptionsPanelController controller) {
        this.controller = controller;
        initComponents();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        emailLbl = new javax.swing.JLabel();
        emailTxt = new javax.swing.JTextField();
        passLbl = new javax.swing.JLabel();
        passTxt = new javax.swing.JPasswordField();
        loginCkb = new javax.swing.JCheckBox();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(FacebookPanel.class, "FacebookPanel.jPanel1.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(emailLbl, org.openide.util.NbBundle.getMessage(FacebookPanel.class, "FacebookPanel.emailLbl.text")); // NOI18N

        emailTxt.setText(org.openide.util.NbBundle.getMessage(FacebookPanel.class, "FacebookPanel.emailTxt.text")); // NOI18N
        emailTxt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                emailTxtKeyTyped(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(passLbl, org.openide.util.NbBundle.getMessage(FacebookPanel.class, "FacebookPanel.passLbl.text")); // NOI18N

        passTxt.setText(org.openide.util.NbBundle.getMessage(FacebookPanel.class, "FacebookPanel.passTxt.text")); // NOI18N
        passTxt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                passTxtKeyTyped(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(loginCkb, org.openide.util.NbBundle.getMessage(FacebookPanel.class, "FacebookPanel.loginCkb.text")); // NOI18N
        loginCkb.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                loginCkbStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(passLbl)
                            .addComponent(emailLbl))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(passTxt)
                            .addComponent(emailTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)))
                    .addComponent(loginCkb))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(emailLbl)
                    .addComponent(emailTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(passLbl)
                    .addComponent(passTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(loginCkb))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void emailTxtKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_emailTxtKeyTyped
        controller.changed();
    }//GEN-LAST:event_emailTxtKeyTyped

    private void passTxtKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_passTxtKeyTyped
        controller.changed();
    }//GEN-LAST:event_passTxtKeyTyped

    private void loginCkbStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_loginCkbStateChanged
        controller.changed();
    }//GEN-LAST:event_loginCkbStateChanged

    void load() {
        emailTxt.setText(NbPreferences.forModule(FacebookManager.class).get("facebook_email", ""));
        passTxt.setText(NbPreferences.forModule(FacebookManager.class).get("facebook_pass", ""));
        loginCkb.setSelected(NbPreferences.forModule(FacebookManager.class).getBoolean("facebook_auto_login", false));
    }

    void store() {
        NbPreferences.forModule(FacebookManager.class).put("facebook_email", emailTxt.getText().replace(" ", ""));
        NbPreferences.forModule(FacebookManager.class).put("facebook_pass", new String(passTxt.getPassword()));
        NbPreferences.forModule(FacebookManager.class).putBoolean("facebook_auto_login", loginCkb.isSelected());
    }

    boolean valid() {
        // TODO check whether form is consistent and complete
        return true;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel emailLbl;
    private javax.swing.JTextField emailTxt;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JCheckBox loginCkb;
    private javax.swing.JLabel passLbl;
    private javax.swing.JPasswordField passTxt;
    // End of variables declaration//GEN-END:variables
}
