package Search;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import Classes.Query;
import Classes.Document;
import IndexingLucene.MyIndexReader;

public class QueryRetrievalModel {
	
	protected MyIndexReader indexReader;
	long miu=2000;
	long terms;
	public QueryRetrievalModel(MyIndexReader ixreader) {
		indexReader = ixreader;
		try{
			terms=indexReader.getCollectionLength();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Search for the topic information. 
	 * The returned results (retrieved documents) should be ranked by the score (from the most relevant to the least).
	 * TopN specifies the maximum number of results to be returned.
	 * 
	 * @param aQuery The query to be searched for.
	 * @param TopN The maximum number of returned document
	 * @return
	 */
	
	public List<Document> retrieveQuery( Query aQuery, int TopN ) throws IOException {
		// NT: you will find our IndexingLucene.Myindexreader provides method: docLength()
		// implement your retrieval model here, and for each input query, return the topN retrieved documents
		// sort the docs based on their relevance score, from high to low
		List<Document> ld = new ArrayList<>();
		//store the words' frequency
		Map<String, Double> cfq = new HashMap<>();
		//store the words' posting list
		Map<String, Object> post = new HashMap<>();
		Map<String, Double> md = new HashMap<>();
		//divide query into words
		List<String> words = new ArrayList<>();
		String queryCon = aQuery.GetQueryContent();
		//traverse word by word in content
		int index = 0;
		while(index < queryCon.length() && queryCon.charAt(index) == ' '){
			index ++;
		}
		//divide content into words
		while(index < queryCon.length()){
			StringBuilder sb = new StringBuilder();
			while(index < queryCon.length() && queryCon.charAt(index) != ' '){
				sb.append(queryCon.charAt(index));
				index ++;
			}
			words.add(sb.toString());
			post.put(sb.toString(), indexReader.getPostingList(sb.toString()));
			cfq.put(sb.toString(), (double)indexReader.CollectionFreq(sb.toString()));
			while(index < queryCon.length() && queryCon.charAt(index) == ' '){
				index ++;
			}
		}
		//computation
		int j = 0;
		String docno = new String();
		try{
			while((docno = indexReader.getDocno(j)) != null){
				double sc = 1;
				//D
				double d = indexReader.docLength(j);
				for(String i : words){
					//p(w|REF)
					double pwc = cfq.get(i) / terms;
					if(pwc == 0){
						break;
					}
					//posting list
					int[][] pl = (int[][])post.get(i);
					//c(w|D)
					double cwd = 0;
					for(int[] p : pl){
						if(p[0] == j){
							cwd = p[1];
							break;
						}
					}
					sc *= (cwd + miu * pwc) / (d + miu);
				}
				docno += '&';
				docno += String.valueOf(j);
				md.put(docno, sc);
				j ++;
			}

		}
		catch(Exception e){
			;
		}
		// sort map
		List<Map.Entry<String, Double>> wordMap = new ArrayList<Map.Entry<String, Double>>(md.entrySet());
		Collections.sort(wordMap, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
				double result = o2.getValue() - o1.getValue();
				if (result > 0)
					return 1;
				else if (result == 0)
					return 0;
				else
					return -1;
			}
		});
		int c = 0;
		for(Map.Entry<String, Double> set : wordMap){
			if(c >= TopN)
				break;
			String key = set.getKey();
			String docNo = key.substring(0, key.indexOf('&'));
			String docid = key.substring(key.indexOf('&') + 1, key.length());
			Document doc = new Document(docid, docNo, set.getValue());
			ld.add(doc);
			c ++;
		}
		if(ld.size() > 0)
			return ld;

		return null;
	}

}
