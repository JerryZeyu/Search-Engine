package PreProcessData;
import Classes.Stemmer;

/**
 * This is for INFSCI 2140 in 2018
 * 
 */
public class WordNormalizer {
	//you can add essential private methods or variables
	
	// YOU MUST IMPLEMENT THIS METHOD
	public char[] lowercase( char[] chars ) 
	{
		int i;
		for(i=0;i<chars.length;i++)
		{
			int integer = (int)chars[i];
			if(integer>=65&&integer<=90)
			{
				integer+=32;
				chars[i]=(char)integer;
			}
		}
		//transform the uppercase characters in the word to lowercase
		return chars;
	}
	
	public String stem(char[] chars)
	{
		//use the stemmer in Classes package to do the stemming on input word, and return the stemmed word
		String str="";
		Stemmer ste= new Stemmer();
		ste.add(chars, chars.length);
		ste.stem();
		str += ste;
		return str;
	}
	
}
