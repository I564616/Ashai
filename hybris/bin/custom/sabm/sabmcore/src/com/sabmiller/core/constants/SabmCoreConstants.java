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
package com.sabmiller.core.constants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.sabmiller.core.enums.AutoPayStatus;


/**
 * Global class for all SabmCore constants. You can add global constants for your extension into this class.
 */
public final class SabmCoreConstants extends GeneratedSabmCoreConstants
{
	public static final String EXTENSIONNAME = "sabmcore";

	private SabmCoreConstants()
	{
		//empty
	}

	// implement here constants used by this extension
	public static final String ZADP = "ZADP";
	public static final String ZALB = "ZALB";
	public static final String ZA02 = "ZA02";
	//ALB Code Start
	public static final String ALB_STORE = "sga";

	public static final String CUB_STORE = "sabmStore";

	public static final String APB_STORE = "apb";

	public static final String CUB_COMPANY_UID = "sabmStore";
	public static final String CUB_FACET_SEARCH_CONFIG = "sabmStoreIndex";


	//ALB Code END
	public static final String SEARCH_PRODUCT_UOM_SEPARATOR = "_:_";
	public static final String GST = "GST";
	public static final String WET = "WET";
	public static final String ENTRY_TOTAL_DISCOUNT = "entry";
	public static final String ENTRY_UNIT_DISCOUNT = "unit";

	public static final String[] SAP_DATE_FORMATS =
	{ "yyyymmdd" };
	public static final String OFFER_TYPE_DISCOUNT = "discount";
	public static final String OFFER_TYPE_FREEGOOD = "bogof";
	public static final String OFFER_TYPE_COMPLEX = "complexDiscount";
	public static final String OFFER_TYPE_LIMITED = "limitedDiscount";
	public static final String CART_DELIVERY_CUBARRANGED = "sabmstorefront.cart.delivery.cubArranged";
	public static final String DEFAULT_DELIVERY_MODE = CART_DELIVERY_CUBARRANGED;
	public static final String CART_DELIVERY_CUSTOMERARRANGED = "sabmstorefront.cart.delivery.customerArranged";

	public static final String SESSION_ATTR_B2B_UNIT = "session.attr.b2bUnit";
	public static final String SESSION_ATTR_IMPERSONATED_B2B_UNIT = "session.attr.impersonate.b2bUnit";
	public static final String SESSION_ATTR_CART = "cart";
	public static final String SAP_REQUEST_TYPE_CUP_DEAL = "CUP_DEAL";
	public static final String SAP_REQUEST_TYPE_DEAL = "DEAL";

	public static final String DELIVERY_DATE_PATTERN = "yyyyMMdd";
	public static final String SESSION_ATTR_DELIVERY_DATE = "session_delivery_date";
	public static final String SESSION_ATTR_DISABLED_DELIVERY_DATE = "session_disabled_delivery_date";
	public static final String SESSION_ATTR_DELIVERY_MODE = "session_delivery_mode";
	public static final String SESSION_ATTR_DELIVERY_DATE_PACKTYPE = "session_delivery_date_packtype";

	public static final String SESSION_B2BUNIT_INVOICES = "session.attr.invoices";

	public static final String SESSION_B2BUNIT_INVOICES_MAP = "session.attr.invoices.map";

	/** Deal animation displayed mark */
	public static final String SESSION_DEAL_ANIMATION = "session.attr.dealAnimation";

	public static final String COOKIE_REMEMBERME = "-rememberMe";
	public static final String ACTIVATED_DEAL_KEY = "activated";
	public static final String DEACTIVATED_DEAL_KEY = "deactivated";

	public static final String SOLR_INDEXTYPE_PRODUCT = "Product";

	public static final String PAYMENT_CARD_CODE_PREFIX = "payment.card.code.";

	public static final String B2B = "B2B";
	public static final String DEAL_ONLINE_STATUS = "R";

	public static final String ONCE_OFF_DEALS_CONDITION_TYPE = "YDX0";

	public static final String SEARCH_B2BUNIT_STATUS_ACTIVE = "active";
	public static final String SEARCH_B2BUNIT_STATUS_INACTIVE = "inactive";
	public static final String SEARCH_B2BUNIT_STATUS_INVITED = "invited";

