/**
 *
 */
package com.sabmiller.facades.populators;

import de.hybris.platform.acceleratorfacades.cart.action.populator.AcceleratorCartEntryActionPopulator;
import de.hybris.platform.commercefacades.order.converters.populator.OrderEntryPopulator;
import de.hybris.platform.commercefacades.order.data.EntryOfferInfoData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.util.Collection;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.deals.dao.DealsDao;
import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.core.model.AsahiDealModel;
import com.sabmiller.core.model.AsahiFreeGoodsDealBenefitModel;
import com.sabmiller.core.model.AsahiProductDealConditionModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.EntryOfferInfoModel;
import com.sabmiller.facades.deal.data.DealJson;
import com.sabmiller.facades.product.data.UomData;


public class SABMOrderEntryPopulator<T extends OrderEntryData> extends OrderEntryPopulator
{


	private Converter<EntryOfferInfoModel, EntryOfferInfoData> entryOfferInfoConverter;

	@Resource(name = "commonI18NService")
	private CommonI18NService commonI18NService;

	@Resource(name = "dealsService")
	private DealsService dealsService;

	@Resource(name = "dealJsonConverter")
	private Converter<DealModel, DealJson> dealJsonConverter;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;

	@Resource(name="cartEntryActionPopulator")
	private AcceleratorCartEntryActionPopulator cartEntryActionPopulator;


	/** The deals dao. */
	@Resource(name = "dealsDao")
	private DealsDao dealsDao;

	@Resource(name = "asahiCoreUtil")
	private AsahiCoreUtil asahiCoreUtil;




