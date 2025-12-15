/**
 *
 */
package com.sabmiller.core.deals.services.response;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.DealModel;


/**
 * The Class PartialDealQualificationResponse.
 */
public class PartialDealQualificationResponse implements Serializable
{

	/** The Constant serialVersionUID. */
	@Serial
	private static final long serialVersionUID = 1L;

	/** The partial availabilites. */
	private final Set<PartialAvailability> partialAvailabilites = new HashSet<>();

	/** The conflict group. */
	private ConflictGroup conflictGroup;

	/**
	 * Adds the a PartialAvailability to the Set.
	 *
	 * @param deal
	 *           the deal
	 * @param dealCondition
	 *           the deal condition
	 * @param triggerQty
	 *           the trigger qty
	 * @param matchingCartEntries
	 *           the matching cart entries
	 * @param scale
	 *           the scale
	 */
	public void add(final DealModel deal, final AbstractDealConditionModel dealCondition, final int triggerQty,
			final List<AbstractOrderEntryModel> matchingCartEntries, final int scale, final int ratio, final boolean addAll)
	{
		final PartialAvailability pa = get(partialAvailabilites, deal, scale);

		final int realTriggerQty = BooleanUtils.isTrue(deal.getScaleDeal()) && BooleanUtils.isNotTrue(dealCondition.getMandatory())
				&& scale > 0 ? scale : triggerQty;

		pa.add(dealCondition, realTriggerQty, matchingCartEntries);
		pa.setRatio(ratio);
		boolean addDeal = false;

		if (pa.getTotalAvailableQty() < scale)
		{
			addDeal = true;
		}
		else
		{
			for (final AbstractDealConditionModel condition : deal.getConditionGroup().getDealConditions())
			{
				final long requiredQty = pa.getRequiredQtyWithGivenDealCondition(condition);

				if (requiredQty > 0
						&& (BooleanUtils.isTrue(condition.getMandatory()) || deal.getConditionGroup().getDealConditions().size() == 1))
				{
					addDeal = true;
					break;
				}
			}
		}
		if (addAll || addDeal)
		{
			partialAvailabilites.add(pa);
		}
	}

	/**
	 * The Class PartialAvailability.
	 */
	public class PartialAvailability
	{

		/**
		 * The Class Availability.
		 */
		class Availability
		{

			/** The trigger qty. */
			private final long triggerQty;

			/** The available qty. */
			private final long availableQty;

			private boolean isMandatory = false;

			/**
			 * Instantiates a new availability.
			 *
			 * @param triggerQty
			 *           the trigger qty
			 * @param cartEntries
			 *           the cart entries
			 */
			public Availability(final int triggerQty, final List<AbstractOrderEntryModel> cartEntries, final boolean isMandatory)
			{
				this.triggerQty = triggerQty;
				this.availableQty = determineAvailableQty(cartEntries);
				this.isMandatory = isMandatory;
			}

			/**
			 * Required qty.
			 *
			 * @return the long
			 */
			public long requiredQty()
			{
				return triggerQty - availableQty;
			}

			/**
			 * Gets the available qty.
			 *
			 * @return the available qty
			 */
			public long getAvailableQty()
			{
				return availableQty;
			}

			/**
			 * Determine available qty.
			 *
			 * @param cartEntries
			 *           the cart entries
			 * @return the long
			 */
			private long determineAvailableQty(final List<AbstractOrderEntryModel> cartEntries)
			{
				long totalQty = 0;
				for (final AbstractOrderEntryModel eachEntry : cartEntries)
				{
					if (BooleanUtils.isNotTrue(eachEntry.getIsFreeGood()))
					{
						totalQty = totalQty + eachEntry.getQuantity();
					}
				}
				return totalQty;
			}

			public boolean isMandatory()
			{
				return this.isMandatory;
			}
		}



		/** The deal. */
		private final DealModel deal;

		/** The map. */
		private final Map<AbstractDealConditionModel, Availability> map = new HashMap<>();

		/** The scale. */
		private int scale;

		private int ratio;

		/**
		 * Instantiates a new partial availability.
		 *
		 * @param deal
		 *           the deal
		 */
		public PartialAvailability(final DealModel deal)
		{
			this.deal = deal;
		}

		/**
		 * Instantiates a new partial availability.
		 *
		 * @param deal
		 *           the deal
		 * @param scale
		 *           the scale
		 */
		public PartialAvailability(final DealModel deal, final int scale)
		{
			this(deal);
			this.scale = scale;
		}

		/**
		 * @return the ratio
		 */
		public int getRatio()
		{
			return ratio;
		}

