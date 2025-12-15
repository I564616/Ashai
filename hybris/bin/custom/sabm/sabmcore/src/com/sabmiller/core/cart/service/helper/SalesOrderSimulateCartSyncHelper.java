/**
 *
 */
package com.sabmiller.core.cart.service.helper;


import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.TaxValue;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.sabmiller.core.cart.errors.exceptions.SalesOrderSimulateCartUpdateException;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.EntryOfferInfoModel;
import com.sabmiller.core.util.SabmDateUtils;
import com.sabmiller.integration.enums.ErrorEventType;
import com.sabmiller.integration.facade.ErrorEventFacade;
import com.sabmiller.integration.sap.enums.PricingCalculationType;
import com.sabmiller.integration.sap.enums.PricingConditionType;
import com.sabmiller.integration.sap.ordersimulate.response.SalesOrderSimulateResponse;
import com.sabmiller.integration.sap.ordersimulate.response.SalesOrderSimulateResponse.Error;
import com.sabmiller.integration.sap.ordersimulate.response.SalesOrderSimulateResponse.SalesOrderResItem;
import com.sabmiller.integration.sap.ordersimulate.response.SalesOrderSimulateResponse.SalesOrderResItem.SalesOrderFreeGoods;
import com.sabmiller.integration.sap.ordersimulate.response.SalesOrderSimulateResponse.SalesOrderResItem.SalesOrderItemCondition;
import com.sabmiller.integration.sap.ordersimulate.response.SalesOrderSimulateResponse.SalesOrderResItem.SalesOrderItemCondition.SalesOrderItemScales;
import com.sabmiller.integration.sap.ordersimulate.response.SalesOrderSimulateResponse.SalesOrderResItem.SalesOrderItemScheduling;


/**
 *
 */
public class SalesOrderSimulateCartSyncHelper
{
	private static final Logger LOG = LoggerFactory.getLogger(SalesOrderSimulateCartSyncHelper.class);

	private ProductService productService;
	private UnitService unitService;
	private ModelService modelService;
	private CommonI18NService commonI18NService;
	private ErrorEventFacade errorEventFacade;


	public AbstractOrderEntryModel createFreeGoods(final SalesOrderResItem item) throws SalesOrderSimulateCartUpdateException
	{
		final AbstractOrderEntryModel freeGoodEntry = getModelService().create(CartEntryModel.class);

		final ProductModel product = getProduct(item.getMaterialNumber());
		freeGoodEntry.setProduct(product);
		freeGoodEntry.setIsFreeGood(Boolean.TRUE);
		freeGoodEntry.setBasePrice(0d);
		freeGoodEntry.setTotalPrice(0d);
		freeGoodEntry.setQuantity(getQuantity(item.getMaterialQuantity()));
		freeGoodEntry.setSapLineNumber(item.getLineNumber());
		if (CollectionUtils.isNotEmpty(item.getDealCondition()))
		{
			freeGoodEntry.setFreeGoodsForDeal(item.getDealCondition().get(0).getDealConditionNumber());
		}
		try
		{
			freeGoodEntry.setUnit(getUnit(item.getUnitOfMeasure()));
		}
		catch (UnknownIdentifierException | AmbiguousIdentifierException e)
		{
			LOG.error("Not able to find unit in hybris", e);
			throw new SalesOrderSimulateCartUpdateException("Not able to find unit in hybris");
		}
		return freeGoodEntry;
	}

	/*
	 * return false if low or out of stock
	 */
	public boolean updateEntryProductAvailabilty(final AbstractOrderEntryModel entryModel, final Date requestedDeliveryDate,
			final List<SalesOrderItemScheduling> productAvailabilityList) throws SalesOrderSimulateCartUpdateException
	{

		if (CollectionUtils.isNotEmpty(productAvailabilityList))
		{
			for (final SalesOrderItemScheduling scheduleEtry : productAvailabilityList)
			{
				final Long enteredQuantity = entryModel.getQuantity();

				final Long confirmedQuantity = getQuantity(scheduleEtry.getConfirmedQty());
				if (checkRequestedDeliveryDate(scheduleEtry.getRequestedDeliveryDate(), requestedDeliveryDate)
						&& confirmedQuantity != null && enteredQuantity != null)
				{

					if (confirmedQuantity.compareTo(enteredQuantity) == 0)
					{
						return true;
					}

					if (confirmedQuantity == 0)
					{
						entryModel.setAvailabilityInfo(StockLevelStatus.OUTOFSTOCK.getCode());
						return false;
					}

					if (confirmedQuantity < enteredQuantity)
					{
						entryModel.setAvailabilityInfo(StockLevelStatus.LOWSTOCK.getCode());
						entryModel.setSapConfirmedQuantity(confirmedQuantity);
						return false;
					}
				}
			}
		}
		return false;
	}

