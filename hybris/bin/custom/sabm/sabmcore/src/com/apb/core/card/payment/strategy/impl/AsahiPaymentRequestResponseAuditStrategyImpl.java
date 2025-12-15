package com.apb.core.card.payment.strategy.impl;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.ordermanagementfacades.payment.data.PaymentTransactionData;
import de.hybris.platform.ordermanagementfacades.payment.data.PaymentTransactionEntryData;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.card.payment.strategy.AsahiPaymentRequestResponseAuditStrategy;
import com.apb.core.model.AsahiPaymentTransactionModel;
import com.sabmiller.core.model.AsahiSAMInvoiceModel;
import com.apb.core.service.sam.invoice.AsahiSAMInvoiceService;
import com.apb.facades.card.payment.AsahiPaymentDetailsData;
import com.apb.facades.card.payment.AsahiPaymentPurchaseResponseData;
import com.apb.facades.sam.data.AsahiSAMInvoiceData;
import com.apb.facades.sam.data.AsahiSAMPaymentData;


/**
 * Transaction Audit Strategy.
 */
public class AsahiPaymentRequestResponseAuditStrategyImpl implements AsahiPaymentRequestResponseAuditStrategy
{
	private Converter<PaymentTransactionData, AsahiPaymentTransactionModel> asahiPaymentTransactionReverseConverter;
	private ModelService modelService;

	private final static Logger LOG = LoggerFactory.getLogger("AsahiPaymentRequestResponseAuditStrategyImpl");

	@Resource
	private CartService cartService;

	/** The asahi SAM invoice service. */
	@Resource
	private AsahiSAMInvoiceService asahiSAMInvoiceService;

	@Override
	public void makePaymentRequestAuditEntry(final PaymentTransactionData paymentTransactionData)
	{
		updateCartDataWithPaymentTransaction(paymentTransactionData);
	}

	private void updateCartDataWithPaymentTransaction(final PaymentTransactionData paymentTransactionData)
	{
		final CartModel cartModel = getCart();
		final List<PaymentTransactionData> paymentTransactionDataList = new ArrayList<>();
		paymentTransactionDataList.add(paymentTransactionData);
		final List<PaymentTransactionModel> paymentTransactions = new ArrayList<>();
		paymentTransactionDataList.forEach(paymentTransactionEntry -> {

			final AsahiPaymentTransactionModel paymentTransaction = getModelService().create(AsahiPaymentTransactionModel.class);
			getAsahiPaymentTransactionReverseConverter().convert(paymentTransactionEntry, paymentTransaction);

			paymentTransactions.add(paymentTransaction);
		});

		if (null != cartModel)
		{
			cartModel.setPaymentTransactions(paymentTransactions);
			getModelService().save(cartModel);
			LOG.debug("Updated Cart Model with Payment Transaction");
		}


	}

	/**
	 * Method will fetch the session cart.
	 *
	 * @return
	 */
	private CartModel getCart()
	{
		return cartService.hasSessionCart() ? cartService.getSessionCart() : null;
	}

	public PaymentTransactionData createPaymentTransactionData(final AsahiPaymentDetailsData asahiPaymentDetailsData,
			final String defaultCurrency, final boolean capture)
	{
		final PaymentTransactionData paymentTransactionData = new PaymentTransactionData();

		final String caputureStatus = Boolean.toString(capture);
		paymentTransactionData.setCaptureStatus(caputureStatus);
		paymentTransactionData.setCurrencyIsocode(defaultCurrency);
		paymentTransactionData.setPlannedAmount(BigDecimal.valueOf(asahiPaymentDetailsData.getTotalAmount()));
		paymentTransactionData.setCode(asahiPaymentDetailsData.getCardReference());
		paymentTransactionData.setRequestId(asahiPaymentDetailsData.getCustomerIP());
		paymentTransactionData.setRequestToken(asahiPaymentDetailsData.getCardToken());
		LOG.debug("Create Payment Transaction Card token : " + asahiPaymentDetailsData.getCardToken());
		return paymentTransactionData;
	}

	/**
	 * @return
	 */
	public Converter<PaymentTransactionData, AsahiPaymentTransactionModel> getAsahiPaymentTransactionReverseConverter()
	{
		return asahiPaymentTransactionReverseConverter;
	}

