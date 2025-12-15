/**
 *
 */
package com.sabmiller.facades.search.converters.populator;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commercefacades.search.converters.populator.SearchResultProductPopulator;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.Config;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.services.cart.ApbProductStockInCartEntryService;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.product.data.BrandData;
import com.apb.facades.product.data.PackageSizeData;
import com.apb.facades.product.data.PackageTypeData;
import com.apb.facades.product.data.UnitVolumeData;
import com.apb.integration.data.AsahiProductInfo;
import com.sabm.core.config.SabmConfigurationService;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.cart.service.SABMCartService;
import com.sabmiller.core.cdlvalue.service.SabmCDLValueService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.core.deals.strategies.SABMDiscountPerUnitCalculationStrategy;
import com.sabmiller.core.enums.CUBStockStatus;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.PlantModel;
import com.sabmiller.core.product.SabmPriceRowService;
import com.sabmiller.core.product.SabmProductService;
import com.sabmiller.core.product.strategy.CatalogVersionDeterminationStrategy;
import com.sabmiller.core.stock.DefaultSabmCommerceStockService;
import com.sabmiller.core.util.SabmStringUtils;
import com.sabmiller.facades.deal.SABMDealsSearchFacade;
import com.sabmiller.facades.deal.data.DealJson;
import com.sabmiller.facades.product.data.UomData;
import com.sabmiller.facades.util.SavePriceUtil;
import com.thoughtworks.xstream.converters.ConversionException;


/**
 * Convert to the packConfiguration attribute of the product in Solr
 *
 * @author xue.zeng
 *
 */
