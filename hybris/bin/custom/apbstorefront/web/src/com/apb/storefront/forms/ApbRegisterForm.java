package com.apb.storefront.forms;

import java.util.List;

import com.apb.storefront.data.AlbCompanyInfoData;

import de.hybris.platform.acceleratorstorefrontcommons.forms.RegisterForm;


/**
 * @author C5252631
 *
 *         Custom Registration Form
 */
public class ApbRegisterForm extends RegisterForm
{
	private String code;
	private String abnAccountId;
	private String abnNumber;
	private String liquorLicensenumber;
	private String roleOther;
	private String phone;
	private String role;
	private String confirmEmail;
	private boolean customerType;
	private boolean termsCondition;
	private String roleOtherTemp;
	private String email;
	private String accountNumber;
	private String teleFax;
	private String invoiceAccount;
	private String custGroup;
	private String eclAccountGroupId;
	private String eclAccountTypeId;
	private String eclBannerGroupid;
	private String eclFreightAreaId;
	private String eclSubchannelCode;
	private String eclChannelCode;
	private String eclLicenseTypeCode;
	private String eclLicenseClassCode;
	private String apbCompany;
	private String samAccess;
	private List<AlbCompanyInfoData> albCompanyInfoData;

	/**
	 * @return List<String>
	 */
	public List<AlbCompanyInfoData> getAlbCompanyInfoData() {
		return albCompanyInfoData;
	}

	/**
	 * @param albCompanyInfoData
	 */
	public void setAlbCompanyInfoData(List<AlbCompanyInfoData> albCompanyInfoData) {
		this.albCompanyInfoData = albCompanyInfoData;
	}

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
	 * @return the abnAccountId
	 */
	public String getAbnAccountId()
	{
		return abnAccountId;
	}

	/**
	 * @param abnAccountId
	 *           the abnAccountId to set
	 */
	public void setAbnAccountId(final String abnAccountId)
	{
		this.abnAccountId = abnAccountId;
	}

	/**
	 * @return the abnNumber
	 */
	public String getAbnNumber()
	{
		return abnNumber;
	}

	/**
	 * @param abnNumber
	 *           the abnNumber to set
	 */
	public void setAbnNumber(final String abnNumber)
	{
		this.abnNumber = abnNumber;
	}

	/**
	 * @return the liquorLicensenumber
	 */
	public String getLiquorLicensenumber()
	{
		return liquorLicensenumber;
	}

	/**
	 * @param liquorLicensenumber
	 *           the liquorLicensenumber to set
	 */
	public void setLiquorLicensenumber(final String liquorLicensenumber)
	{
		this.liquorLicensenumber = liquorLicensenumber;
	}

	/**
	 * @return the phone
	 */
	public String getPhone()
	{
		return phone;
	}

	/**
	 * @param phone
	 *           the phone to set
	 */
	public void setPhone(final String phone)
	{
		this.phone = phone;
	}

	/**
	 * @return the role
	 */
	public String getRole()
	{
		return role;
	}

	/**
	 * @param role
	 *           the role to set
	 */
	public void setRole(final String role)
	{
		this.role = role;
	}

	/**
	 * @return the customerType
	 */
	public boolean getCustomerType()
	{
		return customerType;
	}

	/**
	 * @param customerType
	 *           the customerType to set
	 */
	public void setCustomerType(final boolean customerType)
	{
		this.customerType = customerType;
	}

	/**
	 * @return the termsCondition
	 */
	public boolean isTermsCondition()
	{
		return termsCondition;
	}

	/**
	 * @param termsCondition
	 *           the termsCondition to set
	 */
	public void setTermsCondition(final boolean termsCondition)
	{
		this.termsCondition = termsCondition;
	}

	/**
	 * @return the email
	 */
	@Override
	public String getEmail()
	{
		return email;
	}