		/**
		 * @param ratio
		 *           the ratio to set
		 */
		public void setRatio(final int ratio)
		{
			this.ratio = ratio;
		}

		/**
		 * Adds the @Availability to the map.
		 *
		 * @param dealCondition
		 *           the deal condition
		 * @param triggerQty
		 *           the trigger qty
		 * @param matchingCartEntries
		 *           the matching cart entries
		 */
		public void add(final AbstractDealConditionModel dealCondition, final int triggerQty,
				final List<AbstractOrderEntryModel> matchingCartEntries)
		{
			map.put(dealCondition,
					new Availability(triggerQty, matchingCartEntries, BooleanUtils.isTrue(dealCondition.getMandatory())));
		}

		/**
		 * Gets the required qty with given deal condition.
		 *
		 * @param dealCondition
		 *           the deal condition
		 * @return the required qty with given deal condition
		 */
		public long getRequiredQtyWithGivenDealCondition(final AbstractDealConditionModel dealCondition)
		{
			final Availability availability = map.get(dealCondition);

			if (availability == null)
			{
				return 0;
			}

			return availability.requiredQty();
		}

		/**
		 * Gets the available qty with given deal condition.
		 *
		 * @param dealCondition
		 *           the deal condition
		 * @return the available qty with given deal condition
		 */
		public long getAvailableQtyWithGivenDealCondition(final AbstractDealConditionModel dealCondition)
		{
			final Availability availability = map.get(dealCondition);

			if (availability == null)
			{
				return 0;
			}

			return availability.getAvailableQty();
		}

		/**
		 * Gets the deal.
		 *
		 * @return the deal
		 */
		public DealModel getDeal()
		{
			return deal;
		}

		/**
		 * Checks if is scale deal.
		 *
		 * @return true, if is scale deal
		 */
		public boolean isScaleDeal()
		{
			return BooleanUtils.toBoolean(getDeal().getScaleDeal());
		}

		/**
		 * Gets the scale.
		 *
		 * @return the scale
		 */
		public int getScale()
		{
			return scale;
		}

		/**
		 * Gets the total available qty.
		 *
		 * @return the total available qty
		 */
		public long getTotalAvailableQty()
		{
			long total = 0;
			for (final Availability eachAvailability : map.values())
			{
				total = total + eachAvailability.availableQty;
			}
			return total;
		}
	}

	/**
	 * Gets the.
	 *
	 * @param availabilities
	 *           the availabilities
	 * @param deal
	 *           the deal
	 * @param scale
	 *           the scale
	 * @return the partial availability
	 */
	private PartialAvailability get(final Set<PartialAvailability> availabilities, final DealModel deal, final int scale)
	{
		for (final PartialAvailability eachAvail : availabilities)
		{
			if (eachAvail.getDeal().equals(deal))
			{
				return eachAvail;
			}
		}
		return deal.getScaleDeal() ? new PartialAvailability(deal, scale) : new PartialAvailability(deal);
	}

	/**
	 * Gets the all partially qualified deals.
	 *
	 * @return the all partially qualified deals
	 */
	public List<DealModel> getAllPartiallyQualifiedDeals()
	{
		final List<DealModel> deals = new ArrayList<>();
		for (final PartialAvailability pa : getPartialAvailabilites())
		{
			deals.add(pa.getDeal());
		}
		return deals;
	}

	/**
	 * Checks for deal.
	 *
	 * @param dealModel
	 *           the deal model
	 * @return true, if successful
	 */
	public boolean hasDeal(final DealModel dealModel)
	{
		return hasDeal(dealModel.getCode());
	}

	/**
	 * Checks for deal.
	 *
	 * @param dealCode
	 *           the deal code
	 * @return true, if successful
	 */
	public boolean hasDeal(final String dealCode)
	{
		for (final DealModel eachDeal : CollectionUtils.emptyIfNull(getAllPartiallyQualifiedDeals()))
		{
			if (eachDeal.getCode().equals(dealCode))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the partial availabilites.
	 *
	 * @return the partial availabilites
	 */
	public Set<PartialAvailability> getPartialAvailabilites()
	{
		return partialAvailabilites;
	}

	/**
	 * Sets the conflict group.
	 *
	 * @param conflictGroup
	 *           the new conflict group
	 */
	public void setConflictGroup(final ConflictGroup conflictGroup)
	{
		this.conflictGroup = conflictGroup;
	}

	/**
	 * Gets the conflict group.
	 *
	 * @return the conflict group
	 */
	public ConflictGroup getConflictGroup()
	{
		return conflictGroup;
	}
}
