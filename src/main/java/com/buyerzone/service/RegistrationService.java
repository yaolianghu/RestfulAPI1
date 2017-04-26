package com.buyerzone.service;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.buyerzone.dao.mapper.RegistrationMapper;
import com.buyerzone.model.Lead;
import com.buyerzone.model.LeadPreference;
import com.buyerzone.model.cld.QuestionWithAnswer;

@Service
public class RegistrationService {

	@Autowired
	private RegistrationMapper registrationMapper;
	
	public Lead getLead(int quoteId) {
		return registrationMapper.getLead(quoteId);
	}
	
	public HashMap<String, String> getCustomLead(@Param("quoteId") int quoteId, @Param("supplierId") int supplierId, @Param("list") String[] fields) {
		return registrationMapper.getCustomLead(quoteId, supplierId, fields);
	}
	
	public List<LeadPreference> getLeadMappings(@Param("prefId") int prefId, int version) {
		return registrationMapper.getLeadMappings(prefId, version);
	}
	
	public List<String> getCustomFields(@Param("prefId") int prefId) {
		return registrationMapper.getCustomFields(prefId);
	}
	
	public LeadPreference getLeadPreferences(@Param("supplierId") int supplierId) {
		return registrationMapper.getLeadPreferences(supplierId);
	}
	
	public List<HashMap<String,String>> getFieldOptions(@Param("groupName")String groupName) {
		return registrationMapper.getFieldOptions(groupName);
	}
	
	public List<LeadPreference> getTestLeadMappings(@Param("prefId") int prefId, @Param("version") int version) {
		return registrationMapper.getTestLeadMappings(prefId, version);
	}
	
	public List<QuestionWithAnswer> getAnswerOptions(@Param("questionSetId") int questionSetId) {
		return registrationMapper.getAnswerOptions(questionSetId);
	}
	
	public String getVersionByLive(@Param("prefId") int preferenceId) {
		return registrationMapper.getVersionByLive(preferenceId);
	}
	
	public HashMap<String, String> getUnmatchedCustomLead(@Param("quoteId") int quoteId, @Param("list") String[] fields) {
		return registrationMapper.getUnmatchedCustomLead(quoteId, fields);
	}
	
	public List<LeadPreference> getExtraData() {
		return registrationMapper.getExtraData();
	}
	
	public LeadPreference getXmlLeadPreferences(@Param("supplierId") int supplierId, @Param("formatId") int formatId) {
		return registrationMapper.getXmlLeadPreferences(supplierId, formatId);
	}
	
	public String getStateFullName(@Param("stateId") String stateId) {
		return registrationMapper.getStateFullName(stateId);
	}
	
	public List<HashMap<String, String>> getTestCustomRegistration(@Param("list") String[] fields) {
		return registrationMapper.getTestCustomRegistration(fields);
	}
	
	public List<QuestionWithAnswer> getTestQuestionWithAnswer(@Param("questionSetId") int questionSetId) {
		return registrationMapper.getTestQuestionWithAnswer(questionSetId);
	}
}