	public void updateEntryPrices(final AbstractOrderEntryModel entryModel, final SalesOrderResItem item, final CurrencyModel curr)
			throws SalesOrderSimulateCartUpdateException
	{
		double gst = 0;
		double loyaltyFee = 0;
		double autoPayAdvantageDiscount = 0;
		double autoPayAdvantagePlusDiscount = 0;
		double wet = 0;
		double entryBasePrice = 0;
		double entryTotalPrice = 0;
		double deliveryCost = 0;
		double freightLimit = 0;
		double deposit = 0;
		double totalPrice = 0;

		final String currencyCode = curr.getIsocode();
		final int digits = curr.getDigits();
		final UnitModel itemUnit = getUnit(item.getUnitOfMeasure());

		final List<EntryOfferInfoModel> discountInfo = new ArrayList<EntryOfferInfoModel>();
		final List<SalesOrderItemCondition> discountCoditionList = new ArrayList<SalesOrderItemCondition>();

		for (final SalesOrderItemCondition itemCondition : item.getSalesOrderItemCondition())
		{
			final String sapConditionType = StringUtils.trim(itemCondition.getConditionType());
			final PricingConditionType conditionCategoryType = PricingConditionType.lookup(sapConditionType);

			final double totalConditionValue = getDoubleValue(itemCondition.getTotalCondValue());
			final double conditionAmount = getDoubleValue(itemCondition.getConditionAmount());
			final String conditionUom = StringUtils.trim(itemCondition.getConditionUOM());

			switch (conditionCategoryType)
			{
				case PRICE:
					checkBasePriceConditionUom(conditionUom, entryModel.getUnit().getCode());
					entryTotalPrice += totalConditionValue;
					entryBasePrice += conditionAmount;
					break;

				case DISCOUNT:
					discountCoditionList.add(itemCondition);
					break;

				case COMPLEX_DEAL_TYPE:
					discountCoditionList.add(itemCondition);
					break;

				case LIMITED_DEAL_TYPE:
					discountCoditionList.add(itemCondition);
					break;

				case GST:
					gst += totalConditionValue;
					break;

				case LOYALTY_FEE:
					loyaltyFee += totalConditionValue;
					break;

				case AUTO_PAY_ADVANTAGE_DISCOUNT:
					autoPayAdvantageDiscount += totalConditionValue;
					break;

				case AUTO_PAY_ADVANTAGE_PLUS_DISCOUNT:
					autoPayAdvantagePlusDiscount += totalConditionValue;
					break;

				case WET:
					wet += totalConditionValue;
					break;

				case DELIVERY_COST:
					deliveryCost += totalConditionValue;
					break;

				case CONTAINER_DEPOSIT:
					deposit += totalConditionValue;
					break;

				case FREIGHT_LIMIT:
					freightLimit += conditionAmount;
					break;

				default:
					LOG.error("Unknown condition type from SAP in sales order simulate" + sapConditionType);
					break;
			}
		}

		gst = commonI18NService.roundCurrency(gst, digits);
		loyaltyFee = commonI18NService.roundCurrency(loyaltyFee, digits);
		autoPayAdvantageDiscount = commonI18NService.roundCurrency(autoPayAdvantageDiscount, digits);
		autoPayAdvantagePlusDiscount = commonI18NService.roundCurrency(autoPayAdvantagePlusDiscount, digits);
		wet = commonI18NService.roundCurrency(wet, digits);
		final TaxValue gstTaxValue = new TaxValue(SabmCoreConstants.GST, gst, true, currencyCode);
		final TaxValue wetTaxValue = new TaxValue(SabmCoreConstants.WET, wet, true, currencyCode);
		final Collection<TaxValue> entryTaxValues = new ArrayList<TaxValue>();

		entryTaxValues.add(gstTaxValue);
		entryTaxValues.add(wetTaxValue);


		double totalEntryDiscount = processDiscountConditions(discountCoditionList, currencyCode, discountInfo, itemUnit);

		entryBasePrice = commonI18NService.roundCurrency(entryBasePrice, digits);
		totalEntryDiscount = commonI18NService.roundCurrency(totalEntryDiscount, digits);
		totalPrice = commonI18NService.roundCurrency(entryTotalPrice + totalEntryDiscount + wet + deposit, digits);
		deliveryCost = commonI18NService.roundCurrency(deliveryCost, digits);
		freightLimit = commonI18NService.roundCurrency(freightLimit, digits);
		deposit = commonI18NService.roundCurrency(deposit, digits);

		entryModel.setBasePrice(entryBasePrice);
		entryModel.setTaxValues(entryTaxValues);
		entryModel.setTotalEntryDiscount(totalEntryDiscount);
		entryModel.setLoyaltyFee(loyaltyFee);
		entryModel.setAutoPayAdvantageDiscount(autoPayAdvantageDiscount);
		entryModel.setAutoPayAdvantagePlusDiscount(autoPayAdvantagePlusDiscount);

		if (CollectionUtils.isNotEmpty(entryModel.getOfferInfo()))
		{
			getModelService().removeAll(entryModel.getOfferInfo());
		}
		//if it is a free goods, should not display discount info.
		if (!entryModel.getIsFreeGood())
		{
			entryModel.setOfferInfo(discountInfo);
		}
		entryModel.setTotalPrice(totalPrice); // discount values will be -ve from SAP
		entryModel.setDeliveryCost(deliveryCost);
		entryModel.setFreightLimit(freightLimit);
		entryModel.setDeposit(deposit);


	}


