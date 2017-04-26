package com.buyerzone.service;

import com.buyerzone.model.econnect.*;

import java.util.List;
import java.util.Map;

/**
 * Created by isantiago on 9/2/16.
 */
public interface QuoteRequestService {
    ECategory findCategory(long categoryId);

    long getQuestionSetId(long categoryId, String questionSetVersion);

    List<Question> getQuestions(long questionSetId);

    EmailOnlyLead getEmailOnlyLead(long emailOnlyLeadId);

    List<QuoteRequestParam> getQuoteRequestParamList(long quoteRequestId);

    List<QuoteRequestParam> getQuoteRequestParamList(long quoteRequestId, String paramValue);

    Answer getAnswer(Question question, String paramValue);

    List<Answer> getAnswers(Question question);

    List<EmailOnlyLead> getQuoteRequests(long supplierCategoryId, String startDate, String endDate);

    List<EmailLeadsLog> getEmailLeadsLogs(long supplierCategoryId, String startDate, String endDate);

    List<Map<String,Object>> getQuoteQuestionAnswerDetails(EmailOnlyLead quoteRequest, List<Question> questions);
}
