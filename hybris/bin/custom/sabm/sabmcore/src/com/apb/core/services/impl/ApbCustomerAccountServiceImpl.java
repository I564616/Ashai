package com.apb.core.services.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.catalog.model.CatalogUnawareMediaModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.customer.impl.DefaultCustomerAccountService;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;
import de.hybris.platform.commerceservices.event.ForgottenPwdEvent;
import de.hybris.platform.commerceservices.event.RegisterEvent;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.security.SecureToken;
import de.hybris.platform.commerceservices.security.SecureTokenService;
import de.hybris.platform.commons.renderer.exceptions.RendererException;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.payment.AdapterException;
import de.hybris.platform.payment.dto.BillingInfo;
import de.hybris.platform.payment.dto.CardInfo;
import de.hybris.platform.payment.dto.NewSubscription;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.media.MediaIOException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.ticket.enums.CsTicketCategory;
import de.hybris.platform.ticket.enums.CsTicketPriority;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.util.Config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.customer.dao.AsahiCustomerAccountDao;
import com.apb.core.event.ApbCompanyDetailsEvent;
import com.apb.core.event.ApbContactUsEvent;
import com.apb.core.event.ApbKegReturnEvent;
import com.apb.core.event.AsahiAssistedForgottenPwdEvent;
import com.apb.core.event.AsahiCustomerWelcomeEmailEvent;
import com.apb.core.event.AsahiOrderPlacedEvent;
import com.apb.core.event.AsahiPasswordResetEmailEvent;
import com.apb.core.event.AsahiPayerAccessEvent;
import com.apb.core.event.RequestRegisterEvent;
import com.apb.core.event.SuperRegisterEvent;
import com.apb.core.model.ApbCompanyDetailsEmailModel;
import com.apb.core.model.ApbKegReturnEmailModel;
import com.apb.core.model.ApbRequestRegisterEmailModel;
import com.apb.core.model.AsahiEmployeeModel;
import com.apb.core.model.ContactUsQueryEmailModel;
import com.apb.core.model.KegReturnSizeModel;
import com.apb.core.model.ProdPricingTierModel;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.services.ApbCustomerAccountService;
import com.apb.core.services.ApbNumberKeyGeneratorService;
import com.apb.core.util.ApbEmailConfigurationUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.dao.b2bunit.ApbB2BUnitDao;
import com.apb.facades.contactust.data.ApbContactUsData;
import com.apb.facades.contactust.data.AsahiContactUsSaleRepData;
import com.apb.facades.user.data.ApbCompanyData;
import com.apb.integration.credit.check.service.AsahiCreditCheckIntegrationService;
import com.apb.integration.data.ApbCreditCheckData;
import com.apb.service.b2bunit.ApbB2BUnitService;
import com.asahi.facades.planograms.PlanogramData;
import com.sabmiller.core.enums.AddressType;
import com.sabmiller.core.enums.AsahiEnquirySubType;
import com.sabmiller.core.enums.AsahiEnquiryType;
import com.sabmiller.core.enums.PlanogramAssociationType;
import com.sabmiller.core.event.ProfileUpdatedNoticeEvent;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.AsahiCatalogProductMappingModel;
import com.sabmiller.core.model.AsahiSAMAccessModel;
import com.sabmiller.core.model.BDECustomerModel;
import com.sabmiller.core.model.EnquiryTypeContactMappingModel;
import com.sabmiller.core.model.PlanogramModel;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;

/**
 * @author C5252631
 *
 *         DefaultApbCustomerAccountService implementation of {@link DefaultCustomerAccountService}
 */
public class ApbCustomerAccountServiceImpl extends DefaultCustomerAccountService implements ApbCustomerAccountService
{
	private static Logger LOG = LoggerFactory.getLogger("ApbCustomerAccountServiceImpl");

	/** The Constant PAYER_ACCESS_APPROVE. */
	private static final String PAYER_ACCESS_APPROVE = "approve";

	/** The Constant PAYER_ACCESS_REJECT. */
	private static final String PAYER_ACCESS_REJECT = "reject";

	/** The Constant PAYER_ACCESS_REQUEST. */
	private static final String PAYER_ACCESS_REQUEST = "request";

	/** The Constant PAYER_ACCESS_REQUEST_BY_SUPERUSER. */
	private static final String PAYER_ACCESS_SUPERUSER_REQUEST = "superUserRequest";

	/** The Constant PAYER_ACCESS_REQUEST_BY_SUPERUSER. */
	private static final String PAYER_ACCESS_EXPIRED = "expired";

	private static final String STAFF_PERSON = "Territory Manager";

	private long asahiTokenValiditySeconds;

	@Autowired
	private ApbNumberKeyGeneratorService apbNumberKeyGeneratorService;

	@Resource(name = "asahiCreditCheckIntegrationService")
	private AsahiCreditCheckIntegrationService asahiCreditCheckIntegrationService;

	@Autowired
	private Converter<AsahiB2BUnitModel, ApbCompanyData> apbCompanyDetailsConverter;

	@Autowired
	private Converter<AsahiB2BUnitModel, ApbContactUsData> apbContactUsConverter;

	@Autowired
	private Converter<AsahiContactUsSaleRepData, ApbContactUsData> asahiContactUsSalesRepDataConverter;

	@Autowired
	private Converter<AsahiEmployeeModel, AsahiContactUsSaleRepData> asahiContactUsSalesRepConverter;

	/** The asahi customer account dao. */
	@Resource(name = "asahiCustomerAccountDao")
	private AsahiCustomerAccountDao asahiCustomerAccountDao;

	@Autowired
	private CMSSiteService cmsSiteService;

	@Resource(name = "apbEmailConfigurationUtil")
	private ApbEmailConfigurationUtil apbEmailConfigurationUtil;

	/** The asahi configuration service. */
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	/** The apb B2B unit service. */
	@Resource(name = "apbB2BUnitService")
	private ApbB2BUnitService apbB2BUnitService;

	@Resource(name = "secureTokenService")
	private SecureTokenService secureTokenService;

	/** The apb B 2 B unit dao. */
	@Resource(name = "apbB2BUnitDao")
	private ApbB2BUnitDao apbB2BUnitDao;

	@Resource(name = "enumerationService")
	private EnumerationService enumerationService;

	@Resource
	private SessionService sessionService;

	@Autowired
	private MediaService mediaService;


	@Override
	public void register(final B2BCustomerModel b2bCustomerModel, final String password, final Set<AsahiB2BUnitModel> asahiUnits)
			throws DuplicateUidException
	{
		LOG.trace("register {}");
		registerCustomer(b2bCustomerModel, password);
		getEventService().publishEvent(initializeEvent(new SuperRegisterEvent(), b2bCustomerModel, asahiUnits));
		getEventService().publishEvent(initializeEvent(new RegisterEvent(), b2bCustomerModel));
	}



