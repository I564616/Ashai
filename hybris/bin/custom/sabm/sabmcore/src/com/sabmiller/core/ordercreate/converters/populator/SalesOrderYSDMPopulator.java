package com.sabmiller.core.ordercreate.converters.populator;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.sabmiller.core.model.SalesDataModel;
import com.sabmiller.facades.ysdm.data.YSDMRequest;
import com.sabmiller.integration.sap.constants.SabmintegrationConstants;


/**
 * The Class SalesOrderYSDMPopulator.
 */
public class SalesOrderYSDMPopulator implements Populator<AbstractOrderModel, YSDMRequest>
{

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final AbstractOrderModel order, final YSDMRequest target)
	{
		final B2BUnitModel b2bUnit = order.getUnit();

		if (order.getDeliveryShippingCarrier() != null)
		{
			target.setCarrier(order.getDeliveryShippingCarrier().getCarrierCode());
		}
		else
		{
			if (b2bUnit != null && b2bUnit.getDefaultCarrier() != null)
			{
				target.setCarrier(b2bUnit.getDefaultCarrier().getCarrierCode());
			}
		}

		if (order.getPaymentInfo() instanceof CreditCardPaymentInfoModel)
		{
			final String sapCardType = getPaymentCardTypeToken(((CreditCardPaymentInfoModel) order.getPaymentInfo()).getType());
			if (StringUtils.isNotEmpty(sapCardType))
			{
				target.setCcPaymentFlag(sapCardType);
			}
		}

		if (order.getCurrency() != null)
		{
			target.setCurrency(order.getCurrency().getIsocode());
		}


		if (b2bUnit != null && b2bUnit.getSalesData() != null)
		{
			final SalesDataModel salesData = b2bUnit.getSalesData();
			target.setSalesOrg(salesData.getSalesOrgId());
			target.setDistributionChannel(salesData.getDistributionChannel());
			target.setDivision(salesData.getDivision());
		}

		target.setGrossTotal(order.getTotalPrice());
		target.setPoNumber(
				StringUtils.isNotBlank(order.getPurchaseOrderNumber()) ? order.getPurchaseOrderNumber() : order.getUser().getUid());
		target.setPoOrderType(SabmintegrationConstants.SAP_PO_TYPE);
		target.setRequestedDeliveryDate(new Date());

		if (order.getDeliveryAddress() != null && StringUtils.isNotEmpty(order.getDeliveryAddress().getPartnerNumber()))
		{
			target.setShipTo(order.getDeliveryAddress().getPartnerNumber());
		}
		else if (b2bUnit != null && b2bUnit.getDefaultShipTo() != null)
		{
			target.setShipTo(order.getUnit().getDefaultShipTo().getPartnerNumber());
		}

		if (b2bUnit != null)
		{
			target.setSoldTo(b2bUnit.getSoldto());
		}

		if (b2bUnit.getDefaultUnloadingPoint() != null)
		{
			target.setUnloadingPoint(b2bUnit.getDefaultUnloadingPoint().getCode());
		}
	}

	/**
	 * Gets the payment card type token.
	 *
	 * @param hybrisCardType
	 *           the hybris card type
	 * @return the payment card type token
	 */
	private String getPaymentCardTypeToken(final CreditCardType hybrisCardType)
	{
		String sapCardType = null;

		if (CreditCardType.VISA.equals(hybrisCardType))
		{
			sapCardType = SabmintegrationConstants.SAP_VISA_CARD_CODE;
		}
		else if (CreditCardType.MASTER.equals(hybrisCardType))
		{
			sapCardType = SabmintegrationConstants.SAP_MASTER_CARD_CODE;
		}
		else if (CreditCardType.AMEX.equals(hybrisCardType))
		{
			sapCardType = SabmintegrationConstants.SAP_AMEX_CARD_CODE;
		}

		return sapCardType;
	}
}
