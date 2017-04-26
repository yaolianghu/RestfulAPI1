package com.buyerzone.model.cld;

import java.util.HashMap;
import java.util.List;

public class Question {
	private int questionSetId;
	private String questionId;
	private String buyerText;
	private String sourceValue;
	private String type;
	private List<HashMap<String,String>> answers;
	 
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	 
	public String getQuestionId() {
		return questionId;
	}
	 
	public void setBuyerText(String buyerText) {
		this.buyerText = buyerText;
	}
	
	public String getBuyerText() {
		return buyerText;
	}

	public String getSourceValue() {
		return sourceValue;
	}

	public void setSourceValue(String sourceValue) {
		this.sourceValue = sourceValue;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
	public void setAnswers(List<HashMap<String, String>> answers) {
		this.answers = answers;
	}
	
	public List<HashMap<String, String>> getAnswers() {
		return answers;
	}
	
	public void setQuestionSetId(int questionSetId) {
		this.questionSetId = questionSetId;
	}
	
	public int getQuestionSetId() {
		return questionSetId;
	}

}
