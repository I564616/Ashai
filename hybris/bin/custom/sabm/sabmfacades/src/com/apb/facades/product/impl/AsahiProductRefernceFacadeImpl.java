package com.apb.facades.product.impl;


import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.model.AlcoholTypeModel;
import com.apb.core.model.ApbProductModel;
import com.apb.core.model.BrandModel;
import com.apb.core.model.FlavourModel;
import com.apb.core.model.ItemGroupsModel;
import com.apb.core.model.PackageTypeModel;
import com.apb.core.model.ProdPricingTierModel;
import com.apb.core.model.ProductGroupModel;
import com.apb.core.model.SubProductGroupModel;
import com.apb.core.product.service.ApbProductReferenceService;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.services.ApbCustomerAccountService;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.facades.product.AsahiProductRefernceFacade;
import com.apb.facades.product.data.AlcoholTypeData;
import com.apb.facades.product.data.BrandData;
import com.apb.facades.product.data.FlavourData;
import com.apb.facades.product.data.ItemGroupData;
import com.apb.facades.product.data.PackageTypeData;
import com.apb.facades.product.data.ProductGroupData;
import com.apb.facades.product.data.SubProductGroupData;
import com.apb.integration.data.AsahiProductInfo;
import com.sabmiller.core.model.AsahiB2BUnitModel;


public class AsahiProductRefernceFacadeImpl implements AsahiProductRefernceFacade
{

	private static final Logger LOGGER = LoggerFactory.getLogger(AsahiProductRefernceFacadeImpl.class);
	private static final String RECOMMENDED_PRODUCT_DISPLAY_LIMIT = "recommeded.product.display.limit.";

	/** The apb product reference service. */
	@Resource(name = "apbProductReferenceService")
	private ApbProductReferenceService apbProductReferenceService;

