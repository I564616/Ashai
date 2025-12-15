/*
 *
 */
package com.apb.core.integration;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import com.apb.core.model.*;
import com.apb.integration.data.Error;
import com.google.gson.Gson;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.AsahiSAMDirectDebitModel;
import com.sabmiller.core.model.AsahiSAMInvoiceModel;
import com.sabmiller.core.model.SIPFailedObjectProcessorCronjobModel;
import com.sabmiller.core.model.SIPFailedPaymentModel;

import de.hybris.platform.commerceservices.impersonation.ImpersonationContext;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.site.BaseSiteService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;

import com.apb.core.checkout.service.ApbCheckoutService;
import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.dao.sam.invoice.AsahiSAMInvoiceDao;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.services.ApbCustomerAccountService;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.sam.data.AsahiDirectDebitData;
import com.apb.facades.sam.data.AsahiSAMInvoiceData;
import com.apb.facades.sam.data.AsahiSAMPaymentData;
import com.apb.integration.data.AsahiCheckoutInclusionRequest;
import com.apb.integration.data.AsahiCheckoutInclusionRequestDTO;
import com.apb.integration.data.AsahiCheckoutInclusionResponseDTO;
import com.apb.integration.data.AsahiCustomerAccountCheckRequestData;
import com.apb.integration.data.AsahiDirectDebitRequest;
import com.apb.integration.data.AsahiDirectDebitRequestDTO;
import com.apb.integration.data.AsahiDirectDebitResponse;
import com.apb.integration.data.AsahiDirectDebitResponseDTO;
import com.apb.integration.data.AsahiInvoiceDownloadRequest;
import com.apb.integration.data.AsahiInvoiceDownloadRequestDTO;
import com.apb.integration.data.AsahiInvoiceDownloadResponseDTO;
import com.apb.integration.data.AsahiInvoicePaymentRequest;
import com.apb.integration.data.AsahiInvoicePaymentRequestDTO;
import com.apb.integration.data.AsahiInvoicePaymentResponse;
import com.apb.integration.data.AsahiInvoicePaymentResponseDTO;
import com.apb.integration.data.AsahiInvoiceRequest;
import com.apb.integration.data.AsahiLoginInclusionRequestDTO;
import com.apb.integration.data.AsahiLoginInclusionResponseDTO;
import com.apb.integration.data.AsahiProductInfo;
import com.apb.integration.data.AsahiSAMDocumentRequest;
import com.apb.integration.data.AsahiSAMDocumentRequestDTO;
import com.apb.integration.data.AsahiSAMPaymentHistoryRequest;
import com.apb.integration.data.AsahiSAMPaymentHistoryRequestDTO;
import com.apb.integration.data.AsahiSalesPartnerInfo;
import com.apb.integration.data.AsahiStatementDownloadRequest;
import com.apb.integration.data.AsahiStatementDownloadRequestDTO;
import com.apb.integration.data.AsahiStatementDownloadResponseDTO;
import com.apb.service.b2bunit.ApbB2BUnitService;
import com.asahi.integration.rest.client.AsahiRestClientUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.hybris.platform.apboccaddon.dto.sam.AsahiSAMDocumentResponse;
import de.hybris.platform.apboccaddon.dto.sam.AsahiSAMDocumentResponseDTO;
import de.hybris.platform.apboccaddon.dto.sam.AsahiSAMDocuments;
import de.hybris.platform.apboccaddon.dto.sam.AsahiSAMaymentHistoryResponse;
import de.hybris.platform.apboccaddon.dto.sam.AsahiSAMaymentHistoryResponseDTO;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;


/**
 * All Asahi ECC integration touch points to be listed here.
 *
 */
public class AsahiIntegrationPointsServiceImpl {

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(AsahiIntegrationPointsServiceImpl.class);

	/** The Constant INTEGRATION_INVOICE_DOWNLOAD_STUB. */
	private static final String INTEGRATION_INVOICE_DOWNLOAD_STUB = "integration.invoice.download.service.stub.";

	/** The Constant INTEGRATION_INVOICE_DOWNLOAD_STUB_RESPONSE. */
	private static final String INTEGRATION_INVOICE_DOWNLOAD_STUB_RESPONSE = "integration.invoice.download.service.stub.response.";

	/** The Constant INTEGRATION_INVOICE_DOWNLOAD_SERVICE. */
	private static final String INTEGRATION_INVOICE_DOWNLOAD_SERVICE = "integration.invoice.download.service";

	/** The Constant INTEGRATION_STATEMENT_DOWNLOAD_STUB. */
	private static final String INTEGRATION_STATEMENT_DOWNLOAD_STUB = "integration.statement.download.service.stub.";

	/** The Constant INTEGRATION_STATEMENT_DOWNLOAD_STUB_RESPONSE. */
	private static final String INTEGRATION_STATEMENT_DOWNLOAD_STUB_RESPONSE = "integration.statement.download.service.stub.response.";

	/** The Constant INTEGRATION_STATEMENT_DOWNLOAD_SERVICE. */
	private static final String INTEGRATION_STATEMENT_DOWNLOAD_SERVICE = "integration.statement.download.service";

	/** The Constant INTEGRATION_INVOICE_PAYMENT_STUB. */
	private static final String INTEGRATION_INVOICE_PAYMENT_STUB = "integration.invoice.payment.service.stub.";

