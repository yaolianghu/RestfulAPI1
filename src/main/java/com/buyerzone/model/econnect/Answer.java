package com.buyerzone.model.econnect;

/**
 * Created by isantiago on 5/18/16.
 */
public class Answer {
    private String label;
    private boolean isOther;
    private String xmlId;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isOther() {
        return isOther;
    }

    public void setOther(boolean other) {
        isOther = other;
    }

    public String getXmlId() {
        return xmlId;
    }

    public void setXmlId(String xmlId) {
        this.xmlId = xmlId;
    }
}