	/**
	 * @param conditionUom
	 * @param code
	 * @throws SalesOrderSimulateCartUpdateException
	 */
	protected void checkBasePriceConditionUom(final String conditionUom, final String code)
			throws SalesOrderSimulateCartUpdateException
	{
		if (!StringUtils.equalsIgnoreCase(conditionUom, code))
		{
			throw new SalesOrderSimulateCartUpdateException("uom in response does not match with request uom");
		}
	}

	/**
	 * @param discountCoditionList
	 * @param currencyCode
	 * @param discountInfo
	 * @param itemUnit
	 * @throws SalesOrderSimulateCartUpdateException
	 */
	protected double processDiscountConditions(final List<SalesOrderItemCondition> discountCoditionList, final String currencyCode,
			final List<EntryOfferInfoModel> discountInfo, final UnitModel itemUnit) throws SalesOrderSimulateCartUpdateException
	{
		double entryTotalDiscount = 0;

		if (CollectionUtils.isNotEmpty(discountCoditionList))
		{
			for (final SalesOrderItemCondition itemCondition : discountCoditionList)
			{
				entryTotalDiscount += getDoubleValue(itemCondition.getTotalCondValue());
				// skip adding every day discount YDE0 saving title.
				// Skip adding all discounts saving title except YDD0 , as per new sap pricing chnages
				if (getDoubleValue(itemCondition.getTotalCondValue()) != 0
						&& StringUtils.equalsIgnoreCase("YDD0", StringUtils.trim(itemCondition.getConditionType())))
				{
					addDiscountInfo(discountInfo, currencyCode, itemCondition, itemUnit);
				}

			}
		}
		return entryTotalDiscount;
	}

