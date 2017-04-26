package com.buyerzone.model.cld;

import java.util.List;

public class CategoryDefinition {
	private String displayName;
	private int supplierCatId;
	private int categoryId;
	private int questionSetId;
	private int preferenceId;
	private List<RegistrationFields> registration;
	private List<Question> questions;
	
	
	public int getSupplierCatId() {
		return supplierCatId;
	}

	public void setSupplierCatId(int supplierCatId) {
		this.supplierCatId = supplierCatId;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
	
	public int getCategoryId() {
		return categoryId;
	}
	
	public void setQuestionSetId(int questionSetId) {
		this.questionSetId = questionSetId;
	}
	
	public int getQuestionSetId() {
		return questionSetId;
	}
	
	public void setRegistration(List<RegistrationFields> registration) {
		this.registration = registration;
	}
	
	public List<RegistrationFields> getRegistration() {
		return registration;
	}
	
	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}
	
	public List<Question> getQuestions() {
		return questions;
	}
	
	public void setPreferenceId(int preferenceId) {
		this.preferenceId = preferenceId;
	}
	
	public int getPreferenceId() {
		return preferenceId;
	}
	
}

