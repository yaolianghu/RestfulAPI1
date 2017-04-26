package com.purch.messaging.service.sms;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.purch.messaging.dao.mapper.SmsMapper;
import com.purch.messaging.model.SmsMessage;
import com.purch.messaging.model.SmsOptIn;

@Service
public class SmsOooberService {
	// move this to properties or DB configuration
	private static final String msgSuffix = "Standard msg & data rates may apply.\nReply BZHELP for info.\nReply BZSTOP to end.";
	public static final Map<String, String> optInMessages;
	public static final Set<String> messageTypes;
	public static final Map<String, String> keywords;
	public static final Map<Long, String> failedSendMsgs;

	// message types
	public static final String OPT1 = "opt1";
	public static final String OPT2 = "opt2";
	public static final String HELP = "help";
	public static final String STOP = "stop";
	public static final String TEXT = "text";
	
	// message origin
	public static final String OUTGOING = "BZ";
	public static final String INCOMING = "MO";
	
	// failed send message id
	public static final long MSG_ID_UNKNOWN = 0;
	public static final long MSG_ID_NOT_OPTED_IN = -1;
	public static final long MSG_ID_FAILED_SEND = -2;
	
    // ooober API details
	private static final String username = "bzone";
	private static final String password = "Bz0ne";
	private static final String shaParam = "username=%s&password=%s&timestamp=%s";
	private static final String oooberURI = "http://104.238.76.84/api/index.php";
	private static final String oooberAPI = oooberURI + "?username=%s&timestamp=%s&signature=%s&to=%s&message=%s";

	
	static {
		optInMessages = new HashMap<String, String>();
		optInMessages.put(OPT1,
				"Do you agree to receive lead notifications from BZONE? Reply BZYES.\n"
                + "You will receive recurring messages with lead info up to your cap.\n"
			    + msgSuffix);
		optInMessages.put(OPT2,
				"Thank you - you are all set to receive recurring text messages from BuyerZone. You will receive leads up to your cap.\n"
				+ msgSuffix);
		optInMessages.put(HELP,
				"Please call us at 888-393-5000 and we will be glad to help.\n"
				+ msgSuffix);
		optInMessages.put(STOP,
				"Thank you - you will no longer receive leads via text message from BuyerZone.");

		messageTypes = new HashSet<String>();
		messageTypes.addAll(Arrays.asList(OPT1, OPT2, HELP, STOP, TEXT));
		
		keywords = new HashMap<String, String>();
		keywords.put(OPT1, "BZONE");
		keywords.put(OPT2, "BZYES");
		keywords.put(HELP, "BZHELP");
		keywords.put(STOP, "BZSTOP");
		
		failedSendMsgs = new HashMap<Long, String>();
		failedSendMsgs.put(MSG_ID_UNKNOWN, "message handoff status unknown");
		failedSendMsgs.put(MSG_ID_NOT_OPTED_IN, "mobile number has not opted in for this keyword");
		failedSendMsgs.put(MSG_ID_FAILED_SEND, "ooober API failed to send this message");
	}

	@Autowired
	private SmsMapper smsMapper;

	private Logger logger = Logger.getLogger(SmsOooberService.class);

	public SmsOooberService() {
	}

    protected String oooberApiGet(String url) throws IOException {
    	return Request.Get(url).connectTimeout(5000).socketTimeout(5000).execute().returnContent().asString();
    }
      
    protected String oooberApiPost(long timestamp, String signature, SmsMessage smsMessage) throws IOException {
    	// create form/post body
    	Form form = Form.form()
    			.add("username", username)
    			.add("timestamp", String.valueOf(timestamp))
    			.add("signature", signature)
    			.add("to", String.valueOf(smsMessage.getMobileNumber()))
    			.add("message",smsMessage.getText());

    	return Request.Post(oooberURI).bodyForm(form.build()).connectTimeout(5000).socketTimeout(5000).execute().returnContent().asString();
    }
    
