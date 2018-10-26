package PreProcessData;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import Classes.Path;

/**
 * This is for INFSCI 2140 in 2018
 *
 */
public class TrecwebCollection implements DocumentCollection {
	//you can add essential private methods or variables
	
	BufferedReader rl;
	// YOU SHOULD IMPLEMENT THIS METHOD
	public TrecwebCollection() throws IOException {
		rl = new BufferedReader(new InputStreamReader(new FileInputStream(Path.DataWebDir)));
		// This constructor should open the file in Path.DataWebDir
		// and also should make preparation for function nextDocument()
		// you cannot load the whole corpus into memory here!!
	}
	
	// YOU SHOULD IMPLEMENT THIS METHOD
	public Map<String, Object> nextDocument() throws IOException {
		
		String number = new String();
		String content = new String();
		Map<String, Object> result = new HashMap<String, Object>();
		String number_line= new String();
		while((number_line=rl.readLine())!=null)
		{
			///Firstly read the number of document 
			if(number_line.contains("<DOCNO>"))
			{
				// extract number between tags
				int end = number_line.lastIndexOf("</DOCNO>");
				number=number_line.substring(7, end);
			}
			else
			{
				// extract the content
				if(number_line.contains("</DOCHDR>"))
				{
					while((number_line=rl.readLine())!=null&&!number_line.contains("</DOC>"))
					{
						content += number_line;
						content +=" ";
					}
					content = content.replaceAll("<!?/?[^>]+>", "");
					content = content.replaceAll("\\&[a-zA-Z]{1,10};", "");
					content = content.replaceAll("\\pP", "");
					result.put(number, content.toCharArray());
					return result;
				}
			}
		}
		if(number_line==null)
		{
			rl.close();
			return null;
		}
		rl.close();
		// this method should load one document from the corpus, and return this document's number and content.
		// the returned document should never be returned again.
		// when no document left, return null
		// NT: the returned content of the document should be cleaned, all html tags should be removed.
		// NTT: remember to close the file that you opened, when you do not use it any more
		
		return null;
	}
	
}
