package com.apb.core.services;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.store.BaseStoreModel;

import java.util.Date;
import java.util.List;

import com.sabmiller.core.model.AsahiB2BUnitModel;


/**
 * The Interface AsahiOrderService.
 *
 * @author Kuldeep.Singh1
 */
public interface AsahiOrderService extends OrderService
{

	/**
	 * Gets the order list.
	 *
	 * @param status
	 *           the status
	 * @param cmsSiteModel
	 * @return the order list
	 */
	List<OrderModel> getOrderList(String status, CMSSiteModel cmsSiteModel);

	/**
	 * Gets the order for code.
	 *
	 * @param code
	 *           the code
	 * @return the order for code
	 */
	OrderModel getOrderForCode(String code);

	/**
	 * Gets the order entry by backend uid.
	 *
	 * @param backendUid
	 *           the backend uid
	 * @return the order entry by backend uid
	 */
	OrderEntryModel getOrderEntryByBackendUid(String backendUid);

	String getOrderMapping(String backendStatusCode);

	String getDisplayOrderStatus(String statusCode,String companyCode);

	/**
	 * Gets the base site by uid.
	 *
	 * @param siteUid
	 *           the site uid
	 * @return the base site by uid
	 */
	BaseSiteModel getBaseSiteByUid(String siteUid);

	/**
	 * Gets the base store by uid.
	 *
	 * @param storeUid
	 *           the store uid
	 * @return the base store by uid
	 */
	BaseStoreModel getBaseStoreByUid(String storeUid);

	/**
	 * Gets the order entries for user.
	 *
	 * @param b2bUnitModel the b 2 b unit model
	 * @return the order entries for user
	 */
	List<OrderModel> getOrderEntriesForUser(final AsahiB2BUnitModel b2bUnitModel);

	/**
	 * Gets the orders based on date and site.
	 *
	 * @param siteId the site id
	 * @param previousYear the previous year
	 * @param currentDate the current date
	 * 
	 */
	void removeOrdersBasedOnDateAndSite(
			String siteId, Date previousYear, Date currentDate);
	
	String exportOrderCSV(List<OrderHistoryData> orders);


}
