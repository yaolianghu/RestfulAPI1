package com.purch.messaging.model;

import org.springframework.http.HttpStatus;

public class ApiResponseWrapper<T> {
    private HttpStatus status;
    private long duration;
    private T data;
    private String message;

    public ApiResponseWrapper() {
    	this.duration = System.currentTimeMillis();
    }
    
	public HttpStatus getStatus() {
		return status;
	}
	public void setStatus(HttpStatus status) {
		this.status = status;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public void start() {
		duration = System.currentTimeMillis();
	}
	
	public ApiResponseWrapper<T> complete(HttpStatus status) {
		return this.complete(status, this.data, "");
	}

	public ApiResponseWrapper<T> complete(HttpStatus status, T data) {
		return this.complete(status, data, "");
	}

	public ApiResponseWrapper<T> complete(HttpStatus status, T data, String message) {
		this.status = status;
		this.data = data;
		this.duration = System.currentTimeMillis() - duration;
		this.message = message;
		return this;
	}
}
