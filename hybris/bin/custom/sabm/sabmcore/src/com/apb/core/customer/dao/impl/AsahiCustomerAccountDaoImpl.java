/*
 *
 */
package com.apb.core.customer.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.customer.dao.impl.DefaultCustomerAccountDao;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.paginated.PaginatedFlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.ticket.model.CsTicketModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.apb.core.customer.dao.AsahiCustomerAccountDao;
import com.apb.core.model.AsahiEmployeeModel;
import com.apb.core.model.KegReturnSizeModel;
import com.apb.core.model.ProdPricingTierModel;
import com.apb.core.util.AsahiAdhocCoreUtil;
import com.sabmiller.core.enums.AsahiEnquirySubType;
import com.sabmiller.core.enums.AsahiEnquiryType;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.AsahiCatalogProductMappingModel;
import com.sabmiller.core.model.EnquiryTypeContactMappingModel;
import com.sabmiller.core.model.PlanogramModel;

/**
 * The Class AsahiCustomerAccountDaoImpl.
 *
 * @author Kuldeep.Singh1
 */
public class AsahiCustomerAccountDaoImpl extends DefaultCustomerAccountDao implements AsahiCustomerAccountDao
{

	private static final String ASAHI_BUSINESSCODE = "asahi";

	/** The Constant FIND_ORDERS_BY_CUSTOMER_STORE_QUERY. */
	private static final String FIND_ORDERS_BY_CUSTOMER_STORE_QUERY = "SELECT {" + OrderModel.PK + "}, {"
			+ OrderModel.CREATIONTIME + "}, {" + OrderModel.CODE + "} FROM {" + OrderModel._TYPECODE + "} WHERE {" + OrderModel.UNIT
			+ "} = ?b2bunit AND {" + OrderModel.VERSIONID + "} IS NULL AND {" + OrderModel.STORE + "} = ?store";

	/** The Constant FIND_ORDERS_BY_CUSTOMER_STORE_QUERY_BY_COFODATE. */
	private static final String FIND_ORDERS_BY_CUSTOMER_STORE_QUERY_BY_COFODATE = "SELECT {" + OrderModel.PK + "}, {"
			+ OrderModel.CREATIONTIME + "}, {" + OrderModel.CODE + "} FROM {" + OrderModel._TYPECODE + "} WHERE {" + OrderModel.UNIT
			+ "} = ?b2bunit AND {" + OrderModel.VERSIONID + "} IS NULL AND {" + OrderModel.STORE + "} = ?store AND {"
			+ OrderModel.CREATIONTIME + "} >= ?cofoDate";
	/** The Constant SORT_ORDERS_BY_DATE. */
	private static final String SORT_ORDERS_BY_DATE = " ORDER BY {" + OrderModel.CREATIONTIME + "} DESC";

	/** The Constant SORT_ENQUIRIES_BY_DATE. */
	private static final String SORT_ENQUIRIES_BY_DATE = " ORDER BY {" + CsTicketModel.CREATIONTIME + "} DESC";

	/** The Constant SORT_ORDERS_BY_CODE. */
	private static final String SORT_ORDERS_BY_CODE = " ORDER BY {" + OrderModel.CODE + "},{" + OrderModel.CREATIONTIME + "} DESC";

	/** The Constant FIND_CUSTOMER_BY_ASAHI_B2B_UNIT_GROUP_QUERY. */
	private static final String FIND_CUSTOMER_BY_ASAHI_B2B_UNIT_GROUP = "SELECT {PK} FROM {B2BCUSTOMER AS ADMIN JOIN PRINCIPALGROUPRELATION AS PGR ON {PGR.SOURCE}={ADMIN.PK} "
			+ "JOIN USERGROUP AS GROUP ON {PGR.TARGET}={GROUP.PK}} WHERE {GROUP.UID} = ?userGroup AND {ADMIN.DEFAULTB2BUNIT}=?defaultUnit";
	private static final String FIND_ORDERS_BY_B2B_UNIT_STORE_QUERY = "SELECT {" + OrderModel.PK + "}, {"
			+ OrderModel.CREATIONTIME + "}, {" + OrderModel.CODE + "} FROM {" + OrderModel._TYPECODE + "} WHERE {" + OrderModel.CODE
			+ "} = ?code AND {" + OrderModel.VERSIONID + "} IS NULL AND {" + OrderModel.UNIT + "} = ?b2bUnit AND {"
			+ OrderModel.STORE + "} = ?store";

