/*
 *
 */
package com.apb.core.constants;

/**
 * The Class ApbQueryConstant.
 *
 * @author Kuldeep.Singh1
 */
public class ApbQueryConstant
{

	/** The Constant GET_ALCOHOL_TYPE_FOR_CODE. */
	public static final String GET_ALCOHOL_TYPE_FOR_CODE = "SELECT {PK} FROM {AlcoholType} WHERE {code} = ?code";

	/** The Constant GET_ITEM_GROUP_FOR_CODE. */
	public static final String GET_ITEM_GROUP_FOR_CODE = "SELECT {PK} FROM {ItemGroups} WHERE {code} = ?code";

	/** The Constant GET_PACKAGE_TYPE_FOR_CODE. */
	public static final String GET_PACKAGE_TYPE_FOR_CODE = "SELECT {PK} FROM {PackageType} WHERE {code} = ?code";

	/** The Constant GET_FLAVOUR_FOR_CODE. */
	public static final String GET_FLAVOUR_FOR_CODE = "SELECT {PK} FROM {Flavour} WHERE {code} = ?code";

	/** The Constant GET_BRAND_FOR_CODE. */
	public static final String GET_BRAND_FOR_CODE = "SELECT {PK} FROM {Brand} WHERE {code} = ?code";

	/** The Constant GET_APB_B2B_UNIT_BY_ABN_NUMBER. */
	public static final String GET_APB_B2B_UNIT_BY_ABN_NUMBER = "SELECT {PK} FROM {AsahiB2BUnit} WHERE {abnNumber} = ?abnNumber";

	/** The Constant GET_ACCOUNT_GROUP_FOR_CODE. */
	public static final String GET_ACCOUNT_GROUP_FOR_CODE = "SELECT {PK} FROM {AccountGroups} WHERE {code} = ?code";

	/** The Constant GET_ACCOUNT_TYPE_FOR_CODE. */
	public static final String GET_ACCOUNT_TYPE_FOR_CODE = "SELECT {PK} FROM {AccountType} WHERE {code} = ?code";

	/** The Constant GET_BANNER_GROUPS_FOR_CODE. */
	public static final String GET_BANNER_GROUPS_FOR_CODE = "SELECT {PK} FROM {BannerGroups} WHERE {code} = ?code";

	/** The Constant GET_CHANNEL_FOR_CODE. */
	public static final String GET_CHANNEL_FOR_CODE = "SELECT {PK} FROM {Channel} WHERE {code} = ?code";

	/** The Constant GET_LICENSE_CLASS_FOR_CODE. */
	public static final String GET_LICENSE_CLASS_FOR_CODE = "SELECT {PK} FROM {LicenceClass} WHERE {code} = ?code";

	/** The Constant GET_SUB_CHANNEL_FOR_CODE. */
	public static final String GET_SUB_CHANNEL_FOR_CODE = "SELECT {PK} FROM {SubChannel} WHERE {code} = ?code";

	/** The Constant GET_LICENSE_TYPES_FOR_CODE. */
	public static final String GET_LICENSE_TYPES_FOR_CODE = "SELECT {PK} FROM {LicenseTypes} WHERE {code} = ?code";

	/** The Constant GET_UNIT_FOR_CODE. */
	public static final String GET_UNIT_FOR_CODE = "SELECT {PK} FROM {Unit} WHERE {code} = ?code";

	/** The Constant GET_CURRENCY_FOR_ISO_CODE. */
	public static final String GET_CURRENCY_FOR_ISO_CODE = "SELECT {PK} FROM {Currency} WHERE {isocode} = ?currencyIso";

	/** The Constant GET_WAREHOUSR_FOR_CODE. */
	public static final String GET_WAREHOUSE_FOR_CODE = "SELECT {PK} FROM {Warehouse} WHERE {code} = ?code";

	/** The Constant GET_PRODUCTS_FOR_CODE_AND_CATALOG_VERSION. */
	public static final String GET_PRODUCTS_FOR_CODE_AND_CATALOG_VERSION = "SELECT {PK} FROM {ApbProduct} WHERE {code} = ?code AND  {catalogversion} = ?catalogVersion";

