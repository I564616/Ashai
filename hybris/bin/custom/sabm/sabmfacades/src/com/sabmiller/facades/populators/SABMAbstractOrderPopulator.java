/**
 *
 */
package com.sabmiller.facades.populators;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.enums.OrderSimulationStatus;
import com.sabmiller.core.model.ShippingCarrierModel;
import com.sabmiller.facades.b2bunit.data.ShippingCarrier;


/**
 * SABMAbstractOrderPopulator Populate the target instance from the source instance.
 */
public class SABMAbstractOrderPopulator implements Populator<AbstractOrderModel, AbstractOrderData>
{
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SABMAbstractOrderPopulator.class);

	/** The b2 b unit converter. */
	private Converter<B2BUnitModel, B2BUnitData> b2BUnitConverter;

	private PriceDataFactory priceDataFactory;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	/*
	 * Populate the target instance from the source instance
	 *
	 */
	@Override
	public void populate(final AbstractOrderModel source, final AbstractOrderData target)
	{

		if(asahiSiteUtil.isCub())
		{

   		target.setRequestedDeliveryDate(source.getRequestedDeliveryDate());

   		if (null != source.getPurchaseOrderNumber())
   		{
   			target.setPurchaseOrderNumber(source.getPurchaseOrderNumber());
   		}
   		if (StringUtils.isNotEmpty(source.getDeliveryInstructions()))
   		{
   			LOG.info("populate DeliveryInstructions to target");
   			target.setDeliveryInstructions(source.getDeliveryInstructions());
   		}
   		target.setLanguage(source.getSite().getDefaultLanguage().getIsocode());
   		//add by SAB-535 populate the deliveryInstruction
   		if (null != source.getDeliveryShippingCarrier())
   		{
   			LOG.info("populate DeliveryShippingCarrier to target");
   			setShippingCarrier(source.getDeliveryShippingCarrier(), target);
   		}
   		if (source.getSapSalesOrderNumber() != null)
   		{
   			target.setSapSalesOrderNumber(source.getSapSalesOrderNumber());
   		}

   		if (source.getUnit() != null)
   		{
   			target.setB2bUnit(b2BUnitConverter.convert(source.getUnit()));
   		}
   		else if (source.getUser() instanceof B2BCustomerModel)
   		{
   			target.setB2bUnit(b2BUnitConverter.convert(((B2BCustomerModel) source.getUser()).getDefaultB2BUnit()));
   		}
   		target.setSalesOrderSimulateSyncDate(source.getSalesOrderSimulateSyncDate());
   		if (source.getOrderSimulationStatus() == null)
   		{
   			target.setOrderSimulationStatus(OrderSimulationStatus.NEED_CALCULATION.getCode());
   		}
   		else
   		{
   			target.setOrderSimulationStatus(source.getOrderSimulationStatus().getCode());
   		}

   		target.setUserDisplayName(source.getUserDisplayName());
   		final boolean bdeOrder = BooleanUtils.isTrue(source.getBdeOrder()) ? true : false;
   		target.setBdeOrder(bdeOrder);

   		if (bdeOrder)
   		{
   		target.setBdeOrderCustomerEmails(source.getBdeOrderCustomerEmails());
   		target.setBdeOrderUserEmails(source.getBdeOrderUserEmails());
   			target.setBdeOrderEmails(ListUtils.union(source.getBdeOrderUserEmails(), source.getBdeOrderCustomerEmails()));

   		target.setBdeOrderEmailText(source.getBdeOrderEmailText());
   		}
   		addTotals(source, target);
		}
		else if(asahiSiteUtil.isSga()) {
			final boolean bdeOrder = BooleanUtils.isTrue(source.getBdeOrder()) ? true : false;
			target.setBdeOrder(bdeOrder);

			if (bdeOrder)
			{
				target.setBdeOrderCustomerEmails(source.getBdeOrderCustomerEmails());
				target.setBdeOrderUserEmails(source.getBdeOrderUserEmails());
				target.setBdeOrderEmails(ListUtils.union(source.getBdeOrderUserEmails(), source.getBdeOrderCustomerEmails()));
				target.setBdeOrderEmailText(source.getBdeOrderEmailText());
			}


			if (asahiSiteUtil.isSga())
			{
				int dealSeqNo = 1;
				for (final OrderEntryData entry : target.getEntries())
				{
					if (StringUtils.isNotEmpty(entry.getFreeGoodEntryNumber()))
					{
						entry.setDealSequenceNumber(dealSeqNo);
						dealSeqNo++;
					}
				}
			}
		}
	}

	/**
	 * SAB-535 Populate the target.ShippingCarrier from the ShippingCarrierModel.
	 *
	 * @param shippingCarrierModel
	 *           the shipping carrier model
	 * @param target
	 *           the target
	 */
	public void setShippingCarrier(final ShippingCarrierModel shippingCarrierModel, final AbstractOrderData target)
	{
		final ShippingCarrier shippingCarrier = new ShippingCarrier();
		shippingCarrier.setCode(shippingCarrierModel.getCarrierCode());
		shippingCarrier.setDescription(shippingCarrierModel.getCarrierDescription());
		shippingCarrier.setCustomerOwned(shippingCarrierModel.getCustomerOwned());
		target.setDeliveryShippingCarrier(shippingCarrier);
	}



	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.commercefacades.order.converters.populator.AbstractOrderPopulator#addTotals(de.hybris.platform.
	 * core.model.order.AbstractOrderModel, de.hybris.platform.commercefacades.order.data.AbstractOrderData)
	 */

	protected void addTotals(final AbstractOrderModel source, final AbstractOrderData prototype)
	{

		prototype.setNetAmount(createPrice(source, source.getNetAmount()));
		final Collection<TaxValue> taxValues = source.getTotalTaxValues();

		LOG.info("TaxValues for order [{}]: {}", source.getCode(), taxValues);

		if (taxValues != null)
		{
			for (final Iterator<TaxValue> iter = taxValues.iterator(); iter.hasNext();)
			{
				final TaxValue taxValue = iter.next();
				if (StringUtils.equalsIgnoreCase(SabmCoreConstants.GST, taxValue.getCode()))
				{
					prototype.setGst(createPrice(source, Double.valueOf(taxValue.getValue())));
				}
				else if (StringUtils.equalsIgnoreCase(SabmCoreConstants.WET, taxValue.getCode()))
				{
					prototype.setWet(createPrice(source, Double.valueOf(taxValue.getValue())));
				}

			}
		}
		if (source.getLoyaltyFee() != null)
		{
			prototype.setTotalLoyaltyFeePrice(createPrice(source, source.getLoyaltyFee()));
		}
		if (source.getAutoPayAdvantageDiscount() != null)
		{
			prototype.setAutoPayAdvantageDiscount(createPrice(source, source.getAutoPayAdvantageDiscount()));
		}
		if (source.getAutoPayAdvantagePlusDiscount() != null)
		{
			prototype.setAutoPayAdvantagePlusDiscount(createPrice(source, source.getAutoPayAdvantagePlusDiscount()));
		}
		prototype.setDeliveryCost(createPrice(source, source.getDeliveryCost()));
		prototype.setTotalDiscounts(createPrice(source, Math.abs(source.getTotalDiscounts())));
		prototype.setDeposit(createPrice(source, Math.abs(source.getDeposit())));
		prototype.setActualDeliveryCost(
				createPrice(source, Math.abs(source.getActualDeliveryCost() != null ? source.getActualDeliveryCost() : 0)));
		prototype.setFreightLimit(createPrice(source, Math.abs(source.getFreightLimit() != null ? source.getFreightLimit() : 0)));
		if (source.getFreightLimit() != null && source.getActualDeliveryCost() != null)
		{
			prototype.setFreightSurcharge(createPrice(source, source.getFreightLimit() - source.getActualDeliveryCost()));
		}
		else
		{
			prototype.setFreightSurcharge(createPrice(source, 0d));
		}
	}



	protected PriceData createPrice(final AbstractOrderModel source, final Double val)
	{
		if (source == null)
		{
			throw new IllegalArgumentException("source order must not be null");
		}

		final CurrencyModel currency = source.getCurrency();
		if (currency == null)
		{
			throw new IllegalArgumentException("source order currency must not be null");
		}

		// Get double value, handle null as zero
		final double priceValue = val != null ? val.doubleValue() : 0d;

		return getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.valueOf(priceValue), currency);
	}

	/**
	 * Sets the b2 b unit converter.
	 *
	 * @param b2bUnitConverter
	 *           the b2b unit converter
	 */
	public void setB2BUnitConverter(final Converter<B2BUnitModel, B2BUnitData> b2bUnitConverter)
	{
		b2BUnitConverter = b2bUnitConverter;
	}

	/**
	 * @return the priceDataFactory
	 */
	public PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	/**
	 * @param priceDataFactory
	 *           the priceDataFactory to set
	 */
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

}
