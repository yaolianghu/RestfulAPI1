package com.buyerzone.util.report.impl;

import com.buyerzone.util.report.ReportCriteria;

import java.util.Date;

/**
 * Created by isantiago on 6/3/16.
 */
public class EQReportCriteria extends ReportCriteria{

    public long getSupplierEntityOwnerId() {
        return supplierEntityOwnerId;
    }

    public void setSupplierEntityOwnerId(long supplierEntityOwnerId) {
        this.supplierEntityOwnerId = supplierEntityOwnerId;
    }

    private long supplierEntityOwnerId;

    public EQReportCriteria(ReportName reportName){
        super(reportName);
    }

    public ReportName getReportName() {
        return super.getReportName();
    }

}
