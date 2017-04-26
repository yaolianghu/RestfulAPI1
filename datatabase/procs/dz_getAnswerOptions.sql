DELIMITER $$

DROP PROCEDURE IF EXISTS dz_getAnswerOptions$$

CREATE PROCEDURE dz_getAnswerOptions(in questionSetId int)
begin
/*
	select q.question_set_id as questionSetId, q.type as questionType, q.buyer_text as buyerText, 
	q.supplier_text as supplierText, a.xml_id as xmlId, a.label as answerOption, a.question_id as questionId
	from question q, answer a
	where q.question_id = a.question_id
	and q.question_set_id = questionSetId;
    */
    
	select q.xml_id as questionXmlId, case when q.type = 'textmultiple' then a.xml_id else q.xml_id end as questionId, ifnull(a.supplier_text,a.label) as answerOption, q.type as questionType
	from question q, answer a
	where q.question_id = a.question_id
	and q.question_set_id = questionSetId;
    
    /* Test:
    call dz_getAnswerOptions(13018);
    call dz_getAnswerOptions(13249);
    call dz_getAnswerOptions(13171);
    */
    
END$$
DELIMITER ;