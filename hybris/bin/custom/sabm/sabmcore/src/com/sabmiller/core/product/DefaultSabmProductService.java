/**
 *
 */
package com.sabmiller.core.product;

import static com.sabmiller.core.constants.SabmCoreConstants.LOWEST_POPULARITY_RANK;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateIfSingleResult;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static java.lang.String.format;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.product.impl.DefaultProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;
import de.hybris.platform.variants.model.VariantProductModel;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azure.storage.blob.BlobContainerClient;
import com.opencsv.CSVWriter;
import com.sabmiller.core.b2b.dao.CUBMaxOrderQuantityDao;
import com.sabmiller.core.cart.service.SABMCartService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.enums.LifecycleStatusType;
import com.sabmiller.core.enums.MaxOrderQtyRuleType;
import com.sabmiller.core.enums.SAPAvailabilityStatus;
import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.ComplexDealConditionModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.MaxOrderQtyModel;
import com.sabmiller.core.model.ProductDealConditionModel;
import com.sabmiller.core.model.ProductSubchannelPopularityMappingModel;
import com.sabmiller.core.model.SABMAlcoholProductModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.core.model.SabmCoreProductRangeModel;
import com.sabmiller.core.product.dao.SabmProductDao;
import com.sabmiller.core.product.strategy.CatalogVersionDeterminationStrategy;
import com.sabmiller.core.util.SabmAzureStorageUtils;


/**
 * The Class DefaultSabmProductService.
 */
@SuppressWarnings("SE_BAD_FIELD")
public class DefaultSabmProductService extends DefaultProductService implements SabmProductService
{

