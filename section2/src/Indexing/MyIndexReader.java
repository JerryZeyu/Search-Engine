package Indexing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;



import Classes.Path;


public class MyIndexReader {
	//you are suggested to write very efficient code here, otherwise, your memory cannot hold our corpus...
	BufferedReader rl_index;
	BufferedReader rl_posting;
	BufferedReader rl_docid;
	String index_path;
	String posting_path;
	String docid_path;
	Map<Integer, String> docid_docno;
	Map<String, Integer> index;
	Map<String, Map<Integer, Integer>> posting_list;
	Map<Integer, Integer> tokenDoc;

	
	public MyIndexReader( String type ) throws IOException {
		//read the index files you generated in task 1
		//remember to close them when you finish using them
		//use appropriate structure to store your index
		if(type=="trectext")
			index_path=Path.IndexTextDir+type;
		else
			index_path=Path.IndexWebDir+type;
		posting_path="data//postinglist." + type;
		docid_path = "data//docidTodocio." + type;
		rl_index=new BufferedReader(new InputStreamReader(new FileInputStream(index_path)));
		rl_posting=new BufferedReader(new InputStreamReader(new FileInputStream(posting_path)));
		rl_docid=new BufferedReader(new InputStreamReader(new FileInputStream(docid_path)));
		docid_docno = new HashMap<Integer, String>();
		index = new HashMap<String, Integer>();
		posting_list = new HashMap<String, Map<Integer, Integer>>();
		tokenDoc = new HashMap<Integer, Integer>();
		String temp = new String();
		while((temp = rl_docid.readLine()) != null){
			docid_docno.put(Integer.parseInt(temp), rl_docid.readLine());
		}
		while((temp = rl_index.readLine()) != null){
			if(index.containsKey(temp))
				index.put(temp, index.get(temp) +  Integer.parseInt(rl_index.readLine()));
			else {
				index.put(temp, Integer.parseInt(rl_index.readLine()));
			}
		}
	}
	
	//get the non-negative integer dociId for the requested docNo
	//If the requested docno does not exist in the index, return -1
	public int GetDocid( String docno ) {
		for(int i : docid_docno.keySet()){
			if(docid_docno.get(i) == docno)
				return i;
		}
		return -1;
	}

	// Retrieve the docno for the integer docid
	public String GetDocno( int docid ) {
		if(docid_docno.containsKey(docid))
			return docid_docno.get(docid);
		return null;
	}
	
	/**
	 * Get the posting list for the requested token.
	 * 
	 * The posting list records the documents' docids the token appears and corresponding frequencies of the term, such as:
	 *  
	 *  [docid]		[freq]
	 *  1			3
	 *  5			7
	 *  9			1
	 *  13			9
	 * 
	 * ...
	 * 
	 * In the returned 2-dimension array, the first dimension is for each document, and the second dimension records the docid and frequency.
	 * 
	 * For example:
	 * array[0][0] records the docid of the first document the token appears.
	 * array[0][1] records the frequency of the token in the documents with docid = array[0][0]
	 * ...
	 * 
	 * NOTE that the returned posting list array should be ranked by docid from the smallest to the largest. 
	 * 
	 * @param token
	 * @return
	 */
	public int[][] GetPostingList( String token ) throws IOException {
		int[][] token_array = new int[tokenDoc.size()][2];
		Iterator iter_tokenarray = tokenDoc.entrySet().iterator();
		int i = 0;
		while(iter_tokenarray.hasNext()){
			Map.Entry tokenarray = (Map.Entry)iter_tokenarray.next();
			String key_string = new String();
			key_string=tokenarray.getKey().toString();
			int key=Integer.parseInt(key_string);
			token_array[i][0] = key;
			token_array[i][1] = tokenDoc.get(key);
			i++;
		}
		return token_array;

	}

	// Return the number of documents that contains the token.
	public int GetDocFreq( String token ) throws IOException {
		String token_term = new String();
		while((token_term = rl_posting.readLine()) != null) {
			if(token_term.equals(token)) {
				String temp = rl_posting.readLine();
				int i = 0;
				int j = 0, k;
				String s = new String();
				while(i < temp.length()) {
					if(temp.charAt(i) == ' '){
						j = Integer.parseInt(s);
						s = new String();
						i ++;
						continue;
					}
					if(temp.charAt(i) == ';'){
						k = Integer.parseInt(s);
						s = new String();
						tokenDoc.put(j, k);
						i ++;
						continue;
					}
					s += temp.charAt(i);
					i ++;
				}
			}
		}
		return tokenDoc.size();
	}
	
	// Return the total number of times the token appears in the collection.
	public long GetCollectionFreq( String token ) throws IOException {
		Iterator iter_tokendoc = tokenDoc.entrySet().iterator();
		int total_times = 0;
		while(iter_tokendoc.hasNext()){
			Map.Entry tokendoc = (Map.Entry)iter_tokendoc.next();
			String key_string = new String();
			key_string=tokendoc.getKey().toString();
			int key = Integer.parseInt(key_string);
			total_times += tokenDoc.get(key);
		}
		return total_times;
	}
	
	public void Close() throws IOException {

		rl_index.close();
		rl_posting.close();
		rl_docid.close();
	}
	
}