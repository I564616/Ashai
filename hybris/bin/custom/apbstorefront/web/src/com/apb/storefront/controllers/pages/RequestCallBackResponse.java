package com.apb.storefront.controllers.pages;

import java.util.Map;

public class RequestCallBackResponse {
	
	
	private Map<String,String> errors;
	private Boolean success;
	/**
	 * @return the errors
	 */
	public Map<String, String> getErrors() {
		return errors;
	}
	/**
	 * @param errors the errors to set
	 */
	public void setErrors(Map<String, String> errors) {
		this.errors = errors;
	}
	/**
	 * @return the success
	 */
	public Boolean getSuccess() {
		return success;
	}
	/**
	 * @param success the success to set
	 */
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	

}
