package com.apb.facades.sam.invoice.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.notificationservices.enums.NotificationType;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.ui.Model;

import com.apb.core.card.payment.AsahiCreditCardPaymentService;
import com.apb.core.card.payment.AsahiPaymentCaptureRequestService;
import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.event.AsahiPaymentConfirmationEvent;
import com.apb.core.exception.AsahiPaymentException;
import com.apb.core.integration.AsahiIntegrationPointsServiceImpl;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.service.sam.invoice.AsahiSAMInvoiceService;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.card.payment.AsahiPaymentDetailsData;
import com.apb.facades.sam.data.AsahiCaptureResponseData;
import com.apb.facades.sam.data.AsahiSAMDetailData;
import com.apb.facades.sam.data.AsahiSAMInvoiceData;
import com.apb.facades.sam.data.AsahiSAMPaymentData;
import com.apb.facades.sam.invoice.AsahiSAMInvoiceFacade;
import com.apb.integration.data.AsahiInvoiceDownloadResponse;
import com.apb.integration.data.AsahiInvoiceDownloadResponseDTO;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.AsahiSAMInvoiceModel;
import com.sabmiller.core.notification.service.NotificationService;


/**
 * The Class AsahiSAMInvoiceFacadeImpl.
 *
 * @author Kuldeep.Singh1
 */
public class AsahiSAMInvoiceFacadeImpl implements AsahiSAMInvoiceFacade
{
	private static final Logger LOG = Logger.getLogger(AsahiSAMInvoiceFacadeImpl.class);

	/** The CONSTANT for Invoice Status OPEN */
	private static final String INVOICE_STATUS_OPEN = "open";

	/** The CONSTANT for Invoice Status CLOSE */
	private static final String INVOICE_STATUS_CLOSED = "closed";

	/** The CONSTANT for DOCUMENT_TYPE_CREDIT */
	private static final String DOCUMENT_TYPE_CREDIT = "credit";

	/** The CONSTANT for DOCUMENT_TYPE_INVOICE */
	private static final String DOCUMENT_TYPE_INVOICE = "invoice";

	/** The CONSTANT for DUESTATUS_DUENOW */
	private static final String DUESTATUS_DUENOW = "dueNow";

	/** The CONSTANT for DUESTATUS_NOTYETDUE */
	private static final String DUESTATUS_NOTYETDUE = "notYetDue";

	private static final String NEGATIVE_SYMBOL = "-";

	/** The Constant INVOICE_PAYMENT. */
	private static final String INVOICE_PAYMENT = "payment";

	/**
	 * The ADD SURCHARGE CONFIG
	 */
	private static final String IS_ADD_SURCHARGE = "sam.payment.apply.credit.surcharge";

	/**
	 * THE CONSTANT FOR AMEX CARD SURCHARGE
	 */
	private static final String SAM_CREDIT_SURCHARGE_FOR_AMEX = "sam.credit.surcharge.for.amex.card";
	/**
	 * THE CONSTANT FOR VISA CARD SURCHARGE
	 */
	private static final String SAM_CREDIT_SURCHARGE_FOR_VISA = "sam.credit.surcharge.for.visa.card";
	/**
	 * THE CONSTANT FOR MASTER CARD SURCHARGE
	 */
	private static final String SAM_CREDIT_SURCHARGE_FOR_MASTER = "sam.credit.surcharge.for.master.card";

	/** The CONSTANT for DOLLAR_SYMBOL */
	private static final String DOLLAR_SYMBOL = "$";

	/** The asahi SAM invoice reverse converter. */
	private Converter<AsahiSAMInvoiceData, AsahiSAMInvoiceModel> asahiSAMInvoiceReverseConverter;

	/** The model service. */
	@Resource
	private ModelService modelService;

	/** The asahi SAM invoice service. */
	@Resource
	private AsahiSAMInvoiceService asahiSAMInvoiceService;

	/** The asahi SAM invoice reverse converter. */
	private Converter<AsahiSAMInvoiceModel, AsahiSAMInvoiceData> asahiSAMInvoiceConverter;

	/** The user service. */
	@Resource
	private UserService userService;

	/** The asahi integration points service. */
	@Resource
	private AsahiIntegrationPointsServiceImpl asahiIntegrationPointsService;

