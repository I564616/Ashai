package com.apb.dao.b2bunit.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;

import com.apb.core.constants.ApbQueryConstant;
import com.apb.core.model.AccountGroupsModel;
import com.apb.core.model.AccountTypeModel;
import com.apb.core.model.BannerGroupsModel;
import com.apb.core.model.ChannelModel;
import com.apb.core.model.LicenceClassModel;
import com.apb.core.model.LicenseTypesModel;
import com.apb.core.model.SubChannelModel;
import com.apb.dao.b2bunit.ApbB2BUnitDao;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.AsahiSAMAccessModel;


/**
 * The Interface ApbB2BUnitDaoImpl.v
 *
 * Kuldeep.Singh1
 */
public class ApbB2BUnitDaoImpl extends AbstractItemDao implements ApbB2BUnitDao
{
	public static final String APBB2BUNIT_ABNNUMBER = "abnNumber";
	public static final String REFERENCE_CODE = "code";
	public static final String ADDRESS_RECORD_ID = "addressRecordid";
	public static final String ADDRESS_OWNER = "owner";
	public static final String ISO_CODE = "isocode";

	/** The search restriction service. */
	@Resource(name = "searchRestrictionService")
	private SearchRestrictionService searchRestrictionService;

	@Resource
	private TypeService typeService;
	@Resource
	private CMSSiteService cmsSiteService;

