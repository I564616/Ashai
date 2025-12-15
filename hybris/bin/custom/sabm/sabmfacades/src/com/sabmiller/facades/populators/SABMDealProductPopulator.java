/**
 *
 */
package com.sabmiller.facades.populators;

import com.sabmiller.core.constants.SabmCoreConstants;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.product.CommerceProductService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.google.common.collect.Maps;
import com.sabmiller.core.comparators.DealScaleComparator;
import com.sabmiller.core.model.AbstractDealBenefitModel;
import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.ComplexDealConditionModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.DealScaleModel;
import com.sabmiller.core.model.DiscountDealBenefitModel;
import com.sabmiller.core.model.FreeGoodsDealBenefitModel;
import com.sabmiller.core.model.ProductDealConditionModel;
import com.sabmiller.core.model.SABMAlcoholProductModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.facades.deal.data.DealBaseProductJson;
import com.sabmiller.facades.deal.data.DealFreeProductJson;
import com.sabmiller.facades.deal.data.DealJson;
import com.sabmiller.facades.deal.data.DealRangeJson;


/**
 * The Class SABMDealProductPopulator.
 */
public class SABMDealProductPopulator extends SABMAbstractDealPopulator implements Populator<List<DealModel>, DealJson>
{
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SABMDealProductPopulator.class);

	/** The deal product json converter. */
	@Resource(name = "dealFreeProductJsonConverter")
	private Converter<ProductModel, DealFreeProductJson> dealFreeProductJsonConverter;

	/** The deal product json converter. */
	@Resource(name = "dealBaseProductJsonConverter")
	private Converter<ProductModel, DealBaseProductJson> dealBaseProductJsonConverter;

	/** The commerce product service. */
	@Resource(name = "commerceProductService")
	private CommerceProductService commerceProductService;


	@Resource(name = "sessionService")
	private SessionService sessionService;

	/**
	 * Populate the Deal's Product to DealJson.
	 *
	 * @param source
	 *           the source
	 * @param target
	 *           the target
	 */
	@Override
	public void populate(final List<DealModel> source, final DealJson target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		Assert.notEmpty(source, "Parameter source cannot be empty.");

		LOG.debug("Populating deal: [{}]", source);

		final DealModel deal = source.get(0);

		if (deal.getConditionGroup() == null)
		{
			LOG.warn("Deal: [{}] is without conditionGroup!", deal.getCode());
			return;
		}

		populateCondition(deal, target);
		populateBenefits(source, target);
		String deliveryDatePackType = sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE_PACKTYPE);

		target.setIsInDeliveryPackType(true);
		if(deliveryDatePackType == null){
			target.setIsInDeliveryPackType(false);
		} else {
			for(DealRangeJson dealRanges : target.getRanges()){
				for(DealBaseProductJson productJson : dealRanges.getBaseProducts()){
					String packType = "KEG";
					if (!productJson.getUomS().toUpperCase().equals("KEG")) {
						packType = "PACK";
					}

					if(deliveryDatePackType.indexOf(packType) == -1){
						target.setIsInDeliveryPackType(false);
						break;
					}
				}
			}

		}

	}


	/**
	 * populate the product in the condition.
	 *
	 * @param source
	 *           the source
	 * @param target
	 *           the target
	 */
	protected void populateCondition(final DealModel source, final DealJson target)
	{
		final List<DealRangeJson> ranges = new ArrayList<>();
		final Set<String> brands = new HashSet<>();
		final Set<String> categories = new HashSet<>();

		final List<AbstractDealConditionModel> dealConditions = source.getConditionGroup().getDealConditions();

		final boolean multiRange = isMultiRange(dealConditions);
		final List<ProductModel> excluded = getProductService().findExcludedProduct(dealConditions);

		final List<DealBaseProductJson> baseProducts = new ArrayList<>();

		DealRangeJson dealRange = null;
		List<DealScaleModel> dealScales = null;

		if (CollectionUtils.isNotEmpty(source.getConditionGroup().getDealScales()))
		{
			dealScales = new ArrayList<>(source.getConditionGroup().getDealScales());
			Collections.sort(dealScales, DealScaleComparator.INSTANCE);
		}

		if (!multiRange || isAcross(source.getConditionGroup()))
		{
			dealRange = new DealRangeJson();
			dealRange.setBaseProducts(baseProducts);
			dealRange.setMaxQty(getMaxQuantity(source));

			if (CollectionUtils.isNotEmpty(dealScales))
			{
				dealRange.setMinQty(dealScales.get(0).getFrom());
			}
			else if (isAcross(source.getConditionGroup()))
			{
				dealRange.setMinQty(getAcrossQty(source.getConditionGroup()));
			}
			else
			{
				dealRange.setMinQty(Integer.valueOf(0));
			}

			ranges.add(dealRange);
		}

		for (final AbstractDealConditionModel condition : dealConditions)
		{
			if (BooleanUtils.isTrue(condition.getExclude()))
			{
				continue;
			}

			if (condition instanceof ProductDealConditionModel)
			{
				final ProductModel product = getProductService()
						.getProductForCodeSafe(((ProductDealConditionModel) condition).getProductCode());

				try
				{
					if (product == null)
					{
						throw new ConversionException("Null product in deal: " + source.getCode());
					}
					SABMAlcoholVariantProductEANModel eanProduct = null;

					if (product instanceof SABMAlcoholVariantProductMaterialModel && ((SABMAlcoholVariantProductMaterialModel) product)
							.getBaseProduct() instanceof SABMAlcoholVariantProductEANModel)
					{
						eanProduct = (SABMAlcoholVariantProductEANModel) ((SABMAlcoholVariantProductMaterialModel) product)
								.getBaseProduct();
					}
					if (eanProduct == null)
					{
						throw new ConversionException("Null EAN product in deal: " + product.getCode());
					}
					if (!eanProduct.getPurchasable())
					{
						throw new ConversionException("Product[" + source.getCode() + "] can not be purchased.");
					}
					final DealBaseProductJson productJson = dealBaseProductJsonConverter.convert(product);

					int qty = 0;
					if (BooleanUtils.isTrue(condition.getMandatory()) || getNumberOfRealConditions(dealConditions) == 1)
					{
						if (((ProductDealConditionModel) condition).getMinQty() != null)
						{
							qty = ((ProductDealConditionModel) condition).getMinQty();
						}
						else if (((ProductDealConditionModel) condition).getQuantity() != null)
						{
							qty = ((ProductDealConditionModel) condition).getQuantity();
						}
					}

					brands.add(getBrandFromProduct(product));
					categories.addAll(getCategoriesFromProduct(product));
					productJson.setQty(qty);
					baseProducts.add(productJson);
				}
				catch (final ConversionException e)
				{
					e.printStackTrace();

				}
			}
			else if (condition instanceof ComplexDealConditionModel)
			{
				final ComplexDealConditionModel complexCondition = (ComplexDealConditionModel) condition;

				final List<? extends ProductModel> materials = getProductService().getProductByHierarchy(complexCondition.getLine(),
						complexCondition.getBrand(), complexCondition.getVariety(), complexCondition.getEmpties(),
						complexCondition.getEmptyType(), complexCondition.getPresentation());

				if (CollectionUtils.isEmpty(materials))
				{
					throw new ConversionException("There are no products with brand: " + complexCondition.getBrand());
				}


				//Filtering the material that have the same EAN
				final Collection<ProductModel> filteredMaterial = CollectionUtils.subtract(materials, excluded);

				if (CollectionUtils.isEmpty(filteredMaterial))
				{
					throw new ConversionException("There are no valid products for deal: " + source);
				}

				final Map<ProductModel, ProductModel> mapMaterial = new HashMap<>();

				for (final ProductModel product : filteredMaterial)
				{
					if (product instanceof SABMAlcoholVariantProductMaterialModel)
					{
						final SABMAlcoholVariantProductEANModel eanProduct = (SABMAlcoholVariantProductEANModel) ((SABMAlcoholVariantProductMaterialModel) product)
								.getBaseProduct();

						/*
						 * If there's lead sku, then the approved MAT product must be the lead sku. Otherwise use the first
						 * MAT product of an EAN product as the priority
						 */
						if (eanProduct.getLeadSku() != null)
						{
							if (eanProduct.getLeadSku() == product)
							{
								mapMaterial.put(eanProduct, product);
							}
						}
						else
						{
							// It'll add the first MAT product only.
							if (mapMaterial.get(eanProduct) == null)
							{
								mapMaterial.put(eanProduct, product);
							}
						}
					}
				}

				if (CollectionUtils.isEmpty(mapMaterial.values()))
				{
					throw new ConversionException("There are no purchasable products with brand: " + complexCondition.getBrand()
							+ " for deal:" + source.getCode());
				}

				final List<DealBaseProductJson> rangeProducts = new ArrayList<>();

				final DealRangeJson brandRange = new DealRangeJson();
				brandRange.setBaseProducts(rangeProducts);
				brandRange.setMaxQty(getMaxQuantity(source));
				// if the mapMaterial count greater than 1 and the is is not across
				if (!isAcross(source.getConditionGroup()))
				{
					if (multiRange || mapMaterial.values().size() > 1)
					{
						ranges.add(brandRange);
					}

				}

				final String complexDealBrand = getBrandFromProduct(filteredMaterial.iterator().next());
				brands.add(complexDealBrand);
				// add the 'range' after the brand
				//	final Locale locale = getI18nService().getCurrentLocale();
				brandRange.setTitle(complexDealBrand + " range");


				for (final ProductModel material : mapMaterial.values())
				{
					SABMAlcoholVariantProductEANModel eanProduct = null;

					if (material instanceof SABMAlcoholVariantProductMaterialModel
							&& ((SABMAlcoholVariantProductMaterialModel) material)
									.getBaseProduct() instanceof SABMAlcoholVariantProductEANModel)
					{
						eanProduct = (SABMAlcoholVariantProductEANModel) ((SABMAlcoholVariantProductMaterialModel) material)
								.getBaseProduct();
					}

					try
					{
						if (eanProduct == null)
						{
							throw new ConversionException("Null EAN product in deal: " + material.getCode());
						}
						if (!eanProduct.getPurchasable())
						{
							throw new ConversionException("Product[" + source.getCode() + "] can not be purchased.");
						}
					}
					catch (final ConversionException e)
					{
						e.printStackTrace();
						continue;
					}
					final DealBaseProductJson productJson = dealBaseProductJsonConverter.convert(material);

					productJson.setQty(complexCondition.getQuantity() != null && mapMaterial.values().size() == 1
							&& (BooleanUtils.isTrue(condition.getMandatory()) || getNumberOfRealConditions(dealConditions) == 1)
									? complexCondition.getQuantity() : 0);

					if (isAcross(source.getConditionGroup()) || (!multiRange && mapMaterial.values().size() == 1))
					{
						baseProducts.add(productJson);
					}
					else
					{
						rangeProducts.add(productJson);
					}

					// Add the categories to the filter
					categories.addAll(getCategoriesFromProduct(material));
				}

				// if the brandRange have been added to the ranges, this deal must not be an across deal, so we need to get the quantity from the condition
				if (complexCondition.getQuantity() != null)
				{
					brandRange.setMinQty(complexCondition.getQuantity());
				}
				else
				{
					brandRange.setMinQty(0);
				}
			}
		}

		target.setBrands(new ArrayList<>(brands));
		target.setCategories(new ArrayList<>(categories));
		setMinQtyRanges(ranges, dealScales);
		target.setRanges(ranges);
	}

	/**
	 * Gets the number of real conditions.
	 *
	 * @param conditions
	 *           the conditions
	 * @return the number of real conditions
	 */
	protected int getNumberOfRealConditions(final List<AbstractDealConditionModel> conditions)
	{
		int count = 0;

		for (final AbstractDealConditionModel condition : conditions)
		{
			if (BooleanUtils.isNotTrue(condition.getExclude()))
			{
				count++;
			}
		}

		return count;
	}

	/**
	 * Sets the min qty ranges.
	 *
	 * @param ranges
	 *           the new min qty ranges
	 */
	protected void setMinQtyRanges(final List<DealRangeJson> ranges, final List<DealScaleModel> dealScales)
	{
		if (CollectionUtils.isNotEmpty(ranges))
		{
			if (CollectionUtils.isNotEmpty(dealScales) && ranges.size() == 1)
			{
				if (dealScales.get(0).getFrom() != null && dealScales.get(0).getFrom() > ranges.get(0).getMinQty())
				{
					ranges.get(0).setMinQty(dealScales.get(0).getFrom());
				}
			}

			for (final DealRangeJson range : ranges)
			{
				int minQty = 0;

				for (final DealBaseProductJson product : ListUtils.emptyIfNull(range.getBaseProducts()))
				{
					if (product.getQty() != null)
					{
						minQty += product.getQty();
					}
				}

				if (range.getMinQty() == null || minQty > range.getMinQty())
				{
					range.setMinQty(minQty);
				}
				else if (range.getMinQty() != null && range.getBaseProducts() != null && range.getBaseProducts().size() == 1)
				{
					range.getBaseProducts().get(0).setQty(range.getMinQty());
				}
			}
		}
	}

	/**
	 * populate the product in the benefit.
	 *
	 * @param deals
	 *           the source
	 * @param target
	 *           the target
	 */
	protected void populateBenefits(final List<DealModel> deals, final DealJson target)
	{
		final List<DealFreeProductJson> freeProducts = new ArrayList<>();
		final boolean multiScale = isMultiScale(deals.get(0));
		Map<String, Map<Integer, Integer>> productQtyMap = null;
		if (multiScale)
		{
			productQtyMap = Maps.newHashMap();
		}
		for (final DealModel deal : deals)
		{
			final List<AbstractDealBenefitModel> dealBenefits = deal.getConditionGroup().getDealBenefits();

			for (final AbstractDealBenefitModel benefit : dealBenefits)
			{
				if (benefit instanceof FreeGoodsDealBenefitModel)
				{
					final ProductModel product = getProductService()
							.getProductForCodeSafe(((FreeGoodsDealBenefitModel) benefit).getProductCode());

					if (product == null)
					{
						throw new ConversionException("Null product in deal benefit: " + benefit);
					}

					final DealFreeProductJson freeProductJson = dealFreeProductJsonConverter.convert(product);
					freeProductJson.setCode(deal.getCode());
					freeProductJson.setProportionalFreeGood(
							benefit.getProportionalFreeGood() == null ? false : benefit.getProportionalFreeGood());

					if (multiScale && CollectionUtils.isNotEmpty(deal.getConditionGroup().getDealScales()))
					{
						final List<DealScaleModel> dealScales = deal.getConditionGroup().getDealScales();

						if (productQtyMap.containsKey(freeProductJson.getCode()))
						{
							final Map<Integer, Integer> map = productQtyMap.get(freeProductJson.getCode());
							map.put(getQtyFromScale(dealScales, benefit.getScale()),
									((FreeGoodsDealBenefitModel) benefit).getQuantity());
						}
						else
						{
							freeProducts.add(freeProductJson);
							final Map<Integer, Integer> map = Maps.newHashMap();
							map.put(getQtyFromScale(dealScales, benefit.getScale()),
									((FreeGoodsDealBenefitModel) benefit).getQuantity());
							productQtyMap.put(freeProductJson.getCode(), map);
						}
					}
					else
					{
						final Map<Integer, Integer> map = Maps.newHashMap();
						map.put(0, ((FreeGoodsDealBenefitModel) benefit).getQuantity());
						freeProductJson.setQty(map);
						freeProducts.add(freeProductJson);
					}
				}
			}
		}

		if (multiScale && MapUtils.isNotEmpty(productQtyMap))
		{
			final List<AbstractDealBenefitModel> dealBenefits = deals.get(0).getConditionGroup().getDealBenefits();

			for (final DealFreeProductJson freeProductJson : freeProducts)
			{
				//if there have discount benefit need add discount qty to map for the free product
				if (!isAllOfFreeGoodsBenefit(dealBenefits))
				{
					setDiscountQtyMap(deals, productQtyMap, freeProductJson.getCode());
				}
				freeProductJson.setQty(productQtyMap.get(freeProductJson.getCode()));
			}
		}

		if (deals.size() > 1)
		{
			target.setSelectableProducts(freeProducts);
			target.setFreeProducts(new ArrayList<>());
		}
		else
		{
			target.setFreeProducts(freeProducts);
			target.setSelectableProducts(new ArrayList<>());
		}
	}


	/**
	 * if the deal is multiScale and it have discount benefit so need set the discount qty to map
	 *
	 * @param deals
	 * @param productQtyMap
	 */
	protected void setDiscountQtyMap(final List<DealModel> deals, final Map<String, Map<Integer, Integer>> productQtyMap,
			final String productCode)
	{
		for (final AbstractDealBenefitModel benefit : deals.get(0).getConditionGroup().getDealBenefits())
		{
			final List<DealScaleModel> dealScales = deals.get(0).getConditionGroup().getDealScales();
			//the benefit should discount
			if (benefit instanceof DiscountDealBenefitModel)
			{
				final Map<Integer, Integer> map = productQtyMap.get(productCode);
				// if the map.scale is not null then not set value to map
				if (null == map.get(getQtyFromScale(dealScales, benefit.getScale())))
				{
					map.put(getQtyFromScale(dealScales, benefit.getScale()), 0);
				}
			}
		}
	}

	protected Integer getQtyFromScale(final List<DealScaleModel> dealScales, final String scale)
	{
		for (final DealScaleModel dealScaleModel : dealScales)
		{
			if (StringUtils.equalsIgnoreCase(dealScaleModel.getScale(), scale))
			{
				return dealScaleModel.getFrom();
			}
		}

		return 0;
	}

	/**
	 * convert the free product.
	 *
	 * @param product
	 *           the product
	 * @param dealCode
	 *           the dealCode
	 * @param qtyMap
	 *           the qty map
	 * @return the deal free product json
	 */
	protected DealFreeProductJson convertFreeProduct(final ProductModel product, final String dealCode,
			final Map<Integer, Integer> qtyMap)
	{
		final DealFreeProductJson productJson = dealFreeProductJsonConverter.convert(product);
		if (productJson != null)
		{
			productJson.setCode(dealCode);
			productJson.setQty(qtyMap);
		}

		return productJson;
	}

	/**
	 * Gets the brand from product.
	 *
	 * @param product
	 *           the product
	 * @return the brand from product
	 */
	protected String getBrandFromProduct(final ProductModel product)
	{
		ProductModel variant = product;
		SABMAlcoholProductModel baseProduct = null;
		//Checking if the source product is instance of SABMAlcoholVariantProductEANModel because the attribute UomMappings belongs to it.
		while (variant instanceof VariantProductModel)
		{
			if (((VariantProductModel) variant).getBaseProduct() instanceof SABMAlcoholProductModel)
			{
				baseProduct = (SABMAlcoholProductModel) ((VariantProductModel) variant).getBaseProduct();
				break;
			}

			variant = ((VariantProductModel) variant).getBaseProduct();
		}

		return baseProduct != null ? baseProduct.getBrand() : StringUtils.EMPTY;
	}

	/**
	 * Gets the categories from product.
	 *
	 * @param product
	 *           the product
	 * @return the categories from variant product
	 */
	protected List<String> getCategoriesFromProduct(final ProductModel product)
	{
		final SABMAlcoholProductModel baseProduct = getBaseProduct(product);
		final List<String> categories = new ArrayList<>();
		if (baseProduct != null)
		{
			//Just get the real category except the classification
			final Collection<CategoryModel> categoryModels = commerceProductService
					.getSuperCategoriesExceptClassificationClassesForProduct(baseProduct);
			for (final CategoryModel categoryModel : CollectionUtils.emptyIfNull(categoryModels))
			{
				categories.add(categoryModel.getName());
			}
		}
		return categories;
	}


	/**
	 * Gets the base alcohol product from variant product.
	 *
	 * @param product
	 *           the variant product
	 * @return the base product
	 */
	protected SABMAlcoholProductModel getBaseProduct(final ProductModel product)
	{
		ProductModel variant = product;
		SABMAlcoholProductModel baseProduct = null;
		// Try to get the baseProduct
		while (variant instanceof VariantProductModel)
		{
			if (((VariantProductModel) variant).getBaseProduct() instanceof SABMAlcoholProductModel)
			{
				baseProduct = (SABMAlcoholProductModel) ((VariantProductModel) variant).getBaseProduct();
				break;
			}

			variant = ((VariantProductModel) variant).getBaseProduct();
		}
		return baseProduct;
	}

}
