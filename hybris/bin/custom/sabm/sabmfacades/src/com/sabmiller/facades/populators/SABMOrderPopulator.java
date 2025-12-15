/**
 *
 */
package com.sabmiller.facades.populators;

import de.hybris.platform.commercefacades.order.converters.populator.OrderPopulator;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.promotions.result.PromotionOrderResults;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.store.services.BaseStoreService;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import com.apb.core.model.AsahiPaymentTransactionEntryModel;
import com.apb.core.model.AsahiPaymentTransactionModel;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.services.AsahiOrderService;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.card.payment.AsahiCardData;
import com.apb.facades.comparator.order.entry.AsahiOrderEntryDataComparator;
import com.sabmiller.facades.order.SABMOrderFacade;
import com.sabmiller.facades.util.SabmFeatureUtil;


public class SABMOrderPopulator extends OrderPopulator
{

	@Resource(name = "orderFacade")
	private SABMOrderFacade orderFacade;
	@Resource(name = "sabmFeatureUtil")
	private SabmFeatureUtil sabmFeatureUtil;

	@Resource(name = "asahiOrderService")
	private AsahiOrderService asahiOrderService;
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;
	private static final String ORDER_NUMBER_FOR_BLANK = "order.number.for.not.process.order";
	private static final String FREE_GOODS_ORDER_PROCESSING_CODE = "YSFO";

	@Resource
	private EnumerationService enumerationService;

	@Resource
	private I18NService i18nService;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;


	@Override
	public void populate(final OrderModel source, final OrderData target)
	{
		//Invoking orderPopulator to set the OOTB fields
		super.populate(source, target);

		if(asahiSiteUtil.isCub())
		{

   		if (source.getSalesApplication() != null)
   		{
   			target.setSalesApplication(source.getSalesApplication().toString());
   		}

   		final String cutoffTime = orderFacade.getCutoffTime(source.getUnit(), source.getRequestedDeliveryDate());
   		target.setCutoffTime(cutoffTime);
   		target.setStatusDisplay(sabmFeatureUtil.displayTrackOrderStatus(source.getStatusDisplay()));
   	    target.setUserDisplayName(source.getUserDisplayName());
		}
	}

	@Override
	protected void addTotals(final AbstractOrderModel source, final AbstractOrderData prototype)
	{
		if(!asahiSiteUtil.isCub())
		{
   		prototype.setDeliveryInstruction(prototype.getDeliveryInstruction());
   		prototype.setTotalPrice(createPrice(source, source.getTotalPrice()));
   		prototype.setTotalTax(createPrice(source, calcTotalTax(source)));

   		prototype.setSubTotal(createPrice(source, calcSubtotal(source)));

   		prototype.setPortalWET(createPrice(source, source.getOrderWET()));
   		prototype.setPortalFreight(createPrice(source, source.getFreight()));
   		if (asahiSiteUtil.isSga())
   		{
   			prototype.setOrderCDL(
   					createPrice(source, Double.valueOf(null != source.getOrderCDL() ? source.getOrderCDL().doubleValue() : 0.0D)));
   		}
   		prototype.setDeliveryCost(createPrice(source, source.getDeliveryCost()));
   		prototype.setTotalPriceWithTax((createPrice(source, calcTotalWithTax(source))));
   		prototype.setPlacedByName(getPlacedByName(source));
   		if (source.getStatus().equals(OrderStatus.COMPLETED) && asahiSiteUtil.isApb())
   		{
   			prototype.setTotalUnitCount(calcInvoicedTotalUnitCount(source));
   		}
		}
		else
		{
			super.addTotals(source, prototype);
		}
	}

	@Override
	protected void addEntries(final AbstractOrderModel source, final AbstractOrderData prototype)
	{
		if(!asahiSiteUtil.isCub())
		{
		final List<OrderEntryData> entries = new ArrayList<>(getOrderEntryConverter().convertAll(source.getEntries()));
		Collections.sort(entries,
				AsahiOrderEntryDataComparator.getComparator(AsahiOrderEntryDataComparator.SORT_BASED_ON.BRAND_SORT,
						AsahiOrderEntryDataComparator.SORT_BASED_ON.PRODUCT_NAME_SORT,
						AsahiOrderEntryDataComparator.SORT_BASED_ON.PRODUCT_CODE_SORT));
		prototype.setEntries(entries);
		}
		else
		{
			super.addEntries(source, prototype);
		}
	}

