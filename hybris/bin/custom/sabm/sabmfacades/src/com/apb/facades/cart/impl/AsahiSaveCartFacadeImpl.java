/*
 *
 */
package com.apb.facades.cart.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.order.data.OrderTemplateData;
import de.hybris.platform.commercefacades.order.data.OrderTemplateEntryData;
import de.hybris.platform.commercefacades.order.impl.DefaultSaveCartFacade;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.impl.DefaultOrderService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;
import de.hybris.platform.servicelayer.time.TimeService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.model.ApbProductModel;
import com.apb.core.model.OrderTemplateEntryModel;
import com.apb.core.model.OrderTemplateModel;
import com.apb.core.services.AsahiOrderService;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.cart.AsahiSaveCartFacade;
import com.apb.facades.order.data.AsahiQuickOrderData;
import com.apb.facades.order.data.AsahiQuickOrderEntryData;
import com.apb.facades.price.ApbPriceUpdateFacade;
import com.apb.facades.price.PriceInfo;
import com.apb.facades.price.PriceInfoData;
import com.apb.integration.data.AsahiProductInfo;
import com.apb.product.strategy.AsahiInclusionExclusionProductStrategy;
import com.sabmiller.core.cart.dao.DefaultSabmCommerceCartDao;
import com.sabmiller.core.cart.service.SABMB2BCommerceCartService;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.order.impl.SabmCommerceAddToCartStrategy;
import com.sabmiller.facades.order.SABMOrderFacade;


/**
 * The Class AsahiSaveCartFacadeImpl.
 *
 * @author Kuldeep.Singh1
 */
public class AsahiSaveCartFacadeImpl extends DefaultSaveCartFacade implements AsahiSaveCartFacade
{

	// Creates logger
	final Logger logger = LoggerFactory.getLogger(AsahiSaveCartFacadeImpl.class);

	/** The default save cart expiry days. */
	final private static int DEFAULT_SAVE_CART_EXPIRY_DAYS = 30;

	final private static String QUICK_ORDER_TEMPLETE_PREFIX = "Quick Order";
	/** The cub commerce cart service. */

	private static final String TEMPLATE_SORT_CODE="A-Z";

	@Resource(name = "defaultSabmB2BCommerceCartService")
	private SABMB2BCommerceCartService sabMB2BCommerceCartService;


	/** The time service. */
	@Resource(name = "timeService")
	private TimeService timeService;

	/** The cms site service. */
	@Resource(name = "cmsSiteService")
	private CMSSiteService cmsSiteService;

	/** The entry number key generator. */
	@Resource(name = "entryNumberKeyGenerator")
	private PersistentKeyGenerator entryNumberKeyGenerator;

	/** The product service. */
	@Resource(name = "productService")
	private ProductService productService;

	/** The catalog version service. */
	@Resource(name = "catalogVersionService")
	private CatalogVersionService catalogVersionService;

	@Resource
	private DefaultOrderService orderService;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	@Resource(name = "inclusionExclusionProductStrategy")
	private AsahiInclusionExclusionProductStrategy inclusionExclusionProductStrategy;

	/** The asahi order service. */
	@Resource(name = "asahiOrderService")
	private AsahiOrderService asahiOrderService;

	/** The asahi commerce cart dao. */
	@Resource(name = "defaultSabmCommerceCartDao")
	private DefaultSabmCommerceCartDao defaultSabmCommerceCartDao;

	/** The asahi order template converter. */
	private Converter<OrderTemplateModel, OrderTemplateData> asahiOrderTemplateConverter;

	@Resource
	ApbPriceUpdateFacade apbPriceUpdateFacade;

	@Resource
	PriceDataFactory priceDataFactory;

	@Resource
	CommonI18NService commonI18NService;

	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	@Resource(name = "orderFacade")
	private SABMOrderFacade sabmOrderFacade;

	@Resource(name = "sabmCommerceAddToCartStrategy")
	private SabmCommerceAddToCartStrategy sabmCommerceAddToCartStrategy;


