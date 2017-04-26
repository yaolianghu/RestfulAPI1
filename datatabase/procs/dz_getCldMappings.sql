DELIMITER $$

DROP PROCEDURE IF EXISTS dz_getCldMappings$$

CREATE PROCEDURE dz_getCldMappings(in supplierCatId int, in mappingType varchar(32), in version int, in live varchar(32))
begin

	select m.lead_delivery_preference_id as preferenceId, m.cld_mapping_id as mappingId, 
	m.mapping_type_id as mappingType, t.mapping_type_name as mappingTypeName,  m.param_name as targetName, m.param_value as sourceValue,
	'remove this field' as translationId
	from lead_delivery_preference p, cld_mappings m, cld_mapping_types t
	where p.lead_delivery_pref_id = m.lead_delivery_preference_id
    and m.mapping_type_id = t.cld_mapping_type_id
	and p.supplier_entity_owner_id =  supplierCatId
	and p.lead_delivery_format_id = 2221
    and case
		when live = 'live' Then m.is_live = 1
        else 1=1
        end
    and case 
		when version = 0 Then 1=1
        else m.version = version
        end
    and t.mapping_type_name = ifnull(replace(mappingType,'null',null), t.mapping_type_name);
    
    /* Test:
    call dz_getCldMappings(28413, 'registration', 1, 'live');
    call dz_getCldMappings(28413, 'static', 1, null);
    call dz_getCldMappings(28413, 'null', 0, null);
    
    */
    
END$$
DELIMITER ;