	@Override
	protected Double calcTotalWithTax(final AbstractOrderModel source)
	{
		if(!asahiSiteUtil.isCub())
		{
		if (source == null)
		{
			throw new IllegalArgumentException("source order must not be null");
		}

		if (source.getTotalPrice() == null)
		{
			return 0.0d;
		}
		BigDecimal totalPrice = new BigDecimal(0);
		if (source.getInvoiceAmountWithGST() != null)
		{
			totalPrice = BigDecimal.valueOf(source.getInvoiceAmountWithGST().doubleValue());
		}
		else
		{
			if (null != source.getTotalPrice())
			{
				totalPrice = BigDecimal.valueOf(source.getTotalPrice().doubleValue());
			}

			// Add the taxes to the total price if the cart is net; if the total was null taxes should be null as well
			if (Boolean.TRUE.equals(source.getNet()) && totalPrice.compareTo(BigDecimal.ZERO) != 0 && source.getOrderGST() != null)
			{
				totalPrice = totalPrice.add(BigDecimal.valueOf(source.getOrderGST().doubleValue()));
			}

		}
		/*if (asahiSiteUtil.isSga())
		{
			totalPrice = totalPrice
					.add(BigDecimal.valueOf(source.getOrderCDL() != null ? source.getOrderCDL().doubleValue() : 0.0D));
		}*/
		return totalPrice.doubleValue();
		}
		else
		{
			return super.calcTotalWithTax(source);
		}
	}


	protected Double calcTotalTax(final AbstractOrderModel source)
	{
		if (source == null)
		{
			throw new IllegalArgumentException("source order must not be null");
		}
		if (source.getTotalTax() == null)
		{
			return 0.0d;
		}
		BigDecimal totalTax = new BigDecimal(0);
		if (null != source.getOrderGST())
		{
			totalTax = BigDecimal.valueOf(source.getOrderGST().doubleValue());
		}
		return totalTax.doubleValue();
	}

	private Double calcSubtotal(final AbstractOrderModel source)
	{
		Double subtotal = source.getSubtotal();
		if (source.getInvoiceAmountWithGST() != null)
		{
			subtotal = source.getInvoiceAmountWithGST() - (null != source.getFreight() ? source.getFreight() : 0D)
					- (null != source.getOrderGST() ? source.getOrderGST() : 0D)
					- (null != source.getDeliveryCost() ? source.getDeliveryCost() : 0D);
		}
		return subtotal;
	}

	@Override
	protected void addDetails(final OrderModel source, final OrderData target)
	{
		super.addDetails(source, target);
		if(!asahiSiteUtil.isCub())
		{
			if(source.getSite().getUid().equalsIgnoreCase("apb"))
			{
				target.setStatusDisplay(asahiOrderService.getDisplayOrderStatus(source.getStatus().getCode()
						,source.getSite().getUid()));
			}
			else if(source.getSite().getUid().equalsIgnoreCase("sga"))
			{
				target.setStatusDisplay(asahiOrderService.getDisplayOrderStatus(source.getStatus().getCode()
						,source.getSite().getUid()));
			}
   		target.setOnlineOrder(source.getOnlineOrder());
   		target.setPoNumber(source.getPurchaseOrderNumber());
   		if (source.getOrderType() != null)
   		{
   			final String orderType = enumerationService.getEnumerationName(source.getOrderType(), i18nService.getCurrentLocale());
   			target.setOrderType(orderType);
   		}
   		if (source.getSalesOrderId() != null)
   		{
   			target.setSalesOrderId(source.getSalesOrderId());
   		}
   		else
   		{
   			if(this.asahiSiteUtil.isSga()){

   				target.setSalesOrderId(target.getCode());
   			}else{

   				final String salesOrderId = asahiConfigurationService.getString(ORDER_NUMBER_FOR_BLANK, "In Process");
   				target.setSalesOrderId(salesOrderId);
   			}
   		}
   		target.setDeliveryInstruction(source.getDeliveryInstruction());
   		target.setDeferredDelivery(source.getDeferredDelivery());
   		target.setIsOnlyBonus(!asahiCoreUtil.isNonBonusProductExist(source.getEntries()));
   		if (!asahiSiteUtil.isSga())
   		{
   			if (null != source.getDeliveryRequestDate())
   			{
   				final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
   				target.setDeliveryRequestDate(dateFormat.format(source.getDeliveryRequestDate()));
   			}
   		}
   		else
   		{
   			if (null != source.getDeliveryRequestDate())
   			{
   				final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
   				target.setDeliveryRequestDate(dateFormat.format(source.getDeliveryRequestDate()));
   			}
   		}
   		addCardDetail(source, target);
		}
		else
		{
			setOrderType(source, target);
		}
	}

