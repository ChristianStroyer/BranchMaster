package com.blubi.branchmaster.commandline;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Git_BranchList extends AbstractCommandLineRunner {
	
	List<String> branchNames = new ArrayList<String>();

	public Git_BranchList(File homedir) {
		super(homedir);
	}

	public List<String> execute() {
		this.run("git branch --list --all --no-color");
		return branchNames;
	}
	
	@Override
	protected void handle(String line) {
		line = line.substring(2); // Strip prefix
		branchNames.add(line);
	}

}
