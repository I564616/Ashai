/*
 *
 */
package com.apb.facades.sam.payment.history.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.apb.core.integration.AsahiIntegrationPointsServiceImpl;
import com.apb.core.integration.AsahiPaymentIntegrationPointsServiceImpl;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.service.sam.invoice.AsahiSAMInvoiceService;
import com.apb.core.service.sam.payment.history.AsahiSAMPaymentHistoryService;
import com.apb.facades.sam.data.AsahiDirectDebitData;
import com.apb.facades.sam.data.AsahiSAMInvoiceData;
import com.apb.facades.sam.data.AsahiSAMPaymentData;
import com.apb.facades.sam.payment.history.AsahiSAMPaymentHistoryFacade;
import com.apb.integration.data.AsahiDirectDebitBankResponse;
import com.apb.service.b2bunit.ApbB2BUnitService;
import com.sabmiller.core.enums.AsahiDirectDebitPaymentMode;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.AsahiDirectDebitPmtTransModel;
import com.sabmiller.core.model.AsahiSAMDirectDebitModel;
import com.sabmiller.core.model.AsahiSAMInvoiceModel;
import com.sabmiller.core.model.AsahiSAMPaymentModel;

/**
 * The Class AsahiSAMPaymentHistoryFacadeImpl.
 *
 * @author Kuldeep.Singh1
 */
public class AsahiSAMPaymentHistoryFacadeImpl implements AsahiSAMPaymentHistoryFacade{

	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(AsahiSAMPaymentHistoryFacadeImpl.class);

	/** The Constant INVOICE_CLOSED_STATUS. */
	private static final String INVOICE_CLOSED_STATUS = "10";

	/** The Constant INVOICE_PAYMENT_STATUS. */
	private static final String INVOICE_PAYMENT_STATUS = "12";

	/** The Constant SAM_PAYMENT_DOCUMENT_IDENTIFIER. */
	private static final String SAM_PAYMENT_DOCUMENT_IDENTIFIER = "sam.payment.document.identifier.sga";

	private static final String DOCUMENT_TYPE_ZT = "ZT";

	/** The asahi SAM invoice reverse converter. */
	private Converter<AsahiSAMInvoiceData, AsahiSAMPaymentModel> asahiSAMPaymentHistReverseConverter;

	/** The asahi SAM invoice reverse converter. */
	private Converter<AsahiSAMPaymentModel, AsahiSAMPaymentData> asahiSAMPaymentHistoryConverter;

	/** The model service. */
	@Resource
	private ModelService modelService;

	/** The apb B 2 B unit service. */
	@Resource
	private ApbB2BUnitService apbB2BUnitService;

	/** The asahi SAM payment history service. */
	@Resource
	private AsahiSAMPaymentHistoryService asahiSAMPaymentHistoryService;

	@Resource
	private UserService userService;

	/** The asahi configuration service. */
	@Resource
	private AsahiConfigurationService asahiConfigurationService;

	/** The asahi SAM invoice reverse converter. */
	private Converter<AsahiSAMInvoiceData, AsahiSAMInvoiceModel> asahiSAMInvoiceReverseConverter;

	/** The asahi SAM invoice service. */
	@Resource
	private AsahiSAMInvoiceService asahiSAMInvoiceService;

	/** The asahi integration points service. */
	@Resource
	private AsahiIntegrationPointsServiceImpl asahiIntegrationPointsService;

	/** The asahi payment integration points service. */
	@Resource
	private AsahiPaymentIntegrationPointsServiceImpl asahiPaymentIntegrationPointsService;

	/** The enumeration service. */
	@Resource(name="enumerationService")
	private EnumerationService enumerationService;

	/** The asahi direct debit converter. */
	private Converter<AsahiSAMDirectDebitModel, AsahiDirectDebitData> asahiDirectDebitConverter;

	/** The common I 18 N service. */
	@Resource
	private CommonI18NService commonI18NService;

