DELIMITER $$

DROP PROCEDURE IF EXISTS dz_getCldMappingTranslations$$

CREATE PROCEDURE dz_getCldMappingTranslations(in preferenceId int, in versionNum int)
begin

	SELECT lead_delivery_preference_id as prefId,
	param_name as 'key', param_value as 'value', t.cld_translations_id as translationId,
	t.original_value as valueToTranslate, t.new_value as translatedValue, 
    mapping_type_id as mappingType, m.question_answer_separator as questionSeparator,
    m.question_set_separator as questionSetSeparator, m.char_limit as charLimit
	FROM cld_mappings m left join cld_translations t
	on m.cld_mapping_id = t.cld_mapping_id
	WHERE m.lead_delivery_preference_id = preferenceId
    and m.version = versionNum
    order by param_name;
    
    /* Test:
    
       call dz_getCldMappingTranslations(30119, 1);
       call dz_getCldMappingTranslations(27782, 1);
       
       select * from cld_mappings 
       where lead_delivery_preference_id = 27783
       and version = 1;
       
       desc cld_mappings;
       
    */
    
END$$
DELIMITER ;