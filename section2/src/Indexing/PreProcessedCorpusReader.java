package Indexing;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import Classes.Path;

import java.io.BufferedReader;
import java.io.FileInputStream;

public class PreProcessedCorpusReader {

	BufferedReader rl;
	
	public PreProcessedCorpusReader(String type) throws IOException {
		// This constructor opens the pre-processed corpus file, Path.ResultHM1 + type
		// You can use your own version, or download from http://crystal.exp.sis.pitt.edu:8080/iris/resource.jsp
		// Close the file when you do not use it any more
		String path=Path.ResultHM1+type;
		rl = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
	}
	

	public Map<String, Object> NextDocument() throws IOException {
		// read a line for docNo and a line for content, put into the map with <docNo, content>
		Map<String, Object> result = new HashMap<String, Object>();
		String n= new String();
		if((n=rl.readLine())!=null)
		{
//			result.put("docno", n);
//			result.put("content", rl.readLine().toCharArray());
			result.put(n, rl.readLine().toCharArray());
		}
		if(!result.isEmpty())
		{
			return result;
		}
		return null;
	}

}
