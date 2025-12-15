package com.apb.storefront.controllers.integration;

import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.facades.price.ApbPriceUpdateFacade;
import com.apb.facades.price.PriceInfo;
import com.apb.facades.price.PriceInfoData;
import com.apb.storefront.forms.ProductsWrapper;


/**
 * Class to provide prices from service or session during PLP, category page
 */
@Controller
public class ApbPriceUpdateController
{
	@Resource
	ApbPriceUpdateFacade apbPriceUpdateFacade;

	@Resource(name="asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Resource
	private CMSSiteService cmsSiteService;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	private static final String GET_PLP_PRICE_FROM_EXTERNAL_SYSTEM = "fetch.plp.price.from.service";

	private static final String APB_SITE = "apb";

	/**
	 * Method will fetch the prices for given products for PLP, Category pages
	 *
	 * @param productWrapper
	 * @param request
	 * @param model
	 * @param response
	 * @return map of products
	 */
	@PostMapping("/integration/price")
	@ResponseBody
	public Map<String, PriceInfo> fetchPriceForListingPage(@RequestBody final ProductsWrapper productWrapper, final HttpServletRequest request,final Model model, final HttpServletResponse response)
	{
		
		final Map<String, Long> productQuantityMap = createMapFromProductList(productWrapper.getProducts());
		if (isFetchPriceFromService() && !asahiSiteUtil.isSga()) {
			return createProductPriceMap(apbPriceUpdateFacade.updatePriceInfoData(productQuantityMap, false));
		}
		if (cmsSiteService.getCurrentSite().getUid().equals(APB_SITE)) {
			model.addAttribute("apbSite", true);
		}

		if (asahiSiteUtil.isSga() && !asahiCoreUtil.isNAPUser()) {
			final Set<String> productIds = productQuantityMap.keySet();
			return apbPriceUpdateFacade.getPriceMapFromSession(productIds);
			}
		return null;

	}

	private Map<String, PriceInfo> createProductPriceMap(final PriceInfoData priceData)
	{
		final Map<String, PriceInfo> productPriceMap = new HashMap<>();
		if(null != priceData && null != priceData.getProductPriceInfo()){
			for(final PriceInfo productPrice : priceData.getProductPriceInfo())
			{
				productPriceMap.put(productPrice.getCode(), productPrice);
			}
			return productPriceMap;
		}
		return null;
	}

	private Map<String, Long> createMapFromProductList(final List<String> products) {
		final Map<String, Long> productQuantityMap = new HashMap<>();
		for(final String productCode : products){
			productQuantityMap.put(productCode, 1L);
		}
		return productQuantityMap;
	}

	private boolean isFetchPriceFromService() {
		return Boolean.parseBoolean(asahiConfigurationService.getString(GET_PLP_PRICE_FROM_EXTERNAL_SYSTEM,"true"));
	}

}
