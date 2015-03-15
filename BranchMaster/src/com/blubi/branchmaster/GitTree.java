package com.blubi.branchmaster;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.blubi.branchmaster.commandline.Git_CommonAncestor;
import com.blubi.branchmaster.commandline.Git_CountCommits;
import com.blubi.branchmaster.commandline.Git_IsAncestor;
import com.blubi.branchmaster.commandline.Git_LogSingle;

public class GitTree {

	private File homedir;
	private HashMap<String,List<String>>   	branchToID = new HashMap<String,List<String>>();
	private HashMap<String,Integer>  		gitNodeIds = new HashMap<String,Integer>();
	private List<NodeContainer> 	 		gitNodes = new ArrayList<NodeContainer>();
	private boolean[][] 			 		ancestors;
	private boolean[][] 			 		adjancency;
	private int[][]     			 		commitCount;
	
	private class NodeContainer {
		public String id = "";
		public String date = "";
		public boolean innerNode = false;
		public NodeContainer(String id, boolean innerNode, String date) {
			this.id=id;
			this.innerNode=innerNode;
			this.date = date.substring(0,10);
		}
	}
	
	public GitTree(int branches, File homedir) {
		this.homedir = homedir;
		int size = branches*2;
		ancestors = new boolean[size][size];
		commitCount = new int[size][size];
	}
	
	public String buildTree(String[] branches) {
		String [] uniq_branches = findIDs(branches);
    	addCommonAncestors(uniq_branches);
    	fillGraph();
    	return jsonResult();		
	}

	private String[] findIDs(String[] branches) {
		for(int i=0;i<branches.length;i++) {
			Git_LogSingle.Container data = new Git_LogSingle(this.homedir).execute(branches[i]);
			List<String> list = branchToID.get(data.id);
			if(list==null) {
				list = new ArrayList<String>();
				branchToID.put(data.id, list);
			}
			list.add(branches[i]);
		}
		return branchToID.keySet().toArray(new String[0]);
	}
	
	private void addCommonAncestors(String[] branches) {		
    	for(int i=0;i<branches.length;i++)
    		for(int j=i+1;j<branches.length;j++) {
    			String b1 = branches[i];
    			String b2 = branches[j];
    			String ancId = new Git_CommonAncestor(this.homedir).execute(b1,b2);
    			if(ancId !=null)
    				addAncestorNode(b1, b2, ancId);
    		}		
	}
	
	private void addAncestorNode(String branch1, String branch2, String ancestorNode) {
		int b1_id  = idFromGitNode(branch1, false);
		int b2_id  = idFromGitNode(branch2, false);
		int anc_id = idFromGitNode(ancestorNode, true);
		
		System.out.println(b1_id+";" +b2_id+";"+anc_id);
		
		if(b1_id==anc_id){ // b1 IS the ancestor
			ancestors[anc_id][b2_id] = true;
		} 
		else if (b2_id==anc_id){  // b2 IS the ancestor
			ancestors[anc_id][b1_id] = true;
		}
		else {  // the ancestor is not b1 og b2
			ancestors[anc_id][b1_id] = true;
			ancestors[anc_id][b2_id] = true;
		}
	}	
	
	private int idFromGitNode(String node, boolean innerNode) {
		Integer id = gitNodeIds.get(node);
		if(id==null) {
			id = gitNodes.size();			
			gitNodeIds.put(node, id);
			String date = getBranchpointDate(node);
			gitNodes.add(new NodeContainer(node, innerNode, date));
		}
		return id;
	}
	
	private void fillGraph() {
		
		int size = gitNodes.size();
		
		for(int i=0;i<size;i++)
			for(int j=0;j<size;j++) {
				if(i==j) 
					continue;
				if(ancestors[i][j]==false) {
					ancestors[i][j]=new Git_IsAncestor(this.homedir).execute(gitNodes.get(i).id, gitNodes.get(j).id);
				}
			}
		
		adjancency = ancestorMatrixToAdjacencyMatrix(ancestors);
		
		for(int i=0;i<size;i++)
			for(int j=0;j<size;j++)
				if(adjancency[i][j]) {
					commitCount[i][j] = new Git_CountCommits(this.homedir).execute(gitNodes.get(i).id, gitNodes.get(j).id);				
				}	
	}
	