	private static final String FIND_ASAHI_SALES_REP_BY_ID = "SELECT {PK} FROM {ASAHIEMPLOYEE AS SALESEMP} WHERE {SALESEMP.purposeCode} = ?purposeCode";

	/** The Constant KEG_RETURN_SIZES_BY_SITE_ID QUERY */
	private static final String KEG_RETURN_SIZES_BY_SITE_ID = "SELECT {" + KegReturnSizeModel.PK + "} FROM {"
			+ KegReturnSizeModel._TYPECODE + "} WHERE {" + KegReturnSizeModel.SITE + "} = ?site AND {" + KegReturnSizeModel.ACTIVE
			+ "} =?active ORDER BY {" + KegReturnSizeModel.CODE + "} ASC";


	/** The Constant KEG_RETURN_SIZES_BY_SITE_ID QUERY */
	final String EMAIL_ADDRESS_BY_EMAIL_DISPLAYNAME = "SELECT {" + EmailAddressModel.PK + "} FROM {" + EmailAddressModel._TYPECODE
			+ "} WHERE {" + EmailAddressModel.DISPLAYNAME + "} = ?displayName AND {" + EmailAddressModel.EMAILADDRESS
			+ "}=?emailAddress";

	/** The Constant TITLE_LIST_ QUERY */
	final String TITLE_LIST_ = "SELECT {" + TitleModel.PK + "} FROM {" + TitleModel._TYPECODE + "} ORDER BY {" + TitleModel.CODE
			+ "} ASC";

	/** The Constant TITLE_LIST_ QUERY */
	final String TITLE_LIST_SITE = "SELECT {" + TitleModel.PK + "} FROM {" + TitleModel._TYPECODE + "} where {"+ TitleModel.BUSINESSCODE +"} =?businessCode ORDER BY {" + TitleModel.CODE
			+ "} ASC";

	/** The Constant FIND_PRODUCT_PRICINGTIER_BY_CODE_QUERY */
	private static final String FIND_PRODUCT_PRICINGTIER_BY_CODE = "Select {pk} from {ProdPricingTier} where {pricTierCode}=?tierCode";

	/** The Constant FIND_ENQUIRIES_BY_B2BUNIT_QUERY. */
	private static final String FIND_ENQUIRIES_BY_B2BUNIT_QUERY = "SELECT {" + CsTicketModel.PK + "} FROM {" + CsTicketModel._TYPECODE + "} WHERE {" + CsTicketModel.B2BUNIT
			+ "} = ?b2bunit ";
	private static final String FIND_ENQUIRIES_BY_B2BUNIT_QUERY_BY_COFODATE = "SELECT {" + CsTicketModel.PK + "} FROM {"
			+ CsTicketModel._TYPECODE + "} WHERE {" + CsTicketModel.B2BUNIT + "} = ?b2bunit " + " AND {" + CsTicketModel.CREATIONTIME
			+ "} >= ?cofoDate";
	/** The Constant FIND_CONTACT_BY_ENQUIRYTYPES_QUERY */
	private static final String FIND_CONTACT_BY_ENQUIRYTYPES_QUERY = "SELECT {" + EnquiryTypeContactMappingModel.PK + "} FROM {" + EnquiryTypeContactMappingModel._TYPECODE + "} WHERE {" + EnquiryTypeContactMappingModel.ENQUIRYTYPE + "} = ?enquiryType AND {" + EnquiryTypeContactMappingModel.ENQUIRYSUBTYPE + "} = ?enquirySubType";
	private static final String FILTER_START_END_DATE = " AND {" + OrderModel.CREATIONTIME + "} >= ?startDate AND {" +  OrderModel.CREATIONTIME + "} <= ?endDate";

	private static final String FILTER_ENQUIRY_START_END_DATE = " AND {" + CsTicketModel.CREATIONTIME + "} >= ?startDate AND {" +  CsTicketModel.CREATIONTIME + "} <= ?endDate";