	public AbstractCommerceUserEvent initializeEvent(final SuperRegisterEvent event, final B2BCustomerModel b2bCustomerModel,
			final Set<AsahiB2BUnitModel> asahiUnits)
	{
		event.setAsahiUnits(asahiUnits);
		return super.initializeEvent(event, b2bCustomerModel);
	}

	/**
	 * Sending Request Registration Email
	 *
	 * @param requestRegistrationEmailModel
	 * @throws DuplicateUidException
	 */
	public void sendRequestRegisterEmail(final ApbRequestRegisterEmailModel requestRegistrationEmailModel)
			throws DuplicateUidException, RendererException
	{
		LOG.trace("sendRequestRegistrationEmail {}");
		saveRequestRegistrationEmail(requestRegistrationEmailModel);
		getEventService().publishEvent(initializeEvent(new RequestRegisterEvent(), requestRegistrationEmailModel));
		if (requestRegistrationEmailModel.getReferenceNumber() != null)
		{
			throw new RendererException(requestRegistrationEmailModel.getReferenceNumber());
		}
	}

	private AbstractEvent initializeEvent(final RequestRegisterEvent event,
			final ApbRequestRegisterEmailModel requestRegistrationEmailModel)
	{
		event.setBaseStore(getBaseStoreService().getCurrentBaseStore());
		event.setSite(getBaseSiteService().getCurrentBaseSite());
		event.setRequestRegisterEmail(requestRegistrationEmailModel);
		event.setLanguage(getCommonI18NService().getCurrentLanguage());
		event.setCurrency(getCommonI18NService().getCurrentCurrency());
		return event;
	}

	/**
	 * Saves the customer translating model layer exceptions regarding duplicate identifiers
	 */
	protected void saveRequestRegistrationEmail(final ApbRequestRegisterEmailModel requestRegistrationEmailModel)
			throws DuplicateUidException
	{
		final String referencePrefixCode = asahiConfigurationService.getString(ApbCoreConstants.REQUEST_REFERENCE_PREFIX
				+ cmsSiteService.getCurrentSite().getUid(), "");
		try
		{
			requestRegistrationEmailModel.setReferenceNumber(apbNumberKeyGeneratorService.generateCode(referencePrefixCode));
			getModelService().save(requestRegistrationEmailModel);
			saveCsTicket(requestRegistrationEmailModel);
		}
		catch (final ModelSavingException e)
		{
			throw new DuplicateUidException(requestRegistrationEmailModel.getCode(), e);
		}
		catch (final AmbiguousIdentifierException e)
		{
			throw new DuplicateUidException(requestRegistrationEmailModel.getCode(), e);
		}
	}

	/**
	 * @param requestRegistrationEmailModel
	 */
	private void saveCsTicket(final ApbRequestRegisterEmailModel requestRegistrationEmailModel)
	{
		final CsTicketModel csTicketModel = getModelService().create(CsTicketModel.class);
		csTicketModel.setTicketID(requestRegistrationEmailModel.getReferenceNumber());
		csTicketModel.setHeadline(apbEmailConfigurationUtil.getSubject(requestRegistrationEmailModel));
		csTicketModel.setCategory(CsTicketCategory.ENQUIRY);
		csTicketModel.setPriority(CsTicketPriority.MEDIUM);
		csTicketModel.setBaseSite(cmsSiteService.getCurrentSite());
		getModelService().save(csTicketModel);
		getModelService().refresh(csTicketModel);
	}

	/**
	 * Credit Limit check of log in customer for order purchase online
	 *
	 * @return
	 */
	public boolean getCustomerAccountCreditLimit()
	{
		// Call to service hereasahiCreditCheckIntegrationService
		boolean isBlockedCredit = Boolean.FALSE;

		final UserModel user = getUserService().getCurrentUser();
		if (user instanceof B2BCustomerModel)
		{
			final B2BCustomerModel b2bCustomerModel = (B2BCustomerModel) getUserService().getCurrentUser();
			final B2BUnitModel b2bUnitModel = b2bCustomerModel.getDefaultB2BUnit();

			if (b2bUnitModel instanceof AsahiB2BUnitModel)
			{
				final AsahiB2BUnitModel asahiB2BUnit = (AsahiB2BUnitModel) b2bUnitModel;
				final String accNumber = asahiB2BUnit.getAccountNum();
				final ApbCreditCheckData creditCheckData = asahiCreditCheckIntegrationService.getCreditCheck(accNumber);
				if (creditCheckData != null)
				{
					isBlockedCredit = creditCheckData.isIsBlocked();

					final double creditRemaining = creditCheckData.getCreditRemaining();
					asahiB2BUnit.setIsCreditBlock(isBlockedCredit);
					asahiB2BUnit.setCreditRemaining(creditRemaining);
					getModelService().save(asahiB2BUnit);
					if (creditRemaining < 1 || isBlockedCredit)
					{
						return isBlockedCredit = Boolean.TRUE;
					}
					return isBlockedCredit;
				}
			}
		}
		return isBlockedCredit;
	}

	public ApbCompanyData getB2BCustomerData()
	{
		final ApbCompanyData apbCompanyData = new ApbCompanyData();
		final UserModel user = getUserService().getCurrentUser();
		if (user instanceof B2BCustomerModel)
		{
			final B2BCustomerModel b2bCustomerModel = (B2BCustomerModel) getUserService().getCurrentUser();
			final B2BUnitModel b2bUnitModel = b2bCustomerModel.getDefaultB2BUnit();
			if (b2bUnitModel instanceof AsahiB2BUnitModel)
			{
				final AsahiB2BUnitModel asahiB2BUnitModel = (AsahiB2BUnitModel) b2bUnitModel;
				apbCompanyDetailsConverter.convert(asahiB2BUnitModel, apbCompanyData);
			}
		}
		return apbCompanyData;
	}

	/**
	 * Gets the order list.
	 *
	 * @param customerModel
	 *           the customer model
	 * @param store
	 *           the store
	 * @param status
	 *           the status
	 * @param pageableData
	 *           the pageable data
	 * @return the order list
	 * @throws ParseException
	 */
	@Override
	public SearchPageData<OrderModel> getOrderList(final CustomerModel customerModel, final BaseStoreModel store,
			final PageableData pageableData, final String cofoDate) throws ParseException
	{
		return this.asahiCustomerAccountDao.findOrdersByCustomerAndStore(customerModel, store, pageableData, cofoDate);
	}

	@Override
	public void updateCompanyDetails(final ApbCompanyDetailsEmailModel apbCompanyDetailsEmailModel)

	{
		LOG.debug("updateCompanyDetails {}");
		saveCompanyDetailsEmail(apbCompanyDetailsEmailModel);
		getEventService().publishEvent(initializeEvent(new ApbCompanyDetailsEvent(), apbCompanyDetailsEmailModel));
	}

