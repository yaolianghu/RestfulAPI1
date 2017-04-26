package com.buyerzone.util.report;

import java.util.Date;

/**
 * Created by isantiago on 6/1/16.
 */
public abstract class ReportCriteria {

    private ReportName reportName;
    private String startDate;
    private String endDate;

    public ReportCriteria(ReportName reportName){
        this.reportName = reportName;
    }

    public ReportName getReportName() {
        return reportName;
    }

    public void setReportName(ReportName reportName) {
        this.reportName = reportName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public enum ReportName{
        EQLEADS_REPORT("eqleads-report");

        private String name;

        ReportName(String name){
            this.name = name;
        }

    }
}
