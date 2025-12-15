package com.apb.facades.sam.invoice;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;

import java.util.List;

import org.springframework.ui.Model;

import com.apb.core.exception.AsahiPaymentException;
import com.apb.facades.card.payment.AsahiPaymentDetailsData;
import com.apb.facades.sam.data.AsahiCaptureResponseData;
import com.apb.facades.sam.data.AsahiSAMDetailData;
import com.apb.facades.sam.data.AsahiSAMInvoiceData;
import com.apb.facades.sam.data.AsahiSAMPaymentData;
import com.apb.integration.data.AsahiInvoiceDownloadResponse;


/**
 * The Interface AsahiSAMInvoiceFacade.
 *
 * @author Kuldeep.Singh1
 */
public interface AsahiSAMInvoiceFacade
{

	/**
	 * Import invoice.
	 *
	 * @param invoiceData
	 *           the invoice data
	 */
	void importInvoice(AsahiSAMInvoiceData invoiceData);

	/**
	 * Gets All the invoice.
	 *
	 * @param status
	 *           the invoice status
	 * @param pageableData
	 * @param dueStatus
	 *
	 * @return the invoice details
	 */
	AsahiSAMDetailData getSAMInvoiceList(final String status, final PageableData pageableData, final String documentType,
			final String dueStatus, final String keyword);


	/**
	 * Gets the invoice pdf.
	 *
	 * @param documentNumber
	 *           the document number
	 * @return the invoice pdf
	 */
	AsahiInvoiceDownloadResponse getInvoicePdf(String documentNumber, String lineNumber);

	/**
	 * This Method will make a credit card payment call.
	 *
	 * @param asahiPaymentDetailsData
	 * @throws AsahiPaymentException
	 */
	void makeCreditCardPayment(AsahiPaymentDetailsData asahiPaymentDetailsData) throws AsahiPaymentException;

	/**
	 * Update the total amount for sam payment.
	 *
	 * @param cardType
	 * @param totalAmount
	 * @return
	 */
	String updateTotalwithCreditSurcharge(String cardType, String totalAmount);


	/**
	 * @param cardType
	 * @param asahiSAMPaymentData
	 */
	void updateTotalwithCreditSurcharge(String cardType, AsahiSAMPaymentData asahiSAMPaymentData);

	/**
	 * This Method will make capture request
	 *
	 * @param asahiPaymentDetailsData
	 * @return
	 */
	AsahiCaptureResponseData makePaymentCaptureRequest(AsahiPaymentDetailsData asahiPaymentDetailsData);

	/**
	 * @return Method will Fetch all due now invoices
	 */
	AsahiSAMPaymentData getAllDueNowOpenInvoices();

	/**
	 * This Method will return total amount for all invoices
	 *
	 * @param invoiceDataList
	 * @return
	 */
	Double calculateTotalAmount(final List<AsahiSAMInvoiceData> invoiceDataList);

	/**
	 * Send invoice payment.
	 *
	 * @param asahiSAMPaymentData
	 *           the asahi SAM payment data
	 */
	void sendInvoicePayment(AsahiSAMPaymentData asahiSAMPaymentData);

	/**
	 * Method will check whether to enable or disable the surcharge
	 *
	 * @return is surcharge enabled
	 */
	boolean isAddSurcharge();

	/**
	 * Method to set invoice header item count in session
	 * 
	 * @param model
	 */
	void setSAMHeaderSessionAttributes(Model model);

	/**
	 * Method to generate payment confirmation email
	 * 
	 * @param asahiCaptureResponseData
	 * @param asahiPaymentDetailsData
	 */
	void generatePaymentConfirmationProcess(AsahiCaptureResponseData asahiCaptureResponseData,
			AsahiPaymentDetailsData asahiPaymentDetailsData);
}
