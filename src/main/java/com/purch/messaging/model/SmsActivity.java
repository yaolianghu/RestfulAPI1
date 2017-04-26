package com.purch.messaging.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class SmsActivity {
	private long mobileNumber;
    private String keyword;
    private long msgId;
    private String msgType;
    private long msgTimestamp;
    private String msgText;
    private String msgOrigin;
    private String apiUrl;
    private String apiResponse;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ssZ")
    private Date dateCreated;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ssZ")
    private Date dateDelivered;

    public long getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(long mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
	
	public String getMsgType() {
		return msgType;
	}
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	
	public long getMsgTimestamp() {
		return msgTimestamp;
	}
	public void setMsgTimestamp(long msgTimestamp) {
		this.msgTimestamp = msgTimestamp;
	}
	
	public String getMsgText() {
		return msgText;
	}
	public void setMsgText(String msgText) {
		this.msgText = msgText;
	}
	
	public String getMsgOrigin() {
		return msgOrigin;
	}
	public void setMsgOrigin(String msgOrigin) {
		this.msgOrigin = msgOrigin;
	}
	
	public String getApiUrl() {
		return apiUrl;
	}
	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}
	public String getApiResponse() {
		return apiResponse;
	}
	public void setApiResponse(String apiResponse) {
		this.apiResponse = apiResponse;
	}
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateDelivered() {
		return dateDelivered;
	}
	public void setDateDelivered(Date dateDelivered) {
		this.dateDelivered = dateDelivered;
	}
}
