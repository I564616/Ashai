package com.apb.integration.rest.order.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.apb.core.model.AsahiPaymentTransactionEntryModel;
import com.apb.integration.order.dto.AsahiLineRequest;
import com.apb.integration.order.dto.AsahiOrderAddressRequest;
import com.apb.integration.order.dto.AsahiOrderLinesRequest;
import com.apb.integration.order.dto.AsahiOrderRequest;
import com.apb.integration.service.config.AsahiConfigurationService;
import com.apb.integration.util.ApbAddressTimeUtil;


public class AsahiOrderRequestPopulator implements Populator<OrderModel, AsahiOrderRequest>
{

	public static final String PRODUCT_CODE_FOR_DELIVERY_SURCHARGE = "product.code.for.delivery.surcharge.apb";

	@Resource(name = "asahiIconfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	/** The Constant DATE_FORMAT. */
	private static final String DATE_FORMAT = "order.delivery.date.format.";
	private static final String TIME_FORMAT = "site.time.format.validation.";
	private static final String DATE_TIME_FORMAT = "site.date.format.validation.";

	private static final String DEFAULT_SALES_UNIT = "order.to.AX.default.sales.unit.";
	private static final String DEFAULT_CURRENCY_CODE = "order.to.AX.default.currency.code.";
	private static final String DEFAULT_ORDER_ORIGIN_ID = "order.to.AX.default.origin.id.apb";
	private static final String DEFAULT_CUSTOMER_REFERENCE = "order.to.AX.default.customer.reference.";
	private static final String DEFAULT_DELIVERY_INSTRUCTIONS = "order.to.AX.default.delivery.instruction.";
	private static final String DEFAULT_EXTERNAL_DISCOUNT = "order.to.AX.default.external.discount.";
	private static final String DEFAULT_EXTERNAL_DISCOUNT_DESC = "order.to.AX.default.external.discount.desc.";
	private static final String DEFAULT_PAYMENT_SURCHARGE = "order.to.AX.default.payment.surcharge.";
	private static final String DEFAULT_ONLINE_PAYMENT_REF = "order.to.AX.default.payment.reference.";
	private static final String DEFAULT_ONLINE_PAYMENT_AMT = "order.to.AX.default.payment.amount.";
	private static final String DEFAULT_ONLINE_PAYMENT_OTHER_REF = "order.to.AX.default.payment.other.ref.";

	private static final String SGA_SITE_ID = "sga";

	/** The Constant SHIP_TO_CODE. */
	private static final String SHIP_TO_CODE = "12";
	
	/** The Constant SOLD_TO. */
	private static final String SOLD_TO = "11";
	

	@Override
	public void populate(final OrderModel source, final AsahiOrderRequest target)
	{

		final SimpleDateFormat dateFormat = new SimpleDateFormat(
				this.asahiConfigurationService.getString(DATE_FORMAT + source.getSite().getUid(), "yyyy-MM-dd"));
		final SimpleDateFormat timeFormat = new SimpleDateFormat(
				this.asahiConfigurationService.getString(TIME_FORMAT + source.getSite().getUid(), "hh:mm:ss"));
		final SimpleDateFormat dateTimeFormat = new SimpleDateFormat(
				this.asahiConfigurationService.getString(DATE_TIME_FORMAT + source.getSite().getUid(), "dd-MM-yyyy hh:mm:ss"));

		final String salesUnit = this.asahiConfigurationService.getString(DEFAULT_SALES_UNIT + source.getSite().getUid(), "each");
		final String currencyCode = this.asahiConfigurationService.getString(DEFAULT_CURRENCY_CODE + source.getSite().getUid(),
				"AUD");
		final String orderOriginId = this.asahiConfigurationService.getString(DEFAULT_ORDER_ORIGIN_ID, "HP");
		final String externalDiscount = this.asahiConfigurationService
				.getString(DEFAULT_EXTERNAL_DISCOUNT + source.getSite().getUid(), "0");
		final String externalDiscountDesc = this.asahiConfigurationService
				.getString(DEFAULT_EXTERNAL_DISCOUNT_DESC + source.getSite().getUid(), "0");

		target.setExternalOrderNumber(source.getCode());
		if (null != source.getUnit())
		{
			final AsahiB2BUnitModel customerAccount = (AsahiB2BUnitModel) source.getUnit();
			target.setCustAccount(customerAccount.getAccountNum());

			if(source.getSite().getUid().equalsIgnoreCase(SGA_SITE_ID)){
				target.setDivision(customerAccount.getDivision());
				target.setSalesOrg(customerAccount.getSalesOrg());
				target.setDistChannel(customerAccount.getDistributionChannel());
				final AsahiOrderAddressRequest orderAddressForSoldTo = new AsahiOrderAddressRequest();
				final AsahiOrderAddressRequest orderAddressForShipTo = new AsahiOrderAddressRequest();
				final List<AsahiOrderAddressRequest> orderAddresses = new ArrayList<AsahiOrderAddressRequest>();

				orderAddressForSoldTo.setAddressType(SOLD_TO);
				orderAddressForSoldTo.setBackendRecordId(customerAccount.getAccountNum());
				orderAddresses.add(orderAddressForSoldTo);

				if(null!=source.getDeliveryAddress()){
					orderAddressForShipTo.setBackendRecordId(source.getDeliveryAddress().getAddressRecordid());
				}
				
				orderAddressForShipTo.setAddressType(SHIP_TO_CODE);
				orderAddresses.add(orderAddressForShipTo);

				target.setOrderAddress(orderAddresses);
			}
		}
		if (source.getPurchaseOrderNumber() != null)
		{
			target.setCustomerReference(source.getPurchaseOrderNumber());
		}
		else
		{
			final String cutomerReference = this.asahiConfigurationService
					.getString(DEFAULT_CUSTOMER_REFERENCE + source.getSite().getUid(), "");
			target.setCustomerReference(cutomerReference);
		}
		if (source.getDeliveryAddress() != null)
		{
			final AddressModel address = source.getDeliveryAddress();
			if (null != address.getEclDeliveryInstruction())
			{
				target.setDeliveryInstructions(StringEscapeUtils.escapeHtml4(address.getEclDeliveryInstruction()));
			}
			else
			{
				final String deliveryInstructions = this.asahiConfigurationService
						.getString(DEFAULT_DELIVERY_INSTRUCTIONS + source.getSite().getUid(), "");
				target.setDeliveryInstructions(deliveryInstructions);
			}
			if (null != address.getEclDeliveryTimeslotFrom())
			{
				final String deliveryTimeFrom = ApbAddressTimeUtil.getDeliveryTimeStringFull(address.getEclDeliveryTimeslotFrom());
				target.setDeliveryTimeFrom(deliveryTimeFrom);
			}
			if (null != address.getEclDeliveryTimesLotto())
			{
				final String deliveryTimeTO = ApbAddressTimeUtil.getDeliveryTimeStringFull(address.getEclDeliveryTimesLotto());
				target.setDeliveryTimeTo(deliveryTimeTO);
			}
			target.setAddressUID(address.getAddressRecordid());

		}
		if (!source.getSite().getUid().equalsIgnoreCase(SGA_SITE_ID) && null != source.getDeferredDelivery())
		{
			target.setIsDeferredDelivery(source.getDeferredDelivery().toString());
		}

		String requestedDeliveryDate;

		if (null != source.getDeliveryRequestDate())
		{
			requestedDeliveryDate = dateFormat.format(source.getDeliveryRequestDate()).toString();
		}
		else
		{
			requestedDeliveryDate = dateFormat.format(new Date()).toString();
		}
		target.setRequestedDeliveryDate(requestedDeliveryDate);

		if (CollectionUtils.isNotEmpty(source.getPaymentTransactions()))
		{
			final PaymentTransactionModel paymentTransaction = source.getPaymentTransactions().get(0);
			if (CollectionUtils.isNotEmpty(paymentTransaction.getEntries()))
			{
				final AsahiPaymentTransactionEntryModel paymentTransactionEntry = (AsahiPaymentTransactionEntryModel) paymentTransaction
						.getEntries().get(0);
				target.setOnlinePaymentOtherRefs(paymentTransactionEntry.getCardNumber());
				target.setOnlinePaymentReference(paymentTransactionEntry.getCode());
				if(source.getSite().getUid().equals(SGA_SITE_ID)){
				    target.setCCTokenId(paymentTransactionEntry.getRequestToken());
                }
			}
			double totalPrice = source.getTotalPrice();
			if (source.getOrderGST() != null)
			{
				totalPrice += source.getOrderGST();
			}
			if (source.getCreditSurCharge() != null)
			{
				totalPrice += source.getCreditSurCharge();
			}
			target.setOnlinePaymentAmount(totalPrice);
		}
		else
		{
			final String onlinePaymentOtherRef = this.asahiConfigurationService
					.getString(DEFAULT_ONLINE_PAYMENT_OTHER_REF + source.getSite().getUid(), "0");
			final String onlinePaymentReference = this.asahiConfigurationService
					.getString(DEFAULT_ONLINE_PAYMENT_REF + source.getSite().getUid(), "0");
			final String onlinePaymentAmount = this.asahiConfigurationService
					.getString(DEFAULT_ONLINE_PAYMENT_AMT + source.getSite().getUid(), "0");

			target.setOnlinePaymentOtherRefs(onlinePaymentOtherRef);
			target.setOnlinePaymentReference(onlinePaymentReference);
			target.setOnlinePaymentAmount(Double.valueOf(onlinePaymentAmount));
		}

		if (source.getCreditSurCharge() != null)
		{
			target.setOnlinePaymentSurcharge(source.getCreditSurCharge());
		}
		else
		{
			final String paymentSurcharge = this.asahiConfigurationService
					.getString(DEFAULT_PAYMENT_SURCHARGE + source.getSite().getUid(), "0");
			target.setOnlinePaymentSurcharge(Double.valueOf(paymentSurcharge));
		}
		if (null != source.getDate())
		{
			final String orderCreationTime = dateTimeFormat.format(source.getDate()).toString();
			target.setOrderCreatedDateTime(orderCreationTime);
		}
		target.setIsPrepaid(String.valueOf(source.getIsPrepaid()));
		if (source.getSite().getUid().equalsIgnoreCase(SGA_SITE_ID))
		{

			target.setOrderOriginId(source.getCode());
		}
		else
		{
			target.setOrderOriginId(orderOriginId);
		}
		if (null != source.getUser())
		{
			target.setOrderPlacedBy(source.getUser().getUid());
		}
		final AsahiOrderLinesRequest orderLineRequest = new AsahiOrderLinesRequest();
		final List<AsahiLineRequest> line = new ArrayList<>();
		for (final AbstractOrderEntryModel entryProduct : source.getEntries())
		{
			final AsahiLineRequest lineRequest = new AsahiLineRequest();
			if (source.getSite().getUid().equalsIgnoreCase(SGA_SITE_ID))
			{
				setDiscountValues(entryProduct, lineRequest);
				lineRequest.setLineNum(entryProduct.getEntryNumber()+1);				
			}
			else
			{
				lineRequest.setExternalDiscount(externalDiscount);
				lineRequest.setExternalDiscountDescription(externalDiscountDesc);
			}

			lineRequest.setIsBonusStock(BooleanUtils.isTrue(entryProduct.getIsBonusStock()) 
					|| BooleanUtils.isTrue(entryProduct.getIsFreeGood()) ? "true" : "false");
			   

			lineRequest.setProductId(entryProduct.getProduct().getCode());
			lineRequest.setQuantity(entryProduct.getQuantity());
			lineRequest.setCurrencyCode(currencyCode);
			lineRequest.setSalesUnit(salesUnit);
			line.add(lineRequest);
		}
		if (source.getDeliveryCost() != null && source.getDeliveryCost() > 0)
		{
			final String deliverySurchargeCode = asahiConfigurationService
					.getString(PRODUCT_CODE_FOR_DELIVERY_SURCHARGE, "delivery_product");
			final AsahiLineRequest lineRequest = new AsahiLineRequest();
			lineRequest.setExternalDiscount(externalDiscount);
			lineRequest.setExternalDiscountDescription(externalDiscountDesc);
			if (!source.getSite().getUid().equalsIgnoreCase(SGA_SITE_ID))
			{
				lineRequest.setIsBonusStock("false");
			}
			lineRequest.setProductId(deliverySurchargeCode);
			lineRequest.setQuantity(1L);
			lineRequest.setCurrencyCode(currencyCode);
			lineRequest.setSalesUnit(salesUnit);
			line.add(lineRequest);
		}
		orderLineRequest.setLine(line);
		target.setOrderLines(orderLineRequest);

		//Sga specific request attribtues start

		if (source.getSite().getUid().equalsIgnoreCase(SGA_SITE_ID))
		{
			if (StringUtils.isNotEmpty(source.getDeviceType()))
			{
				target.setDeviceType(source.getDeviceType());
			}


			target.setCustOrderType(source.getCustOrderType());

			if (StringUtils.isNotEmpty(source.getCompanyCode()))
			{
				target.setCompanyCode(source.getCompanyCode());
			}
		}

		//Sga specific request attribtues end
	}

	/**
	 * <p>
	 * This method will set the discount attributes
	 * </p>
	 *
	 * @param entryProduct
	 * @param lineRequest
	 */
	private void setDiscountValues(final AbstractOrderEntryModel entryProduct, final AsahiLineRequest lineRequest)
	{

		if (CollectionUtils.isNotEmpty(entryProduct.getDiscountValues()) && null != entryProduct.getDiscountValues().get(0))
		{
			lineRequest.setExternalDiscount(String.valueOf(entryProduct.getDiscountValues().get(0).getValue()));
			lineRequest.setExternalDiscountDescription(entryProduct.getDiscountValues().get(0).getCode());
		}
		else
		{
			lineRequest.setExternalDiscount("0");
			lineRequest.setExternalDiscountDescription("0");
		}

	}
}
