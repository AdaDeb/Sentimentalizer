package org.panhandlers.sentimentalizer;

import java.util.ArrayList;
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

	public static Double standardDeviation(ArrayList<Double> successRates) {
		Double sum = 0d;
		for (Double rate : successRates) {
			sum += rate;
		}
		Double mean = sum / successRates.size();
		Double sumOfSquares = 0d;
		for (Double rate : successRates) {
			sumOfSquares += Math.pow(rate - mean, 2);
		}
		Double standardDeviation = Math.sqrt(sumOfSquares / successRates.size());
		return standardDeviation;
	}
}
