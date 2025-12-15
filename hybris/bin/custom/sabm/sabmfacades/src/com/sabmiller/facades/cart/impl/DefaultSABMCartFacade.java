/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.sabmiller.facades.cart.impl;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.order.EntryGroupData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.CartRestorationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.impl.DefaultCartFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.services.BaseStoreService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.deals.strategies.AsahiDealValidationStrategy;
import com.apb.core.model.ApbProductModel;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.comparator.order.entry.AsahiOrderEntryDataComparator;
import com.apb.facades.stock.check.ApbStockOnHandFacade;
import com.apb.integration.data.ApbStockonHandProductData;
import com.apb.product.strategy.AsahiInclusionExclusionProductStrategy;
import com.google.common.collect.Lists;
import com.sabmiller.core.b2b.services.SABMDeliveryDateCutOffService;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.cart.service.SABMB2BCommerceCartService;
import com.sabmiller.core.cart.service.SABMCalculationService;
import com.sabmiller.core.cart.service.SABMCartService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.customer.service.SABMUserFlagService;
import com.sabmiller.core.deals.dao.DealsDao;
import com.sabmiller.core.deals.services.DealConditionService;
import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.core.deals.services.response.ConflictGroup;
import com.sabmiller.core.deals.services.response.ConflictGroup.Conflict;
import com.sabmiller.core.deals.services.response.DealQualificationResponse;
import com.sabmiller.core.enums.DealConditionStatus;
import com.sabmiller.core.enums.OrderSimulationStatus;
import com.sabmiller.core.enums.PackType;
import com.sabmiller.core.enums.SmartRecommendationType;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.CartDealConditionModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.DeliveryDefaultAddressModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.core.model.SabmCartRuleModel;
import com.sabmiller.core.model.ShippingCarrierModel;
import com.sabmiller.core.order.impl.SabmCommerceAddToCartStrategy;
import com.sabmiller.core.util.SabmDateUtils;
import com.sabmiller.facades.cart.SABMCartFacade;
import com.sabmiller.facades.deal.data.CartDealsJson;
import com.sabmiller.facades.deal.data.ConflictDealJson;
import com.sabmiller.facades.deal.data.DealJson;


/**
 * DefaultSabCartFacade.
 *
 * @author yaopeng
 */
public class DefaultSABMCartFacade extends DefaultCartFacade implements SABMCartFacade
{

	/** The Constant LOG. */
	protected static final Logger LOG = LoggerFactory.getLogger(DefaultSABMCartFacade.class);
	@Resource(name = "defaultSabmUnitService")
	private UnitService unitService;
	@Resource(name = "b2bCommerceUnitService")
	private B2BCommerceUnitService b2bCommerceUnitService;
	@Resource(name = "cartService")
	private SABMCartService sabmCartService;
	@Resource(name = "dealsService")
	private DealsService dealsService;
	@Resource(name = "modelService")
	private ModelService modelService;
	@Resource(name = "userFlagService")
	private SABMUserFlagService userFlagService;

	@Resource(name = "orderEntryConverter")
	private Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;

	@Value(value = "${max.entries.minicart:10}")
	private int maxEntriesMinicart;

	@Resource(name = "dealTitlePopulator")
	private Populator<List<DealModel>, DealJson> dealTitlePopulator;

	@Resource(name = "dealProductPopulator")
	private Populator<List<DealModel>, DealJson> dealProductPopulator;

	@Resource(name = "dealJsonPopulator")
	private Populator<List<DealModel>, DealJson> dealJsonPopulator;

	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;

