DELIMITER $$

DROP PROCEDURE IF EXISTS dz_getCldFieldOptions$$

CREATE PROCEDURE dz_getCldFieldOptions()
begin

	select field_name as fieldName, lookup_group as lookupGroup
    from cld_field_options;

    /* Test:
    call dz_getCldFieldOptions();
    */
    
END$$
DELIMITER ;