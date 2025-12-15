/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.apb.core.constants;

import com.sabmiller.core.constants.GeneratedSabmCoreConstants;

/**
 * Global class for all ApbCore constants. You can add global constants for your extension into this class.
 */
public final class ApbCoreConstants extends GeneratedSabmCoreConstants
{
	public static final String EXTENSIONNAME = "apbcore";


	private ApbCoreConstants()
	{
		//empty
	}

	// implement here constants used by this extension
	public static final String QUOTE_BUYER_PROCESS = "quote-buyer-process";
	public static final String QUOTE_SALES_REP_PROCESS = "quote-salesrep-process";
	public static final String QUOTE_USER_TYPE = "QUOTE_USER_TYPE";
	public static final String QUOTE_SELLER_APPROVER_PROCESS = "quote-seller-approval-process";
	public static final String QUOTE_TO_EXPIRE_SOON_EMAIL_PROCESS = "quote-to-expire-soon-email-process";
	public static final String QUOTE_EXPIRED_EMAIL_PROCESS = "quote-expired-email-process";
	public static final String QUOTE_POST_CANCELLATION_PROCESS = "quote-post-cancellation-process";

	public static final String SELF_REGISTRATION_FROM_EMAIL = "self.registration.from.emailid.";
	public static final String SELF_REGISTRATION_EMAIL_NAME = "self.registration.from.emailid.name.";
	public static final String SELF_REGISTRATION__EMAIL_SUBJECT = "self.registration.to.email.subject.";
	public static final String CUSTOMERDATA = "customerData";

	public static final String SUPER_REGISTRATION_FROM_EMAIL = "super.registration.from.emailid.";
	public static final String SUPER_REGISTRATION_EMAIL_NAME = "super.registration.email.name.";
	public static final String SUPER_REGISTRATION__EMAIL_SUBJECT = "super.registration.to.email.subject.";

	public static final String REQUEST_REGISTER_TO_EMAIL = "request.registration.to.email.";
	public static final String REQUEST_REGISTER_FROM_EMAIL = "request.registration.from.emailid.";
	public static final String REQUEST_REGISTER_EMAIL_NAME = "request.registration.from.email.name.";
	public static final String REQUEST_REGISTER_EMAIL_SUBJECT = "request.registration.email.subject.";
	public static final String REQUEST_REFERENCE_PREFIX = "request.registration.keygen.code.prefix.";

	public static final String COMPANY_DETAILS_TO_EMAIL = "company.details.to.email.";
	public static final String COMPANY_DETAILS_FROM_EMAIL = "company.details.email.fromid.";
	public static final String COMPANY_DETAILS_EMAIL_NAME = "company.details.email.fromname.";
	public static final String COMPANY_DETAILS_EMAIL_SUBJECT = "company.details.email.subject.";
	public static final String CC_EMAIL_ADDRESS = "company.details.email.send.copy.";
	public static final String COMPANY_REFERENCE_PREFIX = "company.details.keygen.code.prefix.";

	public static final String SITE = "site";
	public static final String CONTACT_US_TO_EMAIL = "contactus.to.email.";
	public static final String CONTACT_US_FROM_EMAIL = "contactus.from.emailid.";
	public static final String CONTACT_US_EMAIL_NAME = "contactus.from.email.name.";
	public static final String CONTACT_US_EMAIL_SUBJECT = "contactus.email.subject.";
	public static final String CONTACT_US_REFERENCE_PREFIX = "contactus.keygen.code.prefix.";
	public static final String CONTACT_US_CUSTOMERSERVICE = "contactus.customerservice.";
	public static final String CONTACT_US_CUSTOMERSERVICE_PHONE = "contactus.customerservice.phoneno.";

	public static final String CONTACT_US_SELF_TO_EMAIL = "contactus.to.email.";
	public static final String CONTACT_US_SELF_FROM_EMAIL = "contactus.from.emailid.";
	public static final String CONTACT_US_SELF_EMAIL_NAME = "contactus.from.email.name.";
	public static final String CONTACT_US_SELF_EMAIL_SUBJECT = "contactus.email.subject.";
	public static final String CONTACT_US_SELF_REFERENCE_PREFIX = "contactus.keygen.code.prefix.";

