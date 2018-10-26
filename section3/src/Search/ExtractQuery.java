package Search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.StringBuilder;
import Classes.Path;
import Classes.Query;
import Classes.Stemmer;

public class ExtractQuery {
	BufferedReader rl_topic;
	BufferedReader rl_stop;
	Map<StringBuilder, String> stop_Words=new HashMap<>();
	List<Query> topic=new ArrayList<>();
	int index;

	public ExtractQuery() throws FileNotFoundException{
		//you should extract the 4 queries from the Path.TopicDir
		//NT: the query content of each topic should be 1) tokenized, 2) to lowercase, 3) remove stop words, 4) stemming
		//NT: you can simply pick up title only for query, or you can also use title + description + narrative for the query content.
		rl_topic=new BufferedReader(new InputStreamReader(new FileInputStream(Path.TopicDir)));
		rl_stop=new BufferedReader(new InputStreamReader(new FileInputStream(Path.StopwordDir)));
		index=0;
		String temp_topic = new String();
		//read stop words into a hash map
		try
		{
			String temp_stop;
			while((temp_stop=rl_stop.readLine())!=null)
			{
				stop_Words.put(new StringBuilder(temp_stop), null );
			}
			//read topic statements
			while((temp_topic=rl_topic.readLine())!=null)
			{
				if(temp_topic.contains("<num>"))
				{
					String temp=new String();
					Query query=new Query();
					query.SetTopicId(temp_topic.substring(temp_topic.indexOf(':')+2,temp_topic.length()));
					StringBuilder content = new StringBuilder();
					while((temp=rl_topic.readLine())!=null&&!temp.contains("</top>"))
					{
						if(temp.contains("<title>"))
						{
							content.append(temp.substring(temp.indexOf('>')+2, temp.length()));
						}
					}
					query.SetQueryContent(token(content.toString().replaceAll("[\\pP]","")));
					topic.add(query);
				}
			}
			rl_stop.close();
			rl_topic.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	String token(String content)
	{
		StringBuilder newString = new StringBuilder();
		int i=0;
		while(i<content.length()&&content.charAt(i)==' ')
		{
			i++;
		}
		while(i < content.length()){
			StringBuilder sb = new StringBuilder();
			while(i < content.length() && content.charAt(i) != ' '){
				sb.append(content.charAt(i));
				i ++;
			}
			newString.append(wordProcess(sb));
			newString.append(' ');
			while(i < content.length() && content.charAt(i) == ' '){
				i ++;
			}
		}
		return newString.toString();
	}
	StringBuilder wordProcess(StringBuilder word){
		//convert the upper case to lower case
		StringBuilder newWord = new StringBuilder();
		for(int i = 0; i < word.length(); i ++){
			int x = (int)word.charAt(i);
			if(x >= 65 && x <= 90){
				x += 32;
				newWord.append((char)x);
			}
			else
				newWord.append(word.charAt(i));
		}
		if(stop_Words.containsKey(newWord))
			return null;
		StringBuilder str = new StringBuilder();
		Stemmer stem = new Stemmer();
		stem.add(newWord.toString().toCharArray(), newWord.length());
		stem.stem();
		str.append(stem);
		return str;
	}


	
	public boolean hasNext()
	{
		if(index < topic.size())
			return true;
		return false;
	}
	
	public Query next()
	{
		if(hasNext())
			return topic.get(index ++);
		return null;
	}
}
