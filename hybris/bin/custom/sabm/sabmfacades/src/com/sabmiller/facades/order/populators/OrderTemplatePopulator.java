/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.sabmiller.facades.order.populators;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.SABMOrderTemplateModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.util.Assert;

import com.sabmiller.facades.order.data.OrderTemplateData;


/**
 * The Class OrderTemplatePopulator.
 */
public class OrderTemplatePopulator implements Populator<SABMOrderTemplateModel, OrderTemplateData>
{
	/** The order entry populator. */
	@Resource(name = "orderEntryConverter")
	private Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final SABMOrderTemplateModel source, final OrderTemplateData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setCode(source.getCode());
		target.setName(source.getName());

		List<OrderEntryData> orderEntryList = null;

		if (CollectionUtils.isNotEmpty(source.getEntries()))
		{
			orderEntryList = Converters.convertAll(source.getEntries(), orderEntryConverter);
			Collections.sort(orderEntryList, Comparator.comparingInt(OrderEntryData::getSequenceNumber));
		}

		target.setEntries(ListUtils.emptyIfNull(orderEntryList));
	}
}
