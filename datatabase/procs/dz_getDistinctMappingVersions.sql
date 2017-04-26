DELIMITER $$

DROP PROCEDURE IF EXISTS dz_getDistinctMappingVersions$$

CREATE PROCEDURE dz_getDistinctMappingVersions(in preferenceId int)
begin
	select distinct(version), is_live as isLive
    from cld_mappings
    where lead_delivery_preference_id = preferenceId
    order by version;

    /* Test:
    call dz_getDistinctMappingVersions(27782);
    
	select distinct(version), is_live
    from cld_mappings
    where lead_delivery_preference_id = 27782
    order by version;
    */
    
END$$
DELIMITER ;