	@Override
	public void populate(final AbstractOrderEntryModel source, final OrderEntryData target)
	{
		if(asahiSiteUtil.isCub())
		{
   		if (source != null && target != null)
   		{
   			super.populate(source, target);

   			if (source.getUnit() != null)
   			{
   				target.setUnit(convertUnit(source.getUnit()));
   			}

   			if (source.getDeliveryCost() != null)
   			{
   				target.setDeliveryCost(createPrice(source, source.getDeliveryCost()));
   			}

   			if (StringUtils.isNotEmpty(source.getFreeGoodEntryNumber()))
   			{
   				target.setFreeGoodEntryNumber(source.getFreeGoodEntryNumber());
   			}
   			target.setAvailabilityInfo(source.getAvailabilityInfo());
   			if (source.getOfferInfo() != null)
   			{
   				target.setOfferData(Converters.convertAll(source.getOfferInfo(), getEntryOfferInfoConverter()));
   				target.setIsLimitedExceed(getIsLimitedExceed(source));
   			}
   			target.setIsFreeGood(source.getIsFreeGood() != null ? source.getIsFreeGood().booleanValue() : false);
   			target.setAvailabilityInfo(source.getAvailabilityInfo());


   			target.setSapLineNumber(source.getSapLineNumber());
   			if (source.getSapConfirmedQuantity() != null)
   			{
   				target.setSapConfirmedQuantity(source.getSapConfirmedQuantity());
   			}
   			if (source.getTotalEntryDiscount() != null)
   			{
   				target.setTotalDiscountAmount(super.createPrice(source, Math.abs(source.getTotalEntryDiscount())));

   				if (source.getOrder() != null && source.getOrder().getCurrency() != null
   						&& source.getOrder().getCurrency().getDigits() != null)
   				{
   					final double unitDiscount = source.getQuantity() == 0? 0:commonI18NService.roundCurrency(source.getTotalEntryDiscount() / source.getQuantity(),
   							source.getOrder().getCurrency().getDigits().intValue()); //added 0 verification to avoid divide by zero

   					target.setUnitDiscountAmount(super.createPrice(source, Math.abs(unitDiscount)));

   					if (source.getBasePrice() != null)
   					{
   						target.setBasePrice(createPrice(source, BigDecimal.valueOf(source.getBasePrice())
   								.subtract(BigDecimal.valueOf(Math.abs(unitDiscount))).doubleValue()));
   					}
   				}
   			}

   			if (source.getLoyaltyFee() != null)
   			{
   				target.setLoyaltyFeePrice(super.createPrice(source, source.getLoyaltyFee()));
   			}

   			if (source.getAutoPayAdvantageDiscount() != null)
   			{
   				target.setAutoPayAdvantageDiscount(super.createPrice(source, source.getAutoPayAdvantageDiscount()));
   			}
   			if (source.getAutoPayAdvantagePlusDiscount() != null)
   			{
   				target.setAutoPayAdvantagePlusDiscount(super.createPrice(source, source.getAutoPayAdvantagePlusDiscount()));
   			}

   			if (source.getInfo() != null)
   			{
   				target.setInfo(source.getInfo());
   			}
   			target.setFreeGoodsForDeal(source.getFreeGoodsForDeal());
   			target.setIsChange(BooleanUtils.toBoolean(source.getIsChange()));

   			target.setMinimumStockOnHand(source.getMinimumStockOnHand());
   			target.setSequenceNumber(source.getSequenceNumber() != null ? source.getSequenceNumber() : source.getEntryNumber());

   			// Set wet and deposit data
   			if (source.getDeposit() != null)
   			{
   				target.setDeposit(createPrice(source, source.getDeposit()));
   			}

   			if (CollectionUtils.isNotEmpty(source.getTaxValues()))
   			{
   				target.setWet(createPrice(source, getWETTaxValue(source.getTaxValues())));
   			}


   			String orderDetails = "";
   			if (target.getQuantity() != null && target.getUnit() != null)
   			{
   				orderDetails = target.getQuantity() + " "
   						+ (target.getQuantity() > 1 ? target.getUnit().getPluralName() : target.getUnit().getName());
   			}
   			target.setOrderDetails(orderDetails);


   		}
		}
		else
		{
			super.populate(source, target);
			if (asahiSiteUtil.isSga())
			{
				target.setIsFreeGood(null != source.getIsFreeGood() ? source.getIsFreeGood().booleanValue() : false);
				target.setFreeGoodEntryNumber(source.getFreeGoodEntryNumber());
				if (StringUtils.isNotBlank(source.getAsahiDealCode()))
				{
					final AsahiDealModel deal = dealsDao.getSgaDealByCode(source.getAsahiDealCode());
					if (null != ((AsahiFreeGoodsDealBenefitModel) deal.getDealBenefit())
							&& null != ((AsahiFreeGoodsDealBenefitModel) deal.getDealBenefit()).getQuantity())
					{
						target.setFreeGoodEntryQty(
								Long.valueOf(((AsahiFreeGoodsDealBenefitModel) deal.getDealBenefit()).getQuantity())
										* (source.getQuantity()
												/ ((AsahiProductDealConditionModel) deal.getDealCondition()).getQuantity()));
					}
					target.setAsahiDealTitle(asahiCoreUtil.getAsahiDealTitle(deal));
				}
			}
		}
	}

	/**
	 * Get the wet tax value of the order entry given the list of tax values.
	 *
	 * @param taxValues
	 *           the list of tax values from the order entry model
	 * @return wetValue the wet value retrieved from the list of tax values.
	 */
	private Double getWETTaxValue(final Collection<TaxValue> taxValues)
	{
		Double wetValue = 0.0;
		for (final TaxValue taxValue : taxValues)
		{
			if (SabmCoreConstants.WET.equals(taxValue.getCode()))
			{
				wetValue = taxValue.getValue();
				break;
			}
		}
		return wetValue;
	}

	/**
	 * try to convert the UomData
	 *
	 * @param unitModel
	 * @return UomData
	 */
	protected UomData convertUnit(final UnitModel unitModel)
	{
		final UomData unitData = new UomData();
		if (unitModel != null)
		{
			unitData.setCode(unitModel.getCode());
			unitData.setName(unitModel.getName());
			unitData.setPluralName(unitModel.getPluralName());
		}

		return unitData;
	}

