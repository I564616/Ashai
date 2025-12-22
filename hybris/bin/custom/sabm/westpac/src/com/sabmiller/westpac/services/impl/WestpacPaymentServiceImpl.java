package com.sabmiller.westpac.services.impl;

import com.sabmiller.westpac.data.*;
import com.sabmiller.westpac.strategy.WestpacPaymentTypePersistenceStategy;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.i18n.daos.CurrencyDao;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;

import java.math.BigDecimal;
import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.Lists;
import com.sabmiller.core.model.InvoicePaymentModel;
import com.sabmiller.westpac.constants.WestpacConstants;
import com.sabmiller.westpac.exceptions.WestpacPaymentErrorException;
import com.sabmiller.westpac.exceptions.WestpacTokenException;
import com.sabmiller.westpac.services.WestpacPaymentService;
import com.sabmiller.westpac.services.WestpacService;
import org.springframework.beans.factory.InitializingBean;
import javax.annotation.Resource;


public class WestpacPaymentServiceImpl implements WestpacPaymentService,InitializingBean
{
	private final static Logger LOG = LoggerFactory.getLogger(WestpacPaymentServiceImpl.class.getName());
	public final static String PROVIDER = "westpac";

	public static final String COMMUNITY_CODE = "westpac.payment.token.communitycode";
	public static final String POST_URL = "westpac.payment.capture.url";

	private WestpacService westpacService;
	private KeyGenerator keyGenerator;
	private ModelService modelService;
	private B2BCommerceUnitService b2bCommerceUnitService;
	private CurrencyDao currencyDao;

	@Resource(name = "westpacPaymentTypePersistenceMap")
	private Map<String, WestpacPaymentTypePersistenceStategy> westpacPaymentTypePersistenceMap;


	@Override
	public boolean isApproved(final WestpacResponseData responseData) throws WestpacPaymentErrorException
	{
		return westpacService.isApproved(responseData.getSummaryCode(), responseData.getAction());
	}

	@Override
	public boolean isApproved(final PostBackData responseData) throws WestpacPaymentErrorException
	{
		return westpacService.isApproved(responseData.getSummaryCode(), null);
	}

	@Override
	public WestpacRequestData initiateWestpacCheckout(final CartModel cartModel) throws WestpacTokenException
	{

		final WestpacRequestData requestData = new WestpacRequestData();

		LOG.debug(
				"Westpac request is a top level (ZADP) B2B Unit. Request Customer Reference Number : {}, The B2BUnit with account group: {}",
				b2bCommerceUnitService.getRootUnit().getUid(), b2bCommerceUnitService.getRootUnit().getAccountGroup());
		requestData.setCustomerReferenceNumber(b2bCommerceUnitService.getRootUnit().getUid());
		requestData.setPaymentReference(generatePaymentID(WestpacConstants.PAYMENT_MODE.CHECKOUT));
		requestData.setIgnoreDuplicate(Config.getString("westpac.payment.token.ignoreDuplicate", "false"));

		//used for direct post
		requestData.setCommunityCode(Config.getString(COMMUNITY_CODE, null));
		requestData.setUrl(Config.getString(POST_URL, "#"));

		westpacService.initiateCheckout(requestData, cartModel);
		return requestData;
	}

	@Override
	public WestpacRequestData initiateWestpacCheckout(final Set<String> invoices, final BigDecimal total, final String currencyIso,
			final UserModel userModel, final String paymentType) throws WestpacTokenException
	{

		final WestpacRequestData requestData = new WestpacRequestData();

		requestData.setPaymentType(paymentType);

		LOG.debug(
				"Westpac request is a top level (ZADP) B2B Unit. Request Customer Reference Number : {}, The B2BUnit with account group: {}",
				b2bCommerceUnitService.getRootUnit().getUid(), b2bCommerceUnitService.getRootUnit().getAccountGroup());
		requestData.setCustomerReferenceNumber(b2bCommerceUnitService.getRootUnit().getUid());
		final String id = generatePaymentID(WestpacConstants.PAYMENT_MODE.INVOICE);
		requestData.setPaymentReference(id);
		requestData.setInvoiceTrackingNumber(id);
		requestData.setIgnoreDuplicate(Config.getString("westpac.payment.token.ignoreDuplicate", "false"));

		saveInvoiceData(invoices, total, currencyIso, userModel, requestData.getPaymentReference());

		//used for direct post
		requestData.setCommunityCode(Config.getString(COMMUNITY_CODE, null));
		requestData.setUrl(Config.getString(POST_URL, "#"));

		westpacService.initiateCheckout(requestData, total.doubleValue(), currencyIso, userModel);

		return requestData;
	}