	/** The Constant GET_ADDRESS_FOR_ADDRESS_RECORD_ID. */
	public static final String GET_ADDRESS_FOR_ADDRESS_RECORD_ID = "SELECT {PK} FROM {Address} WHERE {addressRecordid} = ?addressRecordid AND {owner} = ?owner";

	/** The Constant GET_CATEGORY_FOR_CODE. */
	public static final String GET_CATEGORY_FOR_CODE = "SELECT {PK} FROM {Category} WHERE {code} = ?code AND {catalogVersion} = ?catalogVersion";

	/** The Constant GET_PACKAGE_SIZE_FOR_CODE. */
	public static final String GET_PACKAGE_SIZE_FOR_CODE = "SELECT {PK} FROM {PackageSize} WHERE {code} = ?code";

	/** The Constant GET_ORDER_FOR_ORDER_STATUS. */
	public static final String GET_ORDER_FOR_ORDER_STATUS = "SELECT {PK} FROM {Order} where  {status} in (?status) AND {site} = ?site";

	/** The Constant GET_PROCESS_LOG_FOR_OBJECT. */
	public static final String GET_PROCESS_LOG_FOR_OBJECT = "SELECT {PK} FROM {ProcessingJobLog} where  {objectType}=?objectType AND {objectId}=?objectId";

	/** The Constant GET_UNIT_VOLUME_FOR_CODE. */
	public static final String GET_UNIT_VOLUME_FOR_CODE = "SELECT {PK} FROM {UnitVolume} WHERE {code} = ?code";

	/** The Constant GET_ORDER_FOR_CODE. */
	public static final String GET_ORDER_FOR_CODE = "SELECT {PK} FROM {Order} WHERE {code} = ?code";

	/** The Constant GET_CONFIG_VALUE_FOR_KEY. */
	public static final String GET_CONFIG_VALUE_FOR_KEY = "SELECT {configValue} FROM {Configuration} WHERE {configKey}=?configKey AND {configValue} IS NOT NULL";

	/** The Constant GET_ORDER_ENTRY_FOR_BACKEND_UID. */
	public static final String GET_ORDER_ENTRY_FOR_BACKEND_UID = "SELECT {PK} FROM {AbstractOrderEntry} WHERE {backendUid}=?backendUid";

	/** The Constant GET_CART_FOR_CODE_AND_B2BUNIT. */
	public static final String GET_CART_FOR_CODE_AND_B2BUNIT = "SELECT {PK} FROM {OrderTemplate} WHERE lower({name})=lower(?code) AND {b2bUnit} = ?b2bUnit";

	/** The Constant GET_CART_FOR_B2BUNIT. */
	public static final String GET_CART_FOR_B2BUNIT = "SELECT {PK} FROM {OrderTemplate} WHERE {b2bUnit} = ?b2bUnit";

	/** The Constant GET_SUB_PRODUCT_GROUP_FOR_CODE. */
	public static final String GET_SUB_PRODUCT_GROUP_FOR_CODE = "SELECT {PK} FROM {SubProductGroup} WHERE {code} = ?code";

	/** The Constant GET_ORDER_FOR_ORDER_STATUS. */
	public static final String GET_ORDER_DISPLAY_STATUS_MAPPING = "SELECT {PK} FROM {OrderStatusMapping} where  {statusCode} in (?statusCode)";

	public static final String GET_ORDER_STATUS_MAPPING = "SELECT {PK} FROM {OrderStatusMapping} where  {dynamicsStatusCode} in (?dynamicsStatusCode)";

	/** The Constant GET_BASE_SITE_BY_UID. */
	public static final String GET_BASE_SITE_BY_UID = "SELECT {PK} FROM {BaseSite} WHERE {uid}= ?uid";