	public static final String KEG_RETURN_TO_EMAIL = "kegreturn.to.email.";
	public static final String KEG_RETURN_FROM_EMAIL = "kegreturn.from.emailid.";
	public static final String KEG_RETURN_EMAIL_NAME = "kegreturn.from.email.name.";
	public static final String KEG_RETURN_EMAIL_SUBJECT = "kegreturn.email.subject.";
	public static final String KEG_RETURN_REFERENCE_PREFIX = "kegreturn.keygen.code.prefix.";
	public static final String KEG_RETURN_HEADING = "Keg Return";

	public static final String FORGOT_PASSWORD_FROM_EMAIL = "forgot.password.from.emailid.";
	public static final String FORGOT_PASSWORD_EMAIL_NAME = "forgot.password.from.email.name.";
	public static final String FORGOT_PASSWORD_EMAIL_SUBJECT = "forgot.password.email.subject.";

	public static final String CUSTOMER_WELCOME_FROM_EMAIL = "customer.welcome.email.from.emailid.";
	public static final String CUSTOMER_WELCOME_EMAIL_NAME = "customer.welcome.email.from.email.name.";
	public static final String CUSTOMER_WELCOME_EMAIL_SUBJECT = "customer.welcome.email.subject.";

	public static final String ASSISTED_CUSTOMER_REGISTRATION_EMAIL_SUBJECT = "assisted.customer.registration.email.subject.";

	public static final String PAYMETN_CREDIT_CARD_REFERENCE_PREFIX = "payment.credit.card.reference.prefix.";

	/** The Constant CATALOG_ID_KEY. */
	public static final String CATALOG_ID_KEY = ".company.catalog.id";

	/** The Constant CATALOG_VERSION_KEY. */
	public static final String CATALOG_VERSION_KEY = ".company.catalog.version.staged";


	public static final String DELIVERY_DATETIMESLOT_PATTERN = "dd-MM-yyyyhh:mm:ss";
	public static final String DEFER_DELIVERY_DATEPATTERN = "dd/MM/yyyy";
	public static final String SAM_DOCUMENT_HYBRIS_DATEPATTERN = "yyyy-MM-dd HH:mm:ss";

	public static final String DATE_PATTERN_DDMMYYYY = "dd-MM-yyyy";
	public static final String DATE_PATTERN_DDMMMYYYY = "dd-MMM-yyyy";
	public static final String STRING_SEPARATOR_WHITESPACE = "\\s+";
	public static final String STRING_SEPARATOR_PIPE = "\\|";


	/**
	 * The Order Confirmation From Email
	 */
	public static final String ORDER_CONFIRMATION_FROM_EMAIL = "order.confirmation.from.emailid.";
	/**
	 * The Order Confirmation Display Name
	 */
	public static final String ORDER_CONFIRMATION_EMAIL_NAME = "order.confirmation.email.name.";
	/**
	 * The Order Confirmation Email Subject
	 */
	public static final String ORDER_CONFIRMATION__EMAIL_SUBJECT = "order.confirmation.to.email.subject.";

	public static final String CUSTOMER_SESSION_INCLUSION_LIST = "customerInclusionList";

	public static final String IS_CUSTOMER_SESSION_CREDIT_BLOCK = "isCreditBlock";
	public static final String IS_SHOW_PRODUCT_WITHOUT_PRICE = "showProductWithoutPrice";

	public static final String APB_SITE_ID = "apb";

	public static final String SGA_SITE_ID = "sga";

	public static final Object SGA_CREDTI_BLOCK_ERROR = "sga.user.credit.block.message";

	public static final String CREDIT_BLOCK_CODE = "credit_block";
	public static final String PRODUCTS_BLOCK_CODE = "products_block";

	public static final String SAMPLE_LOGIN_JSON = "{ \"loginResponse\": { \"isBlocked\": false, \"items\": [ { \"isPromoFlag\" : true, \"isExcluded\" : false, \"netPrice\": 7.78, \"isListed\": true, \"materialNumber\": \"2558\", \"gst\": 10, \"listPrice\": 31.74, \"containerDepositLevy\": 3.03, \"promoText\" : \"You will get 10 % discount\" }, { \"isPromoFlag\" : true, \"isExcluded\" : false, \"netPrice\": 7.78, \"isListed\": true, \"materialNumber\": \"10000840\", \"gst\": 10, \"listPrice\": 31.74, \"containerDepositLevy\": 3.03, \"promoText\" : \"You will get 10 % discount\" } ] } } ";

