package com.apb.storefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.b2bunit.ApbB2BUnitFacade;
import com.sabmiller.facades.cart.SABMCartFacade;
import com.sabmiller.facades.customer.SABMCustomerFacade;
import com.apb.service.b2bunit.ApbB2BUnitService;
import com.apb.storefront.constant.ApbStoreFrontContants;


/**
 * Controller for Multi Account page
 */
@Controller
@RequestMapping(value = "/multiAccount")
public class AsahiMultiAccountPageController extends AbstractSearchPageController
{
	private static final Logger LOG = Logger.getLogger(AsahiMultiAccountPageController.class);
	private static final String ASAHI_MULTI_ACCOUNT_PAGE_ID = "multiAccount";
	private static final String REDIRECT_CONFIRMATION_PAGE = "/register/confirmation/?firstName=";
	private static final String REDIRECT_TO_HOME_PAGE = REDIRECT_PREFIX + ROOT;
	private static final String MULTI_ACCOUNT_LIST_SIZE = "multi.account.list.size.";

	@Resource(name = "userService")
	private UserService userService;

	@Autowired
	private SABMCustomerFacade sabmCustomerFacade;
	/** The asahi configuration service. */
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Resource(name = "cartFacade")
	private SABMCartFacade sabmCartFacade;

	@Resource
	private ApbB2BUnitFacade apbB2BUnitFacade;

	@Resource
	private ApbB2BUnitService apbB2BUnitService;
	
	@Resource
	private AsahiCoreUtil asahiCoreUtil;
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	
	@Resource
	private SessionService sessionService;


	/**
	 * Method will return total cart items count
	 *
	 * @return
	 */
	@ModelAttribute("totalItemsInCart")
	public int getTotalCartItems()
	{
		return sabmCartFacade.hasSessionCart() ? sabmCartFacade.getSessionCart().getTotalUnitCount().intValue() : 0;
	}

	/**
	 * @param model
	 * @param redirectModel
	 * @param request
	 * @return
	 * @throws CMSItemNotFoundException
	 */
	@GetMapping
	public String multiAccountView(@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode, final Model model,
			final RedirectAttributes redirectModel, final HttpServletRequest request) throws CMSItemNotFoundException
	{

		getUnitDisplayDetails(model,request);
		
		final PageableData pageableData = createPageableData(page, Integer.parseInt(this.asahiConfigurationService
				.getString(MULTI_ACCOUNT_LIST_SIZE + getCmsSiteService().getCurrentSite().getUid(), "10")), sortCode, showMode);
		final SearchPageData<B2BUnitData> searchPageData = this.sabmCustomerFacade.getPagedMultiAccounts(pageableData);
		populateModel(model, searchPageData, showMode);
		this.sabmCustomerFacade.sortMultiAccountResults(searchPageData);
		this.sabmCustomerFacade.retrictResultsPerPage(searchPageData);

		//check if the customer logged in for first time...
		model.addAttribute("firstTimeLoggedIn", this.sabmCustomerFacade.getCurrentCustomer().getLoggedInBefore());

		if (null != searchPageData && CollectionUtils.isNotEmpty(searchPageData.getResults()))
		{

			if ((getContentPageForLabelOrId(ASAHI_MULTI_ACCOUNT_PAGE_ID)).getBackgroundImage() != null)
			{
				model.addAttribute("media", (getContentPageForLabelOrId(ASAHI_MULTI_ACCOUNT_PAGE_ID)).getBackgroundImage().getURL());
			}
			storeCmsPageInModel(model, getContentPageForLabelOrId(ASAHI_MULTI_ACCOUNT_PAGE_ID));
			setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ASAHI_MULTI_ACCOUNT_PAGE_ID));
			updatePageTitle(model, getContentPageForLabelOrId(ASAHI_MULTI_ACCOUNT_PAGE_ID));

			
			return getViewForPage(model);

		}

		//By Default return to home page..
		return REDIRECT_TO_HOME_PAGE;
	}

	private void getUnitDisplayDetails(Model model, HttpServletRequest request)
	{
		final Boolean isDefaultDisabled = this.sessionService.getAttribute(ApbStoreFrontContants.DEFAULT_UNIT_DISABLED_FLAG);
		if (null != isDefaultDisabled && isDefaultDisabled)
		{
			model.addAttribute("isDefaultDisabled",isDefaultDisabled);
			sabmCartFacade.removeSessionCart();
			this.sessionService.setAttribute(ApbStoreFrontContants.DEFAULT_UNIT_DISABLED_FLAG, Boolean.FALSE);
		}
		
		model.addAttribute("isDefaultUnitBelongsCurrSite",sessionService.getAttribute(ApbStoreFrontContants.IS_DEFAULT_UNIT_BELONGS_TO_CURRENT_SITE));
		final String otherSiteUrl = this.asahiConfigurationService.getConfiguration().getString("website." + (asahiSiteUtil.getCurrentSite().getUid().equalsIgnoreCase("sga") ? "apb" : "sga") + ".https");
		model.addAttribute("otherSiteUrl",otherSiteUrl);
		asahiCoreUtil.setMultiAccountDisplayLink(request);
	}


	/**
	 * Make b2bunit as default for current customer
	 *
	 * @param b2bUnitId
	 *           b2bunit uid
	 * @param request request
	 * @return boolean
	 */
	@PostMapping(value = "/updateB2bUnit", produces = "application/json")
	@ResponseBody
	public String updateB2bUnit(@RequestParam("b2bUnitId") final String b2bUnitId, final HttpServletRequest request)
	{
		String returnValue = "false";
		if (apbB2BUnitFacade.setCurrentUnit(b2bUnitId))
		{
			sessionService.setAttribute(ApbStoreFrontContants.IS_DEFAULT_UNIT_BELONGS_TO_CURRENT_SITE,Boolean.TRUE);
			sabmCartFacade.removeSessionCart();
			returnValue = "true";
			request.getSession().setAttribute("makeLoginCall", true);
			sessionService.setAttribute(ApbCoreConstants.EXCLUDED_CATEGORY_RECALCULATION, Boolean.TRUE);
			if(asahiSiteUtil.isSga() && request.getSession().getAttribute("multiAccountSelfRegistration") != null && (Boolean) request.getSession().getAttribute("multiAccountSelfRegistration")) {
				final UserModel userModel = this.userService.getCurrentUser();
				return REDIRECT_CONFIRMATION_PAGE + userModel.getName();
			}
		}
		return returnValue;
	}
	
	/**
	 * Clear user session
	 *
	 * @return boolean
	 */
	@GetMapping(value = "/exitCustomer", produces = "application/json")
	@ResponseBody
	public Boolean exitCustomer(final HttpServletRequest request)
	{
		this.getSessionService().closeCurrentSession();
		return Boolean.TRUE;
	}

	protected void updatePageTitle(final Model model, final AbstractPageModel cmsPage)
	{
		storeContentPageTitleInModel(model, getPageTitleResolver().resolveContentPageTitle(cmsPage.getTitle()));
	}
}
