package PseudoRFSearch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import Classes.Document;
import Classes.Query;
import IndexingLucene.MyIndexReader;
import SearchLucene.*;
//import Search.*;

public class PseudoRFRetrievalModel {

	MyIndexReader ixreader;
	List<Document> topK;
	long terms;
	Map<String, Double> wfc;
	Map<String, Object> post_list;
	List<String> words;
	int doc_lenth = 0;
	int miu = 2000;
	Map<String, Double> word_rele_freq = new HashMap<>();
	HashMap<String,Double> TokenRFScore;
	HashMap<Integer, Integer> doc = new HashMap<>();

	public PseudoRFRetrievalModel(MyIndexReader ixreader)
	{
		this.ixreader=ixreader;
		try{
			terms=this.ixreader.getCollectionLength();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Search for the topic with pseudo relevance feedback in 2017 spring assignment 4.
	 * The returned results (retrieved documents) should be ranked by the score (from the most relevant to the least).
	 *
	 * @param aQuery The query to be searched for.
	 * @param TopN The maximum number of returned document
	 * @param TopK The count of feedback documents
	 * @param alpha parameter of relevance feedback model
	 * @return TopN most relevant document, in List structure
	 */
	public List<Document> RetrieveQuery( Query aQuery, int TopN, int TopK, double alpha) throws Exception {
		// this method will return the retrieval result of the given Query, and this result is enhanced with pseudo relevance feedback
		// (1) you should first use the original retrieval model to get TopK documents, which will be regarded as feedback documents
		// (2) implement GetTokenRFScore to get each query token's P(token|feedback model) in feedback documents
		// (3) implement the relevance feedback model for each token: combine the each query token's original retrieval score P(token|document) with its score in feedback documents P(token|feedback model)
		// (4) for each document, use the query likelihood language model to get the whole query's new score, P(Q|document)=P(token_1|document')*P(token_2|document')*...*P(token_n|document')

		//store collection freq for words
		wfc = new HashMap<>();
		//store posting list for words
		post_list = new HashMap<>();
		//divide query content into words
		words = new ArrayList<>();
		String queryCon = aQuery.GetQueryContent();
		//traverse content word by word
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
			int[][] p = ixreader.getPostingList(sb.toString());
			post_list.put(sb.toString(), p);
			if(p != null){
				for(int[] x : p){
					if(!doc.containsKey(x[0]))
						doc.put(x[0], null);
				}
			}
			//cfq.put()
			wfc.put(sb.toString(), (double)ixreader.CollectionFreq(sb.toString()));
			while(index < queryCon.length() && queryCon.charAt(index) == ' '){
				index ++;
			}
		}

		QueryRetrievalModel qrm = new QueryRetrievalModel(ixreader);
		topK = qrm.retrieveQuery(aQuery, TopK);
		// mark word frequency of relevant documents
		for(Document d : topK){
			int docid = Integer.parseInt(d.docid());
			doc_lenth += ixreader.docLength(docid);
			for(String s : words){
				//posting list
				int[][] pl = (int[][])post_list.get(s);
				//ignore tokens which does not exist in the collection
				if(pl == null)
					break;
				//c(w|D)
				double cwd = 0;
				for(int[] p : pl){
					if(p[0] == docid){
						cwd += p[1];
						//break;
					}
				}
				if(!word_rele_freq.containsKey(s))
					word_rele_freq.put(s, cwd);
				else
					word_rele_freq.put(s, word_rele_freq.get(s) + cwd);
			}
		}

		//get P(token|feedback documents)
		TokenRFScore = GetTokenRFScore(aQuery,TopK);
		//implement the relevance feedback model
		// sort all retrieved documents from most relevant to least, and return TopN
		List<Document> results = implement(TopN, alpha);

		return results;
	}

	public HashMap<String,Double> GetTokenRFScore(Query aQuery,  int TopK) throws Exception
	{
		// for each token in the query, you should calculate token's score in feedback documents: P(token|feedback documents)
		// use Dirichlet smoothing
		// save <token, score> in HashMap TokenRFScore, and return it
		HashMap<String,Double> TokenRFScore=new HashMap<String,Double>();

		for(String i : words){
			//p(w|REF)
			double pwc = wfc.get(i) / terms;
			if(pwc == 0){
				break;
			}
			double cwd = word_rele_freq.get(i);
			double sc = (cwd + miu * pwc) / (doc_lenth + miu);
			TokenRFScore.put(i, sc);
		}
		return TokenRFScore;
	}

	public List<Document> implement(int n, double alpha){
		List<Document> ld = new ArrayList<>();
		Map<String, Double> md = new HashMap<>();
		//computation
		//int j = 0;
		String docno = new String();
		try{
			for(Entry<Integer, Integer> entry : doc.entrySet()){
				//while((docno = ixreader.getDocno(j)) != null){
				double sc = 1;
				int j = entry.getKey();
				//D
				double d = ixreader.docLength(j);
				for(String i : words){
					//p(w|REF)
					double pwc = wfc.get(i) / terms;
					//ignore tokens which does not exist in the collection
					if(pwc == 0){
						break;
					}
					//posting list
					int[][] pl = (int[][])post_list.get(i);
					//c(w|D)
					double cwd = 0;
					for(int[] p : pl){
						if(p[0] == j){
							cwd += p[1];
							//break;
						}
					}
					//token score = P(token|feedback model) + P(token|document)
					sc *= (cwd + miu * pwc) / (d + miu) * alpha + TokenRFScore.get(i) * (1 - alpha);
				}
				docno = ixreader.getDocno(j) + '&';
				docno += String.valueOf(j);
				md.put(docno, sc);
			}
		}
		catch(Exception e){
			;
		}
		// sort map
		List<Map.Entry<String, Double>> wordMap = new ArrayList<Map.Entry<String, Double>>(md.entrySet());
		//System.out.println(wordMap);
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
			if(c >= n)
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
