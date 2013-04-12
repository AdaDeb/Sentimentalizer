package org.panhandlers.sentimentalizer;

import java.util.ArrayList;

/**
 * We may want to use this class instead ArrayList<String> for sake of clarity
 * @author jesjos
 *
 */
public class TokenItem {
	private ArrayList<String> tokens;

	public ArrayList<String> getTokens() {
		return tokens;
	}

	public void setTokens(ArrayList<String> tokens) {
		this.tokens = tokens;
	}
}
