package org.chartsy.chatsy.chatimpl.plugin.gateways;

import org.chartsy.chatsy.chatimpl.plugin.gateways.transports.Transport;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TransportItem extends JPanel
{
	
	private JLabel iconLabel;
    private JLabel titleLabel;
    private JLabel descriptionLabel;
    private JLabel activeLabel;
    private String transportAddress;
    private Transport transport;

    public TransportItem(Transport transport, boolean active, String address)
	{
        setLayout(new GridBagLayout());

        iconLabel = new JLabel();
        titleLabel = new JLabel();
        descriptionLabel = new JLabel();
        activeLabel = new JLabel();
		
        iconLabel.setIcon(transport.getIcon());
        titleLabel.setText(transport.getTitle());
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        descriptionLabel.setText(transport.getName());
        descriptionLabel.setFont(new Font("Dialog", Font.PLAIN, 11));
        descriptionLabel.setForeground(Color.lightGray);
        descriptionLabel.setHorizontalTextPosition(JLabel.LEFT);
        descriptionLabel.setHorizontalAlignment(JLabel.LEFT);
        activeLabel.setFont(new Font("Dialog", Font.PLAIN, 10));

        if (active)
		{
            activeLabel.setText("Active");
            activeLabel.setForeground(Color.green);
        }
        else
		{
            activeLabel.setText("Not registred");
            activeLabel.setForeground(Color.GRAY);
        }

        this.transportAddress = address;
        this.transport = transport;
		
        buildUI();
    }

    private void buildUI()
	{
        add(iconLabel, new GridBagConstraints(0, 0, 1, 2, 0.0, 0.0, 
			GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
			new Insets(0, 10, 0, 0), 0, 0));
        add(titleLabel, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(0, 5, 0, 0), 0, 0));
        add(descriptionLabel, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(0, 5, 2, 0), 0, 0));
        add(activeLabel, new GridBagConstraints(1, 2, 1, 2, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(0, 5, 0, 0), 0, 0));
    }

    public String getTransportAddress()
	{
        return transportAddress;
    }

    public void setTransportAddress(String transportAddress)
	{
        this.transportAddress = transportAddress;
    }

    public Transport getTransport()
	{
        return transport;
    }
	
}