	/** The Constant GET_BASE_STORE_BY_UID. */
	public static final String GET_BASE_STORE_BY_UID = "SELECT {PK} FROM {BaseStore} WHERE {uid}= ?uid";

	/** The Constant GET_ORDER_TEMPLATE_FOR_CODE_AND_B2B_UNIT. */
	public static final String GET_ORDER_TEMPLATE_FOR_CODE_AND_B2B_UNIT = "SELECT {PK} FROM {OrderTemplate} WHERE {code} = ?code AND {b2bunit} = ?b2bunit";

	/** The Constant GET_ORDER_TEMPLATE_ENTRY_FOR_PK. */
	public static final String GET_ORDER_TEMPLATE_ENTRY_FOR_PK = "SELECT {PK} FROM {OrderTemplateEntry} WHERE {pk}= ?pk";

	/** The Constant GET_CONTACTUS_QUERYTYPE_BY_SITE. */
	public static final String GET_CONTACTUS_QUERYTYPE_BY_SITE = "SELECT {PK} FROM {ContactUsQueryType} WHERE {site}= ?site ORDER BY {code} ASC";
	/** The Constant GET_ORDER_ENTRY_FOR_PRODUCT_AND_ORDER_ID. */
	public static final String GET_ORDER_ENTRY_FOR_PRODUCT_AND_ORDER_ID = "SELECT {PK} FROM {OrderEntry} WHERE {product}= ?product AND {isBonusStock} = false AND {order} = ({{SELECT {PK} FROM {ORDER} WHERE {code} = ?order}})";

	/** The Constant GET_APB_B2B_UNIT_BY_BACKEND_RECORD_ID. */
	public static final String GET_APB_B2B_UNIT_BY_BACKEND_RECORD_ID = "SELECT {PK} FROM {AsahiB2BUnit} WHERE {backendRecordID} = ?backendRecordID";

	/** The Constant GET_APB_B2B_UNIT_BY_ACCOUNT_NUMBER. */
	public static final String GET_APB_B2B_UNIT_BY_ACCOUNT_NUMBER = "SELECT {PK} FROM {AsahiB2BUnit} WHERE {accountNum} = ?accountNum";

	/** The Constant GET_APB_B2B_UNIT_BY_ACCOUNT_NUMBER. */
	public static final String GET_APB_B2B_UNIT_BY_UID = "SELECT {PK} FROM {AsahiB2BUnit} WHERE {uid} = ?uid";

	/** The Constant GET_PRODUCT_GROUP_FOR_CODE. */
	public static final String GET_PRODUCT_GROUP_FOR_CODE = "SELECT {PK} FROM {ProductGroup} WHERE {code} = ?code";

	public static final String GET_ADDRESS_STATUS_MAPPING = "SELECT {PK} FROM {AddressStatusMapping} where  {dynamicsStatusCode} in (?dynamicsStatusCode)";

	public static final String GET_TITLES = "SELECT {PK} FROM {Title} where  {businessCode} = ?businessCode";

	public static final String GET_CART_PRODUCT_CODES = "SELECT DISTINCT {code}, {oe.quantity}, {oe.entrynumber}, {oe.isFreeGood},{oe.isBonusStock} from {ApbProduct as prd join AbstractOrderEntry as oe on {oe.product}={prd.pk} join AbstractOrder as order on {order.pk}={oe.order}} where {order.code}=?code order by {oe.entrynumber}";

	/** The Constant GET_ORDER_ENTRY_FOR_ORDER_AND_USER. */
	public static final String GET_ORDER_ENTRY_FOR_ORDER_AND_USER = "SELECT {PK} FROM {Order} WHERE {unit} = ?unit AND {creationtime}< ?currentDate  AND {creationtime}> ?previousDate ORDER BY {creationtime} DESC";

	/** The Constant GET_PREVIOUS_ORDER_ENTRIES. */
	public static final String GET_PREVIOUS_ORDER_ENTRIES = "SELECT {PK} FROM {Order} WHERE {user} = ?user AND {creationtime}< ?currentDate  AND {creationtime}> ?previousDate ORDER BY {creationtime} DESC";

