DELIMITER $$

DROP PROCEDURE IF EXISTS dz_getCategoryInfo_new$$

CREATE PROCEDURE dz_getCategoryInfo_new(in catOwnerId int, in formatId int)
BEGIN
	select distinct sc.supplier_id as 'supplierId', c.category_id as 'categoryId', sc.supplier_entity_owner_id as 'supplierCatId', p.lead_delivery_pref_id as preferenceId, 
    filter_label as 'filterLabel', c.display_name as 'displayName', qs.question_set_id as questionSetId
	FROM supplier_category sc join category c on sc.category_id = c.category_id
    left join supplier_lead_filter f on f.supplier_entity_owner_id = sc.supplier_entity_owner_id
    left join question_set qs on qs.category_id = c.category_id
    left join lead_delivery_preference p on p.supplier_entity_owner_id = sc.supplier_entity_owner_id
    where sc.supplier_entity_owner_id = catOwnerId
    and qs.question_set_id = (select max(question_set_id) from question_set where category_id = c.category_id)
    and p.lead_delivery_format_id = formatId;
    
    /* Test:
    call dz_getCategoryInfo('9713');
    
    select * from lead_delivery_preference limit 100;
    */
    
END$$
DELIMITER ;