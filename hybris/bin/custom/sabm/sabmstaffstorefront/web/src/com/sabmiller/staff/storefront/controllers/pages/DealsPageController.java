/**
 *
 */
package com.sabmiller.staff.storefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.facades.customer.SABMCustomerFacade;
import com.sabmiller.facades.deal.SABMDealsSearchFacade;
import com.sabmiller.facades.deal.SABMRepDrivenDealConditionStatusFacade;
import com.sabmiller.facades.deal.data.DealJson;
import com.sabmiller.staff.storefront.form.SABMChangesDealForm;


/**
 * @author xiaowu.a.zhang
 *
 *         Controller for deals page
 */
@Controller
@Scope("tenant")
@RequestMapping(value = "/deals")
public class DealsPageController extends AbstractStaffSearchPageController
{

	private static final Logger LOG = LoggerFactory.getLogger(DealsPageController.class);

	private static final String SPECIFIC_DEALS_CMS_PAGE = "specificDeals";
	private static final String BREADCRUMBS_CUSTOMER_SEARCH_TEXT_KEY = "text.staff.portal.customer.search.breadcrumb";
	private static final String BREADCRUMBS_SEARCH_RESULT_TEXT_KEY = "staff.portal.customer.searchResults.breadcrumb";
	private static final String BREADCRUMBS_DEALS = "staff.portal.customer.deals.breadcrumb";
	private static final String SESSION_CHANGED_DEAL_STATUS_DATA = "changedDealStatusData";

	@Resource(name = "sabmDealsSearchFacade")
	private SABMDealsSearchFacade dealsSearchFacade;

	@Resource(name = "customerFacade")
	private SABMCustomerFacade customerFacade;

	@Resource(name = "sabmRepDrivenDealConditionStatusFacade")
	private SABMRepDrivenDealConditionStatusFacade dealConditionStatusFacade;

	@GetMapping("specific/{uid:.*}")
	@RequireHardLogIn
	public String getSpecificDeals(final Model model, @PathVariable("uid") final String uid) throws CMSItemNotFoundException
	{
		if (StringUtils.isNotEmpty(uid))
		{
			final B2BUnitModel b2bUnit = customerFacade.getB2BUnitForId(uid);

			if (null != b2bUnit)
			{
				getSessionService().setAttribute(SabmCoreConstants.SESSION_SELECT_B2BUNIT_UID_DATA, b2bUnit.getUid());
				final List<DealJson> deals = dealsSearchFacade.getSpecificDeals(b2bUnit, true);
				model.addAttribute("b2bUnitData", customerFacade.getB2BUnitForUnitModel(b2bUnit));
				model.addAttribute("specificDeals", deals);
				//add go back link for mobile
				model.addAttribute("backUrl", "/backToCustomerSearchResults");

			}
			else
			{
				return FORWARD_PREFIX + "/404";
			}
		}

		final List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();
		breadcrumbs.add(new Breadcrumb("/customer-search",
				getMessageSource().getMessage(BREADCRUMBS_CUSTOMER_SEARCH_TEXT_KEY, null, getI18nService().getCurrentLocale()),
				null));
		breadcrumbs.add(new Breadcrumb("/backToCustomerSearchResults",
				getMessageSource().getMessage(BREADCRUMBS_SEARCH_RESULT_TEXT_KEY, null, getI18nService().getCurrentLocale()), null));
		breadcrumbs.add(new Breadcrumb("#",
				getMessageSource().getMessage(BREADCRUMBS_DEALS, null, getI18nService().getCurrentLocale()), null));

		model.addAttribute("breadcrumbs", breadcrumbs);
		storeCmsPageInModel(model, getContentPageForLabelOrId(SPECIFIC_DEALS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(SPECIFIC_DEALS_CMS_PAGE));

		return getViewForPage(model);
	}

	@PostMapping(value = "/addChanges", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String, Object> addChangesDeal(@Valid @RequestBody final SABMChangesDealForm form) throws CMSItemNotFoundException
	{
		final Map<String, Object> result = Maps.newHashMap();
		result.put("code", "0");
		if (CollectionUtils.isNotEmpty(form.getConditions()) && StringUtils.isNotEmpty(form.getUid()))
		{
			try
			{
				getSessionService().setAttribute(SESSION_CHANGED_DEAL_STATUS_DATA, form);

				if (form.getSaveChanges())
				{
					dealConditionStatusFacade.saveRepDrivenDealConditionStatus(form.getUid(), form.getConditions());
				}

				result.put("code", "1");
			}
			catch (final Exception e)
			{
				result.put("message", getMessageSource().getMessage("staff.portal.deals.changes.save.error", null,
						getI18nService().getCurrentLocale()));
				LOG.error("Save Customer[" + form.getUid() + "] RepDrivenDealCondition failure.", e);
			}
		}
		return result;
	}
}
