package com.buyerzone.util.report;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.nio.file.StandardOpenOption.CREATE;

/**
 * Created by isantiago on 9/2/16.
 */
public class FileHelper {

    public static void createTsvFile(Path path, byte[] fileData) throws IOException {
        ByteBuffer buff = ByteBuffer.allocate(1024);
        BufferedOutputStream fileOut = new BufferedOutputStream(Files.newOutputStream(path, CREATE));
        buff.put(fileData);
        fileOut.write(buff.array(), 0, buff.capacity());
        fileOut.flush();
        fileOut.close();
    }
    /*
     * Generate comma separated values from a list of strings
     */
    public static ByteArrayOutputStream generateTsvContents(List<LinkedList<String>> dataStructure)throws IOException{
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        for(LinkedList<String> linkedHashMap : dataStructure) {
            StringBuilder sb = new StringBuilder();
            for(String item : linkedHashMap) {
                sb.append("\"").append(item).append("\",");
            }
            sb.setLength(sb.length() - 1);
            sb.append("\n");
            bytes.write(sb.toString().getBytes());
        }
        bytes.close();
        return bytes;
    }

    /*
     * Generate comma separated values from a list of maps
     */
    public static ByteArrayOutputStream generateCsvContents(List<HashMap<String, String>> dataStructure)throws IOException{
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        List<String> keyList = new ArrayList<String>(dataStructure.get(0).keySet());
        for(HashMap<String,String> m : dataStructure){
        	for(String key : m.keySet()){
        		if(!keyList.contains(key))
        			keyList.add(key);
        	}
        }

        StringBuilder sb = new StringBuilder();
        for(String key : keyList){
            sb.append("\"").append(key).append("\",");
        }
        sb.setLength(sb.length() - 1);
        sb.append("\n");
        bytes.write(sb.toString().getBytes());
        for(HashMap<String,String> hashMap : dataStructure) {
            sb = new StringBuilder();
            for(String key : keyList){
            	Object o = hashMap.get(key);
            	String value = "";
            	if(o instanceof Long)
            		value = o.toString();
            	else
            		value = (o == null) ? "" : String.valueOf(o);
                sb.append("\"").append(value).append("\",");
            }
            sb.setLength(sb.length() - 1);
            sb.append("\n");
            bytes.write(sb.toString().getBytes());
        }
        bytes.close();
        return bytes;
    }
}