	public static final String SAMPLE_CHECKOUT_JSON = "{ \"checkoutResponse\": { \"isBlocked\": false, \"items\": [ { \"isPromoFlag\" : true, \"isExcluded\" : false, \"netPrice\": 7.78, \"isListed\": true, \"materialNumber\": \"2558\", \"gst\": 10, \"totalGst\": 5, \"totalCdl\":4 ,\"listPrice\": 31.74, \"containerDepositLevy\": 3.03, \\\"dicount\\\": 3.03, \"promoText\" : \"You will get 10 % discount\" }, { \"isPromoFlag\" : true, \"isExcluded\" : false, \"netPrice\": 7.78, \"isListed\": true, \"materialNumber\": \"10000840\", \"gst\": 10, \"totalGst\": 5, \"totalCdl\":4 , \"listPrice\": 31.74, \"containerDepositLevy\": 3.03, \\\"discount\\\": 3.03, \"promoText\" : \"You will get 10 % discount\" } ] } } ";
	public static final String INCLUSION_CHECKOUT_FLAG = "isCheckoutInvoked";
	public static final String PRODUCT_BLOCK_FLAG = "isProductsBlocked";


	public static final String NO_DELIVERY_PROCESS = "asahiNoDeliveryNotifyEmailProcess";
	public static final String ALT_CALLDAY_DELIVERY_PROCESS = "asahiAlternateCallDayNotifyEmailProcess";
	public static final String ALT_DELDATE_DELIVERY_PROCESS = "asahiAlternateDeliveryDateNotifyEmailProcess";

	public static final String NO_DELIVERY = "sga.email.notify.noDelivery";
	public static final String ALT_CALLDAY_DELIVERY = "sga.email.notify.altCallDay";
	public static final String ALT_DELDATE_DELIVERY = "sga.email.notify.altDelDate";

	public static final String NO_DELIVERY_TEMPLATE = "asahiNoDeliveryEmail";
	public static final String ALT_CALLDAY_DELIVERY_TEMPLATE = "asahiAlternateCallDayEmail";
	public static final String ALT_DELDATE_DELIVERY_TEMPLATE = "asahiAlternateDeliveryDateEmail";

	public static final String PAYMENT_CONFIRMATION_EMAIL_PROCESS = "asahiPaymentConfirmationEmailProcess";
	public static final String PAYMENT_CONFIRMATION_FROM_EMAIL = "email.payment.confirmation.from.emailid";
	public static final String PAYMENT_CONFIRMATION_EMAIL_NAME = "email.payment.confirmation.name";
	public static final String PAYMENT_CONFIRMATION_EMAIL_SUBJECT = "email.payment.confirmation.to.email.subject";

	public static final String NOTIFY_FROM_EMAIL = "email.notify.from.emailid.";
	public static final String NOTIFY_EMAIL_NAME = "email.notify.email.name.";
	public static final String NOTIFY_EMAIL_SUBJECT = "email.notify.to.email.subject.";
	public static final String NOTIFY_EMAIL_PAGE = "email.notify.email.page.";
	public static final String SAVE_CUSTOMER_NOTIFY_PROCESS = "sga.save.customer.notify.process";
	public static final String ERROR_001 = "001";
	public static final String ERROR_002 = "002";
	public static final String ERROR_003 = "003";
	public static final String ERROR_004 = "004";
	public static final String BONUS_STOCK_GLOBAL_MAX_QUANTITY = "bonus.stock.global.max.quantity.";
	public static final String BONUS_STOCK__MAX_QTY_QUANTITY = "bonus.stock.product.max.quantity.";
	public static final String B2B_ADMIN_GROUP = "B2BADMINGROUP";

	public static final String SAM_INVOICE_FINANCIALYEAR_START = "sam.financial.year.start";
	public static final String SAM_OPEN_INVOICE_HISTORY_TIMEFRAME = "sam.open.invoice.history.timeframe";
	public static final String SAM_CLOSED_INVOICE_HISTORY_TIMEFRAME = "sam.closed.invoice.history.timeframe";
	public static final String SAM_INVOICE_HISTORY_PAGESIZE = "sam.invoice.history.pagesize";

	/**
	 * The Component Constant for Direct Debit Form.
	 */
	public static final String DIRECT_DEBIT_FORM_COMPONENT = "direct.debit.form.component.";

	public static final String SAM_PAYMENT_HYBRIS_PREFIX_CODE = "sam.payment.hybris.prefix.code.";
	public static final String ASAHI_DATE_FORMAT_KEY = "site.date.format.sga";

