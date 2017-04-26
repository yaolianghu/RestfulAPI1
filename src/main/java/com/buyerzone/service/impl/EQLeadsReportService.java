package com.buyerzone.service.impl;

import com.buyerzone.model.econnect.*;
import com.buyerzone.service.EConnectSettingsService;
import com.buyerzone.service.QuoteRequestService;
import com.buyerzone.service.ReportService;
import com.buyerzone.util.report.FileHelper;
import com.buyerzone.util.report.impl.EQReportCriteria;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by isantiago on 9/6/16.
 */
@Service
public class EQLeadsReportService implements ReportService {

    private Logger logger = Logger.getLogger(EQLeadsReportService.class);
    @Autowired
    private QuoteRequestService quoteRequestService;

    @Override
    public byte[] getReportData(EQReportCriteria eqReportCriteria){
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        List<EmailOnlyLead> emailLeads = this.quoteRequestService.getQuoteRequests(eqReportCriteria.getSupplierEntityOwnerId(),eqReportCriteria.getStartDate(), eqReportCriteria.getEndDate());
        if(!(emailLeads.size() > 0))
            return byteStream.toByteArray();
        List<LinkedList<String>> dataStructure = new ArrayList<>();
        List<Map<String,Object>>  leadDetails;
        List<Question> questions = getQuestions(emailLeads.get(0));
        LinkedList<String> ll = new LinkedList<>();
        ll.add("ERFQ Number");
        ll.add("ERFQ Date");
        ll.add("Email");
        for(Question question : questions){
            ll.add(question.getSupplierText());
        }
        dataStructure.add(ll);
        for(EmailOnlyLead emailOnlyLead : emailLeads){
            leadDetails = quoteRequestService.getQuoteQuestionAnswerDetails(emailOnlyLead, questions);
            ll = new LinkedList<>();
            ll.add(emailOnlyLead.getQuoteRequestId().toString());

            try{
                GregorianCalendar calendar = (GregorianCalendar)Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
                calendar.setTime(df.parse(emailOnlyLead.getDateCreated()));
                ll.add(DateFormatUtils.format(calendar.getTime(),"MM/dd/yy HH:mm"));

            }catch(ParseException e){
                logger.error("Failed to create date from format, using default value instead.");
                ll.add(emailOnlyLead.getDateCreated());
            }
            ll.add(emailOnlyLead.getEmail());
            for (Map<String, Object> leadDetail : leadDetails) {
                if (leadDetail.get("answer") instanceof Map) {
                    Map<?, ?> subQA = (Map<?, ?>) leadDetail.get("answer");
                    List<String> l = new ArrayList<>();
                    for (Map.Entry<?, ?> entry : subQA.entrySet()) {
                        l.add((String) entry.getValue());
                    }
                    ll.add(StringUtils.join(l, ", "));
                } else {
                    ll.add((String) leadDetail.get("answer"));
                }
            }
            dataStructure.add(ll);
        }
        //}

        try{
            byteStream =  FileHelper.generateTsvContents(dataStructure);
        }catch(IOException e){
            logger.error(e.getMessage());
        }
        return byteStream.toByteArray();

    }
    public List<Question> getQuestions(EmailOnlyLead quoteRequest) {

        List<Question> questions = new ArrayList<>();
        ECategory category = this.quoteRequestService.findCategory(quoteRequest.getCategory().getCategoryId());
        if(category != null){
            //String contentPath = category.getContentPath();
            List<QuoteRequestParam> quoteRequestParams = this.getQuoteRequestParamList(quoteRequest.getQuoteRequestId(), "_questionSetVersionId_");
            if((quoteRequestParams != null) && (quoteRequestParams.size() > 0)){
                QuoteRequestParam quoteRequestParam = quoteRequestParams.get(0);
                String questionSetVersion = quoteRequestParam.getParamValue();
                long questionSetId = this.quoteRequestService.getQuestionSetId(category.getCategoryId(), questionSetVersion);
                questions = this.quoteRequestService.getQuestions(questionSetId);
            }

        }else{
            logger.error(String.format("Invalid category id [%s]", quoteRequest.getCategory().getCategoryId()));
        }
        return questions;
    }
    public List<QuoteRequestParam> getQuoteRequestParamList(long quoteRequestId, String paramValue){
        return this.quoteRequestService.getQuoteRequestParamList(quoteRequestId, paramValue);
    }
}

