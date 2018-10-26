package PreProcessData;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import Classes.*;

public class StopWordRemover 
{
	BufferedReader rl;
	List<String> stop_words=new ArrayList<String>();
	
	//you can add essential private methods or variables.
	
	public StopWordRemover( ) throws IOException
	{
		rl = new BufferedReader(new InputStreamReader(new FileInputStream(Path.StopwordDir)));
		String stop_line;
		while((stop_line=rl.readLine())!=null)
		{
			stop_words.add(stop_line);
		}
		// load and store the stop words from the fileinputstream with appropriate data structure
		// that you believe is suitable for matching stop words.
		// address of stopword.txt should be Path.StopwordDir
	}
	
	// YOU MUST IMPLEMENT THIS METHOD
	public boolean isStopword( char[] word ) 
	{
		String word_string;
		word_string=String.valueOf(word);
		if(stop_words.contains(word_string))
		{
			return true;
		}
		// return true if the input word is a stopword, or false if not
		return false;
	}
}
