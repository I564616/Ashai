/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package com.sabmiller.sfmc.constants;

/**
 * Global class for all Sabmsfmcservices constants. You can add global constants for your extension into this class.
 */
public final class SabmsfmcservicesConstants extends GeneratedSabmsfmcservicesConstants
{
	public static final String EXTENSIONNAME = "sabmsfmcservices";

	public static final String EMAIL_FROM_DEFAULT = "email@e.cub.com.au";
	public static final String EMAIL_NAME_DEFAULT = "CUB Online";

	public static final String API_VERSION = "salesforce.api.version";
	public static final String CASE_ORIGIN = "salesforce.case.origin";
	public static final String BUSINESS_UNIT = "salesforce.case.businessUnit";

	public static final String QUERY_IDENTIFIER = "salesforce.query.identifier";
	public static final String QUERY_CUSTOMER_HIERARCHY = "salesforce.query.customerHierarchy";
	public static final String QUERY_OUTLET = "salesforce.query.outlet";
	public static final String QUERY_CONTACT = "salesforce.query.contact";
	public static final String QUERY_PRODUCT = "salesforce.query.product";
	public static final String RECORD_TYPE_KEG_COMPLAINT = "salesforce.kegComplaint.case.recordtype";

	public static final String DEFAULT_API_VERSION = "v62.0";

	public static final String DEFAULT_QUERY_IDENTIFIER = "SELECT Id, AccountProfile__r.Account__c FROM Identifier__c WHERE Value__c='%s' LIMIT 1";
	public static final String DEFAULT_QUERY_CUSTOMER_HIERARCHY = "SELECT Id, cgcloud__Child_Account__c FROM cgcloud__Account_Trade_Org_Hierarchy__c WHERE Active__c=true AND cgcloud__Valid_From__c<=TODAY AND cgcloud__Valid_Thru__c>TODAY AND cgcloud__Parent_Account__c='@{GetIdentifier.records[0].AccountProfile__r.Account__c}' LIMIT 1";
	public static final String DEFAULT_QUERY_OUTLET = "SELECT Id FROM RetailStore WHERE AccountOutlet__c='@{GetCustomerTradeOrgHierarchy.records[0].cgcloud__Child_Account__c}' LIMIT 1";
	public static final String DEFAULT_QUERY_CONTACT = "SELECT Id FROM Contact WHERE CUBOnlinePK__c='%s' OR Email='%s' OR CUBOnlineEmail__c='%s' LIMIT 1";
	public static final String DEFAULT_QUERY_PRODUCT = "SELECT Id FROM Product2 WHERE StockKeepingUnit='%s' LIMIT 1";

	public static final String DEFAULT_ORIGIN = "Web";
	public static final String DEFAULT_BUSINESS_UNIT = "CUB";
	public static final String ADMIN_USER = "admin";
	public static final String SERVICES_DATA_STRING = "/services/data/";
	public static final String QUERY_STRING = "/query/?q=";
	public static final String SOBJECTS_CASE_STRING = "/sobjects/Case";
	public static final String SOBJECTS_CASEPRODUCT_STRING = "/sobjects/CaseProduct__c";
	public static final String COMPOSITE = "/composite";

	public static final String GET_ACCOUNTID = "@{GetIdentifier.records[0].AccountProfile__r.Account__c}";
	public static final String GET_CONTACTID = "@{GetContact.records[0].Id}";
	public static final String GET_RECORDTYPEID = "0129n000002BL9dAAG";
	public static final String GET_OUTLET = "@{GetOutlet.records[0].Id}";
	public static final String GET_CASE = "@{CreateCase.id}";
	public static final String GET_PRODUCT = "@{GetProduct.records[0].Id}";

	public static final String SUCCESS_STATUS = "success";
	public static final String FAILED_STATUS = "failed";
	public static final int RESPONSE_CODE_400 = 400;

	public static final String GET_IDENTIFIER_PARAMETER = "GetIdentifier";
	public static final String GET_CUSTOMER_TRADE_ORG_HIERARCHY_PARAMETER = "GetCustomerTradeOrgHierarchy";
	public static final String GET_OUTLET_PARAMETER = "GetOutlet";
	public static final String GET_CONTACT_PARAMETER = "GetContact";
	public static final String CREATE_CASE_PARAMETER = "CreateCase";
	public static final String GET_PRODUCT_PARAMETER = "GetProduct";
	public static final String CREATE_CASE_PRODUCT_PARAMETER = "CreateCaseProduct";

	public static final String ACCOUNT_ID_PARAMETER = "AccountId";
	public static final String CONTACT_ID_PARAMETER = "ContactId";
	public static final String RECORD_TYPE_ID_PARAMETER = "RecordTypeId";
	public static final String OUTLET_PARAMETER = "Outlet__c";
	public static final String DESCRIPTION_PARAMETER = "Description";
	public static final String TYPE_PARAMETER = "Type";
	public static final String SUPPLIED_EMAIL_PARAMETER = "SuppliedEmail";
	public static final String ORIGIN_PARAMETER = "Origin";
	public static final String BUSINESS_UNIT_PARAMETER = "BusinessUnit__c";
	public static final String CONTACT_PHONE_NUMBER_PARAMETER = "ContactPhoneNumber__c";
	public static final String REASON_CODE_PARAMETER = "ReasonCode__c";
	public static final String CASE_PARAMETER = "Case__c";
	public static final String PRODUCT_PARAMETER = "Product__c";
	public static final String KEG_SERIAL_NUMBER_PARAMETER = "KegSerialNumber__c";
	public static final String BEST_BEFORE_DATE_AVAILABLE_PARAMETER = "BestBeforeDateAvailable__c";
	public static final String BEST_BEFORE_DATE_PARAMETER = "BestBeforeDate__c";
	public static final String PRODUCTION_PLANT_CODE_PARAMETER = "ProductionPlantCode__c";
	public static final String TIME_CODE_PARAMETER = "TimeCode__c";
}