	/**
	 * Save cart.
	 *
	 * @param inputParameters
	 *           the input parameters
	 * @return the commerce save cart result data
	 */
	@Override
	public boolean saveOrderTemplate(final String templateName)
	{
		final B2BCustomerModel customer = (B2BCustomerModel) getUserService().getCurrentUser();
		boolean isOrderTemplateExistForNameAndB2BUnit = true;
		final List<OrderTemplateModel> orderTemplateCodeAndB2BUnit = this.sabMB2BCommerceCartService
				.getCartForCodeAndB2BUnit(templateName, customer.getDefaultB2BUnit());
		final boolean isSga = asahiSiteUtil.isSga();
		if (CollectionUtils.isNotEmpty(orderTemplateCodeAndB2BUnit) && orderTemplateCodeAndB2BUnit.size() > 0)
		{
			logger.info("Cannot find a cart for code [" + templateName + "]");
			isOrderTemplateExistForNameAndB2BUnit = false;
		}
		else
		{
			final OrderTemplateModel orderTemplate = getModelService().create(OrderTemplateModel.class);

			final Date currentDate = this.timeService.getCurrentTime();

			orderTemplate.setSaveTime(currentDate);
			orderTemplate.setExpirationTime(this.calculateExpirationTime(currentDate));
			orderTemplate.setSavedBy(customer);
			orderTemplate.setUser(customer);
			orderTemplate.setName(templateName);
			orderTemplate.setDescription(templateName);
			orderTemplate.setDate(currentDate);
			orderTemplate.setCurrency(this.cmsSiteService.getCurrentSite().getStores().get(0).getDefaultCurrency());
			orderTemplate.setB2bUnit((AsahiB2BUnitModel) customer.getDefaultB2BUnit());

			// setting order entries
			if (getCartService().hasSessionCart())
			{
				final List<OrderTemplateEntryModel> newEntries = new ArrayList<OrderTemplateEntryModel>();
				final List<AbstractOrderEntryModel> entries = getCartService().getSessionCart().getEntries();

				if (CollectionUtils.isNotEmpty(entries))
				{
					for (final AbstractOrderEntryModel entry : entries)
					{
						if (null == entry.getIsBonusStock() || !entry.getIsBonusStock())
						{

							final OrderTemplateEntryModel templateEntry = getModelService().create(OrderTemplateEntryModel.class);
							templateEntry.setTotalPrice(entry.getTotalPrice());
							templateEntry.setBasePrice(entry.getBasePrice());
							if (isSga)
							{
								templateEntry.setQuantity(entry.getQuantity().longValue());
							}
							else
							{
								templateEntry.setQuantity(0L);
							}
							templateEntry.setEntryNumber(entry.getEntryNumber());

							if (null != entry.getProduct())
							{
								templateEntry.setProduct(entry.getProduct());
							}
							newEntries.add(templateEntry);
						}
					}
				}
				getModelService().saveAll(newEntries);
				orderTemplate.setTemplateEntry(newEntries);
			}

			getModelService().save(orderTemplate);
			getModelService().refresh(orderTemplate);
		}

		return isOrderTemplateExistForNameAndB2BUnit;
	}

	protected Date calculateExpirationTime(final Date currentDate)
	{
		final Integer expirationDays = getConfigurationService().getConfiguration()
				.getInteger("commerceservices.saveCart.expiryTime.days", Integer.valueOf(DEFAULT_SAVE_CART_EXPIRY_DAYS));
		return new DateTime(currentDate).plusDays(expirationDays.intValue()).toDate();
	}

	/**
	 * Gets the saved carts for current user B 2 B unit.
	 *
	 * @param pageableData
	 *           the pageable data
	 * @return the saved carts for current user B 2 B unit
	 */
	@Override
	public SearchPageData<OrderTemplateData> getSavedCartsForCurrentUserB2BUnit(final PageableData pageableData)
	{
		final SearchPageData<OrderTemplateData> result = new SearchPageData<>();
		final B2BCustomerModel customer = (B2BCustomerModel) getUserService().getCurrentUser();
		if (null != customer.getDefaultB2BUnit())
		{
			final SearchPageData<OrderTemplateModel> savedOrderTemplates = this.sabMB2BCommerceCartService
					.getSavedCartForCodeAndB2BUnit(pageableData, (AsahiB2BUnitModel) customer.getDefaultB2BUnit());
			result.setPagination(savedOrderTemplates.getPagination());
			result.setSorts(savedOrderTemplates.getSorts());

			final List<OrderTemplateModel> templateList = savedOrderTemplates.getResults();
			final List<OrderTemplateData> orderTemplateData = new ArrayList<>();

			if (CollectionUtils.isNotEmpty(templateList))
			{
				for (final OrderTemplateModel template : templateList)
				{
					final OrderTemplateData orderTempData = asahiOrderTemplateConverter.convert(template);
					if (asahiSiteUtil.isSga())
					{
						inclusionExclusionProductStrategy.updateProductDataTemplate(orderTempData);
					}
					orderTemplateData.add(orderTempData);
				}
			}

			if (asahiSiteUtil.isSga())
			{
				final OrderTemplateModel quickOrderTemplateModel = getQuickOrderTemplateModel(customer);
				// checks if the smart order is already included in the result or not
				if (templateList.contains(quickOrderTemplateModel))
				{
					for (final OrderTemplateData ordTempData : orderTemplateData)
					{
						if (QUICK_ORDER_TEMPLETE_PREFIX.equalsIgnoreCase(ordTempData.getName()))
						{
							ordTempData.setIsQuickOrder(true);
							break;
						}
					}
				}
				else
				{
					final OrderTemplateData orderTempData = asahiOrderTemplateConverter.convert(quickOrderTemplateModel);
					inclusionExclusionProductStrategy.updateProductDataTemplate(orderTempData);
					orderTempData.setIsQuickOrder(true);
					orderTemplateData.add(orderTempData);
				}

			}

			result.setResults(orderTemplateData);
		}
		return result;
	}

