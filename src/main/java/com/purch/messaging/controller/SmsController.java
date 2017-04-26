package com.purch.messaging.controller;

import com.purch.messaging.dao.mapper.SmsMapper;
import com.purch.messaging.model.ApiResponseWrapper;
import com.purch.messaging.model.Hello;
import com.purch.messaging.service.sms.SmsOooberService;
import org.apache.catalina.util.URLEncoder;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/sms/{keyword}")
public class SmsController {
    private static final String helloTemplate = "Hello, %s! You are using the %s SMS API \\o/";
    
    @Autowired
    private SmsOooberService smsOooberService;
    
    @Autowired
    private SmsMapper smsMapper;

	@Value("${mds.url}")
	public String mdsUrl;


	private Logger logger = Logger.getLogger(SmsController.class);

    @RequestMapping("/hello")
    public ResponseEntity<ApiResponseWrapper<Hello>> hello(
    		@PathVariable String keyword, 
    		@RequestParam(value="name", defaultValue="SMS Client") String name) {
    	
        ApiResponseWrapper<Hello> responseWrapper = new ApiResponseWrapper<Hello>();
        responseWrapper.complete(HttpStatus.OK, new Hello(String.format(helloTemplate, name, keyword.toUpperCase())));

        return ResponseEntity.ok(responseWrapper);
    }

    @RequestMapping(value = "/send/{type}/{mobileNumber}", produces = "application/json")
    public String sendSmsMessage(
    		@PathVariable String keyword,
    		@PathVariable String type,
    		@PathVariable long mobileNumber,
    		@RequestParam(value="text", required = false) String text) {

		String mdsRequestUrl = String.format("%s/sms/%s/send/%s/%d", mdsUrl, keyword, type, mobileNumber);
		logger.info("Request proxied to : " + mdsRequestUrl);

		HttpResponse res;
		String response = null;
		try{
			res = Request.Post(mdsRequestUrl).bodyForm(Form.form().add("text", text).build()).execute().returnResponse();
			response = EntityUtils.toString(res.getEntity());
		}
		catch (Exception e){
			logger.error("Error in MDS proxy request");
			logger.error(e);
		}

		return response;
    }

    @RequestMapping(value = "/status/{mobileNumber}", produces = "application/json")
    public String optInStatus(
    		@PathVariable String keyword,
    		@PathVariable long mobileNumber) {

		String mdsRequestUrl = String.format("%s/sms/%s/status/%d", mdsUrl, keyword, mobileNumber);
		logger.info("Request proxied to : " + mdsRequestUrl);

		HttpResponse res;
		String response = null;
		try{
			res = Request.Get(mdsRequestUrl).execute().returnResponse();
			response = EntityUtils.toString(res.getEntity());
		}
		catch (Exception e){
			logger.error("Error in MDS proxy request");
			logger.error(e);
		}

		return response;
    }
    
    @RequestMapping(value = "/activity/{mobileNumber}", produces = "application/json")
    public String activity(
    		@PathVariable String keyword,
    		@PathVariable long mobileNumber) {

		String mdsRequestUrl = String.format("%s/sms/%s/activity/%d", mdsUrl, keyword, mobileNumber);
		logger.info("Request proxied to : " + mdsRequestUrl);

		HttpResponse res;
		String response = null;
		try{
			res = Request.Get(mdsRequestUrl).execute().returnResponse();
			response = EntityUtils.toString(res.getEntity());
		}
		catch (Exception e){
			logger.error("Error in MDS proxy request");
			logger.error(e);
		}

		return response;
    }

    @RequestMapping(value = "/receive", produces = "application/json")
    public String replySmsMessage(
    		@PathVariable String keyword,
    		@RequestParam(value="who") long mobileNumber,
    		@RequestParam(value="what") String text,
    		@RequestParam(value="time") long timestamp) {

		String mdsRequestUrl = String.format("%s/sms/%s/receive?who=%s&what=%s&time=%d", mdsUrl, keyword, mobileNumber, text, timestamp);
		logger.info("Request proxied to : " + mdsRequestUrl);

		HttpResponse res;
		String response = null;
		try{
			res = Request.Get(mdsRequestUrl).execute().returnResponse();
			response = EntityUtils.toString(res.getEntity());
		}
		catch (Exception e){
			logger.error("Error in MDS proxy request");
			logger.error(e);
		}

    	return response;
    }

	@RequestMapping(value = "/delivery", produces = "application/json")
	public String deliveryReceipt(@PathVariable String keyword,
								  @RequestParam(value="id") long id,
								  @RequestParam(value="status") int status){
		String mdsRequestUrl = String.format("%s/sms/%s/delivery?id=%d&status=%d", mdsUrl, keyword, id, status);
		logger.info("Request proxied to : " + mdsRequestUrl);
		HttpResponse res;
		String response = null;
		try{
			res = Request.Get(mdsRequestUrl).execute().returnResponse();
			response = EntityUtils.toString(res.getEntity());
		}
		catch (Exception e){
			logger.error("Error in MDS proxy request");
			logger.error(e);
		}

		return response;
	}

}
