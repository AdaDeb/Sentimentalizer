package org.panhandlers.sentimentalizer;

import java.util.ArrayList;

/**
 * We may want to use this class instead of ArrayList<ArrayList<String>>
 * @author jesjos
 *
 */
public class TokenCategory {
	private ArrayList<TokenItem> items;

	public ArrayList<TokenItem> getItems() {
		return items;
	}

	public void setItems(ArrayList<TokenItem> items) {
		this.items = items;
	}
}