	/**
	 * Create or update Quick Order for the customer and
	 *
	 * @param customer
	 * @return
	 */
	private OrderTemplateModel getQuickOrderTemplateModel(final B2BCustomerModel customer)
	{
		final String quickOrderTemplateName = QUICK_ORDER_TEMPLETE_PREFIX;
		final List<OrderTemplateModel> quickOrderTemplateCodeAndB2BUnit = this.sabMB2BCommerceCartService
				.getCartForCodeAndB2BUnit(quickOrderTemplateName, customer.getDefaultB2BUnit());

		if (CollectionUtils.isEmpty(quickOrderTemplateCodeAndB2BUnit))
		{
			//create a new quick order for the customer
			final OrderTemplateModel quickOrderTemplateModel = getModelService().create(OrderTemplateModel.class);
			final Date currentDate = this.timeService.getCurrentTime();

			quickOrderTemplateModel.setSaveTime(currentDate);
			quickOrderTemplateModel.setExpirationTime(this.calculateExpirationTime(currentDate));
			quickOrderTemplateModel.setSavedBy(customer);
			quickOrderTemplateModel.setUser(customer);
			quickOrderTemplateModel.setName(quickOrderTemplateName);
			quickOrderTemplateModel.setDescription(quickOrderTemplateName);
			quickOrderTemplateModel.setDate(currentDate);
			quickOrderTemplateModel.setCurrency(this.cmsSiteService.getCurrentSite().getStores().get(0).getDefaultCurrency());
			quickOrderTemplateModel.setB2bUnit((AsahiB2BUnitModel) customer.getDefaultB2BUnit());

			setOrderTemplateEntries(customer, quickOrderTemplateModel);

			getModelService().save(quickOrderTemplateModel);
			getModelService().refresh(quickOrderTemplateModel);

			return quickOrderTemplateModel;
		}
		else
		{
			//update the quick order
			final OrderTemplateModel quickOrderTemplate = quickOrderTemplateCodeAndB2BUnit.get(0);
			if (null != quickOrderTemplate.getTemplateEntry())
			{
				final List<OrderTemplateEntryModel> existingOrderTemplateModel = quickOrderTemplate.getTemplateEntry();
				getModelService().removeAll(existingOrderTemplateModel);

				final Date currentDate = this.timeService.getCurrentTime();

				quickOrderTemplate.setSaveTime(currentDate);
				quickOrderTemplate.setExpirationTime(this.calculateExpirationTime(currentDate));
				setOrderTemplateEntries(customer, quickOrderTemplate);
				getModelService().save(quickOrderTemplate);
				getModelService().refresh(quickOrderTemplate);
			}
			return quickOrderTemplate;
		}
	}

	/**
	 * adds the OrderTemplateEntryModel to the Quick Order OrderTemplateModel
	 *
	 * @param customer
	 * @param quickOrderTemplateModel
	 */
	private void setOrderTemplateEntries(final B2BCustomerModel customer, final OrderTemplateModel quickOrderTemplateModel)
	{
		final Map<String, Map<String, Long>> productQuantities = new HashMap<String, Map<String, Long>>();
		final Map<String, AbstractOrderEntryModel> orderEntryModelMap = new HashMap<String, AbstractOrderEntryModel>();
		List<OrderModel> orderModels = null;
		final AsahiB2BUnitModel b2bUnitModel = (AsahiB2BUnitModel) customer.getDefaultB2BUnit();

		if (null != b2bUnitModel)
		{
			orderModels = this.asahiOrderService.getOrderEntriesForUser(b2bUnitModel);
		}
		if (null != orderModels && CollectionUtils.isNotEmpty(orderModels))
		{
			final AsahiQuickOrderData quickOrderData = sabmOrderFacade.getQuickOrders(null);
			if (null != quickOrderData && CollectionUtils.isNotEmpty(quickOrderData.getEntries()))
			{
				for (final AsahiQuickOrderEntryData qoe : quickOrderData.getEntries())
				{
					productQuantities.put(qoe.getCode(), qoe.getDateRange());
				}
			}
			for (final OrderModel om : orderModels)
			{
				for (final AbstractOrderEntryModel oeModel : om.getEntries())
				{
					if (null != oeModel.getProduct() && (null == oeModel.getIsBonusStock() || !oeModel.getIsBonusStock()))
					{
						orderEntryModelMap.put(oeModel.getProduct().getCode(), oeModel);
					}
				}
			}
			final List<OrderTemplateEntryModel> newEntries = new ArrayList<OrderTemplateEntryModel>();
			if (CollectionUtils.isNotEmpty(orderEntryModelMap.values()))
			{
				getOrderTemplateEntries(productQuantities, orderEntryModelMap, newEntries);
				getModelService().saveAll(newEntries);
				quickOrderTemplateModel.setTemplateEntry(newEntries);
			}
		}
	}

