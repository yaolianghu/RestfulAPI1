DELIMITER $$

DROP PROCEDURE IF EXISTS dz_getAnswerByQuestionAndRequestId$$

CREATE PROCEDURE dz_getAnswerByQuestionAndRequestId(in questionId int, in quoteRequestId int)
BEGIN
	select qrp.quote_request_id, q.question_id,q.supplier_text as question, qrp.param_key,
	case when a.label is null then qrp.param_value else a.label end as answer, q.type  
	from quote_request_param qrp 
	join quote_request qr on qr.quote_request_id = qrp.quote_request_id
	join question q on q.xml_id = qrp.param_key
	and q.question_set_id = (select max(question_set_id) from question_set where category_id = qr.category_id) 
	left join answer a on a.question_id = q.question_id and a.xml_id = qrp.param_value
	where qrp.quote_request_id =  quoteRequestId -- 8817157
	UNION ALL 
	-- This is explicitly needed for getting question/answers with in a question. Ex: Steel Buildings.
	select qrp.quote_request_id,q.question_id, a.supplier_text as question , qrp.param_key, qrp.param_value as answer, q.type  
	from quote_request_param qrp 
	join quote_request qr on qr.quote_request_id = qrp.quote_request_id
	join answer a on a.xml_id = qrp.param_key
	join question q on q.question_id = a.question_id
	and q.question_set_id = (select max(question_set_id) from question_set where category_id = qr.category_id) 
	where qrp.quote_request_id =  quoteRequestId;

END$$
DELIMITER ;