	/**
	 * @param email
	 *           the email to set
	 */
	@Override
	public void setEmail(final String email)
	{
		this.email = email;
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
	 * @return the teleFax
	 */
	public String getTeleFax()
	{
		return teleFax;
	}

	/**
	 * @param teleFax
	 *           the teleFax to set
	 */
	public void setTeleFax(final String teleFax)
	{
		this.teleFax = teleFax;
	}

	/**
	 * @return the invoiceAccount
	 */
	public String getInvoiceAccount()
	{
		return invoiceAccount;
	}

	/**
	 * @param invoiceAccount
	 *           the invoiceAccount to set
	 */
	public void setInvoiceAccount(final String invoiceAccount)
	{
		this.invoiceAccount = invoiceAccount;
	}

	/**
	 * @return the custGroup
	 */
	public String getCustGroup()
	{
		return custGroup;
	}

	/**
	 * @param custGroup
	 *           the custGroup to set
	 */
	public void setCustGroup(final String custGroup)
	{
		this.custGroup = custGroup;
	}

	/**
	 * @return the eclAccountGroupId
	 */
	public String getEclAccountGroupId()
	{
		return eclAccountGroupId;
	}

	/**
	 * @param eclAccountGroupId
	 *           the eclAccountGroupId to set
	 */
	public void setEclAccountGroupId(final String eclAccountGroupId)
	{
		this.eclAccountGroupId = eclAccountGroupId;
	}

	/**
	 * @return the eclAccountTypeId
	 */
	public String getEclAccountTypeId()
	{
		return eclAccountTypeId;
	}

	/**
	 * @param eclAccountTypeId
	 *           the eclAccountTypeId to set
	 */
	public void setEclAccountTypeId(final String eclAccountTypeId)
	{
		this.eclAccountTypeId = eclAccountTypeId;
	}

	/**
	 * @return the eclBannerGroupid
	 */
	public String getEclBannerGroupid()
	{
		return eclBannerGroupid;
	}

	/**
	 * @param eclBannerGroupid
	 *           the eclBannerGroupid to set
	 */
	public void setEclBannerGroupid(final String eclBannerGroupid)
	{
		this.eclBannerGroupid = eclBannerGroupid;
	}

	/**
	 * @return the eclFreightAreaId
	 */
	public String getEclFreightAreaId()
	{
		return eclFreightAreaId;
	}

	/**
	 * @param eclFreightAreaId
	 *           the eclFreightAreaId to set
	 */
	public void setEclFreightAreaId(final String eclFreightAreaId)
	{
		this.eclFreightAreaId = eclFreightAreaId;
	}

	/**
	 * @return the eclSubchannelCode
	 */
	public String getEclSubchannelCode()
	{
		return eclSubchannelCode;
	}

	/**
	 * @param eclSubchannelCode
	 *           the eclSubchannelCode to set
	 */
	public void setEclSubchannelCode(final String eclSubchannelCode)
	{
		this.eclSubchannelCode = eclSubchannelCode;
	}

	/**
	 * @return the eclChannelCode
	 */
	public String getEclChannelCode()
	{
		return eclChannelCode;
	}

	/**
	 * @param eclChannelCode
	 *           the eclChannelCode to set
	 */
	public void setEclChannelCode(final String eclChannelCode)
	{
		this.eclChannelCode = eclChannelCode;
	}

	/**
	 * @return the eclLicenseTypeCode
	 */
	public String getEclLicenseTypeCode()
	{
		return eclLicenseTypeCode;
	}

	/**
	 * @param eclLicenseTypeCode
	 *           the eclLicenseTypeCode to set
	 */
	public void setEclLicenseTypeCode(final String eclLicenseTypeCode)
	{
		this.eclLicenseTypeCode = eclLicenseTypeCode;
	}

	/**
	 * @return the eclLicenseClassCode
	 */
	public String getEclLicenseClassCode()
	{
		return eclLicenseClassCode;
	}

	/**
	 * @param eclLicenseClassCode
	 *           the eclLicenseClassCode to set
	 */
	public void setEclLicenseClassCode(final String eclLicenseClassCode)
	{
		this.eclLicenseClassCode = eclLicenseClassCode;
	}

	/**
	 * @return the apbCompany
	 */
	public String getApbCompany()
	{
		return apbCompany;
	}

	/**
	 * @param apbCompany
	 *           the apbCompany to set
	 */
	public void setApbCompany(final String apbCompany)
	{
		this.apbCompany = apbCompany;
	}

	/**
	 * @return the confirmEmail
	 */
	public String getConfirmEmail()
	{
		return confirmEmail;
	}

	/**
	 * @param confirmEmail
	 *           the confirmEmail to set
	 */
	public void setConfirmEmail(final String confirmEmail)
	{
		this.confirmEmail = confirmEmail;
	}

	/**
	 * @return the roleOther
	 */
	public String getRoleOther()
	{
		return roleOther;
	}

	/**
	 * @param roleOther
	 *           the roleOther to set
	 */
	public void setRoleOther(final String roleOther)
	{
		this.roleOther = roleOther;
	}

	/**
	 * @return the roleOtherTemp
	 */
	public String getRoleOtherTemp()
	{
		return roleOtherTemp;
	}

	/**
	 * @param roleOtherTemp
	 *           the roleOtherTemp to set
	 */
	public void setRoleOtherTemp(final String roleOtherTemp)
	{
		this.roleOtherTemp = roleOtherTemp;
	}

	/**
	 * @return the samAccess
	 */
	public String getSamAccess()
	{
		return samAccess;
	}

	/**
	 * @param samAccess
	 *           the samAccess to set
	 */
	public void setSamAccess(final String samAccess)
	{
		this.samAccess = samAccess;
	}


}