	/** The Constant GET_ADDRESS_BY_ADDRESS_ID. */
	public static final String GET_ADDRESS_BY_ADDRESS_ID = "SELECT {PK} FROM {Address} WHERE {addressRecordid} = ?addressRecordid";

	/** The Constant GET_STATEMENT_BY_NUMBER. */
	public static final String GET_STATEMENT_BY_NUMBER = "SELECT {PK} FROM {AsahiSAMStatements} WHERE {statementNumber} = ?statementNumber";

	/** The Constant GET_INVOICE_BY_DOCUMENT_NUMBER. */
	public static final String GET_INVOICE_BY_DOCUMENT_NUMBER = "SELECT {PK} FROM {AsahiSAMInvoice} WHERE {documentNumber} = ?documentNumber AND {lineNumber} = ?lineNumber";

	/** The Constant GET_PAYMENT_BY_CLEARING_DOC_NUMBER. */
	public static final String GET_PAYMENT_BY_CLEARING_DOC_NUMBER = "SELECT {PK} FROM {AsahiSAMPayment} WHERE {clrDocNumber} = ?clrDocNumber";

	/** The Constant GET_SAM_INVOICE_DATA. */
	public static final String GET_SAM_INVOICE_DATA = "SELECT {asi.PK} FROM {AsahiSAMInvoice as asi JOIN AsahiB2BUnit as abu on {asi.custAccount}={abu:pk} JOIN AsahiSAMInvoiceStatus as status on {asi.status}={status.pk} JOIN AsahiSAMDocumentType as docType on {asi.documentType}={docType.pk}} WHERE {asi.custAccount} IN ({{SELECT {PK} FROM {AsahiB2BUnit} WHERE {payerAccount} = ({{SELECT {PK} FROM {AsahiB2BUnit} WHERE {uid} =?uid}})}}) AND {asi.invoiceDueDate}>= ?previousDate";

	/** The Constant GET_SAM_INVOICE_DATA_COFODATE. */
	public static final String GET_SAM_INVOICE_DATA_COFODATE = "SELECT {asi.PK} FROM {AsahiSAMInvoice as asi JOIN AsahiB2BUnit as abu on {asi.custAccount}={abu:pk} JOIN AsahiSAMInvoiceStatus as status on {asi.status}={status.pk} JOIN AsahiSAMDocumentType as docType on {asi.documentType}={docType.pk}} WHERE {asi.custAccount} IN ({{SELECT {PK} FROM {AsahiB2BUnit} WHERE {payerAccount} = ({{SELECT {PK} FROM {AsahiB2BUnit} WHERE {uid} =?uid}})}}) AND {asi.invoiceDate}>= ?previousDate";

	/** The Constant GET_SAM_INVOICE_COUNT. */
	public static final String GET_SAM_INVOICE_COUNT = "SELECT count({asi.PK}) FROM {AsahiSAMInvoice as asi JOIN AsahiB2BUnit as abu on {asi.custAccount}={abu:pk} JOIN AsahiSAMInvoiceStatus as status on {asi.status}={status.pk} JOIN AsahiSAMDocumentType as docType on {asi.documentType}={docType.pk}} WHERE {asi.custAccount} IN ({{SELECT {PK} FROM {AsahiB2BUnit} WHERE {payerAccount} = ({{SELECT {PK} FROM {AsahiB2BUnit} WHERE {uid} =?uid}})}}) AND {asi.invoiceDueDate}>= ?previousDate";

	/** The Constant GET_SAM_INVOICE_COUNT_COFODATE. */
	public static final String GET_SAM_INVOICE_COUNT_COFODATE = "SELECT count({asi.PK}) FROM {AsahiSAMInvoice as asi JOIN AsahiB2BUnit as abu on {asi.custAccount}={abu:pk} JOIN AsahiSAMInvoiceStatus as status on {asi.status}={status.pk} JOIN AsahiSAMDocumentType as docType on {asi.documentType}={docType.pk}} WHERE {asi.custAccount} IN ({{SELECT {PK} FROM {AsahiB2BUnit} WHERE {payerAccount} = ({{SELECT {PK} FROM {AsahiB2BUnit} WHERE {uid} =?uid}})}}) AND {asi.invoiceDate}>= ?previousDate";

