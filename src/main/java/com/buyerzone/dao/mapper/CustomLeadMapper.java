package com.buyerzone.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.mapping.StatementType;

import com.buyerzone.model.cld.CustomMapping;
import com.buyerzone.model.cld.CustomNewMapping;

public interface CustomLeadMapper {

	@Insert({
		"<script>"
		        + "INSERT INTO cld_mappings"
		        + "(mapping_type_id,lead_delivery_preference_id, param_name, param_value, version, is_live, question_answer_separator, question_set_separator, created_date) "
		        + "VALUES"
		        + "<foreach collection='mappings' item='element' open='(' separator='),(' close=')'>"
		        + "#{element.mappingType}, #{element.preferenceId}, #{element.targetName}, #{element.sourceValue}, #{element.version}, #{element.isLive}, #{element.questionAnswerSeparator}, #{element.questionSetSeparator}, Now()"
		        + "</foreach>"
			  	+ "</script>"
	})
	int insertCustomMapping(@Param("mappings") List<CustomMapping> mappings);
	
	
	@Select({
		"<script>"
		+ " select cld_mapping_id as mappingId, mapping_type_id as mappingType, lead_delivery_preference_id as preferenceId,"
		+ " param_name as targetName, param_value as sourceValue, version from cld_mappings"
		+ " where lead_delivery_preference_id = #{preferenceId}"
		+ "</script>"
	})
	List<CustomMapping> getCustomMappingByPreferenceId(@Param("preferenceId") int preferenceId);
	
	
	@Select({
		"<script>"
		+ " select cld_mapping_id as mappingId, mapping_type_id as mappingType, lead_delivery_preference_id as preferenceId,"
		+ " param_name as targetName, param_value as sourceValue, version from cld_mappings"
		+ " where lead_delivery_preference_id = #{preferenceId} and version = #{version}"
		+ "</script>"
	})
	List<CustomMapping> getCustomMappingByPreferenceIdAndVersion(@Param("preferenceId") int preferenceId, @Param("version") int version);
	
	@Delete({
		"<script>"
		+ " delete from cld_mappings"
		+ " where version = #{version}"
		+ " and lead_delivery_preference_id = #{preferenceId}"
		+ "</script>"
	})
	int deleteCustomMappingByVersion(@Param("version") int version, @Param("preferenceId") int preferenceId);
	
	
	@Select({" CALL ${proc}(#{param})"})
	@Options(statementType = StatementType.CALLABLE)
	CustomMapping getPreferenceId(@Param("proc") String procName, @Param("param") String param);
	
	
	@Select({" CALL ${proc}(#{preferenceId}, #{version}, #{questionSetId})"})
	@Options(statementType = StatementType.CALLABLE)
	List<CustomNewMapping> getCustomNewMapping(@Param("proc") String procName, @Param("preferenceId") int preferenceId, @Param("version") int version, @Param("questionSetId") int questionSetId);
	
	@Update({
		"<script>" 
		+ " Update cld_mappings" 
		+ " set is_live=1"
		+ " where version = #{version} and lead_delivery_preference_id = #{preferenceId}"
		+ "</script>"
	})
	int updateCustomMapping(@Param("version") int version, @Param("preferenceId") int preferenceId);
	
	@Update({
		"<script>" 
		+ " Update cld_mappings" 
		+ " set is_live=0"
		+ " where lead_delivery_preference_id = #{preferenceId}"
		+ "</script>"
	})
	int removeLive(@Param("preferenceId") int preferenceId);
	
	@Select({
		"<script>"
		+ " select cld_mapping_id as mappingId, mapping_type_id as mappingType, lead_delivery_preference_id as preferenceId,"
		+ " param_name as targetName, param_value as sourceValue, version, is_live"
		+ " from cld_mappings"
		+ " where is_live = 1 and lead_delivery_preference_id = #{preferenceId}"
		+ "</script>"
	})
	List<CustomMapping> getPublishLead(@Param("preferenceId") int preferenceId);
	
	@Select({
		"<script>"
		+ " select max(version) from cld_mappings mm"
		+ " where mm.lead_delivery_preference_id = #{preferenceId}"
		+ "</script>"
	})
	int getLatestVersion(@Param("preferenceId") int preferenceId);
	
	@Select({
		"select csd.mapping_type_id as mappingType, csd.source as 'sourceValue', csd.target as targetName from cld_salesforce_default csd"
	})
	List<CustomMapping> getDefaultLead();
	
	@Select({
		"select display_name from supplier_category s, category c",
		" where s.category_id = c.category_id and s.supplier_entity_owner_id = #{supplierCatId}"
	})
	String getCategoryName(@Param("supplierCatId") int supplierCatId);
	
	@Select({
		"select pay_load_param from http_lead_delivery_settings hlds, lead_delivery_preference ldp where" +
		" hlds.lead_delivery_pref_id = ldp.lead_delivery_pref_id and supplier_entity_owner_id = #{supplierCatId} and ldp.lead_delivery_format_id = #{formatId}"
	})
	String getParam(@Param("supplierCatId") int supplierCatId, @Param("formatId") int formatId);
	
	@Select({
		"select supplier_entity_owner_id from lead_delivery_preference where lead_delivery_pref_id = #{leadPreferenceId}"
	})
	int getSupplierCatId(@Param("leadPreferenceId") int leadPreferenceId);
	
	
	@Select({
		"select oid from sales_force_lead_delivery_settings sfld where lead_delivery_pref_id = #{leadPreferenceId}"
	})
	String getSalesForceLeadDeliverySettings(@Param("leadPreferenceId") int leadPreferenceId);
	
	@Select({
		"select oid from sales_force_lead_delivery_settings sflds, lead_delivery_preference ldp" +
		" where ldp.lead_delivery_pref_id = sflds.lead_delivery_pref_id" +
		" and ldp.supplier_entity_owner_id = #{supplierCatId}"
	})
	String getSalesForceLeadDeliverySettingsBySupplierCatId(@Param("supplierCatId") int supplierCatId);
}
