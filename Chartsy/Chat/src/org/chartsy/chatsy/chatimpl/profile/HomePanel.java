package org.chartsy.chatsy.chatimpl.profile;

import java.awt.Color;
import java.awt.Component;
import java.awt.FocusTraversalPolicy;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.chartsy.chatsy.chat.component.focus.SpecifiedOrderFocusTraversalPolicy;

public class HomePanel extends JPanel
{

	private JLabel streetLabel = new JLabel("Street");
    private JLabel cityLabel = new JLabel("City");
    private JLabel stateLabel = new JLabel("State");
    private JLabel zipCodeLabel = new JLabel("Zip");
    private JLabel countryLabel = new JLabel("Country");
	private JLabel phoneLabel = new JLabel("Phone");
    private JLabel faxLabel = new JLabel("Fax");
    private JLabel pagerLabel = new JLabel("Pager");
    private JLabel mobileLabel = new JLabel("Mobile");

    private JTextField cityField = new JTextField();
    private JTextField stateField = new JTextField();
    private JTextField zipCodeField = new JTextField();
    private JTextField countryField = new JTextField();
    private JTextField streetField = new JTextField();
    private JTextField phoneField = new JTextField();
    private JTextField faxField = new JTextField();
    private JTextField pagerField = new JTextField();
    private JTextField mobileField = new JTextField();
    private JTextField webPageField = new JTextField();

    public HomePanel()
	{
        this.setLayout(new GridBagLayout());
		this.setBackground(Color.WHITE);

        this.add(streetLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
        this.add(streetField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 0, 0));

        this.add(cityLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
        this.add(cityField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 0, 0));

        this.add(stateLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
        this.add(stateField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 0, 0));

        this.add(zipCodeLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
        this.add(zipCodeField, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 0, 0));

        this.add(countryLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
        this.add(countryField, new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 0, 0));

        this.add(phoneLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
		this.add(phoneField, new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 0, 0));

        this.add(faxLabel, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
		this.add(faxField, new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 0, 0));

        this.add(pagerLabel, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
		this.add(pagerField, new GridBagConstraints(3, 2, 1, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 0, 0));

        this.add(mobileLabel, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
        this.add(mobileField, new GridBagConstraints(3, 3, 1, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 0, 0));

        final Component order[] = new Component[]
		{
			streetField, cityField, stateField,
			zipCodeField,countryField, phoneField,
			faxField, pagerField, mobileField,
		};
        FocusTraversalPolicy policy = new SpecifiedOrderFocusTraversalPolicy(order);
        setFocusTraversalPolicy(policy);
        setFocusTraversalPolicyProvider(true); 
    }

    public void setStreetAddress(String address)
	{
        streetField.setText(address);
    }

    public String getStreetAddress()
	{
        return streetField.getText();
    }

    public void setCity(String city)
	{
        cityField.setText(city);
    }

    public String getCity()
	{
        return cityField.getText();
    }

    public void setState(String state)
	{
        stateField.setText(state);
    }

    public String getState()
	{
        return stateField.getText();
    }

    public void setZipCode(String zip)
	{
        zipCodeField.setText(zip);
    }

    public String getZipCode()
	{
        return zipCodeField.getText();
    }

    public void setCountry(String country)
	{
        countryField.setText(country);
    }

    public String getCountry()
	{
        return countryField.getText();
    }

    public void setPhone(String phone)
	{
        phoneField.setText(phone);
    }

    public String getPhone()
	{
        return phoneField.getText();
    }

    public void setFax(String fax)
	{
        faxField.setText(fax);
    }

    public String getFax()
	{
        return faxField.getText();
    }

    public void setPager(String pager)
	{
        pagerField.setText(pager);
    }

    public String getPager()
	{
        return pagerField.getText();
    }

    public void setMobile(String mobile)
	{
        mobileField.setText(mobile);
    }

    public String getMobile()
	{
        return mobileField.getText();
    }

    public void setWebPage(String webPage)
	{
        webPageField.setText(webPage);
    }

    public String getWebPage()
	{
        return webPageField.getText();
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