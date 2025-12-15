/**
 *
 */
package com.sabmiller.core.comparators;

import de.hybris.platform.commerceservices.util.AbstractComparator;
import de.hybris.platform.core.model.order.SABMOrderTemplateModel;

import java.util.Comparator;
import java.util.Objects;


@SuppressWarnings("SE_COMPARATOR_SHOULD_BE_SERIALIZABLE")
public class OrderTemplateNameComparator extends AbstractComparator<SABMOrderTemplateModel>
{
	@Override
	protected int compareInstances(final SABMOrderTemplateModel ot1, final SABMOrderTemplateModel ot2)
	{
		if (ot1 != null && ot2 != null)
		{
			return Objects.compare(ot1.getName(), ot2.getName(), Comparator.naturalOrder());
		}
		else if (ot1 == ot2)
		{
			return 0;
		}
		else if (ot1 == null)
		{
			return -1;
		}
		else
		{
			return 1;
		}
	}
}
