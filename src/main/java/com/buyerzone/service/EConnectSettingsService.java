package com.buyerzone.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.buyerzone.dao.mapper.EConnectMapper;
import com.buyerzone.model.econnect.ECategory;
import com.buyerzone.model.econnect.EConnectSettings;
import com.buyerzone.model.econnect.ESupplier;
import com.buyerzone.model.econnect.ESupplierCategory;

@Service
public class EConnectSettingsService {

	@Autowired
	private EConnectMapper eConnectMapper;
	
	public int insertEConnectSetting(EConnectSettings eConnectSettings) {
		return eConnectMapper.insertEConnectSetting(eConnectSettings);
	}

	public int updateEConnectSetting(EConnectSettings eConnectSettings) {
		return eConnectMapper.updateEConnectSetting(eConnectSettings);
	}
	
	public EConnectSettings getEConnectClient(long eConnectClientId){
		return eConnectMapper.getEConnectClient(eConnectClientId);
	}
	
	public EConnectSettings getEConnectSettingsBySupplierId(int eConnectSupplierId){
		return eConnectMapper.getEConnectSettingsBySupplierId(eConnectSupplierId);
	}
	
	public List<EConnectSettings> getEConnectClients() throws SQLException{
		return eConnectMapper.getEConnectClients();
	}
	
	public List<Map<String,Long>> findSupplierByName(String name) throws SQLException{
		return eConnectMapper.findSupplierByName(name);
	}
	
	public ESupplier findSupplierById(long supplierId){
		return eConnectMapper.findSupplierById(supplierId);
	}
	
	public List<ESupplierCategory> getSupplierCategoriesBySupplierId(int supplierId){
		return eConnectMapper.getSupplierCategoriesBySupplierId(supplierId);
	}
	
	public ESupplierCategory getSupplierCategoryById(int supplierCategoryId){
		return eConnectMapper.getSupplierCategoryById(supplierCategoryId);
	}
	
	public ECategory getCategoryById(long categoryId){
		return eConnectMapper.getCategoryById(categoryId);
	}

    public Integer getEConnectClientId(long supplierCategoryId){
        return eConnectMapper.getEConnectClientId(supplierCategoryId);
    }
}
