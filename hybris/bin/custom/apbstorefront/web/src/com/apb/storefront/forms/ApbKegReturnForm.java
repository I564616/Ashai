package com.apb.storefront.forms;

import java.util.List;

import com.apb.facades.kegreturn.data.KegSizeData;


/**
 * @author C5252631
 *
 *         Custom ContactUs Form
 */
public class ApbKegReturnForm
{
	private String code;
	private String accountNumber;
	private String accountName;
	private String contactName;
	private String contactNumber;
	private String emailAddress;
	private String pickupAddress;
	private String kegComments;
	private List<KegSizeData> kegSizeDataList;
	private List<ApbKegReturnKegSizForm> apbKegReturnKegSizForm;

	/**
	 * @return the code
	 */
	public String getCode()
	{
		return code;
	}

	/**
	 * @param code
	 *           the code to set
	 */
	public void setCode(final String code)
	{
		this.code = code;
	}

	/**
	 * @return the accountNumber
	 */
	public String getAccountNumber()
	{
		return accountNumber;
	}

	/**
	 * @param accountNumber
	 *           the accountNumber to set
	 */
	public void setAccountNumber(final String accountNumber)
	{
		this.accountNumber = accountNumber;
	}

	/**
	 * @return the accountName
	 */
	public String getAccountName()
	{
		return accountName;
	}

	/**
	 * @param accountName
	 *           the accountName to set
	 */
	public void setAccountName(final String accountName)
	{
		this.accountName = accountName;
	}

	/**
	 * @return the contactName
	 */
	public String getContactName()
	{
		return contactName;
	}

	/**
	 * @param contactName
	 *           the contactName to set
	 */
	public void setContactName(final String contactName)
	{
		this.contactName = contactName;
	}

	/**
	 * @return the contactNumber
	 */
	public String getContactNumber()
	{
		return contactNumber;
	}

	/**
	 * @param contactNumber
	 *           the contactNumber to set
	 */
	public void setContactNumber(final String contactNumber)
	{
		this.contactNumber = contactNumber;
	}

	/**
	 * @return the emailAddress
	 */
	public String getEmailAddress()
	{
		return emailAddress;
	}

	/**
	 * @param emailAddress
	 *           the emailAddress to set
	 */
	public void setEmailAddress(final String emailAddress)
	{
		this.emailAddress = emailAddress;
	}

	/**
	 * @return the pickupAddress
	 */
	public String getPickupAddress()
	{
		return pickupAddress;
	}

	/**
	 * @param pickupAddress
	 *           the pickupAddress to set
	 */
	public void setPickupAddress(final String pickupAddress)
	{
		this.pickupAddress = pickupAddress;
	}

	/**
	 * @return the kegComments
	 */
	public String getKegComments()
	{
		return kegComments;
	}

	/**
	 * @param kegComments
	 *           the kegComments to set
	 */
	public void setKegComments(final String kegComments)
	{
		this.kegComments = kegComments;
	}

	/**
	 * @return the kegSizeDataList
	 */
	public List<KegSizeData> getKegSizeDataList()
	{
		return kegSizeDataList;
	}

	/**
	 * @param kegSizeDataList
	 *           the kegSizeDataList to set
	 */
	public void setKegSizeDataList(final List<KegSizeData> kegSizeDataList)
	{
		this.kegSizeDataList = kegSizeDataList;
	}

	/**
	 * @return the apbKegReturnKegSizForm
	 */
	public List<ApbKegReturnKegSizForm> getApbKegReturnKegSizForm()
	{
		return apbKegReturnKegSizForm;
	}

	/**
	 * @param apbKegReturnKegSizForm
	 *           the apbKegReturnKegSizForm to set
	 */
	public void setApbKegReturnKegSizForm(final List<ApbKegReturnKegSizForm> apbKegReturnKegSizForm)
	{
		this.apbKegReturnKegSizForm = apbKegReturnKegSizForm;
	}


}
