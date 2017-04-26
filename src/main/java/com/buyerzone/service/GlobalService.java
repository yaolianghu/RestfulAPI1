package com.buyerzone.service;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.buyerzone.dao.mapper.GlobalMapper;

@Service
public class GlobalService {

	@Autowired
	private GlobalMapper globalMapper;
	
	public List<HashMap<String, String>> getData(@Param("proc") String procName,@Param("list") String[] params) {
		return globalMapper.getData(procName, params);
	}
	
	public List<HashMap<String,String>> getDataBySingleParam(@Param("proc") String procName, @Param("param") String param) {
		return globalMapper.getDataBySingleParam(procName, param);
	}
	
	public HashMap<String,String> getDataNoParams(@Param("proc") String procName) {
		return globalMapper.getDataNoParams(procName);
	}
	
	public List<HashMap<String,String>> getListDataNoParams(@Param("proc") String procName) {
		return globalMapper.getListDataNoParams(procName);
	}
	
	public List<HashMap<String, String>> cloneMapping(@Param("proc") String procName, @Param("list") String[] params) {
		return globalMapper.cloneMapping(procName, params);
	}
}
