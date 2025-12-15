/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.sabmiller.storefront.controllers.pages;

import com.sabmiller.facades.notification.SABMNotificationFacade;

import de.hybris.platform.acceleratorcms.model.components.SimpleBannerComponentModel;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.UpdateEmailForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.UpdatePasswordForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.AddressValidator;
import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.EmailValidator;
import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.PasswordValidator;
import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.ProfileValidator;
import de.hybris.platform.acceleratorstorefrontcommons.forms.verification.AddressVerificationResultHandler;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import de.hybris.platform.commercefacades.address.AddressVerificationFacade;
import de.hybris.platform.commercefacades.address.data.AddressVerificationResult;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.converters.populator.GroupCartModificationListPopulator;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;

import com.sabmiller.merchantsuiteservices.data.InvoicePaymentData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.PrincipalGroupData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commercefacades.user.data.TitleData;
import de.hybris.platform.commercefacades.user.exceptions.PasswordMismatchException;
import de.hybris.platform.commerceservices.address.AddressVerificationDecision;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.util.Config;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.sabmiller.commons.constants.SabmcommonsConstants;
import com.sabmiller.commons.enumerations.OrderToCartStatus;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.model.SABMNotificationModel;
import com.sabmiller.core.model.SABMNotificationPrefModel;
import com.sabmiller.core.util.SabmDateUtils;
import com.sabmiller.core.util.SabmUtils;
import com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade;
import com.sabmiller.facades.cart.SABMCartFacade;
import com.sabmiller.facades.customer.SABMCustomerFacade;
import com.sabmiller.facades.customer.SABMInvoiceFacade;
import com.sabmiller.facades.customer.data.RemoveCustomerJson;
import com.sabmiller.facades.invoice.SABMInvoiceDiscrepancyData;
import com.sabmiller.facades.invoice.SABMInvoiceList;
import com.sabmiller.facades.invoice.SABMInvoicePDFData;
import com.sabmiller.facades.invoice.SABMInvoicePageData;
import com.sabmiller.facades.invoice.SABMInvoiceValidationResult;
import com.sabmiller.facades.order.SABMOrderFacade;
import com.sabmiller.facades.order.data.OrderTemplateData;
import com.sabmiller.facades.order.json.OrderHistoryJson;
import com.sabmiller.facades.product.data.UomData;
import com.sabmiller.facades.util.SabmFeatureUtil;
import com.sabmiller.merchantsuiteservices.facade.SABMMerchantSuitePaymentFacade;
import com.sabmiller.merchantsuiteservices.facade.impl.SABMMerchantSuitePaymentFacadeImpl;
import com.sabmiller.storefront.controllers.ControllerConstants;
import com.sabmiller.storefront.controllers.SABMWebConstants;
import com.sabmiller.storefront.filters.XSSFilterUtil;
import com.sabmiller.storefront.form.SABMRemoveUserForm;
import com.sabmiller.storefront.form.SABMUpdateOrderTemplateForm;
import com.sabmiller.storefront.form.SABMUpdateQuantityForm;
import com.sabmiller.storefront.form.UpdateProfileForm;
import com.apb.core.util.AsahiCoreUtil;
import com.sabmiller.storefront.controllers.pages.SabmAbstractSearchPageController;

/**
 * Controller for home page
 */
@Controller
@Scope("tenant")
@RequestMapping("/your-business")
public class AccountPageController extends SabmAbstractSearchPageController {
    // Internal Redirects
    private static final String REDIRECT_TO_ADDRESS_BOOK_PAGE = REDIRECT_PREFIX + "/your-business/address-book";
    private static final String REDIRECT_TO_PAYMENT_INFO_PAGE = REDIRECT_PREFIX + "/your-business/payment-details";
    private static final String REDIRECT_TO_EDIT_ADDRESS_PAGE = REDIRECT_PREFIX + "/your-business/edit-address/";
    private static final String REDIRECT_TO_UPDATE_EMAIL_PAGE = REDIRECT_PREFIX + "/your-business/update-email";
    private static final String REDIRECT_TO_UPDATE_PROFILE = REDIRECT_PREFIX + "/your-business/update-profile";
    private static final String REDIRECT_TO_PASSWORD_UPDATE_PAGE = REDIRECT_PREFIX + "/your-business/update-password";
    private static final String REDIRECT_TO_ORDER_HISTORY_PAGE = REDIRECT_PREFIX + "/your-business/orders";
    private static final String REDIRECT_TO_PROFILE = REDIRECT_PREFIX + "/your-business/profile";

    private static final String SHOWN_PRODUCT_COUNT = "sabmstorefront.storefront.minicart.shownProductCount";

    /**
     * We use this suffix pattern because of an issue with Spring 3.1 where a Uri value is incorrectly extracted if it
     * contains on or more '.' characters. Please see https://jira.springsource.org/browse/SPR-6164 for a discussion on
     * the issue and future resolution.
     */
    private static final String DOCUMENT_NUMBER_PATH_VARIABLE_PATTERN = "{docNum:.*}";
    private static final String ORDER_CODE_PATH_VARIABLE_PATTERN = "{orderCode:.*}";
    private static final String ADDRESS_CODE_PATH_VARIABLE_PATTERN = "{addressCode:.*}";

    // CMS Pages
    private static final String ACCOUNT_CMS_PAGE = "account";
    private static final String PROFILE_CMS_PAGE = "profile";
    private static final String BILLING_CMS_PAGE = "billing";
    private static final String BILLING_CONFIRMATION_CMS_PAGE = "paymentConfirmation";
    private static final String UPDATE_PASSWORD_CMS_PAGE = "updatePassword";
    private static final String UPDATE_PROFILE_CMS_PAGE = "update-profile";
    private static final String UPDATE_EMAIL_CMS_PAGE = "update-email";
    private static final String ADDRESS_BOOK_CMS_PAGE = "address-book";
    private static final String ADD_EDIT_ADDRESS_CMS_PAGE = "add-edit-address";
    private static final String PAYMENT_DETAILS_CMS_PAGE = "payment-details";
    private static final String ORDER_HISTORY_CMS_PAGE = "orders";
    private static final String ORDER_TEMPLATES_CMS_PAGE = "orderTemplates";
    private static final String ORDER_TEMPLATE_DETAIL_CMS_PAGE = "orderTemplateDetail";
    private static final String ORDER_DETAIL_CMS_PAGE = "order";
    private static final String ACCESS_GROUPS_INVOICE = "b2binvoicecustomer";
    private static final String ACCESS_GROUPS_ORDER = "b2bordercustomer";

    private static final String PAGE_SECTION_ORDER_HISTORY = "Order History";
    private static final String PAGE_SECTION_ORDER_TEMPLATES = "Order Templates";

    private  static final String TEST_MODE="merchant.suite.test.mode";

    /**
     * The Constant raise_invoice_discrepancy_CMS_PAGE.
     */
    private static final String INVOICEDISCREPANCY_CMS_PAGE = "invoiceDiscrepancyPage";
    private static final String RAISEDINVOICEDISCREPANCY_CMS_PAGE = "raisedInvoiceDiscrepancyPage";

    private static final Logger LOG = LoggerFactory.getLogger(AccountPageController.class);

    @Resource(name = "orderFacade")
    private SABMOrderFacade orderFacade;

    @Resource(name = "acceleratorCheckoutFacade")
    private CheckoutFacade checkoutFacade;

    @Resource(name = "userFacade")
    private UserFacade userFacade;

    @Resource(name = "customerFacade")
    private SABMCustomerFacade customerFacade;

    @Resource(name = "b2bCommerceUnitFacade")
    private SabmB2BCommerceUnitFacade b2bUnitFacade;

    @Resource(name = "accountBreadcrumbBuilder")
    private ResourceBreadcrumbBuilder accountBreadcrumbBuilder;

    @Resource(name = "passwordValidator")
    private PasswordValidator passwordValidator;

    @Resource(name = "addressValidator")
    private AddressValidator addressValidator;

    @Resource(name = "profileValidator")
    private ProfileValidator profileValidator;

    @Resource(name = "emailValidator")
    private EmailValidator emailValidator;

    @Resource(name = "i18NFacade")
    private I18NFacade i18NFacade;

    @Resource(name = "addressVerificationFacade")
    private AddressVerificationFacade addressVerificationFacade;

    @Resource(name = "addressVerificationResultHandler")
    private AddressVerificationResultHandler addressVerificationResultHandler;

    @Resource(name = "groupCartModificationListPopulator")
    private GroupCartModificationListPopulator groupCartModificationListPopulator;

    @Resource(name = "siteConfigService")
    private SiteConfigService siteConfigService;
    
    @Resource(name = "b2bUnitService")
 	 private SabmB2BUnitService b2bUnitService;

    @Resource(name = "cartFacade")
    private SABMCartFacade cartFacade;

    @Resource(name = "sabmCustomerFacade")
    private SABMCustomerFacade sabmCustomerFacade;

    @Resource(name = "configurationService")
    private ConfigurationService configurationService;

    @Resource(name = "cmsComponentService")
    private CMSComponentService cmsComponentService;

    @Resource(name = "mediaService")
    private MediaService mediaService;

    @Resource(name = "accProductFacade")
    private ProductFacade productFacade;

    @Resource(name = "sabmFeatureUtil")
    private SabmFeatureUtil sabmFeatureUtil;

    @Resource(name = "sabmMobileNumberValidator")
    private Validator sabmMobileNumberValidator;

    @Resource(name = "invoiceFacade")
    private SABMInvoiceFacade invoiceFacade;

    @Resource
    private AsahiCoreUtil asahiCoreUtil;
    @Resource
    private SessionService sessionService;
    
	@Resource(name = "notificationFacade")
	private SABMNotificationFacade notificationFacade;
	

    @Resource
    private SABMMerchantSuitePaymentFacadeImpl sabmMerchantSuitePaymentFacade;

    private static final String BUSINESS_CMS_PAGE = "your-business-overview";

    private static final String BUSINESSUNITS_CMS_PAGE = "businessUnits";
    private static final String BUSINESSUNITDETAILS_CMS_PAGE = "businessUnitDetail";

    private static final String MANAGE_BUSINESSUNITS_URL = "/your-business/businessunits";
    private static final String MANAGE_BUSINESSUNITDETAILS_URL = "/your-business/unitsdetails/";
    private static final String BUSINESS_UID_PATH_VARIABLE_PATTERN = "/{b2bUnitId:.*}";

    private static final String YOUR_ACCOUNT_PAGE = "/your-business/billing";
    private static final String INVOICE_DISCREPANCY_PAGE = "/your-business/invoicediscrepancy";
    private static final String RAISED_INVOICE_DISCREPANCY_PAGE = "/your-business/raisedinvoicediscrepancy";

    @Resource
    private SABMMerchantSuitePaymentFacade sabmMerchantSuitePaymentFacadeImpl;

    protected PasswordValidator getPasswordValidator() {
        return passwordValidator;
    }

    protected AddressValidator getAddressValidator() {
        return addressValidator;
    }

    protected ProfileValidator getProfileValidator() {
        return profileValidator;
    }

    protected EmailValidator getEmailValidator() {
        return emailValidator;
    }

    protected I18NFacade getI18NFacade() {
        return i18NFacade;
    }

    protected AddressVerificationFacade getAddressVerificationFacade() {
        return addressVerificationFacade;
    }

    protected AddressVerificationResultHandler getAddressVerificationResultHandler() {
        return addressVerificationResultHandler;
    }

