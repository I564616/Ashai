/**
 *
 */
package com.sabmiller.core.cart.service.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.impl.DefaultCalculationService;
import de.hybris.platform.order.strategies.calculation.OrderRequiresCalculationStrategy;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.TaxValue;

import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.annotation.Resource;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.model.ApbProductModel;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.integration.data.ApbPriceData;
import com.apb.integration.data.ApbProductPriceInfo;
import com.apb.integration.data.AsahiProductInfo;
import com.apb.integration.price.dto.ApbPriceRequestData;
import com.apb.integration.price.service.AsahiPriceIntegrationService;
import com.apb.product.strategy.AsahiInclusionExclusionProductStrategy;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AtomicDouble;
import com.sabmiller.core.cart.errors.exceptions.CartThresholdExceededException;
import com.sabmiller.core.cart.errors.exceptions.SalesOrderSimulateCartUpdateException;
import com.sabmiller.core.cart.service.SABMCalculationService;
import com.sabmiller.core.cart.service.helper.SalesOrderSimulateCartSyncHelper;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.enums.DealConditionStatus;
import com.sabmiller.core.enums.DealTypeEnum;
import com.sabmiller.core.enums.OrderSimulationStatus;
import com.sabmiller.core.enums.TaxType;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.CartDealConditionModel;
import com.sabmiller.core.model.OrderMessageModel;
import com.sabmiller.core.util.SabmDateUtils;
import com.sabmiller.integration.restclient.commons.SABMIntegrationException;
import com.sabmiller.integration.sap.constants.SabmintegrationConstants;
import com.sabmiller.integration.sap.ordersimulate.SalesOrderSimulateRequestHandler;
import com.sabmiller.integration.sap.ordersimulate.request.SalesOrderSimulateRequest;
import com.sabmiller.integration.sap.ordersimulate.response.SalesOrderSimulateResponse;
import com.sabmiller.integration.sap.ordersimulate.response.SalesOrderSimulateResponse.SalesOrderResItem;
import com.sabmiller.integration.sap.ordersimulate.response.SalesOrderSimulateResponse.SalesOrderResItem.DealCondition;
import com.sabmiller.integration.sap.ordersimulate.response.SalesOrderSimulateResponse.SalesOrderResItem.SalesOrderItemCondition;
import com.sabmiller.integration.sap.ordersimulate.response.SalesOrderSimulateResponse.SalesOrderResItem.SalesOrderItemCondition.SalesOrderItemScales;

/**
 * No calculations in hybris since everything will be coming from SAP. Hybris just need to sum up all the individual
 * entry totals.
 */
/**
 * @author GQ485VQ
 *
 */
