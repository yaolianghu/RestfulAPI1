package com.buyerzone.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class GenericLeadController {
	Logger logger = Logger.getLogger(GenericLeadController.class);

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
    
    @RequestMapping(value = "/generic/send", method=RequestMethod.POST)
    public ResponseEntity<HashMap<String, Object>> sendLead(@RequestParam(value="quoteId", required=false, defaultValue="12345678") int quoteId, 
    														@RequestParam(value="leadPreferenceId", required=false, defaultValue="12345") int leadPreferenceId,
    														@RequestParam(value="supplierCatId", required=false, defaultValue="12345") int supplierCatId,
    														@RequestParam(value="xslt", required=false, defaultValue="") String xslt,
    														@RequestParam(value="test", required=false, defaultValue="false") boolean test,
    														HttpServletRequest methodRequest){
    	String methodName = null;
    	HashMap<String, Object> responses = new HashMap<String, Object>();
    	ResponseEntity<HashMap<String, String>> content = null;
    	LeadPreference deliveryConfig = null;
    	int supplierId = 0;
    	String type = "Generic Http";
    	try {
    		methodName = CommonUtil.getMethodName(methodRequest);
    		logger.info(String.format("Getting API call. URL = [%s]", methodName));
    		if(test) 
    			content = this.getTestContent(quoteId, supplierCatId, xslt, methodRequest);
    		else
    			content = this.getContent(quoteId, leadPreferenceId, supplierCatId, xslt, false, methodRequest);
    			
    		if(content != null && content.getStatusCode().is2xxSuccessful()) {
	    		
	    		HttpManager request = new HttpManager();
	    		if(test)
	    			supplierId = supplierCatId;
	    		else
	    			supplierId = customLeadService.getSupplierCatId(leadPreferenceId);
	    		deliveryConfig = registrationService.getXmlLeadPreferences(supplierId, 2221);
	    		if(deliveryConfig != null) {
	    			String url = deliveryConfig.getTargetUrl() != null ? deliveryConfig.getTargetUrl() : deliveryConfig.getTestUrl();
	    				
		    		if(url != null) {
		    			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		    			
		    			for(String key : content.getBody().keySet()) {
			    			urlParameters.add(new BasicNameValuePair(key, content.getBody().get(key)));
			    		}
		    			
		    			StringBuilder paramsLog = new StringBuilder();
		    			for(NameValuePair nvp : urlParameters) {
		    				paramsLog.append(nvp.getName() + "=" + nvp.getValue() + "&");
		    			}
		    			if (paramsLog.length() > 0) {
	    					logger.debug(paramsLog.toString());
	    				} else {
	    					logger.debug("No parameters found!");
	    				}
		    			
		    			Response response = request.doPost(url, urlParameters);
		    			
			    		if(response.getResponseCode() == 200) {
			    			responses.put("response", response);
			    			if(logger.isDebugEnabled())
			    				logger.debug(MessageUtil.successMessage(type, quoteId, response.getResponseCode(), url, response.getResponseMessage(), paramsLog.toString()));
			    			else
			    				logger.info(MessageUtil.successMessage(type, quoteId, response.getResponseCode(), url, null, paramsLog.toString()));
			    			return new ResponseEntity<HashMap<String, Object>>(responses, org.springframework.http.HttpStatus.OK);
			    		}
			    		else {
			    			responses.put("response", response);
			    			if(logger.isDebugEnabled())
			    				logger.debug(MessageUtil.failedMessage(type, quoteId, response.getResponseCode(), url, response.getResponseMessage(), paramsLog.toString()));
			    			else
			    				logger.error(MessageUtil.failedMessage(type, quoteId, response.getResponseCode(), url, null, paramsLog.toString()));
			    			return new ResponseEntity<HashMap<String, Object>>(responses, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
			    		}
		    		}
		    		else {
		    			responses.put("response", String.format("No post url exists: [%s]", url));
		    			logger.error(MessageUtil.errorMessage(type, quoteId, url, "No post url exists.", "No post url exists."));
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
    
    @RequestMapping("/generic/content/test")
    public ResponseEntity<HashMap<String, String>> getTestContent(@RequestParam(value="quoteId", required=false, defaultValue="12345678") int quoteId, 
			  @RequestParam("supplierCatId") int supplierCatId, 
			  @RequestParam(value="xslt", required=false, defaultValue="") String xslt,
			  HttpServletRequest methodRequest){
    	HashMap<String, Object> xmlData = null;
    	String[] swappedFieldsArray = null;
    	String methodName = null;
    	HashMap<String, String> output = new HashMap<String, String>();
    	boolean isDefault = false;
    	CategoryDefinition info = null;
    	String type = "Generic Http";
    	try {
    		methodName = CommonUtil.getMethodName(methodRequest);
    		logger.info(String.format("Getting API call. URL = [%s]", methodName));
    		HashMap<String, Object> questionWithAnswers = new HashMap<String, Object>();
    		HashMap<String, String> customRegistrations = new HashMap<String, String>();
    		xmlData = new HashMap<String, Object>();
    		List<String> swappedFields = new ArrayList<String>();
    		logger.info(String.format("Got the supplier category Id = [%d]", supplierCatId));
    		
    		info = supplierCategoryService.getSupplierCategoryInfoNew(supplierCatId, 2221);
    		
    		if(info == null) {
    			output.put("Internal Server Error: ", String.format( "No data related to supplierId = [%d]", supplierCatId));
    			logger.warn(String.format( "No data related to supplierId = [%d]", supplierCatId));
				return new ResponseEntity<HashMap<String, String>>(output, HttpStatus.INTERNAL_SERVER_ERROR); 
    		}
		    HttpManager request = new HttpManager();
			String url = baseURL + "/category/customreg/" + info.getCategoryId(); 
			
			String response = request.getData(url);
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
			
			url = baseURL + "/category/default/" + info.getCategoryId(); 
		    response = request.getData(url);
		    if(response.contains("true"))
		    	isDefault = true;
		    logger.info(String.format("The category [%d] is custom registration [%s]", info.getCategoryId(), isDefault));

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
		    	
				url = baseURL + "/questionset/hidden/" + info.getCategoryId(); 
				response = request.getData(url);
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
				
				if(generatePostString(quoteId, info, questionWithAnswers, xmlData, true, isDefault) != null) {
					output = generatePostString(quoteId, info, questionWithAnswers, xmlData, true, isDefault);
					logger.info(String.format("Generic Http Post Content: [%s]", output));
					return new ResponseEntity<HashMap<String, String>>(output, HttpStatus.OK);
				}
				else {
					output.put("Internal Server Error: ", "No data");
					logger.warn(String.format("Generic Http Post Content: [%s]", output));
					return new ResponseEntity<HashMap<String,String>>(output, HttpStatus.INTERNAL_SERVER_ERROR); 
				}
			} 
			else {
				output.put("Internal Server Error: ", String.format( "No data related to quoteId = [%d] and supplierId = [%d]", quoteId, supplierCatId));
				logger.warn(String.format( "No data related to quoteId = [%d] and supplierId = [%d]", quoteId, supplierCatId));
				return new ResponseEntity<HashMap<String, String>>(output, HttpStatus.INTERNAL_SERVER_ERROR); 
			}
		 
    	} catch (BeansException e) {
    		logger.error(MessageUtil.errorMessage(type, quoteId, null, "Internal Server Error", e.getMessage()));
			output.put("Internal Server Error", e.getMessage());
			return new ResponseEntity<HashMap<String, String>>(output, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (IllegalStateException e) {
			logger.error(MessageUtil.errorMessage(type, quoteId, null, "Internal Server Error", e.getMessage()));
			output.put("Internal Server Error", e.getMessage());
			return new ResponseEntity<HashMap<String, String>>(output, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch(Exception e) {
			logger.error(MessageUtil.errorMessage(type, quoteId, null, "Internal Server Error", e.getMessage()));
			output.put("Internal Server Error", e.getMessage());
			return new ResponseEntity<HashMap<String, String>>(output, HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
    @RequestMapping(value="/generic/content")
    public ResponseEntity<HashMap<String, String>> getContent(@RequestParam(value="quoteId", required=false, defaultValue="12345678") int quoteId, 
    														  @RequestParam(value="leadPreferenceId", required=false, defaultValue="12345") int leadPreferenceId, 
    														  @RequestParam(value="supplierCatId", required=false, defaultValue="12345") int supplierCatId,
    														  @RequestParam(value="xslt", required=false, defaultValue="") String xslt,
    														  @RequestParam(value="test", required=false, defaultValue="false") boolean test,
    														  HttpServletRequest methodRequest){
    	List<QuestionWithAnswer> answers = null;
    	HashMap<String, Object> xmlData = null;
    	String[] swappedFieldsArray = null;
    	CategoryDefinition info = null;
    	String methodName = null;
    	HashMap<String, String> output = new HashMap<String, String>();
    	String url = null;
    	String response = null;
    	HttpManager request = new HttpManager();
    	boolean isDefault = false;
    	String type = "Generic Http";
    	try {
    		if(test) {
    			return this.getTestContent(quoteId, supplierCatId, xslt, methodRequest);
    		}
    		else {
	    		methodName = CommonUtil.getMethodName(methodRequest);
	    		logger.info(String.format("Getting API call. URL = [%s]", methodName));
	    		HashMap<String, Object> questionWithAnswers = new HashMap<String, Object>();
	    		HashMap<String, String> customRegistrations = new HashMap<String, String>();
	    		xmlData = new HashMap<String, Object>();
	    		List<HashMap<String, String>> quoteRequestParam = new ArrayList<HashMap<String,String>>();
	    		List<String> swappedFields = new ArrayList<String>();
	    		
	    		int supplierId = customLeadService.getSupplierCatId(leadPreferenceId);
	    		logger.info(String.format("Got the supplier category Id = [%d]", supplierId));
	    		info = supplierCategoryService.getSupplierCategoryInfoNew(supplierId, 2221);
	    		if(info == null) {
	    			output.put("Internal Server Error: ", String.format( "No data related to quoteId = [%d] and supplierId = [%d]", quoteId, supplierId));
	    			logger.warn(String.format( "No data related to supplierId = [%d]", supplierCatId));
	    			return new ResponseEntity<HashMap<String, String>>(output, HttpStatus.INTERNAL_SERVER_ERROR); 
	    		}
	    		
	    		logger.info(String.format("Getting lead data. QuoteId = [%d]", quoteId));
			    
			    answers = supplierCategoryService.getXmlQuestionWithAnswers(quoteId);
			    url = baseURL + "/questionset/hidden/" + info.getCategoryId(); 
				response = request.getData(url);
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
			    	addValue(questionWithAnswers, answer.getQuestion(), answer.getAnswer().trim());
			    }
			    
			    url = baseURL + "/category/default/" + info.getCategoryId(); 
			    response = request.getData(url);
			    if(response.contains("true"))
			    	isDefault = true;
			    logger.info(String.format("The category [%d] is custom registration [%s]", info.getCategoryId(), isDefault));
			    
				url = baseURL + "/category/customreg/" + info.getCategoryId(); 
				response = request.getData(url);
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
					//String escapeCharacter = this.escapeCharacter(customRegistrations.get(key));
					//customRegistrations.put(key, customRegistrations.get(key));
					
					if(!customRegistrations.get(key).equals("null")) 
						customRegistrations.put(key, customRegistrations.get(key));
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
					
					
					if(generatePostString(quoteId, info, questionWithAnswers, xmlData, false, isDefault) != null) {
						output = generatePostString(quoteId, info, questionWithAnswers, xmlData, false, isDefault);
						logger.info(String.format("Generic Http Post Content: [%s]", output));
						return new ResponseEntity<HashMap<String, String>>(output, HttpStatus.OK);
					}
					else {
						output.put("Internal Server Error: ", "No data");
						logger.warn(String.format("Generic Http Post Content: [%s]", output));
						return new ResponseEntity<HashMap<String, String>>(output, HttpStatus.INTERNAL_SERVER_ERROR); 
					}
				} 
				else {
					output.put("Internal Server Error: ", String.format( "No data related to quoteId = [%d] and supplierId = [%d]", quoteId, supplierId));
					logger.warn(String.format( "No data related to quoteId = [%d] and supplierId = [%d]", quoteId, supplierCatId));
					return new ResponseEntity<HashMap<String, String>>(output, HttpStatus.INTERNAL_SERVER_ERROR); 
				}
    		}
    	} catch (BeansException e) {
    		logger.error(MessageUtil.errorMessage(type, quoteId, null, "Internal Server Error", e.getMessage()));
			output.put("Internal Server Error", e.getMessage());
			return new ResponseEntity<HashMap<String, String>>(output, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (IllegalStateException e) {
			logger.error(MessageUtil.errorMessage(type, quoteId, null, "Internal Server Error", e.getMessage()));
			output.put("Internal Server Error", e.getMessage());
			return new ResponseEntity<HashMap<String, String>>(output, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch(Exception e) {
			logger.error(MessageUtil.errorMessage(type, quoteId, null, "Internal Server Error", e.getMessage()));
			output.put("Internal Server Error", e.getMessage());
			return new ResponseEntity<HashMap<String, String>>(output, HttpStatus.INTERNAL_SERVER_ERROR);
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
    
    private HashMap<String, String> generatePostString(int quoteRequestId, CategoryDefinition category, HashMap<String, Object> questionAndAnswers, HashMap<String, Object> xmlData, boolean test, boolean isDefault) {
    	HashMap<String, String> result = new HashMap<String, String>();
    	if(isDefault)
    		result = this.generateRegularLead(quoteRequestId, category, questionAndAnswers, xmlData, test);
    	else
    		result = this.generateCustomLead(quoteRequestId, category, questionAndAnswers, xmlData, test);
    	
    	return result;
    }
    
    private HashMap<String, String> generateRegularLead(int quoteRequestId, CategoryDefinition category, HashMap<String, Object> questionAndAnswers, HashMap<String, Object> xmlData, boolean test) {
    	HashMap<String, String> genericContent = new HashMap<String, String>();
    	logger.info(String.format("Generate Regular Lead: [%d]", quoteRequestId));
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
    		
    		genericContent.put("quote_number", String.valueOf(quoteRequestId));
    		
    		// categoryName elements
    		Element categoryName = doc.createElement("categoryName");
    		categoryName.appendChild(doc.createTextNode(String.valueOf(category.getDisplayName())));
    		quote.appendChild(categoryName);
    		
    		genericContent.put("category", String.valueOf(category.getDisplayName()));
    		
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
    		
    		// categoryName elements
    		
    		Element street = doc.createElement("street");
    		if(xmlData.get("streetName") != null && !xmlData.get("streetName").toString().isEmpty()) {
    			street.appendChild(doc.createTextNode(this.escapeCharacter((String)xmlData.get("streetName"))));
    		}
	    	address.appendChild(street);
	    	
	    	if(xmlData.get("streetName") != null && !xmlData.get("streetName").toString().isEmpty()) 
	    		genericContent.put("address", (String)xmlData.get("streetName"));
	    	else
	    		genericContent.put("address", "");
    		
    		
    		Element street2 = doc.createElement("street2");
    		if(xmlData.get("streetLine2") != null && !xmlData.get("streetLine2").toString().isEmpty()) {
	    		street2.appendChild(doc.createTextNode(this.escapeCharacter((String)xmlData.get("streetLine2"))));
    		}
    		address.appendChild(street2);
    		
    		if(xmlData.get("streetLine2") != null && !xmlData.get("streetLine2").toString().isEmpty()) 
    			genericContent.put("address2", (String)xmlData.get("streetLine2"));
    		else
    			genericContent.put("address2", "");
    		
    		
    		if(xmlData.get("city") != null) {
	    		Element city = doc.createElement("city");
	    		city.appendChild(doc.createTextNode(this.escapeCharacter((String)xmlData.get("city"))));
	    		address.appendChild(city);
	    		
	    		genericContent.put("city", (String)xmlData.get("city"));
    		}
    		
    		if(xmlData.get("stateName") != null) {
	    		Element state = doc.createElement("state");
	    		address.appendChild(state);
	    		
	    		Element stateName = doc.createElement("stateName");
	    		stateName.appendChild(doc.createTextNode(this.escapeCharacter((String)xmlData.get("stateName"))));
	    		state.appendChild(stateName);
	    		
	    		Element stateAbbreviation = doc.createElement("stateAbbreviation");
	    		stateAbbreviation.appendChild(doc.createTextNode(this.escapeCharacter((String)xmlData.get("stateId"))));
	    		state.appendChild(stateAbbreviation);
	    		
	    		genericContent.put("state", (String)xmlData.get("stateId"));
    		}
    		
    		if(xmlData.get("postCode") != null) {
	    		Element zip = doc.createElement("zip");
	    		zip.appendChild(doc.createTextNode((String)xmlData.get("postCode")));
	    		address.appendChild(zip);
	    		
	    		genericContent.put("zip", (String)xmlData.get("postCode"));
    		}
    		
    		Element phone = doc.createElement("phone");
    		contactInfo.appendChild(phone);
    		
    		if(xmlData.get("phone") != null && !xmlData.get("phone").toString().isEmpty()) 
    			genericContent.put("phone", (String)xmlData.get("phone"));
    		
    		else
    			genericContent.put("phone", "");
    		
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
    		
    		if(xmlData.get("email") != null) {
    			Element email = doc.createElement("email");
    			contactInfo.appendChild(email);
    			Element emailAddress = doc.createElement("emailAddress");
    			emailAddress.appendChild(doc.createTextNode(xmlData.get("email").toString()));
    			email.appendChild(emailAddress);
    			
    			genericContent.put("email", (String)xmlData.get("email"));
    		}
    		
    		Element personalInfo = doc.createElement("personalInfo");
    		quoter.appendChild(personalInfo);
    		
    		Element firstName = doc.createElement("firstName");
    		if(xmlData.get("firstName") != null && !xmlData.get("firstName").toString().isEmpty()) {
    			firstName.appendChild(doc.createTextNode(this.escapeCharacter((String)xmlData.get("firstName"))));
    		}
    		personalInfo.appendChild(firstName);
    		
    		if(xmlData.get("firstName") != null && !xmlData.get("firstName").toString().isEmpty()) 
    			genericContent.put("first_name", (String)xmlData.get("firstName"));
    		else
    			genericContent.put("first_name", "");
    		
    		Element lastName = doc.createElement("lastName");
    		if(xmlData.get("lastName") != null && !xmlData.get("lastName").toString().isEmpty()) {
    			lastName.appendChild(doc.createTextNode(this.escapeCharacter((String)xmlData.get("lastName"))));
    		}
    		personalInfo.appendChild(lastName);
    		
    		if(xmlData.get("lastName") != null && !xmlData.get("lastName").toString().isEmpty()) 
    			genericContent.put("last_name", (String)xmlData.get("lastName"));  		
    		else
    			genericContent.put("last_name", "");
    		
    		
    		Element fullName = doc.createElement("fullName");
    		
    		if(xmlData.get("firstName") != null || xmlData.get("lastName") != null) {
	    		if((String)xmlData.get("firstName") != null && (String)xmlData.get("lastName") == null)
	    			fullName.appendChild(doc.createTextNode(this.escapeCharacter((String)xmlData.get("firstName"))));
	    		else if((String)xmlData.get("firstName") == null && (String)xmlData.get("lastName") != null)
	    			fullName.appendChild(doc.createTextNode(this.escapeCharacter((String)xmlData.get("lastName"))));
	    		else if((String)xmlData.get("firstName") != null && (String)xmlData.get("lastName") != null)
	    			fullName.appendChild(doc.createTextNode(this.escapeCharacter((String)xmlData.get("firstName")) + " " + this.escapeCharacter((String)xmlData.get("lastName"))));
    		}
    		personalInfo.appendChild(fullName);
    		
    		if((String)xmlData.get("firstName") != null && (String)xmlData.get("lastName") == null)
    			genericContent.put("full_name", (String)xmlData.get("firstName"));
    		else if((String)xmlData.get("firstName") == null && (String)xmlData.get("lastName") != null)
    			genericContent.put("full_name", (String)xmlData.get("lastName"));
    		else if((String)xmlData.get("firstName") != null && (String)xmlData.get("lastName") != null)
    			genericContent.put("full_name", (String)xmlData.get("firstName") + " " + (String)xmlData.get("lastName"));
    		else 
    			genericContent.put("full_name", "");
    		
    		Element businessInfo = doc.createElement("businessInfo");
    		quoter.appendChild(businessInfo);
    		
    		Element companyName = doc.createElement("companyName");
    		if(xmlData.get("companyName") != null && !xmlData.get("companyName").toString().isEmpty()) {
    			companyName.appendChild(doc.createTextNode(this.escapeCharacter((String)xmlData.get("companyName"))));
    		}
    		businessInfo.appendChild(companyName);
    		
    		if(xmlData.get("companyName") != null && !xmlData.get("companyName").toString().isEmpty()) {
    			genericContent.put("company", (String)xmlData.get("companyName"));
    		}
    		else 
    			genericContent.put("company", "");
    		
    		if(test) {
	    		
	    		Element title = doc.createElement("title");
	    		if(xmlData.get("title_id") != null && !xmlData.get("title").toString().isEmpty()) {
	    			title.appendChild(doc.createTextNode(this.escapeCharacter((String)xmlData.get("title_id"))));
	    		}
	    		businessInfo.appendChild(title);
	    		
	    		if(xmlData.get("title_id") != null && !xmlData.get("title").toString().isEmpty()) {
	    			genericContent.put("job_title", (String)xmlData.get("title_id"));
	    		}
	    		else
	    			genericContent.put("job_title", "");
    		}
    		else {
	    		
	    		Element title = doc.createElement("title");
	    		if(xmlData.get("title") != null && !xmlData.get("title").toString().isEmpty()) {
	    			title.appendChild(doc.createTextNode(this.escapeCharacter((String)xmlData.get("title"))));
	    		}
	    		businessInfo.appendChild(title);
	    		
	    		if(xmlData.get("title") != null && !xmlData.get("title").toString().isEmpty()) {
	    			genericContent.put("job_title", (String)xmlData.get("title"));
	    		}
	    		else
	    			genericContent.put("job_title", "");
    		}
    		
    		if(test) {
	    		
	    		Element industry = doc.createElement("industry");
	    		if(xmlData.get("company_industry") != null && !xmlData.get("company_industry").toString().isEmpty()) {
	    			industry.appendChild(doc.createTextNode(this.escapeCharacter((String)xmlData.get("company_industry"))));
	    		}
	    		businessInfo.appendChild(industry);
	    		
	    		if(xmlData.get("company_industry") != null && !xmlData.get("company_industry").toString().isEmpty()) {
	    			genericContent.put("industry", (String)xmlData.get("company_industry"));
	    		}
	    		else
	    			genericContent.put("industry", "");
    		}
    		else {
	    		
	    		Element industry = doc.createElement("industry");
	    		if(xmlData.get("companyIndustry") != null && !xmlData.get("companyIndustry").toString().isEmpty()) {
	    			industry.appendChild(doc.createTextNode(this.escapeCharacter((String)xmlData.get("companyIndustry"))));
	    		}
	    		businessInfo.appendChild(industry);
	    		
	    		if(xmlData.get("companyIndustry") != null && !xmlData.get("companyIndustry").toString().isEmpty()) {
	    			genericContent.put("industry", (String)xmlData.get("companyIndustry"));
	    		}
	    		else
	    			genericContent.put("industry", "");
    		}
    		
    		if(test) {
    			
	    		Element companySize = doc.createElement("companySize");
	    		if(xmlData.get("company_size_id") != null && !xmlData.get("company_size_id").toString().isEmpty()) {
	    			companySize.appendChild(doc.createTextNode(this.escapeCharacter((String)xmlData.get("company_size_id"))));
	    		}
	    		businessInfo.appendChild(companySize);
	    		
	    		if(xmlData.get("company_size_id") != null && !xmlData.get("company_size_id").toString().isEmpty()) {
	    			genericContent.put("company_size", (String)xmlData.get("company_size_id"));
	    		}
	    		else
	    			genericContent.put("company_size", "");
    		}
    		else {
	    		
	    		Element companySize = doc.createElement("companySize");
	    		if(xmlData.get("companySize") != null && !xmlData.get("companySize").toString().isEmpty()) {
	    			companySize.appendChild(doc.createTextNode(this.escapeCharacter((String)xmlData.get("companySize"))));
	    		}
	    		businessInfo.appendChild(companySize);
	    		
	    		if(xmlData.get("companySize") != null && !xmlData.get("companySize").toString().isEmpty()) {
	    			genericContent.put("company_size", (String)xmlData.get("companySize"));
	    		}
	    		else
	    			genericContent.put("company_size", "");
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
	        			answer.appendChild(doc.createTextNode(escapeCharacter(questionAndAnswers.get(key).toString().replace("[", "").replace("]", ""))));
	        		questionWithAnswers.appendChild(answer);
	        		
	        		Element priorityLevel = doc.createElement("priorityLevel");
	        		priorityLevel.appendChild(doc.createTextNode(String.valueOf(0)));
	        		questionWithAnswers.appendChild(priorityLevel);
	    		}
	    		
	    		StringBuilder sb = new StringBuilder();
	    		for(String key : questionAndAnswers.keySet()) {
	    			if(key.contains(":")) {
	    				sb.append(key.trim() + " " + questionAndAnswers.get(key).toString().replace("[", "").replace("]", ""));
	    				sb.append(System.getProperty("line.separator"));
	    			}
	    			else {
	    				sb.append(key.trim() + ":" + " " + questionAndAnswers.get(key).toString().replace("[", "").replace("]", ""));
	    				sb.append(System.getProperty("line.separator"));
	    			}
	    		}
	    		genericContent.put("description", sb.toString());
    		}
    		
    		genericContent.put("xml", getStringFromDoc(doc).replace("<?xml version=\"1.0\" encoding=\"UTF-16\"?>", "").trim().replace("&amp;", "&"));
    		return genericContent;
    	}
    	catch(Exception e) {
    		logger.info(String.format("GenerateRegularLead method Exception: %s", e));
    		return null;
    	}
    }
    
    private HashMap<String, String> generateCustomLead(int quoteRequestId, CategoryDefinition category, HashMap<String, Object> questionAndAnswers, HashMap<String, Object> xmlData, boolean test) {
    	HashMap<String, String> genericContent = new HashMap<String, String>();
    	logger.info(String.format("Generate Custom Lead: [%d]", quoteRequestId));
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
    		
    		genericContent.put("quote_number", String.valueOf(quoteRequestId));
    		
    		// categoryName elements
    		Element categoryName = doc.createElement("categoryName");
    		categoryName.appendChild(doc.createTextNode(String.valueOf(category.getDisplayName())));
    		quote.appendChild(categoryName);
    		
    		genericContent.put("category", String.valueOf(category.getDisplayName()));
    		
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
    		
    		// categoryName elements
    		if(xmlData.get("streetName") != null) {
	    		Element street = doc.createElement("street");
	    		street.appendChild(doc.createTextNode(this.escapeCharacter((String)xmlData.get("streetName"))));
	    		address.appendChild(street);
	    		
	    		genericContent.put("address", (String)xmlData.get("streetName"));

    		}
    		
    		if(xmlData.get("streetLine2") != null) {
	    		Element street2 = doc.createElement("street2");
	    		street2.appendChild(doc.createTextNode(this.escapeCharacter((String)xmlData.get("streetLine2"))));
	    		address.appendChild(street2);
	    		
	    		genericContent.put("address2", (String)xmlData.get("streetLine2"));
    		}
    		
    		if(xmlData.get("city") != null) {
	    		Element city = doc.createElement("city");
	    		city.appendChild(doc.createTextNode(this.escapeCharacter((String)xmlData.get("city"))));
	    		address.appendChild(city);
	    		
	    		genericContent.put("city", (String)xmlData.get("city"));
    		}
    		
    		if(xmlData.get("stateName") != null) {
	    		Element state = doc.createElement("state");
	    		address.appendChild(state);
	    		
	    		Element stateName = doc.createElement("stateName");
	    		stateName.appendChild(doc.createTextNode(this.escapeCharacter((String)xmlData.get("stateName"))));
	    		state.appendChild(stateName);
	    		
	    		Element stateAbbreviation = doc.createElement("stateAbbreviation");
	    		stateAbbreviation.appendChild(doc.createTextNode(this.escapeCharacter((String)xmlData.get("stateId"))));
	    		state.appendChild(stateAbbreviation);
	    		
	    		genericContent.put("state", (String)xmlData.get("stateId"));
    		}
    		
    		if(xmlData.get("postCode") != null) {
	    		Element zip = doc.createElement("zip");
	    		zip.appendChild(doc.createTextNode((String)xmlData.get("postCode")));
	    		address.appendChild(zip);
	    		
	    		genericContent.put("zip", (String)xmlData.get("postCode"));
    		}
    		
    		if(xmlData.get("phone") != null) {
	    		Element phone = doc.createElement("phone");
	    		contactInfo.appendChild(phone);
	    		
	    		genericContent.put("phone", (String)xmlData.get("phone"));
	    		
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
    			
    			genericContent.put("email", (String)xmlData.get("email"));
    		}
    		
    		Element personalInfo = doc.createElement("personalInfo");
    		quoter.appendChild(personalInfo);
    		
    		if(xmlData.get("firstName") != null) {
	    		Element firstName = doc.createElement("firstName");
	    		firstName.appendChild(doc.createTextNode(this.escapeCharacter((String)xmlData.get("firstName"))));
	    		personalInfo.appendChild(firstName);
	    		
	    		genericContent.put("first_name", (String)xmlData.get("firstName"));
    		}
    		
    		if(xmlData.get("lastName") != null) {
	    		Element lastName = doc.createElement("lastName");
	    		lastName.appendChild(doc.createTextNode(this.escapeCharacter((String)xmlData.get("lastName"))));
	    		personalInfo.appendChild(lastName);
	    		
	    		genericContent.put("last_name", (String)xmlData.get("lastName"));
    		}
    		
    		if(xmlData.get("firstName") != null || xmlData.get("lastName") != null) {
	    		Element fullName = doc.createElement("fullName");
	    		
	    		if((String)xmlData.get("firstName") != null && (String)xmlData.get("lastName") == null)
	    			fullName.appendChild(doc.createTextNode(this.escapeCharacter((String)xmlData.get("firstName"))));
	    		else if((String)xmlData.get("firstName") == null && (String)xmlData.get("lastName") != null)
	    			fullName.appendChild(doc.createTextNode(this.escapeCharacter((String)xmlData.get("lastName"))));
	    		else if((String)xmlData.get("firstName") != null && (String)xmlData.get("lastName") != null)
	    			fullName.appendChild(doc.createTextNode(this.escapeCharacter((String)xmlData.get("firstName")) + " " + this.escapeCharacter((String)xmlData.get("lastName"))));
	    		
	    		if((String)xmlData.get("firstName") != null && (String)xmlData.get("lastName") == null)
	    			genericContent.put("full_name", (String)xmlData.get("firstName"));
	    		else if((String)xmlData.get("firstName") == null && (String)xmlData.get("lastName") != null)
	    			genericContent.put("full_name", (String)xmlData.get("lastName"));
	    		else if((String)xmlData.get("firstName") != null && (String)xmlData.get("lastName") != null)
	    			genericContent.put("full_name", (String)xmlData.get("firstName") + " " + (String)xmlData.get("lastName") + "\n");
    		}
    		
    		Element businessInfo = doc.createElement("businessInfo");
    		quoter.appendChild(businessInfo);
    		
    		if(xmlData.get("companyName") != null) {
	    		Element companyName = doc.createElement("companyName");
	    		companyName.appendChild(doc.createTextNode(this.escapeCharacter((String)xmlData.get("companyName"))));
	    		businessInfo.appendChild(companyName);
	    		
	    		genericContent.put("company", (String)xmlData.get("companyName"));
    		}
    		
    		if(test) {
	    		if(xmlData.get("title_id") != null) {
		    		Element title = doc.createElement("title");
		    		title.appendChild(doc.createTextNode(this.escapeCharacter((String)xmlData.get("title_id"))));
		    		businessInfo.appendChild(title);
		    		
		    		genericContent.put("job_title", (String)xmlData.get("title_id"));
	    		}
    		}
    		else {
	    		if(xmlData.get("title") != null) {
		    		Element title = doc.createElement("title");
		    		title.appendChild(doc.createTextNode(this.escapeCharacter((String)xmlData.get("title"))));
		    		businessInfo.appendChild(title);
		    		
		    		genericContent.put("job_title", (String)xmlData.get("title"));
	    		}
    		}
    		
    		if(test) {
	    		if(xmlData.get("company_industry") != null) {
		    		Element industry = doc.createElement("industry");
		    		industry.appendChild(doc.createTextNode(this.escapeCharacter((String)xmlData.get("company_industry"))));
		    		businessInfo.appendChild(industry);
		    		
		    		genericContent.put("industry", (String)xmlData.get("company_industry"));
	    		}
    		}
    		else {
	    		if(xmlData.get("companyIndustry") != null) {
		    		Element industry = doc.createElement("industry");
		    		industry.appendChild(doc.createTextNode(this.escapeCharacter((String)xmlData.get("companyIndustry"))));
		    		businessInfo.appendChild(industry);
		    		
		    		genericContent.put("industry", (String)xmlData.get("companyIndustry"));
	    		}
    		}
    		
    		if(test) {
    			if(xmlData.get("company_size_id") != null) {
		    		Element companySize = doc.createElement("companySize");
		    		companySize.appendChild(doc.createTextNode(this.escapeCharacter((String)xmlData.get("company_size_id"))));
		    		businessInfo.appendChild(companySize);
		    		
		    		genericContent.put("company_size", (String)xmlData.get("company_size_id"));
	    		}
    		}
    		else {
	    		if(xmlData.get("companySize") != null) {
		    		Element companySize = doc.createElement("companySize");
		    		companySize.appendChild(doc.createTextNode(this.escapeCharacter((String)xmlData.get("companySize"))));
		    		businessInfo.appendChild(companySize);
		    		
		    		genericContent.put("company_size", (String)xmlData.get("companySize"));
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
	        			answer.appendChild(doc.createTextNode(escapeCharacter(questionAndAnswers.get(key).toString().replace("[", "").replace("]", ""))));
	        		questionWithAnswers.appendChild(answer);
	        		
	        		Element priorityLevel = doc.createElement("priorityLevel");
	        		priorityLevel.appendChild(doc.createTextNode(String.valueOf(0)));
	        		questionWithAnswers.appendChild(priorityLevel);
	    		}
	    		
	    		StringBuilder sb = new StringBuilder();
	    		for(String key : questionAndAnswers.keySet()) {
	    			if(key.contains(":")) {
	    				sb.append(key.trim() + " " + questionAndAnswers.get(key).toString().replace("[", "").replace("]", ""));
	    				sb.append(System.getProperty("line.separator"));
	    			}
	    			else {
	    				sb.append(key.trim() + ":" + " " + questionAndAnswers.get(key).toString().replace("[", "").replace("]", ""));
	    				sb.append(System.getProperty("line.separator"));
	    			}
	    		}
	    		genericContent.put("description", sb.toString());
    		}
    		
    		genericContent.put("xml", getStringFromDoc(doc).replace("<?xml version=\"1.0\" encoding=\"UTF-16\"?>", "").trim().replace("&amp;", "&"));
    		return genericContent;
    	}
    	catch(Exception e) {
    		logger.info(String.format("GenerateCustomLeads method Exception: %s", e));
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
    
}
