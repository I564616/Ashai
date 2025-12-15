/**
 *
 */
package com.sabmiller.storefront.controllers;

/**
 * SABMWebConstants for Define constants
 *
 * @author yaopeng
 *
 */
public class SABMWebConstants
{

	public static final String SPRING_SECURITY_LOGINDISABLED_STATUS = "SPRING_SECURITY_LOGINDISABLED_STATUS";

	//add by 535
	public static final String CART_DELIVERY_INSTRUCTIONS = "deliveryInstructions";

	public static final String MAX_ORDERHISTROY_COUNT = "sabmstorefront.max.orderHistory.desplayCount";

	public static final String MAX_ORDERTEMPLATE_COUNT = "sabmstorefront.max.ordertemplate.displayCount";

	//add by 1392
	public static final String ACCOUNT_ISDEACTIVATED = "ACCOUNT_ISDEACTIVATED";
	public static final String ACCOUNT_LOGINDISABLED_STATUS_ISDEACTIVATED = "isDeactivated";
	public static final String ACCOUNT_LOGINDISABLED_STATUS_UNDEACTIVATED = "unDeactivated";
	public static final String USERID_ANONYMOUS = "anonymous";

	public static final String EMPOYEE_USER_SEARCH_URL = "/paSearch";

	public static final String ASSISTANT_DEFAULT_PAGE_URL = "/your-business";

	//add by Ref-7

	public static final String LOGIN_ATTEMPTS = "LOGIN_ATTEMPTS";

	public enum PageType
	{
		HOME, DEAL, CONTACT_US, EMAIL_REQUEST, BUSINESS_ENQUIRY, LOGIN, REGISTER, ACCOUNT, PASSWORD_RESET,
		PERSONAL_ASSISTANCE, PREVIEW_CONTENT, SMART_ORDERS, DELIVERY_METHOD_CHECKOUT_STEP, SOP_PAYMENT_RESPONSE,
		MY_ACCOUNT_INVOICE, PAGE_NOT_FOUND, CHECKOUT, RECOMMENDATION , TRACKORDER, INVOICEDISCREPANCY,RAISEDINVOICEDISCREPANCY
	}

	public static final String TRACK_DELIVERY_ORDER = "TrackDeliveryOrder";



}
