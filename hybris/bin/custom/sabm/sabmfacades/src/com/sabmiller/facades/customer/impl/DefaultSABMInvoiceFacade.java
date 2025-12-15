/**
 *
 */
package com.sabmiller.facades.customer.impl;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.notificationservices.enums.NotificationType;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.Config;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Resource;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import com.apb.core.service.config.AsahiConfigurationService;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.email.service.SabmEmailService;
import com.sabmiller.core.enums.InvoiceDiscrepancyProcessResultEnum;
import com.sabmiller.core.event.PaymentConfirmationEmailEvent;
import com.sabmiller.core.invoices.converters.populators.InvoiceDiscrepancyPopulator;
import com.sabmiller.core.invoices.converters.reversePopulators.CreditAdjustmentSAPReversePopulator;
import com.sabmiller.core.invoices.converters.reversePopulators.InvoiceDiscrepancyReversePopulator;
import com.sabmiller.core.invoices.services.SabmInvoiceService;
import com.sabmiller.core.invoices.strategy.SabmCreditAdjustmentSalesTeamReportStrategy;
import com.sabmiller.core.model.InvoiceDiscrepancyRequestModel;
import com.sabmiller.core.model.InvoiceDiscrepancyRequestNotificationEmailModel;
import com.sabmiller.core.model.InvoicePaymentDetailModel;
import com.sabmiller.core.model.InvoicePaymentModel;
import com.sabmiller.core.model.SABMNotificationModel;
import com.sabmiller.core.model.SABMNotificationPrefModel;
import com.sabmiller.core.notification.service.NotificationService;
import com.sabmiller.core.util.SabmDateUtils;
import com.sabmiller.core.util.SabmStringUtils;
import com.sabmiller.core.util.SabmUtils;
import com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade;
import com.sabmiller.facades.customer.InvoiceUpdateException;
import com.sabmiller.facades.customer.SABMInvoiceFacade;
import com.sabmiller.facades.invoice.SABMInvoiceData;
import com.sabmiller.facades.invoice.SABMInvoiceDiscrepancyData;
import com.sabmiller.facades.invoice.SABMInvoiceList;
import com.sabmiller.facades.invoice.SABMInvoiceValidationResult;
import com.sabmiller.facades.sfmc.context.SABMCreditApprovedEmailContextData;
import com.sabmiller.facades.sfmc.context.SABMCreditApprovedSMSContextData;
import com.sabmiller.facades.sfmc.context.SABMCreditRecievedEmailContextData;
import com.sabmiller.integration.restclient.commons.SABMIntegrationException;
import com.sabmiller.integration.sap.invoice.InvoiceItemDataRequestHandler;
import com.sabmiller.integration.sap.invoice.InvoiceListRequestHandler;
import com.sabmiller.integration.sap.invoice.InvoiceValidationeRequestHandler;
import com.sabmiller.integration.sap.invoices.discrepancy.request.InvoiceItemDataRequest;
import com.sabmiller.integration.sap.invoices.discrepancy.request.InvoiceListRequest;
import com.sabmiller.integration.sap.invoices.discrepancy.request.InvoiceValidationRequest;
import com.sabmiller.integration.sap.invoices.discrepancy.response.InvoiceItemDataResponse;
import com.sabmiller.integration.sap.invoices.discrepancy.response.InvoiceListResponse;
import com.sabmiller.integration.sap.invoices.discrepancy.response.InvoiceValidationResponse;
import com.sabmiller.merchantsuiteservices.dao.InvoicePaymentDao;
import com.sabmiller.sfmc.enums.SFMCRequestEmailTemplate;
import com.sabmiller.sfmc.enums.SFMCRequestSMSTemplate;
import com.sabmiller.sfmc.exception.SFMCClientException;
import com.sabmiller.sfmc.exception.SFMCEmptySubscribersException;
import com.sabmiller.sfmc.exception.SFMCRequestKeyNotFoundException;
import com.sabmiller.sfmc.exception.SFMCRequestPayloadException;
import com.sabmiller.sfmc.pojo.SFMCRequest;
import com.sabmiller.sfmc.pojo.SFMCRequestTo;
import com.sabmiller.sfmc.service.SabmSFMCService;


/**
 * The Class DefaultSABMInvoiceFacade.
 */
public class DefaultSABMInvoiceFacade implements SABMInvoiceFacade {

    /**
     * The Constant LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(DefaultSABMInvoiceFacade.class);

	 /* Stubs related constants */
	 private static final String INVOICELIST_STUB_AVAILABLE_CHECK = "cub.invoicelist.stub.available.check";
	 public static final String INVOICELIST_STUB_MEDIA_RESPONSE = "invoicelist.data.stub.response";

	 private static final String INVOICEVALID_STUB_AVAILABLE_CHECK = "cub.invoicevaliate.stub.available.check";
	 public static final String INVOICEVALID_STUB_MEDIA_RESPONSE = "invoice.validate.stub.response";

