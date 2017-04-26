DELIMITER $$

DROP PROCEDURE IF EXISTS dz_getCategoryQuestions$$

CREATE PROCEDURE dz_getCategoryQuestions(in categoryId int)
begin

	select qs.category_id as 'categoryId', q.xml_id as 'questionId', q.type, qs.question_set_id as 'questionSetId', c.display_name as 'displayName',
    q.buyer_text as 'buyerText', q.supplier_text as 'sourceValue', c.content_path as 'contentPath',
	concat('http://www.buyerzone.com',c.content_path,qs.version,'.xml') as 'questionSetUrl', qs.version
	from question_set qs, category c, question q
	where qs.category_id = c.category_id  and qs.question_set_id = q.question_set_id
	and c.category_request_type_id != 2113 -- Not RFX
	and c.category_id = categoryId
	and qs.question_set_id = (select max(question_set_id) from question_set where category_id = c.category_id)
	and q.xml_id is not null;

    /* Test:
    call dz_getCategoryQuestions('business phone systems');
    call dz_getCategoryQuestions('home security systems');
    */
    
END$$
DELIMITER ;