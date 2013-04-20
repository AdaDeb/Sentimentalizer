package org.panhandlers.sentimentalizer;

import java.util.ArrayList;

public class Vector {
	private int exist = 0;
	private int type = 0;
	private String token = "";
	private ArrayList<Integer> freq = new ArrayList<>();
	
 	public Vector(int exist, int type, String token){
		this.exist = exist;
		this.type = type;
		this.setToken(token);
		
	}
	public int getExist() {
		return exist;
	}
	public void setExist(int exist) {
		this.exist = exist;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public ArrayList<Integer> getFreq() {
		return freq;
	}
	public void setFreq(ArrayList<Integer> freq) {
		this.freq = freq;
	}
	
	

}
