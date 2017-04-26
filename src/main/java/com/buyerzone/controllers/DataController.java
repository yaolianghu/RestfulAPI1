package com.buyerzone.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.buyerzone.util.report.FileHelper;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.buyerzone.model.Response;
import com.buyerzone.service.GlobalService;
import com.buyerzone.util.ApiResponse;

@RestController
public class DataController {
	Logger logger = Logger.getLogger(LeadController.class);
	
	@Autowired
	private GlobalService dataService;
	
    @RequestMapping("/data")
    public ResponseEntity getData(@RequestParam(value="proc") String procName, @RequestParam(value="params", defaultValue="") String params, @RequestParam(value="format", defaultValue="") String format){
    	ResponseEntity response = null;
    	List<HashMap<String, String>> data = null;
    	String[] parameters = null;
    	try {
    		data = new ArrayList<>();
    		if(procName.contains("dz_get")) {
	    		if (!params.isEmpty()) {
					parameters = org.springframework.util.StringUtils.commaDelimitedListToStringArray(params);
					data = dataService.getData(procName, parameters);
					if (!data.isEmpty()) {
						if (format.equals("csv"))
							response = createResponseEntity(data, String.format("%s_report.csv", procName));
						else
						    response = ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(data);
					}else{
						response = ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(data);
					}
				}
	    		else {
					data = dataService.getListDataNoParams(procName);
					if(!data.isEmpty()){
						if(format.equals("csv"))
							response = createResponseEntity(data, String.format("%s_report.csv",procName));
						else 
							response = ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(data);
					}else{
						response = ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(data);
					}
	    		}
    		}
		} catch (IOException | BeansException | IllegalStateException e) {
			logger.error("Error", e);
			response = ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(e);
		}
    	
		return response;
	    
    }

    private ResponseEntity createResponseEntity(List<HashMap<String, String>> data, String fileName) throws IOException{
		byte [] reportData = FileHelper.generateCsvContents(data).toByteArray();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", String.format("filename=%s", fileName));
		return ResponseEntity.ok().headers(headers).contentLength(reportData.length).contentType(MediaType.parseMediaType("text/csv")).body(new InputStreamResource(new ByteArrayInputStream(reportData)));

	}
    
    @RequestMapping(value = "/data/post", method=RequestMethod.POST, headers="Accept=*")
    public ApiResponse<List<HashMap<String, String>>> postData(@RequestParam(value="proc") String procName, @RequestParam(value="params", defaultValue="") String params) {
    	String msg = String.format("Successfully post data.");
    	String methodName = "postData";
    	List<HashMap<String, String>> data = null; 
        String[] parameters = null;
        Response response = null;
        ApiResponse<List<HashMap<String, String>>> api = null;
        
    	try {
    		
    		if (!params.isEmpty()){
    			parameters = org.springframework.util.StringUtils.commaDelimitedListToStringArray(params);
    		}
    		
		    data = dataService.cloneMapping(procName, parameters);
		    api = new ApiResponse<List<HashMap<String, String>>>();
		    response = new Response();
		    response.setDeveloperMessage(msg);
	    	response.setResponseCode(HttpStatus.SC_OK);
	    	response.setResponseMessage(msg);
		    api.setResponse(response);
		    api.setMethodName(methodName);
		    logger.info(msg);
		   
		    return api;
    		    
    		   
    		} catch (BeansException e) {
    			api = new ApiResponse<List<HashMap<String, String>>>();
    			response = new Response();
    			response.setResponseMessage("Failed.");
    			response.setResponseCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    			response.setDeveloperMessage(e.getMessage());
    		    api.setResponse(response);
    		    api.setMethodName(methodName);
    			logger.error("Error", e);
    			
    			return api;
    			
    		} catch (IllegalStateException e) {
    			api = new ApiResponse<List<HashMap<String, String>>>();
    			response = new Response();
    			response.setResponseMessage("Failed.");
    			response.setResponseCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    			response.setDeveloperMessage(e.getMessage());
    		    api.setResponse(response);
    		    api.setMethodName(methodName);
    			logger.error("Error", e);
    			
    			return api;
    		}
    }
    

}
