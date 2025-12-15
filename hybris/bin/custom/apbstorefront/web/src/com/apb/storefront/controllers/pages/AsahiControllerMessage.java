package com.apb.storefront.controllers.pages;

/**
 * The Class AsahiControllerMessage.
 * 
 * @author Kuldeep.Singh1
 */
public class AsahiControllerMessage {

	private boolean successKey;
	private boolean errorKey;
	private String message;
	
	/**
	 * @return the successKey
	 */
	public boolean getSuccessKey() {
		return successKey;
	}
	/**
	 * @param successKey the successKey to set
	 */
	public void setSuccessKey(boolean successKey) {
		this.successKey = successKey;
	}
	/**
	 * @return the errorKey
	 */
	public boolean getErrorKey() {
		return errorKey;
	}
	/**
	 * @param errorKey the errorKey to set
	 */
	public void setErrorKey(boolean errorKey) {
		this.errorKey = errorKey;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}