	private AbstractEvent initializeEvent(final ApbCompanyDetailsEvent event,
			final ApbCompanyDetailsEmailModel apbCompanyDetailsEmailModel)
	{
		event.setBaseStore(getBaseStoreService().getCurrentBaseStore());
		event.setSite(getBaseSiteService().getCurrentBaseSite());
		event.setApbCompanyDetailsEmailModel(apbCompanyDetailsEmailModel);
		event.setLanguage(getCommonI18NService().getCurrentLanguage());
		event.setCurrency(getCommonI18NService().getCurrentCurrency());
		if (getUserService().getCurrentUser() instanceof B2BCustomerModel)
		{
			final B2BCustomerModel b2bCustomerModel = (B2BCustomerModel) getUserService().getCurrentUser();
			event.setCustomer(b2bCustomerModel);
		}
		return event;
	}

	private void saveCompanyDetailsEmail(final ApbCompanyDetailsEmailModel apbCompanyDetailsEmail)
	{
		final String referencePrefixCode = asahiConfigurationService.getString(ApbCoreConstants.COMPANY_REFERENCE_PREFIX
				+ cmsSiteService.getCurrentSite().getUid(), "");
		try
		{
			if (getUserService().getCurrentUser() instanceof B2BCustomerModel)
			{
				final B2BCustomerModel b2bCustomerModel = (B2BCustomerModel) getUserService().getCurrentUser();
				apbCompanyDetailsEmail.setCustomer(b2bCustomerModel);
			}
			apbCompanyDetailsEmail.setReferenceNumber(apbNumberKeyGeneratorService.generateCode(referencePrefixCode).toString());
			getModelService().save(apbCompanyDetailsEmail);
			saveCsTicket(apbCompanyDetailsEmail);
		}
		catch (final ModelSavingException e)
		{
			LOG.error("Model Saving Exception " + e.getMessage());
			//throw new DuplicateUidException(apbCompanyDetailsEmail.getCode(), e);
		}
		catch (final AmbiguousIdentifierException e)
		{
			LOG.error("Ambiguous Identifier Exception " + e.getMessage());
			//throw new DuplicateUidException(apbCompanyDetailsEmail.getCode(), e);
		}
	}

	/**
	 * @param requestRegistrationEmailModel
	 */
	private void saveCsTicket(final ApbCompanyDetailsEmailModel apbCompanyDetailsEmailModel)
	{
		final CsTicketModel csTicketModel = getModelService().create(CsTicketModel.class);
		csTicketModel.setTicketID(apbCompanyDetailsEmailModel.getReferenceNumber());
		csTicketModel.setHeadline(apbEmailConfigurationUtil.getSubject(apbCompanyDetailsEmailModel));
		csTicketModel.setCategory(CsTicketCategory.ENQUIRY);
		csTicketModel.setPriority(CsTicketPriority.MEDIUM);
		csTicketModel.setBaseSite(cmsSiteService.getCurrentSite());
		getModelService().save(csTicketModel);
		getModelService().refresh(csTicketModel);
	}

	/**
	 * @return
	 */
	public Converter<AsahiB2BUnitModel, ApbCompanyData> getApbCompanyDetailsConverter()
	{
		return apbCompanyDetailsConverter;
	}

	/**
	 * @param apbCompanyDetailsConverter
	 */
	public void setApbCompanyDetailsConverter(final Converter<AsahiB2BUnitModel, ApbCompanyData> apbCompanyDetailsConverter)
	{
		this.apbCompanyDetailsConverter = apbCompanyDetailsConverter;
	}

	@Override
	public List<B2BCustomerModel> findB2BCustomerByGroup(final AsahiB2BUnitModel unit, final String userGroupId)
	{
		return asahiCustomerAccountDao.findB2BCustomerByGroup(unit, userGroupId);
	}

	@Override
	public OrderModel getOrderForCode(final AsahiB2BUnitModel b2bUnitModel, final String code, final BaseStoreModel store)
	{
		validateParameterNotNull(b2bUnitModel, "b2bUnit Model cannot be null");
		validateParameterNotNull(code, "Order code cannot be null");
		validateParameterNotNull(store, "Store must not be null");
		return this.asahiCustomerAccountDao.findOrderByB2BUnitAndCodeAndStore(b2bUnitModel, code, store);

	}

	@Override
	public ApbContactUsData getLogedInB2BCustomer()
	{
		final ApbContactUsData apbContactUsData = new ApbContactUsData();
		final UserModel user = getUserService().getCurrentUser();
		AsahiContactUsSaleRepData asahiContactUsSaleRepData = null;
		if (user instanceof B2BCustomerModel)
		{
			final B2BCustomerModel b2bCustomerModel = (B2BCustomerModel) getUserService().getCurrentUser();
			final B2BUnitModel b2bUnitModel = b2bCustomerModel.getDefaultB2BUnit();
			apbContactUsData.setName(b2bCustomerModel.getDisplayName());
			if (user instanceof BDECustomerModel) {
				apbContactUsData.setEmailAddress(b2bCustomerModel.getEmail());
			} else {
				apbContactUsData.setEmailAddress(b2bCustomerModel.getContactEmail());
			}
			apbContactUsData.setContactNumber(b2bCustomerModel.getContactNumber());

			if (b2bUnitModel instanceof AsahiB2BUnitModel)
			{
				final AsahiB2BUnitModel asahiB2BUnitModel = (AsahiB2BUnitModel) b2bUnitModel;
				apbContactUsConverter.convert(asahiB2BUnitModel, apbContactUsData);

				//Get the sales representative
				if (!asahiSiteUtil.isSga())
				{
					asahiContactUsSaleRepData = getAsahiSalesRep(asahiB2BUnitModel.getSalesRepCode());
					if (null != asahiContactUsSaleRepData)
					{
						asahiContactUsSalesRepDataConverter.convert(asahiContactUsSaleRepData, apbContactUsData);
					}
				}

				if(asahiConfigurationService.getBoolean("sga.contactus.update.available", false) && asahiSiteUtil.isSga()) {
					final AsahiSAMAccessModel samaccess = getAccessModel(b2bCustomerModel, asahiB2BUnitModel);
					if(null != samaccess)
					{
						apbContactUsData.setPayAccess(samaccess.isPayAccess());
					}
					else
					{
						apbContactUsData.setPayAccess(false);
					}
				}
			}
		}
		return apbContactUsData;
	}




	private AsahiContactUsSaleRepData getAsahiSalesRep(final String salesRepCode)
	{
		// TODO Auto-generated method stub

		if (StringUtils.isNotEmpty(salesRepCode))
		{
			//get the sale rep from db
			final AsahiContactUsSaleRepData asahiContactUsSaleRepData = new AsahiContactUsSaleRepData();
			LOG.info("sales rep with the ref id " + salesRepCode);
			final AsahiEmployeeModel asahiEmployeeModel = this.asahiCustomerAccountDao.findAsahiSalesRepById(salesRepCode);
			if (null != asahiEmployeeModel)
			{
				asahiContactUsSalesRepConverter.convert(asahiEmployeeModel, asahiContactUsSaleRepData);
			}
			return asahiContactUsSaleRepData;

		}
		else
		{
			LOG.info("No sales rep defined for the b2b unit.");
			return null;
		}


	}