	/** The model service. */
	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "cartService")
	private CartService cartService;

	@Resource(name = "productService")
	private ProductService productService;

	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	@Resource(name = "productVariantFacade")
	private ProductFacade productFacade;

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Resource(name = "cmsSiteService")
	private CMSSiteService cmsSiteService;

	@Resource(name = "customerAccountService")
	private ApbCustomerAccountService customerAccountService;

	public void importProductGroup(final ProductGroupData productGroupData)
	{
		if (null != productGroupData)
		{
			//Getting AlcoholType for the requested code
			final ProductGroupModel existingProductGroup = this.apbProductReferenceService.getProductGroupForCode(productGroupData
					.getCode());

			//Check If AlcoholType exist in hybris for the requested code
			if (null == existingProductGroup)
			{
				//Creating new AlcoholType If AlcoholType does not exist for requested code
				final ProductGroupModel newProductGroup = this.modelService.create(ProductGroupModel.class);
				newProductGroup.setCode(productGroupData.getCode());
				newProductGroup.setName(productGroupData.getName());
				this.modelService.save(newProductGroup);
			}
			else
			{
				existingProductGroup.setName(productGroupData.getName());
				this.modelService.save(existingProductGroup);
			}
		}
	}

	/**
	 * Import alcohol type.
	 *
	 * @param alcoholTypeData
	 *           the alcohol type data
	 */
	@Override
	public void importAlcoholType(final AlcoholTypeData alcoholTypeData)
	{
		if (null != alcoholTypeData)
		{
			//Getting AlcoholType for the requested code
			final AlcoholTypeModel existingAlcoholType = this.apbProductReferenceService.getAlcoholTypeForCode(alcoholTypeData
					.getCode());

			//Check If AlcoholType exist in hybris for the requested code
			if (null == existingAlcoholType)
			{
				//Creating new AlcoholType If AlcoholType does not exist for requested code
				final AlcoholTypeModel newAlcoholType = this.modelService.create(AlcoholTypeModel.class);
				newAlcoholType.setCode(alcoholTypeData.getCode());
				newAlcoholType.setName(alcoholTypeData.getName());
				this.modelService.save(newAlcoholType);
			}
			else
			{
				existingAlcoholType.setName(alcoholTypeData.getName());
				this.modelService.save(existingAlcoholType);
			}
		}
	}

	/**
	 * Import package type.
	 *
	 * @param packageTypeData
	 *           the package type data
	 */
	@Override
	public void importPackageType(final PackageTypeData packageTypeData)
	{
		if (null != packageTypeData)
		{
			//Getting PackageType for the requested code
			final PackageTypeModel existingPackageType = this.apbProductReferenceService.getPackageTypeForCode(packageTypeData
					.getCode());

			//Check If PackageType exist in hybris for the requested code
			if (null == existingPackageType)
			{
				//Creating new PackageType If PackageType does not exist for requested code
				final PackageTypeModel newPackageType = this.modelService.create(PackageTypeModel.class);
				newPackageType.setCode(packageTypeData.getCode());
				newPackageType.setName(packageTypeData.getName());
				this.modelService.save(newPackageType);
			}
			else
			{
				existingPackageType.setName(packageTypeData.getName());
				this.modelService.save(existingPackageType);
			}
		}

	}

	/**
	 * Import flavour.
	 *
	 * @param flavour
	 *           the flavour
	 */
	@Override
	public void importFlavour(final FlavourData flavour)
	{
		if (null != flavour)
		{
			//Getting Flavour for the requested code
			final FlavourModel existingFlavour = this.apbProductReferenceService.getFlavourForCode(flavour.getCode());

			//Check If Flavour exist for the requested code
			if (null == existingFlavour)
			{
				//Creating new Flavour If Flavour does not exist for requested code
				final FlavourModel newFlavour = this.modelService.create(FlavourModel.class);
				newFlavour.setCode(flavour.getCode());
				newFlavour.setName(flavour.getName());
				this.modelService.save(newFlavour);
			}
			else
			{
				existingFlavour.setName(flavour.getName());
				this.modelService.save(existingFlavour);
			}
		}
	}

	/**
	 * Import brand.
	 *
	 * @param brandData
	 *           the brand data
	 */
	@Override
	public void importBrand(final BrandData brandData)
	{
		if (null != brandData)
		{
			//Getting Brand for the requested code
			final BrandModel existingBrand = this.apbProductReferenceService.getBrandForCode(brandData.getCode());

			//Check If Brand exist in hybris for the requested code
			if (null == existingBrand)
			{
				//Creating new Brand If Brand does not exist for requested code
				final BrandModel newBrand = this.modelService.create(BrandModel.class);
				newBrand.setCode(brandData.getCode());
				newBrand.setBackendBrandName(brandData.getName());
				this.modelService.save(newBrand);
			}
			else
			{
				existingBrand.setBackendBrandName(brandData.getName());
				this.modelService.save(existingBrand);
			}
		}
	}

	/**
	 * Import item group.
	 *
	 * @param itemGroupData
	 *           the item group data
	 */
	@Override
	public void importItemGroup(final ItemGroupData itemGroupData)
	{
		if (null != itemGroupData)
		{
			//Getting ItemGroup for the requested code
			final ItemGroupsModel existingItemGroup = this.apbProductReferenceService.getItemGroupForCode(itemGroupData.getCode());

			//Check If ItemGroup exist for the requested code
			if (null == existingItemGroup)
			{
				//Creating new ItemGroup If ItemGroup does not exist for requested code
				final ItemGroupsModel newItemGroup = this.modelService.create(ItemGroupsModel.class);
				newItemGroup.setCode(itemGroupData.getCode());
				newItemGroup.setName(itemGroupData.getName());
				this.modelService.save(newItemGroup);
			}
			else
			{
				existingItemGroup.setName(itemGroupData.getName());
				this.modelService.save(existingItemGroup);
			}
		}
	}

	/**
	 * Import sub product group.
	 *
	 * @param subProductGroupData
	 *           the sub product group data
	 */
	@Override
	public void importSubProductGroup(final SubProductGroupData subProductGroupData)
	{
		if (null != subProductGroupData)
		{
			//Getting subProductGroup for the requested code
			final SubProductGroupModel existingSubProductGroup = this.apbProductReferenceService
					.getSubProductGroupForCode(subProductGroupData.getCode());

			//Check If SubProductGroup exist for the requested code
			if (null == existingSubProductGroup)
			{
				//Creating new SubProductGroup If SubProductGroup does not exist for requested code
				final SubProductGroupModel newSubProductGroup = this.modelService.create(SubProductGroupModel.class);
				newSubProductGroup.setCode(subProductGroupData.getCode());
				newSubProductGroup.setName(subProductGroupData.getName());

				this.modelService.save(newSubProductGroup);
			}
			else
			{
				existingSubProductGroup.setName(subProductGroupData.getName());
				this.modelService.save(existingSubProductGroup);
			}
		}
	}


	/**
	 * The method will fetch the PDP recommended product for the user
	 *
	 * @param code
	 *           the product code
	 * @return list of products
	 */
	@Override
	public List<ProductData> getPDPRecommendedProducts(final String code) {

		List<ProductData> products = getRecommendedProducts();
		if(null!=products && !products.isEmpty()){
			products.removeIf(product -> product.getCode().equalsIgnoreCase(code));
			products=getSortedList(products);
		}

		return products;
	}

	/**
	 * The method will fetch the Cart recommended product for the user
	 *
	 * @param code
	 *           the product code
	 * @return list of products
	 */
	@Override
	public List<ProductData> getCartRecommendedProducts()
	{
		try
		{
   		List<ProductData> products = getRecommendedProducts();

   		if (null != products && !products.isEmpty())
   		{
   			//Sort the Product based on the rank of the product and send top n products...
			if(asahiConfigurationService.getBoolean("sga.recommendation.popup.enabled", false))
			{
				products.sort((p1, p2) -> p1.getRecommendedRank().compareTo(p2.getRecommendedRank()));

				List<ProductData> topList = new ArrayList<ProductData>();

				final int topProducts = asahiConfigurationService.getInt("sga.recommendation.popup.product.list.size",99);

				topList = products.stream().limit(topProducts).collect(Collectors.toList());

				return topList;

			}
   			else{
   				products = getSortedList(products);
			}
   		}
   		return products;
		}catch(final Exception ex){
          LOGGER.error("Exception while fetching recommended products -> ", ex);
          return Collections.emptyList();
		}

	}

	/**
	 * The method will calculate the recommended products for the user
	 *
	 * @return the list of products
	 */
	private List<ProductData> getRecommendedProducts(){

		final Set<ProductModel> exclusionProducts= new HashSet<ProductModel>();
		Set<AsahiProductInfo> inclusionProducts = new HashSet<AsahiProductInfo>();
		Set<AsahiProductInfo> filteredInclusionSet = new HashSet<AsahiProductInfo>();
		final List<ProductData> productDataList = new ArrayList<ProductData>();
		List<ApbProductModel> tieredProdList = new ArrayList<ApbProductModel>();
		ProdPricingTierModel pricingTier =null;
		final List<String> productCodes  = new ArrayList<String>();

		final B2BCustomerModel currentCustomer = (B2BCustomerModel) userService.getCurrentUser();
		final AsahiB2BUnitModel b2bUnit = (AsahiB2BUnitModel)currentCustomer.getDefaultB2BUnit();

		if(null!= b2bUnit.getTier()){
			pricingTier = customerAccountService.getPricingTierProductIds(b2bUnit.getTier());
			if(null!=pricingTier && CollectionUtils.isNotEmpty(pricingTier.getProducts())){
				tieredProdList = pricingTier.getProducts().stream().filter(product->product.getApprovalStatus().equals(ArticleApprovalStatus.APPROVED)).collect(Collectors.toList());
				for(final ApbProductModel product : tieredProdList){
					productCodes.add(product.getCode());
				}
			}
		}

		Set<ApbProductModel> tieredProdListSet = new HashSet<ApbProductModel>();
		final Set<String> finalproductList = new LinkedHashSet<String>();
		final Set<String> productList = new HashSet<String>();

		//Get the inclusion list of user...
		final Map<String, AsahiProductInfo> inclusionList = asahiCoreUtil.getSessionInclusionMap();
		if (null != inclusionList && !inclusionList.isEmpty()) {
			inclusionProducts = inclusionList.entrySet()
					.stream()
					.map(Map.Entry::getValue)
			        .collect(Collectors.toSet());

			//Get the list of previous purchased products...
			final List<OrderModel> orders= this.apbProductReferenceService.getRecommendedProducts(currentCustomer);
			if(null!=orders && !orders.isEmpty()){
				orders.forEach(
						order -> {
							order.getEntries().forEach(
									entry ->{
										exclusionProducts.add(entry.getProduct());
									});
						});
			}

			//Get the cart entries...
			final CartModel cartModel = cartService.getSessionCart();
			final List<AbstractOrderEntryModel> sessionEntries = cartModel.getEntries();
			if(null!=sessionEntries && !sessionEntries.isEmpty()){
				sessionEntries.forEach(
						entries -> {
							exclusionProducts.add(entries.getProduct());
						});
			}

			//Get the Filter list of the included Products...
			if(null!= exclusionProducts && !exclusionProducts.isEmpty()){
				filteredInclusionSet = inclusionProducts.stream()
						.filter(include -> (exclusionProducts.stream()
								.filter(exclude -> exclude.getCode().equalsIgnoreCase(include.getMaterialNumber())).count())<1)
								.collect(Collectors.toSet());

				if(CollectionUtils.isNotEmpty(tieredProdList))
				{
					tieredProdListSet = tieredProdList.stream().filter(tierprod -> (exclusionProducts.stream()
						.filter(exclude -> exclude.getCode().equalsIgnoreCase(tierprod.getCode())).count())<1)
						.collect(Collectors.toSet());
				}

			}else{
				filteredInclusionSet = inclusionProducts;
				if(CollectionUtils.isNotEmpty(tieredProdList))
				{
					tieredProdListSet.addAll(tieredProdList);
				}
			}
			if(CollectionUtils.isNotEmpty(tieredProdListSet)){
				for(final ApbProductModel prodModel : tieredProdListSet){
					for(final AsahiProductInfo prodInfo : filteredInclusionSet)
					{
						if(productCodes.contains(prodInfo.getMaterialNumber())){
							if(prodModel.getCode().equalsIgnoreCase(prodInfo.getMaterialNumber()))
							{
								finalproductList.add(prodModel.getCode());
							}
							else
							{
								productList.add(prodInfo.getMaterialNumber());
							}
						}
					}
				}
				finalproductList.addAll(productList);
				//Get the Product Data...
				finalproductList.forEach(
						filterInclusion -> {
							addFinalRecommendedProductData(productDataList, filterInclusion);
						});
			}
			else{
				//Get the Product Data...
				filteredInclusionSet.forEach(
						filterInclusion -> {
							addFinalRecommendedProductData(productDataList, filterInclusion.getMaterialNumber());
						});
			}

		}

		return productDataList;
	}

	/**
	 * @param productDataList
	 * @param filterInclusion
	 */
	private void addFinalRecommendedProductData(final List<ProductData> productDataList, final String filterInclusion)
	{
		try
		{
			final ProductModel product = productService.getProductForCode(filterInclusion);
			if (product.getApprovalStatus().equals(ArticleApprovalStatus.APPROVED))
			{
				productDataList.add(
						productFacade.getProductForCodeAndOptions(filterInclusion, Arrays.asList(ProductOption.BASIC)));
			}
		}
		catch (final Exception e)
		{
			LOGGER.debug("unable to fetch product with code " + filterInclusion);
			LOGGER.debug(e.getMessage());
		}
	}

	/**
	 * The method will fetch the sorted list based on the product rank
	 *
	 * @param products
	 *           the list of products
	 * @return the sorted list of products
	 */
	private List<ProductData> getSortedList(List<ProductData> products)
	{

		Collections.sort(products, new Comparator<ProductData>()
		{
			@Override
			public int compare(final ProductData ProductData1, final ProductData ProductData2)
			{
				return ProductData1.getRank().compareTo(ProductData2.getRank());
			}
		});

		final int productToDisplay = Integer.parseInt(asahiConfigurationService.getString(RECOMMENDED_PRODUCT_DISPLAY_LIMIT, "5"));
		if (products.size() > productToDisplay)
		{
			products = products.stream().limit(productToDisplay).collect(Collectors.toList());
		}
		return products;
	}
}