	/** The Constant INTEGRATION_INVOICE_PAYMENT_SERVICE. */
	private static final String INTEGRATION_INVOICE_PAYMENT_SERVICE = "integration.invoice.payment.service";

	/** The Constant INTEGRATION_SAM_DOCUMENT_SERVICE_STUB. */
	private static final String INTEGRATION_SAM_DOCUMENT_SERVICE_STUB = "integration.sam.document.service.stub.";

	/** The Constant INTEGRATION_SAM_DOCUMENT_SERVICE. */
	private static final String INTEGRATION_SAM_DOCUMENT_SERVICE = "integration.invoice.document.service";

	/** The Constant INTEGRATION_SAM_PAYMENT_HIST_DOCUMENT_SERVICE_STUB. */
	private static final String INTEGRATION_SAM_PAYMENT_HIST_DOCUMENT_SERVICE_STUB = "integration.sam.payment.hist.document.service.stub.";

	/** The Constant INTEGRATION_SAM_PAYMENT_HIST_DOCUMENT_SERVICE. */
	private static final String INTEGRATION_SAM_PAYMENT_HIST_DOCUMENT_SERVICE = "integration.invoice.payment.hist.document.service";

	/** The Constant INTEGRATION_DIRECT_DEBIT_STUB. */
	private static final String INTEGRATION_DIRECT_DEBIT_STUB = "integration.direct.debit.service.stub";

	/** The Constant INTEGRATION_DIRECT_DEBIT_SERVICE. */
	private static final String INTEGRATION_DIRECT_DEBIT_SERVICE = "integration.direct.debit.service";


	/** The Constant ADDRESS_TYPE. */
	private static final String SHIP_TO = "12";

	/** The Constant SOLD_TO. */
	private static final String SOLD_TO = "11";

	/** The Constant PAYER. */
	private static final String PAYER = "10";

	private static final String BILL_TO = "13";

    private static final String PROCESSING_CRONJOB_NAME = "sam.failed.transaction.process.cron.name";

    private static final String NO_BACKEND_RESPONSE = "no response from backend received";

    private static final String SITE_SGA_UID = "sga";

	/** The asahi core util. */
	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	/** The asahi site util. */
	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	/** The user service. */
	@Resource
	private UserService userService;

	/** The apb customer account service. */
	@Resource(name="customerAccountService")
	private ApbCustomerAccountService apbCustomerAccountService;

	/** The asahi SAM invoice dao. */
	@Resource
	private AsahiSAMInvoiceDao asahiSAMInvoiceDao;

	/** The model service. */
	@Resource
	private ModelService modelService;

	/** The cart service. */
	@Resource
	private CartService cartService;

	/** The asahi configuration service. */
	@Resource
	private AsahiConfigurationService asahiConfigurationService;

	/** The asahi rest client util. */
	@Resource
	private AsahiRestClientUtil asahiRestClientUtil;

	/** The apb checkout service. */
	@Resource
	private ApbCheckoutService apbCheckoutService;

	/** The type service. */
	@Resource
	private TypeService typeService;

	/** The cms site service. */
	@Resource
	private CMSSiteService cmsSiteService;

	/** The apb B 2 B unit service. */
	@Resource
	private ApbB2BUnitService apbB2BUnitService;

	@Autowired
	private CronJobService cronJobService;

	@Autowired
    private SessionService sessionService;
    @Resource
    private Converter<AsahiSAMPaymentData, SIPFailedPaymentModel> sipFailedPaymentReverseConverter;

    @Resource
    private Converter<SIPFailedPaymentModel, AsahiSAMPaymentData> sipFailedPaymentConverter;

    @Autowired
    private BaseSiteService baseSiteService;

    @Autowired
    private ImpersonationService impersonationService;
	/**
	 * Get Login inclusion response data object.
	 *
	 * @param user the user
	 * @param productIds the product ids
	 * @return response
	 */
	public AsahiLoginInclusionResponseDTO getInclusionListDetailsForLogin(final UserModel user, final Set<String> productIds)
	{
		LOG.debug("Processing login inclusion request for user : " + user.getUid());
		return getLoginInclusionResponse(createLoginInclusionRequest((B2BCustomerModel) user, productIds));
	}


	/**
	 * This method would create the rest call request object.
	 *
	 * @param b2bUser - user details
	 * @param productIds - product for which data required
	 * @return - object containing the request
	 */
	private AsahiLoginInclusionRequestDTO createLoginInclusionRequest(
			final B2BCustomerModel b2bUser, final Set<String> productIds)
	{
		final AsahiB2BUnitModel b2bUnit = (AsahiB2BUnitModel)b2bUser.getDefaultB2BUnit();
		final AsahiCustomerAccountCheckRequestData request = new AsahiCustomerAccountCheckRequestData();
		request.setCustomerNumber(b2bUnit.getAccountNum());
		request.setCreditControlArea(b2bUnit.getCreditControlArea());
		request.setDistributionChannel(b2bUnit.getDistributionChannel());
		request.setDivision(b2bUnit.getDivision());

		final List<AsahiProductInfo> items = new ArrayList<>();
		for(final String id : productIds){
			final AsahiProductInfo item = new AsahiProductInfo();
			item.setMaterialNumber(id);
			items.add(item);
		}
		request.setItems(items);
		request.setSalesOrganization(b2bUnit.getSalesOrg());

		final AsahiLoginInclusionRequestDTO requestDto = new AsahiLoginInclusionRequestDTO();
		requestDto.setLoginRequest(request);

		return requestDto;
	}