	public static final String B2BUNIT_WEB_ORDERING_STATUS_GREEN = "GREEN";
	public static final int B2BUNIT_WEB_ORDERING_PERCENTAGE_GREEN = 80;
	public static final int ASAHI_B2BUNIT_WEB_ORDERING_PERCENTAGE_GREEN = 70;

	public static final String B2BUNIT_WEB_ORDERING_STATUS_YELLOW = "YELLOW";
	public static final int B2BUNIT_WEB_ORDERING_PERCENTAGE_YELLOW = 60;

	public static final String B2BUNIT_WEB_ORDERING_STATUS_RED = "RED";

	public static final String B2BUNIT_WEB_ORDERING_STATUS_NONE = "NO_WEB_ORDERS";
	public static final String B2BUNIT_ORDERING_STATUS_NONE = "NO_ORDERS";

	// Cart customer cash-only flag
	public static final String CART_CUSTOMER_CASHONLY_FLAG = "cart.customer.cashonly.flag";

	// business enquiry
	public static final String BUSINESS_ENQUIRY_DELIVERY = "DELIVERY_ENQUIRY";
	public static final String BUSINESS_ENQUIRY_RETURN = "PRODUCT_RETURN";
	public static final String BUSINESS_ENQUIRY_KEG = "KEG_ISSUE";
	public static final String BUSINESS_ENQUIRY_DELIVERY_ISSUE = "DELIVERY_ISSUE";
	public static final String BUSINESS_ENQUIRY_PRICE = "PRICE_ENQUIRY";
	public static final String BUSINESS_ENQUIRY_PRODUCT = "PRODUCT_ENQUIRY";
	public static final String BUSINESS_ENQUIRY_PICKUP = "KEG_PICKUP";
	public static final String BUSINESS_ENQUIRY_UPDATE = "UPDATE_DETAILS_DELIVERY_OPTIONS";
	public static final String BUSINESS_ENQUIRY_PALLET = "EMPTY_PALLET_PICKUP";
	public static final String BUSINESS_ENQUIRY_WEBSITE = "WEBSITE_FEEDBACK";
	public static final String BUSINESS_ENQUIRY_GENERAL = "GENERAL_ENQUIRY";
	public static final String BUSINESS_ENQUIRY_WEBSITE_ENQ = "WEBSITE_ERRORS";
	public static final String BUSINESS_ENQUIRY_AUTOPAY = "autopay";

	// contact us type
	public static final String BUSINESS_ENQUIRY_CONTACT_US = "contactus";

	public static final String BUSINESS_ENQUIRY_TO_EMAIL_PROP_PREFIX = "business.enquiry.toEmail.";
	public static final String BUSINESS_ENQUIRY_TO_EMAIL_DEFAULT = "onlinesupport@asahibeverages.com";

	public static final String BUSINESS_ENQUIRY_KEG_PICKUP_TO_EMAIL_DEFAULT = "kegpickup@cub.com.au";

	public static final String SESSION_ATTR_CONFLICT_DEALS_CODE = "session.attr.conflict.deals.code";
	public static final String SESSION_ATTR_RESOLVED_CONFLICT_DEALS = "session.attr.resolved.conflict.deals";

	// impersonate user
	public static final String SESSION_ATTR_IMPERSONATE_PA = "session.attr.impersonate.pa";

	public static final String SESSION_ATTR_SMARTORDERJSON = "smartOrderJson";
	public static final String SESSION_ATTR_SMARTORDERPRODUCTCODES = "smartOrderProductCodes";
	public static final String SESSION_ATTR_PREVIOUSSMARTORDERDATE = "previousSmartOrderDate";
	public static final String SESSION_ATTR_NEXTSMARTORDERDATE = "nextSmartOrderDate";

	public static final String DATA_IMPORT_LOAD_MODE = "data.import.load.mode";

	public static final String SCALE_AMOUNT_TYPE_PERCENTAGE = "percentage";
	public static final String SCALE_AMOUNT_TYPE_FIXED = "fixed";
	public static final String SCALE_AMOUNT_TYPE_PERUNIT = "perunit";

	// staff portal
	public static final String STAFF_PORTAL_LOGOUT_REDIRECT = "/logoutRedirect";

	//solr product search
	public static final String SOLR_PRODUCT_SEARCH_CODE_KEY = "code";

