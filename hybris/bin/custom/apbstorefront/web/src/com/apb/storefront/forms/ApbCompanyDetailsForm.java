package com.apb.storefront.forms;

import java.util.List;

import com.apb.facades.checkout.data.B2BUnitDeliveryAddressData;


/**
 *
 */
public class ApbCompanyDetailsForm
{
	private boolean sameasInvoiceAddress;
	private String accountNumber;
	private String accountName;
	private String tradingName;
	private String abn;
	private String liquorLicense;
	private String companyBillingAddress;
	private String companyPhone;
	private String companyMobilePhone;
	private String companyFax;
	private String companyEmailAddress;
	private String deliveryAddress;
	private String deliveryCalendar;
	private String deliveryInstructions;
	private Integer deliveryTimeFrameFromMM;
	private Integer deliveryTimeFrameFromHH;
	private Integer deliveryTimeFrameToMM;
	private Integer deliveryTimeFrameToHH;
	private List<B2BUnitDeliveryAddressData> b2bUnitDeliveryAddressDataList;
	private List<ApbCompanyDeliveryAddressForm> apbCompanyDeliveryAddressForm;


	/**
	 * @return the sameasInvoiceAddress
	 */
	public boolean isSameasInvoiceAddress()
	{
		return sameasInvoiceAddress;
	}

