package org.panhandlers.sentimentalizer;

import java.util.LinkedList;
import java.util.List;

public class Utilities {
	public static <T> List<Pair<T>> combinations(List<T> list) {
		List<Pair<T>> pairs = new LinkedList<Pair<T>>();
		Pair<T> pair;
		for(int i = 0; i < (list.size() - 1); i++) {
			for(int j = i + 1; j < list.size(); j++ ) {
				pair = new Pair<T>();
				pair.first = list.get(i);
				pair.other = list.get(j);
				pairs.add(pair);
			}
		}
		return pairs;
	}
}
