package com.buyerzone.model.econnect;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown=false)
public class EConnectSettings {
	private long emailLeadsClientId;
	private int supplierCategoryId;
	private String supplierName;
	private int feePerLead;
	private String startDate;
	private String endDate;
	private String leadCap;
	private int categoryId;
	private String categoryName;
	private String ftpHost;
	private String ftpUser;
	private String ftpPassword;
	private String ftpPassive;
	private String ftpRemoteDirectory;
	private String tsvEmail;
	private String email;
	private String generateEmail;
	private String generateTsvEmail;
	private String generateTsvFtp;
	private String generateTsvEmailDaily;
	private String generateXml;
	private String xmlPostParameter;
	private String xmlPostUrl;
	private String sendRfqNotification;
	private String useGeoCoverage;
	private String weekDaysOnly;
	private String weeklyCap;
	private String dailyCap;
	private String dailyCapLimit;
	private String autoResponder;
	private String active;
	private String createdDate;
	private String modifiedDate;
	private String postToApi;
	private String ftpsEnabled;
	private String useFullCsvHeader;
	
	/**
	 * @return the supplierCategoryId
	 */
	public int getSupplierCategoryId() {
		return supplierCategoryId;
	}
	/**
	 * @param supplierCategoryId the supplierId to set
	 */
	public void setSupplierCategoryId(int supplierCategoryId) {
		this.supplierCategoryId = supplierCategoryId;
	}
	/**
	 * @return the feePerLead
	 */
	public int getFeePerLead() {
		return feePerLead;
	}
	/**
	 * @param feePerLead the feePerLead to set
	 */
	public void setFeePerLead(int feePerLead) {
		this.feePerLead = feePerLead;
	}
	/**
	 * @return the startDate
	 */
	public String getStartDate() {
		return startDate;
	}
	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(String startDate) {
		Calendar c = GregorianCalendar.getInstance();

		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
			c.setTime(sdf.parse(startDate));
			this.startDate = sdf.format(c.getTime()).toString();
		}catch(Exception e){
			e.getCause();
			e.printStackTrace();
			this.startDate = "InvalidDateFormat";
		}	
	}
	/**
	 * @return the endDate
	 */
	public String getEndDate() {
		return endDate;
	}
	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(String endDate) {
		Calendar c = GregorianCalendar.getInstance();

		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
			c.setTime(sdf.parse(endDate));
			this.endDate = sdf.format(c.getTime()).toString();
		}catch(ParseException pe){
			pe.getCause();
			pe.printStackTrace();
			this.endDate = "InvalidDateFormat";
		}	
	}
	/**
	 * @return the leadCap
	 */
	public String getLeadCap() {
		return leadCap;
	}
	/**
	 * @param leadCap the leadCap to set
	 */
	public void setLeadCap(String leadCap) {
		this.leadCap = leadCap;
	}
	/**
	 * @return the categoryId
	 */
	public int getCategoryId() {
		return categoryId;
	}
	/**
	 * @param categoryId the categoryId to set
	 */
	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
	/**
	 * @return the categoryName
	 */
	public String getCategoryName() {
		return categoryName;
	}
	/**
	 * @param categoryName the categoryName to set
	 */
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	/**
	 * @return the ftpHost
	 */
	public String getFtpHost() {
		return ftpHost;
	}
	/**
	 * @param ftpHost the ftpHost to set
	 */
	public void setFtpHost(String ftpHost) {
		this.ftpHost = ftpHost;
	}
	/**
	 * @return the ftpUser
	 */
	public String getFtpUser() {
		return ftpUser;
	}
	/**
	 * @param ftpUser the ftpUser to set
	 */
	public void setFtpUser(String ftpUser) {
		this.ftpUser = ftpUser;
	}
	/**
	 * @return the ftpPassword
	 */
	public String getFtpPassword() {
		return ftpPassword;
	}
	/**
	 * @param ftpPassword the ftpPassword to set
	 */
	public void setFtpPassword(String ftpPassword) {
		this.ftpPassword = ftpPassword;
	}
	/**
	 * @return the ftpPassive
	 */
	public String getFtpPassive() {
		return ftpPassive;
	}
	/**
	 * @param ftpPassive the ftpPassive to set
	 */
	public void setFtpPassive(String ftpPassive) {
		this.ftpPassive = ftpPassive;
	}
	/**
	 * @return the ftpRemoteDirectory
	 */
	public String getFtpRemoteDirectory() {
		return ftpRemoteDirectory;
	}
	/**
	 * @param ftpRemoteDirectory the ftpRemoteDirectory to set
	 */
	public void setFtpRemoteDirectory(String ftpRemoteDirectory) {
		this.ftpRemoteDirectory = ftpRemoteDirectory;
	}
	/**
	 * @return the tsvEmail
	 */
	public String getTsvEmail() {
		return tsvEmail;
	}
	/**
	 * @param tsvEmail the tsvEmail to set
	 */
	public void setTsvEmail(String tsvEmail) {
		this.tsvEmail = tsvEmail;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the generateEmail
	 */
	public String getGenerateEmail() {
		return generateEmail;
	}
	/**
	 * @param generateEmail the generateEmail to set
	 */
	public void setGenerateEmail(String generateEmail) {
		this.generateEmail = generateEmail;
	}
	/**
	 * @return the generateTsvEmail
	 */
	public String getGenerateTsvEmail() {
		return generateTsvEmail;
	}
	/**
	 * @param generateTsvEmail the generateTsvEmail to set
	 */
	public void setGenerateTsvEmail(String generateTsvEmail) {
		this.generateTsvEmail = generateTsvEmail;
	}
	/**
	 * @return the generateTsvFtp
	 */
	public String getGenerateTsvFtp() {
		return generateTsvFtp;
	}
	/**
	 * @param generateTsvFtp the generateTsvFtp to set
	 */
	public void setGenerateTsvFtp(String generateTsvFtp) {
		this.generateTsvFtp = generateTsvFtp;
	}
	/**
	 * @return the generateTsvEmailDaily
	 */
	public String getGenerateTsvEmailDaily() {
		return generateTsvEmailDaily;
	}
	/**
	 * @param generateTsvEmailDaily the generateTsvEmailDaily to set
	 */
	public void setGenerateTsvEmailDaily(String generateTsvEmailDaily) {
		this.generateTsvEmailDaily = generateTsvEmailDaily;
	}
	/**
	 * @return the generateXml
	 */
	public String getGenerateXml() {
		return generateXml;
	}
	/**
	 * @param generateXml the generateXml to set
	 */
	public void setGenerateXml(String generateXml) {
		this.generateXml = generateXml;
	}
	/**
	 * @return the xmlPostParameter
	 */
	public String getXmlPostParameter() {
		return xmlPostParameter;
	}
	/**
	 * @param xmlPostParameter the xmlPostParameter to set
	 */
	public void setXmlPostParameter(String xmlPostParameter) {
		this.xmlPostParameter = xmlPostParameter;
	}
	/**
	 * @return the xmlPostUrl
	 */
	public String getXmlPostUrl() {
		return xmlPostUrl;
	}
	/**
	 * @param xmlPostUrl the xmlPostUrl to set
	 */
	public void setXmlPostUrl(String xmlPostUrl) {
		this.xmlPostUrl = xmlPostUrl;
	}
	/**
	 * @return the sendRfqNotification
	 */
	public String getSendRfqNotification() {
		return sendRfqNotification;
	}
	/**
	 * @param sendRfqNotification the sendRfqNotification to set
	 */
	public void setSendRfqNotification(String sendRfqNotification) {
		this.sendRfqNotification = sendRfqNotification;
	}
	/**
	 * @return the useGeoCoverage
	 */
	public String getUseGeoCoverage() {
		return useGeoCoverage;
	}
	/**
	 * @param useGeoCoverage the useGeoCoverage to set
	 */
	public void setUseGeoCoverage(String useGeoCoverage) {
		this.useGeoCoverage = useGeoCoverage;
	}
	/**
	 * @return the weekDaysOnly
	 */
	public String getWeekDaysOnly() {
		return weekDaysOnly;
	}
	/**
	 * @param weekDaysOnly the weekDaysOnly to set
	 */
	public void setWeekDaysOnly(String weekDaysOnly) {
		this.weekDaysOnly = weekDaysOnly;
	}
	/**
	 * @return the weeklyCap
	 */
	public String getWeeklyCap() {
		return weeklyCap;
	}
	/**
	 * @param weeklyCap the weeklyCap to set
	 */
	public void setWeeklyCap(String weeklyCap) {
		this.weeklyCap = weeklyCap;
	}
	/**
	 * @return the dailyCap
	 */
	public String getDailyCap() {
		return dailyCap;
	}
	/**
	 * @param dailyCap the dailyCap to set
	 */
	public void setDailyCap(String dailyCap) {
		this.dailyCap = dailyCap;
	}
	/**
	 * @return the autoResponder
	 */
	public String getAutoResponder() {
		return autoResponder;
	}
	/**
	 * @param autoResponder the autoResponder to set
	 */
	public void setAutoResponder(String autoResponder) {
		this.autoResponder = autoResponder;
	}
	/**
	 * @return the active
	 */
	public String getActive() {
		return active;
	}
	/**
	 * @param active the active to set
	 */
	public void setActive(String active) {
		this.active = active;
	}
	/**
	 * @return the createdDate
	 */
	public String getCreatedDate() {
		return createdDate;
	}
	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(String createdDate) {
		Calendar c = GregorianCalendar.getInstance();

		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
			c.setTime(sdf.parse(createdDate));
			this.createdDate = sdf.format(c.getTime()).toString();
		}catch(ParseException pe){
			pe.getCause();
			pe.printStackTrace();
			this.createdDate = "InvalidDateFormat";
		}	
	}
	/**
	 * @return the modifiedDate
	 */
	public String getModifiedDate() {
		return modifiedDate;
	}
	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(String modifiedDate) {
		Calendar c = GregorianCalendar.getInstance();

		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
			c.setTime(sdf.parse(modifiedDate));
			this.modifiedDate = sdf.format(c.getTime()).toString();
		}catch(ParseException pe){
			pe.getCause();
			pe.printStackTrace();
			this.modifiedDate = "InvalidDateFormat";
		}	
	}
	/**
	 * @return the postToApi
	 */
	public String getPostToApi() {
		return postToApi;
	}
	/**
	 * @param postToApi the postToApi to set
	 */
	public void setPostToApi(String postToApi) {
		this.postToApi = postToApi;
	}
	/**
	 * @return the ftpsEnabled
	 */
	public String getFtpsEnabled() {
		return ftpsEnabled;
	}
	/**
	 * @param ftpsEnabled the ftpsEnabled to set
	 */
	public void setFtpsEnabled(String ftpsEnabled) {
		this.ftpsEnabled = ftpsEnabled;
	}
	/**
	 * @return the useFullCsvHeader
	 */
	public String getUseFullCsvHeader() {
		return useFullCsvHeader;
	}
	/**
	 * @param useFullCsvHeader the useFullCsvHeader to set
	 */
	public void setUseFullCsvHeader(String useFullCsvHeader) {
		this.useFullCsvHeader = useFullCsvHeader;
	}
	/**
	 * @return the emailLeadsClientId
	 */
	public long getEmailLeadsClientId() {
		return emailLeadsClientId;
	}
	/**
	 * @param emailLeadsClientId the emailLeadsClientId to set
	 */
	public void setEmailLeadsClientId(long emailLeadsClientId) {
		this.emailLeadsClientId = emailLeadsClientId;
	}
	/**
	 * @return the dailyCapLimit
	 */
	public String getDailyCapLimit() {
		return dailyCapLimit;
	}
	/**
	 * @param dailyCapLimit the dailyCapLimit to set
	 */
	public void setDailyCapLimit(String dailyCapLimit) {
		this.dailyCapLimit = dailyCapLimit;
	}
	/**
	 * @return the supplierName
	 */
	public String getSupplierName() {
		return supplierName;
	}
	/**
	 * @param supplierName the supplierName to set
	 */
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	
}