	/**
	 * @param sameasInvoiceAddress
	 *           the sameasInvoiceAddress to set
	 */
	public void setSameasInvoiceAddress(final boolean sameasInvoiceAddress)
	{
		this.sameasInvoiceAddress = sameasInvoiceAddress;
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
	 * @return the acccountName
	 */
	public String getAcccountName()
	{
		return accountName;
	}

	/**
	 * @param accountName
	 *           the acccountName to set
	 */
	public void setAcccountName(final String accountName)
	{
		this.accountName = accountName;
	}

	/**
	 * @return the tradingName
	 */
	public String getTradingName()
	{
		return tradingName;
	}

	/**
	 * @param tradingName
	 *           the tradingName to set
	 */
	public void setTradingName(final String tradingName)
	{
		this.tradingName = tradingName;
	}

	/**
	 * @return the abn
	 */
	public String getAbn()
	{
		return abn;
	}

	/**
	 * @param abn
	 *           the abn to set
	 */
	public void setAbn(final String abn)
	{
		this.abn = abn;
	}

	/**
	 * @return the liquorLicense
	 */
	public String getLiquorLicense()
	{
		return liquorLicense;
	}

	/**
	 * @param liquorLicense
	 *           the liquorLicense to set
	 */
	public void setLiquorLicense(final String liquorLicense)
	{
		this.liquorLicense = liquorLicense;
	}

	/**
	 * @return the companyBillingAddress
	 */
	public String getCompanyBillingAddress()
	{
		return companyBillingAddress;
	}

	/**
	 * @param companyBillingAddress
	 *           the companyBillingAddress to set
	 */
	public void setCompanyBillingAddress(final String companyBillingAddress)
	{
		this.companyBillingAddress = companyBillingAddress;
	}

	/**
	 * @return the companyPhone
	 */
	public String getCompanyPhone()
	{
		return companyPhone;
	}

	/**
	 * @param companyPhone
	 *           the companyPhone to set
	 */
	public void setCompanyPhone(final String companyPhone)
	{
		this.companyPhone = companyPhone;
	}

	/**
	 * @return the companyMobilePhone
	 */
	public String getCompanyMobilePhone()
	{
		return companyMobilePhone;
	}

	/**
	 * @param companyMobilePhone
	 *           the companyMobilePhone to set
	 */
	public void setCompanyMobilePhone(final String companyMobilePhone)
	{
		this.companyMobilePhone = companyMobilePhone;
	}

	/**
	 * @return the companyFax
	 */
	public String getCompanyFax()
	{
		return companyFax;
	}

	/**
	 * @param companyFax
	 *           the companyFax to set
	 */
	public void setCompanyFax(final String companyFax)
	{
		this.companyFax = companyFax;
	}

	/**
	 * @return the companyEmailAddress
	 */
	public String getCompanyEmailAddress()
	{
		return companyEmailAddress;
	}

	/**
	 * @param companyEmailAddress
	 *           the companyEmailAddress to set
	 */
	public void setCompanyEmailAddress(final String companyEmailAddress)
	{
		this.companyEmailAddress = companyEmailAddress;
	}

	/**
	 * @return the deliveryAddress
	 */
	public String getDeliveryAddress()
	{
		return deliveryAddress;
	}

	/**
	 * @param deliveryAddress
	 *           the deliveryAddress to set
	 */
	public void setDeliveryAddress(final String deliveryAddress)
	{
		this.deliveryAddress = deliveryAddress;
	}

	/**
	 * @return the deliveryCalendar
	 */
	public String getDeliveryCalendar()
	{
		return deliveryCalendar;
	}

	/**
	 * @param deliveryCalendar
	 *           the deliveryCalendar to set
	 */
	public void setDeliveryCalendar(final String deliveryCalendar)
	{
		this.deliveryCalendar = deliveryCalendar;
	}

	/**
	 * @return the deliveryInstructions
	 */
	public String getDeliveryInstructions()
	{
		return deliveryInstructions;
	}

	/**
	 * @param deliveryInstructions
	 *           the deliveryInstructions to set
	 */
	public void setDeliveryInstructions(final String deliveryInstructions)
	{
		this.deliveryInstructions = deliveryInstructions;
	}

	/**
	 * @return the deliveryTimeFrameFromMM
	 */
	public Integer getDeliveryTimeFrameFromMM()
	{
		return deliveryTimeFrameFromMM;
	}

	/**
	 * @param deliveryTimeFrameFromMM
	 *           the deliveryTimeFrameFromMM to set
	 */
	public void setDeliveryTimeFrameFromMM(final Integer deliveryTimeFrameFromMM)
	{
		this.deliveryTimeFrameFromMM = deliveryTimeFrameFromMM;
	}

	/**
	 * @return the deliveryTimeFrameFromHH
	 */
	public Integer getDeliveryTimeFrameFromHH()
	{
		return deliveryTimeFrameFromHH;
	}

	/**
	 * @param deliveryTimeFrameFromHH
	 *           the deliveryTimeFrameFromHH to set
	 */
	public void setDeliveryTimeFrameFromHH(final Integer deliveryTimeFrameFromHH)
	{
		this.deliveryTimeFrameFromHH = deliveryTimeFrameFromHH;
	}

	/**
	 * @return the deliveryTimeFrameToMM
	 */
	public Integer getDeliveryTimeFrameToMM()
	{
		return deliveryTimeFrameToMM;
	}

	/**
	 * @param deliveryTimeFrameToMM
	 *           the deliveryTimeFrameToMM to set
	 */
	public void setDeliveryTimeFrameToMM(final Integer deliveryTimeFrameToMM)
	{
		this.deliveryTimeFrameToMM = deliveryTimeFrameToMM;
	}

	/**
	 * @return the deliveryTimeFrameToHH
	 */
	public Integer getDeliveryTimeFrameToHH()
	{
		return deliveryTimeFrameToHH;
	}

	/**
	 * @param deliveryTimeFrameToHH
	 *           the deliveryTimeFrameToHH to set
	 */
	public void setDeliveryTimeFrameToHH(final Integer deliveryTimeFrameToHH)
	{
		this.deliveryTimeFrameToHH = deliveryTimeFrameToHH;
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
	 * @return the b2bUnitDeliveryAddressDataList
	 */
	public List<B2BUnitDeliveryAddressData> getB2bUnitDeliveryAddressDataList()
	{
		return b2bUnitDeliveryAddressDataList;
	}

	/**
	 * @param b2bUnitDeliveryAddressDataList
	 *           the b2bUnitDeliveryAddressDataList to set
	 */
	public void setB2bUnitDeliveryAddressDataList(final List<B2BUnitDeliveryAddressData> b2bUnitDeliveryAddressDataList)
	{
		this.b2bUnitDeliveryAddressDataList = b2bUnitDeliveryAddressDataList;
	}

	/**
	 * @return the apbCompanyDeliveryAddressForm
	 */
	public List<ApbCompanyDeliveryAddressForm> getApbCompanyDeliveryAddressForm()
	{
		return apbCompanyDeliveryAddressForm;
	}

	/**
	 * @param apbCompanyDeliveryAddressForm
	 *           the apbCompanyDeliveryAddressForm to set
	 */
	public void setApbCompanyDeliveryAddressForm(final List<ApbCompanyDeliveryAddressForm> apbCompanyDeliveryAddressForm)
	{
		this.apbCompanyDeliveryAddressForm = apbCompanyDeliveryAddressForm;
	}


}
