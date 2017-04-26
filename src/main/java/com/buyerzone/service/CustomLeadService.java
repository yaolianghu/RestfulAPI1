package com.buyerzone.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.buyerzone.dao.mapper.CustomLeadMapper;
import com.buyerzone.dao.mapper.TranslationMapper;
import com.buyerzone.model.cld.CustomMapping;
import com.buyerzone.model.cld.CustomNewMapping;
import com.buyerzone.model.cld.Translation;

@Service
public class CustomLeadService {

	@Autowired
	private CustomLeadMapper customLeadMapper;
	
	public int insertCustomMapping(@Param("mappings") List<CustomMapping> mappings) {
		return customLeadMapper.insertCustomMapping(mappings);
	}
	
	
	public int deleteCustomMappingByVersion(@Param("version") int version, @Param("preferenceId") int preferenceId) {
		return customLeadMapper.deleteCustomMappingByVersion(version, preferenceId);
	}
	
	public List<CustomMapping> getCustomMappingByPreferenceId(@Param("preferenceId") int preferenceId) {
		return customLeadMapper.getCustomMappingByPreferenceId(preferenceId);
	}
	
	public CustomMapping getPreferenceId(@Param("proc") String procName, @Param("param") String param) {
		return customLeadMapper.getPreferenceId(procName, param);
	}
	
	public int updateCustomMapping(@Param("version") int version, @Param("preferenceId") int preferenceId) {
		return customLeadMapper.updateCustomMapping(version, preferenceId);
	}
	
	public List<CustomMapping> getPublishLead(@Param("preferenceId") int preferenceId) {
		return customLeadMapper.getPublishLead(preferenceId);
	}
	
	public int removeLive(@Param("preferenceId") int preferenceId) {
		return customLeadMapper.removeLive(preferenceId);
	}
	
	public List<CustomNewMapping> getCustomNewMapping(@Param("proc") String procName, @Param("preferenceId") int preferenceId, @Param("version") int version, @Param("questionSetId") int questionSetId) {
		return customLeadMapper.getCustomNewMapping(procName, preferenceId, version, questionSetId);
	}
	
	public int getLatestVersion(@Param("preferenceId") int preferenceId) {
		return customLeadMapper.getLatestVersion(preferenceId);
	}
	
	public List<CustomMapping> getDefaultLead() {
		return customLeadMapper.getDefaultLead();
	}
	
	public String getCategoryName(@Param("supplierCatId") int supplierCatId) {
		return customLeadMapper.getCategoryName(supplierCatId);
	}

	public List<CustomMapping> getCustomMappingByPreferenceIdAndVersion(@Param("preferenceId") int preferenceId, @Param("version") int version) {
		return customLeadMapper.getCustomMappingByPreferenceIdAndVersion(preferenceId, version);
	}
	
	public String getParam(@Param("supplierCatId") int supplierCatId, @Param("formateId") int formatId) {
		return customLeadMapper.getParam(supplierCatId, formatId);
	}
	
	public int getSupplierCatId(@Param("leadPreferenceId") int leadPreferenceId) {
		return customLeadMapper.getSupplierCatId(leadPreferenceId);
	}
	
	public String getSalesForceLeadDeliverySettings(@Param("leadPreferenceId") int leadPreferenceId) {
		return customLeadMapper.getSalesForceLeadDeliverySettings(leadPreferenceId);
	}
	
	public String getSalesForceLeadDeliverySettingsBySupplierCatId(@Param("supplierCatId") int supplierCatId) {
		return customLeadMapper.getSalesForceLeadDeliverySettingsBySupplierCatId(supplierCatId);
	}
}
