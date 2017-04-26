package com.buyerzone.dao.mapper;

import com.buyerzone.model.econnect.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by isantiago on 9/2/16.
 */
public interface EmailOnlyLeadsMapper {
    @Results(value={
            @Result(column = "email_leads_log_id", property = "emailLeadsLogId"),
            @Result(column = "quote_request_id", property = "quoteRequestId"),
            @Result(column = "type_id", property = "typeId"),
            @Result(column = "category_id", property = "categoryId"),
            @Result(column = "supplier_entity_owner_id", property = "supplierEntityOwnerId"),
            @Result(column = "credit_status", property = "creditStatus"),
            @Result(column = "credit_reason", property = "creditReason"),
            @Result(column = "date_created", property = "dateCreated", javaType = String.class),
            @Result(column = "date_modified", property = "dateModified", javaType = String.class),
            @Result(column = "email", property = "email"),
            @Result(column = "delivery_status", property = "deliveryStatus"),
            @Result(column = "partner_id", property = "partner_id")}
    )
    @Select("select * from email_leads_log where supplier_entity_owner_id = #{supplierCategoryId} and date_created between DATE_FORMAT(#{startDate}, '%Y-%m-%d') and DATE_FORMAT(#{endDate}, '%Y-%m-%d') " +
            "and now()")
    List<EmailLeadsLog> getEQLeadsLogs(@Param("supplierCategoryId") long supplierCategoryId, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Results(value={
        @Result(column = "quote_request_id", property = "quoteRequestId"),
            @Result(column = "date_created", property = "dateCreated", javaType=String.class),
            @Result(column = "category_id", property = "category", javaType = ECategory.class, one = @One(select="getCategoryById")),
            @Result(column = "email", property = "email"),
            @Result(column = "zipcode", property = "zipcode"),
            @Result(column = "quote_request_status", property = "quoteRequestStatus"),
            @Result(column = "companyId", property = "companyId")
    })
    @Select("select * from quote_request qr join buyer_registration br on qr.buyer_registration_id = br.buyer_registration_id where quote_request_id = #{quoteRequestId}")
    EmailOnlyLead getQuoteRequest(@Param("quoteRequestId") long quoteRequestId);

    @Results(value={
            @Result(column = "quote_request_id", property = "quoteRequestId"),
            @Result(column = "date_created", property = "dateCreated", javaType=String.class),
            @Result(column = "category_id", property = "category", javaType = ECategory.class, one = @One(select="getCategoryById")),
            @Result(column = "email", property = "email"),
            @Result(column = "zipcode", property = "zipcode"),
            @Result(column = "quote_request_status", property = "quoteRequestStatus"),
            @Result(column = "companyId", property = "companyId")
    })
    @Select("select * from quote_request qr " +
            "join buyer_registration br on qr.buyer_registration_id = br.buyer_registration_id " +
            "join email_leads_log ell on ell.quote_request_id = qr.quote_request_id " +
            "where ell.date_created between #{startDate} and #{endDate} and ell.supplier_entity_owner_id = #{supplierCategoryId}")
    List<EmailOnlyLead> getQuoteRequests(@Param("supplierCategoryId") long supplierCategoryId, @Param("startDate") String startDate, @Param("endDate") String endDate);


    @Select("select qrp.quote_request_param_id as quoteRequestParamId, qrp.quote_request_id as quoteRequestId, qrp.param_key as paramKey, qrp.param_value as paramValue from quote_request_param qrp where quote_request_id = #{quoteRequestId}")
    List<QuoteRequestParam> getQuoteRequestParams(@Param("quoteRequestId") long quoteRequestId);

    @Select("select qrp.quote_request_param_id as quoteRequestParamId, qrp.quote_request_id as quoteRequestId, qrp.param_key as paramKey, qrp.param_value as paramValue from quote_request_param qrp where qrp.param_key = #{paramKey} and qrp.quote_request_id = #{quoteRequestId}")
    List<QuoteRequestParam> getQuoteRequestParamsByParamKey(@Param("quoteRequestId") long quoteRequestId, @Param("paramKey") String paramKey);

    @Select("select c.category_id as categoryId, c.display_name as displayName, c.content_path as contentPath from category c where c.category_id = #{categoryId}")
    ECategory getCategoryById(@Param("categoryId") long categoryId);

    @Select("select a.other as isOther, a.xml_id as xmlId, a.label as label from answer a where a.question_id = #{questionId} and a.xml_id = \'${param2}\' ")
    Answer getAnswer(@Param("questionId") long questionId, @Param("paramValue") String paramValue);

    @Select("select a.other as isOther, a.xml_id as xmlId, a.label as label from answer a where a.question_id = #{questionId}")
    List<Answer> getAnswers(@Param("questionId") long questionId);

    @Select("select q.question_id as questionId, q.supplier_text as supplierText, q.xml_id as xmlId, q.type, q.display_order as displayOrder from question q where q.question_set_id = #{questionSetId}")
    List<Question> getQuestions(@Param("questionSetId") long questionSetId);

    @Select("select question_set_id from question_set where category_id = #{categoryId} and version = #{param2}")
    Long getQuestionSetId(@Param("categoryId") long categoryId, @Param("questionSetVersionId") String questionSetVersionId);
}
