DELIMITER $$

DROP PROCEDURE IF EXISTS dz_getCldMappingTranslationsWithLatestVersion$$

CREATE PROCEDURE dz_getCldMappingTranslationsWithLatestVersion(in questionId int, in quoteRequestId int)
BEGIN

	SELECT m.cld_mapping_id as 'mappingId', lead_delivery_preference_id as 'preferenceId',
	param_name as 'targetName', param_value as 'sourceValue', t.cld_translations_id as translationId,
	t.original_value as 'option', t.new_value as 'translatedOption', 
    mapping_type_id as mappingType, m.version as version, m.question_answer_separator as questionSeparator,
    m.question_set_separator as questionSetSeparator
	FROM lead_delivery_preference p, cld_mappings m left join cld_translations t
	on m.cld_mapping_id = t.cld_mapping_id
	WHERE p.lead_delivery_pref_id = m.lead_delivery_preference_id
    and p.supplier_entity_owner_id =  supplierCatId
    and p.lead_delivery_format_id = 2287 and p.lead_delivery_type_id = 2
    having m.version = (select max(version) from cld_mappings mm where mm.lead_delivery_preference_id = p.lead_delivery_pref_id)
    order by param_name;

END$$
DELIMITER ;