	protected void addDiscountInfo(final List<EntryOfferInfoModel> discountInfo, final String currencyCode,
			final SalesOrderItemCondition itemCondition, final UnitModel itemUnit) throws SalesOrderSimulateCartUpdateException
	{
		//Get $2 off on each case when you buy 10 or more
		final List<SalesOrderItemScales> scales = itemCondition.getSalesOrderItemScales();

		if (CollectionUtils.isNotEmpty(scales))
		{
			for (final SalesOrderItemScales scale : scales)
			{
				final EntryOfferInfoModel offerInfo = createOfferInfoType(itemCondition);

				offerInfo.setScaleAmount(StringUtils.removeEnd(StringUtils.trim(scale.getAmount()), "-"));
				offerInfo.setTotalCondValue(StringUtils.removeEnd(StringUtils.trim(itemCondition.getTotalCondValue()), "-"));

				// if scale qty is 0.00 take the item condition >> condition pricing unit - SABMC-849
				if (getQuantity(scale.getScaleQuantity()) > 0)
				{
					offerInfo.setScaleQuantity(getQuantity(scale.getScaleQuantity()));
				}
				else
				{
					offerInfo.setScaleQuantity(getQuantity(itemCondition.getConditionPricingUnit()));
				}

				offerInfo.setCurrencyCode(currencyCode);
				UnitModel unit = itemUnit;
				if (itemCondition.getConditionUOM() != null)
				{
					unit = getUnit(itemCondition.getConditionUOM());
				}
				offerInfo.setScaleUnit(unit);

				final PricingCalculationType calType = PricingCalculationType.lookup(StringUtils.trim(scale.getCalculationType()));

				if (!calType.equals(PricingCalculationType.UNKNOWN_CAL_TYPE))
				{
					offerInfo.setScaleAmountType(calType.getType());
				}
				discountInfo.add(offerInfo);

			}
		}
		else /* If no scale consider the item condition values */
		{
			final EntryOfferInfoModel offerInfo = createOfferInfoType(itemCondition);
			offerInfo.setScaleAmount(StringUtils.removeEnd(StringUtils.trim(itemCondition.getConditionAmount()), "-"));
			offerInfo.setScaleQuantity(getQuantity(itemCondition.getConditionPricingUnit()));
			offerInfo.setCurrencyCode(currencyCode);
			offerInfo.setTotalCondValue(StringUtils.removeEnd(StringUtils.trim(itemCondition.getTotalCondValue()), "-"));
			UnitModel unit = itemUnit;
			if (itemCondition.getConditionUOM() != null)
			{
				unit = getUnit(itemCondition.getConditionUOM());
			}
			offerInfo.setScaleUnit(unit);

			final PricingCalculationType calType = PricingCalculationType
					.lookup(StringUtils.trim(itemCondition.getCalculationType()));

			if (!calType.equals(PricingCalculationType.UNKNOWN_CAL_TYPE))
			{
				offerInfo.setScaleAmountType(calType.getType());
			}
			discountInfo.add(offerInfo);

		}
	}

	protected EntryOfferInfoModel createOfferInfoType(final SalesOrderItemCondition itemCondition)
	{
		final EntryOfferInfoModel offerInfo = modelService.create(EntryOfferInfoModel.class);

		final PricingConditionType dealType = PricingConditionType.lookup(StringUtils.trim(itemCondition.getConditionType()));

		if (PricingConditionType.COMPLEX_DEAL_TYPE.equals(dealType))
		{
			offerInfo.setOfferType(SabmCoreConstants.OFFER_TYPE_COMPLEX);
		}
		else if (PricingConditionType.LIMITED_DEAL_TYPE.equals(dealType))
		{
			offerInfo.setOfferType(SabmCoreConstants.OFFER_TYPE_LIMITED);
		}
		else
		{
			offerInfo.setOfferType(SabmCoreConstants.OFFER_TYPE_DISCOUNT);
		}

		return offerInfo;
	}

	public AbstractOrderEntryModel getCartEntry(final AbstractOrderModel cartModel, final String productSelected,
			final UnitModel uom) throws SalesOrderSimulateCartUpdateException
	{

		final List<AbstractOrderEntryModel> entries = cartModel.getEntries();

		if (CollectionUtils.isNotEmpty(entries))
		{
			for (final AbstractOrderEntryModel entry : entries)
			{
				if (StringUtils.equalsIgnoreCase(productSelected, entry.getProduct().getCode())
						&& (uom == null || StringUtils.equals(entry.getUnit().getCode(), uom.getCode())))
				{
					return entry;
				}
			}
		}

		throw new SalesOrderSimulateCartUpdateException("SAP sales order response incorrect - not received cart entry sent");

	}


