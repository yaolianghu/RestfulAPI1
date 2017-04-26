package com.buyerzone.model;

import java.util.HashMap;

public class Lead{
	private int quoteId;
	private String FirstName;
	private String LastName;
	private String Address1;
	private String Address2;
	private String State;
	private String Zip;
	private String Country;
	private String Phone;
	private String Email;
	private HashMap<String,String> QuestionSet;
	
	public void setQuoteId(int quoteId) {
		this.quoteId = quoteId;
	}
	
	public int getQuoteId() {
		return quoteId;
	}
	
	public void setFirstName(String firstName) {
		FirstName = firstName;
	}
	
	public String getFirstName() {
		return FirstName;
	}
	
	public void setLastName(String lastName) {
		LastName = lastName;
	}
	
	public String getLastName() {
		return LastName;
	}
	
	public void setAddress1(String address1) {
		Address1 = address1;
	}
	
	public String getAddress1() {
		return Address1;
	}
	
	public void setAddress2(String address2) {
		Address2 = address2;
	}
	
	public String getAddress2() {
		return Address2;
	}
	
	public void setState(String state) {
		State = state;
	}
	
	public String getState() {
		return State;
	}
	
	public void setZip(String zip) {
		Zip = zip;
	}
	
	public String getZip() {
		return Zip;
	}
	
	public void setCountry(String country) {
		Country = country;
	}
	
	public String getCountry() {
		return Country;
	}
	
	public void setPhone(String phone) {
		Phone = phone;
	}
	
	public String getPhone() {
		return Phone;
	}
	
	public void setEmail(String email) {
		Email = email;
	}
	
	public String getEmail() {
		return Email;
	}
	
	public void setQuestionSet(HashMap<String, String> questionSet) {
		QuestionSet = questionSet;
	}
	
	public HashMap<String, String> getQuestionSet() {
		return QuestionSet;
	}
	

}