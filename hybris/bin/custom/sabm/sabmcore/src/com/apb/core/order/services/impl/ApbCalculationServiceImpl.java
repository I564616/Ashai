package com.apb.core.order.services.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.impl.DefaultCalculationService;
import de.hybris.platform.order.strategies.calculation.OrderRequiresCalculationStrategy;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.TaxValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.model.ApbProductModel;
import com.apb.core.order.services.ApbCalculationService;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.integration.data.ApbPriceData;
import com.apb.integration.data.ApbProductPriceInfo;
import com.apb.integration.data.AsahiProductInfo;
import com.apb.integration.price.dto.ApbPriceRequestData;
import com.apb.integration.price.service.AsahiPriceIntegrationService;
import com.apb.product.strategy.AsahiInclusionExclusionProductStrategy;
import com.google.common.util.concurrent.AtomicDouble;
import com.sabmiller.core.enums.TaxType;
import com.sabmiller.core.model.AsahiB2BUnitModel;


public class ApbCalculationServiceImpl extends DefaultCalculationService implements ApbCalculationService
{
	 private static Logger LOG = LoggerFactory.getLogger(ApbCalculationServiceImpl.class);
	@Resource
	private OrderRequiresCalculationStrategy orderRequiresCalculationStrategy;

	@Resource(name = "inclusionExclusionProductStrategy")
	private AsahiInclusionExclusionProductStrategy inclusionExclusionProductStrategy;

	@Resource
	private CommonI18NService commonI18NService;

	@Resource(name = "asahiPriceIntegrationService")
	private AsahiPriceIntegrationService asahiPriceIntegrationService;

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Resource
	private UserService userService;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Resource
	private SessionService sessionService;

	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	public static final String CASES_COUNT_FOR_DELIVERY_FEE = "delivery.surcharge.MOQ.apb";
	public static final String DELIVERY_FEE = "delivery.surcharge.apb";
	public static final String PACK_TYPE = "delivery.surcharge.waived.off.package.type.apb";
	public static final String NON_ALCOHOLIC_TYPE = "product.code.non.alcoholic.product.apb";
	public static final String PRODUCT_CODE_FOR_DELIVERY_SURCHARGE = "product.code.for.delivery.surcharge.apb";

	@Override
	public void calculate(final AbstractOrderModel order) throws CalculationException
	{
		if (orderRequiresCalculationStrategy.requiresCalculation(order))
		{
			if (asahiSiteUtil.isSga())
			{
				updateSGAOrderEntryForPriceAndTax(order);
				calculateOrderTotal(order);
				calculateTotals(order, false, calculateSubtotal(order, false));
			}
			else
			{
				final boolean isDeliveryChargeApplicable = isDeliveryChargeApplicable(order);
				if (updateOrderModel(order, false, isDeliveryChargeApplicable))
				{
					calculateOrderTotal(order);
					// -----------------------------
					final Map taxValueMap = resetAllValues(order);


					// now calculate all totals
					calculateTotals(order, false, taxValueMap);
				}
			}
		}
	}

	/**
	 * This method will get the product from session inclusion list and update in order entry with price and applicable
	 * taxes. As of now, it is applicable for SGA site
	 *
	 * @param AbstractOrderModel
	 *           order
	 *
	 */
	private void updateSGAOrderEntryForPriceAndTax(final AbstractOrderModel order)
	{
		order.setPriceUpdated(true);
		order.getEntries().stream()
				.filter(e -> BooleanUtils.isFalse(e.getIsBonusStock()) && BooleanUtils.isFalse(e.getIsFreeGood()))
				.forEach(updateEntry -> {
			final AsahiProductInfo product = asahiCoreUtil.getProductFromSessionInclusionList(updateEntry.getProduct().getCode());
			updateProductPriceFromSession(product, updateEntry);
			updateTaxEntriesForSga(product, updateEntry);
			getModelService().save(updateEntry);
		});
	}