	public void updateFreeGoodInfo(final List<AbstractOrderEntryModel> entriesToSave,
			final Collection<SalesOrderResItem> freeGoodsList, final AbstractOrderModel cartModel)
			throws SalesOrderSimulateCartUpdateException
	{

		if (CollectionUtils.isNotEmpty(freeGoodsList))
		{
			for (final SalesOrderResItem freeGoodReItem : freeGoodsList)
			{

				final AbstractOrderEntryModel freeGoodEntry = createFreeGoods(freeGoodReItem);
				freeGoodEntry.setOrder(cartModel);
				updateEntryPrices(freeGoodEntry, freeGoodReItem, cartModel.getCurrency());

				final String parentItemLineNo = StringUtils.trim(freeGoodReItem.getSalesItemRelFreeGoods());
				LOG.debug("SalesOrderResItem salesItemRelFreeGoods info:{}, the cartModel: {}", parentItemLineNo, cartModel);

				if (StringUtils.isNotEmpty(parentItemLineNo))
				{
					final AbstractOrderEntryModel entry = getCartEntryMatchingSapLineNo(parentItemLineNo, entriesToSave);
					entry.setFreeGoodEntryNumber(String.valueOf(freeGoodEntry.getEntryNumber()));

					final int parentIndex = entriesToSave.indexOf(entry);
					entriesToSave.add(parentIndex + 1, freeGoodEntry);

					final UnitModel freeGoodUnit = getUnit(freeGoodReItem.getUnitOfMeasure());
					final ProductModel freeProduct = getProduct(freeGoodReItem.getMaterialNumber());
					final List<EntryOfferInfoModel> offerList = new ArrayList<EntryOfferInfoModel>(
							CollectionUtils.emptyIfNull(entry.getOfferInfo()));

					LOG.debug("SalesOrderFreeGoods: [{}] for cart: [{}]", freeGoodReItem.getSalesOrderFreeGoods());

					for (final SalesOrderFreeGoods condition : freeGoodReItem.getSalesOrderFreeGoods())
					{
						final Long freeGoodQty = getQuantity(condition.getFreeGoodsQty());
						final Long parentItemQty = getQuantity(condition.getMinimumQty());

						final EntryOfferInfoModel offerInfo = modelService.create(EntryOfferInfoModel.class);
						offerInfo.setOfferType(SabmCoreConstants.OFFER_TYPE_FREEGOOD);
						offerInfo.setFreeGoodProduct(freeProduct);
						offerInfo.setScaleQuantity(parentItemQty);
						offerInfo.setFreeGoodQuantity(freeGoodQty);
						offerInfo.setScaleUnit(freeGoodUnit);
						getModelService().save(offerInfo);
						offerList.add(offerInfo);

					}
					if (CollectionUtils.isNotEmpty(offerList))
					{
						entry.setOfferInfo(offerList);
					}
				}
				else
				{
					entriesToSave.add(freeGoodEntry);
				}
			}

			// if there have free goods , will call the function to reRank the sort of entries
			if (CollectionUtils.isNotEmpty(freeGoodsList))
			{
				reRankEntries(entriesToSave);
			}
		}

	}

	/**
	 * reRank the entries
	 *
	 * @param entriesToSave
	 */
	private void reRankEntries(final List<AbstractOrderEntryModel> entriesToSave)
	{
		final List<Integer> usableNumList = getUseableEntryNumber(entriesToSave);

		if (CollectionUtils.isNotEmpty(usableNumList) && CollectionUtils.isNotEmpty(entriesToSave))
		{
			for (int i = 0; i < entriesToSave.size(); i++)
			{
				if (usableNumList.size() > i && entriesToSave.size() > i)
				{
					final AbstractOrderEntryModel orderEntry = entriesToSave.get(i);
					LOG.info("Cart Error reRankEntries entryNumber : {} ", usableNumList.get(i));
					orderEntry.setEntryNumber(usableNumList.get(i));
					if (StringUtils.isNotEmpty(orderEntry.getFreeGoodEntryNumber()) && usableNumList.size() > (i + 1))
					{
						LOG.info("Cart Error reRankEntries freeGoodEntryNumber : {} ", usableNumList.get(i + 1).toString());
						orderEntry.setFreeGoodEntryNumber(usableNumList.get(i + 1).toString());
					}
				}
			}
		}
	}