	private static final String FIND_USER_BY_UID = "SELECT {PK} FROM {"+UserModel._TYPECODE+" AS USER} WHERE {USER."+UserModel.UID+"} = ?uid";
	/** The Constant ASAHI_USER_TIMEOFFSET_COOKIE. */
   private static final String ASAHI_USER_TIMEOFFSET_COOKIE = "asahiUserTimeOffsetCookie";

	private static final String GET_CATALOG_HIERARCHY_INFO = "SELECT {" + AsahiCatalogProductMappingModel.PK + "} FROM {"
			+ AsahiCatalogProductMappingModel._TYPECODE + "} WHERE {" + AsahiCatalogProductMappingModel.CATALOGID
			+ "} IN (?catalogIds)";

	/** The Constant FIND_PLANOGRAM_BY_CODE */
	private static final String FIND_PLANOGRAM_BY_CODE = "SELECT {" + PlanogramModel.PK + "} FROM {" + PlanogramModel._TYPECODE
			+ "} WHERE {" + PlanogramModel.CODE + "} =?code";


   private static final Logger LOG = LoggerFactory.getLogger(AsahiCustomerAccountDaoImpl.class);

	@Resource
	private PaginatedFlexibleSearchService paginatedFlexibleSearchService;

	/** The session service. */
	@Resource
	private SessionService sessionService;

	@Autowired
   private AsahiAdhocCoreUtil adhocCoreUtil;


	@Resource(name="enumerationService")
	private EnumerationService enumerationService;
	/** The search restriction service. */
	@Resource(name = "searchRestrictionService")
	private SearchRestrictionService searchRestrictionService;

	/**
	 * Find orders by customer and store.
	 *
	 * @param customerModel
	 *           the customer model
	 * @param store
	 *           the store
	 * @param pageableData
	 *           the pageable data
	 * @return the search page data
	 * @throws ParseException
	 */

	@Override
	public SearchPageData<OrderModel> findOrdersByCustomerAndStore(final CustomerModel customerModel, final BaseStoreModel store,
			final PageableData pageableData, final String cofoDate) throws ParseException
	{
		B2BUnitModel b2bUnitModel = null;
		if (customerModel instanceof B2BCustomerModel)
		{
			final B2BCustomerModel b2bCustomerModel = (B2BCustomerModel) customerModel;
			b2bUnitModel = b2bCustomerModel.getDefaultB2BUnit();

		}
		final Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("b2bunit", b2bUnitModel);

		queryParams.put("store", store);

		final StringBuilder filterClause = new StringBuilder();
		final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

		if(StringUtils.isNotBlank(pageableData.getEndDate()) && StringUtils.isNotBlank(pageableData.getStartDate()))
		{
			final String timeZone = sessionService.getAttribute(ASAHI_USER_TIMEOFFSET_COOKIE);
			final DateTime dateTime = adhocCoreUtil.getUserDateTime(timeZone);
			dateFormat.setTimeZone(dateTime.getZone().toTimeZone());
			filterClause.append(FILTER_START_END_DATE);
			final Date endDate = DateUtils.addDays(dateFormat.parse(pageableData.getEndDate()), 1);
			final Date startDate = dateFormat.parse(pageableData.getStartDate());
			LOG.error("endDate:"+endDate);
			LOG.error("startDate:"+startDate);
			queryParams.put("endDate",endDate);
			queryParams.put("startDate",startDate);
		}
		if (StringUtils.isNotBlank(cofoDate))
		{
			queryParams.put("cofoDate", dateFormat.parse(cofoDate.replaceAll("-", "/")));
		}
		final List<de.hybris.platform.commerceservices.search.flexiblesearch.data.SortQueryData> sortQueries = Arrays.asList(
				createSortQueryData("byDate",
						createQuery(cofoDate != null ? FIND_ORDERS_BY_CUSTOMER_STORE_QUERY_BY_COFODATE
								: FIND_ORDERS_BY_CUSTOMER_STORE_QUERY, filterClause.toString(), SORT_ORDERS_BY_DATE)),
				createSortQueryData("byOrderNumber", createQuery(cofoDate != null ? FIND_ORDERS_BY_CUSTOMER_STORE_QUERY_BY_COFODATE
						: FIND_ORDERS_BY_CUSTOMER_STORE_QUERY, filterClause.toString(), SORT_ORDERS_BY_CODE)));

		return getPagedFlexibleSearchService().search(sortQueries, "byDate", queryParams, pageableData);
	}