	/** The asahi configuration service. */
	@Resource
	private AsahiConfigurationService asahiConfigurationService;

	@Resource(name = "asahiCreditCardPaymentService")
	private AsahiCreditCardPaymentService asahiCreditCardPaymentService;

	@Resource(name = "asahiPaymentCaptureRequestService")
	private AsahiPaymentCaptureRequestService asahiPaymentCaptureRequestService;

	@Resource
	private SessionService sessionService;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	/** The event service. */
	@Resource(name = "eventService")
	private EventService eventService;

	/** The common I 18 N service. */
	@Resource(name = "commonI18NService")
	private CommonI18NService commonI18NService;

	/** The base store service. */
	@Resource(name = "baseStoreService")
	private BaseStoreService baseStoreService;

	/** The base site service. */
	@Resource(name = "baseSiteService")
	private BaseSiteService baseSiteService;

	@Resource(name = "notificationService")
	private NotificationService notificationService;

	/**
	 * Import invoice.
	 *
	 * @param invoiceData
	 *           the invoice data
	 */
	@Override
	public void importInvoice(final AsahiSAMInvoiceData invoiceData)
	{
		// Fetching Invoice based on document number
		AsahiSAMInvoiceModel existingInvoice = this.asahiSAMInvoiceService
				.getInvoiceByDocumentNumber(invoiceData.getDocumentNumber(), invoiceData.getLineNumber());
		/* Check if Invoice already exist in hybris if yes then update otherwise create new. */
		if (null != existingInvoice)
		{
			// update existing invoice
			// calling converter to populate the AsahiSAMInvoiceModel
			existingInvoice = this.asahiSAMInvoiceReverseConverter.convert(invoiceData, existingInvoice);
			// saving existing Invoice into hybris database
			this.modelService.save(existingInvoice);
		}
		else
		{
			//create new Invoice in hybris
			AsahiSAMInvoiceModel newInvoice = this.modelService.create(AsahiSAMInvoiceModel.class);

			//calling converter to populate the AsahiSAMInvoiceModel
			newInvoice = this.asahiSAMInvoiceReverseConverter.convert(invoiceData, newInvoice);

			//saving new Invoice into hybris database
			this.modelService.save(newInvoice);
		}

	}

	/**
	 * Gets All the invoice by status.
	 *
	 * @return the List of Invoices data
	 */
	@Override
	public AsahiSAMDetailData getSAMInvoiceList(final String status, final PageableData pageableData, final String documentType,
			final String dueStatus, final String keyword)
	{

		final AsahiSAMDetailData invoicePageData = new AsahiSAMDetailData();
		final List<AsahiSAMInvoiceData> invoiceDataList = new ArrayList<AsahiSAMInvoiceData>();

		final UserModel user = userService.getCurrentUser();
		if (user instanceof B2BCustomerModel)
		{
			final B2BUnitModel b2bUnit = ((B2BCustomerModel) user).getDefaultB2BUnit();

			if (null != b2bUnit && b2bUnit instanceof AsahiB2BUnitModel)
			{
				final AsahiB2BUnitModel payerAccount = ((AsahiB2BUnitModel) b2bUnit).getPayerAccount();
				final String cofoDate = payerAccount.getCooDate();
				if(null != payerAccount){

					List<AsahiSAMInvoiceModel> invoicelList = Collections.emptyList();

					if(!(INVOICE_STATUS_CLOSED.equalsIgnoreCase(status) && StringUtils.isNotEmpty(dueStatus))){
						invoicelList = this.asahiSAMInvoiceService.getSAMInvoiceList(status,
								payerAccount.getUid(), pageableData, documentType, dueStatus, keyword, cofoDate);
					}

					if (CollectionUtils.isNotEmpty(invoicelList))
					{
						//populate the invoices data...
						invoicelList.forEach(invoice -> {
							invoiceDataList.add(this.asahiSAMInvoiceConverter.convert(invoice));
						});
						invoicePageData.setInvoices(invoiceDataList);
					}

					if(pageableData.getCurrentPage() == 0){

						setInvoiceCounts(status, documentType, dueStatus, keyword, invoicePageData, payerAccount);

					}
				}
			}
		}
		return invoicePageData;
	}