	/**
	 * Sending Request Registration Email
	 *
	 * @param contactUsQueryEmailModel
	 *
	 */
	public String sendContactUsQueryEmail(final ContactUsQueryEmailModel contactUsQueryEmailModel)
	{
		LOG.trace("contactUsQueryEmailModel {}");
		saveContactUsQueryEmail(contactUsQueryEmailModel);
		getEventService().publishEvent(initializeEvent(new ApbContactUsEvent(), contactUsQueryEmailModel));
		return contactUsQueryEmailModel.getReferenceNumber();
	}

	/**
	 * Saves the customer translating model layer exceptions regarding duplicate identifiers
	 */
	private void saveContactUsQueryEmail(final ContactUsQueryEmailModel contactUsQueryEmailModel)
	{

		final String referencePrefixCode = asahiConfigurationService.getString(ApbCoreConstants.CONTACT_US_REFERENCE_PREFIX
				+ cmsSiteService.getCurrentSite().getUid(), "");
		try
		{
			contactUsQueryEmailModel.setReferenceNumber(apbNumberKeyGeneratorService.generateCode(referencePrefixCode));
			getModelService().save(contactUsQueryEmailModel);
			saveCsTicket(contactUsQueryEmailModel);
		}
		catch (final ModelSavingException e)
		{
			LOG.error("Model Saving Exception " + e.getMessage());
			//	throw new DuplicateUidException(contactUsQueryEmailModel.getCode(), e);
		}
		catch (final AmbiguousIdentifierException e)
		{
			LOG.error("Ambiguous Identifier Exception " + e.getMessage());
			//	throw new DuplicateUidException(contactUsQueryEmailModel.getCode(), e);
		}

	}

	private AbstractEvent initializeEvent(final ApbContactUsEvent event, final ContactUsQueryEmailModel contactUsQueryEmailModel)
	{
		event.setBaseStore(getBaseStoreService().getCurrentBaseStore());
		event.setSite(getBaseSiteService().getCurrentBaseSite());
		event.setContactUsQueryEmail(contactUsQueryEmailModel);
		event.setLanguage(getCommonI18NService().getCurrentLanguage());
		event.setCurrency(getCommonI18NService().getCurrentCurrency());
		event.setCustomer((CustomerModel) getUserService().getCurrentUser());
		return event;
	}

	/**
	 * @param ContactUsQueryEmailModel
	 */
	private void saveCsTicket(final ContactUsQueryEmailModel contactUsQueryEmailModel)
	{
		final CsTicketModel csTicketModel = getModelService().create(CsTicketModel.class);
		csTicketModel.setTicketID(contactUsQueryEmailModel.getReferenceNumber());
		csTicketModel.setHeadline(apbEmailConfigurationUtil.getSubject(contactUsQueryEmailModel));
		csTicketModel.setCategory(CsTicketCategory.ENQUIRY);
		csTicketModel.setPriority(CsTicketPriority.MEDIUM);
		csTicketModel.setBaseSite(cmsSiteService.getCurrentSite());

		// new attributes added here
		csTicketModel.setName(contactUsQueryEmailModel.getName());
		AsahiEnquiryType enquiryType=null;
		AsahiEnquirySubType enquirySubType=null;
		if(StringUtils.isNotEmpty(contactUsQueryEmailModel.getEnquiryType()))
		{
			enquiryType = enumerationService.getEnumerationValue(AsahiEnquiryType.class,
				StringUtils.upperCase(contactUsQueryEmailModel.getEnquiryType()));
		}

		if(StringUtils.isNotEmpty(contactUsQueryEmailModel.getEnquirySubType()))
		{
			enquirySubType = enumerationService.getEnumerationValue(AsahiEnquirySubType.class,
				StringUtils.upperCase(contactUsQueryEmailModel.getEnquirySubType()));
		}


		csTicketModel.setEnquiryType(enquiryType);
		csTicketModel.setEnquirySubType(enquirySubType);

		final AsahiB2BUnitModel b2bUnit = apbB2BUnitService.getCurrentB2BUnit();
		csTicketModel.setB2bunit(b2bUnit);

		getModelService().save(csTicketModel);
		csTicketModel.setDatePlaced(csTicketModel.getCreationtime());

		// contact to be mapped
		if(null != enquiryType) {
			final EnquiryTypeContactMappingModel contact = asahiCustomerAccountDao.getContactByEnquiryType(enquiryType, enquirySubType);
			csTicketModel.setContact(contact.getContact());
			contactUsQueryEmailModel.setContact(contact.getContact());
			getModelService().save(contactUsQueryEmailModel);
		}
		getModelService().save(csTicketModel);
		getModelService().refresh(csTicketModel);
	}

	/**
	 * @param ApbKegReturnEmailModel
	 */
	private void saveCsTicket(final ApbKegReturnEmailModel apbKegReturnEmailModel)
	{
		final CsTicketModel csTicketModel = getModelService().create(CsTicketModel.class);
		csTicketModel.setTicketID(apbKegReturnEmailModel.getReferenceNumber());
		csTicketModel.setHeadline(apbEmailConfigurationUtil.getSubject(apbKegReturnEmailModel));
		csTicketModel.setCustomer(apbKegReturnEmailModel.getCustomer());
		csTicketModel.setCategory(CsTicketCategory.ENQUIRY);
		csTicketModel.setPriority(CsTicketPriority.MEDIUM);
		csTicketModel.setBaseSite(cmsSiteService.getCurrentSite());
		getModelService().save(csTicketModel);
		getModelService().refresh(csTicketModel);
	}

	@Override
	public List<KegReturnSizeModel> getKegSizes(final CMSSiteModel currentSite)
	{
		return asahiCustomerAccountDao.getKegSizes(currentSite);
	}

	/**
	 * Sending Keg Return Email
	 *
	 * @param apbKegReturnEmailModel
	 *
	 */
	public void sendKegReturnEmail(final ApbKegReturnEmailModel apbKegReturnEmailModel)
	{
		saveKegReturnEmail(apbKegReturnEmailModel);
		getEventService().publishEvent(initializeEvent(new ApbKegReturnEvent(), apbKegReturnEmailModel));
	}

	private AbstractEvent initializeEvent(final ApbKegReturnEvent event, final ApbKegReturnEmailModel apbKegReturnEmailModel)
	{
		event.setBaseStore(getBaseStoreService().getCurrentBaseStore());
		event.setSite(getBaseSiteService().getCurrentBaseSite());
		event.setApbKegReturnEmail(apbKegReturnEmailModel);
		event.setLanguage(getCommonI18NService().getCurrentLanguage());
		event.setCurrency(getCommonI18NService().getCurrentCurrency());
		event.setCustomer(apbKegReturnEmailModel.getCustomer());
		return event;
	}

