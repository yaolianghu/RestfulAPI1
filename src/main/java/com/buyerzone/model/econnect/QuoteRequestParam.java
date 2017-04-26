package com.buyerzone.model.econnect;

/**
 * Created by isantiago on 9/2/16.
 */
public class QuoteRequestParam {
    /** Primary key representation */
    long quoteRequestParamId;

    /** Associated quote request */
    long quoteRequestId;

    /** Quote request parameter key. (Question Name) */
    String paramKey;

    /** Quote request parameter question response value(s) */
    String paramValue;

    public long getQuoteRequestParamId() {
        return quoteRequestParamId;
    }

    public void setQuoteRequestParamId(long quoteRequestParamId) {
        this.quoteRequestParamId = quoteRequestParamId;
    }

    public long getQuoteRequestId() {
        return quoteRequestId;
    }

    public void setQuoteRequestId(long quoteRequestId) {
        this.quoteRequestId = quoteRequestId;
    }

    public String getParamKey() {
        return paramKey;
    }

    public void setParamKey(String paramKey) {
        this.paramKey = paramKey;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }
}