	/**
	 * Updating the tax entries for order entry
	 */
	private void updateTaxEntriesForSga(final AsahiProductInfo product, final AbstractOrderEntryModel updateEntry)
	{
		/*
		 * Getting total gst and cdl value in case checkout page is initiated
		 */
		Double gstValue = 0.0;
		Double cdlValue = 0.0;
		try
		{
			gstValue = product != null ? asahiCoreUtil.getSessionCheckoutFlag() ? product.getTotalGst()
					: product.getGst() != null ? product.getGst() : 0.0D : 0.0D;
			cdlValue = product != null ? asahiCoreUtil.getSessionCheckoutFlag() ? product.getTotalCdl()
					: product.getContainerDepositLevy() != null ? product.getContainerDepositLevy() : 0.0D : 0.0D;
		}
		catch (final Exception ex)
		{
			LOG.info("Exception Occured while getting CDL and GST" + ex);
		}

		final TaxValue gst = new TaxValue(TaxType.GST.getCode(), gstValue, true, asahiSiteUtil.getCurrency());
		final TaxValue cdl = new TaxValue(TaxType.CDL.getCode(), cdlValue, true, asahiSiteUtil.getCurrency());
		final List<TaxValue> taxList = new ArrayList<>();
		taxList.add(gst);
		taxList.add(cdl);
		updateEntry.setTaxValues(taxList);
		setCalculatedStatus(updateEntry);
	}

	/**
	 * This method will update the price for a product in a entry
	 *
	 * @param -
	 *           AsahiProdcutInfo - product to be updated
	 * @param -
	 *           AbstractOrderEntryModel - entry to be updated
	 */
	private void updateProductPriceFromSession(final AsahiProductInfo product, final AbstractOrderEntryModel updateEntry)
	{
		if (null != product)
		{
			updateEntry.setBasePrice(
					product.getListPrice() != null ? (product.getListPrice() + product.getContainerDepositLevy()) : 0.0D);
			updateEntry.setNetUnitPrice(product.getNetPrice() != null ? product.getNetPrice() : product.getListPrice());
			updateEntry.setPriceUpdated(Boolean.TRUE);
		}
		else
		{
			updateEntry.setBasePrice(null);
			updateEntry.setNetUnitPrice(null);
			updateEntry.setPriceUpdated(Boolean.TRUE);
		}
	}

	private double calculateCheckoutProductPrice(final Double price, final Long quantity)
	{
		if (asahiCoreUtil.getSessionCheckoutFlag())
		{
			return Math.ceil(price / quantity);
		}
		return 0;
	}

	/**
	 * update order model and order entry model bases on price fetching from dynamics
	 */
	@Override
	public boolean updateOrderModel(final AbstractOrderModel order, final boolean isFreightIncluded,
			final boolean isDeliveryChargeApplicable)
	{

		// first get all entries for calc
		boolean isProductDataFetched = false;
		double subtotal = 0d;
		final String deliverySurchargeCode = asahiConfigurationService.getString(PRODUCT_CODE_FOR_DELIVERY_SURCHARGE,
				"delivery_product");
		final ApbPriceData priceData = fetchPriceDataFromBackend(order.getEntries(), isDeliveryChargeApplicable,
				deliverySurchargeCode);
		if (!isDeliveryChargeApplicable)
		{
			order.setDeliveryCost(0d);
			order.setDeliverySurChargeGST(0d);
		}
		if (priceData != null && CollectionUtils.isNotEmpty(priceData.getProductPriceInfo()))
		{
			order.setPriceUpdated(true);
			isProductDataFetched = true;
			final List<ApbProductPriceInfo> productList = priceData.getProductPriceInfo();
			final CurrencyModel curr = order.getCurrency();
			productList.forEach(product -> {
				order.getEntries().forEach(updateEntry -> {
					if (product.getCode().equals(updateEntry.getProduct().getCode())
							&& (null == updateEntry.getIsBonusStock() || !updateEntry.getIsBonusStock())
									&& !product.isBonus())
					{
						updatePrices(product, updateEntry, curr);
					}
					else if (product.getCode().equals(deliverySurchargeCode))
					{
						order.setDeliveryCost(product.getNetPrice());
					}
				});
			});
			order.setFreight(priceData.getFreight());
			order.setOrderGST(priceData.getGST());
			order.setTotalPrice(priceData.getSubTotal());
			if (priceData.getSubTotal() != null && priceData.getSubTotal() > 0)
			{
				subtotal = priceData.getSubTotal() - priceData.getFreight() - order.getDeliveryCost();
			}
			order.setSubtotal(subtotal);
		}
		else
		{
			order.setFreight(0d);
			order.setFreightGST(0d);
			order.setDeliveryCost(0d);
			order.setDeliverySurChargeGST(0d);
			order.setOrderGST(0d);
			order.setOrderWET(0d);
			order.setSubtotal(0d);
			order.setTotalPrice(0d);
			order.setPriceUpdated(false);
			for (final AbstractOrderEntryModel productEntry : order.getEntries())
			{
				productEntry.setBasePrice(0d);
				productEntry.setTotalPrice(0d);
			}
		}
		getModelService().save(order);
		return isProductDataFetched;
	}