	private void saveKegReturnEmail(final ApbKegReturnEmailModel apbKegReturnEmailModel)
	{
		final String referencePrefixCode = asahiConfigurationService.getString(ApbCoreConstants.KEG_RETURN_REFERENCE_PREFIX
				+ cmsSiteService.getCurrentSite().getUid(), "");
		if (getUserService().getCurrentUser() instanceof B2BCustomerModel)
		{
			final B2BCustomerModel b2bCustomerModel = (B2BCustomerModel) getUserService().getCurrentUser();
			apbKegReturnEmailModel.setCustomer(b2bCustomerModel);
			apbKegReturnEmailModel.setContactNumber(b2bCustomerModel.getContactNumber());
		}
		apbKegReturnEmailModel.setReferenceNumber(apbNumberKeyGeneratorService.generateCode(referencePrefixCode).toString());
		getModelService().save(apbKegReturnEmailModel);
		saveCsTicket(apbKegReturnEmailModel);
	}

	@Override
	public List<AddressModel> getB2BUnitAddressesForUser(final UserModel currentUser, final boolean visibleAddressesOnly)
	{
		final List<AddressModel> addresses = new ArrayList<>();
		if (null != currentUser && currentUser instanceof B2BCustomerModel)
		{
			final B2BCustomerModel customer = (B2BCustomerModel) currentUser;
			final AsahiB2BUnitModel b2bUnit = (AsahiB2BUnitModel) customer.getDefaultB2BUnit();

			if (this.asahiSiteUtil.isApb() && null != b2bUnit && CollectionUtils.isNotEmpty(b2bUnit.getAddresses()))
			{
				addresses.addAll(b2bUnit.getAddresses().stream()
						.filter(address -> null != address.getAddressType() && !address.getAddressType().equals(AddressType.INVOICE))
						.collect(Collectors.toList()));
			}
			if (this.asahiSiteUtil.isSga() && null != b2bUnit && CollectionUtils.isNotEmpty(b2bUnit.getShipToAccounts()))
			{
				final AsahiB2BUnitModel shipTo = b2bUnit.getShipToAccounts().get(0);
				if (null != shipTo && CollectionUtils.isNotEmpty(shipTo.getAddresses()))
				{
					addresses.add((AddressModel) ((List) shipTo.getAddresses()).get(0));
				}
			}
		}
		return addresses.isEmpty() ? Collections.emptyList() : visibleAddressesOnly ? addresses.stream()
				.filter(address -> address.getVisibleInAddressBook()).collect(Collectors.toList()) : addresses;

	}

	@Override
	public CreditCardPaymentInfoModel createPaymentSubscription(final CustomerModel customerModel, final CardInfo cardInfo,
			final BillingInfo billingInfo, final String titleCode, final String paymentProvider, final boolean saveInAccount)
	{
		if(!asahiSiteUtil.isCub())
		{
		validateParameterNotNull(customerModel, "Customer cannot be null");
		validateParameterNotNull(cardInfo, "CardInfo cannot be null");
		validateParameterNotNull(billingInfo, "billingInfo cannot be null");
		validateParameterNotNull(paymentProvider, "PaymentProvider cannot be null");
		final CurrencyModel currencyModel = getCurrency(customerModel);
		validateParameterNotNull(currencyModel, "Customer session currency cannot be null");

		final Currency currency = getI18nService().getBestMatchingJavaCurrency(currencyModel.getIsocode());

		final AddressModel billingAddress = getModelService().create(AddressModel.class);
		if (StringUtils.isNotBlank(titleCode))
		{
			final TitleModel title = new TitleModel();
			title.setCode(titleCode);
			billingAddress.setTitle(getFlexibleSearchService().getModelByExample(title));
		}
		billingAddress.setFirstname(billingInfo.getFirstName());
		billingAddress.setLastname(billingInfo.getLastName());
		billingAddress.setLine1(billingInfo.getStreet1());
		billingAddress.setLine2(billingInfo.getStreet2());
		billingAddress.setTown(billingInfo.getCity());
		billingAddress.setPostalcode(billingInfo.getPostalCode());
		billingAddress.setCountry(getCommonI18NService().getCountry(billingInfo.getCountry()));
		billingAddress.setPhone1(billingInfo.getPhoneNumber());
		final String email = getCustomerEmailResolutionService().getEmailForCustomer(customerModel);
		billingAddress.setEmail(email);

		final String merchantTransactionCode = customerModel.getUid() + "-" + UUID.randomUUID();
		try
		{
			final NewSubscription subscription = getPaymentService().createSubscription(merchantTransactionCode, paymentProvider,
					currency, billingAddress, cardInfo);

			if (StringUtils.isNotBlank(subscription.getSubscriptionID()))
			{
				final CreditCardPaymentInfoModel cardPaymentInfoModel = getModelService().create(CreditCardPaymentInfoModel.class);
				cardPaymentInfoModel.setCode(customerModel.getUid() + "_" + UUID.randomUUID());
				cardPaymentInfoModel.setUser(customerModel);
				cardPaymentInfoModel.setSubscriptionId(subscription.getSubscriptionID());
				cardPaymentInfoModel.setNumber(getMaskedCardNumber(cardInfo.getCardNumber()));
				cardPaymentInfoModel.setType(cardInfo.getCardType());
				cardPaymentInfoModel.setCcOwner(cardInfo.getCardHolderFullName());
				cardPaymentInfoModel.setValidToMonth(String.format("%02d", cardInfo.getExpirationMonth()));
				cardPaymentInfoModel.setValidToYear(String.valueOf(cardInfo.getExpirationYear()));
				cardPaymentInfoModel.setToken(cardInfo.getToken());
				if (cardInfo.getIssueMonth() != null)
				{
					cardPaymentInfoModel.setValidFromMonth(String.valueOf(cardInfo.getIssueMonth()));
				}
				if (cardInfo.getIssueYear() != null)
				{
					cardPaymentInfoModel.setValidFromYear(String.valueOf(cardInfo.getIssueYear()));
				}

				cardPaymentInfoModel.setSubscriptionId(subscription.getSubscriptionID());
				cardPaymentInfoModel.setSaved(saveInAccount);
				if (!StringUtils.isEmpty(cardInfo.getIssueNumber()))
				{
					cardPaymentInfoModel.setIssueNumber(Integer.valueOf(cardInfo.getIssueNumber()));
				}

				billingAddress.setOwner(cardPaymentInfoModel);
				cardPaymentInfoModel.setBillingAddress(billingAddress);

				getModelService().saveAll(billingAddress, cardPaymentInfoModel);
				getModelService().refresh(customerModel);
				addPaymentInfo(customerModel, cardPaymentInfoModel);
				return cardPaymentInfoModel;
			}
		}
		catch (final AdapterException ae) //NOSONAR
		{
			LOG.error("Failed to create subscription for customer. Customer PK: " + String.valueOf(customerModel.getPk())
					+ " Exception: " + ae.getClass().getName());

			return null;
		}

		return null;
		}
		return super.createPaymentSubscription(customerModel, cardInfo, billingInfo, titleCode, paymentProvider, saveInAccount);

	}

	@Override
	public Collection<TitleModel> getTitles()
	{
		if(!asahiSiteUtil.isCub())
		{
		return asahiCustomerAccountDao.getAllTitles();
		}
		return super.getTitles();
	}

