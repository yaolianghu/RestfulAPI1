DELIMITER $$

DROP PROCEDURE IF EXISTS dz_getAnswerOptionsNew$$

CREATE PROCEDURE dz_getAnswerOptionsNew(in questionSetId int)
begin
/*
	select q.question_set_id as questionSetId, q.type as questionType, q.buyer_text as buyerText, 
	q.supplier_text as supplierText, a.xml_id as xmlId, a.label as answerOption, a.question_id as questionId
	from question q, answer a
	where q.question_id = a.question_id
	and q.question_set_id = questionSetId;
    */
	
	select q.supplier_text as question, q.xml_id as questionId, q.buyer_text, ifnull(a.label, 'Test') as answer 
	from question q, answer a where question_set_id = questionSetId
	and q.question_id = a.question_id
	group by q.xml_id;
    
    /* Test:
    call dz_getAnswerOptions(13018);
    call dz_getAnswerOptions(13249);
    call dz_getAnswerOptions(13171);
    */
    
END$$
DELIMITER ;