	protected InvoicePaymentModel saveInvoiceData(final Set<String> invoices, final BigDecimal total, final String currencyIso,
			final UserModel userModel, final String trackingNumber)
	{

		final InvoicePaymentModel invoicePaymentModel = modelService.create(InvoicePaymentModel._TYPECODE);

		invoicePaymentModel.setPaymentCode(trackingNumber);
		invoicePaymentModel.setAmount(total);
		invoicePaymentModel.setUser(userModel);
		invoicePaymentModel.setInvoices(Lists.newArrayList(invoices));
		final List<CurrencyModel> list = currencyDao.findCurrenciesByCode(currencyIso);
		if (CollectionUtils.isNotEmpty(list))
		{
			invoicePaymentModel.setCurrency(list.get(0));
		}

		modelService.save(invoicePaymentModel);

		return invoicePaymentModel;
	}

	protected String generatePaymentID(final WestpacConstants.PAYMENT_MODE payment_mode)
	{
		return payment_mode.getCode() + keyGenerator.generate();
	}

	@Override
	public PaymentInfoModel savePaymentRedirectResponse(final InvoicePaymentModel invoicePaymentModel, final WestpacResponseData responseData)
			throws WestpacPaymentErrorException
	{

		final PaymentInfoModel paymentInfoModel = createPaymentInfo(responseData, invoicePaymentModel.getUser());
		invoicePaymentModel.setPaymentInfo(paymentInfoModel);

		try
		{
			modelService.saveAll(invoicePaymentModel, paymentInfoModel);
			modelService.refresh(paymentInfoModel);
			modelService.refresh(invoicePaymentModel);
		}
		catch (final ModelSavingException e)
		{
			LOG.error("Error saving payment details");
			throw new WestpacPaymentErrorException("Error saving payment details", e);
		}

		if (Config.getBoolean("westpac.mock", false))
		{
			//Workaround to allow developers to checkout with CC payments. Westpac postback requires server to be
			//public and have a valid certificate
			savePostBackResponse(invoicePaymentModel, new MockPostBackData());
		}

		return paymentInfoModel;
	}

	protected PaymentInfoModel createPaymentInfo(final WestpacResponseData responseData, final UserModel user)
	{
		if (StringUtils.isNotEmpty(responseData.getBsb()))
		{
			return westpacPaymentTypePersistenceMap.get(WestpacConstants.PAYMENT_METHOD.BANKTRANSFER).createPaymentInfo(responseData, user);
		}

		return westpacPaymentTypePersistenceMap.get(WestpacConstants.PAYMENT_METHOD.CREDITCARD).createPaymentInfo(responseData, user);
	}

	@Override
	public PaymentInfoModel savePaymentRedirectResponse(final CartModel cartModel,
			final WestpacResponseData responseData) throws WestpacPaymentErrorException
	{

		final PaymentInfoModel paymentInfoModel = createPaymentInfo(responseData, cartModel.getUser());
		cartModel.setPaymentInfo(paymentInfoModel);
		cartModel.setCartCode(cartModel.getCode());

		try
		{
			modelService.saveAll(paymentInfoModel, cartModel);
		}
		catch (final ModelSavingException e)
		{
			LOG.error("Error saving payment details");
			throw new WestpacPaymentErrorException("Error saving payment details", e);
		}

		if (Config.getBoolean("westpac.mock", false))
		{
			//Workaround to allow developers to checkout with CC payments. Westpac postback requires server to be
			//public and have a valid certificate
			savePostBackResponse(cartModel, new MockPostBackData());
		}

		return paymentInfoModel;
	}



	@Override
	public PaymentTransactionEntryModel savePostBackResponse(final CartModel cartModel, final PostBackData responseData)
			throws WestpacPaymentErrorException
	{

		//invalidate any previous attempts
		if (CollectionUtils.isNotEmpty(cartModel.getPaymentTransactions()))
		{
			for (final PaymentTransactionModel transactionModel : cartModel.getPaymentTransactions())
			{
				for (final PaymentTransactionEntryModel entryModel : transactionModel.getEntries())
				{
					if (PaymentTransactionType.SURCHARGE.equals(entryModel.getType())
							&& StringUtils.equalsIgnoreCase(TransactionStatus.REVIEW.name(), entryModel.getTransactionStatus()))
					{
						entryModel.setTransactionStatus(TransactionStatus.ACCEPTED.name());
						entryModel.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL.name());
					}
					else
					{
						entryModel.setTransactionStatus(TransactionStatus.REJECTED.name());
					}
				}

				modelService.saveAll(transactionModel.getEntries());
			}
			modelService.saveAll(cartModel.getPaymentTransactions());
		}

		final BigDecimal total = BigDecimal.valueOf(cartModel.getTotalPrice());
		final PaymentTransactionEntryModel entryModel = createPaymentTransaction(responseData, total, cartModel.getCurrency(),
				cartModel.getPaymentInfo());

		entryModel.getPaymentTransaction().setOrder(cartModel);

		try
		{
			modelService.saveAll(entryModel, entryModel.getPaymentTransaction(), cartModel);
		}
		catch (final ModelSavingException e)
		{
			LOG.error("Error saving payment details");
			throw new WestpacPaymentErrorException("Error saving payment details", e);
		}

