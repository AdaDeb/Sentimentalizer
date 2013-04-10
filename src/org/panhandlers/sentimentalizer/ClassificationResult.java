package org.panhandlers.sentimentalizer;

public class ClassificationResult {
	private float p;
	private String category;
	
	public ClassificationResult() {}
	public ClassificationResult(String category, float p) {
		this.category = category;
		this.p = p;
	}
	public float getP() {
		return p;
	}
	public void setP(float p) {
		this.p = p;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
	
}