    @ModelAttribute("countries")
    public Collection<CountryData> getCountries() {
        return checkoutFacade.getDeliveryCountries();
    }

    @ModelAttribute("titles")
    public Collection<TitleData> getTitles() {
        return userFacade.getTitles();
    }

    @ModelAttribute("countryDataMap")
    public Map<String, CountryData> getCountryDataMap() {
        final Map<String, CountryData> countryDataMap = new HashMap<>();
        for (final CountryData countryData : getCountries()) {
            countryDataMap.put(countryData.getIsocode(), countryData);
        }
        return countryDataMap;
    }

    @GetMapping("/addressform")
    public String getCountryAddressForm(@RequestParam("addressCode") final String addressCode,
            @RequestParam("countryIsoCode") final String countryIsoCode, final Model model) {
        model.addAttribute("supportedCountries", getCountries());
        model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(countryIsoCode));
        model.addAttribute("country", countryIsoCode);

        final AddressForm addressForm = new AddressForm();
        model.addAttribute("addressForm", addressForm);
        for (final AddressData addressData : userFacade.getAddressBook()) {
            if (addressData.getId() != null && addressData.getId().equals(addressCode) && countryIsoCode
                    .equals(addressData.getCountry().getIsocode())) {
                model.addAttribute("addressData", addressData);
                addressForm.setAddressId(addressData.getId());
                addressForm.setTitleCode(addressData.getTitleCode());
                addressForm.setFirstName(addressData.getFirstName());
                addressForm.setLastName(addressData.getLastName());
                addressForm.setLine1(addressData.getLine1());
                addressForm.setLine2(addressData.getLine2());
                addressForm.setTownCity(addressData.getTown());
                addressForm.setPostcode(addressData.getPostalCode());
                addressForm.setCountryIso(addressData.getCountry().getIsocode());
                addressForm.setPhone(addressData.getPhone());

                if (addressData.getRegion() != null && !StringUtils.isEmpty(addressData.getRegion().getIsocode())) {
                    addressForm.setRegionIso(addressData.getRegion().getIsocode());
                }

                break;
            }
        }
        return ControllerConstants.Views.Fragments.Account.CountryAddressForm;
    }

    @GetMapping("/overview")
    @RequireHardLogIn
    public String account(final Model model) throws CMSItemNotFoundException {
        storeCmsPageInModel(model, getContentPageForLabelOrId(ACCOUNT_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ACCOUNT_CMS_PAGE));
        model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs(null));
        model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
        return getViewForPage(model);
    }

    @GetMapping("/orders")
    @RequireHardLogIn
    public String orders(@RequestParam(value = "page", defaultValue = "0") final int page,
            @RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
            @RequestParam(value = "sort", required = false) final String sortCode, final Model model) throws CMSItemNotFoundException {

        storeCmsPageInModel(model, getContentPageForLabelOrId(ORDER_HISTORY_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORDER_HISTORY_CMS_PAGE));
        model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.orderHistory"));
        model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
        return ControllerConstants.Views.Pages.Account.AccountOrderHistoryPage;
    }

    @RequestMapping(value = "/ordersjson", method = { RequestMethod.GET, RequestMethod.POST })
    @RequireHardLogIn
    @ResponseBody
    public List<OrderHistoryJson> ordersJson(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyyMMdd") final Date dateFrom,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyyMMdd") final Date dateTo) {
        return orderFacade.getOrderHistory(dateFrom, dateTo);
    }

    @GetMapping("/order/" + ORDER_CODE_PATH_VARIABLE_PATTERN)
    @RequireHardLogIn
    public String order(@PathVariable("orderCode") final String orderCode, final Model model, final RedirectAttributes redirectModel)
            throws CMSItemNotFoundException {
        try {
            final OrderData orderDetails = orderFacade.getOrderDetailsForCode(orderCode);
            model.addAttribute("orderData", orderDetails);
            model.addAttribute("isTrackDeliveryOrderFeatureEnabled",
                    sabmFeatureUtil.isFeatureEnabled(SabmcommonsConstants.TRACK_DELIVERY_ORDER));

            final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);
            breadcrumbs.add(new Breadcrumb("/your-business/orders",
                    getMessageSource().getMessage("text.account.orderHistory", null, getI18nService().getCurrentLocale()), null));
            breadcrumbs.add(new Breadcrumb("#", getMessageSource()
                    .getMessage("text.account.order.orderBreadcrumb", new Object[] { orderDetails.getSapSalesOrderNumber() }, "Order {0}",
                            getI18nService().getCurrentLocale()), null));
            model.addAttribute("breadcrumbs", breadcrumbs);

        } catch (final UnknownIdentifierException e) {
            LOG.warn("Attempted to load a order that does not exist or is not visible", e);
            GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "system.error.page.not.found", null);
            return REDIRECT_TO_ORDER_HISTORY_PAGE;
        }
        storeCmsPageInModel(model, getContentPageForLabelOrId(ORDER_DETAIL_CMS_PAGE));
        model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORDER_DETAIL_CMS_PAGE));

        return ControllerConstants.Views.Pages.Account.AccountOrderDetailsPage;
    }

    @GetMapping("/ordertemplates")
    @RequireHardLogIn
    public String orderTemplates(final Model model) throws CMSItemNotFoundException {
   	 
   	 if (asahiCoreUtil.isNAPUser()) {
				return FORWARD_PREFIX + "/404";
			}
   	 
        storeCmsPageInModel(model, getContentPageForLabelOrId(ORDER_TEMPLATES_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORDER_TEMPLATES_CMS_PAGE));
        model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.orderTemplates"));
        model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);

        model.addAttribute("orderTemplates", b2bUnitFacade.getB2BUnitOrderTemplates());

        return ControllerConstants.Views.Pages.Account.AccountOrderTemplatePage;
    }

    @GetMapping("/orderTemplateDetail/" + ORDER_CODE_PATH_VARIABLE_PATTERN)
    @RequireHardLogIn
    public String getOrderTemplateDetail(@PathVariable("orderCode") final String orderCode, final Model model)
            throws CMSItemNotFoundException {
   	 
   	 if (asahiCoreUtil.isNAPUser()) {
				return FORWARD_PREFIX + "/404";
			}
   	 
        final OrderTemplateData templateData = b2bUnitFacade.getB2BUnitOrderTemplateDetail(orderCode);

        if (templateData != null) {
            storeCmsPageInModel(model, getContentPageForLabelOrId(ORDER_TEMPLATE_DETAIL_CMS_PAGE));
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORDER_TEMPLATE_DETAIL_CMS_PAGE));

            model.addAttribute("orderTemplate", b2bUnitFacade.getB2BUnitOrderTemplateDetail(templateData.getCode()));

            final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);
            if (CollectionUtils.isNotEmpty(breadcrumbs)) {
                breadcrumbs.get(breadcrumbs.size() - 1).setLinkClass(null);
            }
            breadcrumbs.add(new Breadcrumb("/your-business/ordertemplates",
                    getMessageSource().getMessage("text.account.orderTemplates", null, getI18nService().getCurrentLocale()), null));
            breadcrumbs.add(new Breadcrumb("/your-business/orderTemplateDetail/" + orderCode, templateData.getName(), null));
            model.addAttribute("breadcrumbs", breadcrumbs);

            model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
        } else {
            return REDIRECT_PREFIX + "/your-business/orderTemplateDetail";
        }

        try {
            final OrderTemplateData orderTemplateDetails = b2bUnitFacade.getB2BUnitOrderTemplateDetail(orderCode);
            model.addAttribute("orderTemplate", populateOrderTemplateDataProductDatas(orderTemplateDetails));
        } catch (Exception e) {
            LOG.error("Unable to get order template details for tag manager");
        }

        return ControllerConstants.Views.Pages.Account.AccountOrderTemplateDetailPage;
    }

    @PostMapping("/orderTemplateDetail/updateMinStock")
    @RequireHardLogIn
    @ResponseBody
    public Boolean updateMinimumStock(@RequestParam final String orderCode, @RequestParam final Integer entryNumber,
            @RequestParam final Integer minStockOnHand) {
        return b2bUnitFacade.updateMinimumStock(XSSFilterUtil.filter(orderCode), entryNumber, minStockOnHand);
    }

    @PostMapping("/orderTemplateDetail/updateTemplate")
    @RequireHardLogIn
    @ResponseBody
    public Boolean updateEntryOrderTemplateDetail(@RequestBody @Valid final SABMUpdateOrderTemplateForm form,
            final BindingResult bindingResult) throws UnsupportedEncodingException {

        //form.setName(XSSEncoder.encodeXML(XSSFilterUtil.filter(form.getName())));
        //form.setCode(XSSEncoder.encodeXML(XSSFilterUtil.filter(form.getCode())));

        boolean succes = false;

        if (bindingResult.hasErrors() && StringUtils.length(form.getName()) > 255) {
            return Boolean.FALSE;
        }

        if (form != null) {
            succes = b2bUnitFacade.updateProductToTemplateName(form.getCode(), StringUtils.trim(form.getName()));

            if (succes) {
                succes = this.updateAndRemoveProduct(form);
            }
        }

        return succes;
    }

    /**
     * @param form
     */
    private Boolean updateAndRemoveProduct(final SABMUpdateOrderTemplateForm form) {
        boolean succes = true;
        for (final SABMUpdateQuantityForm entryForm : form.getEntries()) {
            succes = b2bUnitFacade
                    .updateProductToTemplate(form.getCode(), entryForm.getEntryNumber(), entryForm.getQuantity(), entryForm.getUnit());
        }
        if (succes && StringUtils.isNotEmpty(form.getEntryNumber())) {
            final String[] entryNumberArray = form.getEntryNumber().split(",");
            //sort the product and remove it.
            Collections.sort(Arrays.asList(entryNumberArray));
            Collections.reverse(Arrays.asList(entryNumberArray));
            for (final String entryNumberForRemove : entryNumberArray) {
                if (StringUtils.isNotEmpty(entryNumberForRemove)) {
                    b2bUnitFacade.removeProductOrderTemplate(form.getCode(), Long.valueOf(entryNumberForRemove));
                }
            }

        }
        return succes;
    }

    @GetMapping("/ordertemplate/addToCart/" + ORDER_CODE_PATH_VARIABLE_PATTERN)
    @RequireHardLogIn
    public String addOrderTemplateToCart(@PathVariable("orderCode") final String orderCode, final RedirectAttributes redirectModel) {
        if (b2bUnitFacade.addOrderTemplateToCart(orderCode)) {
            GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "add.order.template.to.cart.succes");
        } else {
            GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "add.order.template.to.cart.error");
        }

        return REDIRECT_PREFIX + "/your-business/ordertemplates";
    }

    @PostMapping("/orderTemplateDetail/addToCart/" + ORDER_CODE_PATH_VARIABLE_PATTERN)
    @RequireHardLogIn
    public String addOrderTemplateDetailToCart(@RequestBody @Valid final SABMUpdateOrderTemplateForm form,
            final BindingResult bindingResult, final RedirectAttributes redirectModel, final Model model)
            throws UnsupportedEncodingException {
        String[] removeEntryNumberArray = null;
        if (StringUtils.isNotEmpty(form.getEntryNumber())) {
            removeEntryNumberArray = form.getEntryNumber().split(",");
            //sort the product and remove it.
            Collections.sort(Arrays.asList(removeEntryNumberArray));
            Collections.reverse(Arrays.asList(removeEntryNumberArray));
            for (final String entryNumberForRemove : removeEntryNumberArray) {
                if (StringUtils.isNotEmpty(entryNumberForRemove)) {
                    b2bUnitFacade.removeProductOrderTemplate(form.getCode(), Long.valueOf(entryNumberForRemove));
                }
            }

        }

        List<OrderEntryData> orderTemplateEntryList = new ArrayList<OrderEntryData>();
        for (final SABMUpdateQuantityForm entryForm : form.getEntries()) {
            if (removeEntryNumberArray == null || !ArrayUtils.contains(removeEntryNumberArray, entryForm.getEntryNumber())) {
                OrderEntryData entry = new OrderEntryData();
                entry.setEntryNumber(entryForm.getEntryNumber().intValue());
                entry.setQuantity(entryForm.getQuantity());
                final UomData uomData = new UomData();
                uomData.setCode(entryForm.getUnit());
                entry.setUnit(uomData);
                orderTemplateEntryList.add(entry);
            }
        }
        //b2bUnitFacade.addOrderTemplateToCart(form.getCode(),orderTemplateEntryList))

        final List<CartModificationData> cartModificationDatas = b2bUnitFacade
                .addOrderTemplateToCart(form.getCode(), orderTemplateEntryList);
        if (CollectionUtils.isNotEmpty(cartModificationDatas)) {
            groupCartModificationListPopulator.populate(null, cartModificationDatas);

            model.addAttribute("modifications", cartModificationDatas);
        }
        model.addAttribute("cartData", cartFacade.getSessionMiniCart());
        model.addAttribute("numberShowing", Integer.valueOf(Config.getInt(SHOWN_PRODUCT_COUNT, 3)));

        model.addAttribute("requestOrigin", new StringBuilder().append(SabmUtils.HOME).append("/").append(PAGE_SECTION_ORDER_TEMPLATES));

        try {
            final OrderTemplateData orderTemplateDetails = b2bUnitFacade.getB2BUnitOrderTemplateDetail(form.getCode());
            model.addAttribute("orderData", populateOrderTemplateDataProductDatas(orderTemplateDetails));
        } catch (Exception e) {
            LOG.error("Unable to get order template details for tag manager");
        }

        return ControllerConstants.Views.Fragments.Cart.AddToCartPopup;
    }

    @PostMapping("/ordertemplates/move")
    @RequireHardLogIn
    public String rankOrderTemplates(@RequestParam final String orderCode, @RequestParam final Boolean directionUp, final Model model) {
        b2bUnitFacade.moveOrderTemplates(XSSFilterUtil.filter(orderCode), BooleanUtils.isTrue(directionUp));

        model.addAttribute("orderTemplates", b2bUnitFacade.getB2BUnitOrderTemplates());

        return ControllerConstants.Views.Pages.Account.AccountOrderTemplateList;
    }

    @PostMapping("/orderTemplateDetail/move")
    @RequireHardLogIn
    public String rankOrderTemplateDetails(@RequestParam final String orderCode, @RequestParam final Integer newEntryNum,
            @RequestParam final Integer entryNumber, final Model model) {
        b2bUnitFacade.moveOrderEntry(XSSFilterUtil.filter(orderCode), entryNumber, newEntryNum);

        model.addAttribute("orderTemplate", b2bUnitFacade.getB2BUnitOrderTemplateDetail(XSSFilterUtil.filter(orderCode)));

        return ControllerConstants.Views.Pages.Account.AccountOrderTemplateDetailPage;
    }

    @PostMapping("/ordertemplates/sort")
    @RequireHardLogIn
    public String rankOrderTemplates(@RequestParam final String sort, final Model model) {
        if (StringUtils.isEmpty(sort)) {
            model.addAttribute("orderTemplates", b2bUnitFacade.getB2BUnitOrderTemplates());
        } else {
            model.addAttribute("orderTemplates", b2bUnitFacade.getOrderTemplatesNameSorted(BooleanUtils.toBoolean(sort)));
        }

        return ControllerConstants.Views.Pages.Account.AccountOrderTemplateList;
    }

    @GetMapping("/ordertemplate/delete/" + ORDER_CODE_PATH_VARIABLE_PATTERN)
    @RequireHardLogIn
    public String deleteOrderTemplate(@PathVariable("orderCode") final String orderCode, final RedirectAttributes redirectModel) {

        if (b2bUnitFacade.deleteTemplate(orderCode)) {
            GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "delete.order.template.success");
            return REDIRECT_PREFIX + "/your-business/ordertemplates";
        }

        GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "delete.order.template.error");
        return REDIRECT_PREFIX + "/your-business/orderTemplateDetail/" + orderCode;
    }

    @GetMapping("/ordertemplate/print/" + ORDER_CODE_PATH_VARIABLE_PATTERN)
    @RequireHardLogIn
    public void generatePdfReport(@PathVariable("orderCode") final String orderCode, HttpServletResponse response)
            throws CMSItemNotFoundException, IOException ,JRException{

        LOG.debug("--------------generate order template PDF ----------");

        final OrderTemplateData templateData = b2bUnitFacade.getB2BUnitOrderTemplateDetail(orderCode);

        if (templateData != null) {

            Map<String, Object> parameterMap = new HashMap<>();

            List<OrderEntryData> entries = templateData.getEntries();
            SabmUtils.getImageUrl(entries);

            JRDataSource JRdataSource = new JRBeanCollectionDataSource(entries);

            SimpleBannerComponentModel component = (SimpleBannerComponentModel) cmsComponentService
                    .getAbstractCMSComponent("FullLogoWhiteComponent");

            parameterMap.put("datasource", JRdataSource);
            parameterMap.put("logo", mediaService.getStreamFromMedia(component.getMedia()));
            parameterMap.put("templateName", Jsoup.parse(templateData.getName()).text());
            parameterMap.put(JRParameter.REPORT_LOCALE, getI18nService().getCurrentLocale());
          // Fetching the .jrxml file from the resources folder.
          final InputStream inputStream = this.getClass().getResourceAsStream("/jasperreports/order-template-report.jrxml");
   
          // Compile the Jasper report from .jrxml to .japser
          
          final JasperReport report = JasperCompileManager.compileReport(inputStream);      
          final JasperPrint print = JasperFillManager.fillReport(report, parameterMap, JRdataSource);
          inputStream.close();         
          response.setContentType("application/pdf");
          response.setHeader("Content-Disposition", "inline;filename= "+ templateData.getCode());  
          // Export the report to a PDF file.
          JasperExportManager.exportReportToPdfStream(print,response.getOutputStream());
         
          //response.getOutputStream().close();
                 
       }        

    }

    private OrderData populateOrderDataProductDatas(final OrderData orderDetails) {

        for (final OrderEntryData orderEntryData : orderDetails.getEntries()) {
            orderEntryData.setProduct(productFacade.getProductForCodeAndOptions(orderEntryData.getProduct().getBaseProduct(),
                    Arrays.asList(ProductOption.BASIC, ProductOption.CATEGORIES)));
        }

        return orderDetails;
    }

    private OrderTemplateData populateOrderTemplateDataProductDatas(OrderTemplateData orderTemplateDetails) {

        for (final OrderEntryData orderEntryData : orderTemplateDetails.getEntries()) {
            orderEntryData.setProduct(productFacade.getProductForCodeAndOptions(orderEntryData.getProduct().getBaseProduct(),
                    Arrays.asList(ProductOption.BASIC, ProductOption.CATEGORIES)));
        }

        return orderTemplateDetails;
    }

    /**
     * @param orderCode
     * @param model
     * @param redirectModel
     * @return AddToCartPopup
     * @throws CMSItemNotFoundException
     * @author yuxiao.wang
     */
    // FRAMEWORK_UPDATE - TODO - according to the new guide paths should not contain a trailing slash.
    @PostMapping(value = "/orderAdd", produces = MediaType.APPLICATION_JSON_VALUE)
    public String orderAddToCart(@RequestParam("orderCode") final String orderCode, final Model model,
            final RedirectAttributes redirectModel) throws CMSItemNotFoundException {
        final Map<OrderToCartStatus, Object> mapReturn = orderFacade.addToCartForOrderCode(XSSFilterUtil.filter(orderCode));
        return orderAddToCartPopulateModel(XSSFilterUtil.filter(orderCode), mapReturn, model);
    }

    // FRAMEWORK_UPDATE - TODO - according to the new guide paths should not contain a trailing slash.
    @PostMapping(value = "/orderItemsAdd", produces = MediaType.APPLICATION_JSON_VALUE)
    public String orderAddToCart(@RequestParam(value = "orderCode", required = true) final String orderCode,
            @RequestParam(value = "entries", required = true) final String entryNumbers, final Model model,
            final RedirectAttributes redirectModel) throws CMSItemNotFoundException {
        String[] orderItemsToAdd = null;
        if (StringUtils.isNotBlank(entryNumbers)) {
            orderItemsToAdd = entryNumbers.split(",");
        }

        final Map<OrderToCartStatus, Object> mapReturn = orderFacade
                .addToCartForOrderCode(XSSFilterUtil.filter(orderCode), Arrays.asList(orderItemsToAdd));
        return orderAddToCartPopulateModel(XSSFilterUtil.filter(orderCode), mapReturn, model);
    }

    private String orderAddToCartPopulateModel(final String orderCode, Map<OrderToCartStatus, Object> mapReturn, final Model model) {
        //add the errorMessage and product code to model
        final List<String> invalidproductTitles = (List<String>) mapReturn.get(OrderToCartStatus.INVALID_PRODUCT_TITLES);
        if (CollectionUtils.isNotEmpty(invalidproductTitles)) {
            model.addAttribute("errorMessageForOrder", "order.global.addToCart.error");
            model.addAttribute("productTitles", invalidproductTitles);
        }

        final List<String> excludedproductTitles = (List<String>) mapReturn.get(OrderToCartStatus.EXCLUDED_PRODUCT_TITLES);
        if (CollectionUtils.isNotEmpty(excludedproductTitles)) {
            model.addAttribute("errorMessageForExcludedProduct", "order.global.addToCart.error");
            model.addAttribute("excludedProductTitles", excludedproductTitles);
        }

        final List<CartModificationData> cartModificationDatas = (List<CartModificationData>) mapReturn
                .get(OrderToCartStatus.CART_MODIFICATION_DATAS);
        if (CollectionUtils.isNotEmpty(cartModificationDatas)) {
            groupCartModificationListPopulator.populate(null, cartModificationDatas);

            model.addAttribute("modifications", cartModificationDatas);
        }
        model.addAttribute("cartData", cartFacade.getSessionMiniCart());
        model.addAttribute("numberShowing", Integer.valueOf(Config.getInt(SHOWN_PRODUCT_COUNT, 3)));
        model.addAttribute("requestOrigin", new StringBuilder().append(SabmUtils.HOME).append("/").append(PAGE_SECTION_ORDER_HISTORY));
        try {
            final OrderData orderDetails = orderFacade.getOrderDetailsForCode(orderCode);
            model.addAttribute("orderData", populateOrderDataProductDatas(orderDetails));
        } catch (Exception e) {
            LOG.error("Unable to get order details for tag manager");
        }

        return ControllerConstants.Views.Fragments.Cart.AddToCartPopup;
    }

    /**
     * @param orderCode
     * @param model
     * @return AddToCartPopup
     * @throws CMSItemNotFoundException
     * @author yuxiao.wang
     */
    @PostMapping("/addTemplates")
    @RequireHardLogIn
    public String addToTemplate(@RequestParam("orderCode") final String orderCode, final Model model) throws CMSItemNotFoundException {

        final Map<OrderToCartStatus, Object> mapReturn = orderFacade.addToTemplate(XSSFilterUtil.filter(orderCode));

        //add the errorMessage and product code to model
        final List<String> invalidproductTitles = (List<String>) mapReturn.get(OrderToCartStatus.INVALID_PRODUCT_TITLES);
        if (CollectionUtils.isNotEmpty(invalidproductTitles)) {
            model.addAttribute("errorMessageForOrder", "order.global.addToCart.error");
            model.addAttribute("productTitles", invalidproductTitles);
        } else if (MapUtils.getBooleanValue(mapReturn, OrderToCartStatus.EMPTY_ADD_TO_CART)) {
            model.addAttribute("errorMessageForEmptyTemplate", "text.no.products.in.order.template");
        }

        final List<CartModificationData> cartModificationDatas = (List<CartModificationData>) mapReturn
                .get(OrderToCartStatus.CART_MODIFICATION_DATAS);
        if (CollectionUtils.isNotEmpty(cartModificationDatas)) {
            groupCartModificationListPopulator.populate(null, cartModificationDatas);

            model.addAttribute("modifications", cartModificationDatas);
        }
        model.addAttribute("cartData", cartFacade.getSessionMiniCart());
        model.addAttribute("numberShowing", Integer.valueOf(Config.getInt(SHOWN_PRODUCT_COUNT, 3)));

        final List<String> excludedproductTitles = (List<String>) mapReturn.get(OrderToCartStatus.EXCLUDED_PRODUCT_TITLES);
        if (CollectionUtils.isNotEmpty(excludedproductTitles)) {
            model.addAttribute("errorMessageForExcludedProduct", "order.global.addToCart.error");
            model.addAttribute("excludedProductTitles", excludedproductTitles);
        }

        model.addAttribute("requestOrigin", new StringBuilder().append(SabmUtils.HOME).append("/").append(PAGE_SECTION_ORDER_TEMPLATES));

        try {
            final OrderTemplateData orderTemplateDetails = b2bUnitFacade.getB2BUnitOrderTemplateDetail(orderCode);
            model.addAttribute("orderData", populateOrderTemplateDataProductDatas(orderTemplateDetails));
        } catch (Exception e) {
            LOG.error("Unable to get order template details for tag manager");
        }

        return ControllerConstants.Views.Fragments.Cart.AddToCartPopup;
    }

    @PostMapping("/saveOrderTemplate")
    @ResponseBody
    public Boolean createOrderTemplate(@RequestParam("orderName") final String orderName,
            @RequestParam("orderCode") final String orderCode) {
        return b2bUnitFacade.createOrderTemplateByOrder(StringUtils.trim(orderName), XSSFilterUtil.filter(orderCode));
    }

    @GetMapping("/profile")
    @RequireHardLogIn
    public String profile(final Model model) throws CMSItemNotFoundException {

        final CustomerData customerData = customerFacade.getCurrentCustomer();

        final List<String> units = getBranchUidFromCustomer(customerData);
        if (CollectionUtils.isNotEmpty(units)) {
            model.addAttribute("assistants", customerFacade.getUserForUpdateProfile(units));
        }
        model.addAttribute("customerData", customerData);
        List<SABMNotificationModel> notificationModels = notificationFacade.getNotificationForAllUnits(customerData.getUid());
  		if(notificationFacade != null && CollectionUtils.isNotEmpty(notificationModels)) {
  			boolean smsPrefs = notificationModels.stream()
  				    .flatMap(notificationModel -> notificationModel.getNotificationPreferences().stream())
  				    .anyMatch(SABMNotificationPrefModel::getSmsEnabled);
  			if(smsPrefs) {
  				model.addAttribute("notifications", "notifications");
  			}
     	}
        storeCmsPageInModel(model, getContentPageForLabelOrId(PROFILE_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(PROFILE_CMS_PAGE));
        model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.profile"));
        model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
        return getViewForPage(model);
    }

    /**
     * get the unit uids from the customer
     *
     * @param customerData
     * @return List<String>
     */
    protected List<String> getBranchUidFromCustomer(final CustomerData customerData) {
        final List<String> units = new ArrayList<String>();

        if (customerData != null && CollectionUtils.isNotEmpty(customerData.getBranches())) {
            final List<PrincipalGroupData> groupDatas = new ArrayList<PrincipalGroupData>(customerData.getBranches());
            for (final PrincipalGroupData principal : groupDatas) {
                units.add(principal.getUid());
            }
        }
        return units;
    }

    /**
     * update receiveUpdates
     *
     * @return String
     * @throws CMSItemNotFoundException
     */
    @PostMapping("/receiveUpdates")
    @RequireHardLogIn
    // public String updateReceiveUpdates(@RequestParam("receiveUpdates") final String receiveUpdates,
    // 		@RequestParam("defaultUnit") final String defaultUnit, @RequestParam("mobileNumber") final String mobileNumber, @RequestParam("businessPhoneNumber") final String businessPhoneNumber, RedirectAttributes redirectAttributes,final BindingResult bindingErrors)
    // 		throws CMSItemNotFoundException

    public String updateReceiveUpdates(final UpdateProfileForm updateProfileForm, final BindingResult bindingResult, final Model model,
            final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException

    {
        final String returnAction = REDIRECT_TO_PROFILE;

        //validate mobile number , if it's valid, store against person detail.

        /**if (StringUtils.isEmpty(updateProfileForm.getMobileNumber()) && BooleanUtils.isTrue(updateProfileForm.getReceiveUpdatesForSms())){
           GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.ERROR_MESSAGES_HOLDER, "text.account.profile.receiveupdatesforsms.error", null);
           return returnAction;
       }**/

        if(StringUtils.isNotEmpty(updateProfileForm.getMobileNumber())) {
          sabmMobileNumberValidator.validate(updateProfileForm, bindingResult);
        }
        
        if (bindingResult.hasErrors()) {
            GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.ERROR_MESSAGES_HOLDER, "address.phone.invalid", null);
            return returnAction;
        }

        try {
            customerFacade.updateReceiveUpdates_MobileNumber(Boolean.valueOf(updateProfileForm.getReceiveUpdates()),Boolean.valueOf(updateProfileForm.getReceiveUpdatesForSms()),
                    updateProfileForm.getMobileNumber(), updateProfileForm.getBusinessPhoneNumber());
            customerFacade.updateDefaultCustomerUnit(updateProfileForm.getDefaultUnit());

            //add the top message
            GlobalMessages
                    .addFlashMessage(redirectAttributes, GlobalMessages.CONF_MESSAGES_HOLDER, "text.account.profile.receiveupdates.updated",
                            null);
        } catch (final DuplicateUidException e) {
            GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.ERROR_MESSAGES_HOLDER,
                    "text.account.profile.receiveupdates.notupdated", null);
            LOG.debug("update receiveUpdates failed for customer:{}", customerFacade.getCurrentCustomer());
        }

        return returnAction;
    }

    protected TitleData findTitleForCode(final List<TitleData> titles, final String code) {
        if (code != null && !code.isEmpty() && titles != null && !titles.isEmpty()) {
            for (final TitleData title : titles) {
                if (code.equals(title.getCode())) {
                    return title;
                }
            }
        }
        return null;
    }

    @GetMapping("/update-email")
    @RequireHardLogIn
    public String editEmail(final Model model) throws CMSItemNotFoundException {
        final CustomerData customerData = customerFacade.getCurrentCustomer();
        final UpdateEmailForm updateEmailForm = new UpdateEmailForm();

        updateEmailForm.setEmail(customerData.getDisplayUid());

        model.addAttribute("updateEmailForm", updateEmailForm);

        storeCmsPageInModel(model, getContentPageForLabelOrId(UPDATE_EMAIL_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(UPDATE_EMAIL_CMS_PAGE));
        model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.profile"));
        model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
        return getViewForPage(model);
    }

    @PostMapping("/update-email")
    @RequireHardLogIn
    public String updateEmail(final UpdateEmailForm updateEmailForm, final BindingResult bindingResult, final Model model,
            final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException {
        getEmailValidator().validate(updateEmailForm, bindingResult);
        String returnAction = REDIRECT_TO_UPDATE_EMAIL_PAGE;

        if (!bindingResult.hasErrors() && !updateEmailForm.getEmail().equals(updateEmailForm.getChkEmail())) {
            bindingResult.rejectValue("chkEmail", "validation.checkEmail.equals", new Object[] {}, "validation.checkEmail.equals");
        }

        if (bindingResult.hasErrors()) {
            returnAction = setErrorMessagesAndCMSPage(model, UPDATE_EMAIL_CMS_PAGE);
        } else {
            try {
                customerFacade.changeUid(updateEmailForm.getEmail(), updateEmailForm.getPassword());
                GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.CONF_MESSAGES_HOLDER,
                        "text.account.profile.confirmationUpdated", null);

                // Replace the spring security authentication with the new UID
                final String newUid = customerFacade.getCurrentCustomer().getUid().toLowerCase();
                final Authentication oldAuthentication = SecurityContextHolder.getContext().getAuthentication();
                final UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(newUid, null,
                        oldAuthentication.getAuthorities());
                newAuthentication.setDetails(oldAuthentication.getDetails());
                SecurityContextHolder.getContext().setAuthentication(newAuthentication);
            } catch (final DuplicateUidException e) {
                bindingResult.rejectValue("email", "profile.email.unique");
                returnAction = setErrorMessagesAndCMSPage(model, UPDATE_EMAIL_CMS_PAGE);
            } catch (final PasswordMismatchException passwordMismatchException) {
                bindingResult.rejectValue("password", "profile.currentPassword.invalid");
                returnAction = setErrorMessagesAndCMSPage(model, UPDATE_EMAIL_CMS_PAGE);
            }
        }

        return returnAction;
    }

    protected String setErrorMessagesAndCMSPage(final Model model, final String cmsPageLabelOrId) throws CMSItemNotFoundException {
        GlobalMessages.addErrorMessage(model, "form.global.error");
        storeCmsPageInModel(model, getContentPageForLabelOrId(cmsPageLabelOrId));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(cmsPageLabelOrId));
        model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.profile"));
        return getViewForPage(model);
    }

    @GetMapping("/update-profile")
    @RequireHardLogIn
    public String editProfile(final Model model) throws CMSItemNotFoundException {
        model.addAttribute("titleData", userFacade.getTitles());

        final CustomerData customerData = customerFacade.getCurrentCustomer();
        final UpdateProfileForm updateProfileForm = new UpdateProfileForm();

        updateProfileForm.setTitleCode(customerData.getTitleCode());
        updateProfileForm.setFirstName(customerData.getFirstName());
        updateProfileForm.setLastName(customerData.getLastName());

        model.addAttribute("updateProfileForm", updateProfileForm);

        storeCmsPageInModel(model, getContentPageForLabelOrId(UPDATE_PROFILE_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(UPDATE_PROFILE_CMS_PAGE));

        model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.profile"));
        model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
        return getViewForPage(model);
    }

    @PostMapping("/update-profile")
    @RequireHardLogIn
    public String updateProfile(final UpdateProfileForm updateProfileForm, final BindingResult bindingResult, final Model model,
            final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException {
        getProfileValidator().validate(updateProfileForm, bindingResult);

        String returnAction = REDIRECT_TO_UPDATE_PROFILE;
        final CustomerData currentCustomerData = customerFacade.getCurrentCustomer();
        final CustomerData customerData = new CustomerData();
        customerData.setTitleCode(updateProfileForm.getTitleCode());
        customerData.setFirstName(updateProfileForm.getFirstName());
        customerData.setLastName(updateProfileForm.getLastName());
        customerData.setUid(currentCustomerData.getUid());
        customerData.setDisplayUid(currentCustomerData.getDisplayUid());

        model.addAttribute("titleData", userFacade.getTitles());

        storeCmsPageInModel(model, getContentPageForLabelOrId(UPDATE_PROFILE_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(UPDATE_PROFILE_CMS_PAGE));

        if (bindingResult.hasErrors()) {
            returnAction = setErrorMessagesAndCMSPage(model, UPDATE_PROFILE_CMS_PAGE);
        } else {
            try {
                customerFacade.updateProfile(customerData);
                GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.CONF_MESSAGES_HOLDER,
                        "text.account.profile.confirmationUpdated", null);

            } catch (final DuplicateUidException e) {
                bindingResult.rejectValue("email", "registration.error.account.exists.title");
                returnAction = setErrorMessagesAndCMSPage(model, UPDATE_PROFILE_CMS_PAGE);
            }
        }

        model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.profile"));
        return returnAction;
    }

    @GetMapping("/update-password")
    @RequireHardLogIn
    public String updatePassword(final Model model) throws CMSItemNotFoundException {
        final UpdatePasswordForm updatePasswordForm = new UpdatePasswordForm();

        final CustomerData customerData = customerFacade.getCurrentCustomer();
        model.addAttribute("userEmailId", customerData.getUid());
        final ArrayList<String> customerGroups = new ArrayList<>();

        for (final PrincipalGroupData principalGroupData : customerData.getGroups()) {
            customerGroups.add(principalGroupData.getUid());
        }

        if (customerGroups.contains(ACCESS_GROUPS_INVOICE) && customerGroups.contains(ACCESS_GROUPS_ORDER)) {

            model.addAttribute("updatePasswordForm", updatePasswordForm);

            storeCmsPageInModel(model, getContentPageForLabelOrId(UPDATE_PASSWORD_CMS_PAGE));
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(UPDATE_PASSWORD_CMS_PAGE));

            model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.profile.updatePasswordForm"));
            model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
            return getViewForPage(model);
        } else if (customerGroups.contains(ACCESS_GROUPS_INVOICE)) {
            return REDIRECT_PREFIX + "/your-business";
        }
        model.addAttribute("updatePasswordForm", updatePasswordForm);

        storeCmsPageInModel(model, getContentPageForLabelOrId(UPDATE_PASSWORD_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(UPDATE_PASSWORD_CMS_PAGE));

        model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.profile.updatePasswordForm"));
        model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
        return getViewForPage(model);
    }

    @PostMapping("/update-password")
    @RequireHardLogIn
    public String updatePassword(final UpdatePasswordForm updatePasswordForm, final BindingResult bindingResult, final Model model,
            final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException {
        getPasswordValidator().validate(updatePasswordForm, bindingResult);
        if (!bindingResult.hasErrors()) {
            if (updatePasswordForm.getNewPassword().equals(updatePasswordForm.getCheckNewPassword())) {
                try {
                    customerFacade.changePassword(updatePasswordForm.getCurrentPassword(), updatePasswordForm.getNewPassword());
                } catch (final PasswordMismatchException localException) {
                    //SAB386 update the mismatch error message
                    bindingResult.rejectValue("currentPassword", "profile.Password.invalid", new Object[] {}, "profile.Password.invalid");
                }
            } else {
                bindingResult.rejectValue("checkNewPassword", "validation.checkPwd.equals", new Object[] {}, "validation.checkPwd.equals");
            }
        }

        if (bindingResult.hasErrors()) {
            GlobalMessages.addErrorMessage(model, "form.global.error");
            storeCmsPageInModel(model, getContentPageForLabelOrId(UPDATE_PASSWORD_CMS_PAGE));
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(UPDATE_PASSWORD_CMS_PAGE));

            model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.profile.updatePasswordForm"));
            return getViewForPage(model);
        }

        GlobalMessages
                .addFlashMessage(redirectAttributes, GlobalMessages.CONF_MESSAGES_HOLDER, "text.account.confirmation.password.updated",
                        null);
        return REDIRECT_TO_PASSWORD_UPDATE_PAGE;
    }

    @GetMapping("/address-book")
    @RequireHardLogIn
    public String getAddressBook(final Model model) throws CMSItemNotFoundException {
        model.addAttribute("addressData", userFacade.getAddressBook());

        storeCmsPageInModel(model, getContentPageForLabelOrId(ADDRESS_BOOK_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADDRESS_BOOK_CMS_PAGE));
        model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.addressBook"));
        model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
        return getViewForPage(model);
    }

    @GetMapping("/add-address")
    @RequireHardLogIn
    public String addAddress(final Model model) throws CMSItemNotFoundException {
        model.addAttribute("countryData", checkoutFacade.getDeliveryCountries());
        model.addAttribute("titleData", userFacade.getTitles());
        final AddressForm addressForm = getPreparedAddressForm();
        model.addAttribute("addressForm", addressForm);
        model.addAttribute("addressBookEmpty", Boolean.valueOf(userFacade.isAddressBookEmpty()));
        model.addAttribute("isDefaultAddress", Boolean.FALSE);
        storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));

        final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);
        breadcrumbs.add(new Breadcrumb("/your-business/address-book",
                getMessageSource().getMessage("text.account.addressBook", null, getI18nService().getCurrentLocale()), null));
        breadcrumbs.add(new Breadcrumb("#",
                getMessageSource().getMessage("text.account.addressBook.addEditAddress", null, getI18nService().getCurrentLocale()), null));
        model.addAttribute("breadcrumbs", breadcrumbs);
        model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
        return getViewForPage(model);
    }

    protected AddressForm getPreparedAddressForm() {
        final CustomerData currentCustomerData = customerFacade.getCurrentCustomer();
        final AddressForm addressForm = new AddressForm();
        addressForm.setFirstName(currentCustomerData.getFirstName());
        addressForm.setLastName(currentCustomerData.getLastName());
        addressForm.setTitleCode(currentCustomerData.getTitleCode());
        return addressForm;
    }

    @PostMapping("/add-address")
    @RequireHardLogIn
    public String addAddress(final AddressForm addressForm, final BindingResult bindingResult, final Model model,
            final RedirectAttributes redirectModel) throws CMSItemNotFoundException {
        getAddressValidator().validate(addressForm, bindingResult);
        if (bindingResult.hasErrors()) {
            GlobalMessages.addErrorMessage(model, "form.global.error");
            storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
            setUpAddressFormAfterError(addressForm, model);
            return getViewForPage(model);
        }

        final AddressData newAddress = new AddressData();
        newAddress.setTitleCode(addressForm.getTitleCode());
        newAddress.setFirstName(addressForm.getFirstName());
        newAddress.setLastName(addressForm.getLastName());
        newAddress.setLine1(addressForm.getLine1());
        newAddress.setLine2(addressForm.getLine2());
        newAddress.setTown(addressForm.getTownCity());
        newAddress.setPostalCode(addressForm.getPostcode());
        newAddress.setBillingAddress(false);
        newAddress.setShippingAddress(true);
        newAddress.setVisibleInAddressBook(true);
        newAddress.setCountry(getI18NFacade().getCountryForIsocode(addressForm.getCountryIso()));
        newAddress.setPhone(addressForm.getPhone());

        if (addressForm.getRegionIso() != null && !StringUtils.isEmpty(addressForm.getRegionIso())) {
            newAddress.setRegion(getI18NFacade().getRegion(addressForm.getCountryIso(), addressForm.getRegionIso()));
        }

        if (userFacade.isAddressBookEmpty()) {
            newAddress.setDefaultAddress(true);
            newAddress.setVisibleInAddressBook(true);
        } else {
            newAddress.setDefaultAddress(addressForm.getDefaultAddress() != null && addressForm.getDefaultAddress().booleanValue());
        }

        final AddressVerificationResult<AddressVerificationDecision> verificationResult = getAddressVerificationFacade()
                .verifyAddressData(newAddress);
        final boolean addressRequiresReview = getAddressVerificationResultHandler()
                .handleResult(verificationResult, newAddress, model, redirectModel, bindingResult,
                        getAddressVerificationFacade().isCustomerAllowedToIgnoreAddressSuggestions(), "checkout.multi.address.added");

        model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(addressForm.getCountryIso()));
        model.addAttribute("country", addressForm.getCountryIso());
        model.addAttribute("edit", Boolean.TRUE);
        model.addAttribute("isDefaultAddress", Boolean.valueOf(isDefaultAddress(addressForm.getAddressId())));

        if (addressRequiresReview) {
            storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
            return getViewForPage(model);
        }

        userFacade.addAddress(newAddress);

        GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "account.confirmation.address.added", null);

        return REDIRECT_TO_EDIT_ADDRESS_PAGE + newAddress.getId();
    }

    protected void setUpAddressFormAfterError(final AddressForm addressForm, final Model model) {
        model.addAttribute("countryData", checkoutFacade.getDeliveryCountries());
        model.addAttribute("titleData", userFacade.getTitles());
        model.addAttribute("addressBookEmpty", Boolean.valueOf(userFacade.isAddressBookEmpty()));
        model.addAttribute("isDefaultAddress", Boolean.valueOf(isDefaultAddress(addressForm.getAddressId())));
        if (addressForm.getCountryIso() != null) {
            model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(addressForm.getCountryIso()));
            model.addAttribute("country", addressForm.getCountryIso());
        }
    }

    @GetMapping("/edit-address/" + ADDRESS_CODE_PATH_VARIABLE_PATTERN)
    @RequireHardLogIn
    public String editAddress(@PathVariable("addressCode") final String addressCode, final Model model) throws CMSItemNotFoundException {
        final AddressForm addressForm = new AddressForm();
        model.addAttribute("countryData", checkoutFacade.getDeliveryCountries());
        model.addAttribute("titleData", userFacade.getTitles());
        model.addAttribute("addressForm", addressForm);
        model.addAttribute("addressBookEmpty", Boolean.valueOf(userFacade.isAddressBookEmpty()));

        for (final AddressData addressData : userFacade.getAddressBook()) {
            if (addressData.getId() != null && addressData.getId().equals(addressCode)) {
                model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(addressData.getCountry().getIsocode()));
                model.addAttribute("country", addressData.getCountry().getIsocode());
                model.addAttribute("addressData", addressData);
                addressForm.setAddressId(addressData.getId());
                addressForm.setTitleCode(addressData.getTitleCode());
                addressForm.setFirstName(addressData.getFirstName());
                addressForm.setLastName(addressData.getLastName());
                addressForm.setLine1(addressData.getLine1());
                addressForm.setLine2(addressData.getLine2());
                addressForm.setTownCity(addressData.getTown());
                addressForm.setPostcode(addressData.getPostalCode());
                addressForm.setCountryIso(addressData.getCountry().getIsocode());
                addressForm.setPhone(addressData.getPhone());

                if (addressData.getRegion() != null && !StringUtils.isEmpty(addressData.getRegion().getIsocode())) {
                    addressForm.setRegionIso(addressData.getRegion().getIsocode());
                }

                if (isDefaultAddress(addressData.getId())) {
                    addressForm.setDefaultAddress(Boolean.TRUE);
                    model.addAttribute("isDefaultAddress", Boolean.TRUE);
                } else {
                    addressForm.setDefaultAddress(Boolean.FALSE);
                    model.addAttribute("isDefaultAddress", Boolean.FALSE);
                }
                break;
            }
        }

        storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));

        final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);
        breadcrumbs.add(new Breadcrumb("/your-business/address-book",
                getMessageSource().getMessage("text.account.addressBook", null, getI18nService().getCurrentLocale()), null));
        breadcrumbs.add(new Breadcrumb("#",
                getMessageSource().getMessage("text.account.addressBook.addEditAddress", null, getI18nService().getCurrentLocale()), null));
        model.addAttribute("breadcrumbs", breadcrumbs);
        model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
        model.addAttribute("edit", Boolean.TRUE);
        return getViewForPage(model);
    }

    /**
     * Method checks if address is set as default
     *
     * @param addressId - identifier for address to check
     * @return true if address is default, false if address is not default
     */
    protected boolean isDefaultAddress(final String addressId) {
        final AddressData defaultAddress = userFacade.getDefaultAddress();
        return defaultAddress != null && defaultAddress.getId() != null && defaultAddress.getId().equals(addressId);
    }

    @PostMapping("/edit-address/" + ADDRESS_CODE_PATH_VARIABLE_PATTERN)
    @RequireHardLogIn
    public String editAddress(final AddressForm addressForm, final BindingResult bindingResult, final Model model,
            final RedirectAttributes redirectModel) throws CMSItemNotFoundException {
        getAddressValidator().validate(addressForm, bindingResult);
        if (bindingResult.hasErrors()) {
            GlobalMessages.addErrorMessage(model, "form.global.error");
            storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
            setUpAddressFormAfterError(addressForm, model);
            return getViewForPage(model);
        }

        model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);

        final AddressData newAddress = new AddressData();
        newAddress.setId(addressForm.getAddressId());
        newAddress.setTitleCode(addressForm.getTitleCode());
        newAddress.setFirstName(addressForm.getFirstName());
        newAddress.setLastName(addressForm.getLastName());
        newAddress.setLine1(addressForm.getLine1());
        newAddress.setLine2(addressForm.getLine2());
        newAddress.setTown(addressForm.getTownCity());
        newAddress.setPostalCode(addressForm.getPostcode());
        newAddress.setBillingAddress(false);
        newAddress.setShippingAddress(true);
        newAddress.setVisibleInAddressBook(true);
        newAddress.setCountry(getI18NFacade().getCountryForIsocode(addressForm.getCountryIso()));
        newAddress.setPhone(addressForm.getPhone());

        if (addressForm.getRegionIso() != null && !StringUtils.isEmpty(addressForm.getRegionIso())) {
            newAddress.setRegion(getI18NFacade().getRegion(addressForm.getCountryIso(), addressForm.getRegionIso()));
        }

        if (Boolean.TRUE.equals(addressForm.getDefaultAddress()) || userFacade.getAddressBook().size() <= 1) {
            newAddress.setDefaultAddress(true);
            newAddress.setVisibleInAddressBook(true);
        }

        final AddressVerificationResult<AddressVerificationDecision> verificationResult = getAddressVerificationFacade()
                .verifyAddressData(newAddress);
        final boolean addressRequiresReview = getAddressVerificationResultHandler()
                .handleResult(verificationResult, newAddress, model, redirectModel, bindingResult,
                        getAddressVerificationFacade().isCustomerAllowedToIgnoreAddressSuggestions(), "checkout.multi.address.updated");

        model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(addressForm.getCountryIso()));
        model.addAttribute("country", addressForm.getCountryIso());
        model.addAttribute("edit", Boolean.TRUE);
        model.addAttribute("isDefaultAddress", Boolean.valueOf(isDefaultAddress(addressForm.getAddressId())));

        if (addressRequiresReview) {
            storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
            return getViewForPage(model);
        }

        userFacade.editAddress(newAddress);

        GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "account.confirmation.address.updated", null);
        return REDIRECT_TO_EDIT_ADDRESS_PAGE + newAddress.getId();
    }

    @PostMapping("/select-suggested-address")
    public String doSelectSuggestedAddress(final AddressForm addressForm, final RedirectAttributes redirectModel) {
        final Set<String> resolveCountryRegions = org.springframework.util.StringUtils
                .commaDelimitedListToSet(Config.getParameter("resolve.country.regions"));

        final AddressData selectedAddress = new AddressData();
        selectedAddress.setId(addressForm.getAddressId());
        selectedAddress.setTitleCode(addressForm.getTitleCode());
        selectedAddress.setFirstName(addressForm.getFirstName());
        selectedAddress.setLastName(addressForm.getLastName());
        selectedAddress.setLine1(addressForm.getLine1());
        selectedAddress.setLine2(addressForm.getLine2());
        selectedAddress.setTown(addressForm.getTownCity());
        selectedAddress.setPostalCode(addressForm.getPostcode());
        selectedAddress.setBillingAddress(false);
        selectedAddress.setShippingAddress(true);
        selectedAddress.setVisibleInAddressBook(true);
        selectedAddress.setPhone(addressForm.getPhone());

        final CountryData countryData = i18NFacade.getCountryForIsocode(addressForm.getCountryIso());
        selectedAddress.setCountry(countryData);

        if (resolveCountryRegions.contains(countryData.getIsocode()) && addressForm.getRegionIso() != null && !StringUtils
                .isEmpty(addressForm.getRegionIso())) {
            final RegionData regionData = getI18NFacade().getRegion(addressForm.getCountryIso(), addressForm.getRegionIso());
            selectedAddress.setRegion(regionData);
        }

        if (resolveCountryRegions.contains(countryData.getIsocode()) && addressForm.getRegionIso() != null && !StringUtils
                .isEmpty(addressForm.getRegionIso())) {
            final RegionData regionData = getI18NFacade().getRegion(addressForm.getCountryIso(), addressForm.getRegionIso());
            selectedAddress.setRegion(regionData);
        }

        if (Boolean.TRUE.equals(addressForm.getEditAddress())) {
            selectedAddress
                    .setDefaultAddress(Boolean.TRUE.equals(addressForm.getDefaultAddress()) || userFacade.getAddressBook().size() <= 1);
            userFacade.editAddress(selectedAddress);
        } else {
            selectedAddress.setDefaultAddress(Boolean.TRUE.equals(addressForm.getDefaultAddress()) || userFacade.isAddressBookEmpty());
            userFacade.addAddress(selectedAddress);
        }

        GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "account.confirmation.address.added");

        return REDIRECT_TO_ADDRESS_BOOK_PAGE;
    }

    @RequestMapping(value = "/remove-address/" + ADDRESS_CODE_PATH_VARIABLE_PATTERN, method = { RequestMethod.GET, RequestMethod.POST })
    @RequireHardLogIn
    public String removeAddress(@PathVariable("addressCode") final String addressCode, final RedirectAttributes redirectModel) {
        final AddressData addressData = new AddressData();
        addressData.setId(addressCode);
        userFacade.removeAddress(addressData);

        GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "account.confirmation.address.removed");
        return REDIRECT_TO_ADDRESS_BOOK_PAGE;
    }

    @GetMapping("/set-default-address/" + ADDRESS_CODE_PATH_VARIABLE_PATTERN)
    @RequireHardLogIn
    public String setDefaultAddress(@PathVariable("addressCode") final String addressCode, final RedirectAttributes redirectModel) {
        final AddressData addressData = new AddressData();
        addressData.setDefaultAddress(true);
        addressData.setVisibleInAddressBook(true);
        addressData.setId(addressCode);
        userFacade.setDefaultAddress(addressData);
        GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "account.confirmation.default.address.changed");
        return REDIRECT_TO_ADDRESS_BOOK_PAGE;
    }

    @GetMapping("/payment-details")
    @RequireHardLogIn
    public String paymentDetails(final Model model) throws CMSItemNotFoundException {
        model.addAttribute("customerData", customerFacade.getCurrentCustomer());
        model.addAttribute("paymentInfoData", userFacade.getCCPaymentInfos(true));
        storeCmsPageInModel(model, getContentPageForLabelOrId(PAYMENT_DETAILS_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
        model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.paymentDetails"));
        model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
        return getViewForPage(model);
    }

    @PostMapping("/set-default-payment-details")
    @RequireHardLogIn
    public String setDefaultPaymentDetails(@RequestParam final String paymentInfoId) {
        CCPaymentInfoData paymentInfoData = null;
        if (StringUtils.isNotBlank(paymentInfoId)) {
            paymentInfoData = userFacade.getCCPaymentInfoForCode(paymentInfoId);
        }
        userFacade.setDefaultPaymentInfo(paymentInfoData);
        return REDIRECT_TO_PAYMENT_INFO_PAGE;
    }

    @PostMapping("/remove-payment-method")
    @RequireHardLogIn
    public String removePaymentMethod(@RequestParam(value = "paymentInfoId") final String paymentMethodId,
            final RedirectAttributes redirectAttributes) {
        userFacade.unlinkCCPaymentInfo(paymentMethodId);
        GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.CONF_MESSAGES_HOLDER, "text.account.profile.paymentCart.removed");
        return REDIRECT_TO_PAYMENT_INFO_PAGE;
    }

    @GetMapping("/billing")
    public String getBillingPage(final Model model, @RequestParam(value = "error", required = false) final String errors)
            throws CMSItemNotFoundException {
        customerFacade.turnOffImpersonation();

        if (StringUtils.isNotBlank(errors)) {
            final String[] tokens = errors.split(",");
            for (final String token : tokens) {
                GlobalMessages.addErrorMessage(model, token);
            }
        }
        // if the user logged in is a primary admin user(ZADP level user),
        // the business unit name shown as the heading on the Billing and Invoices page should be the ZADP
        final CustomerData customerData = customerFacade.getCurrentCustomer();
        if (BooleanUtils.isTrue(customerData.getIsZadp()) && BooleanUtils.isTrue(customerData.getPrimaryAdmin())) {
            customerData.setUnit(b2bUnitFacade.getZADPB2BUnitByCurrentCustomer());
        }

        model.addAttribute("customerData", customerData);
        model.addAttribute("isInvoiceDiscrepancyEnabled", sabmFeatureUtil.isFeatureEnabled(SabmcommonsConstants.INVOICEDISCREPANY) && !asahiCoreUtil.isNAPUserForSite());

        storeCmsPageInModel(model, getContentPageForLabelOrId(BILLING_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(BILLING_CMS_PAGE));
        model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.billing"));
        model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
        Gson gson = new Gson();
        String json = gson.toJson(sabmMerchantSuitePaymentFacade.fetchCreditCardValidationData());
        model.addAttribute("ccValidationData", json);
        model.addAttribute("paymentTestMode", configurationService.getConfiguration().getBoolean(TEST_MODE,false));

        customerFacade.turnBackImpersonation();

        return ControllerConstants.Views.Pages.Account.AccountBillingPage;
    }

    @GetMapping("/billing/confirmation/{trackingNumber}")
    public String getBillingConfirmation(@PathVariable(value = "trackingNumber") final String trackingNumber, final Model model)
            throws CMSItemNotFoundException {
        customerFacade.turnOffImpersonation();
        InvoicePaymentData invoicePaymentData = sabmMerchantSuitePaymentFacadeImpl.getInvoice(trackingNumber);
        model.addAttribute("customerData", customerFacade.getCurrentCustomer());
        model.addAttribute("invoicePaymentData", invoicePaymentData);
		double surchargeAmt = invoiceFacade.getSurchargeAmtforInvoiceByTrackingNumber(trackingNumber);		
		double totalInvoiceAmt = invoicePaymentData.getAmount().doubleValue() + surchargeAmt;
		model.addAttribute("totalInvoiceAmt", totalInvoiceAmt);
        storeCmsPageInModel(model, getContentPageForLabelOrId(BILLING_CONFIRMATION_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(BILLING_CONFIRMATION_CMS_PAGE));
        model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.billing"));
        model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
        customerFacade.turnBackImpersonation();
        return ControllerConstants.Views.Pages.Account.AccountBillingConfirmationPage;
    }

    @RequestMapping(value = "/billing/invoices", method = { RequestMethod.GET, RequestMethod.POST })
    @RequireHardLogIn
    @ResponseBody
    public SABMInvoicePageData getBillingInvoices(@RequestParam(value = "lineItem", required = false) final String lineItem,
            @RequestParam(value = "forUnit", required = false) final String forUnit,
            @RequestParam(value = "startDate", required = false) final String startDate,
            @RequestParam(value = "endDate", required = false) final String endDate,
            @RequestParam(value = "type", required = false) final String invoiceType) {
        customerFacade.turnOffImpersonation();

        final SABMInvoicePageData invoices = customerFacade.getCustomerInvoices(lineItem, forUnit, startDate, endDate, invoiceType);

        customerFacade.turnBackImpersonation();
        return invoices;
    }

    @GetMapping("/billing/invoice/pdf/" + DOCUMENT_NUMBER_PATH_VARIABLE_PATTERN)
    @RequireHardLogIn
    @ResponseBody
    public void getInvoicePDF(@PathVariable(value = "docNum") final String docNum, final HttpServletResponse httpServletResponse) {
        final SABMInvoicePDFData invoicePDF = customerFacade.getInvoicePDF(docNum);

        //TODO cover error case
        if (invoicePDF != null && invoicePDF.getBinaryData() != null) {
            final String fileName = docNum + ".PDF";
            httpServletResponse.setContentType("application/octet-stream");
            httpServletResponse.addHeader("Content-Disposition", "attachment; filename=" + "" + fileName + "");

            try {
                httpServletResponse.getOutputStream().write(invoicePDF.getBinaryData(), 0, invoicePDF.getBinaryData().length);
                httpServletResponse.getOutputStream().flush();
            } catch (final IOException e) {
                LOG.warn("Error writing PDF in binaryData for document: " + docNum, e);
            }
        }
    }

    @PostMapping("/billing/invoices/email")
    @RequireHardLogIn
    @ResponseBody
    public void sendInvoicesEmail(@RequestParam(value = "docNumList", required = true) final List<String> docNumList)

    {
        customerFacade.sendInvoicesEmail(docNumList);

    }

    @GetMapping
    @RequireHardLogIn
    public String overview(final Model model) throws CMSItemNotFoundException {
        //get customer data for manage profile section
        final List<TitleData> titles = userFacade.getTitles();

        final CustomerData customerData = customerFacade.getCurrentCustomer();
        if (customerData.getTitleCode() != null) {
            model.addAttribute("title", findTitleForCode(titles, customerData.getTitleCode()));
            if (customerData.getCreatedBy() != null) {
                model.addAttribute("adminTitle", findTitleForCode(titles, customerData.getCreatedBy().getTitleCode()));
            }
        }

        model.addAttribute("customerData", customerData);
        model.addAttribute("isInvoiceDiscrepancyEnabled", sabmFeatureUtil.isFeatureEnabled(SabmcommonsConstants.INVOICEDISCREPANY));

        storeCmsPageInModel(model, getContentPageForLabelOrId(BUSINESS_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(BUSINESS_CMS_PAGE));
        model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
        model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs(null));

        return getViewForPage(model);
    }

    //SAB-1391
    @GetMapping("/businessunits")
    @RequireHardLogIn
    public String businessunits(final Model model) throws CMSItemNotFoundException {
        storeCmsPageInModel(model, getContentPageForLabelOrId(BUSINESSUNITS_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(BUSINESSUNITS_CMS_PAGE));

        final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs("text.yourBusiness.businessUnits");
        model.addAttribute("breadcrumbs", breadcrumbs);
        customerFacade.turnOffImpersonation();

        final de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData rootB2bUnit = b2bUnitFacade.getRootB2bUnit();
        final List<B2BUnitData> b2bUnits = b2bUnitFacade.getEntireB2bUnits();
        model.addAttribute("rootB2bUnit", rootB2bUnit);
        model.addAttribute("b2bUnits", b2bUnits);
        
        final List<CustomerData> customers = b2bUnitFacade.getUsersWithZADP();
        model.addAttribute("customers", customers);
        model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
        customerFacade.turnBackImpersonation();

        return ControllerConstants.Views.Pages.Business.BusinessUnitsPage;
    }

    //SAB-1392
    @RequestMapping(value = "/unitsdetails" + BUSINESS_UID_PATH_VARIABLE_PATTERN, method = { RequestMethod.POST, RequestMethod.GET })
    @RequireHardLogIn
    public String unitsdetails(@PathVariable("b2bUnitId") final String b2bUnitId, final Model model) throws CMSItemNotFoundException {
        //Checking if the B2BUnit belongs to the current loggedin customer. It may not be if the customer used an old URL (or inserted manually).
        if (!b2bUnitFacade.b2bUnitBelongsToCurrentCustomer(b2bUnitId)) {
            return REDIRECT_PREFIX + "/";
        }

        storeCmsPageInModel(model, getContentPageForLabelOrId(BUSINESSUNITDETAILS_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(BUSINESSUNITDETAILS_CMS_PAGE));
        final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);
        breadcrumbs.add(new Breadcrumb(MANAGE_BUSINESSUNITS_URL,
                getMessageSource().getMessage("text.yourBusiness.businessUnits", null, getI18nService().getCurrentLocale()), null));
        breadcrumbs.add(new Breadcrumb(MANAGE_BUSINESSUNITDETAILS_URL + b2bUnitId,
                getMessageSource().getMessage("text.yourBusiness.businessUnitDetails", null, getI18nService().getCurrentLocale()), null));
        model.addAttribute("breadcrumbs", breadcrumbs);
        final de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData rootB2bUnit = b2bUnitFacade.getRootB2bUnit();
        Set<CustomerData> allCustomers = new HashSet<CustomerData>();
        Collection<CustomerData> zadpCustomers = rootB2bUnit.getCustomers();
        final B2BUnitData data = b2bUnitFacade.getB2bUnitDataExceptZADPUser(b2bUnitId);
        Collection<CustomerData> zalbCustomer = data.getCustomers();
        if (null != zalbCustomer && CollectionUtils.isNotEmpty(zalbCustomer)) {
            allCustomers.addAll(zalbCustomer);
        }
        if (null != zadpCustomers && CollectionUtils.isNotEmpty(zadpCustomers)) {
            allCustomers.addAll(zadpCustomers);
        }
        b2bUnitFacade.setActiveStatus(allCustomers, b2bUnitId);
        data.setCustomers(allCustomers);
        model.addAttribute("b2bUnit", data);
        return ControllerConstants.Views.Pages.Business.BusinessUnitDetailPage;
    }

    //SAB-1392
    @PostMapping("/customer_active")
    @RequireHardLogIn
    @ResponseBody
    public RemoveCustomerJson customerActive(@RequestBody final SABMRemoveUserForm removeUserForm) {
        final RemoveCustomerJson removeCustomerJson = new RemoveCustomerJson();
        try {
            if (customerFacade.saveActiveForCustomer(removeUserForm.getBusinessCustomerUid(), removeUserForm.getBusinessCustomerActive(), removeUserForm.getBusinessUnitId())) {
                removeCustomerJson.setMessage(getMessageSource()
                        .getMessage("text.businessUnitDetail.active.save.mes", null, getI18nService().getCurrentLocale()));
                removeCustomerJson.setMessageType("good");
                removeCustomerJson.setOpenModal("");
            }
        } catch (final DuplicateUidException e) {
            LOG.error("customerActive failed for business Unit [{}] and  customer [{}]", removeUserForm.getBusinessUnitId(),
                    removeUserForm.getBusinessCustomerUid(), e);
        }
        return removeCustomerJson;
    }

    @PostMapping("/remove_customer")
    @RequireHardLogIn
    @ResponseBody
    public RemoveCustomerJson removeCustomerFromB2BUnit(@RequestBody final SABMRemoveUserForm removeUserForm) {
        final RemoveCustomerJson removeCustomerJson = new RemoveCustomerJson();
        int activeB2bUnitsForCustomer = b2bUnitService.getActiveB2BUnitModelsByCustomer(removeUserForm.getBusinessCustomerUid()).size();
        if (activeB2bUnitsForCustomer > 1) {
            if (customerFacade.removeCustomerFromB2bUnit(removeUserForm.getBusinessUnitId(), removeUserForm.getBusinessCustomerUid())) {
                removeCustomerJson.setMessage(getMessageSource()
                        .getMessage("text.businessUnitDetail.remove.customer.mes", null, getI18nService().getCurrentLocale()));
                removeCustomerJson.setMessageType("good");
                removeCustomerJson.setOpenModal("");
            } else {
                removeCustomerJson.setMessage("");
                removeCustomerJson.setOpenModal("");
                removeCustomerJson.setMessageType("bad");
            }
        } else if (activeB2bUnitsForCustomer == 1){
            removeCustomerJson.setMessage("");
            removeCustomerJson.setMessageType("");
            removeCustomerJson.setOpenModal("deactivateDelete");
        }else {
      	  
      	  removeCustomerJson.setMessage("Customer had no active B2BUnits to remove");
           removeCustomerJson.setMessageType("");
           removeCustomerJson.setOpenModal("");
        }
      	  

        return removeCustomerJson;
    }

    @PostMapping("/createOrderTemplate")
    public String createEmptyOrderTemplate(@RequestParam("templateName") final String templateName,
            @RequestParam("pageName") final String pageName, final RedirectAttributes redirectModel) {
        if (templateName != null && b2bUnitFacade.createEmptyOrderTemplateByName(XSSFilterUtil.filter(templateName)) != null) {
            GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "create.order.template.success");
        } else {
            GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "create.order.template.error");
        }

        // if the pageName is null, will redirect the page to home page
        if (pageName != null) {
            if (("Home").equals(pageName)) {
                return REDIRECT_PREFIX + "/";
            } else if (("Ordertemplates").equals(pageName)) {
                return REDIRECT_PREFIX + "/your-business/ordertemplates";
            }
        }
        return REDIRECT_PREFIX + "/";
    }

    @PostMapping("/deleteUserJson")
    @RequireHardLogIn
    @ResponseBody
    public RemoveCustomerJson deleteUserJson(@RequestBody final SABMRemoveUserForm removeUserForm) {
        final RemoveCustomerJson removeCustomerJson = new RemoveCustomerJson();
        if (StringUtils.isNotEmpty(removeUserForm.getBusinessCustomerUid())) {
            final CustomerData customer = sabmCustomerFacade.deleteUser(removeUserForm.getBusinessCustomerUid());
            if (customer != null) {
                if (StringUtils.isNotEmpty(customer.getFirstName()) && StringUtils.isNotEmpty(customer.getLastName())) {
                    final String[] nameAttributes = { customer.getFirstName(), customer.getLastName(), customer.getUid() };

                    removeCustomerJson.setMessage(getMessageSource()
                            .getMessage("text.business.unit.delete.user.success", nameAttributes, getI18nService().getCurrentLocale()));
                    removeCustomerJson.setMessageType("good");
                    removeCustomerJson.setOpenModal("");
                } else {
                    final String[] nameAttributes = { customer.getUid() };

                    removeCustomerJson.setMessage(getMessageSource()
                            .getMessage("text.business.unit.delete.user.success.no.name", nameAttributes,
                                    getI18nService().getCurrentLocale()));
                    removeCustomerJson.setMessageType("good");
                    removeCustomerJson.setOpenModal("");
                }
            } else {
                final String[] nameAttributes = { removeUserForm.getBusinessCustomerUid() };

                removeCustomerJson.setMessage(getMessageSource()
                        .getMessage("text.business.unit.delete.user.not.exist", nameAttributes, getI18nService().getCurrentLocale()));
                removeCustomerJson.setMessageType("bad");
                removeCustomerJson.setOpenModal("");
            }
        }

        return removeCustomerJson;
    }

    @PostMapping("/deleteUser")
    @RequireHardLogIn
    public String deleteUser(@RequestParam("uid") final String uid, @RequestParam("b2bUnitId") final String b2bUnitId,
            final RedirectAttributes redirectAttributes) {
        if (StringUtils.isNotEmpty(uid)) {
            final CustomerData customer = sabmCustomerFacade.deleteUser(uid);
            if (customer != null) {
                if (StringUtils.isNotEmpty(customer.getFirstName()) && StringUtils.isNotEmpty(customer.getLastName())) {
                    final String[] nameAttributes = { customer.getFirstName(), customer.getLastName(), customer.getUid() };
                    GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.CONF_MESSAGES_HOLDER,
                            "text.business.unit.delete.user.success", nameAttributes);
                } else {
                    final String[] nameAttributes = { customer.getUid() };
                    GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.CONF_MESSAGES_HOLDER,
                            "text.business.unit.delete.user.success.no.name", nameAttributes);
                }
            } else {
                final String[] nameAttributes = { uid };
                GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.CONF_MESSAGES_HOLDER,
                        "text.business.unit.delete.user.not.exist", nameAttributes);
            }
        }
        // if the b2bUnitId is null, will redirect the page to units page
        if (StringUtils.isNotEmpty(b2bUnitId)) {
            return REDIRECT_PREFIX + "/your-business/unitsdetails/" + b2bUnitId;
        }
        return REDIRECT_PREFIX + "/your-business/businessunits";

    }

    @ModelAttribute("pageType")
    protected String getPageType() {
        return SABMWebConstants.PageType.ACCOUNT.name();
    }

    @ModelAttribute("requestOrigin")
    protected String populateRequestOrigin(HttpServletRequest request) {
        return SabmUtils.getRequestOrigin(request.getHeader(SabmUtils.REFERER_KEY), SabmUtils.HOME);
    }

    private boolean orderIsTrackable(OrderData orderDetails) {

        if (orderDetails.getStatus().equals(OrderStatus.CANCELLED) || orderDetails.getStatus().equals(OrderStatus.RETURNED) || orderDetails
                .getStatus().equals(OrderStatus.NOTDELIVERED) || orderDetails.getStatus().equals(OrderStatus.COMPLETED)) {
            return false;
        }

        return true;

    }

    @GetMapping("/invoicediscrepancy")
    @RequireHardLogIn
    public String invoiceDiscrepancyLanding(final Model model) throws CMSItemNotFoundException {
   	 
   	 if (asahiCoreUtil.isNAPUser()) {
				return FORWARD_PREFIX + "/404";
			}

        final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);

        breadcrumbs.add(new Breadcrumb(YOUR_ACCOUNT_PAGE,
                getMessageSource().getMessage("text.account.billing", null, getI18nService().getCurrentLocale()), null));

        breadcrumbs.add(new Breadcrumb(INVOICE_DISCREPANCY_PAGE,
                getMessageSource().getMessage("text.invoicediscrepancy.header", null, getI18nService().getCurrentLocale()), null));

        model.addAttribute("breadcrumbs", breadcrumbs);
        model.addAttribute("isInvoiceDiscrepancyEnabled", sabmFeatureUtil.isFeatureEnabled(SabmcommonsConstants.INVOICEDISCREPANY));

        model.addAttribute("pageType", SABMWebConstants.PageType.INVOICEDISCREPANCY.name());
        storeCmsPageInModel(model, getContentPageForLabelOrId(INVOICEDISCREPANCY_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(INVOICEDISCREPANCY_CMS_PAGE));

        model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
        SABMInvoiceList invoiceList = invoiceFacade.fetchInvoices(b2bUnitFacade.getCurrentB2BUnitId());

        if(Objects.isNull(invoiceList)){
           LOG.info("there is no invoice from last 14 days for -- " + b2bUnitFacade.getCurrentB2BUnitId() );
        }
        model.addAttribute("invoiceList", invoiceList);

        return getViewForPage(model);

    }

    @PostMapping("/validateInvoice")
    @RequireHardLogIn
    @ResponseBody
    public SABMInvoiceValidationResult validateInvoice(@RequestBody @Valid final SABMInvoiceDiscrepancyData data, final Model model,
            final BindingResult bindingErrors) {

        if (StringUtils.isNotEmpty(data.getSoldTo()) || StringUtils.isNumeric(data.getInvoiceNumber())) {
            return invoiceFacade.validateInvoice(data.getSoldTo(), data.getInvoiceNumber());

        }

        return null;

    }

    @PostMapping(value = "/getInvoiceItemData", produces = "application/json")
    @RequireHardLogIn
    @ResponseBody
    public SABMInvoiceDiscrepancyData getInvoiceItemData(@RequestBody @Valid final SABMInvoiceDiscrepancyData data, final Model model,
            final BindingResult bindingErrors) {

        if (StringUtils.isNotEmpty(data.getSoldTo()) || StringUtils.isNumeric(data.getInvoiceNumber())) {
            return invoiceFacade.getInvoiceData(data.getSoldTo(), data.getInvoiceNumber());

        }

        return null;

    }

    @GetMapping("/raisedinvoicediscrepancy")
    @RequireHardLogIn
    public String raisedInvoiceDiscrepancyLanding(final Model model,  @RequestParam(value = "forUnit", required = false) final String forUnit) throws CMSItemNotFoundException {


        final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);

        breadcrumbs.add(new Breadcrumb(YOUR_ACCOUNT_PAGE,
                getMessageSource().getMessage("text.account.billing", null, getI18nService().getCurrentLocale()), null));

        breadcrumbs.add(new Breadcrumb(RAISED_INVOICE_DISCREPANCY_PAGE,
                getMessageSource().getMessage("text.raisedinvoicediscrepancy.header", null, getI18nService().getCurrentLocale()), null));

        model.addAttribute("breadcrumbs", breadcrumbs);

        model.addAttribute("pageType", SABMWebConstants.PageType.RAISEDINVOICEDISCREPANCY.name());

        storeCmsPageInModel(model, getContentPageForLabelOrId(RAISEDINVOICEDISCREPANCY_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(RAISEDINVOICEDISCREPANCY_CMS_PAGE));

        model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
        model.addAttribute("isInvoiceDiscrepancyEnabled", sabmFeatureUtil.isFeatureEnabled(SabmcommonsConstants.INVOICEDISCREPANY));

        List <String> b2bUnitList = new ArrayList<>();

        if (StringUtils.isEmpty(forUnit)) {
            for (PrincipalGroupData data : customerFacade.getCurrentCustomer().getBranches()){
                b2bUnitList.add(data.getUid());
            }
        }

        else {
            b2bUnitList.add(forUnit);
        }

        List<SABMInvoiceDiscrepancyData> raisedInvoiceslist = invoiceFacade.fetchRaisedInvoicesForSelectedB2BUnit(b2bUnitList, null, null);

        model.addAttribute("raisedInvoiceslist", raisedInvoiceslist);

        return getViewForPage(model);

    }

    @PostMapping("/raisedinvoicediscrepancybydaterange")
    @RequireHardLogIn
    @ResponseBody
    public List<SABMInvoiceDiscrepancyData> getRaisedInvoiceDiscrepancyByDateRange(
            @RequestParam(value = "forUnit", required = false) final String forUnit,
            @RequestParam(value = "startDate", required = false) final String startDate,
            @RequestParam(value = "endDate", required = false) final String endDate) {

        String format = "yyyyMMdd";

        Date start = null;
        Date end = null;
        if (StringUtils.isNotEmpty(startDate) && StringUtils.isNotEmpty(endDate)) {
            try {
                start = SabmDateUtils.getDate(startDate, format);
                end = SabmDateUtils.getDate(endDate, format);

            } catch (ParseException e) {
                LOG.error("Date range parse exception");
            }
        }
        List <String> b2bUnitList = new ArrayList<>();

        if (StringUtils.isEmpty(forUnit)) {
            for (PrincipalGroupData data : customerFacade.getCurrentCustomer().getBranches()){
                b2bUnitList.add(data.getUid());
            }
        }

        else {
            b2bUnitList.add(forUnit);
        }

        List<SABMInvoiceDiscrepancyData> raisedInvoiceslist = invoiceFacade
                .fetchRaisedInvoicesForSelectedB2BUnit(b2bUnitList, start, SabmDateUtils.plusOneDay(end));

        return raisedInvoiceslist;
    }

    @PostMapping("/saveInvoiceDiscrepancyRequest")
    @RequireHardLogIn
    @ResponseBody
    public boolean saveInvoiceDiscrepancyRequest(@RequestBody @Valid final SABMInvoiceDiscrepancyData data, final Model model,
            final BindingResult bindingErrors) {

        return invoiceFacade.saveInvoiceDiscrepancyRequest(data);
    }

    @RequestMapping(value = "/getCustomerListForB2BUnitToReceiveInvoiceDiscrepancyNotification_BDE", method = { RequestMethod.GET,
            RequestMethod.POST })
    @RequireHardLogIn
    @ResponseBody

    public de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData getCustomerListForB2BUnitToReceiveInvoiceDiscrpencyNotification_BDE() {
        final de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData b2bUnitData = b2bUnitFacade.getB2bUnitDataOnlyForInvoiceUser(b2bUnitFacade.getCurrentB2BUnitId(), true);
        return b2bUnitData;
    }

    @RequestMapping(value = "/getCustomerListForB2BUnitToReceiveInvoiceDiscrepancyNotification", method = { RequestMethod.GET,
            RequestMethod.POST })
    @RequireHardLogIn
    @ResponseBody
    public de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData getCustomerListForB2BUnitToReceiveInvoiceDiscrpencyNotification() {
        final de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData b2bUnitData = b2bUnitFacade.getB2bUnitDataOnlyForInvoiceUser(b2bUnitFacade.getCurrentB2BUnitId(), false);
        return b2bUnitData;

    }

	/**
	 * @return the b2bUnitService
	 */
	public SabmB2BUnitService getB2bUnitService()
	{
		return b2bUnitService;
	}

	/**
	 * @param b2bUnitService the b2bUnitService to set
	 */
	public void setB2bUnitService(SabmB2BUnitService b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

	/**
	 * @return the sessionService
	 */
	public SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * @param sessionService the sessionService to set
	 */
	public void setSessionService(SessionService sessionService)
	{
		this.sessionService = sessionService;
	}



}