		return entryModel;
	}

	@Override
	public PaymentTransactionEntryModel savePostBackResponse(final InvoicePaymentModel invoicePaymentModel,
			final PostBackData responseData) throws WestpacPaymentErrorException
	{
		PaymentTransactionEntryModel entryModel = null;
		if (invoicePaymentModel != null)
		{
			if (invoicePaymentModel.getTransaction() != null)
			{
				for (final PaymentTransactionEntryModel entry : invoicePaymentModel.getTransaction().getEntries())
				{
					if (PaymentTransactionType.SURCHARGE.equals(entry.getType())
							&& StringUtils.equalsIgnoreCase(TransactionStatus.REVIEW.name(), entry.getTransactionStatus()))
					{
						entry.setTransactionStatus(TransactionStatus.ACCEPTED.name());
						entry.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL.name());
					}
				}

				modelService.saveAll(invoicePaymentModel.getTransaction().getEntries());
				modelService.refresh(invoicePaymentModel);

				entryModel = createPaymentTransactionEntry(responseData, invoicePaymentModel.getAmount(),
						invoicePaymentModel.getCurrency());
				entryModel.setPaymentTransaction(invoicePaymentModel.getTransaction());
			}
			else
			{
				entryModel = createPaymentTransaction(responseData, invoicePaymentModel.getAmount(),
						invoicePaymentModel.getCurrency(), invoicePaymentModel.getPaymentInfo());
			}
		}

		try
		{
			invoicePaymentModel.setTransaction(entryModel.getPaymentTransaction());
			invoicePaymentModel.setPaid(true);

			modelService.saveAll(entryModel, invoicePaymentModel.getTransaction(), invoicePaymentModel);
		}
		catch (final ModelSavingException e)
		{
			LOG.error("Error saving payment details");
			throw new WestpacPaymentErrorException("Error saving payment details", e);
		}

		return entryModel;
	}

	protected PaymentTransactionEntryModel createPaymentTransaction(final PostBackData responseData, final BigDecimal total,
			final CurrencyModel currencyModel, final PaymentInfoModel paymentInfoModel)
	{
		//Add payment transaction
		final PaymentTransactionModel transactionModel = modelService.create(PaymentTransactionModel._TYPECODE);
		transactionModel.setCode(responseData.getPaymentReference());
		transactionModel.setCurrency(currencyModel);
		transactionModel.setInfo(paymentInfoModel);
		transactionModel.setPaymentProvider(PROVIDER);
		transactionModel.setPlannedAmount(total);

		final PaymentTransactionEntryModel entryModel = createPaymentTransactionEntry(responseData, total, currencyModel);
		entryModel.setPaymentTransaction(transactionModel);

		modelService.save(transactionModel);

		return entryModel;
	}

	protected PaymentTransactionEntryModel createPaymentTransactionEntry(final PostBackData responseData, final BigDecimal total,
			final CurrencyModel currencyModel)
	{
		final Date date = Calendar.getInstance().getTime();

		//Add payment transaction info
		final PaymentTransactionEntryModel entryModel = modelService.create(PaymentTransactionEntryModel._TYPECODE);
		entryModel.setCode(responseData.getPaymentReference() + "-" + UUID.randomUUID());
		entryModel.setAmount(total);
		entryModel.setTime(date);
		entryModel.setCurrency(currencyModel);
		entryModel.setRequestToken(responseData.getPaymentReference());
		entryModel.setRequestId(responseData.getReceiptNumber());
		entryModel.setTransactionStatus(TransactionStatus.ACCEPTED.name());
		entryModel.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL.name());
		entryModel.setType(PaymentTransactionType.CAPTURE);
		entryModel.setResponseCode(responseData.getResponseCode());
		entryModel.setSummaryCode(responseData.getSummaryCode());


		modelService.save(entryModel);

		return entryModel;
	}

	//@Required
	public void setWestpacService(final WestpacService westpacService)
	{
		this.westpacService = westpacService;
	}

	//@Required
	public void setKeyGenerator(final KeyGenerator keyGenerator)
	{
		this.keyGenerator = keyGenerator;
	}

	//@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	//@Required
	public void setB2bCommerceUnitService(final B2BCommerceUnitService b2bCommerceUnitService)
	{
		this.b2bCommerceUnitService = b2bCommerceUnitService;
	}

	//@Required
	public void setCurrencyDao(final CurrencyDao currencyDao)
	{
		this.currencyDao = currencyDao;
	}

    @Override
    public final void afterPropertiesSet() throws Exception
    {
        if (this.westpacService == null) {
            throw new IllegalArgumentException("Property 'westpacService' must be set");
        }

        if (this.keyGenerator == null) {
            throw new IllegalArgumentException("Property 'keyGenerator' must be set");
        }

        if (this.modelService == null) {
            throw new IllegalArgumentException("Property 'modelService' must be set");
        }

        if (this.b2bCommerceUnitService == null) {
            throw new IllegalArgumentException("Property 'b2bCommerceUnitService' must be set");
        }

        if (this.currencyDao == null) {
            throw new IllegalArgumentException("Property 'currencyDao' must be set");
        }
    }
}
