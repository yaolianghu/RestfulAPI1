package com.buyerzone.dao.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.StatementType;

import com.buyerzone.model.cld.CategoryDefinition;
import com.buyerzone.model.cld.FieldOption;
import com.buyerzone.model.cld.Question;
import com.buyerzone.model.cld.QuestionWithAnswer;

public interface SupplierCategoryMapper {
	
	@Select({" CALL dz_getCategoryInfo(#{supplierCatId}) "})
	@Options(statementType = StatementType.CALLABLE)
	CategoryDefinition getSupplierCategoryInfo(@Param("supplierCatId") int supplierCatId);
	
	@Select({" CALL dz_getCategoryInfo_new(#{supplierCatId}, #{formatId}) "})
	@Options(statementType = StatementType.CALLABLE)
	CategoryDefinition getSupplierCategoryInfoNew(@Param("supplierCatId") int supplierCatId, @Param("formatId") int formatId);
	
	@Select({" CALL dz_getCategoryQuestions(#{categoryId}) "})
	@Options(statementType = StatementType.CALLABLE)
	List<Question> getCategoryQuestions(@Param("categoryId")int categoryId);
	
	@Select({" CALL dz_getCldFieldOptions() "})
	@Options(statementType = StatementType.CALLABLE)
	List<FieldOption> getFieldOptions();
	
	@Select({" CALL dz_getAnswersByRequestId(#{quoteRequestId}) "})
	@Options(statementType = StatementType.CALLABLE)
	List<HashMap<String,String>> getQuestionAnswers(@Param("quoteRequestId") int quoteRequestId);
	
	@Select({" CALL dz_getAnswersByRequestId(#{quoteRequestId}) "})
	@Options(statementType = StatementType.CALLABLE)
	List<QuestionWithAnswer> getQuestionWithAnswers(@Param("quoteRequestId") int quoteRequestId);
	
	@Select({" CALL dz_getAnswersByRequestId_new(#{quoteRequestId}) "})
	@Options(statementType = StatementType.CALLABLE)
	List<QuestionWithAnswer> getXmlQuestionWithAnswers(@Param("quoteRequestId") int quoteRequestId);
	
	@Select("select param_key, param_value from quote_request_param where quote_request_id = #{quoteRequestId} and param_key like '%_other_text';")
	List<HashMap<String,String>> getQuoteRequestParam(@Param("quoteRequestId") int quoteRequestId);
	
	@Select("select c.category_id as categoryId, c.display_name as categoryName from category;")
	List<HashMap<String,Integer>> getCategories();
	
	/*
	@Insert({"<script>"
        + "<isNotEmpty property='Mappings'>"
        + "INSERT INTO cld_mappings("
        + " mapping_type_id,lead_delivery_preference_id, param_name, param_value, translation_id)"
        + "SELECT mappingTypeId, preferenceId, paramName, paramValue, translationId FROM "
        + "(<iterate property='Mappings' open='' close='' conjunction=' UNION '>"
        + "SELECT #Mappings[].mappingTypeId# as mappingTypeId,"
        + "#Mappings[].preferenceId# as preferenceId,"
        + "#Mappings[].paramName# as paramName,"
        + "#Mappings[].paramValue# as paramValue,"
        + "#Mappings[].translationId# as translationId"
        + " </iterate>) C"
        + "</isNotEmpty>"
	  	+ "</script>"})
	@Options(statementType = StatementType.STATEMENT)
	int insertCustomMapping(@Param("mappings") List<CustomMapping> mappings);*/
	

}
