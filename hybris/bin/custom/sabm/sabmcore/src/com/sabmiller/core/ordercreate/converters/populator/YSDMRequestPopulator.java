package com.sabmiller.core.ordercreate.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.util.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.util.SabmDateUtils;
import com.sabmiller.facades.ysdm.data.YSDMRequest;
import com.sabmiller.integration.sap.ordercreate.request.SalesOrderCreateRequest;
import com.sabmiller.integration.sap.ordercreate.request.SalesOrderCreateRequest.SalesOrderReqHeader;
import com.sabmiller.integration.sap.ordercreate.request.SalesOrderCreateRequest.SalesOrderReqItem;
import com.sabmiller.integration.sap.ordercreate.request.SalesOrderCreateRequest.SalesOrderReqPartner;


public class YSDMRequestPopulator implements Populator<YSDMRequest, SalesOrderCreateRequest>
{

	private static final Logger LOG = LoggerFactory.getLogger(YSDMRequestPopulator.class.getName());

	@Override
	public void populate(final YSDMRequest ysdmRequest, final SalesOrderCreateRequest target)
	{
		populateHeader(ysdmRequest, target);
		populatePartner(ysdmRequest, target);
		populateItem(ysdmRequest, target);

		LOG.debug("After populating SalesOrderCreateRequest, value is : {} ", target);
	}

	protected void populateHeader(final YSDMRequest source, final SalesOrderCreateRequest target)
	{
		final SalesOrderReqHeader salesOrderReqHeader = new SalesOrderReqHeader();
		salesOrderReqHeader.setSalesOrderType(Config.getString("ysdm.salesorder.type", "YSDM"));
		salesOrderReqHeader.setSalesOrganisation(source.getSalesOrg());
		salesOrderReqHeader.setDistributionChannel(source.getDistributionChannel());
		salesOrderReqHeader.setDivision(source.getDivision());
		salesOrderReqHeader.setPONumber(source.getPoNumber());
		salesOrderReqHeader.setPOOrderType(source.getPoOrderType());
		salesOrderReqHeader.setRequestedDeliveryDate(SabmDateUtils.toString(source.getRequestedDeliveryDate(), "yyyyMMdd"));
		salesOrderReqHeader.setCurrency(source.getCurrency());
		salesOrderReqHeader.setCCPaymentFlag(source.getCcPaymentFlag());

		LOG.debug("salesOrderReqHeader : {} ", salesOrderReqHeader);

		target.setSalesOrderReqHeader(salesOrderReqHeader);
	}

	protected void populatePartner(final YSDMRequest source, final SalesOrderCreateRequest target)
	{
		final SalesOrderReqPartner salesOrderReqPartner = new SalesOrderReqPartner();
		salesOrderReqPartner.setSoldTo(source.getSoldTo());
		salesOrderReqPartner.setShipTo(source.getShipTo());
		salesOrderReqPartner.setCarrier(source.getCarrier());
		salesOrderReqPartner.setUnloadingPoint(source.getUnloadingPoint());

		LOG.debug("salesOrderReqPartner : {} ", salesOrderReqPartner);

		target.setSalesOrderReqPartner(salesOrderReqPartner);
	}

	protected void populateItem(final YSDMRequest source, final SalesOrderCreateRequest target)
	{
		final SalesOrderReqItem salesOrderReqItem = new SalesOrderReqItem();
		salesOrderReqItem.setMaterialNumber(deriveMaterialNumber(source));
		salesOrderReqItem.setQuantity(Config.getString("ysdm.default.quantity", "1"));
		salesOrderReqItem.setUnitOfMeasure(Config.getString("ysdm.default.uom", "PU"));
		salesOrderReqItem.setGrossPrice(String.valueOf(source.getGrossTotal()));
		salesOrderReqItem.setLineItemNumber(String.valueOf(10));

		LOG.debug("salesOrderReqItem : {} ", salesOrderReqItem);

		target.getSalesOrderReqItem().add(salesOrderReqItem);
	}

	private String deriveMaterialNumber(final YSDMRequest source)
	{
		return Config.getString(SabmCoreConstants.PAYMENT_CARD_CODE_PREFIX + source.getCcPaymentFlag(), null);
	}
}