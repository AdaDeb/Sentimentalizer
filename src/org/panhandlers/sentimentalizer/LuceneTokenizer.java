package org.panhandlers.sentimentalizer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;


/**
 * Uses Lucene to tokenize strings.
 * Strips stopwords.
 * Removes whitespace.
 * Coerces to lowercase.
 * @author jesjos
 *
 */
public class LuceneTokenizer implements GeneralTokenizer{
	private Analyzer analyzer;
	private ArrayList<String> output;
	
	public LuceneTokenizer() {
		analyzer = new StandardAnalyzer(Version.LUCENE_42, StandardAnalyzer.STOP_WORDS_SET);// new SimpleAnalyzer();
	}
	
	@Override
	public ArrayList<String> tokenize(String input) {
		output = new ArrayList<String>();
		try {
			TokenStream ts = analyzer.tokenStream("wut", new StringReader(input));
			CharTermAttribute attr = ts.addAttribute(CharTermAttribute.class);
			ts.reset();
			while (ts.incrementToken()) {
				output.add(attr.toString());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return output;
	}
	
	/**
	 * Quick and dirty test method.
	 * @param args
	 */
	public static void main(String[] args) {
		GeneralTokenizer t = new LuceneTokenizer();
		ArrayList<String> strings = t.tokenize("Hello and goodbye and mète a tête the bat is my bat.");
		for (String s : strings) {
			System.out.println(s);
		}
	}

}