	private void updatePrices(final ApbProductPriceInfo product, final AbstractOrderEntryModel updateEntry,
			final CurrencyModel curr)
	{
		updateEntry.setBasePrice(product.getNetPrice());
		updateEntry.setPriceUpdated(Boolean.TRUE);
		//TaxValue gst = new TaxValue(TaxType.GST.getCode(), product.getGST(), true, curr.getIsocode());
		final TaxValue wet = new TaxValue(TaxType.WET.getCode(), product.getWET(), true, curr.getIsocode());

		final List<TaxValue> taxList = new ArrayList<>();
		//taxList.add(gst);
		taxList.add(wet);
		updateEntry.setTaxValues(taxList);
		setCalculatedStatus(updateEntry);
		getModelService().save(updateEntry);
	}


	private ApbPriceData fetchPriceDataFromBackend(final List<AbstractOrderEntryModel> entries,
			final boolean isDeliveryChargeApplicable, final String deliverySurchargeCode)
	{
		final Map<String, Map<String, Long>> productMap = new HashMap<>();
		final Map<String, Map<String, Long>> bonusMap = new HashMap<>();

		for (final AbstractOrderEntryModel orderEntry : entries)
		{
				if (null != orderEntry.getIsBonusStock() && orderEntry.getIsBonusStock())
				{
					final Map<String, Long> lineNumberAndQty = new HashMap<>();
					lineNumberAndQty.put(orderEntry.getEntryNumber().toString(), orderEntry.getQuantity());
					bonusMap.put(orderEntry.getProduct().getCode(), lineNumberAndQty);
				}
				else
				{
					final Map<String, Long> lineNumberAndQty = new HashMap<>();
					lineNumberAndQty.put(orderEntry.getEntryNumber().toString(), orderEntry.getQuantity());
					productMap.put(orderEntry.getProduct().getCode(), lineNumberAndQty);
				}

		}
		if (isDeliveryChargeApplicable && null != deliverySurchargeCode)
		{
			final Map<String, Long> lineNumberAndQty = new HashMap<>();
			lineNumberAndQty.put("DeliveryCharge", 1L);
			productMap.put(deliverySurchargeCode, lineNumberAndQty);
		}

		final ApbPriceRequestData requestData = new ApbPriceRequestData();
		requestData.setProductQuantityMap(productMap);
		requestData.setBonusStatusMap(bonusMap);
		requestData.setAccNum(getAccNumForCurrentB2BUnit());
		requestData.setFreightIncluded(Boolean.TRUE);

		return asahiPriceIntegrationService.getProductsPrice(requestData);

	}


