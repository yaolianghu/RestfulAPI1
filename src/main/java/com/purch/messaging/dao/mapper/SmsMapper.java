package com.purch.messaging.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.purch.messaging.model.SmsActivity;
import com.purch.messaging.model.SmsMessage;
import com.purch.messaging.model.SmsOptIn;


public interface SmsMapper {
	String INSERT_SMS_ACTIVITY = 
			"INSERT INTO mds_sms_activity (mobile_number, keyword, msg_id, msg_type, msg_timestamp, msg_text, msg_origin, api_url, api_response, date_created, date_delivered) " +
			"VALUES (#{mobileNumber}, #{keyword}, #{msgId}, #{type}, #{timestamp}, #{textTruncated}, #{origin}, #{apiUrlTruncated}, #{apiResponseTruncated}, NOW(), null)";

	String SELECT_SMS_ACTIVITY =
    		"SELECT mobile_number AS mobileNumber, " +
    		"       keyword, " +
    		"		msg_id AS msgId, " + 
    		"		msg_type AS msgType, " + 
    		"       msg_timestamp AS msgTimestamp, " + 
    		"       msg_text AS msgText, " +
    		"       msg_origin AS msgOrigin, " +
    		"       api_url AS apiUrl, " +
    		"       api_response AS apiResponse, " +
    		"       date_created AS dateCreated, " + 
    		"       date_delivered AS dateDelivered " +
            "  FROM mds_sms_activity " +
    		" WHERE mobile_number = #{mobileNumber} AND keyword = #{keyword} " +
            " ORDER BY date_created DESC";
	
    String INSERT_SMS_OPT_IN =
    		"INSERT INTO mds_sms_opt_in (mobile_number, keyword, msg_id, opt_in_status, msg_timestamp, msg_text, date_created, date_modified) " +
            "VALUES (#{mobileNumber}, #{keyword}, #{msgId}, #{type}, #{timestamp}, #{text}, NOW(), NOW())";
	
    String UPDATE_SMS_OPT_IN =
    		"UPDATE mds_sms_opt_in " +
            "   SET msg_id = #{msgId}, " +
            "       opt_in_status = #{type}, " +
            "       msg_timestamp = #{timestamp}, " +
            "       msg_text = #{text}, " +
            "       date_modified = NOW() " +
            " WHERE mobile_number = #{mobileNumber} AND keyword = #{keyword}";    

    String SELECT_SMS_OPT_IN =
    		"SELECT mobile_number AS mobileNumber, " +
    		"       keyword, " +
    		"       opt_in_status AS optInStatus, " +
    		"		msg_id AS msgId, " + 
    		"       msg_timestamp AS msgTimestamp, " + 
    		"       msg_text AS msgText, " +
    		"       date_created AS dateCreated, " + 
    		"       date_modified AS dateModified " +
            "  FROM mds_sms_opt_in " +
    		" WHERE mobile_number = #{mobileNumber} AND keyword = #{keyword}";
    				
	@Insert(INSERT_SMS_ACTIVITY)
    @Options(flushCache=true)
    public void insertSmsActivity(SmsMessage msg);
	
	@Select(SELECT_SMS_ACTIVITY)
    public List<SmsActivity> selectSmsActivity(@Param("mobileNumber") long mobileNumber,  @Param("keyword") String keyword);	
	
	@Insert(INSERT_SMS_OPT_IN)
	@Options(flushCache=true)
    public void insertSmsOptIn(SmsMessage msg);

	@Update(UPDATE_SMS_OPT_IN)
	@Options(flushCache=true)
    public int updateSmsOptIn(SmsMessage msg);

	@Select(SELECT_SMS_OPT_IN)
    public SmsOptIn selectSmsOptIn(@Param("mobileNumber") long mobileNumber,  @Param("keyword") String keyword);	
}
