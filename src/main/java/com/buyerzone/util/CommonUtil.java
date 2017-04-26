package com.buyerzone.util;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;


public class CommonUtil {

	public static final long ID_FACTOR = 15401;

    public static Long getSecuredIdFromId(Long id) {
        return (id * ID_FACTOR);
    }

    public static Long getIdFromSecuredId(Long securedId) {
        return (((securedId % ID_FACTOR) == 0) ? (securedId / ID_FACTOR) : -1L);
    }
    
	public static void addValueToMap(Map<Object, Object> map, Object key,
			Object value) {
		Object obj = map.get(key);
		List<Object> list;
		if (obj == null) {
			list = new ArrayList<Object>();
		} else {
			list = ((ArrayList<Object>) obj);
		}
		list.add(value);
		map.put(key, list);
	}

	public static String getMethodName(HttpServletRequest methodRequest) {
		HashMap<String, String[]> map = (HashMap<String, String[]>) methodRequest
				.getParameterMap();
		String url = methodRequest.getRequestURL().toString();
		StringBuilder sb = new StringBuilder();
		sb.append("?");
		for (String key : map.keySet()) {
			sb.append(key);
			sb.append("=");
			sb.append(map.get(key)[0]);
			sb.append("&");
		}

		sb.setLength(sb.length() - 1);
		url = url + sb.toString();
		return url;
	}

	public static int extractNumberFromString(String input) {
		int output = 0;
		if (input != null && !input.isEmpty()) {
			Pattern p = Pattern.compile("\\d+");
			Matcher m = p.matcher(input);
			while (m.find()) {
				output = Integer.parseInt(m.group());
			}

			return output;
		} else
			return output;
	}
	
	public static List<String> swapList(List<String> oldFields, String[][] swapFields) {
    	List<String> newFields = new ArrayList<String>();
    	for(String str : oldFields) {
    		for(String[] strArray : swapFields) {
    			if(str.equals(strArray[0]))
    				newFields.add(strArray[1]);
    		}
    	}
    	
    	return newFields;
    }
	
    public static void updateMapKey(Object key1, Object key2, Map map) {
   	 Object temp = map.remove(key1);
   	 if(temp != null)
   		 map.put(key2, temp);
   }
    
    public static long executeStartTime() {
    	long startTime = System.currentTimeMillis();
        return startTime;
    }
    
    public static long executeEndTime() {
    	long stopTime = System.currentTimeMillis();
        return stopTime;
    }
    
    public static long executeTime(long startTime, long stopTime) {
    	long elapsedTime = stopTime - startTime;
    	return elapsedTime;
    }
    
    public static String getFirstDateOfCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = format.format(calendar.getTime());
        return date;
    }
    
    public static String getLastDateOfCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        int lastDate = calendar.getActualMaximum(Calendar.DATE);
    	calendar.set(Calendar.DATE, lastDate);
    	calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = format.format(calendar.getTime());
        return date;
    }
}
