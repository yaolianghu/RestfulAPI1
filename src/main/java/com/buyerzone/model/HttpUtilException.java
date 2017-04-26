package com.buyerzone.model;

/**
 * HttpUtil Exception
 * 
 * @author balasubramanianm
 *
 */
public class HttpUtilException extends Exception
{
 //===============================================================
 // Instance Variables
 //===============================================================
 private Exception exception = null;
 
 
 //===============================================================
 // Constructor
 //===============================================================
 public HttpUtilException(String msg)
 {
     super(msg);
 }
 
 public HttpUtilException(String msg, Exception anException)
 {
     super(msg + anException);
     exception = anException;
 }
 
 //===============================================================
 // Public Methods
 //===============================================================
 public Exception getException()
 {
     return exception;   
 }
}

