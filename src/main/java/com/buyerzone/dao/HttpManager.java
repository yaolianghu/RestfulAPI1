package com.buyerzone.dao;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import com.buyerzone.model.HttpRequestConfig;
import com.buyerzone.model.HttpResult;
import com.buyerzone.model.HttpUtilException;
import com.buyerzone.model.Response;

public class HttpManager {
	
	Logger logger = Logger.getLogger(HttpManager.class);
	private final String USER_AGENT = "Mozilla/5.0";
	
	public Response sendData(String url, Map<String, String> params){
		URL obj;
		// HttpURLConnection connection = null;
		HttpsURLConnection connection = null;
		String parameters = "";
		String charset = "UTF-8";
		int responseCode = -1;
		
		try {
			obj = new URL(url);
			connection = (HttpsURLConnection) obj.openConnection();
			parameters = getStringParam(params);
			logger.info(String.format("Post Parameters: [%s]", parameters));
			//add request header
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Accept-Charset", charset);
			connection.setRequestProperty("User-Agent", USER_AGENT);
			connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			connection.setRequestProperty("Content-Length", "" + 
			          Integer.toString(parameters.getBytes().length));
			 
			//connection.setRequestProperty("Content-Language", "en-US");
		
			// Send post request
			connection.setDoOutput(true);
			

			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(parameters);
			wr.flush();
			wr.close();
 
			responseCode = connection.getResponseCode();
			logger.info(String.format("\nSending 'POST' request to URL : [%s]", url));
			logger.info(String.format("Post parameters : [%s]", params));
			logger.info(String.format("Response Code : [%d]", responseCode));
 
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(connection.getInputStream()));
			
			String inputLine;
			StringBuffer response = new StringBuffer();
 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
 
			//print result
			System.out.println(response.toString());
			

		    Response httpResponse = new Response();
		    httpResponse.setResponseCode(responseCode);
		    httpResponse.setResponseMessage("Success");
		    
		    return httpResponse;
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
		    Response httpResponse = new Response();
		    httpResponse.setResponseCode(responseCode);
		    httpResponse.setResponseMessage("Failed");
		    httpResponse.setDeveloperMessage(e.getMessage());
		    
		    e.printStackTrace();
			return httpResponse;
		} catch (ProtocolException e) {
		    Response httpResponse = new Response();
		    httpResponse.setResponseCode(responseCode);
		    httpResponse.setResponseMessage("Failed");
		    httpResponse.setDeveloperMessage(e.getMessage());
			//e.printStackTrace();
			return httpResponse;
			
		} catch (IOException e) {
		    Response httpResponse = new Response();
		    httpResponse.setResponseCode(responseCode);
		    httpResponse.setResponseMessage("Failed");
		    httpResponse.setDeveloperMessage(e.getMessage());
			//e.printStackTrace();
			return httpResponse;
		}
		finally {
			
			if (connection != null)
			{
				connection.disconnect();
				connection = null;
			}
		}
	}
	
	public Response postDataInQueryString(String url, Map<String, String> params){
		URL obj;
		HttpURLConnection connection = null;
		String parameters = "";
		String charset = "UTF-8";
		int responseCode = -1;
		
		try {
			parameters = getStringParam(params);
			
			logger.info("About to post data to url: " + "[" + url + "]");
			logger.info("Appending parameters to url. \n" + parameters);
			
			if(!url.contains("?"))
				url += "?" + parameters;
			else
				url += "&" + parameters;
			
			logger.info("New URL Request. \n" + url);
			
			obj = new URL(url);
			connection = (HttpURLConnection) obj.openConnection();
 
			//add request header
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Accept-Charset", charset);
			connection.setRequestProperty("User-Agent", USER_AGENT);
			connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			connection.setRequestProperty("Content-Length", "" + 
			          Integer.toString(parameters.getBytes().length));
			 
			connection.setRequestProperty("Content-Language", "en-US");
		
			// Send post request
			connection.setDoOutput(true);
			
			responseCode = connection.getResponseCode();
			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + params);
			System.out.println("Response Code : " + responseCode);
 
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(connection.getInputStream()));
			
			String inputLine;
			StringBuffer response = new StringBuffer();
 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
 
			//print result
			System.out.println(response.toString());
			

		    Response httpResponse = new Response();
		    httpResponse.setResponseCode(responseCode);
		    httpResponse.setResponseMessage("Success");
		    
		    return httpResponse;
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
		    Response httpResponse = new Response();
		    httpResponse.setResponseCode(responseCode);
		    httpResponse.setResponseMessage("Failed");
		    httpResponse.setDeveloperMessage(e.getMessage());
		    
		    e.printStackTrace();
			return httpResponse;
		} catch (ProtocolException e) {
		    Response httpResponse = new Response();
		    httpResponse.setResponseCode(responseCode);
		    httpResponse.setResponseMessage("Failed");
		    httpResponse.setDeveloperMessage(e.getMessage());
			//e.printStackTrace();
			return httpResponse;
			
		} catch (IOException e) {
		    Response httpResponse = new Response();
		    httpResponse.setResponseCode(responseCode);
		    httpResponse.setResponseMessage("Failed");
		    httpResponse.setDeveloperMessage(e.getMessage());
			//e.printStackTrace();
			return httpResponse;
		}
		finally {
			
			if (connection != null)
			{
				connection.disconnect();
				connection = null;
			}
		}
	}
	
	private String getStringParam(Map<String, String> params) throws UnsupportedEncodingException{
		
		String param = "";
		String currentParam = "";
		String charset = "UTF-8";
		
		for(Map.Entry<String, String> entry : params.entrySet()){
			currentParam = entry.getKey() + "=" + entry.getValue() + "&";
			param += URLEncoder.encode(currentParam, charset);
		    //System.out.printf("Key : %s and Value: %s %n", entry.getKey(), entry.getValue());
		    logger.info(String.format("Key : %s and Value: %s %n", entry.getKey(), entry.getValue()));
		}
		
		//Remove last "&" ampersand
		param = param.substring(0, param.length() - 1);
		return param;
	}
	
	public String makeHttpGetRequest(String url, Map<String,String> params) throws IOException{
		int responseCode = -1;
		String responseOutput = "";
		String parameters = "";
		HttpClient client = null;
		HttpGet request = null;
		
		try {
			parameters = getStringParam(params);
			
			logger.info("About to post data to url: " + "[" + url + "]");
			logger.info("Appending parameters to url. \n" + parameters);
			
			if(!url.contains("?"))
				url += "?" + parameters;
			else
				url += "&" + parameters;
			
			client = new DefaultHttpClient();
			request = new HttpGet(url);
	 
			// add request header
			request.addHeader("User-Agent", USER_AGENT);
	 
			HttpResponse response = client.execute(request);
			
	 
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + 
	                       response.getStatusLine().getStatusCode());
	 
			BufferedReader rd = new BufferedReader(
	                       new InputStreamReader(response.getEntity().getContent()));
	 
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
	 
			responseOutput = result.toString();
		    
		    return responseOutput;
		} catch (MalformedURLException e) {
			logger.error(e);
			throw e;
		} catch (ProtocolException e) {
			logger.error(e);
			
		} catch (IOException e) {
			logger.error(e);
			throw e;
		}
		finally {
			if(client != null){
				client = null;
			}
			
			if(request != null){
				request = null;
			}
		}

		return responseOutput;	
		
	}
	
	public Response makeHttpPostRequest(String url, Map<String, String> params){
		int responseCode = 500;
		HttpClient client = null;
		HttpPost post = null;
		
		try {
			
			client = new DefaultHttpClient();
			post = new HttpPost(url);
			post.setHeader("User-Agent", USER_AGENT);
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(getParameterList(params));
			String postParameters = IOUtils.toString(entity.getContent(), "UTF-8"); 
			
			post.setEntity(entity);
			
			HttpResponse response = client.execute(post);
			logger.info(String.format("Sending 'POST' request to URL: [%s]. Content Type:[%s]. Post parameters: [%s]. Response Code: [%d]", url, post.getEntity(), postParameters, response.getStatusLine().getStatusCode()));
			
			responseCode = response.getStatusLine().getStatusCode();
			
			if(responseCode == 302) {
				String redirectUrl = response.getFirstHeader("Location").getValue();
				logger.info(String.format("Redirect URL : [%s]", redirectUrl));
				response = redirectHandler(redirectUrl);
				responseCode = response.getStatusLine().getStatusCode();
				logger.info(String.format("Response Code : [%d]", response.getStatusLine().getStatusCode()));
			}
	 
			BufferedReader rd = new BufferedReader(
	                        new InputStreamReader(response.getEntity().getContent()));
	 
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

		    Response httpResponse = new Response();
		    httpResponse.setResponseCode(responseCode);
		    //logger.info(String.format("Response Message from client: [%s]", result.toString()));
		    httpResponse.setResponseMessage(String.format("Response Message: [%s]", result.toString()));
		    
		    
		    return httpResponse; 
		} catch (MalformedURLException e) {
			logger.error(String.format("makeHttpPostRequest post to [%s] Exception: [%s]. Stack Trace: [%s].", url, e.getMessage(), e));
		    Response httpResponse = new Response();
		    httpResponse.setResponseCode(responseCode);
		    httpResponse.setResponseMessage("Failed");
		    httpResponse.setDeveloperMessage(e.getMessage());
		    
			return httpResponse;
		} catch (ProtocolException e) {
			logger.error(String.format("makeHttpPostRequest post to [%s] Exception: [%s]. Stack Trace: [%s].", url, e.getMessage(), e));
		    Response httpResponse = new Response();
		    httpResponse.setResponseCode(responseCode);
		    httpResponse.setResponseMessage("Failed");
		    httpResponse.setDeveloperMessage(e.getMessage());
			return httpResponse;
			
		} catch (IOException e) {
			logger.error(String.format("makeHttpPostRequest post to [%s] Exception: [%s]. Stack Trace: [%s].", url, e.getMessage(), e));
		    Response httpResponse = new Response();
		    httpResponse.setResponseCode(responseCode);
		    httpResponse.setResponseMessage("Failed");
		    httpResponse.setDeveloperMessage(e.getMessage());
			return httpResponse;
		}
		catch (Exception e) {
			logger.error(String.format("makeHttpPostRequest post to [%s] Exception: [%s]. Stack Trace: [%s].", url, e.getMessage(), e));
		    Response httpResponse = new Response();
		    httpResponse.setResponseCode(responseCode);
		    httpResponse.setResponseMessage("Failed");
		    httpResponse.setDeveloperMessage(e.getMessage());
			return httpResponse;
		}
		finally {
			if(client != null){
				client = null;
			}
			
			if(post != null){
				post = null;
			}
		}	
	}
	
	public Response postDataInQueryString(String url, String params, String contentType){
		URL obj;
		HttpURLConnection connection = null;
		String parameters = "";
		String charset = "UTF-8";
		int responseCode = -1;
		
		try {
			logger.info("About to post data to url: " + "[" + url + "]");
			logger.info("Appending parameters to url. \n" + params);
			
			obj = new URL(url);
			connection = (HttpURLConnection) obj.openConnection();
		
			parameters = params;
			
			//add request header
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Accept-Charset", charset);
			connection.setRequestProperty("Content-Type", contentType);
			connection.setRequestProperty("Content-Length", "" + 
			          Integer.toString(parameters.getBytes().length));
			
			connection.setRequestProperty("Content-Language", "en-US");
			connection.setUseCaches (false);
			// Send post request
			connection.setDoOutput(true);
			connection.setDoInput(true);
			
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
		    wr.writeBytes (parameters);
		    wr.flush ();
		    wr.close ();
			
			responseCode = connection.getResponseCode();
		    
			logger.info("\nSending 'POST' request to URL : " + url);
			logger.info("Post parameters : " + parameters);
			logger.info("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(connection.getInputStream()));
			
			String inputLine;
			StringBuffer response = new StringBuffer();
 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
 
			//print result
			logger.info("Response from client: " + response.toString());
			

		    Response httpResponse = new Response();
		    httpResponse.setResponseCode(responseCode);
		    httpResponse.setResponseMessage(response.toString());
		    
		    return httpResponse;
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
		    Response httpResponse = new Response();
		    httpResponse.setResponseCode(responseCode);
		    httpResponse.setResponseMessage("Failed");
		    httpResponse.setDeveloperMessage(e.getMessage());
		    
		    e.printStackTrace();
			return httpResponse;
		} catch (ProtocolException e) {
		    Response httpResponse = new Response();
		    httpResponse.setResponseCode(responseCode);
		    httpResponse.setResponseMessage("Failed");
		    httpResponse.setDeveloperMessage(e.getMessage());
			//e.printStackTrace();
			return httpResponse;
			
		} catch (IOException e) {
		    Response httpResponse = new Response();
		    httpResponse.setResponseCode(responseCode);
		    httpResponse.setResponseMessage("Failed");
		    httpResponse.setDeveloperMessage(e.getMessage());
			//e.printStackTrace();
			return httpResponse;
		}
		finally {
			
			if (connection != null)
			{
				connection.disconnect();
				connection = null;
			}
		}
	}
	
	
	private List<NameValuePair> getParameterList(Map<String,String> params) {
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		
		for(Map.Entry<String, String> entry : params.entrySet()){
			parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		
		return parameters;
	}
	
	public Response postData(String url, String params){
		//String url = "https://selfsolve.apple.com/wcResults.do";
		URL obj;
		HttpURLConnection connection = null;
		String charset = "UTF-8";
		int responseCode = -1;
		
		try {
			obj = new URL(url);
			connection = (HttpURLConnection) obj.openConnection();
 
			//add request header
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Accept-Charset", charset);
			//connection.setRequestProperty("User-Agent", USER_AGENT);
			connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			connection.setRequestProperty("Content-Length", "" + 
			          Integer.toString(params.getBytes().length));
			 
			connection.setRequestProperty("Content-Language", "en-US");
		
			     
 
			// urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";
 
			// Send post request
			connection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(params);
			wr.flush();
			wr.close();
 
			responseCode = connection.getResponseCode();
			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + params);
			System.out.println("Response Code : " + responseCode);
 
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(connection.getInputStream()));
			
			String inputLine;
			StringBuffer response = new StringBuffer();
 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
 
			//print result
			System.out.println(response.toString());
			

		    Response httpResponse = new Response();
		    httpResponse.setResponseCode(responseCode);
		    httpResponse.setResponseMessage("Success");
		    
		    return httpResponse;
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
		    Response httpResponse = new Response();
		    httpResponse.setResponseCode(responseCode);
		    httpResponse.setResponseMessage("Failed");
		    httpResponse.setDeveloperMessage(e.getMessage());
		    
		    e.printStackTrace();
			return httpResponse;
		} catch (ProtocolException e) {
		    Response httpResponse = new Response();
		    httpResponse.setResponseCode(responseCode);
		    httpResponse.setResponseMessage("Failed");
		    httpResponse.setDeveloperMessage(e.getMessage());
			//e.printStackTrace();
			return httpResponse;
			
		} catch (IOException e) {
		    Response httpResponse = new Response();
		    httpResponse.setResponseCode(responseCode);
		    httpResponse.setResponseMessage("Failed");
		    httpResponse.setDeveloperMessage(e.getMessage());
			//e.printStackTrace();
			return httpResponse;
		}
		finally {
			
			if (connection != null)
			{
				connection.disconnect();
				connection = null;
			}
		}
	}


	
	public String getData(String url) throws IOException{
		//String url = "https://selfsolve.apple.com/wcResults.do";
		URL obj;
		HttpURLConnection connection = null;
		String charset = "UTF-8";
		int responseCode = -1;
		
		try {
			obj = new URL(url);
			connection = (HttpURLConnection) obj.openConnection();
 
			//add request header
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent", USER_AGENT);

			responseCode = connection.getResponseCode();
			logger.info(String.format("\nSending 'GET' request to URL : [%s]",url));
			logger.info(String.format("Response Code : [%d]", responseCode));
 
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(connection.getInputStream()));
			
			String inputLine;
			StringBuffer response = new StringBuffer();
 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
 
			//print result
			logger.info(String.format("Response Data: [%s]", response.toString()));
		    
		    return response.toString();
			
		} catch (MalformedURLException e) {
			logger.error(e);
			throw e;
		} catch (ProtocolException e) {
			logger.error(e);
			throw e;
			
		} catch (IOException e) {
			logger.error(e);
			throw e;
		}
		finally {
			
			if (connection != null)
			{
				connection.disconnect();
				connection = null;
			}
		}
	}
	
	public Response doPost(String url, List<NameValuePair> urlParameters) throws IOException {
		HttpClient client = HttpClientBuilder.create().build();
		logger.info("About to post data to url: " + "[" + url + "]");
		HttpPost post = new HttpPost(url);
		post.setHeader("User-Agent", USER_AGENT);
		
		try {
			post.setEntity(new UrlEncodedFormEntity(urlParameters));
			HttpResponse response = client.execute(post);
			int responseCode = response.getStatusLine().getStatusCode();
			logger.info("Response Code : " + responseCode);
			
			if(responseCode == 302) {
				String redirectUrl = response.getFirstHeader("Location").getValue();
				logger.info(String.format("Redirect URL : [%s]", redirectUrl));
				response = redirectHandler(redirectUrl);
				responseCode = response.getStatusLine().getStatusCode();
				logger.info(String.format("Response Code : [%d]", response.getStatusLine().getStatusCode()));
			}
			
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) { 
				result.append(line);
			}
			rd.close();
			logger.info("Response from client: " + result.toString());
			Response httpResponse = new Response();
		    httpResponse.setResponseCode(response.getStatusLine().getStatusCode());
		    httpResponse.setResponseMessage(result.toString());
		    
		    return httpResponse;
			
		} catch (UnsupportedEncodingException e) {
			logger.error(String.format("doPost post to [%s] Exception: [%s]. Stack Trace: [%s].", url, e.getMessage(), e));
			throw e;
		} catch (ClientProtocolException e) {
			logger.error(String.format("doPost post to [%s] Exception: [%s]. Stack Trace: [%s].", url, e.getMessage(), e));
			throw e;
		} catch (IOException e) {
			logger.error(String.format("doPost post to [%s] Exception: [%s]. Stack Trace: [%s].", url, e.getMessage(), e));
			throw e;
		}
	}
	
	
	public Response doPostNew(String url, Map<String, String> parameters) throws IOException, HttpUtilException {
		PostMethod postMethod = new PostMethod(url);

		org.apache.commons.httpclient.NameValuePair[] nameValuePairs = new org.apache.commons.httpclient.NameValuePair[parameters.size()];

		int i = 0;

		for (Iterator<String> keyIterator = parameters.keySet().iterator(); keyIterator.hasNext();) {
			String name = (String) keyIterator.next();
			String value = (String) parameters.get(name);

			nameValuePairs[i++] = new org.apache.commons.httpclient.NameValuePair(name, value);
		}

		postMethod.setRequestBody(nameValuePairs);

		HttpResult result = serviceRequest(url, HttpRequestConfig.CONFIG.getConnectionTimeout()*2, HttpRequestConfig.CONFIG.getResponseTimeout(), postMethod);
		Response response = new Response();
		response.setResponseCode(result.getStatusCode());
		response.setResponseMessage(new String(result.getResponse()));
		logger.info(String.format("Response code: [%d] and message: [%s]", response.getResponseCode(), response.getResponseMessage()));
		return response;
	}
	
	private static HttpResult serviceRequest(String url, int connectionTimeout, int responseTimeout, HttpMethod method) throws HttpUtilException {
		org.apache.commons.httpclient.HttpClient client = new org.apache.commons.httpclient.HttpClient();
		HttpResult result = new HttpResult();

		int statusCode = -1;

		try {
			// Notify the server that we can process a GZIP-compressed response
			// before
			// sending the request.
			method.addRequestHeader("Accept-Encoding", "gzip");

			statusCode = client.executeMethod(method);
			if (statusCode == -1) {
				return result;
			}

			Header contentEncodingHeader = method.getResponseHeader("Content-Encoding");
			if (contentEncodingHeader != null && contentEncodingHeader.getValue().equalsIgnoreCase("gzip")) {
				GZIPInputStream gzis = new GZIPInputStream(method.getResponseBodyAsStream());
				prepareResult(gzis, result);
			} else {
				InputStream is = method.getResponseBodyAsStream();
				prepareResult(is, result);
			}

		} catch (IOException e) {
			throw new HttpUtilException("HttpServiceRequestFailed", e);
		} finally {
			result.setStatusCode(statusCode);
			method.releaseConnection();
		}

		return result;
	}
	
	
	public HttpResponse redirectHandler(String redirectUrl) throws ClientProtocolException, IOException {
		HttpClient client = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
		HttpResponse response = client.execute(new HttpPost(redirectUrl));
		
		return response;
		
	}
	
	private static void prepareResult(InputStream is, HttpResult result) throws IOException {

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte[] buffer = new byte[com.buyerzone.model.HttpRequestConfig.CONFIG.getResponseBufferSize()];
		int len;
		while ((len = is.read(buffer)) > 0) {
			os.write(buffer, 0, len);
		}
		is.close();
		os.close();

		byte[] responseBody = os.toByteArray();
		result.setResponse(responseBody);

	}
}