	private void setOrderType(final OrderModel source, final OrderData target)
	{
		if (null != source && FREE_GOODS_ORDER_PROCESSING_CODE.equals(source.getProcessingTypeCode()))
		{
			target.setIsFreeGoodOrder(true);
		}
		else
		{
			target.setIsFreeGoodOrder(false);
		}
	}
	private void addCardDetail(final OrderModel source, final OrderData target)
	{
		if(null!=source.getPaymentType() && "Delivery".equalsIgnoreCase(source.getPaymentType().getCode())){
			target.setDeliveryOrder(true);
		}

		target.setIsPrepaid(source.getIsPrepaid());
		if (CollectionUtils.isNotEmpty(source.getPaymentTransactions()))
		{
			final AsahiPaymentTransactionModel paymentTransaction = (AsahiPaymentTransactionModel) source.getPaymentTransactions()
					.get(0);
			if (CollectionUtils.isNotEmpty(paymentTransaction.getEntries()))
			{
				final AsahiPaymentTransactionEntryModel paymentTransactionEntry = (AsahiPaymentTransactionEntryModel) paymentTransaction
						.getEntries().get(0);
				final AsahiCardData cardData = new AsahiCardData();
				cardData.setCardNumber(paymentTransactionEntry.getCardNumber());
				cardData.setCardType(paymentTransactionEntry.getCardType());
				cardData.setCardExpiry(paymentTransactionEntry.getCardExpiry());
				target.setCardData(cardData);
			}
		}
	}

	private String getPlacedByName(final AbstractOrderModel source)
	{
		if (source instanceof OrderModel)
		{
			final UserModel userModel = ((OrderModel) source).getPlacedBy();
			if (null != userModel)
			{
				return WordUtils.capitalize(userModel.getName());

			}
		}
		return StringUtils.EMPTY;
	}

	/**
	 * Gets the day of month suffix.
	 *
	 * @param n
	 *           the n
	 * @return the day of month suffix
	 */
	String getDayOfMonthSuffix(final int n)
	{
		if (n >= 11 && n <= 13)
		{
			return "th";
		}
		switch (n % 10)
		{
			case 1:
				return "st";
			case 2:
				return "nd";
			case 3:
				return "rd";
			default:
				return "th";
		}
	}

	protected Integer calcInvoicedTotalUnitCount(final AbstractOrderModel source)
	{
		int totalUnitCount = 0;
		for (final AbstractOrderEntryModel orderEntryModel : source.getEntries())
		{
			totalUnitCount += (null != orderEntryModel.getInvoicedQty() ? orderEntryModel.getInvoicedQty().intValue() : 0);
		}
		return Integer.valueOf(totalUnitCount);
	}

	@Override
	protected void addPromotions(final AbstractOrderModel source, final AbstractOrderData prototype)
	{
		if(!asahiSiteUtil.isCub())
		{
			addPromotions(source, null, prototype);
		}
		else
		{
			super.addPromotions(source, prototype);
		}
	}

	@Override
	protected void addPromotions(final AbstractOrderModel source, final PromotionOrderResults promoOrderResults,
			final AbstractOrderData prototype)
	{
		if(!asahiSiteUtil.isCub())
		{
			prototype.setTotalDiscounts(createPrice(source, source.getTotalDiscounts()));
		}
		else
		{
			super.addPromotions(source, promoOrderResults, prototype);
		}

	}
}
