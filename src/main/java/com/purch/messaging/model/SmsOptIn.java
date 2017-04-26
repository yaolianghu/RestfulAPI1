package com.purch.messaging.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class SmsOptIn {
    private long mobileNumber;
    private String keyword;
    private String optInStatus;
    private long msgId;
    private long msgTimestamp;
    private String msgText;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ssZ")
    private Date dateCreated;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ssZ")
    private Date dateModified;
    
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

	public String getOptInStatus() {
		return optInStatus;
	}
	public void setOptInStatus(String optInStatus) {
		this.optInStatus = optInStatus;
	}

	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
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

	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateModified() {
		return dateModified;
	}
	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}   
}
