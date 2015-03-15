package com.blubi.branchmaster.commandline;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.blubi.branchmaster.Main;

public class Git_LogRange extends AbstractCommandLineRunner {
	
	List<Container> result = new ArrayList<Container>();

	public Git_LogRange(File homedir) {
		super(homedir);
	}

	public List<Container> execute(String branch1, String branch2) {
		this.run("git --no-pager log "+branch1+".."+branch2+" --pretty=format:\"%H;%ci; %s\"");
		Main.debuglog("     "+result.size()+" commits returned");
		return result;		
	}
	
	@Override
	protected void handle(String line) {
		Container c = new Container();
		String[] split = line.split(";");
		c.id=split[0];
		c.commit_date=split[1];
		c.commit_message=split[2];
		result.add(c);
	}
	
	public class Container	{
		public String id;
		public String commit_date;
		public String commit_message;
	}
}