	@Override
	public boolean checkTokenValid(final String token)
	{
		Assert.hasText(token, "The field [token] cannot be empty");

		final SecureToken data = getSecureTokenService().decryptData(token);
		if (getTokenValiditySeconds() > 0L)
		{
			final long delta = new Date().getTime() - data.getTimeStamp();
			if (delta / 1000 > getTokenValiditySeconds())
			{
				return false;
			}
		}
		final CustomerModel customer = getUserService().getUserForUID(data.getData(), CustomerModel.class);
		return null != customer && null == customer.getToken() ? false : true;
	}

	/**
	 * This method is used to send order confirmation email to customer.
	 *
	 * @param orderModel
	 */
	@Override
	public void sendOrderConfirmationEmail(final OrderModel orderModel)
	{
		getEventService().publishEvent(initializeEvent(new AsahiOrderPlacedEvent(), orderModel));
	}


	/**
	 * The method will initialize the event paramters
	 *
	 * @param event
	 * @param orderModel
	 * @return This method sets the required values in order place event.
	 */
	private AbstractEvent initializeEvent(final AsahiOrderPlacedEvent event, final OrderModel orderModel)
	{
		event.setBaseStore(getBaseStoreService().getCurrentBaseStore());
		event.setSite(getBaseSiteService().getCurrentBaseSite());
		event.setOrderModel(orderModel);
		event.setLanguage(getCommonI18NService().getCurrentLanguage());
		event.setCurrency(getCommonI18NService().getCurrentCurrency());
		return event;
	}

	/**
	 * The method is used to sent the reset password link to the portal user, if created through the back office.
	 *
	 * @param customerModel
	 */
	@Override
	public void assistedForgotPassword(final CustomerModel customerModel)
	{

		validateParameterNotNullStandardMessage("customerModel", customerModel);
		final long timeStamp = getTokenValiditySeconds() > 0L ? new Date().getTime() : 0L;
		final SecureToken data = new SecureToken(customerModel.getUid(), timeStamp);
		final String token = getSecureTokenService().encryptData(data);
		customerModel.setToken(token);
		getModelService().save(customerModel);
		getEventService().publishEvent(initializeEvent(new AsahiAssistedForgottenPwdEvent(token), customerModel));

	}

	/*
	 * fetching catalog list from customer account
	 */
	@Override
	public Set<String> getCustomerCatalogProductIds(final String userId)
	{
		final UserModel user = getUserService().getUserForUID(userId);
		final Set<String> productIds = new HashSet<>();
		if (user instanceof B2BCustomerModel)
		{
			final B2BCustomerModel b2bCustomerModel = (B2BCustomerModel) getUserService().getCurrentUser();
			final Collection<String> catalogs = b2bCustomerModel.getDefaultB2BUnit() != null ? ((AsahiB2BUnitModel) b2bCustomerModel
					.getDefaultB2BUnit()).getCatalogHierarchy() : new HashSet<>();

			catalogs.stream().forEach(
					catalog -> {

						final FlexibleSearchQuery query = new FlexibleSearchQuery(
								"Select {pk} from {AsahiCatalogProductMapping} where {catalogId}=?catalogId ");
						final Map<String, String> param = new HashMap<>();
						param.put("catalogId", catalog);
						query.addQueryParameters(param);
						final SearchResult<AsahiCatalogProductMappingModel> result = getFlexibleSearchService().search(query);
						if (null != result && CollectionUtils.isNotEmpty(result.getResult()))
						{
							final AsahiCatalogProductMappingModel mapping = result.getResult().get(0);
							if (CollectionUtils.isNotEmpty(mapping.getProducts()))
							{
								mapping.getProducts().stream()
										.filter(product -> ArticleApprovalStatus.APPROVED.equals(product.getApprovalStatus()) && product.isActive())
										.forEach(product -> {
									productIds.add(product.getCode());
								});
							}
						}
					});
		}
		return productIds;
	}

	/**
	 * The Method will fetch the pricing tier model.
	 *
	 * @param tierCode
	 * @return ProdPricingTierModel
	 */
	@Override
	public ProdPricingTierModel getPricingTierProductIds(final String tierCode)
	{
		return asahiCustomerAccountDao.findProdPricingTierByCode(tierCode);
	}

	/**
	 * The Method will update the payer access and trigger the email notification
	 *
	 * @param customer
	 * @param accessModel
	 * @param accessType
	 * @return String
	 */
	@Override
	public String updateAndNotifyPayAccess(final B2BCustomerModel customer, final AsahiSAMAccessModel accessModel,
			final String accessType)
	{
		final AsahiPayerAccessEvent event = initializeEvent(new AsahiPayerAccessEvent(), customer);
		boolean checkIfUserDisabled = false;
		if (accessType.equalsIgnoreCase(PAYER_ACCESS_REQUEST))
		{
			event.setEmailType(PAYER_ACCESS_REQUEST);
			accessModel.setRequestDate(accessModel != null ? new Date() : null);

		}
		else if (accessType.equalsIgnoreCase(PAYER_ACCESS_SUPERUSER_REQUEST))
		{
			event.setEmailType(PAYER_ACCESS_SUPERUSER_REQUEST);
			accessModel.setRequestDate(accessModel != null ? new Date() : null);
		}
		else if (accessType.equalsIgnoreCase(PAYER_ACCESS_EXPIRED))
		{
			event.setEmailType(PAYER_ACCESS_EXPIRED);
			accessModel.setApprovalDenied(Boolean.TRUE);
			checkIfUserDisabled = true;
		}
		else if (accessType.equalsIgnoreCase(PAYER_ACCESS_APPROVE))
		{
			event.setEmailType(PAYER_ACCESS_APPROVE);
			accessModel.setPendingApproval(Boolean.FALSE);
			checkIfUserDisabled = true;
		}
		else
		{
			event.setEmailType(PAYER_ACCESS_REJECT);
			accessModel.setApprovalDenied(Boolean.TRUE);
			checkIfUserDisabled = true;
		}

		if (null != accessModel && getModelService().isModified(accessModel))
		{
			getModelService().save(accessModel);
		}

		event.setAccess(accessModel);
		if (BooleanUtils.isTrue(customer.getActive()) && (BooleanUtils.isFalse(checkIfUserDisabled)
				|| (checkIfUserDisabled && !(null != accessModel.getParentAccount()
						&& CollectionUtils.isNotEmpty(accessModel.getParentAccount().getDisabledUser())
						&& accessModel.getParentAccount().getDisabledUser().contains(customer.getUid())))))
		{
			getEventService().publishEvent(event);
		}
		return accessType;
	}

	private AsahiPayerAccessEvent initializeEvent(final AsahiPayerAccessEvent event, final B2BCustomerModel customer)
	{
		event.setBaseStore(getBaseStoreService().getCurrentBaseStore());
		event.setSite(getBaseSiteService().getCurrentBaseSite());
		event.setLanguage(getCommonI18NService().getCurrentLanguage());
		event.setCurrency(getCommonI18NService().getCurrentCurrency());
		event.setCustomer(customer);
		return event;
	}

