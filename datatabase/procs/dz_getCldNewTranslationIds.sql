DELIMITER $$

DROP PROCEDURE IF EXISTS dz_cloneMappings$$

CREATE PROCEDURE dz_getCldNewTranslationIds(in originalValue varchar(255), in newValue varchar(255), in mappingId int)
BEGIN
	select cld_translations_id from cld_translations
    where original_value = originalValue 
    and new_value = newValue
    and cld_mapping_id = mappingId;

END$$
DELIMITER ;