	//deal unit
	public static final String SESSION_SELECT_B2BUNIT_UID_DATA = "selectB2BUnitUid";

	public static final int BESTSELLER_MAX_CONSIDERATiON_TIME_IN_MONTHS = 3;

	public static final int LOWEST_POPULARITY_RANK = 9999;

	public static final String FALLBACK_SOLR_SORT_CODE = "relevance";

	//plant Timezone
	public static final String CUTOFFTIME = "cutofftime";
	public static final String PLANT_CUTOFF_TIMEZONE = "plantcutofftimezone";
	//the lost deals     SABMC-1648
	public static final String SESSION_ATTR_LOST_DEAL_CODES = "session.attr.lost.deal.codes";
	public static final String UNIT_KEG = "KEG";

	public static final String RECOMMENDATION_QTY = "QTY";
	public static final String RECOMMENDATION_UNIT = "UNIT";

	public static final String SABMCONFIGURATION_LOWSTOCK_FLAG = "lowStockFlag";
	public static final String CASE_UOM_CODE = "CAS";
	public static final String CASE_UOM_ALTERNATE_CODE1 = "CAR";
	public static final String CASE_UOM_ALTERNATE_CODE2 = "CS";
	public static final String PALLET_UOM_CODE = "PAL";
	public static final String LAYER_UOM_CODE = "LAY";
	public static final String OUTOFSTOCK = "OOS";
	public static final String LOWSTOCK = "Low";
	public static final String SESSION_ATTR_LAST_UPDATED_ENTITIY_DATE = "lastUpdatedEntityTime";

	// SAP SalesVolume / AutoPay Status codes
	public static final String SAP_SALES_VOLUME_ACTIVE = "P1";
	public static final String SAP_SALES_VOLUME_ACTIVE_WITH_ADTNL_DISCOUNT = "P2";
	public static final String SAP_SALES_VOLUME_OPTEDOUT = "P3";
	public static final String SAP_SALES_VOLUME_NONMEMBER = "P0";

	public static final Map<AutoPayStatus, String> AUTOPAY_STATUS_SAP_SALES_VOLUME_MAP = autoPayStatusSapSalesVolumeMap();

	private static Map<AutoPayStatus, String> autoPayStatusSapSalesVolumeMap()
	{
		final Map<AutoPayStatus, String> map = new HashMap<>();
		map.put(AutoPayStatus.ACTIVE, SabmCoreConstants.SAP_SALES_VOLUME_ACTIVE);
		map.put(AutoPayStatus.ACTIVE_WITH_ADTNL_DISCOUNT, SabmCoreConstants.SAP_SALES_VOLUME_ACTIVE_WITH_ADTNL_DISCOUNT);
		map.put(AutoPayStatus.OPTEDOUT, SabmCoreConstants.SAP_SALES_VOLUME_OPTEDOUT);
		map.put(AutoPayStatus.NONMEMBER, SabmCoreConstants.SAP_SALES_VOLUME_NONMEMBER);
		return Collections.unmodifiableMap(map);
	}

	public static final Map<String, AutoPayStatus> SAP_SALES_VOLUME_AUTOPAY_STATUS_MAP = sapSalesVolumeAutoPayStatusMap();

	private static Map<String, AutoPayStatus> sapSalesVolumeAutoPayStatusMap()
	{
		final Map<String, AutoPayStatus> map = new HashMap<>();
		map.put(SabmCoreConstants.SAP_SALES_VOLUME_ACTIVE, AutoPayStatus.ACTIVE);
		map.put(SabmCoreConstants.SAP_SALES_VOLUME_ACTIVE_WITH_ADTNL_DISCOUNT, AutoPayStatus.ACTIVE_WITH_ADTNL_DISCOUNT);
		map.put(SabmCoreConstants.SAP_SALES_VOLUME_OPTEDOUT, AutoPayStatus.OPTEDOUT);
		map.put(SabmCoreConstants.SAP_SALES_VOLUME_NONMEMBER, AutoPayStatus.NONMEMBER);
		return Collections.unmodifiableMap(map);
	}

	public static final ImmutableList<AutoPayStatus> ACTIVE_AUTOPAY_STATUSES = ImmutableList.of(AutoPayStatus.ACTIVE,
			AutoPayStatus.ACTIVE_WITH_ADTNL_DISCOUNT);