	@Override
	public AsahiSAMAccessModel getAccessModel(final B2BCustomerModel customer, final AsahiB2BUnitModel b2bUnit)
	{
		return apbB2BUnitDao.getAccessModel(customer, b2bUnit);
	}


	@Override
	public SearchPageData<CsTicketModel> getAllEnquiries(final AsahiB2BUnitModel b2bunit, final PageableData pageableData, final String cofoDate) throws ParseException
	{
		return this.asahiCustomerAccountDao.getAllEnquiries(b2bunit, pageableData, cofoDate);
	}

	@Override
	public void forgottenPassword(final CustomerModel customerModel)
	{

		if(customerModel instanceof B2BCustomerModel)
		{
   		validateParameterNotNullStandardMessage("customerModel", customerModel);
   		final long timeStamp = getTokenValiditySeconds() > 0L ? new Date().getTime() : 0L;
   		final SecureToken data = new SecureToken(customerModel.getUid(), timeStamp);
   		final String token = getSecureTokenService().encryptData(data);
   		customerModel.setToken(token);
   		getModelService().save(customerModel);
   		final AbstractCommerceUserEvent event = initializeEvent(new ForgottenPwdEvent(token), customerModel);
   		event.setCurrency(getBaseStoreService().getCurrentBaseStore().getDefaultCurrency());
   		getEventService().publishEvent(event);
		}

		else
		{
			super.forgottenPassword(customerModel);
		}
	}

	/**
	 *
	 */
	public void setRestrictedCategoriesInSession()
	{
		final UserModel user = getUserService().getCurrentUser();
		if (null != user && user instanceof B2BCustomerModel && !getUserService().isAnonymousUser(user))
		{
			final Boolean b2bUnitUpdated = sessionService.getAttribute(ApbCoreConstants.EXCLUDED_CATEGORY_RECALCULATION);
			final Set<String> categories = sessionService.getAttribute(ApbCoreConstants.ALB_CUSTOMER_SESSION_EXCLUDED_CATEGORIES);

   		if (CollectionUtils.isEmpty(categories) || (null != b2bUnitUpdated && b2bUnitUpdated) ) {
   			final Set<String> excludedCategories = getCustomerCatalogRestrictedCategories();
   			sessionService.setAttribute(ApbCoreConstants.ALB_CUSTOMER_SESSION_EXCLUDED_CATEGORIES, excludedCategories);

   		}
		}
	}

	@Override
	public Set<String> getCustomerCatalogRestrictedCategories()
	{
		final Set<String> excludedCategories = new HashSet<>();
		final UserModel user = getUserService().getCurrentUser();
		if (user instanceof B2BCustomerModel)
		{
			final B2BCustomerModel b2bCustomerModel = (B2BCustomerModel) user;
			final Collection<String> catalogs = b2bCustomerModel.getDefaultB2BUnit() != null
					? ((AsahiB2BUnitModel) b2bCustomerModel.getDefaultB2BUnit()).getCatalogHierarchy()
					: new HashSet<>();

			catalogs.stream().forEach(catalog -> {
				final FlexibleSearchQuery query = new FlexibleSearchQuery(
						"Select {pk} from {AsahiCatalogProductMapping} where {catalogId}=?catalogId ");
				final Map<String, String> param = new HashMap<>();
				param.put("catalogId", catalog);
				query.addQueryParameters(param);
				final SearchResult<AsahiCatalogProductMappingModel> result = getFlexibleSearchService().search(query);
				if (null != result && CollectionUtils.isNotEmpty(result.getResult()))
				{
					final AsahiCatalogProductMappingModel mapping = result.getResult().get(0);
					if (CollectionUtils.isNotEmpty(mapping.getExcludedCategories()))
					{
						mapping.getExcludedCategories().stream().forEach(category -> {
							excludedCategories.add(category.getCode());
						});
					}
				}
			});
		}
		return excludedCategories;
	}

	@Override
	public void sendPasswordResetEmail(final B2BCustomerModel customerModel)
	{
		getEventService().publishEvent(initializeEvent(new AsahiPasswordResetEmailEvent(), customerModel));

	}


	@Override
	public boolean sendWelcomeEmail(final UserModel user)
	{
		getEventService().publishEvent(initializeEvent(new AsahiCustomerWelcomeEmailEvent(), (B2BCustomerModel)user));
		return true;
	}

	/**
	 *
	 */
	private AbstractEvent initializeEvent(final AsahiPasswordResetEmailEvent event, final B2BCustomerModel user )
	{
		populateEventDetails(event, user);

		return event;
	}

	/**
	 *
	 */
	private AbstractEvent initializeEvent(final AsahiCustomerWelcomeEmailEvent event, final B2BCustomerModel user )
	{
		populateEventDetails(event, user);

		return event;
	}

	/**
	 * @param event
	 * @param user
	 */
	private void populateEventDetails(final AbstractCommerceUserEvent event, final B2BCustomerModel user)
	{
		event.setBaseStore(getBaseStoreService().getCurrentBaseStore());
		event.setSite(getBaseSiteService().getCurrentBaseSite());
		event.setLanguage(getCommonI18NService().getCurrentLanguage());
		event.setCurrency(getCommonI18NService().getCurrentCurrency());
		event.setCustomer(user);
		boolean orderAccess = Boolean.TRUE;
		boolean payAccess = Boolean.FALSE;
		String payerEmail = StringUtils.EMPTY;
		String customerAccountTradingName = StringUtils.EMPTY;
		if (user instanceof B2BCustomerModel)
		{
			final B2BCustomerModel customer = user;
			final Collection<AsahiSAMAccessModel> samAccessList = customer.getSamAccess();

			if (CollectionUtils.isNotEmpty(samAccessList))
			{
				String payer = null;
				if (null != customer.getDefaultB2BUnit() && null != ((AsahiB2BUnitModel) customer.getDefaultB2BUnit()).getPayerAccount())
				{
					payer = ((AsahiB2BUnitModel) customer.getDefaultB2BUnit()).getPayerAccount().getPk().toString();
				}

				for (final AsahiSAMAccessModel access : samAccessList)
				{
					if (null != access.getPayer() && null != payer && payer.equalsIgnoreCase(access.getPayer().getPk().toString()))
					{
						orderAccess = access.isOrderAccess();
						payAccess = access.isPayAccess();
						if (payAccess) {
							payerEmail = access.getPayer().getEmailAddress();
							customerAccountTradingName = customer.getDefaultB2BUnit().getLocName();
						}
						break;
					}
				}

			}
		}
		if (event instanceof AsahiPasswordResetEmailEvent) {
			final AsahiPasswordResetEmailEvent emailEvent = (AsahiPasswordResetEmailEvent)event;
			emailEvent.setOrderAccess(orderAccess);
			emailEvent.setPayAccess(payAccess);
			emailEvent.setPayerEmail(payerEmail);
			emailEvent.setCustomerAccountName(customerAccountTradingName);
			emailEvent.setToken(user.getToken());
		}
		if (event instanceof AsahiCustomerWelcomeEmailEvent) {
			final AsahiCustomerWelcomeEmailEvent emailEvent = (AsahiCustomerWelcomeEmailEvent)event;
			emailEvent.setOrderAccess(orderAccess);
			emailEvent.setPayAccess(payAccess);
			emailEvent.setPayerEmail(payerEmail);
			emailEvent.setCustomerAccountName(customerAccountTradingName);
		}

	}

