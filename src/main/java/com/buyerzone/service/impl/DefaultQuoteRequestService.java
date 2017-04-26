package com.buyerzone.service.impl;

import com.buyerzone.dao.mapper.EConnectMapper;
import com.buyerzone.dao.mapper.EmailOnlyLeadsMapper;
import com.buyerzone.model.econnect.*;
import com.buyerzone.service.QuoteRequestService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by isantiago on 9/2/16.
 */
@Service
@Cacheable
@Transactional
public class DefaultQuoteRequestService implements QuoteRequestService {
    private static Logger logger = Logger.getLogger(DefaultQuoteRequestService.class);

    @Autowired
    private EmailOnlyLeadsMapper emailOnlyLeadsMapper;
    @Autowired
    private EConnectMapper eConnectMapper;

    @Override
    public ECategory findCategory(long categoryId) {
        return this.eConnectMapper.getCategoryById(categoryId);
    }

    @Override
    public long getQuestionSetId(long categoryId, String questionSetVersion) {
        return this.emailOnlyLeadsMapper.getQuestionSetId(categoryId, questionSetVersion);
    }

    @Override
    public List<Question> getQuestions(long questionSetId) {
        return this.emailOnlyLeadsMapper.getQuestions(questionSetId);
    }

    @Override
    public EmailOnlyLead getEmailOnlyLead(long emailOnlyLeadId) {
        return this.emailOnlyLeadsMapper.getQuoteRequest(emailOnlyLeadId);
    }

    @Override
    public List<QuoteRequestParam> getQuoteRequestParamList(long quoteRequestId) {
        return this.emailOnlyLeadsMapper.getQuoteRequestParams(quoteRequestId);
    }

    @Override
    public List<QuoteRequestParam> getQuoteRequestParamList(long quoteRequestId, String paramValue) {
        return this.emailOnlyLeadsMapper.getQuoteRequestParamsByParamKey(quoteRequestId, paramValue);
    }

    @Override
    public Answer getAnswer(Question question, String paramValue) {
        return this.emailOnlyLeadsMapper.getAnswer(question.getQuestionId(), paramValue);
    }

    @Override
    public List<Answer> getAnswers(Question question) {
        return this.emailOnlyLeadsMapper.getAnswers(question.getQuestionId());
    }
    @Override
    public List<EmailOnlyLead> getQuoteRequests(long supplierCategoryId, String startDate, String endDate){
        return this.emailOnlyLeadsMapper.getQuoteRequests(supplierCategoryId, startDate, endDate);
    }