	/** The Constant GET_SAM_INVOICE_SUM. */
	public static final String GET_SAM_INVOICE_SUM = "SELECT {asi.remainingAmount} FROM {AsahiSAMInvoice as asi JOIN AsahiB2BUnit as abu on {asi.custAccount}={abu:pk} JOIN AsahiSAMInvoiceStatus as status on {asi.status}={status.pk} JOIN AsahiSAMDocumentType as docType on {asi.documentType}={docType.pk}} WHERE {asi.custAccount} IN ({{SELECT {PK} FROM {AsahiB2BUnit} WHERE {payerAccount} = ({{SELECT {PK} FROM {AsahiB2BUnit} WHERE {uid} =?uid}})}}) AND {asi.invoiceDueDate}>= ?previousDate";

	/** The Constant GET_SAM_INVOICE_SUM_COFODATE. */
	public static final String GET_SAM_INVOICE_SUM_COFODATE = "SELECT {asi.remainingAmount} FROM {AsahiSAMInvoice as asi JOIN AsahiB2BUnit as abu on {asi.custAccount}={abu:pk} JOIN AsahiSAMInvoiceStatus as status on {asi.status}={status.pk} JOIN AsahiSAMDocumentType as docType on {asi.documentType}={docType.pk}} WHERE {asi.custAccount} IN ({{SELECT {PK} FROM {AsahiB2BUnit} WHERE {payerAccount} = ({{SELECT {PK} FROM {AsahiB2BUnit} WHERE {uid} =?uid}})}}) AND {asi.invoiceDate}>= ?previousDate";

	/** The Constant APPEND_INVOICE_DATA_STATUS. */
	public static final String APPEND_INVOICE_DATA_STATUS = " AND {status.code}= ?status";

	/** The Constant APPEND_INVOICE_PAYMENT_TYPE. */
	public static final String APPEND_INVOICE_PAYMENT_TYPE = " AND {asi.paymentMade}= 0";

	/** The Constant APPEND_INVOICE_PAYMENT_TYPE2. */
	public static final String APPEND_INVOICE_PAYMENT_TYPE2 = " AND {docType.code} = ?docType";

	/** The Constant APPEND_INVOICE_DATA_DOCUMENTTYPE. */
	public static final String APPEND_INVOICE_DATA_DOCUMENTTYPE = " AND {docType.code}= ?documentType";

	/** The Constant APPEND_INVOICE_DATA_DUENOW. */
	public static final String APPEND_INVOICE_DATA_DUENOW = " AND {asi.invoiceDueDate} <= ?currentDate";

	/** The Constant APPEND_INVOICE_KEYWORD. */
	public static final String APPEND_INVOICE_KEYWORD = " AND ({asi.documentNumber} LIKE ?keyword OR {asi.deliveryNumber} LIKE ?keyword OR {asi.remainingAmount} LIKE ?keyword OR upper({abu.name}) LIKE ?keyword )";

	/** The Constant APPEND_INVOICE_DATA_NOTYETDUE. */
	public static final String APPEND_INVOICE_DATA_NOTYETDUE = " AND {asi.invoiceDueDate} > ?currentDate";

	/** The Constant GET_INVOICE_ORDERBY_INVOICEDATE. */
	public static final String GET_INVOICE_ORDERBY_INVOICEDATE = " ORDER BY {asi.invoiceDueDate}";

	/** The Constant GET_STATEMENT_BY_UNIT_BY_DATE. */
	public static final String GET_STATEMENT_BY_UNIT_BY_DATE = "SELECT {PK} FROM {AsahiSAMStatements} WHERE {statementPeriod}>=?fromDate AND {statementPeriod}<?toDate AND {custAccount} IN ({{SELECT {PK} FROM {AsahiB2BUnit} WHERE {payerAccount} = ({{SELECT {PK} FROM {AsahiB2BUnit} WHERE {uid} =?uid}})}})";

