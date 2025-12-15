
package com.sabmiller.core.ordercreate.converters.populator;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;

import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.sabmiller.core.model.SalesDataModel;
import com.sabmiller.core.ordersimulate.converters.populator.SalesOrderPopulateHelper;
import com.sabmiller.integration.sap.constants.SabmintegrationConstants;
import com.sabmiller.integration.sap.ordercreate.request.SalesOrderCreateRequest.SalesOrderReqHeader;




public class SalesOrderCreateReqHeaderPopulator implements Populator<AbstractOrderModel, SalesOrderReqHeader>
{

	private SalesOrderPopulateHelper salesOrderPopulateHelper;

	@Override
	public void populate(final AbstractOrderModel cart, final SalesOrderReqHeader target)
	{
		target.setSalesOrderType(SabmintegrationConstants.SAP_SALES_ORDER_TYPE);
		final B2BUnitModel b2bUnit = cart.getUnit();

		if (b2bUnit != null)
		{

			if (cart.getRequestedDeliveryDate() != null)
			{
				final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
				target.setRequestedDeliveryDate(formatter.format(cart.getRequestedDeliveryDate()));
			}
			else
			{
				final DateTime dt = new DateTime().plusDays(1);
				final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");
				final String dtStr = fmt.print(dt);
				target.setRequestedDeliveryDate(dtStr);
			}
			final SalesDataModel salesData = b2bUnit.getSalesData();

			if (salesData != null)
			{
				target.setSalesOrganisation(salesData.getSalesOrgId());
				target.setDistributionChannel(salesData.getDistributionChannel());
				target.setDivision(salesData.getDivision());
			}
		}

		target.setPONumber(
				StringUtils.isNotBlank(cart.getPurchaseOrderNumber()) ? cart.getPurchaseOrderNumber() : cart.getUser().getUid());
		target.setPOOrderType(SabmintegrationConstants.SAP_PO_TYPE);
		target.setShippingCondition(getSalesOrderPopulateHelper().getShippingCondition(b2bUnit, cart));

	}

	/**
	 * @return the salesOrderPopulateHelper
	 */
	public SalesOrderPopulateHelper getSalesOrderPopulateHelper()
	{
		return salesOrderPopulateHelper;
	}

	/**
	 * @param salesOrderPopulateHelper
	 *           the salesOrderPopulateHelper to set
	 */
	public void setSalesOrderPopulateHelper(final SalesOrderPopulateHelper salesOrderPopulateHelper)
	{
		this.salesOrderPopulateHelper = salesOrderPopulateHelper;
	}

}