	/**
	 * Import payment history.
	 *
	 * @param paymentHist the payment hist
	 */
	@Override
	public void importPaymentHistory(AsahiSAMInvoiceData invoiceData) {

		AsahiSAMPaymentModel existingPaymentHist = null;

		if(INVOICE_PAYMENT_STATUS.equalsIgnoreCase(invoiceData.getStatus()) && this.asahiConfigurationService.getString(SAM_PAYMENT_DOCUMENT_IDENTIFIER, "X").equalsIgnoreCase(invoiceData.getPaymentDocIdentifier())){
			this.invoiceDocumentCheck(invoiceData);
			this.createPaymentDocument(invoiceData, existingPaymentHist);
		}

		if(INVOICE_CLOSED_STATUS.equalsIgnoreCase(invoiceData.getStatus()) && this.asahiConfigurationService.getString(SAM_PAYMENT_DOCUMENT_IDENTIFIER, "X").equalsIgnoreCase(invoiceData.getPaymentDocIdentifier())){
			this.invoiceDocumentCheck(invoiceData);
			this.createPaymentDocument(invoiceData, existingPaymentHist);
		}else{
			this.createInvoiceDocument(invoiceData);
		}

	}

	private void invoiceDocumentCheck(AsahiSAMInvoiceData invoiceData) {
		AsahiSAMInvoiceModel existingInvoice = this.asahiSAMInvoiceService
				.getInvoiceByDocumentNumber(invoiceData.getDocumentNumber(), invoiceData.getLineNumber());
		if (null != existingInvoice)
		{
			// update existing invoice
			existingInvoice = this.asahiSAMInvoiceReverseConverter.convert(invoiceData, existingInvoice);
			this.modelService.save(existingInvoice);
		}
		else if(invoiceData.getDocumentType().equalsIgnoreCase(DOCUMENT_TYPE_ZT))
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
	 * Creates the invoice document.
	 *
	 * @param invoiceData the invoice data
	 */
	private void createInvoiceDocument(AsahiSAMInvoiceData invoiceData) {
		// Fetching Invoice based on document number
		AsahiSAMInvoiceModel existingInvoice = this.asahiSAMInvoiceService
				.getInvoiceByDocumentNumber(invoiceData.getDocumentNumber(), invoiceData.getLineNumber());

		AsahiSAMPaymentModel existingPaymentHist = null;
		/* Check if Invoice already exist in hybris if yes then update otherwise create new. */
		if (null != existingInvoice)
		{
			// update existing invoice
			// calling converter to populate the AsahiSAMInvoiceModel
			existingInvoice = this.asahiSAMInvoiceReverseConverter.convert(invoiceData, existingInvoice);

			if (INVOICE_CLOSED_STATUS.equalsIgnoreCase(invoiceData.getStatus())
					&& null != invoiceData.getClearingdocumentNumber()) {
				existingPaymentHist = this.asahiSAMPaymentHistoryService
						.getPaymentHistoryByClrDocNumber(invoiceData.getClearingdocumentNumber());
			LOG.info("Closed invoice document "+ invoiceData.getDocumentNumber()+" Clear document number "+ invoiceData.getClearingdocumentNumber());
				if (null == existingPaymentHist) {
					AsahiSAMInvoiceData samInvoiceData = new AsahiSAMInvoiceData();
					samInvoiceData.setClearingdocumentNumber(invoiceData.getClearingdocumentNumber());
					samInvoiceData.setCustAccount(invoiceData.getCustAccount());
					this.createPaymentDocument(samInvoiceData, existingPaymentHist);
					existingPaymentHist = this.asahiSAMPaymentHistoryService
							.getPaymentHistoryByClrDocNumber(invoiceData.getClearingdocumentNumber());
				}
				existingInvoice.setSamPayment(existingPaymentHist);
			}
			// saving existing Invoice into hybris database
			this.modelService.save(existingInvoice);
		}
		else
		{
			//create new Invoice in hybris
			AsahiSAMInvoiceModel newInvoice = this.modelService.create(AsahiSAMInvoiceModel.class);

			//calling converter to populate the AsahiSAMInvoiceModel
			newInvoice = this.asahiSAMInvoiceReverseConverter.convert(invoiceData, newInvoice);

			if (INVOICE_CLOSED_STATUS.equalsIgnoreCase(invoiceData.getStatus())
					&& null != invoiceData.getClearingdocumentNumber()) {
				existingPaymentHist = this.asahiSAMPaymentHistoryService
						.getPaymentHistoryByClrDocNumber(invoiceData.getClearingdocumentNumber());
				LOG.info("Create method: Closed invoice document " + invoiceData.getDocumentNumber() + " Clear document number "
						+ invoiceData.getClearingdocumentNumber());
				if (null == existingPaymentHist) {
					AsahiSAMInvoiceData samInvoiceData = new AsahiSAMInvoiceData();
					samInvoiceData.setClearingdocumentNumber(invoiceData.getClearingdocumentNumber());
					samInvoiceData.setCustAccount(invoiceData.getCustAccount());
					this.createPaymentDocument(samInvoiceData, existingPaymentHist);
					existingPaymentHist = this.asahiSAMPaymentHistoryService
							.getPaymentHistoryByClrDocNumber(invoiceData.getClearingdocumentNumber());
				}
				newInvoice.setSamPayment(existingPaymentHist);
			} else {
				if (null != invoiceData.getClearingdocumentNumber())
				{
					newInvoice.setSamPayment(this.asahiSAMPaymentHistoryService
							.getPaymentHistoryByClrDocNumber(invoiceData.getClearingdocumentNumber()));
				}
			}
			//saving new Invoice into hybris database
			this.modelService.save(newInvoice);
		}
	}

	/**
	 * Creates the payment document.
	 *
	 * @param invoiceData the invoice data
	 * @param existingPaymentHist the existing payment hist
	 */
	private void createPaymentDocument(AsahiSAMInvoiceData invoiceData,
			AsahiSAMPaymentModel existingPaymentHist) {
		// Fetching PaymentHistory based on Doc number
		if(null!=invoiceData.getDocumentNumber() && !invoiceData.getDocumentNumber().isEmpty()){
			existingPaymentHist = this.asahiSAMPaymentHistoryService.getPaymentHistoryByClrDocNumber(invoiceData.getClearingdocumentNumber());
		}

		AsahiSAMInvoiceModel existingInvoice = null;
		if(invoiceData.getDocumentNumber()!=null && invoiceData.getLineNumber()!=null) {
		existingInvoice = this.asahiSAMInvoiceService
				.getInvoiceByDocumentNumber(invoiceData.getDocumentNumber(), invoiceData.getLineNumber());
		}

		/* Check if PaymentHistory already exist in hybris if yes then update otherwise create new. */
		if(null!=existingPaymentHist){

			// update existing Document
			// calling converter to populate the AsahiSAMPaymentModel
			existingPaymentHist = this.asahiSAMPaymentHistReverseConverter.convert(invoiceData,existingPaymentHist);
			// saving existing PaymentHistory into hybris database
			this.modelService.save(existingPaymentHist);
			// Saving invoices with payment
			if(existingInvoice!=null)
			{
			existingInvoice.setSamPayment(existingPaymentHist);
			this.modelService.save(existingInvoice);
			}
		}else{
			//create new PaymentHistory in hybris
			AsahiSAMPaymentModel newPaymentHist =  this.modelService.create(AsahiSAMPaymentModel.class);

			//calling converter to populate the AsahiSAMPaymentModel
			newPaymentHist = this.asahiSAMPaymentHistReverseConverter.convert(invoiceData,newPaymentHist);

			//saving new PaymentHistory into hybris database
			this.modelService.save(newPaymentHist);
			// Saving invoices with payment
			if(existingInvoice!=null)
			{
			existingInvoice.setSamPayment(newPaymentHist);
			this.modelService.save(existingInvoice);
			}
		}
	}

	/**
	 * Gets the asahi SAM payment hist reverse converter.
	 *
	 * @return the asahi SAM payment hist reverse converter
	 */
	public Converter<AsahiSAMInvoiceData, AsahiSAMPaymentModel> getAsahiSAMPaymentHistReverseConverter() {
		return asahiSAMPaymentHistReverseConverter;
	}

	/**
	 * Sets the asahi SAM payment hist reverse converter.
	 *
	 * @param asahiSAMPaymentHistReverseConverter the asahi SAM payment hist reverse converter
	 */
	public void setAsahiSAMPaymentHistReverseConverter(
			Converter<AsahiSAMInvoiceData, AsahiSAMPaymentModel> asahiSAMPaymentHistReverseConverter) {
		this.asahiSAMPaymentHistReverseConverter = asahiSAMPaymentHistReverseConverter;
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
	@Override
	public List<AsahiSAMPaymentData> getPaymentRecords(final PageableData pageableData, final String fromDate, final String toDate, final String searchKeyword)
	{
		final UserModel user = this.userService.getCurrentUser();
		try {
			if(user instanceof B2BCustomerModel) {
				final B2BCustomerModel b2bCustModel = (B2BCustomerModel)user;
				final AsahiB2BUnitModel b2bUnit = (AsahiB2BUnitModel)b2bCustModel.getDefaultB2BUnit();
				/*added for cofoDate*/
				final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				String cofoDate = b2bUnit.getCooDate();
				final List<AsahiSAMPaymentModel> paymentRecords;
				if (null != b2bUnit && null != b2bUnit.getPayerAccount())
				{
					if (null != cofoDate && StringUtils.isNotBlank(cofoDate))
					{
						if (null != fromDate && StringUtils.isNotEmpty(fromDate)
								&& formatter.parse(cofoDate.replaceAll("-", "/")).before(formatter.parse(fromDate.replaceAll("-", "/"))))
						{
							cofoDate = fromDate;
						}
							paymentRecords = asahiSAMPaymentHistoryService.getPaymentRecords(b2bUnit.getPayerAccount().getUid(),
									pageableData, cofoDate.replaceAll("-", "/"), toDate, searchKeyword);

					}
						else
						{
						paymentRecords = asahiSAMPaymentHistoryService.getPaymentRecords(b2bUnit.getPayerAccount().getUid(),
								pageableData, fromDate, toDate, searchKeyword);
					}

					List<AsahiSAMPaymentData> paymentDataRecords = new ArrayList<>();
					if (!CollectionUtils.isEmpty(paymentRecords))
					{
						paymentDataRecords = asahiSAMPaymentHistoryConverter.convertAll(paymentRecords);
					}
					return paymentDataRecords;
				}
			}
				return null;

		}catch(final Exception ex) {
			LOG.error("Exception faced while fetching paymentrecords for user : " + user.getUid() + " : " + ex );
		}
		return null;
	}

	@Override
	public int getPaymentRecordsCount(final String fromDate, final String toDate, final String searchKeyword)
	{
		int recordsCount=0;
		final UserModel user = this.userService.getCurrentUser();
		if (user instanceof B2BCustomerModel)
		{
			final B2BCustomerModel b2bCustModel = (B2BCustomerModel)user;
			final AsahiB2BUnitModel b2bUnit = (AsahiB2BUnitModel)b2bCustModel.getDefaultB2BUnit();
			String cofoDate = b2bUnit.getCooDate();
			final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			if (null != b2bUnit.getPayerAccount()) {

				if (null != cofoDate && StringUtils.isNotBlank(cofoDate))
				{
					try
					{
						if (null != fromDate && StringUtils.isNotEmpty(fromDate)
								&& formatter.parse(cofoDate.replaceAll("-", "/")).before(formatter.parse(fromDate.replaceAll("-", "/"))))
						{
							cofoDate = fromDate;
						}
						recordsCount = asahiSAMPaymentHistoryService.getPaymentRecordsCount(b2bUnit.getPayerAccount().getUid(),
								cofoDate.replaceAll("-", "/"), toDate, searchKeyword);
					}
					catch (final ParseException e)
					{
						// YTODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
				{

					recordsCount = asahiSAMPaymentHistoryService.getPaymentRecordsCount(b2bUnit.getPayerAccount().getUid(), fromDate,
							toDate, searchKeyword);

				}


				}
			} else {
				return recordsCount;
			}


		return recordsCount;
		}

	/**
	 * Asahi Payment history converter
	 * @return converter
	 */
	public Converter<AsahiSAMPaymentModel, AsahiSAMPaymentData> getAsahiSAMPaymentHistoryConverter()
	{
		return asahiSAMPaymentHistoryConverter;
	}

	/**
	 * @param asahiSAMPaymentHistoryConverter
	 */
	public void setAsahiSAMPaymentHistoryConverter(
			Converter<AsahiSAMPaymentModel, AsahiSAMPaymentData> asahiSAMPaymentHistoryConverter)
	{
		this.asahiSAMPaymentHistoryConverter = asahiSAMPaymentHistoryConverter;
	}

	/**
	 * Save direct debit.
	 *
	 * @param directDebitdata the direct debitdata
	 * @return
	 */
	@Override
	public AsahiDirectDebitData saveDirectDebit(final AsahiDirectDebitData directDebitdata) {

		//Calling Faz Zebra API to get Token based on BANK Details
		if("BANK_ACCOUNT".equalsIgnoreCase(directDebitdata.getDirectDebitPaymentData().getTokenType())){
			AsahiDirectDebitBankResponse bankAccountResponse = this.asahiPaymentIntegrationPointsService.createBankAccount(directDebitdata);

			if(null!=bankAccountResponse && null!=bankAccountResponse.getResponse() && bankAccountResponse.isSuccessful()){
				directDebitdata.getDirectDebitPaymentData().setToken(bankAccountResponse.getResponse().getId());
			}else{
				directDebitdata.setError(true);
				return directDebitdata;
			}
		}
		//Saving Direct Debit and Token Details into hybris
		directDebitdata.setCustAccount(this.saveDirectDebitInHybris(directDebitdata));
		return directDebitdata;
	}

	/**
	 * Save direct debit in hybris.
	 *
	 * @param directDebitdata the direct debitdata
	 * @return
	 */
	private String saveDirectDebitInHybris(final AsahiDirectDebitData directDebitdata) {

		final AsahiSAMDirectDebitModel directDebitModel = this.modelService.create(AsahiSAMDirectDebitModel.class);

		AsahiB2BUnitModel asahiB2BUnitModel = this.apbB2BUnitService.getCurrentB2BUnit();
		directDebitModel.setName(directDebitdata.getPersonalName());
		directDebitModel.setCustomer(asahiB2BUnitModel);
		directDebitModel.setSignDate(new Date());

		AsahiDirectDebitPmtTransModel directDebitPmtTransModel = new AsahiDirectDebitPmtTransModel();

		if(null!=directDebitdata.getDirectDebitPaymentData()){
			if("BANK_ACCOUNT".equalsIgnoreCase(directDebitdata.getDirectDebitPaymentData().getTokenType())){
				directDebitPmtTransModel.setPaymentMode(AsahiDirectDebitPaymentMode.BANK_ACCOUNT);
				directDebitPmtTransModel.setAccountName(directDebitdata.getDirectDebitPaymentData().getAccountName());
				directDebitPmtTransModel.setAccountNumber(directDebitdata.getDirectDebitPaymentData().getAccountNum());
				directDebitPmtTransModel.setBsb(directDebitdata.getDirectDebitPaymentData().getBsb());
				directDebitPmtTransModel.setSuburb(directDebitdata.getDirectDebitPaymentData().getSuburb());

				if(null!=directDebitdata.getDirectDebitPaymentData().getRegion()){
					directDebitPmtTransModel.setRegion(this.commonI18NService.getRegion(
							this.commonI18NService.getCountry("AU"), directDebitdata.getDirectDebitPaymentData().getRegion()));
				}
			}else{
				directDebitPmtTransModel.setPaymentMode(AsahiDirectDebitPaymentMode.DEBIT_CREDIT_CARD);
				CreditCardPaymentInfoModel cardPaymentInfoModel = this.modelService.create(CreditCardPaymentInfoModel.class);
				cardPaymentInfoModel.setUser(this.userService.getCurrentUser());
				cardPaymentInfoModel.setCode(directDebitdata.getDirectDebitPaymentData().getToken());
				cardPaymentInfoModel.setCcOwner(directDebitdata.getDirectDebitPaymentData().getNameOnCard());
				cardPaymentInfoModel.setDisplayName(directDebitdata.getDirectDebitPaymentData().getNameOnCard());
				cardPaymentInfoModel.setNumber(this.getMaskedCardNumber(directDebitdata.getDirectDebitPaymentData().getCardNumber()));

				if("VISA".equalsIgnoreCase(directDebitdata.getDirectDebitPaymentData().getCardType())){
					cardPaymentInfoModel.setType(CreditCardType.VISA);
				}else if("AMEX".equalsIgnoreCase(directDebitdata.getDirectDebitPaymentData().getCardType())){
					cardPaymentInfoModel.setType(CreditCardType.AMEX);
				}else{
					cardPaymentInfoModel.setType(CreditCardType.MASTER);
				}
				cardPaymentInfoModel.setValidToMonth(directDebitdata.getDirectDebitPaymentData().getCardExpiry());
				cardPaymentInfoModel.setValidToYear(directDebitdata.getDirectDebitPaymentData().getCardExpiry());
				cardPaymentInfoModel.setToken(directDebitdata.getDirectDebitPaymentData().getToken());
				directDebitPmtTransModel.setInfo(cardPaymentInfoModel);
			}
			directDebitPmtTransModel.setRequestToken(directDebitdata.getDirectDebitPaymentData().getToken());
		}

		directDebitModel.setPaymentTransaction(directDebitPmtTransModel);

		this.modelService.save(directDebitModel);
		return asahiB2BUnitModel.getLocName();
	}

	/**
	 * Gets the masked card number.
	 *
	 * @param cardNumber the card number
	 * @return the masked card number
	 */
	private String getMaskedCardNumber(final String cardNumber)
	{
		if (cardNumber != null && cardNumber.trim().length() > 4)
		{
			final String endPortion = cardNumber.trim().substring(cardNumber.length() - 4);
			return "************" + endPortion;
		}
		return null;
	}

	/**
	 * Gets the direct debit entry for user.
	 *
	 * @param payer the payer
	 * @return the direct debit entry for user
	 */
	@Override
	public AsahiDirectDebitData getDirectDebitEntryForUser(String payer) {
		AsahiSAMDirectDebitModel directDebit = this.asahiSAMPaymentHistoryService.getDirectDebitEntryForUser(payer);
		if(null!=directDebit){
			return asahiDirectDebitConverter.convert(directDebit);
		}
		return null;
	}

	/**
	 * @return the asahiDirectDebitConverter
	 */
	public Converter<AsahiSAMDirectDebitModel, AsahiDirectDebitData> getAsahiDirectDebitConverter() {
		return asahiDirectDebitConverter;
	}

	/**
	 * @param asahiDirectDebitConverter the asahiDirectDebitConverter to set
	 */
	public void setAsahiDirectDebitConverter(
			Converter<AsahiSAMDirectDebitModel, AsahiDirectDebitData> asahiDirectDebitConverter) {
		this.asahiDirectDebitConverter = asahiDirectDebitConverter;
	}
}