	/**
	 * @param asahiPaymentTransactionReverseConverter
	 */
	public void setAsahiPaymentTransactionReverseConverter(
			final Converter<PaymentTransactionData, AsahiPaymentTransactionModel> asahiPaymentTransactionReverseConverter)
	{
		this.asahiPaymentTransactionReverseConverter = asahiPaymentTransactionReverseConverter;
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	@Override
	public void updatePaymentTransactionEntryData(final PaymentTransactionData paymentTransactionData,
			final AsahiPaymentPurchaseResponseData asahiPaymentPurchaseResponseData)
	{
		final PaymentTransactionEntryData paymentTransactionEntryData = new PaymentTransactionEntryData();
		final List<PaymentTransactionEntryData> paymentTransactionEntryDataList = new ArrayList<>();

		if (null != asahiPaymentPurchaseResponseData)
		{
			LOG.debug(
					"asahiPaymentPurchaseResponseData not empty response id : " + asahiPaymentPurchaseResponseData.getResponseId());
			paymentTransactionEntryData.setCode(asahiPaymentPurchaseResponseData.getResponseId());
			paymentTransactionEntryData.setAmount(new BigDecimal(asahiPaymentPurchaseResponseData.getRequestAmount()));
			paymentTransactionEntryData.setAuthorizationId(asahiPaymentPurchaseResponseData.getAuthorizationId());
			paymentTransactionEntryData.setCapturedAmount(asahiPaymentPurchaseResponseData.getCapturedAmount());
			paymentTransactionEntryData.setCaptureStatus(asahiPaymentPurchaseResponseData.getCaptureStatus());
			paymentTransactionEntryData.setCardCategory(asahiPaymentPurchaseResponseData.getCardCategory());
			paymentTransactionEntryData.setCardExpiry(asahiPaymentPurchaseResponseData.getCardExpiry());
			paymentTransactionEntryData.setCardHolder(asahiPaymentPurchaseResponseData.getCardHolder());
			paymentTransactionEntryData.setCardNumber(asahiPaymentPurchaseResponseData.getCardNumber());
			paymentTransactionEntryData.setCardSubCategory(asahiPaymentPurchaseResponseData.getCardSubCategory());
			paymentTransactionEntryData.setCardType(asahiPaymentPurchaseResponseData.getCardType());
			paymentTransactionEntryData.setCurrencyCode(asahiPaymentPurchaseResponseData.getCurrencyCode());
			paymentTransactionEntryData.setCvvMatch(asahiPaymentPurchaseResponseData.getCvvMatch());
			paymentTransactionEntryData.setDecimalAmount(asahiPaymentPurchaseResponseData.getDecimalAmount());
			paymentTransactionEntryData.setRequestedReference(asahiPaymentPurchaseResponseData.getRequestedReference());
			/* paymentTransactionEntryData.setRequestId(asahiPaymentPurchaseResponseData.getre); */
			paymentTransactionEntryData.setRequestToken(asahiPaymentPurchaseResponseData.getCardToken());
			paymentTransactionEntryData.setResponseCode(asahiPaymentPurchaseResponseData.getResponseCode());
			paymentTransactionEntryData.setResponseMessage(asahiPaymentPurchaseResponseData.getResponseMessage());
			paymentTransactionEntryData.setResponsePayload(asahiPaymentPurchaseResponseData.getResponsePayload());
			paymentTransactionEntryData.setRrn(asahiPaymentPurchaseResponseData.getRrn());
			paymentTransactionEntryData.setSettlementDate(asahiPaymentPurchaseResponseData.getSettlementDate());
			paymentTransactionEntryData.setSuccessfulStatus(asahiPaymentPurchaseResponseData.getSuccessfulStatus());
			paymentTransactionEntryData.setTransactionDate(asahiPaymentPurchaseResponseData.getTransactionDate());
			/* paymentTransactionEntryData.setTransactionStatus(asahiPaymentPurchaseResponseData.ge); */
			paymentTransactionEntryData
					.setTransactionStatusDetails(asahiPaymentPurchaseResponseData.getResponseErrorMessages().toString());
		}
		paymentTransactionEntryDataList.add(paymentTransactionEntryData);
		paymentTransactionData.setEntries(paymentTransactionEntryDataList);
	}

	/*
	 * Method to make audit transaction entry for SAM Payment Transactions.
	 *
	 * @see
	 * com.apb.core.card.payment.strategy.AsahiPaymentRequestResponseAuditStrategy#makeSAMPaymentRequestAuditEntry(de.
	 * hybris.platform.ordermanagementfacade.payment.data.PaymentTransactionData,
	 * com.apb.facades.sam.data.AsahiSAMPaymentData)
	 */
	@Override
	public void makeSAMPaymentRequestAuditEntry(final PaymentTransactionData paymentTransactionData,
			final AsahiSAMPaymentData asahiSAMPaymentData)
	{

		final AsahiPaymentTransactionModel paymentTransaction = getModelService().create(AsahiPaymentTransactionModel.class);
		getAsahiPaymentTransactionReverseConverter().convert(paymentTransactionData, paymentTransaction);
		List<AsahiSAMInvoiceData> invoiceList = new ArrayList<>();
		invoiceList = asahiSAMPaymentData.getInvoice();
		final List<AsahiSAMInvoiceModel> invoiceModels = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(invoiceList))
		{
			for (final AsahiSAMInvoiceData invoice : invoiceList)
			{
				final AsahiSAMInvoiceModel asahiSAMInvoiceModel = this.asahiSAMInvoiceService
						.getInvoiceByDocumentNumber(invoice.getDocumentNumber(),invoice.getLineNumber());

				if (null != asahiSAMInvoiceModel)
				{
					asahiSAMInvoiceModel.setAsahiPaymentTransaction(paymentTransaction);
					invoiceModels.add(asahiSAMInvoiceModel);
				}

			}
			if (CollectionUtils.isNotEmpty(invoiceModels))
			{
				modelService.saveAll(invoiceModels);
			}

		}




	}
}