	/**
	 * creates the List of OrderTemplateEntryModel for the Quick Order OrderTemplateModel
	 *
	 * @param productQuantities
	 * @param orderEntryModelMap
	 * @param newEntries
	 */
	private void getOrderTemplateEntries(final Map<String, Map<String, Long>> productQuantities,
			final Map<String, AbstractOrderEntryModel> orderEntryModelMap, final List<OrderTemplateEntryModel> newEntries)
	{
		for (final AbstractOrderEntryModel entry : orderEntryModelMap.values())
		{
			final OrderTemplateEntryModel templateEntry = getModelService().create(OrderTemplateEntryModel.class);
			templateEntry.setBasePrice(entry.getBasePrice());

			if (null != productQuantities.get(entry.getProduct().getCode()))
			{
				final Collection<Long> quantitiesOrdered = productQuantities.get(entry.getProduct().getCode()).values();
				double total = 0;
				for (final Long quantity : quantitiesOrdered)
				{
					total += quantity;
				}
				final long avgQuantity = (long) Math.ceil(total / quantitiesOrdered.size());
				if (avgQuantity == 0)
				{
					templateEntry.setQuantity((long) 1);
				}
				else
				{
					templateEntry.setQuantity(avgQuantity);
				}

			}


			templateEntry.setEntryNumber(entry.getEntryNumber());

			if (null != entry.getProduct())
			{
				templateEntry.setProduct(entry.getProduct());
			}
			newEntries.add(templateEntry);

		}
	}


	/**
	 * Gets the saved carts for current user B 2 B unit.
	 *
	 * @param pageableData
	 *           the pageable data
	 * @return the saved carts for current user B 2 B unit
	 */
	@Override
	public List<OrderTemplateData> getAllSavedCartsForCurrentUserB2BUnit()
	{
		final B2BCustomerModel customer = (B2BCustomerModel) getUserService().getCurrentUser();
		if (null != customer.getDefaultB2BUnit())
		{
			final List<OrderTemplateModel> savedOrderTemplates = this.sabMB2BCommerceCartService
					.getAllSavedCartForB2BUnit((AsahiB2BUnitModel) customer.getDefaultB2BUnit());

			final List<OrderTemplateData> orderTemplateData = new ArrayList<>();
			if (CollectionUtils.isNotEmpty(savedOrderTemplates))
			{
				for (final OrderTemplateModel template : savedOrderTemplates)
				{
					if (!QUICK_ORDER_TEMPLETE_PREFIX.equalsIgnoreCase(template.getName()))
					{
						final OrderTemplateData orderTempData = asahiOrderTemplateConverter.convert(template);
						if (asahiSiteUtil.isSga())
						{
							inclusionExclusionProductStrategy.updateProductDataTemplate(orderTempData);
						}
						orderTemplateData.add(orderTempData);
					}
				}
			}

			return orderTemplateData;
		}
		return Collections.EMPTY_LIST;
	}

