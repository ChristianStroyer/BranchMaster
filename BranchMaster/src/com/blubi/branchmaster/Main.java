package com.blubi.branchmaster;

import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;

import com.blubi.branchmaster.commandline.Git_Exists;

import external.au.edu.ausstage.utils.CommandLineParser;
import external.com.centerkey.utils.BareBonesBrowserLaunch;

public class Main {

	final static String version = "0.9";
	final static int DEFAULT_PORT = 8101;
	
	public static boolean stop = false;

    public static void main(String args[]){
    	
    	System.out.println("-----------------------");
    	System.out.println("BranchMaster v."+version);
    	System.out.println("-----------------------");
    	System.out.println();
    	CommandLineParser cli = new CommandLineParser(args);
    	
    	String[] validArgs = {"homedir","nolaunch","branches","port","gitdir","?","help"};
    	List<String> validArgsList = Arrays.asList(validArgs);   
    	
    	boolean printHelp = false;
    	
    	Set<String> keySet = cli.getKeySet();
    	for(String key:keySet)
    		if(!validArgsList.contains(key)) {
    			System.out.println("ERROR: Invalid argument: "+key);
    			System.out.println();
    			printHelp = true;
    		}
    	
    	if(cli.containsKey("?") || cli.containsKey("help"))
    		printHelp=true;
    	
    	if(printHelp) 
    	{
        	System.out.println("A git branch visualizer, for showing the connection between selected branches.");
        	System.out.println("(author: Christian Strøyer - 2014)");
        	System.out.println();
    		System.out.println("Valid arguments:");
			System.out.println("--help or --?                                 :  This help text");
			System.out.println("--homedir <dir>                               :  The directory where the git repository is located");
			System.out.println("--nolaunch                                    :  Don't launch browser");
			System.out.println("--port                                        :  Port for webserver");
			System.out.println("--branches <branch>[,<branch>,...<branch>]    :  NOT IMPLEMENTED! Initial branch list");
			System.out.println("--gitdir <dir>                                :  NOT IMPLEMENTED! Location of git, if not on path");    			
			return;
    	}
    	
    	File homedir = null;    	
    	try {
    		if(cli.containsKey("homedir"))
    			homedir = new File(cli.getValue("homedir"));
    		else
				homedir = new File(".").getCanonicalFile();

    		if(!homedir.isDirectory())
    			throw new RuntimeException("Not a directory: --homedir "+homedir.getCanonicalPath());
			
    	} catch (IOException e) {
				throw new RuntimeException(e);				
			}

    	boolean git_Exists = new Git_Exists(homedir).execute();
    	if(!git_Exists)
    		throw new RuntimeException("Can't find git in path");
    	
    	
    	int port = DEFAULT_PORT;
    	if(cli.containsKey("port"))
    		port = Integer.parseInt(cli.getValue("port")); 
    	
    	CustomHTTPD.startUp(port,homedir);
    	
    	String branches = "";
    	if(cli.containsKey("branches")) {
    		branches=cli.getValue("branches");
    	}
    	
   	 	GUI gui = new GUI() {public void stopButtonEvent() {stop = true;}};
		JFrame frame = gui.drawAll();
		frame.setVisible(true);
    	
    	if(!cli.containsKey("nolaunch"))
    		BareBonesBrowserLaunch.openURL("http://localhost:"+CustomHTTPD.port+"/BranchMaster.html"+(branches.equals("")?"":"?branches="+branches));

		 while(!stop)
		 {
			 try {
				Thread.sleep(500);
			} catch (InterruptedException e) {	}
		 }
    	
    	System.out.println("Stopping");
    	CustomHTTPD.stopNow();
    	frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }
    
    public static void debuglog(String message) {
    	System.out.println(message);
    }
    
    public static void log(String message) {
    	System.err.println(message);
    }

}
