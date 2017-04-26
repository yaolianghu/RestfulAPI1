package com.buyerzone.dao.mapper;

import com.buyerzone.model.econnect.EmailOnlyLead;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import javax.sql.RowSet;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by isantiago on 5/18/16.
 */
public interface ReportMapper {
    @Select({
          "select qr.quote_request_id, qr.date_created as 'ERFQ Date', e.email as Email, e.email_leads_log_id as 'ERFQ Number' " +
            "from quote_request qr join email_leads_log e on qr.quote_request_id = e.quote_request_id " +
            "where e.supplier_entity_owner_id = #{supplierEntityOwnerId} " +
            "and e.date_created between #{startDate} and #{endDate}"
    })
    List<LinkedHashMap<String,Object>> getEmailOnlyLeads(@Param("supplierEntityOwnerId") long supplierEntityOwnerId, @Param("startDate") String startDate, @Param("endDate") String endDate);
}
