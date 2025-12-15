package com.sabmiller.facades.deal.converters;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.Assert;

import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.core.deals.services.response.ConflictGroup;
import com.sabmiller.core.deals.services.response.ConflictGroup.Conflict;
import com.sabmiller.core.deals.services.response.PartialDealQualificationResponse;
import com.sabmiller.core.deals.services.response.PartialDealQualificationResponse.PartialAvailability;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.facades.deal.data.ConflictDealsJson;
import com.sabmiller.facades.deal.data.DealJson;
import com.sabmiller.facades.deal.data.PartiallyQualifiedDealsJson;


/**
 * Created by wei.yang.ng on 8/05/2016.
 */
public class PartiallyQualifiedDealConverter implements Converter<PartialDealQualificationResponse, PartiallyQualifiedDealsJson>
{
	@Resource(name = "dealJsonPopulator")
	private Populator<List<DealModel>, DealJson> dealJsonPopulator;

	@Resource(name = "dealProductPopulator")
	private Populator<List<DealModel>, DealJson> dealProductPopulator;

	@Resource(name = "partialDealTitlePopulator")
	private Populator<PartialAvailability, DealJson> partialDealTitlePopulator;

	@Resource(name = "partiallyQualifiedDealProductPopulator")
	private Populator<PartialAvailability, DealJson> pqdProductPopulator;

	@Resource
	private DealsService dealsService;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Override
	public PartiallyQualifiedDealsJson convert(final PartialDealQualificationResponse source) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");

		final PartiallyQualifiedDealsJson target = new PartiallyQualifiedDealsJson();

		return convert(source, target);
	}

	@Override
	public PartiallyQualifiedDealsJson convert(final PartialDealQualificationResponse source,
			final PartiallyQualifiedDealsJson target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		final List<DealJson> partialDeals = new ArrayList<>();
		final List<ConflictDealsJson> partialConflicts = new ArrayList<>();
		final Set<PartialAvailability> pas = source.getPartialAvailabilites();
		final List<DealModel> qualifiedDeals = source.getAllPartiallyQualifiedDeals();
		final ConflictGroup conflictGroup = source.getConflictGroup();

		for (final Conflict conflict : conflictGroup.getConflicts())
		{
			qualifiedDeals.removeAll(conflict.getDeals());
		}

		final List<List<DealModel>> composeComplexFreeProducts = dealsService.composeComplexFreeProducts(qualifiedDeals);

		for (final Conflict conflict : conflictGroup.getConflicts())
		{
			composeComplexFreeProducts.add(new ArrayList<>(conflict.getDeals()));
		}

		// SABMC-1648, get the lost deal codes from the session
		final List<String> lostDealCodes = sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_LOST_DEAL_CODES);
		// After using it, remove it. because this will be immediately invoked after the customer apply to lost deal.
		sessionService.removeAttribute(SabmCoreConstants.SESSION_ATTR_LOST_DEAL_CODES);


		for (final List<DealModel> dealList : composeComplexFreeProducts)
		{
			if (CollectionUtils.isEmpty(dealList))
			{
				continue;
			}
			//remove the just lost deal
			subtractLostDeals(dealList, lostDealCodes);
			if (CollectionUtils.isNotEmpty(dealList))
			{
				if (dealList.size() == 1)
				{
					final DealJson convertedDealJson = convertPartialToJson(pas, dealList.get(0));

					if (convertedDealJson != null)
					{
						partialDeals.add(convertedDealJson);
					}
				}
				else
				{
					final ConflictDealsJson partialConflict = new ConflictDealsJson();
					partialConflict.setConflict(new ArrayList<>());
					for (final DealModel dealModel : dealList)
					{
						final DealJson convertedDealJson = convertPartialToJson(pas, dealModel);

						if (convertedDealJson != null)
						{
							partialConflict.getConflict().add(convertedDealJson);
						}
					}

					if (CollectionUtils.isNotEmpty(partialConflict.getConflict()))
					{
						partialConflicts.add(partialConflict);
					}
				}
			}
		}

		target.setConflicts(partialConflicts);
		target.setDeals(partialDeals);

		return target;
	}

	/**
	 * SABMC-1648, add the logic to skip popup the window for the partially qualifier deal which has been lost. so this
	 * logic will subtract the deal which has been lost(means the customer select "Yes" when the cart page popup the lost
	 * deal window)
	 *
	 * @param deals
	 *           the original deals
	 * @param lostDealCodes
	 *           the lost deal code
	 */
	private void subtractLostDeals(final List<DealModel> deals, final List<String> lostDealCodes)
	{
		if (CollectionUtils.isEmpty(deals) || CollectionUtils.isEmpty(lostDealCodes))
		{
			return;
		}

		final List<DealModel> needToBeRemove = new ArrayList<>();
		for (final DealModel deal : CollectionUtils.emptyIfNull(deals))
		{
			if (lostDealCodes.contains(deal.getCode()))
			{
				needToBeRemove.add(deal);
			}
		}
		deals.removeAll(needToBeRemove);

	}



	protected DealJson convertPartialToJson(final Set<PartialAvailability> pas, final DealModel deal)
	{
		for (final PartialAvailability pa : pas)
		{
			if (pa.getDeal().equals(deal))
			{
				final DealJson dealJson = new DealJson();
				final List<DealModel> dealModelList = new ArrayList<>();
				final DealModel partiallyQualifiedDeal = pa.getDeal();
				dealModelList.add(partiallyQualifiedDeal);
				dealJsonPopulator.populate(dealModelList, dealJson);
				dealProductPopulator.populate(dealModelList, dealJson);

				pqdProductPopulator.populate(pa, dealJson);
				partialDealTitlePopulator.populate(pa, dealJson);

				return dealJson;
			}
		}

		return null;
	}
}