	/**
	 * Calling the rest client with API specific configuration.
	 *
	 * @param request the request
	 * @return the login inclusion response
	 */
	private AsahiLoginInclusionResponseDTO getLoginInclusionResponse(final AsahiLoginInclusionRequestDTO request) {
		final Map<String,String> config = asahiCoreUtil.getAPIConfiguration("integration.inclusion.service");
		AsahiLoginInclusionResponseDTO response;

		if(asahiConfigurationService.getBoolean("inclusion.stub.enabled.sga", true)) {
			final Boolean exception = asahiConfigurationService.getBoolean("integration.credit.check.account.exception", false);
			if (exception)
			{
				return null;
			}

			String sampleJson = ApbCoreConstants.SAMPLE_LOGIN_JSON;
			sampleJson = asahiConfigurationService.getString("integration.login.inclusion.stub.response", sampleJson);
			response = (AsahiLoginInclusionResponseDTO) createStubResponse(sampleJson,  AsahiLoginInclusionResponseDTO.class);
		} else {
			LOG.info("Login request customer number : " + request.getLoginRequest().getCustomerNumber());
			response = (AsahiLoginInclusionResponseDTO) asahiRestClientUtil
					.executePOSTRequest(request,AsahiLoginInclusionResponseDTO.class, config);
			if(!Objects.isNull(response)) {
				LOG.info(String.format("Login response customer number : %s and isBlocked : %s", Objects.isNull(response.getLoginResponse().getPayer()) ? "Customer number not sent in response"
						: response.getLoginResponse().getPayer(), response.getLoginResponse().getIsBlocked()));
			}
		}

		return response;
	}

	/**
	 * This method will create sample stub response.
	 *
	 * @param sampleJson the sample json
	 * @param clazz the clazz
	 * @return the object
	 */
	@SuppressWarnings("unchecked")
	private Object createStubResponse(final String sampleJson, final Class clazz) {
		LOG.info("Creating stub data for inclusion request with sample json " + sampleJson);
		final ObjectMapper mapper = new ObjectMapper();
		final MappingJackson2HttpMessageConverter convertor = new MappingJackson2HttpMessageConverter();
		convertor.setObjectMapper(mapper);
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);

