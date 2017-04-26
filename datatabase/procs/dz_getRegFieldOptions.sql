DELIMITER $$

DROP PROCEDURE IF EXISTS dz_getRegFieldOptions$$

CREATE PROCEDURE dz_getRegFieldOptions(in groupName varchar(255))
begin

	
	select 
    case when entity_group_name = 'ADDRESS_STATE_US' then 
    entity_resolver_name else entity_resolver_value end as 'lookupValue' , 
    case when entity_group_name = 'ADDRESS_STATE_US' then 
    entity_resolver_value else entity_resolver_name end as 'option'
	from entity_resolver_lookup
	where entity_group_name = groupName;

    /* Test:
    call dz_getRegFieldOptions('BUYER_REGN_COMPANY_INDUSTRY');
    */
    
END$$
DELIMITER ;