	/**
	 * @param documentType
	 * @param dueStatus
	 * @param keyword
	 * @param invoicePageData
	 * @param payerAccount
	 * set Invoice Page Count values
	 */
	private void setInvoiceCounts(final String status, final String documentType, final String dueStatus, final String keyword,
			final AsahiSAMDetailData invoicePageData, final AsahiB2BUnitModel payerAccount){


		final String cofoDate = payerAccount.getCooDate();
		if(!(INVOICE_STATUS_CLOSED.equalsIgnoreCase(status) && StringUtils.isNotEmpty(dueStatus))){
			invoicePageData.setCreditCount(this.asahiSAMInvoiceService.getSAMInvoiceCount(status,
					payerAccount.getUid(), DOCUMENT_TYPE_CREDIT, dueStatus, keyword, cofoDate));

			invoicePageData.setInvoiceCount(this.asahiSAMInvoiceService.getSAMInvoiceCount(status,
					payerAccount.getUid(), DOCUMENT_TYPE_INVOICE, dueStatus, keyword, cofoDate));
		}
		else{
			invoicePageData.setCreditCount(0);
			invoicePageData.setInvoiceCount(0);
		}

		invoicePageData.setOpenCount(this.asahiSAMInvoiceService.getSAMInvoiceCount(INVOICE_STATUS_OPEN,
					payerAccount.getUid(), documentType, dueStatus, keyword, cofoDate));

		if(INVOICE_STATUS_OPEN.equalsIgnoreCase(status)){
				invoicePageData.setDueNowCount(this.asahiSAMInvoiceService.getSAMInvoiceCount(status,
						payerAccount.getUid(), documentType, DUESTATUS_DUENOW, keyword, cofoDate));

			invoicePageData.setNotYetDueCount(this.asahiSAMInvoiceService.getSAMInvoiceCount(status,
						payerAccount.getUid(), documentType, DUESTATUS_NOTYETDUE, keyword, cofoDate));
		}
		else{
			invoicePageData.setDueNowCount(0);
			invoicePageData.setNotYetDueCount(0);
		}
		if(StringUtils.isEmpty(dueStatus)){
			invoicePageData.setClosedCount(this.asahiSAMInvoiceService.getSAMInvoiceCount(INVOICE_STATUS_CLOSED,
					payerAccount.getUid(), documentType, null, keyword, cofoDate));
		}
		else
		{
			invoicePageData.setClosedCount(0);
		}

		invoicePageData.setInvoicePageSize(Integer
					.parseInt(asahiConfigurationService.getString(ApbCoreConstants.SAM_INVOICE_HISTORY_PAGESIZE, "10")));
	}

	/**
	 * This method returns balance in the formatted String
	 * 
	 * @param balance
	 * @return
	 */
	private String getInvoiceBalanceValue(final String balance) {
		if(StringUtils.isNotEmpty(balance) && balance.contains("-")){
			return "-".concat(DOLLAR_SYMBOL).concat(balance.replace("-", ""));
		}
		return "";
	}

	/**
	 * Gets the invoice pdf.
	 *
	 * @return the invoice pdf
	 */
	@Override
	public AsahiInvoiceDownloadResponse getInvoicePdf(final String documentNumber,final String lineNumber)
	{
		final AsahiInvoiceDownloadResponseDTO resposne = this.asahiIntegrationPointsService.getInvoicePdf(documentNumber,lineNumber);
		if (null != resposne && null != resposne.getInvoiceDownloadResponse())
		{
			return resposne.getInvoiceDownloadResponse();
		}
		return null;
	}


	/**
	 * @return the asahiSAMInvoiceReverseConverter
	 */
	public Converter<AsahiSAMInvoiceData, AsahiSAMInvoiceModel> getAsahiSAMInvoiceReverseConverter()
	{
		return asahiSAMInvoiceReverseConverter;
	}

	/**
	 * @param asahiSAMInvoiceReverseConverter
	 *           the asahiSAMInvoiceReverseConverter to set
	 */
	public void setAsahiSAMInvoiceReverseConverter(
			final Converter<AsahiSAMInvoiceData, AsahiSAMInvoiceModel> asahiSAMInvoiceReverseConverter)
	{
		this.asahiSAMInvoiceReverseConverter = asahiSAMInvoiceReverseConverter;
	}

