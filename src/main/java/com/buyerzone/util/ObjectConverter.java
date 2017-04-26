package com.buyerzone.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import sun.util.calendar.LocalGregorianCalendar.Date;

public final class ObjectConverter {
	
	private ObjectConverter(){
		
	}
	
	public static Map<String, String> toMap(Object obj){
		Map<String, String> currentMap = new HashMap<String, String>();
		String returnedValue = "";
		
	    for (Method method : obj.getClass().getMethods()) {
	        try {
	        	if(method.getName().startsWith("get") && method.getParameterTypes().length == 0)
	        	{
	        		String key = method.getName().replace("get", "");
	        		if (!currentMap.containsKey(key)){
	        			//Object value = method.invoke(object);
	        			if(method.getReturnType().equals(Integer.TYPE)){
	        				returnedValue = String.valueOf(method.invoke(obj));
	        			}
	        			
	        			if (method.getReturnType().equals(String.class))
	        				returnedValue = (String) method.invoke(obj);
	        			
	        			if(method.getReturnType().equals(Boolean.TYPE)){
	        				returnedValue = String.valueOf(method.invoke(obj));
	        			}
	        			
	        			if(method.getReturnType().equals(Double.TYPE)){
	        				returnedValue = String.valueOf(method.invoke(obj));
	        			}
	        			
	        			if (method.getReturnType().equals(Date.class))
	        				returnedValue = (String) method.invoke(obj);
	        			
	        			currentMap.put(key, returnedValue);
	        		}

	        	}
	        	
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
    	return currentMap;
	}

}
