package com.apb.facades.populators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;

import com.apb.core.model.AlcoholTypeModel;
import com.apb.core.model.ApbProductModel;
import com.apb.core.model.BrandModel;
import com.apb.core.model.FlavourModel;
import com.apb.core.model.ItemGroupsModel;
import com.apb.core.model.PackageTypeModel;
import com.apb.core.product.service.ApbProductReferenceService;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.facades.product.data.AlcoholTypeData;
import com.apb.facades.product.data.BrandData;
import com.apb.facades.product.data.FlavourData;
import com.apb.facades.product.data.ItemGroupData;
import com.apb.facades.product.data.PackageTypeData;

import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;

/**
 * The Class ApbProductBasicReversePopulator.
 * 
 * Kuldeep.Singh1
 */
public class ApbProductBasicReversePopulator implements Populator<ProductData,ApbProductModel>
{
	
	/** The Constant PRICE_UNIT. */
	protected static final String PRICE_UNIT = "pieces";
	
	/** The Constant CODE_COMPANY_CATALOG_ID. */
	private static final String CODE_COMPANY_CATALOG_ID = ".company.catalog.id";
	
	/** The Constant CODE_COMPANY_CATALOG_VERSION. */
	private static final String CODE_COMPANY_CATALOG_VERSION = ".company.catalog.version.staged";
	
	/** The Constant DEFAULT_COMPANY_CODE. */
	private static final String DEFAULT_COMPANY_CODE = "apb";
	
	/** The catalog service. */
	@Resource(name="catalogService")
	private CatalogService catalogService;
	
	/** The catalog version service. */
	@Resource(name="catalogVersionService")
	private CatalogVersionService catalogVersionService;
	
	/** The category service. */
	@Resource(name="categoryService")
	private CategoryService categoryService;;
	
	/** The apb product reference service. */
	@Resource(name="apbProductReferenceService")
	private ApbProductReferenceService apbProductReferenceService;
	
	/** The model service. */
	@Resource(name = "modelService")
	private ModelService modelService;
	
