package com.buyerzone.dao.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.StatementType;

import com.buyerzone.model.*;
import com.buyerzone.model.cld.Question;
import com.buyerzone.model.cld.QuestionWithAnswer;


public interface RegistrationMapper {
	
  @Select("SELECT first_name as FirstName, last_name as LastName, address_street_line1 as Address1, "
  		+ " address_street_line2 as Address2, address_state as State, address_zip as Zip,"
		+ " address_country as Country, email as Email, phone as Phone"
		+ " FROM buyer_registration"
		+ " WHERE buyer_id = #{quoteId} LIMIT 1")
  Lead getLead(int quoteId);
  
  /* Keep this for reference.  Used to explicitly map columns to properties.
   * @Results(value = { 
          @Result(column = "first_name", property = "FirstName"),
          @Result(column = "last_name", property = "LastName")
         // @Result(column = "{name=TEACHER_NAME}", property = "teacher", javaType=Teacher.class, one=@One(select="com.objectpartners.TeacherDao.selectTeachForGivenName")),
         //  IF the mapping to Teacher took multiple paramaters it would look like this...
         //  @Result(column = "{name=TEACHER_NAME,param2=COLUMN2}", property = "teacher", javaType=Teacher.class, one=@One(select="com.objectpartners.TeacherDao.selectTeachForGivenName")), 
        })*/
  @Select({"<script>",
      "SELECT ", 
	      "<foreach item='item' index='index' collection='list'",
		      "separator=','>",
		      	"ifnull(${item}, 'null') as ${item}",
	      "</foreach>",
	      "FROM ",
	      "(SELECT br.*, qr.quote_request_id, c.display_name as category_name, c.category_id,",
		  "st.name as state_name, concat(br.first_name, ' ', br.last_name) as full_name,",
		  "now() as sent_date,",
		  "comp_title.entity_resolver_name as title, ",
		  "comp_ind.entity_resolver_name as industry,", 
		  "comp_size.entity_resolver_name as company_size, ",
		  "m.supplier_entity_owner_id",
	      " FROM quote_request qr ",
	      " join buyer_registration br on br.buyer_registration_id = qr.buyer_registration_id",
	      " join state st on st.short_name = br.address_state",
	      " join category c on qr.category_id = c.category_id",
	      " join quote_matched_supplier m on qr.quote_request_id = m.quote_request_id",
	      " left join entity_resolver_lookup comp_title on comp_title.entity_resolver_lookup_id = br.title_id",
	      " left join entity_resolver_lookup comp_ind on comp_ind.entity_resolver_lookup_id = br.company_industry",
	      " left join entity_resolver_lookup comp_size on comp_size.entity_resolver_lookup_id = br.company_size_id",
	      " WHERE qr.quote_request_id = #{quoteId}",
	      " AND m.supplier_entity_owner_id = #{supplierId}"
	      + ")a;",
      "</script>"}) 
  HashMap<String, String> getCustomLead(@Param("quoteId") int quoteId, @Param("supplierId") int supplierId, @Param("list") String[] fields);
 
  
  @Select({"<script>",
      "SELECT ", 
	      "<foreach item='item' index='index' collection='list'",
		      "separator=','>",
		      	"ifnull(${item}, 'null') as ${item}",
	      "</foreach>",
	      "FROM ",
	      "(SELECT br.*, qr.quote_request_id, c.display_name as category_name, c.category_id,",
		  "st.name as state_name, concat(br.first_name, ' ', br.last_name) as full_name,",
		  "now() as sent_date,",
		  "comp_title.entity_resolver_name as title, ",
		  "comp_ind.entity_resolver_name as industry,", 
		  "comp_size.entity_resolver_name as company_size ",
	      " FROM quote_request qr ",
	      " join buyer_registration br on br.buyer_registration_id = qr.buyer_registration_id",
	      " join state st on st.short_name = br.address_state",
	      " join category c on qr.category_id = c.category_id",
	      " left join entity_resolver_lookup comp_title on comp_title.entity_resolver_lookup_id = br.title_id",
	      " left join entity_resolver_lookup comp_ind on comp_ind.entity_resolver_lookup_id = br.company_industry",
	      " left join entity_resolver_lookup comp_size on comp_size.entity_resolver_lookup_id = br.company_size_id",
	      " WHERE qr.quote_request_id = #{quoteId}",
	      ")a;",
      "</script>"}) 
  HashMap<String, String> getUnmatchedCustomLead(@Param("quoteId") int quoteId, @Param("list") String[] fields);
  
  
  @Select({" CALL dz_getCldMappingTranslations(#{prefId},#{version}) "})
  @Options(statementType = StatementType.CALLABLE)
  List<LeadPreference> getLeadMappings(@Param("prefId") int prefId, @Param("version") int version);
  
  
  @Select("SELECT param_value FROM cld_mappings"
		  + " WHERE lead_delivery_preference_id = #{prefId}")
  List<String> getCustomFields(@Param("prefId") int prefId);
  
  
  @Select({" CALL dz_getLeadPreferences(#{supplierId}) "})
  @Options(statementType = StatementType.CALLABLE)
  LeadPreference getLeadPreferences(@Param("supplierId") int supplierId);

  
  @Select({" CALL dz_getLeadPreferences_new(#{supplierId}, #{formatId}) "})
  @Options(statementType = StatementType.CALLABLE)
  LeadPreference getXmlLeadPreferences(@Param("supplierId") int supplierId, @Param("formatId") int formatId);
  
