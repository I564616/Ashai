package com.apb.occ.v2.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.integration.data.AsahiCustomerAccountCheckRequestData;
import com.apb.integration.data.AsahiCustomerAccountCheckResponseData;
import com.apb.integration.data.AsahiProductInfo;

@RestController
@RequestMapping(value = "/{baseSiteId}/logincustomeraccountcheck")
public class AsahiMockLoginCustomerAccountCheckController {
	
	@Resource
	private AsahiConfigurationService asahiConfigurationService;

	@ResponseBody
	@RequestMapping(value = "/getCreditAndInclusionList", method = RequestMethod.POST, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public AsahiCustomerAccountCheckResponseData getCreditAndInclusionList(@RequestBody final AsahiCustomerAccountCheckRequestData request){
		final AsahiCustomerAccountCheckResponseData response = new AsahiCustomerAccountCheckResponseData();
		
		final Boolean isBlocked = asahiConfigurationService.getBoolean("integration.credit.check.account.blocked", false);
		response.setIsBlocked(isBlocked);
		
		final List<AsahiProductInfo> inclusionProducts = new ArrayList<AsahiProductInfo>();
		final List<String> includeProductList =Arrays.asList(asahiConfigurationService.getString("integration.inclusion.product.id", "10000838").split("\\s*,\\s*"))  ;

		includeProductList.forEach(
				productId -> {
					final AsahiProductInfo inclusionProduct = new AsahiProductInfo();
					inclusionProduct.setMaterialNumber(productId);
					inclusionProduct.setIsPromoFlag(true);
					inclusionProduct.setIsExcluded(false);
					inclusionProduct.setListPrice(100.0);
					inclusionProduct.setPromoText("You will get 10% off");
					inclusionProducts.add(inclusionProduct);
				});
		
		
		final List<AsahiProductInfo> exclusionProducts = new ArrayList<AsahiProductInfo>();
		final List<String> exclusionProductList =Arrays.asList(asahiConfigurationService.getString("integration.exclusion.product.id", "10000840").split("\\s*,\\s*"))  ;
		
		exclusionProductList.forEach(
				productId -> {
					final AsahiProductInfo exclusionProduct = new AsahiProductInfo();
					exclusionProduct.setMaterialNumber(productId);
					exclusionProduct.setIsPromoFlag(true);
					exclusionProduct.setIsExcluded(true);
					exclusionProduct.setListPrice(100.0);
					exclusionProduct.setPromoText("You will get 10% off");
					exclusionProducts.add(exclusionProduct);
				});
		
		
		final List<AsahiProductInfo> products = new ArrayList<>();
		products.addAll(inclusionProducts);
		products.addAll(exclusionProducts);
		response.setItems(products);
	
		return response;
	}
}
