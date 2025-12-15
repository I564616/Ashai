/**
 *
 */
package com.sabmiller.core.deals.services.response;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sabmiller.core.enums.DealTypeEnum;
import com.sabmiller.core.model.DealModel;


/**
 * Response of the Cart Evaluation service
 *
 * @author joshua.a.antony
 */
public class DealQualificationResponse implements Serializable
{
	@Serial
	private static final long serialVersionUID = 1L;

	private final List<DealModel> greenDeals;
	private final List<DealModel> amberDeals;
	private final List<DealModel> redDeals;
	private final ConflictGroup conflictGroup;


	public DealQualificationResponse(final List<DealModel> greenDeals, final List<DealModel> amberDeals,
			final List<DealModel> redDeals, final ConflictGroup conflictGroup)
	{
		this.greenDeals = greenDeals;
		this.amberDeals = amberDeals;
		this.redDeals = redDeals;
		this.conflictGroup = conflictGroup;
	}


	public List<DealModel> getGreenDeals()
	{
		return greenDeals;
	}

	public List<DealModel> getAmberDeals()
	{
		return amberDeals;
	}

	public List<DealModel> getRedDeals()
	{
		return redDeals;
	}

	public ConflictGroup getConflictGroup()
	{
		return conflictGroup;
	}

	public boolean hasConflictingDeals()
	{
		return conflictGroup.hasConflictingDeals();
	}

	public List<DealModel> getComplexGreenDeals()
	{
		final List<DealModel> complexDeals = new ArrayList<DealModel>();
		for (final DealModel deal : getGreenDeals())
		{
			if (DealTypeEnum.COMPLEX.equals(deal.getDealType()))
			{
				complexDeals.add(deal);
			}
		}
		return complexDeals;
	}
}