	/**
	 * get the usable entry number
	 *
	 * @param entriesToSave
	 * @return usable entry number
	 */
	private List<Integer> getUseableEntryNumber(final List<AbstractOrderEntryModel> entriesToSave)
	{
		final List<Integer> existNumList = new ArrayList<>();
		final List<Integer> usableNumList = new ArrayList<>();

		for (final AbstractOrderEntryModel orderEntry : CollectionUtils.emptyIfNull(entriesToSave))
		{
			existNumList.add(orderEntry.getEntryNumber());
		}

		int i = 0;

		while (usableNumList.size() < CollectionUtils.emptyIfNull(entriesToSave).size())
		{
			if (!existNumList.contains(i))
			{
				usableNumList.add(i);
			}
			i++;
		}

		return usableNumList;
	}

	/**
	 * @param cartModel
	 * @param error
	 * @param status
	 */
	public AbstractOrderEntryModel updateEntryStatus(final AbstractOrderModel cartModel, final Error error,
			final StockLevelStatus status)
	{
		// <ErrorText>Material matVariant08,
		final String messageText = error.getErrorText();

		final String productCode = StringUtils.substringBetween(messageText, "Material ", ",");

		try
		{
			final AbstractOrderEntryModel entry = getCartEntry(cartModel, productCode, null);
			entry.setAvailabilityInfo(status.getCode());
			//add by SAB-2604 show a unique error code for every error
			final String code = getErrorEventFacade().createErrorEntry(
					new SalesOrderSimulateCartUpdateException("SAP order simulation response error"), "SAP order simulate", null,
					ErrorEventType.SAP, messageText + status.getCode());

			entry.setInfo(code);
			return entry;
		}
		catch (final SalesOrderSimulateCartUpdateException e)
		{
			LOG.error("Not able to find material number in error text to show product unavailable", e);
		}

		return null;
	}

	/**
	 * Verify delivery date SAP returned is the same hybris requested.
	 *
	 * @param sapConfirmedDeliveryDate
	 *           sap date
	 * @param requestedDeliveryDate
	 *           requested date
	 * @throws SalesOrderSimulateCartUpdateException
	 *            error parsing SAP date
	 */
	public boolean checkRequestedDeliveryDate(final String sapConfirmedDeliveryDate, final Date requestedDeliveryDate)
			throws SalesOrderSimulateCartUpdateException
	{
		Date sapReturnedRedDeliveryDate;
		try
		{
			sapReturnedRedDeliveryDate = SabmDateUtils.getDate(sapConfirmedDeliveryDate, SabmCoreConstants.DELIVERY_DATE_PATTERN);
		}
		catch (final ParseException e)
		{
			LOG.error("Sap date format parsing error");
			throw new SalesOrderSimulateCartUpdateException("Sap date format parsing error");
		}
		return requestedDeliveryDate != null && sapReturnedRedDeliveryDate != null
				&& DateUtils.isSameDay(requestedDeliveryDate, sapReturnedRedDeliveryDate);
	}


	public Map<String, ArrayList<Object>> processSapErrors(final SalesOrderSimulateResponse response,
			final AbstractOrderModel cartModel, final List<AbstractOrderEntryModel> entriesToSave)
	{
		final StringBuilder sapErrors = new StringBuilder();

		for (final Error error : response.getError())
		{

			sapErrors.append(error.getErrorCode()).append(":").append(error.getErrorText()).append(", ");
		}

		final String code = getErrorEventFacade().createErrorEntry(
				new SalesOrderSimulateCartUpdateException("SAP order simulation response error"), "SAP order simulate", null,
				ErrorEventType.SAP, sapErrors.toString());

		final Map<String, ArrayList<Object>> errorMap = new HashMap<>();
		for (final Error error : response.getError())
		{

			if (ArrayUtils.contains(Config.getString("sap.ordersimulate.response.customer.error.codes", "").split(","),
					error.getErrorCode()))
			{
				updateErrorMap(errorMap, "basket.page.salesordersimulate.error.message", code);

			}
			else if (ArrayUtils.contains(
					Config.getString("sap.ordersimulate.response.product.no.longer.available.codes", "").split(","),
					error.getErrorCode()))

			{
				updateErrorMap(errorMap, "ordersimulate.product.global.error", code);

				final AbstractOrderEntryModel entry = updateEntryStatus(cartModel, error, StockLevelStatus.BLOCKED);
				if (entry != null)
				{
					entriesToSave.add(entry);
				}
			}

			else if (ArrayUtils.contains(
					Config.getString("sap.ordersimulate.response.product.temporarily.unavailable.codes", "").split(","),
					error.getErrorCode()))

			{
				updateErrorMap(errorMap, "ordersimulate.product.global.error", code);

				final AbstractOrderEntryModel entry = updateEntryStatus(cartModel, error, StockLevelStatus.UNAVAILABLE);
				if (entry != null)
				{
					entriesToSave.add(entry);
				}
			}

			else
			{
				updateErrorMap(errorMap, "basket.page.salesordersimulate.error.message", code);

			}

		}

		LOG.error("Error from sap in simulate response: [{}]", sapErrors);
		return errorMap;
	}

