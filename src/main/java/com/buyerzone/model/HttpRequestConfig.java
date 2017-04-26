package com.buyerzone.model;

public enum HttpRequestConfig {
	
	CONFIG (5000, 10000, 10240); // Default Settings
	
	private int connectionTimeout; // millisecs
	private int responseTimeout;   // millisecs
	private int responseBufferSize;   // bytes
	
	private HttpRequestConfig (int connectionTimeout, int responseTimeout, int responseBufferSize){
		this.connectionTimeout = connectionTimeout;
		this.responseTimeout = responseTimeout;
		this.responseBufferSize = responseBufferSize;
	}
	
	public int getConnectionTimeout() { return this.connectionTimeout;}
	
	public int getResponseTimeout() { return this.responseTimeout; }
	
	public int getResponseBufferSize() { return this.responseBufferSize; }
	
	public void setConnectionTimeout (int connectionTimeout){
		this.connectionTimeout = connectionTimeout;
	}
	
	public void setResponseTimeout (int responseTimeout){
		this.responseTimeout = responseTimeout;
	}
	
	public void setResponseBufferSize (int responseBufferSize){
		this.responseBufferSize = responseBufferSize;
	}
}