	private void calculateOrderTotal(final AbstractOrderModel order) throws CalculationException
	{
		double subtotal = 0.0;
		for (final AbstractOrderEntryModel e : order.getEntries())
		{
			calculateTotals(e, true);
			if (asahiSiteUtil.isApb()
					|| (asahiSiteUtil.isSga() && !inclusionExclusionProductStrategy.isProductIncluded(e.getProduct().getCode())))
			{
				subtotal += e.getTotalPrice().doubleValue();
			}
		}
		order.setTotalPrice(Double.valueOf(subtotal));
	}

	@Override
	protected Map resetAllValues(final AbstractOrderModel order) throws CalculationException
	{
		// set subtotal and get tax value map
		final Map<TaxValue, Map<Set<TaxValue>, Double>> taxValueMap = calculateSubtotal(order, false);
		return taxValueMap;

	}

	private boolean isDeliveryChargeApplicable(final AbstractOrderModel order)
	{
		boolean isAllAlcohol = true;
		boolean isDeliveryChargeApplicable = false;
		long totalQuantity = 0;
		final String packType = asahiConfigurationService.getString(PACK_TYPE, "28");
		final String nonAlcoholicType = asahiConfigurationService.getString(NON_ALCOHOLIC_TYPE, "10");

		final List<String> packTypeList = new ArrayList<>(Arrays.asList(packType.split(",")));
		final List<String> nonAlcoholicTypeList = new ArrayList<>(Arrays.asList(nonAlcoholicType.split(",")));

		for (final AbstractOrderEntryModel entryProduct : order.getEntries())
		{
			if (!entryProduct.getIsBonusStock())
			{
				totalQuantity = totalQuantity + entryProduct.getQuantity();
			}

			final ApbProductModel product = (ApbProductModel) entryProduct.getProduct();

			if (product.getPackageType() != null && packTypeList.contains(product.getPackageType().getCode()))
			{
				return false;
			}
			else if (product.getAlcoholType() != null && nonAlcoholicTypeList.contains(product.getAlcoholType().getCode()))
			{
				isAllAlcohol = false;
			}
		}
		final String caseCountStr = asahiConfigurationService.getString(CASES_COUNT_FOR_DELIVERY_FEE, "5");
		final int caseCount = Integer.parseInt(caseCountStr);


		if (!isAllAlcohol && totalQuantity < caseCount)
		{
			isDeliveryChargeApplicable = true;
		}

		//check if the cart contains only bonus product
		if (!asahiCoreUtil.isNonBonusProductExist(order.getEntries()))
		{
			isDeliveryChargeApplicable = false;
		}
		return isDeliveryChargeApplicable;
	}

	@Override
	public void calculateTotals(final AbstractOrderEntryModel entry, final boolean recalculate)
	{
		if (recalculate || orderRequiresCalculationStrategy.requiresCalculation(entry))
		{
			final AbstractOrderModel order = entry.getOrder();
			final CurrencyModel curr = order.getCurrency();
			final int digits = curr.getDigits().intValue();

			final double totalPriceWithoutDiscount;

			double minicartSubTotal = 0.0d;

			if (asahiSiteUtil.isSga())
			{
				totalPriceWithoutDiscount = commonI18NService
						.roundCurrency((entry.getNetUnitPrice() != null ? entry.getNetUnitPrice().doubleValue() : 0.0D)
								* entry.getQuantity().longValue(), digits);
				minicartSubTotal = commonI18NService
						.roundCurrency((entry.getBasePrice() != null ? entry.getBasePrice().doubleValue() : 0.0D)
								* entry.getQuantity().longValue(), digits);
			}
			else
			{
				totalPriceWithoutDiscount = commonI18NService.roundCurrency(
						(null != entry.getBasePrice() ? entry.getBasePrice().doubleValue() : 0.0D) * entry.getQuantity().longValue(),
						digits);
			}
			// set total price
			entry.setTotalPrice(Double.valueOf(totalPriceWithoutDiscount));

			// SGA - minicart subtotal
			entry.setMinicartSubTotal(Double.valueOf(minicartSubTotal));
			// apply tax values too
			calculateTotalTaxValues(entry);
			setCalculatedStatus(entry);
			getModelService().save(entry);
		}
	}

