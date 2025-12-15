/**
 *
 */
package com.sabmiller.facades.customer.impl;

import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorservices.email.EmailService;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2bacceleratorfacades.customer.exception.InvalidPasswordException;
import de.hybris.platform.b2bacceleratorfacades.customer.impl.DefaultB2BCustomerFacade;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.catalog.model.CatalogUnawareMediaModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.customergroups.CustomerGroupFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.customer.TokenInvalidatedException;
import de.hybris.platform.commerceservices.delivery.DeliveryService;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.security.SecureToken;
import de.hybris.platform.commerceservices.security.SecureTokenService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commons.renderer.exceptions.RendererException;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelLoadingException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.impl.UniqueAttributesInterceptor;
import de.hybris.platform.servicelayer.media.MediaIOException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.PasswordEncoderService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.tx.Transaction;
import de.hybris.platform.util.Config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.exception.AsahiBusinessException;
import com.apb.core.integration.AsahiIntegrationPointsServiceImpl;
import com.apb.core.model.ApbCompanyDetailsEmailModel;
import com.apb.core.model.ApbDeliveryAddressModel;
import com.apb.core.model.ApbKegReturnEmailModel;
import com.apb.core.model.ApbRequestRegisterEmailModel;
import com.apb.core.model.KegReturnSizeModel;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.services.ApbCustomerAccountService;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiDateUtil;
import com.apb.core.util.AsahiPaginationUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.checkout.data.B2BUnitDeliveryAddressData;
import com.apb.facades.contactust.data.ApbContactUsData;
import com.apb.facades.kegreturn.data.ApbKegReturnData;
import com.apb.facades.kegreturn.data.KegSizeData;
import com.apb.facades.register.data.ApbRequestRegisterData;
import com.apb.facades.user.data.ApbCompanyData;
import com.apb.integration.data.AsahiCustomerAccountCheckResponseData;
import com.apb.integration.data.AsahiLoginInclusionResponseDTO;
import com.apb.integration.data.AsahiProductInfo;
import com.apb.integration.data.Error;
import com.apb.integration.user.service.AsahiUserIntegrationService;
import com.apb.service.b2bunit.ApbB2BUnitService;
import com.apb.storefront.data.AlbCompanyInfoData;
import com.apb.storefront.data.ApbRegisterData;
import com.apb.storefront.data.ErrorDTO;
import com.apb.storefront.data.LoginValidateInclusionData;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sabm.core.config.SabmConfigurationService;
import com.sabmiller.commons.enumerations.LoginStatus;
import com.sabmiller.core.b2b.services.SABMDeliveryDateCutOffService;
import com.sabmiller.core.b2b.services.SABMProductExclusionService;
import com.sabmiller.core.b2b.services.SabmB2BCustomerService;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.comparators.ShippingCarrierComparator;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.core.enums.AsahiOnAccountRestrictedCode;
import com.sabmiller.core.enums.AsahiRole;
import com.sabmiller.core.enums.BackendCustomerType;
import com.sabmiller.core.enums.BlockTypeEnum;
import com.sabmiller.core.enums.DeliveryModeType;
import com.sabmiller.core.enums.OrderSimulationStatus;
import com.sabmiller.core.enums.PackType;
import com.sabmiller.core.enums.SapServiceCallStatus;
import com.sabmiller.core.event.ProfileUpdatedNoticeEvent;
import com.sabmiller.core.mail.EmailConfig;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.AsahiSAMAccessModel;
import com.sabmiller.core.model.BDECustomerModel;
import com.sabmiller.core.model.ProductExclusionModel;
import com.sabmiller.core.model.PublicHolidayModel;
import com.sabmiller.core.model.RegistrationRequestModel;
import com.sabmiller.core.model.SABMUserAccessHistoryModel;
import com.sabmiller.core.model.ShippingCarrierModel;
import com.sabmiller.core.search.restriction.SabmSearchRestrictionService;
import com.sabmiller.core.util.SABMFormatterUtils;
import com.sabmiller.core.util.SabmDateUtils;
import com.sabmiller.core.util.SabmUtils;
import com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade;
import com.sabmiller.facades.b2bunit.data.ShippingCarrier;
import com.sabmiller.facades.cart.SABMCartFacade;
import com.sabmiller.facades.constants.SabmFacadesConstants;
import com.sabmiller.facades.customer.B2BUnitJson;
import com.sabmiller.facades.customer.CustomerJson;
import com.sabmiller.facades.customer.PermissionsJson;
import com.sabmiller.facades.customer.ProductExclusionData;
import com.sabmiller.facades.customer.RegionJson;
import com.sabmiller.facades.customer.SABMCustomerFacade;
import com.sabmiller.facades.customer.SABMUserAccessHistoryData;
import com.sabmiller.facades.delivery.data.B2bDeliveryDatesConfig;
import com.sabmiller.facades.delivery.data.DeliveryModePackTypeDeliveryDatesData;
import com.sabmiller.facades.invoice.SABMInvoiceData;
import com.sabmiller.facades.invoice.SABMInvoicePDFData;
import com.sabmiller.facades.invoice.SABMInvoicePageData;
import com.sabmiller.facades.registrationrequest.data.RegistrationRequestForm;
import com.sabmiller.facades.user.NotificationData;
import com.sabmiller.integration.restclient.commons.SABMIntegrationException;
import com.sabmiller.integration.restclient.commons.SABMPostRestRequestHandler;
import com.sabmiller.integration.sap.invoices.customer.request.CustomerBillingDataRequest;
import com.sabmiller.integration.sap.invoices.customer.response.CustomerBillingDataResponse;
import com.sabmiller.integration.sap.invoices.customer.response.CustomerBillingDataResponse.Invoice;
import com.sabmiller.integration.sap.invoices.pdf.request.InvoiceDataRequest;
import com.sabmiller.integration.sap.invoices.pdf.request.InvoiceDataRequest.InvoiceDetails;
import com.sabmiller.integration.sap.invoices.pdf.response.InvoiceDataResponse;
import com.sabmiller.integration.sap.invoices.pdf.response.InvoiceDataResponse.InvoiceData;


/**
 * The Class DefaultSABMCustomerFacade.
 */
