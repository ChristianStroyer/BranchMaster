package com.blubi.branchmaster.commandline;

import java.io.File;

import com.blubi.branchmaster.Main;

public class Git_LogSingle extends AbstractCommandLineRunner {
	
	Container result = new Container();

	public Git_LogSingle(File homedir) {
		super(homedir);
	}

	public Container execute(String commitId) {
		this.run("git --no-pager log "+commitId+" -1 --pretty=format:\"%H;%ci;%s\"");
		Main.debuglog("     id="+result.id+" date="+result.commit_date);
		return result;		
	}
	
	@Override
	protected void handle(String line) {
		Container c = new Container();
		String[] split = line.split(";");
		c.id=split[0];
		c.commit_date=split[1];
		if(split.length>2)
			c.commit_message=split[2];
		else
			c.commit_message="";
		result = c;
	}
	
	public class Container	{
		public String id;
		public String commit_date;
		public String commit_message;
	}
}
