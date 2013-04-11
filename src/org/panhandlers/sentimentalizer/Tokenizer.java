package org.panhandlers.sentimentalizer;

import java.util.ArrayList;

public interface Tokenizer {
	public ArrayList<String> tokenize(String input);
}