	protected void updateErrorMap(final Map<String, ArrayList<Object>> errorMap, final String key, final String errorCode)
	{
		if (CollectionUtils.isNotEmpty(errorMap.get(key)))
		{
			errorMap.get(key).add(errorCode);
		}
		else
		{
			errorMap.put(key, Lists.<Object> newArrayList(errorCode));
		}
	}

	protected ProductModel getProduct(final String productCode) throws SalesOrderSimulateCartUpdateException
	{
		try
		{
			return productService.getProductForCode(StringUtils.trim(productCode));
		}
		catch (UnknownIdentifierException | AmbiguousIdentifierException e)
		{
			LOG.error("Not able to find free product in hybris", e);
			throw new SalesOrderSimulateCartUpdateException("Free good product from SAP not present in hybris");
		}
	}

	protected AbstractOrderEntryModel getCartEntryMatchingSapLineNo(final String sapItemLineNo,
			final Collection<AbstractOrderEntryModel> entriesToSave) throws SalesOrderSimulateCartUpdateException
	{
		for (final AbstractOrderEntryModel entry : entriesToSave)
		{
			if (StringUtils.equalsIgnoreCase(sapItemLineNo, entry.getSapLineNumber()))
			{
				return entry;
			}
		}
		throw new SalesOrderSimulateCartUpdateException("Free good parent product from SAP not present in hybris");
	}


	/**
	 * @param quantityString
	 *
	 * @return Long Quantity
	 */
	protected Long getQuantity(final String quantityString)
	{
		final String confirmedQuantityStr = StringUtils.substringBefore(StringUtils.trim(quantityString), ".");

		return StringUtils.isNotBlank(confirmedQuantityStr) ? Long.parseLong(confirmedQuantityStr) : 0;
	}

	/**
	 * @param unitOfMeasure
	 *           code
	 * @return unit model
	 * @throws SalesOrderSimulateCartUpdateException
	 */
	public UnitModel getUnit(final String unitOfMeasure) throws SalesOrderSimulateCartUpdateException
	{
		final String unitCode = StringUtils.trim(unitOfMeasure);
		if (StringUtils.isBlank(unitCode))
		{
			LOG.error("Blank unit from SAP");
			throw new SalesOrderSimulateCartUpdateException("Blank unit from SAP");
		}
		try
		{
			return unitService.getUnitForCode(unitCode);
		}
		catch (UnknownIdentifierException | AmbiguousIdentifierException e)
		{
			LOG.error("Not able to find unit in hybris", e);
			throw new SalesOrderSimulateCartUpdateException("Not able to find unit in hybris");
		}
	}


	/*
	 * Sap return value will have minus sign at end for discount values. This method will remove minus sign at the end
	 * and add it at start
	 */

	protected double getDoubleValue(final String input)
	{
		String inputString = StringUtils.trim(input);
		if (StringUtils.endsWith(inputString, "-"))
		{
			inputString = "-" + StringUtils.removeEnd(inputString, "-");
		}
		return NumberUtils.toDouble(inputString);
	}


	public ProductService getProductService()
	{
		return productService;
	}

	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	public void setUnitService(final UnitService unitService)
	{
		this.unitService = unitService;
	}

	public UnitService getUnitService()
	{
		return unitService;
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the commonI18NService
	 */
	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	/**
	 * @return the errorEventFacade
	 */
	public ErrorEventFacade getErrorEventFacade()
	{
		return errorEventFacade;
	}

	/**
	 * @param errorEventFacade
	 *           the errorEventFacade to set
	 */
	public void setErrorEventFacade(final ErrorEventFacade errorEventFacade)
	{
		this.errorEventFacade = errorEventFacade;
	}

}
