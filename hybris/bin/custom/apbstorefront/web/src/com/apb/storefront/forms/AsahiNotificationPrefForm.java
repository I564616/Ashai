package com.apb.storefront.forms;

public class AsahiNotificationPrefForm {

	
	private String notificationType;
	
	private boolean emailEnabled;
	/**
	 * @return the notificationType
	 */
	public String getNotificationType() {
		return notificationType;
	}
	/**
	 * @param notificationType the notificationType to set
	 */
	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}
	/**
	 * @return the emailEnabled
	 */
	public boolean getEmailEnabled() {
		return emailEnabled;
	}
	/**
	 * @param emailEnabled the emailEnabled to set
	 */
	public void setEmailEnabled(boolean emailEnabled) {
		this.emailEnabled = emailEnabled;
	}
	
}
