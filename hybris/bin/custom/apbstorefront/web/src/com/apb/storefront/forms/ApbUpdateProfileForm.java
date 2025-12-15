package com.apb.storefront.forms;

import java.util.List;

/**
 * Apb Specific Update Profile Form
 */
public class ApbUpdateProfileForm
{
	private String mobileNumber;
	private String firstName;
	private String lastName;
	private String emailAddress;
	private String titleCode;
	private Boolean disableEmailNotification;
	private List<AsahiNotificationPrefForm> notificationPrefs;


	public Boolean getDisableEmailNotification() {
		return disableEmailNotification;
	}

	public void setDisableEmailNotification(Boolean disableEmailNotification) {
		this.disableEmailNotification = disableEmailNotification;
	}

	/**
	 * @return title code
	 */
	public String getTitleCode()
	{
		return titleCode;
	}

	/**
	 * @param titleCode
	 */
	public void setTitleCode(final String titleCode)
	{
		this.titleCode = titleCode;
	}

	/**
	 * @return mobile Number
	 */
	public String getMobileNumber()
	{
		return mobileNumber;
	}

	/**
	 * @param mobileNumber
	 */
	public void setMobileNumber(final String mobileNumber)
	{
		this.mobileNumber = mobileNumber;
	}

	/**
	 * @return first Name
	 */
	public String getFirstName()
	{
		return firstName;
	}

	/**
	 * @param firstName
	 */
	public void setFirstName(final String firstName)
	{
		this.firstName = firstName;
	}

	/**
	 * @return Last Name
	 */
	public String getLastName()
	{
		return lastName;
	}

	/**
	 * @param lastName
	 */
	public void setLastName(final String lastName)
	{
		this.lastName = lastName;
	}

	/**
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * @param emailAddress the emailAddress to set
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * @return the notificationPrefs
	 */
	public List<AsahiNotificationPrefForm> getNotificationPrefs() {
		return notificationPrefs;
	}

	/**
	 * @param notificationPrefs the notificationPrefs to set
	 */
	public void setNotificationPrefs(List<AsahiNotificationPrefForm> notificationPrefs) {
		this.notificationPrefs = notificationPrefs;
	}



}
