package com.buyerzone.dao.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.StatementType;


public interface GlobalMapper {
	
	@Select({"<script>"
	  		+ " CALL ${proc}(",
		      "<foreach item='item' index='index' collection='list'",
			      "separator=','>",
			      	"#{item}",
			   "</foreach>)",
	  	"</script>"})
	@Options(statementType = StatementType.CALLABLE)
	List<HashMap<String, String>> getData(@Param("proc") String procName,@Param("list") String[] params);
	
	@Select({" CALL ${proc}(#{param})"})
	@Options(statementType = StatementType.CALLABLE)
	List<HashMap<String,String>> getDataBySingleParam(@Param("proc") String procName, @Param("param") String param);
	
	@Select({" CALL ${proc}()"})
	@Options(statementType = StatementType.CALLABLE)
	HashMap<String,String> getDataNoParams(@Param("proc") String procName);
	
	@Select({" CALL ${proc}()"})
	@Options(statementType = StatementType.CALLABLE)
	List<HashMap<String,String>> getListDataNoParams(@Param("proc") String procName);

	@Select({"<script>"
	  		+ " CALL ${proc}(",
		      "<foreach item='item' index='index' collection='list'",
			      "separator=','>",
			      	"#{item}",
			   "</foreach>)",
	  	"</script>"})
	@Options(statementType = StatementType.CALLABLE)
	List<HashMap<String, String>> cloneMapping(@Param("proc") String procName, @Param("list") String[] params);
}