	protected void calculateTaxValues(final AbstractOrderModel order, final boolean recalculate, final int digits,
			final double taxAdjustmentFactor, final Map<TaxValue, Map<Set<TaxValue>, Double>> taxValueMap)
	{
		if (recalculate || orderRequiresCalculationStrategy.requiresCalculation(order))
		{
			final CurrencyModel curr = order.getCurrency();
			final String iso = curr.getIsocode();

			final boolean net = order.getNet().booleanValue();
			double totalGSTTaxes = 0.0;
			double totalWETTaxes = 0.0;
			if (MapUtils.isNotEmpty(taxValueMap))
			{

				final Collection orderTaxValues = new ArrayList<TaxValue>(taxValueMap.size());

				for (final Map.Entry<TaxValue, Map<Set<TaxValue>, Double>> taxValueEntry : taxValueMap.entrySet())
				{
					final TaxValue unappliedTaxValue = taxValueEntry.getKey();

					final Map<Set<TaxValue>, Double> taxGroups = taxValueEntry.getValue();

					final TaxValue appliedTaxValue;

					final double quantitySum = taxGroups.entrySet().iterator().next().getValue().doubleValue();
					appliedTaxValue = calculateAbsoluteTotalTaxValue(curr, iso, digits, net, unappliedTaxValue, quantitySum);

					if (TaxType.GST.getCode().equalsIgnoreCase(unappliedTaxValue.getCode()))
					{
						totalGSTTaxes += appliedTaxValue.getAppliedValue();
					}
					else if (TaxType.WET.getCode().equalsIgnoreCase(unappliedTaxValue.getCode()))
					{
						totalWETTaxes += appliedTaxValue.getAppliedValue();
					}
				}
				if (order.getDeliverySurChargeGST() != null)
				{
					totalGSTTaxes += order.getDeliverySurChargeGST();
				}

				if (order.getFreightGST() != null)
				{
					totalGSTTaxes += order.getFreightGST();
				}

				if (asahiSiteUtil.isApb() && null != order.getOrderGST())
				{
					totalGSTTaxes += order.getOrderGST();
				}

				final double totalRoundedGstTaxes = commonI18NService.roundCurrency(totalGSTTaxes, digits);
				final double totalRoundedWetTaxes = commonI18NService.roundCurrency(totalWETTaxes, digits);
				order.setOrderWET(Double.valueOf(totalRoundedWetTaxes));
				order.setOrderGST(Double.valueOf(totalRoundedGstTaxes));
				saveOrder(order);
			}
		}
	}

	@Override
	protected void calculateTotals(final AbstractOrderModel order, final boolean recalculate,
			final Map<TaxValue, Map<Set<TaxValue>, Double>> taxValueMap) throws CalculationException
	{
		if (recalculate || orderRequiresCalculationStrategy.requiresCalculation(order))
		{
			if (asahiSiteUtil.isSga())
			{
				calculateOrderTaxesAndDoTotal(order, recalculate, taxValueMap);
				return;
			}
			final CurrencyModel curr = order.getCurrency();
			final int digits = curr.getDigits().intValue();
			// subtotal
			final double subtotal = order.getSubtotal().doubleValue();
			double total = subtotal;
			// set total
			if (order.getDeliveryCost() != null)
			{
				total += order.getDeliveryCost().doubleValue();
			}
			if (order.getFreight() != null)
			{
				total += order.getFreight().doubleValue();
			}
			final double totalRounded = commonI18NService.roundCurrency(total, digits);
			order.setTotalPrice(Double.valueOf(totalRounded));
			// taxes
			calculateTaxValues(order, recalculate, digits, getTaxCorrectionFactor(taxValueMap, subtotal, total, order), taxValueMap);
			setCalculatedStatus(order);
			saveOrder(order);
		}
	}

