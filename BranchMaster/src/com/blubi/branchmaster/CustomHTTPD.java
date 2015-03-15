package com.blubi.branchmaster;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import external.fi.iki.elonen.NanoHTTPD;

public class CustomHTTPD extends NanoHTTPD {

	public static int port;
	
	public CustomHTTPD(int port)throws IOException {
       super(port);
       CustomHTTPD.port=port;
   }

   public static final String MIME_DEFAULT_BINARY = "application/octet-stream";
   private static final Map<String, String> MIME_TYPES = new HashMap<String, String>() {{
       put("css",  "text/css");
       put("htm",  "text/html");
       put("html", "text/html");
       put("xml",  "text/xml");
       put("txt",  "text/plain");
       put("gif",  "image/gif");
       put("jpg",  "image/jpeg");
       put("jpeg", "image/jpeg");
       put("png",  "image/png");
       put("ico",  "image/x-icon");       
       put("js",   "application/javascript");
       put("zip",  "application/octet-stream");
       put("json", "application/json");   
   }};
   
   // Get MIME type from file name extension, if possible
   private String getMimeTypeForFile(String uri) {
       int dot = uri.lastIndexOf('.');
       String mime = null;
       if (dot >= 0) {
           mime = MIME_TYPES.get(uri.substring(dot + 1).toLowerCase());
       }
       return mime == null ? MIME_DEFAULT_BINARY : mime;
   }

   List<String> staticFiles = Arrays.asList(new String[] {	"/d3.min.js",
		   													"/BranchMaster.html",
		   													"/chosen.jquery.min.js",
		   													"/jquery-1.11.1.min.js",
		   													"/branchmaster_server.js",
		   													"/chosen.min.css",
		   													"/chosen-sprite.png",
		   													"/chosen-sprite@2x.png",
		   													"/branchmaster.css",
		   													"/Git-Icon-1788C.png"});

@Override
   public Response serve( String uri, Method method,
           Map<String, String> header, Map<String, String> parms,
           Map<String, String> files )	{
	
	
	System.out.println("Serving: "+uri);
	
	if(staticFiles.contains(uri)) {
		return serveFile(uri);
	}

	if(uri.equals("/dir")) {
		String navigate = parms.get("navigate");
		String result = new Commands().dir(homedir,navigate);
		Response response = new Response(Response.Status.OK, getMimeTypeForFile(".json"), result);
		response.addHeader("Access-Control-Allow-Origin", "*");
        return response;        				   
	}
	
//	if(uri.equals("/showbranch")) {
//		String showBranchesResult = new Commands().showBranches(homedir);
//		Response response = new Response(Response.Status.OK, getMimeTypeForFile(".json"), showBranchesResult);
//		response.addHeader("Access-Control-Allow-Origin", "*");
//        return response;        				   
//	}

	if(uri.equals("/gittree")) {
		String branches_parm = parms.get("branches");
		if ( branches_parm == null )
			return new Response(Response.Status.OK, getMimeTypeForFile(".json"), "{ \"error\": \"Wrong input\"}");
		
		// For Json see: https://code.google.com/p/json-simple/
		JSONArray brancharray=(JSONArray) JSONValue.parse(branches_parm);
		String[] branches = (String[]) brancharray.toArray(new String[brancharray.size()]);

//	DEBUG: String[] branches = {"origin/NlpPayables_2.7.1","origin/v.2.8.galop","origin/master", "origin/fr_master", "origin/fr_release_1.5.2"};
		
		String gitTreeResult = new Commands().gitTree(homedir, branches);
		Response response = new Response(Response.Status.OK, getMimeTypeForFile(".json"), gitTreeResult);
		response.addHeader("Access-Control-Allow-Origin", "*");
        return response;
	}

		System.out.println("Not found");
       return new Response(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "404 - "+uri);
   }

	private Response serveFile(String uri) {		
		InputStream stream = getClass().getResourceAsStream(uri);
        if (stream == null) {
            return new Response(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Fatal - Not Found: "+uri);
        }
        return new Response(Response.Status.OK, getMimeTypeForFile(uri), stream);
	}
	
	private static CustomHTTPD server;
	public static File homedir;
	
	public static void startUp(int port, File _homedir) {
	    try {
	    		homedir = _homedir;
	    		server = new CustomHTTPD(port);
	    		server.start();
	       }
	       catch( IOException ioe )
	       {
	           System.err.println( "Couldn't start server on port "+port+"\n" + ioe );
	           System.exit( -1 );
	       }
	       System.out.println( "Listening on port "+port+".\n" );
	}
	
	public static void stopNow() {
		server.stop();
	}
	
	public static void waitForStop() {
		System.out.println( "Hit Enter to stop.\n" );
	    try { System.in.read(); } catch( Throwable t ) {
	    System.out.println("read error");
	    server.stop();
	}	

		
	} 
	
//   public static void main( String[] args ) {	   
//	   try
//       {
//           new CustomHTTPD(8080).start();
//       }
//       catch( IOException ioe )
//       {
//           System.err.println( "Couldn't start server:\n" + ioe );
//           System.exit( -1 );
//       }
//       System.out.println( "Listening on port 8080. Hit Enter to stop.\n" );
//       try { System.in.read(); } catch( Throwable t ) {
//           System.out.println("read error");
//       };
//   }

}
