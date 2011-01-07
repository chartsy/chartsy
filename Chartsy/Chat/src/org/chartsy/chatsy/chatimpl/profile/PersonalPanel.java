package org.chartsy.chatsy.chatimpl.profile;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PersonalPanel extends JPanel
{

	private JLabel firstNameLabel = new JLabel("First Name");
	private JLabel middleNameLabel = new JLabel("Middle Name");
	private JLabel lastNameLabel = new JLabel("Last Name");
	private JLabel nicknameLabel = new JLabel("Nickname");
	private JLabel emaiAddressLabel = new JLabel("Email Address");
	private JLabel jidLabel = new JLabel("JID");

    private JTextField firstNameField = new JTextField();
    private JTextField middleNameField = new JTextField();
    private JTextField lastNameField = new JTextField();
    private JTextField nicknameField = new JTextField();
    private JTextField emailAddressField = new JTextField();
    private JTextField jidField = new JTextField();

    public PersonalPanel()
	{
        setLayout(new GridBagLayout());
		setBackground(Color.WHITE);

        add(firstNameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
        add(firstNameField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 0, 0));

        add(middleNameLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
        add(middleNameField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 0, 0));

        add(lastNameLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
        add(lastNameField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 0, 0));

        add(nicknameLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
        add(nicknameField, new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 0, 0));

        add(emaiAddressLabel, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
        add(emailAddressField, new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 0, 0));

        add(jidLabel, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
        add(jidField, new GridBagConstraints(1, 6, 1, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 0, 0));

        jidLabel.setVisible(false);
        jidField.setVisible(false);
    }

    public String getFirstName()
	{
        return firstNameField.getText();
    }

    public void setFirstName(String firstName)
	{
        firstNameField.setText(firstName);
    }

    public void setMiddleName(String middleName)
	{
        middleNameField.setText(middleName);
    }

    public String getMiddleName()
	{
        return middleNameField.getText();
    }

    public void setLastName(String lastName)
	{
        lastNameField.setText(lastName);
    }

    public String getLastName()
	{
        return lastNameField.getText();
    }

    public void setNickname(String nickname)
	{
        nicknameField.setText(nickname);
    }

    public String getNickname()
	{
        return nicknameField.getText();
    }

    public void setEmailAddress(String emailAddress)
	{
        emailAddressField.setText(emailAddress);
    }

    public String getEmailAddress()
	{
        return emailAddressField.getText();
    }

    public void focus()
	{
        firstNameField.requestFocus();
    }

    public void setJID(String jid)
	{
        jidField.setText(jid);
    }

    public void showJID(boolean show)
	{
        jidLabel.setVisible(show);
        jidField.setVisible(show);
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

}