	@Override
	public void setCustomerToken(final B2BCustomerModel customerModel)
	{
		final long timeStamp = Config.getLong("asahi.welcomeEmail.password.tokenValiditySecond", 9153600000000L);
		final SecureToken data = new SecureToken(customerModel.getUid(), System.currentTimeMillis() + timeStamp);
		final String token = getSecureTokenService().encryptData(data);
		customerModel.setToken(token);

	}


	@Override
	public boolean removeCustomerFromUnit(final B2BCustomerModel customer, final AsahiB2BUnitModel currentUnit)
	{
		try
		{
			if (currentUnit != null && currentUnit.equals(customer.getDefaultB2BUnit()))
			{
				customer.setDefaultB2BUnit(null);
				getModelService().save(customer);
			}
			final Set<PrincipalModel> members = new HashSet<PrincipalModel>(currentUnit.getMembers());
			members.remove(customer);
			currentUnit.setMembers(members);
			getModelService().save(currentUnit);
			getModelService().refresh(customer);
			return true;
		}
		catch (final Exception e)
		{
			throw e;
		}
	}

	@Override
	public void sendCustomerProfileUpdatedNoticeEmail(final B2BCustomerModel customerModel, final AsahiB2BUnitModel currentUnit)
	{
		getEventService()
				.publishEvent(initializeProfileUpdatedNoticeEvent(new ProfileUpdatedNoticeEvent(currentUnit), customerModel));
	}

	/**
	 * To set the attributes to the event.
	 *
	 * @param event
	 *           the event
	 * @param customer
	 *           the customer
	 * @return ConfirmEnableDealEmailEvent
	 */
	protected ProfileUpdatedNoticeEvent initializeProfileUpdatedNoticeEvent(final ProfileUpdatedNoticeEvent event,
			final CustomerModel customer)
	{
		event.setBaseStore(getBaseStoreService().getCurrentBaseStore());
		event.setSite(getBaseSiteService().getCurrentBaseSite());
		event.setLanguage(getCommonI18NService().getCurrentLanguage());
		event.setCurrency(getCommonI18NService().getCurrentCurrency());
		event.setCustomer(customer);
		return event;
	}
	/**
	 *
	 * @param userId
	 * @return
	 */
	public UserModel getUserByUid(final String userId) {
		return asahiCustomerAccountDao.getUserByUid(userId);
	}


	@Override
	public List<PlanogramModel> getDefaultPlanograms(final List<String> catalogHierarchy)
	{
		final List<AsahiCatalogProductMappingModel> catalogHierarchyMapping = asahiCustomerAccountDao
				.findCatalogHierarchyData(catalogHierarchy);
		final List<PlanogramModel> planograms = new ArrayList<PlanogramModel>();

		for (final AsahiCatalogProductMappingModel mapping : catalogHierarchyMapping)
		{
			if (null != mapping.getDefaultPlanogram())
			{
				planograms.add(mapping.getDefaultPlanogram());
			}
		}
		return planograms;
	}

	@Override
	public boolean savePlanogram(final PlanogramData data)
	{
		final PlanogramModel model = getModelService().create(PlanogramModel.class);
		final AsahiB2BUnitModel currentUnit = apbB2BUnitService.getCurrentB2BUnit();
		model.setCode(currentUnit.getUid() + "_" + ThreadLocalRandom.current().nextDouble());
		model.setAssociationtype(PlanogramAssociationType.CUSTOMER_ACCOUNT);
		final UserModel user = getUserService().getCurrentUser();
		if (user instanceof BDECustomerModel)
		{
			if (StringUtils.isNotBlank(((BDECustomerModel) user).getName()))
			{
				model.setUploadedBy(((BDECustomerModel) user).getName());
			}
			else
			{
				model.setUploadedBy(STAFF_PERSON);
			}
		}

		model.setDocumentName(data.getDocumentName());
		try
		{
			model.setMedia(getMediasFromFiles(data.getFile()));
		}
		catch (MediaIOException | IllegalArgumentException | IOException e)
		{
			LOG.error("Error saving Planogram");
		}
		getModelService().save(model);

		final List<PlanogramModel> allPlanograms = new ArrayList<PlanogramModel>();
		allPlanograms.addAll(currentUnit.getPlanograms());
		allPlanograms.add(model);
		currentUnit.setPlanograms(allPlanograms);
		getModelService().save(currentUnit);
		return true;
	}

	@Override
	public void removePlanogram(final String code)
	{
		final PlanogramModel planogram = asahiCustomerAccountDao.fetchPlanogramByCode(code);
		getModelService().remove(planogram);
	}

	/**
	 * @param file
	 * @return media
	 * @throws MediaIOException
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public CatalogUnawareMediaModel getMediasFromFiles(final MultipartFile file)
			throws MediaIOException, IllegalArgumentException, IOException
	{
		if (null != file && file.getSize() > 0)
		{
			final String fileName = file.getOriginalFilename();
			final CatalogUnawareMediaModel mediaModel = getModelService().create(CatalogUnawareMediaModel.class);
			if (null != fileName && !"".equals(fileName))
			{
				mediaModel.setCode(UUID.randomUUID().toString());
				getModelService().save(mediaModel);
				final InputStream inputStream = file.getInputStream();
				try
				{
					mediaService.setStreamForMedia(mediaModel, inputStream, fileName, file.getContentType());
					if (null != inputStream)
					{
						inputStream.close();
					}
				}
				catch (final FileNotFoundException fne)
				{
					if (null != inputStream)
					{
						inputStream.close();
					}
					LOG.error("Error in uploaded file: " + fne.getMessage(), fne);
				}
				getModelService().refresh(mediaModel);
			}
			return mediaModel;
		}
		return null;
	}

	@Override
	public void removeAllPlanogramsForCurrentB2BUnit()
	{
		final AsahiB2BUnitModel currentUnit = apbB2BUnitService.getCurrentB2BUnit();
		if (CollectionUtils.isNotEmpty(currentUnit.getPlanograms()))
		{
			getModelService().removeAll(currentUnit.getPlanograms());
		}

	}

	@Override
	public PlanogramModel fetchPlanogramByCode(final String code)
	{
		final PlanogramModel planogram = asahiCustomerAccountDao.fetchPlanogramByCode(code);
		return planogram;
	}



	/**
	 * @return the sessionService
	 */
	@Override
	public SessionService getSessionService()
	{
		return sessionService;
	}



	/**
	 * @param sessionService
	 *           the sessionService to set
	 */
	@Override
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}
}