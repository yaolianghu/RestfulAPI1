DELIMITER $$

DROP PROCEDURE IF EXISTS dz_cloneMappings$$

CREATE PROCEDURE dz_cloneMappings(in preferenceId int, in versionNum int)
begin
	declare new_version int;
	set new_version = (select max(version) + 1 from cld_mappings where lead_delivery_preference_id = preferenceId);
    
	insert into cld_mappings(mapping_type_id, lead_delivery_preference_id, param_name, param_value, version, is_live, question_answer_separator, question_set_separator, created_date, char_limit)
	select mapping_type_id, lead_delivery_preference_id, param_name, param_value, new_version, 0, question_answer_separator, question_set_separator, now(), char_limit
	from cld_mappings
	where lead_delivery_preference_id = preferenceId and version = versionNum;
    
    insert into cld_translations(original_value, new_value, cld_mapping_id, created_date)
	select a.original_value, a.new_value, b.cld_mapping_id, now()
	from(
		select m.param_name, m.param_value, t.cld_translations_id as translationId,
		t.original_value, t.new_value,m.cld_mapping_id
		from cld_mappings m, cld_translations t 
		where t.cld_mapping_id = m.cld_mapping_id
		and m.lead_delivery_preference_id = preferenceId
		and m.version = versionNum
	)a,
	(
		select m.param_name, m.param_value, m.cld_mapping_id
		from cld_mappings m
		where m.lead_delivery_preference_id = preferenceId
		and m.version = new_version
	)b
	where a.param_name = b.param_name;
    
	select m.cld_mapping_id as mappingId, m.lead_delivery_preference_id as preferenceId, 
	m.mapping_type_id as mappingType, t.mapping_type_name as mappingTypeName,  m.param_name as targetName, 
    m.param_value as sourceValue,'remove this field' as translationId, m.version, m.is_live as isLive, question_answer_separator as questionAnswerSeparator, question_set_separator  as questionSetSeparator, created_date as createdDate
	from lead_delivery_preference p, cld_mappings m, cld_mapping_types t
	where p.lead_delivery_pref_id = m.lead_delivery_preference_id
    and m.mapping_type_id = t.cld_mapping_type_id
	and p.lead_delivery_pref_id =  preferenceId
	#and p.lead_delivery_format_id = 2221
    and m.version = new_version;

    /* Test:
    call dz_cloneMappings(27782, 1);
    */
    
END$$
DELIMITER ;