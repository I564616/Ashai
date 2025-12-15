/**
 *
 */
package com.sabmiller.storefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.util.Config;

import java.util.Date;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.util.SabmUtils;
import com.sabmiller.facades.customer.SABMCustomerFacade;
import com.sabmiller.facades.deal.SABMDealsSearchFacade;
import com.sabmiller.facades.deal.data.DealJson;
import com.sabmiller.storefront.controllers.SABMWebConstants;
import com.sabmiller.storefront.controllers.pages.SabmAbstractPageController;

import com.apb.core.util.AsahiCoreUtil;


/**
 * The Class DealsPageController.
 *
 * @author xiaowu.a.zhang
 *
 *         Controller for deals page
 */
@Controller
@Scope("tenant")
@RequestMapping(value = "/deals")
public class DealsPageController extends SabmAbstractPageController
{

	/** The Constant DEALS_CMS_PAGE. */
	private static final String DEALS_CMS_PAGE = "deals";

	/** The Constant NO_DEALS_AVAILABLE. */
	private static final String NO_DEALS_AVAILABLE = "noDealsAvailable";

	/** The Constant VISITED_DEALS_PAGE. */
	private static final String VISITED_DEALS_PAGE = "visitedDealsPage";

	/** The sabm deals search facade. */
	@Resource(name = "sabmDealsSearchFacade")
	private SABMDealsSearchFacade sabmDealsSearchFacade;
	/** The customer facade. */
	@Resource(name = "customerFacade")
	private SABMCustomerFacade customerFacade;
	/** The session service. */
	@Resource(name = "sessionService")
	private SessionService sessionService;
	
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
	 * @throws InterruptedException 
	 */
	@GetMapping
	public String getDeals(final Model model) throws CMSItemNotFoundException, InterruptedException
	{
		if (asahiCoreUtil.isNAPUser()) {
			return FORWARD_PREFIX + "/404";
		}
		
		if(this.isDealRefreshInProgress()){
			Thread.sleep(Config.getLong("direct.dealpage.redirect.delay", 4000));
		}
		final Date currentDeliveryDate = (Date) sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE);
		
		final List<DealJson> deals = sabmDealsSearchFacade.searchDeals(currentDeliveryDate, Boolean.TRUE);

		sessionService.setAttribute(VISITED_DEALS_PAGE, Boolean.TRUE);
		if (CollectionUtils.isEmpty(deals))
		{
			storeCmsPageInModel(model, getContentPageForLabelOrId(NO_DEALS_AVAILABLE));
			setUpMetaDataForContentPage(model, getContentPageForLabelOrId(NO_DEALS_AVAILABLE));
		}
		else
		{
			model.addAttribute("deals", deals);
			storeCmsPageInModel(model, getContentPageForLabelOrId(DEALS_CMS_PAGE));
			setUpMetaDataForContentPage(model, getContentPageForLabelOrId(DEALS_CMS_PAGE));
		}
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.INDEX_NOFOLLOW);
		model.addAttribute("pageType", SABMWebConstants.PageType.DEAL.name());

		return getViewForPage(model);
	}

	/**
	 * Checks if is deal refresh in progress.
	 *
	 * @return true, if is deal refresh in progress
	 */
	@ModelAttribute("dealsCallInProgress")
	public boolean isDealRefreshInProgress()
	{
		return customerFacade.isDealRefreshInProgress();
	}

	@ModelAttribute("requestOrigin")
	protected String populateRequestOrigin(HttpServletRequest request) {
		return SabmUtils.getRequestOrigin(request.getHeader(SabmUtils.REFERER_KEY), SabmUtils.HOME);
	}
	
}
