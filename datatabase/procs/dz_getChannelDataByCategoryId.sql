DELIMITER $$

DROP PROCEDURE IF EXISTS dz_getChannelDataByCategoryId$$


CREATE PROCEDURE `dz_getChannelDataByCategoryId` (in categoryId int)
BEGIN
select distinct a.category_name, a.category_id, mpp.program_id as channel_id, mpp.name, '1929' as channel_type from
(select mp.program_id, mc.category_id, mc.category_name
from master_program mp, marketing_category mc
where mp.program_id = mc.program_id
and status_id = 215
and category_id = categoryId
) a
right join master_program mpp on mpp.program_id = a.program_id
union
select distinct b.display_name, b.category_id, ss.supplier_entity_owner_id as channel_id, ss.supplier_name, '1930' as channel_type from
(select s.supplier_entity_owner_id, c.category_id, c.display_name
from supplier s, supplier_category sc, category c
where s.supplier_entity_owner_id = sc.supplier_id
and s.supplier_status_id = 3
and c.category_id = sc.category_id
and sc.category_id = categoryId) b
right join supplier ss on ss.supplier_entity_owner_id = b.supplier_entity_owner_id
union
select distinct d.display_name, d.category_id, lepp.lead_exchange_partner_id as channel_id, lepp.account_name, '1931' as channel_type from
(select lep.lead_exchange_partner_id, c.category_id, c.display_name
from lead_exchange_partner lep, lead_exchange_category lec, category c
where lep.lead_exchange_partner_id = lec.lead_exchange_partner_id
and c.category_id = lec.category_id
and lec.category_id = categoryId) d
right join lead_exchange_partner lepp on lepp.lead_exchange_partner_id = d.lead_exchange_partner_id;
END
END$$
DELIMITER ;