	/**
	 * Gets the apb B 2 B unit by abn.
	 *
	 * @param abnNumber
	 *           the abn number
	 * @return the apb B 2 B unit by abn
	 */
	@Override
	public AsahiB2BUnitModel getApbB2BUnitByAbn(final String abnNumber)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_APB_B2B_UNIT_BY_ABN_NUMBER);
		params.put(APBB2BUNIT_ABNNUMBER, abnNumber);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);

		this.searchRestrictionService.disableSearchRestrictions();
		final SearchResult<AsahiB2BUnitModel> result = getFlexibleSearchService().search(query);
		this.searchRestrictionService.enableSearchRestrictions();
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0);
		}
		return null;
	}

	/**
	 * Gets the license types for code.
	 *
	 * @param code
	 *           the code
	 * @return the license types for code
	 */
	@Override
	public LicenseTypesModel getLicenseTypesForCode(final String code)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_LICENSE_TYPES_FOR_CODE);
		params.put(REFERENCE_CODE, code);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);

		final SearchResult<LicenseTypesModel> result = getFlexibleSearchService().search(query);
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0);
		}
		return null;
	}

	/**
	 * Gets the sub channel for code.
	 *
	 * @param code
	 *           the code
	 * @return the sub channel for code
	 */
	@Override
	public SubChannelModel getSubChannelForCode(final String code)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_SUB_CHANNEL_FOR_CODE);
		params.put(REFERENCE_CODE, code);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);

		final SearchResult<SubChannelModel> result = getFlexibleSearchService().search(query);
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0);
		}
		return null;
	}

	/**
	 * Gets the licence class for code.
	 *
	 * @param code
	 *           the code
	 * @return the licence class for code
	 */
	@Override
	public LicenceClassModel getLicenceClassForCode(final String code)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_LICENSE_CLASS_FOR_CODE);
		params.put(REFERENCE_CODE, code);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);

		final SearchResult<LicenceClassModel> result = getFlexibleSearchService().search(query);
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0);
		}
		return null;
	}

	/**
	 * Gets the channel for code.
	 *
	 * @param code
	 *           the code
	 * @return the channel for code
	 */
	@Override
	public ChannelModel getChannelForCode(final String code)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_CHANNEL_FOR_CODE);
		params.put(REFERENCE_CODE, code);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);

		final SearchResult<ChannelModel> result = getFlexibleSearchService().search(query);
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0);
		}
		return null;
	}

	/**
	 * Gets the banner groups for code.
	 *
	 * @param code
	 *           the code
	 * @return the banner groups for code
	 */
	@Override
	public BannerGroupsModel getBannerGroupsForCode(final String code)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_BANNER_GROUPS_FOR_CODE);
		params.put(REFERENCE_CODE, code);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);

		final SearchResult<BannerGroupsModel> result = getFlexibleSearchService().search(query);
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0);
		}
		return null;
	}

	/**
	 * Gets the account type for code.
	 *
	 * @param code
	 *           the code
	 * @return the account type for code
	 */
	@Override
	public AccountTypeModel getAccountTypeForCode(final String code)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_ACCOUNT_TYPE_FOR_CODE);
		params.put(REFERENCE_CODE, code);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);

		final SearchResult<AccountTypeModel> result = getFlexibleSearchService().search(query);
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0);
		}
		return null;
	}

	/**
	 * Gets the account groups for code.
	 *
	 * @param code
	 *           the code
	 * @return the account groups for code
	 */
	@Override
	public AccountGroupsModel getAccountGroupsForCode(final String code)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_ACCOUNT_GROUP_FOR_CODE);
		params.put(REFERENCE_CODE, code);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);

		final SearchResult<AccountGroupsModel> result = getFlexibleSearchService().search(query);
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0);
		}
		return null;
	}

	/**
	 * Gets the apb B 2 B unit.
	 *
	 * @param abpAccountNo
	 *           the abp account no
	 * @param abnNumber
	 *           the abn number
	 * @return the apb B 2 B unit
	 */
	@Override
	public AsahiB2BUnitModel getApbB2BUnit(final String abpAccountNo, final String abnNumber)
	{
		this.searchRestrictionService.disableSearchRestrictions();
		validateParameterNotNull(abpAccountNo, "Apb Account No must not be null!");
		final StringBuffer query = new StringBuffer();
		query.append("SELECT {" + AsahiB2BUnitModel.PK + "}");
		query.append(" FROM {" + AsahiB2BUnitModel._TYPECODE + "}");
		query.append(" WHERE {" + AsahiB2BUnitModel.ACCOUNTNUM + "}=?accountNum");
		if (null != abnNumber)
		{
			query.append(" AND {" + AsahiB2BUnitModel.ABNNUMBER + "}=?abnNumber");
		}

		query.append(" AND {" + AsahiB2BUnitModel.ACTIVE + "}=1");
		//query.append(" AND {" + AsahiB2BUnitModel.COMPANYUID + "}=?" + AsahiB2BUnitModel.COMPANYUID);

		final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query.toString());

		searchQuery.addQueryParameter(AsahiB2BUnitModel.ACCOUNTNUM, abpAccountNo);

		if (null != abnNumber)
		{
			searchQuery.addQueryParameter(AsahiB2BUnitModel.ABNNUMBER, abnNumber);
		}
		//searchQuery.addQueryParameter(AsahiB2BUnitModel.COMPANYUID, cmsSiteService.getCurrentSite().getUid());
		final SearchResult<AsahiB2BUnitModel> result = getFlexibleSearchService().search(searchQuery);
		final int resultCount = result.getTotalCount();
		this.searchRestrictionService.enableSearchRestrictions();
		if (resultCount == 0)
		{
			throw new UnknownIdentifierException(
					"Asahi B2B Unit with abp Account Number '" + abpAccountNo + " & ABN Number " + abnNumber + "' not found!");
		}
		return result.getResult().get(0);
	}

	/**
	 * @param liquorLicenseNo
	 * @return
	 */
	public AsahiB2BUnitModel findLiquorLicense(final String liquorLicenseNo)
	{
		validateParameterNotNull(liquorLicenseNo, "Liquor License Number must not be null!");
		final StringBuffer query = new StringBuffer();
		query.append("SELECT {" + AsahiB2BUnitModel.PK + "}");
		query.append(" FROM {" + AsahiB2BUnitModel._TYPECODE + "}");
		query.append(" WHERE {" + AsahiB2BUnitModel.LIQUORLICENSENUMBER + "}=?liquorLicensenumber");

		final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query.toString());
		searchQuery.addQueryParameter(AsahiB2BUnitModel.LIQUORLICENSENUMBER, liquorLicenseNo);

		final SearchResult<AsahiB2BUnitModel> result = getFlexibleSearchService().search(searchQuery);
		final int resultCount = result.getTotalCount();
		if (resultCount == 0)
		{
			throw new ModelNotFoundException("Not Found Liquor License Number " + liquorLicenseNo);
		}
		return result.getResult().get(0);
	}

	/**
	 * Gets the warehouse for code.
	 *
	 * @param code
	 *           the code
	 * @return the warehouse for code
	 */
	public WarehouseModel getwarehouseForCode(final String code)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_WAREHOUSE_FOR_CODE);
		params.put(REFERENCE_CODE, code);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);

		final SearchResult<WarehouseModel> result = getFlexibleSearchService().search(query);
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0);
		}
		return null;
	}

	/**
	 * Gets the address for address record ID.
	 *
	 * @param id
	 *           the id
	 * @return the address for address record ID
	 */
	@Override
	public AddressModel getAddressForAddressRecordID(final String addressRecordid, final B2BUnitModel b2bUnit)
	{
		final Map<String, Object> params = new HashMap<>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_ADDRESS_FOR_ADDRESS_RECORD_ID);
		params.put(ADDRESS_RECORD_ID, addressRecordid);
		params.put(ADDRESS_OWNER, b2bUnit);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);

		final SearchResult<AddressModel> result = getFlexibleSearchService().search(query);
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0);
		}
		return null;
	}

	/**
	 * Gets the b 2 B unit by backend ID.
	 *
	 * @param customerRecId
	 *           the customer rec id
	 * @return the b 2 B unit by backend ID
	 */
	@Override
	public B2BUnitModel getB2BUnitByBackendID(final String customerRecId)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_APB_B2B_UNIT_BY_BACKEND_RECORD_ID);
		params.put("backendRecordID", customerRecId);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);
		this.searchRestrictionService.disableSearchRestrictions();
		final SearchResult<B2BUnitModel> result = getFlexibleSearchService().search(query);
		this.searchRestrictionService.enableSearchRestrictions();
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0);
		}
		return null;
	}

	/**
	 * Gets the b 2 B unit by account number.
	 *
	 * @param accountNumber
	 *           the account number
	 * @return the b 2 B unit by account number
	 */
	@Override
	public AsahiB2BUnitModel getB2BUnitByAccountNumber(final String accountNumber)
	{
		final Map<String, Object> params = new HashMap<>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_APB_B2B_UNIT_BY_ACCOUNT_NUMBER);
		params.put(AsahiB2BUnitModel.ACCOUNTNUM, accountNumber);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);
		this.searchRestrictionService.disableSearchRestrictions();
		final SearchResult<AsahiB2BUnitModel> result = getFlexibleSearchService().search(query);
		this.searchRestrictionService.enableSearchRestrictions();
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0);
		}
		return null;
	}

	/**
	 * Gets the b 2 B unit by UID.
	 *
	 * @param accountNumber
	 *           the account number
	 * @return the b 2 B unit by account number
	 */
	@Override
	public AsahiB2BUnitModel getB2BUnitByUID(final String uid)
	{
		final Map<String, Object> params = new HashMap<>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_APB_B2B_UNIT_BY_UID);
		params.put(AsahiB2BUnitModel.UID, uid);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);
		this.searchRestrictionService.disableSearchRestrictions();
		final SearchResult<AsahiB2BUnitModel> result = getFlexibleSearchService().search(query);
		this.searchRestrictionService.enableSearchRestrictions();
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0);
		}
		return null;
	}

	/**
	 * Gets the address by record id.
	 *
	 * @param addressId
	 *           the address id
	 * @return the address by record id
	 */
	@Override
	public AddressModel getAddressByRecordId(final String addressId)
	{
		final Map<String, Object> params = new HashMap<>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_ADDRESS_BY_ADDRESS_ID);
		params.put(AddressModel.ADDRESSRECORDID, addressId);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);
		final SearchResult<AddressModel> result = getFlexibleSearchService().search(query);
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0);
		}
		return null;
	}

	@Override
	public AsahiSAMAccessModel getAccessModel(final B2BCustomerModel customer, final AsahiB2BUnitModel b2bUnit) {
		final Map<String, Object> params = new HashMap<>();
		final StringBuilder builder = new StringBuilder("Select {pk} from {" + AsahiSAMAccessModel._TYPECODE +  "} Where {" + AsahiSAMAccessModel.B2BCUSTOMER + "}=?customer AND {" + AsahiSAMAccessModel.PAYER + "}=?b2bUnit");
		params.put("customer", customer);
		params.put("b2bUnit", b2bUnit);
		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);
		final SearchResult<AsahiSAMAccessModel> result = getFlexibleSearchService().search(query);
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0);
		}
		return null;
	}
}