	// AutoPay emails/files generation constants
	public static final String AUTOPAY_GENERATED_FILES_HYBRIS_FOLDER_MAIN = "autopay/";
	public static final String AUTOPAY_GENERATED_FILES_HYBRIS_FOLDER_ORDER_EXTRACT = AUTOPAY_GENERATED_FILES_HYBRIS_FOLDER_MAIN
			+ "order_extract";

	public static final String AUTOPAY_CREDIT_CARD_ORDERS_FILENAME = "CUB AutoPay Advantage Credit Card Orders";

	public static final String AUTOPAY_EMAIL_FROM_NAME_SAP_CREDIT_TEAM = "SAP Credit Team";
	public static final String AUTOPAY_CREDIT_CARD_ORDERS_FILE_CONFIG_EMAIL_FROM = "email.orders.by.credit.card.payment.email.from";
	public static final String AUTOPAY_CREDIT_CARD_ORDERS_FILE_CONFIG_EMAIL_TO = "email.orders.by.credit.card.payment.email.to";
	public static final String AUTOPAY_CREDIT_CARD_ORDERS_FILE_CONFIG_EMAIL_SUBJECT = "email.orders.by.credit.card.payment.email.subject";
	public static final String AUTOPAY_CREDIT_CARD_ORDERS_FILE_CONFIG_EMAIL_MESSAGE = "email.orders.by.credit.card.payment.email.body";


	// File downloads
	public static final String DOWNLOAD_PATH = "/WEB-INF/downloads/";
	public static final String AUTOPAY_SIGNUP_FORM_FILENAME = "AutoPay_ADVANTAGE_DirectDebit_Form.pdf";


	// Credit adjustment
	public static final String CREDITADJUSTMENT_GENERATED_FILES_HYBRIS_FOLDER_MAIN = "creditadjustment/";


	public interface PROPERTIES
	{
		String STATIC_HOST_PATH = "statics.host.path";
	}

	// Azure Storage properties
	public static final String AZURE_STORAGE_CONNECTION_STRING = "azure.hotfolder.storage.account.connection-string";
	public static final String RECOMMENDATION_CONTAINER_REFERENCE = "azure.hotfolder.storage.recommendation.container-reference";
	public static final String RECOMMENDATION_FILE_REFERENCE = "azure.hotfolder.storage.recommendation.file-reference";
	public static final String RECOMMENDATION_GROUP_FILE_REFERENCE = "azure.hotfolder.storage.recommendation.group.file-reference";
	public static final String PRODUCT_EXPORT_CONTAINER_REFERENCE = "azure.hotfolder.storage.product.export.container-reference";
	public static final String PRODUCT_HIERARCHY_FILE_REFERENCE = "azure.hotfolder.storage.product.hierarchy.file-reference";

	public static final String RECOMMENDATION_PROCESSING_CONTAINER = "processing/";
	public static final String RECOMMENDATION_ARCHIVE_CONTAINER = "archive/";
	public static final String SMART_RECOMMENDATION_TYPE = "MODEL";

	public static final String ORDER_ENQUIRY = "ORDER_ENQUIRY";
	public static final String UPDATE_EXISTING_ENQUIRY = "UPDATE_EXISTING_ENQUIRY";

	//WebHook

	public static final String WEBHOOK_SESSION_USER = "WebHook_Session_User";
	public static final String SESSION_CART_PROCESSING_TIME = "Session_Cart_Processing_Time_";
	public static final String SESSION_CUB_WEBSERVICES_ATTR = "session.cub.store.value";
	//max order quantity constants.
	public static final String FINAL_MAX_ORDER_QTY = "finalMaxOrderQty";
	public static final String TOTAL_ORDERED_QTY = "totalOrderedQty";
	public static final String CONFIGURED_MAX_QTY = "configuredMaxOrderQty";
	public static final String CUB_WET_PRICE_PERCENTAGE = "cub.wet.price.percentage";

	public static final String MAX_ORDERQTY_START_DATE = "startDate";
	public static final String MAX_ORDERQTY_END_DATE = "endDate";
	public static final String DELETEDCUSTOMERGROUP = "deletedcustomergroup";
	public static final String CUSTOMER_SUPPORT = "Customer Support";
	public static final String ASAHIDIRECT = "@asahidirect.com";
}
