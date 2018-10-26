package PreProcessData;

/**
 * This is for INFSCI 2140 in 2018
 * 
 * TextTokenizer can split a sequence of text into individual word tokens.
 */
public class WordTokenizer {
	//you can add essential private methods or variables
	char[] before_tokens;
	int i;
	// YOU MUST IMPLEMENT THIS METHOD
	public WordTokenizer( char[] texts ) {
		before_tokens=texts;
		i=0;
		// this constructor will tokenize the input texts (usually it is a char array for a whole document)
	}
	
	// YOU MUST IMPLEMENT THIS METHOD
	public char[] nextWord() {
		String tokens="";
		for(;i<before_tokens.length;i++)
		{
			if(before_tokens[i]!=' ')
				break;
		}
		for(;i<before_tokens.length&&before_tokens[i]!=' ';i++)
		{
			tokens +=before_tokens[i];
		}
		i++;
		if(tokens != "")
		{
			return tokens.toCharArray();
		}
		// read and return the next word of the document
		// or return null if it is the end of the document
		return null;
	}
	
}