	private void calculateOrderTaxesAndDoTotal(final AbstractOrderModel order, final boolean recalculate,
			final Map<TaxValue, Map<Set<TaxValue>, Double>> taxValueMap)
	{
		final CurrencyModel curr = order.getCurrency();
		final int digits = curr.getDigits().intValue();
		final double subtotal = order.getSubtotal().doubleValue();
		order.setTotalPrice(Double.valueOf(commonI18NService.roundCurrency(subtotal, digits)));
		//calculateSGATaxValues(order, recalculate, digits, 0, taxValueMap);
		calculateTotalDiscount(order, digits);
		calculateTotalCDLAndGST(order, digits);
		setCalculatedStatus(order);
		saveOrder(order);
	}

	/**
	 * Calculate total CDL and GST.
	 *
	 * @param order the order
	 * @param digits the digits
	 */
	private void calculateTotalCDLAndGST(final AbstractOrderModel order, final int digits) {
		order.setOrderCDL(0.0D);
		order.setOrderGST(0.0D);
		try
		{
		if (asahiCoreUtil.getSessionCheckoutFlag())
		{
			final AtomicDouble totalCDLValue = new AtomicDouble(0.0D);
			final AtomicDouble totalGSTValue = new AtomicDouble(0.0D);

				order.getEntries().stream()
						.filter(e -> BooleanUtils.isFalse(e.getIsBonusStock()) && BooleanUtils.isFalse(e.getIsFreeGood()))
						.forEach(entry -> {
				final AsahiProductInfo product = asahiCoreUtil.getProductFromSessionInclusionList(entry.getProduct().getCode());
				if(null!=product){
					totalCDLValue.getAndAdd(product.getTotalCdl() != null ? product.getTotalCdl() : 0.0D);
					totalGSTValue.getAndAdd(product.getTotalGst() != null ? product.getTotalGst() : 0.0D);
				}
			});

			final double totalRoundedGST = commonI18NService.roundCurrency(totalGSTValue.get(), digits);
			final double totalRoundedCDL = commonI18NService.roundCurrency(totalCDLValue.get(), digits);
			order.setOrderCDL(Double.valueOf(totalRoundedCDL));
			order.setOrderGST(Double.valueOf(totalRoundedGST));
		}
		else
		{
			final AtomicDouble totalCDLValue = new AtomicDouble(0.0D);
			final AtomicDouble totalGSTValue = new AtomicDouble(0.0D);

				order.getEntries().stream()
						.filter(e -> BooleanUtils.isFalse(e.getIsBonusStock()) && BooleanUtils.isFalse(e.getIsFreeGood()))
						.forEach(entry -> {
				final AsahiProductInfo product = asahiCoreUtil.getProductFromSessionInclusionList(entry.getProduct().getCode());
				if(null!=product){
					totalCDLValue.getAndAdd(product.getContainerDepositLevy() != null ? product.getContainerDepositLevy() : 0.0D);
					totalGSTValue.getAndAdd(product.getGst() != null ? product.getGst() : 0.0D);
				}
			});

			final double totalRoundedGST = commonI18NService.roundCurrency(totalGSTValue.get(), digits);
			final double totalRoundedCDL = commonI18NService.roundCurrency(totalCDLValue.get(), digits);
			order.setOrderCDL(Double.valueOf(totalRoundedCDL));
			order.setOrderGST(Double.valueOf(totalRoundedGST));
		}
		}catch (final Exception exp)
		{
			LOG.info("Error occured while getting total CDL and GST " + exp);
		}

	}

