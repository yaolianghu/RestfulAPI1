DELIMITER $$

DROP PROCEDURE IF EXISTS dz_getCldUnmappedQuestionAnswers$$

CREATE PROCEDURE dz_getCldUnmappedQuestionAnswers(in preferenceId int,in versionNum int, in questionSetId int)
begin

	select a.question_id as questionId, q.supplier_text as sellerQuestion,ifnull(a.supplier_text,a.label) as answerOption, q.type as questionType
	from question q, answer a
	where q.question_id = a.question_id
	and q.question_set_id = questionSetId
    and q.question_id not in(select param_value from cld_mappings 
						   where lead_delivery_preference_id = preferenceId
                           and version = versionNum);
    
    /* Test:
    call dz_getCldUnmappedQuestionAnswers(27813,1,13485);
    */
    
END$$
DELIMITER ;