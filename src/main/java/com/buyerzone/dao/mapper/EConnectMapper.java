package com.buyerzone.dao.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Many;
import com.buyerzone.model.econnect.ECategory;
import com.buyerzone.model.econnect.EConnectSettings;
import com.buyerzone.model.econnect.ESupplier;
import com.buyerzone.model.econnect.ESupplierCategory;

public interface EConnectMapper {
	@Insert({
		"<script>"
		        + "INSERT INTO email_leads_client ("
		        + "supplier_entity_owner_id,"
		        + "fee_per_lead,"
		        + "start_date,"
		        + "end_date,"
		        + "lead_cap,"
		        + "category_id,"
		        + "category_name,"
		        + "ftp_host,"
		        + "ftp_user,"
		        + "ftp_password,"
		        + "ftp_passive,"
		        + "ftp_remote_directory,"
		        + "tsv_email,"
		        + "email,"
		        + "generate_email,"
		        + "generate_tsv_email,"
		        + "generate_tsv_ftp,"
		        + "generate_tsv_email_daily,"
		        + "generate_xml,"
		        + "xml_post_parameter,"
		        + "xml_post_url,"
		        + "send_rfq_notification,"
		        + "use_geo_coverage,"
		        + "week_days_only,"
		        + "weekly_cap,"
		        + "daily_cap,"
		        + "daily_cap_limit,"
		        + "auto_responder,"
		        + "is_active,"
		        + "created_date,"
		        + "modified_date,"
		        + "post_to_api,"
		        + "ftps_enabled,"
		        + "use_full_csv_header"
		        + ")"
		        + "VALUES"
		        + "(#{eConnectSettings.supplierCategoryId},"
		        + "coalesce(#{eConnectSettings.feePerLead},500),"
		        + "coalesce(#{eConnectSettings.startDate},now()),"
		        + "coalesce(#{eConnectSettings.endDate},now()),"
		        + "coalesce(#{eConnectSettings.leadCap},-1),"
		        + "#{eConnectSettings.categoryId},"
		        + "#{eConnectSettings.categoryName},"
		        + "coalesce(#{eConnectSettings.ftpHost},null),"
		        + "coalesce(#{eConnectSettings.ftpUser},null),"
		        + "coalesce(#{eConnectSettings.ftpPassword},null),"
		        + "coalesce(#{eConnectSettings.ftpPassive},0),"
		        + "coalesce(#{eConnectSettings.ftpRemoteDirectory},null),"
		        + "coalesce(#{eConnectSettings.tsvEmail},null),"
		        + "coalesce(#{eConnectSettings.email},null),"
		        + "coalesce(#{eConnectSettings.generateEmail},0),"
		        + "coalesce(#{eConnectSettings.generateTsvEmail},0),"
		        + "coalesce(#{eConnectSettings.generateTsvFtp},0),"
		        + "coalesce(#{eConnectSettings.generateTsvEmailDaily},0),"
		        + "coalesce(#{eConnectSettings.generateXml},0),"
		        + "coalesce(#{eConnectSettings.xmlPostParameter},null),"
		        + "coalesce(#{eConnectSettings.xmlPostUrl},null),"
		        + "coalesce(#{eConnectSettings.sendRfqNotification},0),"
		        + "coalesce(#{eConnectSettings.useGeoCoverage},1),"
		        + "coalesce(#{eConnectSettings.weekDaysOnly},0),"
		        + "coalesce(#{eConnectSettings.weeklyCap},0),"
		        + "coalesce(#{eConnectSettings.dailyCap},0),"
		        + "coalesce(#{eConnectSettings.dailyCapLimit},0),"
		        + "coalesce(#{eConnectSettings.autoResponder},0),"
		        + "coalesce(#{eConnectSettings.active},1),"
		        + "coalesce(#{eConnectSettings.createdDate},now()),"
		        + "coalesce(#{eConnectSettings.modifiedDate},now()),"
		        + "coalesce(#{eConnectSettings.postToApi},null),"
		        + "coalesce(#{eConnectSettings.ftpsEnabled},null),"
		        + "coalesce(#{eConnectSettings.useFullCsvHeader},null))"
			  	+ "</script>"
	})
	int insertEConnectSetting(@Param("eConnectSettings") EConnectSettings eConnectSettings);