	/**
	 * Gets the order template for code and B 2 B unit.
	 *
	 * @param templateCode
	 *           the template code
	 * @return the order template for code and B 2 B unit
	 *
	 */
	@Override
	public OrderTemplateData getOrderTemplateForCodeAndB2BUnit(final String templateCode,final String sortCode)
	{
		final B2BCustomerModel customer = (B2BCustomerModel) getUserService().getCurrentUser();
		List<OrderTemplateEntryModel> sortedOrdertemplateEntry;
		OrderTemplateData orderTemplateData = new OrderTemplateData();
		if (null != customer.getDefaultB2BUnit())
		{
			Map<String, PriceInfo> priceResult = null;
			final OrderTemplateModel orderTemplate = this.sabMB2BCommerceCartService.getOrderTemplateForCodeAndB2BUnit(templateCode,
					(AsahiB2BUnitModel) customer.getDefaultB2BUnit());
			if (CollectionUtils.isNotEmpty(orderTemplate.getTemplateEntry()))
			{
				final List<String> products = orderTemplate.getTemplateEntry().stream().map(entry -> entry.getProduct().getCode())
						.collect(Collectors.toList());
				final Map<String, Long> productQuantityMap = createMapFromProductList(products);
				if (asahiSiteUtil.isSga())
				{
					final Set<String> productIds = productQuantityMap.keySet();
					priceResult = apbPriceUpdateFacade.getPriceMapFromSession(productIds);
				}
				else
				{
					priceResult = createProductPriceMap(apbPriceUpdateFacade.updatePriceInfoData(productQuantityMap, false));
				}
			}

			//HC-1615:Sort order template entry products
			final List<OrderTemplateEntryModel> unSortedOrdertemplateEntry = orderTemplate.getTemplateEntry();

			if(sortCode.equalsIgnoreCase(TEMPLATE_SORT_CODE))
			{
				final Comparator<Object> compareByProductBrand = (t1,t2) ->
				{
					if(((ApbProductModel)((OrderTemplateEntryModel) t1).getProduct()).getBrand()==null) {
						return ((ApbProductModel)((OrderTemplateEntryModel) t2).getProduct()).getBrand()==null ? 0:1;
					}
					if(((ApbProductModel)((OrderTemplateEntryModel) t2).getProduct()).getBrand()==null){
						return -1;
					}
					return ((ApbProductModel)((OrderTemplateEntryModel) t1).getProduct()).getBrand().getName()
							.compareTo( ((ApbProductModel)((OrderTemplateEntryModel) t2).getProduct()).getBrand().getName());
				};

				final Comparator<Object> compareByProductName = (t1,t2) ->
				{
					if(((ApbProductModel)((OrderTemplateEntryModel) t1).getProduct()).getBrand()==null) {
						return (((ApbProductModel)((OrderTemplateEntryModel) t2).getProduct()).getBrand()==null) ? 0:1;
					}
					if(((ApbProductModel)((OrderTemplateEntryModel) t2).getProduct()).getBrand()==null){
						return -1;
					}
					//Product Name Null Check
					if(((ApbProductModel)((OrderTemplateEntryModel) t1).getProduct()).getName()==null) {
						return ((ApbProductModel)((OrderTemplateEntryModel) t2).getProduct()).getName()==null ? 0:1;
					}
					if(((ApbProductModel)((OrderTemplateEntryModel) t2).getProduct()).getName()==null) {
						return -1;
					}
					return ((ApbProductModel)((OrderTemplateEntryModel) t1).getProduct()).getName()
							.compareTo( ((ApbProductModel)((OrderTemplateEntryModel) t2).getProduct()).getName());
				};

			    sortedOrdertemplateEntry = unSortedOrdertemplateEntry.stream()
			   		 .sorted(compareByProductBrand.thenComparing(compareByProductName)).collect(Collectors.toList());
			}
			else
			{
				final Comparator<Object> compareByProductBrand = (t1,t2) ->
				{
					if(((ApbProductModel)((OrderTemplateEntryModel) t1).getProduct()).getBrand()==null) {
						return ((ApbProductModel)((OrderTemplateEntryModel) t2).getProduct()).getBrand()==null ? 0:-1;
					}
					if(((ApbProductModel)((OrderTemplateEntryModel) t2).getProduct()).getBrand()==null){
						return 1;
					}
					return ((ApbProductModel)((OrderTemplateEntryModel) t1).getProduct()).getBrand().getName()
							.compareTo( ((ApbProductModel)((OrderTemplateEntryModel) t2).getProduct()).getBrand().getName());
				};

				final Comparator<Object> compareByProductName = (t1,t2) ->
				{
					if(((ApbProductModel)((OrderTemplateEntryModel) t1).getProduct()).getBrand()==null) {
						return (((ApbProductModel)((OrderTemplateEntryModel) t2).getProduct()).getBrand()==null) ? 0:-1;
					}
					if(((ApbProductModel)((OrderTemplateEntryModel) t2).getProduct()).getBrand()==null)
					{
						return 1;
					}
					//Product Name Null Check
					if(((ApbProductModel)((OrderTemplateEntryModel) t1).getProduct()).getName()==null) {
						return ((ApbProductModel)((OrderTemplateEntryModel) t2).getProduct()).getName() == null ? 0:-1;
					}
					if(((ApbProductModel)((OrderTemplateEntryModel) t2).getProduct()).getName() == null) {
						return 1;
					}

					return ((ApbProductModel)((OrderTemplateEntryModel) t1).getProduct()).getName()
						.compareTo( ((ApbProductModel)((OrderTemplateEntryModel) t2).getProduct()).getName());
				};

				sortedOrdertemplateEntry = unSortedOrdertemplateEntry.stream()
						.sorted(compareByProductBrand.thenComparing(compareByProductName)).collect(Collectors.toList());

				Collections.reverse(sortedOrdertemplateEntry);
			}

			orderTemplate.setTemplateEntry(sortedOrdertemplateEntry);

			orderTemplateData = asahiOrderTemplateConverter.convert(orderTemplate);

			if (asahiSiteUtil.isSga() && QUICK_ORDER_TEMPLETE_PREFIX.equalsIgnoreCase(orderTemplate.getName()))
			{
				orderTemplateData.setIsQuickOrder(true);
			}
			if (null == priceResult)
			{
				orderTemplateData.setPriceError(true);
				return orderTemplateData;
			}
			orderTemplateData.setPriceError(false);
			final CurrencyModel currency = null != orderTemplate.getCurrency() ? orderTemplate.getCurrency()
					: commonI18NService.getCurrentCurrency();
			for (final OrderTemplateEntryData entry : orderTemplateData.getTemplateEntry())
			{
				updatePriceForEntry(entry, priceResult.get(entry.getProduct().getCode()), currency);
			}
		}
		if (asahiSiteUtil.isSga())
		{
			inclusionExclusionProductStrategy.updateProductDataTemplate(orderTemplateData);
		}

		return orderTemplateData;
	}

	/**
	 * @return the asahiOrderTemplateConverter
	 */
	public Converter<OrderTemplateModel, OrderTemplateData> getAsahiOrderTemplateConverter()
	{
		return asahiOrderTemplateConverter;
	}

	/**
	 * @param asahiOrderTemplateConverter
	 *           the asahiOrderTemplateConverter to set
	 */
	public void setAsahiOrderTemplateConverter(final Converter<OrderTemplateModel, OrderTemplateData> asahiOrderTemplateConverter)
	{
		this.asahiOrderTemplateConverter = asahiOrderTemplateConverter;
	}