	 private static final String INVOICEITEM_STUB_AVAILABLE_CHECK = "cub.invoiceitem.stub.available.check";
	 public static final String INVOICEITEM_STUB_MEDIA_RESPONSE = "invoiceitem.data.stub.response";

    /**
     * The invoice payment dao.
     */
    @Resource(name = "sabminvoicePaymentDao")
    private InvoicePaymentDao invoicePaymentDao;

    /**
     * The model service.
     */
    @Resource(name = "modelService")
    private ModelService modelService;

    /**
     * The session service.
     */
    @Resource(name = "sessionService")
    private SessionService sessionService;

    /**
     * The base site service.
     */
    @Resource(name = "baseSiteService")
    private BaseSiteService baseSiteService;

    /**
     * The common i18 n service.
     */
    @Resource(name = "commonI18NService")
    private CommonI18NService commonI18NService;

    /**
     * The base store service.
     */
    @Resource(name = "baseStoreService")
    private BaseStoreService baseStoreService;

    /**
     * The user service.
     */
    @Resource(name = "userService")
    private UserService userService;

    @Resource(name = "b2bUnitService")
    private SabmB2BUnitService b2bUnitService;

    /**
     * The event service.
     */
    @Resource(name = "eventService")
    private EventService eventService;

    @Resource(name = "sabmInvoiceValidationRequestHandler")
    private InvoiceValidationeRequestHandler invoiceValidationeRequestHandler;

    @Resource(name = "sabmInvoiceListRequestHandler")
    private InvoiceListRequestHandler sabmInvoiceListRequestHandler;

    @Resource(name = "sabmInvoiceItemDataRequestHandler")
    private InvoiceItemDataRequestHandler sabmInvoiceItemDataRequestHandler;

    @Resource(name = "invoiceListRestConverter")
    private Converter<InvoiceListResponse.Invoice, SABMInvoiceList> invoiceListRestConverter;

    @Resource(name = "invoiceItemDataRestConverter")
    private Converter<InvoiceItemDataResponse.Invoice, SABMInvoiceDiscrepancyData> invoiceItemDataRestConverter;

    @Resource(name = "invoiceDiscrepancyReversePopulator")
    private InvoiceDiscrepancyReversePopulator invoiceDiscrepancyReversePopulator;

    @Resource(name = "invoiceService")
    private SabmInvoiceService invoiceService;

    @Resource(name = "invoiceDiscrepancyPopulator")
    private InvoiceDiscrepancyPopulator invoiceDiscrepancyPopulator;

    @Resource(name = "creditAdjustmentSAPReversePopulator")
    private CreditAdjustmentSAPReversePopulator creditAdjustmentSAPReversePopulator;

    @Resource(name = "b2bCommerceUnitFacade")
    private SabmB2BCommerceUnitFacade b2bCommerceUnitFacade;

    @Resource(name = "sabmEmailService")
    private SabmEmailService sabmEmailService;
    /**
     * The date format.
     */
    @Value(value = "${sap.service.invoice.dateformat:ddMMYYYY}")
    private String sapInvoiceInterfaceDateFormat;

    @Resource(name = "creditAdjustmentSalesTeamReportStrategy")
    private SabmCreditAdjustmentSalesTeamReportStrategy creditAdjustmentSalesTeamReportStrategy;

    @Resource(name = "sabmSFMCService")
    private SabmSFMCService sabmSFMCService;

    @Resource(name = "sabmCreditRecievedEmailRequestConverter")
    private AbstractPopulatingConverter sabmCreditRecievedEmailRequestConverter;

    @Resource(name = "sabmCreditApprovedEmailRequestConverter")
    private AbstractPopulatingConverter sabmCreditApprovedEmailRequestConverter;

    @Resource(name = "sabmCreditApprovedSMSRequestConverter")
    private AbstractPopulatingConverter sabmCreditApprovedSMSRequestConverter;

    @Resource(name = "notificationService")
    private NotificationService notificationService;


    @Resource(name = "configurationService")
    private ConfigurationService configurationService;

	 @Resource(name = "asahiConfigurationService")
	 private AsahiConfigurationService asahiConfigurationService;

	 @Autowired
	 private MediaService mediaService;

    private Map<String, String> salesforceEventMap;

    /**
	 * @return the salesforceEventMap
	 */
	public Map<String, String> getSalesforceEventMap()
	{
		return salesforceEventMap;
	}

	/**
	 * @param salesforceEventMap the salesforceEventMap to set
	 */
	public void setSalesforceEventMap(final Map<String, String> salesforceEventMap)
	{
		this.salesforceEventMap = salesforceEventMap;
	}


