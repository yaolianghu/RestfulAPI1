package com.buyerzone.controllers;

import java.sql.SQLException;
import org.springframework.jdbc.BadSqlGrammarException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.buyerzone.model.econnect.ECategory;
import com.buyerzone.model.econnect.EConnectSettings;
import com.buyerzone.model.econnect.ESupplier;
import com.buyerzone.service.EConnectSettingsService;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;

@RestController
public class EConnectController {
	
	Logger logger = Logger.getLogger(EConnectController.class);
	
	@Autowired
	private EConnectSettingsService eConnectService;
	
	@RequestMapping(method=RequestMethod.GET, value="econnect/findsupplierbyname")
	public Object findSupplierByName(@RequestParam(value="suppliername", required = true) String supplierName){
		
		try{
	    	logger.info("Find supplier by name: " + supplierName);
	    	List<Map<String,Long>> suppliers = eConnectService.findSupplierByName(supplierName);
	    	if(suppliers == null || suppliers.size() == 0){
	    		HashMap<String,Object> output = new HashMap<>();
				//output.put("No results found", String.format( "No data related to supplierName = [%s]", supplierName));
	    		logger.warn(String.format("No results found for supplierName [%s]", supplierName));
				return new ResponseEntity<HashMap<String, Object>>(output, HttpStatus.OK); 
			}
		    return suppliers;
		 }catch(BadSqlGrammarException sqle){
		    logger.error(sqle.getStackTrace());
		    logger.error(sqle.getCause());
		    HashMap<String,Object> output = new HashMap<>();
		    String errorMessage = "An unexpected error occurred while searching for supplier by name";
			output.put(errorMessage,supplierName);
    		logger.warn(errorMessage);
			return new ResponseEntity<HashMap<String, Object>>(output, HttpStatus.INTERNAL_SERVER_ERROR); 
		}catch(Exception e){
	    	logger.error(e.getStackTrace());
	    	logger.error(e.getCause());
		    HashMap<String,Object> output = new HashMap<>();
		    String errorMessage = String.format( "An unexpected error occurred while searching for supplier name [%s]", supplierName);
			output.put(errorMessage,e.getStackTrace());
    		logger.warn(errorMessage);
			return new ResponseEntity<HashMap<String, Object>>(output, HttpStatus.INTERNAL_SERVER_ERROR); 
	    }
	}
	
	@RequestMapping(value = "econnect/getallsuppliercategories/{supplierid}", method=RequestMethod.GET)
	public Object getSupplierCategoriesBySupplierId(@PathVariable(value="supplierid") int supplierid ){
		try{
	    	logger.info(String.format("Retrieving all supplier categories for supplierid [%s]",supplierid));
	    	ESupplier supplier = eConnectService.findSupplierById(supplierid);
	    	if(supplier == null){
	    		HashMap<String,Object> output = new HashMap<>();
	    		logger.warn(String.format("No results found for supplierid [%s]", supplierid));
				return new ResponseEntity<HashMap<String, Object>>(output, HttpStatus.OK); 
			}	
		    return supplier;
		}catch(Exception e){
	    	logger.error(e.getStackTrace());
	    	logger.error(e.getCause());
    		HashMap<String,Object> output = new HashMap<>();
    		String errorMessage = String.format( "An unexpected error occured while retrieving data for supplierId = [%s]", supplierid);
	    	output.put("Internal Server Error", errorMessage);
    		logger.warn(errorMessage);
			return new ResponseEntity<HashMap<String, Object>>(output, HttpStatus.INTERNAL_SERVER_ERROR); 
	    }
	}
	
