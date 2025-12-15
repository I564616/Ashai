/**
 *
 */
package com.sabmiller.core.product;

import com.sabmiller.core.enums.SapServiceCallStatus;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.europe1.enums.UserPriceGroup;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class DefaultSabmPriceRowDao.
 *
 * @author joshua.a.antony
 */
public class DefaultSabmPriceRowDao implements SabmPriceRowDao
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSabmPriceRowDao.class);

	private static final String CUP_SAP_INPROGRESS_CHECK_ENABLED = "cup.sap.inprogress.check.enabled";
	/** The flexible search service. */
	private FlexibleSearchService flexibleSearchService;

	private ConfigurationService configurationService;

	private SearchRestrictionService searchRestrictionService;

	private UserService userService;

	/** The Constant PRICE_ROW_QUERY. */

			
	private static final String PRICE_ROW_QUERY = "SELECT {" + PriceRowModel.PK + "} " + "FROM {" + PriceRowModel._TYPECODE
			+ "} WHERE {" + PriceRowModel.UG + "}=?userPriceGroup AND {" + PriceRowModel.PRODUCTID + "}=?product ORDER BY {"
			+ PriceRowModel.MODIFIEDTIME + "} DESC";

	private static final String PRICE_ROW_DATE_QUERY = "SELECT {" + PriceRowModel.PK + "} " + "FROM {" + PriceRowModel._TYPECODE
			+ "} WHERE {" + PriceRowModel.UG + "}=?userPriceGroup AND {" + PriceRowModel.PRODUCTID + "}=?product AND ?date ={"
			+ PriceRowModel.STARTTIME + "}";
			
	/** The Constant PRICE_ROW_QUERY_BYUSERANDPRODUCT. */
	private static final String PRICE_ROW_QUERY_BYUSERANDPRODUCT = "SELECT {" + PriceRowModel.PK + "} " + "FROM {"
			+ PriceRowModel._TYPECODE + "} WHERE {" + PriceRowModel.USER + "}=?currentUser AND {" + PriceRowModel.PRODUCTID
			+ "}=?product AND {" + PriceRowModel.CURRENCY + "}=?currentCurrency AND {" + PriceRowModel.UNIT + "}=?unit ORDER BY {"
			+ PriceRowModel.MODIFIEDTIME + "} DESC";
			
	/** The Constant FIND_OLD_PRICE_ROW_QUERY. */
	private static final String FIND_OLD_PRICE_ROW_QUERY = "SELECT {" + PriceRowModel.PK + "} FROM {" + PriceRowModel._TYPECODE
			+ "} WHERE {" + PriceRowModel.STARTTIME + "} <= ?startBefore";

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.product.SabmPriceRowDao#getPriceRow(java.lang.String,
	 * com.sabmiller.core.model.SABMAlcoholVariantProductEANModel)
	 */
	@Override
	public PriceRowModel getPriceRow(final UserPriceGroup ug, final ProductModel productModel, final Date date)
	{
		return getPriceRow(ug,productModel.getCode(),date);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.product.SabmPriceRowDao#getPriceRow(java.lang.String,
	 * com.sabmiller.core.model.SABMAlcoholVariantProductEANModel)
	 */
	@Override
	public PriceRowModel getPriceRow(final UserPriceGroup ug, final String code, final Date date)
	{
		if(isCUPCallInProgress()){
			// if user not in b2bunit and sap status in progress return null
			return null;
		}
		LOG.debug("delivery date to get price row:" + date);
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("userPriceGroup", ug);
		params.put("product", code);
		FlexibleSearchQuery fsq;
		if (date != null)
		{
			params.put("date", date);

			fsq = new FlexibleSearchQuery(PRICE_ROW_DATE_QUERY, params);
		}
		else
		{
			fsq = new FlexibleSearchQuery(PRICE_ROW_QUERY, params);
		}
		final SearchResult<PriceRowModel> result = flexibleSearchService.search(fsq);

		return result.getCount() > 0 ? result.getResult().get(0) : null;

	}

	/**
	 *  return false if CUPCall in progress, else true.
	 * @return
	 */
	protected Boolean isCUPCallInProgress() {
		// This needs to be enabled for priceRowRestriction to be applied
		boolean isPriceRowRestrictionEnabled = configurationService.getConfiguration().getBoolean(CUP_SAP_INPROGRESS_CHECK_ENABLED, true);

		UserModel currentUser = userService.getCurrentUser();
		if (!isPriceRowRestrictionEnabled || !(currentUser instanceof B2BCustomerModel) || !getSearchRestrictionService().isSearchRestrictionsEnabled()) {
			return false;
		}

		final B2BUnitModel defaultB2bUnit = ((B2BCustomerModel) currentUser).getDefaultB2BUnit();
		if (defaultB2bUnit != null && SapServiceCallStatus.IN_PROGRESS.equals(defaultB2bUnit.getCupCallStatus())) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.product.SabmPriceRowDao#getPriceRow(de.hybris.platform.core.model.user.UserModel,
	 * de.hybris.platform.core.model.c2l.CurrencyModel, de.hybris.platform.core.model.product.ProductModel)
	 */
	@Override
	public PriceRowModel getPriceRowByProduct(final UserModel currentUser, final CurrencyModel currentCurrency,
			final ProductModel product)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		//find priceRow by currency user product and productUnit.
		params.put("currentCurrency", currentCurrency);
		params.put("currentUser", currentUser);
		params.put("product", product.getCode());
		params.put("unit", product.getUnit());

		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(PRICE_ROW_QUERY_BYUSERANDPRODUCT, params);
		final SearchResult<PriceRowModel> result = flexibleSearchService.search(fsq);
		if (result != null)
		{
			return result.getCount() > 0 ? result.getResult().get(0) : null;
		}
		return null;
	}

	/**
	 * Find old price rows while PriceRowModel.startTime < startBefore.
	 *
	 * @param startBefore
	 *           the started before
	 * @param batchSize
	 *           the batch size
	 * @return list of @PriceRowModel
	 */
	@Override
	public List<PriceRowModel> findOldPriceRow(final Date startBefore, final int batchSize)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("startBefore", startBefore);
		return doSearch(FIND_OLD_PRICE_ROW_QUERY, params, batchSize, PriceRowModel.class);
	}

	protected <T> List<T> doSearch(final String query, final Map<String, Object> params, final int batchSize,
			final Class<T> resultClass)
	{
		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(query);
		if (params != null)
		{
			fQuery.addQueryParameters(params);

			//will fetch all price row data if batch size is 0
			if (batchSize > 0)
			{
				LOG.debug("Fetch RepDrivenDealConditionStatus by batch, batch size is {}", batchSize);
				//set paging data
				fQuery.setNeedTotal(Boolean.TRUE);
				fQuery.setStart(0);
				fQuery.setCount(batchSize);
			}
		}

		fQuery.setResultClassList(Collections.singletonList(resultClass));

		final SearchResult<T> searchResult = flexibleSearchService.search(fQuery);
		return searchResult.getResult();
	}

	protected FlexibleSearchService getFlexibleSearchService() {
		return flexibleSearchService;
	}

	public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService) {
		this.flexibleSearchService = flexibleSearchService;
	}

	protected ConfigurationService getConfigurationService() {
		return configurationService;
	}

	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	protected UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	protected SearchRestrictionService getSearchRestrictionService() {
		return searchRestrictionService;
	}

	public void setSearchRestrictionService(SearchRestrictionService searchRestrictionService) {
		this.searchRestrictionService = searchRestrictionService;
	}
}