	/**
	 * Delete order template for id.
	 *
	 * @param orderTemplateId
	 *           the order template id
	 *
	 */
	@Override
	public void deleteOrderTemplateForId(final String orderTemplateId)
	{
		final B2BCustomerModel customer = (B2BCustomerModel) getUserService().getCurrentUser();
		if (null != customer.getDefaultB2BUnit())
		{
			final OrderTemplateModel orderTemplate = this.sabMB2BCommerceCartService
					.getOrderTemplateForCodeAndB2BUnit(orderTemplateId, (AsahiB2BUnitModel) customer.getDefaultB2BUnit());
			if (null != orderTemplate)
			{
				getModelService().remove(orderTemplate);
			}
		}
	}

	/**
	 * Delete all entries for order template.
	 *
	 * @param orderTemplateId
	 *           the order template id
	 */
	@Override
	public void deleteAllEntriesForOrderTemplate(final String orderTemplateId)
	{
		final B2BCustomerModel customer = (B2BCustomerModel) getUserService().getCurrentUser();
		if (null != customer.getDefaultB2BUnit())
		{
			final OrderTemplateModel orderTemplate = this.sabMB2BCommerceCartService
					.getOrderTemplateForCodeAndB2BUnit(orderTemplateId, (AsahiB2BUnitModel) customer.getDefaultB2BUnit());
			if (null != orderTemplate)
			{
				orderTemplate.setTemplateEntry(null);
				getModelService().save(orderTemplate);
				getModelService().refresh(orderTemplate);
			}
		}
	}

	/**
	 * Delete order template entry for PK.
	 *
	 * @param orderTemplateEntryPK
	 *           the order template entry PK
	 */
	@Override
	public void deleteOrderTemplateEntryForPK(final String orderTemplateEntryPK)
	{

		final OrderTemplateEntryModel orderTemplateEntry = this.sabMB2BCommerceCartService
				.getOrderTemplateEntryForPK(orderTemplateEntryPK);
		if (null != orderTemplateEntry)
		{
			getModelService().remove(orderTemplateEntry);
		}
	}

	/**
	 * Reorder entries for order template.
	 *
	 * @param orderTemplateId
	 *           the order template id
	 * @param keepCart
	 *           the keep cart
	 */
	@Override
	public void reorderEntriesForOrderTemplate(final String orderTemplateId, final boolean keepCart)
	{
		final B2BCustomerModel customer = (B2BCustomerModel) getUserService().getCurrentUser();
		if (null != customer.getDefaultB2BUnit())
		{
			this.sabMB2BCommerceCartService.reorderEntriesForOrderTemplate(orderTemplateId,
					(AsahiB2BUnitModel) customer.getDefaultB2BUnit(), keepCart);
		}
	}

	@Override
	public void reorderOrderTemplateEntries(final String templateCode, final Map<String, Long> entryQtyMap, final boolean keepCart)
	{

		final B2BCustomerModel customer = (B2BCustomerModel) getUserService().getCurrentUser();
		if (null != customer.getDefaultB2BUnit())
		{
			final OrderTemplateModel orderTemplate = this.defaultSabmCommerceCartDao.getOrderTemplateForCodeAndB2BUnit(templateCode,
					(AsahiB2BUnitModel) customer.getDefaultB2BUnit());

			if (null != orderTemplate && getCartService().hasSessionCart())
			{
				int entryNumber = 0;
				List<AbstractOrderEntryModel> completeEntries = null;
				if (keepCart)
				{
					final List<AbstractOrderEntryModel> entries = getCartService().getSessionCart().getEntries();
					completeEntries = new ArrayList<>(entries);
					entryNumber = entries.size();
				}
				else
				{
					getCartService().removeSessionCart();
					completeEntries = new ArrayList<>();
				}

				completeEntries = this.addEntriesToCurrentCart(entryQtyMap, completeEntries, orderTemplate, entryNumber);

				/*
				 * completeEntries.addAll(getCartService().getSessionCart().getEntries().stream().filter(e ->
				 * e.getIsFreeGood()) .collect(Collectors.toList()));
				 */
				getCartService().getSessionCart().setEntries(completeEntries);
				final CartModel cartModel = getCartService().getSessionCart();

				getModelService().save(cartModel);

				if (asahiSiteUtil.isSga())
				{
					cartModel.getEntries().stream().filter(entry -> BooleanUtils.isFalse(entry.getIsFreeGood())).forEach(entry -> {
						try
						{
							sabmCommerceAddToCartStrategy.removeOrUpdateFreeDealProductOnQtyUpdate(entry.getQuantity(),
									entry.getEntryNumber());
						}
						catch (final CommerceCartModificationException exception)
						{
							logger.error("Could not perform removeOrUpdateFreeDealProduct on adding product from Order Template");
						}
					});
				}
			}
		}
	}