	@RequestMapping(value = "/econnect/getallclients", method=RequestMethod.GET)
    public ResponseEntity getAllEConnectClients(){
		try{
	    	logger.info("Retrieving Email Connect settings for all suppliers");
	    	List<EConnectSettings> eConnectClients = eConnectService.getEConnectClients();
		    return new ResponseEntity(eConnectClients,HttpStatus.OK);
	    }catch(BadSqlGrammarException sqle){
	    	logger.error(sqle.getStackTrace());
	    	logger.error(sqle.getCause());
		    HashMap<String,Object> output = new HashMap<>();
		    String errorMessage = "An unexpected error occurred while retrieving data for all clients";
			output.put("Internal Server Error", errorMessage);
    		logger.warn(errorMessage);
			return new ResponseEntity<HashMap<String, Object>>(output, HttpStatus.INTERNAL_SERVER_ERROR); 
	    }catch(Exception e){
	    	logger.error(e.getStackTrace());
	    	logger.error(e.getCause());
	    	HashMap<String,Object> output = new HashMap<>();
			String errorMessage = "An unexpected error occurred while retrieving data for all clients";
			output.put("Internal Server Error", errorMessage);
	    	logger.warn(errorMessage);
			return new ResponseEntity<HashMap<String, Object>>(output, HttpStatus.INTERNAL_SERVER_ERROR); 
	    }
	}
	