    /*
     * (non-Javadoc)
     *
     * @see com.sabmiller.facades.customer.SABMInvoiceFacade#sendPaymentConfirmationEmail(java.lang.String)
     */
    @Override
    public void sendPaymentConfirmationEmail(final String trackingNumber) {

        final InvoicePaymentModel invoice = invoicePaymentDao.getInvoice(trackingNumber);
        if (null != invoice && null != invoice.getInvoices()) {
            if (CollectionUtils.isEmpty(invoice.getInvoicesDetail())) {
                Map<String, SABMInvoiceData> invoiceMap = new HashMap<String, SABMInvoiceData>();
                if (null != sessionService.getAttribute(SabmCoreConstants.SESSION_B2BUNIT_INVOICES_MAP)) {
                    invoiceMap = sessionService.getAttribute(SabmCoreConstants.SESSION_B2BUNIT_INVOICES_MAP);
                }
                final List<InvoicePaymentDetailModel> list = new ArrayList<>();
                for (final String test : invoice.getInvoices()) {
                    if (invoiceMap.containsKey(test)) {
                        final SABMInvoiceData sabmInvoiceData = invoiceMap.get(test);
                        final InvoicePaymentDetailModel model = modelService.create(InvoicePaymentDetailModel._TYPECODE);
                        model.setInvoiceNumber(sabmInvoiceData.getInvoiceNumber());
                        model.setInvoiceTrackingNumber(trackingNumber);
                        model.setPurchaseOrderNumber(sabmInvoiceData.getPurchaseOrderNumber());
                        model.setAmount(sabmInvoiceData.getOpenAmount());
                        model.setType(sabmInvoiceData.getType());
                        list.add(model);
                    }

                }
                invoice.setInvoicesDetail(list);
                modelService.saveAll(invoice);
                sessionService.removeAttribute(SabmCoreConstants.SESSION_B2BUNIT_INVOICES_MAP);
            }
            createPaymentConfirmationEmailEvent(invoice);
        }

    }

    @Override
    public SABMInvoiceValidationResult validateInvoice(final String b2bUnit, final String invoiceNumber) {

        final InvoiceValidationRequest request = new InvoiceValidationRequest();
        request.setInvoiceNumber(invoiceNumber);
        request.setCustomerSoldTo(SabmStringUtils.stripLeadingZeroes(b2bUnit));

        final SABMInvoiceValidationResult result = new SABMInvoiceValidationResult();

        try {

            //check invoice already raise in SAP or not.
				InvoiceValidationResponse respone = null;
				if (asahiConfigurationService.getBoolean(INVOICEVALID_STUB_AVAILABLE_CHECK, false))
				{
					try
					{
						final MediaModel stubMedia = mediaService.getMedia(INVOICEVALID_STUB_MEDIA_RESPONSE);
						final InputStream targetStream = mediaService.getStreamFromMedia(stubMedia);
						final JAXBContext jaxbContext = JAXBContext.newInstance(InvoiceValidationResponse.class);
						final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
						final XMLInputFactory factory = XMLInputFactory.newInstance();
						final XMLEventReader fileSource = factory.createXMLEventReader(targetStream);
						final JAXBElement<InvoiceValidationResponse> userElement = unmarshaller.unmarshal(fileSource,
								InvoiceValidationResponse.class);
						respone = userElement.getValue();
					}
					catch (final Exception e)
					{
						LOG.error("Exception occurred while trying to create stub for CustomerBillingData ", e);
					}
				}
				else
				{
					respone = invoiceValidationeRequestHandler.sendPostRequest(request);
				}

            if (BooleanUtils.toBoolean(respone.getInvoice().getStatus())) {
                result.setInvoiceInSAP(true);

                //if invoice exists in SAP, then check invoice whether already raised requst  in Hybris or not.
                boolean exist = false;
                final List<InvoiceDiscrepancyRequestModel> invoices = invoiceService
                        .findRaisedInvoiceDiscrepancyByInvoiceNumber(invoiceNumber);

                if (invoices.size() > 0) {
                    exist = true;
                }
                result.setInvoiceDiscrepencyRequestRaised(exist);
            }

            return result;

        } catch (final SABMIntegrationException e) {
            LOG.error("Rest post call exception: " + ":" + e.getMessage());
        } catch (final Exception e) {
            LOG.error("fetch invoice number: " + invoiceNumber + "return error");
        }
        return null;
    }

