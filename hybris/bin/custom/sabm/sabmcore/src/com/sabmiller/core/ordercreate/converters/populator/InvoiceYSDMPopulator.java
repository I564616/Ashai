package com.sabmiller.core.ordercreate.converters.populator;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Date;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.model.InvoicePaymentModel;
import com.sabmiller.core.model.SalesDataModel;
import com.sabmiller.facades.ysdm.data.YSDMRequest;
import com.sabmiller.integration.sap.constants.SabmintegrationConstants;


/**
 * The Class SalesOrderYSDMPopulator.
 */
public class InvoiceYSDMPopulator implements Populator<InvoicePaymentModel, YSDMRequest>
{
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(InvoiceYSDMPopulator.class);

	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;

	@Resource(name = "b2bCommerceUnitService")
	private B2BCommerceUnitService b2bCommerceUnitService;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final InvoicePaymentModel invoice, final YSDMRequest target)
	{
		B2BUnitModel b2bUnit = null;
		final UserModel user = invoice.getUser();

		if (user instanceof B2BCustomerModel)
		{
			b2bUnit = b2bUnitService.getParent((B2BCustomerModel) user);
		}
		else
		{
			b2bUnit = b2bCommerceUnitService.getParentUnit();
		}

		if (b2bUnit == null)
		{
			LOG.error("Unable to populate YSDMRequest with null b2bUnit");
			return;
		}

		if (b2bUnit.getDefaultCarrier() != null)
		{
			target.setCarrier(b2bUnit.getDefaultCarrier().getCarrierCode());
		}

		if (invoice.getPaymentInfo() instanceof CreditCardPaymentInfoModel)
		{
			final String sapCardType = getPaymentCardTypeToken(((CreditCardPaymentInfoModel) invoice.getPaymentInfo()).getType());
			if (StringUtils.isNotEmpty(sapCardType))
			{
				target.setCcPaymentFlag(sapCardType);
			}
		}

		if (invoice.getCurrency() != null)
		{
			target.setCurrency(invoice.getCurrency().getIsocode());
		}


		if (b2bUnit.getSalesData() != null)
		{
			final SalesDataModel salesData = b2bUnit.getSalesData();
			target.setSalesOrg(salesData.getSalesOrgId());
			target.setDistributionChannel(salesData.getDistributionChannel());
			target.setDivision(salesData.getDivision());
		}

		if (invoice.getAmount() != null)
		{
			target.setGrossTotal(invoice.getAmount().doubleValue());
		}

		target.setPoNumber(user.getUid());
		target.setPoOrderType(SabmintegrationConstants.SAP_PO_TYPE);
		target.setRequestedDeliveryDate(new Date());

		if (b2bUnit.getDefaultShipTo() != null)
		{
			target.setShipTo(b2bUnit.getDefaultShipTo().getPartnerNumber());
		}

		target.setSoldTo(b2bUnit.getSoldto());

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