	@Resource(name="sessionService")
	private SessionService sessionService; 
	
	
	/** The asahi configuration service. */
	@Resource(name="asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;
	/**
	 * Populate.
	 *
	 * @param productData the product data
	 * @param productModel the product model
	 * @throws ConversionException the conversion exception
	 */
	public void populate(ProductData productData, ApbProductModel productModel)
			throws ConversionException {
		
		//setting basic product attributes
		this.populatingBasicAttributes(productData,productModel);
		
		//setting product catalogName and CatalogVersion
		StringBuilder catalogConfigKeyWithCompanyCode = new StringBuilder(productData.getCompanyCode().toLowerCase()).append(CODE_COMPANY_CATALOG_ID);
		StringBuilder catalogVersionConfigKeyWithCompanyCode = new StringBuilder(productData.getCompanyCode()).append(CODE_COMPANY_CATALOG_VERSION);
		
		productModel.setCatalogVersion(this.catalogVersionService.getCatalogVersion(this.asahiConfigurationService.getString(catalogConfigKeyWithCompanyCode.toString(), "apbProductCatalog"), this.asahiConfigurationService.getString(catalogVersionConfigKeyWithCompanyCode.toString(), "Staged")));
		
		//setting product category attributes
		if(DEFAULT_COMPANY_CODE.equalsIgnoreCase(productData.getCompanyCode())){
			this.populatingCategoryAttributes(productData,productModel,productModel.getCatalogVersion());
			
			//setting product brand attributes
			this.populatingBrandAttributes(productData.getApbBrand(),productModel);
			
			//setting product package attributes
			this.populatingPackageAttributes(productData.getPackageType(),productModel);
		}
		
		//setting product pricing attributes
		this.populatingPriceAttributes(productData,productModel);
		
		//setting product alcohol attributes
		this.populatingAlcoholAttributes(productData.getAlcoholType(),productModel);
		
		//setting product flavour attributes
		this.populatingFlavourAttributes(productData.getFlavour(),productModel);
		
		//setting Item group attributes
		this.populatingItemGroupAttributes(productData.getItemGroup(),productModel);
		
	}

	
	/**
	 * Populating flavour attributes.
	 *
	 * @param flavour the flavour
	 * @param productModel the product model
	 */
	private void populatingFlavourAttributes(FlavourData flavour,
			ApbProductModel productModel) {
		
		if(null!=flavour && null!=flavour.getCode()){
			//Getting Flavour for the requested code
			FlavourModel existingFlavour = this.apbProductReferenceService.getFlavourForCode(flavour.getCode());
			
			//Check If Flavour exist for the requested code
			if(null==existingFlavour){
				//Creating new Flavour If Flavour does not exist for requested code
				FlavourModel newFlavour = this.modelService.create(FlavourModel.class);
				newFlavour.setCode(flavour.getCode());
				newFlavour.setName(flavour.getName());
				this.modelService.save(newFlavour);
				productModel.setFlavour(newFlavour);
			}else{
				existingFlavour.setName(flavour.getName());
				this.modelService.save(existingFlavour);
				productModel.setFlavour(existingFlavour);
			}
		}else{
			productModel.setFlavour(null);
		}
	}

	/**
	 * Populating product group attributes.
	 *
	 * @param itemGroupData the product data
	 * @param productModel the product model
	 */
	private void populatingItemGroupAttributes(ItemGroupData itemGroupData,
			ApbProductModel productModel) {
		
		if(null!=itemGroupData && null!=itemGroupData.getCode()){
			//Getting ItemGroup for the requested code
			ItemGroupsModel existingItemGroup = this.apbProductReferenceService.getItemGroupForCode(itemGroupData.getCode());
			
			//Check If ItemGroup exist for the requested code
			if(null==existingItemGroup){
				//Creating new ItemGroup If ItemGroup does not exist for requested code
				ItemGroupsModel newItemGroup = this.modelService.create(ItemGroupsModel.class);
				newItemGroup.setCode(itemGroupData.getCode());
				newItemGroup.setName(itemGroupData.getName());
				this.modelService.save(newItemGroup);
				productModel.setItemGroup(newItemGroup);
			}else{
				existingItemGroup.setName(itemGroupData.getName());
				this.modelService.save(existingItemGroup);
				productModel.setItemGroup(existingItemGroup);
			}
		}else{
			productModel.setItemGroup(null);
		}
	}

	/**
	 * Populating alcohol attributes.
	 *
	 * @param alcoholTypeData the product data
	 * @param productModel the product model
	 */
	private void populatingAlcoholAttributes(AlcoholTypeData alcoholTypeData,
			ApbProductModel productModel) {
		
		if(null!=alcoholTypeData && null!= alcoholTypeData.getCode()){
			//Getting AlcoholType for the requested code
			AlcoholTypeModel existingAlcoholType = this.apbProductReferenceService.getAlcoholTypeForCode(alcoholTypeData.getCode());
			
			//Check If AlcoholType exist in hybris for the requested code
			if(null==existingAlcoholType){
				//Creating new AlcoholType If AlcoholType does not exist for requested code
				AlcoholTypeModel newAlcoholType = this.modelService.create(AlcoholTypeModel.class);
				newAlcoholType.setCode(alcoholTypeData.getCode());
				newAlcoholType.setName(alcoholTypeData.getName());
				this.modelService.save(newAlcoholType);
				productModel.setAlcoholType(newAlcoholType);
			}else{
				existingAlcoholType.setName(alcoholTypeData.getName());
				this.modelService.save(existingAlcoholType);
				productModel.setAlcoholType(existingAlcoholType);
			}
		}else{
			productModel.setAlcoholType(null);
		}
	}

	/**
	 * Populating package attributes.
	 *
	 * @param packageTypeData the product data
	 * @param productModel the product model
	 */
	private void populatingPackageAttributes(PackageTypeData packageTypeData,
			ApbProductModel productModel) {
		
		if(null!=packageTypeData && null!=packageTypeData.getCode()){
			//Getting PackageType for the requested code
			PackageTypeModel existingPackageType = this.apbProductReferenceService.getPackageTypeForCode(packageTypeData.getCode());
			
			//Check If PackageType exist in hybris for the requested code
			if(null==existingPackageType){
				//Creating new PackageType If PackageType does not exist for requested code
				PackageTypeModel newPackageType = this.modelService.create(PackageTypeModel.class);
				newPackageType.setCode(packageTypeData.getCode());
				newPackageType.setName(packageTypeData.getName());
				this.modelService.save(newPackageType);
				productModel.setPackageType(newPackageType);
			}else{
				existingPackageType.setName(packageTypeData.getName());
				this.modelService.save(existingPackageType);
				productModel.setPackageType(existingPackageType);
			}
		}else{
			productModel.setPackageType(null);
		}
	}

	/**
	 * Populating brand attributes.
	 *
	 * @param brandData the product data
	 * @param productModel the product model
	 */
	private void populatingBrandAttributes(BrandData brandData,
			ApbProductModel productModel) {
		if(null!=brandData && null!= brandData.getCode()){
			//Getting Brand for the requested code
			BrandModel existingBrand = this.apbProductReferenceService.getBrandForCode(brandData.getCode());
			
			//Check If Brand exist in hybris for the requested code
			if(null==existingBrand){
				//Creating new Brand If Brand does not exist for requested code
				BrandModel newBrand = this.modelService.create(BrandModel.class);
				newBrand.setCode(brandData.getCode());
				newBrand.setBackendBrandName(brandData.getName());
				this.modelService.save(newBrand);
				productModel.setBrand(newBrand);
			}else{
				existingBrand.setBackendBrandName(brandData.getName());
				this.modelService.save(existingBrand);
				productModel.setBrand(existingBrand);
			}
		}else{
			productModel.setBrand(null);
		}
	}

	/**
	 * Populating price attributes.
	 *
	 * @param productData the product data
	 * @param productModel the product model
	 */
	private void populatingPriceAttributes(ProductData productData,
			ApbProductModel productModel) {
		
		Collection<PriceRowModel> prices = new ArrayList<PriceRowModel>();
		PriceRowModel price = new PriceRowModel();
		
		if(null!=productData.getPrice()){
			price.setPrice(productData.getPrice().getValue().doubleValue());
			CurrencyModel currency = this.apbProductReferenceService.getCurrencyForIsoCode(productData.getPrice().getCurrencyIso());
			price.setUnit(this.apbProductReferenceService.getUnitForCode(PRICE_UNIT));
			price.setCurrency(currency);
		}
		
		prices.add(price);
		productModel.setEurope1Prices(prices);
	}

	/**
	 * Populating category attributes.
	 *
	 * @param productData the product data
	 * @param productModel the product model
	 */
	private void populatingCategoryAttributes(ProductData productData,
			ApbProductModel productModel,CatalogVersionModel catalogVersion) {
		
		Collection<CategoryData> categoryDataList = productData.getCategories();
		List<CategoryModel> categories = new ArrayList<CategoryModel>();
		if(CollectionUtils.isNotEmpty(categoryDataList)){

			categories = sessionService.executeInLocalView(new SessionExecutionBody() {
				@Override
				public Object execute() {
					List<CategoryModel> stagedCategories = new ArrayList<CategoryModel>();
					catalogVersionService.setSessionCatalogVersions(Collections.singleton(catalogVersion));
					for(CategoryData category :categoryDataList){
						stagedCategories.add(categoryService.getCategoryForCode(catalogVersion, category.getCode()));
					}
					return stagedCategories;
				}
			}); 

		}
		productModel.setSupercategories(categories);
	}

	/**
	 * Populating basic attributes.
	 *
	 * @param productData the product data
	 * @param productModel the product model
	 */
	private void populatingBasicAttributes(ProductData productData,
			ApbProductModel productModel) {
		
		productModel.setCompanyCode(productData.getCompanyCode());
		productModel.setCode(productData.getCode());
		productModel.setBackendName(productData.getBackendName());
		productModel.setAlcoholPercent(productData.getAlcoholPercent());
		productModel.setUnitPerInner(productData.getUnitPerInner());
		productModel.setQtyPerLayer(productData.getQtyPerLayer());
		productModel.setStandardPalletQuantity(productData.getStandardPalletQuantity());
		productModel.setDepth(productData.getDepth());
		productModel.setHeight(productData.getApbHeight());
		productModel.setWidth(productData.getApbWidth());
		productModel.setBackendUnitPerCase(productData.getBackendUnitPerCase());
		productModel.setBackendUnitVolume(productData.getBackendUnitVolume());
		
		if(null!=productData.getActive()){
			productModel.setActive(productData.getActive());
			productModel.setApprovalStatus(productData.getActive() ? ArticleApprovalStatus.APPROVED : ArticleApprovalStatus.UNAPPROVED);
		}
	}
}