public class SABMSearchResultProductPopulator extends SearchResultProductPopulator
{
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SABMSearchResultProductPopulator.class);

	private static final String CUBSTOCKLINESEPERATOR = "_";

	public static final String NON_ALCOHOLIC_TYPE = "product.code.non.alcoholic.product.apb";

	private static final String MAXORDER_DELEMETER = "_";



	private SabmPriceRowService priceRowService;
	@Resource(name = "b2bCommerceUnitService")
	private B2BCommerceUnitService b2bCommerceUnitService;


	@Resource(name = "dealsService")
	private DealsService dealsService;

	@Resource(name = "productService")
	private SabmProductService productService;


	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;

	@Resource(name = "userService")
	private UserService userService;

	@Resource
	private SabmCDLValueService sabmCDLValueService;

	@Resource(name = "dealTitlePopulator")
	private Populator<List<DealModel>, DealJson> dealTitlePopulator;

	@Resource(name = "discountPerUnitCalculationStrategy")
	private SABMDiscountPerUnitCalculationStrategy discountPerUnitCalculationStrategy;

	@Resource(name = "sabmConfigurationService")
	private SabmConfigurationService sabmConfigurationService;


	/** The sabm deals search facade. */
	@Resource(name = "sabmDealsSearchFacade")
	private SABMDealsSearchFacade sabmDealsSearchFacade;

	@Resource(name = "catalogVersionDeterminationStrategy")
	private CatalogVersionDeterminationStrategy catalogVersionDeterminationStrategy;

	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Resource
	private CommerceStockService commerceStockService;

	@Resource
	private ApbProductStockInCartEntryService apbProductStockInCartEntryService;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	@Resource
	private DefaultSabmCommerceStockService defaultSabmCommerceStockService;

	@Resource
	private EnumerationService enumerationService;

	@Resource(name = "cartService")
	private SABMCartService sabmCartService;

	@Resource
	private ConfigurationService configurationService;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final SearchResultValueData source, final ProductData target) throws ConversionException
	{
		validateParameterNotNullStandardMessage("SearchResultValueData", source);
		validateParameterNotNullStandardMessage("ProductData", source);

		super.populate(source, target);
		//ALB COde Start
		if (!asahiSiteUtil.isCub())
		{
			target.setName(null != this.<String> getValue(source, "name") ? this.<String> getValue(source, "name") : " ");

			populateApbAttributes(source, target);
			if (asahiSiteUtil.isSga())
			{
				populateSgaAttributes(source, target);
			}
		}

		else
		{

   		//ALB COde END
   		if (StringUtils.isNotEmpty(this.<String> getValue(source, "sellingName"))
   				&& StringUtils.isNotEmpty(this.<String> getValue(source, "packConfiguration")))
   		{
   			target.setName(this.<String> getValue(source, "sellingName"));
   			target.setPackConfiguration(this.<String> getValue(source, "packConfiguration"));
   		}
   		else
   		{
   			target.setName(this.<String> getValue(source, "name"));
   		}

   		//SAB-558  add dealsFlag to productData
   		//target.setDealsFlag(BooleanUtils.isTrue(this.<Boolean> getValue(source, "dealsFlag")));

   		target.setSearchable(BooleanUtils.isTrue(this.<Boolean> getValue(source, "searchable")));
   		target.setPurchasable(BooleanUtils.isTrue(this.<Boolean> getValue(source, "purchasable")));
   		target.setVisible(BooleanUtils.isTrue(this.<Boolean> getValue(source, "visible")));
   		target.setBrand(this.<String> getValue(source, "brand"));
   		target.setUnit(this.<String> getValue(source, "unit"));
   		//SAB-76 add newProductFlag to productData
   		target.setNewProductFlag(BooleanUtils.isTrue(this.<Boolean> getValue(source, "isNewProduct")));
			this.populateMaxOrderQty(source, target);

   		//Set CUB Stock Status
   		try
   		{
   			//Check Stock Information from Solr Index
   			final List<String> stockInfoList = this.<List<String>> getValue(source, "cubStockInformation");
   			for (final String stockInfoLine : stockInfoList)
   			{
   				if (StringUtils.isNotEmpty(stockInfoLine))
   				{
   					final String[] stockInfoArray = StringUtils.split(stockInfoLine, CUBSTOCKLINESEPERATOR);
   					//Checking the splitted String length, should be equal to 3
   					if (stockInfoArray.length == 2)
   					{
   						//Get the current user's default b2bunit
   						final B2BUnitModel parentB2bUnit = b2bCommerceUnitService.getParentUnit();
   						final PlantModel plant = parentB2bUnit.getPlant();
   						if (plant.getPlantId().equals(stockInfoArray[0]))
   						{
   							setStockStatus(stockInfoArray[1], target);
   						}
   						else if (plant.getFallbackPlant() != null && plant.getFallbackPlant().getPlantId().equals(stockInfoArray[0]))
   						{
   							setStockStatus(stockInfoArray[1], target);
   						}
   					}
   				}
   			}
   		}
   		catch (final Exception e)
   		{
   			LOG.info("Attribute sku in not set: {}" + source);
   		}

   		final List<String> uomStrs = this.<List<String>> getValue(source, "uoms");
   		if (CollectionUtils.isNotEmpty(uomStrs))
   		{
   			final List<UomData> uomLists = new ArrayList<UomData>();
   			for (final String str : uomStrs)
   			{
   				if (StringUtils.isNotEmpty(str))
   				{
   					final String[] strs = str.split(SabmCoreConstants.SEARCH_PRODUCT_UOM_SEPARATOR);
   					if (strs.length != 0 && strs.length == 2)
   					{
   						if (!StringUtils.isBlank(strs[1]) && !StringUtils.isBlank(strs[0]))
   						{
   							final UomData uomData = new UomData();
   							uomData.setCode(strs[0]);
   							uomData.setName(strs[1]);
   							uomLists.add(uomData);
   						}
   					}
   					else
   					{
   						LOG.warn("Product " + source.getValues().get("code") + " UOM code or name no value!");
   					}
   				}
   			}
			
   			if (CollectionUtils.isNotEmpty(uomLists))
   			{
				final List<String> excludedUoms = Arrays
						.asList(configurationService.getConfiguration().getString("uom.exclude.codes", "").split(","));
						
   				target.setUomList(!excludedUoms.isEmpty() && excludedUoms != null ? uomLists.stream()
							.filter(uom -> !excludedUoms.contains(uom.getCode()))
							.collect(Collectors.toList()) : uomLists);
   			}
   		}

   		try
   		{
   			if(!asahiCoreUtil.isNAPUser())
   			{
   				updateDealInfo(source, target);
   			}
   		}
   		catch (final Exception e)
   		{
   			LOG.warn("error while fetching deals for the the product {}", this.<String> getValue(source, "code"));
   		}

		}

	}


	@Override
	protected void populateStock(final SearchResultValueData source, final ProductData target)
	{
		//ALB COde Start

		if (!asahiSiteUtil.isCub())
		{
			try
			{
				/*
				 * Fetching quantity from solr indexed property and if in case not present fetching the global value
				 */
				final ProductModel productModel = getProductService().getProductForCode(target.getCode());
				if (asahiSiteUtil.isSga())
				{
					final String maxIndexed = this.<String> getValue(source, "maxOrderQuantity");
					final Long maxQty = maxIndexed != null ? Long.valueOf(maxIndexed) : asahiSiteUtil.getSgaGlobalMaxOrderQty();

					final StockData stock = new StockData();
					stock.setStockLevel(maxQty - apbProductStockInCartEntryService.getProductQtyFromCart(target.getCode()));

					if(Boolean.valueOf(asahiConfigurationService.getString("sga.product.status.available", "false")))
					{
   					final StockLevelStatus status = defaultSabmCommerceStockService.getStockLevelForSGA(productModel);
   					if(status!=null)
   					{
   						stock.setStockLevelStatus(status);

   						stock.setStockLevelStatusName(enumerationService.getEnumerationName(status, Locale.ENGLISH));
   					}
   					else
   					{
   						stock.setStockLevelStatus(StockLevelStatus.INSTOCK);
   					}
					}
					else
					{
						stock.setStockLevelStatus(StockLevelStatus.INSTOCK);
					}

					target.setStock(stock);

				}
				else
				{

					if (productModel != null)
					{
						target.setStock(getStockConverter().convert(productModel));
					}
				}
			}
			catch (final UnknownIdentifierException ex)
			{
				// If the product is no longer visible to the customergroup then this exception can be thrown

				// We can't remove the product from the results, but we can mark it as out of stock
				target.setStock(getStockLevelStatusConverter().convert(StockLevelStatus.OUTOFSTOCK));
			}
		}
		//ALB COde END
		else
		{
			super.populateStock(source, target);
		}


	}
	//ALB CODE START
	private void populateApbAttributes(final SearchResultValueData source, final ProductData target)
	{
		populateBrandData(source, target);
		populatePackageType(source, target);
		populatePackageSize(source, target);
		populatePortalUnitVolume(source, target);
		populateAlcoholType(source, target);
	}


	private void populatePortalUnitVolume(final SearchResultValueData source, final ProductData target)
	{

		final String portalUnitVolume = this.<String> getValue(source, "portalUnitVolume");

		if (StringUtils.isNotEmpty(portalUnitVolume))
		{
			final UnitVolumeData unitVolumeData = new UnitVolumeData();
			unitVolumeData.setName(portalUnitVolume);
			target.setUnitVolume(unitVolumeData);
		}


	}

	/**
	 * @param source
	 * @param target
	 * @see populate package size from solr.
	 */
	private void populatePackageSize(final SearchResultValueData source, final ProductData target)
	{


		final String packageSize = this.<String> getValue(source, "packageSize");

		if (StringUtils.isNotEmpty(packageSize))
		{
			final PackageSizeData packageSizeData = new PackageSizeData();
			packageSizeData.setName(packageSize);
			target.setPackageSize(packageSizeData);
		}
	}

	/* Populate brand Attribute from solr Response */
	private void populateBrandData(final SearchResultValueData source, final ProductData target)
	{

		final String brand = this.<String> getValue(source, "brand");

		if (StringUtils.isNotEmpty(brand))
		{
			final BrandData brandData = new BrandData();
			brandData.setName(brand);
			target.setApbBrand(brandData);
		}
	}

	/* Populate PackageType Attribute from solr Response */
	private void populatePackageType(final SearchResultValueData source, final ProductData target)
	{

		final ArrayList<Object> packList = this.<ArrayList<Object>> getValue(source, "packageType");
		if (CollectionUtils.isNotEmpty(packList))
		{
			final PackageTypeData packData = new PackageTypeData();
			packData.setName(packList.get(0).toString());
			target.setPackageType(packData);
		}
	}

	private void populateAlcoholType(final SearchResultValueData source, final ProductData productData)
	{
		final String alcoholType = this.<String> getValue(source, "alcoholType");
		final String nonAlcoholicType = asahiConfigurationService.getString(NON_ALCOHOLIC_TYPE, "10");
		final List<String> nonAlcoholicTypeList = new ArrayList<>(Arrays.asList(nonAlcoholicType.split(",")));
		productData.setLicenseRequired(false);
		final UserModel user = this.userService.getCurrentUser();
		if (null != user && user instanceof B2BCustomerModel && !userService.isAnonymousUser(user))
		{
			final B2BCustomerModel customer = (B2BCustomerModel) user;
			final B2BUnitModel b2bUnit = customer.getDefaultB2BUnit();
			if (b2bUnit instanceof AsahiB2BUnitModel)
			{
				final AsahiB2BUnitModel asahiB2BUnitModel = (AsahiB2BUnitModel) b2bUnit;
				if (StringUtils.isNotEmpty(alcoholType) && nonAlcoholicTypeList.contains(alcoholType))
				{
					productData.setLicenseRequired(false);
				}
				else
				{
					if (StringUtils.isEmpty(asahiB2BUnitModel.getLiquorLicensenumber()))

					{
						productData.setLicenseRequired(true);
					}
					else
					{
						productData.setLicenseRequired(false);
					}
				}
			}
		}
	}

	/** The Method will populate the SGA specific attribute i.e. 'NewProduct'
	 *
	 * @param source - SearchResultValueData from solr
	 * @param target - Product data to display on site
	 */
	private void populateSgaAttributes(final SearchResultValueData source, final ProductData target)
	{
		target.setNewProduct(this.<Boolean> getValue(source, "newProduct"));
		final AsahiProductInfo product = asahiCoreUtil.getProductFromSessionInclusionList(target.getCode());
		target.setIsPromotionActive(null != product && null !=product.getIsPromoFlag()? product.getIsPromoFlag() : Boolean.FALSE);
		target.setPromotionMsg(null != product && StringUtils.isNotEmpty(product.getPromoText()) ? product.getPromoText() : StringUtils.EMPTY);
		updateDealInfo(source, target);
	}

	//ALB CODE END


	/* SAB-560, add by xue.zeng */
	@Override
	protected void populatePrices(final SearchResultValueData source, final ProductData target)
	{

		if(!asahiCoreUtil.isNAPUserForSite()) {

		if (asahiSiteUtil.isCub())
		{
   		final String code = this.<String> getValue(source, "code");

   		validateParameterNotNullStandardMessage("product code", code);
   		final PriceRowModel priceRow = priceRowService.getPriceRowByProductCode(code);

   		// Convert the current user product price from the PriceRow, if not the default product price is shown
   		if (null != priceRow && null != priceRow.getPrice())
   		{
				BigDecimal cdlPrice = null;
				BigDecimal netPrice = BigDecimal.valueOf(priceRow.getPrice().doubleValue());
				final boolean wetEligible = this.<Boolean> getValue(source, "wetEligible");
				final String cdl = this.<String> getValue(source, "level4");
				if (null != cdl && ("C".equalsIgnoreCase(cdl) || "N".equalsIgnoreCase(cdl) || "P".equalsIgnoreCase(cdl)))
				{
					final String presentation = this.<String> getValue(source, "presentation");
					cdlPrice = sabmCDLValueService.getCDLPrice(cdl, presentation);
					netPrice = null != cdlPrice ? netPrice.add(cdlPrice) : netPrice;
				}

				if (wetEligible)
				{
					final BigDecimal wetPercentage = this.configurationService.getConfiguration()
							.getBigDecimal(SabmCoreConstants.CUB_WET_PRICE_PERCENTAGE);
					netPrice = null != wetPercentage ? (netPrice.multiply(wetPercentage)).setScale(2, BigDecimal.ROUND_HALF_UP)
							: netPrice;

				}



   			final PriceData priceData = getPriceDataFactory().create(PriceDataType.BUY,
						netPrice, getCommonI18NService().getCurrentCurrency());
   			target.setPrice(priceData);


   			/*
   			 * SABMC-980 Add "Saving ..."
   			 */

   			//final ProductModel productModel = priceRow.getProduct();
   			//According to the productModel achieve PriceRowModel
   			PriceDataType priceType;
   			if (BooleanUtils.isNotTrue(getValue(source, "hasVariants")))
   			{
   				priceType = PriceDataType.BUY;
   			}
   			else
   			{
   				priceType = PriceDataType.FROM;
   			}

   			// calculated  the SavingsPrice by BasePrice subtract Price
   			target.setSavingsPrice(
   					SavePriceUtil.getSavingsPrice(priceType, priceRow, getCommonI18NService(), getPriceDataFactory()));
   		}

		}

		else
		{
			super.populatePrices(source, target);
		}

		}
	}

	/**
	 * @return the priceRowService
	 */
	public SabmPriceRowService getPriceRowService()
	{
		return priceRowService;
	}

	/**
	 * @param priceRowService
	 *           the priceRowService to set
	 */
	public void setPriceRowService(final SabmPriceRowService priceRowService)
	{
		this.priceRowService = priceRowService;
	}


	private void setStockStatus(final String stockStatus, final ProductData target)
	{
		if (stockStatus != null)
		{
			if (stockStatus.equalsIgnoreCase(CUBStockStatus.OUTOFSTOCK.getCode()))
			{
				if (sabmConfigurationService.isLowStockFlagEnforced())
				{
					target.setCubStockStatus(StockLevelStatus.LOWSTOCK);
				}
				else
				{
					target.setCubStockStatus(StockLevelStatus.OUTOFSTOCK);
				}
			}
			else if (stockStatus.equalsIgnoreCase(CUBStockStatus.LOWSTOCK.getCode()))
			{
				target.setCubStockStatus(StockLevelStatus.LOWSTOCK);
			}
		}
	}




	private void updateDealInfo(final SearchResultValueData source, final ProductData target)
	{
		final UserModel userModel = userService.getCurrentUser();
		if (userModel instanceof B2BCustomerModel)
		{
			final B2BUnitModel b2bUnitModel = b2bUnitService.getParent((B2BCustomerModel) userModel);
			if (null != b2bUnitModel)
			{
				if (asahiSiteUtil.isCub())
				{
					setDealsDetails(source, target, b2bUnitModel);
				}
				else
				{
					populateDealsTitle(source, target, b2bUnitModel);
				}
			}
			else
			{
				LOG.warn("The B2BUnitModel not exists in User");
			}
		}
		else
		{
			LOG.warn("ignoring product exclusions for a regular customer");
		}
	}

	/**
	 * @param source
	 * @param target
	 * @param b2bUnitModel
	 */
	private void populateDealsTitle(final SearchResultValueData source, final ProductData target, final B2BUnitModel b2bUnitModel)
	{
		if (!asahiCoreUtil.isNAPUser())
		{
			final List<String> dealTitles = sabmDealsSearchFacade.getSGADealsTitleForProductAndUnit(target.getCode(), b2bUnitModel);

			//To determine whether to exist deals by b2bunit,productCode,date
			if (CollectionUtils.isNotEmpty(dealTitles))
			{
				LOG.debug("The product: {}  for customer :{} exist deals", target.getCode(), b2bUnitModel);
				// If exist deals set the dealsFlag is true
				target.setDealsFlag(Boolean.TRUE);
				SabmStringUtils.getSortedDealTitles(dealTitles);
				target.setDealsTitle(dealTitles);
			}
			else
			{
				target.setDealsFlag(Boolean.FALSE);
			}
		}

	}


	/**
	 *
	 * @param target
	 * @param b2bUnitModel
	 *
	 *           protected void setDealsDetails(final ProductData target, final B2BUnitModel b2bUnitModel) { if
	 *           (Config.getBoolean("show.deal.titles", true)) { final Date date =
	 *           sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE); final String code =
	 *           target.getCode(); final List<List<DealModel>> deals = dealsService.dealExistForProduct(b2bUnitModel,
	 *           code, date); //To determine whether to exist deals by b2bunit,productCode,date if
	 *           (StringUtils.isNotEmpty(code) && CollectionUtils.isNotEmpty(deals)) { LOG.debug( "The product: {} for
	 *           customer :{} exist deals", code, b2bUnitModel); // If exist deals set the dealsFlag is true
	 *           target. (Boolean.TRUE); target.setDealsTitle(getDealsTitles(deals)); } else {
	 *           target.setDealsFlag(Boolean.FALSE); } }
	 *
	 *           }
	 */

	protected void setDealsDetails(final SearchResultValueData source, final ProductData target, final B2BUnitModel b2bUnitModel)
	{
		if (Config.getBoolean("show.deal.titles", true) && !asahiCoreUtil.isNAPUser())
		{
			final String leadSku = getValue(source, "leadSku");

			final List<String> dealTitles = sabmDealsSearchFacade.getDealsForProduct(leadSku);

			//To determine whether to exist deals by b2bunit,productCode,date
			if (CollectionUtils.isNotEmpty(dealTitles))
			{
				LOG.debug("The product: {}  for customer :{} exist deals", target.getCode(), b2bUnitModel);
				// If exist deals set the dealsFlag is true
				target.setDealsFlag(Boolean.TRUE);
				SabmStringUtils.getSortedDealTitles(dealTitles);
				target.setDealsTitle(dealTitles);
			}
			else
			{
				target.setDealsFlag(Boolean.FALSE);
			}
		}

	}

	private void populateMaxOrderQty(final SearchResultValueData source, final ProductData target)
	{
		final List<String> customerMaxOrderQty = this.<List<String>> getValue(source, "customerMaxOrderQty");
		final List<String> plantMaxOrderQty = this.<List<String>> getValue(source, "plantMaxOrderQty");
		final List<String> globalMaxOrderQty = this.<List<String>> getValue(source, "globalMaxOrderQty");
		Integer effectiveMaxOrderQty = null;
		final CartModel cartModel = sabmCartService.getSessionCart();
		final UserModel currentUserModel = this.userService.getCurrentUser();
		B2BCustomerModel b2bCustomerModel = null;
		List<String> validMaxOrderQtyList = Collections.emptyList();
		if (currentUserModel instanceof B2BCustomerModel)
		{
			b2bCustomerModel = (B2BCustomerModel) currentUserModel;
		}
		if (null != cartModel && null != b2bCustomerModel)
		{
			final Date requestedDispatchDate = cartModel.getRequestedDeliveryDate();
			final B2BUnitModel defaultUnit = b2bCustomerModel.getDefaultB2BUnit();

			if(null != requestedDispatchDate) {
				if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(customerMaxOrderQty))
				{
					validMaxOrderQtyList = customerMaxOrderQty.stream().filter(maxOrderQty -> maxOrderQty.startsWith(defaultUnit
							.getUid()))
							.collect(Collectors.toList());
					effectiveMaxOrderQty = calculateMaxOrderQty(validMaxOrderQtyList, requestedDispatchDate, false);
				}
				if (0 == ObjectUtils.defaultIfNull(effectiveMaxOrderQty, 0)
						&& org.apache.commons.collections4.CollectionUtils.isNotEmpty(plantMaxOrderQty))
				{
					validMaxOrderQtyList = plantMaxOrderQty
							.stream()
							.filter(maxOrderQty -> maxOrderQty.startsWith(defaultUnit.getPlant().getPlantId()))
							.collect(Collectors.toList());
					effectiveMaxOrderQty = calculateMaxOrderQty(validMaxOrderQtyList, requestedDispatchDate, false);
				}
				if (0 == ObjectUtils.defaultIfNull(effectiveMaxOrderQty, 0)
						&& org.apache.commons.collections4.CollectionUtils.isNotEmpty(globalMaxOrderQty))
				{
					validMaxOrderQtyList = globalMaxOrderQty;
					effectiveMaxOrderQty = calculateMaxOrderQty(validMaxOrderQtyList, requestedDispatchDate, true);
				}
			}
		}
		if (0 != ObjectUtils.defaultIfNull(effectiveMaxOrderQty, 0))
		{
			target.setMaxOrderQuantity(effectiveMaxOrderQty);
			final Integer maxOrderRuleDays = Integer.parseInt(asahiConfigurationService
					.getString(ApbCoreConstants.CUB_MAX_ORDER_QTY_RULE_DAYS, ApbCoreConstants.DEFAULT_MAX_ORDER_QTY_RULE_DAYS));
			target.setMaxOrderQuantityDays(maxOrderRuleDays);
		}
	}

	private Integer calculateMaxOrderQty(final List<String> customerMaxOrderQty, final Date requestedDispatchDate, final boolean isGlobal)
	{
		Integer effectiveMaxOrderQty = null;
		final SimpleDateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
		final Iterator<String> iterator = customerMaxOrderQty.iterator();
		while (iterator.hasNext())
		{
			//DateUtils.truncate(maxOrderQty.getStartDate(), Calendar.DATE)
			final List<String> maxOrderValues = Arrays
					.asList(org.apache.commons.lang3.StringUtils.split(iterator.next(), MAXORDER_DELEMETER));
			try
			{
				final Date startDate;
				final Date endDate;
				if (isGlobal)
				{
					startDate = DateUtils.truncate(formatter.parse(maxOrderValues.get(1)), Calendar.DATE);
					endDate = DateUtils.truncate(formatter.parse(maxOrderValues.get(2)), Calendar.DATE);
				}
				else
				{
					startDate = DateUtils.truncate(formatter.parse(maxOrderValues.get(2)), Calendar.DATE);
					endDate = DateUtils.truncate(formatter.parse(maxOrderValues.get(3)), Calendar.DATE);
				}
				if ((requestedDispatchDate.equals(startDate) || requestedDispatchDate.after(startDate))
						&& (requestedDispatchDate.equals(endDate) || requestedDispatchDate.before(endDate)))
				{
					if (isGlobal)
					{
						effectiveMaxOrderQty = Integer.parseInt(maxOrderValues.get(0));
					}
					else
					{
						effectiveMaxOrderQty = Integer.parseInt(maxOrderValues.get(1));
					}
					break;
				}
			}
			catch (final ParseException e)
			{
				// YTODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return effectiveMaxOrderQty;
	}
}
