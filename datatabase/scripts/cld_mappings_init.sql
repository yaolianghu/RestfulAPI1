/*

DROP TABLE cld_mappings;
DROP TABLE cld_mapping_types;

TRUNCATE TABLE cld_mapping_types;
TRUNCATE TABLE cld_mappings;
TRUNCATE TABLE cld_field_options;
*/

/* TABLE CLD_MAPPING_TYPES */

CREATE TABLE cld_mapping_types (
  cld_mapping_type_id int NOT NULL AUTO_INCREMENT,
  mapping_type_name varchar(50) NOT NULL,
  PRIMARY KEY (cld_mapping_type_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;


/*
ALTER TABLE cld_mapping_types AUTO_INCREMENT = 1;
*/

insert into cld_mapping_types(mapping_type_name)
select 'registration' as mapping_name
union
select 'static' as mapping_name
union
select 'question' as mapping_name
union
select 'question-all' as mapping_name
union
select 'description-standard-salesforce' as mapping_name;


/* Validate */

select * from cld_mapping_types;

CREATE TABLE cld_mappings (
  cld_mapping_id bigint(20) NOT NULL AUTO_INCREMENT,
  mapping_type_id int NOT NULL DEFAULT 1,
  lead_delivery_preference_id bigint(20) NOT NULL,
  param_name varchar(255) NOT NULL,
  param_value varchar(500) NOT NULL,
  version bigint(20) NOT NULL,
  is_live bit NOT NULL,
  question_answer_separator varchar(10),
  question_set_separator varchar(10),
  char_limit int DEFAULT -1,
  created_date datetime NOT NULL,
  PRIMARY KEY (cld_mapping_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;


CREATE INDEX idx_cld_mappings_prefId on cld_mappings(lead_delivery_preference_id);

ALTER TABLE cld_mappings 
    ADD CONSTRAINT fk_pref_id
    FOREIGN KEY(lead_delivery_preference_id)
    REFERENCES lead_delivery_preference(lead_delivery_pref_id);


ALTER TABLE cld_mappings 
    ADD CONSTRAINT fk_mapping_type_id
    FOREIGN KEY(mapping_type_id)
    REFERENCES cld_mapping_types(cld_mapping_type_id);

    
CREATE TABLE cld_translations (
  cld_translations_id bigint(20) NOT NULL AUTO_INCREMENT,
  original_value varchar(255) NOT NULL,
  new_value varchar(255)  NULL,
  cld_mapping_id bigint(20) NOT NULL,
  created_date datetime NOT NULL,
  PRIMARY KEY (cld_translations_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;


ALTER TABLE cld_translations 
    ADD CONSTRAINT fk_cld_mapping_id
    FOREIGN KEY(cld_mapping_id)
    REFERENCES cld_mappings(cld_mapping_id)
    ON DELETE CASCADE;
    

/* Validate */

select *
from cld_translations;
    
-- CLD_FIELD_OPTIONS 

 CREATE TABLE cld_field_options (
  cld_field_options_id bigint(20) NOT NULL AUTO_INCREMENT,
  field_name varchar(50) NOT NULL,
  lookup_group varchar(255) NOT NULL,
  PRIMARY KEY (cld_field_options_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;


CREATE UNIQUE INDEX idx_cld_field_options_field_name on cld_field_options(field_name);

-- cld_field_options

insert into cld_field_options(field_name,lookup_group)
select 'company_size_id' as field,'BUYER_REGN_COMPANY_SIZE' as group_name
union
select 'company_industry' as field, 'BUYER_REGN_COMPANY_INDUSTRY' as group_name
union
select 'title_id' as field, 'BUYER_REGN_TITLE' as group_name
union
select 'address_state' as field, 'ADDRESS_STATE_US' as groupName;


select *
from cld_field_options;


-- CLD_TEST_DATA

CREATE TABLE cld_test_data (
  cld_test_data_id int(11) NOT NULL AUTO_INCREMENT,
  param_key varchar(255) DEFAULT NULL,
  param_value varchar(255) DEFAULT NULL,
  PRIMARY KEY (cld_test_data_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;


INSERT INTO cld_test_data
(param_key,param_value)
select 'address_city' as param_key, 'Waltham' as param_value
union
select 'address_country' as param_key, 'usa' as param_value
union
select 'address_state' as param_key, 'MA' as param_value
union
select 'address_street_line1' as param_key, '225 Wyman Street' as param_value
union
select 'address_street_number' as param_key, '225' as param_value
union
select 'address_zip' as param_key, '02451' as param_value
union
select 'alt_phone' as param_key, '9781111111' as param_value
union
select 'company_industry' as param_key, 'Advertising/Marketing/PR' as param_value
union
select 'company_name' as param_key, 'BuyerZone Test Lead' as param_value
union
select 'company_size_id' as param_key, '1 to 9' as param_value
union
select 'email' as param_key, 'testlead@buyerzone.com' as param_value
union
select 'first_name' as param_key, 'John' as param_value
union
select 'last_name' as param_key, 'Doe' as param_value
union
select 'phone' as param_key, '888-393-5000' as param_value
union
select 'phone_extn' as param_key, '1234' as param_value
union
select 'title_id' as param_key, 'CFO' as param_value;



SELECT * FROM cld_test_data;


-- cld_salesforce_default

CREATE TABLE cld_salesforce_default (
  cld_salesforce_default_id int(11) NOT NULL AUTO_INCREMENT,
  mapping_type_id int NOT NULL,
  source varchar(255) DEFAULT NULL,
  target varchar(255) DEFAULT NULL,
  PRIMARY KEY (cld_salesforce_default_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;


INSERT INTO cld_salesforce_default
(mapping_type_id, source, target)
select 1, 'address_street_line1' as source, 'street' as target
union
select 1, 'address_city' as source, 'city' as target
union
select 1, 'address_state' as source, 'state' as target
union
select 2, 'USA' as source, 'country' as target
union
select 1, 'address_zip' as source, 'zip' as target
union
select 2, 'BuyerZone Lead' as source, 'campaign' as target
union
select 1, 'company_name' as source, 'company' as target
union
select 1, 'email' as source, 'email' as target
union
select 1, 'first_name' as source, 'first_name' as target
union
select 1, 'last_name' as source, 'last_name' as target
union
select 1, 'phone' as source, 'phone' as target
union
select 1, 'title_id' as source, 'title' as target
union 
select 2, 'BuyerZone - {category}' as source,  'lead_source' as target
union
select 5, 'BuyerZone Quote_id; industry; employees; questionWithAnswers' as source, 'description' as target;

select * from cld_salesforce_default;


create table cld_default_config (
	cld_default_config_id int not null auto_increment,
    param_key varchar(255) not null,
    param_value varchar(255) not null,
    created_date datetime,
    primary key(cld_default_config_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

INSERT INTO cld_default_config
(param_key, param_value, created_date)
select 'question_answer_separator', ' ', now()
union
select 'question_set_separator', ' | ', now();

select * from cld_default_config;

-- cld_mappings test data

/*
insert into cld_mappings(mapping_type_id,lead_delivery_preference_id, param_name, param_value, translation_id)
select 1 as mapping_type, 27782 as prefId, 'first_name' as param_name, 'first_name' as param_value, null as translation
union
select 1 as mapping_type, 27782 as prefId, 'last_name' as param_name, 'last_name' as param_value, null as translation
union
select 1 as mapping_type, 27782 as prefId, 'company_size' as param_name, 'company_size' as param_value, 1 as translation
union
select 1 as mapping_type, 27782 as prefId, 'company' as param_name, 'company_name' as param_value, 1 as translation
union
select 2 as mapping_type, 27782 as prefId, 'oid' as param_name, '00Dc0000003vBIL' as param_value, null as translation
union 
select 1 as mapping_type, 27782 as prefId, 'street' as param_name, 'address_street_line1' as param_value, null as translation
union
select 1 as mapping_type, 27782 as prefId, 'zip' as param_name, 'address_zip' as param_value, null as translation
union
select 1 as mapping_type, 27782 as prefId, 'city' as param_name, 'address_city' as param_value, null as translation
union
select 1 as mapping_type, 27782 as prefId, 'state' as param_name, 'address_state' as param_value, null as translation
union
select 1 as mapping_type, 27782 as prefId, 'email' as param_name, 'email' as param_value, null as translation
union
select 3 as mapping_type, 27782 as prefId, 'horse_power' as param_name, 164609 as param_value, null as translation;

*/


/* Validate */

desc buyer_registration;

select *
from cld_mappings
where lead_delivery_preference_id = 27782;

select *
from cld_field_options;


select *
from cld_translations;

select *
from cld_mapping_types;

select *
from cld_test_data;

/* Updates 

alter table cld_mappings
add column version bigint(20);

alter table cld_translations
add column cld_mapping_id bigint(20);

alter table cld_mappings
add column is_live bit;

alter table cld_mappings
add column question_answer_separator varchar(10);

alter table cld_mappings
add column question_set_separator varchar(10);

update cld_mappings
set version = 1, is_live = 1
where lead_delivery_preference_id = 27782;


*/












    
    


