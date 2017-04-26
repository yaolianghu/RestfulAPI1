package com.buyerzone.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.buyerzone.model.Response;

public class ApiResponse<T> {
    private String methodName;
    private String RequestedDatetime;
    private Response response;
    private T content;

	public T getContent() {
		return content;
	}

	public void setContent(T content) {
		this.content = content;
	}

	public <T> T getObject(Class<T> clazz)  {
		T value = null;
		try {
			value = clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return value;
	}
    
	public Response getResponse() {
		return response;
	}
	public void setResponse(Response response) {
		this.response = response;
	}
	
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getRequestedDatetime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	    Date date = new Date();
		return dateFormat.format(date);
	}
	
}