    @Override
    /**
     * NOT cache it until further performance improvement required
     */
//  @Cacheable(value = "invoiceNumberList", key = "T(com.sabmiller.cache.impl.SabmCacheKeyGenerator).generateKey(false,true,false,'invoiceList')")
    public SABMInvoiceList fetchInvoices(final String b2bUnit) {

        final String endDate = SabmDateUtils.toString(new Date(), sapInvoiceInterfaceDateFormat);
        final String startDate = SabmDateUtils
                .toString(SabmDateUtils.minusDays(new Date(), Integer.valueOf(Config.getString("sap.service.invoice.daysrange", "14"))),
                        sapInvoiceInterfaceDateFormat);

        final InvoiceListRequest request = new InvoiceListRequest();
        request.setCustomerSoldTo(SabmStringUtils.stripLeadingZeroes(b2bUnit));
        request.setDateStart(startDate);
        request.setDateEnd(endDate);

        try {

			  InvoiceListResponse response = null;

			  if (asahiConfigurationService.getBoolean(INVOICELIST_STUB_AVAILABLE_CHECK, false))
			  {
				  try
				  {
				  final MediaModel stubMedia = mediaService.getMedia(INVOICELIST_STUB_MEDIA_RESPONSE);
				  final InputStream targetStream = mediaService.getStreamFromMedia(stubMedia);
				  final JAXBContext jaxbContext = JAXBContext.newInstance(InvoiceListResponse.class);
				  final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				  final XMLInputFactory factory = XMLInputFactory.newInstance();
				  final XMLEventReader fileSource = factory.createXMLEventReader(targetStream);
				  final JAXBElement<InvoiceListResponse> userElement = unmarshaller.unmarshal(fileSource, InvoiceListResponse.class);
				  response = userElement.getValue();
			  }
			  catch (final Exception e)
			  {
				  LOG.error("Exception occurred while trying to create stub for InvoiceList ", e);
			  }
			  }
			  else
			  {
				  response = sabmInvoiceListRequestHandler.sendPostRequest(request);
			  }

            final SABMInvoiceList invoiceList = invoiceListRestConverter.convert(response.getInvoice());
            return invoiceList;

        } catch (final SABMIntegrationException e) {
            LOG.error("Rest post call exception: " + ":" + e.getMessage());
        }

        return null;
    }

    @Override
    public SABMInvoiceDiscrepancyData getInvoiceData(final String b2bUnit, final String invoiceNumber) {

        final InvoiceItemDataRequest request = new InvoiceItemDataRequest();

        request.setCustomerSoldTo(SabmStringUtils.stripLeadingZeroes(b2bUnit));
        request.setInvoiceNumber(invoiceNumber);

        try {
			  InvoiceItemDataResponse response = null;

			  if (asahiConfigurationService.getBoolean(INVOICEITEM_STUB_AVAILABLE_CHECK, false))
				{
				try
				{
					final MediaModel stubMedia = mediaService.getMedia(INVOICEITEM_STUB_MEDIA_RESPONSE);
					final InputStream targetStream = mediaService.getStreamFromMedia(stubMedia);
					final JAXBContext jaxbContext = JAXBContext.newInstance(InvoiceItemDataResponse.class);
					final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
					final XMLInputFactory factory = XMLInputFactory.newInstance();
					final XMLEventReader fileSource = factory.createXMLEventReader(targetStream);
					final JAXBElement<InvoiceItemDataResponse> userElement = unmarshaller.unmarshal(fileSource,
							InvoiceItemDataResponse.class);
					response = userElement.getValue();
				}
				catch (final Exception e)
				{
					LOG.error("Exception occurred while trying to create stub for InvoiceItemData ", e);
				}
			}
			else
			{
				response = sabmInvoiceItemDataRequestHandler.sendPostRequest(request);
			}

            final SABMInvoiceDiscrepancyData invoiceSapData = invoiceItemDataRestConverter.convert(response.getInvoice());
            return invoiceSapData;

        } catch (final SABMIntegrationException e) {
            LOG.error("Rest post call exception: " + ":" + e.getMessage());
        }

        return null;
    }

    @Override
    public List<SABMInvoiceDiscrepancyData> fetchRaisedInvoicesForSelectedB2BUnit(final List<String> b2bUnits, final Date dateFrom,
            final Date dateTo) {

        final List<B2BUnitModel> selectedB2BUnits = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(b2bUnits)) {

            for (final String b2bUnit : b2bUnits) {
                selectedB2BUnits.add(b2bUnitService.getUnitForUid(b2bUnit));
            }

        }

        List<InvoiceDiscrepancyRequestModel> invoiceDiscrepancyList = new ArrayList<>();

        if (dateFrom != null && dateTo != null) {
            invoiceDiscrepancyList = invoiceService
                    .getRaisedInvoiceDiscrepancyForB2BUnitsAndForDateRange(selectedB2BUnits, dateFrom, dateTo);
        } else {
            invoiceDiscrepancyList = invoiceService.getRaisedInvoiceDiscrepancyForB2BUnit(selectedB2BUnits);
        }

        final List<SABMInvoiceDiscrepancyData> list = new ArrayList<SABMInvoiceDiscrepancyData>();


        for (final InvoiceDiscrepancyRequestModel model : invoiceDiscrepancyList) {

            final SABMInvoiceDiscrepancyData data = new SABMInvoiceDiscrepancyData();
            invoiceDiscrepancyPopulator.populate(model, data);

            list.add(data);
        }