	/**
	 * Calculate total discount.
	 *
	 * @param order the order
	 * @param digits the digits
	 */
	private void calculateTotalDiscount(final AbstractOrderModel order, final int digits)
	{
		order.setTotalDiscounts(0.0D);
		try
		{
			if (asahiCoreUtil.getSessionCheckoutFlag())
			{
				final AtomicDouble discountValue = new AtomicDouble(0.0D);

				order.getEntries().stream()
						.filter(e -> BooleanUtils.isFalse(e.getIsBonusStock()) && BooleanUtils.isFalse(e.getIsFreeGood()))
						.forEach(entry -> {
					final AsahiProductInfo product = asahiCoreUtil.getProductFromSessionInclusionList(entry.getProduct().getCode());
					if (null != product && null!=product.getIsPromoFlag() && product.getIsPromoFlag())
					{
						discountValue.getAndAdd(product.getDiscount() != null ? product.getDiscount() : 0.0D);
					}
				});
				order.setTotalDiscounts(discountValue.get());
			}
			else
			{
				final AtomicDouble discountValue = new AtomicDouble(0.0D);

				order.getEntries().stream().forEach(entry -> {
					final AsahiProductInfo product = asahiCoreUtil.getProductFromSessionInclusionList(entry.getProduct().getCode());
					if (null != product && null != product.getListPrice() && null != product.getNetPrice()
							&& product.getListPrice() > product.getNetPrice())
					{
						discountValue.getAndAdd((product.getListPrice() - product.getNetPrice()) * entry.getQuantity());
					}
				});
				order.setTotalDiscounts(discountValue.get());

			}
		}
		catch (final Exception exp)
		{
			LOG.info("Error occured while getting the product discount" + exp);
		}
	}

	private void calculateSGATaxValues(final AbstractOrderModel order, final boolean recalculate, final int digits, final int i,
			final Map<TaxValue, Map<Set<TaxValue>, Double>> taxValueMap)
	{
		if (recalculate || orderRequiresCalculationStrategy.requiresCalculation(order))
		{
			double totalGST = 0.0;
			double totalCDL = 0.0;
			if (MapUtils.isNotEmpty(taxValueMap))
			{
				for (final Map.Entry<TaxValue, Map<Set<TaxValue>, Double>> taxValueEntry : taxValueMap.entrySet())
				{
					final TaxValue unappliedTaxValue = taxValueEntry.getKey();

					final Map<Set<TaxValue>, Double> taxGroups = taxValueEntry.getValue();

					final TaxValue appliedTaxValue;

					double quantitySum;

					/**
					 * At time of checkout, we will be getting total GST and total CDL from ECC per entry level.
					 */
					if (asahiCoreUtil.getSessionCheckoutFlag())
					{
						quantitySum = 1.0D;
					}
					else
					{
						quantitySum = taxGroups.entrySet().iterator().next().getValue().doubleValue();
					}
					appliedTaxValue = calculateAbsoluteTotalTaxValue(order.getCurrency(), order.getCurrency().getIsocode(), digits,
							order.getNet().booleanValue(), unappliedTaxValue, quantitySum);

					if (TaxType.GST.getCode().equalsIgnoreCase(unappliedTaxValue.getCode()))
					{
						totalGST += appliedTaxValue.getAppliedValue();
					}
					else if (TaxType.CDL.getCode().equalsIgnoreCase(unappliedTaxValue.getCode()))
					{
						totalCDL += appliedTaxValue.getAppliedValue();
					}
				}

				final double totalRoundedGST = commonI18NService.roundCurrency(totalGST, digits);
				final double totalRoundedCDL = commonI18NService.roundCurrency(totalCDL, digits);
				order.setOrderCDL(Double.valueOf(totalRoundedCDL));
				order.setOrderGST(Double.valueOf(totalRoundedGST));
				saveOrder(order);
			}
		}
	}

	private String getAccNumForCurrentB2BUnit()
	{
		final UserModel user = userService.getCurrentUser();
		if (null != user && user instanceof B2BCustomerModel)
		{
			final B2BCustomerModel customer = (B2BCustomerModel) user;
			final B2BUnitModel b2bUnit = customer.getDefaultB2BUnit();
			if (b2bUnit instanceof AsahiB2BUnitModel)
			{
				final AsahiB2BUnitModel asahiB2BUnitModel = (AsahiB2BUnitModel) b2bUnit;
				return null != asahiB2BUnitModel.getAccountNum() ? asahiB2BUnitModel.getAccountNum() : null;
			}
		}
		return null;
	}


