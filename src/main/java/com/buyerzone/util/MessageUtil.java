package com.buyerzone.util;

public class MessageUtil {
	
	public static String successMessage(String type, long quoteId, int responseCode, String targetUrl, String responseMessage, String data) {
		String message = "";
		if(quoteId > 0)
			message = String.format("Successfully sent. Type: [%s]. Quote request id: [%d]. Response code: [%d]. Target url: [%s]. Date sent: [%s]. Response message: [%s].", type, quoteId, responseCode, targetUrl, data, responseMessage);
		else 
			message = String.format("Successfully sent. Type: [%s]. Quote request id: [%d]. Response code: [%d]. Target url: [%s]. Date sent: [%s]. Response message: [%s].", type, -1, responseCode, targetUrl, data, responseMessage);
		
		return message;
	}
	
	public static String failedMessage(String type, long quoteId, int responseCode, String targetUrl, String responseMessage, String data) {
		String message = "";
		if(quoteId >0)
			message = String.format("Failed to send. Type: [%s]. Quote request id: [%d]. Response code: [%d]. Target url: [%s]. Date sent: [%s]. Response message: [%s].", type, quoteId, responseCode, targetUrl, data, responseMessage);
		else
			message = String.format("Failed to send. Type: [%s]. Quote request id: [%d]. Response code: [%d]. Target url: [%s]. Date sent: [%s]. Response message: [%s].", type, -1, responseCode, targetUrl, data, responseMessage);
		
		return message;
	}
	
	public static String errorMessage(String type, long quoteId, String targetUrl, String developerMessage, String errorMessage) {
		String message = "";
		if(quoteId >0)
			message = String.format("Datazone Internal Server Error. Type: [%s]. Quote request id: [%d]. TagetUrl: [%s]. Developer message: [%s]. Error message: [%s].", type, quoteId, targetUrl, developerMessage, errorMessage);
		else
			message = String.format("Datazone Internal Server Error. Type: [%s]. Quote request id: [%d]. TagetUrl: [%s]. Developer message: [%s]. Error message: [%s].", type, -1, targetUrl, developerMessage, errorMessage);
		
		return message;
	}
	
	
}