	/**
	 * The Constant for invoice payment selected
	 */
	public static final String INVOICE_PAY_SELECTED = "isPaySelected";
	/**
	 * The Constant for invoice payment Form
	 */
	public static final String INVOICE_PAYMENT_FORM = "invoicePaymentForm";
	public static final String PAY_AND_ORDER_ACCESS = "PAY_AND_ORDER";
	public static final String PAY_ACCESS = "PAY_ONLY";
	public static final String ORDER_ACCESS = "ORDER_ONLY";
	public static final String SAM_ACCESS_APPROVAL_PENDING = "APPROVAL_PENDING";


	public static final String QUERY_DATE_FORMAT = "dd/mm/YYYY";
	public static final String ASAHI_QUERY_DATE_FORMAT_KEY = "query.date.format.";

	public static final String PAYER_ACCESS_APPROVE_EMAIL_PROCESS = "approvePayerAccessEmailProcess";
	public static final String PAYER_ACCESS_REJECT_EMAIL_PROCESS = "rejectPayerAccessEmailProcess";
	public static final String PAYER_ACCESS_REQUEST_EMAIL_PROCESS = "requestPayerAccessEmailProcess";
	public static final String PAYER_ACCESS_EXPIRED_EMAIL_PROCESS = "expiredPayerAccessEmailProcess";
	public static final String PAYER_ACCESS_SUPERUSER_REQUEST_EMAIL_PROCESS = "superUserRequestPayerAccessEmailProcess";
	public static final String PAYER_ACCESS_FROM_EMAIL = "payer.access.from.emailid.";
	public static final String PAYER_ACCESS_EMAIL_NAME = "payer.access.from.email.name.";
	public static final String PAYER_ACCESS_EMAIL_SUBJECT = "payer.access.email.subject.";
	public static final String PAYER_ACCESS_TO_EMAIL = "payer.access.to.emailid.";
	public static final String PAYER_ACCESS_APPROVE = "approve";
	public static final String PAYER_ACCESS_REJECT = "reject";
	public static final String PAYER_ACCESS_REQUEST = "request";
	public static final String PAYER_ACCESS_EXPIRED = "expired";
	public static final String PAYER_ACCESS_SUPERUSER_REQUEST = "superUserRequest";
	public static final String PAYER_ACCESS_COMPLETED = "completed";
	public static final String UPDATE_ORDER_ACCESS = "UPDATE_ORDER_ACCESS";
	public static final String ALB_CUSTOMER_SESSION_EXCLUDED_CATEGORIES = "excludedCategories";
	public static final String EXCLUDED_CATEGORY_RECALCULATION = "recalculateExclusionCategories";
	public static final String STAFF_FLOW_CUSTOMER_REGISTRATION_EMAIL_SUBJECT = "staff.flow.customer.registration.subject.";

	public static final String CUSTOMER_PROFILE_UPDATE_FROM_EMAIL = "customer.profile.update.email.from.emailid.";
	public static final String CUSTOMER_PROFILE_UPDATE_EMAIL_NAME = "customer.profile.update.email.from.email.name.";
	public static final String CUSTOMER_PROFILE_UPDATE_EMAIL_SUBJECT = "customer.profile.update.email.subject.";
	public static final String ITEM_CATEGORY = "TAN";
	public static final String FREE_ITEM_CATEGORY = "TANN";


	public static final String IS_CLOSE_TO_CREDIT_BLCOK = "isCloseToCreditBlock";
	public static final String CLOSE_TO_CREDIT_BLOCK = "close_to_credit_block";
	public static final String IS_ON_ACCOUNT_DISABLED = "isOnAccountDisabled";
	public static final String DELTA_TO_LIMIT = "deltaToLimit";
	public static final String CREDIT_LIMIT = "creditLimit";


	public static final String ASAHI_DEAL_UPDATE_FROM_EMAIL = "asahi.deal.update.email.from.emailid.";
	public static final String ASAHI_DEAL_UPDATE_EMAIL_NAME = "asahi.deal.update.email.name.";
	public static final String ASAHI_DEAL_UPDATE_EMAIL_SUBJECT = "asahi.deal.update.email.subject.";
	public static final String EXPIRED_OR_INVALID_FREEGOODS_ENTRY = "ExpiredOrInvalidFreeGoodsEntry";

	public static final String CUB_MAX_ORDER_QTY_RULE_DAYS = "cub.max.order.qty.rule.days";
	public static final String CUB_MAXORDER_QTY_RULE_DAYS = "maxOrderQtyRuleDays";
	public static final String DEFAULT_MAX_ORDER_QTY_RULE_DAYS = "7";

}
