/**
 *
 */
package com.sabmiller.core.order;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BOrderService;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.facades.smartOrders.json.SmartOrdersJson;


/**
 * @author joshua.a.antony
 *
 */
public interface SabmB2BOrderService extends B2BOrderService
{
	public OrderModel getOrderBySapSalesOrderNumber(String paramString);

	public ConsignmentModel lookupConsignment(OrderModel orderModel, ConsignmentStatus consignmentStatus);

	public ConsignmentModel lookupConsignment(OrderModel orderModel, String deliveryNumber);

	public List<OrderModel> getOrderByB2BUnit(B2BUnitModel b2bUnitModel);

	/**
	 * Returns the top history with specified limit, sorted by timestamp
	 * @param limit
	 * @return
	 */
	List<OrderModel> getTopOrder(final int limit);

	public AbstractOrderEntryModel lookupOrderEntry(final OrderModel orderModel, final String material, final String lineNumber);

	List<OrderModel> getOrderByB2BUnit(B2BUnitModel parentUnit, Date dateFrom, Date dateTo);

	List<OrderModel> getB2BUnitOrdersByDeliveryDate(B2BUnitModel parentUnit, Date requestedDeliveryDate);

	List<OrderModel> getOrdersByOrderStatus(OrderStatus orderStatus);

	public OrderModel getOrderByConsignment(String trackingId);

	SmartOrdersJson getPagedOrdersByB2BUnit(B2BUnitModel b2bUnitModel, int page, String date, String sort);

	/**
	 * Fetches the number of times the provided Product EAN has been ordered on Hybris by the given sub channel. This
	 * method takes all orders into consideration (phone/online).
	 *
	 * @param subChannel
	 *           the b2b unit's sub channel (Pub, Bar, Restaurant) to use when doing the count.
	 * @param productEAN
	 *           the product EAN to evaluate when getting the count.
	 *
	 * @return number of times product EAN has been ordered on Hybris with the given criteria.
	 */
	int getProductOrderCountBySubChannelAndEAN(final String subChannel, final SABMAlcoholVariantProductEANModel productEAN);

	public OrderModel getLastOrderByCustomer(UserModel user);

	/**
	 * @param user
	 * @return
	 */
	OrderModel getFirstOnlineOrderByCustomer(UserModel user);

	/**
	 * @param consignmentStatus
	 * @return
	 */
	Set<OrderModel> getB2BUnitOrdersTimePassesETA(ConsignmentStatus consignmentStatus);
	
	
	/**
	 * Gets all orders which were "Paid" or "Partly Paid" using "Credit Card" for customers on "P1" or "P2" AutoPay membership status
	 * 
	 *  @return List<OrderModel>
	 */
	List<OrderModel> getOrdersByCreditCardPayment();

	/**
	 * Retrieves all orders whose create date is less than or equal than specified date
	 * */
	List<OrderModel> getOrdersToDate(final Date endDate, final int limit);
	
	OrderModel getOrderByCartCode(final String cartCode);
}