        return list;
    }

    public boolean saveInvoiceDiscrepancyRequest(final SABMInvoiceDiscrepancyData request) {

        // 1. save invoiceDiscrepancy into hybris DB

        final InvoiceDiscrepancyRequestModel invoiceDiscrepancyRequestModel = modelService.create(InvoiceDiscrepancyRequestModel.class);

        invoiceDiscrepancyReversePopulator.populate(request, invoiceDiscrepancyRequestModel);

        try {
            modelService.save(invoiceDiscrepancyRequestModel);
        } catch (final ModelSavingException e) {
            LOG.error("error while save invoiceDiscrepancyRequestModel");
            return false;
        } catch (final ConversionException e) {
            LOG.error("error while convert invoiceDiscrepancyRequestModel in InvoiceDiscrepancyReversePopulator");
            return false;
        }

        // 2. send email to sale marketing team which is a Hybris internal email , if issue encoutners, return false;

        File file;
        try {
            file = creditAdjustmentSalesTeamReportStrategy.getEmailData(invoiceDiscrepancyRequestModel);

            sabmEmailService.sendCreditAdjustmentEmailToSupportTeam(file,configurationService.getConfiguration().getString("sabm.sftp.credit.remote.directory"));

            file.delete();

            // SabmCSVUtils.purgeOldFiles(
            //         SabmCSVUtils.getFullPath(SabmCoreConstants.CREDITADJUSTMENT_GENERATED_FILES_HYBRIS_FOLDER_MAIN).getPath());

        } catch (final Exception e) {
            LOG.error("Exception encountered while generating/sending the credit card orders file/email", e);
            return false;
        }

        modelService.save(invoiceDiscrepancyRequestModel);

        //3 . send email to Salesforce market cloud framework to trigger customer confirmation email , if issue encoutners, return false;

        final SFMCRequest sfmcrequest = new SFMCRequest();
        final List<SFMCRequestTo> toList = new ArrayList<>();

        // one object used by all email user who will receive confirmation email
        final SABMCreditRecievedEmailContextData creditRecievedEmailContextData = (SABMCreditRecievedEmailContextData) sabmCreditRecievedEmailRequestConverter
                .convert(invoiceDiscrepancyRequestModel);

        final SFMCRequestTo sfmcRequestTo = new SFMCRequestTo();
        sfmcRequestTo.setDynamicData(creditRecievedEmailContextData);
        sfmcRequestTo.setTo(invoiceDiscrepancyRequestModel.getRaisedBy().getEmail());
        sfmcRequestTo.setPk(invoiceDiscrepancyRequestModel.getRaisedBy().getPk().toString()+getSubscriberKeySuffixForSMFC());
        sfmcRequestTo.setEventId(salesforceEventMap.get(SFMCRequestEmailTemplate.CREDITRECIEVEDEMAIL.getCode()));
        toList.add(sfmcRequestTo);

        // Added below for loop logic inorder to fix - INC1076578 to send the email to cc address separately
        for(final InvoiceDiscrepancyRequestNotificationEmailModel model : org.apache.commons.collections4.CollectionUtils.emptyIfNull(invoiceDiscrepancyRequestModel.getConfirmationEmailList()))
        {
      	  final SFMCRequestTo sfRequestTo = new SFMCRequestTo();
      	  sfRequestTo.setDynamicData(creditRecievedEmailContextData);
      	  sfRequestTo.setTo(model.getEmailAddress());
      	  sfRequestTo.setPk(model.getUserPk()+getSubscriberKeySuffixForSMFC());
      	  sfRequestTo.setEventId(salesforceEventMap.get(SFMCRequestEmailTemplate.CREDITRECIEVEDEMAIL.getCode()));
           toList.add(sfRequestTo);
        }

        sfmcrequest.setToList(toList);
        sfmcrequest.setKey(SFMCRequestEmailTemplate.CREDITRECIEVEDEMAIL.getCode());
        sfmcrequest.setInitiatorEmail(invoiceDiscrepancyRequestModel.getRaisedBy().getEmail());

        try {
            sabmSFMCService.sendEmail(sfmcrequest);

        } catch (final SFMCClientException e) {
            LOG.error(e.getMessage());
            return false;
        } catch (final SFMCRequestPayloadException e) {
            LOG.error(e.getMessage());
            return false;
        } catch (final SFMCRequestKeyNotFoundException e) {
            LOG.error(e.getMessage());
            return false;
        } catch (final SFMCEmptySubscribersException e) {
            LOG.error(e.getMessage());
            return false;
        }

        /* set CreditAdjustmentConfirmationEmailSent fto true assume no error return from sfmc for credit confirmation email
        *  any issue/error occured for cc email, it will not be tracked ,only track to email
        */

        invoiceDiscrepancyRequestModel.setCreditAdjustmentConfirmationEmailSent(true);

        modelService.save(invoiceDiscrepancyRequestModel);

        return true;
    }

    /**
     * Update Credit approval process result from SAP call back and send email/sms base on subscription
     * @param data
     * @throws InvoiceUpdateException
     */
    @Override
    public void updateInvoiceDiscrepancyRequestWithProcessResult(final SABMInvoiceDiscrepancyData data) throws InvoiceUpdateException {

        final String invoiceNumber = data.getInvoiceNumber();
        final Double amount = Double.valueOf(data.getActualTotalAmount());
        final String requestId = data.getCreditAdjustmentRequestId();
        final String processResultDescription = data.getCreditAdjustmentStatusDescription();
        final String b2bUnitCode = data.getSoldTo();
        final String invoiceDate = data.getInvoiceDate();
        final String sapInvoiceNumber = data.getSapInvoiceNumber();
        final String sapInvoiceType = data.getInvoiceType();

        Assert.notNull(invoiceNumber, "InvoiceNumber cannot be null.");

        final B2BUnitModel b2BUnitModel = b2bUnitService.getUnitForUid(b2bUnitCode);

        if(Objects.isNull(b2BUnitModel)){
            throw new InvoiceUpdateException("the b2bUnit code: "+b2bUnitCode+ " is not valid in B2B(Hybris) online application");

        }

        List<InvoiceDiscrepancyRequestModel> result =new ArrayList<>();


        if(StringUtils.isNotEmpty(requestId)) {
            result = invoiceService.findRaisedInvoiceDiscrepancyByInvoiceNumberAndRequestId(invoiceNumber, requestId);
        }
        if (StringUtils.isEmpty(requestId) && StringUtils.isNotEmpty(sapInvoiceNumber)){
            result = invoiceService.findRaisedInvoiceDiscrepancyByInvoiceNumberAndSapInvoiceNumber(invoiceNumber, sapInvoiceNumber);
        }


        /**
         * Invoice Discrenpancy is NOT raised From Hybris, it has to now store as one record in InvoiceDiscrepancyRequestModel and mark as Raided from SAP
         * Request ID has to be empty
         */
        if (CollectionUtils.isEmpty(result) && StringUtils.isEmpty(requestId)) {
            LOG.info("Invoice Discrenpancy is NOT raised From Hybris , invoiceNumber: "+invoiceNumber + "and requestId: " + requestId);

            /*
            Save invoice Discrepancy into Hybris even it is not raised from Hybris in order to send email/sms to credit adjustment has been made

              setRaisedFrom(InvoiceDiscrepancyRaisedFromEnum.SAP);
             */

            final InvoiceDiscrepancyRequestModel invoiceDiscrepancyRequestModel = modelService.create(InvoiceDiscrepancyRequestModel.class);

            creditAdjustmentSAPReversePopulator.populate(data, invoiceDiscrepancyRequestModel);

            try {
                modelService.save(invoiceDiscrepancyRequestModel);
            } catch (final ModelSavingException e) {
                LOG.error("error while save invoiceDiscrepancyRequestModel");

            } catch (final ConversionException e) {
                LOG.error("error while convert invoiceDiscrepancyRequestModel in InvoiceDiscrepancyReversePopulator");

            }

            sendEmailOrSmsForCreditAdjustmentApprovedProcessResult(b2BUnitModel,invoiceDiscrepancyRequestModel, false);

        }

        if (CollectionUtils.isNotEmpty(result) && StringUtils.isEmpty(requestId)) {
            throw new InvoiceUpdateException("Invoice Number: " + invoiceNumber + " and requestId: " + requestId
                    + ". We found existing records, credit adjustment approved email/sms already sent , same payload has sent before");
        }

        if (StringUtils.isNotEmpty(requestId) && result.size() > 1) {

            throw new InvoiceUpdateException("Invoice Number: " + invoiceNumber + " and requestId: " + requestId
                    + ". We found multiple records, please verify invocie number or request Id");

        }

        if (CollectionUtils.isEmpty(result) && StringUtils.isNotEmpty(requestId)) {
            throw new InvoiceUpdateException("Invoice Number: " + invoiceNumber + " and requestId: " + requestId
                    + ". We couldn't find any result for above invoice and request Id");
        }

        /**
         * Invoice Discrenpancy is  Raised From Hybris
         */
        if (StringUtils.isNotEmpty(requestId) && result.size() == 1) {
            final InvoiceDiscrepancyRequestModel invoiceDiscrepancyRequestModel = result.get(0);
            invoiceDiscrepancyRequestModel.setProcessResult(InvoiceDiscrepancyProcessResultEnum.APPROVED);
            invoiceDiscrepancyRequestModel.setProcessResultDescription(processResultDescription);
            invoiceDiscrepancyRequestModel.setSapInvoiceNumber(sapInvoiceNumber);
            invoiceDiscrepancyRequestModel.setTotalAmountCreditedFromSAP(amount);
            invoiceDiscrepancyRequestModel.setSapInvoiceType(sapInvoiceType);

            if (StringUtils.isNotEmpty(invoiceDate)) {
                try {
                    invoiceDiscrepancyRequestModel.setInvoiceDate(SabmDateUtils.getDate(invoiceDate, sapInvoiceInterfaceDateFormat));
                } catch (final ParseException e) {
                    LOG.error("error while convert data format");
                }
            }

            try {
                modelService.save(invoiceDiscrepancyRequestModel);
            } catch (final ModelSavingException e) {
                LOG.error("error while save invoiceDiscrepancyRequestModel");
            }


            sendEmailOrSmsForCreditAdjustmentApprovedProcessResult(b2BUnitModel,invoiceDiscrepancyRequestModel,true);

        }

    }

    /**
     * Creates the payment confirmation email event.
     *
     * @param invoice the invoice
     */
    public void createPaymentConfirmationEmailEvent(final InvoicePaymentModel invoice) {
        final PaymentConfirmationEmailEvent event = new PaymentConfirmationEmailEvent();
        event.setInvoicePayment(invoice);
        event.setBaseStore(baseStoreService.getBaseStoreForUid("sabmStore"));
        event.setSite(baseSiteService.getBaseSiteForUID("sabmStore"));
        event.setCustomer((CustomerModel) userService.getCurrentUser());
        event.setLanguage(commonI18NService.getLanguage("en"));
        event.setCurrency(commonI18NService.getCurrency("AUD"));
        eventService.publishEvent(event);
    }

    private void sendEmailOrSmsForCreditAdjustmentApprovedProcessResult(final B2BUnitModel b2BUnitModel,final InvoiceDiscrepancyRequestModel model,final boolean raisedFromHybris){

        final List<SABMNotificationModel> notifications = notificationService
                .getNotificationsForUnit(NotificationType.CREDITPROCESSED, true, b2BUnitModel);


            /*once it is approved credit adjustment, do following things :
            1. find b2bUnit belong to invoice discrepancy request,
            2. get all SabmNotification and iterate each notification preference.
            3. check each each notification preference, if subscribe to email and sms, send email via sfmc
            */

        // Construct sfmcRequestEmails object
        final SFMCRequest sfmcRequestEmails = new SFMCRequest();
        final List<SFMCRequestTo> toListEmails = new ArrayList<>();
        // one object used by all email user who will receive credit approved email
        final SABMCreditApprovedEmailContextData creditApprovedEmailContextData = (SABMCreditApprovedEmailContextData) sabmCreditApprovedEmailRequestConverter
                .convert(model);

        // Construct sfmcRequestSMS object
        final SFMCRequest sfmcRequestSMS = new SFMCRequest();
        final List<SFMCRequestTo> toListSMS = new ArrayList<>();
        // one object used by all SMS user who will receive credit approved SMS message
        final SABMCreditApprovedSMSContextData creditApprovedSMSContextData = (SABMCreditApprovedSMSContextData) sabmCreditApprovedSMSRequestConverter
                .convert(model);

        for (final SABMNotificationModel notification : notifications) {
      	  if(notification.getUser() != null && notification.getUser().getActive() != null && !notification.getUser().getActive()) {
      		  LOG.error("User is Inactive for this Notification: [{}] " ,notification.getUser().getUid());
      		  continue;
      	  }

			  if (notification.getUser() != null
					  && SabmUtils.isUserDisabledForCUBAccount(notification.getB2bUnit(), notification.getUser()))
			  {
      		  LOG.error("User [{}] is Inactive for this Notification for B2bUnit : [{}] " ,notification.getUser().getUid(), notification.getB2bUnit().getUid());
      		  continue;
      	  }

            for (final SABMNotificationPrefModel preference : notification.getNotificationPreferences()) {

                if (preference.getNotificationTypeEnabled() && preference.getEmailEnabled() && NotificationType.CREDITPROCESSED
                        .equals(preference.getNotificationType())) {
                    //send Email via SFMC
                    final SFMCRequestTo sfmcRequestTo = new SFMCRequestTo();
                    sfmcRequestTo.setDynamicData(creditApprovedEmailContextData);
                    sfmcRequestTo.setTo(notification.getUser().getEmail());
                    sfmcRequestTo.setPk(notification.getUser().getPk().toString()+getSubscriberKeySuffixForSMFC());
                    sfmcRequestTo.setEventId(salesforceEventMap.get(SFMCRequestEmailTemplate.CREDITAPPROVEDEMAIL.getCode()));
                    toListEmails.add(sfmcRequestTo);

                }

                if (preference.getNotificationTypeEnabled() && preference.getSmsEnabled() && NotificationType.CREDITPROCESSED
                        .equals(preference.getNotificationType())) {
                    //send SMS via SFMC
                    final SFMCRequestTo sfmcRequestTo = new SFMCRequestTo();
                    sfmcRequestTo.setDynamicData(creditApprovedSMSContextData);


                    // make sure mobile number is not empty in any cases, otherwise , sfmc will reject
                    if (StringUtils.isEmpty(notification.getUser().getMobileContactNumber())){
                        LOG.error("Mobile number can not be empty for sfmc credit Approved sms integration for user: [{}] " ,notification.getUser());
                        continue;
                    }


                    sfmcRequestTo.setTo(SabmStringUtils
                            .convertToInternationalMobileNumber(notification.getUser().getMobileContactNumber()));
                    sfmcRequestTo.setPk(notification.getUser().getPk().toString()+getSubscriberKeySuffixForSMFC());
                    sfmcRequestTo.setEventId(salesforceEventMap.get(SFMCRequestSMSTemplate.CREDITAPPROVEDSMS.getCode()));

                    toListSMS.add(sfmcRequestTo);
                }

            }
        }
        sfmcRequestEmails.setToList(toListEmails);
        sfmcRequestEmails.setKey(SFMCRequestEmailTemplate.CREDITAPPROVEDEMAIL.getCode());

        if(raisedFromHybris && !Objects.isNull(model.getRaisedBy())) {
            sfmcRequestEmails.setInitiatorEmail(model.getRaisedBy().getEmail());
            sfmcRequestSMS.setInitiatorEmail(model.getRaisedBy().getEmail());
        }
        else {
            sfmcRequestEmails.setInitiatorEmail(configurationService.getConfiguration().getString("sfmc.default.initiator.Email.from","noreply@cub.com"));
            sfmcRequestSMS.setInitiatorEmail(configurationService.getConfiguration().getString("sfmc.default.initiator.Email.from","noreply@cub.com"));
        }


        sfmcRequestSMS.setToList(toListSMS);
        sfmcRequestSMS.setKey(SFMCRequestSMSTemplate.CREDITAPPROVEDSMS.getCode());

        try {
            if (!model.getCreditAdjustmentProcessEmailSent() && CollectionUtils.isNotEmpty(sfmcRequestEmails.getToList())) {
                sabmSFMCService.sendEmail(sfmcRequestEmails);
                model.setCreditAdjustmentProcessEmailSent(true);
            } else {
                LOG.info("credit approved email already sent for b2bUnit: " + b2BUnitModel.getUid());
            }
            if (!model.getCreditAdjustmentProcessSmsSent() && CollectionUtils.isNotEmpty(sfmcRequestSMS.getToList())) {
                sabmSFMCService.sendSMS(sfmcRequestSMS);
                model.setCreditAdjustmentProcessSmsSent(true);
            } else {
                LOG.info("credit approved SMS already sent for b2bUnit: " +  b2BUnitModel.getUid());
            }

        } catch (final SFMCClientException e) {
            LOG.error(e.getMessage());
            return;

        } catch (final SFMCRequestPayloadException e) {
            LOG.error(e.getMessage());
            return;

        } catch (final SFMCRequestKeyNotFoundException e) {
            LOG.error(e.getMessage());
            return;

        } catch (final SFMCEmptySubscribersException e) {
            LOG.error(e.getMessage());
            return;

        }


        //save model
        try {
            modelService.save(model);
        } catch (final ModelSavingException e) {
            LOG.error("error while save invoiceDiscrepancyRequestModel");
        }
    }

		/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.customer.SABMInvoiceFacade#getInvoiceByTrackingNumber(java.lang.String)
	 */
	@Override
	public double getSurchargeAmtforInvoiceByTrackingNumber(final String trackingNumber)
	{

		if (StringUtils.isNotEmpty(trackingNumber))
		{
			final InvoicePaymentModel invoice = invoicePaymentDao.getInvoice(trackingNumber);

			if (invoice != null)
			{
				final BigDecimal surcharge = getMsf(invoice);
				return surcharge.doubleValue();
			}
		}

		return 0.0;

	}

	/**
	 * @param invoicePayment
	 * @return
	 */
	private BigDecimal getMsf(final InvoicePaymentModel invoicePayment)
	{
		BigDecimal msfValue = null;
		if (CollectionUtils.isNotEmpty(invoicePayment.getTransaction().getEntries()))
		{
			for (final PaymentTransactionEntryModel entry : invoicePayment.getTransaction().getEntries())
			{
				if (PaymentTransactionType.SURCHARGE.equals(entry.getType()))
				{
					msfValue = entry.getAmount();

				}
			}
		}
		return msfValue != null ? msfValue : BigDecimal.valueOf(0.0);
	}

    private String getSubscriberKeySuffixForSMFC() {

        if("prod".equalsIgnoreCase(configurationService.getConfiguration().getString("envType", "prod"))){
            return StringUtils.EMPTY;
        }
		else
		{
			return StringUtils.EMPTY;
		}


    }


}

