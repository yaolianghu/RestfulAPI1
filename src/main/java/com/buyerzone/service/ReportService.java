package com.buyerzone.service;

import com.buyerzone.util.report.impl.EQReportCriteria;

/**
 * Created by isantiago on 9/8/16.
 */
public interface ReportService {
    byte[] getReportData(EQReportCriteria eqReportCriteria);
}
