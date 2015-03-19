package com.blubi.branchmaster.commandline;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

import com.blubi.branchmaster.Main;

public abstract class AbstractCommandLineRunner {

	private static long stat_total_ms = 0;
	private static long stat_total_calls = 0;
	
	File homedir = null;
		
	public AbstractCommandLineRunner(File homedir) {
		this.homedir = homedir;
	}

	public static int run2(String command) {
		try {
			ByteArrayOutputStream stdout = new ByteArrayOutputStream();
		    PumpStreamHandler psh = new PumpStreamHandler(stdout);
		    CommandLine cl = CommandLine.parse(command);
		    DefaultExecutor exec = new DefaultExecutor();
		    exec.setStreamHandler(psh);
		    int exitValue = exec.execute(cl);
		    System.out.println(stdout.toString());
		    return exitValue;	    	
		} catch (Exception e)  {
			System.out.println(e.getMessage());
			//return handleException(e);
			return -1;
		}
	}

	public static void main(String[] args) {
		run2("git --hel");
	}
	
	protected int run(String command) {
		try {
			long startTime = System.currentTimeMillis();			
	        Process p = Runtime.getRuntime().exec(command,null, this.homedir);
	        int exitCode = p.waitFor();
	        long runTime = System.currentTimeMillis()-startTime;
	        stat_total_ms += runTime;
	        stat_total_calls++;
	        Main.debuglog("RUN("+runTime+"ms): "+command+ " in ["+ this.homedir.getAbsolutePath() +"]");
	        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
	        String line; 
	        while ((line = input.readLine()) != null) {
	        	handle(line);
	        }
	        
	        BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
	        while ((line = error.readLine()) != null) {
	        	handleErr(line);	    
	        }
	        
	        return exitCode;

		} catch (IOException e)  {
			return handleException(e);
		} catch (InterruptedException e) {
			return handleException(e);
		}		
	}
	
	protected void handle(String line) {
        System.out.println(line);
	}
	
	protected void handleErr(String line) {
        System.out.println("ERROR: "+ line);		
	}
	
	protected int handleException(Throwable e) {
		throw new RuntimeException(e);		
	}
	
	
	public static void output_stat() {
		System.out.println("\n-- Stats --");
		System.out.println("Total time in commands: "+stat_total_ms+"ms");
		System.out.println("Total commands: "+stat_total_calls);
		
	}
}