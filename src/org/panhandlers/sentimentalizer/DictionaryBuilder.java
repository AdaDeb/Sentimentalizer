package org.panhandlers.sentimentalizer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DictionaryBuilder {
	public static Set<String> buildDictionary(HashMap<String, List<List<String>>> data) {
		Set<String> output = new HashSet<String>();
		for (List<List<String>> categoryItems : data.values()) {
			for(List<String> itemTokens : categoryItems) {
				for(String token : itemTokens) {
					output.add(token);
				}
			}
		}
		return output;
	}
}
