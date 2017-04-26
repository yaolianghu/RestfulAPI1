package com.buyerzone.model.econnect;

import java.util.List;

public class ESupplier {
	
	private String supplierName;
	private int supplierEntityOwnerId;
	private List<ESupplierCategory> supplierCategories;
	
	public ESupplier(String supplierName, int supplierEntityOwnerId, List<ESupplierCategory> supplierCategories){
		super();
		this.supplierName = supplierName;
		this.supplierEntityOwnerId = supplierEntityOwnerId;
		this.supplierCategories = supplierCategories;
	}
	
	public ESupplier(){
		
	}
	
	/**
	 * @return the supplierName
	 */
	public String getSupplierName() {
		return supplierName;
	}
	/**
	 * @param supplierName the supplierName to set
	 */
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	/**
	 * @return the supplierEntityOwnerId
	 */
	public int getSupplierEntityOwnerId() {
		return supplierEntityOwnerId;
	}
	/**
	 * @param supplierEntityOwnerId the supplierEntityOwnerId to set
	 */
	public void setSupplierEntityOwnerId(int supplierEntityOwnerId) {
		this.supplierEntityOwnerId = supplierEntityOwnerId;
	}

	/**
	 * @return the supplierCategories
	 */
	public List<ESupplierCategory> getSupplierCategories() {
		return supplierCategories;
	}

	/**
	 * @param supplierCategories the supplierCategories to set
	 */
	public void setSupplierCategories(List<ESupplierCategory> supplierCategories) {
		this.supplierCategories = supplierCategories;
	}

}
