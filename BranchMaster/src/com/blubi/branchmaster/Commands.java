package com.blubi.branchmaster;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.blubi.branchmaster.commandline.Git_BranchList;

public class Commands {
	
	private static FileFilter directoryFilter = new FileFilter() {
		public boolean accept(File file) {
			return file.isDirectory();
		}
	};
	
	private static HashMap<File,List<String>> BranchChoiceCache = new HashMap<File,List<String>>();
	
	public String gitTree(File homedir, String[] branches) {
		
		BranchChoiceCache.put(homedir, Arrays.asList(branches));
		
    	GitTree gtc = new GitTree(branches.length, homedir);
    	
    	
    	String jsonResult = gtc.buildTree(branches);
    	gtc.debugoutput();
    	return jsonResult;
	}
	
	public String dir(File homedir, String navigate) {
		
		if(navigate!=null) {
			if(navigate.equals("..") && homedir.getParentFile()!=null) {
				homedir = homedir.getParentFile();
				CustomHTTPD.homedir = homedir;
			}
			else if(navigate.equals("\\")) {
				while(homedir.getParentFile()!=null) {
					homedir = homedir.getParentFile();
				}
				CustomHTTPD.homedir = homedir;
			} else if(!navigate.equals("")) {
				File[] listFiles = homedir.listFiles(directoryFilter);
				for(File f: Arrays.asList(listFiles)) {
					if(f.getName().equals(navigate)) {
						homedir = f;
						CustomHTTPD.homedir = homedir;
						break;
					}
				}					
			}			
		}		

		JSONObject obj=new JSONObject();
		obj.put("version", Main.version);
		obj.put("homedir", homedir.getAbsolutePath());
		JSONArray navigation = new JSONArray();

		if(homedir.getParentFile()!=null) {
			navigation.add("\\");
			navigation.add("..");
		}
		boolean isGitDir = false;
		File[] listFiles = homedir.listFiles(directoryFilter);
		JSONArray dirs = new JSONArray();
		for(File f:Arrays.asList(listFiles)) {
			if(f.getName().equals(".git")) {
				dirs = new JSONArray();
				isGitDir = true;
				break;
				}
			dirs.add(f.getName());			
		}
		navigation.addAll(dirs);
		obj.put("dirs", navigation);
		obj.put("gitdir", isGitDir);
		
		if(isGitDir) {
			List<String> branchChoiceList = BranchChoiceCache.get(homedir);
			if(branchChoiceList!=null) {
				JSONArray latestChoice = new JSONArray();
				latestChoice.addAll(branchChoiceList);
				obj.put("selection", latestChoice);
			}

			List<String> branchList = new Git_BranchList(homedir).execute();
			
			
			JSONArray branches = new JSONArray();
			for(String b:branchList) {
				if(b.startsWith("remotes/"))
					branches.add(b.substring(8));
				else
					branches.add(b);
			}
			obj.put("branches", branches);		
		}
		
		return obj.toString();
	}
}
