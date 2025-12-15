package com.apb.storefront.forms;

import org.springframework.web.multipart.MultipartFile;
import com.apb.storefront.forms.DiscrepancyForm;
import java.util.List;
/**
 * @author C5252631
 *
 *         Custom ContactUs Form
 */
public class ApbContactUsForm
{
	private String code;
	private String accountNumber;
	private String companyName;
	private String name;
	private String emailAddress;
	private String contactNumber;
	private String subject; // list box
	private String subjectOther;
	private String subjectFlag;
	private MultipartFile pdfFile;	//send a request to delivery with parameter of delivery number
	private String asahiSalesRepEmail;
	private String asahiSalesRepName;
	private boolean showSalesRep;
	private boolean activeSalesRep;



	private List<DiscrepancyForm> discrepancies; /// [ material, qty, price], [material, qty, price] [,material, ]
	private String deliveryNumber;
	private String addInfo;
	private String enquiryType;
	private String enquirySubType;
	private String message;
	private String discrepancyRowError;

	public String getDiscrepancyRowError() {
		return discrepancyRowError;
	}

	public void setDiscrepancyRowError(String discrepancyRowError) {
		this.discrepancyRowError = discrepancyRowError;
	}

	/**
	 * @return
	 */
	public boolean isShowSalesRep()
	{
		return showSalesRep;
	}

	/**
	 * @param showSalesRep
	 */
	public void setShowSalesRep(final boolean showSalesRep)
	{
		this.showSalesRep = showSalesRep;
	}

	/**
	 * @return
	 */
	public boolean isActiveSalesRep()
	{
		return activeSalesRep;
	}

	/**
	 * @param activeSalesRep
	 */
	public void setActiveSalesRep(final boolean activeSalesRep)
	{
		this.activeSalesRep = activeSalesRep;
	}

	/**
	 * @return
	 */
	public String getAsahiSalesRepEmail()
	{
		return asahiSalesRepEmail;
	}

	/**
	 * @param asahiSalesRepEmail
	 */
	public void setAsahiSalesRepEmail(final String asahiSalesRepEmail)
	{
		this.asahiSalesRepEmail = asahiSalesRepEmail;
	}

	/**
	 * @return
	 */
	public String getAsahiSalesRepName()
	{
		return asahiSalesRepName;
	}

	/**
	 * @param asahiSalesRepName
	 */
	public void setAsahiSalesRepName(final String asahiSalesRepName)
	{
		this.asahiSalesRepName = asahiSalesRepName;
	}

	/**
	 * @return the subjectOther
	 */
	public String getSubjectOther()
	{
		return subjectOther;
	}

	/**
	 * @param subjectOther
	 *           the subjectOther to set
	 */
	public void setSubjectOther(final String subjectOther)
	{
		this.subjectOther = subjectOther;
	}

	private String furtherDetail;

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
	 * @return the companyName
	 */
	public String getCompanyName()
	{
		return companyName;
	}

	/**
	 * @param companyName
	 *           the companyName to set
	 */
	public void setCompanyName(final String companyName)
	{
		this.companyName = companyName;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name
	 *           the name to set
	 */
	public void setName(final String name)
	{
		this.name = name;
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
	 * @return the subject
	 */
	public String getSubject()
	{
		return subject;
	}

	/**
	 * @param subject
	 *           the subject to set
	 */
	public void setSubject(final String subject)
	{
		this.subject = subject;
	}

	/**
	 * @return the furtherDetail
	 */
	public String getFurtherDetail()
	{
		return furtherDetail;
	}

	/**
	 * @param furtherDetail
	 *           the furtherDetail to set
	 */
	public void setFurtherDetail(final String furtherDetail)
	{
		this.furtherDetail = furtherDetail;
	}

	/**
	 * @return the pdfFile
	 */
	public MultipartFile getPdfFile()
	{
		return pdfFile;
	}

	/**
	 * @param pdfFile
	 *           the pdfFile to set
	 */
	public void setPdfFile(final MultipartFile pdfFile)
	{
		this.pdfFile = pdfFile;
	}

	/**
	 * @return the subjectFlag
	 */
	public String getSubjectFlag()
	{
		return subjectFlag;
	}

	/**
	 * @param subjectFlag
	 *           the subjectFlag to set
	 */
	public void setSubjectFlag(final String subjectFlag)
	{
		this.subjectFlag = subjectFlag;
	}

	public List<DiscrepancyForm> getDiscrepancies() {
		return discrepancies;
	}

	public void setDiscrepancies(List<DiscrepancyForm> discrepancies) {
		this.discrepancies = discrepancies;
	}

	public String getDeliveryNumber() {
		return deliveryNumber;
	}

	public void setDeliveryNumber(String deliveryNumber) {
		this.deliveryNumber = deliveryNumber;
	}

	public String getAddInfo() {
		return addInfo;
	}

	public void setAddInfo(String addInfo) {
		this.addInfo = addInfo;
	}

	public String getEnquiryType() {
		return enquiryType;
	}

	public void setEnquiryType(String enquiryType) {
		this.enquiryType = enquiryType;
	}

	public String getEnquirySubType() {
		return enquirySubType;
	}

	public void setEnquirySubType(String enquirySubType) {
		this.enquirySubType = enquirySubType;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
