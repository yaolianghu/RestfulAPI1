select distinct b.supplier_entity_owner_id as id, b.supplier_name as displayName, b.categoryId, b.categoryName, 'publisher' as type,
a.channelId, ifnull(isActive, 0) as active, 
case when a.channelId=1930 and isActive=1 then true else false end as special
from (select supplier_name, s.supplier_entity_owner_id, sc.category_id as categoryId, c.display_name as categoryName
 from supplier s, supplier_category sc , category c, entity_resolver_lookup erl
where s.supplier_entity_owner_id = sc.supplier_id and sc.category_id = c.category_id and s.supplier_type_id = erl.entity_resolver_lookup_id and erl.entity_resolver_value= 'PUBLISHER') b
left join  
(SELECT svs.channel_id as id, 
s.supplier_name as displayName,
svs.channel_type as channelId,
c.category_id as categoryId,
c.display_name as categoryName, 
CASE WHEN c.category_id in (select svsc.category_id 
FROM special_verification_settings_category svsc 
WHERE svsc.special_verification_settings_id = svs.id) 
THEN 1 else 0 END as isActive 
FROM supplier s 
JOIN supplier_category sc on s.supplier_entity_owner_id = sc.supplier_id 
JOIN category c on sc.category_id = c.category_id 
left JOIN special_verification_settings svs on s.supplier_entity_owner_id = svs.channel_id 
JOIN entity_resolver_lookup erl on erl.entity_resolver_lookup_id = svs.channel_type
ORDER BY c.display_name) a on b.supplier_entity_owner_id = a.id and a.categoryId = b.categoryId
and a.channelId = 1930;


select * from supplier ;
select * from entity_resolver_lookup where entity_group_name = 'SUPPLIER_TYPE';


select * from special_verification_settings;

select *
from entity_resolver_lookup
where entity_group_name = 'MARKETING_CHANNEL_TYPE';

select count(*) from supplier s, supplier_category sc 
where s.supplier_entity_owner_id = sc.supplier_id
and s.supplier_entity_owner_id = 200;

select *
 from lead_exchange_partner lep
left join lead_exchange_category lec on lep.lead_exchange_partner_id = lec.lead_exchange_partner_id;

select *
from lead_exchange_partner lep
left join special_verification_settings svs on lep.lead_exchange_partner_id = svs.channel_id;

select * from lead_exchange_partner;
select * from lead_exchange_category;