	/**
	 * Gets the checks if is limited exceed.
	 *
	 * @param source
	 *           the source
	 * @return the limited exceed
	 */
	protected boolean getIsLimitedExceed(final AbstractOrderEntryModel source)
	{
		if (CollectionUtils.isEmpty(source.getOfferInfo()))
		{
			return false;
		}

		final Long quantity = source.getQuantity();

		for (final EntryOfferInfoModel offerInfo : source.getOfferInfo())
		{
			if (SabmCoreConstants.OFFER_TYPE_LIMITED.equals(offerInfo.getOfferType())
					&& NumberUtils.isNumber(offerInfo.getScaleAmount()) && NumberUtils.isNumber(offerInfo.getTotalCondValue()))
			{
				final BigDecimal scaleAmount = new BigDecimal(offerInfo.getScaleAmount());
				final BigDecimal totalCondValue = new BigDecimal(offerInfo.getTotalCondValue());

				if (totalCondValue.compareTo(scaleAmount.multiply(BigDecimal.valueOf(quantity))) < 0)
				{
					return true;
				}
			}
		}

		return false;
	}

	@Override
	protected void addCommon(final AbstractOrderEntryModel orderEntry, final OrderEntryData entry)
	{
		if(!asahiSiteUtil.isCub())
		{
		super.addCommon(orderEntry, entry);
		if (orderEntry.getInvoicedQty() != null)
		{
			entry.setInvoicedQty(orderEntry.getInvoicedQty());
		}
		if (orderEntry.getStatus() != null)
		{
			entry.setOrderEntryStatus(orderEntry.getStatus().getCode());
		}
		entry.setCalculated(orderEntry.getCalculated());
		entry.setWetNotIncluded(orderEntry.isWetNotIncluded());
		entry.setIsBonusStock(orderEntry.getIsBonusStock());
		cartEntryActionPopulator.populate(orderEntry, entry);
		}
		else {
			super.addCommon(orderEntry, entry);
		}
	}

	@Override
	protected void addTotals(final AbstractOrderEntryModel orderEntry, final OrderEntryData entry)
	{
		if(!asahiSiteUtil.isCub())
		{
		if (orderEntry.getBasePrice() != null)
		{
			entry.setBasePrice(createPrice(orderEntry, orderEntry.getBasePrice()));
		}

		if (orderEntry.getNetUnitPrice() != null)
		{
			entry.setDiscountPrice(createPrice(orderEntry, orderEntry.getNetUnitPrice()));
		}
		else if (orderEntry.getBasePrice() != null)
		{
			entry.setDiscountPrice(createPrice(orderEntry, orderEntry.getBasePrice()));
		}

		final AbstractOrderModel order = orderEntry.getOrder();
		final CurrencyModel curr = order.getCurrency();
		final int digits = curr.getDigits().intValue();
		double totalPriceWithoutDiscount = 0.0D;
		if (this.asahiSiteUtil.isSga())
		{
			totalPriceWithoutDiscount = commonI18NService
					.roundCurrency((orderEntry.getNetUnitPrice() != null ? Double.valueOf(orderEntry.getNetUnitPrice()) : 0.0D)
							* (null != orderEntry.getQuantity() ? orderEntry.getQuantity().longValue() : 0), digits);
		}
		else
		{
			totalPriceWithoutDiscount = commonI18NService
					.roundCurrency((null != orderEntry.getBasePrice() ? orderEntry.getBasePrice().doubleValue() : 0.0D)
							* (null != orderEntry.getQuantity() ? orderEntry.getQuantity().longValue() : 0), digits);
		}

		entry.setTotalPrice(createPrice(orderEntry, totalPriceWithoutDiscount));
		}
		else
		{
			super.addTotals(orderEntry, entry);
		}

	}

	public Converter<EntryOfferInfoModel, EntryOfferInfoData> getEntryOfferInfoConverter()
	{
		return entryOfferInfoConverter;
	}

	public void setEntryOfferInfoConverter(final Converter<EntryOfferInfoModel, EntryOfferInfoData> entryOfferInfoConverter)
	{
		this.entryOfferInfoConverter = entryOfferInfoConverter;
	}


}