	/**
	 * @return
	 */
	public Converter<AsahiSAMInvoiceModel, AsahiSAMInvoiceData> getAsahiSAMInvoiceConverter()
	{
		return asahiSAMInvoiceConverter;
	}

	/**
	 * @param asahiSAMInvoiceConverter
	 */
	public void setAsahiSAMInvoiceConverter(final Converter<AsahiSAMInvoiceModel, AsahiSAMInvoiceData> asahiSAMInvoiceConverter)
	{
		this.asahiSAMInvoiceConverter = asahiSAMInvoiceConverter;
	}

	/*
	 * This method is used for making credit card payment
	 *
	 * @param asahiPaymentDetailsData
	 *
	 * @see com.apb.facades.sam.invoice.AsahiSAMInvoiceFacade#makeCreditCardPayment(com.apb.facades.card.payment.
	 * AsahiPaymentDetailsData)
	 */
	public void makeCreditCardPayment(final AsahiPaymentDetailsData asahiPaymentDetailsData) throws AsahiPaymentException
	{
		if (null != asahiPaymentDetailsData)
		{
			asahiCreditCardPaymentService.makeCreditCardPaymentRequest(asahiPaymentDetailsData);
		}

	}

	@Override
	public String updateTotalwithCreditSurcharge(final String cardType, final String totalAmount)
	{

		final double finalTotalAmt = calculateTotalPriceWithSurcharge(cardType, totalAmount);
		return String.format("%.2f", finalTotalAmt);

	}

	private double calculateTotalPriceWithSurcharge(final String cardType, final String totalAmount)
	{
		CreditCardType creditCardType = null;
		String surcharge = null;
		double finalTotalAmt = 0.0;
		double surchargeValue = 0.0;
		double totalPrice = 0.0;
		if (StringUtils.isNotEmpty(cardType))
		{
			if ("MASTERCARD".equalsIgnoreCase(cardType))
			{
				creditCardType = CreditCardType.valueOf("MASTER");
			}
			else
			{
				creditCardType = CreditCardType.valueOf(cardType.toUpperCase());
			}
		}

		if (!isAddSurcharge())
		{
			surcharge = "0";
		}
		else if (creditCardType.equals(CreditCardType.AMEX))
		{
			surcharge = asahiConfigurationService.getString(SAM_CREDIT_SURCHARGE_FOR_AMEX, "0");
		}
		else if (creditCardType.equals(CreditCardType.VISA))
		{
			surcharge = asahiConfigurationService.getString(SAM_CREDIT_SURCHARGE_FOR_VISA, "0");
		}
		else
		{
			surcharge = asahiConfigurationService.getString(SAM_CREDIT_SURCHARGE_FOR_MASTER, "0");
		}

		try
		{
			surchargeValue = Double.parseDouble(surcharge);
			totalPrice = Double.parseDouble(totalAmount);
		}
		catch (final NumberFormatException ex)
		{
			LOG.error("Failed to get double value ", ex);
		}

		final double creditSurcharge = (totalPrice * surchargeValue) / 100;
		final BigDecimal creditSurchargeVal = BigDecimal.valueOf(creditSurcharge);
		final double creditValue = creditSurchargeVal.setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();

		finalTotalAmt = totalPrice + creditValue;
		return finalTotalAmt;
	}

	@Override
	public void updateTotalwithCreditSurcharge(final String cardType, final AsahiSAMPaymentData asahiSAMPaymentData)
	{
		final double finalTotalAmt = calculateTotalPriceWithSurcharge(cardType, asahiSAMPaymentData.getTotalAmount().toString());
		LOG.info("Total Amount " + finalTotalAmt);
		asahiSAMPaymentData.setTotalAmount(finalTotalAmt);
	}

	/**
	 * @return Method to check whether surcharge is enabled or not.
	 */
	public boolean isAddSurcharge()
	{
		return Boolean.parseBoolean(asahiConfigurationService.getString(IS_ADD_SURCHARGE, "false"));
	}