	@Override
	public boolean saveOrderTemplate(final String templateCode, final Map<String, Long> entryQtyMap)
	{

		final B2BCustomerModel customer = (B2BCustomerModel) getUserService().getCurrentUser();
		if (null != customer.getDefaultB2BUnit())
		{
			final OrderTemplateModel orderTemplate = this.defaultSabmCommerceCartDao.getOrderTemplateForCodeAndB2BUnit(templateCode,
					(AsahiB2BUnitModel) customer.getDefaultB2BUnit());

			if (null != orderTemplate)
			{
				final List<OrderTemplateEntryModel> entries = orderTemplate.getTemplateEntry();
				if (CollectionUtils.isNotEmpty(entries))
				{
					for (final OrderTemplateEntryModel entry : entries)
					{
						final long quantity = entryQtyMap.get(entry.getPk().toString());
						entry.setTotalPrice(entry.getTotalPrice() * quantity);
						entry.setBasePrice(entry.getBasePrice());
						entry.setQuantity(quantity);
					}
				}
				getModelService().saveAll(entries);
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean addProductToOrderTemplate(final String templateCode, final String productCode, final Long quantity,
			final boolean existingTemplate) throws DuplicateUidException
	{

		final B2BCustomerModel customer = (B2BCustomerModel) getUserService().getCurrentUser();
		if (null != customer.getDefaultB2BUnit())
		{
			if (existingTemplate)
			{
				final OrderTemplateModel orderTemplate = this.defaultSabmCommerceCartDao
						.getOrderTemplateForCodeAndB2BUnit(templateCode, (AsahiB2BUnitModel) customer.getDefaultB2BUnit());

				if (null != orderTemplate)
				{
					if (addorUpdateOrderTemplateEntry(productCode, quantity, orderTemplate))
					{
						return true;
					}
				}
			}

			else
			{
				final List<OrderTemplateModel> orderTemplateCodeAndB2BUnit = this.sabMB2BCommerceCartService
						.getCartForCodeAndB2BUnit(templateCode, customer.getDefaultB2BUnit());
				final boolean isSga = asahiSiteUtil.isSga();
				if (CollectionUtils.isNotEmpty(orderTemplateCodeAndB2BUnit) && orderTemplateCodeAndB2BUnit.size() > 0)
				{
					throw new DuplicateUidException(templateCode);
				}

				final OrderTemplateModel orderTemplate = getModelService().create(OrderTemplateModel.class);

				final Date currentDate = this.timeService.getCurrentTime();

				orderTemplate.setSaveTime(currentDate);
				orderTemplate.setExpirationTime(this.calculateExpirationTime(currentDate));
				orderTemplate.setSavedBy(customer);
				orderTemplate.setUser(customer);
				orderTemplate.setName(templateCode);
				orderTemplate.setDescription(templateCode);
				orderTemplate.setDate(currentDate);
				orderTemplate.setCurrency(this.cmsSiteService.getCurrentSite().getStores().get(0).getDefaultCurrency());
				orderTemplate.setB2bUnit((AsahiB2BUnitModel) customer.getDefaultB2BUnit());
				final int entryNumber = 0;
				final List<OrderTemplateEntryModel> entries = new ArrayList<>();
				final OrderTemplateEntryModel templateEntry = getModelService().create(OrderTemplateEntryModel.class);

				if (addorUpdateOrderTemplateEntry(productCode, quantity, orderTemplate))
				{
					getModelService().save(orderTemplate);
					getModelService().refresh(orderTemplate);
					return true;
				}
			}

		}

		return false;
	}

	private boolean addorUpdateOrderTemplateEntry(final String productCode, final Long quantity,
			final OrderTemplateModel orderTemplate)
	{

		final ProductModel productModel = productService.getProductForCode(productCode);

		if (null != productModel)
		{

			int entryNumber = 0;
			final List<OrderTemplateEntryModel> entries = new ArrayList<>();

			if (CollectionUtils.isNotEmpty(orderTemplate.getTemplateEntry()))
			{
				entries.addAll(orderTemplate.getTemplateEntry());
				entryNumber = entries.size();
			}

			final OrderTemplateEntryModel templateEntry = getEntryIfProductExist(productModel, orderTemplate);
			if (templateEntry.getProduct() != null)
			{
				final Long qty = templateEntry.getQuantity() + quantity;

				final BigDecimal basePrice = BigDecimal.valueOf(templateEntry.getBasePrice());

				final BigDecimal totlaPrice = basePrice.multiply(new BigDecimal(qty));

				templateEntry.setQuantity(qty);
				templateEntry.setTotalPrice(totlaPrice.doubleValue());
				getModelService().save(templateEntry);
				return true;
			}
			else
			{
				final AsahiProductInfo product = asahiCoreUtil.getProductFromSessionInclusionList(productCode);
				if (product != null)
				{
					final BigDecimal basePrice = BigDecimal
							.valueOf((product.getListPrice() != null
									? product.getListPrice()
											+ (product.getContainerDepositLevy() != null ? product.getContainerDepositLevy() : 0.0D)
									: 0.0D));

					final BigDecimal qty = new BigDecimal(quantity);

					final BigDecimal totalPrice = basePrice.multiply(qty);
					templateEntry.setTotalPrice(totalPrice.doubleValue());
					templateEntry.setBasePrice(basePrice.doubleValue());
					templateEntry.setQuantity(quantity);
					templateEntry.setEntryNumber(entryNumber);

					if (null != productModel)
					{
						templateEntry.setProduct(productModel);
					}

					entries.add(templateEntry);
					orderTemplate.setTemplateEntry(entries);
					getModelService().saveAll(entries);
					getModelService().save(orderTemplate);
					return true;
				}
			}
		}

		return false;
	}


	private OrderTemplateEntryModel getEntryIfProductExist(final ProductModel product, final OrderTemplateModel template)
	{
		final List<OrderTemplateEntryModel> entries = template.getTemplateEntry();
		if (CollectionUtils.isNotEmpty(entries))
		{
			final Optional<OrderTemplateEntryModel> entry = entries.stream().filter(e -> product.equals(e.getProduct())).findFirst();

			if (entry.isPresent())
			{
				return entry.get();
			}
		}
		return getModelService().create(OrderTemplateEntryModel.class);
	}

	/**
	 * Adds the entries to current cart.
	 *
	 * @param templateEntries
	 * @param completeEntries
	 *
	 * @param orderTemplate
	 *           the order template
	 * @param entryNumber
	 *           the entry number
	 * @return
	 */
	private List<AbstractOrderEntryModel> addEntriesToCurrentCart(final Map<String, Long> entryQtyMap,
			final List<AbstractOrderEntryModel> completeEntries, final OrderTemplateModel orderTemplate, int entryNumber)
	{
		if (null != orderTemplate && CollectionUtils.isNotEmpty(orderTemplate.getTemplateEntry()))
		{

			for (final OrderTemplateEntryModel templateEntry : orderTemplate.getTemplateEntry())
			{
				final CartModel cartModel = getCartService().getSessionCart();
				final List<CartEntryModel> entriesList = getCartService().getEntriesForProduct(cartModel, templateEntry.getProduct());

				//Check if the Non Bonus Product exists in the cart...
				final CartEntryModel entry = this.sabMB2BCommerceCartService.getNonBonusEntry(entriesList);
				if (entryQtyMap.get(templateEntry.getPk().toString()) > 0)
				{
					if (null != entry)
					{
						final CartEntryModel cartEntry = entry;
						final long oldQty = cartEntry.getQuantity();
						long newQty = oldQty;

						final Long maxProductQty = asahiSiteUtil.getSgaGlobalMaxOrderQty();
						if (asahiSiteUtil.isSga() && (entryQtyMap.get(templateEntry.getPk().toString()) > (maxProductQty - oldQty)))
						{
							newQty = oldQty + (maxProductQty - oldQty);
						}
						else
						{
							newQty = oldQty + entryQtyMap.get(templateEntry.getPk().toString());
						}

						cartEntry.setQuantity(newQty);

						getModelService().save(cartEntry);

					}
					else
					{
						final ApbProductModel product = (ApbProductModel) templateEntry.getProduct();
						if (null != product && (this.inclusionExclusionProductStrategy.isProductIncluded(product.getCode())
								&& product.isActive() && product.getApprovalStatus().equals(ArticleApprovalStatus.APPROVED)))
						{
							final CartEntryModel newCartEntry = getModelService().create(CartEntryModel.class);

							newCartEntry.setEntryNumber(++entryNumber);
							newCartEntry.setBasePrice(templateEntry.getBasePrice());
							newCartEntry.setTotalPrice(templateEntry.getTotalPrice());
							newCartEntry.setQuantity((long) entryQtyMap.get(templateEntry.getPk().toString()));

							final ProductModel productModel = templateEntry.getProduct();
							newCartEntry.setProduct(productModel);

							if (null != productModel.getUnit())
							{
								newCartEntry.setUnit(productModel.getUnit());
							}
							else
							{
								newCartEntry.setUnit(getProductService().getOrderableUnit(productModel));
							}

							final AbstractOrderModel orderModel = cartModel;
							newCartEntry.setOrder(orderModel);

							completeEntries.add(newCartEntry);
						}
					}
				}
			}
		}
		return completeEntries;
	}

	private Map<String, PriceInfo> createProductPriceMap(final PriceInfoData priceData)
	{
		final Map<String, PriceInfo> productPriceMap = new HashMap<>();
		if (null != priceData && null != priceData.getProductPriceInfo())
		{
			for (final PriceInfo productPrice : priceData.getProductPriceInfo())
			{
				productPriceMap.put(productPrice.getCode(), productPrice);
			}
			return productPriceMap;
		}
		return null;
	}

	private Map<String, Long> createMapFromProductList(final List<String> products)
	{
		final Map<String, Long> productQuantityMap = new HashMap<>();
		for (final String productCode : products)
		{
			productQuantityMap.put(productCode, 1L);
		}
		return productQuantityMap;
	}

	private void updatePriceForEntry(final OrderTemplateEntryData entry, final PriceInfo priceInfo, final CurrencyModel currency)
	{
		if (null != priceInfo && null != entry.getQuantity())
		{
			entry.setBasePrice(asahiSiteUtil.isSga() ? priceInfo.getListPrice().getValue().doubleValue()
					: priceInfo.getNetPrice().getValue().doubleValue());
			entry.setTotalPrice(entry.getQuantity() * entry.getBasePrice());
			entry.setTemplateTotalPrice(
					priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(entry.getTotalPrice()), currency));
			entry.setTemplateBasePrice(
					priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(entry.getBasePrice()), currency));
			entry.setPriceUpdated(true);
		}
		else
		{
			entry.setPriceUpdated(false);
		}
	}

}