	@Update({
		"<script>"
		        + "update email_leads_client"
				+ "<set>"
			        + "<if test='eConnectSettings.feePerLead != 0'>fee_per_lead = #{eConnectSettings.feePerLead},</if>"
			        + "<if test='eConnectSettings.startDate != null'>start_date = #{eConnectSettings.startDate},</if>"
			        + "<if test='eConnectSettings.endDate != null'>end_date = #{eConnectSettings.endDate},</if>"
			        + "<if test='eConnectSettings.leadCap != null'>lead_cap = #{eConnectSettings.leadCap},</if>"
			        + "<if test='eConnectSettings.ftpHost != null'> ftp_host = #{eConnectSettings.ftpHost},</if>"
			        + "<if test='eConnectSettings.ftpUser != null'> ftp_user = #{eConnectSettings.ftpUser},</if>"
			        + "<if test='eConnectSettings.ftpPassword != null'> ftp_password = #{eConnectSettings.ftpPassword},</if>"
			        + "<if test='eConnectSettings.ftpPassive != null'> ftp_passive = cast(#{eConnectSettings.ftpPassive} as unsigned),</if>"
			        + "<if test='eConnectSettings.ftpRemoteDirectory != null'> ftp_remote_directory = #{eConnectSettings.ftpRemoteDirectory},</if>"
			        + "<if test='eConnectSettings.tsvEmail != null'> tsv_email = #{eConnectSettings.tsvEmail},</if>"
			        + "<if test='eConnectSettings.email != null'> email = #{eConnectSettings.email},</if>"
			        + "<if test='eConnectSettings.generateEmail != null'> generate_email = cast(#{eConnectSettings.generateEmail} as unsigned),</if>"
			        + "<if test='eConnectSettings.generateTsvEmail != null'> generate_tsv_email = cast(#{eConnectSettings.generateTsvEmail} as unsigned),</if>"
			        + "<if test='eConnectSettings.generateTsvFtp != null'> generate_tsv_ftp = cast(#{eConnectSettings.generateTsvFtp} as unsigned),</if>"
			        + "<if test='eConnectSettings.generateTsvEmailDaily != null'> generate_tsv_email_daily = cast(#{eConnectSettings.generateTsvEmailDaily} as unsigned),</if>"
			        + "<if test='eConnectSettings.generateXml != null'> generate_xml = cast(#{eConnectSettings.generateXml} as unsigned),</if>"
			        + "<if test='eConnectSettings.xmlPostParameter != null'> xml_post_parameter = #{eConnectSettings.xmlPostParameter},</if>"
			        + "<if test='eConnectSettings.xmlPostUrl != null'> xml_post_url = #{eConnectSettings.xmlPostUrl},</if>"
			        + "<if test='eConnectSettings.sendRfqNotification != null'> send_rfq_notification = cast(#{eConnectSettings.sendRfqNotification} as unsigned),</if>"
			        + "<if test='eConnectSettings.useGeoCoverage != null'> use_geo_coverage = cast(#{eConnectSettings.useGeoCoverage} as unsigned),</if>"
			        + "<if test='eConnectSettings.weekDaysOnly != null'> week_days_only = cast(#{eConnectSettings.weekDaysOnly} as unsigned),</if>"
			        + "<if test='eConnectSettings.weeklyCap != null'> weekly_cap = cast(#{eConnectSettings.weeklyCap} as unsigned),</if>"
			        + "<if test='eConnectSettings.dailyCap != null'> daily_cap = cast(#{eConnectSettings.dailyCap} as unsigned),</if>"
			        + "<if test='eConnectSettings.dailyCapLimit != null'> daily_cap_limit = cast(#{eConnectSettings.dailyCapLimit} as unsigned),</if>"
			        + "<if test='eConnectSettings.autoResponder != null'> auto_responder = cast(#{eConnectSettings.autoResponder} as unsigned),</if>"
			        + "<if test='eConnectSettings.active != null'> is_active = cast(#{eConnectSettings.active} as unsigned),</if>"
			        + "<if test='eConnectSettings.createdDate != null'> created_date = #{eConnectSettings.createdDate},</if>"
			        + "<if test='eConnectSettings.modifiedDate != null'> modified_date = #{eConnectSettings.modifiedDate},</if>"
			        + "<if test='eConnectSettings.postToApi != null'> post_to_api = cast(#{eConnectSettings.postToApi} as unsigned),</if>"
			        + "<if test='eConnectSettings.ftpsEnabled != null'>ftps_enabled = cast(#{eConnectSettings.ftpsEnabled} as unsigned),</if>"
			        + "<if test='eConnectSettings.useFullCsvHeader != null'> use_full_csv_header = cast(#{eConnectSettings.useFullCsvHeader} as unsigned),</if>"
		        + "</set>"
		        + "where email_leads_client_id = #{eConnectSettings.emailLeadsClientId}"
	  + "</script>"
	})
	int updateEConnectSetting(@Param("eConnectSettings") EConnectSettings eConnectSettings);
	
