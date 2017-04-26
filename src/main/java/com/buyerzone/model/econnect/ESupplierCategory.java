package com.buyerzone.model.econnect;
/*
 * This class represents a supplier category that can be added to email_leads_client table
 */
public class ESupplierCategory {
	private int supplierCategoryId;
	private ECategory category;
	private String filterLabel;
	
	public ESupplierCategory(int supplierCategoryId, ECategory category, String filterLabel){
		super();
		this.supplierCategoryId = supplierCategoryId;
		this.category = category;
		this.filterLabel = filterLabel;
	}
	
	public ESupplierCategory(){
		
	}
	/**
	 * @return the supplierCategoryId
	 */
	public int getSupplierCategoryId() {
		return supplierCategoryId;
	}
	/**
	 * @param supplierCategoryId the supplierCategoryId to set
	 */
	public void setSupplierCategoryId(int supplierCategoryId) {
		this.supplierCategoryId = supplierCategoryId;
	}
	/**
	 * @return the category
	 */
	public ECategory getCategory() {
		return category;
	}
	/**
	 * @param category the category to set
	 */
	public void setCategory(ECategory category) {
		this.category = category;
	}
	/**
	 * @return the filter
	 */
	public String getFilterLabel() {
		return filterLabel;
	}
	/**
	 * @param filter the filter to set
	 */
	public void setFilterLabel(String filterLabel) {
		this.filterLabel = filterLabel;
	}
}
