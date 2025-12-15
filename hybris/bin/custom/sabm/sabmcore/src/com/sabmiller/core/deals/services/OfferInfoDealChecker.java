/**
 *
 */
package com.sabmiller.core.deals.services;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;

import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.EntryOfferInfoModel;


/**
 *
 */
public class OfferInfoDealChecker extends AbstractLostDealChecker<EntryOfferInfoModel>
{

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.AbstractLostDealChecker#isThisDealType(com.sabmiller.core.model.DealModel,
	 * de.hybris.platform.core.model.order.CartModel)
	 */
	@Override
	public boolean isThisDealType(final EntryOfferInfoModel offerInfo)
	{
		return offerInfo.getOfferType() != null && !offerInfo.equals("");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.AbstractLostDealChecker#isLostDeal(com.sabmiller.core.model.DealModel,
	 * de.hybris.platform.core.model.order.AbstractOrderEntryModel, int, de.hybris.platform.core.model.order.CartModel)
	 */
	@Override
	public boolean isLostDeal(final EntryOfferInfoModel deal, final AbstractOrderEntryModel entry, final int newQuantity,
			final CartModel cart)
	{
		if (SabmCoreConstants.OFFER_TYPE_DISCOUNT.equals(deal.getOfferType().toLowerCase())
				|| SabmCoreConstants.OFFER_TYPE_FREEGOOD.equals(deal.getOfferType().toLowerCase()))
		{
			final Long minQuantity = deal.getScaleQuantity();
			if (minQuantity == 0 && newQuantity == 0)
			{
				return true;
			}
			if (newQuantity < minQuantity && entry.getQuantity() >= minQuantity)
			{
				return true;
			}
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.AbstractLostDealChecker#isDeleteDeal(java.lang.Object,
	 * de.hybris.platform.core.model.order.AbstractOrderEntryModel, int, de.hybris.platform.core.model.order.CartModel)
	 */
	@Override
	public boolean isDeleteDeal(final EntryOfferInfoModel deal, final AbstractOrderEntryModel entry, final int newQuantity,
			final CartModel cart)
	{
		return false;
	}

}
