package com.buyerzone.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.util.*;

import com.buyerzone.dao.HttpManager;
import com.buyerzone.model.Lead;
import com.buyerzone.model.LeadPreference;
import com.buyerzone.model.LeadRequest;
import com.buyerzone.model.Response;
import com.buyerzone.model.cld.CategoryDefinition;
import com.buyerzone.model.cld.FieldOption;
import com.buyerzone.model.cld.Question;
import com.buyerzone.model.cld.QuestionWithAnswer;
import com.buyerzone.model.cld.RegistrationFields;
import com.buyerzone.root.Greeting;
import com.buyerzone.service.CustomLeadService;
import com.buyerzone.service.GlobalService;
import com.buyerzone.service.RegistrationService;
import com.buyerzone.service.SupplierCategoryService;
import com.buyerzone.util.ApiResponse;
import com.buyerzone.util.MessageUtil;
import com.buyerzone.util.ObjectConverter;

@RestController
public class LeadController {
	Logger logger = Logger.getLogger(LeadController.class);

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    private static final String[][] swapFields = new String[][] {
    	{"company_industry", "industry"},
    	{"company_size_id", "company_size"},
    	{"title_id", "title"}
    };
    
    private static final String[][] swapFieldsForMetaData = new String[][] {
    	{"city", "address_city"},
    	{"stateId", "address_state"},
    	{"country", "address_country"},
    	{"postCode", "address_zip"},
    	{"firstName", "first_name"},
    	{"lastName", "last_name"},
    	{"streetName", "address_street_line1"},
    	{"companyName", "company_name"},
    	{"companyIndustry", "company_industry"},
    	{"companySize", "company_size_id"},
    	{"streetLine2", "address_street_line2"},
    	{"title", "title_id"},
    	{"phone", "phone"},
    	{"altPhone", "alt_phone"}
    };
    
    private static final String[] additionalFields = new String[] {
    	"industry","company_size","title"
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

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World Dev") String name) {
        return new Greeting(counter.incrementAndGet(),
                            String.format(template, name));
    }
    
    @RequestMapping("/post")
    public Response post(@RequestParam(value="method", defaultValue="neopost.aspx") String method){
    	
    	HttpManager request = new HttpManager();
       	return request.postData("http://leaddelivery.buyerzone-labs.com/" + method, "");	
    	
    }
    
    @RequestMapping("/lead")
    public Lead get(@RequestParam(value="quoteId", defaultValue="123") int quoteId){
    	
    	Lead lead = null;
    	try {
		    
		    logger.info(String.format("Getting lead data. QuoteId = [%d]", quoteId));
		    lead = registrationService.getLead(quoteId);
		   
		    return lead;
		} catch (BeansException e) {
			
			System.out.print(e.getMessage());
			return lead;
			
		} catch (IllegalStateException e) {
			
			System.out.print(e.getMessage());
			return lead;
		} 
    	
    }
    
