package com.blubi.branchmaster;	

import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingConstants;

import external.com.centerkey.utils.BareBonesBrowserLaunch;
	
public abstract class GUI {
	
	 public static void main(String[] args) throws URISyntaxException {
		 GUI gui = new GUI() {public void stopButtonEvent() {System.out.println("STOP!");};};
		 JFrame frame = gui.drawAll();
		 frame.setVisible(true);
		  }
	 
	 public JFrame drawAll() {
		  JFrame frame = new JFrame("BranchMaster Server");
		    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    frame.setSize(600, 300);
		    Container container = frame.getContentPane();
		    container.setLayout(new GridBagLayout());
		    GridBagConstraints gbc = new GridBagConstraints();
		    gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(2, 2, 2, 2);
		    
		    container.add(linkButton(),gbc);
		    gbc.gridy++;
		    gbc.gridy++;
		    gbc.gridy++;
		    container.add(stopButton(),gbc);
		    return frame;		 
	 }
	 
	 private JButton linkButton()  {
		    final String url = "http://localhost:"+CustomHTTPD.port+"/BranchMaster.html";
			
		    class OpenUrlAction implements ActionListener {
		      @Override public void actionPerformed(ActionEvent e) {
		        BareBonesBrowserLaunch.openURL(url);
		      }
		    }
		    
		    JButton button = new JButton();
		    button.setText("<HTML>Launching browser for: <FONT color=\"#000099\"><U>"+url+"</U></FONT>"
		        + "</HTML>");
		    button.setHorizontalAlignment(SwingConstants.LEFT);
		    button.setBorderPainted(false);
		    button.setOpaque(false);
		    button.setBackground(Color.WHITE);
		    button.setToolTipText(url);
		    button.addActionListener(new OpenUrlAction());
		    return button;
	 }
	 	 
	 private JButton stopButton()  {
		 
	    class StopAction implements ActionListener {
		      @Override public void actionPerformed(ActionEvent e) {
		    	  stopButtonEvent();
		      }
		    }
		 
		 JButton button = new JButton("Stop Server");
		 button.addActionListener(new StopAction());
		 return button;
	 }
	 
	 public abstract void stopButtonEvent();
	 
	 
	}

