DELIMITER $$

DROP PROCEDURE IF EXISTS dz_getActiveCategoriesBySupplierCatId$$

CREATE PROCEDURE dz_getActiveCategoriesBySupplierCatId(in supplier_owner_id int)
BEGIN
select distinct sc.supplier_id, sc.supplier_entity_owner_id as 'supplier_cat_id',
	s.supplier_name, c.display_name as 'category', ifnull(filter_label,'') as 'filterLabel'
	FROM supplier_category sc join supplier s on sc.supplier_id = s.supplier_entity_owner_id 
    join category c on sc.category_id = c.category_id
    left join supplier_lead_filter f on f.supplier_entity_owner_id = sc.supplier_entity_owner_id,
    question_set qs, http_lead_delivery_settings hlds, lead_delivery_preference ldp
	WHERE sc.supplier_id in (select supplier_id from supplier_category where supplier_entity_owner_id = supplier_owner_id)
	and qs.category_id = sc.category_id
    and ldp.lead_delivery_pref_id = hlds.lead_delivery_pref_id
    and ldp.lead_delivery_format_id = 2287
    and ldp.supplier_entity_owner_id = sc.supplier_entity_owner_id
    order by c.display_name;

END$$
DELIMITER ;