	@Select({
		"<script>"
		+ "select * from email_leads_client where email_leads_client_id = #{eConnectClientId}"
		+ "</script>"
	})
	EConnectSettings getEConnectClient(@Param("eConnectClientId") long eConnectClientId);
	
	@Results(value={
			@Result(column = "email_leads_client_id", property = "emailLeadsClientId"),
			@Result(column = "supplier_entity_owner_id", property = "supplierCategoryId"),
			@Result(column = "supplier_name", property = "supplierName"),
			@Result(column = "fee_per_lead", property = "feePerLead"),
			@Result(column = "start_date", property = "startDate", javaType = String.class),
			@Result(column = "end_date", property = "endDate", javaType = String.class),
			@Result(column = "lead_cap", property = "leadCap"),
			@Result(column = "category_id", property = "categoryId"),
			@Result(column = "category_name", property = "categoryName"),
			@Result(column = "ftp_host", property = "ftpHost"),
			@Result(column = "ftp_user", property = "ftpUser"),
			@Result(column = "ftp_password", property = "ftpPassword"),
			@Result(column = "ftp_passive", property = "ftpPassive"),
			@Result(column = "ftp_remote_directory", property = "ftpRemoteDirectory"),			
			@Result(column = "tsv_email", property = "tsvEmail"),
			@Result(column = "email", property = "email"),
			@Result(column = "generate_email", property = "generateEmail"),
			@Result(column = "generate_tsv_email", property = "generateTsvEmail"),
			@Result(column = "generate_tsv_ftp", property = "generateTsvFtp"),
			@Result(column = "generate_tsv_email_daily", property = "generateTsvEmailDaily"),
			@Result(column = "generate_xml", property = "generateXml"),
			@Result(column = "xml_post_parameter", property = "xmlPostParameter"),
			@Result(column = "xml_post_url", property = "xmlPostUrl"),
			@Result(column = "send_rfq_notification", property = "sendRfqNotification"),
			@Result(column = "use_geo_coverage", property = "useGeoCoverage"),
			@Result(column = "week_days_only", property = "weekDaysOnly"),
			@Result(column = "weekly_cap", property = "weeklyCap"),
			@Result(column = "daily_cap", property = "dailyCap"),
			@Result(column = "daily_cap_limit", property = "dailyCapLimit"),
			@Result(column = "auto_responder", property = "autoResponder"),
			@Result(column = "is_active", property = "active"),
			@Result(column = "created_date", property = "createdDate", javaType = String.class),
			@Result(column = "modified_date", property = "modifiedDate", javaType = String.class),
			@Result(column = "post_to_api", property = "postToApi"),
			@Result(column = "ftps_enabled", property = "ftpsEnabled"),
			@Result(column = "use_full_csv_heder", property = "useFullCsvHeader")
	})
	@Select({
		"<script>"
		+ "select * from email_leads_client elc join supplier_entity_owner seo on seo.supplier_entity_owner_id = elc.supplier_entity_owner_id "
		+ "join supplier s on s.supplier_entity_owner_id = seo.parent_sup_entity_owner_id where elc.supplier_entity_owner_id = #{eConnectSupplierId}"
		+ "</script>"
	})
	EConnectSettings getEConnectSettingsBySupplierId(@Param("eConnectSupplierId") int eConnectSupplierId);
	