	@RequestMapping(value = "/econnect/details/{supplierid}", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getEmailLeadClientDetails(@PathVariable int supplierid){
		EConnectSettings data;
		
		logger.info(String.format("Retrieving Email Connect data for supplierid: %s", supplierid));
		try{
			data = eConnectService.getEConnectSettingsBySupplierId(supplierid);
			
			if(data == null){
				HashMap<String,Object> output = new HashMap<>();
				String errorMessage = String.format("No results found for supplierId [%s]", supplierid);
				//output.put("Accepted", errorMessage);
		    	logger.warn(errorMessage);
				return new ResponseEntity<HashMap<String, Object>>(output, HttpStatus.OK);
			}	
		}catch(Exception e){
			data = null;
			String errorMessage = String.format("Error while retrieving EConnect Settings for supplierid [%s]", supplierid);
			logger.error(errorMessage);
			HashMap<String,Object> output = new HashMap<>();
			output.put("Internal Server Error", errorMessage);
			return new ResponseEntity<HashMap<String, Object>>(output, HttpStatus.INTERNAL_SERVER_ERROR);
		}
    
        return new ResponseEntity(data, HttpStatus.OK);

    }
	
	@RequestMapping(value = "/econnect/insert", method=RequestMethod.PUT, consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, Object>> insertEmailLeadClient(@RequestBody EConnectSettings eConnectSettings){

		String requiredParamError = "The following field is required: %s";
		int supplierCategoryId = eConnectSettings.getSupplierCategoryId();
		int categoryId = eConnectSettings.getCategoryId();
		int feePerLead = eConnectSettings.getFeePerLead();
		String categoryName = eConnectSettings.getCategoryName();
        HashMap<String,Object> output = new HashMap<>();
        try
		{
			if(supplierCategoryId == 0){
                output.put("Error",String.format(requiredParamError, "supplierCategoryId"));
				return new ResponseEntity<HashMap<String, Object>>(output,HttpStatus.BAD_REQUEST);
			}
			if(categoryId == 0){
                output.put("Error",String.format(requiredParamError, "categoryId"));
                return new ResponseEntity<HashMap<String, Object>>(output,HttpStatus.BAD_REQUEST);
			}
			if(feePerLead == 0){
                output.put("Error",String.format(requiredParamError, "feePerLead"));
                return new ResponseEntity<HashMap<String, Object>>(output,HttpStatus.BAD_REQUEST);
			}
			if(categoryName == null){
                output.put("Error",String.format(requiredParamError, "categoryName"));
                return new ResponseEntity<HashMap<String, Object>>(output,HttpStatus.BAD_REQUEST);
			}
		
		    String msg = "";
		    if(eConnectService.getEConnectClientId(eConnectSettings.getSupplierCategoryId()) != null){
		    	msg = "Email leads client with supplierCategoryId [%s] already exists";
				output.put("Insert failed",String.format(msg,eConnectSettings.getSupplierCategoryId()));
				return new ResponseEntity<HashMap<String, Object>>(output, HttpStatus.CONFLICT); 
		    }
		    else{
		    	logger.info(String.format("Proceed to insert new email leads client data."));
		    	int result = eConnectService.insertEConnectSetting(eConnectSettings);
		    
			    if(result > 0){
			    	msg = String.format("Successfully inserted new email leads client: ");
					output.put("Insert successful",String.format(msg,eConnectSettings.getSupplierCategoryId()));
					return new ResponseEntity<HashMap<String, Object>>(output, HttpStatus.OK); 
			    }
		    }

		} catch (Exception e) {
			String errMsg = String.format("Failed to insert new email leads client.");
			logger.error(errMsg + "." + e.getMessage(), e);
			output.put("Failed to insert new supplier with id ",eConnectSettings.getSupplierCategoryId());
			output.put("Error reason: ", e.getMessage());
			return new ResponseEntity<HashMap<String, Object>>(output, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return null;
	}
		
	@RequestMapping(value = "/econnect/update", method=RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
	public ResponseEntity<HashMap<String, Object>> updateEmailLeadClient(@Valid @RequestBody EConnectSettings eConnectSettings){
		
		 String errMsg = null;
        ResponseEntity<HashMap<String, Object>> responseEntity = null;
        HashMap<String,Object> output = new HashMap<>();
		 try {
             int supplierCategoryId = eConnectSettings.getSupplierCategoryId();
             if (supplierCategoryId == 0) {
                 output.put("Failed to update supplier", "Invalid value for supplierCategoryId");
                 return new ResponseEntity<HashMap<String, Object>>(output, HttpStatus.BAD_REQUEST);
             }
             Integer econnectId = eConnectService.getEConnectClientId(supplierCategoryId);
             if (econnectId == null) {
                 errMsg = String.format("There are no records for supplierId [%s]. Cannot update record.", supplierCategoryId);
                 output.put("Failed to update supplier", errMsg);
                 return new ResponseEntity<HashMap<String, Object>>(output, HttpStatus.BAD_REQUEST);
             }
             eConnectSettings.setEmailLeadsClientId(econnectId);

             logger.info(String.format("Updating email leads client data."));

             int result = eConnectService.updateEConnectSetting(eConnectSettings);
             String msg = "";
             if (result > 0) {
                 msg = String.format("Succesfully updated email leads client [%s] with supplier category id [%s] .", econnectId, supplierCategoryId);
             } else {
                 msg = String.format("No changes were applied to email leads client [%s] with supplier category id [%s] ", econnectId, supplierCategoryId);
             }

             output.put("Result", msg);
             responseEntity = new ResponseEntity<HashMap<String, Object>>(output, HttpStatus.OK);
         }catch(BadSqlGrammarException e){
             logger.error(e.getStackTrace());
             logger.error(e.getCause());
             output.put("Failed to update supplier.",e.getMessage());
             output.put("Possible cause", "bad parameter name");
             return new ResponseEntity<HashMap<String, Object>>(output, HttpStatus.BAD_REQUEST);
         }catch (Exception e) {
		 	logger.error(e.getStackTrace());
		 	logger.error(e.getCause());
		 	output.put("Failed to update supplier.",e.getMessage());
		 	return new ResponseEntity<HashMap<String, Object>>(output, HttpStatus.INTERNAL_SERVER_ERROR); 
		 }
        return responseEntity;
	}
	@RequestMapping(value = "/econnect/category/{categoryId}", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getCategoryById(@PathVariable long categoryId){
		ECategory category;
		
		logger.info(String.format("Retrieving Email Connect data for supplierid: %s", categoryId));
		try{
			category = eConnectService.getCategoryById(categoryId);
			
			if(category == null){
				HashMap<String,Object> output = new HashMap<>();
				output.put("Internal server error: ",String.format("No results found for categoryId [%s]", categoryId));
				return new ResponseEntity<HashMap<String, Object>>(output, HttpStatus.INTERNAL_SERVER_ERROR); 

			}	
		}catch(Exception e){
			category = null;
			logger.error(String.format("Error while retrieving EConnect Settings for categoryId [%s]", categoryId));
		}
    
    	return new ResponseEntity(category,HttpStatus.OK);
	}
	
}
