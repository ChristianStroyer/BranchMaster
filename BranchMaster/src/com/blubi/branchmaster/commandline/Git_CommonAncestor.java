package com.blubi.branchmaster.commandline;

import java.io.File;

import com.blubi.branchmaster.Main;

public class Git_CommonAncestor extends AbstractCommandLineRunner {
	
	String result;

	public Git_CommonAncestor(File homedir) {
		super(homedir);
	}

	public String execute(String branch1, String branch2) {
		this.run("git merge-base "+branch1+" "+branch2);
		Main.debuglog("     id="+result);
		return result;		
	}
	
	@Override
	protected void handle(String line) {
		result = line;
	}
}
