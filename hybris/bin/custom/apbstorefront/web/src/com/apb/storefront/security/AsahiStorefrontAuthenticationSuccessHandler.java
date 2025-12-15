package com.apb.storefront.security;

import de.hybris.platform.acceleratorstorefrontcommons.security.StorefrontAuthenticationSuccessHandler;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;

import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.facades.cart.SABMCartFacade;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.service.b2bunit.ApbB2BUnitService;


/**
 * Custom Asahi Authentication Succes Handler
 */
public class AsahiStorefrontAuthenticationSuccessHandler extends StorefrontAuthenticationSuccessHandler
{
	private static final String REDIRECT_TO_MULTI_ACCOUNT_PAGE = "/multiAccount";

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "apbB2BUnitService")
	private ApbB2BUnitService apbB2BUnitService;

	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	@Resource(name = "cartFacade")
	private SABMCartFacade sabmCartFacade;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	/*
	 * This Method will identify the target url
	 *
	 * @param request
	 *
	 * @param response
	 *
	 * @return`target url
	 */
	@Override
	protected String determineTargetUrl(final HttpServletRequest request, final HttpServletResponse response)
	{
		String targetUrl = super.determineTargetUrl(request, response);
		//List<AsahiB2BUnitModel> siteSpecificList = new ArrayList<>();
		//final List<AsahiB2BUnitModel> activeUnits = new ArrayList<>();

		// Get the B2Bunits of current customer
		final  Map<String,List<AsahiB2BUnitModel>> b2bUnits = apbB2BUnitService
				.getUserActiveB2BUnits(this.userService.getCurrentUser().getUid());

		/*b2bUnits.stream().forEach(unit -> {
			activeUnits.add((AsahiB2BUnitModel) unit);
		});*/

		//siteSpecificList = asahiCoreUtil.getSiteBasedUnits(activeUnits);
		final String currentSite = asahiSiteUtil.getCurrentSite().getUid();
		if (CollectionUtils.isNotEmpty(b2bUnits.get(currentSite)) && b2bUnits.get(currentSite).size() > 1)
		{
			targetUrl = REDIRECT_TO_MULTI_ACCOUNT_PAGE;
		}

		request.getSession().setAttribute("makeLoginCall", true);
		return targetUrl;
	}
}