	/** The Constant GET_SAM_PAYMENT_RECORDS_QUERY. */
	public static final String GET_SAM_PAYMENT_RECORDS_QUERY = "SELECT {PK} FROM  {AsahiSAMPayment as asp join AsahiSAMInvoice as asi on {asp.pk}= {asi.sampayment}} where  {asp.custAccount} IN ({{SELECT {PK} FROM {AsahiB2BUnit} WHERE {payerAccount} = ({{SELECT {PK} FROM {AsahiB2BUnit} WHERE {uid} =?uid}})}})";

	/** The Constant GET_TOTAL_COUNT_SAM_PAYMENT_RECORDS_QUERY. */
	public static final String GET_TOTAL_COUNT_SAM_PAYMENT_RECORDS_QUERY = "SELECT COUNT(DISTINCT({PK})) FROM  {AsahiSAMPayment as asp join AsahiSAMInvoice as asi on {asp.pk}= {asi.sampayment}} where {asp.custAccount} IN ({{SELECT {PK} FROM {AsahiB2BUnit} WHERE {payerAccount} = ({{SELECT {PK} FROM {AsahiB2BUnit} WHERE {uid} =?uid}})}})";

	/** The Constant GET_ALL_DUE_NOW_INVOICES_BY_PAYER_UID. */
	public static final String GET_ALL_DUE_NOW_INVOICES_BY_PAYER_UID = "SELECT {PK} FROM {AsahiSAMInvoice as asi JOIN AsahiSAMInvoiceStatus as status on {asi.status}={status.pk} JOIN AsahiSAMDocumentType as docType on {asi.documentType}={docType.pk}} WHERE {status.code} =?statusCode AND {custAccount} IN ({{SELECT {PK} FROM {AsahiB2BUnit} WHERE {payerAccount} = ({{SELECT {PK} FROM {AsahiB2BUnit} WHERE {uid} =?uid}})}}) AND {docType.code} != '12' AND {invoiceDueDate} <= ?date ORDER BY {invoiceDueDate}";

	/** The Constant GET_INVOICE_BASED_ON_DATE. */
	public static final String GET_INVOICE_BASED_ON_DATE = "SELECT {PK} FROM {AsahiSAMInvoice} WHERE {creationtime}< ?currentDate  AND {creationtime}>= ?startdate";

	/** The Constant GET_INVOICE_PAYMENT_BASED_ON_DATE. */
	public static final String GET_INVOICE_PAYMENT_BASED_ON_DATE = "SELECT {PK} FROM {AsahiSAMPayment} WHERE {creationtime}< ?currentDate  AND {creationtime}>= ?startdate";

	/** The Constant GET_USER_DIRECT_DEBIT_BY_PAYER. */
	public static final String GET_USER_DIRECT_DEBIT_BY_PAYER = "SELECT {PK} FROM {AsahiSAMDirectDebit AS debit JOIN AsahiDirectDebitPmtTrans AS pmt on {pmt.pk} = {debit.paymentTransaction}} WHERE {debit.customer} = ?payer AND {pmt.requestToken} is not null";

	/** The Constant GET_ORDER_BASED_ON_DATE_AND_SITE. */
	public static final String GET_ORDER_BASED_ON_DATE_AND_SITE = "SELECT {PK} FROM {Order} WHERE {creationtime} < ?currentDate  AND {creationtime}>= ?startdate AND {site} = ({{SELECT {PK} FROM {BaseSite} WHERE {uid}= ?uid}})";

	/** The Constant GET_INVOICE_BY_DELIVERY_NUMBER. */
	public static final String GET_INVOICE_BY_DELIVERY_NUMBER = "SELECT {PK} FROM {AsahiSAMInvoice} WHERE {deliveryNumber} = ?deliveryNumber";

}
