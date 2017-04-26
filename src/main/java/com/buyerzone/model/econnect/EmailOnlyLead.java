package com.buyerzone.model.econnect;

import java.util.Date;
import java.util.List;

/**
 * Created by isantiago on 5/18/16.
 */
public class EmailOnlyLead {

    /** Primary Key representation */
    private Long quoteRequestId;

    /** Date the quote request was created */
    private String dateCreated;

    /** Associated quote request category */
    private ECategory category;

    private String email;

    private String zipcode;

    /** Quote request status */
    private int quoteRequestStatus;

    private long companyId;

    public Long getQuoteRequestId() {
        return quoteRequestId;
    }

    public void setQuoteRequestId(Long quoteRequestId) {
        this.quoteRequestId = quoteRequestId;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public ECategory getCategory() {
        return category;
    }

    public void setCategory(ECategory category) {
        this.category = category;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public int getQuoteRequestStatus() {
        return quoteRequestStatus;
    }

    public void setQuoteRequestStatus(int quoteRequestStatus) {
        this.quoteRequestStatus = quoteRequestStatus;
    }

    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

}