    @RequestMapping("/category/metadata/{categoryId}")
    public CategoryDefinition getCategory(@PathVariable int categoryId){	
    	CategoryDefinition info = null;
    	List<RegistrationFields> registration = new ArrayList<RegistrationFields>();
    	
    	try {
		    info = supplierCategoryService.getSupplierCategoryInfo(categoryId);
		    if(info != null) {
		    List<HashMap<String,String>> columns = globalService.getDataBySingleParam("dz_getTableInfo", "buyer_registration");
		    List<FieldOption> optionSettings = supplierCategoryService.getFieldOptions();
	    	List<Question> questions = supplierCategoryService.getCategoryQuestions(info.getCategoryId());
	    	List<HashMap<String,String>> answersOptions = globalService.getDataBySingleParam("dz_getAnswerOptions",Integer.toString(info.getQuestionSetId()));
	    	List<String> swappedFields = new ArrayList<String>();
	    	
	    	HttpManager request = new HttpManager();
			String url = baseURL + "/category/customreg/" + info.getCategoryId(); 
			
			String response = request.getData(url);
			
			if(response != null) {
			String[] customRegistrationFields = response.replace("\"", "").replace("[", "").replace("]", "").split(",");
			
				for(String[] strArray : swapFieldsForMetaData) {
					for(String field : customRegistrationFields) {
						if(field.equals(strArray[0])) {
							swappedFields.add(strArray[1]);
						}
					}
				}
			}
			swappedFields.add("email");
			swappedFields.add("{quoteId}");
			swappedFields.add("full_name");
			swappedFields.add("sent_date");
			
			for(String field : swappedFields ){
	        	RegistrationFields reg = new RegistrationFields();
	        	reg.setSourceValue(field);
	        	if(field.contains("{}"))
	        		reg.setSourceValue(field);
	        	
	        	for(FieldOption option: optionSettings){
	        		if(option.getFieldName().equals(field)){
		        		String groupName = option.getLookupGroup();
		        		List<HashMap<String,String>> options = globalService.getDataBySingleParam("dz_getRegFieldOptions", groupName);
		        		reg.setAnswers(options);
	        		}
	        	}
	        	registration.add(reg);
		    }
			
//		    for(HashMap<String,String> map : columns ){
//		        for( String key : map.keySet() ){
//		        	String value = map.get(key);
//		        	RegistrationFields reg = new RegistrationFields();
//		        	reg.setSourceValue(value);
//		        	if(key.contains("{}"))
//		        		reg.setSourceValue(value);
//		        	
//		        	for(FieldOption option: optionSettings){
//		        		if(option.getFieldName().equals(value)){
//			        		String groupName = option.getLookupGroup();
//			        		List<HashMap<String,String>> options = globalService.getDataBySingleParam("dz_getRegFieldOptions", groupName);
//			        		reg.setAnswers(options);
//		        		}
//		        	}
//		        	registration.add(reg);
//		        }
//		    }
		    
		    List<Question> newQuestions = new ArrayList<Question>();
		    for(Question question : questions) {
		    	for(HashMap<String,String> map :answersOptions) {
	    			for(Map.Entry<String,String> entry : map.entrySet()) {
	    				if(question.getType().equals("textmultiple") && question.getQuestionId().equals(map.get("questionXmlId"))) {
	    					if(entry.getValue().equals("textmultiple")) {
	    						Question tempQuestion = new Question();
	    						tempQuestion.setType(question.getType());
	    						tempQuestion.setBuyerText(question.getBuyerText());
	    						tempQuestion.setQuestionId(map.get("questionId"));
	    						tempQuestion.setSourceValue(map.get("answerOption"));
	    						tempQuestion.setQuestionSetId(question.getQuestionSetId());
	    						newQuestions.add(tempQuestion);
	    					}
	    				}
	    			}
	    		}
		    }
		    
		    List<Question> responseQuestions = new ArrayList<Question>(questions);
		    for(Question question : questions) {
		    	if(question.getType().equals("textmultiple"))
		    		responseQuestions.remove(question);
		    }
		    
	    	for(Question question: responseQuestions){
	    		List<HashMap<String,String>> questionAnswers = new ArrayList<HashMap<String,String>>();
	    		
	    		for(HashMap<String,String> map : answersOptions ){
	    			for( String key : map.keySet() ){
	    				
	    				if(key.equals("questionId")){
	    					String value = String.valueOf(map.get(key));
	    					if(question.getQuestionId().equals(value)){
	    						
	    						if(map.get("questionType").equals("multiplechoice") ){
		    						HashMap<String,String> option = new HashMap<String, String>();
		    						option.put("option", map.get("answerOption"));
		    						questionAnswers.add(option);
	    						}

	    					}
	    				}

	    			}
	    			
	    			if((questionAnswers.size() > 0))
	    				question.setAnswers(questionAnswers);
	    			
	    		}
	    	}
	    	if(newQuestions != null) {
	    		for(Question q : newQuestions) {
	    			responseQuestions.add(q);
	    		}
	    	}
		    
	    	info.setRegistration(registration);
	    	info.setQuestions(responseQuestions);
		    }
		    else 
		    	info = new CategoryDefinition();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
    	
    	return info;

    }
    
    
    @RequestMapping(value = "/lead/send", method=RequestMethod.POST)
    public ResponseEntity<ApiResponse<HashMap<String, String>>> sendLead(@RequestParam(value="quoteId", defaultValue="8817157") int quoteId, @RequestParam("supplierId") int supplierId, HttpServletRequest methodRequest){
    	String msg = String.format("Successfully sent lead. QuoteId = [%s]", quoteId);
    	String errMsg = String.format("Failed to sent lead. QuoteId = [%s]", quoteId);
    	String methodName = null;
    	List<LeadPreference> mappings = null;
    	LeadPreference deliveryConfig = null;
    	Response response = null;
    	ApiResponse<HashMap<String, String>> api = null;
    	HashMap<String, String> data = null;
    	List<QuestionWithAnswer> answers = null;
    	String version = null;
    	String type = "New CLD";
    	String getUrl = null;
    	CategoryDefinition info = null;
    	try {
    		methodName = methodRequest.getRequestURL().toString() + "?quoteId=" + methodRequest.getParameter("quoteId") + "&supplierId=" + methodRequest.getParameter("supplierId");
    		api = new ApiResponse<HashMap<String, String>>();
    		mappings = new ArrayList<LeadPreference>();
    		info = supplierCategoryService.getSupplierCategoryInfo(supplierId);
    		logger.info(String.format("Getting lead data. QuoteId = [%d]", quoteId));
    		deliveryConfig = registrationService.getLeadPreferences(supplierId);
    		version = registrationService.getVersionByLive(deliveryConfig.getPreferenceId());
    		logger.info(String.format("Got verion number. Version = [%s]", version));
    		if(version != null) {
			    logger.info(String.format("Getting mapping data. PreferenceId = [%d]", deliveryConfig.getPreferenceId()));
			    mappings = registrationService.getLeadMappings(deliveryConfig.getPreferenceId(),Integer.parseInt(version));
			    String[] mappingsFields = getFields(mappings);
			    List<String> newFields = new ArrayList<String>();
			    for(String field : mappingsFields) {
			    	if(field.matches("\\{(.*?)\\}") || field.matches("(.*?)-(.*?)") || field.matches("_(.*?)_")) {
			    		String replaceString = "'" + field + "'";
			    		newFields.add(replaceString);
			    	} 
			    	else 
			    		newFields.add(field);
			    }
			    for(String field : additionalFields) {
			    	newFields.add(field);
			    }
			    String[]  newMappingsField = newFields.toArray(new String[newFields.size()]);
			    
			    answers = supplierCategoryService.getQuestionWithAnswers(quoteId);

				answers = removeHiddenQuestion(info, answers);
			    
			    for(QuestionWithAnswer option : answers) {
			    	if(option.getAnswer() == null || option.getAnswer().isEmpty()) {
			    		option.setAnswer("");
			    	}
			    }
			    
			    List<HashMap<String, String>> quoteRequestParam = new ArrayList<HashMap<String,String>>();
			    quoteRequestParam =	supplierCategoryService.getQuoteRequestParam(quoteId);
			    if(quoteRequestParam != null && quoteRequestParam.size() > 0) {
				    for(QuestionWithAnswer q : answers) {
				    	for(HashMap<String,String> hashMap: quoteRequestParam) {
				    		for(String key : hashMap.keySet()) {
				    			String value = hashMap.get(key);
				    			if(value.contains(q.getQuestionId()) && q.getAnswer().contains("Other")) {
					    			//q.setAnswer(q.getQuestion() + " " + hashMap.get("param_value"));
				    				q.setAnswer(q.getQuestion() + " " + q.getAnswer() + " " + hashMap.get("param_value"));
				    			}
				    		}
				    	}
				    }
			    }		
			    data = registrationService.getCustomLead(quoteId, supplierId, newMappingsField);
			    for(String[] strArray : swapFields) {
			    	swap(strArray[0], strArray[1], data);
			    }
			    
			    //Remove white space.
			    for(Map.Entry<String, String> entry : data.entrySet()) {
			    	if(entry.getValue() == null || entry.getValue().equals("null") || entry.getValue().isEmpty() || entry.getValue().trim().length() == 0) 
			    		entry.setValue("");
			    }
			    
			    replaceData(answers, data, false);
			    replaceQuoteId(quoteId, data);

			    mappings = setParamValues(mappings, data, quoteId, answers, null);
			    LeadRequest requestInfo = new LeadRequest();
			    
			    requestInfo.setData(getRequestData(mappings));
			    requestInfo.setLeadPreference(deliveryConfig);
			    
			   HttpManager request = new HttpManager();
			   
			   String url = deliveryConfig.getTargetUrl() != null ? deliveryConfig.getTargetUrl() : deliveryConfig.getTestUrl(); 
			   response = request.makeHttpPostRequest(url,requestInfo.getData());
			   
			   StringBuffer paramsLog = new StringBuffer();
				for (Map.Entry<String, String> entry : requestInfo.getData().entrySet()) {
					paramsLog.append(entry.getKey() + "=" + entry.getValue() + "&");
				}
				if (paramsLog.length() > 0) {
					logger.debug(paramsLog.toString().substring(0, paramsLog.length() - 1));
				} else {
					logger.debug(MessageUtil.errorMessage(type, quoteId, url, "No parameters found!", "No parameters found!"));
				}
				
			   if(response.getResponseCode() == 200 || response.getResponseCode() == 201 || response.getResponseCode() == 202) {
				   response.setDeveloperMessage(msg);
				   api.setResponse(response);
				   api.setMethodName(methodName);
				   api.setContent(getRequestData(mappings));
				   if(logger.isDebugEnabled())
					   logger.debug(MessageUtil.successMessage(type, quoteId, response.getResponseCode(), url, response.getResponseMessage(), paramsLog.toString().substring(0, paramsLog.length() - 1)));
				   else
					   logger.info(MessageUtil.successMessage(type, quoteId, response.getResponseCode(), url, null, paramsLog.toString().substring(0, paramsLog.length() - 1)));
				   return new ResponseEntity<ApiResponse<HashMap<String, String>>>(api, org.springframework.http.HttpStatus.OK);
			   } else {
				   response.setDeveloperMessage(errMsg);
				   api.setResponse(response);
				   api.setMethodName(methodName);
				   api.setContent(getRequestData(mappings));
				   if(logger.isDebugEnabled())
					   logger.debug(MessageUtil.failedMessage(type, quoteId, response.getResponseCode(), url, response.getResponseMessage(), paramsLog.toString().substring(0, paramsLog.length() - 1)));
				   else
					   logger.error(MessageUtil.failedMessage(type, quoteId, response.getResponseCode(), url, null, paramsLog.toString().substring(0, paramsLog.length() - 1)));
				   return new ResponseEntity<ApiResponse<HashMap<String, String>>>(api, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
			   }
				   
    		} else {
    		   errMsg = String.format("No live mapping data detected with the quoteId = [%d] and supplierId = [%d].", quoteId, supplierId);
    		   response = new Response();
  			   response.setDeveloperMessage(errMsg);
  			   response.setResponseMessage(errMsg);
  			   response.setResponseCode(Integer.parseInt(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR.toString()));
  			   api.setResponse(response);
  			   api.setMethodName(methodName);
  			   logger.error(MessageUtil.errorMessage(type, quoteId, null, "No live mapping data detected", "No live mapping data detected"));
  			   return new ResponseEntity<ApiResponse<HashMap<String, String>>>(api, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
    		}
		 
    	} catch (BeansException e) {
    		logger.error(String.format("Internal Server Error [%s]", e));
			response = new Response();
			response.setDeveloperMessage(e.getMessage());
			response.setResponseMessage(errMsg);
			response.setResponseCode(Integer.parseInt(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR.toString()));
			api.setResponse(response);
			api.setMethodName(methodName);
			api.setContent(getRequestData(mappings));
			return new ResponseEntity<ApiResponse<HashMap<String, String>>>(api, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
			
		} catch (IllegalStateException e) {
			logger.error(String.format("Internal Server Error [%s]", e));
			response = new Response();
			response.setResponseCode(Integer.parseInt(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR.toString()));
			response.setDeveloperMessage(e.getMessage());
			response.setResponseMessage(errMsg);
			api.setResponse(response);
			api.setMethodName(methodName);
			api.setContent(getRequestData(mappings));
			return new ResponseEntity<ApiResponse<HashMap<String, String>>>(api, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
		} catch(Exception e) {
			logger.error(String.format("Internal Server Error [%s]", e));
			response = new Response();
			response.setResponseCode(Integer.parseInt(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR.toString()));
			response.setDeveloperMessage(e.getMessage());
			response.setResponseMessage(errMsg);
			api.setResponse(response);
			api.setMethodName(methodName);
			api.setContent(getRequestData(mappings));
			return new ResponseEntity<ApiResponse<HashMap<String, String>>>(api, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
    
    @RequestMapping("/lead/content")
    public HashMap<String, String> getContent(@RequestParam(value="quoteId") int quoteId, @RequestParam("supplierId") int supplierId){
    	String msg = String.format("Successfully sent lead. QuoteId = [%s]", quoteId);
    	List<LeadPreference> mappings = null;
    	LeadPreference deliveryConfig = null;
    	ApiResponse<HashMap<String, String>> api = null;
    	HashMap<String, String> data = null;
    	List<QuestionWithAnswer> answers = null;
    	String version = null;
    	CategoryDefinition info = null;
    	try {
    		api = new ApiResponse<HashMap<String, String>>();
    		mappings = new ArrayList<LeadPreference>();
    		logger.info(String.format("Getting lead data. QuoteId = [%d]", quoteId));
    		deliveryConfig = registrationService.getLeadPreferences(supplierId);
    		version = registrationService.getVersionByLive(deliveryConfig.getPreferenceId());
    		logger.info(String.format("Got verion number. Version = [%s]", version));
    		info = supplierCategoryService.getSupplierCategoryInfo(supplierId);

    		if(version != null) {
			    logger.info(String.format("Getting mapping data. PreferenceId = [%d]", deliveryConfig.getPreferenceId()));
			    mappings = registrationService.getLeadMappings(deliveryConfig.getPreferenceId(),Integer.parseInt(version));
			    String[] mappingsFields = getFields(mappings);
			    List<String> newFields = new ArrayList<String>();
			    for(String field : mappingsFields) {
			    	if(field.matches("\\{(.*?)\\}") || field.matches("(.*?)-(.*?)") || field.matches("_(.*?)_")) {
			    		String replaceString = "'" + field + "'";
			    		newFields.add(replaceString);
			    	} 
			    	else 
			    		newFields.add(field);
			    }
			    for(String field : additionalFields) {
			    	newFields.add(field);
			    }
			    String[]  newMappingsField = newFields.toArray(new String[newFields.size()]);
			    
			    answers = supplierCategoryService.getQuestionWithAnswers(quoteId);
			    
			    answers = removeHiddenQuestion(info, answers);
			    
			    for(QuestionWithAnswer option : answers) {
			    	if(option.getAnswer() == null || option.getAnswer().isEmpty()) {
			    		option.setAnswer("");
			    	}
			    }
			    
			    List<HashMap<String, String>> quoteRequestParam = new ArrayList<HashMap<String,String>>();
			    quoteRequestParam =	supplierCategoryService.getQuoteRequestParam(quoteId);
			    if(quoteRequestParam != null && quoteRequestParam.size() > 0) {
				    for(QuestionWithAnswer q : answers) {
				    	for(HashMap<String,String> hashMap: quoteRequestParam) {
				    		for(String key : hashMap.keySet()) {
				    			String value = hashMap.get(key);
				    			if(value.contains(q.getQuestionId()) && q.getAnswer().contains("Other")) {
					    			//q.setAnswer(q.getQuestion() + " " + hashMap.get("param_value"));
				    				q.setAnswer(q.getQuestion() + " " + q.getAnswer() + " " + hashMap.get("param_value"));
				    			}
				    		}
				    	}
				    }
			    }		
			    data = registrationService.getCustomLead(quoteId, supplierId, newMappingsField);
			    for(String[] strArray : swapFields) {
			    	swap(strArray[0], strArray[1], data);
			    }
			    
			    //Remove white space.
			    for(Map.Entry<String, String> entry : data.entrySet()) {
			    	if(entry.getValue() == null || entry.getValue().equals("null") || entry.getValue().isEmpty() || entry.getValue().trim().length() == 0) 
			    		entry.setValue("");
			    }
			    replaceData(answers, data, false);
			    replaceQuoteId(quoteId, data);

			    mappings = setParamValues(mappings, data, quoteId, answers, null);
			    
			    return getRequestData(mappings);
				   
    		} else {
    		   String errMsg = String.format("No live mapping data detected with the quoteId = [%d] and supplierId = [%d].", quoteId, supplierId);
  			   logger.info(errMsg);
  			   return  getRequestData(mappings);
    		}
		 
    	} catch (BeansException e) {
			logger.error(e);
			return  getRequestData(mappings);
		} catch (IllegalStateException e) {
			logger.error(e);
			return getRequestData(mappings);
		} catch(Exception e) {
			logger.error(e);
			return getRequestData(mappings);
    	}
    }
    
    
    @RequestMapping("/lead/mappings")
    public ResponseEntity<ApiResponse<List<LeadPreference>>> getLeadMappings(
    		@RequestParam(value="quoteId", defaultValue="8817157") int quoteId, 
    		@RequestParam(value="supplierId", defaultValue="28413") int supplierId,
    		@RequestParam(value="version", defaultValue="1") int version){
    	
    	Lead lead = null;
    	ApiResponse<List<LeadPreference>> mappings = new ApiResponse<List<LeadPreference>>();
    	LeadPreference deliveryConfig = null;
    	List<QuestionWithAnswer> answers = null;
    	
    	try {
		    
		    logger.info(String.format("Getting lead data. QuoteId = [%d]", quoteId));
		    
		    deliveryConfig = registrationService.getLeadPreferences(supplierId);
		    
		    if(deliveryConfig != null){
		    	
			    mappings.setContent(registrationService.getLeadMappings(deliveryConfig.getPreferenceId(),version));
			    String[] fields = getFields(mappings.getContent());
			    answers = supplierCategoryService.getQuestionWithAnswers(quoteId);
			    HashMap<String, String> data = registrationService.getCustomLead(quoteId, supplierId, fields);
			    replaceData(answers, data, false);
			    mappings.setContent(setParamValues(mappings.getContent(), data, quoteId, answers, null));
			    
		    }

		    return new ResponseEntity<ApiResponse<List<LeadPreference>>>(mappings, org.springframework.http.HttpStatus.ACCEPTED);
		    
		} catch (BeansException e) {
			logger.error(e);
			Response response = new Response();
			response.setDeveloperMessage(e.getMessage());
			mappings.setResponse(response);
			return new ResponseEntity<ApiResponse<List<LeadPreference>>>(mappings, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
			
		} catch (IllegalStateException e) {
			logger.error(e);
			Response response = new Response();
			response.setDeveloperMessage(e.getMessage());
			mappings.setResponse(response);
			return new ResponseEntity<ApiResponse<List<LeadPreference>>>(mappings, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
		} catch(Exception e) {
			logger.error(e);
			Response response = new Response();
			response.setDeveloperMessage(e.getMessage());
			mappings.setResponse(response);
			return new ResponseEntity<ApiResponse<List<LeadPreference>>>(mappings, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
    	}


    	
    }
    
    private String[] getFields(List<LeadPreference> settings){
    	List<String> fields = new ArrayList<String>();
    	
    	for(LeadPreference setting: settings){
    		if((setting.getMappingType() == 1) || (setting.getMappingType() == 3)){
    			
    			if(!fields.contains(setting.getValue()))
    				fields.add(setting.getValue());
    		}
    	}
    	
    	String[] fieldNames = fields.toArray(new String[fields.size()]);
    	
    	return fieldNames;
    }
    
    private String[] getTestFieldsandData(List<LeadPreference> settings){
    	List<String> fields = new ArrayList<String>();
    	for(LeadPreference setting: settings){
    			fields.add(setting.getKey() + "=" + setting.getValue());
    	}
    	
    	String[] fieldNames = fields.toArray(new String[fields.size()]);
    	
    	return fieldNames;
    }
    
    private String getAllQuestionsAndAnswers(List<QuestionWithAnswer> questionWithAnswers, String questionSeparator, String questionSetSeparator, boolean test){
    	StringBuilder sb = new StringBuilder();
    	String separator = questionSeparator;
    	if(separator == null) 
    		separator = "=";
    	String nameValueSeparator = questionSetSeparator;
    	if(nameValueSeparator == null)
    		nameValueSeparator = "; ";
    	
    	int length = 0;
    	if(test) {
    		//For mapping type equals 4. Set sub questions answers to be NULL for random test lead.
    		for(QuestionWithAnswer answer : questionWithAnswers) {
    			if(answer.getType().equals("textmultiple")) {
    				sb.append(answer.getAnswer()).append(separator);
    				sb.append("NULL").append(nameValueSeparator);
    			} else {
    				sb.append(answer.getQuestion()).append(separator);
    				sb.append(answer.getAnswer()).append(nameValueSeparator);
    			}
    		}
	    	
    		length = sb.toString().length();
			int pos = sb.toString().lastIndexOf(nameValueSeparator);
			
	    	return sb.toString().substring(0, pos);
    	} 
    	else {
    		
    		for(QuestionWithAnswer answer : questionWithAnswers) {
    			String subString = sb.toString();
    			if(subString.contains(answer.getQuestion())) {
    				sb.setLength(sb.length() - 1);
    				sb.append(",");
    				sb.append(answer.getAnswer()).append(nameValueSeparator);	
    			} else {
    				sb.append(answer.getQuestion()).append(separator);
    				sb.append(answer.getAnswer()).append(nameValueSeparator);	
    			}
    		}
		    	
    		length = sb.toString().length();
			int pos = sb.toString().lastIndexOf(nameValueSeparator);
			
	    	return sb.toString().substring(0, pos);
    	}
    }

    private HashMap<String, String> getRequestData(List<LeadPreference> mappings) {
    	HashMap<String,String> requestData = new HashMap<String, String>();
    	HashMap<String, String> answers = new HashMap<String,String>();
    	for(LeadPreference pref : mappings){
    		if(pref.getMappingType() == 3) {
    			//Make sure correct value appended to final answers.
    			if(pref.getValue() != null && !pref.getValue().isEmpty())
    				addValue(answers, pref.getKey(), pref.getValue());
    		} else {
    			requestData.put(pref.getKey(), pref.getValue());
    		}
    	}
		
		//Set value to be one white space for Null or empty value. Or process multiple or single answers' values.
		for(Map.Entry mapEntry : answers.entrySet()) {
			for(Map.Entry<String, String> dataEntry : answers.entrySet()) {
				StringBuilder sb = new StringBuilder();
				if(dataEntry.getKey().equals(mapEntry.getKey())) {
					List<String> answerLists = (List<String>) mapEntry.getValue();
					if(answerLists != null && !answerLists.isEmpty()) {
    					for(String value : answerLists) {
    						sb.append(value);
    						sb.append("|");
    					}
    					dataEntry.setValue(sb.toString());
					} 
					else 
						dataEntry.setValue("");
				}
			}
		}

		//Removed the last character from the answer string, which is "|".
		for(Map.Entry<String, String> dataEntry : answers.entrySet()) {
			if(dataEntry.getValue() != null && dataEntry.getValue().matches("^(.*?\\|)$")) {
				String answerValue = dataEntry.getValue();
				if (answerValue.length() > 0 && answerValue.charAt(answerValue.length()-1)=='|') {
					answerValue = answerValue.substring(0, answerValue.length()-1);
					dataEntry.setValue(answerValue);
				}
			}
		}

  
    	for(LeadPreference lp : mappings) {
	    	if(lp.getCharLimit() != -1 && (lp.getMappingType() == 3 || lp.getMappingType() == 1))
	    		limitCharLength(lp.getCharLimit(), lp.getKey(), requestData);
	    }
    	
    	
    	requestData.putAll(answers);
    	
    	return requestData;
    }
    
    /*
    private HashMap<String, String> getRequestData2(List<LeadPreference> mappings){
    	HashMap<String,String> requestData = new HashMap<String, String>();
    	HashMap<String, String> answers = new HashMap<String,String>();
    	String newValue = "";
    	for(LeadPreference pref : mappings){
    		if(pref.getMappingType() == 3) {
    			if(answers.keySet() == null || answers.keySet().size() == 0) {
    				newValue = pref.getValue() + "|";
    				answers.put(pref.getKey(), pref.getValue());
    			} else {
	    			for(String key : answers.keySet()) {
	    				if(key != null && key.equals(pref.getKey())) {
	    					newValue = newValue + pref.getValue() + "|";
	    				}
	    				else 
	    					newValue = pref.getValue();
	    			}
	    			answers.put(pref.getKey(), newValue);
    			}
    		} else {
    			requestData.put(pref.getKey(), pref.getValue());
    		}
    	}
    	
    	for(String key : answers.keySet()) {
    		requestData.put(key, answers.get(key));
    	}
  
    	for(LeadPreference lp : mappings) {
	    	if(lp.getCharLimit() != -1 && (lp.getMappingType() == 3 || lp.getMappingType() == 1))
	    		limitCharLength(lp.getCharLimit(), lp.getKey(), requestData);
	    }
    	
    	return requestData;
    	
    }*/
    
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

    
    private void replaceData(List<QuestionWithAnswer> questionWithAnswer, HashMap<String, String> data, boolean test) {
    	if(test) {
    		for(QuestionWithAnswer answer : questionWithAnswer) {
    			for(Map.Entry<String, String> dataEntry : data.entrySet()) {
    				//Set random lead data to be null value for all sub questions.
    				if(answer.getQuestionId().equals(dataEntry.getValue())) {
    					if(answer.getType().equals("textmultiple")) {
    						dataEntry.setValue("NULL");
    					} else {
							dataEntry.setValue(answer.getAnswer());
	    					break;
    					}
    				} 
    			}
    		}
    	} else {
    		
    		HashMap<String, String> newMaps = new HashMap<String, String>();
    		
    		for(QuestionWithAnswer answer : questionWithAnswer) {
    			addValue(newMaps, answer.getQuestionId(), answer.getAnswer());
    		}
    		
    		//Set value to be one white space for Null or empty value. Or process multiple or single answers' values.
    		for(Map.Entry mapEntry : newMaps.entrySet()) {
    			for(Map.Entry<String, String> dataEntry : data.entrySet()) {
    				StringBuilder sb = new StringBuilder();
    				if(dataEntry.getKey().equals(mapEntry.getKey())) {
    					List<String> answerLists = (List<String>) mapEntry.getValue();
    					if(answerLists != null && !answerLists.isEmpty()) {
	    					for(String value : answerLists) {
	    						sb.append(value);
	    						sb.append("|");
	    					}
	    					dataEntry.setValue(sb.toString());
    					} 
    					else 
    						dataEntry.setValue("");
    				}
    			}
    		}

    		//Removed the last character from the answer string, which is "|".
    		for(Map.Entry<String, String> dataEntry : data.entrySet()) {
    			if(dataEntry.getValue() != null && dataEntry.getValue().matches("^(.*?\\|)$")) {
    				String answerValue = dataEntry.getValue();
    				if (answerValue.length() > 0 && answerValue.charAt(answerValue.length()-1)=='|') {
    					answerValue = answerValue.substring(0, answerValue.length()-1);
    					dataEntry.setValue(answerValue);
    				}
    			}
    		}
    	}
    }
    
    
    @RequestMapping("/lead/sendit")
    public Response send(@RequestParam(value="quoteId", defaultValue="123") int quoteId){
    	
        Lead lead = null;
        
        try {
			   lead = registrationService.getLead(quoteId);
			   
			   Map<String,String> currMap = ObjectConverter.toMap(lead);
			   HttpManager request = new HttpManager();
			   Response response = request.sendData("http://leaddelivery.buyerzone-labs.com/posttest.aspx",currMap);
			   response.setDeveloperMessage(String.format("Lead sent. QuoteId = [%s], Email = [%s], Name = [%s %s]", quoteId, lead.getEmail(), lead.getFirstName(), lead.getLastName()));
			   
			   logger.info(String.format("Successfully sent lead. QuoteId = [%d], Name = [%s %s], Email = [%s]", quoteId, lead.getFirstName(), lead.getLastName(), lead.getEmail()));
			   
			   return response;
   
		} catch (BeansException e) {
			
			System.out.print(e.getMessage());
			return new Response();
			
		} catch (IllegalStateException e) {
			
			System.out.print(e.getMessage());
			return new Response();
		} 
        
    }
    
    
    @RequestMapping(value = "/lead/test", method=RequestMethod.POST)
    public ResponseEntity<ApiResponse<HashMap<String, String>>> sendTestLead(@RequestParam(value = "quoteId", required=false, defaultValue="0") int quoteId, @RequestParam("supplierId") int supplierId, @RequestParam(value = "version", defaultValue="0") int version, HttpServletRequest methodRequest){
    	String msg = String.format("Successfully sent lead. QuoteId = [%s]", quoteId);
    	String errMsg = String.format("Failed to sent lead. QuoteId = [%s]", quoteId);
    	boolean isTest = false;
    	List<LeadPreference> mappings = null;
    	LeadPreference deliveryConfig = null;
    	Response response = null;
    	ApiResponse<HashMap<String, String>> api = null;
    	CategoryDefinition info = null;
    	HashMap<String, String> data = new HashMap<String, String>();
    	String newVersion = null;
    	String methodName = null;
    	List<QuestionWithAnswer> allAnswers = null;
    	List<QuestionWithAnswer> answers = null;
    	Set<String> questions = null;
    	String type = "New CLD";
    	try {
    		methodName = methodRequest.getRequestURL().toString() + "?quoteId=" + methodRequest.getParameter("quoteId") + "&supplierId=" + methodRequest.getParameter("supplierId") + "&version=" + methodRequest.getParameter("version") ;
    		
		    logger.info(String.format("Getting lead data. QuoteId = [%d]", quoteId));
		    
		    if(quoteId != 0) {
	    		api = new ApiResponse<HashMap<String, String>>();
	    		mappings = new ArrayList<LeadPreference>();
	    		logger.info(String.format("Getting lead data. QuoteId = [%d]", quoteId));
	    		deliveryConfig = registrationService.getLeadPreferences(supplierId);
	    		if(version == 0) {
			    	newVersion = registrationService.getVersionByLive(deliveryConfig.getPreferenceId());
			    	if(newVersion == null || newVersion.isEmpty())
			    		newVersion = String.valueOf(customLeadService.getLatestVersion(deliveryConfig.getPreferenceId())); 
			    }
			    else 
			    	newVersion = String.valueOf(version);
			    logger.info(String.format("Getting mapping data. PreferenceId = [%d]", deliveryConfig.getPreferenceId()));
			    mappings = registrationService.getLeadMappings(deliveryConfig.getPreferenceId(),Integer.parseInt(newVersion));
			    String[] mappingsFields = getFields(mappings);
			    List<String> newFields = new ArrayList<String>();
			    
			    //Add quotation marks to String data 
			    for(String field : mappingsFields) {
			    	if(field.matches("\\{(.*?)\\}") || field.matches("(.*?)-(.*?)") || field.matches("_(.*?)_")) {
			    		String replaceString = "'" + field + "'";
			    		newFields.add(replaceString);
			    	} 
			    	else 
			    		newFields.add(field);
			    }
			    
			    for(String field : additionalFields) {
			    	newFields.add(field);
			    }
			    String[]  newMappingsField = newFields.toArray(new String[newFields.size()]);
			    
			    answers = supplierCategoryService.getQuestionWithAnswers(quoteId);
			    
			    answers = removeHiddenQuestion(info, answers);
			    
			    for(QuestionWithAnswer option : answers) {
			    	if(option.getAnswer() == null || option.getAnswer().isEmpty()) {
			    		option.setAnswer("");
			    	}
			    }
			    
			    List<HashMap<String, String>> quoteRequestParam = new ArrayList<HashMap<String,String>>();
			    quoteRequestParam =	supplierCategoryService.getQuoteRequestParam(quoteId);
			    
			    //Add real data to Other option (radio or checkboxes).
			    if(quoteRequestParam != null && quoteRequestParam.size() > 0) {
				    for(QuestionWithAnswer q : answers) {
				    	for(HashMap<String,String> hashMap: quoteRequestParam) {
				    		for(String key : hashMap.keySet()) {
				    			String value = hashMap.get(key);
				    			if(value.contains(q.getQuestionId()) && q.getAnswer().contains("Other"))
				    				//q.setAnswer(q.getQuestion() + " " + hashMap.get("param_value"));
					    			q.setAnswer(q.getQuestion() + " " + q.getAnswer() + " " + hashMap.get("param_value"));
				    		}
				    	}
				    }
			    }
			    data = registrationService.getUnmatchedCustomLead(quoteId, newMappingsField);
			    
			    //Swap some IDs to be real value
			    for(String[] strArray : swapFields) {
			    	swap(strArray[0], strArray[1], data);
			    }
			    
			    //Remove white space.
			    for(Map.Entry<String, String> entry : data.entrySet()) {
			    	if(entry.getValue() == null || entry.getValue().equals("null") || entry.getValue().isEmpty() || entry.getValue().trim().length() == 0) 
			    		entry.setValue("");
			    }
			    
			    replaceData(answers, data, false);
			    replaceQuoteId(quoteId, data);

			    //Removed unselected fields for both regs and questions.
			    mappings = setParamValues(mappings, data, quoteId, answers, null);
			    
			    LeadRequest requestInfo = new LeadRequest();
			    requestInfo.setData(getRequestData(mappings));
			    requestInfo.setLeadPreference(deliveryConfig);
			    
			   HttpManager request = new HttpManager();
			   
			   String url = deliveryConfig.getTestUrl(); 
			   if(url == null) {
			    	msg = "The TestUrl does not setup.";
			    	api = new ApiResponse<HashMap<String, String>>();
			    	response = api.getObject(Response.class);
				    response.setDeveloperMessage(errMsg);
				    response.setResponseMessage("Failed.");
				    api.setResponse(response);
				    api.setMethodName(methodName);
				    api.setContent(getRequestData(mappings));
				    logger.info(MessageUtil.errorMessage(type, quoteId, url, "The TestUrl does not setup.", "The TestUrl does not setup."));
				    return new ResponseEntity<ApiResponse<HashMap<String, String>>>(api, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
			    }
			   
			   StringBuffer paramsLog = new StringBuffer();
				for (Map.Entry<String, String> entry : requestInfo.getData().entrySet()) {
					paramsLog.append(entry.getKey() + "=" + entry.getValue() + "&");
				}
				if (paramsLog.length() > 0) {
					logger.debug(paramsLog.toString().substring(0, paramsLog.length() - 1));
				} else {
					logger.debug(MessageUtil.errorMessage(type, quoteId, url, "No parameters found!", "No parameters found!"));
				}
			   
			   response = request.makeHttpPostRequest(url,requestInfo.getData());
			   
			   if(response.getResponseCode() == 200 || response.getResponseCode() == 201 || response.getResponseCode() == 202) {
				   response.setDeveloperMessage(msg);
				   api.setResponse(response);
				   api.setMethodName(methodName);
				   api.setContent(getRequestData(mappings));
				   if(logger.isDebugEnabled())
					   logger.debug(MessageUtil.successMessage(type, quoteId, response.getResponseCode(), url, response.getResponseMessage(), paramsLog.toString().substring(0, paramsLog.length() - 1)));
				   else
					   logger.info(MessageUtil.successMessage(type, quoteId, response.getResponseCode(), url, null, paramsLog.toString().substring(0, paramsLog.length() - 1)));
				   return new ResponseEntity<ApiResponse<HashMap<String, String>>>(api, org.springframework.http.HttpStatus.OK);
			   } else {
				   response.setDeveloperMessage(errMsg);
				   //response.setResponseMessage(errMsg);
				   api.setResponse(response);
				   api.setMethodName(methodName);
				   api.setContent(getRequestData(mappings));
				   if(logger.isDebugEnabled())
					   logger.debug(MessageUtil.failedMessage(type, quoteId, response.getResponseCode(), url, response.getResponseMessage(), paramsLog.toString().substring(0, paramsLog.length() - 1)));
				   else
					   logger.error(MessageUtil.failedMessage(type, quoteId, response.getResponseCode(), url, null, paramsLog.toString().substring(0, paramsLog.length() - 1)));
				   return new ResponseEntity<ApiResponse<HashMap<String, String>>>(api, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
			   }
			 
		    }
		    else {
		    	isTest = true;
		    	List<LeadPreference> extraData = null;
		    	info = supplierCategoryService.getSupplierCategoryInfo(supplierId);
		    	List<QuestionWithAnswer> answersOptions = registrationService.getAnswerOptions(info.getQuestionSetId());
		    	
		    	answersOptions = removeHiddenQuestion(info, answersOptions);
				
			    deliveryConfig = registrationService.getLeadPreferences(supplierId);
			    
			    if(version == 0) {
			    	newVersion = registrationService.getVersionByLive(deliveryConfig.getPreferenceId());
			    	if(newVersion == null || newVersion.isEmpty())
			    		newVersion = String.valueOf(customLeadService.getLatestVersion(deliveryConfig.getPreferenceId())); 
			    }
			    else 
			    	newVersion = String.valueOf(version);
			    mappings = registrationService.getTestLeadMappings(deliveryConfig.getPreferenceId(), Integer.parseInt(newVersion));	
			    for(LeadPreference lp : mappings) {
			    	if(lp.getMappingType() == 5) {
			    		extraData = registrationService.getExtraData();
			    		if(extraData != null) {
			    			mappings.addAll(extraData);
			    			break;
			    		}
			    	}
			    }
			    questions = new HashSet<String>();
			    for(QuestionWithAnswer option : answersOptions) {
			    	questions.add(option.getQuestionId());
			    	if(option.getAnswer()==null || option.getAnswer().isEmpty()) {
			    		option.setAnswer("NULL");
			    	}
			    }
			    
			    String[] fieldsAndData = getTestFieldsandData(mappings);
			    for(String field : fieldsAndData) {
			    	String[] str = field.split("=");
			    	data.put(str[0], str[1]);
			    }
			    allAnswers = new ArrayList<QuestionWithAnswer>();
			    for(Map.Entry<String, String> dataEntry : data.entrySet()) {
			    	for(QuestionWithAnswer answersOptionsMapping : answersOptions) {
			    			if(dataEntry.getValue().equals(String.valueOf(answersOptionsMapping.getQuestionId()))) {
			    				allAnswers.add(answersOptionsMapping);
			    		}
			    	}
			    }
			    replaceData(allAnswers, data, isTest);
			    
			    LeadRequest requestInfo = new LeadRequest();
			    List<QuestionWithAnswer> questionAndAnswers = new ArrayList<QuestionWithAnswer>();
			   
			    for(String q : questions) {
			    	for(QuestionWithAnswer qwa : answersOptions) {
			    		if(qwa.getQuestionId() == q) {
			    			questionAndAnswers.add(qwa);
			    			break;
			    		}
			    	}
			    }
			    
			    for(LeadPreference leadPreference : mappings) {
			    	
			    	if(leadPreference.getMappingType() == 5) {
			    		StringBuilder sb = new StringBuilder();
		    			sb.append("BuyerZone Quote_id: " + "; ");
		    			sb.append("Industry: " + data.get("company_industry") + "; ");
		    			sb.append("Employees: " + data.get("company_size_id") + "; ");
		    			sb.append(getAllQuestionsAndAnswers(questionAndAnswers, leadPreference.getQuestionSeparator(), leadPreference.getQuestionSetSeparator(), isTest));
		    			data.put("description", sb.toString());
			    	}
			    	
			    	if(leadPreference.getMappingType() == 4) {
			    		leadPreference.setValue(getAllQuestionsAndAnswers(questionAndAnswers, leadPreference.getQuestionSeparator(), leadPreference.getQuestionSetSeparator(), isTest));
			    		for(Map.Entry<String, String> dataEntry : data.entrySet()) {
			    			if(leadPreference.getKey().equals(dataEntry.getKey())) {
			    				dataEntry.setValue(leadPreference.getValue());
			    			}
			    		}	
			    	}
	    			if(leadPreference.getTranslationId() > 0) {
			    		for(Map.Entry<String, String> dataEntry : data.entrySet()) {
			    			if(leadPreference.getValueToTranslate().equals(dataEntry.getValue())) {
			    				dataEntry.setValue(leadPreference.getTranslatedValue());
			    			}
			    		}		    			
			    	}
			    }
			    
			    for(LeadPreference lp : mappings) {
			    	if(lp.getCharLimit() != -1 && (lp.getMappingType() == 3 || lp.getMappingType() == 1))
			    		limitCharLength(lp.getCharLimit(), lp.getKey(), data);
			    }
			    
			    requestInfo.setData(data);
			    requestInfo.setLeadPreference(deliveryConfig);
			    
			    HttpManager request = new HttpManager();
			    String url = deliveryConfig.getTestUrl(); 
			    
			    if(url == null) {
			    	msg = "The TestUrl does not setup.";
			    	api = new ApiResponse<HashMap<String, String>>();
			    	response = api.getObject(Response.class);
				    response.setDeveloperMessage(errMsg);
				    response.setResponseMessage("Failed.");
				    api.setResponse(response);
				    api.setMethodName(methodName);
				    api.setContent(data);
				    logger.info(MessageUtil.errorMessage(type, quoteId, url, "The TestUrl does not setup.", "The TestUrl does not setup."));
				    return new ResponseEntity<ApiResponse<HashMap<String, String>>>(api, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
			    }
			    
			    StringBuffer paramsLog = new StringBuffer();
				for (Map.Entry<String, String> entry : requestInfo.getData().entrySet()) {
					paramsLog.append(entry.getKey() + "=" + entry.getValue() + "&");
				}
				if (paramsLog.length() > 0) {
					logger.debug(paramsLog.toString().substring(0, paramsLog.length() - 1));
				} else {
					logger.debug(MessageUtil.errorMessage(type, quoteId, url, "No parameters found!", "No parameters found!"));
				}
			    
			    api = new ApiResponse<HashMap<String, String>>();
			    response = request.makeHttpPostRequest(url,requestInfo.getData());
			    
			    if(response.getResponseCode() == 200 || response.getResponseCode() == 201 || response.getResponseCode() == 202) {
				    response.setDeveloperMessage(msg);
				    api.setResponse(response);
				    api.setMethodName(methodName);
				    api.setContent(data);
				    if(logger.isDebugEnabled())
				    	logger.debug(MessageUtil.successMessage(type, quoteId, response.getResponseCode(), url, response.getResponseMessage(), paramsLog.toString().substring(0, paramsLog.length() - 1)));
				    else
				    	logger.info(MessageUtil.successMessage(type, quoteId, response.getResponseCode(), url, null, paramsLog.toString().substring(0, paramsLog.length() - 1)));
				    return new ResponseEntity<ApiResponse<HashMap<String, String>>>(api, org.springframework.http.HttpStatus.OK);
			    
			    } else {
			    	response.setDeveloperMessage(errMsg);
					api.setResponse(response);
					api.setMethodName(methodName);
					api.setContent(data);
					if(logger.isDebugEnabled())
						logger.debug(MessageUtil.failedMessage(type, quoteId, response.getResponseCode(), url, response.getResponseMessage(), paramsLog.toString().substring(0, paramsLog.length() - 1)));
					else
						logger.error(MessageUtil.failedMessage(type, quoteId, response.getResponseCode(), url, null, paramsLog.toString().substring(0, paramsLog.length() - 1)));
					return new ResponseEntity<ApiResponse<HashMap<String, String>>>(api, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
			    }
		    }
		   
    	} catch (BeansException e) {
    		logger.error(String.format("Internal Server Error [%s]", e));
			response = new Response();
			response.setDeveloperMessage(e.getMessage());
			response.setResponseMessage(errMsg);
			api.setResponse(response);
			api.setMethodName(methodName);
			return new ResponseEntity<ApiResponse<HashMap<String, String>>>(api, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
			
		} catch (IllegalStateException e) {
			logger.error(String.format("Internal Server Error [%s]", e));
			response = new Response();
			response.setDeveloperMessage(e.getMessage());
			response.setResponseMessage(errMsg);
			api.setResponse(response);
			api.setMethodName(methodName);
			return new ResponseEntity<ApiResponse<HashMap<String, String>>>(api, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
		} catch(Exception e) {
			logger.error(String.format("Internal Server Error [%s]", e));
			response = new Response();
			response.setDeveloperMessage(e.getMessage());
			response.setResponseMessage(errMsg);
			api.setResponse(response);
			api.setMethodName(methodName);
			return new ResponseEntity<ApiResponse<HashMap<String, String>>>(api, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
    
    private List<LeadPreference> setParamValues(List<LeadPreference> settings, Map<String, String> data, int quoteId, List<QuestionWithAnswer> questionWithAnswers, List<HashMap<String,String>> answers){
		List<LeadPreference> removeList = new ArrayList<LeadPreference>();
    	
    	for(LeadPreference pref : settings){
    		
    		if(pref.getMappingType() == 3) {
    			if(data.containsValue(pref.getValue()))
    				pref.setValue("");
    		}
    		
    		//If mapping type set to 5 then set it to 3 reg fields + all questions and answers string
    		if(pref.getMappingType() == 5) {
    			StringBuilder sb = new StringBuilder();
    			sb.append("BuyerZone Quote_id: " + String.valueOf(quoteId) + "; ");
    			sb.append("Industry: " + data.get("company_industry") + "; ");
    			sb.append("Employees: " + data.get("company_size_id") + "; ");
    			sb.append(getAllQuestionsAndAnswers(questionWithAnswers, pref.getQuestionSeparator(), pref.getQuestionSetSeparator(), false));
    			pref.setValue(sb.toString());
    		}
    		
			//If mapping type set to question-all (id 4) then set it to all questions and answers string
			if(pref.getMappingType() == 4) {
				pref.setValue(getAllQuestionsAndAnswers(questionWithAnswers, pref.getQuestionSeparator(), pref.getQuestionSetSeparator(), false));
				
				//Replaced the mapping type 4 with translated value.
				for(LeadPreference preference : settings) {
					for(QuestionWithAnswer qwa : questionWithAnswers) {
						if(qwa.getQuestionId().equals(preference.getValue())) {
							String orginialValue = pref.getValue();
							String containsValue = qwa.getQuestion() + pref.getQuestionSeparator() + preference.getValueToTranslate();
							String translatedValue = qwa.getQuestion() + pref.getQuestionSeparator() + preference.getTranslatedValue();
							if(preference.getTranslationId() > 0) {
								if(preference.getMappingType() == 3) {
									if(orginialValue.contains(containsValue)) {
										orginialValue = orginialValue.replace(containsValue, translatedValue);
										pref.setValue(orginialValue);
									}
								}
							}
						}
					}
				}
			}
				
    		for(String key : data.keySet()) {	
    			if(key.equals(pref.getValue())) {
	    			String currentKey = String.valueOf(data.get(pref.getValue()));
	    			String[] array = currentKey.split("\\|");
	    			String currentValue = String.valueOf(pref.getValue());
	    			
	    			pref.setValue(currentKey);

	    			// Set translated value otherwise remove if no match found.
	    			if(pref.getTranslationId() > 0){
	    				if(pref.getMappingType() == 3) {

	    					//Very important! Make sure value in preference is the value in array.
	    					if(Arrays.asList(array).contains(pref.getValueToTranslate())) {
	    						String translatedValue = pref.getTranslatedValue().trim();
	    						if(translatedValue.length() != 0)
	    							pref.setValue((currentValue.replace(currentValue, pref.getTranslatedValue())));
	    						else
	    							pref.setValue(currentValue.replace(currentValue, pref.getValueToTranslate()));
		        			}
	    					else if(currentKey.contains(pref.getValueToTranslate()) && currentKey.contains("Other")) {
	    						String translatedValue = pref.getTranslatedValue().trim();
	    						if(translatedValue.length() != 0)
	    							pref.setValue((currentValue.replace(currentValue, pref.getTranslatedValue())));
	    						else {
	    							for(String str : array) {
	    								if(str.contains("Other")) {
	    									pref.setValue(currentValue.replace(currentValue, str));
	    								}
	    							}
	    						}
	    					} 
		    				else
		        				removeList.add(pref);
	    				}
	    				else {
		    				if (currentKey.equals(pref.getValueToTranslate())){
		    					pref.setValue((currentValue.replace(currentValue, pref.getTranslatedValue())));
		    				}
		    				else
		    					removeList.add(pref);
	    				}
	    			}
    			}
    		}
    	}
    	
    	settings.removeAll(removeList);

    	return settings;

    }
    
    private void swap(Object key1, Object key2, Map map) {
    	 Object temp = map.get(key1);
	     map.put(key1, map.get(key2));
	     map.put(key2, temp);
    }
    
    
    private void replaceQuoteId(int quoteId, HashMap<String, String> data) {
    	for(Map.Entry<String, String> entry : data.entrySet()) {
    		if(entry.getKey().contains("{quoteId}"))
    			entry.setValue(String.valueOf(quoteId));
    	}
    }
    
    private void limitCharLength(int charLimit, String key, HashMap<String, String> data) {
    	String value = data.get(key);
    	if(value != null && value.length() > charLimit) {
	    	value = value.substring(0, charLimit);
	    	data.put(key, value);
    	}
    }
    
    private List<QuestionWithAnswer> removeHiddenQuestion(CategoryDefinition info, List<QuestionWithAnswer> questionWithAnswers) {
    	String getUrl = baseURL + "/questionset/hidden/" + info.getCategoryId(); 
    	
    	try {
		    HttpManager getRequest = new HttpManager();
			String response = getRequest.getData(getUrl);
	    	
			List<QuestionWithAnswer> hiddenQuestions = new ArrayList<QuestionWithAnswer>();
	    	if(response != null) {
				String[] hiddenQuestion = response.replace("[", "").replace("]", "").split(",");
				for(String question : hiddenQuestion) {
					for(QuestionWithAnswer answer : questionWithAnswers) {
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
    	} catch(Exception ex) {
    		logger.error(ex);
    		return questionWithAnswers;
    	}
    	
    }
    
}
