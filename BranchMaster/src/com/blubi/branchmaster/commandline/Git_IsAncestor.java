package com.blubi.branchmaster.commandline;

import java.io.File;

import com.blubi.branchmaster.Main;

public class Git_IsAncestor extends AbstractCommandLineRunner {
	
	public Git_IsAncestor(File homedir) {
		super(homedir);
	}

	public boolean execute(String commit1, String commit2) {
		int exitlevel = this.run("git merge-base --is-ancestor "+commit1+" "+commit2);
		Main.debuglog("     result="+(exitlevel==0?"true":"false"));
		if (exitlevel==0)
			return true;
		
		return false;		
	}
	
	@Override
	protected void handle(String line) {
		// Returns exitcode, but no output
	}
	
	public class Container	{
		public String id;
		public String commit_date;
		public String commit_message;
	}
}
