package com.buyerzone.model;

public class LeadPreference {
	private int preferenceId;
	private int supplierId;
	private String targetUrl;
	private String testUrl;
	private String userName;
	private String failoverEmail;
	private String key;
	private String value;
	private String valueToTranslate;
	private String translatedValue;
	private int translationId;
	private int mappingType;
	private String questionSeparator;
	private String questionSetSeparator;
	private int charLimit;
	
	
	public int getCharLimit() {
		return charLimit;
	}

	public void setCharLimit(int charLimit) {
		this.charLimit = charLimit;
	}

	public void setPreferenceId(int preferenceId) {
		this.preferenceId = preferenceId;
	}
	
	public int getPreferenceId() {
		return preferenceId;
	}
	
	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}
	
	public int getSupplierId() {
		return supplierId;
	}
	
	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}
	
	public String getTargetUrl() {
		return targetUrl;
	}
	
	public void setTestUrl(String testUrl) {
		this.testUrl = testUrl;
	}
	
	public String getTestUrl() {
		return testUrl;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setFailoverEmail(String failoverEmail) {
		this.failoverEmail = failoverEmail;
	}
	
	public String getFailoverEmail() {
		return failoverEmail;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setTranslatedValue(String translatedValue) {
		this.translatedValue = translatedValue;
	}
	
	public String getTranslatedValue() {
		return translatedValue;
	}
	
	public void setValueToTranslate(String valueToTranslate) {
		this.valueToTranslate = valueToTranslate;
	}
	
	public String getValueToTranslate() {
		return valueToTranslate;
	}
	
	public void setTranslationId(int translationId) {
		this.translationId = translationId;
	}
	
	public int getTranslationId() {
		return translationId;
	}
	
	public void setMappingType(int mappingType) {
		this.mappingType = mappingType;
	}
	
	public int getMappingType() {
		return mappingType;
	}
	
	public void setQuestionSeparator(String questionSeparator) {
		this.questionSeparator = questionSeparator;
	}
	
	public String getQuestionSeparator() {
		return questionSeparator;
	}
	
	public void setQuestionSetSeparator(String questionSetSeparator) {
		this.questionSetSeparator = questionSetSeparator;
	}
	
	public String getQuestionSetSeparator() {
		return questionSetSeparator;
	}
	
}

