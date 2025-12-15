/**
 *
 */
package com.sabmiller.storefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.servicelayer.session.SessionService;

import jakarta.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.apb.core.util.AsahiCoreUtil;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.facades.order.SABMOrderFacade;
import com.sabmiller.facades.smartOrders.json.SmartOrdersJson;
import com.sabmiller.storefront.controllers.SABMWebConstants;
import com.sabmiller.storefront.controllers.pages.SabmAbstractPageController;



/**
 * The Class DealsPageController.
 *
 * @author madhu.c.dasari
 *
 *         Controller for Smart Orders page
 */
@Controller
@Scope("tenant")
public class SmartOrdersController extends SabmAbstractPageController
{

	/** The Constant DEALS_CMS_PAGE. */
	private static final String SMARTORDERS_CMS_PAGE = "smartOrders";

	@Resource(name = "orderFacade")
	private SABMOrderFacade orderFacade;

	/** The session service. */
	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "simpleBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder resourceBreadcrumbBuilder;
	
   @Resource
   private AsahiCoreUtil asahiCoreUtil;

	/**
	 * Gets the deals.
	 *
	 * @param model
	 *           the model
	 * @return the deals
	 * @throws CMSItemNotFoundException
	 *            the CMS item not found exception
	 */
	@RequestMapping(value = "/smartOrders", method =
	{ RequestMethod.GET, RequestMethod.POST })
	public String getSmartOrders(final Model model) throws CMSItemNotFoundException
	{
		
		if (asahiCoreUtil.isNAPUser()) {
			return FORWARD_PREFIX + "/404";
		}
		
		SmartOrdersJson smartOrdersJson;
		sessionService.setAttribute("smartOrderPage", true);
		if (null != sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_SMARTORDERJSON))
		{
			smartOrdersJson = sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_SMARTORDERJSON);
		}
		else
		{
			smartOrdersJson = orderFacade.smartOrdersJson(null, null);
		}
		model.addAttribute("smartOrders", smartOrdersJson);
		storeCmsPageInModel(model, getContentPageForLabelOrId(SMARTORDERS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(SMARTORDERS_CMS_PAGE));

		model.addAttribute(WebConstants.BREADCRUMBS_KEY, resourceBreadcrumbBuilder.getBreadcrumbs("breadcrumb.smartorder"));

		model.addAttribute("pageType", SABMWebConstants.PageType.SMART_ORDERS.name());
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return getViewForPage(model);
	}

	@RequestMapping(value = "/smartOrdersAjax", method =
	{ RequestMethod.GET, RequestMethod.POST })
	@RequireHardLogIn
	@ResponseBody
	public SmartOrdersJson getSmartOrdersJson(@RequestParam(value = "date", required = false) final String date,
			@RequestParam(value = "sort", required = false) final String sort) throws CMSItemNotFoundException
	{
		return orderFacade.smartOrdersJson(date, sort);
	}

}