	protected long parseMsgId(String apiResponse) {
		/* 
		<ApiResponse>
		   <status>SUCCESS</status>
		   <Message>
		      <status>Success</status>
		      <id>853</id>
		      <description>Message Delivered</description>
		      <number>15027978247</number>
		   </Message>
		</ApiResponse>
		*/
		try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(apiResponse)));
            XPath xPath =  XPathFactory.newInstance().newXPath();
            String msgId = (String) xPath.compile("/ApiResponse/Message/id").evaluate(document, XPathConstants.STRING);
            return Long.parseLong(msgId);
		} catch (Exception e) {
			logger.error("Error parsing message id: " + e.getMessage());
			return 0;
		}
	}
	
    protected SmsMessage sendMessage(SmsMessage smsMessage) {
		// send API message request to ooober
		try {
			long ts = System.currentTimeMillis();
			String sha1 = DigestUtils.shaHex(String.format(shaParam, username, password, ts));
			String encodedText = URLEncoder.encode(smsMessage.getText(), "UTF-8");
			smsMessage.setApiUrl(String.format(oooberAPI, username, ts, sha1, smsMessage.getMobileNumber(), encodedText));
			smsMessage.setApiResponse(oooberApiGet(smsMessage.getApiUrl()));
			// parse message id
			smsMessage.setMsgId(parseMsgId(smsMessage.getApiResponse()));
			// record response as outgoing message
    		smsMessage.setOrigin(OUTGOING);
    		insertSmsActivity(smsMessage);
		} catch (UnsupportedEncodingException uee) {
			smsMessage.setMsgId(MSG_ID_FAILED_SEND);
			smsMessage.setApiResponse(uee.getMessage());
			logger.error("Error sending message to ooober: " + uee.getMessage());
		} catch (IOException ioe) {
			smsMessage.setMsgId(MSG_ID_FAILED_SEND);
			smsMessage.setApiResponse(ioe.getMessage());
			logger.error("Error sending message to ooober: " + ioe.getMessage());
		}

		return smsMessage;
    }
    
	public SmsMessage sendOptIn(SmsMessage smsMessage) {
		smsMessage.setText(optInMessages.get(smsMessage.getType()));
		return sendMessage(smsMessage);
	}
	
	public SmsMessage sendText(SmsMessage smsMessage) {
		// check the subscription status, can only send if opt2
		SmsOptIn smsOptIn = smsMapper.selectSmsOptIn(smsMessage.getMobileNumber(), smsMessage.getKeyword());
		if (smsOptIn != null && OPT2.equalsIgnoreCase(smsOptIn.getOptInStatus())) {
			sendMessage(smsMessage);
		} else {
			smsMessage.setMsgId(MSG_ID_NOT_OPTED_IN);
		}
		
		return smsMessage;
	}
	
	public SmsMessage send(SmsMessage smsMessage) {
		if (TEXT.equalsIgnoreCase(smsMessage.getType())) {
			return sendText(smsMessage);
		} else if (optInMessages.containsKey(smsMessage.getType())) {
            return sendOptIn(smsMessage);
		} else {
			return null;
		}
	}
	
    public void processMO(SmsMessage smsMessage) {
    	if (smsMessage != null) {
    		// log incoming message
    		smsMessage.setOrigin(INCOMING);
    		insertSmsActivity(smsMessage);
    		
    		// update subscription status with opt1, opt2 or stop (stop & opt2 wins out)
    		if (Arrays.asList(OPT1, OPT2, STOP).contains(smsMessage.getType())) {
    			// get subscription status
    			SmsOptIn smsOptIn = smsMapper.selectSmsOptIn(smsMessage.getMobileNumber(), smsMessage.getKeyword());
    			// if subscription status doesn't exist, then create it
    			if (smsOptIn == null) {
    				smsMapper.insertSmsOptIn(smsMessage);
                // opt2 and stop status should be updated no matter what
    			} else if (!OPT1.equalsIgnoreCase(smsMessage.getType())) {
    				smsMapper.updateSmsOptIn(smsMessage);
    			// opt1 type is left, don't overwrite an opt2 with an opt1 (edge case)	
    			} else if (!OPT2.equalsIgnoreCase(smsOptIn.getOptInStatus())) {
    				smsMapper.updateSmsOptIn(smsMessage);
    			}
    		}
    	}
    }
    
	public void insertSmsActivity(SmsMessage smsMessage) {
		smsMapper.insertSmsActivity(smsMessage);
	}	
}
