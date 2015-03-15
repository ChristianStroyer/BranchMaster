package com.blubi.branchmaster.commandline;

import java.io.File;

import com.blubi.branchmaster.Main;

public class Git_Exists extends AbstractCommandLineRunner {
	
	public Git_Exists(File homedir) {
		super(homedir);
	}

	public boolean execute() {
		int exitlevel = this.run("git --version");
		Main.debuglog("     result="+(exitlevel==0?"true":"false"));
		if (exitlevel==0)
			return true;
		
		return false;		
	}
	
	@Override
	protected void handle(String line) {
		// Returns exitcode, but no output
	}
	
	@Override
	protected int handleException(Throwable e) {
		return -1;
	}
	
	public class Container	{
		public String id;
		public String commit_date;
		public String commit_message;
	}
}
