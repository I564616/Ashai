package com.apb.storefront.sam.payment.form;

/**
 * @author Ganesh.Muddliyar SAM Invoice Form
 */
public class AsahiSAMInvoiceForm
{
	private String docNumber;
	private String remainingAmount;
	private String paidAmount;
	private String documentType;
	private String lineNumber;


	/**
	 * Default constructor
	 */
	public AsahiSAMInvoiceForm()
	{

	}

	/**
	 * @param docNumber
	 * @param remainingAmount
	 * @param documentType
	 */
	public AsahiSAMInvoiceForm(final String docNumber, final String remainingAmount, final String documentType,final String lineNumber)
	{
		this.docNumber = docNumber;
		this.remainingAmount = remainingAmount;
		this.documentType = documentType;
		this.lineNumber = lineNumber;
	}


	/**
	 * @return
	 *
	 */
	public String getRemainingAmount()
	{
		return remainingAmount;
	}

	/**
	 * @param remainingAmount
	 */
	public void setRemainingAmount(final String remainingAmount)
	{
		this.remainingAmount = remainingAmount;
	}

	/**
	 * @return
	 */
	public String getDocNumber()
	{
		return docNumber;
	}

	/**
	 * @param docNumber
	 */
	public void setDocNumber(final String docNumber)
	{
		this.docNumber = docNumber;
	}

	/**
	 * @return
	 */
	public String getDocumentType()
	{
		return documentType;
	}

	/**
	 * @param documentType
	 */
	public void setDocumentType(final String documentType)
	{
		this.documentType = documentType;
	}

	/**
	 * @return
	 */
	public String getPaidAmount()
	{
		return paidAmount;
	}

	/**
	 * @param paidAmount
	 */
	public void setPaidAmount(final String paidAmount)
	{
		this.paidAmount = paidAmount;
	}

	/**
	 * @return the lineNumber
	 */
	public String getLineNumber() {
		return lineNumber;
	}

	/**
	 * @param lineNumber the lineNumber to set
	 */
	public void setLineNumber(String lineNumber) {
		this.lineNumber = lineNumber;
	}


}