	/*
	 * Method will make capture request.
	 *
	 * @see com.apb.facades.sam.invoice.AsahiSAMInvoiceFacade#makePaymentCaptureRequest(com.apb.facades.card.payment.
	 * AsahiPaymentDetailsData)
	 */
	@Override
	public AsahiCaptureResponseData makePaymentCaptureRequest(final AsahiPaymentDetailsData asahiPaymentDetailsData)
	{
		AsahiCaptureResponseData asahiCaptureResponseData = null;
		if (null != asahiPaymentDetailsData && null != asahiPaymentDetailsData.getAsahiSAMPaymentData()
				&& CollectionUtils.isNotEmpty(asahiPaymentDetailsData.getAsahiSAMPaymentData().getInvoice()))
		{
			final List<AsahiSAMInvoiceData> invoiceList = asahiPaymentDetailsData.getAsahiSAMPaymentData().getInvoice();
			if (null != invoiceList.get(0))
			{

				final AsahiSAMInvoiceModel asahiSAMInvoiceModel = this.asahiSAMInvoiceService
						.getInvoiceByDocumentNumber(invoiceList.get(0).getDocumentNumber(), invoiceList.get(0).getLineNumber());
				if (null != asahiSAMInvoiceModel)
				{
					asahiCaptureResponseData = this.asahiPaymentCaptureRequestService
							.createSAMPaymentCaptureRequest(asahiSAMInvoiceModel);
				}
			}
		}
		return asahiCaptureResponseData;
	}

	/*
	 * This Method will return all open due now invoices.
	 *
	 * @see com.apb.facades.sam.invoice.AsahiSAMInvoiceFacade#getAllDueNowOpenInvoices()
	 */
	@Override
	public AsahiSAMPaymentData getAllDueNowOpenInvoices()
	{
		final AsahiSAMPaymentData invoiceData = new AsahiSAMPaymentData();
		final List<AsahiSAMInvoiceData> invoiceDataList = new ArrayList<AsahiSAMInvoiceData>();
		List<AsahiSAMInvoiceModel> invoicelList = new ArrayList<>();

		final UserModel user = userService.getCurrentUser();
		if (user instanceof B2BCustomerModel)
		{
			final B2BCustomerModel customer = (B2BCustomerModel) user;
			final B2BUnitModel b2bUnit = customer.getDefaultB2BUnit();

			final AsahiB2BUnitModel asahib2bUnit = (AsahiB2BUnitModel) b2bUnit;

			if (null != asahib2bUnit.getPayerAccount())
			{
				invoicelList = this.asahiSAMInvoiceService.getAllDueNowOpenInvoices(asahib2bUnit.getPayerAccount().getUid());
			}

			if (CollectionUtils.isNotEmpty(invoicelList))
			{
				//populate the invoices data...
				for(final AsahiSAMInvoiceModel invoice : invoicelList){
					if(!invoice.isPaymentMade()){
						invoiceDataList.add(this.asahiSAMInvoiceConverter.convert(invoice));
					}
				}

				invoiceData.setInvoice(invoiceDataList);

			}
		}

		return invoiceData;
	}

	/**
	 * This Method will calculate total amount of all invoices
	 *
	 * @param invoiceData
	 * @param invoiceDataList
	 */
	public Double calculateTotalAmount(final List<AsahiSAMInvoiceData> invoiceDataList)
	{
		Double totalAmount = 0.0;
		if (CollectionUtils.isNotEmpty(invoiceDataList))
		{
			for (final AsahiSAMInvoiceData data : invoiceDataList)
			{
				try
				{
					if (StringUtils.isNotEmpty(data.getRemainingAmount()))
					{
						totalAmount = totalAmount + Double.parseDouble(data.getRemainingAmount());
					}
				}
				catch (final NumberFormatException ex)
				{
					LOG.error("Failed to get remaining double amount value", ex);
				}
			}
		}
		return totalAmount;


	}

	/**
	 * Send invoice payment.
	 *
	 * @param asahiSAMPaymentData
	 *           the asahi SAM payment data
	 */
	@Override
	public void sendInvoicePayment(final AsahiSAMPaymentData asahiSAMPaymentData)
	{
		this.asahiIntegrationPointsService.sendInvoicePayment(asahiSAMPaymentData);
	}

