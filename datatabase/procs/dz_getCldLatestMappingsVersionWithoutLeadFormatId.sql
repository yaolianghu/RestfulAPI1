DELIMITER $$

DROP PROCEDURE IF EXISTS dz_getCldLatestMappingsVersionWithoutLeadFormatId$$

CREATE PROCEDURE dz_getCldLatestMappingsVersionWithoutLeadFormatId(in supplierCatId int, in mappingType varchar(32))
BEGIN
	select m.lead_delivery_preference_id as preferenceId, m.cld_mapping_id as mappingId, 
	m.mapping_type_id as mappingType, t.mapping_type_name as mappingTypeName,  m.param_name as targetName, m.param_value as sourceValue,
	'remove this field' as translationId, m.version as version
	from lead_delivery_preference p, cld_mappings m, cld_mapping_types t
	where p.lead_delivery_pref_id = m.lead_delivery_preference_id
    and m.mapping_type_id = t.cld_mapping_type_id
	and p.supplier_entity_owner_id =  supplierCatId
	#and p.lead_delivery_format_id = 2221
    and t.mapping_type_name = ifnull(replace(mappingType,'null',null), t.mapping_type_name)
    having m.version = (select max(version) from cld_mappings mm where mm.lead_delivery_preference_id = p.lead_delivery_pref_id);
 
	 /* Test:
		call dz_getCldLatestMappingsVersionWithoutLeadFormatId(28413, 'registration');
		call dz_getCldLatestMappingsVersionWithoutLeadFormatId(28413, 'static');
		call dz_getCldLatestMappingsVersionWithoutLeadFormatId(28413, 'null');
        call dz_getCldLatestMappingsVersionWithoutLeadFormatId(6464, 'null');
		
		*/
END$$
DELIMITER ;