    public List<EmailLeadsLog> getEmailLeadsLogs(long supplierCategoryId, String startDate, String endDate){
        return this.emailOnlyLeadsMapper.getEQLeadsLogs(supplierCategoryId, startDate, endDate);
    }
    public List<Map<String,Object>> getQuoteQuestionAnswerDetails(EmailOnlyLead quoteRequest, List<Question> questions){

        List<Map<String,Object>> questionAnswerList = new ArrayList<>();
        List<String> answerKV = null;
        HashMap<String,Object> questionProperties = null;
        String displayOrder = null;
        List<QuoteRequestParam> quoteRequestParams = this.getQuoteRequestParamList(quoteRequest.getQuoteRequestId());
        for(Question question : questions){
            displayOrder = String.valueOf(question.getDisplayOrder());
            String answerText = "";
            switch (question.getType()) {

                case "multiplechoice": {
                    answerKV = new ArrayList<>();
                    ArrayList<String> paramValues = new ArrayList<>();
                    List<QuoteRequestParam> filteredQuoteRequestParams = filterParamsByParamKey(quoteRequestParams, question.getXmlId());
                    if (filteredQuoteRequestParams != null)
                        paramValues = getParamValues(filteredQuoteRequestParams);
                    String other = "";
                    if (paramValues.size() > 0) {
                        for (String paramValue : paramValues) {
                            Answer answer = this.getAnswer(question, paramValue);
                            if (answer != null) {
                                if (answer.isOther()) {
                                    String xmlId = String.format("%s%s", question.getXmlId(), "_other_text");
                                    filteredQuoteRequestParams = filterParamsByParamKey(quoteRequestParams, xmlId);
                                    String otherValues = "";
                                    if (filteredQuoteRequestParams != null && filteredQuoteRequestParams.size() > 0)
                                        otherValues = filteredQuoteRequestParams.get(0).getParamValue();
                                    if (!otherValues.equals("")) {
                                        if (!(answer.getLabel().trim().endsWith(":") || answer.getLabel().trim().endsWith("?"))) {
                                            other = String.format("%s %s", answer.getLabel() + ":", otherValues);
                                        } else {
                                            other = String.format("%s %s", answer.getLabel(), otherValues);
                                        }
                                    }
                                } else {
                                    answerKV.add(answer.getLabel());
                                }
                            }
                        }
                    }
                    StringBuilder questionText = new StringBuilder(question.getSupplierText());
                    if (!(questionText.toString().trim().endsWith(":") || questionText.toString().trim().endsWith("?")))
                        questionText.append(":");
                    questionProperties = new HashMap<>();
                    questionProperties.put("displayOrder", Integer.parseInt(displayOrder));
                    questionProperties.put("question", questionText.toString());
                    String answerList = StringUtils.collectionToDelimitedString(answerKV, ", ");
                    questionProperties.put("answer", String.format("%s %s", answerList, other));
                    questionAnswerList.add(questionProperties);
                    break;
                }
                case "textmultiple": {
                    List<Answer> answers = this.getAnswers(question);
                    Map<String, String> subQA = new HashMap<>();
                    for (Answer answer : answers) {
                        StringBuilder sQuestion = new StringBuilder(answer.getLabel());
                        if (!(sQuestion.toString().trim().endsWith(":") || sQuestion.toString().trim().endsWith("?")))
                            sQuestion.append(":");
                        List<QuoteRequestParam> filteredParamsByParamKeyc= filterParamsByParamKey(quoteRequestParams, answer.getXmlId());
                        String sAnswer = "";
                        if (filteredParamsByParamKeyc != null && filteredParamsByParamKeyc.size() > 0)
                            sAnswer = filteredParamsByParamKeyc.get(0).getParamValue();
                        subQA.put(sQuestion.toString(), sAnswer);
                    }
                    StringBuilder questionText = new StringBuilder(question.getSupplierText());
                    if (!(questionText.toString().trim().endsWith(":") || questionText.toString().trim().endsWith("?")))
                        questionText.append(":");
                    questionProperties = new HashMap<>();
                    questionProperties.put("displayOrder", Integer.parseInt(displayOrder));
                    questionProperties.put("question", questionText.toString());
                    questionProperties.put("answer", subQA);
                    questionAnswerList.add(questionProperties);
                    break;
                }
                case "email":
                case "zipcode":
                case "textsingle":
                case "additionalnotes":
                case "textparagraph": {
                    StringBuilder questionText = new StringBuilder(question.getSupplierText());
                    if (!(questionText.toString().trim().endsWith(":") || questionText.toString().trim().endsWith("?")))
                        questionText.append(":");
                    answerText = "";
                    List<QuoteRequestParam> filteredParamsByParamKey = filterParamsByParamKey(quoteRequestParams, question.getXmlId());
                    if (filteredParamsByParamKey != null && filteredParamsByParamKey.size() > 0)
                        answerText = filteredParamsByParamKey.get(0).getParamValue();
                    questionProperties = new HashMap<>();
                    questionProperties.put("displayOrder", Integer.parseInt(displayOrder));
                    questionProperties.put("question", questionText.toString());
                    questionProperties.put("answer", answerText);
                    questionAnswerList.add(questionProperties);
                    break;
                }
                default:
                    break;
            }
        }

        return questionAnswerList;

    }
    public List<QuoteRequestParam> filterParamsByParamKey(List<QuoteRequestParam> quoteRequestParams, String paramKey){
        List<QuoteRequestParam> newQuoteRequestParam = new ArrayList<QuoteRequestParam>();
        for(QuoteRequestParam quoteRequestParam: quoteRequestParams){
            if(quoteRequestParam.getParamKey().equals(paramKey))
                newQuoteRequestParam.add(quoteRequestParam);
        }
        return newQuoteRequestParam;
    }
    public List<Question> getQuestions(EmailOnlyLead quoteRequest) {

        List<Question> questions = new ArrayList<>();
        ECategory category = this.findCategory(quoteRequest.getCategory().getCategoryId());
        if(category != null){
            //String contentPath = category.getContentPath();
            List<QuoteRequestParam> quoteRequestParams = this.getQuoteRequestParamList(quoteRequest.getQuoteRequestId(), "_questionSetVersionId_");
            if((quoteRequestParams != null) && (quoteRequestParams.size() > 0)){
                QuoteRequestParam quoteRequestParam = quoteRequestParams.get(0);
                String questionSetVersion = quoteRequestParam.getParamValue();
                long questionSetId = this.getQuestionSetId(category.getCategoryId(), questionSetVersion);
                questions = this.getQuestions(questionSetId);
            }

        }else{
            logger.error(String.format("Invalid category id [%s]", quoteRequest.getCategory().getCategoryId()));
        }
        return questions;
    }

    private ArrayList<String> getParamValues(List<QuoteRequestParam> quoteRequestParams){
        ArrayList<String> paramValues = new ArrayList<>();
        for(QuoteRequestParam quoteRequestParam : quoteRequestParams) {
            paramValues.add(quoteRequestParam.getParamValue());
        }
        return paramValues;
    }
}
