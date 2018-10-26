package Indexing;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import Classes.Path;


public class MyIndexWriter {
	// I suggest you to write very efficient code here, otherwise, your memory cannot hold our corpus...
	int docid;
	FileWriter wl_index;
	String index_path;
	FileWriter wl_posting;
	String posting_path;
	FileWriter wl_docid;
	String docid_path;
	Map<String, Integer> token_list;
	Map<String, Map<Integer,Integer>> posting_list;
	Map<Integer, String>docid_docno;// represent the relations between docid and docno
	
	
	public MyIndexWriter(String type) throws IOException {
		// This constructor should initiate the FileWriter to output your index files
		// remember to close files if you finish writing the index
		docid=1;
		if(type=="trectext")
			index_path=Path.IndexTextDir+type;
		else
			index_path=Path.IndexWebDir+type;
		posting_path="data//postinglist." + type;
		docid_path = "data//docidTodocio." + type;
		wl_index=new FileWriter(index_path);
		wl_posting=new FileWriter(posting_path);
		wl_docid=new FileWriter(docid_path);
		token_list=new HashMap<String, Integer>();
		posting_list=new HashMap<String, Map<Integer, Integer>>();
		docid_docno=new HashMap<Integer,String>();
	}
	
	public void IndexADocument(String docno, char[] content) throws IOException {
		// you are strongly suggested to build the index by installments
		// you need to assign the new non-negative integer docId to each document, which will be used in MyIndexReader
		if(docid%50000==0)//every block contain 50000 documents
		{
			Writer();
		}
		docid_docno.put(docid, docno);
        //making the dictionary for the document
		String temp_char = new String();
		for(int i=0;i<content.length;i++)
		{
			if(content[i]==' ')
			{
				if(!token_list.containsKey(temp_char))
				{
					token_list.put(temp_char, 1);
					Map<Integer, Integer> n = new HashMap<Integer,Integer>();
					n.put(docid, 1);
					posting_list.put(temp_char, n);
					temp_char=new String();
					continue;
				}
				else
				{
					token_list.put(temp_char, token_list.get(temp_char)+1);
					if(posting_list.get(temp_char).containsKey(docid))
						posting_list.get(temp_char).put(docid, posting_list.get(temp_char).get(docid)+1);
					else
						posting_list.get(temp_char).put(docid, 1);
					temp_char=new String();
					continue;
				}
			}
			temp_char=temp_char+content[i];
		}
		docid++;
	}
	public void Writer() throws IOException
	{
		Iterator iter_tl= token_list.entrySet().iterator();
		while(iter_tl.hasNext())
		{
			Map.Entry tl=(Map.Entry)iter_tl.next();
			String tl_key=new String();
			tl_key=tl.getKey().toString();
			wl_index.append(tl_key+"\n"+token_list.get(tl_key)+"\n");
		}
		Iterator iter_pl = posting_list.entrySet().iterator();
		while(iter_pl.hasNext())
		{
			Map.Entry pl = (Map.Entry)iter_pl.next();
			String pl_key=new String();
			pl_key=pl.getKey().toString();
			wl_posting.append(pl_key+"\n");
			Iterator iter_n=posting_list.get(pl_key).entrySet().iterator();
			while(iter_n.hasNext())
			{
				Map.Entry list_n=(Map.Entry)iter_n.next();
				String aa=new String();
				aa=list_n.getKey().toString();
				int n_key=Integer.parseInt(aa);
				wl_posting.append(n_key+" "+posting_list.get(pl_key).get(n_key)+";");
			}
			wl_posting.append("\n");
		}
		Iterator iter_dd=docid_docno.entrySet().iterator();
		while(iter_dd.hasNext())
		{
			Map.Entry dd=(Map.Entry)iter_dd.next();
			String bb=new String();
			bb=dd.getKey().toString();
			int dd_key=Integer.parseInt(bb);
			wl_docid.append(dd_key+"\n"+docid_docno.get(dd_key)+"\n");
		}
		docid_docno.clear();
		posting_list.clear();
		token_list.clear();
	}
	
	public void Close() throws IOException {
		// close the index writer, and you should output all the buffered content (if any).
		// if you write your index into several files, you need to fuse them here.
		Writer();
		wl_index.close();
		wl_posting.close();
		wl_docid.close();
	}
	
}
