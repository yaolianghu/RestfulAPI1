DELIMITER $$

DROP PROCEDURE IF EXISTS dz_getTableInfo$$

CREATE PROCEDURE dz_getTableInfo(in tableName varchar(255))
BEGIN
	select column_name as 'columnName' 
    from information_schema.columns 
    where table_name = 'buyer_registration'
    and table_schema = 'bz20'
    and column_name not in('address_geo_latitude',
    'address_geo_longitude','address_county',
    'news_letter_subscribe','status_id',
    'date_created','date_modified','address_street_line2','buyer_id',
    'buyer_registration_id','buyer_registration_id','company_id')
    union all
    select '{quoteId}' as 'columnName'
    union all
    select 'full_name' as 'columnName' 
    union all
    select 'sent_date' as 'columnName';
    /* Test:
    call dz_getTableInfo('buyer_registration');
    */
    
END$$
DELIMITER ;