	@Select({
		"<script>"
		+ "select email_leads_client_id as emailLeadsClientId, elc.supplier_entity_owner_id as supplierCategoryId, s.supplier_name as supplierName, is_active as active, fee_per_lead as feePerLead, coalesce(date_format(start_date,'%Y-%m-%d %T'),'null') as startDate, coalesce(date_format(end_date,'%Y-%m-%d %T'),'null') as endDate, lead_cap as leadCap, category_id as categoryId, " 
		+ "category_name as categoryName, ftp_host as ftpHost,ftp_user as ftpUser,ftp_password as ftpPassword, "
		+ "ftp_passive as ftpPassive,ftp_remote_directory as ftpRemoteDirectory, tsv_email as tsvEmail, email, "
		+ "generate_email as generateEmail,generate_tsv_email as generateTsvEmail, generate_tsv_ftp as generateTsvFtp, "
		+ "generate_tsv_email_daily as generateTsvEmailDaily, generate_xml as generateXml, xml_post_parameter as xmlPostParameter, "
		+ "xml_post_url as xmlPostUrl, send_rfq_notification as sendRfqNotification, use_geo_coverage as useGeoCoverage, week_days_only as weekDaysOnly, "
		+ "weekly_cap as weeklyCap, daily_cap as dailyCap, daily_cap_limit as dailyCapLimit, auto_responder as autoResponder, post_to_api as postToApi, ftps_enabled as ftpsEnabled, "
		+ "use_full_csv_header as useFullCsvHeader, coalesce(date_format(created_date,'%Y-%m-%d %T'),'null') as createdDate, coalesce(date_format(modified_date,'%Y-%m-%d %T'),'null') as modifiedDate "
		+ "from email_leads_client elc join supplier_entity_owner seo on seo.supplier_entity_owner_id = elc.supplier_entity_owner_id "
		+ "join supplier s on s.supplier_entity_owner_id = seo.parent_sup_entity_owner_id "
		+ "</script>"
	})
	List<EConnectSettings> getEConnectClients();

	@Select({
		"<script>"
		+ "select supplier_name as supplierName, supplier_entity_owner_id as supplierEntityOwnerId from supplier where supplier_name like '${name}%' "
		+ "and supplier_type_id not in (1934,2109,1145,1742)</script>"
	})
	List<Map<String,Long>> findSupplierByName(@Param("name") String name);
	
	@Results(value={
			@Result(column = "supplier_name", property = "supplierName"),
			@Result(column = "supplier_entity_owner_id", property = "supplierEntityOwnerId", javaType = Integer.class),
			@Result(column = "supplier_entity_owner_id", property = "supplierCategories", javaType = List.class, many=@Many(select="getSupplierCategoriesBySupplierId"))
	})
	@Select({
		"select supplier_name, supplier_entity_owner_id from supplier where supplier_entity_owner_id = #{supplierId} "
		+ "and supplier_type_id not in (1934,2109,1145,1742)"
	})
	ESupplier findSupplierById(@Param("supplierId") long supplierId);

	@Select({
		"select sc.supplier_entity_owner_id, sc.category_id,slf.filter_label from supplier_category sc "
		+ "left join supplier_lead_filter slf on slf.supplier_entity_owner_id = sc.supplier_entity_owner_id "
		+ "and slf.supplier_lead_filter_id = (select max(supplier_lead_filter_id) from supplier_lead_filter where supplier_entity_owner_id = sc.supplier_entity_owner_id) "
		+ "where sc.supplier_id = #{supplierId} "
	})
	@Results(value={
			@Result(column = "supplier_entity_owner_id", property = "supplierCategoryId"),
			@Result(column = "filter_label", property = "filterLabel"),
			@Result(column = "category_id", property = "category", javaType = ECategory.class, one=@One(select="getCategoryById"))
	})
	List<ESupplierCategory> getSupplierCategoriesBySupplierId(@Param("supplierId") int supplierId);

	@Results(value={
			@Result(column = "supplier_entity_owner_id", property = "supplierCategoryId"),
			@Result(column = "filter_label", property = "filterLabel"),
			@Result(column = "category_id", property = "category", javaType = ECategory.class, one=@One(select="getCategoryById"))
	})
	@Select({
		"select sc.supplier_entity_owner_id, sc.category_id, slf.filter_label from supplier_category sc "
		+ "left join supplier_lead_filter slf on slf.supplier_entity_owner_id = sc.supplier_entity_owner_id "
		+ "and slf.supplier_lead_filter_id = (select max(supplier_lead_filter_id) from supplier_lead_filter where supplier_lead_filter.supplier_entity_owner_id = sc.supplier_entity_owner_id) "
		+ "where sc.supplier_entity_owner_id = #{supplierCategoryId} "
	})
	ESupplierCategory getSupplierCategoryById(@Param("supplierCategoryId") int supplierCategoryId);
	
	@Results(value={
			@Result(column = "category_id", property = "categoryId"),
			@Result(column = "display_name", property = "displayName", javaType = String.class)
	})
	@Select(
		"select category_id as categoryId, display_name as displayName from category where category_id = #{categoryId}"
	)
	ECategory getCategoryById(@Param("categoryId") long categoryId);

	@Select({
            "select email_leads_client_id from email_leads_client where supplier_entity_owner_id = #{supplierCategoryId}"
    })
	Integer getEConnectClientId(@Param("supplierCategoryId") long supplierCategoryId);
}
