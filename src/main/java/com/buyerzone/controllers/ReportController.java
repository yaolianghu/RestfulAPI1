package com.buyerzone.controllers;

import com.buyerzone.service.ReportService;
import com.buyerzone.util.DateUtil;
import com.buyerzone.util.report.ReportCriteria;
import com.buyerzone.util.report.impl.EQReportCriteria;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by isantiago on 5/18/16.
 */
@RestController
public class ReportController {
    private Logger logger = Logger.getLogger(ReportController.class);

    @Autowired
    private ReportService reportService;

    @InitBinder
    public void customizeBinding(WebDataBinder binder){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
    @RequestMapping(method= RequestMethod.GET, value="/econnect/report/leads")
    public ResponseEntity getEQLeadsReport(@RequestParam("suppliercategoryid") long supplierCategoryId, @RequestParam("startdate") Date startDate, @RequestParam("enddate") Date endDate, HttpServletResponse response) {
        EQReportCriteria reportCriteria = new EQReportCriteria(ReportCriteria.ReportName.EQLEADS_REPORT);
        reportCriteria.setStartDate(DateUtil.getFormattedDate(startDate,DateUtil.DATE));
        reportCriteria.setEndDate(DateUtil.getEndOfDay(endDate, DateUtil.DATE_TIME));
        reportCriteria.setSupplierEntityOwnerId(supplierCategoryId);
        HashMap<String, Object> output = new HashMap<>();
        output.put("Report Name", ReportCriteria.ReportName.EQLEADS_REPORT);
        output.put("supplierCategoryId", supplierCategoryId);
        output.put("startDate", reportCriteria.getStartDate());
        output.put("endDate", reportCriteria.getEndDate());
        try{
            byte[] reportData = reportService.getReportData(reportCriteria);

            if (reportData != null && reportData.length > 0) {
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Disposition", String.format("filename=EQleads_%s_%s_%s.csv",reportCriteria.getStartDate(), reportCriteria.getEndDate(), supplierCategoryId));
                return ResponseEntity.ok().headers(headers).contentLength(reportData.length).contentType(MediaType.parseMediaType("text/csv")).body(new InputStreamResource(new ByteArrayInputStream(reportData)));

            }else{
                output.put("result","No data found");
                output.put("message","No results found for the search options");
                return new ResponseEntity<>(output,HttpStatus.OK);
            }
        }catch(Exception e){
            logger.error(e.getStackTrace());
            output = new HashMap<>();
            String errorMessage = String.format("An unexpected error occurred while retrieving report data for supplier category id [%s]", supplierCategoryId);
            output.put("stacktrace", e.getStackTrace());
            output.put("result","Failed");
            output.put("message",errorMessage);
            logger.warn(errorMessage);
            return new ResponseEntity<>(output, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
