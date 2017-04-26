package com.buyerzone.model.econnect;

/**
 * Created by isantiago on 9/2/16.
 */
public class EmailLeadsLog {
    private long emailLeadsLogId;
    private long quoteRequestId;
    private int typeId;
    private long categoryId;
    private long supplierEntityOwnerId;
    private int creditStatus;
    private String creditReason;
    private String dateCreated;
    private String dateModified;
    private String email;
    private int deliveryStatus;
    private long partnerId;

    public long getEmailLeadsLogId() {
        return emailLeadsLogId;
    }

    public void setEmailLeadsLogId(long emailLeadsLogId) {
        this.emailLeadsLogId = emailLeadsLogId;
    }

    public long getQuoteRequestId() {
        return quoteRequestId;
    }

    public void setQuoteRequestId(long quoteRequestId) {
        this.quoteRequestId = quoteRequestId;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public long getSupplierEntityOwnerId() {
        return supplierEntityOwnerId;
    }

    public void setSupplierEntityOwnerId(long supplierEntityOwnerId) {
        this.supplierEntityOwnerId = supplierEntityOwnerId;
    }

    public int getCreditStatus() {
        return creditStatus;
    }

    public void setCreditStatus(int creditStatus) {
        this.creditStatus = creditStatus;
    }

    public String getCreditReason() {
        return creditReason;
    }

    public void setCreditReason(String creditReason) {
        this.creditReason = creditReason;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(int deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(long partnerId) {
        this.partnerId = partnerId;
    }
}