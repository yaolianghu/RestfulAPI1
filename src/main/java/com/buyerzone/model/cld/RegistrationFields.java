package com.buyerzone.model.cld;

import java.util.HashMap;
import java.util.List;

public class RegistrationFields {
	private String sourceValue;
	private List<HashMap<String,String>> answers;
	
	public void setSourceValue(String paramValue) {
		this.sourceValue = paramValue;
	}
	
	public String getSourceValue() {
		return sourceValue;
	}
	
	public void setAnswers(List<HashMap<String, String>> options) {
		this.answers = options;
	}
	
	public List<HashMap<String, String>> getAnswers() {
		return answers;
	}
	
}
