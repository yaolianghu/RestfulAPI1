package com.buyerzone.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import com.buyerzone.dao.HttpManager;
import com.buyerzone.model.LeadPreference;
import com.buyerzone.model.Response;
import com.buyerzone.model.cld.CategoryDefinition;
import com.buyerzone.model.cld.QuestionWithAnswer;
import com.buyerzone.service.CustomLeadService;
import com.buyerzone.service.GlobalService;
import com.buyerzone.service.RegistrationService;
import com.buyerzone.service.SupplierCategoryService;
import com.buyerzone.util.CommonUtil;
import com.buyerzone.util.MessageUtil;

@RestController
public class LeadXMLController {
	Logger logger = Logger.getLogger(LeadXMLController.class);

	private static final String[][] swapFields = new String[][] {
    	{"city", "address_city"},
    	{"stateId", "address_state"},
    	{"postCode", "address_zip"},
    	{"firstName", "first_name"},
    	{"lastName", "last_name"},
    	{"streetName", "address_street_line1"},
    	{"companyName", "company_name"},
    	{"companyIndustry", "industry"},
    	{"companySize", "company_size"},
    	{"streetLine2", "address_street_line2"},
    	{"title", "title"},
    	{"phone", "phone"},
    	{"altPhone", "alt_phone"}
    };
    
	@Value("${env.bzbaseurl}")
    public String bzBaseUrl;

    @Autowired
    private SupplierCategoryService supplierCategoryService;
    
    @Autowired
    private RegistrationService registrationService;
    
    @Autowired
    private GlobalService globalService;
    
    @Autowired
    private CustomLeadService customLeadService;
    
    @Value("${env.baseurl}")
    public String baseURL;
    
