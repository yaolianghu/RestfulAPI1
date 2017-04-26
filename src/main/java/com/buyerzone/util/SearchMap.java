package com.buyerzone.util;

import java.util.HashMap;
import java.util.List;

public final class SearchMap {

	public SearchMap() {
	}
	
	public static String searchForMap(List<HashMap<String,String>> mapList, String keySearchIn, String valueToSearch){
    	String result = "";
    	for(HashMap<String,String> map : mapList){
    		if(map.containsValue(valueToSearch)) {
	    		//if(map.get("question_id").equals(valueToSearch)){
	    			result = map.get("answer");
	    		//}
    		}
    	}
    	return result;
    }

}