	@Override
	public List<B2BCustomerModel> findB2BCustomerByGroup(final AsahiB2BUnitModel unit, final String userGroupId)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("userGroupId", userGroupId);
		params.put("defaultUnit", unit.getPk());

		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_CUSTOMER_BY_ASAHI_B2B_UNIT_GROUP);
		query.addQueryParameters(params);
		final SearchResult<B2BCustomerModel> result = getFlexibleSearchService().search(query);
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult();
		}
		return null;
	}

	@Override
	public OrderModel findOrderByB2BUnitAndCodeAndStore(final AsahiB2BUnitModel b2bUnitModel, final String code,
			final BaseStoreModel store)
	{
		validateParameterNotNull(b2bUnitModel, "b2b must not be null");
		validateParameterNotNull(code, "Code must not be null");
		validateParameterNotNull(store, "Store must not be null");
		final Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("b2bUnit", b2bUnitModel);
		queryParams.put("code", code);
		queryParams.put("store", store);
		final OrderModel result = getFlexibleSearchService().searchUnique(
				new FlexibleSearchQuery(FIND_ORDERS_BY_B2B_UNIT_STORE_QUERY, queryParams));
		return result;
	}

	@Override
	public AsahiEmployeeModel findAsahiSalesRepByPurposeAndRepCode(final String salesRepCode, final String purposeCode)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put("purposeCode", purposeCode);
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_ASAHI_SALES_REP_BY_ID);
		query.addQueryParameters(params);
		final SearchResult<AsahiEmployeeModel> result = getFlexibleSearchService().search(query);
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0);
		}
		return null;
	}

	/**
	 * Get all Keg Size based on Site
	 */
	@Override
	public List<KegReturnSizeModel> getKegSizes(final CMSSiteModel currentSite)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("site", currentSite.getPk());
		params.put("active", true);
		final FlexibleSearchQuery query = new FlexibleSearchQuery(KEG_RETURN_SIZES_BY_SITE_ID);
		query.addQueryParameters(params);
		final SearchResult<KegReturnSizeModel> result = getFlexibleSearchService().search(query);
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult();
		}
		return null;
	}


	/**
	 * @param displayName
	 * @param emailAddress
	 * @return
	 */
	public EmailAddressModel getEmailAddressModel(final String displayName, final String emailAddress)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("displayName", displayName);
		params.put("emailAddress", emailAddress);
		final FlexibleSearchQuery query = new FlexibleSearchQuery(EMAIL_ADDRESS_BY_EMAIL_DISPLAYNAME);
		query.addQueryParameters(params);
		final SearchResult<EmailAddressModel> result = getFlexibleSearchService().search(query);
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0);
		}
		return null;
	}

	/**
	 * @return titles
	 */
	@Override
	public Collection<TitleModel> getAllTitles()
	{

		final Map<String, Object> params = new HashMap<>();
		params.put("businessCode", ASAHI_BUSINESSCODE);
		final FlexibleSearchQuery query = new FlexibleSearchQuery(TITLE_LIST_SITE.toString());
		query.addQueryParameters(params);
		final SearchResult<TitleModel> result = getFlexibleSearchService().search(query);
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult();
		}
		return null;
	}


	@Override
	public AsahiEmployeeModel findAsahiSalesRepById(final String salesRepCode)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("purposeCode", salesRepCode);
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_ASAHI_SALES_REP_BY_ID);
		query.addQueryParameters(params);
		final SearchResult<AsahiEmployeeModel> result = getFlexibleSearchService().search(query);
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0);
		}
		return null;
	}

	@Override
	public ProdPricingTierModel findProdPricingTierByCode(final String tierCode)
	{

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("tierCode", tierCode);
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_PRODUCT_PRICINGTIER_BY_CODE);
		query.addQueryParameters(params);

		final SearchResult<ProdPricingTierModel> result = getFlexibleSearchService().search(query);
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0);
		}
		return null;
	}

	@Override
	public SearchPageData<CsTicketModel> getAllEnquiries(final AsahiB2BUnitModel b2bunit, final PageableData pageableData, final String cofoDate) throws ParseException
	{
		final Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("b2bunit", b2bunit);


		final StringBuilder filterClause = new StringBuilder();
		final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
		if(StringUtils.isNotBlank(pageableData.getEndDate()) && StringUtils.isNotBlank(pageableData.getStartDate()))
		{
			final String timeZone = sessionService.getAttribute(ASAHI_USER_TIMEOFFSET_COOKIE);
			final DateTime dateTime = adhocCoreUtil.getUserDateTime(timeZone);
			dateFormat.setTimeZone(dateTime.getZone().toTimeZone());
			filterClause.append(FILTER_ENQUIRY_START_END_DATE);
			final Date endDate = DateUtils.addDays(dateFormat.parse(pageableData.getEndDate()), 1);
			final Date startDate = dateFormat.parse(pageableData.getStartDate());
			LOG.error("endDate:"+endDate);
			LOG.error("startDate:"+startDate);
			queryParams.put("endDate",endDate);
			queryParams.put("startDate",startDate);
		}
		if (StringUtils.isNotBlank(cofoDate))
		{
			queryParams.put("cofoDate", dateFormat.parse(cofoDate.replaceAll("-", "/")));
		}
		final List<de.hybris.platform.commerceservices.search.flexiblesearch.data.SortQueryData> sortQueries = Arrays.asList(
				createSortQueryData("byDate",
						createQuery(cofoDate != null ? FIND_ENQUIRIES_BY_B2BUNIT_QUERY_BY_COFODATE : FIND_ENQUIRIES_BY_B2BUNIT_QUERY,
								filterClause.toString(), SORT_ENQUIRIES_BY_DATE)));

		return getPagedFlexibleSearchService().search(sortQueries, "byDate", queryParams, pageableData);
	}


	  @Override
	  public EnquiryTypeContactMappingModel getContactByEnquiryType(final
	  AsahiEnquiryType enquiryType, final AsahiEnquirySubType enquirySubType) {

	  final Map<String, Object> params = new HashMap<String, Object>();
	  params.put("enquiryType", enquiryType);
	  if(null != enquirySubType)
	{
		params.put("enquirySubType", enquirySubType);
	}
	else
		  {
			  final AsahiEnquirySubType otherSubType =  enumerationService.getEnumerationValue(AsahiEnquirySubType.class, "OTHER");
			  params.put("enquirySubType", otherSubType);
		  }
	  final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_CONTACT_BY_ENQUIRYTYPES_QUERY);
	  query.addQueryParameters(params);

	  final SearchResult<EnquiryTypeContactMappingModel> result =
	  getFlexibleSearchService().search(query); if
	  (CollectionUtils.isNotEmpty(result.getResult())) { return
	  result.getResult().get(0); } return null; }

	public void setEnumerationService(final EnumerationService enumerationService) {
		this.enumerationService = enumerationService;
	}

	public UserModel getUserByUid(final String userId) {
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", userId);
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_USER_BY_UID);
		query.addQueryParameters(params);
		this.searchRestrictionService.disableSearchRestrictions();
		final SearchResult<UserModel> result = getFlexibleSearchService().search(query);
		this.searchRestrictionService.enableSearchRestrictions();
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0);
		}
		return null;
	}

	@Override
	public List<AsahiCatalogProductMappingModel> findCatalogHierarchyData(final List<String> catalogIds)
	{

		if (CollectionUtils.isNotEmpty(catalogIds))
		{
			final Map<String, Object> params = new HashMap<>();
			final StringBuilder builder = new StringBuilder(GET_CATALOG_HIERARCHY_INFO);
			params.put("catalogIds", catalogIds);

			final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
			query.addQueryParameters(params);

			final SearchResult<AsahiCatalogProductMappingModel> result = flexibleSearchService.search(query);
			return CollectionUtils.isNotEmpty(result.getResult()) ? result.getResult() : Collections.emptyList();
		}

		return Collections.emptyList();
	}



	@Override
	public PlanogramModel fetchPlanogramByCode(final String code)
	{
		final Map<String, Object> params = new HashMap<>();
		final StringBuilder builder = new StringBuilder(FIND_PLANOGRAM_BY_CODE);
		params.put("code", code);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);

		final SearchResult<PlanogramModel> result = flexibleSearchService.search(query);
		return CollectionUtils.isNotEmpty(result.getResult()) ? result.getResult().get(0) : null;
	}

}
