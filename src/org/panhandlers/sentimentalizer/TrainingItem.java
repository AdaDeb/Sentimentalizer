package org.panhandlers.sentimentalizer;
import redis.clients.johm.*;
/**
 * Incomplete class for possible future Redis implementation
 * @author jesjos
 *
 */
@Model
public class TrainingItem {
	@Id
	private Long id;
	@Attribute
	private String text;
	@Attribute
	private String category;
	@Attribute
	private String sentiment;
	
	public TrainingItem() {}
	public TrainingItem(String text, String category, String sentiment) {
		this.text = text;
		this.category = category;
		this.sentiment = sentiment;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getSentiment() {
		return sentiment;
	}
	public void setSentiment(String sentiment) {
		this.sentiment = sentiment;
	}
	
}