	@Resource(name = "addressConverter")
	private Converter<AddressModel, AddressData> addressConverter;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "dealConditionService")
	private DealConditionService dealConditionService;

	@Resource(name = "conflictDealJsonConverter")
	private Converter<List<DealModel>, ConflictDealJson> conflictDealJsonConverter;

	@Resource(name = "dealAppliedTimesHelper")
	private DealAppliedTimesHelper dealAppliedTimesHelper;

	@Resource(name = "calculationService")
	private SABMCalculationService calculationService;

	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;

	@Resource(name = "apbStockOnHandFacade")
	private ApbStockOnHandFacade apbStockOnHandFacade;

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Resource
	private ProductService productService;

	@Resource
	private CommerceStockService commerceStockService;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Resource(name = "inclusionExclusionProductStrategy")
	private AsahiInclusionExclusionProductStrategy inclusionExclusionProductStrategy;

	@Resource(name = "cmsSiteService")
	private CMSSiteService cmsSiteService;

	@Resource(name = "siteConfigService")
	private SiteConfigService siteConfigService;

	/** The asahi core util. */
	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	@Resource(name = "sabmCommerceAddToCartStrategy")
	private SabmCommerceAddToCartStrategy sabmCommerceAddToCartStrategy;

	@Resource
	private SABMDeliveryDateCutOffService sabmDeliveryDateCutOffService;

	@Resource(name = "asahiDealValidationStrategy")
	private AsahiDealValidationStrategy asahiDealValidationStrategy;

	/** The deals dao. */
	@Resource(name = "dealsDao")
	private DealsDao dealsDao;

	public static final String NON_ALCOHOLIC_TYPE = "product.code.non.alcoholic.product.apb";

	public static final String CREDIT_SURCHARGE_FOR_AMEX = "credit.surcharge.for.amex.card.";
	public static final String CREDIT_SURCHARGE_FOR_VISA = "credit.surcharge.for.visa.card.";
	public static final String CREDIT_SURCHARGE_FOR_MASTER = "credit.surcharge.for.master.card.";
	public static final String IS_ADD_SURCHARGE = "payment.apply.credit.surcharge.";
	public static final String MAX_PACKED_PRODUCT_ORDER_QTY = "max.packed.product.order.qty.";
	public static final String MAX_BIB_PRODUCT_ORDER_QTY = "max.bib.product.order.qty.";

	public static final String MAX_MIX_PACKED_PRODUCT_ORDER_QTY = "max.mix.packed.product.order.qty.";
	public static final String MAX_MIX_BIB_PRODUCT_ORDER_QTY = "max.mix.bib.product.order.qty.";
	protected static final int APPEND_AS_LAST = -1;
	/*
	 * Override addToCart add new param unit from SABMAddToCartForm.unit
	 *
	 * @see com.sabmiller.facades.cart.SABMCartFacade#addToCart(java.lang.String, java.lang.String, long)
	 */
	@Override
	public CartModificationData addToCart(final String code, final String fromUnit, final long quantity)
			throws CommerceCartModificationException
	{
		final ProductModel product = getProductService().getProductForCode(code);
		final CartModel cartModel = getCartService().getSessionCart();
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		parameter.setQuantity(quantity);
		parameter.setProduct(product instanceof SABMAlcoholVariantProductMaterialModel
				? ((SABMAlcoholVariantProductMaterialModel) product).getBaseProduct()
				: product);
		parameter.setCreateNewEntry(false);

		//fund UnitModel from selected Unit.code
		UnitModel unitm = null;
		try
		{
			unitm = unitService.getUnitForCode(StringUtils.upperCase(fromUnit));
		}
		catch (final UnknownIdentifierException | AmbiguousIdentifierException e)
		{
			LOG.warn("Unit with code " + fromUnit + " not found! " + e, e);
		}

		if (unitm != null)
		{
			LOG.debug("Setting unit {} to the cart parameter ", unitm);
			parameter.setUnit(unitm);
		}
		else
		{
			//If no unit is found, usisng the default from the product
			LOG.debug("Unit not found. Fetching the default unit {} from the product ", product.getUnit());
			parameter.setUnit(product.getUnit());
		}
		final CommerceCartModification modification = getCommerceCartService().addToCart(parameter);

		LOG.debug("unit in cart modification entry is : {} ", modification.getEntry().getUnit());
		return getCartModificationConverter().convert(modification);
	}

	@Override
	public CartModificationData addToCart(final String code, final long quantity) throws CommerceCartModificationException
	{
		if (asahiSiteUtil.isCub())
		{

			final ProductModel product = getProductService().getProductForCode(code);
			final CartModel cartModel = getCartService().getSessionCart();
			final CommerceCartParameter parameter = new CommerceCartParameter();
			parameter.setEnableHooks(true);
			parameter.setCart(cartModel);
			parameter.setQuantity(quantity);
			parameter.setProduct(product instanceof SABMAlcoholVariantProductMaterialModel
					? ((SABMAlcoholVariantProductMaterialModel) product).getBaseProduct()
					: product);
			parameter.setUnit(product.getUnit());
			parameter.setCreateNewEntry(false);

			final CommerceCartModification modification = getCommerceCartService().addToCart(parameter);

			return getCartModificationConverter().convert(modification);
		}
		else
		{
			return super.addToCart(code, quantity);
		}
	}

	/*
	 * Override updateCartEntry add new param unit from SABMAddToCartForm.unit
	 *
	 * @see com.sabmiller.facades.cart.SABMCartFacade#updateCartEntry(long, long, java.lang.String)
	 */
	@Override
	public CartModificationData updateCartEntry(final long entryNumber, final long quantity, final String fromUnit)
			throws CommerceCartModificationException
	{
		return updateCartEntry(getCartService().getSessionCart(), entryNumber, quantity, fromUnit);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.cart.SABMCartFacade#updateCartEntry(de.hybris.platform.core.model.order.CartModel,
	 * long, long, java.lang.String)
	 */
	@Override
	public CartModificationData updateCartEntry(final CartModel cartModel, final long entryNumber, final long quantity,
			final String fromUnit) throws CommerceCartModificationException
	{
		final CartEntryModel cartEntryModel = getCartService().getEntryForNumber(cartModel,
				Integer.parseInt(String.valueOf(entryNumber)));
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		parameter.setEntryNumber(entryNumber);
		parameter.setQuantity(quantity);

		if (cartEntryModel != null)
		{
			parameter.setProduct(cartEntryModel.getProduct());
		}

		//fund UnitModel from selected Unit.code
		UnitModel unitm = null;
		try
		{
			if (StringUtils.isNotEmpty(fromUnit))
			{
				unitm = unitService.getUnitForCode(fromUnit);
			}
		}
		catch (final UnknownIdentifierException | AmbiguousIdentifierException e)
		{
			LOG.warn("Unit with code " + fromUnit + " not found! " + e, e);
		}

		if (unitm != null)
		{
			parameter.setUnit(unitm);
		}
		else
		{
			//If no unit is found, using the default from the product, if the product is null ,use the unit in the cart entry

			if (cartEntryModel != null)
			{
				if (cartEntryModel.getProduct() != null)
				{
					parameter.setUnit(cartEntryModel.getProduct().getUnit());
				}
				else
				{
					parameter.setUnit(cartEntryModel.getUnit());
				}
			}

		}

		final CommerceCartModification modification = getCommerceCartService().updateQuantityForCartEntry(parameter);

		return getCartModificationConverter().convert(modification);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.cart.SABMCartFacade#saveDeliveryInstructions(java.lang.String)
	 */
	@Override
	public void saveDeliveryInstructions(final String deliveryInstructions)
	{
		if (getCartService().hasSessionCart())
		{
			sabmCartService.saveDeliveryInstructions(deliveryInstructions, getCartService().getSessionCart());
		}
	}

	@Override
	public void savePurchaseOrderNumber(final String poNumber)
	{
		if (getCartService().hasSessionCart())
		{
			sabmCartService.savePurchaseOrderNumber(poNumber, getCartService().getSessionCart());
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.cart.SABMCartFacade#saveRequestedDeliveryDate(java.util.Date)
	 */
	@Override
	public boolean saveRequestedDeliveryDate(final Date date, final String packType)
	{
		return sabmCartService.saveRequestedDeliveryDate(date, packType);
	}



	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.cart.SABMCartFacade#saveShippingCarriers(java.lang.String,
	 * de.hybris.platform.commercefacades.user.data.CustomerData)
	 */
	@Override
	public boolean saveShippingCarriers(final String shippingCarrierCode)

	{
		if (getCartService().hasSessionCart())
		{
			final ShippingCarrierModel shippingCarrier = getShippingCarrierModelForCode(shippingCarrierCode);

			final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
			parameter.setEnableHooks(true);
			parameter.setCart(getCartService().getSessionCart());
			parameter.setShippingCarrier(shippingCarrier);
			return sabmCartService.setShippingCarrier(parameter);
		}
		return false;
	}

	/*
	 * Save the cart shipping carrier as default carrier only in case if the default carrier is not customer owned. In
	 * case if default carrier is customer owned, loop through all the shipping carriers and select first not customer
	 * owned.
	 *
	 */
	@Override
	public boolean saveDefaultShippingCarriers()

	{
		if (getCartService().hasSessionCart())
		{
			final CartModel cartModel = getCartService().getSessionCart();
			B2BUnitModel b2bUnitModel = b2bCommerceUnitService.getParentUnit();
			ShippingCarrierModel shippingCarrier = null;
			final B2BUnitModel b2bUnitForAlternativeAddress = getUnitBasedForAlternativeAddress();
			b2bUnitModel = null != b2bUnitForAlternativeAddress ? b2bUnitForAlternativeAddress : b2bUnitModel;
			if (b2bUnitModel != null && b2bUnitModel.getDefaultCarrier() != null)
			{

				final ShippingCarrierModel defaultCarrier = b2bUnitModel.getDefaultCarrier();
				shippingCarrier = defaultCarrier;
				if (defaultCarrier.getCustomerOwned())
				{
					for (final ShippingCarrierModel carrier : b2bUnitModel.getShippingCarriers())
					{
						if (!carrier.getCustomerOwned())
						{
							shippingCarrier = carrier;
							break;
						}
					}
				}
				final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
				parameter.setEnableHooks(true);
				parameter.setCart(cartModel);
				parameter.setShippingCarrier(shippingCarrier);

				return sabmCartService.setShippingCarrier(parameter);
			}

		}
		return false;
	}

	private B2BUnitModel getUnitBasedForAlternativeAddress()
	{
		B2BUnitModel b2bUnitModel = null;
		if (getCartService().hasSessionCart())
		{
			final CartModel cartModel = getCartService().getSessionCart();
			if (null != cartModel && null != cartModel.getDeliveryAddress()
					&& null != cartModel.getDeliveryAddress().getPartnerNumber())
			{
				b2bUnitModel = b2bUnitService.getUnitForUid(cartModel.getDeliveryAddress().getPartnerNumber());
			}
		}

		return b2bUnitModel;
	}
	/**
	 * Gets the shipping carrier model for code from the session customer.
	 *
	 * @param code
	 *           the code
	 * @return the shipping carrier model for code
	 */
	protected ShippingCarrierModel getShippingCarrierModelForCode(final String code)
	{
		Assert.notNull(code, "Parameter code cannot be null.");

		final B2BUnitModel b2bUnitModel = b2bCommerceUnitService.getParentUnit();

		return getShippingCarrierModelForCode(code, b2bUnitModel);
	}

	protected ShippingCarrierModel getShippingCarrierModelForCode(final String shippingCarrier, final B2BUnitModel b2bUnitModel)
	{
		if (null != b2bUnitModel)
		{
			if (CollectionUtils.isNotEmpty(b2bUnitModel.getShippingCarriers()))
			{
				for (final ShippingCarrierModel shipcarModel : b2bUnitModel.getShippingCarriers())
				{
					if (shipcarModel.getCarrierCode().equals(shippingCarrier))
					{
						return shipcarModel;
					}
				}
			}
		}

		return null;
	}

	// Remove the logic of delete free product. change the logic to DefaultSABMCommerceUpdateCartEntryStrategy.
	// Because of the delete free product and delete the basic product entry are the same Transaction. SAB-2779

	/**
	 * Override the OOTB implementation to restore the cart based on the selected B2BUnit. OOTB, the cart is fetched for
	 * an user and not by the company (B2BUnit). Every time B2BUnit is changed, this method needs to be invoked so that
	 * the cart specific to the B2BUnit can be fetched and persisted in the session
	 */
	@Override
	public CartRestorationData restoreSavedCart(final String guid) throws CommerceCartRestorationException
	{
		if (asahiSiteUtil.isCub())
		{
			LOG.info("In restoreSavedCart(). guid : {} ", guid);

			if (!hasEntries())
			{
				getCartService().setSessionCart(null);
			}

			final CommerceCartParameter parameter = new CommerceCartParameter();
			parameter.setEnableHooks(true);

			CartModel cart = null;

			if (guid != null)
			{
				cart = getCommerceCartService().getCartForGuidAndSiteAndUser(guid, getBaseSiteService().getCurrentBaseSite(),
						getUserService().getCurrentUser());
			}
			else
			{
				final B2BUnitModel currentB2BUnit = b2bCommerceUnitService.getParentUnit();
				cart = ((SABMB2BCommerceCartService) getCommerceCartService()).getCartForSiteAndUserAndB2BUnit(
						getBaseSiteService().getCurrentBaseSite(), getUserService().getCurrentUser(), currentB2BUnit);

				LOG.debug("Restored cart for B2BUnit Id {} ", currentB2BUnit.getUid());
			}

			parameter.setCart(cart);

			return getCartRestorationConverter().convert(getCommerceCartService().restoreCart(parameter));
		}
		else
		{
			return super.restoreSavedCart(guid);
		}
	}


	@Override
	public CartData getSessionCartWithEntryOrdering(final boolean recentlyAddedFirst)
	{
		if (hasSessionCart())
		{
			if (!asahiSiteUtil.isCub())
			{
				final CartData data = getSessionCartPage(recentlyAddedFirst);
				if (recentlyAddedFirst)
				{
					final List<OrderEntryData> recentlyAddedListEntries = new ArrayList<>(data.getEntries());
					Collections.reverse(recentlyAddedListEntries);
					data.setEntries(Collections.unmodifiableList(recentlyAddedListEntries));
					final List<EntryGroupData> recentlyChangedEntryGroups = new ArrayList<>(data.getRootGroups());
					Collections.reverse(recentlyChangedEntryGroups);
					data.setRootGroups(Collections.unmodifiableList(recentlyChangedEntryGroups));
				}

				return data;
			}
			else
			{
				return getSessionCart();
			}

		}
		return createEmptyCart();
	}

	@Override
	public void updateCartForPrice()
	{
		final CartModel cart = getCartService().getSessionCart();
		for (final AbstractOrderEntryModel e : cart.getEntries())
		{
			e.setCalculated(false);
			getModelService().save(e);
		}
		cart.setCalculated(false);
		getModelService().save(cart);
		try
		{
			calculationService.calculate(cart);
		}
		catch (final CalculationException exp)
		{
			LOG.error("error while calculating cart calculation service", exp);
		}
	}

	@Override
	public CartData getSessionCartPage(final boolean requireCartCalculation)
	{

		final CartData cartData;
		if (hasSessionCart())
		{
			final CartModel cart = getCartService().getSessionCart();
			setUnit(cart);
			final boolean isProductsRemove = removeUnLicensedProduct(cart);
			//cart calculation is based on the 'requireCartCalculation' flag
			if (requireCartCalculation)
			{
				for (final AbstractOrderEntryModel e : cart.getEntries())
				{
					e.setCalculated(false);
					getModelService().save(e);
				}
				cart.setCalculated(false);
				getModelService().save(cart);
				try
				{
					calculationService.calculate(cart);
				}
				catch (final CalculationException exp)
				{
					LOG.error("error while calculating cart calculation service", exp);
				}
				updateCartForPrice();
			}
			cartData = getCartConverter().convert(cart);
			if (isProductsRemove)
			{
				cartData.setLicenseRequired(true);
			}

			if (asahiSiteUtil.isSga())
			{
				inclusionExclusionProductStrategy.updateProductCartData(cartData);
			}

		}
		else
		{
			cartData = createEmptyCart();
		}
		return cartData;
	}

	@Override
	public CartData getSessionCartPage()
	{
		final CartData cartData;
		if (hasSessionCart())
		{
			final CartModel cart = getCartService().getSessionCart();
			setUnit(cart);
			final boolean isProductsRemove = removeUnLicensedProduct(cart);
			for (final AbstractOrderEntryModel e : cart.getEntries())
			{
				e.setCalculated(false);
				getModelService().save(e);
			}
			cart.setCalculated(false);
			getModelService().save(cart);
			try
			{
				calculationService.calculate(cart);
			}
			catch (final CalculationException exp)
			{
				LOG.error("error while calculating cart calculation service", exp);
			}
			cartData = getCartConverter().convert(cart);
			if (isProductsRemove)
			{
				cartData.setLicenseRequired(true);
			}
			if (asahiSiteUtil.isSga())
			{
				inclusionExclusionProductStrategy.updateProductCartData(cartData);
			}
		}
		else
		{
			cartData = createEmptyCart();
		}

		return cartData;
	}

	@Override
	public CartData getSessionCart()
	{
		if (!asahiSiteUtil.isCub())
		{
			final CartData cartData;
			if (hasSessionCart())
			{
				final CartModel cart = getCartService().getSessionCart();
				setUnit(cart);
				for (final AbstractOrderEntryModel e : cart.getEntries())
				{
					e.setCalculated(false);
					getModelService().save(e);
				}
				cart.setCalculated(false);
				getModelService().save(cart);
				try
				{
					calculationService.calculate(cart);
				}
				catch (final CalculationException exp)
				{
					LOG.error("error while calculating cart calculation service", exp);
				}
				cartData = getCartConverter().convert(cart);
				if (asahiSiteUtil.isSga())
				{
					inclusionExclusionProductStrategy.updateProductCartData(cartData);
				}
			}
			else
			{
				cartData = createEmptyCart();
			}

			return cartData;
		}
		else
		{
			return super.getSessionCart();
		}
	}

	@Override
	public CartData getSessionCartWithCreditSurcharge(final CreditCardType cardType, final String paymentMethod)
	{
		final CartData cartData;
		String surcharge = null;
		if (!isAddSurcharge() || "ACCOUNT".equalsIgnoreCase(paymentMethod))
		{
			surcharge = "0";
		}
		else if (cardType.equals(CreditCardType.AMEX))
		{
			surcharge = asahiConfigurationService.getString(CREDIT_SURCHARGE_FOR_AMEX + cmsSiteService.getCurrentSite().getUid(),
					"10");
		}
		else if (cardType.equals(CreditCardType.VISA))
		{
			surcharge = asahiConfigurationService.getString(CREDIT_SURCHARGE_FOR_VISA + cmsSiteService.getCurrentSite().getUid(),
					"10");
		}
		else
		{
			surcharge = asahiConfigurationService.getString(CREDIT_SURCHARGE_FOR_MASTER + cmsSiteService.getCurrentSite().getUid(),
					"10");
		}
		final double surchargeValue = Double.parseDouble(surcharge);
		if (hasSessionCart())
		{
			final CartModel cart = getCartService().getSessionCart();
			setUnit(cart);
			for (final AbstractOrderEntryModel e : cart.getEntries())
			{
				e.setCalculated(false);
				getModelService().save(e);
			}
			cart.setCalculated(false);
			getModelService().save(cart);
			try
			{
				calculationService.calculate(cart);

				final double totalPrice = cart.getTotalPrice();
				double totalTax = 0;
				double total = 0;

				if (null != cart.getOrderGST())
				{
					totalTax = cart.getOrderGST();
				}

				if (null != cart.getTotalPrice())
				{
					total = totalPrice;
				}

				if (cart.getOrderGST() != null)
				{
					total = totalPrice + totalTax;
				}
				final double creditSurcharge = (total * surchargeValue) / 100;
				final BigDecimal creditSurchargeVal = BigDecimal.valueOf(creditSurcharge);
				final double creditValue = creditSurchargeVal.setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
				cart.setCreditSurCharge(creditValue);
				cart.setTotalPrice(totalPrice + creditValue);


			}
			catch (final CalculationException exp)
			{
				LOG.error("error while calculating cart calculation service", exp);
			}
			cartData = getCartConverter().convert(cart);
			if (asahiSiteUtil.isSga())
			{
				inclusionExclusionProductStrategy.updateProductCartData(cartData);
			}

		}
		else
		{
			cartData = createEmptyCart();
		}
		return cartData;
	}

	@Override
	public boolean isAddSurcharge()
	{
		return Boolean.parseBoolean(
				asahiConfigurationService.getString(IS_ADD_SURCHARGE + cmsSiteService.getCurrentSite().getUid(), "true"));
	}

	@Override
	public void removeAllProducts(final RedirectAttributes redirectModel)
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		if (null != getSessionCart() && !getSessionCart().getEntries().isEmpty())
		{
			parameter.setCart(getCartService().getSessionCart());
			getCommerceCartService().removeAllEntries(parameter);
		}
	}

	private boolean removeUnLicensedProduct(final CartModel cart)
	{
		boolean isProductsRemove = false;

		final UserModel user = getUserService().getCurrentUser();
		if (null != user && user instanceof B2BCustomerModel)
		{
			final B2BCustomerModel customer = (B2BCustomerModel) user;
			final B2BUnitModel b2bUnit = customer.getDefaultB2BUnit();
			if (b2bUnit instanceof AsahiB2BUnitModel)
			{
				final AsahiB2BUnitModel asahiB2BUnit = (AsahiB2BUnitModel) b2bUnit;

				final String licenseNumber = asahiB2BUnit.getLiquorLicensenumber();
				if (StringUtils.isEmpty(licenseNumber))
				{
					final List<AbstractOrderEntryModel> entires = cart.getEntries();
					if (CollectionUtils.isNotEmpty(entires))
					{
						final String nonAlcoholicType = asahiConfigurationService.getString(NON_ALCOHOLIC_TYPE, "10");

						final List<String> nonAlcoholicTypeList = new ArrayList<>(Arrays.asList(nonAlcoholicType.split(",")));
						final List<AbstractOrderEntryModel> updatedEntries = new ArrayList<>();
						;
						for (final AbstractOrderEntryModel entry : entires)
						{
							final ApbProductModel product = (ApbProductModel) entry.getProduct();
							if (product.getAlcoholType() != null && !(nonAlcoholicTypeList.contains(product.getAlcoholType().getCode())))
							{
								isProductsRemove = true;
							}
							else
							{
								if(!asahiSiteUtil.isSga()) {
									updatedEntries.add(entry);
								}
								if (asahiSiteUtil.isSga() && (nonFreeGoodsEntry(entry, cart)
										|| (StringUtils.isBlank(entry.getFreeGoodsForDeal()) || !entry.getFreeGoodsForDeal()
												.equalsIgnoreCase(ApbCoreConstants.EXPIRED_OR_INVALID_FREEGOODS_ENTRY))))
								{
									updatedEntries.add(entry);
								}
							}
						}
						cart.setEntries(updatedEntries);
						getModelService().save(cart);

					}
				}
			}
		}
		return isProductsRemove;
	}

	/**
	 * @param entry
	 * @param cart
	 * @return
	 */
	private boolean nonFreeGoodsEntry(final AbstractOrderEntryModel entry, final CartModel cart)
	{
		if (BooleanUtils.isFalse(entry.getIsFreeGood()))
		{

			if (StringUtils.isBlank(entry.getFreeGoodEntryNumber())){
				return true;
			}
			else
			{
				if (!asahiDealValidationStrategy.validateDeal(dealsDao.getSgaDealByCode(entry.getAsahiDealCode())))
				{
					try
					{
						final CartEntryModel freeCartEntry = getCartService().getEntryForNumber(cart,
								Integer.parseInt(String.valueOf(entry.getFreeGoodEntryNumber())));
						freeCartEntry.setFreeGoodsForDeal(ApbCoreConstants.EXPIRED_OR_INVALID_FREEGOODS_ENTRY);
						getModelService().save(freeCartEntry);

					}
					catch (final Exception ex)
					{
						LOG.error(
								"FreeGoodsDealEntry is no longer available as it is already removed as part of previous cart calculation");
					}
					entry.setFreeGoodEntryNumber(StringUtils.EMPTY);
					entry.setAsahiDealCode(StringUtils.EMPTY);
					getModelService().save(entry);
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public void updateStockEntry(final CartData cartData, final List<String> productList)
	{
		final List<ApbStockonHandProductData> stockDataList = apbStockOnHandFacade.checkStock(cartData.getWarehouse(), productList);
		if (CollectionUtils.isNotEmpty(stockDataList))
		{
			for (final OrderEntryData entry : cartData.getEntries())
			{
				for (final ApbStockonHandProductData stockData : stockDataList)
				{
					if (stockData.getProductId().equalsIgnoreCase(entry.getProduct().getCode())
							&& stockData.getAvailablePhysical() < entry.getQuantity())
					{
						entry.setProductOutOfStock(true);
					}
				}
			}
		}
		Collections.sort(cartData.getEntries(),
				(entry1, entry2) -> Boolean.compare(entry2.isProductOutOfStock(), entry1.isProductOutOfStock()));
	}

	@Override
	public CartData getMiniCart()
	{
		if (!asahiSiteUtil.isCub())
		{
			final CartData cartData;
			if (hasSessionCart())
			{
				final CartModel cart = getCartService().getSessionCart();

				if (asahiSiteUtil.isSga())
				{
					final List<AbstractOrderEntryModel> updatedEntries = new ArrayList<>();

					for (final AbstractOrderEntryModel entry : cart.getEntries())
					{
						if (asahiSiteUtil.isSga() && (nonFreeGoodsEntry(entry, cart) || (StringUtils
								.isBlank(entry.getFreeGoodsForDeal())
								|| !entry.getFreeGoodsForDeal().equalsIgnoreCase(ApbCoreConstants.EXPIRED_OR_INVALID_FREEGOODS_ENTRY))))
						{
							updatedEntries.add(entry);
						}
					}
					cart.setEntries(updatedEntries);
					getModelService().save(cart);
				}

				cartData = getCartConverter().convert(cart);


				if (asahiSiteUtil.isSga())
				{
					inclusionExclusionProductStrategy.updateProductCartData(cartData);
				}
			}
			else
			{
				cartData = createEmptyCart();
			}
			return cartData;
		}
		else
		{
			return super.getMiniCart();
		}
	}

	/**
	 * @param cartData
	 */
	private void validateEntriesToBeAdded(final CartData cartData)
	{
		// YTODO Auto-generated method stub

	}

	private void setUnit(final CartModel cart)
	{
		final UserModel user = getUserService().getCurrentUser();
		if (null != user && user instanceof B2BCustomerModel)
		{
			final B2BCustomerModel customer = (B2BCustomerModel) user;
			final B2BUnitModel b2bUnit = customer.getDefaultB2BUnit();
			cart.setUnit(b2bUnit);
		}
	}

	@Override
	public boolean isMaxQtyReached(final String productCode, final int quantityToBeAdded, final boolean isUpdate)
	{
		if (asahiSiteUtil.isSga())
		{
			final ProductModel model = productService.getProductForCode(productCode);
			if (isUpdate)
			{
				final Long configuredQty = model.getMaxOrderQuantity() != null ? model.getMaxOrderQuantity()
						: asahiSiteUtil.getSgaGlobalMaxOrderQty();
				return configuredQty < quantityToBeAdded;
			}
			/*
			 * allowedqty = configured - quantity in cart
			 */
			return commerceStockService.getStockLevelForProductAndBaseStore(model, null) <= 0;
		}
		return false;
	}

	/*
	 * <p>This method will fetch the total quantity of packed products in cart.</p>
	 *
	 * @see com.apb.facades.cart.APBCartFacade#getTotalQtyForPackProduct(de.hybris.platform.commercefacades.order.data.
	 * CartData)
	 *
	 * @param cartData
	 */
	@Override
	public Integer getTotalQtyForPackProduct(final CartData cartData)
	{

		int totalPackProd = 0;

		totalPackProd = cartData.getEntries().stream()
				.filter(cartEntry -> (cartEntry.getProduct().getUnitVolume() == null
						|| !(cartEntry.getProduct().getUnitVolume() != null && cartEntry.getProduct().getBagInBox())))
				.collect(Collectors.toList()).stream().mapToInt(product -> product.getQuantity().intValue()).sum();

		if (totalPackProd == 0)
		{
			totalPackProd = Integer.MAX_VALUE;
		}
		return Integer.valueOf(totalPackProd);

	}

	/*
	 * <p>This method will fetch the total quantity of BIB products in cart.</p>
	 *
	 * @see com.apb.facades.cart.APBCartFacade#getTotalQtyForBibProduct(de.hybris.platform.commercefacades.order.data.
	 * CartData)
	 *
	 * @param cartData
	 */
	@Override
	public Integer getTotalQtyForBibProduct(final CartData cartData)
	{
		int totalBIBProduct = 0;

		totalBIBProduct = cartData.getEntries().stream()
				.filter(cartEntry -> (cartEntry.getProduct().getUnitVolume() != null
						&& cartEntry.getProduct().getBagInBox()))
				.collect(Collectors.toList()).stream().mapToInt(product -> product.getQuantity().intValue()).sum();


		if (totalBIBProduct == 0)
		{
			totalBIBProduct = Integer.MAX_VALUE;
		}
		return Integer.valueOf(totalBIBProduct);
	}

	/***
	 *
	 * @param entries
	 * @param prodShowMinicartCount
	 * @return the no of Unavailable products in the Minicart top 3 list
	 */
	public int getRemainUnavProd(final List<OrderEntryData> entries, final int prodShowMinicartCount)
	{
		int count = 0;
		for (int counter = 0; counter < prodShowMinicartCount; counter++)
		{
			if (inclusionExclusionProductStrategy.isProductIncluded(entries.get(counter).getProduct().getCode()))
			{
				count++;
			}
		}

		return count;
	}

	/*
	 * <p> This method will validate the cart for min order qty for BIB and Packed product. </p>
	 *
	 * @param cartData
	 */
	@Override
	public boolean validateMinOrderQuantity(final CartData cartData)
	{
		boolean disableCheckoutButton = false;

			final Integer maxPackProduct = Integer.valueOf(this.asahiConfigurationService
					.getString(MAX_PACKED_PRODUCT_ORDER_QTY + cmsSiteService.getCurrentSite().getUid(), "10"));

			final Integer maxBIBProduct = Integer.valueOf(this.asahiConfigurationService
					.getString(MAX_BIB_PRODUCT_ORDER_QTY + cmsSiteService.getCurrentSite().getUid(), "4"));

			final Integer maxMixPackProduct = Integer.valueOf(this.asahiConfigurationService
					.getString(MAX_MIX_PACKED_PRODUCT_ORDER_QTY + cmsSiteService.getCurrentSite().getUid(), "6"));

			final Integer maxMixBIBProduct = Integer.valueOf(this.asahiConfigurationService
					.getString(MAX_MIX_BIB_PRODUCT_ORDER_QTY + cmsSiteService.getCurrentSite().getUid(), "2"));

			if (cartData.getEntries() != null && !cartData.getEntries().isEmpty())
			{

				final Integer packProductQty = this.getTotalQtyForPackProduct(cartData);
				final Integer bibProductQty = this.getTotalQtyForBibProduct(cartData);

				LOG.info("packProductQty inside validateminorderqty " + packProductQty);
				LOG.info("bibProductQty inside validateminorderqty " + bibProductQty);

				if ((bibProductQty.equals(Integer.MAX_VALUE) && packProductQty < maxPackProduct)
						|| (packProductQty.equals(Integer.MAX_VALUE) && bibProductQty < maxBIBProduct)
						|| (packProductQty < maxPackProduct && bibProductQty < maxMixBIBProduct)
						|| (bibProductQty < maxBIBProduct && packProductQty < maxMixPackProduct))
				{
					disableCheckoutButton = true;
				}


			}

		return disableCheckoutButton;
	}


	/**
	 * This Method will add/Update the Bonus Stock in Cart
	 *
	 * @param code
	 *           , the Product Code
	 * @param quantity
	 *           , quantity to be added
	 * @param action
	 *           , whether for add to cart / Bonus
	 * @return cart Modification Data
	 */
	@Override
	public CartModificationData addToCart(final String code, final long quantity, final String action)
			throws CommerceCartModificationException
	{
		if (!asahiSiteUtil.isCub())
		{

			final ProductModel product = getProductService().getProductForCode(code);
			try
			{
				final CommerceCartParameter parameter = new CommerceCartParameter();
				parameter.setEnableHooks(true);
				parameter.setProduct(product);
				parameter.setQuantity(quantity);
				if (!action.equals(""))
				{
					parameter.setBonusAction(action);
				}
				/*if (hasSessionCart())
				{*/
					parameter.setCart(getCartService().getSessionCart());
					/* } */
				final CommerceCartModification modification = getCommerceCartService().addToCart(parameter);


				return getCartModificationConverter().convert(modification);
			}
			catch (final CommerceCartModificationException ex)
			{
				LOG.error("Error while adding bonus product to cart" + ex);
			}
			return null;
		}
		else
		{
			return super.addToCart(code, quantity, action);
		}
	}



	/**
	 * The Method will check for the bonus stock product in the session cart
	 *
	 * @return true incase of only bonus stock product
	 */
	public boolean isBonusStockProductsInCart()
	{
		boolean flag = true;

		if (hasSessionCart())
		{
			final CartModel cart = getCartService().getSessionCart();
			if (null != cart.getEntries() && !cart.getEntries().isEmpty())
			{
				for (final AbstractOrderEntryModel entry : cart.getEntries())
				{
					if (null == entry.getIsBonusStock() || !entry.getIsBonusStock())
					{
						flag = false;
						break;
					}
				}
			}
		}
		return flag;
	}

	/**
	 * Updating Cartentries to show product and its Bonus continuously
	 *
	 * @return updated CartEntries
	 */
	public void updateProductEntries(final CartData cartdata)
	{
		updateIsBonusAvForEntry(cartdata);

		final List<OrderEntryData> lowStockEntryDatas = new ArrayList<>();
		final List<OrderEntryData> cartEntryDatas = new ArrayList<>();
		final List<OrderEntryData> mergedEntryDatas = new ArrayList<>();
		for (final OrderEntryData orderEntryData : cartdata.getEntries())
		{
			if (orderEntryData.isProductOutOfStock())
			{
				lowStockEntryDatas.add(orderEntryData);
			}
			else
			{
				cartEntryDatas.add(orderEntryData);
			}
		}

		Collections.sort(lowStockEntryDatas,
				AsahiOrderEntryDataComparator.getComparator(AsahiOrderEntryDataComparator.SORT_BASED_ON.BONUS_STOCK_SORT));

		if (asahiCoreUtil.checkIfBonusEntryPresent(cartEntryDatas))
		{
			Collections.sort(cartEntryDatas,
					AsahiOrderEntryDataComparator.getComparator(AsahiOrderEntryDataComparator.SORT_BASED_ON.BONUS_STOCK_SORT));
		}

		mergedEntryDatas.addAll(lowStockEntryDatas);
		mergedEntryDatas.addAll(cartEntryDatas);


		cartdata.setEntries(mergedEntryDatas);
	}

	/**
	 * The Method will remove bonus product from the cart
	 *
	 */
	@Override
	public void removeBonusProductFromCart()
	{
		if (getCartService().hasSessionCart())
		{
			final CartModel cart = getCartService().getSessionCart();

			for (final AbstractOrderEntryModel entry : cart.getEntries())
			{
				if (null != entry && entry.getQuantity() >= 1 && null != entry.getIsBonusStock() && entry.getIsBonusStock())
				{
					getModelService().remove(entry);
				}
			}
			//cart.setCalculated(Boolean.FALSE);
			getModelService().refresh(cart);
		}
	}

	/**
	 * This Method checks if the cart is empty.
	 */
	@Override
	public Boolean isCartEmpty()
	{
		Boolean cartEmpty = Boolean.TRUE;
		if (hasSessionCart())
		{
			final CartData cartData = getSessionCart();
			if (!cartData.getEntries().isEmpty())
			{
				cartEmpty = Boolean.FALSE;
			}
		}
		return cartEmpty;
	}

	/**
	 * This Method will check if the cart contain any bonus product
	 *
	 * @return boolean true for any bonus product
	 */
	public boolean hasAnyBonusProduct()
	{

		Boolean flag = Boolean.FALSE;

		if (hasSessionCart())
		{
			final CartModel cart = getCartService().getSessionCart();
			if (null != cart.getEntries() && !cart.getEntries().isEmpty())
			{
				for (final AbstractOrderEntryModel entry : cart.getEntries())
				{
					if (null != entry.getIsBonusStock() && entry.getIsBonusStock())
					{
						flag = Boolean.TRUE;
						break;
					}
				}
			}
		}
		return flag;
	}

	/**
	 * method to update the Cartdata to add the available line item attribute
	 *
	 * @param cartData
	 */
	private void updateIsBonusAvForEntry(final CartData cartData)
	{

		for (final OrderEntryData ordEntryData : cartData.getEntries())
		{
			ordEntryData.setIsBonusLineAvailable(Boolean.FALSE);
			if (!ordEntryData.getIsBonusStock())
			{
				for (final OrderEntryData orderEntryData : cartData.getEntries())
				{
					if (ordEntryData.getProduct().getCode().equals(orderEntryData.getProduct().getCode())
							&& orderEntryData.getIsBonusStock())
					{
						ordEntryData.setIsBonusLineAvailable(Boolean.TRUE);
						break;
					}
				}
			}
		}
	}



	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.cart.SABMCartFacade#getSessionMiniCart()
	 */
	@Override
	public CartData getSessionMiniCart()
	{
		final CartData cartData;

		if (hasSessionCart())
		{
			final CartModel cart = getCartService().getSessionCart();

			final List<OrderEntryData> entryDataList = new ArrayList<>();

			if (CollectionUtils.isNotEmpty(cart.getEntries()))
			{
				final List<AbstractOrderEntryModel> entries = new ArrayList<>();
				entries.addAll(cart.getEntries());

				//Sorting the CartEntries by modified time to display the latest ones on top.
				entries.sort(new Comparator<AbstractOrderEntryModel>()
				{
					@Override
					public int compare(final AbstractOrderEntryModel o1, final AbstractOrderEntryModel o2)
					{
						if (o1 != null && o2 != null)
						{
							return Objects.compare(o2.getModifiedtime(), o1.getModifiedtime(), Comparator.naturalOrder());
						}
						return 0;
					}
				});

				int i = 0;

				//Converting only the CartEntries to display

				while (i < entries.size())
				{
					entryDataList.add(orderEntryConverter.convert(entries.get(i)));
					i++;
				}
			}
			cartData = getMiniCartConverter().convert(cart);
			cartData.setEntries(entryDataList);
		}
		else
		{
			cartData = createEmptyCart();
		}
		return cartData;
	}


	public void addApplyDealToCart(final DealModel dealModel, final DealConditionStatus addMethod)
	{
		if (dealModel == null)
		{
			LOG.warn("Impossible to add a null deal to the cart.");
		}

		if (hasSessionCart())
		{
			final CartModel cartModel = getCartService().getSessionCart();

			// Create and save CartDealConditionModel object
			if (DealConditionStatus.MANUAL.equals(addMethod) || DealConditionStatus.MANUAL_CONFLICT.equals(addMethod)
					|| !cartContainsDCN(cartModel, dealModel, true))
			{
				CartDealConditionModel dealConditionModel = null;
				//get DCN when select manual conflict deal
				// if cart contain the selected deal, get DCN from cart
				// else create a new one
				if (cartContainsDCN(cartModel, dealModel, false))
				{
					for (final CartDealConditionModel dcn : cartModel.getComplexDealConditions())
					{
						if (Objects.equals(dealModel, dcn.getDeal()) && !DealConditionStatus.REJECTED.equals(dcn.getStatus()))
						{
							dealConditionModel = dcn;
						}
					}
				}
				if (dealConditionModel == null)
				{
					dealConditionModel = modelService.create(CartDealConditionModel.class);
				}
				// if manual add deal in deal page ,need to update conflict flag for previous manual conflict deal
				if (DealConditionStatus.MANUAL.equals(addMethod))
				{
					updateConflictDeal(cartModel, dealModel);
				}
				//if selected conflict manual need to set conflict group in DCN
				if (DealConditionStatus.MANUAL_CONFLICT.equals(addMethod))
				{
					final B2BUnitModel b2bUnit = b2bCommerceUnitService.getParentUnit();
					dealConditionModel.setConflictDeals(sabmCartService.findConflictDealForCurrentDeal(dealModel, cartModel, b2bUnit));
				}
				dealConditionModel.setDeal(dealModel);
				dealConditionModel.setStatus(addMethod);
				dealConditionModel.setAbstractOrder(cartModel);
				modelService.save(dealConditionModel);
				LOG.debug("Create CartDealConditionModel is [{}], The deal code : {}", dealConditionModel, dealModel.getCode());

				if (DealConditionStatus.MANUAL_CONFLICT.equals(addMethod))
				{
					final List<CartDealConditionModel> conditionModels = getNeedRemovedDealCondition(cartModel, dealModel.getCode());
					if (CollectionUtils.isNotEmpty(conditionModels))
					{
						modelService.removeAll(conditionModels);
					}
				}

				// Added to CartModel will create CartDealConditionModel object
				modelService.refresh(cartModel);
				cartModel.setOrderSimulationStatus(OrderSimulationStatus.NEED_CALCULATION);
				modelService.save(cartModel);
				LOG.debug("In the CartModel ComplexDealConditions size is : {}", cartModel.getComplexDealConditions().size());
			}
			else
			{
				LOG.warn("According to the deal[{}] can't query to DealModel", dealModel);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.cart.SABMCartFacade#addApplyDealToCart(java.lang.String)
	 */
	@Override
	public DealJson addApplyDealToCart(final String dealCode, final DealConditionStatus addMethod)
	{
		if (StringUtils.isEmpty(dealCode))
		{
			return null;
		}

		final DealModel dealModel = dealsService.getDeal(dealCode);

		addApplyDealToCart(dealModel, addMethod);

		final DealJson dealJson = new DealJson();
		dealJsonPopulator.populate(Arrays.asList(dealModel), dealJson);
		return dealJson;
	}

	/**
	 * @param cartModel
	 * @param dealModel
	 */
	private void updateConflictDeal(final CartModel cartModel, final DealModel dealModel)
	{
		final List<CartDealConditionModel> cartDealConditions = cartModel.getComplexDealConditions();
		for (final CartDealConditionModel cartDealCondition : cartDealConditions)
		{
			if (DealConditionStatus.MANUAL_CONFLICT.equals(cartDealCondition.getStatus())
					&& cartDealCondition.getConflictDeals().contains(dealModel))
			{
				modelService.remove(cartDealCondition);
				modelService.refresh(cartModel);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.cart.SABMCartFacade#cartContainsDCN(de.hybris.platform.core.model.order.CartModel,
	 * com.sabmiller.core.model.DealModel)
	 */
	@Override
	public boolean cartContainsDCN(final CartModel cart, final DealModel deal, final boolean checkConflicting)
	{
		if (cart != null && CollectionUtils.isNotEmpty(cart.getComplexDealConditions()))
		{
			for (final CartDealConditionModel dcn : cart.getComplexDealConditions())
			{
				if (Objects.equals(deal, dcn.getDeal()) || (checkConflicting && dcn.getConflictDeals().contains(deal)))
				{
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public String getCartCode()
	{
		if (hasSessionCart())
		{
			return getCartService().getSessionCart().getCode();
		}

		return null;
	}

	/**
	 * Gets the rejected deal from cart
	 *
	 * @return the rejected deal titles
	 */
	@Override
	public List<String> getRejectedDealFromCart()
	{
		if (hasSessionCart())
		{
			final CartModel cartModel = getCartService().getSessionCart();
			if (cartModel != null && CollectionUtils.isNotEmpty(cartModel.getComplexDealConditions()))
			{
				return getRejectedDealFromComplexDealConditions(cartModel);
			}
		}
		return Collections.emptyList();
	}

	/**
	 * Gets the deal which have conflicts x
	 */

	@Override
	public void findConflictingDeals(final CartDealsJson cartDealsJson)
	{
		final CartModel cart = sabmCartService.getSessionCart();
		final B2BUnitModel b2bUnit = b2bCommerceUnitService.getParentUnit();

		if (cart == null || b2bUnit == null)
		{

			return;
		}

		final DealQualificationResponse dealQualificationResponse = dealConditionService.findQualifiedDeals(b2bUnit, cart);
		final Map<DealModel, List<DealModel>> conflictGroupMap = dealConditionService
				.findConflictingDeals(dealQualificationResponse.getGreenDeals(), cart);
		if (dealQualificationResponse == null || dealQualificationResponse.getConflictGroup() == null
				|| CollectionUtils.isEmpty(dealQualificationResponse.getConflictGroup().getConflicts()))
		{
			return;
		}

		updateManualConflictWhenHasNewConflict(cart.getComplexDealConditions(), dealQualificationResponse.getConflictGroup());
		final Set<DealModel> manualDeals = getManualConflictDeals(cart.getComplexDealConditions());
		final ConflictGroup conflictGroup = dealQualificationResponse.getConflictGroup();


		final List<List<DealModel>> groupedFreeDeals = new ArrayList<>();
		final List<List<DealModel>> allConflictDeals = new ArrayList<>(); // to save all conflict deal for user to choose


		final List<List<DealModel>> groupedDeals = dealsService.composeComplexFreeProducts(
				getAllUnresolvedConflictDeal(conflictGroup.getConflicts(), manualDeals, conflictGroupMap));


		LOG.debug("got the groupedDeals: {}", groupedDeals);


		final List<CartDealConditionModel> complexDealConditions = cart.getComplexDealConditions();
		final List<DealModel> listManual = new ArrayList<>();
		for (final CartDealConditionModel cartDealConditionModel : complexDealConditions)
		{
			if (DealConditionStatus.MANUAL.equals(cartDealConditionModel.getStatus())
					|| DealConditionStatus.MANUAL_CONFLICT.equals(cartDealConditionModel.getStatus()))
			{
				listManual.add(cartDealConditionModel.getDeal());
			}
		}

		for (final List<DealModel> list : groupedDeals)
		{
			// size > 1 means it's a free goods deal.
			if (CollectionUtils.isNotEmpty(list) && list.size() > 1)
			{
				// pick out the deal with free goods. the deals must be choosed by user before.
				if (CollectionUtils.intersection(list, listManual).size() != 1)
				{
					// 1: > 1 means the cart contains 1+ free good deals of a free-good-deal suite.
					// 2: =0 means that user has not select the free good.
					// so, need to popup the free good select window now.
					groupedFreeDeals.add(list);
				}

				// the free good deals also should be included in the conflict deal list for user to choose.
				for (final DealModel dealModel : list)
				{

					final List<DealModel> conflictWithCurrentDeal = conflictGroupMap.get(dealModel);
					if (conflictWithCurrentDeal.size() == list.size() && !CollectionUtils.intersection(listManual, list).isEmpty())
					{
						break;
					}
					allConflictDeals.add(Lists.newArrayList(dealModel));
				}
			}
			else
			{
				allConflictDeals.add(list);
			}
		}



		// if only one deals left, do not need to deal with conflict.
		// confirmed with Alex, this situation should not happen. because there is a filter out of this method.
		if (allConflictDeals.size() <= 1 || (groupedDeals.size() == 1 && allConflictDeals.size() == groupedDeals.get(0).size()))
		{
			LOG.debug("no need to pop up conflict window. cart:{}, groupedDeals:{}", cart, groupedDeals);
			allConflictDeals.clear();
		}

		//Set the conflict deal code to the session, this session will be used to delete the unselected deal condition
		sessionService.setAttribute(SabmCoreConstants.SESSION_ATTR_CONFLICT_DEALS_CODE, conflictGroupMap);
		//setConflicDealCodeToSession(groupedFreeDeals, groupedNoFreeDeals);


		cartDealsJson.setConflict(Converters.convertAll(allConflictDeals, conflictDealJsonConverter));
		if (LOG.isDebugEnabled())
		{
			if (CollectionUtils.isNotEmpty(cartDealsJson.getConflict()))
			{
				final StringBuffer sb = new StringBuffer();
				for (final ConflictDealJson conf : cartDealsJson.getConflict())
				{
					sb.append(conf.getCode()).append(",");
				}
				LOG.debug("the cart conflict deals are :{}", sb);
			}
			else
			{
				LOG.debug("no conflict deals for cart: {}", cart);
			}
		}
		if (CollectionUtils.isEmpty(cartDealsJson.getConflict()) && CollectionUtils.isNotEmpty(groupedFreeDeals))
		{
			LOG.debug("no conflict, only the free deals:{}", groupedFreeDeals);
			try
			{
				final DealJson dealJson = new DealJson();
				dealProductPopulator.populate(groupedFreeDeals.get(0), dealJson);
				checkFreeGoodsDealAppliedTimes(dealJson);
				cartDealsJson.setFree(dealJson.getSelectableProducts());
			}
			catch (final ConversionException e)
			{
				LOG.warn("Unable to convert deals", e);
			}
		}
	}

	/**
	 * @param complexDealConditions
	 * @param conflictGroup
	 */
	private void updateManualConflictWhenHasNewConflict(final List<CartDealConditionModel> complexDealConditions,
			final ConflictGroup conflictGroup)
	{
		final List<Conflict> conflicts = conflictGroup.getConflicts();
		for (final Conflict conflict : conflicts)
		{
			final Set<DealModel> currentConflictDeal = conflict.getDeals();
			for (final CartDealConditionModel cartDealCondition : complexDealConditions)
			{
				if (DealConditionStatus.MANUAL_CONFLICT.equals(cartDealCondition.getStatus()))
				{
					//if invoke a new deal in deal conflict list. need to let user to select conflict deal
					//it will reset status to manual
					final List<DealModel> previousConflictDeal = cartDealCondition.getConflictDeals();
					if (!previousConflictDeal.containsAll(currentConflictDeal)
							&& currentConflictDeal.contains(cartDealCondition.getDeal()))
					{
						cartDealCondition.setStatus(DealConditionStatus.MANUAL);
					}
				}
			}
		}


	}


	/**
	 * Gets the rejected deal from Complex Deal Conditions
	 *
	 * @param groupedFreeDeals
	 *           grouped Free Deals
	 *
	 * @param groupedNoFreeDeals
	 *           grouped No Free Deals
	 */
	protected void setConflicDealCodeToSession(final List<List<DealModel>> groupedFreeDeals,
			final List<List<DealModel>> groupedNoFreeDeals)
	{
		if (CollectionUtils.isNotEmpty(groupedNoFreeDeals))
		{
			final Set<String> dealsCode = new HashSet<>();
			for (final List<DealModel> deals : CollectionUtils.emptyIfNull(groupedNoFreeDeals))
			{
				if (CollectionUtils.isNotEmpty(deals) && deals.get(0) != null)
				{
					dealsCode.add(deals.get(0).getCode());
				}
			}
			sessionService.setAttribute(SabmCoreConstants.SESSION_ATTR_CONFLICT_DEALS_CODE, dealsCode);
		}
		else if (CollectionUtils.isNotEmpty(groupedFreeDeals))
		{
			final Set<String> dealsCode = new HashSet<>();
			for (final DealModel deal : CollectionUtils.emptyIfNull(groupedFreeDeals.get(0)))
			{
				if (deal != null)
				{
					dealsCode.add(deal.getCode());
				}
			}
			sessionService.setAttribute(SabmCoreConstants.SESSION_ATTR_CONFLICT_DEALS_CODE, dealsCode);
		}

	}

	/**
	 * Gets the rejected deal from Complex Deal Conditions
	 *
	 * @param cartModel
	 *           the cartModel
	 *
	 * @return the rejected deal titles
	 */
	protected List<String> getRejectedDealFromComplexDealConditions(final CartModel cartModel)
	{

		final List<CartDealConditionModel> conditionModels = cartModel.getComplexDealConditions();
		final List<String> rejectedDealTitles = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(conditionModels))
		{
			//SABMC-1865 - dont show the deals message when product unavailable
			//Get the unavailable deals code from the cart
			final Set<String> unAvailableDealsCode = getUnAvailableDeals(cartModel);

			final Set<DealModel> deals = new HashSet<>();
			for (final CartDealConditionModel conditionModel : conditionModels)
			{
				if (DealConditionStatus.REJECTED.equals(conditionModel.getStatus()) && conditionModel.getDeal() != null
						&& !unAvailableDealsCode.contains(conditionModel.getDeal().getCode()))
				{
					deals.add(conditionModel.getDeal());
				}
			}

			for (final DealModel dealModel : deals)
			{
				final DealJson dealJson = new DealJson();
				dealTitlePopulator.populate(Arrays.asList(dealModel), dealJson);
				rejectedDealTitles.add(dealJson.getTitle());
			}
		}

		return rejectedDealTitles;
	}

	/**
	 * Get the entries which the product is no longer available in sap SABMC-1865
	 *
	 * @param cart
	 *           the cart
	 * @return the set of the Deals code
	 */
	private Set<String> getUnAvailableDeals(final CartModel cart)
	{
		final Map<Integer, List<DealModel>> entryDeals = sabmCartService.getEntryApplyDeal(cart, true);
		if (MapUtils.isEmpty(entryDeals))
		{
			return Collections.emptySet();
		}

		final Set<String> unAvailableDealsCode = new HashSet<>();
		for (final AbstractOrderEntryModel entry : CollectionUtils.emptyIfNull(cart.getEntries()))
		{
			if (entryDeals.containsKey(entry.getEntryNumber())
					&& (StockLevelStatus.BLOCKED.getCode().equals(entry.getAvailabilityInfo())
							|| StockLevelStatus.UNAVAILABLE.getCode().equals(entry.getAvailabilityInfo())))
			{
				for (final DealModel deal : CollectionUtils.emptyIfNull(entryDeals.get(entry.getEntryNumber())))
				{
					unAvailableDealsCode.add(deal.getCode());
				}
			}
		}
		return CollectionUtils.isEmpty(unAvailableDealsCode) ? Collections.emptySet() : unAvailableDealsCode;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.cart.SABMCartFacade#isCurrentUserCashOnlyCustomer()
	 */
	@Override
	public boolean isCurrentUserCashOnlyCustomer()
	{
		return userFlagService.isCashOnlyCustomer();
	}

	/**
	 * Gets all the deals which have not been resolved
	 *
	 * @param conflicts
	 *           the conflicts
	 * @param manualDeals
	 *           the deal which have been manual resolved
	 * @param conflictGroupMap
	 *
	 * @return the deal have conflicts
	 */

	private List<DealModel> getAllUnresolvedConflictDeal(final List<Conflict> conflicts, final Set<DealModel> manualDeals,
			final Map<DealModel, List<DealModel>> conflictGroupMap)
	{
		final List<Conflict> unresolvedGroup = new ArrayList<Conflict>();
		//set resolved and unresolved list
		for (final Conflict conflict : conflicts)
		{
			boolean isResolved = false;
			if (CollectionUtils.isEmpty(conflict.getDeals()) || conflict.getDeals().size() == 1)
			{
				continue;
			}
			for (final DealModel dealModel : CollectionUtils.emptyIfNull(conflict.getDeals()))
			{
				if (manualDeals.contains(dealModel))
				{
					isResolved = true;
					break;
				}

			}
			if (!isResolved)
			{
				unresolvedGroup.add(conflict);
			}

		}
		final Set<DealModel> resolvedDealSet = getResolvedDealList(conflictGroupMap, manualDeals);
		//excluded deal from unresolved list. if deal has been resolved.
		if (CollectionUtils.isNotEmpty(unresolvedGroup))
		{
			final Set<DealModel> conflictDeal = new HashSet<>();
			for (int i = 0; i < unresolvedGroup.size(); i++)
			{
				final Conflict conflict = unresolvedGroup.get(i);
				conflictDeal.addAll(conflict.getDeals());
			}

			return new ArrayList<>(
					CollectionUtils.subtract(CollectionUtils.emptyIfNull(conflictDeal), CollectionUtils.emptyIfNull(resolvedDealSet)));
		}

		return Collections.emptyList();
	}

	/**
	 * @param conflictGroupMap
	 * @param manualDeals
	 * @return
	 */
	private Set<DealModel> getResolvedDealList(final Map<DealModel, List<DealModel>> conflictGroupMap,
			final Set<DealModel> manualDeals)
	{
		final Set<DealModel> resolvedDeals = new HashSet<>();
		for (final DealModel dealModel : manualDeals)
		{
			resolvedDeals.addAll(CollectionUtils.emptyIfNull(conflictGroupMap.get(dealModel)));
			resolvedDeals.add(dealModel);
		}
		return resolvedDeals;
	}

	/**
	 * Gets the deals which have been manual resolved the conflict
	 *
	 * @param complexDealConditions
	 *           the complexDealConditions
	 *
	 * @return the deal have been resolved
	 */
	private Set<DealModel> getManualConflictDeals(final List<CartDealConditionModel> complexDealConditions)
	{
		final Set<DealModel> deals = new HashSet<>();
		for (final CartDealConditionModel manual : CollectionUtils.emptyIfNull(complexDealConditions))
		{
			if (manual != null && DealConditionStatus.MANUAL_CONFLICT.equals(manual.getStatus()) && manual.getDeal() != null)
			{
				deals.add(manual.getDeal());
			}
		}

		return CollectionUtils.isNotEmpty(deals) ? deals : Collections.emptySet();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.cart.SABMCartFacade#getDeliveryDefaultAddress(java.lang.String)
	 */
	@Override
	public AddressData getDeliveryDefaultAddress(final String unitId)
	{
		final CartModel cartModel = getCartService().getSessionCart();
		if (cartModel != null)
		{
			final UserModel user = cartModel.getUser();
			if (null != user && user instanceof B2BCustomerModel)
			{
				final B2BCustomerModel b2bCustomer = (B2BCustomerModel) user;
				return popultorAddress(unitId, b2bCustomer);
			}
		}
		return null;
	}

	/**
	 * Converter B2BUnit delivery default address
	 *
	 * @param unitId
	 * @param b2bCustomer
	 */
	private AddressData popultorAddress(final String unitId, final B2BCustomerModel b2bCustomer)
	{
		final B2BUnitModel b2bUnit = b2bUnitService.getUnitForUid(unitId);
		final Set<DeliveryDefaultAddressModel> deliveryDefaultAddresses = b2bCustomer.getDefaultAddresses();
		for (final DeliveryDefaultAddressModel deliveryDefaultAddress : SetUtils.emptyIfNull(deliveryDefaultAddresses))
		{
			if (b2bUnit.equals(deliveryDefaultAddress.getB2bUnit()) && null != deliveryDefaultAddress.getAddress())
			{
				return addressConverter.convert(deliveryDefaultAddress.getAddress());
			}
		}
		return null;
	}

	/**
	 * @param addressConverter
	 *           the addressConverter to set
	 */
	public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter)
	{
		this.addressConverter = addressConverter;
	}

	/**
	 * @param cartModel
	 *           the cartModel
	 * @param selectedDealCode
	 *           the selected Deal Code
	 * @return List<CartDealConditionModel>
	 */
	protected List<CartDealConditionModel> getNeedRemovedDealCondition(final CartModel cartModel, final String selectedDealCode)
	{
		final Map<DealModel, List<DealModel>> conflictingDeals = sessionService
				.getAttribute(SabmCoreConstants.SESSION_ATTR_CONFLICT_DEALS_CODE);
		if (StringUtils.isEmpty(selectedDealCode) || MapUtils.isEmpty(conflictingDeals)
				|| CollectionUtils.isEmpty(cartModel.getComplexDealConditions()))
		{
			return Collections.emptyList();
		}
		sessionService.removeAttribute(SabmCoreConstants.SESSION_ATTR_CONFLICT_DEALS_CODE);

		final List<CartDealConditionModel> needRemovedCondition = new ArrayList<>();
		final DealModel deal = dealsService.getDeal(selectedDealCode);
		for (final CartDealConditionModel cartDealCondition : CollectionUtils.emptyIfNull(cartModel.getComplexDealConditions()))
		{
			if (cartDealCondition != null && cartDealCondition.getDeal() != null
					&& !CollectionUtils.isEmpty(conflictingDeals.get(deal))
					&& conflictingDeals.get(deal).contains(cartDealCondition.getDeal()))
			{
				if (!selectedDealCode.equals(cartDealCondition.getDeal().getCode()))
				{
					needRemovedCondition.add(cartDealCondition);
				}
				else if (!DealConditionStatus.MANUAL_CONFLICT.equals(cartDealCondition.getStatus()))
				{
					needRemovedCondition.add(cartDealCondition);
				}
			}
		}

		return CollectionUtils.isEmpty(needRemovedCondition) ? Collections.emptyList() : needRemovedCondition;
	}


	@Override
	public String validateProductsBeforeAddtoCart(final String baseProducts)
	{
		return sabmCartService.validateProductsBeforeAddtoCart(baseProducts);
	}



	/**
	 *
	 */
	@Override
	public void removeRejectedDealIfNotQualify()
	{
		final CartModel cart = sabmCartService.getSessionCart();
		final B2BUnitModel b2bUnit = b2bCommerceUnitService.getParentUnit();
		sabmCartService.removeRejectedDealIfNotQualify(cart, b2bUnit);


	}

	/**
	 * check how many times the deal have been applied and modify the deal json base the applied times only the
	 * proportional free product deal could invoke this method the deal must have been applied
	 *
	 * @param dealJson
	 *           the deal need to be check
	 */
	@Override
	public void checkFreeGoodsDealAppliedTimes(final DealJson dealJson)
	{
		Assert.notNull(dealJson, "The dealJson can not be null");
		if (CollectionUtils.isNotEmpty(dealJson.getSelectableProducts()) && dealJson.getSelectableProducts().get(0) != null
				&& BooleanUtils.isTrue(dealJson.getSelectableProducts().get(0).getProportionalFreeGood()))
		{
			final CartData cartData = getSessionCartWithEntryOrdering(true);

			if (cartData != null && CollectionUtils.isNotEmpty(cartData.getEntries()))
			{
				dealAppliedTimesHelper.checkDealAppliedTimes(dealJson, cartData.getEntries());
			}
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.cart.SABMCartFacade#isExistBaseProduct()
	 */
	@Override
	public boolean isExistBaseProduct()
	{
		if (!hasEntries())
		{
			return false;
		}

		final CartModel cart = sabmCartService.getSessionCart();
		for (final AbstractOrderEntryModel orderEntry : ListUtils.emptyIfNull(cart.getEntries()))
		{
			if (BooleanUtils.isNotTrue(orderEntry.getIsFreeGood()))
			{
				return true;
			}
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.cart.SABMCartFacade#requiresCalculation()
	 */
	@Override
	public boolean requiresCalculation()
	{
		if (sabmCartService.hasSessionCart())
		{
			return calculationService.requiresCalculation(sabmCartService.getSessionCart());
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.cart.SABMCartFacade#getDefaultShipTo(java.lang.String)
	 */
	@Override
	public AddressData getDefaultShipTo(final String unitId)
	{
		final B2BUnitModel b2bUnit = b2bUnitService.getUnitForUid(unitId);
		final AddressModel defaultShipTo = b2bUnit.getDefaultShipTo();
		if (null != defaultShipTo)
		{
			return addressConverter.convert(defaultShipTo);
		}

		return null;
	}

	/**
	 * Clear the cart entries of the session cart
	 */
	@Override
	public void clearCartEntries()
	{
		final CartModel cartModel = getCartService().getSessionCart();

		modelService.removeAll(cartModel.getEntries());
		modelService.refresh(cartModel);

	}

	@Override
	public List<String> validateCustomCartRules(final CartData cartData)
	{

		final List<SabmCartRuleModel> cartRules = sabmCartService.getCustomCartRules();

		final List<String> errors = new ArrayList<>();

		if (CollectionUtils.size(cartRules) > 0)
		{
			Long cases = 0L;
			Long kegs = 0L;
			for (final OrderEntryData entry : cartData.getEntries())
			{
				if (entry.getBaseUnit() != null && StringUtils.equalsIgnoreCase("KEG", entry.getBaseUnit().getCode()))
				{
					kegs += entry.getBaseQuantity();
				}
				else
				{
					cases += entry.getBaseQuantity();
				}
			}
			for (final SabmCartRuleModel cartRule : cartRules)
			{
				if (checkCartRuleDeliveryDate(cartData.getRequestedDeliveryDate(), cartRule.getDeliveryDates()))
				{
					final Long minCases = cartRule.getMinCases() != null ? cartRule.getMinCases() : 0L;
					final Long minKegs = cartRule.getMinKegs() != null ? cartRule.getMinKegs() : 0L;

					if (minCases != 0L && cases.compareTo(minCases) < 0 && minKegs == 0L)
					{
						errors.add(cartRule.getErrorMessage());
					}
					else if (minKegs != 0L && kegs.compareTo(minKegs) < 0 && minCases == 0L)
					{
						errors.add(cartRule.getErrorMessage());
					}
					else if (minCases != 0L && minKegs != 0L && cases.compareTo(minCases) < 0 && kegs.compareTo(minKegs) < 0)
					{
						errors.add(cartRule.getErrorMessage());
					}
				}
			}
		}
		return errors;
	}

	private boolean checkCartRuleDeliveryDate(final Date cartDeliveryDate, final List<Date> dates)
	{

		for (final Date date : dates)
		{
			if (SabmDateUtils.sameDay(cartDeliveryDate, date))
			{
				return true;
			}

		}
		return false;
	}

	/**
	 * @param smartRecommendationModel
	 * @param entryNumber
	 */
	@Override
	public void setSmartRecommendationModelToEntry(final String smartRecommendationModel, final int entryNumber)
	{
		final CartModel cartModel = getCartService().getSessionCart();
		if (null != cartModel && CollectionUtils.isNotEmpty(cartModel.getEntries()))
		{
			final AbstractOrderEntryModel entryModel = cartModel.getEntries().get(entryNumber);
			if (null != entryModel)
			{
				entryModel.setSmartRecommendationModel(SmartRecommendationType.valueOf(smartRecommendationModel));
				modelService.save(entryModel);
			}
		}
	}

	@Override
	public void validateShippingCarrier()
	{
		final CartModel cartModel = getCartService().getSessionCart();
		B2BUnitModel b2bUnitModel = b2bCommerceUnitService.getParentUnit();
		final ShippingCarrierModel shippingCarrier = null;
		final B2BUnitModel b2bUnitForAlternativeAddress = getUnitBasedForAlternativeAddress();
		b2bUnitModel = null != b2bUnitForAlternativeAddress ? b2bUnitForAlternativeAddress : b2bUnitModel;
		final Map<String, Object> deliveryPackType = sabmDeliveryDateCutOffService.getDeliveryDatePackType(b2bUnitModel,
				cartModel.getRequestedDeliveryDate());
		sessionService.setAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE_PACKTYPE,
				deliveryPackType.get(PackType._TYPECODE));
		if (b2bUnitModel.getShippingCarriers() != null
				&& !b2bUnitModel.getShippingCarriers().contains(cartModel.getDeliveryShippingCarrier()))
		{
			if (cartModel.getDeliveryMode().getCode()
					.equals(siteConfigService.getString(SabmCoreConstants.CART_DELIVERY_CUBARRANGED, "")))
			{
				this.saveDefaultShippingCarriers();
			}
			else
			{
				if (b2bUnitModel.getShippingCarriers() != null
						&& b2bUnitModel.getShippingCarriers().contains(cartModel.getDeliveryShippingCarrier()))
				{
					return;
				}
				for (final ShippingCarrierModel carrier : b2bUnitModel.getShippingCarriers())
				{
					if (carrier.getCustomerOwned())
					{
						final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
						parameter.setEnableHooks(true);
						parameter.setCart(cartModel);
						parameter.setShippingCarrier(carrier);

						sabmCartService.setShippingCarrier(parameter);
						break;
					}
				}

			}

		}

	}

	/**
	 * Removes or add/update free deal product on parent line item qty update in Cart.
	 *
	 * @param cartModification
	 *           the cart modification
	 * @param entryNumber
	 *           the entry number
	 * @throws CommerceCartModificationException
	 *            the commerce cart modification exception
	 */
	@Override
	public void removeOrUpdateFreeDealProductOnQtyUpdate(final long updatedQuantity, final long entryNumber)
			throws CommerceCartModificationException
	{
		sabmCommerceAddToCartStrategy.removeOrUpdateFreeDealProductOnQtyUpdate(updatedQuantity, entryNumber);
	}




}
