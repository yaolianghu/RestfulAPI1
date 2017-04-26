package com.buyerzone.service;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.buyerzone.dao.mapper.SupplierCategoryMapper;
import com.buyerzone.model.cld.CategoryDefinition;
import com.buyerzone.model.cld.FieldOption;
import com.buyerzone.model.cld.Question;
import com.buyerzone.model.cld.QuestionWithAnswer;

@Service
public class SupplierCategoryService {
	
	@Autowired
	protected SupplierCategoryMapper supplierCategoryMapper;
	
	public CategoryDefinition getSupplierCategoryInfo(@Param("supplierCatId") int supplierCatId) {
		return supplierCategoryMapper.getSupplierCategoryInfo(supplierCatId);
	}
	
	public List<Question> getCategoryQuestions(@Param("categoryId")int categoryId) {
		return supplierCategoryMapper.getCategoryQuestions(categoryId);
	}
	
	public List<FieldOption> getFieldOptions() {
		return supplierCategoryMapper.getFieldOptions();
	}
	
	public List<HashMap<String,String>> getQuestionAnswers(@Param("quoteRequestId") int quoteRequestId) {
		return supplierCategoryMapper.getQuestionAnswers(quoteRequestId);
	}
	
	public List<HashMap<String,String>> getQuoteRequestParam(@Param("quoteRequestId") int quoteRequestId) {
		return supplierCategoryMapper.getQuoteRequestParam(quoteRequestId);
	}
	
	public List<QuestionWithAnswer> getQuestionWithAnswers(@Param("quoteRequestId") int quoteRequestId) {
		return supplierCategoryMapper.getQuestionWithAnswers(quoteRequestId);
	}
	
	public List<QuestionWithAnswer> getXmlQuestionWithAnswers(@Param("quoteRequestId") int quoteRequestId) {
		return supplierCategoryMapper.getXmlQuestionWithAnswers(quoteRequestId);
	}
	
	public CategoryDefinition getSupplierCategoryInfoNew(@Param("supplierCatId") int supplierCatId, @Param("formatId") int formatId) {
		return supplierCategoryMapper.getSupplierCategoryInfoNew(supplierCatId, formatId);
	}
}
