/**
 *
 */
package com.sabmiller.facades.product;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.impl.DefaultProductFacade;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.util.Config;
import de.hybris.platform.variants.model.VariantTypeModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.category.SabmCategoryService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.core.enums.SAPAvailabilityStatus;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.SABMAlcoholProductModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.core.product.SabmProductService;
import com.sabmiller.core.product.strategy.CatalogVersionDeterminationStrategy;
import com.sabmiller.core.product.strategy.HybrisCategoryInfoDeterminationStrategy;
import com.sabmiller.facades.deal.SABMDealsSearchFacade;
import com.sabmiller.facades.deal.data.DealJson;


/**
 * Extension of the OOTB Product Facade for custom SABM implementation
 *
 * @author joshua.a.antony
 */
public class DefaultSabmProductFacade extends DefaultProductFacade<ProductModel> implements SabmProductFacade
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSabmProductFacade.class);

	protected static final String DATE_SAFE_FORMAT = "yyyy-MM-dd";

	@Resource(name = "productService")
	private SabmProductService productService;

	@Resource(name = "categoryService")
	private SabmCategoryService categoryService;

	@Resource(name = "categoryReverseConverter")
	private Converter<CategoryData, CategoryModel> categoryReverseConverter;

	@Resource(name = "productMaterialReverseConverter")
	private Converter<ProductData, SABMAlcoholVariantProductMaterialModel> productMaterialReverseConverter;

	@Resource(name = "productEanReverseConverter")
	private Converter<ProductData, SABMAlcoholVariantProductEANModel> productEanReverseConverter;

	@Resource(name = "productReverseConverter")
	private Converter<ProductData, SABMAlcoholProductModel> productReverseConverter;

	@Resource(name = "typeService")
	private TypeService typeService;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "catalogVersionDeterminationStrategy")
	private CatalogVersionDeterminationStrategy catalogVersionDeterminationStrategy;

	@Resource(name = "hybrisCategoryInfoDeterminationStrategy")
	private HybrisCategoryInfoDeterminationStrategy categoryInfoDeterminationStrategy;

	@Resource(name = "dealsService")
	private DealsService dealsService;

	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;

	@Resource(name = "dealJsonConverter")
	private Converter<List<DealModel>, DealJson> dealJsonConverter;

	@Resource(name = "sabmDealsSearchFacade")
	private SABMDealsSearchFacade sabmDealsSearchFacade;



	/**
	 * As we are maintaining two hierarchies : one replicating the actual product hierarchy in SAP and our own custom
	 * hierarchy, this method will create the products/categories in both the hierarchy. At some point in future, one of
	 * the hierarchy might become obsolete, however at this point (as we are designing the import framework) - we will
	 * have a product belonging to both SAP and Hybris hierarchy
	 */
	@Override
	public void saveProduct(final ProductData productData)
	{
		//saveInSapHierarchy(productData);
		saveInHybrisHierarchy(productData);
	}

	/**
	 * Replicate the SAP product hierarchy in Hybris. This means that there will be 3 category levels under which the
	 * product would belong. The category is determined by the 18 digit product hierarchy that we get from SAP
	 */
	protected void saveInSapHierarchy(final ProductData productData)
	{
		persistCategory(productData.getParent());
		persistProduct(productData);
	}

	/**
	 * As the category name in Hybris hierarhcy is tightly coupled with the cateogry attribute - and since the hierarchy
	 * is just 1 level, this method will just set single parent(category) to the product and then invoke the appropriate
	 * methods for persisting the product along with the category
	 **/
	protected void saveInHybrisHierarchy(final ProductData productData)
	{
		//Setting the parent category first
		final CategoryData category = new CategoryData();
		category.setCode(categoryInfoDeterminationStrategy.deriveCategoryCode(productData.getCategoryAttribute()));
		category.setName(categoryInfoDeterminationStrategy.deriveCategoryName(productData.getCategoryAttribute()));

		//Now, set the category as the parent of the product
		productData.setParent(category);

		//Finally invoke the methods to persist the category and products
		persistCategory(productData.getParent());
		persistProduct(productData);
	}

	/**
	 * Create/update the product. Product in Hybris is represented by {@link SABMAlcoholProductModel} ,
	 * {@link SABMAlcoholVariantProductEANModel} and {@link SABMAlcoholVariantProductMaterialModel}. Apart from creating
	 * these 3 product relation, the product is also assigned to a category - the category information is available in
	 * the 'parent' property of {@link ProductData}
	 */
	protected SABMAlcoholProductModel persistProduct(final ProductData productData)
	{
		final SABMAlcoholProductModel productModel = productReverseConverter.convert(productData);
		final String code = productData.getEan();
		final boolean productEanExist = productService.productExistInOfflineCatalog(code);

		final SABMAlcoholVariantProductEANModel productEanModel = productEanReverseConverter.convert(productData);
		final SABMAlcoholVariantProductMaterialModel productMaterialModel = productMaterialReverseConverter.convert(productData);

		productModel
				.setVariantType((VariantTypeModel) typeService.getComposedTypeForCode(SABMAlcoholVariantProductEANModel._TYPECODE));

		productEanModel.setVariantType(
				(VariantTypeModel) typeService.getComposedTypeForCode(SABMAlcoholVariantProductMaterialModel._TYPECODE));

		productEanModel.setBaseProduct(productModel);
		productMaterialModel.setBaseProduct(productEanModel);

		getModelService().save(productModel);

		if (productData.isLeadSku())
		{
			productEanModel.setLeadSku(productMaterialModel);
		}

		// EAN has to updated only if Material is lead SKU or EAN is doesn't exists.
		if (!productEanExist || productData.isLeadSku())
		{
			productEanModel.setSapAvailabilityStatus(SAPAvailabilityStatus.valueOf(productData.getSapAvailability()));
			getModelService().save(productEanModel);
		}

		getModelService().save(productMaterialModel);

		final CategoryModel parentCategoryModel = categoryService
				.getCategoryForCode(catalogVersionDeterminationStrategy.offlineCatalogVersion(), productData.getParent().getCode());

		/** Use case when the product category has been modified. De-associate with the existing category **/
		if (CollectionUtils.isNotEmpty(productModel.getSupercategories()))
		{
			final CategoryModel existingParentCategory = productModel.getSupercategories().iterator().next();

			if (!existingParentCategory.getCode().equals(parentCategoryModel.getCode()))
			{
				productModel.setSupercategories(Collections.emptyList());
				getModelService().save(productModel);
			}
		}

		final List<ProductModel> products = new ArrayList<ProductModel>();
		products.add(productModel);
		for (final ProductModel eachProductModel : parentCategoryModel.getProducts())
		{
			if (!productModel.getCode().equals(eachProductModel.getCode()))
			{
				products.add(eachProductModel);
			}
		}

		parentCategoryModel.setProducts(products);
		getModelService().save(parentCategoryModel);

		return productModel;
	}

	/**
	 * Creates or updates the category hierarchy. It is assumed that the {@link CategoryData} has the hierarchy setup
	 * through its 'parent' property. Categories are created top to bottom, i.e the parent category is created/updated
	 * before the child categories. This is achieved through recursion where parents are queried until top level (parent
	 * = null) is reached.
	 */
	protected CategoryModel persistCategory(final CategoryData categoryData)
	{

		//If top level category just save this category , else determine parent and update it first!
		if (categoryData.getParent() == null)
		{
			LOG.debug("Found top level category : " + categoryData.getCode());
			final CategoryModel model = categoryReverseConverter.convert(categoryData);
			getModelService().save(model);//Save current category after parent.
			return model;
		}
		LOG.debug("Persist the parent first. Parent category : " + categoryData.getParent().getCode());

		final CategoryModel parentCategoryModel = persistCategory(categoryData.getParent()); //Else, create parent first!
		final CategoryModel childCategoryModel = categoryReverseConverter.convert(categoryData);
		getModelService().save(childCategoryModel);//Save current category after parent.

		LOG.debug("Saved category : " + childCategoryModel.getCode());

		final List<CategoryModel> childCategories = new ArrayList<CategoryModel>();
		childCategories.add(childCategoryModel);
		for (final CategoryModel eachCategoryModel : parentCategoryModel.getCategories())
		{
			if (!childCategoryModel.getCode().equals(eachCategoryModel.getCode()))
			{
				childCategories.add(eachCategoryModel);
			}
		}
		parentCategoryModel.setCategories(childCategories);
		getModelService().save(parentCategoryModel);

		LOG.debug("Associated child category " + childCategoryModel.getCode() + " to parent " + parentCategoryModel.getCode()
				+ ". Returning " + childCategoryModel.getCode());
		return childCategoryModel;
	}

	@Override
	public boolean productExistInOfflineCatalog(final String code)
	{
		return productService.productExistInOfflineCatalog(code);
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.product.SabmProductFacade#getDealsForProduct(java.lang.String, java.util.Date)
	 */
	@Override
	public List<DealJson> getDealsForProduct(final String productCode)
	{
		final UserModel currentUser = getUserService().getCurrentUser();
		if (currentUser instanceof B2BCustomerModel)
		{
			final B2BUnitModel b2bUnitModel = b2bUnitService.getParent((B2BCustomerModel) currentUser);
			if (b2bUnitModel != null)
			{
				sessionService.setAttribute(SabmCoreConstants.SESSION_SELECT_B2BUNIT_UID_DATA, b2bUnitModel.getUid());
			}
			try
			{
				final ProductModel eanProduct = productService.getProductForCode(productCode);

				//To determine whether to exist deals by b2bunit,productCode,date
				if (eanProduct != null && CollectionUtils.isNotEmpty(eanProduct.getVariants()))
				{
					final Date fromDate = sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE);

					final List<DealModel> dealModels = dealsService.getDealsForProduct(b2bUnitModel,
							productService.getProductsCode(eanProduct.getVariants()), fromDate, fromDate);
					// To determine the valid deals
					final List<DealModel> dealsFiltered = dealsService.getValidationDeals(dealModels, Boolean.TRUE);

					final List<List<DealModel>> composedDeals = dealsService.composeComplexFreeProducts(dealsFiltered);
					LOG.debug("Composed Deals: {}" + composedDeals);
					final List<DealJson> dealsJson = new ArrayList<>();
					for (final List<DealModel> dealList : composedDeals)
					{
						try
						{
							final DealJson dealJson = dealJsonConverter.convert(dealList);
							dealsJson.add(dealJson);
						}
						catch (final ConversionException e)
						{
							LOG.warn("Unable to convert deal: " + dealList.get(0), e);
						}
					}

					return dealsJson;
				}
			}
			catch (AmbiguousIdentifierException | UnknownIdentifierException | IllegalArgumentException e)
			{
				LOG.debug(e.getMessage(), e);
				LOG.warn("Error fetching product with code: " + productCode);
			}

		}
		return Collections.emptyList();
	}



	// default, the next two (2) weeks, will be shown in the deals list.
	protected Date forNextPeriodDate(final Date date)
	{
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.DATE, Config.getInt("deal.valid.next.default.day", 14));
		return cal.getTime();
	}

	/**
	 * @return the productService
	 */
	@Override
	public SabmProductService getProductService()
	{
		return productService;
	}

	public void setProductService(final SabmProductService productService)
	{
		this.productService = productService;
	}

	@Override
	public List<SABMAlcoholVariantProductMaterialModel> getKegMaterials()
	{
		// YTODO Auto-generated method stub
		return productService.getKegMaterials();
	}
}