	@Override
	public void setSAMHeaderSessionAttributes(final Model model) {

		if(!this.asahiConfigurationService.getBoolean("populate.sql.parameter",Boolean.TRUE)) {
			return;
		}
		final UserModel user = userService.getCurrentUser();
		final B2BUnitModel b2bUnit = ((B2BCustomerModel) user).getDefaultB2BUnit();
		final String payerAccount = sessionService.getAttribute("payerAccountID");
		final AsahiB2BUnitModel payerAccountModel = ((AsahiB2BUnitModel) b2bUnit).getPayerAccount();
		LOG.info("Setting SAM Header for payer : " + payerAccount);
		final String cofoDate = payerAccountModel.getCooDate();

		if(StringUtils.isNotEmpty(payerAccount)) {
			final String curSymbol = asahiSiteUtil.getCurrencySymbol();
			Double bal = this.asahiSAMInvoiceService.getSAMInvoiceSum(INVOICE_STATUS_OPEN,
   				payerAccount, DOCUMENT_TYPE_INVOICE, null, null, cofoDate) - this.asahiSAMInvoiceService.getSAMInvoiceSum(INVOICE_STATUS_OPEN,
   						payerAccount, DOCUMENT_TYPE_CREDIT, null, null, cofoDate);

			final Double balWithPayment = this.asahiSAMInvoiceService.getSAMInvoiceSum(INVOICE_PAYMENT,
	   				payerAccount, INVOICE_PAYMENT, null, null, cofoDate);

			bal = bal-balWithPayment;
			final String totalBalance = String.format("%,.2f",bal);
			model.addAttribute("totalBalance", bal>=0 ? curSymbol +totalBalance : NEGATIVE_SYMBOL + curSymbol
					+ totalBalance.replaceAll(NEGATIVE_SYMBOL, StringUtils.EMPTY));

			Double dueBal = this.asahiSAMInvoiceService.getSAMInvoiceSum(INVOICE_STATUS_OPEN,
   				payerAccount, DOCUMENT_TYPE_INVOICE, DUESTATUS_DUENOW, null, cofoDate) - this.asahiSAMInvoiceService.getSAMInvoiceSum(INVOICE_STATUS_OPEN,
   						payerAccount, DOCUMENT_TYPE_CREDIT, DUESTATUS_DUENOW, null, cofoDate);


			/*Double dueBalanceWithPayment = this.asahiSAMInvoiceService.getSAMInvoiceSum(INVOICE_STATUS_OPEN,
   				payerAccount, INVOICE_PAYMENT, null, null);*/

			dueBal = dueBal-balWithPayment;

			if(dueBal < 1){
				model.addAttribute("dueNowCredit",true);
			}
			final String dueNowBalance = String.format("%,.2f",dueBal);

			model.addAttribute("dueNowBalance", dueBal>=0 ? curSymbol + dueNowBalance : NEGATIVE_SYMBOL + curSymbol +
					dueNowBalance.replace(NEGATIVE_SYMBOL, StringUtils.EMPTY));

			final Integer dueNowCount = this.asahiSAMInvoiceService.getSAMInvoiceCount(INVOICE_STATUS_OPEN,
   				payerAccount, null, DUESTATUS_DUENOW, null, cofoDate);
			model.addAttribute("dueNowCount",dueNowCount);

			final Integer notYetDueCount = this.asahiSAMInvoiceService.getSAMInvoiceCount(INVOICE_STATUS_OPEN,payerAccount, null,
					DUESTATUS_NOTYETDUE, null, cofoDate);

			model.addAttribute("notYetDueCount",notYetDueCount);
			model.addAttribute("totalInvoiceCount",(dueNowCount + notYetDueCount));

			final Double creditLimit = sessionService.getAttribute(ApbCoreConstants.CREDIT_LIMIT);
			final Double deltaLimit = sessionService.getAttribute(ApbCoreConstants.DELTA_TO_LIMIT);

			final String formattedCreditLimit = creditLimit!=null?(curSymbol+String.format("%,.2f",creditLimit)):"NA";
			final String formattedDeltaLimit = deltaLimit!=null?(curSymbol+String.format("%,.2f",deltaLimit)):"NA";

			model.addAttribute("creditLimit",formattedCreditLimit);
			model.addAttribute("deltaToLimit", formattedDeltaLimit);
		}
	}