@SuppressWarnings("SE_BAD_FIELD")
public class SABMCalculationServiceImpl extends DefaultCalculationService implements SABMCalculationService
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SABMCalculationServiceImpl.class.getName());

	public static final String CASES_COUNT_FOR_DELIVERY_FEE = "delivery.surcharge.MOQ.apb";
	public static final String DELIVERY_FEE = "delivery.surcharge.apb";
	public static final String PACK_TYPE = "delivery.surcharge.waived.off.package.type.apb";
	public static final String NON_ALCOHOLIC_TYPE = "product.code.non.alcoholic.product.apb";
	public static final String PRODUCT_CODE_FOR_DELIVERY_SURCHARGE = "product.code.for.delivery.surcharge.apb";
	private static final String ORDER_SIMULATE_STUB_AVAILABLE_CHECK = "cub.ordersimulate.stub.available.check";
	public static final String ORDER_SIMULATE_STUB_RESPONSE = "stub/simulateResponse.xml";
	public static final String ORDER_SIMULATE_MEDIA_STUB_RESPONSE = "cub_stub_order_simulate";

	/** The common i18 n service. */
	private CommonI18NService commonI18NService;

	/** The sales order simulate rest handler. */
	private SalesOrderSimulateRequestHandler salesOrderSimulateRestHandler;

	/** The cart sync helper. */
	private SalesOrderSimulateCartSyncHelper cartSyncHelper;

	/** The sales order simulate request converter. */
	private Converter<AbstractOrderModel, SalesOrderSimulateRequest> salesOrderSimulateRequestConverter;

	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;

	@Resource
	private OrderRequiresCalculationStrategy orderRequiresCalculationStrategy;

	@Resource(name = "inclusionExclusionProductStrategy")
	private AsahiInclusionExclusionProductStrategy inclusionExclusionProductStrategy;

	@Resource(name = "asahiPriceIntegrationService")
	private AsahiPriceIntegrationService asahiPriceIntegrationService;

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Resource
	private UserService userService;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Resource
	private SessionService sessionService;

	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	@Resource
	private MediaService mediaService;

	@Resource
	private CMSSiteService cmsSiteService;

	/**
	 * Update cart with sap changes.
	 *
	 * @param response
	 *           the response
	 * @param cartModel
	 *           the cart model
	 * @throws SalesOrderSimulateCartUpdateException
	 *            the sales order simulate cart update exception
	 */
	protected void updateCartWithSapChanges(final SalesOrderSimulateResponse response, final AbstractOrderModel cartModel)
			throws SalesOrderSimulateCartUpdateException
	{
		//keep track of entries return from SAP, may not match to what we have... some clean up may be necessary
		final LinkedList<AbstractOrderEntryModel> entriesToSave = new LinkedList<>();
		final List<OrderMessageModel> cartMessages = Lists.newArrayList();

		// return if response is empty
		if (response == null || response.getSalesOrderResHeder() == null)
		{
			LOG.error("Blank response from sap");
			cartMessages.add(createOrderMessage("basket.page.salesordersimulate.error.message", null));

			cartModel.setSimulationMessages(cartMessages);
			getModelService().saveAll(cartMessages);
			getModelService().save(cartModel);
			return;
		}

		//Collect errors from SAP and update stock level of cart entries
		if (CollectionUtils.isNotEmpty(response.getError()))
		{
			final Map<String, ArrayList<Object>> errorMap = cartSyncHelper.processSapErrors(response, cartModel, entriesToSave);
			for (final Map.Entry<String, ArrayList<Object>> entry : errorMap.entrySet())
			{
				cartMessages.add(createOrderMessage(entry.getKey(), entry.getValue()));
			}
		}

		//check delivery date hasn't changed
		checkRDD(response, cartModel, cartMessages);

		boolean stockStatusMsgAdded = false;
		final Collection<SalesOrderSimulateResponse.SalesOrderResItem> freeGoodsList = new ArrayList<>();

		for (final SalesOrderSimulateResponse.SalesOrderResItem item : response.getSalesOrderResItem())
		{
			if (StringUtils.equalsIgnoreCase(SabmintegrationConstants.SAP_FREE_GOOD_FLAG, item.getFreeGoodsFlag()))
			{
				freeGoodsList.add(item);
			}
			else
			{
				// ignoring the substitution flag: SAB-1908 SAP_MATERIAL_SUBSTITUTION_FLAG

				final String materialNumberEntered = StringUtils.trim(item.getMaterialEntered());
				final UnitModel uom = cartSyncHelper.getUnit(item.getUnitOfMeasure());
				final AbstractOrderEntryModel entryModel = cartSyncHelper.getCartEntry(cartModel, materialNumberEntered, uom);

				entryModel.setSapLineNumber(item.getLineNumber());
				cartSyncHelper.updateEntryPrices(entryModel, item, cartModel.getCurrency());

				final boolean stockStatus = cartSyncHelper.updateEntryProductAvailabilty(entryModel,
						cartModel.getRequestedDeliveryDate(), item.getSalesOrderItemScheduling());

				if (!stockStatus && !stockStatusMsgAdded)
				{
					cartMessages.add(createOrderMessage("basket.page.error.message.lowstock", null));
					stockStatusMsgAdded = true;
				}
				getModelService().save(entryModel);
				entriesToSave.add(entryModel);
			}
		}
		setRejectedComplexDeal(cartModel, response);
		cartSyncHelper.updateFreeGoodInfo(entriesToSave, freeGoodsList, cartModel);
		saveCartEntries(cartModel, entriesToSave);
		cartModel.setSimulationMessages(cartMessages);
		cartModel.setOneOffDealApplied(hasYDXOConditionType(response));
		getModelService().save(cartModel);
	}



	/**
	 * Set rejected complex deal from response of SAP.
	 *
	 * @param cartModel
	 *           the cart model
	 * @param response
	 *           the response
	 */
	private void setRejectedComplexDeal(final AbstractOrderModel cartModel, final SalesOrderSimulateResponse response)
	{
		// get all applied deals in response
		final List<SalesOrderResItem> salesOrderResItems = response.getSalesOrderResItem();
		final Set<String> dealCodeSet = new HashSet<>();
		if (salesOrderResItems != null)
		{
			for (final SalesOrderResItem salesOrderResItem : salesOrderResItems)
			{
				final List<DealCondition> dealConditions = salesOrderResItem.getDealCondition();
				if (dealConditions != null)
				{
					for (final DealCondition dealCondition : dealConditions)
					{
						dealCodeSet.add(dealCondition.getDealConditionNumber());
					}
				}

			}
		}

		//check the deal with cart. if the cart deal is not contained in response deals, set the status as rejected
		final List<CartDealConditionModel> cartDealConditions = cartModel.getComplexDealConditions();
		for (final CartDealConditionModel cartDealConditionModel : cartDealConditions)
		{
			if (cartDealConditionModel.getDeal() != null && !dealCodeSet.contains(cartDealConditionModel.getDeal().getCode())
					&& DealTypeEnum.COMPLEX.equals(cartDealConditionModel.getDeal().getDealType()))
			{
				cartDealConditionModel.setStatus(DealConditionStatus.REJECTED);
			}
		}

		getModelService().saveAll(cartDealConditions);
	}



	/**
	 * Save updated, new entries and remove ones not returned by SAP. Any entry from the cart not found in entriesToSave
	 * will be deleted.
	 *
	 * @param cartModel
	 *           the cart
	 * @param entriesToSave
	 *           new and updated entries
	 */
	protected void saveCartEntries(final AbstractOrderModel cartModel, final LinkedList<AbstractOrderEntryModel> entriesToSave)
	{

		if (CollectionUtils.isNotEmpty(entriesToSave))
		{
			final Collection toRemove = CollectionUtils.subtract(cartModel.getEntries(), entriesToSave);
			if (CollectionUtils.isNotEmpty(toRemove))
			{
				getModelService().removeAll(toRemove);
			}
			getModelService().saveAll(entriesToSave);
			cartModel.setEntries(entriesToSave);
		}
		else
		{
			LOG.debug("SAP returned no changes, keeping existing entries");
		}
	}



	/**
	 * Verify delivery date SAP returned is the same hybris requested.
	 *
	 * @param response
	 *           SAP response
	 * @param cartModel
	 *           the cart
	 * @param cartMessages
	 *           cart messages
	 * @throws SalesOrderSimulateCartUpdateException
	 *            dates don't match
	 */
	protected void checkRDD(final SalesOrderSimulateResponse response, final AbstractOrderModel cartModel,
			final List<OrderMessageModel> cartMessages) throws SalesOrderSimulateCartUpdateException
	{
		try
		{
			final String sapReturnedReqDeliveryDate = response.getSalesOrderResHeder().getRequestedDeliveryDate();
			if (StringUtils.isNotBlank(sapReturnedReqDeliveryDate)
					&& !cartSyncHelper.checkRequestedDeliveryDate(sapReturnedReqDeliveryDate, cartModel.getRequestedDeliveryDate()))
			{
				cartMessages.add(createOrderMessage("basket.page.salesordersimulate.error.changeDeliveryDate",
						Lists.<Object> newArrayList(SabmDateUtils.getSiteFormattedDateFromSapDate(sapReturnedReqDeliveryDate))));
			}
		}
		catch (final ParseException e)
		{
			LOG.error("Sap date format parsing error");
			throw new SalesOrderSimulateCartUpdateException("Sap date format parsing error");
		}
	}

	/**
	 * Create a new and unsaved order message.
	 *
	 * @param messageCode
	 *           message code
	 * @param attributes
	 *           message attributes
	 * @return the message
	 */
	protected OrderMessageModel createOrderMessage(final String messageCode, final Collection<Object> attributes)
	{

		final OrderMessageModel message = getModelService().create(OrderMessageModel.class);
		message.setCode(messageCode);
		if (attributes != null)
		{
			message.setArugments(attributes);
		}

		return message;
	}


	/**
	 * Set and calculate totals all based on SAP values.
	 *
	 * @param order
	 *           the cart
	 */
	protected void calculateTotals(final AbstractOrderModel order)
	{
		final CurrencyModel curr = order.getCurrency();
		final int digits = curr.getDigits();

		double netAmount = 0;
		double totalDiscount = 0;
		double totalLoyaltyFee = 0;
		double totalAutoPayAdvantageDiscount = 0;
		double totalAutoPayAdvantagePlusDiscount = 0;
		double totalGST = 0;
		double totalWET = 0;
		double deliveryCost = 0;
		double freightLimit = 0;
		double actualDeliveryCost = 0;
		double deposit = 0;
		final Collection<TaxValue> taxValues = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(order.getEntries()))
		{
			for (final AbstractOrderEntryModel entry : order.getEntries())
			{
				netAmount += entry.getTotalPrice() != null ? entry.getTotalPrice() : 0;
				totalLoyaltyFee += entry.getLoyaltyFee() != null ? entry.getLoyaltyFee() : 0;
				totalAutoPayAdvantageDiscount += entry.getAutoPayAdvantageDiscount() != null ? entry.getAutoPayAdvantageDiscount() : 0;
				totalAutoPayAdvantagePlusDiscount += entry.getAutoPayAdvantagePlusDiscount() != null ? entry.getAutoPayAdvantagePlusDiscount() : 0;


				totalDiscount += entry.getTotalEntryDiscount() != null ? entry.getTotalEntryDiscount() : 0;

				for (final TaxValue taxValue : entry.getTaxValues())
				{
					totalGST += SabmCoreConstants.GST.equalsIgnoreCase(taxValue.getCode()) ? taxValue.getValue() : 0;
					totalWET += SabmCoreConstants.WET.equalsIgnoreCase(taxValue.getCode()) ? taxValue.getValue() : 0;
				}
				deliveryCost += entry.getDeliveryCost() != null ? entry.getDeliveryCost() : 0;


				if (freightLimit == 0)
				{
					freightLimit = entry.getFreightLimit() != null ? entry.getFreightLimit() : 0;
				}

				deposit += entry.getDeposit() != null ? entry.getDeposit() : 0;

				entry.setCalculated(true);
				getModelService().save(entry);

			}
			deliveryCost = commonI18NService.roundCurrency(deliveryCost, digits);

			//SABMC-1823, the max of the freightLimit and the deliveryCost will be the real deliveryCost
			actualDeliveryCost = deliveryCost;
			freightLimit = commonI18NService.roundCurrency(freightLimit, digits);

			if (freightLimit > deliveryCost)
			{
				deliveryCost = freightLimit;
			}

			deposit = commonI18NService.roundCurrency(deposit, digits);
			totalDiscount = commonI18NService.roundCurrency(totalDiscount, digits);
			netAmount = commonI18NService.roundCurrency(netAmount, digits);
			totalGST = commonI18NService.roundCurrency(totalGST, digits);
			totalWET = commonI18NService.roundCurrency(totalWET, digits);
			totalLoyaltyFee = commonI18NService.roundCurrency(totalLoyaltyFee, digits);
			totalAutoPayAdvantageDiscount = commonI18NService.roundCurrency(totalAutoPayAdvantageDiscount, digits);
			totalAutoPayAdvantagePlusDiscount = commonI18NService.roundCurrency(totalAutoPayAdvantagePlusDiscount, digits);
			final TaxValue gstTaxValue = new TaxValue(SabmCoreConstants.GST, totalGST, true, order.getCurrency().getIsocode());
			final TaxValue wetTaxValue = new TaxValue(SabmCoreConstants.WET, totalWET, true, order.getCurrency().getIsocode());

			double subTotal = netAmount + deliveryCost + totalLoyaltyFee + totalAutoPayAdvantageDiscount + totalAutoPayAdvantagePlusDiscount;
			double totalPrice = subTotal + totalGST;
			subTotal = commonI18NService.roundCurrency(subTotal, digits);
			totalPrice = commonI18NService.roundCurrency(totalPrice, digits);
			taxValues.add(gstTaxValue);
			taxValues.add(wetTaxValue);

			order.setTotalTaxValues(taxValues);
			order.setTotalDiscounts(totalDiscount);
			order.setNetAmount(netAmount); // discount will be -ve from sap
			order.setDeliveryCost(deliveryCost);
			//SABMC-1823, set the sum of actual delivery cost and the sum of freight limit to the order
			order.setActualDeliveryCost(actualDeliveryCost);
			order.setFreightLimit(freightLimit);
			order.setDeposit(deposit);
			order.setSubtotal(subTotal);
			order.setTotalPrice(totalPrice);
			order.setLoyaltyFee(totalLoyaltyFee);
			order.setAutoPayAdvantageDiscount(totalAutoPayAdvantageDiscount);
			order.setAutoPayAdvantagePlusDiscount(totalAutoPayAdvantagePlusDiscount);
		}
	}

	/**
	 * Clean up all free items before a order simulate run.
	 *
	 * @param cartModel
	 *           the cart
	 */
	protected void removeFreeGoods(final AbstractOrderModel cartModel)
	{
		//FIXME do we really need to remove all?? we should try to reuse rather than always delete and re-create

		final List<AbstractOrderEntryModel> freeGood = new LinkedList<>();
		for (final AbstractOrderEntryModel cartEntry : cartModel.getEntries())
		{
			if (BooleanUtils.isTrue(cartEntry.getIsFreeGood()))
			{
				freeGood.add(cartEntry);
			}
		}
		if (CollectionUtils.isNotEmpty(freeGood))
		{
			getModelService().removeAll(freeGood);
		}
	}


	/**
	 * Calculate.
	 *
	 * @param order
	 *           the order
	 * @param forceRun
	 *           the force run
	 * @throws SalesOrderSimulateCartUpdateException
	 *            the sales order simulate cart update exception
	 * @throws CartThresholdExceededException
	 *            the cart threshold exceeded exception
	 */
	protected void calculate(final AbstractOrderModel order, final boolean forceRun)
			throws SalesOrderSimulateCartUpdateException, CartThresholdExceededException
	{
		if (forceRun || requiresCalculation(order))
		{
			if (Config.getBoolean("salesordersimulate.service.call.enabled", true))
			{
				runOrderSimulate(order);
			}

			//			if(configurationService.getConfiguration().getBoolean("cub.order.dummy.price.flag",true)) {
			//				setOfflinePrices(order);
			//			}

			calculateTotals(order);
			setCalculatedStatus(order);
		}
		else
		{
			calculateTotals(order);
			setCalculatedStatus(order);
		}
		if (order.getUser() instanceof B2BCustomerModel)
		{
			final Double cartTotal = order.getTotalPrice();
			final Integer orderLimit = ((B2BCustomerModel) order.getUser()).getOrderLimit();
			if (cartTotal != null && orderLimit != null && cartTotal > orderLimit)
			{
				final List<OrderMessageModel> newList = Lists.newArrayList();
				newList.addAll(order.getSimulationMessages());
				newList.add(createOrderMessage("basket.page.orderthreshold.error.message", null));
				order.setSimulationMessages(newList);
				getModelService().save(order);

			}
		}
	}

	/**
	 * Run order simulate.
	 *
	 * @param order
	 *           the order
	 * @throws SalesOrderSimulateCartUpdateException
	 *            the sales order simulate cart update exception
	 */
	public void runOrderSimulate(final AbstractOrderModel order) throws SalesOrderSimulateCartUpdateException
	{

		if (order instanceof CartModel)
		{
			removeFreeGoods(order);

			order.setCalculated(false);
			order.setOrderSimulationStatus(OrderSimulationStatus.CALCULATION_IN_PROGRESS);
			if (CollectionUtils.isNotEmpty(order.getSimulationMessages()))
			{
				getModelService().removeAll(order.getSimulationMessages());
				order.setSimulationMessages(Collections.emptyList());
			}
			getModelService().save(order);
			try
			{
				//run order simulate
				LOG.info("##############################################");
				LOG.info("# Running Order Simulate: [{}]", order.getCode());
				LOG.info("##############################################");

				SalesOrderSimulateResponse response = null;

				if (configurationService.getConfiguration().getBoolean(ORDER_SIMULATE_STUB_AVAILABLE_CHECK, false))
				{
					final MediaModel stubMedia = mediaService.getMedia(ORDER_SIMULATE_MEDIA_STUB_RESPONSE);
					final InputStream targetStream = mediaService.getStreamFromMedia(stubMedia);
					final JAXBContext jaxbContext = JAXBContext.newInstance(SalesOrderSimulateResponse.class);
					final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
					final XMLInputFactory factory = XMLInputFactory.newInstance();
					final XMLEventReader fileSource = factory.createXMLEventReader(targetStream);
					final JAXBElement<SalesOrderSimulateResponse> userElement = unmarshaller.unmarshal(fileSource,
							SalesOrderSimulateResponse.class);
					response = userElement.getValue();
				}
				else
				{
					response = salesOrderSimulateRestHandler.sendPostRequest(salesOrderSimulateRequestConverter.convert(order));
				}

				//update cart with response - list changes will also be kept in the cart
				updateCartWithSapChanges(response, order);
			}
			catch (final SABMIntegrationException | JAXBException | XMLStreamException e)
			{
				LOG.error("Order simulate failed for cart [{}]", order.getCode());
				throw new SalesOrderSimulateCartUpdateException("Error while calling sales order simulate", e);
			}
		}
		else
		{
			LOG.warn("Order simulate only runs for cartmodel");
		}
	}

	/**
	 * Checks for ydxo condition type.
	 *
	 * @param response
	 *           the response
	 * @return true, if successful
	 */
	private boolean hasYDXOConditionType(final SalesOrderSimulateResponse response)
	{
		for (final SalesOrderResItem item : CollectionUtils.emptyIfNull(response.getSalesOrderResItem()))
		{
			for (final SalesOrderItemCondition eachCondition : CollectionUtils.emptyIfNull(item.getSalesOrderItemCondition()))
			{
				if (SabmCoreConstants.ONCE_OFF_DEALS_CONDITION_TYPE.equals(eachCondition.getConditionType()))
				{
					return true;
				}
				for (final SalesOrderItemScales eachScale : CollectionUtils.emptyIfNull(eachCondition.getSalesOrderItemScales()))
				{
					if (SabmCoreConstants.ONCE_OFF_DEALS_CONDITION_TYPE.equals(eachScale.getConditionType()))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Sets the calculated status.
	 *
	 * @param order
	 *           the new calculated status
	 */
	@Override
	protected void setCalculatedStatus(final AbstractOrderModel order)
	{
		if(SabmCoreConstants.CUB_STORE.equalsIgnoreCase(getCMSSiteForOrder(order).getUid()))
		{
		if (OrderSimulationStatus.UPDATE_DURING_CALCULATION != order.getOrderSimulationStatus())
		{
			order.setOrderSimulationStatus(OrderSimulationStatus.CALCULATED);
			order.setSalesOrderSimulateSyncDate(new Date());

			final List<AbstractOrderEntryModel> entries = order.getEntries();
			if (entries != null)
			{
				for (final AbstractOrderEntryModel entry : entries)
				{
					entry.setCalculated(Boolean.TRUE);
				}
				getModelService().saveAll(entries);
			}

			//necessary to save all changes before saving the calculated status
			getModelService().save(order);
			getModelService().refresh(order);
			order.setCalculated(true);
			getModelService().save(order);

		}
		else
		{
			//necessary to save all changes before saving the calculated status
			getModelService().save(order);
			getModelService().refresh(order);
			order.setCalculated(false);
			getModelService().save(order);
		}
		}
		else
		{
			super.setCalculatedStatus(order);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.order.CalculationService#requiresCalculation(de.hybris.platform.core.model.order.
	 * AbstractOrderModel)
	 */
	@Override
	public boolean requiresCalculation(final AbstractOrderModel order)
	{

		if(SabmCoreConstants.CUB_STORE.equalsIgnoreCase(getCMSSiteForOrder(order).getUid()))
		{

		ServicesUtil.validateParameterNotNullStandardMessage("order", order);

		if (!OrderSimulationStatus.CALCULATED.equals(order.getOrderSimulationStatus())
				|| CollectionUtils.isNotEmpty(order.getSimulationMessages()))
		{
			return true;
		}
		else if (order.getSalesOrderSimulateSyncDate() != null)
		{
			final Calendar cal = Calendar.getInstance();
			final Date now = cal.getTime();
			cal.setTime(order.getSalesOrderSimulateSyncDate());
			cal.add(Calendar.MINUTE, Config.getInt("sales.order.simulate.expiry.mins", 0));

			if (now.after(cal.getTime()))
			{
				return true;
			}
		}
		return false;
		}
		else
		{
			return super.requiresCalculation(order);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.order.CalculationService#calculate(de.hybris.platform.core.model.order.AbstractOrderModel)
	 */
	@Override
	public void calculate(final AbstractOrderModel order) throws CalculationException
	{
		if(!SabmCoreConstants.CUB_STORE.equalsIgnoreCase(getCMSSiteForOrder(order).getUid()))
		{
			if (orderRequiresCalculationStrategy.requiresCalculation(order))
			{
				if (ApbCoreConstants.SGA_SITE_ID.equalsIgnoreCase(getCMSSiteForOrder(order).getUid()))
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

		else
		{
   		try
   		{
   			calculate(order, false);
   		}
   		catch (SalesOrderSimulateCartUpdateException | CartThresholdExceededException e)
   		{
   			throw new IllegalStateException("Error running order simulate", e);
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

	private void calculateOrderTotal(final AbstractOrderModel order) throws CalculationException
	{
		double subtotal = 0.0;
		for (final AbstractOrderEntryModel e : order.getEntries())
		{
			calculateTotals(e, true);
			if (ApbCoreConstants.APB_SITE_ID.equalsIgnoreCase(getCMSSiteForOrder(order).getUid())
					|| (ApbCoreConstants.SGA_SITE_ID.equalsIgnoreCase(getCMSSiteForOrder(order).getUid()) && !inclusionExclusionProductStrategy.isProductIncluded(e.getProduct().getCode())))
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
		if(SabmCoreConstants.CUB_STORE.equalsIgnoreCase(getCMSSiteForOrder(order).getUid()))
		{
			return super.resetAllValues(order);
		}

		final Map<TaxValue, Map<Set<TaxValue>, Double>> taxValueMap = calculateSubtotal(order, false);
		return taxValueMap;

	}


	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.order.CalculationService#calculate(de.hybris.platform.core.model.order.AbstractOrderModel,
	 * java.util.Date)
	 */
	@Override
	public void calculate(final AbstractOrderModel order, final Date date) throws CalculationException
	{
		if(SabmCoreConstants.CUB_STORE.equalsIgnoreCase(getCMSSiteForOrder(order).getUid()))
		{
		try
		{
			calculate(order, false);
		}
		catch (SalesOrderSimulateCartUpdateException | CartThresholdExceededException e)
		{
			throw new IllegalStateException("Error running order simulate", e);
		}
		}
		else
		{
			super.calculate(order, date);
		}
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.order.CalculationService#recalculate(de.hybris.platform.core.model.order.AbstractOrderModel)
	 */
	@Override
	public void recalculate(final AbstractOrderModel order) throws CalculationException
	{
		if(SabmCoreConstants.CUB_STORE.equalsIgnoreCase(getCMSSiteForOrder(order).getUid()))
		{

		try
		{
			calculate(order, true);
		}
		catch (SalesOrderSimulateCartUpdateException | CartThresholdExceededException e)
		{
			throw new IllegalStateException("Error running order simulate", e);
		}
		}
		else
		{
			super.recalculate(order);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.order.CalculationService#recalculate(de.hybris.platform.core.model.order.AbstractOrderModel,
	 * java.util.Date)
	 */
	@Override
	public void recalculate(final AbstractOrderModel order, final Date date) throws CalculationException
	{
		if(SabmCoreConstants.CUB_STORE.equalsIgnoreCase(getCMSSiteForOrder(order).getUid()))
		{
		try
		{
			calculate(order, true);
		}
		catch (SalesOrderSimulateCartUpdateException | CartThresholdExceededException e)
		{
			throw new IllegalStateException("Error running order simulate", e);
		}
		}
		else
		{
			super.recalculate(order, date);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.order.CalculationService#calculateTotals(de.hybris.platform.core.model.order.
	 * AbstractOrderModel, boolean)
	 */
	@Override
	public void calculateTotals(final AbstractOrderModel order, final boolean recalculate) throws CalculationException
	{
		if(!SabmCoreConstants.CUB_STORE.equalsIgnoreCase(getCMSSiteForOrder(order).getUid()))
		{
			super.calculateTotals(order, recalculate);
		}
		else
		{
   		try
   		{
   			calculate(order, recalculate);
   		}
   		catch (SalesOrderSimulateCartUpdateException | CartThresholdExceededException e)
   		{
   			throw new IllegalStateException("Error running order simulate", e);
   		}
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

				if (ApbCoreConstants.APB_SITE_ID.equalsIgnoreCase(getCMSSiteForOrder(order).getUid()) && null != order.getOrderGST())
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

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.order.CalculationService#calculateTotals(de.hybris.platform.core.model.order.
	 * AbstractOrderEntryModel, boolean)
	 */
	@Override
	public void calculateTotals(final AbstractOrderEntryModel entry, final boolean recalculate)
	{

		if (getCMSSiteForOrder(entry.getOrder()) == null || !SabmCoreConstants.CUB_STORE.equalsIgnoreCase(getCMSSiteForOrder(entry.getOrder()).getUid()))
		{
			if (recalculate || orderRequiresCalculationStrategy.requiresCalculation(entry))
			{
				final AbstractOrderModel order = entry.getOrder();
				final CurrencyModel curr = order.getCurrency();
				final int digits = curr.getDigits().intValue();

				final double totalPriceWithoutDiscount;

				double minicartSubTotal = 0.0d;

				if (ApbCoreConstants.SGA_SITE_ID.equalsIgnoreCase(getCMSSiteForOrder(entry.getOrder()).getUid()))
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

		else
		{
			try
   		{
   			calculate(entry.getOrder(), recalculate);
   		}
   		catch (SalesOrderSimulateCartUpdateException | CartThresholdExceededException e)
   		{
   			LOG.error("Error running order simulate", e);
   		}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.order.CalculationService#recalculate(de.hybris.platform.core.model.order.
	 * AbstractOrderEntryModel)
	 */
	@Override
	public void recalculate(final AbstractOrderEntryModel entry) throws CalculationException
	{
		if(SabmCoreConstants.CUB_STORE.equalsIgnoreCase(getCMSSiteForOrder(entry.getOrder()).getUid()))
		{
		try
		{
			calculate(entry.getOrder(), false);
		}
		catch (SalesOrderSimulateCartUpdateException | CartThresholdExceededException e)
		{
			LOG.error("Error running order simulate", e);
		}
		}
		else
		{
			super.recalculate(entry);
		}
	}

	@Override
	protected void calculateTotals(final AbstractOrderModel order, final boolean recalculate,
			final Map<TaxValue, Map<Set<TaxValue>, Double>> taxValueMap) throws CalculationException
	{
		if(!SabmCoreConstants.CUB_STORE.equalsIgnoreCase(getCMSSiteForOrder(order).getUid()))
		{
   		if (recalculate || orderRequiresCalculationStrategy.requiresCalculation(order))
   		{
   			if (ApbCoreConstants.SGA_SITE_ID.equalsIgnoreCase(getCMSSiteForOrder(order).getUid()))
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
		else
		{
			super.calculateTotals(order, recalculate, taxValueMap);
		}
	}

	@Override
	protected Map<TaxValue, Map<Set<TaxValue>, Double>> calculateSubtotal(final AbstractOrderModel order,
			final boolean recalculate)
	{
		//if(!asahiSiteUtil.isCub())
		if(getCMSSiteForOrder(order)==null || !SabmCoreConstants.CUB_STORE.equalsIgnoreCase(getCMSSiteForOrder(order).getUid()))
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
   				if (ApbCoreConstants.APB_SITE_ID.equalsIgnoreCase(getCMSSiteForOrder(order).getUid())
   						|| (ApbCoreConstants.SGA_SITE_ID.equalsIgnoreCase(getCMSSiteForOrder(order).getUid()) && inclusionExclusionProductStrategy.isProductIncluded(entry.getProduct().getCode())))
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

   			if (ApbCoreConstants.SGA_SITE_ID.equalsIgnoreCase(getCMSSiteForOrder(order).getUid()))
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
		else
		{
			return super.calculateSubtotal(order, recalculate);
		}
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
		if (ApbCoreConstants.SGA_SITE_ID.equalsIgnoreCase(getCMSSiteForOrder(entry.getOrder()).getUid()))
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


	/**
	 * Sets the common i18 n service.
	 *
	 * @param commonI18NService
	 *           the new common i18 n service
	 */
	@Override
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
		super.setCommonI18NService(commonI18NService);
	}

	/**
	 * Sets the sales order simulate rest handler.
	 *
	 * @param salesOrderSimulateRestHandler
	 *           the new sales order simulate rest handler
	 */
	public void setSalesOrderSimulateRestHandler(final SalesOrderSimulateRequestHandler salesOrderSimulateRestHandler)
	{
		this.salesOrderSimulateRestHandler = salesOrderSimulateRestHandler;
	}

	/**
	 * Sets the cart sync helper.
	 *
	 * @param cartSyncHelper
	 *           the new cart sync helper
	 */
	public void setCartSyncHelper(final SalesOrderSimulateCartSyncHelper cartSyncHelper)
	{
		this.cartSyncHelper = cartSyncHelper;
	}

	/**
	 * Sets the sales order simulate request converter.
	 *
	 * @param salesOrderSimulateRequestConverter
	 *           the sales order simulate request converter
	 */
	public void setSalesOrderSimulateRequestConverter(
			final Converter<AbstractOrderModel, SalesOrderSimulateRequest> salesOrderSimulateRequestConverter)
	{
		this.salesOrderSimulateRequestConverter = salesOrderSimulateRequestConverter;
	}

	public void setOfflinePrices(final AbstractOrderModel order)
	{
		final List<AbstractOrderEntryModel> entries = order.getEntries();
		final LinkedList<AbstractOrderEntryModel> entriesToSave = new LinkedList<>();

		for(final AbstractOrderEntryModel entry:entries)
		{
			entry.setBasePrice(Double.parseDouble(configurationService.getConfiguration().getString("cub.order.dummy.cart.totalprice","")));
			entry.setTotalPrice(Double.parseDouble(configurationService.getConfiguration().getString("cub.order.dummy.cart.totalprice","")) * entry.getQuantity());

			getModelService().save(entry);
			entriesToSave.add(entry);
		}

		saveCartEntries(order, entriesToSave);
	}

	/**
	 * Returns cmssite based on order.
	 *
	 * @param AbstractOrderModel
	 *           order
	 */
	private BaseSiteModel getCMSSiteForOrder(final AbstractOrderModel order)
	{
		return cmsSiteService.getCurrentSite() == null ? order.getSite():cmsSiteService.getCurrentSite();
	}

	@Override
	public void calculateEntries(final AbstractOrderModel order, final boolean forceRecalculate) throws CalculationException
	{
		double subtotal = 0.0D;
		AbstractOrderEntryModel e;
		for (final Iterator var6 = order.getEntries().iterator(); var6.hasNext(); subtotal += e.getTotalPrice())
		{

   			e = (AbstractOrderEntryModel) var6.next();
   			if (BooleanUtils.isFalse(e.getIsFreeGood())) {
   				this.recalculateOrderEntryIfNeeded(e, forceRecalculate);
   			}
		}

		order.setTotalPrice(subtotal);

	}
}
