package com.apb.core.order.impl;

import de.hybris.platform.commerceservices.order.strategies.impl.DefaultEntryMergeStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.List;
import java.util.Objects;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;

import org.apache.commons.lang3.BooleanUtils;

import com.apb.core.util.AsahiSiteUtil;
/**
 * Customized so that the Non Bonus product will not add into the bonus Stock
 */
public class ApbEntryMergeStrategy extends DefaultEntryMergeStrategy
{

	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Override
	public AbstractOrderEntryModel getEntryToMerge(
			final List<AbstractOrderEntryModel> entries,
			@Nonnull final AbstractOrderEntryModel newEntry)
	{
		if(!asahiSiteUtil.isCub())
		{
		ServicesUtil.validateParameterNotNullStandardMessage("newEntry", newEntry);

		if (entries == null)
		{
			return null;
		}
		return entries.stream()
				.filter(Objects::nonNull)
					.filter(e -> !newEntry.equals(e) && !e.getIsBonusStock() && BooleanUtils.isFalse(e.getIsFreeGood()))
				.filter(entry -> canMerge(entry, newEntry).booleanValue())
				.sorted(getEntryModelComparator())
				.findFirst()
				.orElse(null);
		}
		return super.getEntryToMerge(entries, newEntry);
	}
}
