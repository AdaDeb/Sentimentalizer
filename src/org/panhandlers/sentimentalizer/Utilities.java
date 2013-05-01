package org.panhandlers.sentimentalizer;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class Utilities {
	private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
	private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
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
	
	public static String normalize(String input) {
	    String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
	    String normalized = Normalizer.normalize(nowhitespace, Form.NFD);
	    String slug = NONLATIN.matcher(normalized).replaceAll("");
	    return slug.toLowerCase(Locale.ENGLISH);
	}
	
	public static String detokenize(List<String> input) {
		StringBuilder b = new StringBuilder();
		for (String s : input) {
			b.append(s + " ");
		}
		return b.toString();
	}
}
