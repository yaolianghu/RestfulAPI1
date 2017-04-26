package com.purch.messaging.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SmsMessage {
	private final String keyword;
	private final String type;
	private final long mobileNumber;
	private String text;
	private String origin;
	private long timestamp;
	private String apiUrl;
	private String apiResponse;
	private long msgId;
	
	public SmsMessage(String keyword, String type, long mobileNumber) {
		this.keyword = keyword;
		this.type = type;
		this.mobileNumber = mobileNumber;
		this.timestamp = System.currentTimeMillis();
	}

	public String getKeyword() {
		return keyword;
	}
	
	public String getType() {
		return type;
	}
	
	public long getMobileNumber() {
		return mobileNumber;
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	@JsonIgnore
	public String getTextTruncated() {
		return (text != null && text.length() > 252) ? text.substring(0, 252) + "..." : text;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getOrigin() {
		return this.origin;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public String getApiUrl() {
		return apiUrl;
	}

	@JsonIgnore
	public String getApiUrlTruncated() {
		return (apiUrl != null && apiUrl.length() > 252) ? apiUrl.substring(0, 252) + "..." : apiUrl;
	}

	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	public String getApiResponse() {
		return apiResponse;
	}

	@JsonIgnore
	public String getApiResponseTruncated() {
		return (apiResponse != null && apiResponse.length() > 252) ? apiResponse.substring(0, 252) + "..." : apiResponse;
	}

	public void setApiResponse(String apiResponse) {
		this.apiResponse = apiResponse;
	}

	public long getMsgId() {
		return msgId;
	}

	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("keyword: ").append(keyword).append("\n");
		sb.append("type: ").append(type).append("\n");
		sb.append("mobileNumber: ").append(mobileNumber).append("\n");
		sb.append("text: ").append(text).append("\n");
		sb.append("origin: ").append(origin).append("\n");
		sb.append("timestamp: ").append(timestamp).append("\n");
		sb.append("apiUrl: ").append(apiUrl).append("\n");
		sb.append("apiResponse: ").append(apiResponse).append("\n");
		sb.append("msgId: ").append(msgId).append("\n");

		return sb.toString();
	}
}
