package org.panhandlers.sentimentalizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DictionaryBuilder {
	public static Set<String> buildDictionary(HashMap<String, ArrayList<ArrayList<String>>> data) {
		Set<String> output = new HashSet<String>();
		for (ArrayList<ArrayList<String>> categoryItems : data.values()) {
			for(ArrayList<String> itemTokens : categoryItems) {
				for(String token : itemTokens) {
					output.add(token);
				}
			}
		}
		return output;
	}
}
