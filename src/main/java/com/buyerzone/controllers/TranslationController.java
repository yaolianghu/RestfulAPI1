package com.buyerzone.controllers;

import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.buyerzone.model.Response;
import com.buyerzone.model.cld.Translation;
import com.buyerzone.service.TranslationService;

@RestController
public class TranslationController {
	Logger logger = Logger.getLogger(CustomLeadController.class);
	
	@Autowired
	private TranslationService translationService;

	@RequestMapping(value = "/lead/translation/save", method = RequestMethod.POST)
	public Response saveTranslation(@RequestBody List<Translation> translations) {
		String msg = String.format("Successfully save a translation.");
		String errMsg = String.format("Failed to save a translation.");
		Response response = null;
		int row = 0;
		try {
		    logger.info(String.format("Saving translation data."));
		    row = translationService.insertTranslationMapping(translations);
		    
	    	response = new Response();
	    	response.setDeveloperMessage(msg);
	    	response.setResponseCode(HttpStatus.SC_OK);
	    	response.setResponseMessage(msg);
	    	logger.info(msg);

	    	return response;
			
		} catch (BeansException e) {

			logger.error(errMsg + "." + e.getMessage(), e);
			response = new Response();

			response.setResponseMessage("Failed.");
			response.setResponseCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			response.setDeveloperMessage(e.getMessage());

			return response;

		} catch (IllegalStateException e) {

			logger.error(errMsg + "." + e.getMessage(), e);
			response = new Response();

			response.setResponseMessage("Failed.");
			response.setResponseCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			response.setDeveloperMessage(e.getMessage());

			return response;
		}

	}
}