		Object obj = null;
		try {
			obj = mapper.readValue(sampleJson, clazz);
		} catch (final JsonParseException e) {
			LOG.error(" Json Parsing exception " + e);
		} catch (final JsonMappingException e) {
			LOG.error(" Json Mapping exception " + e);
		} catch (final IOException e) {
			LOG.error("Json IO exception " + e);
		}
		return obj;
	}

	/**
	 * Get Checkout inclusion response.
	 *
	 * @return response
	 */
	public AsahiCheckoutInclusionResponseDTO getCheckoutInclusionResponse(final boolean updateCart, final long formQty) {
		return getCheckoutInclusionResponse(createCheckoutRequestData(updateCart,formQty));
	}

	/**
	 * Get checkout inclusion response.
	 *
	 * @param request the request
	 * @return the checkout inclusion response
	 */
	private AsahiCheckoutInclusionResponseDTO getCheckoutInclusionResponse(
			final AsahiCheckoutInclusionRequestDTO request) {
		final Map<String,String> config = asahiCoreUtil.getAPIConfiguration("integration.inclusion.checkout.service");
		AsahiCheckoutInclusionResponseDTO response;

		if(asahiConfigurationService.getBoolean("inclusion.checkout.stub.enabled.sga", true)) {
			final Boolean exception = asahiConfigurationService.getBoolean("integration.credit.check.account.exception", false);
			if (exception)
			{
				return null;
			}

			String sampleJson = ApbCoreConstants.SAMPLE_CHECKOUT_JSON;
			sampleJson = asahiConfigurationService.getString("integration.checkout.inclusion.stub.response", sampleJson);
			response = (AsahiCheckoutInclusionResponseDTO) createStubResponse(sampleJson,  AsahiCheckoutInclusionResponseDTO.class);
		} else {
			LOG.info("Checkout request customer number : "+ request.getCheckoutRequest().getSalesDocHeader().getCustomerNumber());
			response = (AsahiCheckoutInclusionResponseDTO) asahiRestClientUtil
					.executePOSTRequest(request,AsahiCheckoutInclusionResponseDTO.class, config);
		}

		/**
		 * get the excluded products in map with excluded as true
		 *
		 * i.e. match requested item with response, and if requested item is not present in response, mark them as excluded
		 */

        if (null != response && null != response.getCheckoutResponse() && null != response.getCheckoutResponse().getErrorText() && checkErrorTextExist(response.getCheckoutResponse().getErrorText())) {
		    return response;
        }
		else if (null!=response && null!=response.getCheckoutResponse() && !response.getCheckoutResponse().getIsBlocked())
		{
			LOG.info("Checkout response customer number : " + null == response.getCheckoutResponse().getCustomerNumber() ? "Customer number not sent in response" : response.getCheckoutResponse().getCustomerNumber() + "\t" + response.getCheckoutResponse().getIsBlocked());
			final AtomicBoolean isError = new AtomicBoolean(Boolean.FALSE);
			final List<AsahiProductInfo> excludedItems = new ArrayList<>();
			request.getCheckoutRequest().getSalesDocItem().stream().forEach(requestItem -> {
				if (!ApbCoreConstants.FREE_ITEM_CATEGORY.equals(requestItem.getItemcat()))
				{
					final AtomicBoolean exists = new AtomicBoolean(Boolean.FALSE);
					response.getCheckoutResponse().getItems().stream()
							.filter(item -> !ApbCoreConstants.FREE_ITEM_CATEGORY.equals(item.getItemcat())).forEach(responseItem -> {
								if (requestItem.getMaterialNumber().equalsIgnoreCase(responseItem.getMaterialNumber()))
								{
									exists.set(Boolean.TRUE);
									final Double quantity = Double.valueOf(requestItem.getQuantity());
									final DecimalFormat df2 = new DecimalFormat(".##");
									if (null != responseItem.getNetPrice() && null != responseItem.getTotalCdl())
									{
										responseItem.setNetPrice(Double.valueOf(df2.format((responseItem.getNetPrice()) / quantity)));
									}
									else
									{
										isError.set(Boolean.TRUE);
									}
									if (null != responseItem.getListPrice() && null != responseItem.getTotalCdl())
									{
										responseItem.setListPrice(Double.valueOf(df2.format((responseItem.getListPrice()) / quantity)));
									}
									else
									{
										isError.set(Boolean.TRUE);
									}
								}
							});

					if (!exists.get())
					{
						requestItem.setIsExcluded(Boolean.TRUE);
						excludedItems.add(requestItem);
					}

				}});

			if(isError.get())
			{
				return null;
			}

			if (CollectionUtils.isNotEmpty(excludedItems))
			{
				LOG.info("ECC checkout responded with items not in request : " + excludedItems);
				response.getCheckoutResponse().getItems().addAll(excludedItems);
			}
		}

		return response;
	}


	/**
	 * Method to create checkout request data object.
	 * @param formQty
	 * @param updateCart
	 *
	 * @return the asahi checkout inclusion request DTO
	 */
	private AsahiCheckoutInclusionRequestDTO createCheckoutRequestData(final boolean updateCart, final long formQty) {
		final UserModel b2bUser = userService.getCurrentUser();
		final AsahiCheckoutInclusionRequestDTO checkoutECCRequest = new AsahiCheckoutInclusionRequestDTO();
		final AsahiCheckoutInclusionRequest requestData = new AsahiCheckoutInclusionRequest();
		final AsahiCustomerAccountCheckRequestData customerAccountDetails = new AsahiCustomerAccountCheckRequestData();
		if (b2bUser instanceof B2BCustomerModel) {
			final AsahiB2BUnitModel b2bUnit = (AsahiB2BUnitModel)((B2BCustomerModel) b2bUser).getDefaultB2BUnit();
			final List<AsahiProductInfo> cartProductDetails = apbCheckoutService.getProductDetailsFromCart(updateCart,formQty,cartService.getSessionCart().getCode());
			customerAccountDetails.setCustomerNumber(b2bUnit.getAccountNum());
			customerAccountDetails.setCreditControlArea(b2bUnit.getCreditControlArea());
			customerAccountDetails.setDistributionChannel(b2bUnit.getDistributionChannel());
			customerAccountDetails.setDivision(b2bUnit.getDivision());
			customerAccountDetails.setSalesOrganization(b2bUnit.getSalesOrg());
			customerAccountDetails.setCustOrderType(b2bUnit.getCustOrderType());
			/*
			 * final DeliveryInfoData deliveryInfo = apbCheckoutService.getDeliveryInfo(null); if (null != deliveryInfo &&
			 * CollectionUtils.isNotEmpty(deliveryInfo.getDeferredDeliveryOptions())) {
			 * customerAccountDetails.setRequestedDeliveryDate(deliveryInfo.getDeferredDeliveryOptions().get(0)); }
			 */

			final AsahiSalesPartnerInfo shipTOsalesPartner = new AsahiSalesPartnerInfo();
			final AsahiSalesPartnerInfo soldTOsalesPartner = new AsahiSalesPartnerInfo();
			final List<AsahiSalesPartnerInfo> salesPartner = new ArrayList<AsahiSalesPartnerInfo>();

			if (CollectionUtils.isNotEmpty(b2bUnit.getShipToAccounts())) {
				final AsahiB2BUnitModel shipToCustomer = b2bUnit.getShipToAccounts().get(0);
				if (null!=shipToCustomer) {
					if(CollectionUtils.isNotEmpty(shipToCustomer.getAddresses())){
						final AddressModel addressModel = shipToCustomer.getAddresses().stream().filter(address -> address.getDefaultAddress()).findFirst().orElse(new AddressModel());
						shipTOsalesPartner.setAddressType(SHIP_TO);
						shipTOsalesPartner.setBackendRecordId(shipToCustomer.getAccountNum());
					}
					salesPartner.add(shipTOsalesPartner);
				}
			}

			soldTOsalesPartner.setAddressType(SOLD_TO);
			if(SOLD_TO.equalsIgnoreCase(b2bUnit.getBackendCustomerType().getCode())){
				soldTOsalesPartner.setAddressType(SOLD_TO);
			}else if(BILL_TO.equalsIgnoreCase(b2bUnit.getBackendCustomerType().getCode())){
				soldTOsalesPartner.setAddressType(BILL_TO);
			}else if(PAYER.equalsIgnoreCase(b2bUnit.getBackendCustomerType().getCode())){
				soldTOsalesPartner.setAddressType(PAYER);
			}

			soldTOsalesPartner.setBackendRecordId(b2bUnit.getAccountNum());
			salesPartner.add(soldTOsalesPartner);

			requestData.setSalesDocHeader(customerAccountDetails);
			requestData.setSalesDocItem(cartProductDetails);
			requestData.setSalesPartnerInfo(salesPartner);
			checkoutECCRequest.setCheckoutRequest(requestData);
		}

		return checkoutECCRequest;
	}

	/**
	 * Gets the invoice pdf.
	 * @param documentNumber
	 *
	 * @return the invoice pdf
	 */
	public AsahiInvoiceDownloadResponseDTO getInvoicePdf(final String documentNumber, final String lineNumber) {

		if (this.asahiConfigurationService.getBoolean(INTEGRATION_INVOICE_DOWNLOAD_STUB + cmsSiteService.getCurrentSite().getUid(), false))
		{
			LOG.info("Calling Invoice Download Mock Service---");

			AsahiInvoiceDownloadResponseDTO pdfresponse = new AsahiInvoiceDownloadResponseDTO();
			final String sampleJson = this.asahiConfigurationService.getString(INTEGRATION_INVOICE_DOWNLOAD_STUB_RESPONSE + cmsSiteService.getCurrentSite().getUid(), " ");

			pdfresponse = (AsahiInvoiceDownloadResponseDTO) createStubResponse(sampleJson,  AsahiInvoiceDownloadResponseDTO.class);

			return pdfresponse;
		}
		else
		{
			try
			{
				return this.getInvoicePdfFromBackend(documentNumber,lineNumber);
			}
			catch (final Exception e)
			{
				LOG.error("exception in fetching invoice pdf", e);
			}
		}
		return null;

	}


	/**
	 * Gets the invoice pdf from backend.
	 *
	 * @param documentNumber the document number
	 * @param lineNumber
	 * @return the invoice pdf from backend
	 */
	private AsahiInvoiceDownloadResponseDTO getInvoicePdfFromBackend(
			final String documentNumber, final String lineNumber) {
		LOG.info("Calling Invoice Download Real Service");

		final AsahiInvoiceDownloadRequest downloadRequest = new AsahiInvoiceDownloadRequest();
		final AsahiInvoiceDownloadRequestDTO requestDTO = new AsahiInvoiceDownloadRequestDTO();
		final AsahiB2BUnitModel b2bUnit = this.apbB2BUnitService.getCurrentB2BUnit();
		downloadRequest.setCustomerNumber(b2bUnit.getAccountNum());
		downloadRequest.setInvoiceNumber(documentNumber);
		downloadRequest.setLineNumber(lineNumber);
		requestDTO.setInvoiceDownloadRequest(downloadRequest);

		final Map<String,String> config = this.asahiCoreUtil.getAPIConfiguration(INTEGRATION_INVOICE_DOWNLOAD_SERVICE);

		final AsahiInvoiceDownloadResponseDTO downloadResponse = (AsahiInvoiceDownloadResponseDTO) asahiRestClientUtil
				.executePOSTRequest(requestDTO, AsahiInvoiceDownloadResponseDTO.class, config);

		LOG.info("Calling Invoice Download Real Service End");
		return downloadResponse;
	}

	/**
	 * Gets the statement pdf.
	 *
	 * @param statementPeriod the statement period
	 * @param statementYear
	 * @return the statement pdf
	 */
	public AsahiStatementDownloadResponseDTO getStatementPdf(final String statementMonth, final String statementYear) {

		if (this.asahiConfigurationService.getBoolean(INTEGRATION_STATEMENT_DOWNLOAD_STUB + cmsSiteService.getCurrentSite().getUid(), false))
		{
			LOG.info("Calling Statement Download Mock Service---");

			AsahiStatementDownloadResponseDTO pdfresponse = new AsahiStatementDownloadResponseDTO();
			final String sampleJson = this.asahiConfigurationService.getString(INTEGRATION_STATEMENT_DOWNLOAD_STUB_RESPONSE + cmsSiteService.getCurrentSite().getUid(), " ");

			pdfresponse = (AsahiStatementDownloadResponseDTO) createStubResponse(sampleJson,  AsahiStatementDownloadResponseDTO.class);

			return pdfresponse;
		}
		else
		{
			try
			{
				return this.getStatementPdfFromBackend(statementMonth, statementYear);
			}
			catch (final Exception e)
			{
				LOG.info("exception in fetching Statement pdf", e);
			}
		}
		return null;
	}


	/**
	 * Gets the statement pdf from backend.
	 *
	 * @param statementMonth the statement month
	 * @param statementYear the statement year
	 * @return the statement pdf from backend
	 */
	private AsahiStatementDownloadResponseDTO getStatementPdfFromBackend(final String statementMonth, final String statementYear) {
		LOG.info("Calling Statement Download Real Service");

		final AsahiStatementDownloadRequest downloadRequest = new AsahiStatementDownloadRequest();
		final AsahiStatementDownloadRequestDTO requestDTO = new AsahiStatementDownloadRequestDTO();

		final AsahiB2BUnitModel b2bUnit = this.apbB2BUnitService.getCurrentB2BUnit();
		downloadRequest.setCustAccount(b2bUnit.getAccountNum());
		downloadRequest.setStatementMonth(statementMonth);
		downloadRequest.setStatementYear(statementYear);

		requestDTO.setStatementDownloadRequest(downloadRequest);

		final Map<String,String> config = this.asahiCoreUtil.getAPIConfiguration(INTEGRATION_STATEMENT_DOWNLOAD_SERVICE);

		final AsahiStatementDownloadResponseDTO downloadResponse = (AsahiStatementDownloadResponseDTO) asahiRestClientUtil
				.executePOSTRequest(requestDTO, AsahiStatementDownloadResponseDTO.class, config);

		LOG.info("Calling Statement Download Real Service End");
		return downloadResponse;
	}

    /**
     * Send invoice payment.
     *
     * @param requestDTO the request DTO
     * @return the asahi invoice payment response DTO
     */
    public AsahiInvoicePaymentResponseDTO sendInvoicePayment(final AsahiSAMPaymentData invoicePaymentData) {
        AsahiInvoicePaymentResponseDTO paymentResponse = null;
        if (this.asahiConfigurationService.getBoolean(INTEGRATION_INVOICE_PAYMENT_STUB + cmsSiteService.getCurrentSite().getUid(), false)) {
            LOG.info("Calling sendInvoicePayment Mock Service---");
            final AsahiInvoicePaymentResponseDTO responseDTO = new AsahiInvoicePaymentResponseDTO();
            final AsahiInvoicePaymentResponse response = new AsahiInvoicePaymentResponse();
            response.setSuccess(true);
            responseDTO.setPaymentResponse(response);
            return responseDTO;
        } else {
            try {
                final AsahiInvoicePaymentRequestDTO requestDTO = this.populateAsahiInvoicePaymentRequestDTO(invoicePaymentData);
                final Map<String, String> config = this.asahiCoreUtil.getAPIConfiguration(INTEGRATION_INVOICE_PAYMENT_SERVICE);

                paymentResponse = (AsahiInvoicePaymentResponseDTO) asahiRestClientUtil
                        .executePOSTRequest(requestDTO, AsahiInvoicePaymentResponseDTO.class, config);
                /*if (null == paymentResponse || !paymentResponse.getPaymentResponse().getSuccess()) {
                    final SIPFailedPaymentModel sipFailedPaymentModel = sipFailedPaymentReverseConverter.convert(invoicePaymentData);
                    sipFailedPaymentModel.setUser(userService.getCurrentUser());
                    modelService.save(sipFailedPaymentModel);
                    saveFailedTransactionsInCronjob(sipFailedPaymentModel, paymentResponse != null ? paymentResponse.getPaymentResponse() : NO_BACKEND_RESPONSE, null);
                }*/
            } catch (final Exception e) {
                LOG.info("exception in sendInvoicePayment", e);
            }
        }
        return paymentResponse;
    }


	/**
	 * Populate asahi invoice payment request DTO.
	 *
	 * @param invoicePaymentData the invoice payment data
	 * @return the asahi invoice payment request DTO
	 */
	private AsahiInvoicePaymentRequestDTO populateAsahiInvoicePaymentRequestDTO(
			final AsahiSAMPaymentData invoicePaymentData) {
		final AsahiInvoicePaymentRequestDTO requestDTO = new AsahiInvoicePaymentRequestDTO();
		final AsahiInvoicePaymentRequest request = new AsahiInvoicePaymentRequest();
		final List<AsahiInvoiceRequest> invoiceList = new ArrayList<AsahiInvoiceRequest>();

		final List<AsahiSAMInvoiceModel> invoiceListToBeSaved = new ArrayList<AsahiSAMInvoiceModel>();

		final AsahiB2BUnitModel b2bUnit = this.apbB2BUnitService.getCurrentB2BUnit();

		request.setCustAccount(b2bUnit.getAccountNum());
		request.setPaymentReference(invoicePaymentData.getPaymentReference());

		request.setTotalAmountPaid(String.valueOf(invoicePaymentData.getTotalAmount()));
		request.setPaymentTransactionId(invoicePaymentData.getPaymentTransactionId());
		request.setPaymentDate(invoicePaymentData.getTransactionDate());
		request.setPaymentOption(invoicePaymentData.getPartialPaymentReason());
		request.setPaymentReference(invoicePaymentData.getPaymentReference());

		if(CollectionUtils.isNotEmpty(invoicePaymentData.getInvoice())){
			for(final AsahiSAMInvoiceData invoiceData : invoicePaymentData.getInvoice()){
				final AsahiInvoiceRequest invoiceRequest = new AsahiInvoiceRequest();
				invoiceRequest.setDocumentNumber(invoiceData.getDocumentNumber());
				invoiceRequest.setDeliveryNumber(invoiceData.getDeliveryNumber());
				invoiceRequest.setAmountPaid(invoiceData.getTotalPaidAmount());
				invoiceRequest.setLineNumber(invoiceData.getLineNumber());
				invoiceRequest.setRemainingAmount(String.valueOf(Double.valueOf(invoiceData.getRemainingAmount())));
				invoiceList.add(invoiceRequest);

				final AsahiSAMInvoiceModel invoice = this.asahiSAMInvoiceDao.getInvoiceByDocumentNumber(invoiceData.getDocumentNumber(),invoiceData.getLineNumber());
				invoice.setPaymentMade(true);
				invoiceListToBeSaved.add(invoice);
			}
			this.modelService.saveAll(invoiceListToBeSaved);
		}

		request.setInvoice(invoiceList);
		requestDTO.setPaymentRequest(request);

		return requestDTO;
	}


    /**
     * Send direct debit details.
     *
     * @param directDebitdata the direct debitdata
     * @return the asahi direct debit response DTO
     */
    public boolean sendDirectDebitDetails(final AsahiDirectDebitData directDebitdata, final AsahiSAMDirectDebitModel directDebitModel, final boolean isCronjobCall, final String indicator) {
        if (this.asahiConfigurationService.getBoolean(INTEGRATION_DIRECT_DEBIT_STUB, false)) {
            LOG.info("Calling sendDirectDebitDetails Mock Service---");
            final AsahiDirectDebitResponseDTO responseDTO = new AsahiDirectDebitResponseDTO();
            final AsahiDirectDebitResponse response = new AsahiDirectDebitResponse();
            response.setSuccess(true);
            responseDTO.setDirectDebitResponse(response);
            return true;
        } else {
            try {
                final AsahiDirectDebitRequestDTO requestDTO = this.populateDirectDebitRequestDTO(directDebitdata);
                final Map<String, String> config = this.asahiCoreUtil.getDirectDebitAPIConfiguration(INTEGRATION_DIRECT_DEBIT_SERVICE);

                final AsahiDirectDebitResponseDTO directDebitResponse = (AsahiDirectDebitResponseDTO) asahiRestClientUtil
                        .executePOSTRequest(requestDTO, AsahiDirectDebitResponseDTO.class, config);

                if (!isCronjobCall && !directDebitResponse.getDirectDebitResponse().isSuccess()) {
                    saveFailedTransactionsInCronjob(directDebitModel, directDebitResponse.getDirectDebitResponse(), indicator);
                }
                if (!isCronjobCall && directDebitResponse.getDirectDebitResponse().isSuccess()) {
                    clearSuccessTransactionsFromCronjob(directDebitModel);
                }
				return directDebitResponse.getDirectDebitResponse().isSuccess();
			}
			catch (final Exception e)
			{
				LOG.error("exception in sendDirectDebitDetails", e);
				return false;
			}
		}
	}


	/**
	 * Populate direct debit request DTO.
	 *
	 * @param directDebitdata the direct debitdata
	 * @return the asahi direct debit request DTO
	 */
	private AsahiDirectDebitRequestDTO populateDirectDebitRequestDTO(
			final AsahiDirectDebitData directDebitdata) {
		final AsahiDirectDebitRequestDTO requestDTO = new AsahiDirectDebitRequestDTO();
		final AsahiDirectDebitRequest request = new AsahiDirectDebitRequest();

		request.setPayerAccount(directDebitdata.getCustAccount());
		request.setPaymentTerm(directDebitdata.getPaymentTerm());
		if(null!=directDebitdata.getDirectDebitPaymentData()){
			request.setToken(directDebitdata.getDirectDebitPaymentData().getToken());
			request.setTokenType(directDebitdata.getDirectDebitPaymentData().getTokenType());
			request.setAccountName(directDebitdata.getDirectDebitPaymentData().getAccountName());
			request.setAccountNumber(directDebitdata.getDirectDebitPaymentData().getAccountNum());
			request.setBsb(directDebitdata.getDirectDebitPaymentData().getBsb());
			request.setCompanyCode(directDebitdata.getDirectDebitPaymentData().getCompanyCode());
		}
		requestDTO.setDirectDebitRequest(request);
		return requestDTO;
	}

    private <MODEL, DTO> void saveFailedTransactionsInCronjob(final MODEL model, final DTO response, final String indicator) {
        final SIPFailedObjectProcessorCronjobModel cronJob = (SIPFailedObjectProcessorCronjobModel) cronJobService.getCronJob(asahiConfigurationService.getConfiguration().getString(PROCESSING_CRONJOB_NAME));
        if (model instanceof AsahiSAMDirectDebitModel && response instanceof AsahiDirectDebitResponse) {
            final AsahiDirectDebitResponse asahiDirectDebitResponse = (AsahiDirectDebitResponse) response;
            LOG.info("Failed sending direct debits to backend, payment info will be sent again. Reason of failure - " + asahiDirectDebitResponse.getReasonOfFailure());
            final List<AsahiSAMDirectDebitModel> directDebitModels = new ArrayList<>();
            final AsahiSAMDirectDebitModel directDebitModel = (AsahiSAMDirectDebitModel) model;
            directDebitModels.add(directDebitModel);
            try {
                final List<AsahiSAMDirectDebitModel> modifiedDirectDebitList = cronJob.getFailedDirectDebits().stream().filter(directDebit -> !directDebit.getPk().equals(directDebitModel.getPk())).collect(Collectors.toList());
                directDebitModel.setUpdateRemoveIndicator(indicator);
                modelService.save(directDebitModel);
                directDebitModels.addAll(modifiedDirectDebitList);
                Collections.sort(directDebitModels, (a, b) -> a.getModifiedtime().after(b.getModifiedtime()) ? -1 : 1);
                cronJob.setFailedDirectDebits(directDebitModels);
                modelService.save(cronJob);
            } catch (final Exception e) {
                LOG.error("Exception occurred while saving direct debit model in Cronjob.", e);
            }
        } else if (model instanceof SIPFailedPaymentModel && (response instanceof AsahiInvoicePaymentResponse || response instanceof String)) {
            final boolean typeStr = (response instanceof String);
            LOG.info(String.format("SIP invoice was not sent to backend :: backend response - %s", typeStr ? response : ((AsahiInvoicePaymentResponse)response).getSuccess()));
            LOG.info("Invoice payment is being saved in cronjob to process again.");
            final List<SIPFailedPaymentModel> paymentModels = new ArrayList<>();
            final SIPFailedPaymentModel sipFailedPaymentModel = (SIPFailedPaymentModel) model;
            paymentModels.add(sipFailedPaymentModel);
            try {
                final List<SIPFailedPaymentModel> modifiedPaymentList = cronJob.getFailedInvoicePayments().stream().filter(invPayment -> !invPayment.getPk().equals(sipFailedPaymentModel.getPk())).collect(Collectors.toList());
                paymentModels.addAll(modifiedPaymentList);
                Collections.sort(paymentModels, (a, b) -> a.getModifiedtime().after(b.getModifiedtime()) ? -1 : 1);
                cronJob.setFailedInvoicePayments(paymentModels);
                modelService.save(cronJob);
            } catch (final Exception e) {
                LOG.error("Exception occurred while saving direct debit model in Cronjob.", e);
            }
        }
    }


    private <T> void clearSuccessTransactionsFromCronjob(final T t) {
        try {
            List<AsahiSAMDirectDebitModel> directDebits = new ArrayList<>();
            final SIPFailedObjectProcessorCronjobModel cronJob = (SIPFailedObjectProcessorCronjobModel) cronJobService.getCronJob(asahiConfigurationService.getConfiguration().getString(PROCESSING_CRONJOB_NAME));
            if (t instanceof AsahiSAMDirectDebitModel) {
                final AsahiSAMDirectDebitModel directDebitModel = (AsahiSAMDirectDebitModel) t;
                directDebits = cronJob.getFailedDirectDebits().stream().filter(directDebit -> !directDebit.getPk().equals(directDebitModel.getPk())).collect(Collectors.toList());
            }
            if (t instanceof SIPFailedPaymentModel) {
                final SIPFailedPaymentModel sipFailedPaymentModel = (SIPFailedPaymentModel) t;
                modelService.remove(cronJob.getFailedInvoicePayments().stream().filter(invPayment -> invPayment.getPk().equals(sipFailedPaymentModel.getPk())).findAny().get());
            }
            Collections.sort(directDebits, (a, b) -> a.getModifiedtime().after(b.getModifiedtime()) ? -1 : 1);
            cronJob.setFailedDirectDebits(directDebits);
            modelService.save(cronJob);
        } catch (final Exception e) {
            LOG.error("Exception occurred while removing direct debit model from Cronjob.", e);
        }
    }

    private boolean checkErrorTextExist(final List<Error> errors) {
        final AtomicBoolean exist = new AtomicBoolean(Boolean.FALSE);
        for (final Error error : errors) {
            if (StringUtils.isNotEmpty(error.getMessageCode()) && StringUtils.isNotBlank(error.getMessageCode())) {
                exist.set(Boolean.TRUE);
                break;
            }
        }
        return exist.get();
    }

    public boolean sendInvoicePaymentThruJob(final SIPFailedPaymentModel sipFailedPaymentModel) {
        final ImpersonationContext context = new ImpersonationContext();
        context.setSite(baseSiteService.getBaseSiteForUID(SITE_SGA_UID));
        context.setUser(sipFailedPaymentModel.getUser());
        return impersonationService.executeInContext(context, () -> {
            try {
                final Gson gson = new Gson();
                final AsahiSAMPaymentData invoicePaymentData = sipFailedPaymentConverter.convert(sipFailedPaymentModel);
                final AsahiInvoicePaymentRequestDTO requestDTO = this.populateAsahiInvoicePaymentRequestDTO(invoicePaymentData);
                final Map<String, String> config = this.asahiCoreUtil.getAPIConfiguration(INTEGRATION_INVOICE_PAYMENT_SERVICE);
                final AsahiInvoicePaymentResponseDTO paymentResponse = (AsahiInvoicePaymentResponseDTO) asahiRestClientUtil
                        .executePOSTRequest(requestDTO, AsahiInvoicePaymentResponseDTO.class, config);
                LOG.info("Invoice payment interface response through cronjob - " + gson.toJson(paymentResponse));
                if (null != paymentResponse && paymentResponse.getPaymentResponse().getSuccess()) {
                    clearSuccessTransactionsFromCronjob(sipFailedPaymentModel);
                }
                return (null != paymentResponse && Boolean.TRUE.equals(paymentResponse.getPaymentResponse().getSuccess()));
            } catch (final Exception e) {
                LOG.info("exception in sendInvoicePayment", e);
                return false;
            }
        });
    }
}