  @Select({" CALL dz_getRegFieldOptions(#{groupName}) "})
  @Options(statementType = StatementType.CALLABLE)
  List<HashMap<String,String>> getFieldOptions(@Param("groupName")String groupName);
  
  
  @Select("SELECT lead_delivery_preference_id as prefId,"
		  + " m.param_name as 'key',"
		  + " case when m.mapping_type_id != 1 Then m.param_value else d.param_value "
		  + " end as 'value',"
		  + " t.cld_translations_id as translationId,"
		  + " t.original_value as valueToTranslate, t.new_value as translatedValue, mapping_type_id as mappingType, version,"
		  + " m.question_answer_separator as questionSeparator, m.question_set_separator as questionSetSeparator, m.char_limit as charLimit"
		  + " FROM cld_mappings m"
		  + " LEFT JOIN cld_translations t ON m.cld_mapping_id = t.cld_mapping_id"
		  + " LEFT JOIN cld_test_data d on m.param_value = d.param_key "
		  + " WHERE lead_delivery_preference_id = #{prefId} and version = #{version}")
  List<LeadPreference> getTestLeadMappings(@Param("prefId") int prefId, @Param("version") int version);
  
  
  @Select("SELECT case when q.type = 'textmultiple' then a.xml_id else q.xml_id end as questionId, q.supplier_text as question, ifnull(a.supplier_text,a.label) as answer, q.type as type"
		  + " from question q, answer a"
		  + " where q.question_id = a.question_id"
		  + " and q.question_set_id = #{questionSetId}"
		  )
  List<QuestionWithAnswer> getAnswerOptions(@Param("questionSetId") int questionSetId);
  
  @Select("select distinct version from cld_mappings where is_live = 1 and lead_delivery_preference_id = #{prefId}")
  String getVersionByLive(@Param("prefId") int preferenceId);
  
  @Select("select param_key as 'key', param_value as 'value' from cld_test_data where param_key = 'company_industry' or param_key = 'company_size_id'")
  List<LeadPreference> getExtraData();
  
  @Select({
	  "select entity_resolver_name from entity_resolver_lookup where entity_group_name = 'ADDRESS_STATE_US' and entity_resolver_value = #{stateId}"
  })
  String getStateFullName(@Param("stateId") String stateId);
  
  @Select({"<script>",
      "SELECT param_key as paramKey, param_value as paramValue from cld_test_data where param_key in ", 
	      "<foreach item='item' index='index' collection='list' open='(' separator=',' close=')'>",
		      	"'${item}'",
	      "</foreach>",
      "</script>"}) 
  List<HashMap<String, String>> getTestCustomRegistration(@Param("list") String[] fields);
  
  
  @Select({" CALL dz_getAnswerOptionsNew(#{questionSetId}) "})
  @Options(statementType = StatementType.CALLABLE)
  List<QuestionWithAnswer> getTestQuestionWithAnswer(@Param("questionSetId") int questionSetId);
} 