	@Override
	public void updatePriceForProduct(final AbstractOrderEntryModel entry, final CurrencyModel curr, final long quantity)
	{
		if (asahiSiteUtil.isSga())
		{
			final AsahiProductInfo product = asahiCoreUtil.getProductFromSessionInclusionList(entry.getProduct().getCode());
			updateProductPriceFromSession(product, entry);
			return;
		}

		final Map<String, Map<String, Long>> productMap = new HashMap<>();
		final Map<String, Map<String, Long>> bonusMap = new HashMap<>();

		if (null != entry.getIsBonusStock() && entry.getIsBonusStock())
		{
			final Map<String, Long> entryLine = new HashMap<>();
			entryLine.put(entry.getEntryNumber().toString(), quantity);
			bonusMap.put(entry.getProduct().getCode(), entryLine);
		}
		else
		{
			final Map<String, Long> entryLine = new HashMap<>();
			entryLine.put(entry.getEntryNumber().toString(), quantity);
			productMap.put(entry.getProduct().getCode(), entryLine);
		}

		final ApbPriceRequestData requestData = new ApbPriceRequestData();
		requestData.setProductQuantityMap(productMap);
		requestData.setBonusStatusMap(bonusMap);
		requestData.setAccNum(getAccNumForCurrentB2BUnit());
		requestData.setFreightIncluded(Boolean.TRUE);

		final ApbPriceData priceData = asahiPriceIntegrationService.getProductsPrice(requestData);
		if (priceData != null && CollectionUtils.isNotEmpty(priceData.getProductPriceInfo()))
		{
			for (final ApbProductPriceInfo product : priceData.getProductPriceInfo())
			{
				if (product.getCode().equals(entry.getProduct().getCode()))
				{
					updatePrices(product, entry, curr);
				}
			}
		}
	}

	@Override
	protected Map<TaxValue, Map<Set<TaxValue>, Double>> calculateSubtotal(final AbstractOrderModel order,
			final boolean recalculate)
	{
		if (recalculate || orderRequiresCalculationStrategy.requiresCalculation(order))
		{
			double subtotal = 0.0;
			double sgaMiniCartSubtotal = 0.0;
			// entry grouping via map { tax code -> Double }
			final List<AbstractOrderEntryModel> entries = order.getEntries();
			final Map<TaxValue, Map<Set<TaxValue>, Double>> taxValueMap = new LinkedHashMap<TaxValue, Map<Set<TaxValue>, Double>>(
					entries.size() * 2);

			for (final AbstractOrderEntryModel entry : entries)
			{
				if (asahiSiteUtil.isApb()
						|| (asahiSiteUtil.isSga() && inclusionExclusionProductStrategy.isProductIncluded(entry.getProduct().getCode())))
				{

					calculateTotals(entry, recalculate);
					final double entryTotal = entry.getTotalPrice().doubleValue();
					subtotal += entryTotal;

					final double minicartEntryTotal = entry.getMinicartSubTotal().doubleValue();
					sgaMiniCartSubtotal += minicartEntryTotal;

					// use un-applied version of tax values!!!
					final Collection<TaxValue> allTaxValues = entry.getTaxValues();
					for (final TaxValue taxValue : allTaxValues)
					{
						addAbsoluteEntryTaxValue(entry.getQuantity().longValue(), taxValue.unapply(), taxValueMap);
					}
				}
			}

			if (asahiSiteUtil.isSga())
			{
				// store subtotal
				subtotal = commonI18NService.roundCurrency(subtotal, order.getCurrency().getDigits().intValue());
				sgaMiniCartSubtotal = commonI18NService.roundCurrency(sgaMiniCartSubtotal, order.getCurrency().getDigits().intValue());
				order.setSubtotal(Double.valueOf(subtotal));
				order.setMinicartSubTotal(sgaMiniCartSubtotal);
			}

			return taxValueMap;
		}
		return Collections.EMPTY_MAP;
	}

}
