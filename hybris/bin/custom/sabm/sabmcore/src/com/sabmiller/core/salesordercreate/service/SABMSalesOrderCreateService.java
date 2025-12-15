/**
 *
 */
package com.sabmiller.core.salesordercreate.service;

import de.hybris.platform.core.model.order.AbstractOrderModel;

import com.sabmiller.core.cart.errors.exceptions.SalesOrderCreateException;
import com.sabmiller.facades.ysdm.data.YSDMRequest;


/**
 * The Interface SABMSalesOrderCreateService.
 */
public interface SABMSalesOrderCreateService
{

	/**
	 * Creates the order in sap.
	 *
	 * @param cartModel
	 *           the cart model
	 * @throws SalesOrderCreateException
	 *            the sales order create exception
	 */
	void createOrderInSAP(AbstractOrderModel cartModel) throws SalesOrderCreateException;

    /*
     * (non-Javadoc)
     *
     * @see
     * com.sabmiller.core.salesordercreate.service.SABMSalesOrderCreateService#createOrderInSAP(de.hybris.platform.core.
     * model.order.AbstractOrderModel)
     */
    void createOrderInSAPForPostback(AbstractOrderModel cartModel) throws SalesOrderCreateException;

    /**
	 * Creates the ysdm order in sap.
	 *
	 * @param ysdmRequest
	 *           the ysdm request
	 */
	void createYSDMOrderInSAP(YSDMRequest ysdmRequest);
}
