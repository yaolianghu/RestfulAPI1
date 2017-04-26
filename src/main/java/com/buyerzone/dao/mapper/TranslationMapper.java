package com.buyerzone.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.StatementType;

import com.buyerzone.model.cld.CustomMapping;
import com.buyerzone.model.cld.Translation;

public interface TranslationMapper {

	@Insert({
		"<script>"
		        + "INSERT INTO cld_translations"
		        + " (original_value, new_value, cld_mapping_id, created_date) "
		        + "VALUES"
		        + "<foreach collection='translations' item='element' open='(' separator='),(' close=')'>"
		        + "#{element.option}, #{element.translatedOption}, #{element.mappingId}, Now()"
		        + "</foreach>"
			  	+ "</script>"
	})
	int insertTranslationMapping(@Param("translations") List<Translation> translations);

	
	@Select({"<script>"
	  		+ " CALL dz_getCldNewTranslationIds(#{translation.option}, #{translation.translatedOption}, #{translation.mappingId})"
	  		+ "</script>"})
	@Options(statementType = StatementType.CALLABLE)
	int getNewTranslationIds(@Param("translation") Translation translation);
	
	@Select({" CALL ${proc}(#{param})"})
	@Options(statementType = StatementType.CALLABLE)
	List<Translation> getDataBySingleParam(@Param("proc") String procName, @Param("param") String param);
}
