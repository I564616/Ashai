/**
 *
 */
package com.sabmiller.core.order.dao;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.daos.OrderDao;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.store.BaseStoreModel;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;


/**
 * @author joshua.a.antony
 */
public interface SabmOrderDao extends OrderDao {
    OrderModel getOrderBySapSalesOrderNumber(String sapSalesOrderNumber);

    List<OrderModel> getOrderByB2BUnit(B2BUnitModel b2bUnitModel);

    List<OrderModel> getTopOrder(int limit);

    List<OrderModel> getB2BUnitOrdersByDeliveryDate(B2BUnitModel parentUnit, Date requestedDeliveryDate);

    List<OrderModel> getOrdersByOrderStatus(OrderStatus orderStatus);

    List<OrderModel> getOrderByB2BUnit(B2BUnitModel parentUnit, Date dateFrom, Date dateTo);

    OrderModel getOrderByConsignment(String trackingId);


    /**
     * @param b2bUnitModel
     * @param page
     * @param dateTo
     * @return
     */
    List<OrderModel> getNextPagedOrdersByB2BUnit(B2BUnitModel b2bUnitModel, int page, Date dateTo);

    /**
     * @param b2bUnitModel
     * @param page
     * @param dateTo
     * @return
     */
    List<OrderModel> getPreviousPagedOrdersByB2BUnit(B2BUnitModel b2bUnitModel, int page, Date dateTo);

    /**
     * @param b2bUnitModel
     * @param page
     * @param orderBy
     * @return
     */
    List<OrderModel> getTotalPagedOrdersByB2BUnit(B2BUnitModel b2bUnitModel, int page, String orderBy);

    /**
     * Fetches the number of times the provided Product EAN has been ordered on Hybris by the given sub channel. This
     * method takes all orders into consideration (phone/online).
     *
     * @param subChannel    the b2b unit's sub channel (Pub, Bar, Restaurant) to use when doing the count.
     * @param productEAN    the product EAN to evaluate when getting the count.
     * @param fromOrderDate the from date to consider the orders from.
     * @return order entries linked to the product EAN has been ordered on Hybris with the given criteria.
     */
    Collection<OrderEntryModel> getProductOrderCountBySubChannelAndEAN(final String subChannel,
                                                                       final SABMAlcoholVariantProductEANModel productEAN, final Date fromOrderDate);

    /**
     * @param customerId
     * @return
     */
    OrderModel getLastOrderByCustomer(UserModel user);

    /**
     * @param user
     * @return
     */
    OrderModel getFirstOnlineOrderByCustomer(UserModel user);


    /**
     * @param b2bUnit
     * @param consignmentStatus
     * @return
     */
    List<ConsignmentModel> getB2BUnitOrdersTimePassesETA( ConsignmentStatus consignmentStatus);


    /**
     * Gets all orders which were "Paid" or "Partly Paid" using "Credit Card" for customers on "P1" or "P2" AutoPay membership status
     *
     * @return List<OrderModel>
     */
    List<OrderModel> getOrdersByCreditCardPayment();

    /**
     * Retrieves all orders whose create date is less than or equal than specified date
     * */
    List<OrderModel> getOrdersToDate(final Date endDate, final int limit);
    
    OrderModel getOrderByCartCode(final String cartCode);
    
    List<OrderModel> findOrdersByOrderStatus(String status, CMSSiteModel cmsSiteModel);
    
    OrderModel getOrderForCode(String code);
    
    OrderEntryModel getOrderEntryByBackendUid(String backendUid);
    
    BaseSiteModel getBaseSiteByUid(String siteUid);
    
    BaseStoreModel getBaseStoreByUid(String storeUid);
    
    List<OrderModel> getOrderEntriesForUser(final AsahiB2BUnitModel unit);
    
    List<OrderModel> getOrdersBasedOnDateAndSite(
 			String siteId, Date previousYear, Date currentDate);

	/**
	 * @param user
	 * @param siteUid
	 * @return
	 */
	int fetchOnlineOrderCountBasedOnUserB2BUnitAndSite(UserModel user, String siteUid, final AsahiB2BUnitModel currentUnit);
}