	// Iterative thinning of ancestor matrix to create a minimum edge graph
	// Algorithm from: http://cs.stackexchange.com/questions/23408/reconstruct-directed-graph-from-list-of-ancestors-for-each-node
	//
	private boolean[][] ancestorMatrixToAdjacencyMatrix(boolean[][] ancestors) {		
		int size = ancestors.length;
		boolean[][] adjancency = new boolean[size][size];
		
		// Initial clone
		for(int i=0;i<size;i++)
			for(int j=0;j<size;j++)
				adjancency[i][j] = ancestors[i][j];
		
		// If there exist an indirect path, eliminate the direct path
		for(int k=0;k<size;k++)
			for(int l=0;l<size;l++)
				for(int m=0;m<size;m++) {
					if(ancestors[k][l] && ancestors[l][m] && ancestors[k][m])
						adjancency[k][m] = false;
				}
		
		return adjancency;
	}

	private HashMap<String,String> branchpoint_to_Date = new HashMap<String,String>();

	private String getBranchpointDate(String id) {
		String result = branchpoint_to_Date.get(id); 
		if(result==null) {
			Git_LogSingle.Container gitlog = new Git_LogSingle(this.homedir).execute(id);
			branchpoint_to_Date.put(id, gitlog.commit_date);
			result = gitlog.commit_date;
		}
		return result;
	}
	
	private String jsonResult() {
		int size = gitNodes.size();
		
		JSONObject obj  = new JSONObject();
		JSONArray nodes = new JSONArray(); 
		
		for(int i=0;i<size;i++) {
			JSONObject node  = new JSONObject();
			node.put("id", i);
			node.put("date", gitNodes.get(i).date);
			node.put("inner", gitNodes.get(i).innerNode);
			
			List<String> list = branchToID.get(gitNodes.get(i).id);

			if(list!=null) {
				JSONArray names = new JSONArray();
				for(String s:list) {
					names.add(s);
				}
				node.put("names", names);
			}			
			String id = gitNodes.get(i).id;
			String id_short = (gitNodes.get(i).innerNode?id.substring(0,10):id.substring(0,10));
			node.put("name", id_short);
			nodes.add(node);
		}
		obj.put("nodes", nodes);
		
		JSONArray links = new JSONArray();
		
		for(int i=0;i<size;i++) {
			for(int j=0;j<size;j++) {
				if(commitCount[i][j]==0)
					continue;
				JSONObject link  = new JSONObject();
				link.put("source", i);
				link.put("target", j);
				link.put("commits", commitCount[i][j]);
				links.add(link);
			}
		}
		obj.put("links", links);		

		return obj.toString();
	}

	
	public void debugoutput(){
		System.out.println("\n-- Nodes --");
		int size = gitNodes.size();
		int count=0;
		for(NodeContainer gn : gitNodes) {
			List<String> list = branchToID.get(gn.id);
			String b="";
			if(list!=null)
				for(String s:list) {
					b += s;
				}
			System.out.println((count++)+":"+gn.id+" - "+b);
		}
		
		System.out.println("-- Ancestor --");
		for(int i=0;i<size;i++) {
			String line = gitNodes.get(i).id+"  : ";
			for(int j=0;j<size;j++) {
				line += (ancestors[i][j]?"1-":"0-");
			}
			System.out.println(line);
		}
		
		System.out.println("-- Adjancency --");
		for(int i=0;i<size;i++) {
			String line = gitNodes.get(i).id+"  : ";
			for(int j=0;j<size;j++) {
				line += (adjancency[i][j]?"1-":"0-");
			}
			System.out.println(line);
		}

		System.out.println("-- Commit count --");
		for(int i=0;i<size;i++) {
			String line = gitNodes.get(i).id+"  : ";
			for(int j=0;j<size;j++) {
				line += commitCount[i][j]+"-";
			}
			System.out.println(line);
		}

		
	}
	
}