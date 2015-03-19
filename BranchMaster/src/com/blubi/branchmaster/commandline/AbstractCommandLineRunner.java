package com.blubi.branchmaster.commandline;

import java.io.File;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.exec.PumpStreamHandler;

public abstract class AbstractCommandLineRunner {

	private static long stat_total_ms = 0;
	private static long stat_total_calls = 0;
	
	File homedir = null;
		
	public AbstractCommandLineRunner(File homedir) {
		this.homedir = homedir;
	}

	public int run(String command) {
		try {
			LogOutputStream outStrm = new LogOutputStream() {
			    @Override protected void processLine(String line, int level) {
		        handle(line);
		      }			
			};
			
			LogOutputStream errStrm = new LogOutputStream() {
			    @Override protected void processLine(String line, int level) {
		        handleErr(line);
		      }
			};

		    PumpStreamHandler psh = new PumpStreamHandler(outStrm,errStrm);
		    CommandLine cl = CommandLine.parse(command);
		    DefaultExecutor exec = new DefaultExecutor();
		    exec.setWorkingDirectory(this.homedir);
		    exec.setExitValues(new int[] {0,1});
		    exec.setStreamHandler(psh);
		    
		    int exitValue = exec.execute(cl);
		    return exitValue;	    	
		} catch (Exception e)  {
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