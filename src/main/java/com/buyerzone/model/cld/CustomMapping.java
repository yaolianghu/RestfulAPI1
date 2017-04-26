package com.buyerzone.model.cld;

import java.util.List;

public class CustomMapping {
	private int mappingId;
	private int mappingType;
	private String sourceValue;
	private String targetName;
	private int preferenceId;
	private int version;
	private String questionId;
	private int questionSetId;
	private String questionAnswerSeparator;
	private String questionSetSeparator;
	private int isLive;
	private List<Translation> answers;
	

	public int getIsLive() {
		return isLive;
	}

	public void setIsLive(int isLive) {
		this.isLive = isLive;
	}

	public String getQuestionAnswerSeparator() {
		return questionAnswerSeparator;
	}

	public void setQuestionAnswerSeparator(String questionAnswerSeparator) {
		this.questionAnswerSeparator = questionAnswerSeparator;
	}

	public String getQuestionSetSeparator() {
		return questionSetSeparator;
	}

	public void setQuestionSetSeparator(String questionSetSeparator) {
		this.questionSetSeparator = questionSetSeparator;
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public int getQuestionSetId() {
		return questionSetId;
	}

	public void setQuestionSetId(int questionSetId) {
		this.questionSetId = questionSetId;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public List<Translation> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Translation> answers) {
		this.answers = answers;
	}

	public void setMappingId(int mappingId) {
		this.mappingId = mappingId;
	}

	public int getMappingType() {
		return mappingType;
	}

	public void setMappingType(int mappingType) {
		this.mappingType = mappingType;
	}
	
	public String getSourceValue() {
		return sourceValue;
	}

	public void setSourceValue(String sourceValue) {
		this.sourceValue = sourceValue;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public int getMappingId() {
		return mappingId;
	}
	
	public int getPreferenceId() {
		return preferenceId;
	}

	public void setPreferenceId(int preferenceId) {
		this.preferenceId = preferenceId;
	}
	
}
