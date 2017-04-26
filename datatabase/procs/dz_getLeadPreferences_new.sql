DELIMITER $$

DROP PROCEDURE IF EXISTS dz_getLeadPreferences_new$$

CREATE PROCEDURE dz_getLeadPreferences_new(in supplierCategoryId int, in formatId int)
BEGIN
	
	SELECT p.lead_delivery_pref_id as preferenceId, p.supplier_entity_owner_id as supplierId, 
	target_url as targetUrl, test_url as testUrl, user_name as userName, failover_email as failoverEmail
	FROM lead_delivery_preference p left join http_lead_delivery_settings h
	ON p.lead_delivery_pref_id = h.lead_delivery_pref_id
	WHERE p.supplier_entity_owner_id = supplierCategoryId and p.lead_delivery_format_id = formatId;
    
	/* Test:
    call dz_getLeadPreferences(28413);
    */

END$$
DELIMITER ;