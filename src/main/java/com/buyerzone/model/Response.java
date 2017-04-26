package com.buyerzone.model;

public class Response {
	private int responseCode;
	private String responseMessage;
	private String developerMessage;
	
	
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
	
	public int getResponseCode() {
		return responseCode;
	}
	
	public String getResponseMessage() {
		return responseMessage;
	}
	
	public void setDeveloperMessage(String developerMessage) {
		this.developerMessage = developerMessage;
	}
	
	public String getDeveloperMessage() {
		return developerMessage;
	}
	
}
 