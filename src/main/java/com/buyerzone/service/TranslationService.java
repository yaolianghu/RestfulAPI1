package com.buyerzone.service;

import java.util.List;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.StatementType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.buyerzone.dao.mapper.TranslationMapper;
import com.buyerzone.model.cld.CustomMapping;
import com.buyerzone.model.cld.Translation;

@Service
public class TranslationService {

	@Autowired
	private TranslationMapper translationMapper;
	
	public int insertTranslationMapping(@Param("translations") List<Translation> translations) {
		return translationMapper.insertTranslationMapping(translations);
	}
	
	public int getNewTranslationIds(@Param("translation") Translation translation) {
		return translationMapper.getNewTranslationIds(translation);
	}
	
	public List<Translation> getDataBySingleParam(@Param("proc") String procName, @Param("param") String param) {
		return translationMapper.getDataBySingleParam(procName, param);
	}
}
