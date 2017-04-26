package com.buyerzone.model;

import java.util.HashMap;

public class LeadRequest {
	LeadPreference leadPreference;
	HashMap<String, String> data;
	
	public void setLeadPreference(LeadPreference leadPreference) {
		this.leadPreference = leadPreference;
	}
	
	public LeadPreference getLeadPreference() {
		return leadPreference;
	}
	
	public void setData(HashMap<String, String> data) {
		this.data = data;
	}
	
	public HashMap<String, String> getData() {
		return data;
	}
	
	

}