    @RequestMapping(value = "/xml/send", method=RequestMethod.POST)
    public ResponseEntity<HashMap<String, Object>> sendLead(@RequestParam(value="quoteId", required=false, defaultValue="12345678") int quoteId, 
    														@RequestParam(value="leadPreferenceId", required=false, defaultValue="12345") int leadPreferenceId,
    														@RequestParam(value="supplierCatId", required=false, defaultValue="12345") int supplierCatId,
    														@RequestParam(value="url", required=false, defaultValue="") String url,
    														@RequestParam(value="test", required=false, defaultValue="false") boolean test,
    														HttpServletRequest methodRequest){
    	String methodName = null;
    	HashMap<String, Object> responses = new HashMap<String, Object>();
    	ResponseEntity<HashMap<String, Object>> content = null;
    	LeadPreference deliveryConfig = null;
    	int supplierId = 0;
    	String params = null;
    	Response response = null;
    	String type = "XML";
    	StringBuffer paramsLog = null;
    	boolean internal = true;
    	try {
    		methodName = CommonUtil.getMethodName(methodRequest);
    		logger.info(String.format("Getting API call. URL = [%s]", methodName));
    		if(url!= null && !url.isEmpty()) {
    			url = url.replace("/mnt/incoming_data/BZ/", "/incoming/");
    			logger.info(String.format("Getting xslt URL = [%s]", url));
    		}
    		if(test) 
    			content = this.getTestContent(quoteId, supplierCatId, url, internal, methodRequest);
    		else 
    			content = this.getContent(quoteId, leadPreferenceId, supplierCatId, url, false, internal, methodRequest);

    		if(content != null && content.getStatusCode().is2xxSuccessful()) {
	    		for(String key : content.getBody().keySet()) {
	    			params = key + "=" + content.getBody().get(key);
	    		}
			
	    		HttpManager request = new HttpManager();
	    		if(test)
	    			supplierId = supplierCatId;
	    		else
	    			supplierId = customLeadService.getSupplierCatId(leadPreferenceId);

	    		deliveryConfig = registrationService.getXmlLeadPreferences(supplierId, 7);
	    		if(deliveryConfig != null) {
	    			String postUrl = deliveryConfig.getTargetUrl() != null ? deliveryConfig.getTargetUrl() : deliveryConfig.getTestUrl();
	    				
		    		if(postUrl != null) {
		    			
		    			if(url != null && !url.isEmpty()) {
		    				type = "Salesforce XML";
		    				params = escapeBack(params);
		    				Map<String, String> salesforceParams = new HashMap<String, String>();
		    				salesforceParams = this.extractParamValues(params.replace("N/A=", ""));
		    				
		    				paramsLog = new StringBuffer();
		    				for (Map.Entry<String, String> entry : salesforceParams.entrySet()) {
		    					paramsLog.append(entry.getKey() + "=" + entry.getValue() + "&");
		    				}
		    				if (paramsLog.length() > 0) {
		    					logger.debug(paramsLog.toString().substring(0, paramsLog.length() - 1));
		    				} else {
		    					logger.debug(MessageUtil.errorMessage(type, quoteId, postUrl, "No parameters found!", "No parameters found!"));
		    				}
		    				
		    				response = request.doPostNew(postUrl, salesforceParams);
		    			}
		    			else {
		    				
			    			Map<String, String> urlParameters = new HashMap<String, String>();
			    			for(String key : content.getBody().keySet()) {
				    			urlParameters.put(key, (String) content.getBody().get(key));
				    		}
			    			
			    			paramsLog = new StringBuffer();
		    				for (Map.Entry<String, String> entry : urlParameters.entrySet()) {
		    					paramsLog.append(entry.getKey() + "=" + entry.getValue() + "&");
		    				}
		    				if (paramsLog.length() > 0) {
		    					logger.debug(paramsLog.toString().substring(0, paramsLog.length() - 1));
		    				} else {
		    					logger.debug(MessageUtil.errorMessage(type, quoteId, postUrl, "No parameters found!", "No parameters found!"));
		    				}
			    			
			    			response = request.doPostNew(postUrl, urlParameters);
		    			}
		    				
			    		if(response.getResponseCode() == 200) {
			    			if (logger.isDebugEnabled()) {
			    				logger.debug(MessageUtil.successMessage(type, quoteId, response.getResponseCode(), postUrl, response.getResponseMessage(), paramsLog.toString().substring(0, paramsLog.length() - 1)));
			    			}
			    			else 
			    				logger.info(MessageUtil.successMessage(type, quoteId, response.getResponseCode(), postUrl, null, paramsLog.toString().substring(0, paramsLog.length() - 1)));
			    			
			    			responses.put("Response", response);
			    			return new ResponseEntity<HashMap<String, Object>>(responses, org.springframework.http.HttpStatus.OK);
			    		}
			    		else {
			    			if (logger.isDebugEnabled()) {
			    				logger.debug(MessageUtil.failedMessage(type, quoteId, response.getResponseCode(), postUrl, response.getResponseMessage(), paramsLog.toString().substring(0, paramsLog.length() - 1)));
			    			}
			    			else 
			    				logger.error(MessageUtil.failedMessage(type, quoteId, response.getResponseCode(), postUrl, null, paramsLog.toString().substring(0, paramsLog.length() - 1)));
			    			
			    			responses.put("Response", response);
			    			return new ResponseEntity<HashMap<String, Object>>(responses, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
			    		}
		    		}
		    		else {
		    			responses.put("response", String.format("No post url exists: [%s]", postUrl));
		    			logger.error(MessageUtil.errorMessage(type, quoteId, postUrl, "No post url exists.", "No post url exists."));
		    			return new ResponseEntity<HashMap<String, Object>>(responses, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
		    		}
	    		}
	    		else {
	    			responses.put("response", String.format("No lead preference data related to supplier category id: [%d]", supplierId));
	    			logger.error(MessageUtil.errorMessage(type, quoteId, null, "No lead preference data exists.", "No lead preference data exists."));
	    			return new ResponseEntity<HashMap<String, Object>>(responses, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
	    		}
    		}
    		else {
    			responses.put("response", String.format("No content exists with quote Id : [%d] and lead preference Id : [%d]", quoteId, leadPreferenceId));
    			logger.error(MessageUtil.errorMessage(type, quoteId, null, " No content exists.", " No content exists."));
    			return new ResponseEntity<HashMap<String, Object>>(responses, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
    		}
    		
    	} catch (BeansException e) {
    		logger.error(MessageUtil.errorMessage(type, quoteId, null, "Internal Server Error", e.getMessage()));
			responses.put("Internal Server Error: ", e.getMessage());
			return new ResponseEntity<HashMap<String, Object>>(responses, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
			
		} catch (IllegalStateException e) {
			logger.error(MessageUtil.errorMessage(type, quoteId, null, "Internal Server Error", e.getMessage()));
			responses.put("Internal Server Error: ", e.getMessage());
			return new ResponseEntity<HashMap<String, Object>>(responses, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
		} catch(Exception e) {
			logger.error(MessageUtil.errorMessage(type, quoteId, null, "Internal Server Error", e.getMessage()));
			responses.put("Internal Server Error: ", e.getMessage());
			return new ResponseEntity<HashMap<String, Object>>(responses, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
    @RequestMapping("/xml/content/test")
    public ResponseEntity<HashMap<String, Object>> getTestContent(@RequestParam(value="quoteId", required=false, defaultValue="12345678") int quoteId, 
			  @RequestParam("supplierCatId") int supplierCatId, 
			  @RequestParam(value="url", required=false, defaultValue="") String url,
			  @RequestParam(value="internal", required=false, defaultValue="false") boolean internal,
			  HttpServletRequest methodRequest){
    	HashMap<String, Object> xmlData = null;
    	String[] swappedFieldsArray = null;
    	String param = null;
    	String methodName = null;
    	HashMap<String, Object> output = new HashMap<String, Object>();
    	CategoryDefinition info = null;
    	String generateXmlString = null;
    	String getUrl = null;
    	String response = null;
    	boolean isDefault = false;
    	String type = "XML";
    	try {
    		methodName = CommonUtil.getMethodName(methodRequest);
    		logger.info(String.format("Getting API call. URL = [%s]", methodName));
    		if(url!= null && !url.isEmpty()) {
    			url = bzBaseUrl + url.replace("/mnt/incoming_data/BZ/", "/incoming/");
    			logger.info(String.format("Getting xslt URL = [%s]", url));
    		}
    		Map<String, Object> questionWithAnswers = new LinkedHashMap<String, Object>();
    		HashMap<String, String> customRegistrations = new HashMap<String, String>();
    		xmlData = new HashMap<String, Object>();
    		List<String> swappedFields = new ArrayList<String>();
    		logger.info(String.format("Got the supplier category Id = [%d]", supplierCatId));
    		String oid = customLeadService.getSalesForceLeadDeliverySettingsBySupplierCatId(supplierCatId);

    		param = customLeadService.getParam(supplierCatId, 7);
    		info = supplierCategoryService.getSupplierCategoryInfoNew(supplierCatId, 7);
    		
    		if(param == null) {
    			output.put("Internal Server Error: ", String.format("No param data related to supplierId = [%d]", supplierCatId));
    			logger.warn(String.format("No param data related to supplierId = [%d]", supplierCatId));
				return new ResponseEntity<HashMap<String, Object>>(output, HttpStatus.INTERNAL_SERVER_ERROR);  
    		}
    		
    		if(info == null) {
    			output.put("Internal Server Error: ", String.format( "No data related to supplierId = [%d]", supplierCatId));
    			logger.warn(String.format( "No data related to supplierId = [%d]", supplierCatId));
				return new ResponseEntity<HashMap<String, Object>>(output, HttpStatus.INTERNAL_SERVER_ERROR); 
    		}
    		
		    HttpManager request = new HttpManager();
		    
		    getUrl = baseURL + "/category/default/" + info.getCategoryId(); 
		    response = request.getData(getUrl);
		    if(response.contains("true"))
		    	isDefault = true;
		    logger.info(String.format("The category [%d] is custom registration [%s]", info.getCategoryId(), isDefault));
		    
		    getUrl = baseURL + "/category/customreg/" + info.getCategoryId(); 
			
			response = request.getData(getUrl);
			String[] customRegistrationFields = response.replace("\"", "").replace("[", "").replace("]", "").split(",");
			
			for(String[] strArray : swapFields) {
				for(String field : customRegistrationFields) {
					if(field.equals(strArray[0])) 
						swappedFields.add(strArray[1]);
					if(field.equals("companyIndustry"))
						swappedFields.add("company_industry");
					if(field.equals("title"))
						swappedFields.add("title_id");
					if(field.equals("companySize"))
						swappedFields.add("company_size_id");
				}
			}
			swappedFields.add("email");
			
			if(swappedFields.size() > 0)
				swappedFieldsArray = (String[]) swappedFields.toArray(new String[swappedFields.size()]);


			List<HashMap<String, String>> testData = registrationService.getTestCustomRegistration(swappedFieldsArray);
			customRegistrations = extractHashMapFromList(testData);
			if(customRegistrations != null && customRegistrations.size() > 0) {
				for(String[] strArray : swapFields) {
					if(customRegistrations.containsKey(strArray[1])) {
						swap(strArray[1], strArray[0], customRegistrations);
	
						if((customRegistrations.get(strArray[1]) == null))
							customRegistrations.remove(strArray[1]);
					}
				}
				
				List<QuestionWithAnswer> answersOptions = registrationService.getTestQuestionWithAnswer(info.getQuestionSetId());
		    	
				getUrl = baseURL + "/questionset/hidden/" + info.getCategoryId(); 
				response = request.getData(getUrl);
				answersOptions = removeHiddenQuestion(response, answersOptions);
				
			    for(QuestionWithAnswer answer : answersOptions) {
			    	if(answer.getQuestionId().equals("_emailQuestion_")) {
			    		addValue(questionWithAnswers, answer.getQuestion(), customRegistrations.get("email").trim());
			    	}
			    	else if(answer.getQuestionId().equals("_zipcodeQuestion_")) {
			    		if(customRegistrations.get("postCode") != null)
			    			addValue(questionWithAnswers, answer.getQuestion(), customRegistrations.get("postCode").trim());
			    		else
			    			addValue(questionWithAnswers, answer.getQuestion(), "12345");
			    	}
			    	else
			    		addValue(questionWithAnswers, answer.getQuestion(), answer.getAnswer().trim());
			    }
			    
				xmlData.putAll(questionWithAnswers);
				xmlData.putAll(customRegistrations);
				
				if(xmlData.containsKey("stateId"))
					xmlData.put("stateName", registrationService.getStateFullName((String)xmlData.get("stateId")));
				
				generateXmlString = generateXmlString(quoteId, info, questionWithAnswers, xmlData, true, url, oid, isDefault);
				if(generateXmlString != null) {
					if(internal) {
						if(url != null && !url.isEmpty())
							output.put(param, generateXmlString);
						else
							output = this.convertPayLoadParams(param, generateXmlString);
					}
					else {
						output.put(param, generateXmlString);
					}
					return new ResponseEntity<HashMap<String, Object>>(output, HttpStatus.OK);
				}
				else {
					generateXmlString = generateXmlString(quoteId, info, questionWithAnswers, xmlData, true, url, oid, isDefault);
					output.put("Internal Server Error: ", generateXmlString);
					logger.warn("Internal Server Error: " + generateXmlString);
					return new ResponseEntity<HashMap<String, Object>>(output, HttpStatus.INTERNAL_SERVER_ERROR); 
				}
			} 
			else {
				output.put("Internal Server Error: ", String.format( "No data related to quoteId = [%d] and supplierId = [%d]", quoteId, supplierCatId));
				logger.warn(String.format("XML http post failed. No data related to quoteId = [%d] and supplierId = [%d]", quoteId, supplierCatId));
				return new ResponseEntity<HashMap<String, Object>>(output, HttpStatus.INTERNAL_SERVER_ERROR); 
			}
		 
    	} catch (BeansException e) {
    		logger.error(MessageUtil.errorMessage(type, quoteId, null, "Internal Server Error", e.getMessage()));
			output.put("Internal Server Error", e.getMessage());
			return new ResponseEntity<HashMap<String, Object>>(output, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (IllegalStateException e) {
			logger.error(MessageUtil.errorMessage(type, quoteId, null, "Internal Server Error", e.getMessage()));
			output.put("Internal Server Error", e.getMessage());
			return new ResponseEntity<HashMap<String, Object>>(output, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch(Exception e) {
			logger.error(MessageUtil.errorMessage(type, quoteId, null, "Internal Server Error", e.getMessage()));
			output.put("Internal Server Error", e.getMessage());
			return new ResponseEntity<HashMap<String, Object>>(output, HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
    @RequestMapping("/xml/content")
    public ResponseEntity<HashMap<String, Object>> getContent(@RequestParam(value="quoteId", required=false, defaultValue="12345678") int quoteId, 
    														  @RequestParam(value="leadPreferenceId", required=false, defaultValue="12345") int leadPreferenceId, 
    														  @RequestParam(value="supplierCatId", required=false, defaultValue="12345") int supplierCatId,
    														  @RequestParam(value="url", required=false, defaultValue="") String url,
    														  @RequestParam(value="test", required=false, defaultValue="false") boolean test,
    														  @RequestParam(value="internal", required=false, defaultValue="false") boolean internal,
    														  HttpServletRequest methodRequest){
    	List<QuestionWithAnswer> answers = null;
    	HashMap<String, Object> xmlData = null;
    	String[] swappedFieldsArray = null;
    	CategoryDefinition info = null;
    	String param = null;
    	String methodName = null;
    	HashMap<String, Object> output = new HashMap<String, Object>();
    	String getUrl = null;
    	String response = null;
    	HttpManager request = new HttpManager();
    	String generateXmlString = null;
    	boolean isDefault = false;
    	String type = "XML";
    	try {
    		if(test) {
    			return this.getTestContent(quoteId, supplierCatId, url, internal, methodRequest);
    		}
    		else {
	    		methodName = CommonUtil.getMethodName(methodRequest);
	    		logger.info(String.format("Getting API call. URL = [%s]", methodName));
	    		
	    		if(url!= null && !url.isEmpty()) {
	    			url = bzBaseUrl + url.replace("/mnt/incoming_data/BZ/", "/incoming/");
	    			logger.info(String.format("Getting xslt URL = [%s]", url));
	    		}
	    		
	    		Map<String, Object> questionWithAnswers = new LinkedHashMap<String, Object>();
	    		HashMap<String, String> customRegistrations = new HashMap<String, String>();
	    		xmlData = new HashMap<String, Object>();
	    		List<HashMap<String, String>> quoteRequestParam = new ArrayList<HashMap<String,String>>();
	    		List<String> swappedFields = new ArrayList<String>();
	    		
	    		String oid = customLeadService.getSalesForceLeadDeliverySettings(leadPreferenceId);
	    		int supplierId = customLeadService.getSupplierCatId(leadPreferenceId);
	    		logger.info(String.format("Got the supplier category Id = [%d]", supplierId));
	    		
	    		param = customLeadService.getParam(supplierId, 7);
	    		info = supplierCategoryService.getSupplierCategoryInfoNew(supplierId, 7);
	    		
	    		if(info == null) {
	    			output.put("Internal Server Error: ", String.format( "No data related to supplierId = [%d]", supplierId));
	    			logger.warn(String.format( "No data related to quote request id = [%d] and supplierId = [%d]", quoteId, supplierCatId));
					return new ResponseEntity<HashMap<String, Object>>(output, HttpStatus.INTERNAL_SERVER_ERROR); 
	    		}
	    		
	    		if(param == null) {
	    			output.put("Internal Server Error: ", String.format("No param data related to supplierId = [%d]", supplierId));
	    			logger.warn(String.format("No param data related to quote request id = [%d] and supplierId = [%d]", quoteId, supplierId));
					return new ResponseEntity<HashMap<String, Object>>(output, HttpStatus.INTERNAL_SERVER_ERROR);  
	    		}
	    		
	    		logger.info(String.format("Getting lead data. QuoteId = [%d]", quoteId));
			    
			    answers = supplierCategoryService.getXmlQuestionWithAnswers(quoteId);
			    getUrl = baseURL + "/questionset/hidden/" + info.getCategoryId(); 
				response = request.getData(getUrl);
				answers = removeHiddenQuestion(response, answers);
				
			    quoteRequestParam =	supplierCategoryService.getQuoteRequestParam(quoteId);
			    
			    if(quoteRequestParam != null && quoteRequestParam.size() > 0) {
				    for(QuestionWithAnswer q : answers) {
				    	for(HashMap<String,String> hashMap: quoteRequestParam) {
				    		for(String key : hashMap.keySet()) {
				    			String value = hashMap.get(key);
				    			if(value.contains(q.getQuestionId()) && q.getAnswer().contains("Other")) {
				    				q.setAnswer(q.getAnswer() + hashMap.get("param_value"));
				    			}
				    		}
				    	}
				    }
			    }	
			    
			    for(QuestionWithAnswer answer : answers) {
			    	String encodingAnswer = this.escapeCharacter(answer.getAnswer().trim());
			    	addValue(questionWithAnswers, answer.getQuestion(), encodingAnswer);
			    }
			    
			    getUrl = baseURL + "/category/default/" + info.getCategoryId(); 
			    response = request.getData(getUrl);
			    if(response.contains("true"))
			    	isDefault = true;
			    logger.info(String.format("The category [%d] is custom registration [%s]", info.getCategoryId(), isDefault));
			    
				getUrl = baseURL + "/category/customreg/" + info.getCategoryId(); 
				response = request.getData(getUrl);
				String[] customRegistrationFields = response.replace("\"", "").replace("[", "").replace("]", "").split(",");
				
				for(String[] strArray : swapFields) {
					for(String field : customRegistrationFields) {
						if(field.equals(strArray[0])) {
							swappedFields.add(strArray[1]);
						}
					}
				}
				swappedFields.add("email");
				
				if(swappedFields.size() > 0)
					swappedFieldsArray = (String[]) swappedFields.toArray(new String[swappedFields.size()]);
				
				customRegistrations = registrationService.getCustomLead(quoteId, supplierId, swappedFieldsArray);
				for(String key : customRegistrations.keySet()) {
					if(!customRegistrations.get(key).equals("null")) {
						String escapeCharacter = this.escapeCharacter(customRegistrations.get(key));
						customRegistrations.put(key, escapeCharacter);
					}
					else 
						customRegistrations.put(key, "");
				}
				
				if(customRegistrations != null && customRegistrations.size() > 0) {
					for(String[] strArray : swapFields) {
						if(customRegistrations.containsKey(strArray[1])) {
							swap(strArray[1], strArray[0], customRegistrations);
		
							if((customRegistrations.get(strArray[1]) == null))
								customRegistrations.remove(strArray[1]);
							
						}
					}
		
					xmlData.putAll(questionWithAnswers);
					xmlData.putAll(customRegistrations);
					
					if(xmlData.containsKey("stateId"))
						xmlData.put("stateName", registrationService.getStateFullName((String)xmlData.get("stateId")));
					
					generateXmlString = generateXmlString(quoteId, info, questionWithAnswers, xmlData, false, url, oid, isDefault);
					if(generateXmlString != null) {
						if(internal) {
							if(url != null && !url.isEmpty())
								output.put(param, generateXmlString);
							else
								output = this.convertPayLoadParams(param, generateXmlString);
						}
						else {
							output.put(param, generateXmlString);
						}

						return new ResponseEntity<HashMap<String, Object>>(output, HttpStatus.OK);
					}
					else {
						generateXmlString = generateXmlString(quoteId, info, questionWithAnswers, xmlData, false, url, oid, isDefault);
						output.put("Internal Server Error: ", generateXmlString);
						logger.warn(String.format("Quote request id: [%d]. Internal Server Error: ", quoteId, generateXmlString));
						return new ResponseEntity<HashMap<String, Object>>(output, HttpStatus.INTERNAL_SERVER_ERROR); 
					}
				} 
				else {
					output.put("Internal Server Error: ", String.format( "No data related to quoteId = [%d] and supplierId = [%d]", quoteId, supplierId));
					logger.warn(String.format( "No data related to quoteId = [%d] and supplierId = [%d]", quoteId, supplierCatId));
					return new ResponseEntity<HashMap<String, Object>>(output, HttpStatus.INTERNAL_SERVER_ERROR); 
				}
    		}
    	} catch (BeansException e) {
    		logger.error(MessageUtil.errorMessage(type, quoteId, null, "Internal Server Error", e.getMessage()));
			output.put("Internal Server Error", e.getMessage());
			return new ResponseEntity<HashMap<String, Object>>(output, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (IllegalStateException e) {
			logger.error(MessageUtil.errorMessage(type, quoteId, null, "Internal Server Error", e.getMessage()));
			output.put("Internal Server Error", e.getMessage());
			return new ResponseEntity<HashMap<String, Object>>(output, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch(Exception e) {
			logger.error(MessageUtil.errorMessage(type, quoteId, null, "Internal Server Error", e.getMessage()));
			output.put("Internal Server Error", e.getMessage());
			return new ResponseEntity<HashMap<String, Object>>(output, HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
    private HashMap<String, String> extractHashMapFromList(List<HashMap<String, String>> customRegistration) {
    	HashMap<String, String> maps = new HashMap<String, String>();
    	if(customRegistration != null && customRegistration.size() >0) {
    		for(HashMap<String, String> map : customRegistration) {
    			maps.put(map.get("paramKey"), map.get("paramValue"));
    		}
    	}
    	
    	return maps;
    }
    
    private String generateXmlString(int quoteRequestId, CategoryDefinition category, Map<String, Object> questionAndAnswers, HashMap<String, Object> xmlData, boolean test, String url, String oid, boolean isDefault) {
    	String result = null;
    	
    	if(isDefault)
    		result = generateRegularLeads(quoteRequestId, category, questionAndAnswers, xmlData, test, url, oid);
    	else
    		result = generateCustomLeads(quoteRequestId, category, questionAndAnswers, xmlData, test, url, oid);
    	return result;

    }
    
    private String generateRegularLeads(int quoteRequestId, CategoryDefinition category, Map<String, Object> questionAndAnswers, HashMap<String, Object> xmlData, boolean test, String url, String oid) {
    	try{
    		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    		
    		// root elements
    		Document doc = docBuilder.newDocument();
    		Element rootElement = doc.createElement("quoteList");
    		doc.appendChild(rootElement);
    		
    		// quote elements
    		Element quote = doc.createElement("quote");
    		rootElement.appendChild(quote);
    		
    		// quoteId elements
    		Element quoteId = doc.createElement("quoteId");
    		quoteId.appendChild(doc.createTextNode(String.valueOf(quoteRequestId)));
    		quote.appendChild(quoteId);
    		
    		// categoryName elements
    		Element categoryName = doc.createElement("categoryName");
    		categoryName.appendChild(doc.createTextNode(String.valueOf(category.getDisplayName())));
    		quote.appendChild(categoryName);
    		
    		// categoryName elements
    		Element categoryId = doc.createElement("categoryId");
    		categoryId.appendChild(doc.createTextNode(String.valueOf(category.getCategoryId())));
    		quote.appendChild(categoryId);
    		
    		// categoryName elements
    		Element quoter = doc.createElement("quoter");
    		quote.appendChild(quoter);
    		
    		// categoryName elements
    		Element contactInfo = doc.createElement("contactInfo");
    		quoter.appendChild(contactInfo);
    		
    		// categoryName elements
    		Element address = doc.createElement("address");
    		contactInfo.appendChild(address);
    		
	    	Element street = doc.createElement("street");
	    	if(xmlData.get("streetName") != null && !xmlData.get("streetName").toString().isEmpty()) {
	    		street.appendChild(doc.createTextNode((String)xmlData.get("streetName")));
	    	}
	    	address.appendChild(street);
    		
	    	Element street2 = doc.createElement("street2");
	    	if(xmlData.get("streetLine2") != null && !xmlData.get("streetLine2").toString().isEmpty()) {
	    		street2.appendChild(doc.createTextNode((String)xmlData.get("streetLine2")));
	    	}
	    	address.appendChild(street2);
    		
	    	Element city = doc.createElement("city");
	    	if(xmlData.get("city") != null && !xmlData.get("city").toString().isEmpty()) {
	    		city.appendChild(doc.createTextNode((String)xmlData.get("city")));
	    	}
	    	address.appendChild(city);
    		
    		if(xmlData.get("stateName") != null && !xmlData.get("stateName").toString().isEmpty()) {
	    		Element state = doc.createElement("state");
	    		address.appendChild(state);
	    		
	    		Element stateName = doc.createElement("stateName");
	    		stateName.appendChild(doc.createTextNode((String)xmlData.get("stateName")));
	    		state.appendChild(stateName);
	    		
	    		Element stateAbbreviation = doc.createElement("stateAbbreviation");
	    		stateAbbreviation.appendChild(doc.createTextNode((String)xmlData.get("stateId")));
	    		state.appendChild(stateAbbreviation);
    		}
    		
    		if(xmlData.get("postCode") != null && !xmlData.get("postCode").toString().isEmpty()) {
	    		Element zip = doc.createElement("zip");
	    		zip.appendChild(doc.createTextNode((String)xmlData.get("postCode")));
	    		address.appendChild(zip);
    		}
    		
    		Element phone = doc.createElement("phone");
    		contactInfo.appendChild(phone);
    		
    		Element phoneFreetext = doc.createElement("phoneFreeText");
    		if(xmlData.get("phone") != null && !xmlData.get("phone").toString().isEmpty()) {
    			phoneFreetext.appendChild(doc.createTextNode((String)xmlData.get("phone")));
    		}
    		phone.appendChild(phoneFreetext);
    		
    		Element numericPhone = doc.createElement("numericPhone");
    		if(xmlData.get("phone") != null && !xmlData.get("phone").toString().isEmpty()) {
	    		String phoneNumber = (String)xmlData.get("phone");
	    		if(phoneNumber != null)
	    			phoneNumber = phoneNumber.replace("-", "");
	    		numericPhone.appendChild(doc.createTextNode(phoneNumber));
    		}
    		phone.appendChild(numericPhone);
    		
    		if(xmlData.get("email") != null && !xmlData.get("email").toString().isEmpty()) {
    			Element email = doc.createElement("email");
    			contactInfo.appendChild(email);
    			Element emailAddress = doc.createElement("emailAddress");
    			emailAddress.appendChild(doc.createTextNode(xmlData.get("email").toString()));
    			email.appendChild(emailAddress);
    		}
    		
    		Element personalInfo = doc.createElement("personalInfo");
    		quoter.appendChild(personalInfo);
    		
	    	Element firstName = doc.createElement("firstName");
	    	if(xmlData.get("firstName") != null && !xmlData.get("firstName").toString().isEmpty()) {
	    		firstName.appendChild(doc.createTextNode((String)xmlData.get("firstName")));
    		}
	    	personalInfo.appendChild(firstName);
    		
    		
	    	Element lastName = doc.createElement("lastName");
	    	if(xmlData.get("lastName") != null && !xmlData.get("lastName").toString().isEmpty()) {
	    		lastName.appendChild(doc.createTextNode((String)xmlData.get("lastName")));
    		}
	    	personalInfo.appendChild(lastName);
    		
    		
	    	Element fullName = doc.createElement("fullName");
	    	if(xmlData.get("firstName") != null || xmlData.get("lastName") != null) {
	    		if((String)xmlData.get("firstName") != null && (String)xmlData.get("lastName") == null)
	    			fullName.appendChild(doc.createTextNode((String)xmlData.get("firstName")));
	    		else if((String)xmlData.get("firstName") == null && (String)xmlData.get("lastName") != null)
	    			fullName.appendChild(doc.createTextNode((String)xmlData.get("lastName")));
	    		else if((String)xmlData.get("firstName") != null && (String)xmlData.get("lastName") != null)
	    			fullName.appendChild(doc.createTextNode((String)xmlData.get("firstName") + " " + (String)xmlData.get("lastName")));
	    	}	
	    	personalInfo.appendChild(fullName);
    		
    		Element businessInfo = doc.createElement("businessInfo");
    		quoter.appendChild(businessInfo);
    		
	    	Element companyName = doc.createElement("companyName");
	    	if(xmlData.get("companyName") != null && !xmlData.get("companyName").toString().isEmpty()) {
	    		companyName.appendChild(doc.createTextNode((String)xmlData.get("companyName")));
	    	}
	    	businessInfo.appendChild(companyName);
    		
    		if(test) {
		    	Element title = doc.createElement("title");
		    	if(xmlData.get("title_id") != null && !xmlData.get("title_id").toString().isEmpty()) {
		    		title.appendChild(doc.createTextNode((String)xmlData.get("title_id")));
		    	}
		    	businessInfo.appendChild(title);
    		}
    		else {
		    	Element title = doc.createElement("title");
		    	if(xmlData.get("title") != null && !xmlData.get("title").toString().isEmpty()) {
		    		title.appendChild(doc.createTextNode((String)xmlData.get("title")));
		    	}
		    	businessInfo.appendChild(title);
    		}
    		
    		if(test) {
		    	Element industry = doc.createElement("industry");
		    	if(xmlData.get("company_industry") != null && !xmlData.get("company_industry").toString().isEmpty()) {
		    		industry.appendChild(doc.createTextNode((String)xmlData.get("company_industry")));
		    	}
		    	businessInfo.appendChild(industry);
    		}
    		else {
		    	Element industry = doc.createElement("industry");
		    	if(xmlData.get("companyIndustry") != null && !xmlData.get("companyIndustry").toString().isEmpty()) {
		    		industry.appendChild(doc.createTextNode((String)xmlData.get("companyIndustry")));
		    	}
		    	businessInfo.appendChild(industry);
    		}
    		
    		if(test) {
    			
		    	Element companySize = doc.createElement("companySize");
		    	if(xmlData.get("company_size_id") != null && !xmlData.get("company_size_id").toString().isEmpty()) {
		    		companySize.appendChild(doc.createTextNode((String)xmlData.get("company_size_id")));
		    	}
		    	businessInfo.appendChild(companySize);
    		}
    		else {
	    		
		    	Element companySize = doc.createElement("companySize");
		    	if(xmlData.get("companySize") != null && !xmlData.get("companySize").toString().isEmpty()) {
		    		companySize.appendChild(doc.createTextNode((String)xmlData.get("companySize")));
		    	}
		    	businessInfo.appendChild(companySize);
    		}
    		
    		if(questionAndAnswers.size() > 0) {
	    		Element requestDetails = doc.createElement("requestDetails");
	    		quote.appendChild(requestDetails);
	    		
	    		for(String key : questionAndAnswers.keySet()) {
	    			Element questionWithAnswers = doc.createElement("questionWithAnswers");
	    			requestDetails.appendChild(questionWithAnswers);
	        		
	        		Element question = doc.createElement("question");
	        		question.appendChild(doc.createTextNode(key));
	        		questionWithAnswers.appendChild(question);
	        		
	        		Element answer = doc.createElement("answer");
	        		if(questionAndAnswers.get(key).toString() != null) 
	        			answer.appendChild(doc.createTextNode(questionAndAnswers.get(key).toString().replace("[", "").replace("]", "")));
	        		questionWithAnswers.appendChild(answer);
	        		
	        		Element priorityLevel = doc.createElement("priorityLevel");
	        		priorityLevel.appendChild(doc.createTextNode(String.valueOf(0)));
	        		questionWithAnswers.appendChild(priorityLevel);
	    		}
    		}
    		
    		StringBuilder sb = new StringBuilder();
    		if(url != null && !url.isEmpty()) {
    			sb.append(applyStyleSheet(url, getStringFromDoc(doc).replace("<?xml version=\"1.0\" encoding=\"UTF-16\"?>", "").trim()));
    			if(sb.toString().substring(sb.toString().length() - 1).equals("&"))
    				sb.append("oid=" + oid);
    			else
    				sb.append("&oid=" + oid);
    			logger.info(String.format("Final original xml send to Salesforce: [%s]", sb.toString()));
    		}
    		else
    			sb.append(getStringFromDoc(doc).replace("<?xml version=\"1.0\" encoding=\"UTF-16\"?>", "").trim());
    		
    		return sb.toString().replace("&amp;", "&");
    		
    	}
    	catch(Exception e) {
    		logger.info(String.format("GenerateRegularLead method Exception: %s", e));
    		return e.getMessage();
    	}
    }
    
    private String generateCustomLeads(int quoteRequestId, CategoryDefinition category, Map<String, Object> questionAndAnswers, HashMap<String, Object> xmlData, boolean test, String url, String oid) {
    	try{
    		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    		
    		// root elements
    		Document doc = docBuilder.newDocument();
    		Element rootElement = doc.createElement("quoteList");
    		doc.appendChild(rootElement);
    		
    		// quote elements
    		Element quote = doc.createElement("quote");
    		rootElement.appendChild(quote);
    		
    		// quoteId elements
    		Element quoteId = doc.createElement("quoteId");
    		quoteId.appendChild(doc.createTextNode(String.valueOf(quoteRequestId)));
    		quote.appendChild(quoteId);
    		
    		// categoryName elements
    		Element categoryName = doc.createElement("categoryName");
    		categoryName.appendChild(doc.createTextNode(String.valueOf(category.getDisplayName())));
    		quote.appendChild(categoryName);
    		
    		// categoryName elements
    		Element categoryId = doc.createElement("categoryId");
    		categoryId.appendChild(doc.createTextNode(String.valueOf(category.getCategoryId())));
    		quote.appendChild(categoryId);
    		
    		// categoryName elements
    		Element quoter = doc.createElement("quoter");
    		quote.appendChild(quoter);
    		
    		// categoryName elements
    		Element contactInfo = doc.createElement("contactInfo");
    		quoter.appendChild(contactInfo);
    		
    		// categoryName elements
    		Element address = doc.createElement("address");
    		contactInfo.appendChild(address);
    		
    		if(xmlData.get("streetName") != null) {
    			Element street = doc.createElement("street");
	    		street.appendChild(doc.createTextNode((String)xmlData.get("streetName")));
	    		address.appendChild(street);
	    	}
    		
    		if(xmlData.get("city") != null) {
    			Element city = doc.createElement("city");
	    		city.appendChild(doc.createTextNode((String)xmlData.get("city")));
	    		address.appendChild(city);
	    	}
    		
    		if(xmlData.get("stateName") != null) {
	    		Element state = doc.createElement("state");
	    		address.appendChild(state);
	    		
	    		Element stateName = doc.createElement("stateName");
	    		stateName.appendChild(doc.createTextNode((String)xmlData.get("stateName")));
	    		state.appendChild(stateName);
	    		
	    		Element stateAbbreviation = doc.createElement("stateAbbreviation");
	    		stateAbbreviation.appendChild(doc.createTextNode((String)xmlData.get("stateId")));
	    		state.appendChild(stateAbbreviation);
    		}
    		
    		if(xmlData.get("postCode") != null) {
	    		Element zip = doc.createElement("zip");
	    		zip.appendChild(doc.createTextNode((String)xmlData.get("postCode")));
	    		address.appendChild(zip);
    		}
    		
    		if(xmlData.get("phone") != null) {
	    		Element phone = doc.createElement("phone");
	    		contactInfo.appendChild(phone);
	    		
	    		Element phoneFreetext = doc.createElement("phoneFreeText");
	    		phoneFreetext.appendChild(doc.createTextNode((String)xmlData.get("phone")));
	    		phone.appendChild(phoneFreetext);
	    		
	    		Element numericPhone = doc.createElement("numericPhone");
	    		String phoneNumber = (String)xmlData.get("phone");
	    		if(phoneNumber != null)
	    			phoneNumber = phoneNumber.replace("-", "");
	    		numericPhone.appendChild(doc.createTextNode(phoneNumber));
	    		phone.appendChild(numericPhone);
    		}
    		
    		if(xmlData.get("email") != null) {
    			Element email = doc.createElement("email");
    			contactInfo.appendChild(email);
    			Element emailAddress = doc.createElement("emailAddress");
    			emailAddress.appendChild(doc.createTextNode(xmlData.get("email").toString()));
    			email.appendChild(emailAddress);
    		}
    		
    		Element personalInfo = doc.createElement("personalInfo");
    		quoter.appendChild(personalInfo);
    		
    		if(xmlData.get("firstName") != null) {
    			Element firstName = doc.createElement("firstName");
	    		firstName.appendChild(doc.createTextNode((String)xmlData.get("firstName")));
	    		personalInfo.appendChild(firstName);
    		}
    		
    		if(xmlData.get("lastName") != null) {
    			Element lastName = doc.createElement("lastName");
	    		lastName.appendChild(doc.createTextNode((String)xmlData.get("lastName")));
	    		personalInfo.appendChild(lastName);
    		}
    		
	    	if(xmlData.get("firstName") != null || xmlData.get("lastName") != null) {
	    		Element fullName = doc.createElement("fullName");
	    		if((String)xmlData.get("firstName") != null && (String)xmlData.get("lastName") == null)
	    			fullName.appendChild(doc.createTextNode((String)xmlData.get("firstName")));
	    		else if((String)xmlData.get("firstName") == null && (String)xmlData.get("lastName") != null)
	    			fullName.appendChild(doc.createTextNode((String)xmlData.get("lastName")));
	    		else if((String)xmlData.get("firstName") != null && (String)xmlData.get("lastName") != null)
	    			fullName.appendChild(doc.createTextNode((String)xmlData.get("firstName") + " " + (String)xmlData.get("lastName")));
	    		personalInfo.appendChild(fullName);
	    	}	
    		
    		Element businessInfo = doc.createElement("businessInfo");
    		quoter.appendChild(businessInfo);
    		
    		if(xmlData.get("companyName") != null) {
    			Element companyName = doc.createElement("companyName");
	    		companyName.appendChild(doc.createTextNode((String)xmlData.get("companyName")));
	    		businessInfo.appendChild(companyName);
	    	}
	    	
    		
    		if(test) {
    			if(xmlData.get("title_id") != null) {
    				Element title = doc.createElement("title");
		    		title.appendChild(doc.createTextNode((String)xmlData.get("title_id")));
		    		businessInfo.appendChild(title);
		    	}
    		}
    		else {
    			if(xmlData.get("title") != null) {
    				Element title = doc.createElement("title");
		    		title.appendChild(doc.createTextNode((String)xmlData.get("title")));
		    		businessInfo.appendChild(title);
		    	}
		    	
    		}
    		
    		if(test) {
		    	if(xmlData.get("company_industry") != null) {
		    		Element industry = doc.createElement("industry");
		    		industry.appendChild(doc.createTextNode((String)xmlData.get("company_industry")));
		    		businessInfo.appendChild(industry);
		    	}
    		}
    		else {
		    	if(xmlData.get("companyIndustry") != null) {
		    		Element industry = doc.createElement("industry");
		    		industry.appendChild(doc.createTextNode((String)xmlData.get("companyIndustry")));
		    		businessInfo.appendChild(industry);
		    	}
    		}
    		
    		if(test) {
    			if(xmlData.get("company_size_id") != null) {
    				Element companySize = doc.createElement("companySize");
		    		companySize.appendChild(doc.createTextNode((String)xmlData.get("company_size_id")));
		    		businessInfo.appendChild(companySize);
		    	}
    		}
    		else {
	    		if(xmlData.get("companySize") != null) {
	    			Element companySize = doc.createElement("companySize");
		    		companySize.appendChild(doc.createTextNode((String)xmlData.get("companySize")));
		    		businessInfo.appendChild(companySize);
		    	}
    		}
    		
    		if(questionAndAnswers.size() > 0) {
	    		Element requestDetails = doc.createElement("requestDetails");
	    		quote.appendChild(requestDetails);
	    		
	    		for(String key : questionAndAnswers.keySet()) {
	    			Element questionWithAnswers = doc.createElement("questionWithAnswers");
	    			requestDetails.appendChild(questionWithAnswers);
	        		
	        		Element question = doc.createElement("question");
	        		question.appendChild(doc.createTextNode(key));
	        		questionWithAnswers.appendChild(question);
	        		
	        		Element answer = doc.createElement("answer");
	        		if(questionAndAnswers.get(key).toString() != null) 
	        			answer.appendChild(doc.createTextNode(questionAndAnswers.get(key).toString().replace("[", "").replace("]", "")));
	        		questionWithAnswers.appendChild(answer);
	        		
	        		Element priorityLevel = doc.createElement("priorityLevel");
	        		priorityLevel.appendChild(doc.createTextNode(String.valueOf(0)));
	        		questionWithAnswers.appendChild(priorityLevel);
	    		}
    		}
    		
    		StringBuilder sb = new StringBuilder();
    		if(url != null && !url.isEmpty()) {
    			sb.append(applyStyleSheet(url, getStringFromDoc(doc).replace("<?xml version=\"1.0\" encoding=\"UTF-16\"?>", "").trim()));
    			if(sb.toString().substring(sb.toString().length() - 1).equals("&"))
    				sb.append("oid=" + oid);
    			else
    				sb.append("&oid=" + oid);
    			logger.info(String.format("Final original xml send to Salesforce: [%s]", sb.toString()));
    		}
    		else
    			sb.append(getStringFromDoc(doc).replace("<?xml version=\"1.0\" encoding=\"UTF-16\"?>", "").trim());
    		
    		return sb.toString().replace("&amp;", "&");
    		
    	}
    	catch(Exception e) {
    		logger.info(String.format("GenerateCustomLeads method Exception: %s", e));
    		return e.getMessage();
    	}
    }
    
    private String applyStyleSheet(String url, String xmlContent) {
    	try {
	        URL urlFile = new URL(url);
	        InputStream is = urlFile.openStream();
	        Source xmlSource = new StreamSource(IOUtils.toInputStream(xmlContent));
	        Source urlSource = new StreamSource(is);
	        TransformerFactory transFact = TransformerFactory.newInstance();
	        Transformer trans = transFact.newTransformer(urlSource);
	        StringWriter stringWriter = new StringWriter();
	        StreamResult result = new StreamResult(stringWriter);
	        logger.info("\n\nXML before transformation: \n" + xmlContent);
	        trans.transform(xmlSource, result);
	        logger.info("\n\nXML after transformation: \n" + stringWriter.toString());
	        return stringWriter.toString();
    	}
    	catch (Exception ex) {
    		
    		logger.info(String.format("XSLT File Exception: %s", ex));
    		ex.printStackTrace();
    		return null;
    	}
    }
    
    private String getStringFromDoc(Document doc)    {
        DOMImplementationLS domImplementation = (DOMImplementationLS) doc.getImplementation();
        LSSerializer lsSerializer = domImplementation.createLSSerializer();
        return lsSerializer.writeToString(doc);   
    }
  
    
    /***
     * Added multiple value int a map with same key.
     * @param map
     * @param key
     * @param value
     */
    private void addValue(Map map, Object key, Object value) {
        Object obj = map.get(key);
        List list;
        if (obj == null) {  
            list = new ArrayList<Object>();  
        } else {
            list = ((ArrayList) obj);
        }
        list.add(value);
        map.put(key, list);
    }
   
    private List<QuestionWithAnswer> removeHiddenQuestion(String response, List<QuestionWithAnswer> questionWithAnswers) {
		List<QuestionWithAnswer> hiddenQuestions = new ArrayList<QuestionWithAnswer>();
    	if(response != null) {
			String[] hiddenQuestion = response.replace("[", "").replace("]", "").split(",");
			for(String question : hiddenQuestion) {
				for(QuestionWithAnswer answer : questionWithAnswers) {
					//System.out.println(answer.getQuestionId());
					if(question.replace("\"", "").equals(answer.getQuestionId())) {
						hiddenQuestions.add(answer);
					}
				}
			}
			questionWithAnswers.removeAll(hiddenQuestions);
			return questionWithAnswers;
		}
		else 
			return questionWithAnswers;
    }
    
    private void swap(Object key1, Object key2, Map map) {
    	 Object temp = map.get(key1);
	     map.put(key1, map.get(key2));
	     map.put(key2, temp);
    }
    
    private String escapeCharacter(String inputString) {
    	return inputString.replace("&", "and").replace("\"", "&quot;").replace("'", "&apos;");
    }
    
    private String escapeBack(String inputString) {
    	return inputString.replace("&quot;", "\"").replace("&apos;", "'");
    }
    
    private Map<String, String> extractParamValues(String params) {
		if (logger.isDebugEnabled()) {
			logger.debug("Extracting parameters from: " + params);
		}
		Map<String, String> paramMap = new HashMap<String, String>();
		String[] paramsArray = params.split("&");
		if (paramsArray != null && paramsArray.length > 0) {
			for (String s : paramsArray) {
				if (StringUtils.hasText(s) && s.contains("=")) {
					paramMap.put(s.substring(0, s.indexOf("=")), s.substring(s.indexOf("=") + 1));
				}
			}
		}
		if (paramMap.isEmpty()) {
			return null;
		}
		return paramMap;
	}
    
    private HashMap<String, Object> convertPayLoadParams(String param, String xmlContent) {
    	HashMap<String, Object> params = new HashMap<String, Object>();
		if (param.indexOf("&") != -1) {
			String[] extraParams =param.split( "&" );
			for ( int i = 0; i < extraParams.length-1; i++ ) {
				if (extraParams[i].indexOf("=") != -1) {
					String[] vals = extraParams[i].split("=");
					params.put(vals[0], vals[1]);
					logger.info("--> Adding " + vals[0] + ": " + vals[1]);
				} else {
					params.put(extraParams[i], "");
					logger.info("--> Adding " + extraParams[i] + ": " + "");
				}
			}
			params.put(extraParams[extraParams.length-1], xmlContent);
			logger.info("--> Adding " + extraParams[extraParams.length-1] + ": " + xmlContent);
		} else {
			params.put(param, xmlContent);
			logger.info("--> Adding " + param +  ": " + xmlContent);
		}
		
		return params;
    }
}
