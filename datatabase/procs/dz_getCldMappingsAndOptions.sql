DELIMITER $$

DROP PROCEDURE IF EXISTS dz_getCldMappingsAndOptions$$

CREATE PROCEDURE dz_getCldMappingsAndOptions(in preferenceId int, in version int, in questionSetId int)
begin

select distinct mappingId, preferenceId, mappingTypeId, targetName, sourceValue,
a.questionSetId, case when a.mappingTypeId = 3 then a.sourceValue end as questionId, a.type,
case when a.type is null then ans.supplier_text else a.sellerQuestion  end as sellerQuestion, 
a.answerOption, version, a.option, translationId, a.questionSetSeparator, a.questionAnswerSeparator,
valueToTranslate, translatedValue from
	(select m.cld_mapping_id as mappingId,lead_delivery_preference_id as preferenceId,
    m.mapping_type_id as mappingTypeId,
	param_name as targetName, param_value as sourceValue,
    q.question_set_id as questionSetId, q.xml_id as questionId, q.type as 'type',
	q.supplier_text as sellerQuestion,ifnull(a.supplier_text,a.label) as answerOption, 
    m.version,m.question_answer_separator as questionAnswerSeparator, m.question_set_separator as questionSetSeparator,
    case when entity_group_name = 'ADDRESS_STATE_US' then 
    entity_resolver_value else entity_resolver_name end as 'option', 
    t.cld_translations_id as translationId,
	t.original_value as valueToTranslate, t.new_value as translatedValue
	from cld_mappings m 
	left join cld_field_options o on m.param_value = o.field_name
	left join entity_resolver_lookup l on o.lookup_group = l.entity_group_name
	left join question q on q.xml_id = m.param_value and q.question_set_id = questionSetId 
	left join answer a on q.question_id = a.question_id
	left join cld_translations t on t.cld_mapping_id = m.cld_mapping_id  and (
	(case when l.entity_group_name ='ADDRESS_STATE_US' then t.original_value = l.entity_resolver_value else t.original_value = l.entity_resolver_name end)
    or t.original_value = ifnull(a.supplier_text,a.label))
	where m.lead_delivery_preference_id = preferenceId
    and m.version = version) a left join answer ans on a.sourceValue = ans.xml_id;

    /*
	select m.cld_mapping_id as mappingId,lead_delivery_preference_id as preferenceId,
    m.mapping_type_id as mappingTypeId,
	param_name as targetName, param_value as sourceValue,
    q.question_set_id as questionSetId, q.xml_id as questionId,
	q.supplier_text as sellerQuestion,ifnull(a.supplier_text,a.label) as answerOption, 
    m.version,l.entity_resolver_name as 'option', t.cld_translations_id as translationId,
	t.original_value as valueToTranslate, t.new_value as translatedValue
	from cld_mappings m 
	left join cld_field_options o on m.param_value = o.field_name
	left join entity_resolver_lookup l on o.lookup_group = l.entity_group_name
	left join question q on q.xml_id = m.param_value
	left join answer a on q.question_id = a.question_id
	left join cld_translations t on t.cld_mapping_id = m.cld_mapping_id  and (t.original_value = l.entity_resolver_name
    or t.original_value = ifnull(a.supplier_text,a.label))
	where m.lead_delivery_preference_id = preferenceId
    and m.version = version;
    
    /* Test:
		call dz_getCldMappingsAndOptions(27782,1);
        call dz_getCldMappingsAndOptions(27813,1);
        
        select *
        from cld_translations
        where cld_mapping_id = 1029;
        
        select *
        from cld_translations
        where delivery_preference_id = 27782;
        
        select *
        from cld_translations
        where delivery_preference_id = 27813;
        
        select *
        from cld_translations;
        
        select *
        from cld_mappings
        where lead_delivery_preference_id = 27813
        and version = 1;

    */
    
END$$
DELIMITER ;