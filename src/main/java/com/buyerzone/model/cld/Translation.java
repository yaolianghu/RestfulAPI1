package com.buyerzone.model.cld;

public class Translation {
	private int translationId;
	private String option;
	private String translatedOption;
	private int mappingId;
	
	
	public String getOption() {
		return option;
	}
	public void setOption(String option) {
		this.option = option;
	}
	public String getTranslatedOption() {
		return translatedOption;
	}
	public void setTranslatedOption(String translatedOption) {
		this.translatedOption = translatedOption;
	}
	public int getMappingId() {
		return mappingId;
	}
	public void setMappingId(int mappingId) {
		this.mappingId = mappingId;
	}
	public int getTranslationId() {
		return translationId;
	}
	public void setTranslationId(int translationId) {
		this.translationId = translationId;
	}
	
}
