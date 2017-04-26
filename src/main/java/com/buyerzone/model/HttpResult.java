package com.buyerzone.model;

/**
 * Container for holding result information about a http request.
 * 
 * @author balasubramanianm
 *
 */
public class HttpResult
{     
 
 //===============================================================
 // Instance Variables
 //===============================================================
 private byte[] response;
 
 private int    statusCode   = -1;
 
 //===============================================================
 // Constructors
 //===============================================================
 public HttpResult()
 {
     
 }
 
 public HttpResult(int aStatusCode, byte[] aResponse)
 {
     response    = aResponse;
     statusCode  = aStatusCode;
 }

 //===============================================================
 // Getters and Setters
 //===============================================================
 public int getStatusCode()
 {   
     return statusCode;
 }
 
 public void setStatusCode(int aStatusCode)
 {
     statusCode = aStatusCode;
 }
 
 public byte[] getResponse()
 {   
     return response;
 }
 
 public void setResponse(byte[] aResponse)
 {
     response = aResponse;
 }
 
 //===============================================================
 // Interrogators
 //===============================================================
 public boolean isSuccessful()
 {
     return (statusCode == 200);   
 }
 
 public String toString()
 {
     StringBuffer buffer = new StringBuffer();
     
     buffer.append("Status Code:");
     buffer.append(getStatusCode());
     buffer.append("Response:");
     buffer.append(new String(getResponse()));
     
     return buffer.toString();
 }
}

