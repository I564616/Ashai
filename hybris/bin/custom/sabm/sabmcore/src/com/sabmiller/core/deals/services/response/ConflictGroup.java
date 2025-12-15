/**
 *
 */
package com.sabmiller.core.deals.services.response;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.model.DealModel;


/**
 * Group of deal conflicts determined by the Cart Evaluation Service
 *
 * @author joshua.a.antony
 */
public class ConflictGroup
{
	private static final Logger LOG = LoggerFactory.getLogger(ConflictGroup.class);

	final List<Conflict> conflicts;

	public ConflictGroup(final Map<DealModel, List<DealModel>> conflictDealsMap)
	{
		this.conflicts = toList(conflictDealsMap);
	}

	private List<Conflict> toList(final Map<DealModel, List<DealModel>> conflictDealsMap)
	{
		final List<Conflict> conflicts = new ArrayList<Conflict>();
		for (final DealModel k : conflictDealsMap.keySet())
		{
			final Conflict eachConflict = new Conflict();
			eachConflict.add(k).addAll(conflictDealsMap.get(k));

			if (!conflicts.contains(eachConflict))
			{
				conflicts.add(eachConflict);
			}
		}

		LOG.info("Total conflicts = {} , conflicts : {}", conflicts.size(), conflicts);

		return conflicts;
	}


	public int getTotalConflicts()
	{
		return conflicts.size();
	}

	public boolean hasConflictingDeals()
	{
		return conflicts != null && !conflicts.isEmpty();
	}

	public List<Conflict> getConflicts()
	{
		return conflicts;
	}

	public class Conflict
	{
		private final Set<DealModel> conflictingDeals = new HashSet<DealModel>();

		public Conflict add(final DealModel deal)
		{
			conflictingDeals.add(deal);
			return this;
		}

		public Conflict addAll(final List<DealModel> deals)
		{
			conflictingDeals.addAll(deals);
			return this;
		}

		public Set<DealModel> getDeals()
		{
			return conflictingDeals;
		}

		public int totalDeals()
		{
			return conflictingDeals.size();
		}

		@Override
		public int hashCode()
		{
			return totalDeals();
		}

		@Override
		public boolean equals(final Object obj)
		{
			final Conflict other = (Conflict) obj;
			return this.totalDeals() == other.totalDeals() && this.conflictingDeals.containsAll(other.conflictingDeals);

		}

		@Override
		public String toString()
		{
			final StringBuilder sb = new StringBuilder("Total deals : " + totalDeals() + " =>> deal codes ");
			for (final DealModel eachDeal : getDeals())
			{
				sb.append(" : ").append(eachDeal.getCode());
			}
			return sb.toString();
		}
	}


}