	/* (non-Javadoc)
	 * @see com.apb.facades.sam.invoice.AsahiSAMInvoiceFacade#generatePaymentConfirmationProcess(com.apb.facades.sam.data.AsahiCaptureResponseData, com.apb.facades.card.payment.AsahiPaymentDetailsData)
	 * This method is used to generate Payment Confirmation Process
	 */
	@Override
	public void generatePaymentConfirmationProcess(final AsahiCaptureResponseData asahiCaptureResponseData, final AsahiPaymentDetailsData asahiPaymentDetailsData) {
		if(asahiSiteUtil.isSga()){
			final UserModel userModel = this.userService.getCurrentUser();

			final boolean emailEnabled = notificationService
					.getEmailPreferenceForNotificationType(NotificationType.PAYMENT_CONFIRMATION, (B2BCustomerModel) userModel,
							((B2BCustomerModel) userModel).getDefaultB2BUnit());
			if(userModel instanceof B2BCustomerModel && emailEnabled){
				eventService.publishEvent(initializeEvent(new AsahiPaymentConfirmationEvent(), asahiCaptureResponseData, userModel, asahiPaymentDetailsData));
			}
		}

	}

	private AbstractEvent initializeEvent(final AsahiPaymentConfirmationEvent event,
			final AsahiCaptureResponseData asahiCaptureResponseData, final UserModel member, final AsahiPaymentDetailsData asahiPaymentDetailsData) {
		event.setBaseStore(baseStoreService.getCurrentBaseStore());
		event.setSite(baseSiteService.getCurrentBaseSite());
		event.setLanguage(commonI18NService.getCurrentLanguage());
		event.setCurrency(commonI18NService.getCurrentCurrency());
		event.setAmountPaid(asahiCaptureResponseData.getTotalPaidAmount());
		final AsahiSAMPaymentData asahiSAMPaymentData = asahiPaymentDetailsData.getAsahiSAMPaymentData();
		if(null != asahiSAMPaymentData){
			event.setPaymentDate(asahiSAMPaymentData.getTransactionDate());
			event.setPaymentReference(asahiSAMPaymentData.getPaymentReference());

			final List<AsahiSAMInvoiceData> asahiSAMInvoiceDatas = asahiSAMPaymentData.getInvoice();
			List<AsahiSAMInvoiceModel> asahiSAMInvoiceModels = null;
			if(CollectionUtils.isNotEmpty(asahiSAMInvoiceDatas)){
				asahiSAMInvoiceModels = new ArrayList<>();
				for(final AsahiSAMInvoiceData asahiSAMInvoiceData: asahiSAMInvoiceDatas){
					if(StringUtils.isNoneEmpty(asahiSAMInvoiceData.getDocumentNumber()) && StringUtils.isNotEmpty(asahiSAMInvoiceData.getLineNumber())){
					final AsahiSAMInvoiceModel asahiSAMInvoiceModel = this.asahiSAMInvoiceService
								.getInvoiceByDocumentNumber(asahiSAMInvoiceData.getDocumentNumber(), asahiSAMInvoiceData.getLineNumber());
						if(null != asahiSAMInvoiceModel){
							asahiSAMInvoiceModel.setPaidAmount(asahiSAMInvoiceData.getTotalPaidAmount());
							if(StringUtils.isNotEmpty(asahiSAMInvoiceData.getRemainingAmount()) && StringUtils.isNotEmpty(asahiSAMInvoiceData.getTotalPaidAmount()))
							{
								asahiSAMInvoiceModel.setSamRemainingAmount(String.valueOf(new DecimalFormat("0.00").format(Double.parseDouble(asahiSAMInvoiceData.getRemainingAmount()) - Double.parseDouble(asahiSAMInvoiceData.getTotalPaidAmount()))));
							}
							this.modelService.save(asahiSAMInvoiceModel);
							asahiSAMInvoiceModels.add(asahiSAMInvoiceModel);
						}
					}
				}
			}
			event.setAsahiSAMInvoices(asahiSAMInvoiceModels);
		}
		event.setReferenceNo(asahiCaptureResponseData.getPaymentReference());
		event.setPaymentMethod(asahiPaymentDetailsData.getCardTypeInfo());
		event.setCustomer((B2BCustomerModel)member);

		return event;
	}

}