	private SabmAzureStorageUtils sabmAzureStorageUtils;
	private ConfigurationService configurationService;

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSabmProductService.class);
	private static final String CUB_SITE = "sabmStore";

	/** The valid sap avail status list. */
	private List<SAPAvailabilityStatus> validSapAvailStatusList;

	/** The catalog version determination strategy. */
	@Resource(name = "catalogVersionDeterminationStrategy")
	private CatalogVersionDeterminationStrategy catalogVersionDeterminationStrategy;

	/** The core product range dao. */
	@Resource(name = "sabmCoreProductRangeDao")
	private GenericDao<SabmCoreProductRangeModel> coreProductRangeDao;

	@Resource(name = "productDao")
	private SabmProductDao sabmProductDao;
	@Resource
	private CMSSiteService cmsSiteService;
	@Resource(name = "cartService")
	private SABMCartService sabmCartService;
	@Resource
	private UserService userService;
	@Resource
	private CUBMaxOrderQuantityDao cubMaxOrderQuantityDao;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.product.SabmProductService#isProductVisible(com.sabmiller.core.model.
	 * SABMAlcoholVariantProductEANModel)
	 */
	@Override
	public boolean isProductVisible(final SABMAlcoholVariantProductEANModel productModel)
	{
		return match(LifecycleStatusType.PREVIEW, ArticleApprovalStatus.APPROVED, productModel)
				|| match(LifecycleStatusType.OBSOLETE, ArticleApprovalStatus.APPROVED, productModel)
				|| match(LifecycleStatusType.LIVE, ArticleApprovalStatus.APPROVED, productModel);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.product.SabmProductService#isProductPurchasable(com.sabmiller.core.model.
	 * SABMAlcoholVariantProductEANModel)
	 */
	@Override
	public boolean isProductPurchasable(final SABMAlcoholVariantProductEANModel productModel)
	{
		return match(LifecycleStatusType.LIVE, ArticleApprovalStatus.APPROVED, productModel);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.product.SabmProductService#isProductSearchable(com.sabmiller.core.model.
	 * SABMAlcoholVariantProductEANModel)
	 */
	@Override
	public boolean isProductSearchable(final SABMAlcoholVariantProductEANModel productModel)
	{
		return match(LifecycleStatusType.LIVE, ArticleApprovalStatus.APPROVED, productModel);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.product.SabmProductService#productExist(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean productExist(final String code, final String heirarchy)
	{
		return getProductInSapHierarchy(code, heirarchy) != null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.product.SabmProductService#productExist(java.lang.String)
	 */
	@Override
	public boolean productExistInOfflineCatalog(final String code)
	{
		LOG.debug("Checking if product with code {} eists", code);

		final Collection<ProductModel> products = ((SabmProductDao) getProductDao())
				.findProductsByCode(catalogVersionDeterminationStrategy.offlineCatalogVersion(), code);
		return products != null && !products.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.product.SabmProductService#getProductInSapHierarchy(java.lang.String, java.lang.String)
	 */
	@Override
	public SABMAlcoholVariantProductMaterialModel getProductInSapHierarchy(final String code, final String hierarchy)
	{
		return ((SabmProductDao) getProductDao()).findProductByCodeAndHierarchy(code, hierarchy);
	}


	/**
	 * Match.
	 *
	 * @param lifecycleStatus
	 *           the lifecycle status
	 * @param approvalStatus
	 *           the approval status
	 * @param productModel
	 *           the product model
	 * @return true, if successful
	 */
	private boolean match(final LifecycleStatusType lifecycleStatus, final ArticleApprovalStatus approvalStatus,
			final SABMAlcoholVariantProductEANModel productModel)
	{
		return lifecycleStatus.equals(productModel.getLifecycleStatus()) && approvalStatus.equals(productModel.getApprovalStatus())
				&& validSapAvailStatusList.contains(productModel.getSapAvailabilityStatus());
	}

	/**
	 * Sets the valid sap avail status list.
	 *
	 * @param validSapAvailStatusList
	 *           the new valid sap avail status list
	 */
	public void setValidSapAvailStatusList(final List<SAPAvailabilityStatus> validSapAvailStatusList)
	{
		this.validSapAvailStatusList = validSapAvailStatusList;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.product.SabmProductService#getMaterialFromEan(java.lang.String)
	 */
	@Override
	public SABMAlcoholVariantProductMaterialModel getMaterialFromEan(final String eanProductCode)
	{
		final ProductModel productModel = getProductForCode(eanProductCode);
		return getMaterialFromEan(productModel);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.product.SabmProductService#getMaterialFromEan(de.hybris.platform.core.model.product.
	 * ProductModel)
	 */
	@Override
	public SABMAlcoholVariantProductMaterialModel getMaterialFromEan(final ProductModel productModel)
	{
		if (productModel instanceof SABMAlcoholVariantProductEANModel)
		{
			//First check for Lead Sku. If it does not exist, then query for material whose base product is this ean
			final SABMAlcoholVariantProductEANModel eanProductModel = (SABMAlcoholVariantProductEANModel) productModel;
			if (eanProductModel.getLeadSku() != null)
			{
				return eanProductModel.getLeadSku();
			}

			//Lead Sku not avaialble,query for material whose base product is this ean
			final SABMAlcoholVariantProductMaterialModel materialModel = ((SabmProductDao) getProductDao())
					.findMaterialProductByEan(eanProductModel);
			if (materialModel != null)
			{
				return materialModel;
			}
		}

		throw new ModelNotFoundException("No Material Product found for this Ean Product " + productModel);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.product.SabmProductService#getMaterialCodeFromEan(java.lang.String)
	 */
	@Override
	public String getMaterialCodeFromEan(final String eanProductCode)
	{
		return getMaterialFromEan(eanProductCode).getCode();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.product.SabmProductService#getEanFromMaterial(java.lang.String)
	 */
	@Override
	public SABMAlcoholVariantProductEANModel getEanFromMaterial(final String material)
	{
		final ProductModel productModel = getProductForCode(catalogVersionDeterminationStrategy.onlineCatalogVersion(), material);
		if (productModel instanceof SABMAlcoholVariantProductMaterialModel)
		{
			final SABMAlcoholVariantProductMaterialModel materialModel = (SABMAlcoholVariantProductMaterialModel) productModel;
			return (SABMAlcoholVariantProductEANModel) materialModel.getBaseProduct();
		}
		throw new ModelNotFoundException("No Ean Product found for this material product " + material);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.product.SabmProductService#getEanCodeFromMaterial(java.lang.String)
	 */
	@Override
	public String getEanCodeFromMaterial(final String material)
	{
		return getEanFromMaterial(material).getCode();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.product.SabmProductService#generateAlcoholProductCode(java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String generateAlcoholProductCode(final String abv, final String categoryAttribute, final String categoryVariety,
			final String brand, final String style)
	{
		return String.valueOf((abv + categoryAttribute + categoryVariety + brand + style).hashCode());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.product.SabmProductService#fetchCoreProductRange()
	 */
	@Override
	public List<SABMAlcoholVariantProductEANModel> fetchCoreProductRange()
	{
		final String coreProductRangeName = Config.getString("core.product.range.name", "Core Product");

		LOG.debug("Looking up for Core Product Range with Id " + coreProductRangeName);

		final List<SabmCoreProductRangeModel> coreProdRangeList = coreProductRangeDao
				.find(Collections.singletonMap(SabmCoreProductRangeModel.NAME, coreProductRangeName));
		if (CollectionUtils.size(coreProdRangeList) > 1)
		{
			throw new AmbiguousIdentifierException("Found more than one SabmCoreProductRange for name : " + coreProductRangeName);
		}
		if (CollectionUtils.isNotEmpty(coreProdRangeList))
		{
			final List<SABMAlcoholVariantProductEANModel> eanProducts = coreProdRangeList.get(0).getEans();

			LOG.debug("Found eans : " + eanProducts);
			return eanProducts;
		}
		LOG.warn("No Core Product Range found for Id " + coreProductRangeName + " ,  returning null");
		return Collections.emptyList();
	}

	/**
	 * Get the Brand from the fetched product.
	 *
	 * @param dealBrand
	 *           the deal brand
	 * @return SABMAlcoholProductModel
	 */
	@Override
	public SABMAlcoholProductModel getSABMAlcoholProduct(final String dealBrand)
	{
		return ((SabmProductDao) getProductDao()).getSABMAlcoholProduct(dealBrand);
	}

	/**
	 * Get the product by the Product's level2.
	 *
	 * @param dealBrand
	 *           the deal brand
	 * @return List<SABMAlcoholVariantProductMaterialModel>
	 */
	@Override
	public List<SABMAlcoholVariantProductMaterialModel> getProductByLevel2(final String dealBrand)
	{
		return ((SabmProductDao) getProductDao()).getProductByLevel2(dealBrand);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.product.SabmProductService#getProductByHierarchy(java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<SABMAlcoholVariantProductMaterialModel> getProductByHierarchy(final String level1, final String level2,
			final String level3, final String level4, final String level5, final String level6)
	{
		return ((SabmProductDao) getProductDao()).getProductByHierarchy(level1, level2, level3, level4, level5, level6);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.product.SabmProductService#getProductByHierarchy(com.sabmiller.core.model.
	 * ComplexDealConditionModel)
	 */
	@Override
	public List<SABMAlcoholVariantProductMaterialModel> getProductByHierarchy(final ComplexDealConditionModel condition)
	{
		return ((SabmProductDao) getProductDao()).getProductByHierarchy(condition.getLine(), condition.getBrand(),
				condition.getVariety(), condition.getEmpties(), condition.getEmptyType(), condition.getPresentation());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.product.SabmProductService#getProductByHierarchyFilterExcluded(com.sabmiller.core.model.
	 * ComplexDealConditionModel)
	 */
	@Override
	public List<SABMAlcoholVariantProductMaterialModel> getProductByHierarchyFilterExcluded(
			final ComplexDealConditionModel condition)
	{
		final List<SABMAlcoholVariantProductMaterialModel> productByHierarchy = ((SabmProductDao) getProductDao())
				.getProductByHierarchy(condition.getLine(), condition.getBrand(), condition.getVariety(), condition.getEmpties(),
						condition.getEmptyType(), condition.getPresentation());

		final List<SABMAlcoholVariantProductMaterialModel> excludedProduct = findExcludedMaterials(
				condition.getDealConditionGroup().getDealConditions());

		return ListUtils.subtract(productByHierarchy, excludedProduct);
	}

	@Override
	public List<SABMAlcoholVariantProductMaterialModel> getProductsByDeal(final DealModel deal)
	{
		final List<SABMAlcoholVariantProductMaterialModel> products = new ArrayList<>();

		for (final AbstractDealConditionModel condition : deal.getConditionGroup().getDealConditions())
		{
			if (BooleanUtils.isTrue(condition.getExclude()))
			{
				continue;
			}

			if (condition instanceof ProductDealConditionModel)
			{
				final ProductModel productModel = getProductForCodeSafe(((ProductDealConditionModel) condition).getProductCode());

				if (productModel instanceof SABMAlcoholVariantProductMaterialModel)
				{
					products.add((SABMAlcoholVariantProductMaterialModel) productModel);
				}
			}
			else if (condition instanceof ComplexDealConditionModel)
			{
				products.addAll(getProductByHierarchy((ComplexDealConditionModel) condition));
			}
		}
		final List<SABMAlcoholVariantProductMaterialModel> excludedMaterials = findExcludedMaterials(
				deal.getConditionGroup().getDealConditions());

		return ListUtils.subtract(products, excludedMaterials);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.product.SabmProductService#getProductsCode(java.util.Collection)
	 */
	@Override
	public List<String> getProductsCode(final Collection<? extends ProductModel> products)
	{
		final List<String> productsCode = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(products))
		{
			for (final ProductModel product : products)
			{
				productsCode.add(product.getCode());
			}
		}

		return productsCode;
	}

	/**
	 * Retrieves a product by its code.
	 *
	 * @param catalogVersionModel
	 *           the catalog version to search the product from.
	 * @param code
	 *           the product code
	 * @return true, if the product exists, false otherwise.
	 */
	public ProductModel getProductFromCodeWithGivenCatalogVersion(final CatalogVersionModel catalogVersionModel, final String code)
	{
		try
		{
			return super.getProductForCode(catalogVersionModel, code);
		}
		catch (final UnknownIdentifierException uie)
		{
			LOG.warn("Product with code [{}] not found, exception [{}]", code);
		}
		catch (final AmbiguousIdentifierException aie)
		{
			LOG.warn("Multiple instances of Product [{}] was found, exception [{}]", code);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.product.SabmProductService#filterNonPurchaseableProducts(java.util.Collection)
	 */
	@Override
	public Collection<ProductModel> filterNonPurchaseableProducts(final Collection<ProductModel> products)
	{
		final List<ProductModel> results = new ArrayList<>(products);
		for (final ProductModel product : products)
		{
			if (product instanceof SABMAlcoholVariantProductMaterialModel)
			{
				final SABMAlcoholVariantProductEANModel eanProduct = (SABMAlcoholVariantProductEANModel) ((SABMAlcoholVariantProductMaterialModel) product)
						.getBaseProduct();
				//If product is not purchasable then filter it out.
				if (!eanProduct.getPurchasable())
				{
					results.remove(product);
				}
			}
		}
		return results;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.product.SabmProductService#getProductForCodeSafe(java.lang.String)
	 */
	@Override
	public ProductModel getProductForCodeSafe(final String code)
	{
		if (StringUtils.isEmpty(code))
		{
			return null;
		}

		final List<ProductModel> products = getProductDao().findProductsByCode(code);
		//Fixed as per incident-INC0245538
		//if (CollectionUtils.isEmpty(products) || CollectionUtils.size(products) > 1)
		if (CollectionUtils.isEmpty(products) || CollectionUtils.size(products) == 0)
		{
			return null;
		}
		return products.get(0);
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.DealsService#findExcludedProduct(java.util.List)
	 */
	@Override
	public List<ProductModel> findExcludedProduct(final List<AbstractDealConditionModel> dealConditions)
	{
		final List<ProductModel> excluded = new ArrayList<>();

		if (CollectionUtils.isEmpty(dealConditions))
		{
			return excluded;
		}

		String level1 = null;
		String level2 = null;
		String level3 = null;
		String level4 = null;
		String level5 = null;
		String level6 = null;

		for (final AbstractDealConditionModel condition : dealConditions)
		{
			if (condition instanceof ProductDealConditionModel && BooleanUtils.isTrue(condition.getExclude()))
			{
				excluded.add(getProductFromCodeWithGivenCatalogVersion(catalogVersionDeterminationStrategy.onlineCatalogVersion(),
						((ProductDealConditionModel) condition).getProductCode()));
			}
			else if (condition instanceof ComplexDealConditionModel && BooleanUtils.isTrue(condition.getExclude()))
			{
				if (StringUtils.isEmpty(level2))
				{
					for (final AbstractDealConditionModel innerCondition : dealConditions)
					{
						if (innerCondition instanceof ComplexDealConditionModel && BooleanUtils.isNotTrue(innerCondition.getExclude()))
						{
							final ComplexDealConditionModel complexCondition = (ComplexDealConditionModel) innerCondition;
							level1 = complexCondition.getLine();
							level2 = complexCondition.getBrand();
							level3 = complexCondition.getVariety();
							level4 = complexCondition.getEmpties();
							level5 = complexCondition.getEmptyType();
							level6 = complexCondition.getPresentation();
						}
					}
				}

				final ComplexDealConditionModel excCondition = (ComplexDealConditionModel) condition;
				excluded.addAll(getProductByHierarchy(StringUtils.defaultIfEmpty(level1, excCondition.getLine()),
						StringUtils.defaultIfEmpty(level2, excCondition.getBrand()),
						StringUtils.defaultIfEmpty(level3, excCondition.getVariety()),
						StringUtils.defaultIfEmpty(level4, excCondition.getEmpties()),
						StringUtils.defaultIfEmpty(level5, excCondition.getEmptyType()),
						StringUtils.defaultIfEmpty(level6, excCondition.getPresentation())));
			}
		}
		return excluded;
	}

	public List<SABMAlcoholVariantProductMaterialModel> findExcludedMaterials(
			final List<AbstractDealConditionModel> dealConditions)
	{
		final List<ProductModel> excludedProduct = findExcludedProduct(dealConditions);
		final List<SABMAlcoholVariantProductMaterialModel> excludedMaterial = new ArrayList<>();
		for (final ProductModel productModel : excludedProduct)
		{
			if (productModel instanceof SABMAlcoholVariantProductMaterialModel)
			{
				excludedMaterial.add((SABMAlcoholVariantProductMaterialModel) productModel);
			}
		}

		return excludedMaterial;
	}

	/**
	 * Finds the product's sub-channel popularity
	 *
	 * @param eanProduct
	 *           the EAN product to retrieve the ranking from.
	 * @param subChannel
	 *           the sub-channel used to retrieve the Product's sub-channel popularity.
	 * @return the product's popularity rank, 999 if none is found.
	 */
	public int getProductSubchannelPopularityRankBySubchannel(final SABMAlcoholVariantProductEANModel eanProduct,
			final String subChannel)
	{
		final List<ProductSubchannelPopularityMappingModel> subChannelRankings = eanProduct.getSubchannelRankingMappings();

		if (CollectionUtils.isEmpty(subChannelRankings))
		{
			return LOWEST_POPULARITY_RANK;
		}

		for (final ProductSubchannelPopularityMappingModel subChannelRanking : subChannelRankings)
		{
			if (subChannel.equals(subChannelRanking.getSubChannel()))
			{
				return subChannelRanking.getRanking();
			}
		}

		return LOWEST_POPULARITY_RANK;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.product.SabmProductService#getProductForCodeForTemplate(java.lang.String)
	 */
	@Override
	public ProductModel getProductForCodeForTemplate(final String code)
	{
		validateParameterNotNull(code, "Parameter code must not be null");
		final List<ProductModel> products = getProductDao().findProductsByCode(code);

		if (!products.isEmpty() && products.size() > 0)
		{
			validateIfSingleResult(products, format("Product with code '%s' not found!", code),
					format("Product code '%s' is not unique, %d products found!", code, Integer.valueOf(products.size())));
			return products.get(0);
		}
		return null;
	}

	/**
	 * Retrieve product hierarchy
	 *
	 * @param catalogVersion
	 */
	@Override
	public boolean exportProductHierarchy(final CatalogVersionModel catalogVersion) {
		final String containerReference = getConfigurationService().getConfiguration()
				.getString(SabmCoreConstants.PRODUCT_EXPORT_CONTAINER_REFERENCE, "productexport");
		final BlobContainerClient container = sabmAzureStorageUtils.getAzureBlobContainer(containerReference);
		if (null == container) {
			LOG.error("Container not found");
			return false;
		}

		try {
			final String outputFileName = getConfigurationService().getConfiguration().getString(SabmCoreConstants.PRODUCT_HIERARCHY_FILE_REFERENCE, "product_hierarchy.csv");
			final File outputFile = File.createTempFile(outputFileName, null);
			final Writer writer = Files.newBufferedWriter(Path.of(outputFile.getPath()));
			final CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
			final String[] headerRecord = {"Line 1", "EAN", "Material", "Lead SKU"};
			csvWriter.writeNext(headerRecord);

			final List<SABMAlcoholVariantProductEANModel> eanModels = ((SabmProductDao)getProductDao()).getMaterialProducts(catalogVersion);
			for (final SABMAlcoholVariantProductEANModel ean: eanModels) {
				for (final VariantProductModel materialModel: ean.getVariants()) {
					final String[] line = new String[4];
					line[0] = null != ean.getBaseProduct() ? ean.getBaseProduct().getCode() : "";
					line[1] = ean.getCode();
					line[2] = materialModel.getCode();
					line[3] = null != ean.getLeadSku() ? ean.getLeadSku().getCode() : "";
					csvWriter.writeNext(line);
				}
			}

			writer.close();

			if (sabmAzureStorageUtils.writeToAzureStorage(container, outputFile, outputFileName)) {
				LOG.debug("Successfully archived file");
				outputFile.delete();
				return true;
			}

		} catch (final IOException e) {
			LOG.error("Error encountered writing to file. ", e);
			return false;
		}
		return false;
	}

	@Override
	public SABMAlcoholVariantProductMaterialModel getMaterialByCode(final String code) {
		validateParameterNotNull(code, "Parameter code must not be null");
		final List<SABMAlcoholVariantProductMaterialModel> products = sabmProductDao.findMaterialsByCode(code);

		validateIfSingleResult(products, format("Product with code '%s' not found!", code),
				format("Product code '%s' is not unique, %d products found!", code, Integer.valueOf(products.size())));

		return products.get(0);
	}

	protected SabmAzureStorageUtils getSabmAzureStorageUtils() {
		return sabmAzureStorageUtils;
	}

	public void setSabmAzureStorageUtils(final SabmAzureStorageUtils sabmAzureStorageUtils) {
		this.sabmAzureStorageUtils = sabmAzureStorageUtils;
	}

	protected ConfigurationService getConfigurationService() {
		return configurationService;
	}

	public void setConfigurationService(final ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	@Override
	public List<SABMAlcoholVariantProductMaterialModel> getKegMaterials()
	{
		// YTODO Auto-generated method stub
		return sabmProductDao.getKegMaterials(catalogVersionDeterminationStrategy.onlineCatalogVersion());
	}

	@Override
	public int getAverageQuantity(final MaxOrderQtyModel maxOrderQty)
	{
		final Optional<CMSSiteModel> optional = cmsSiteService.getSites().stream().filter(site-> CUB_SITE.equals(site.getUid())).findFirst();
		final CMSSiteModel cmsSiteModel = optional.isPresent() ? optional.get() : null;
		List<OrderEntryModel> entryModels = Collections.emptyList();
		Integer avgQty = 1;
		Long totalQty = 0L;
		final ProductModel productModel = this.getProductForCode(catalogVersionDeterminationStrategy.onlineCatalogVersion(),maxOrderQty.getProduct());
		SABMAlcoholVariantProductMaterialModel sabmAlcoholVariantProductMaterialModel = null;
		if (!(productModel instanceof SABMAlcoholVariantProductEANModel)) {
			maxOrderQty.setProduct(null);
			return 0;
		}
		if (((SABMAlcoholVariantProductEANModel) productModel).getLeadSku() != null)
		{
			sabmAlcoholVariantProductMaterialModel = ((SABMAlcoholVariantProductEANModel) productModel).getLeadSku();
		}
		else
		{
			//Lead Sku not avaialble,query for material whose base product is this ean
			sabmAlcoholVariantProductMaterialModel = sabmProductDao
					.findMaterialProductByEan((SABMAlcoholVariantProductEANModel) productModel);
		}
		if (sabmAlcoholVariantProductMaterialModel != null)
		{
			if (MaxOrderQtyRuleType.CUSTOMER_RULE.equals(maxOrderQty.getRuleType()) && maxOrderQty.getB2bunit() !=null)
			{
				entryModels = sabmProductDao.getOrderEntryForCustomerRule(sabmAlcoholVariantProductMaterialModel,
						maxOrderQty.getB2bunit(),
						cmsSiteModel);
			}
			else if (MaxOrderQtyRuleType.PLANT_RULE.equals(maxOrderQty.getRuleType()) && maxOrderQty.getPlant() != null)
			{
				entryModels = sabmProductDao.getOrderEntryForPlantRule(sabmAlcoholVariantProductMaterialModel, maxOrderQty.getPlant(),
						cmsSiteModel);
			}
			else if (MaxOrderQtyRuleType.GLOBAL_RULE.equals(maxOrderQty.getRuleType()))
			{
				entryModels = sabmProductDao.getOrderEntryForGlobalRule(sabmAlcoholVariantProductMaterialModel, cmsSiteModel);
			}
			if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(entryModels))
			{
				totalQty = entryModels.stream().map(entry -> entry.getQuantity()).reduce(0L, (a, b) -> a + b);
				avgQty = Integer.valueOf(Math.round(totalQty / entryModels.size()));
				LOG.info("max order quantity code:" + maxOrderQty.getCode() + "Average quantity:" + avgQty + "maxOrderQty ruletype:"
						+ maxOrderQty.getRuleType());
			}
		}
		return avgQty;
	}

	public MaxOrderQtyModel getMaxOrderQuantity(final ProductModel productModel) {
		Integer effectiveMaxOrderQty = null;
		MaxOrderQtyModel effectiveMaxOrderQtyModel = null;
		if(null !=productModel) {
			final List<MaxOrderQtyModel> maxOrderQtyModels = this.cubMaxOrderQuantityDao.getCUBMaxOrderQuantityForProductCode(productModel.getCode());
			if (CollectionUtils.isNotEmpty(maxOrderQtyModels))
			{
				final CartModel cartModel = sabmCartService.getSessionCart();
				final UserModel currentUserModel = this.userService.getCurrentUser();
				B2BCustomerModel b2bCustomerModel = null;
				if (currentUserModel instanceof B2BCustomerModel)
				{
					b2bCustomerModel = (B2BCustomerModel) currentUserModel;
				}
				if (null != cartModel && null != b2bCustomerModel)
				{
					final Date requestedDispatchDate = cartModel.getRequestedDeliveryDate();
					final List<MaxOrderQtyModel> validMaxOrderQtyList = maxOrderQtyModels
							.stream()
							.filter(maxOrderQty -> (((requestedDispatchDate
									.equals(DateUtils.truncate(maxOrderQty.getStartDate(), Calendar.DATE)))
									|| (requestedDispatchDate.after(DateUtils.truncate(maxOrderQty.getStartDate(), Calendar.DATE))))
									&& ((requestedDispatchDate
											.equals(DateUtils.truncate(maxOrderQty.getEndDate(), Calendar.DATE))) || (requestedDispatchDate.before(DateUtils.truncate(maxOrderQty.getEndDate(), Calendar.DATE))))))
							.collect(Collectors.toList());
					if (CollectionUtils.isNotEmpty(validMaxOrderQtyList))
					{
						final B2BUnitModel defaultUnitModel = b2bCustomerModel.getDefaultB2BUnit();
						List<MaxOrderQtyModel> validRuleBasedMaxOrderQtyList = validMaxOrderQtyList.stream()
								.filter(maxOrderQty -> MaxOrderQtyRuleType.CUSTOMER_RULE.equals(maxOrderQty.getRuleType())
										&& defaultUnitModel != null && defaultUnitModel.equals(maxOrderQty.getB2bunit()))
								.collect(Collectors.toList());
						effectiveMaxOrderQty = this.calculateMaxOrderQtyBasedOnRule(validRuleBasedMaxOrderQtyList);
						if (0 == ObjectUtils.defaultIfNull(effectiveMaxOrderQty, 0))
						{
							validRuleBasedMaxOrderQtyList = validMaxOrderQtyList.stream()
									.filter(maxOrderQty -> MaxOrderQtyRuleType.PLANT_RULE.equals(maxOrderQty.getRuleType())
											&& defaultUnitModel != null && defaultUnitModel.getPlant().equals(maxOrderQty.getPlant()))
									.collect(Collectors.toList());
							effectiveMaxOrderQty = this.calculateMaxOrderQtyBasedOnRule(validRuleBasedMaxOrderQtyList);
						}
						if (0 == ObjectUtils.defaultIfNull(effectiveMaxOrderQty, 0))
						{
							validRuleBasedMaxOrderQtyList = validMaxOrderQtyList.stream()
									.filter(maxOrderQty -> MaxOrderQtyRuleType.GLOBAL_RULE.equals(maxOrderQty.getRuleType()))
									.collect(Collectors.toList());
							effectiveMaxOrderQty = this.calculateMaxOrderQtyBasedOnRule(validRuleBasedMaxOrderQtyList);
						}
						effectiveMaxOrderQtyModel = CollectionUtils.isNotEmpty(validRuleBasedMaxOrderQtyList)
								? validRuleBasedMaxOrderQtyList.get(0)
								: null;
					}
				}
			}
		}
		return effectiveMaxOrderQtyModel;
	}
	private Integer calculateMaxOrderQtyBasedOnRule(final List<MaxOrderQtyModel> validCustomerRuleMaxOrderQtyList)
	{
		if (CollectionUtils.isNotEmpty(validCustomerRuleMaxOrderQtyList))
		{
			//consdier the first entry as valid entry
			final MaxOrderQtyModel maxOrderQtyModel = validCustomerRuleMaxOrderQtyList.get(0);
			if (null != maxOrderQtyModel)
			{
				if (maxOrderQtyModel.getDefaultAvgMaxOrderQtyEnabled())
				{
					if (0 != ObjectUtils.defaultIfNull(maxOrderQtyModel.getDefaultAvgMaxOrderQty(), 0))
					{
						return maxOrderQtyModel.getDefaultAvgMaxOrderQty();
					}
				}
				else
				{
					return maxOrderQtyModel.getMaxOrderQty();
				}
			}
		}
		return null;
	}
}
