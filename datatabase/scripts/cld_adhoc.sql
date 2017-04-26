SELECT * FROM bz20dev.timtest_lead_delivery_param_mapping;

desc lead_delivery_preference;

select lead_delivery_preference_id as prefId,
param_key as "key", param_value as "value"
from timtest_lead_delivery_param_mapping;


select *
from lead_delivery_preference p, timtest_lead_delivery_param_mapping m
where p.lead_delivery_pref_id = m.lead_delivery_param_mapping_id
and p.lead_delivery_pref_id = 22031
and supplier_entity_owner_id = 237;

select p.lead_delivery_pref_id, p.supplier_entity_owner_id, target_url, test_url, user_name, failover_email,
p.lead_delivery_format_id
from lead_delivery_preference p left join http_lead_delivery_settings h
on p.lead_delivery_pref_id = h.lead_delivery_pref_id
where p.supplier_entity_owner_id = 28413
and lead_delivery_format_id = 2221;  -- 17305 24574


select *
from entity_resolver_lookup
where entity_group_name = 'SUPPLIER_LEAD_DELIVERY_TYPE';

select *
from entity_resolver_lookup
where entity_group_name = 'BUYER_REGN_COMPANY_SIZE';



select *
from entity_resolver_lookup
where entity_group_name like '%SUPPLIER_LEAD_DELIVERY_FORMAT%';



select *
from lead_delivery_preference
where supplier_entity_owner_id =  28413
and lead_delivery_format_id = 2221; -- 2221 is generic_post and 2287 is custom_post

 


select m.lead_delivery_preference_id as preferenceId, m.cld_mapping_id as mappingId, 
m.mapping_type_id as mappingType, m.param_name as paramName, m.param_value as paramValue,
m.translation_id as translationId
from lead_delivery_preference p, cld_mappings m
where p.lead_delivery_pref_id = m.lead_delivery_preference_id
and p.supplier_entity_owner_id =  28413
and p.lead_delivery_format_id = 2221; -- 2221 is generic_post and 2287 is custom_post

                                  

select *
from http_lead_delivery_settings
where lead_delivery_pref_id = 22031;


update http_lead_delivery_settings
set lead_delivery_pref_id = 22031
where lead_delivery_pref_id = 237;

commit;

update lead_delivery_preference
set supplier_entity_owner_id = 237
where lead_delivery_pref_id = 22031;

commit;

select *
from http_lead_delivery_settings
where lead_delivery_pref_id = 237;


update timtest_lead_delivery_param_mapping
set lead_delivery_preference_id = 22031
where lead_delivery_preference_id = 27778;

commit;

update timtest_lead_delivery_param_mapping
set param_value = 'first_name', param_key = 'firstName'
where lead_delivery_param_mapping_id = 1;

insert into timtest_lead_delivery_param_mapping(lead_delivery_preference_id, param_key, param_value, param_value_type_id)
values (27778, 'lastName', 'last_name', 1);

commit;



select * from buyer_registration limit 1;


select column_name, data_type, is_nullable, CHARACTER_MAXIMUM_LENGTH
from information_schema.columns
where table_name = 'buyer_registration'
and table_schema = 'bz20';

