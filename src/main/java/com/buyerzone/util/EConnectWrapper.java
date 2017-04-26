package com.buyerzone.util;

import java.util.*;

import com.buyerzone.model.Response;
import com.buyerzone.model.econnect.EConnectSettings;

public class EConnectWrapper<T>{
	private List<EConnectSettings> eConnectClients;
	private Response response;
	
	/**
	 * @return the eConnectClients
	 */
	public List<EConnectSettings> geteConnectClients() {
		return eConnectClients;
	}
	/**
	 * @param eConnectClients the eConnectClients to set
	 */
	public void seteConnectClients(List<EConnectSettings> eConnectClients) {
		this.eConnectClients = eConnectClients;
	}
	/**
	 * @return the response
	 */
	public Response getResponse() {
		return response;
	}
	/**
	 * @param response the response to set
	 */
	public void setResponse(Response response) {
		this.response = response;
	}
	
}