public class DefaultSABMCustomerFacade extends DefaultB2BCustomerFacade implements SABMCustomerFacade
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSABMCustomerFacade.class);

	private static final String EMAIL_DISPLAY = "Update Password";
	private static final String EMAIL_SUBJECT = "Update Password Successfully";
	private static final String EMAIL_BODY = "Your Password has been Updated Successfully";
	private static final String DELETE_UID_PRE_FIX = "(";
	private static final String DELETE_UID_END_FIX = ")";
	private static final String DEFAULT_BDEVIEWONLY_USERGROUP = "bdeviewonlygroup";
	private static final String DEFAULT_B2BCUSTOMER_USERGROUP = "b2bcustomergroup";
	private static final String DEFAULT_BDECUSTOMER_USERGROUP = "bdecustomergroup";

	private static final String EMAIL_PREFIX = "bde-";
	private static final String EMAIL_SUFFIX = "@cub.com.au";
	private static final String ASAHI_EMAIL_SUFFIX = "@asahi.com.au";
	private static final String REGEX_WHITE_SPACE = "\\s";
	private static final String DEFAULT_PASSWORD_ENCODING = "argon2";
	private static final String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
	private static final String NUMBERS = "0123456789";
	private static final String SYMBOLS = "!@#$%^&*()+";
	private static final int PASSWORD_LENGTH = 16;
	private static final String EMAIL_REGISTRATION_REQUEST_DISPLAY = "Registration Request";
	private static final String SYNC_DEALS_FROM_SAP_PROPERTY = "sync.deals.from.sap";
	private static final String CHECKED = "checked";
	private static final String UNCHECKED = "unChecked";
	private static final String ADJUSTMENT = "Adjustment";
	private static final String DEDUCTION_CREDIT = "Deduction Credit";
	private static final String EFT_PAYMENT = "EFT payment";
	private long tokenValiditySeconds;

	/* Stubs related constants */
	private static final String BILLINGDATA_STUB_AVAILABLE_CHECK = "cub.billing.stub.available.check";
	public static final String BILLINGDATA_STUB_MEDIA_RESPONSE = "billing.data.stub.response";
	private static final String INVOICEPDF_STUB_AVAILABLE_CHECK = "cub.invoicepdf.stub.available.check";
	public static final String INVOICEPDF_STUB_MEDIA_RESPONSE = "invoice.pdf.stub.response";
	private static final String SEND_CUSTOMER_TO_SF = "integration.salesforce.users.enable";
	private static final String CUB_STORE = "sabmStore";

	@Resource(name = "sabmConfigurationService")
	private SabmConfigurationService sabmConfigurationService;

	@Resource(name = "passwordEncoderService")
	private PasswordEncoderService passwordEncoderService;

	@Resource(name = "sabmProductExclusionService")
	private SABMProductExclusionService productExclusionService;


	@Resource(name = "sabmInvoiceRequestHandler")
	private SABMPostRestRequestHandler<CustomerBillingDataResponse, CustomerBillingDataRequest> invoiceRequestHandler;

	@Resource(name = "sabmInvoicePDFRequestHandler")
	private SABMPostRestRequestHandler<InvoiceDataResponse, InvoiceDataRequest> invoicePDFRequestHandler;


	/** The user access history populator. */
	@Resource(name = "userAccessHistoryPopulator")
	private Populator<SABMUserAccessHistoryData, SABMUserAccessHistoryModel> userAccessHistoryPopulator;

	@Resource(name = "invoiceRestConverter")
	private Converter<Invoice, SABMInvoiceData> invoiceConverter;

	@Resource(name = "invoicePDFRestConverter")
	private Converter<InvoiceData, SABMInvoicePDFData> invoicePdfConverter;

	@Resource(name = "b2bCommerceUnitService")
	private B2BCommerceUnitService b2bCommerceUnitService;

	private UserService userService;

	@Resource(name = "emailService")
	private EmailService emailService;

	@Resource(name = "emailConfig")
	private EmailConfig emailConfig;

	@Resource(name = "customerGroupFacade")
	private CustomerGroupFacade customerGroupFacade;

	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;

	private SABMDeliveryDateCutOffService sabmDeliveryDateCutOffService;

	@Resource(name = "sabFormatterUtil")
	private SABMFormatterUtils sabFormatterUtil;

	@Resource(name = "acceleratorCheckoutFacade")
	private AcceleratorCheckoutFacade checkoutFacade;

	@Resource(name = "cartFacade")
	private SABMCartFacade cartFacade;

	@Resource(name = "secureTokenService")
	private SecureTokenService secureTokenService;

	@Resource(name = "sabmB2BCustomerService")
	private SabmB2BCustomerService sabmB2BCustomerService;

	private DealsService dealsService;

	private Converter<UserModel, CustomerData> customerConverter;

	@Resource(name = "sabmCustomerStatesPopulator")
	private Populator<CustomerModel, CustomerJson> sabmCustomerStatesPopulator;

	@Resource(name = "customerJsonConverter")
	private Converter<CustomerModel, CustomerJson> customerJsonConverter;

	@Resource(name = "b2bCommerceUnitFacade")
	private SabmB2BCommerceUnitFacade b2bCommerceUnitFacade;
	/** The b2 b unit converter. */
	@Resource(name = "b2bUnitConverter")
	private Converter<B2BUnitModel, B2BUnitData> b2BUnitConverter;

	/** The event service. */
	@Resource(name = "eventService")
	private EventService eventService;

	/** The base site service. */
	@Resource(name = "baseSiteService")
	private BaseSiteService baseSiteService;

	/** The common i18 n service. */
	@Resource(name = "commonI18NService")
	private CommonI18NService commonI18NService;

	/** The base store service. */
	@Resource(name = "baseStoreService")
	private BaseStoreService baseStoreService;

	@Resource(name = "deliveryService")
	private DeliveryService deliveryService;

	@Resource(name = "shippingCarrierComparator")
	private ShippingCarrierComparator shippingCarrierComparator;

	@Resource(name = "siteConfigService")
	private SiteConfigService siteConfigService;


	@Value(value = "${sap.invoice.default.lineitem:A}")
	private String defaultLineItem;

	@Value(value = "${frontend.date.selector.pattern:yyyyMMdd}")
	private String dateSelectorPattern;

	@Value(value = "${sap.invoice.date.pattern:yyyyMMdd}")
	private String dateSapPattern;

	@Value(value = "${sap.invoice.date.separator:-}")
	private String dateSapSeparator;

	@Value(value = "${sap.customer.suspended.flag:SUSPE}")
	private String customerSuspended;

	@Resource
	private B2BCustomerService b2bCustomerService;


	@Resource(name = "asahiEnquiryConverter")
	private Converter<CsTicketModel, ApbContactUsData> asahiEnquiryConverter;

	private static final String STOREFRONT_PASSWORDPATTERN_APB = "storefront.passwordPattern.";

	private static final String FIELD_SEPARATOR = ", ";

	private static final String PAY_ACCESS_REQUEST_EMAIL_CONSTANT = "request";

	private String passwordValidationPattern;

	@Autowired
	private ApbCustomerAccountService apbCustomerAccountService;

	@Autowired
	private EnumerationService enumerationService;

	@Autowired
	private ApbB2BUnitService apbB2BUnitService;

	@Autowired
	private MediaService mediaService;

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Autowired
	private FlexibleSearchService flexibleSearchService;

	@Autowired
	private CMSSiteService cmsSiteService;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	@Resource
	private AsahiPaginationUtil asahiPaginationUtil;

	@Resource
	private AsahiIntegrationPointsServiceImpl asahiIntegrationPointsService;

	/** The apb B2B unit converter. */
	private Converter<B2BUnitModel, B2BUnitData> asahiB2BUnitConverter;

	@Resource
	private TypeService typeService;

	@Resource(name = "asahiDateUtil")
	private AsahiDateUtil asahiDateUtil;

	@Resource(name = "sabmSearchRestrictionService")
	private SabmSearchRestrictionService sabmSearchRestrictionService;

	@Resource(name = "asahiUserIntegrationService")
	private AsahiUserIntegrationService asahiUserIntegrationService;

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.customer.impl.DefaultCustomerFacade#getCurrentCustomer()
	 */
	@Override
	public CustomerData getCurrentCustomer()
	{
		final UserModel userModel = getUserService().getCurrentUser();
		if (asahiSiteUtil.isCub())
		{
			if (userModel instanceof B2BCustomerModel)
			{
				return getCustomerConverter().convert(userModel);
			}
			return new CustomerData();
		}
		else
		{
			if (userModel instanceof B2BCustomerModel)
			{
				return super.getCurrentCustomer();
			}
			return new CustomerData();
		}
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.customer.SABMCustomerFacade#createProductExclusion(com.sabmiller.facades.customer.
	 * ProductExclusionData)
	 */
	@Override
	public void createProductExclusion(final Set<ProductExclusionData> productExclusions, final B2BUnitModel b2bUnitModel)
	{
		final Transaction tx = Transaction.current();
		tx.begin();
		boolean success = true;
		try
		{
			if (Config.getBoolean("product.exclusion.importhandler.useV1", false))
			{
				createProductExclusionV1(productExclusions, b2bUnitModel);
			}
			else
			{
				createProductExclusionV2(productExclusions, b2bUnitModel);
			}
			b2bUnitModel.setProductExclCallStatus(SapServiceCallStatus.DONE);
			getModelService().save(b2bUnitModel);
			b2bUnitService.markProductExclAsRefreshed(b2bUnitModel, SabmDateUtils.getOnlyDate(b2bUnitService.getStoreDate()));
		}
		catch (final Exception e)
		{
			success = false;
			LOG.error("Unable to update ProductEclusions", e);
		}
		finally
		{
			if (success)
			{
				tx.commit();
			}
			else
			{
				tx.rollback();
				getModelService().refresh(b2bUnitModel);
				b2bUnitModel.setProductExclCallStatus(SapServiceCallStatus.ERROR);
				getModelService().save(b2bUnitModel);
			}
		}
	}

	private void createProductExclusionV2(final Set<ProductExclusionData> productExclusions, final B2BUnitModel b2bUnitModel)
	{

		final Queue<ProductExclusionModel> b2bExclusionsToRemove = new LinkedList<>(
				productExclusionService.getCustomerProductExclusions(b2bUnitModel));

		if (CollectionUtils.isEmpty(productExclusions))
		{
			getModelService().removeAll(b2bExclusionsToRemove);
			return;
		}

		final List<ProductExclusionModel> exclusionModelList = getProductExclusionModels(productExclusions, b2bUnitModel,
				b2bExclusionsToRemove);

		if (CollectionUtils.isNotEmpty(b2bExclusionsToRemove))
		{
			getModelService().removeAll(b2bExclusionsToRemove);
		}

		final Iterator<ProductExclusionModel> productExclusionIterator = exclusionModelList.listIterator();

		//removed full matched
		while (productExclusionIterator.hasNext())
		{
			final ProductExclusionModel dbProductExclusion = productExclusionIterator.next();
			if (!getModelService().isNew(dbProductExclusion) && !getModelService().isModified(dbProductExclusion))
			{
				productExclusionIterator.remove();
			}
		}

		if (!exclusionModelList.isEmpty())
		{
			getModelService().saveAll(exclusionModelList);
		}



	}

	private Map<ProductExclusionData, Object[]> mapDataToModelProductExclusions(
			final Collection<ProductExclusionData> productExclusions, final Queue<ProductExclusionModel> productExclusionModels)
	{

		if (CollectionUtils.isEmpty(productExclusionModels))
		{
			return Collections.emptyMap();
		}

		final Map<ProductExclusionData, Object[]> exclusionDataMap = new HashMap();
		final List<ProductExclusionData> exclusionsToCheck = new ArrayList<>(productExclusions);

		//first find all matching exactly
		for (final ProductExclusionData productExclusionData : exclusionsToCheck)
		{

			final Optional<ProductExclusionModel> productExclusion = findFullMatch(productExclusionData, productExclusionModels);

			if (!productExclusion.isPresent())
			{
				continue;
			}
			productExclusionModels.remove(productExclusion.get());
			exclusionDataMap.put(productExclusionData, new Object[]
			{ productExclusion.get(), true });
		}

		if (!exclusionDataMap.isEmpty())
		{
			exclusionsToCheck.removeAll(exclusionDataMap.keySet());
		}

		if (exclusionsToCheck.isEmpty())
		{
			return exclusionDataMap;
		}

		//second loop is for same product check only

		for (final ProductExclusionData productExclusion : exclusionsToCheck)
		{
			final Optional<ProductExclusionModel> sameProduct = productExclusionModels.stream()
					.filter((p) -> StringUtils.equals(p.getProduct(), productExclusion.getProduct())).findFirst();
			if (sameProduct.isPresent())
			{
				exclusionDataMap.put(productExclusion, new Object[]
				{ sameProduct.get(), false });
				productExclusionModels.remove(sameProduct.get());
			}
		}

		return exclusionDataMap;
	}

	private Optional<ProductExclusionModel> findFullMatch(final ProductExclusionData productExclusion,
			final Queue<ProductExclusionModel> productExclusionModels)
	{

		for (final ProductExclusionModel productExclusionModel : productExclusionModels)
		{
			if (!StringUtils.equals(productExclusionModel.getProduct(), productExclusion.getProduct()))
			{
				continue;
			}

			if (!DateUtils.isSameDay(productExclusionModel.getValidFrom(), productExclusion.getValidFrom()))
			{
				continue;
			}

			if (!DateUtils.isSameDay(productExclusionModel.getValidTo(), productExclusion.getValidTo()))
			{
				continue;
			}

			return Optional.of(productExclusionModel);
		}

		return Optional.empty();
	}

	private void createProductExclusionV1(final Set<ProductExclusionData> productExclusions, final B2BUnitModel b2bUnitModel)
	{

		getModelService().removeAll(productExclusionService.getCustomerProductExclusions(b2bUnitModel));
		final List<ProductExclusionModel> exclusionModelList = getProductExclusionModels(productExclusions, b2bUnitModel);
		getModelService().saveAll(exclusionModelList);


	}


	/**
	 * Helper method to reuse product exclusions, not thread safe.
	 *
	 * @return
	 */
	private Function<ProductExclusionData, ProductExclusionModel> createProductExclusionSupplierForRemoval(
			final B2BUnitModel customer, final Map<ProductExclusionData, Object[]> exclusionTupleModelMap,
			final Queue<ProductExclusionModel> b2bExclusionsToRemove)
	{
		final Function<ProductExclusionData, ProductExclusionModel> productExclusionSupplier = (ped) -> {

			final Object tupleModelState[] = exclusionTupleModelMap.get(ped);

			if (tupleModelState != null)
			{
				final ProductExclusionModel productExclusionModel = (ProductExclusionModel) tupleModelState[0];
				final Boolean isFullMatch = (Boolean) tupleModelState[1];

				if (!isFullMatch)
				{
					productExclusionModel.setValidFrom(ped.getValidFrom());
					productExclusionModel.setValidTo(ped.getValidTo());
				}

				return productExclusionModel;
			}




			ProductExclusionModel productExclusion = b2bExclusionsToRemove.poll();

			if (productExclusion == null)
			{
				productExclusion = getModelService().create(ProductExclusionModel.class);
				productExclusion.setCustomer(customer);
			}

			productExclusion.setProduct(ped.getProduct());
			productExclusion.setValidFrom(ped.getValidFrom());
			productExclusion.setValidTo(ped.getValidTo());

			return productExclusion;

		};

		return productExclusionSupplier;
	}

	/**
	 * @param productExclusions
	 * @param b2bUnitModel
	 * @return
	 */
	private List<ProductExclusionModel> getProductExclusionModels(final Collection<ProductExclusionData> productExclusions,
			final B2BUnitModel b2bUnitModel, final Queue<ProductExclusionModel> b2bExclusionsToRemove)
	{
		//creates a mapping from database to new one
		final Map<ProductExclusionData, Object[]> productExclusionModelMap = mapDataToModelProductExclusions(productExclusions,
				b2bExclusionsToRemove);

		final Function<ProductExclusionData, ProductExclusionModel> exclusionCreationSupplier = createProductExclusionSupplierForRemoval(
				b2bUnitModel, productExclusionModelMap, b2bExclusionsToRemove);

		return productExclusions.stream().map(exclusionCreationSupplier).collect(Collectors.toList());

	}

	private List<ProductExclusionModel> getProductExclusionModels(final Collection<ProductExclusionData> productExclusions,
			final B2BUnitModel b2bUnitModel)
	{
		final List<ProductExclusionModel> list = new ArrayList<>();

		for (final ProductExclusionData data : productExclusions)
		{
			final ProductExclusionModel model = getModelService().create(ProductExclusionModel.class);
			model.setCustomer(b2bUnitModel);
			model.setProduct(data.getProduct());
			model.setValidFrom(data.getValidFrom());
			model.setValidTo(data.getValidTo());
			list.add(model);
		}
		return list;
	}


	/**
	 * ADD by SAB-574 Set OrderSimulationStatus to need calculation status, which will be used to call order simulate
	 */
	@Override
	@CacheEvict(value = "recommendationCache", key = "T(com.sabmiller.cache.impl.SabmCacheKeyGenerator).generateKey(false,true,false,'recommendations')")
	public void loginSuccess()
	{
		if (asahiSiteUtil.isCub())
		{
			final UserModel userModel = getUserService().getCurrentUser();
			if (userModel instanceof B2BCustomerModel)
			{
				final CustomerData userData = getCustomerConverter().convert(userModel);

				// First thing to do is to try to change the user on the session cart
				if (getCartService().hasSessionCart())
				{
					getCartService().changeCurrentCartUser(userModel);
				}

				// Update the session currency (which might change the cart currency)
				if (!updateSessionCurrency(userData.getCurrency(), getStoreSessionFacade().getDefaultCurrency()))
				{
					// Update the user
					getUserFacade().syncSessionCurrency();
				}

				// Update the user
				getUserFacade().syncSessionLanguage();

				// Calculate the cart after setting everything up
				if (getCartService().hasSessionCart())
				{
					final CartModel sessionCart = getCartService().getSessionCart();

					// Clean the existing info on the cart if it does not beling to the current user
					getCartCleanStrategy().cleanCart(sessionCart);
					try
					{
						final CommerceCartParameter parameter = new CommerceCartParameter();
						parameter.setEnableHooks(true);
						parameter.setCart(sessionCart);
						getCommerceCartService().recalculateCart(parameter);
					}
					catch (final CalculationException ex)
					{
						LOG.error("Failed to recalculate order [" + sessionCart.getCode() + "]", ex);
					}
				}
				postLoginSuccess();
			}

			if (userModel != null)
			{
				userModel.setLastLogin(new Date());
				getModelService().save(userModel);
			}

			final Set<String> disableSearchRestrictions = new HashSet<>();

			disableSearchRestrictions.add("branch_restriction");
			disableSearchRestrictions.add("employee_restriction");
			disableSearchRestrictions.add("apbb2border_restriction");
			disableSearchRestrictions.add("Frontend_RestrictedCategory");
			disableSearchRestrictions.add("Frontend_ProductOfflineDate");



			sabmSearchRestrictionService.simulateSearchRestrictionDisabledInSession(disableSearchRestrictions);
		}
		else
		{
			super.loginSuccess();
			final Set<String> disableSearchRestrictions = new HashSet<>();
			disableSearchRestrictions.add("Frontend_ProductExclusionRestriction");
			disableSearchRestrictions.add("b2border_restriction");

			sabmSearchRestrictionService.simulateSearchRestrictionDisabledInSession(disableSearchRestrictions);
		}
	}

	/**
	 * add for fix the bug SAB-2531
	 */
	@Override
	public void rememberMeLoginSuccessWithUrlEncoding(final boolean languageEncoding, final boolean currencyEncoding)
	{
		super.rememberMeLoginSuccessWithUrlEncoding(languageEncoding, currencyEncoding);
		if (asahiSiteUtil.isCub())
		{
			postLoginSuccess();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.customer.SABMCustomerFacade#canUserLogin(de.hybris.platform.b2b.model.B2BCustomerModel)
	 */
	@Override
	public boolean canUserLogin(final B2BCustomerModel user)
	{
		final LoginStatus loginStatus = getLoginStatus(user);
		return LoginStatus.ENABLE.equals(loginStatus) || LoginStatus.SAP_CHECKOUT_BLOCKED.equals(loginStatus);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.facades.customer.SABMCustomerFacade#getLoginStatus(de.hybris.platform.b2b.model.B2BCustomerModel)
	 */
	@Override
	public LoginStatus getLoginStatus(final UserModel user)
	{
		if (user instanceof B2BCustomerModel)
		{
			final B2BUnitModel b2bUnitModel = b2bUnitService.getParent((B2BCustomerModel) user);

			if (BlockTypeEnum.BLOCK_ACCOUNT.equals(b2bUnitModel.getBlockType()))
			{
				return LoginStatus.SAP_ACCOUNT_BLOCKED;
			}
			else if (BlockTypeEnum.BLOCK_CHECKOUT.equals(b2bUnitModel.getBlockType()))
			{
				return LoginStatus.SAP_CHECKOUT_BLOCKED;
			}
			else if (StringUtils.equalsIgnoreCase(b2bUnitModel.getCustomerFlag(), customerSuspended))
			{
				return LoginStatus.SAP_SUSPENDED;
			}
		}

		return LoginStatus.ENABLE;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.customer.SABMCustomerFacade#getLoginStatus()
	 */
	@Override
	public LoginStatus getLoginStatus()
	{
		return getLoginStatus(userService.getCurrentUser());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.customer.SABMCustomerFacade#getCustomerInvoices()
	 */
	@Override
	public SABMInvoicePageData getCustomerInvoices(final String lineItem, final String forUnit, final String startDate,
			final String endDate, final String invoiceType)
	{
		final SABMInvoicePageData invoicePageData = new SABMInvoicePageData();
		BigDecimal totalOpenBalance = BigDecimal.ZERO;
		List<SABMInvoiceData> invoiceList = null;
		B2BUnitModel b2bUnit = null;
		Map<String, SABMInvoiceData> invoiceMap = null;
		try
		{
			final CustomerModel customer = getCurrentSessionCustomer();

			//Check if customer is B2BCustomer to avoid ClassCastException retrieving the B2BUnit
			if (customer instanceof B2BCustomerModel)
			{
				if (StringUtils.isNotEmpty(forUnit))
				{
					b2bUnit = b2bUnitService.getUnitForUid(forUnit);
				}
				if (b2bUnit == null)
				{
					b2bUnit = b2bCommerceUnitService.getParentUnit();
				}
				if (b2bUnit != null)
				{
					CustomerBillingDataResponse invoiceResponseItems = null;

					if (asahiConfigurationService.getBoolean(BILLINGDATA_STUB_AVAILABLE_CHECK, false))
					{
						try
						{
							final MediaModel stubMedia = mediaService.getMedia(BILLINGDATA_STUB_MEDIA_RESPONSE);
							final InputStream targetStream = mediaService.getStreamFromMedia(stubMedia);
							final JAXBContext jaxbContext = JAXBContext.newInstance(CustomerBillingDataResponse.class);
							final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
							final XMLInputFactory factory = XMLInputFactory.newInstance();
							final XMLEventReader fileSource = factory.createXMLEventReader(targetStream);
							final JAXBElement<CustomerBillingDataResponse> userElement = unmarshaller.unmarshal(fileSource,
									CustomerBillingDataResponse.class);
							invoiceResponseItems = userElement.getValue();
						}
						catch (final Exception e)
						{
							LOG.error("Exception occurred while trying to create stub for CustomerBillingData ", e);
						}
					}
					else
					{
						invoiceResponseItems = invoiceRequestHandler
								.sendPostRequest(createInvoiceRequest(b2bUnit, lineItem, startDate, endDate));
					}

					if (invoiceResponseItems != null && CollectionUtils.isNotEmpty(invoiceResponseItems.getInvoice()))
					{
						invoiceList = new ArrayList<>();
						final List<String> invoiceNumbers = new ArrayList<>();
						invoiceMap = new HashMap<String, SABMInvoiceData>();

						for (final Invoice invoice : invoiceResponseItems.getInvoice())
						{
							if (StringUtils.equalsIgnoreCase(b2bUnit.getAccountGroup(), "ZADP")
									|| StringUtils.isBlank(invoice.getSoldTo())
									|| StringUtils.equalsIgnoreCase(invoice.getSoldTo(), b2bUnit.getUid()))
							{

								final SABMInvoiceData invoiceData = invoiceConverter.convert(invoice);
								invoiceList.add(invoiceData);
								invoiceNumbers.add(invoice.getInvoiceNumber());
								invoiceMap.put(invoice.getInvoiceNumber(), invoiceData);

							}
						}

						if (StringUtils.isNotEmpty(invoiceType))
						{
							invoiceList = invoiceList.stream().filter(sabmInvoiceData -> invoiceType.equals(sabmInvoiceData.getType()))
									.collect(Collectors.toList());
						}
						if (asahiCoreUtil.isNAPUser())
						{
							invoiceList = invoiceList.stream()
									.filter(sabmInvoiceData -> (!ADJUSTMENT.equals(sabmInvoiceData.getType())
											&& !DEDUCTION_CREDIT.equals(sabmInvoiceData.getType())
											&& !EFT_PAYMENT.equals(sabmInvoiceData.getType())))
									.collect(Collectors.toList());
						}
						getSessionService().setAttribute(SabmCoreConstants.SESSION_B2BUNIT_INVOICES, invoiceNumbers);
						getSessionService().setAttribute(SabmCoreConstants.SESSION_B2BUNIT_INVOICES_MAP, invoiceMap);
					}

					CustomerBillingDataResponse openInvoiceResponseItems = null;

					if (asahiConfigurationService.getBoolean(BILLINGDATA_STUB_AVAILABLE_CHECK, true))
					{
						try
						{
							final MediaModel stubMedia = mediaService.getMedia(BILLINGDATA_STUB_MEDIA_RESPONSE);
							final InputStream targetStream = mediaService.getStreamFromMedia(stubMedia);
							final JAXBContext jaxbContext = JAXBContext.newInstance(CustomerBillingDataResponse.class);
							final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
							final XMLInputFactory factory = XMLInputFactory.newInstance();
							final XMLEventReader fileSource = factory.createXMLEventReader(targetStream);
							final JAXBElement<CustomerBillingDataResponse> userElement = unmarshaller.unmarshal(fileSource,
									CustomerBillingDataResponse.class);
							openInvoiceResponseItems = userElement.getValue();
						}
						catch (final Exception e)
						{
							LOG.error("Exception occurred while trying to create stub for CustomerBillingData ", e);
						}
					}
					else
					{
						openInvoiceResponseItems = invoiceRequestHandler
								.sendPostRequest(createInvoiceRequest(b2bUnit, defaultLineItem, null, null));
					}

					if (openInvoiceResponseItems != null && CollectionUtils.isNotEmpty(openInvoiceResponseItems.getInvoice()))
					{
						for (final Invoice invoice : openInvoiceResponseItems.getInvoice())
						{
							if (StringUtils.equalsIgnoreCase(b2bUnit.getAccountGroup(), "ZADP")
									|| StringUtils.isBlank(invoice.getSoldTo())
									|| StringUtils.equalsIgnoreCase(invoice.getSoldTo(), b2bUnit.getUid()))
							{
								final BigDecimal bigDecimal = sabFormatterUtil.parseSAPNumber(invoice.getValueLocalCurrency());
								totalOpenBalance = totalOpenBalance.add(bigDecimal);
							}
						}
					}
				}

			}
		}
		catch (final SABMIntegrationException e)
		{
			LOG.warn("Error getting invoices for customer: " + getCurrentSessionCustomer(), e);
		}
		invoicePageData.setInvoices(invoiceList != null ? invoiceList : Collections.emptyList());
		invoicePageData.setOpenBalance(totalOpenBalance.toString());
		return invoicePageData;

	}

	/**
	 * Calculate delivery date.
	 *
	 * @return the date
	 */
	protected Date calculateDeliveryDate()
	{
		Date nextDeliveryDate = null;
		if (getCartService().hasSessionCart())
		{
			final CartModel cart = getCartService().getSessionCart();

			if (cart.getRequestedDeliveryDate() != null)
			{
				final Date rdd = SabmDateUtils.getOnlyDate(cart.getRequestedDeliveryDate());
				if (sabmDeliveryDateCutOffService.isValidDeliveryDate(rdd))
				{
					getSessionService().setAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE, rdd);
					getSessionService().setAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE_PACKTYPE,
							getSabmDeliveryDateCutOffService().getDeliveryDatePackType(b2bCommerceUnitService.getParentUnit(), rdd)
									.get(PackType._TYPECODE));

					return rdd;
				}
				nextDeliveryDate = getNextDeliveryDateAndUpdateSession();
			}
		}

		return nextDeliveryDate != null ? nextDeliveryDate : getNextDeliveryDateAndUpdateSession();
	}

	@Override
	public void refreshCoreEntities()
	{
		// this is to avoid the exceptions in local environment.
		// by default, empty properties means do the sync process.
		if (!Config.getBoolean(SYNC_DEALS_FROM_SAP_PROPERTY, true))
		{
			return;
		}

		final B2BUnitModel b2bUnitModel = b2bCommerceUnitService.getParentUnit();

		if (sabmConfigurationService.isEnableProductExclusion())
		{
			b2bUnitService.requestProductExclusions(b2bUnitModel);
		}

		dealsService.refreshDealCache();
		b2bUnitService.refreshCUP(b2bUnitModel);
		dealsService.refreshDeals(b2bUnitModel);

	}

	@Override
	public void verifyAndUpdateCUPForRDD(final Date date, final String packType)
	{
		// this is to avoid the exceptions in local environment.
		// by default, empty properties means do the sync process.
		if (!Config.getBoolean(SYNC_DEALS_FROM_SAP_PROPERTY, true))
		{
			return;
		}

		final B2BUnitModel b2bUnitModel = b2bCommerceUnitService.getParentUnit();

		b2bUnitService.updateCUP(b2bUnitModel, date);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.customer.SABMCustomerFacade#saveActiveForCustomer(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean saveActiveForCustomer(final String customerUid, final String activeFlag, final String b2bUnitId)
	{
		//final B2BCustomerModel b2bCustomerModel = b2bCommerceUnitService.getCustomerForUid(customerUid);
		final B2BCustomerModel b2bCustomerModel = (B2BCustomerModel) b2bCustomerService.getUserForUID(customerUid);
		final B2BUnitModel b2bUnitModel = b2bUnitService.getUnitForUid(b2bUnitId);
		if (null != b2bCustomerModel)
		{
			try
			{
				final Collection<String> disabledUsersList = new ArrayList<String>(b2bUnitModel.getCubDisabledUsers());
				if (("false").equals(activeFlag))
				{
					if (!(disabledUsersList.contains(customerUid)))
					{
						disabledUsersList.add(customerUid);
					}
				}
				else if (("true").equals(activeFlag))
				{
					disabledUsersList.remove(customerUid);
				}
				b2bUnitModel.setCubDisabledUsers(disabledUsersList);
				getModelService().save(b2bUnitModel);
				getModelService().refresh(b2bUnitModel);
				b2bCustomerModel.setModifiedtime(new Date());
				getModelService().save(b2bCustomerModel);
				getModelService().refresh(b2bCustomerModel);
				return true;
			}
			catch (final ModelSavingException e)
			{
				LOG.warn("Failed to save b2bCustomerModel [{}] ", b2bCustomerModel, e);
				return false;
			}

		}
		LOG.warn("Unable to found b2bCustomerModel");
		return false;
	}

	/**
	 * Checks if is password set.
	 *
	 * @param userUid
	 *           the user uid
	 * @return true, if is password set
	 */
	@Override
	public boolean isPasswordSet(final String token)
	{
		final SecureToken data = secureTokenService.decryptData(token);

		if (data == null || data.getData() == null)
		{
			return false;
		}

		final CustomerModel customer = getUserService().getUserForUID(data.getData(), CustomerModel.class);

		return customer != null && StringUtils.isNotEmpty(customer.getEncodedPassword());
	}

	/**
	 * Creates the invoice request.
	 *
	 * @param b2bUnit
	 *           the b2b unit
	 * @param lineItem
	 *           the line item
	 * @param startDateString
	 *           the start date string
	 * @param endDateString
	 *           the end date string
	 * @return the customer billing data request
	 */
	protected CustomerBillingDataRequest createInvoiceRequest(final B2BUnitModel b2bUnit, final String lineItem,
			final String startDateString, final String endDateString)
	{
		final CustomerBillingDataRequest request = new CustomerBillingDataRequest();

		if (b2bUnit != null)
		{
			request.setCustomerSoldTo(b2bUnit.getUid());
			request.setSalesOrganization(b2bUnit.getSalesOrgId());
		}

		request.setLineitemselection(StringUtils.defaultIfEmpty(lineItem, defaultLineItem));

		final Date startDate = sabFormatterUtil.parseDate(startDateString, dateSelectorPattern);
		final Date endDate = sabFormatterUtil.parseDate(endDateString, dateSelectorPattern);

		if (startDate != null && endDate != null)
		{
			final StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(DateFormatUtils.format(startDate, dateSapPattern));
			stringBuilder.append(dateSapSeparator);
			stringBuilder.append(DateFormatUtils.format(endDate, dateSapPattern));

			request.setDateRange(stringBuilder.toString());
		}

		return request;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.customer.SABMCustomerFacade#getInvoicePDF(java.lang.String)
	 */
	@Override
	public SABMInvoicePDFData getInvoicePDF(final String docNum)
	{
		SABMInvoicePDFData invoiceData = null;

		if (StringUtils.isEmpty(docNum))
		{
			return invoiceData;
		}

		try
		{
			final List<String> invoices = getSessionService().getAttribute(SabmCoreConstants.SESSION_B2BUNIT_INVOICES);

			if (CollectionUtils.isNotEmpty(invoices) && invoices.contains(docNum))
			{
				final InvoiceDataRequest request = new InvoiceDataRequest();
				final InvoiceDetails invoiceDetail = new InvoiceDetails();
				invoiceDetail.setDocumentNumber(docNum);

				//Setting (S)ynchronous call method
				invoiceDetail.setProcessFlag("S");

				request.getInvoiceDetails().add(invoiceDetail);

				InvoiceDataResponse invoiceDataResponse = null;

				if (asahiConfigurationService.getBoolean(INVOICEPDF_STUB_AVAILABLE_CHECK, false))
				{
					try
					{
						final MediaModel stubMedia = mediaService.getMedia(INVOICEPDF_STUB_MEDIA_RESPONSE);
						final InputStream targetStream = mediaService.getStreamFromMedia(stubMedia);
						final JAXBContext jaxbContext = JAXBContext.newInstance(InvoiceDataResponse.class);
						final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
						final XMLInputFactory factory = XMLInputFactory.newInstance();
						final XMLEventReader fileSource = factory.createXMLEventReader(targetStream);
						final JAXBElement<InvoiceDataResponse> userElement = unmarshaller.unmarshal(fileSource,
								InvoiceDataResponse.class);
						invoiceDataResponse = userElement.getValue();
					}
					catch (final Exception e)
					{
						LOG.error("Exception occurred while trying to create stub for InvoiceData ", e);
					}
				}
				else
				{
					invoiceDataResponse = invoicePDFRequestHandler.sendPostRequest(request);
				}


				if (invoiceDataResponse != null && CollectionUtils.isNotEmpty(invoiceDataResponse.getInvoiceData()))
				{
					invoiceData = invoicePdfConverter.convert(invoiceDataResponse.getInvoiceData().get(0));
				}
			}
		}
		catch (final SABMIntegrationException e)
		{
			LOG.warn("Error getting invoice PDF for customer: " + getCurrentSessionCustomer(), e);
		}

		return invoiceData;
	}

	@Override
	public boolean sendInvoicesEmail(final List<String> docNumList)
	{
		boolean success = false;
		try
		{
			final List<String> invoices = getSessionService().getAttribute(SabmCoreConstants.SESSION_B2BUNIT_INVOICES);

			if (CollectionUtils.isEmpty(invoices))
			{
				return success;
			}

			final CustomerModel customer = getCurrentSessionCustomer();

			if (customer != null && CollectionUtils.isNotEmpty(docNumList))
			{
				final InvoiceDataRequest request = new InvoiceDataRequest();

				for (final String docNum : docNumList)
				{
					if (invoices.contains(docNum))
					{
						final InvoiceDetails invoiceDetail = new InvoiceDetails();
						invoiceDetail.setDocumentNumber(docNum);

						//Setting (B)atch call method
						invoiceDetail.setProcessFlag("B");
						invoiceDetail.setEmail(customer.getUid());

						request.getInvoiceDetails().add(invoiceDetail);
					}
				}

				invoicePDFRequestHandler.sendPostRequest(request);

				success = true;
			}
		}
		catch (final SABMIntegrationException e)
		{
			LOG.warn("Error getting invoice PDF for customer: " + getCurrentSessionCustomer(), e);
		}

		return success;

	}


	/**
	 * @author yuxiao.wang
	 *
	 *         Sent a mail to User when Update Password Successfully
	 */
	@Override
	public void sendChangePwdEmailMessage()
	{
		try
		{
			//customer mail
			final String emailAddress = ((CustomerModel) userService.getCurrentUser()).getContactEmail();
			//get the EmailAddressModel
			final EmailAddressModel emailAddressModel = emailService.getOrCreateEmailAddressForEmail(emailAddress, EMAIL_DISPLAY);
			final EmailAddressModel formEmailAddressModel = emailService.getOrCreateEmailAddressForEmail(emailConfig.getMailFrom(),
					EMAIL_DISPLAY);
			final List<EmailAddressModel> emailAddressModels = new ArrayList<EmailAddressModel>();
			emailAddressModels.add(emailAddressModel);
			final EmailMessageModel emailMessageModel = emailService.createEmailMessage(emailAddressModels, null, null,
					formEmailAddressModel, formEmailAddressModel.getEmailAddress(), EMAIL_SUBJECT, EMAIL_BODY, null);
			// send the email
			emailService.send(emailMessageModel);
		}
		catch (final Exception e)
		{
			LOG.error("Failed to send change password mail [{}] ", getCurrentSessionCustomer(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.customer.SABMCustomerFacade#saveUser(java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public CustomerData saveUser(final CustomerJson customerJson)
	{
		Assert.hasText(customerJson.getFirstName(), "The field [FirstName] cannot be empty");
		Assert.hasText(customerJson.getSurName(), "The field [surName] cannot be empty");
		Assert.hasText(customerJson.getEmail(), "The field [email] cannot be empty");

		if (LOG.isDebugEnabled())
		{
			LOG.debug("CustomerJson is {} ", ReflectionToStringBuilder.toString(customerJson));
		}
		B2BCustomerModel b2bCustomerModel = null;
		try
		{
			final List<B2BUnitJson> b2bUnitJsons = getSelectedB2BUnit(customerJson);

			if (CollectionUtils.isEmpty(b2bUnitJsons))
			{
				LOG.warn("No select B2BUnit");
				return null;
			}

			if (!customerJson.isExists())
			{
				final UserModel user = asahiCoreUtil.checkIfUserExists(customerJson.getEmail());
				if (null != user && user instanceof B2BCustomerModel && sabmB2BCustomerService.isRegistrationAllowed(user, null))
				{
					b2bCustomerModel = (B2BCustomerModel) user;
					if (BooleanUtils.isTrue(b2bCustomerModel.isLoginDisabled()))
					{
						b2bCustomerModel.setLoginDisabled(Boolean.FALSE);
					}

					//Preventive check , even though all inactive customers would have been marked as active with a one-time script execution
					if (BooleanUtils.isFalse(b2bCustomerModel.getActive()))
					{
						b2bCustomerModel.setActive(Boolean.TRUE);
						//add all b2bunits to list of disabled user list
						for (final PrincipalGroupModel group : b2bCustomerModel.getGroups())
						{
							if (group instanceof AsahiB2BUnitModel)
							{
								final Collection<String> disabledUsers = new HashSet<String>();
								disabledUsers.addAll(((AsahiB2BUnitModel) group).getDisabledUser());
								disabledUsers.add(b2bCustomerModel.getUid());
								((AsahiB2BUnitModel) group).setDisabledUser(disabledUsers);
								getModelService().save(group);
								b2bCustomerModel.setModifiedtime(new Date());
								getModelService().save(b2bCustomerModel);
								getModelService().refresh(b2bCustomerModel);
							}
							else if (group instanceof B2BUnitModel)
							{
								final Collection<String> disabledUsers = new HashSet<String>();
								disabledUsers.addAll(((B2BUnitModel) group).getCubDisabledUsers());
								disabledUsers.add(b2bCustomerModel.getUid());
								((B2BUnitModel) group).setCubDisabledUsers(disabledUsers);
								getModelService().save(group);
								b2bCustomerModel.setModifiedtime(new Date());
								getModelService().save(b2bCustomerModel);
								getModelService().refresh(b2bCustomerModel);
							}

						}
					}
				}
				else
				{
					b2bCustomerModel = getModelService().create(B2BCustomerModel.class);
					b2bCustomerModel.setActive(Boolean.TRUE);
				}
				b2bCustomerModel.setFirstName(customerJson.getFirstName());
				b2bCustomerModel.setLastName(customerJson.getSurName());
				b2bCustomerModel.setName(getCustomerNameStrategy().getName(customerJson.getFirstName(), customerJson.getSurName()));
				b2bCustomerModel.setEmail(customerJson.getEmail());
				b2bCustomerModel.setUid(StringUtils.lowerCase(customerJson.getEmail()));
				b2bCustomerModel.setOriginalUid(customerJson.getEmail());
				b2bCustomerModel.setMobileContactNumber(StringUtils.deleteWhitespace(customerJson.getPhoneNumber()));
				b2bCustomerModel.setReceiveUpdates(Boolean.TRUE);
				b2bCustomerModel.setReceiveUpdatesForSms(Boolean.TRUE);
				if (customerJson.getPermissions().isOrders())
				{
					b2bCustomerModel.setOrderLimit(customerJson.getPermissions().getOrderLimit());
				}
				b2bCustomerModel.setSessionLanguage(getCommonI18NService().getCurrentLanguage());
				b2bCustomerModel.setSessionCurrency(getCommonI18NService().getCurrentCurrency());
				//Save currentUser to the new customer's createdBy for SAB-1969
				b2bCustomerModel.setCreatedBy((B2BCustomerModel) userService.getCurrentUser());
				// Added for fetching correct Created by User
				b2bCustomerModel.setCreatedName(userService.getCurrentUser().getName());
				//b2bCustomerModel.setDefaultB2BUnit(parentB2BUnit);
				setParentB2BUnit(b2bCustomerModel, b2bUnitJsons);
				updateCustomerB2BUnit(b2bCustomerModel, b2bUnitJsons, new HashSet<PrincipalGroupModel>());
				//save model
				internalSaveCustomer(b2bCustomerModel);
			}
			else
			{
				return this.editUser(customerJson);
			}
			//manage the user group
			this.addUserToCustomerGroup(customerJson);
			// sent to the nominated email address for the new user to set a password, but now no email template, pending SAB-1599.
			this.sendWelcomeEmailMessage(customerJson.getEmail());

			return getCustomerConverter().convert(b2bCustomerModel);
		}
		catch (final DuplicateUidException e)
		{
			LOG.error("Save user fail. The uid: [{}]", customerJson.getEmail(), e);
		}
		return null;
	}

	/**
	 *
	 * @param b2bCustomerModel
	 * @param b2bUnitJsons
	 * @param asahiUnits
	 */
	private void updateCustomerB2BUnit(final B2BCustomerModel b2bCustomerModel, final List<B2BUnitJson> b2bUnitJsons,
			final Set<PrincipalGroupModel> asahiUnits)
	{
		final Set<PrincipalGroupModel> setPrincipal = new HashSet<PrincipalGroupModel>(
				null != b2bCustomerModel.getGroups() ? b2bCustomerModel.getGroups() : Collections.emptySet());

		//remove deletedcustomergroup, if any for  an existing user during re-registration
		setPrincipal.removeIf(group -> group.getUid().equalsIgnoreCase(SabmCoreConstants.DELETEDCUSTOMERGROUP));


		for (final B2BUnitJson b2bUnitJson : ListUtils.emptyIfNull(b2bUnitJsons))
		{
			setPrincipal.add(b2bUnitService.getUnitForUid(b2bUnitJson.getCode()));
		}

		if (CollectionUtils.isNotEmpty(asahiUnits))
		{
			setPrincipal.addAll(asahiUnits);
		}
		b2bCustomerModel.setGroups(setPrincipal);
	}

	/**
	 * @param customerJson
	 * @return List<B2BUnitJson>
	 */
	private List<B2BUnitJson> getSelectedB2BUnit(final CustomerJson customerJson)
	{
		final List<B2BUnitJson> b2bUnitJsons = Lists.newArrayList();
		final int noOfStates = ListUtils.emptyIfNull(customerJson.getStates()).size();
		for (final RegionJson regionJson : ListUtils.emptyIfNull(customerJson.getStates()))
		{
			for (final B2BUnitJson b2bUnitJson : ListUtils.emptyIfNull(regionJson.getB2bunits()))
			{
				if (b2bUnitJson.isSelected())
				{
					b2bUnitJsons.add(b2bUnitJson);
				}
			}

			if (b2bUnitJsons.isEmpty() && noOfStates == 1)
			{
				final int noOfb2bUnits = ListUtils.emptyIfNull(regionJson.getB2bunits()).size();
				if (noOfb2bUnits == 1)
				{
					b2bUnitJsons.add(regionJson.getB2bunits().get(0));
				}
			}
		}
		return b2bUnitJsons;
	}

	private List<B2BUnitJson> getAllB2BUnit(final CustomerJson customerJson, final B2BCustomerModel b2bCustomerModel)
	{
		final List<B2BUnitJson> b2bUnitJsons = Lists.newArrayList();
		for (final RegionJson regionJson : ListUtils.emptyIfNull(customerJson.getStates()))
		{
			for (final B2BUnitJson b2bUnitJson : ListUtils.emptyIfNull(regionJson.getB2bunits()))
			{
				if (b2bUnitJson.isSelected())
				{
					b2bUnitJsons.add(b2bUnitJson);
				}
				else
				{
					if (null != b2bCustomerModel && customerB2bUnitCheck(b2bCustomerModel, b2bUnitJson.getCode()))
					{
						b2bUnitJsons.add(b2bUnitJson);
					}
				}

			}
		}
		return b2bUnitJsons;
	}

	/**
	 * @param b2bCustomerModel
	 * @param code
	 * @return
	 */
	private boolean customerB2bUnitCheck(final B2BCustomerModel b2bCustomerModel, final String b2bUnitCode)
	{

		final Set<PrincipalGroupModel> principalGroups = b2bCustomerModel.getGroups();
		for (final PrincipalGroupModel principalGroup : SetUtils.emptyIfNull(principalGroups))
		{
			if (principalGroup instanceof AsahiB2BUnitModel)
			{
				continue;
			}

			if (principalGroup instanceof B2BUnitModel)
			{
				final B2BUnitModel b2bUnit = (B2BUnitModel) principalGroup;

				if (b2bUnitCode.equals(b2bUnit.getUid()))
				{
					return true;
				}
			}
		}
		return false;
	}


	/**
	 * add role to customer group
	 *
	 * @param customerJson
	 */
	private void addUserToCustomerGroup(final CustomerJson customerJson)
	{
		final String uid = StringUtils.lowerCase(customerJson.getEmail());
		getCustomerGroupFacade().addUserToCustomerGroup(B2BConstants.B2BCUSTOMERGROUP, uid);
		final PermissionsJson permissionsJson = customerJson.getPermissions();
		// permission: Create and Edit user
		if (permissionsJson.isPa())
		{
			getCustomerGroupFacade().addUserToCustomerGroup(SabmFacadesConstants.B2BASSISTANTGROUP, uid);
			/*
			 * final B2BUnitData b2bUnitData = b2bCommerceUnitFacade.getTopLevelB2BUnit(); if (null != b2bUnitData) { final
			 * Set<PrincipalGroupModel> setPrincipal = new HashSet<PrincipalGroupModel>(b2bCustomerModel.getGroups());
			 * setPrincipal.add(b2bUnitService.getUnitForUid(b2bUnitData.getUid()));
			 * b2bCustomerModel.setGroups(setPrincipal); getModelService().save(b2bCustomerModel); }
			 */
		}
		//  permission: Place orders
		if (permissionsJson.isOrders())
		{
			getCustomerGroupFacade().addUserToCustomerGroup(SabmFacadesConstants.B2BORDERCUSTOMER, uid);
		}
		// permission: View and pay invoices
		if (permissionsJson.isPay())
		{
			getCustomerGroupFacade().addUserToCustomerGroup(SabmFacadesConstants.B2BINVOICECUSTOMER, uid);
		}
	}

	/**
	 * Saves the customer translating model layer exceptions regarding duplicate identifiers
	 *
	 * @param customerModel
	 * @throws DuplicateUidException
	 *            if the uid is not unique
	 */
	protected void internalSaveCustomer(final B2BCustomerModel customerModel) throws DuplicateUidException
	{
		try
		{
			getModelService().save(customerModel);
		}
		catch (final ModelSavingException e)
		{
			if (e.getCause() instanceof InterceptorException
					&& ((InterceptorException) e.getCause()).getInterceptor().getClass().equals(UniqueAttributesInterceptor.class))
			{
				throw new DuplicateUidException(customerModel.getUid(), e);
			}
			throw e;
		}
		catch (final AmbiguousIdentifierException e)
		{
			throw new DuplicateUidException(customerModel.getUid(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.customer.SABMCustomerFacade#getCustomerForUid()
	 */
	@Override
	public CustomerData getCustomerForUid(final String uId)
	{
		B2BCustomerModel customerModel = null;
		try
		{
			//customerModel = b2bCommerceUnitService.getCustomerForUid(StringUtils.lowerCase(uId));
			customerModel = (B2BCustomerModel) b2bCustomerService.getUserForUID(StringUtils.lowerCase(uId));
		}
		catch (final Exception e)
		{
			LOG.error("Can not find this user ,the UID:" + uId, e);
			return null;
		}

		return getCustomerConverter().convert(customerModel);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.customer.SABMCustomerFacade#getB2BUnitForId(java.lang.String)
	 */
	@Override
	public B2BUnitModel getB2BUnitForId(final String businessId)
	{
		return b2bUnitService.getUnitForUid(businessId);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.customer.SABMCustomerFacade#editUser(java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public CustomerData editUser(final CustomerJson customerJson) throws DuplicateUidException
	{
		Assert.hasText(customerJson.getFirstName(), "The field [FirstName] cannot be empty");
		Assert.hasText(customerJson.getSurName(), "The field [surName] cannot be empty");
		Assert.hasText(customerJson.getEmail(), "The field [email] cannot be empty");
		B2BCustomerModel b2bCustomerModel = null;
		final boolean isActive = customerJson.isActive();
		try
		{

			//b2bCustomerModel = getB2bCommerceUnitService().getCustomerForUid(StringUtils.lowerCase(StringUtils.trim(customerJson.getCurrentEmail())));
			b2bCustomerModel = (B2BCustomerModel) b2bCustomerService.getUserForUID(StringUtils.trim(customerJson.getCurrentEmail()));

			final List<B2BUnitJson> allCustomerb2bUnitJsons = getAllB2BUnit(customerJson, b2bCustomerModel);
			if (isNeedSendNoticeEmail(b2bCustomerModel, customerJson))
			{
				sendCustomerProfileUpdatedNoticeEmail(b2bCustomerModel, getUserService().getCurrentUser());
			}
			b2bCustomerModel.setMobileContactNumber(StringUtils.deleteWhitespace(customerJson.getPhoneNumber()));
			b2bCustomerModel.setFirstName(customerJson.getFirstName());
			b2bCustomerModel.setFirstName(customerJson.getFirstName());
			b2bCustomerModel.setLastName(customerJson.getSurName());
			b2bCustomerModel.setActive(isActive);
			if (isActive == false)
			{
				b2bCustomerModel.setReceiveUpdates(Boolean.FALSE);
				b2bCustomerModel.setReceiveUpdatesForSms(Boolean.FALSE);
			}
			else
			{
				b2bCustomerModel.setReceiveUpdates(Boolean.TRUE);
				b2bCustomerModel.setReceiveUpdatesForSms(Boolean.TRUE);
			}
			if (!StringUtils.equalsIgnoreCase(StringUtils.trim(customerJson.getCurrentEmail()),
					StringUtils.trim(customerJson.getEmail())) && getUserService().getCurrentUser() instanceof BDECustomerModel)
			{
				b2bCustomerModel.setUid(StringUtils.trim(customerJson.getEmail()));
				b2bCustomerModel.setEmail(StringUtils.trim(customerJson.getEmail()));

			}

			b2bCustomerModel.setName(getCustomerNameStrategy().getName(customerJson.getFirstName(), customerJson.getSurName()));
			if (customerJson.getPermissions().isOrders())
			{
				b2bCustomerModel.setOrderLimit(customerJson.getPermissions().getOrderLimit());
			}
			else
			{
				b2bCustomerModel.setOrderLimit(null);
			}


			final Set<PrincipalGroupModel> asahiUnits = new HashSet<PrincipalGroupModel>();

			for (final PrincipalGroupModel pg : b2bCustomerModel.getGroups())
			{
				if (pg instanceof AsahiB2BUnitModel || pg.getUid().equalsIgnoreCase(B2BConstants.B2BADMINGROUP))
				{
					asahiUnits.add(pg);
				}
			}

			//b2bCustomerModel.setDefaultB2BUnit(this.getB2BUnitForId(createUserFormData.getBusinessUnit()));
			final HashMap<String, List<B2BUnitJson>> b2bUnitSelection = getActiveB2BUnits(allCustomerb2bUnitJsons);
			final List<B2BUnitJson> selectedActiveB2bUnits = b2bUnitSelection.get(CHECKED);
			final List<B2BUnitJson> deselectedActiveB2bUnits = b2bUnitSelection.get(UNCHECKED);
			updateB2bUnitsDisabledList(b2bCustomerModel, selectedActiveB2bUnits, deselectedActiveB2bUnits);

			if (b2bCustomerModel.getDefaultB2BUnit() != null && CollectionUtils.isNotEmpty(deselectedActiveB2bUnits)
					&& checkDefaultB2BUnit(b2bCustomerModel.getDefaultB2BUnit().getUid(), deselectedActiveB2bUnits))
			{
				setDefaultB2BUnit(b2bCustomerModel.getDefaultB2BUnit().getUid(), b2bCustomerModel);
			}
			//reset the user group
			b2bCustomerModel.setGroups(Sets.newConcurrentHashSet());

			updateCustomerB2BUnit(b2bCustomerModel, allCustomerb2bUnitJsons, asahiUnits);

			//save this user .
			internalSaveCustomer(b2bCustomerModel);
			//manage the user group
			this.addUserToCustomerGroup(customerJson);

			return getCustomerConverter().convert(b2bCustomerModel);
		}
		catch (final DuplicateUidException e)
		{
			LOG.error("Update user fail. The uid: [{}]", customerJson.getEmail(), e);
			throw e;
		}
	}



	/**
	 * @param b2bCustomerModel
	 * @param selectedActiveB2bUnits
	 * @param deselectedActiveB2bUnits
	 */
	private void updateB2bUnitsDisabledList(final B2BCustomerModel b2bCustomerModel,
			final List<B2BUnitJson> selectedActiveB2bUnits, final List<B2BUnitJson> deselectedActiveB2bUnits)
	{
		final List<B2BUnitModel> modifiedB2bUnits = new ArrayList<B2BUnitModel>();
		for (final B2BUnitJson selectedB2bUnit : ListUtils.emptyIfNull(selectedActiveB2bUnits))
		{
			final B2BUnitModel b2bUnit = getB2BUnitForId(selectedB2bUnit.getCode());
			if (null != b2bUnit)
			{
				final List<String> disabledCubUsers = getDisabledUsers(b2bUnit);
				if (CollectionUtils.isNotEmpty(disabledCubUsers) && disabledCubUsers.contains(b2bCustomerModel.getUid()))
				{
					disabledCubUsers.remove(b2bCustomerModel.getUid());
				}
				b2bUnit.setCubDisabledUsers(disabledCubUsers);
				modifiedB2bUnits.add(b2bUnit);
			}
		}
		for (final B2BUnitJson deselectedB2bUnit : ListUtils.emptyIfNull(deselectedActiveB2bUnits))
		{
			final B2BUnitModel b2bUnit = getB2BUnitForId(deselectedB2bUnit.getCode());
			if (null != b2bUnit)
			{
				final List<String> disabledCubUsers = getDisabledUsers(b2bUnit);
				if (CollectionUtils.isEmpty(disabledCubUsers) || !disabledCubUsers.contains(b2bCustomerModel.getUid()))
				{
					disabledCubUsers.add(b2bCustomerModel.getUid());
				}
				b2bUnit.setCubDisabledUsers(disabledCubUsers);
				modifiedB2bUnits.add(b2bUnit);
			}
		}
		getModelService().saveAll(modifiedB2bUnits);

	}


	/**
	 * @param b2bUnit
	 * @return
	 */
	private List<String> getDisabledUsers(final B2BUnitModel b2bUnit)
	{
		return new ArrayList<String>(b2bUnit.getCubDisabledUsers());
	}


	/**
	 * @param allb2bUnitJsons
	 * @return List of selected B2bUnits
	 */
	private HashMap<String, List<B2BUnitJson>> getActiveB2BUnits(final List<B2BUnitJson> allb2bUnitJsons)
	{
		final List<B2BUnitJson> checkedB2bUnitJsons = Lists.newArrayList();
		final List<B2BUnitJson> unCheckedB2bUnitJsons = Lists.newArrayList();
		for (final B2BUnitJson b2bUnitJson : ListUtils.emptyIfNull(allb2bUnitJsons))
		{
			if (Boolean.TRUE.equals(b2bUnitJson.isActive()))
			{
				if (Boolean.TRUE.equals(b2bUnitJson.isSelected()))
				{
					checkedB2bUnitJsons.add(b2bUnitJson);
				}
				else
				{
					unCheckedB2bUnitJsons.add(b2bUnitJson);

				}

			}
		}
		final HashMap<String, List<B2BUnitJson>> b2bUnitSelectionMap = new HashMap<String, List<B2BUnitJson>>();
		b2bUnitSelectionMap.put(CHECKED, checkedB2bUnitJsons);
		b2bUnitSelectionMap.put(UNCHECKED, unCheckedB2bUnitJsons);
		return b2bUnitSelectionMap;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.customer.SABMCustomerFacade#getNextDeliveryDate()
	 */
	@Override
	public Date getNextDeliveryDateAndUpdateSession()
	{

		B2BUnitModel b2bUnit = null;
		try
		{
			b2bUnit = b2bCommerceUnitService.getParentUnit();
		}
		catch (final Exception e)
		{
			LOG.error("unable to get the b2bunit");
		}
		if (b2bUnit == null)
		{
			b2bUnit = getSessionService().getAttribute(SabmCoreConstants.SESSION_ATTR_B2B_UNIT);
		}

		if (b2bUnit != null)
		{
			final Date nextAvailableDayDelivery = sabmDeliveryDateCutOffService.getSafeNextAvailableDeliveryDate(b2bUnit);

			getSessionService().setAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE, nextAvailableDayDelivery);

			final Map<String, Object> deliveryPackType = getSabmDeliveryDateCutOffService().getDeliveryDatePackType(b2bUnit,
					nextAvailableDayDelivery);
			getSessionService().setAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE_PACKTYPE,
					deliveryPackType.get(PackType._TYPECODE));

			if (getCartService().hasSessionCart())
			{
				final CartModel cart = getCartService().getSessionCart();
				cart.setRequestedDeliveryDate(nextAvailableDayDelivery);

				final DeliveryModeType deliveryModeType = (DeliveryModeType) deliveryPackType.get(DeliveryModeType._TYPECODE);

				DeliveryModeModel deliveryMode = deliveryService
						.getDeliveryModeForCode(Config.getString(SabmCoreConstants.CART_DELIVERY_CUBARRANGED, ""));

				if (deliveryModeType.equals(DeliveryModeType.CUSTOMER_DELIVERY))
				{
					deliveryMode = deliveryService
							.getDeliveryModeForCode(Config.getString(SabmCoreConstants.CART_DELIVERY_CUSTOMERARRANGED, ""));
				}
				cart.setDeliveryMode(deliveryMode);
				try
				{
					getModelService().save(cart);
				}
				catch (final ModelSavingException e)
				{
					LOG.error("Error persisting request delivery date in cart: " + cart, e);
				}
			}

			return nextAvailableDayDelivery;
		}
		else
		{
			return null;
		}

	}

	/**
	 * This method will check is the unit have been changed. Will return true if the unit have changed e.g contain one of
	 * the below condition: 1. add new unit 2.remove new unit
	 *
	 * @param orignalCustomer
	 *           the orignal customer
	 * @param newCustomer
	 *           the new customer
	 * @return boolean
	 */
	protected boolean checkUnitChanged(final CustomerJson orignalCustomer, final CustomerJson newCustomer)
	{

		boolean isChanged = false;
		final List<String> orignalSelectedUnits = getSelectedB2BUnitCodes(orignalCustomer);
		final List<String> newSelectedUnits = getSelectedB2BUnitCodes(newCustomer);

		if (newSelectedUnits.size() != orignalSelectedUnits.size())
		{
			isChanged = true;
		}
		else if (CollectionUtils.isNotEmpty(newSelectedUnits))
		{
			for (final String b2bUnitJson : newSelectedUnits)
			{
				if (!orignalSelectedUnits.contains(b2bUnitJson))
				{
					isChanged = true;
					break;
				}
			}

		}
		return isChanged;
	}

	/**
	 * @param customerJson
	 * @return List<String>
	 */
	private List<String> getSelectedB2BUnitCodes(final CustomerJson customerJson)
	{
		final List<String> b2bUnitCodes = Lists.newArrayList();
		for (final RegionJson regionJson : ListUtils.emptyIfNull(customerJson.getStates()))
		{
			for (final B2BUnitJson b2bUnitJson : ListUtils.emptyIfNull(regionJson.getB2bunits()))
			{
				if (b2bUnitJson.isSelected())
				{
					b2bUnitCodes.add(b2bUnitJson.getCode());
				}
			}
		}
		return b2bUnitCodes;
	}

	/**
	 * @param b2bCustomerModel
	 * @param b2bUnitJsons
	 */
	private void setParentB2BUnit(final B2BCustomerModel b2bCustomerModel, final List<B2BUnitJson> b2bUnitJsons)
	{
		final B2BUnitModel parentB2BUnit = this.getB2BUnitForId(b2bUnitJsons.get(0).getCode());
		b2bUnitService.updateParentB2BUnit(parentB2BUnit, b2bCustomerModel);
		b2bUnitJsons.remove(0);
	}

	/**
	 * @param uid
	 * @param b2bUnitJsons
	 */
	private boolean checkDefaultB2BUnit(final String uid, final List<B2BUnitJson> b2bUnitJsons)
	{
		for (final B2BUnitJson b2bUnitJson : ListUtils.emptyIfNull(b2bUnitJsons))
		{
			if (uid.equals(b2bUnitJson.getCode()))
			{
				return true;
			}
		}
		return false;
	}


	/*
	 * * update the receiveUpdates
	 *
	 * @param receiveUpdates set the attribute receiveUpdates to CustomerModel
	 *
	 * @throws DuplicateUidException
	 */
	@Override
	public void updateReceiveUpdates_MobileNumber(final Boolean receiveUpdates, final Boolean receiveUpdatesForSms,
			final String mobileNumber, final String businessPhoneNumber) throws DuplicateUidException
	{
		final CustomerModel customer = getCurrentSessionCustomer();

		//Check if customer is B2BCustomer to avoid ClassCastException retrieving the B2BUnit
		if (customer instanceof B2BCustomerModel)
		{
			customer.setReceiveUpdates(receiveUpdates);
			customer.setReceiveUpdatesForSms(receiveUpdatesForSms);
			customer.setMobileContactNumber(mobileNumber);
			customer.setBusinessContactPhoneNumber(businessPhoneNumber);
			try
			{
				getModelService().save(customer);
			}
			catch (final ModelSavingException e)
			{
				if (e.getCause() instanceof InterceptorException
						&& ((InterceptorException) e.getCause()).getInterceptor().getClass().equals(UniqueAttributesInterceptor.class))
				{
					throw new DuplicateUidException(customer.getUid(), e);
				}
				throw e;
			}
			catch (final AmbiguousIdentifierException e)
			{
				throw new DuplicateUidException(customer.getUid(), e);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.customer.SABMCustomerFacade#getUsersByGroups()
	 */
	@Override
	public List<CustomerData> getUsersByUserId(final String registerEmail)
	{
		Assert.notNull(registerEmail, "registerEmail must not be null.");
		//find the userModel by userId
		final UserModel userModel;
		try
		{
			userModel = userService.getUserForUID(registerEmail);
		}
		catch (final UnknownIdentifierException ex)
		{
			LOG.warn("Cannot find user with uid [{}]'", registerEmail);
			return Collections.emptyList();
		}

		if (null != userModel && userModel instanceof B2BCustomerModel)
		{
			//find the list B2BCustomerModel by UserModel
			final List<B2BCustomerModel> customerModels = sabmB2BCustomerService.getUsersByGroups((B2BCustomerModel) userModel);
			return Converters.convertAll(customerModels, getCustomerConverter());
		}
		LOG.debug("No B2BCustomerModel found for registerEmail {} , returning empty ", registerEmail);
		return Collections.emptyList();
	}



	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.customer.SABMCustomerFacade#sendWelcomeEmailMessage(java.lang.String)
	 */
	@Override
	public void sendWelcomeEmailMessage(final String uId)
	{
		//final B2BCustomerModel customerModel = getB2bCommerceUnitService().getCustomerForUid(StringUtils.lowerCase(uId));
		final B2BCustomerModel customerModel = (B2BCustomerModel) b2bCustomerService.getUserForUID(StringUtils.lowerCase(uId));
		customerModel.setOnboardWithWelcomeEmail(Boolean.TRUE);
		customerModel.setWelcomeEmailStatus(Boolean.FALSE);

		try
		{
			this.internalSaveCustomer(customerModel);
		}
		catch (final DuplicateUidException e)
		{
			LOG.error("Save customer fail. the customer:" + uId, e);
		}

	}

	@Override
	public void changeB2BUnit(final String b2bUnitId)
	{
		final B2BUnitModel currentB2BUnit = b2bCommerceUnitService.getParentUnit();
		//Remove the if Condition For Create/Edit Page
		LOG.debug("B2BUnit has been changed to {}. Original b2bunit was {} ", b2bUnitId, currentB2BUnit);

		//Set the currently selected B2BUnit in the session
		final B2BUnitModel b2bUnitModel = b2bUnitService.getUnitForUid(b2bUnitId);
		getSessionService().setAttribute(SabmCoreConstants.SESSION_ATTR_B2B_UNIT, b2bUnitModel);

		//set default b2bunit when changed b2bunit if remember previous business unit flag is true
		if (b2bUnitModel != null)
		{
			b2bUnitService.updateDefaultCustomerUnit(b2bUnitId, userService.getCurrentUser().getUid());

			//Refresh CUP and Deals as these are tied to the B2BUnit and needs to be refreshed on B2BUnit selection
			refreshCoreEntities();

			//Clear the session cart
			getSessionService().removeAttribute(SabmCoreConstants.SESSION_ATTR_CART);

			//Refresh Smart Orders
			refreshSmartOrders();

			//Finally restore cart for the selected B2BUnit
			try
			{
				cartFacade.restoreSavedCart(null);
			}
			catch (final CommerceCartRestorationException e)
			{
				LOG.error("Error occured while restoring cart. ", e);
			}
			calculateDeliveryDate();
		}
	}

	/**
	 *
	 */
	private void refreshSmartOrders()
	{
		getSessionService().removeAttribute(SabmCoreConstants.SESSION_ATTR_SMARTORDERJSON);
		getSessionService().removeAttribute(SabmCoreConstants.SESSION_ATTR_SMARTORDERPRODUCTCODES);
		getSessionService().removeAttribute(SabmCoreConstants.SESSION_ATTR_PREVIOUSSMARTORDERDATE);
		getSessionService().removeAttribute(SabmCoreConstants.SESSION_ATTR_NEXTSMARTORDERDATE);
	}

	public List<Long> getDeliveryDates(final boolean validDates)
	{
		final List<Long> timestamps = new ArrayList<>();

		Set<Date> dates = null;
		if (validDates)
		{
			dates = sabmDeliveryDateCutOffService.enabledCalendarDates();
		}
		else
		{
			dates = sabmDeliveryDateCutOffService.disabledCalendarDates();
		}

		if (CollectionUtils.isNotEmpty(dates))
		{
			for (final Date date : dates)
			{
				timestamps.add(date.getTime());
			}
		}

		return timestamps;
	}

	@Override
	public List<Long> addPublicHolidayData(final B2BUnitModel b2bUnit)
	{
		if (Objects.nonNull(b2bUnit) && Objects.nonNull(b2bUnit.getPlant())
				&& Objects.nonNull(b2bUnit.getPlant().getHolidayCalendar())
				&& Objects.nonNull(b2bUnit.getPlant().getHolidayCalendar().getPublicHolidays()))
		{
			final List<PublicHolidayModel> publicHolidays = b2bUnit.getPlant().getHolidayCalendar().getPublicHolidays();
			if (CollectionUtils.isNotEmpty(publicHolidays))
			{
				final Set<Date> holidays = publicHolidays.stream().map(holiday -> holiday.getHolidayDate())
						.collect(Collectors.toSet());
				return SabmDateUtils.getLongDates(holidays);
			}

		}
		return new ArrayList<>();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.customer.SABMCustomerFacade#createUserAccessHistory(com.sabmiller.facades.customer.
	 * SABMUserAccessHistoryData)
	 */
	@Override
	public void createUserAccessHistory(final SABMUserAccessHistoryData userAccessHistoryData)
	{

		Assert.hasText(userAccessHistoryData.getUid(), "The field [Uid] cannot be empty");

		final SABMUserAccessHistoryModel userAccessHistoryModel = getModelService().create(SABMUserAccessHistoryModel.class);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("creating new user access history for user: " + userAccessHistoryData.getUid() + ", public IP address: "
					+ userAccessHistoryData.getPublicIPAddress() + ", user agent: " + userAccessHistoryData.getUserAgent()
					+ ", remember me enabled: " + userAccessHistoryData.getRememberMeEnabled());
		}
		//Populating the new model from the parameter DataBean.
		userAccessHistoryPopulator.populate(userAccessHistoryData, userAccessHistoryModel);

		getModelService().save(userAccessHistoryModel);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.customer.SABMCustomerFacade#isDealRefreshInProgress(de.hybris.platform.b2b.model.
	 * B2BUnitModel )
	 */
	@Override
	public boolean isDealRefreshInProgress()
	{
		return b2bUnitService.isDealRefreshInProgress(b2bCommerceUnitService.getParentUnit());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.facades.customer.SABMCustomerFacade#isCupRefreshInProgress(de.hybris.platform.b2b.model.B2BUnitModel
	 * )
	 */
	@Override
	public boolean isCupRefreshInProgress()
	{
		/*
		 * final UserModel userModel = userService.getCurrentUser(); if (userModel instanceof B2BCustomerModel) { return
		 * b2bUnitService.isCupRefreshInProgress(b2bCommerceUnitService.getParentUnit()); }
		 */
		return false;
	}

	@Override
	public boolean isProductExclRefreshInProgress()
	{
		return b2bUnitService.isProductExclRefreshInProgress(b2bCommerceUnitService.getParentUnit());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.customer.SABMCustomerFacade#turnOffImpersonation(de.hybris.platform.b2b.model.
	 * B2BCustomerModel )
	 */
	@Override
	public void turnOffImpersonation()
	{
		final UserModel userModel = userService.getCurrentUser();
		if (userModel instanceof B2BCustomerModel)
		{
			b2bUnitService.turnOffImpersonation((B2BCustomerModel) userModel);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.customer.SABMCustomerFacade#turnBackImpersonation()
	 */
	@Override
	public void turnBackImpersonation()
	{
		b2bUnitService.turnBackImpersonation();
	}


	/**
	 * Get the uids for update profile
	 *
	 * @param units
	 *           the b2b unit ids
	 * @return List<String> the uids
	 *
	 */
	@Override
	public List<CustomerData> getUserForUpdateProfile(final List<String> units)
	{
		final List<String> andUids = new ArrayList<>();
		andUids.add("b2badmingroup");
		andUids.add("b2bassistantgroup");
		final List<B2BCustomerModel> customers = sabmB2BCustomerService.getCustomerForUpdateProfile(units, andUids);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Could Update Profile Customers: {}", customers);
		}

		// If the current user is not a primary admin (non-ZADP level user) AND has the permission to create/edit other users,
		// then the current user's own details are to be excluded from the user list on the personal details page.
		final List<B2BCustomerModel> usersForUpdate = getValidUserPermissionList(customers);

		return Converters.convertAll(usersForUpdate, getCustomerConverter());
	}

	/**
	 * If the current user is not a primary admin (non-ZADP level user) AND has the permission to create/edit other
	 * users, then the current user's own details are to be excluded from the user list on the personal details page.
	 *
	 * @param customers
	 * @return list of allowed users
	 */
	private List<B2BCustomerModel> getValidUserPermissionList(final List<B2BCustomerModel> customers)
	{
		final List<B2BCustomerModel> usersForUpdate = new ArrayList<B2BCustomerModel>();
		usersForUpdate.addAll(customers);

		final CustomerData currentCustomer = getCurrentCustomer();

		if (BooleanUtils.isNotTrue(currentCustomer.getPrimaryAdmin()) && BooleanUtils.isNotTrue(currentCustomer.getIsZadp())
				&& CollectionUtils.isNotEmpty(usersForUpdate))
		{
			for (final Iterator<B2BCustomerModel> iterator = usersForUpdate.iterator(); iterator.hasNext();)
			{
				final B2BCustomerModel b2bCustomerModel = iterator.next();

				if (StringUtils.isNotEmpty(b2bCustomerModel.getUid()) && StringUtils.isNotEmpty(currentCustomer.getUid())
						&& b2bCustomerModel.getUid().equals(currentCustomer.getUid()))
				{
					iterator.remove();
				}
			}
		}

		return usersForUpdate;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.customer.SABMCustomerFacade#updateDefaultCustomerUnit(java.lang.String)
	 */
	@Override
	public void updateDefaultCustomerUnit(final String unitId)
	{
		b2bUnitService.updateDefaultCustomerUnit(unitId);

	}



	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.customer.SABMCustomerFacade#removeCustomerFromB2bUnit(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public boolean removeCustomerFromB2bUnit(final String unitId, final String customerId)
	{
		boolean isSuccess = false;
		B2BCustomerModel customer = null;
		try
		{
			/*
			 * Remove the default business unit. SABMC-1543 Added by Ross.
			 */
			final UserModel user = getUserService().getUserForUID(customerId);
			customer = (B2BCustomerModel) user;
			if (customer.getDefaultB2BUnit() != null && unitId.equals(customer.getDefaultB2BUnit().getUid()))
			{
				setDefaultB2BUnit(unitId, customer);


			}
			getModelService().save(customer);

			isSuccess = b2bUnitService.removeCustomerFromUnit(unitId, customerId);
		}
		catch (final Exception e)
		{
			LOG.error("can not remove this customer: " + customerId + " from unit '" + unitId + "'", e);
		}

		if (customer != null && isSuccess)
		{
			sendCustomerProfileUpdatedNoticeEmail(customer, getUserService().getCurrentUser());
		}

		return isSuccess;
	}


	/**
	 * @param unitId
	 * @param customer
	 */
	private void setDefaultB2BUnit(final String unitId, final B2BCustomerModel customer)
	{
		final Set<PrincipalGroupModel> principalGroups = customer.getGroups();
		for (final PrincipalGroupModel principalGroup : SetUtils.emptyIfNull(principalGroups))
		{
			if (principalGroup instanceof AsahiB2BUnitModel)
			{
				continue;
			}

			if (principalGroup instanceof B2BUnitModel)
			{
				final B2BUnitModel b2bUnit = (B2BUnitModel) principalGroup;

				if (BooleanUtils.isTrue(b2bUnit.getActive()) && !unitId.equals(b2bUnit.getUid())
						&& (CollectionUtils.isEmpty(b2bUnit.getCubDisabledUsers())
								|| !(b2bUnit.getCubDisabledUsers().contains(customer.getUid()))))
				{
					customer.setDefaultB2BUnit(b2bUnit);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.customer.SABMCustomerFacade#getCurrentCustomerForZadp()
	 */
	@Override
	public CustomerJson getCurrentCustomerJsonStates()
	{
		final CustomerModel customer = (CustomerModel) getUserService().getCurrentUser();
		final CustomerJson customerJson = new CustomerJson();
		sabmCustomerStatesPopulator.populate(customer, customerJson);
		return customerJson;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.customer.SABMCustomerFacade#getCustomerJsonByUid(java.lang.String)
	 */
	@Override
	public CustomerJson getCustomerJsonByUid(final String uid)
	{
		CustomerModel customerModel = null;
		try
		{
			//customerModel = b2bCommerceUnitService.getCustomerForUid(StringUtils.lowerCase(uid));
			customerModel = (CustomerModel) b2bCustomerService.getUserForUID(StringUtils.lowerCase(uid));
		}
		catch (final Exception e)
		{
			LOG.error("Can not find this user ,the UID:" + uid, e);
		}
		return customerJsonConverter.convert(customerModel);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.customer.SABMCustomerFacade#getCustomerJsonByUid(java.lang.String)
	 */
	@Override
	public CustomerJson isExistUserMail(final String uid)
	{
		CustomerModel customerModel = null;
		try
		{
			//customerModel = b2bCommerceUnitService.getCustomerForUid(StringUtils.lowerCase(uid));
			customerModel = (CustomerModel) b2bCustomerService.getUserForUID(StringUtils.lowerCase(uid));
		}
		catch (final Exception e)
		{
			LOG.error("Can not find this user ,the UID:" + uid, e);
			return customerJsonConverter.convert(customerModel);
		}
		if (null != customerModel && !sabmB2BCustomerService.isRegistrationAllowed(customerModel, null))
		{
			return customerJsonConverter.convert(customerModel);
		}
		else
		{
			return customerJsonConverter.convert(null);
		}
	}

	protected void postLoginSuccess()
	{
		// SAB-574
		//set OrderSimulationStatus to need calulation status, which will be used to call order simulate
		if (getCartService().hasSessionCart())
		{ // IF ADDED BY MB
			final CartModel sessionCart = getCartService().getSessionCart();
			sessionCart.setOrderSimulationStatus(OrderSimulationStatus.NEED_CALCULATION);

			getModelService().save(sessionCart);
			getCartService().setSessionCart(sessionCart);
			getModelService().refresh(sessionCart);
			if (LOG.isDebugEnabled())
			{
				LOG.info("Update OrderSimulationStatus in cartModel in loginSuccess "
						+ (getCartService().getSessionCart().getOrderSimulationStatus() == null));
			}
		}

		calculateDeliveryDate();
		refreshCoreEntities();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.customer.SABMCustomerFacade#getB2BUnitForId(de.hybris.platform.b2b.model.B2BUnitModel)
	 */

	@Override
	public B2BUnitData getB2BUnitForUnitModel(final B2BUnitModel b2BUnitModel)
	{
		return b2BUnitConverter.convert(b2BUnitModel);
	}

	/**
	 * @return the sabmCustomerStatesPopulator
	 */
	public Populator<CustomerModel, CustomerJson> getSabmCustomerStatesPopulator()
	{
		return sabmCustomerStatesPopulator;
	}

	/**
	 * @param sabmCustomerStatesPopulator
	 *           the sabmCustomerStatesPopulator to set
	 */
	public void setSabmCustomerStatesPopulator(final Populator<CustomerModel, CustomerJson> sabmCustomerStatesPopulator)
	{
		this.sabmCustomerStatesPopulator = sabmCustomerStatesPopulator;
	}

	/**
	 * @return the customerJsonConverter
	 */
	public Converter<CustomerModel, CustomerJson> getCustomerJsonConverter()
	{
		return customerJsonConverter;
	}

	/**
	 * @param customerJsonConverter
	 *           the customerJsonConverter to set
	 */
	public void setCustomerJsonConverter(final Converter<CustomerModel, CustomerJson> customerJsonConverter)
	{
		this.customerJsonConverter = customerJsonConverter;
	}

	/**
	 * @return the customerConverter
	 */
	@Override
	public Converter<UserModel, CustomerData> getCustomerConverter()
	{
		return customerConverter;
	}

	/**
	 * @param customerConverter
	 *           the customerConverter to set
	 */
	@Override
	public void setCustomerConverter(final Converter<UserModel, CustomerData> customerConverter)
	{
		this.customerConverter = customerConverter;
	}

	/**
	 * @return the b2bCommerceUnitService
	 */
	public B2BCommerceUnitService getB2bCommerceUnitService()
	{
		return b2bCommerceUnitService;
	}


	/**
	 * @param b2bCommerceUnitService
	 *           the b2bCommerceUnitService to set
	 */
	public void setB2bCommerceUnitService(final B2BCommerceUnitService b2bCommerceUnitService)
	{
		this.b2bCommerceUnitService = b2bCommerceUnitService;
	}

	/**
	 * @return the customerGroupFacade
	 */
	public CustomerGroupFacade getCustomerGroupFacade()
	{
		return customerGroupFacade;
	}

	/**
	 * @param customerGroupFacade
	 *           the customerGroupFacade to set
	 */
	public void setCustomerGroupFacade(final CustomerGroupFacade customerGroupFacade)
	{
		this.customerGroupFacade = customerGroupFacade;
	}

	/**
	 * @return the b2bUnitService
	 */
	public SabmB2BUnitService getB2bUnitService()
	{
		return b2bUnitService;
	}

	/**
	 * @param b2bUnitService
	 *           the b2bUnitService to set
	 */
	public void setB2bUnitService(final SabmB2BUnitService b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

	/**
	 * @return the sabmDeliveryDateCutOffService
	 */
	public SABMDeliveryDateCutOffService getSabmDeliveryDateCutOffService()
	{
		return sabmDeliveryDateCutOffService;
	}

	/**
	 * @param sabmDeliveryDateCutOffService
	 *           the sabmDeliveryDateCutOffService to set
	 */
	public void setSabmDeliveryDateCutOffService(final SABMDeliveryDateCutOffService sabmDeliveryDateCutOffService)
	{
		this.sabmDeliveryDateCutOffService = sabmDeliveryDateCutOffService;
	}

	/**
	 * @return the userService
	 */
	@Override
	public UserService getUserService()
	{
		return userService;
	}

	/**
	 * @param userService
	 *           the userService to set
	 */
	@Override
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * @return the dealsService
	 */
	public DealsService getDealsService()
	{
		return dealsService;
	}

	/**
	 *
	 * @param uid
	 *
	 *           the user who need to be delete
	 *
	 * @return CustomerData, the user need to be delete
	 */
	@Override
	public CustomerData deleteUser(final String uid)
	{
		final UserModel user = getUserForUid(uid);
		/*
		 * final List<B2BCustomerModel> similarCustomers = sabmB2BCustomerService .getSimilarB2BCustomer(uid +
		 * DELETE_UID_PRE_FIX + "%"); final int uidIndex = CollectionUtils.isEmpty(similarCustomers) ? 1 :
		 * similarCustomers.size() + 1;
		 *
		 * final String newUid = uid + DELETE_UID_PRE_FIX + uidIndex + DELETE_UID_END_FIX;
		 */
		if (user instanceof B2BCustomerModel)
		{
			final CustomerData customer = customerConverter.convert(user);
			final B2BCustomerModel deletedCustomer = sabmB2BCustomerService.deleteCustomer((B2BCustomerModel) user);
			if (deletedCustomer != null)
			{
				return customer;
			}
		}

		return null;
	}

	/**
	 * @param uid
	 *
	 *           the uid used to find user
	 *
	 * @return UserModel, the user have been find
	 */
	protected UserModel getUserForUid(final String uid)
	{
		Assert.hasText(uid, "The field [uid] cannot be empty");

		UserModel user = null;
		try
		{
			user = userService.getUserForUID(uid);
		}
		catch (final UnknownIdentifierException e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Can not get user:" + uid, e);
			}
			return null;
		}
		return user;
	}

	/**
	 * @param dealsService
	 *           the dealsService to set
	 */
	public void setDealsService(final DealsService dealsService)
	{
		this.dealsService = dealsService;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.customer.SABMCustomerFacade#getPrimaryAdminStatus(java.lang.String)
	 */
	@Override
	public String getPrimaryAdminStatus(final String uid)
	{
		return b2bUnitService.findPrimaryAdminStatus(uid);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.customer.SABMCustomerFacade#enabledCalendarDates()
	 */
	@Override
	public Set<Date> enabledCalendarDates()
	{
		return sabmDeliveryDateCutOffService.enabledCalendarDates();
	}

	/*
	 * (non-Javadoc)
	 *
	 *
	 *
	 * @see com.sabmiller.facades.customer.SABMCustomerFacade#updateB2BUnitStatus(java.lang.String, boolean, boolean)
	 */
	@Override
	public void updateB2BUnitStatus(final String token, final boolean sendMail, final boolean setPassword)
	{
		//decrypt the token
		final SecureToken data = secureTokenService.decryptData(token);
		// update the status
		b2bUnitService.updateB2BUnitStatus(userService.getUserForUID(data.getData(), CustomerModel.class), sendMail, setPassword);
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.customer.SABMCustomerFacade#isEmployeeUser()
	 */
	@Override
	public boolean isEmployeeUser(final UserModel userModel)
	{
		if (userModel instanceof EmployeeModel)
		{
			final UserGroupModel userGroup = userService.getUserGroupForUID(SabmFacadesConstants.B2BAPPROVERGROUP);
			return userService.isMemberOfGroup(userModel, userGroup);
		}
		return false;
	}

	@Override
	public BDECustomerModel getOrCreateBDECustomer(final String b2bUnitId, final String emailAddress)
	{
		BDECustomerModel bdeCustomer = null;
		B2BUnitModel defaultB2BUnit = null;

		final B2BUnitModel b2bUnitModel = b2bUnitService.getUnitForUid(b2bUnitId);



		//For ALB Staff Portal

		if (b2bUnitModel instanceof AsahiB2BUnitModel)
		{
			bdeCustomer = getOrCreateAsahiBDECustomer(b2bUnitModel, emailAddress, bdeCustomer);
		}
		else
		{
			if (isTopLevelB2BUnit(b2bUnitModel))
			{
				defaultB2BUnit = b2bUnitModel;
			}
			else if (isBranch(b2bUnitModel))
			{
				defaultB2BUnit = b2bUnitService.findTopLevelB2BUnit(b2bUnitModel.getPayerId());
			}
			if (null != defaultB2BUnit)
			{
				final String zadpUid = defaultB2BUnit.getUid();

				final String uid = EMAIL_PREFIX + zadpUid.replaceAll(REGEX_WHITE_SPACE, StringUtils.EMPTY) + EMAIL_SUFFIX;

				bdeCustomer = sabmB2BCustomerService.getBDECustomer(uid);

				final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>();
				if (bdeCustomer == null)
				{
					bdeCustomer = getModelService().create(BDECustomerModel.class);
					bdeCustomer.setUid(uid);
					if (StringUtils.isEmpty(emailAddress))
					{
						bdeCustomer.setEmail(uid);
					}
					else
					{
						bdeCustomer.setEmail(emailAddress);
					}
				}

				/*
				 * update existing bdeCustomer's email address if it is different
				 */

				if (StringUtils.isNotEmpty(emailAddress) && !emailAddress.equalsIgnoreCase(bdeCustomer.getEmail()))
				{
					bdeCustomer.setEmail(emailAddress);

				}


				bdeCustomer.setName(getUserService().getCurrentUser().getName());
				bdeCustomer.setFirstName(getUserService().getCurrentUser().getName());
				groups.add(defaultB2BUnit);
				for (final PrincipalModel member : SetUtils.emptyIfNull(defaultB2BUnit.getMembers()))
				{
					if (member instanceof B2BUnitModel)
					{
						groups.add((B2BUnitModel) member);
					}
				}

				groups.add(getUserService().getUserGroupForUID(DEFAULT_BDEVIEWONLY_USERGROUP));
				bdeCustomer.setGroups(groups);
				bdeCustomer.setDefaultB2BUnit(b2bUnitModel);

				final String password = generateComplexPassword();
				getUserService().setPassword(bdeCustomer, password, DEFAULT_PASSWORD_ENCODING);
				getModelService().save(bdeCustomer);
				getModelService().refresh(bdeCustomer);
			}
		}
		return bdeCustomer;
	}



	protected String generateComplexPassword()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(UPPERCASE_LETTERS).append(LOWERCASE_LETTERS).append(NUMBERS).append(SYMBOLS);

		final char[] characterSet = sb.toString().toCharArray();

		final Random random = new SecureRandom();
		final char[] result = new char[PASSWORD_LENGTH];
		for (int i = 0; i < result.length; i++)
		{
			// picks a random index out of character set > random character
			final int randomCharIndex = random.nextInt(characterSet.length);
			result[i] = characterSet[randomCharIndex];
		}
		return new String(result);
	}

	protected boolean isTopLevelB2BUnit(final B2BUnitModel b2bUnitModel)
	{
		return b2bUnitModel != null && SabmCoreConstants.ZADP.equals(b2bUnitModel.getAccountGroup());
	}

	protected boolean isBranch(final B2BUnitModel b2bUnitModel)
	{
		return b2bUnitModel != null && SabmCoreConstants.ZALB.equals(b2bUnitModel.getAccountGroup());
	}

	@Override
	public String getNewSecureToken(final String uid)
	{
		final UserModel userForUID = getUserService().getUserForUID(uid);
		final long timeStamp = getTokenValiditySeconds() > 0L ? new Date().getTime() : 0L;
		final SecureToken data = new SecureToken(uid, timeStamp);
		final String token = secureTokenService.encryptData(data);
		if (userForUID instanceof BDECustomerModel)
		{
			((BDECustomerModel) userForUID).setToken(token);
		}
		else if (userForUID instanceof EmployeeModel)
		{
			((EmployeeModel) userForUID).setToken(token);
		}
		else
		{
			return null;
		}
		getModelService().save(userForUID);
		return token;
	}

	@Override
	public UserModel validateSecureToken(final String token) throws TokenInvalidatedException
	{
		final SecureToken data = secureTokenService.decryptData(token);
		if (getTokenValiditySeconds() > 0L)
		{
			final long delta = new Date().getTime() - data.getTimeStamp();
			if (delta / 1000 > getTokenValiditySeconds())
			{
				throw new IllegalArgumentException("token expired");
			}
		}
		final UserModel userForUID = getUserService().getUserForUID(data.getData());
		if (userForUID == null)
		{
			throw new IllegalArgumentException("user for token not found");
		}
		if (userForUID instanceof BDECustomerModel && !token.equals(((BDECustomerModel) userForUID).getToken()))
		{
			throw new TokenInvalidatedException();
		}
		else if (userForUID instanceof EmployeeModel && !token.equals(((EmployeeModel) userForUID).getToken()))
		{
			throw new TokenInvalidatedException();
		}
		return userForUID;
	}

	/**
	 * @return the tokenValiditySeconds
	 */
	protected long getTokenValiditySeconds()
	{
		return tokenValiditySeconds;
	}

	/**
	 * @param tokenValiditySeconds
	 *           the tokenValiditySeconds to set
	 */
	public void setTokenValiditySeconds(final long tokenValiditySeconds)
	{
		if (tokenValiditySeconds < 0)
		{
			throw new IllegalArgumentException("tokenValiditySeconds has to be >= 0");
		}
		this.tokenValiditySeconds = tokenValiditySeconds;
	}


	/**
	 * send the customer profile updated notice email
	 *
	 * @param customer
	 *           the customer
	 * @param fromUser
	 *           the admin
	 */
	@Override
	public void sendCustomerProfileUpdatedNoticeEmail(final CustomerModel customer, final UserModel fromUser)
	{
		eventService.publishEvent(initializeProfileUpdatedNoticeEvent(new ProfileUpdatedNoticeEvent(fromUser), customer));
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
		event.setBaseStore(baseStoreService.getBaseStoreForUid("sabmStore"));
		event.setSite(baseSiteService.getBaseSiteForUID("sabmStore"));
		event.setLanguage(commonI18NService.getLanguage("en"));
		event.setCurrency(commonI18NService.getCurrency("AUD"));
		event.setCustomer(customer);
		return event;
	}

	@Override
	public boolean sendRegistrationRequestsMessage(final RegistrationRequestForm form)
	{
		final RegistrationRequestModel registrationRequestModel = getModelService().create(RegistrationRequestModel.class);
		registrationRequestModel.setFirstName(form.getFirstName());
		registrationRequestModel.setLastName(form.getLastName());
		registrationRequestModel.setEmail(form.getEmail());
		registrationRequestModel.setCubAccount(form.getCubAccount());
		registrationRequestModel.setAccountName(form.getAccountName());
		registrationRequestModel.setWorkPhoneNum(form.getWorkPhoneNum());
		registrationRequestModel.setMobilePhoneNum(form.getMobilePhoneNum());

		registrationRequestModel.setAccountType(form.getAccessType());
		registrationRequestModel.setAccessRequired(form.getAccessType());

		registrationRequestModel.setHaveMoreAccount(BooleanUtils.isTrue(form.getHaveMoreAccount()));

		try
		{
			getModelService().save(registrationRequestModel);
			//get the EmailAddressModel
			final EmailAddressModel toEmailAddressModel = emailService.getOrCreateEmailAddressForEmail(
					Config.getString("email.registration.request.to.email", "b2boperations@cub.com.au"),
					EMAIL_REGISTRATION_REQUEST_DISPLAY);
			final EmailAddressModel formEmailAddressModel = emailService.getOrCreateEmailAddressForEmail(emailConfig.getMailFrom(),
					EMAIL_REGISTRATION_REQUEST_DISPLAY);
			final List<EmailAddressModel> toEmailAddressModels = new ArrayList<EmailAddressModel>();
			toEmailAddressModels.add(toEmailAddressModel);
			final EmailMessageModel emailMessageModel = emailService.createEmailMessage(toEmailAddressModels, null, null,
					formEmailAddressModel, formEmailAddressModel.getEmailAddress(), EMAIL_REGISTRATION_REQUEST_DISPLAY,
					buildRegistrationRequestsEmailBody(form), null);
			// send the email
			emailService.send(emailMessageModel);
		}
		catch (final Exception e)
		{
			LOG.error("Failed to send change password mail [{}] ", getCurrentSessionCustomer(), e);
			return false;
		}

		return true;
	}

	protected String buildRegistrationRequestsEmailBody(final RegistrationRequestForm form)
	{
		final StringBuilder body = new StringBuilder();
		body.append("Below are the details of the registration request form submitted:<br>");
		body.append("First name: 						" + form.getFirstName() + "<br>");
		body.append("Last name:  						" + form.getLastName() + "<br>");
		body.append("Email address: 					" + form.getEmail() + "<br>");
		if (StringUtils.isNotBlank(form.getCubAccount()))
		{
			body.append("CUB Account: 						" + form.getCubAccount() + "<br>");
		}
		body.append("Account name:   					" + form.getAccountName() + "<br>");
		body.append("Work phone number: 				" + form.getWorkPhoneNum() + "<br>");
		if (StringUtils.isNotBlank(form.getMobilePhoneNum()))
		{
			body.append("Mobile phone number: 			" + form.getMobilePhoneNum() + "<br>");
		}
		body.append("Are you: 					  		" + form.getAccoutType() + "<br>");
		body.append("Type of access required: 		" + form.getAccessType() + "<br>");
		body.append("Have more than one account: 	");
		if (BooleanUtils.isTrue(form.getHaveMoreAccount()))
		{
			body.append("Yes <br>");
		}
		else
		{
			body.append("No <br>");
		}

		return body.toString();
	}

	@Override
	public List<NotificationData> getUnreadSiteNotification()
	{
		final UserModel user = userService.getCurrentUser();
		return sabmB2BCustomerService.getUnreadSiteNotification(user);
	}

	@Override
	public void markSiteNotificationAsRead(final String messageCode)
	{
		final UserModel user = userService.getCurrentUser();

		sabmB2BCustomerService.markSiteNotificationAsRead(user.getUid(), messageCode);

	}

	@Override
	public B2bDeliveryDatesConfig getDeliveryDatesConfig(final B2BUnitModel b2bUnit)
	{
		final B2bDeliveryDatesConfig b2bDeliveryDatesConfig = getDefaultDeliveryDateConfig(b2bUnit);

		updateDeliveryDateConfigFromCart(b2bDeliveryDatesConfig);


		return b2bDeliveryDatesConfig;
	}

	/**
	 * This method will check is the customer edited and need to send the notice email Will return true if contain one of
	 * the below condition: 1.Addition of Permissions (but not removal) 2.Changes to assigned business units 3.Changing
	 * the status of the account to 'Activated' (but not if the account is changed to 'Deactivated')
	 *
	 * @param b2bCustomerModel
	 *           the orignal customer
	 * @param newCustomer
	 *           the edited result
	 * @return boolean
	 */
	protected boolean isNeedSendNoticeEmail(final B2BCustomerModel b2bCustomerModel, final CustomerJson newCustomer)
	{
		if (b2bCustomerModel == null)
		{
			return false;
		}
		final CustomerJson orignalCustomer = customerJsonConverter.convert(b2bCustomerModel);

		// validate is activated
		if (!orignalCustomer.isActive() && newCustomer.isActive())
		{
			return true;
		}

		// validate is change both the first name and last name
		if (StringUtils.isNotEmpty(newCustomer.getFirstName()) && StringUtils.isNotEmpty(newCustomer.getSurName())
				&& !newCustomer.getFirstName().equals(orignalCustomer.getFirstName())
				&& !newCustomer.getSurName().equals(orignalCustomer.getSurName()))
		{
			return true;
		}

		//Validate is addition of permissions
		if ((!orignalCustomer.getPermissions().isOrders() && newCustomer.getPermissions().isOrders())
				|| (!orignalCustomer.getPermissions().isPa() && newCustomer.getPermissions().isPa())
				|| (!orignalCustomer.getPermissions().isPay() && newCustomer.getPermissions().isPay()))
		{
			return true;
		}

		//validate is change to assigned business unit
		return checkUnitChanged(orignalCustomer, newCustomer);

	}

	/**
	 * @param b2bUnit
	 * @return
	 */
	@Cacheable(value = "deliveryDateConfig", key = "T(com.sabmiller.cache.impl.SabmCacheKeyGenerator).generateKey(false,false,false,'DeliveryDateConfig',#b2bUnit.uid)")
	private B2bDeliveryDatesConfig getDefaultDeliveryDateConfig(B2BUnitModel b2bUnit)
	{

		final B2BUnitModel b2bUnitForAlternativeAddress = getUnitBasedForAlternativeAddress();
		b2bUnit = null != b2bUnitForAlternativeAddress ? b2bUnitForAlternativeAddress : b2bUnit;
		final B2bDeliveryDatesConfig b2bDeliveryDatesConfig = new B2bDeliveryDatesConfig();

		final List<DeliveryModePackTypeDeliveryDatesData> deliveryDatesData = sabmDeliveryDateCutOffService
				.getDeliveryModePackTypeDeliveryDatesData(b2bUnit, true);

		b2bDeliveryDatesConfig.setDeliveryDatesData(deliveryDatesData);

		b2bDeliveryDatesConfig.setCubArrangedEnabled(getEnabledDeliveryMode(deliveryDatesData, DeliveryModeType.CUB_DELIVERY));
		b2bDeliveryDatesConfig
				.setCustomerArrangedEnabled(getEnabledDeliveryMode(deliveryDatesData, DeliveryModeType.CUSTOMER_DELIVERY));

		final ShippingCarrierModel defaultCarrier = b2bUnit.getDefaultCarrier();

		if (defaultCarrier != null)
		{
			b2bDeliveryDatesConfig.setCustomerOwned(defaultCarrier.getCustomerOwned());
		}

		final List<ShippingCarrier> shippingCarriers = getCustomerOwnedShippingCarriers(b2bUnit);

		if (CollectionUtils.isNotEmpty(shippingCarriers))
		{
			Collections.sort(shippingCarriers, shippingCarrierComparator);
			b2bDeliveryDatesConfig.setShippingCarriers(shippingCarriers);
			b2bDeliveryDatesConfig.setSelectedCarrier(shippingCarriers.get(0));
		}

		return b2bDeliveryDatesConfig;
	}

	private B2BUnitModel getUnitBasedForAlternativeAddress()
	{
		B2BUnitModel b2bUnitModel = null;
		if (getCartService().hasSessionCart())
		{
			final CartModel cartModel = getCartService().getSessionCart();
			if (null != cartModel && null != cartModel.getDeliveryAddress()
					&& null != cartModel.getDeliveryAddress().getPartnerNumber())
			{
				b2bUnitModel = b2bUnitService.getUnitForUid(cartModel.getDeliveryAddress().getPartnerNumber());
			}
		}

		return b2bUnitModel;
	}

	/**
	 * @param b2bDeliveryDatesConfig
	 */
	private void updateDeliveryDateConfigFromCart(final B2bDeliveryDatesConfig b2bDeliveryDatesConfig)
	{
		if (getCartService().hasSessionCart())
		{
			final CartModel sessionCart = getCartService().getSessionCart();
			boolean isCustomerArranged = false;

			if (sessionCart != null && sessionCart.getDeliveryMode() != null
					&& StringUtils.equalsIgnoreCase(sessionCart.getDeliveryMode().getCode(),
							siteConfigService.getString(SabmCoreConstants.CART_DELIVERY_CUSTOMERARRANGED, "")))
			{
				isCustomerArranged = true;
			}
			b2bDeliveryDatesConfig.setCustomerOwned(isCustomerArranged);
			if (null != sessionCart.getDeliveryShippingCarrier() && isCustomerArranged && b2bDeliveryDatesConfig != null
					&& b2bDeliveryDatesConfig.getShippingCarriers() != null)
			{
				final List<String> carrierCodes = b2bDeliveryDatesConfig.getShippingCarriers().stream()
						.map(carrier -> carrier.getCode()).collect(Collectors.toList());

				if (null != carrierCodes && !carrierCodes.isEmpty()
						&& carrierCodes.contains(sessionCart.getDeliveryShippingCarrier().getCarrierCode()))
				{
					b2bDeliveryDatesConfig.setSelectedCarrier(populateShippingCarrier(sessionCart.getDeliveryShippingCarrier()));
				}
			}
		}
	}


	public List<ShippingCarrier> getCustomerOwnedShippingCarriers(final B2BUnitModel b2bUnitModel)
	{
		List<ShippingCarrier> shippingCarriers = null;
		if (CollectionUtils.isNotEmpty(b2bUnitModel.getShippingCarriers()))
		{
			shippingCarriers = new ArrayList<>();
			List<ShippingCarrierModel> allowedCarries = b2bUnitModel.getShippingCarriers();
			if (configurationService.getConfiguration().getBoolean("cub.enable.carrier.restriction", true)
					&& cmsSiteService.getCurrentSite() != null
					&& cmsSiteService.getCurrentSite().getUid().equalsIgnoreCase(CUB_STORE))
			{
				allowedCarries = b2bUnitService.getAllowedCarries(b2bUnitModel.getShippingCarriers());
				LOG.debug("Allowed carriers {}", allowedCarries);
			}

			for (final ShippingCarrierModel shippCarrierModel : allowedCarries)
			{
				if (null != shippCarrierModel.getCustomerOwned() && Boolean.TRUE.equals(shippCarrierModel.getCustomerOwned()))
				{
					shippingCarriers.add(populateShippingCarrier(shippCarrierModel));
				}
			}
		}
		else
		{
			LOG.debug("Attribute ShippingCarriers is null in B2BUnitModel: {}", b2bUnitModel.getPk());
		}
		return shippingCarriers;

	}

	public ShippingCarrier populateShippingCarrier(final ShippingCarrierModel shippCarrierModel)
	{
		final ShippingCarrier carrierData = new ShippingCarrier();
		carrierData.setCode(shippCarrierModel.getCarrierCode());
		if (StringUtils.isEmpty(shippCarrierModel.getCarrierDescription()))
		{
			carrierData.setDescription(shippCarrierModel.getCarrierCode());
		}
		else
		{
			carrierData.setDescription(shippCarrierModel.getCarrierDescription());
		}
		carrierData.setCustomerOwned(shippCarrierModel.getCustomerOwned());
		return carrierData;
	}


	private String getShippingCarrierName(final ShippingCarrierModel shippingCarrier)
	{

		if (StringUtils.isNotEmpty(shippingCarrier.getCarrierDescription()))
		{
			return shippingCarrier.getCarrierDescription();
		}
		else
		{
			return shippingCarrier.getCarrierCode();
		}
	}

	private boolean getEnabledDeliveryMode(final List<DeliveryModePackTypeDeliveryDatesData> deliveryDatesData,
			final DeliveryModeType mode)
	{
		return CollectionUtils
				.isNotEmpty(deliveryDatesData.stream().filter(data -> mode.equals(data.getMode())).collect(Collectors.toList()));
	}

	@Override
	public void register(final ApbRegisterData registerData, final BindingResult bindingResult)
			throws DuplicateUidException, AsahiBusinessException
	{

		if (!asahiSiteUtil.isCub())
		{
			validateParameterNotNullStandardMessage("apbRegisterData", registerData);
			Assert.hasText(registerData.getFirstName(), "The field [FirstName] cannot be empty");
			Assert.hasText(registerData.getLastName(), "The field [LastName] cannot be empty");

			if (!asahiSiteUtil.isSga())
			{
				Assert.hasText(registerData.getAbnAccountId(), "The field [Abn Account ID] cannot be empty");
				Assert.hasText(registerData.getAbnNumber(), "The field [ABN Number] cannot be empty");
				Assert.hasText(registerData.getRole(), "The field [Role] cannot be empty");
			}
			else
			{
				validateParameterNotNullStandardMessage("albCompanyInfoData", registerData.getAlbCompanyInfoData());
				registerData.getAlbCompanyInfoData().forEach(companyData -> {
					Assert.hasText(companyData.getAbnAccountId(), "The field [Abn Account ID] cannot be empty");
					Assert.hasText(companyData.getAbnNumber(), "The field [ABN Number] cannot be empty");
				});
			}
			try
			{
				B2BCustomerModel newCustomer = null;
				boolean existingUser = false;
				final UserModel user = asahiCoreUtil.checkIfUserExists(registerData.getEmail());
				if (null != user && user instanceof B2BCustomerModel)
				{
					existingUser = true;
					newCustomer = (B2BCustomerModel) user;

				}
				else
				{
					newCustomer = getModelService().create(B2BCustomerModel.class);
				}
				setUidForRegister(registerData, newCustomer);
				if (StringUtils.isNotBlank(registerData.getFirstName()) && StringUtils.isNotBlank(registerData.getLastName()))
				{
					newCustomer.setName(getCustomerNameStrategy().getName(registerData.getFirstName(), registerData.getLastName()));
					final String[] splitName = getCustomerNameStrategy().splitName(newCustomer.getName());
					if (splitName != null)
					{
						newCustomer.setFirstName(splitName[0]);
						newCustomer.setLastName(splitName[1]);
					}
				}
				newCustomer.setSessionLanguage(getCommonI18NService().getCurrentLanguage());
				newCustomer.setSessionCurrency(getCommonI18NService().getCurrentCurrency());
				newCustomer.setEmail(registerData.getEmail());
				//adding site refrence
				AsahiB2BUnitModel asahiB2BUnitModel = null;

				/*
				 * ACP - 1404 As per customer liquor license no not checking for validation
				 *
				 * if((StringUtils.isNotEmpty(asahiB2BUnitModel.getLiquorLicensenumber()) &&
				 * !(asahiB2BUnitModel.getLiquorLicensenumber().equals(registerData.getLiquorLicense()))) ||
				 * (StringUtils.isNotEmpty(asahiB2BUnitModel.getLiquorLicensenumber()) &&
				 * StringUtils.isEmpty(registerData.getLiquorLicense()))) { checkLiquorLicense(registerData); }
				 */

				if (asahiSiteUtil.isSga())
				{
					final Map<AsahiB2BUnitModel, String> b2bUnitSamAccessMap = new HashMap<AsahiB2BUnitModel, String>();
					final Iterator<AlbCompanyInfoData> albCompanyInfoDataIterator = registerData.getAlbCompanyInfoData().iterator();
					int counter = 0;
					while (albCompanyInfoDataIterator.hasNext())
					{
						final AlbCompanyInfoData albCompanyInfoData = albCompanyInfoDataIterator.next();
						try
						{
							final AsahiB2BUnitModel albB2BUnitModel = apbB2BUnitService
									.getApbB2BUnit(albCompanyInfoData.getAbnAccountId(), albCompanyInfoData.getAbnNumber());
							/*
							 * As per the comments in SCP-2355, Z001 added with other validations in below method
							 */
							blockRegBasedOnCustomerType(albCompanyInfoData.getSamAccess(), albB2BUnitModel);
							if (null == asahiB2BUnitModel)
							{
								asahiB2BUnitModel = albB2BUnitModel;
							}


							if (BooleanUtils
									.isFalse(sabmB2BCustomerService.isRegistrationAllowed(newCustomer, albB2BUnitModel.getUid())))
							{
								if (CollectionUtils.isNotEmpty(albB2BUnitModel.getDisabledUser())
										&& albB2BUnitModel.getDisabledUser().contains(newCustomer.getUid())
										&& null == b2bUnitSamAccessMap.get(albB2BUnitModel))
								{
									LOG.error("Validation failed: " + "disabled b2b unit found");
									final String adminUserName = AsahiCoreUtil.getAdminUsername(albB2BUnitModel);
									if (SabmCoreConstants.CUSTOMER_SUPPORT.equalsIgnoreCase(adminUserName))
									{
										bindingResult.rejectValue("albCompanyInfoData[" + counter + "].abnAccountId",
												"register.check.sga.disabled.account.customer.support.sga", "");
									}
									else
									{
										bindingResult.rejectValue("albCompanyInfoData[" + counter + "].abnAccountId",
												"register.check.sga.disabled.account", new Object[]
												{ adminUserName }, "");
									}

								}
								else if (null == b2bUnitSamAccessMap.get(albB2BUnitModel))
								{
									LOG.error("Validation failed: " + "Exist b2b unit found");
									bindingResult.rejectValue("albCompanyInfoData[" + counter + "].abnAccountId",
											"register.check.sga.existing.account", "");
								}
							}

							final String albB2BUnit = b2bUnitSamAccessMap.put(albB2BUnitModel, albCompanyInfoData.getSamAccess());
							if (null != albB2BUnit)
							{
								//duplicate b2bunti got-> user entered duplicate data hence ask them to remove one.
								LOG.error("Validation failed: " + "duplicate b2b unit found");
								bindingResult.rejectValue("albCompanyInfoData[" + counter + "].abnAccountId",
										"register.check.sga.duplicate.account",
										"Please remove this account as it has been entered already");
							}

						}
						catch (final UnknownIdentifierException uie)
						{
							LOG.error("Validation failed: " + uie.getMessage());
							bindingResult.rejectValue("albCompanyInfoData[" + counter + "].abnAccountId",
									"register.check.sga.account.id.invalid");
						}
						catch (final AsahiBusinessException abe)
						{
							LOG.error("Customer Account Validation failed: " + abe.getMessage());
							//create new error here. earlier it was global level.
							bindingResult.rejectValue("albCompanyInfoData[" + counter + "].abnAccountId", abe.getMessage(), "");
						}

						finally
						{
							counter++;
						}

					}
					if (bindingResult.hasErrors())
					{
						return;
					}
					if (existingUser)
					{
						isActiveUserCheck(newCustomer);
					}
					/*
					 * SCP-2355 validation added if pay_only registers, payer account should be added to his groups
					 */
					if (null != asahiB2BUnitModel && !b2bUnitSamAccessMap.isEmpty()
							&& null != b2bUnitSamAccessMap.get(asahiB2BUnitModel)
							&& b2bUnitSamAccessMap.get(asahiB2BUnitModel).equalsIgnoreCase(ApbCoreConstants.PAY_ACCESS))
					{
						final AsahiB2BUnitModel payUnit = asahiB2BUnitModel.getPayerAccount();
						newCustomer.setDefaultB2BUnit(payUnit);
					}
					else
					{
						newCustomer.setDefaultB2BUnit(asahiB2BUnitModel);
					}
					if (!b2bUnitSamAccessMap.isEmpty() && CollectionUtils.isNotEmpty(b2bUnitSamAccessMap.keySet()))
					{
						this.addMemebrToB2bUnit(newCustomer, b2bUnitSamAccessMap.keySet());
					}
					getApbCustomerAccountService().register(newCustomer, registerData.getPassword(), b2bUnitSamAccessMap.keySet());

					//Local variable customer in the following logic must be final
					final B2BCustomerModel customer = newCustomer;
					if (!b2bUnitSamAccessMap.isEmpty() && CollectionUtils.isNotEmpty(b2bUnitSamAccessMap.entrySet()))
					{
						b2bUnitSamAccessMap.entrySet().forEach(entrySet -> {
							if (!entrySet.getValue().equalsIgnoreCase(ApbCoreConstants.ORDER_ACCESS))
							{
								final AsahiSAMAccessModel payAccess = apbB2BUnitService.createSamAccess(entrySet.getValue(), customer,
										entrySet.getKey().getUid());
								getApbCustomerAccountService().updateAndNotifyPayAccess(customer, payAccess,
										PAY_ACCESS_REQUEST_EMAIL_CONSTANT);
							}
						});
					}

					newCustomer.setAsahiWelcomeEmailStatus(Boolean.TRUE);

					//Send newly created user to SF
					if (asahiConfigurationService.getBoolean(SEND_CUSTOMER_TO_SF, true))
					{
						asahiUserIntegrationService.sendUsersToSalesforce(Arrays.asList(newCustomer));
					}

				}
				else
				{
					asahiB2BUnitModel = apbB2BUnitService.getApbB2BUnit(registerData.getAbnAccountId(), registerData.getAbnNumber());
					LOG.debug("AbnAccountId : " + registerData.getAbnAccountId() + " ABN Number : " + registerData.getAbnNumber());
					final TitleModel title = getUserService().getTitleForCode(registerData.getTitleCode());
					final AsahiRole asahiRole = enumerationService.getEnumerationValue(AsahiRole.class, registerData.getRole());
					newCustomer.setTitle(title);
					newCustomer.setAsahiRole(asahiRole);
					newCustomer.setRoleOther(registerData.getRoleOther());
					newCustomer.setDefaultB2BUnit(asahiB2BUnitModel);


					if (BooleanUtils.isFalse(sabmB2BCustomerService.isRegistrationAllowed(newCustomer, asahiB2BUnitModel.getUid())))
					{
						if (CollectionUtils.isNotEmpty(asahiB2BUnitModel.getDisabledUser())
								&& asahiB2BUnitModel.getDisabledUser().contains(newCustomer.getUid()))
						{
							final String adminUserName = AsahiCoreUtil.getAdminUsername(asahiB2BUnitModel);
							LOG.error("Validation failed: " + "disabled b2b unit found");
							if (SabmCoreConstants.CUSTOMER_SUPPORT.equalsIgnoreCase(adminUserName))
							{
								bindingResult.rejectValue("abnAccountId", "register.check.sga.disabled.account.customer.support.apb", "");
							}
							else
							{
								bindingResult.rejectValue("abnAccountId", "register.check.sga.disabled.account", new Object[]
								{ adminUserName }, "");
							}
						}
						else
						{
							LOG.error("Validation failed: " + "Exist b2b unit found");
							bindingResult.rejectValue("abnAccountId", "register.check.apb.existing.account", "");
						}
					}
					if (bindingResult.hasErrors())
					{
						return;
					}
					if (existingUser)
					{
						isActiveUserCheck(newCustomer);
					}
					getApbCustomerAccountService().register(newCustomer, registerData.getPassword(), null);
				}
			}
			catch (final UnknownIdentifierException uie)
			{
				LOG.error("AbnAccountId : " + registerData.getAbnAccountId() + " ABN Number : " + registerData.getAbnNumber());
				throw new UnknownIdentifierException(uie);
			}
		}
		else
		{
			super.register(registerData);
		}

	}


	/**
	 * @param newCustomer
	 */
	private void isActiveUserCheck(final B2BCustomerModel newCustomer)
	{
		if (BooleanUtils.isTrue(newCustomer.isLoginDisabled()))
		{
			newCustomer.setLoginDisabled(Boolean.FALSE);
			newCustomer.setModifiedtime(new Date());
			getModelService().save(newCustomer);
		}
		//Preventive check , even though all inactive customers would have been marked as active with a one-time script execution
		if (BooleanUtils.isFalse(newCustomer.getActive()))
		{
			newCustomer.setActive(Boolean.TRUE);
			//add all b2bunits to list of disabled user list
			for (final PrincipalGroupModel group : newCustomer.getGroups())
			{
				if (group instanceof AsahiB2BUnitModel)
				{
					final Collection<String> disabledUsers = new HashSet<String>();
					disabledUsers.addAll(((AsahiB2BUnitModel) group).getDisabledUser());
					disabledUsers.add(newCustomer.getUid());
					((AsahiB2BUnitModel) group).setDisabledUser(disabledUsers);
					getModelService().save(group);
				}
				else if (group instanceof B2BUnitModel)
				{
					final Collection<String> disabledUsers = new HashSet<String>();
					disabledUsers.addAll(((B2BUnitModel) group).getCubDisabledUsers());
					disabledUsers.add(newCustomer.getUid());
					((B2BUnitModel) group).setCubDisabledUsers(disabledUsers);
					getModelService().save(group);
				}

			}
			newCustomer.setModifiedtime(new Date());
			getModelService().save(newCustomer);
		}

	}



	protected void addMemebrToB2bUnit(final PrincipalModel member, final Set<AsahiB2BUnitModel> newGroups)
	{

		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>(
				(member.getGroups() != null ? member.getGroups() : Collections.emptySet()));
		groups.addAll(newGroups);
		member.setGroups(groups);

	}

	@Override
	public void blockRegBasedOnCustomerType(final String samAccess, final AsahiB2BUnitModel asahiB2BUnitModel)
			throws AsahiBusinessException
	{
		/*
		 * SCP-2356 : Validations added
		 */
		final String customerType = typeService.getEnumerationValue(asahiB2BUnitModel.getBackendCustomerType()).getName();

		if ((null == asahiB2BUnitModel.getEclAccountGroupId() || null == asahiB2BUnitModel.getEclAccountGroupId().getCode()
				|| !asahiB2BUnitModel.getEclAccountGroupId().getCode().equalsIgnoreCase("Z001"))
				&& !samAccess.equalsIgnoreCase(ApbCoreConstants.PAY_ACCESS))
		{
			throw new AsahiBusinessException("sga.register.invalid.soldto");
		}

		if (samAccess.equalsIgnoreCase(ApbCoreConstants.ORDER_ACCESS)
				&& !customerType.equalsIgnoreCase(BackendCustomerType.SOLD_TO.toString()))
		{
			/*
			 * If user registering for order only access, check for sold to customer type
			 */
			throw new AsahiBusinessException("sga.register.invalid.soldto");

		}
		else if (samAccess.equalsIgnoreCase(ApbCoreConstants.PAY_ACCESS) && asahiB2BUnitModel.getPayerAccount() == null)
		{
			/*
			 * If user is registering for pay only access, check for payer account
			 */
			throw new AsahiBusinessException("sga.register.invalid.payer");
		}
		else if (samAccess.equalsIgnoreCase(ApbCoreConstants.PAY_ACCESS)
				&& StringUtils.isEmpty(asahiB2BUnitModel.getPayerAccount().getEmailAddress()))
		{
			throw new AsahiBusinessException("sga.register.invalid.payer.email");
		}
		else if (samAccess.equalsIgnoreCase(ApbCoreConstants.PAY_AND_ORDER_ACCESS))
		{
			/*
			 * If user registers for order and pay both, check for above both
			 */
			if (!customerType.equalsIgnoreCase(BackendCustomerType.SOLD_TO.toString()))
			{
				throw new AsahiBusinessException("sga.register.invalid.soldto");
			}
			else if (asahiB2BUnitModel.getPayerAccount() == null)
			{
				throw new AsahiBusinessException("sga.register.invalid.payer");
			}
			else if (StringUtils.isEmpty(asahiB2BUnitModel.getPayerAccount().getEmailAddress()))
			{
				throw new AsahiBusinessException("sga.register.invalid.payer.email");
			}
		}
	}

	/**
	 * check if liquor license number exist or not of Unit
	 *
	 * @param registerData
	 */
	protected void checkLiquorLicense(final ApbRegisterData registerData)
	{
		try
		{
			apbB2BUnitService.findLiquorLicense(registerData.getLiquorLicense());
		}
		catch (final ModelNotFoundException m)
		{
			LOG.error("Liquor License : " + registerData.getLiquorLicense());
			throw new ModelNotFoundException(m);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.customer.impl.DefaultCustomerFacade#updateProfile(de.hybris.platform.
	 * commercefacades.user.data.CustomerData)
	 */
	@Override
	public void updateProfile(final CustomerData customerData) throws DuplicateUidException
	{

		if (!asahiSiteUtil.isCub())
		{
			if (asahiSiteUtil.isSga())
			{
				validateCustomerDataBeforeUpdate(customerData);
			}
			else
			{
				validateDataBeforeUpdate(customerData);
			}

			final String name = getCustomerNameStrategy().getName(customerData.getFirstName(), customerData.getLastName());
			final CustomerModel customer = getCurrentSessionCustomer();
			customer.setOriginalUid(customerData.getDisplayUid());
			//adding or updating contact number
			if (customer instanceof B2BCustomerModel)
			{
				final String[] splitName = getCustomerNameStrategy().splitName(name);
				if (splitName != null)
				{
					customer.setFirstName(splitName[0]);
					customer.setLastName(splitName[1]);
				}
				if (customerData.getContactNumber() != null) {
					((B2BCustomerModel) customer).setContactNumber(StringUtils.deleteWhitespace(customerData.getContactNumber()));
				} else {
					((B2BCustomerModel) customer).setContactNumber(null);
				}
				if (asahiSiteUtil.isSga())
				{
					((B2BCustomerModel) customer).setDisableEmailNotification(customerData.getDisableEmailNotification());
				}
			}
			getCustomerAccountService().updateProfile(customer, customerData.getTitleCode(), name, customerData.getUid());
		}
		else
		{
			super.updateProfile(customerData);
		}
	}

	/**
	 * This method validates customerData specific to SGA before update.
	 *
	 * @param customerData
	 */
	private void validateCustomerDataBeforeUpdate(final CustomerData customerData)
	{
		validateParameterNotNullStandardMessage("customerData", customerData);
		Assert.hasText(customerData.getFirstName(), "The field [FirstName] cannot be empty");
		Assert.hasText(customerData.getLastName(), "The field [LastName] cannot be empty");
		Assert.hasText(customerData.getUid(), "The field [Uid] cannot be empty");
	}

	public void setApbCustomerAccountService(final ApbCustomerAccountService apbCustomerAccountService)
	{
		this.apbCustomerAccountService = apbCustomerAccountService;
	}

	/**
	 * @return apbCustomerAccountService
	 */
	public ApbCustomerAccountService getApbCustomerAccountService()
	{
		return apbCustomerAccountService;
	}

	/**
	 * @return apbB2BUnitService
	 */
	public ApbB2BUnitService getApbB2BUnitService()
	{
		return apbB2BUnitService;
	}

	public void setApbB2BUnitService(final ApbB2BUnitService apbB2BUnitService)
	{
		this.apbB2BUnitService = apbB2BUnitService;
	}

	/**
	 * @param parameter
	 * @param nullMessage
	 */
	public static void validateParameterNotNull(final Object parameter, final String nullMessage)
	{
		Preconditions.checkArgument(parameter != null, nullMessage);
	}

	/**
	 * @param parameter
	 * @param parameterValue
	 */
	public static void validateParameterNotNullStandardMessage(final String parameter, final Object parameterValue)
	{
		validateParameterNotNull(parameterValue, "Parameter " + parameter + " can not be null");
	}

	/**
	 * @return
	 */
	public EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	@Override
	public void sendRequestRegisterEmail(final ApbRequestRegisterData apbRequestRegisterData)
			throws DuplicateUidException, RendererException, MediaIOException, IllegalArgumentException, IOException
	{
		validateParameterNotNull(apbRequestRegisterData, "ApbRequestRegisterData can not be null");
		apbCustomerAccountService.sendRequestRegisterEmail(setRequestRegisterModel(apbRequestRegisterData));
	}

	/**
	 * @param requestRegisterData
	 * @param setRequestRegistrationModel
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @throws MediaIOException
	 */
	private ApbRequestRegisterEmailModel setRequestRegisterModel(final ApbRequestRegisterData requestRegisterData)
			throws MediaIOException, IllegalArgumentException, IOException
	{
		final ApbRequestRegisterEmailModel requestRegistration = getModelService().create(ApbRequestRegisterEmailModel.class);
		requestRegistration.setCode(UUID.randomUUID().toString());
		requestRegistration.setUploadFile(getMediasFromFiles(requestRegisterData.getPdfFile()));
		requestRegistration.setOutletName(requestRegisterData.getOutletName());
		requestRegistration.setTraidingName(requestRegisterData.getTradingName());
		requestRegistration.setCompanyName(requestRegisterData.getCompanyName());
		requestRegistration.setStreetNumber(requestRegisterData.getStreetNumber());
		requestRegistration.setStreetName(requestRegisterData.getStreetName());
		requestRegistration.setStreetAvreviation(requestRegisterData.getStreetAbreviation());
		requestRegistration.setUnitNo(requestRegisterData.getUnitNoShopNo());
		requestRegistration.setLevel(requestRegisterData.getLevel());
		requestRegistration.setSuburb(requestRegisterData.getSuburb());
		final RegionModel regionModelInvoice = new RegionModel();
		regionModelInvoice.setIsocode(requestRegisterData.getStateInvoice());
		final List<RegionModel> regionModelList1 = flexibleSearchService.getModelsByExample(regionModelInvoice);
		if (CollectionUtils.isNotEmpty(regionModelList1))
		{
			requestRegistration.setStateInvoice(regionModelList1.get(0).getName());
		}
		requestRegistration.setPostalCodeInvoice(requestRegisterData.getPostcodeInvoice());
		requestRegistration.setContactName(requestRegisterData.getContactName());
		requestRegistration.setAlternateContact(requestRegisterData.getAlternateContact());
		requestRegistration.setPhoneInvoice(requestRegisterData.getPhoneNoInvoice());
		requestRegistration.setCustomerType(requestRegisterData.getCustomerType());
		requestRegistration.setAlternatePhoneInvoice(requestRegisterData.getAlternativePhoneNo());
		requestRegistration.setWarehouseNo(requestRegisterData.getWarehouseNo());
		requestRegistration.setEmailAddress(requestRegisterData.getEmailAddress());
		requestRegistration.setAbn(requestRegisterData.getAbn());
		requestRegistration.setLiquorLicense(requestRegisterData.getLiquorLicense());
		requestRegistration.setAcn(requestRegisterData.getAcn());
		requestRegistration.setSameAsAddress(requestRegisterData.isSameasInvoiceAddress());
		requestRegistration.setShippingStreet(requestRegisterData.getShippingStreet());
		requestRegistration.setShippingSuburb(requestRegisterData.getShippingSuburb());
		final RegionModel regionModel = new RegionModel();
		regionModel.setIsocode(requestRegisterData.getStateDelivery());
		final List<RegionModel> regionModelList = flexibleSearchService.getModelsByExample(regionModel);
		if (CollectionUtils.isNotEmpty(regionModelList))
		{
			requestRegistration.setStateDelivery(regionModelList.get(0).getName());
		}
		requestRegistration.setPostalCodeDelivery(requestRegisterData.getPostcodeDelivery());
		requestRegistration.setDeliveryInstruction(requestRegisterData.getDeliveryInstructions());
		requestRegistration.setTrust(requestRegisterData.isApplicantCarry());
		requestRegistration.setTypeofEntity(requestRegisterData.getTypeofEntity());
		requestRegistration.setTypeofBusiness(requestRegisterData.getTypeofBusiness());
		requestRegistration.setDateBusinessEst(requestRegisterData.getDateBusinessEstablished());
		requestRegistration.setLicensePermisesAddress(requestRegisterData.getLicensedPremisesAddress());
		requestRegistration.setLicense(requestRegisterData.getLicensee());
		requestRegistration.setBannerGroup(requestRegisterData.getBannerGroup());
		requestRegistration.setDateExpiryLiquorLicense(requestRegisterData.getDateandExpiryofLiquorLicense());
		requestRegistration.setPurchasingOfficer(requestRegisterData.getPurchasingOfficer());
		requestRegistration.setAccountContact(requestRegisterData.getAccountsContact());
		requestRegistration.setName(requestRegisterData.getName());
		requestRegistration.setPositionIndividual(requestRegisterData.getPosition());
		requestRegistration.setAddressIndividual(requestRegisterData.getAddress());
		requestRegistration.setPhoneIndividual(requestRegisterData.getPhoneNo());
		requestRegistration.setDobIndividual(requestRegisterData.getDateofBirth());
		requestRegistration.setNameIndividual1(requestRegisterData.getName1());
		requestRegistration.setPositionIndividual1(requestRegisterData.getPosition1());
		requestRegistration.setAddressIndividual1(requestRegisterData.getAddress1());
		requestRegistration.setPhone1Individual1(requestRegisterData.getPhoneNo1());
		requestRegistration.setDobIndividual1(requestRegisterData.getDateofBirth1());
		requestRegistration.setBankBranch(requestRegisterData.getBankBranch());
		requestRegistration.setContactIndividual(requestRegisterData.getContact());
		requestRegistration.setPhoneNoIndividual(requestRegisterData.getPhoneNoReference());
		requestRegistration.setTermsConditions(requestRegisterData.isRequestTermsConditions());
		requestRegistration.setTrustName(requestRegisterData.getTrustName());
		requestRegistration.setTrustDeed(requestRegisterData.getTrustDeed());
		requestRegistration.setTrustAbn(requestRegisterData.getTrustAbn());
		requestRegistration.setSameasDeliveryAddressLPA(requestRegisterData.isSameasDeliveryAddressLPA());
		return requestRegistration;
	}

	/**
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @throws MediaIOException
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.crm.services.CRMMediaService#getMediasFromFiles(java.util.List)
	 */

	public MediaModel getMediasFromFiles(final MultipartFile file)
			throws MediaIOException, IllegalArgumentException, IOException
	{
		if (null != file && file.getSize() > 0)
		{
			final String fileName = file.getOriginalFilename();
			final CatalogUnawareMediaModel mediaModel = getModelService().create(CatalogUnawareMediaModel.class);
			if (StringUtils.isNotEmpty(fileName))
			{
				mediaModel.setCode(UUID.randomUUID().toString());
				getModelService().save(mediaModel);
				final InputStream inputStream = file.getInputStream();
				try
				{
					mediaService.setStreamForMedia(mediaModel, file.getInputStream(), fileName, file.getContentType());
					if (null != inputStream)
					{
						inputStream.close();
					}
				}
				catch (final FileNotFoundException fne)
				{
					LOG.error("Error in uploaded file: " + fne.getMessage(), fne);
					if (null != inputStream)
					{
						inputStream.close();
					}
				}
				getModelService().refresh(mediaModel);
			}
			return mediaModel;
		}
		return null;
	}

	@Override
	public boolean validatePassword(final String password)
	{
		if (!asahiSiteUtil.isCub())
		{
			boolean isValid = false;
			passwordValidationPattern = asahiConfigurationService
					.getString(STOREFRONT_PASSWORDPATTERN_APB + cmsSiteService.getCurrentSite().getUid(), "");
			if (StringUtils.isNotBlank(passwordValidationPattern))
			{
				isValid = password.matches(passwordValidationPattern);
			}
			if (!isValid)
			{
				throw new InvalidPasswordException("Password does not match pattern.");
			}
			return isValid;
		}
		else
		{
			return super.validatePassword(password);
		}
	}

	/**
	 * @return passwordValidationPattern
	 */
	public String getPasswordValidationPattern()
	{
		return passwordValidationPattern;
	}

	/**
	 * @param passwordValidationPattern
	 */
	public void setPasswordValidationPattern(final String passwordValidationPattern)
	{
		this.passwordValidationPattern = passwordValidationPattern;
	}

	public boolean getCustomerAccountCreditLimit()
	{
		return apbCustomerAccountService.getCustomerAccountCreditLimit();
	}

	public AsahiConfigurationService getAsahiConfigurationService()
	{
		return asahiConfigurationService;
	}

	public void setAsahiConfigurationService(final AsahiConfigurationService asahiConfigurationService)
	{
		this.asahiConfigurationService = asahiConfigurationService;
	}

	public ApbCompanyData getB2BCustomerData()
	{
		return apbCustomerAccountService.getB2BCustomerData();
	}

	@Override
	public void updateCompanyDetails(final ApbCompanyData apbCompanyData)
	{
		validateParameterNotNull(apbCompanyData, "apbCompanyData can not be null");
		apbCustomerAccountService.updateCompanyDetails(setCompanyDetailsModel(apbCompanyData));
	}

	/**
	 * Company Details Email Data Set
	 *
	 * @param apbCompanyData
	 * @return
	 */
	protected ApbCompanyDetailsEmailModel setCompanyDetailsModel(final ApbCompanyData apbCompanyData)
	{
		final ApbCompanyDetailsEmailModel apbCompanyDetailsEmail = getModelService().create(ApbCompanyDetailsEmailModel.class);
		apbCompanyDetailsEmail.setCode(UUID.randomUUID().toString());
		apbCompanyDetailsEmail.setAccountNumber(apbCompanyData.getAccountNumber());
		apbCompanyDetailsEmail.setAccountName(apbCompanyData.getAcccountName());
		apbCompanyDetailsEmail.setAbn(apbCompanyData.getAbn());
		apbCompanyDetailsEmail.setTraidingName(apbCompanyData.getTradingName());

		if (CollectionUtils.isNotEmpty(apbCompanyData.getDeliveryAddresses()))
		{
			setDeliveryAddress(apbCompanyData, apbCompanyDetailsEmail);
		}
		apbCompanyDetailsEmail.setCompanyBillingAddress(apbCompanyData.getCompanyBillingAddress());
		apbCompanyDetailsEmail.setCompanyEmailAddress(apbCompanyData.getCompanyEmailAddress());
		apbCompanyDetailsEmail.setCompanyFax(apbCompanyData.getCompanyFax());
		apbCompanyDetailsEmail.setCompanyMobilePhone(apbCompanyData.getCompanyMobilePhone());
		apbCompanyDetailsEmail.setCompanyPhone(apbCompanyData.getCompanyPhone());
		apbCompanyDetailsEmail.setLiquorLicense(apbCompanyData.getLiquorLicense());
		apbCompanyDetailsEmail.setSameAsAddress(apbCompanyData.isSameasInvoiceAddress());
		return apbCompanyDetailsEmail;
	}

	/**
	 * @param apbCompanyData
	 * @param apbCompanyDetailsEmail
	 */
	private void setDeliveryAddress(final ApbCompanyData apbCompanyData, final ApbCompanyDetailsEmailModel apbCompanyDetailsEmail)
	{
		final List<B2BUnitDeliveryAddressData> b2bUnitDelAddressDataList = apbCompanyData.getDeliveryAddresses();
		for (final B2BUnitDeliveryAddressData b2bUnitDeliveryAddressData : b2bUnitDelAddressDataList)
		{
			final List<ApbDeliveryAddressModel> apbDeliveryAddressModelList = new LinkedList<ApbDeliveryAddressModel>();
			if (CollectionUtils.isNotEmpty(b2bUnitDeliveryAddressData.getDeliveryAddresses()))
			{
				for (final AddressData addressData : b2bUnitDeliveryAddressData.getDeliveryAddresses())
				{
					final ApbDeliveryAddressModel apbDeliveryAddressModel = getModelService().create(ApbDeliveryAddressModel.class);
					apbDeliveryAddressModel.setAddressId(addressData.getAddressId());
					apbDeliveryAddressModel.setCode(UUID.randomUUID().toString());
					apbDeliveryAddressModel.setDeliveryAddress(addressData.getDeliveryAddress());
					apbDeliveryAddressModel.setDeliveryInstruction(addressData.getDeliveryInstruction());
					apbDeliveryAddressModel.setDeliveryCalendar(addressData.getDeliveryCalendar());
					apbDeliveryAddressModel.setDeliveryTimeFrameFromMM(addressData.getDeliveryTimeFrameFromMM());
					apbDeliveryAddressModel.setDeliveryTimeFrameFromHH(addressData.getDeliveryTimeFrameFromHH());
					apbDeliveryAddressModel.setDeliveryTimeFrameToMM(addressData.getDeliveryTimeFrameToMM());
					apbDeliveryAddressModel.setDeliveryTimeFrameToHH(addressData.getDeliveryTimeFrameToHH());
					apbDeliveryAddressModel.setRemoveRequestAddress(addressData.getRemoveRequestAddress());
					apbDeliveryAddressModel.setChangeRequestAddress(addressData.getChangeRequestAddress());
					apbDeliveryAddressModelList.add(apbDeliveryAddressModel);
				}
				apbCompanyDetailsEmail.setApbDeliveryAddressColl(apbDeliveryAddressModelList);
			}
		}
	}

	/**
	 * Get LogedInB2BCustomer from db to set in Form
	 */
	@Override
	public ApbContactUsData getLogedInB2BCustomer()
	{
		return apbCustomerAccountService.getLogedInB2BCustomer();
	}

	@Override
	public void sendKegReturnEmail(final ApbKegReturnData apbKegReturnData)
	{
		validateParameterNotNull(apbKegReturnData, "apbKegReturnData can not be null");
		apbCustomerAccountService.sendKegReturnEmail(setKegReturnModel(apbKegReturnData));
	}

	private ApbKegReturnEmailModel setKegReturnModel(final ApbKegReturnData apbKegReturnData)
	{
		final ApbKegReturnEmailModel kegReturnEmailModel = getModelService().create(ApbKegReturnEmailModel.class);
		kegReturnEmailModel.setCode(UUID.randomUUID().toString());

		final ApbCompanyData apbCompanyData = apbCustomerAccountService.getB2BCustomerData();
		kegReturnEmailModel.setAccountName(apbCompanyData.getTradingName());
		kegReturnEmailModel.setAccountNumber(apbCompanyData.getAccountNumber());
		setCustomerData(kegReturnEmailModel);
		kegReturnEmailModel.setKegComments(apbKegReturnData.getKegComments());
		final AddressModel addressModel = new AddressModel();
		addressModel.setAddressRecordid(apbKegReturnData.getPickupAddressId());
		try
		{
			final List<AddressModel> addressModelList = flexibleSearchService.getModelsByExample(addressModel);
			if (CollectionUtils.isNotEmpty(addressModelList))
			{
				kegReturnEmailModel.setPickupAddress(addressModelList.get(0));
			}
		}
		catch (final ModelLoadingException mle)
		{
			LOG.error("Address Model Not Found! ", mle);
		}
		setKegSize(apbKegReturnData, kegReturnEmailModel);
		return kegReturnEmailModel;
	}

	/**
	 * @param kegReturnEmailModel
	 */
	private void setCustomerData(final ApbKegReturnEmailModel kegReturnEmailModel)
	{
		final UserModel user = getUserService().getCurrentUser();
		if (user instanceof B2BCustomerModel)
		{
			final B2BCustomerModel b2bCustomer = (B2BCustomerModel) user;
			kegReturnEmailModel.setContactName(b2bCustomer.getDisplayName());
			kegReturnEmailModel.setContactNumber(b2bCustomer.getContactNumber());
			kegReturnEmailModel.setEmailAddress(b2bCustomer.getContactEmail());
		}
	}

	/**
	 * @param apbKegReturnData
	 * @param kegReturnEmailModel
	 */
	private void setKegSize(final ApbKegReturnData apbKegReturnData, final ApbKegReturnEmailModel kegReturnEmailModel)
	{
		if (CollectionUtils.isNotEmpty(apbKegReturnData.getKegSize()))
		{
			final List<KegReturnSizeModel> kegReturnSizeModelList = new LinkedList<>();
			for (final KegSizeData kegReturnData : apbKegReturnData.getKegSize())
			{
				final KegReturnSizeModel kegReturnSizeModel = getModelService().create(KegReturnSizeModel.class);
				kegReturnSizeModel.setKegSize(kegReturnData.getKegSize());
				kegReturnSizeModel.setCode(UUID.randomUUID().toString());
				kegReturnSizeModel.setKegQuantity(kegReturnData.getKegQuantity());
				kegReturnSizeModel.setSite(cmsSiteService.getCurrentSite());
				getModelService().save(kegReturnSizeModel);
				kegReturnSizeModelList.add(kegReturnSizeModel);
			}
			kegReturnEmailModel.setKegReturnSizeList(kegReturnSizeModelList);
		}
	}

	/**
	 * This method will set the customer specific inclusion product list is session
	 *
	 * @return - list of error codes
	 */
	@Override
	public LoginValidateInclusionData setCustomerCreditAndInclusionInSession()
	{

		/*
		 * Marking session user credit block as false, since we are trigering a new login request
		 */
		asahiCoreUtil.setSessionUserCreditBlock(false);
		asahiCoreUtil.removeCreditInfoInSession();
		final List<ErrorDTO> errorCodes = new ArrayList<>();
		final LoginValidateInclusionData responseDTO = new LoginValidateInclusionData();
		final ErrorDTO error = new ErrorDTO();

		final UserModel user = getUserService().getCurrentUser();
		final AsahiSAMAccessModel currentSAMAccess = new AsahiSAMAccessModel();
		final AtomicBoolean payAccess = new AtomicBoolean(Boolean.FALSE);
		final AtomicBoolean orderAccess = new AtomicBoolean(Boolean.FALSE);
		final AtomicBoolean isApprovalPending = new AtomicBoolean(Boolean.FALSE);
		final AtomicBoolean isApprovalDenied = new AtomicBoolean(Boolean.FALSE);

		if (user instanceof B2BCustomerModel)
		{
			final B2BCustomerModel customer = (B2BCustomerModel) user;
			final Collection<AsahiSAMAccessModel> samAccessList = customer.getSamAccess();

			if (CollectionUtils.isNotEmpty(samAccessList))
			{
				String payer = null;
				if (null != ((AsahiB2BUnitModel) customer.getDefaultB2BUnit()).getPayerAccount())
				{
					payer = ((AsahiB2BUnitModel) customer.getDefaultB2BUnit()).getPayerAccount().getPk().toString();
				}

				for (final AsahiSAMAccessModel access : samAccessList)
				{
					if (null != access.getPayer() && payer.equalsIgnoreCase(access.getPayer().getPk().toString()))
					{
						payAccess.set(access.isPayAccess());
						orderAccess.set(access.isOrderAccess());
						isApprovalPending.set(access.isPendingApproval());
						isApprovalDenied.set(access.isApprovalDenied());

						break;
					}
				}

			}

			final Set<String> productIds = apbCustomerAccountService.getCustomerCatalogProductIds(user.getUid());
			asahiCoreUtil.setShowProductWithoutPrice(false);
			if (CollectionUtils.isEmpty(productIds))
			{
				asahiCoreUtil.setSessionProductBlock(Boolean.TRUE);
				error.setError("inclusion.product.not.found");
				error.setErrorCode(ApbCoreConstants.PRODUCTS_BLOCK_CODE);
				errorCodes.add(error);
				responseDTO.setErrors(errorCodes);
				responseDTO.setSuccess(Boolean.FALSE);
				return responseDTO;
			}

			final AsahiLoginInclusionResponseDTO serviceResponse = asahiIntegrationPointsService
					.getInclusionListDetailsForLogin(user, productIds);

			if (null == serviceResponse || null == serviceResponse.getLoginResponse())
			{
				//1. if response is null due to any error --> user will see all products without price
				LOG.info("Material inclusion response is null");
				error.setError("sga.user.inclusion.error.message");
				asahiCoreUtil.setShowProductWithoutPrice(true);
			}
			else if (serviceResponse.getLoginResponse().getIsBlocked())
			{
				//2. if user is blocked --> user will see the products but add to cart disabled
				if (null != serviceResponse.getLoginResponse().getItems() || !serviceResponse.getLoginResponse().getItems().isEmpty())
				{
					final Map<String, AsahiProductInfo> inclusionMap = serviceResponse.getLoginResponse().getItems().stream()
							.filter(item -> !item.getIsExcluded())
							.collect(Collectors.toMap(AsahiProductInfo::getMaterialNumber, obj -> obj));
					asahiCoreUtil.setInclusionMapInSession(inclusionMap);
				}
				asahiCoreUtil.setSessionUserCreditBlock(true);

				if (orderAccess.get() && payAccess.get())
				{
					error.setError("sga.order.and.pay.user.credit.block.message");
				}
				else if (orderAccess.get())
				{
					error.setError("sga.order.only.user.credit.block.message");
				}
				else if (payAccess.get())
				{
					if (!isApprovalPending.get())
					{
						error.setError("sga.pay.only.user.credit.block.message");
					}
					else
					{
						error.setError("sga.order.only.user.pending.credit.block.message");
					}
				}
				else
				{
					error.setError("sga.user.credit.block.message");
				}
				error.setErrorCode(ApbCoreConstants.CREDIT_BLOCK_CODE);
				final AsahiCustomerAccountCheckResponseData customerAccountCheckResponseData = serviceResponse.getLoginResponse();
				final Double percentageUsed = customerAccountCheckResponseData.getPercentageUsed() != null
						? customerAccountCheckResponseData.getPercentageUsed()
						: 0;
				final boolean isCloseToCreditBlock = asahiCoreUtil.isCloseToCreditBlock(percentageUsed);
				addCreditInfo(customerAccountCheckResponseData, percentageUsed, isCloseToCreditBlock);

			}
			else if (!CollectionUtils.isEmpty(serviceResponse.getLoginResponse().getErrors())
					&& !isErrorCodesEmpty(serviceResponse.getLoginResponse().getErrors()))
			{
				// In case any error encountered, work accordingly
				serviceResponse.getLoginResponse().getErrors().stream().forEach(errorItem -> {
					if (null != errorItem.getCode() && errorItem.getCode().equalsIgnoreCase(ApbCoreConstants.ERROR_001))
					{
						asahiCoreUtil.setSessionProductBlock(Boolean.TRUE);
						if (payAccess.get() && !orderAccess.get())
						{
							error.setError("inclusion.product.not.found.for.payer");
						}
						else
						{
							error.setError("inclusion.product.not.found");
						}
						error.setErrorCode(ApbCoreConstants.PRODUCTS_BLOCK_CODE);
					}
					else if (null != errorItem.getCode() && errorItem.getCode().equalsIgnoreCase(ApbCoreConstants.ERROR_002))
					{
						error.setError("sga.user.inclusion.error.message");
						asahiCoreUtil.setShowProductWithoutPrice(true);
					}
					else if (null != errorItem.getCode() && errorItem.getCode().equalsIgnoreCase(ApbCoreConstants.ERROR_003))
					{
						error.setError("sga.user.inclusion.pricing.error.message");
					}
					else if (null != errorItem.getCode() && errorItem.getCode().equalsIgnoreCase(ApbCoreConstants.ERROR_004))
					{
						error.setError("sga.user.inclusion.promotion.error.message");
					}
				});

				final AsahiCustomerAccountCheckResponseData customerAccountCheckResponseData = serviceResponse.getLoginResponse();
				final Double percentageUsed = customerAccountCheckResponseData.getPercentageUsed() != null
						? customerAccountCheckResponseData.getPercentageUsed()
						: 0;
				final boolean isCloseToCreditBlock = asahiCoreUtil.isCloseToCreditBlock(percentageUsed);
				addCreditInfo(customerAccountCheckResponseData, percentageUsed, isCloseToCreditBlock);
			}
			else if (!serviceResponse.getLoginResponse().getIsBlocked() && (null == serviceResponse.getLoginResponse().getItems()
					|| serviceResponse.getLoginResponse().getItems().isEmpty()))
			{
				//3. if user is not blocked but inclusion error comes --> user will be notified and all products would be displayed
				asahiCoreUtil.setShowProductWithoutPrice(true);
				error.setError("sga.user.inclusion.pricing.error.message");
				final AsahiCustomerAccountCheckResponseData customerAccountCheckResponseData = serviceResponse.getLoginResponse();
				final Double percentageUsed = customerAccountCheckResponseData.getPercentageUsed() != null
						? customerAccountCheckResponseData.getPercentageUsed()
						: 0;
				final boolean isCloseToCreditBlock = asahiCoreUtil.isCloseToCreditBlock(percentageUsed);
				addCreditInfo(customerAccountCheckResponseData, percentageUsed, isCloseToCreditBlock);
			}
			else
			{
				//4. successfully get inclusion list without any error -- > no message displayed
				/*
				 * Converting the response to Map and removing the duplicate items along with the excluded one
				 */
				final AsahiCustomerAccountCheckResponseData customerAccountCheckResponseData = serviceResponse.getLoginResponse();

				final Map<String, AsahiProductInfo> inclusionMap = new HashMap<>();
				customerAccountCheckResponseData.getItems().stream().forEach(item -> {
					if (!inclusionMap.containsKey(item.getMaterialNumber()) && !item.getIsExcluded())
					{
						inclusionMap.put(item.getMaterialNumber(), item);
					}
				});
				/*
				 * if all the items in response are excluded and no product stands eligible and user will not see any
				 * product
				 */
				if (MapUtils.isEmpty(inclusionMap))
				{
					asahiCoreUtil.setSessionProductBlock(Boolean.TRUE);
					error.setError("inclusion.product.not.found");
					error.setErrorCode(ApbCoreConstants.PRODUCTS_BLOCK_CODE);
				}

				asahiCoreUtil.setInclusionMapInSession(inclusionMap);
				final Double percentageUsed = customerAccountCheckResponseData.getPercentageUsed() != null
						? customerAccountCheckResponseData.getPercentageUsed()
						: 0;
				final boolean isCloseToCreditBlock = asahiCoreUtil.isCloseToCreditBlock(percentageUsed);

				if (isCloseToCreditBlock)
				{
					if (asahiCoreUtil.isNAPUser())
					{
						error.setError("sga.close.to.block.is.national.account");
					}
					else if (orderAccess.get())
					{
						if (payAccess.get())
						{
							if (isApprovalPending.get())
							{
								error.setError("sga.order.only.user.pending.close.to.block.message");
							}
							else
							{
								error.setError("sga.order.and.pay.user.close.to.block.message");
							}
						}
						else
						{
							error.setError("sga.order.only.user.close.to.block.message");
						}

					}
					else if (payAccess.get())
					{
						if (isApprovalPending.get())
						{
							error.setError("sga.order.only.user.pending.close.to.block.message");
						}
						else
						{
							error.setError("sga.pay.only.user.close.to.block.message");
						}

					}
					else
					{
						error.setError("sga.order.only.user.close.to.block.message");
					}

					error.setErrorCode(ApbCoreConstants.CLOSE_TO_CREDIT_BLOCK);
				}

				addCreditInfo(customerAccountCheckResponseData, percentageUsed, isCloseToCreditBlock);

			}
			if (StringUtils.isNotEmpty(error.getError()))
			{
				errorCodes.add(error);
			}

		}
		else
		{
			error.setError("inclusion.customer.not.found");
			responseDTO.setSuccess(Boolean.FALSE);
			errorCodes.add(error);
		}
		if (!errorCodes.isEmpty())
		{ // If there is no error, dont add any empty list to the response
			responseDTO.setErrors(errorCodes);
		}
		return responseDTO;
	}

	private void addCreditInfo(final AsahiCustomerAccountCheckResponseData customerAccountCheckResponseData,
			final Double percentageUsed, final boolean isCloseToCreditBlock)
	{
		asahiCoreUtil.setCreditInfoInSession(
				customerAccountCheckResponseData.getCreditLimit() != null ? customerAccountCheckResponseData.getCreditLimit() : 0,
				customerAccountCheckResponseData.getDeltaToLimit() != null ? customerAccountCheckResponseData.getDeltaToLimit() : 0,
				percentageUsed, isCloseToCreditBlock);
	}

	private boolean isErrorCodesEmpty(final List<Error> errors)
	{
		final AtomicBoolean isEmpty = new AtomicBoolean(Boolean.TRUE);
		errors.stream().forEach(error -> {
			if (StringUtils.isNotEmpty(error.getCode()))
			{
				isEmpty.set(Boolean.FALSE);
			}
		});
		return isEmpty.get();
	}

	/*
	 * This Method will fetch the paged multi accounts related with current user.
	 *
	 * @param pageableData
	 */
	@Override
	public SearchPageData<B2BUnitData> getPagedMultiAccounts(final PageableData pageableData)
	{
		List<B2BUnitData> entries = new ArrayList<>();
		//List<AsahiB2BUnitModel> siteSpecificList = new ArrayList<>();
		final Map<String, List<AsahiB2BUnitModel>> allB2bUnits = apbB2BUnitService
				.getUserActiveB2BUnits(getUserService().getCurrentUser().getUid());

		final String currentSite = asahiSiteUtil.getCurrentSite().getUid();
		if (CollectionUtils.isNotEmpty(allB2bUnits.get(currentSite)))
		{
			/*
			 * final List<AsahiB2BUnitModel> activeUnits = new ArrayList<>(); allB2bUnits.stream().forEach(unit -> {
			 * activeUnits.add((AsahiB2BUnitModel) unit); });
			 */

			//siteSpecificList = asahiCoreUtil.getSiteBasedUnits(activeUnits);
			entries = getAsahiB2BUnitConverter().convertAll(allB2bUnits.get(currentSite));
			//get the default address for b2bunit.
			getDefaultAddress(entries);

		}


		final SearchPageData<B2BUnitData> searchPageData = asahiPaginationUtil.convertPageData(entries, pageableData);
		return searchPageData;

	}

	/**
	 * This method will restrict results per page for pagination.
	 *
	 * @param searchPageData
	 * @param pageableData
	 * @return
	 */
	public void retrictResultsPerPage(final SearchPageData<B2BUnitData> searchPageData)
	{
		final int startIndex = searchPageData.getPagination().getCurrentPage() * searchPageData.getPagination().getPageSize();
		int endIndex = searchPageData.getPagination().getCurrentPage() * searchPageData.getPagination().getPageSize()
				+ searchPageData.getPagination().getPageSize();
		if (endIndex > searchPageData.getResults().size())
		{
			endIndex = searchPageData.getResults().size();
		}
		searchPageData.setResults(searchPageData.getResults().subList(startIndex, endIndex));

	}

	/**
	 * Mehtod will update the b2bunits with default address.
	 *
	 * @param entries
	 */
	private void getDefaultAddress(final List<B2BUnitData> entries)
	{
		if (CollectionUtils.isNotEmpty(entries))
		{
			for (final B2BUnitData entry : entries)
			{
				if (CollectionUtils.isNotEmpty(entry.getAddresses()))
				{
					final List<AddressData> updatedList = new ArrayList<>();
					AddressData resultantAddress = new AddressData();

					final Optional<AddressData> defaultAddress = entry.getAddresses().stream()
							.filter(address -> address.isDefaultAddress()).findFirst();

					if (defaultAddress.isPresent())
					{
						resultantAddress = defaultAddress.get();
					}
					else
					{
						resultantAddress = entry.getAddresses().get(0);
					}

					updatedList.add(resultantAddress);
					concatStreetFieldsForDisplay(resultantAddress);
					entry.setAddresses(updatedList);
				}
			}
		}
	}


	/**
	 * This Method will make the street field based on street name and number for frontend display only.
	 *
	 * @param addressData
	 */
	private void concatStreetFieldsForDisplay(final AddressData addressData)
	{
		final StringBuilder builder = new StringBuilder();
		if (StringUtils.isNotBlank(addressData.getStreetnumber()))
		{
			builder.append(addressData.getStreetnumber());
		}
		if (StringUtils.isNotBlank(addressData.getStreetname()) && StringUtils.isNotBlank(addressData.getStreetnumber()))
		{
			builder.append(FIELD_SEPARATOR);
		}
		if (StringUtils.isNotBlank(addressData.getStreetname()))
		{
			builder.append(addressData.getStreetname());
		}
		addressData.setStreetField(builder.toString());
	}

	/**
	 * @return
	 */
	public Converter<B2BUnitModel, B2BUnitData> getAsahiB2BUnitConverter()
	{
		return asahiB2BUnitConverter;
	}

	/**
	 * @param asahiB2BUnitConverter
	 */
	public void setAsahiB2BUnitConverter(final Converter<B2BUnitModel, B2BUnitData> asahiB2BUnitConverter)
	{
		this.asahiB2BUnitConverter = asahiB2BUnitConverter;
	}



	/*
	 * This Method will perform sorting on MultiAccount results.
	 *
	 * @see
	 * com.apb.facades.customer.ApbCustomerFacade#sortMultiAccountResults(de.hybris.platform.commerceservices.search.
	 * pagedata.SearchPageData)
	 */
	@Override
	public void sortMultiAccountResults(final SearchPageData<B2BUnitData> searchPageData)
	{
		final List<B2BUnitData> b2bUnits = searchPageData.getResults();

		if (CollectionUtils.isNotEmpty(b2bUnits))
		{
			b2bUnits.sort((final B2BUnitData o1, final B2BUnitData o2) -> o1.getName().compareTo(o2.getName()));
			searchPageData.setResults(b2bUnits);
		}
	}

	/**
	 * The Method will check if the current user has the payer account permission.
	 *
	 * @return boolean
	 */
	@Override
	public boolean isSAMPayAccessEnable()
	{
		final UserModel userModel = getUserService().getCurrentUser();
		if (userModel instanceof B2BCustomerModel)
		{

			final B2BCustomerModel customerModel = ((B2BCustomerModel) userModel);
			final B2BUnitModel soldToAccount = customerModel.getDefaultB2BUnit();
			if (soldToAccount instanceof AsahiB2BUnitModel)
			{
				final AsahiB2BUnitModel payerAccount = ((AsahiB2BUnitModel) soldToAccount).getPayerAccount();

				for (final AsahiSAMAccessModel samAccessModel : CollectionUtils.emptyIfNull(customerModel.getSamAccess()))
				{
					if (null != samAccessModel.getPayer() && payerAccount.getUid().equalsIgnoreCase(samAccessModel.getPayer().getUid())
							&& (samAccessModel.isPayAccess()))
					{
						return true;

					}
				}
			}
		}
		return false;
	}

	/**
	 * The Method will update the boolean attribute if customer logged in earlier.
	 *
	 * @return boolean
	 */
	@Override
	public void updateCustomerLoggedIn(final UserModel user)
	{
		if (user instanceof B2BCustomerModel)
		{
			final B2BCustomerModel customer = ((B2BCustomerModel) user);
			customer.setLoggedInBefore(false);
			getModelService().save(customer);
		}
	}

	@Override
	public String approveORRejectPayAccess(String samAccess, final String pk)
	{
		String response = null;
		if (StringUtils.isNotEmpty(pk) && StringUtils.isNotEmpty(samAccess)
				&& (samAccess.equalsIgnoreCase(ApbCoreConstants.PAYER_ACCESS_APPROVE)
						|| samAccess.equalsIgnoreCase(ApbCoreConstants.PAYER_ACCESS_REJECT)))
		{
			try
			{
				final AsahiSAMAccessModel accessModel = this.getModelService().get(PK.parse(pk));

				if (null != accessModel)
				{
					if (accessModel.isApprovalDenied() || !accessModel.isPendingApproval())
					{
						return ApbCoreConstants.PAYER_ACCESS_COMPLETED;
					}

					if (!asahiDateUtil.validateDate(accessModel.getRequestDate()))
					{
						samAccess = ApbCoreConstants.PAYER_ACCESS_EXPIRED;
					}
					response = apbCustomerAccountService.updateAndNotifyPayAccess(accessModel.getB2bCustomer(), accessModel,
							samAccess);
				}
			}
			catch (final Exception exp)
			{
				LOG.error("Error in getting the Access Model");
			}
		}
		return response;
	}

	/**
	 * The Method will update the payer access and trigger the email notification
	 *
	 * @param samAccess
	 * @return String
	 */
	@Override
	public Boolean requestOrderORPayAccess(final String samAccess)
	{
		final UserModel userModel = getUserService().getCurrentUser();
		if (userModel instanceof B2BCustomerModel)
		{
			final B2BCustomerModel customerModel = ((B2BCustomerModel) userModel);
			final AsahiB2BUnitModel defaultUnit = (AsahiB2BUnitModel) customerModel.getDefaultB2BUnit();
			String customerType = null;
			AsahiSAMAccessModel access = null;
			Boolean isOrderAccessValid = Boolean.FALSE;
			Boolean isPayAccessValid = Boolean.FALSE;

			//If the User Account has been eligible for Order Access...
			if (null != defaultUnit.getBackendCustomerType())
			{
				customerType = typeService.getEnumerationValue(defaultUnit.getBackendCustomerType()).getName();
				if (null != customerType && customerType.equalsIgnoreCase(BackendCustomerType.SOLD_TO.toString())
						&& null != defaultUnit.getEclAccountGroupId()
						&& defaultUnit.getEclAccountGroupId().getCode().equalsIgnoreCase("Z001"))
				{
					isOrderAccessValid = Boolean.TRUE;
				}
			}

			//If the User Account has been eligible for Pay Access...
			if (null != defaultUnit.getPayerAccount() && StringUtils.isNotBlank(defaultUnit.getPayerAccount().getEmailAddress()))
			{
				isPayAccessValid = Boolean.TRUE;
			}

			//Check if the SAM Access Model Exists for the User and Account...
			if (null != defaultUnit.getPayerAccount())
			{
				access = apbCustomerAccountService.getAccessModel(customerModel, defaultUnit.getPayerAccount());
			}

			//If SAM Access Model does not exists...
			//When user registered with the order access only...
			//Case 1 : Then user request for Pay access...User will granted with Order And Pay Access...
			if (access == null && samAccess.equalsIgnoreCase(ApbCoreConstants.PAY_ACCESS) && isPayAccessValid)
			{
				final AsahiSAMAccessModel accessModel = apbB2BUnitService.updateSamAccessByUser(customerModel, defaultUnit, access,
						ApbCoreConstants.PAY_AND_ORDER_ACCESS);
				apbCustomerAccountService.updateAndNotifyPayAccess(customerModel, accessModel, PAY_ACCESS_REQUEST_EMAIL_CONSTANT);
				return Boolean.TRUE;
			}

			//If the SAM Access Model Exists....User has requested for ORDER Access
			//If the User Registered with PAY_ONLY Access and access is awaiting...
			//Case 2 : User requested for ORDER_ONLY access...The ORDER_ONLY Access is granted...
			//If the User Registered with PAY_ONLY Access and access is Denied earlier...
			//Case 3 : User requested for ORDER_ONLY access...The ORDER_ONLY Access is granted...
			//If the User Registered with PAY_ONLY Access and access is Approved earlier...
			//Case 4 : User requested for ORDER_ONLY access...The ORDER_AND_PAY Access is granted...
			if (null != access && samAccess.equalsIgnoreCase(ApbCoreConstants.ORDER_ACCESS) && isOrderAccessValid)
			{
				apbB2BUnitService.updateSamAccessByUser(customerModel, defaultUnit, access, ApbCoreConstants.UPDATE_ORDER_ACCESS);
				return Boolean.TRUE;
			}
			//If the SAM Access Model Exists....
			//If the User Registered with ORDER Access and access is available/unavailable ...
			//Case 5 : User requested for PAY_ONLY/PAY_AND_ORDER access...
			else if (null != access && samAccess.equalsIgnoreCase(ApbCoreConstants.PAY_ACCESS) && isPayAccessValid)
			{
				final AsahiSAMAccessModel accessModel = apbB2BUnitService.updateSamAccessByUser(customerModel, defaultUnit, access,
						access.isOrderAccess() ? ApbCoreConstants.PAY_AND_ORDER_ACCESS : ApbCoreConstants.PAY_ACCESS);
				apbCustomerAccountService.updateAndNotifyPayAccess(customerModel, accessModel, PAY_ACCESS_REQUEST_EMAIL_CONSTANT);
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	/**
	 * The Method will validate the Pay Access for the user, Send the expiry Email to user in case request for access
	 * expire. Update the Pay Access Model in case the Request Expire.
	 *
	 * @param user
	 */
	@Override
	public void validatePayAccess(final UserModel user)
	{
		if (user instanceof B2BCustomerModel)
		{
			final B2BCustomerModel customer = ((B2BCustomerModel) user);
			final AsahiB2BUnitModel defaultUnit = (AsahiB2BUnitModel) customer.getDefaultB2BUnit();
			if (null != defaultUnit.getPayerAccount())
			{
				final AsahiSAMAccessModel accessModel = apbCustomerAccountService.getAccessModel(customer,
						defaultUnit.getPayerAccount());

				if (null != accessModel && accessModel.isPendingApproval() && (!accessModel.isApprovalDenied())
						&& (!asahiDateUtil.validateDate(accessModel.getRequestDate())))
				{
					//send the expire email to the user...
					apbCustomerAccountService.updateAndNotifyPayAccess(accessModel.getB2bCustomer(), accessModel,
							ApbCoreConstants.PAYER_ACCESS_EXPIRED);
				}
			}
		}
	}

	@Override
	public boolean isOnAccountPaymentRestricted()
	{
		final AsahiB2BUnitModel b2BUnit = apbB2BUnitService.getCurrentB2BUnit().getPayerAccount();
		final Set<String> restrictedCodes = enumerationService.getEnumerationValues(AsahiOnAccountRestrictedCode.class).stream()
				.map(enumValue -> enumValue.getCode()).collect(Collectors.toSet());
		return restrictedCodes.contains(b2BUnit.getPaymentTerm());
	}

	/**
	 * Gets the paged enquiry history for statuses.
	 *
	 * @param pageableData
	 *           the pageable data
	 * @return the paged enquiries for b2bunit
	 * @throws ParseException
	 */

	@Override
	public SearchPageData<ApbContactUsData> getAllEnquiries(final PageableData pageableData) throws ParseException
	{
		final AsahiB2BUnitModel b2bUnitModel = apbB2BUnitService.getCurrentB2BUnit();
		LOG.info("startdate in enquiries is", pageableData.getStartDate());
		final SearchPageData<CsTicketModel> enquiries = getApbCustomerAccountService().getAllEnquiries(b2bUnitModel, pageableData, b2bUnitModel.getCooDate());
		final SearchPageData<ApbContactUsData> result = new SearchPageData<ApbContactUsData>();
		result.setPagination(enquiries.getPagination());
		result.setSorts(enquiries.getSorts());
		result.setResults(Converters.convertAll(enquiries.getResults(), getAsahiEnquiryConverter()));
		return result;

	}

	public void setAsahiEnquiryConverter(final Converter<CsTicketModel, ApbContactUsData> asahiEnquiryConverter)
	{
		this.asahiEnquiryConverter = asahiEnquiryConverter;
	}

	public Converter<CsTicketModel, ApbContactUsData> getAsahiEnquiryConverter()
	{
		return asahiEnquiryConverter;
	}

	public void setRestrictedCategoriesInSession()
	{
		apbCustomerAccountService.setRestrictedCategoriesInSession();
	}


	/**
	 * @param b2bUnitModel
	 * @param emailAddress
	 * @param bdeCustomer
	 * @return
	 */
	private BDECustomerModel getOrCreateAsahiBDECustomer(final B2BUnitModel b2bUnitModel, final String emailAddress,
			BDECustomerModel bdeCustomer)
	{
		final String uid = EMAIL_PREFIX + b2bUnitModel.getUid().replaceAll(REGEX_WHITE_SPACE, StringUtils.EMPTY)
				+ ASAHI_EMAIL_SUFFIX;

		bdeCustomer = sabmB2BCustomerService.getBDECustomer(uid);


		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>();
		if (bdeCustomer == null)
		{
			bdeCustomer = getModelService().create(BDECustomerModel.class);
			bdeCustomer.setUid(uid);
			if (StringUtils.isEmpty(emailAddress))
			{
				bdeCustomer.setEmail(uid);
			}
			else
			{
				bdeCustomer.setEmail(emailAddress);
			}
		}

		if (b2bUnitModel instanceof AsahiB2BUnitModel)
		{
			bdeCustomer.setIsAsahiUser(true);
		}
		/*
		 * update existing bdeCustomer's email address if it is different
		 */

		if (StringUtils.isNotEmpty(emailAddress) && !emailAddress.equalsIgnoreCase(bdeCustomer.getEmail()))
		{
			bdeCustomer.setEmail(emailAddress);

		}


		bdeCustomer.setName(getUserService().getCurrentUser().getName());
		bdeCustomer.setFirstName(getUserService().getCurrentUser().getName());
		groups.add(b2bUnitModel);
		for (final PrincipalModel member : SetUtils.emptyIfNull(b2bUnitModel.getMembers()))
		{
			if (member instanceof B2BUnitModel)
			{
				groups.add((B2BUnitModel) member);
			}
		}

		groups.add(getUserService().getUserGroupForUID(DEFAULT_B2BCUSTOMER_USERGROUP));
		groups.add(getUserService().getUserGroupForUID(DEFAULT_BDECUSTOMER_USERGROUP));
		bdeCustomer.setGroups(groups);
		bdeCustomer.setDefaultB2BUnit(b2bUnitModel);
		bdeCustomer.setAdminUnits(Arrays.asList((AsahiB2BUnitModel) b2bUnitModel));

		//Create Pay and Order SAM Access and not sending email
		final AsahiSAMAccessModel accessModel = apbB2BUnitService.createSamAccess(ApbCoreConstants.PAY_AND_ORDER_ACCESS,
				bdeCustomer, b2bUnitModel.getUid());
		accessModel.setPendingApproval(Boolean.FALSE);
		accessModel.setApprovalDenied(Boolean.FALSE);
		bdeCustomer.setSamAccess(Arrays.asList(accessModel));

		final String password = generateComplexPassword();
		getUserService().setPassword(bdeCustomer, password, DEFAULT_PASSWORD_ENCODING);
		getModelService().save(bdeCustomer);
		getModelService().save(accessModel);
		getModelService().refresh(bdeCustomer);
		return bdeCustomer;

	}

	public boolean isCustomerActiveForCUB(final B2BCustomerModel customer)
	{
		return SabmUtils.isCustomerActiveForCUB(customer);
	}

}
