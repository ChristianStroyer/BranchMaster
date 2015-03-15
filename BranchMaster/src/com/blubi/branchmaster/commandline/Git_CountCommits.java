package com.blubi.branchmaster.commandline;

import java.io.File;

import com.blubi.branchmaster.Main;

public class Git_CountCommits extends AbstractCommandLineRunner {
	
	int result;

	public Git_CountCommits(File homedir) {
		super(homedir);
	}

	public int execute(String branch1, String branch2) {
		this.run("git rev-list --count "+branch1+".."+branch2);
		Main.debuglog("     count="+result);
		return result;		
	}
	
	@Override
	protected void handle(String line) {
		result = Integer.parseInt(line);
	}
	
}
