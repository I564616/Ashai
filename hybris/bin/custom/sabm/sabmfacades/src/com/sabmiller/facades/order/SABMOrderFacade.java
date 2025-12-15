/**
 *
 */
package com.sabmiller.facades.order;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorfacades.order.B2BOrderFacade;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.servicelayer.data.PaginationData;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.apb.facades.order.data.AsahiQuickOrderData;
import com.sabmiller.commons.enumerations.OrderToCartStatus;
import com.sabmiller.facades.dataimport.response.SalesOrderDataImportResponse;
import com.sabmiller.facades.order.data.TrackOrderData;
import com.sabmiller.facades.order.json.OrderHistoryJson;
import com.sabmiller.facades.smartOrders.json.SmartOrdersJson;


/**
 * The Interface SABMOrderFacade.
 */
public interface SABMOrderFacade extends B2BOrderFacade
{

	/**
	 * Gets the order history for current user in session.
	 *
	 * @param dateFrom
	 *           the date from
	 * @param dateTo
	 *           the date to
	 * @return the order history
	 */
	List<OrderHistoryJson> getOrderHistory(Date dateFrom, Date dateTo);

	List<OrderHistoryJson> getTopOrderHistory(final int count);

	/**
	 * Adds the to cart for order code.
	 *
	 * @param orderCode
	 *           the order code
	 * @return Map<String, Object>
	 *         <p>
	 *         key | value<br/>
	 *         invalidproductTitles | Invalid product name collection <br/>
	 *         cartModificationDatas | Add to cart return CartModificationData collection
	 *         </p>
	 */
	Map<OrderToCartStatus, Object> addToCartForOrderCode(String orderCode);

	/**
	 * Adds the to cart for order code.
	 *
	 * @param orderCode
	 *           the order code
	 * @param entryNumbers
	 *           the list of entry numbers
	 * @return Map<String, Object>
	 *         <p>
	 *         key | value<br/>
	 *         invalidproductTitles | Invalid product name collection <br/>
	 *         cartModificationDatas | Add to cart return CartModificationData collection
	 *         </p>
	 */
	Map<OrderToCartStatus, Object> addToCartForOrderCode(String orderCode, List<String> entryNumbers);

	/**
	 * Persist order.
	 *
	 * @param orderData
	 *           the order data
	 * @return the sales order data import response
	 */
	SalesOrderDataImportResponse persistOrder(OrderData orderData);

	/**
	 * Adds the to template.
	 *
	 * @param orderCode
	 *           the order code
	 * @return the map
	 */
	Map<OrderToCartStatus, Object> addToTemplate(String orderCode);

	/**
	 * Check if there is an order in the system with the sales order number *.
	 *
	 * @param salesOrderNumber
	 *           the sales order number
	 * @return true, if successful
	 */
	boolean orderExist(String salesOrderNumber);

	/**
	 * Gets the email by order.
	 *
	 * @param orderCode
	 *           the order code
	 * @return the email by order
	 */
	String getEmailByOrder(String orderCode);

	/**
	 * @param date
	 * @param sort
	 * @return
	 */
	public SmartOrdersJson smartOrdersJson(String date, String sort);

	/**
	 * @param unit
	 * @param requestedDeliveryDate
	 * @return
	 */
	String getCutoffTime(B2BUnitModel unit, Date requestedDeliveryDate);

	/**
	 *
	 * @return
	 */
	List<TrackOrderData> getTrackOrderData(final OrderModel orderModel);


	List<TrackOrderData> getActiveOrderByB2BUnit();

	OrderModel getOrderBySapSalesOrderNumber(final String orderCode);

	OrderModel getOrderByCartCode(final String cartCode);

	/**
	 * Import order.
	 *
	 * @param map
	 *           the map
	 */
	void importOrder(OrderData map, String siteUid);

	/**
	 * Gets the paged order history.
	 *
	 * @param pageableData
	 *           the pageable data
	 * @return the paged order history
	 */
	SearchPageData<OrderHistoryData> getPagedOrderHistory(PageableData pageableData, String cofoDate) throws ParseException;

	/**
	 * <p>
	 * This method will retreive the order based on order code.
	 *
	 * @param code
	 * @return
	 */
	OrderData getAsahiOrderDetailsForCode(OrderModel orderModel);

	AsahiQuickOrderData getQuickOrders(String sortCode);

	/**
	 *
	 * This Method will return order details for order code.
	 *
	 * @param code
	 * @return
	 */
	OrderModel getOrderDetails(final String code);

	/**
	 * This Method will send order to backened
	 *
	 * @param orderModel
	 */
	void sendOrderToBackendSystem(final OrderModel orderModel);

   String exportOrderCSV(final PageableData pageableData, final String cofoDate) throws ParseException;
}
