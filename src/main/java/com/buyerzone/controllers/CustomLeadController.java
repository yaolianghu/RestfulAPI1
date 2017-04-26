package com.buyerzone.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.buyerzone.model.Response;
import com.buyerzone.model.cld.CategoryDefinition;
import com.buyerzone.model.cld.CustomMapping;
import com.buyerzone.model.cld.CustomNewMapping;
import com.buyerzone.model.cld.Translation;
import com.buyerzone.service.CustomLeadService;
import com.buyerzone.service.GlobalService;
import com.buyerzone.service.RegistrationService;
import com.buyerzone.service.SupplierCategoryService;
import com.buyerzone.service.TranslationService;
import com.buyerzone.util.ApiResponse;

@RestController
public class CustomLeadController {
	Logger logger = Logger.getLogger(CustomLeadController.class);
    
    @Autowired
    private GlobalService dataService;
    
    @Autowired
    private SupplierCategoryService supplierCategoryService;
    
    @Autowired
    private TranslationService translationService;
    
    @Autowired
    private CustomLeadService customLeadService;
    
    @Autowired
    private RegistrationService regsitrationService;
    
    @RequestMapping(value = "/lead/mappings/save", method=RequestMethod.POST)
    public List<CustomMapping> saveLead(@RequestBody List<CustomMapping> mappings) {
    	String errMsg = String.format("Failed to save a lead.");
    	int row = 0;
    	List<CustomMapping> customs = null;
    	List<Translation> translations = new ArrayList<Translation>();
    	int insertedRow = 0;
    	int deletedRow = 0;
    	int maxVersion = 0;
    	int versionNumber = 0;
    	boolean checkVersionFlag = false;
    	List<Integer> versions = new ArrayList<Integer>();
    	List<CustomMapping> newMappings = null;
    	CustomMapping templateMap = null;
     	try {
     		logger.info(String.format("Getting mapping data. Preference Id = [%d]", mappings.get(0).getPreferenceId()));
     		newMappings = new ArrayList<CustomMapping>();
     		
     		for(CustomMapping map : mappings) {
     			if(map.getMappingType() == 4) {
     				if(map.getQuestionAnswerSeparator() == null ) 
     					map.setQuestionAnswerSeparator(" ");
     				if(map.getQuestionSetSeparator() == null)
     					map.setQuestionSetSeparator(" | ");
     			}
     		}
     		
     		for(CustomMapping map : mappings) {
     			
 				templateMap = new CustomMapping();
 				templateMap.setAnswers(map.getAnswers());
 				templateMap.setMappingId(map.getMappingId());
 				templateMap.setMappingType(map.getMappingType());
 				templateMap.setPreferenceId(map.getPreferenceId());
 				templateMap.setQuestionId(map.getQuestionId());
 				templateMap.setQuestionSetId(map.getQuestionSetId());
 				templateMap.setSourceValue(map.getSourceValue());
 				templateMap.setTargetName(map.getTargetName());
 				templateMap.setVersion(map.getVersion());
 				templateMap.setIsLive(map.getIsLive());
     			templateMap.setQuestionAnswerSeparator(map.getQuestionAnswerSeparator());
     			templateMap.setQuestionSetSeparator(map.getQuestionSetSeparator());
     			newMappings.add(templateMap);
     		}
     		
     		for(CustomMapping map : mappings) {
     			if(map.getTargetName() == null) {
     				map.setTargetName(map.getSourceValue());
     			}
     		}
     		
    		customs = customLeadService.getCustomMappingByPreferenceId(mappings.get(0).getPreferenceId());
    		for(CustomMapping map : mappings) {
    			if(customs.isEmpty() || customs == null) {
    				map.setVersion(1);
    			}
    			else if(!customs.isEmpty() && map.getVersion() == 0 && map.getMappingId() == 0) {
    				for(CustomMapping custom : customs) {
    					versions.add(custom.getVersion());
    				}
    				Collections.sort(versions);
    				maxVersion = versions.get(versions.size() - 1);
    				map.setVersion(maxVersion+1);
    			}
    			else {
    				versionNumber = map.getVersion();
    				deletedRow = customLeadService.deleteCustomMappingByVersion(versionNumber, map.getPreferenceId());
    				logger.info(String.format("Deleted [%d] existing mapping data. Preference Id = [%d] and version = [%d].", deletedRow, map.getPreferenceId(), versionNumber));
    				checkVersionFlag = true;
    				break;
    			}
    		}
    		
    		if(checkVersionFlag) {
    			for(CustomMapping map : mappings) {
    				map.setVersion(versionNumber);
    			}
    		}
    		
    		for(CustomMapping map : mappings) {
    			if(map.getMappingType() == 3) 
    				map.setSourceValue(String.valueOf(map.getQuestionId()));
    		}
    		
			insertedRow = customLeadService.insertCustomMapping(mappings);
			
    		customs = customLeadService.getCustomMappingByPreferenceId(mappings.get(0).getPreferenceId());
    		for(CustomMapping customMap : customs) {
    			for(CustomMapping map : mappings) {
    				if(customMap.getMappingType() == map.getMappingType() && customMap.getPreferenceId() == map.getPreferenceId() &&
    						customMap.getSourceValue().equals(map.getSourceValue()) && customMap.getTargetName().equals(map.getTargetName()) &&
    						customMap.getVersion() == map.getVersion()) { 					
    					map.setMappingId(customMap.getMappingId());
    					logger.info(String.format("Saved mapping data. Mapping Id = [%d]", map.getMappingId()));
    				}
    			}
    		}
    		
    		for(CustomMapping map : mappings) {
    			if(map.getAnswers() != null) {
	    			for(Translation translation : map.getAnswers()) {
	    				translation.setMappingId(map.getMappingId());
	    				if(translation.getTranslatedOption() != null)
	    					translations.add(translation);
	    					//translation.setTranslatedOption(translation.getOption());
	    			}
    			}
    		}
    		if(!translations.isEmpty())
    		{
    			row = translationService.insertTranslationMapping(translations);
	    		for(Translation trans : translations) {   			
	    			trans.setTranslationId(translationService.getNewTranslationIds(trans));
	    			logger.info(String.format("Saved translation data. Transpation Id = [%d]", trans.getTranslationId()));
	    		}
    		}
    		for(CustomMapping cm : mappings) {
    			for(CustomMapping newCm : newMappings) {
    				if(cm.getQuestionId() != null && newCm.getQuestionId() != null && cm.getQuestionId() == newCm.getQuestionId()) {
    					cm.setSourceValue(newCm.getSourceValue());
    				}
    			}
    		}
    		newMappings = mappings;
    		
    		return newMappings;
    		
    	} catch (BeansException e) {
			
			logger.error(errMsg + "." + e.getMessage(),e);
			return newMappings;
			
		} catch (IllegalStateException e) {
			
			logger.error(errMsg + "." + e.getMessage(),e);
			return newMappings;
		}
     	catch (Exception e) {
			
			logger.error(errMsg + "." + e.getMessage(),e);
			return newMappings;
		}
    	
    }
    
    
    @RequestMapping(value = "/lead/mappings/get")
    public List<CustomMapping> getData(@RequestParam(value = "supplierCatId") int supplierCatId, @RequestParam(value = "version", defaultValue = "0") int version, HttpServletRequest methodRequest) {

    	CustomMapping custom = null;
    	List<CustomNewMapping> customNewMappings = null;
        List<CustomMapping> customMappings = null;
        List<Translation> answers = null;
    	int latestVersion = 0;
    	List<CustomMapping> responses = null;
    	CategoryDefinition info = new CategoryDefinition();
    	String methodName;
    	try {
    		methodName = methodRequest.getRequestURL().toString() + "?supplierCatId=" + methodRequest.getParameter("supplierCatId") + "&version=" + methodRequest.getParameter("version") ;
    		logger.info(methodName);
    		logger.info(String.format("Getting mapping data. Supplier Category Id = [%d]", supplierCatId));
    		customMappings = new ArrayList<CustomMapping>();
    		answers = new ArrayList<Translation>();
    		responses = new ArrayList<CustomMapping>();
    		info = supplierCategoryService.getSupplierCategoryInfo(supplierCatId);
		    custom = customLeadService.getPreferenceId("dz_getLeadPreferences", String.valueOf(supplierCatId));  
		    logger.info(String.format("Getting Preference Id according to supplier category Id. Supplier Category Id = [%d]", supplierCatId));
		    customMappings = customLeadService.getCustomMappingByPreferenceId(custom.getPreferenceId());
		    if(customMappings == null || customMappings.isEmpty()) {
		    	return responses;
		    }
		    if(version == 0) {
		    	latestVersion = customLeadService.getLatestVersion(custom.getPreferenceId()); 
		    }
		    else
		    	latestVersion = version;
		    customMappings.clear();
		    logger.info(String.format("Getting version. Version = [%s]", latestVersion));
		    customNewMappings = customLeadService.getCustomNewMapping("dz_getCldMappingsAndOptions", custom.getPreferenceId(), latestVersion, info.getQuestionSetId());
		    
		    for(CustomNewMapping newMapping : customNewMappings) {
				
		    	CustomMapping cMapping = new CustomMapping();
				cMapping.setMappingId(newMapping.getMappingId());
				cMapping.setMappingType(newMapping.getMappingTypeId());
				cMapping.setPreferenceId(newMapping.getPreferenceId());
				cMapping.setQuestionId(newMapping.getQuestionId());
				cMapping.setQuestionSetId(newMapping.getQuestionSetId());
				cMapping.setQuestionAnswerSeparator(newMapping.getQuestionAnswerSeparator());
				cMapping.setQuestionSetSeparator(newMapping.getQuestionSetSeparator());
				if(newMapping.getMappingTypeId() != 3) 
					cMapping.setSourceValue(newMapping.getSourceValue());
				else 
					cMapping.setSourceValue(newMapping.getSellerQuestion());
				cMapping.setTargetName(newMapping.getTargetName());
				cMapping.setVersion(newMapping.getVersion());
				
				Translation translation = new Translation();
				if(newMapping.getTranslationId() > 0) {
					translation.setMappingId(newMapping.getMappingId());
					translation.setOption(newMapping.getValueToTranslate());
					translation.setTranslatedOption(newMapping.getTranslatedValue());
					translation.setTranslationId(newMapping.getTranslationId());
					answers.add(translation);
				} else if(newMapping.getMappingTypeId() != 3 && newMapping.getOption() != null && !newMapping.getOption().isEmpty()){
					translation.setMappingId(newMapping.getMappingId());
					translation.setOption(newMapping.getOption());
					translation.setTranslationId(newMapping.getTranslationId());
					answers.add(translation);
				} else if(newMapping.getMappingTypeId() == 3 && newMapping.getAnswerOption() != null && !newMapping.getAnswerOption().isEmpty() ) {
					translation.setMappingId(newMapping.getMappingId());
					translation.setOption(newMapping.getAnswerOption());
					translation.setTranslationId(newMapping.getTranslationId());
					answers.add(translation);
				}				
				
				customMappings.add(cMapping);
			}
		    
		    Set<Integer> set = new HashSet<Integer>();
		    for( CustomMapping item : customMappings ) {
		        if(set.add(item.getMappingId())) {
		        	responses.add( item );
		        }
		    }
		    
		    for(CustomMapping m : responses) {
		    	List<Translation> trans = new ArrayList<Translation>();
		    	for(Translation t : answers) {
		    		if(t.getMappingId() == m.getMappingId()) {
		    			trans.add(t);
		    		}
		    	}
		    	m.setAnswers(trans);
		    }
		    logger.info(String.format("Getting Response data. Response Size = [%d]", responses.size()));
		    return responses;
			
		} catch (BeansException e) {
			logger.error("Error", e);
			return responses;
		} catch (IllegalStateException e) {
			logger.error("Error", e);
			return responses;
		} catch (Exception e) {
			logger.error("Error", e);
			return responses;
		}
    }
    
    
    @RequestMapping(value = "/lead/mappings/publish", method=RequestMethod.POST)
    public ApiResponse<List<CustomMapping>> publishLead(@RequestBody CustomMapping mapping) {
    	String msg = String.format("Successfully publish. PreferenceId = [%s], version = [%s]", mapping.getPreferenceId(), mapping.getVersion());
    	String errMsg = String.format("Failed to publish. PreferenceId = [%s], version = [%s]", mapping.getPreferenceId(), mapping.getVersion());
    	String methodName = "publishLead";
    	Response response = null;
    	ApiResponse<List<CustomMapping>> api = null;
    	try {
    		logger.info(String.format("Update publish. PreferenceId = [%s], version = [%s]", mapping.getPreferenceId(), mapping.getVersion()));
    		int rowUpdated = customLeadService.removeLive(mapping.getPreferenceId());
    		rowUpdated = customLeadService.updateCustomMapping(mapping.getVersion(), mapping.getPreferenceId());
    		api = new ApiResponse<List<CustomMapping>>();
    		if(rowUpdated > 0) {
    			response = new Response();
    			response.setResponseCode(HttpStatus.SC_OK);
    			response.setResponseMessage(msg);
    			api.setMethodName(methodName);
    			api.setResponse(response);
    			logger.info(msg);
    			return api;
    		} 
    		else {
    			response = new Response();
    			response.setResponseCode(HttpStatus.SC_OK);
    			response.setResponseMessage("No data updated.");
    			api.setMethodName(methodName);
    			api.setResponse(response);
    			return api;
    		}
		   
		} catch (BeansException e) {
			
			logger.error(errMsg + "." + e.getMessage(),e);
			api = new ApiResponse<List<CustomMapping>>();
		    response = api.getObject(Response.class);
			response.setResponseMessage("Failed.");
			response.setResponseCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			response.setDeveloperMessage(e.getMessage());
			api.setResponse(response);
			api.setMethodName(methodName);
			logger.error("Error", e);
			return api;
			
			
		} catch (IllegalStateException e) {
			
			logger.error(errMsg + "." + e.getMessage(),e);
			api = new ApiResponse<List<CustomMapping>>();
		    response = api.getObject(Response.class);
			
			response.setResponseMessage("Failed.");
			response.setResponseCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			response.setDeveloperMessage(e.getMessage());
			api.setResponse(response);
			api.setMethodName(methodName);
			logger.error("Error", e);
			return api;
		} catch(Exception e) {
			logger.error(errMsg + "." + e.getMessage(),e);
			api = new ApiResponse<List<CustomMapping>>();
		    response = api.getObject(Response.class);
			
			response.setResponseMessage("Failed.");
			response.setResponseCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			response.setDeveloperMessage(e.getMessage());
			api.setResponse(response);
			api.setMethodName(methodName);
			logger.error("Error", e);
			return api;
		}
    }
    
    
    @RequestMapping(value = "/lead/mappings/default/save", method=RequestMethod.POST)
    public List<CustomMapping> saveDefaultLead(@RequestParam("supplierCatId") int supplierCatId, @RequestParam(value = "version", required=false, defaultValue="0") int version, HttpServletRequest methodRequest) {
    	String errMsg = String.format("Failed to save a standard lead.");
    	List<CustomMapping> newMappings = null;
    	List<CustomMapping> standardMappings = null;
    	int insertedRow = 0;
    	int deletedRow = 0;
    	CustomMapping custom = null;
    	String methodName = null;
    	try {
    		methodName = methodRequest.getRequestURL().toString() + "?supplierCatId=" + methodRequest.getParameter("supplierCatId") + "&version=" + methodRequest.getParameter("version") ;
    		logger.info(methodName);
    		logger.info(String.format("Getting lead data. supplierCatId = [%d] and version = [%d]", supplierCatId, version));
    		custom = customLeadService.getPreferenceId("dz_getLeadPreferences", String.valueOf(supplierCatId)); 
    		if(version != 0) {
    			deletedRow = customLeadService.deleteCustomMappingByVersion(version, custom.getPreferenceId());
    			logger.info(String.format("Deleted existing mapping data according to the preferenceId = [%d] and version = [%d]. Deleted Row = [%d]", custom.getPreferenceId(), version, deletedRow));
    		}
    		
    		String categoryName = customLeadService.getCategoryName(supplierCatId);
    		if(custom.getPreferenceId() != 0) {
    			standardMappings = customLeadService.getDefaultLead();
    			for(CustomMapping map : standardMappings) {
    				map.setPreferenceId(custom.getPreferenceId());
    				if(version == 0)
    					map.setVersion(1);
    				else 
    					map.setVersion(version);
    				map.setIsLive(0);
    				if(map.getSourceValue().contains("{category}") && categoryName != null) {
    					String newSourceValue = map.getSourceValue().replace("{category}", categoryName);
    					map.setSourceValue(newSourceValue);
    				}
    				if(map.getMappingType() == 5) {
    					map.setQuestionSetSeparator("; ");
    					map.setQuestionAnswerSeparator(" ");
    				}
    			}
    			
    			insertedRow = customLeadService.insertCustomMapping(standardMappings);
    			logger.info(String.format("Inserted mapping data according to the preferenceId = [%d] and version = [%d]. Inserted Row = [%d]", custom.getPreferenceId(), standardMappings.get(0).getVersion(), insertedRow));
    			if(insertedRow > 0) {
	    			if(version == 0)
	    				newMappings = getData(supplierCatId, 1, methodRequest);
	    			else
	    				newMappings = getData(supplierCatId, version, methodRequest);
    			}
    		}
    		if(newMappings != null)
    			return newMappings;
    		else 
    			return new ArrayList<CustomMapping>();
    		
    	} catch (BeansException e) {
			
			logger.error(errMsg + "." + e.getMessage(),e);
			return new ArrayList<CustomMapping>();
			
		} catch (IllegalStateException e) {
			
			logger.error(errMsg + "." + e.getMessage(),e);
			return new ArrayList<CustomMapping>();
		}
     	catch (Exception e) {
			
			logger.error(errMsg + "." + e.getMessage(),e);
			return new ArrayList<CustomMapping>();
		}
    	
    }
 
}
