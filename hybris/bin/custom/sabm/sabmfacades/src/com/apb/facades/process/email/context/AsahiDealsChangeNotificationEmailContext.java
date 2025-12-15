/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.apb.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.facades.deal.data.AsahiDealData;
import com.sabm.core.model.AsahiDealChangeEmailProcessModel;
import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.core.model.AsahiDealModel;


/**
 * Velocity context for asahi deal change notification email.
 */
public class AsahiDealsChangeNotificationEmailContext extends AbstractEmailContext<AsahiDealChangeEmailProcessModel>
{
	/** The asahi configuration service. */
	@Resource
	private AsahiConfigurationService asahiConfigurationService;
	@Resource
	protected DealsService dealsService;
	@Resource(name = "asahiDealDataConverter")
	private Converter<AsahiDealModel, AsahiDealData> asahiDealDataConverter;
	private static final String SGA_DEAL_ADDITIONAL_DETAILS = "sgaadditionaldetails";
	private static final String SGA_AVAILABLE_DEALS = "asahiavailableDealData";
	private static final String SGA_REMOVED_DEALS = "asahiremoveddeals";

	/**
	 * The method initialize the dynamic values for the email template
	 *
	 * @param orderProcessModel
	 * @param emailPageModel
	 */
	@Override
	public void init(final AsahiDealChangeEmailProcessModel asahiDealChangeEmailProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(asahiDealChangeEmailProcessModel, emailPageModel);
		put(DISPLAY_NAME,asahiDealChangeEmailProcessModel.getUserDisplayName());
		put(SGA_DEAL_ADDITIONAL_DETAILS, !StringUtils.isBlank(asahiDealChangeEmailProcessModel.getAdditionalDealDetails())? asahiDealChangeEmailProcessModel.getAdditionalDealDetails():null);
		List<AsahiDealData> activatedDealsData = null;
		List<AsahiDealData> removedDealsData = null;
		if (CollectionUtils.isNotEmpty(asahiDealChangeEmailProcessModel.getActivatedDeals()))
		{
			final List<AsahiDealModel> activatedDealsModels = dealsService
					.getSGADealsForCode(asahiDealChangeEmailProcessModel.getActivatedDeals());
			activatedDealsData = Converters.convertAll(activatedDealsModels, asahiDealDataConverter);
		}
		if (CollectionUtils.isNotEmpty(asahiDealChangeEmailProcessModel.getRemovedDeals()))
		{
			final List<AsahiDealModel> removedDealsModels = dealsService
					.getSGADealsForCode(asahiDealChangeEmailProcessModel.getRemovedDeals());
			removedDealsData = Converters.convertAll(removedDealsModels, asahiDealDataConverter);
		}
		put(EMAIL,asahiDealChangeEmailProcessModel.getToEmails().get(0));
		put(SGA_AVAILABLE_DEALS, activatedDealsData);
		put(SGA_REMOVED_DEALS, removedDealsData);
	}

	/**
	 * Method to get and set Date in String format
	 *
	 * @param deliveryRequestDate
	 * @return date in string format
	 */
	private String setDateFormat(final Date deliveryRequestDate)
	{
		return new SimpleDateFormat(this.asahiConfigurationService.getString(ApbCoreConstants.ASAHI_DATE_FORMAT_KEY,
				ApbCoreConstants.DEFER_DELIVERY_DATEPATTERN)).format(deliveryRequestDate);
	}

	@Override
	protected BaseSiteModel getSite(final AsahiDealChangeEmailProcessModel businessProcessModel)
	{
		// YTODO Auto-generated method stub
		return businessProcessModel.getSite();
	}

	@Override
	protected CustomerModel getCustomer(final AsahiDealChangeEmailProcessModel businessProcessModel)
	{
		// YTODO Auto-generated method stub
		return businessProcessModel.getCustomer();
	}

	@Override
	protected LanguageModel getEmailLanguage(final AsahiDealChangeEmailProcessModel businessProcessModel)
	{
		// YTODO Auto-generated method stub
		return businessProcessModel.getLanguage();
	}


}
