package com.apb.facades.populators;

import de.hybris.platform.basecommerce.enums.OrderEntryStatus;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.product.service.ApbProductReferenceService;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.services.AsahiOrderService;
import com.apb.facades.constants.ApbFacadesConstants;
import com.apb.service.b2bunit.ApbB2BUnitService;
import com.sabmiller.core.enums.AddressType;
import com.sabmiller.core.enums.OrderType;
import com.sabmiller.core.model.AsahiB2BUnitModel;



/**
 * The Class AsahiOrderReversePopulator.
 *
 * @author Kuldeep.Singh1
 */
public class AsahiOrderReversePopulator implements Populator<OrderData, OrderModel>
{

	// Creates logger
	final Logger logger = LoggerFactory.getLogger(AsahiOrderReversePopulator.class);

	private static final String CODE_COMPANY_SITE_UID = ".company.site.uid";

	/** The Constant DATE_FORMAT. */
	private static final String DATE_FORMAT = "site.date.format.apb";

	private static final String ORDER_DATE_FORMAT = "order.date.format.apb";
	/** The Constant PRODUCT_CODE_FOR_DELIVERY_SURCHARGE. */
	public static final String PRODUCT_CODE_FOR_DELIVERY_SURCHARGE = "product.code.for.delivery.surcharge.apb";

	/** The Constant PRODUCT_CODE_FOR_FREIGHT. */
	public static final String PRODUCT_CODE_FOR_FREIGHT = "product.code.for.freight.apb";

	/** The Constant BACKEND_ORDER_PLACED_BY. */
	public static final String BACKEND_ORDER_PLACED_BY = "apb.backend.order.placed.by";

	/** The Constant STATUS_PICKLIST_GENERATED. */
	private static final String STATUS_PICKLIST_GENERATED = "30";

	/** The Constant STATUS_INVOICE_GENERATED. */
	private static final String STATUS_INVOICE_GENERATED = "50";

	/** The Constant STATUS_IN_PROGRESS. */
	private static final String STATUS_IN_PROGRESS = "10";

	/** The Constant STATUS_PICKLIST_CANCELLED. */
	private static final String STATUS_PICKLIST_CANCELLED = "40";

	/** The Constant APB_COMPANY_CODE. */
	private static final String APB_COMPANY_CODE = "apb";

	/** The Constant SGA_COMPANY_CODE. */
	private static final String SGA_COMPANY_CODE = "sga";

	/** The apb address reverse converter. */
	private Converter<AddressData, AddressModel> apbAddressReverseConverter;

	/** The asahi order entry reverse converter. */
	private Converter<OrderEntryData, AbstractOrderEntryModel> asahiOrderEntryReverseConverter;

	/** The asahi order entry basic reverse converter. */
	private Converter<OrderEntryData, AbstractOrderEntryModel> asahiOrderEntryBasicReverseConverter;

	/** The asahi configuration service. */
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	/** The model service. */
	@Resource(name = "modelService")
	private ModelService modelService;

	/** The user service. */
	@Resource(name = "userService")
	private UserService userService;

	/** The apb product reference service. */
	@Resource(name = "apbProductReferenceService")
	private ApbProductReferenceService apbProductReferenceService;

	/** The asahi order service. */
	@Resource(name = "asahiOrderService")
	private AsahiOrderService asahiOrderService;

	/** The enumeration service. */
	@Resource(name = "enumerationService")
	private EnumerationService enumerationService;

	/** The apb B 2 B unit service. */
	@Resource(name = "apbB2BUnitService")
	ApbB2BUnitService apbB2BUnitService;

	/** The common I 18 N service. */
	@Resource
	CommonI18NService commonI18NService;

	/**
	 * Populate.
	 *
	 * @param orderData
	 *           the order data
	 * @param orderModel
	 *           the order model
	 * @throws ConversionException
	 *            the conversion exception
	 */
	@Override
	public void populate(final OrderData orderData, final OrderModel orderModel) throws ConversionException
	{
		boolean firstTimeOrder = false;
		if (null == orderData)
		{
			return;
		}
		//Getting Site Date Format
		final SimpleDateFormat format = new SimpleDateFormat(this.asahiConfigurationService.getString(DATE_FORMAT, "dd-MM-yyyy"));

		if (StringUtils.isEmpty(orderModel.getCode()))
		{
			firstTimeOrder = true;
			this.populateFirstTimeAttributes(orderData, orderModel, format);
		}

		this.populateStatus(orderData, orderModel);

		this.populateOrderMissingAttributes(orderData, orderModel);

		if (firstTimeOrder)
		{
			populatingLineItems(orderData, orderModel, orderData.getCompanyCode(), asahiOrderEntryBasicReverseConverter);

			//populating Order Delivery Address
			if (null != orderData.getDeliveryAddress())
			{
				//populating Order Line Items
				if (orderData.getCompanyCode().equalsIgnoreCase("sga"))
				{
					this.fetchingAndPopulatingDeliveryAddress(orderData.getDeliveryAddress(), orderModel, orderData);
				}
				else
				{
					this.populatingDeliveryAddress(orderData.getDeliveryAddress(), orderModel);
				}
			}
		}

		this.populateAttributesBasedOnStatus(orderData, orderModel);

		//update Price and Invoice Entries for SGA.
		if (orderData.getCompanyCode().equalsIgnoreCase("sga"))
		{
			this.updateEntriesWithInvoice(orderData, orderModel);
		}
			this.updatePickListGeneratedAttributes(orderData, orderModel);
		   orderModel.setDeliveryInstruction(orderData.getDeliveryInstruction());
	}


	/**
	 * Populate status.
	 *
	 * @param orderData
	 *           the order data
	 * @param orderModel
	 *           the order model
	 */
	private void populateStatus(final OrderData orderData, final OrderModel orderModel)
	{
		if (CollectionUtils.isNotEmpty(orderModel.getEntries()))
		{
			orderModel.getEntries().stream().forEach(entry -> clearEntryInvoicedAttributes(entry));
			modelService.saveAll(orderModel.getEntries());
		}
		if (StringUtils.isNotEmpty(orderData.getStatusCode()))
		{
			orderModel.setStatus(this.enumerationService.getEnumerationValue(OrderStatus.class,
					asahiOrderService.getOrderMapping(orderData.getStatusCode())));
		}
		else if (null == orderModel.getStatus())
		{
			orderModel.setStatus(OrderStatus.IN_PROGRESS);
		}
		orderModel.setBackendStatusCode(orderData.getStatusCode());
		orderModel.setCustOrderType(orderData.getCustOrderType());

		if (StringUtils.isNotEmpty(orderData.getDeviceType()))
		{
			orderModel.setDeviceType(orderData.getDeviceType());
		}
	}

	/**
	 * Clear entry invoiced attributes.
	 *
	 * @param entry
	 *           the entry
	 */
	private void clearEntryInvoicedAttributes(final AbstractOrderEntryModel entry)
	{
		entry.setInvoicedQty(null);
		entry.setStatus(null);
	}

	/**
	 * Populate first time attributes.
	 *
	 * @param orderData
	 *           the order data
	 * @param orderModel
	 *           the order model
	 * @param format
	 *           the format
	 */
	private void populateFirstTimeAttributes(final OrderData orderData, final OrderModel orderModel, final SimpleDateFormat format)
	{
		if (StringUtils.isNotEmpty(orderData.getPortalOrderId()))
		{
			orderModel.setCode(orderData.getPortalOrderId()); //hybris portal Order Id
		}
		else
		{
			orderModel.setCode(orderData.getSalesOrderId()); //hybris portal Order Id
		}
		orderModel.setSalesOrderId(orderData.getSalesOrderId());
		orderModel.setIsPrepaid(orderData.getIsPrepaid());
		orderModel.setPurchaseOrderNumber(orderData.getCustomerReference());
		orderModel.setUnit(apbB2BUnitService.getB2BUnitByAccountNumber(orderData.getCustAccount()));
		orderModel.setLocale(commonI18NService.getCurrentLanguage().getIsocode());
		orderModel.setSalesApplication(SalesApplication.WEB);
		try
		{
			orderModel.setBackendCreatedDate(format.parse(orderData.getBackendCreatedDate()));
		}
		catch (final ParseException exp)
		{
			logger.error("Parse Exception occured" + exp.getMessage());
		}
		orderModel.setDate(getCreationDateTime(orderData.getBackendCreatedDate()));
		if (StringUtils.isNotEmpty(orderData.getOrderPlacedBy()))
		{
			orderModel.setUser(this.userService.getUserForUID(orderData.getOrderPlacedBy()));
		}
		else
		{
			orderModel.setUser(this.userService.getUserForUID(this.asahiConfigurationService.getString(BACKEND_ORDER_PLACED_BY,
					ApbFacadesConstants.ORDER_PLACED_BY_CONF_DEFAULT)));
		}

		orderModel.setCurrency(this.apbProductReferenceService.getCurrencyForIsoCode(orderData.getCurrencyIso()));

		if (StringUtils.isNotEmpty(orderData.getOrderOriginId())
				&& StringUtils.equalsIgnoreCase(orderData.getOrderOriginId(), ApbFacadesConstants.HYBRIS_ORDER_ORIGIN_CONST))
		{
			orderModel
					.setOrderType(this.enumerationService.getEnumerationValue(OrderType.class, ApbFacadesConstants.ORDER_TYPE_ONLINE));
			orderModel.setOnlineOrder(Boolean.TRUE);
		}
		else
		{
			orderModel.setOrderType(
					this.enumerationService.getEnumerationValue(OrderType.class, ApbFacadesConstants.ORDER_TYPE_CALLCENTERORDER));
			orderModel.setOnlineOrder(Boolean.FALSE);
		}

		//getting site Uid from configuration based on company code
		final StringBuilder siteUidConfigKeyWithCompanyCode = new StringBuilder(orderData.getCompanyCode())
				.append(CODE_COMPANY_SITE_UID);
		final String siteUid = this.asahiConfigurationService.getString(siteUidConfigKeyWithCompanyCode.toString(), "apb");

		//setting BaseSite
		final BaseSiteModel baseSiteModel = this.asahiOrderService.getBaseSiteByUid(siteUid);
		orderModel.setSite(baseSiteModel);

		//setting BaseStore
		if (CollectionUtils.isNotEmpty(baseSiteModel.getStores()))
		{
			orderModel.setStore(baseSiteModel.getStores().get(0));
		}
	}

	/**
	 * Populate order missing attributes.
	 *
	 * @param orderData
	 *           the order data
	 * @param orderModel
	 *           the order model
	 */
	private void populateOrderMissingAttributes(final OrderData orderData, final OrderModel orderModel)
	{
		orderModel.setOrderOriginId(orderData.getOrderOriginId());
		if (StringUtils.isNotEmpty(orderData.getOrderOriginId())
				&& StringUtils.equalsIgnoreCase(orderData.getOrderOriginId(), ApbFacadesConstants.HYBRIS_ORDER_ORIGIN_CONST))
		{
			if (orderModel.getOrderType() == null)
			{
				orderModel.setOrderType(
						this.enumerationService.getEnumerationValue(OrderType.class, ApbFacadesConstants.ORDER_TYPE_ONLINE));
			}
			if (orderModel.getOnlineOrder() == null)
			{
				orderModel.setOnlineOrder(Boolean.TRUE);
			}
		}
		else
		{
			if (orderModel.getOrderType() == null)
			{
				orderModel.setOrderType(
						this.enumerationService.getEnumerationValue(OrderType.class, ApbFacadesConstants.ORDER_TYPE_CALLCENTERORDER));
			}
			if (orderModel.getOnlineOrder() == null)
			{
				orderModel.setOnlineOrder(Boolean.FALSE);
			}
		}
		if (StringUtils.isEmpty(orderModel.getSalesOrderId()))
		{
			orderModel.setSalesOrderId(orderData.getSalesOrderId());
		}
	}

	/**
	 * Populate attributes based on status.
	 *
	 * @param orderData
	 *           the order data
	 * @param orderModel
	 *           the order model
	 */
	private void populateAttributesBasedOnStatus(final OrderData orderData, final OrderModel orderModel)
	{
		final String orderStatus = orderData.getStatusCode();
		if (StringUtils.isNotEmpty(orderStatus))
		{
			switch (orderStatus)
			{
				case STATUS_PICKLIST_GENERATED:
					updatePickListGeneratedAttributes(orderData, orderModel);
					break;

				case STATUS_PICKLIST_CANCELLED:
					updatePicklistCancelledAttributes(orderModel);
					break;

				case STATUS_INVOICE_GENERATED:
					updateEntriesWithInvoice(orderData, orderModel);
					break;

				default:
					break;
			}
		}

	}

	/**
	 * Update entries with invoice.
	 *
	 * @param orderData
	 *           the order data
	 * @param orderModel
	 *           the order model
	 */
	private void updateEntriesWithInvoice(final OrderData orderData, final OrderModel orderModel)
	{
		orderModel.setInvoiceId(orderData.getInvoiceId()!=null?orderData.getInvoiceId():orderModel.getInvoiceId());
		orderModel.setInvoiceCreatedDate(getCreationDateTime(orderData.getInvoiceCreatedDate()));
		orderModel.setShippingWarehouse(orderData.getShippingWarehouse()!=null?orderData.getShippingWarehouse()
				:orderModel.getShippingWarehouse());
		populatePriceTotals(orderData, orderModel);
		if (null != orderModel.getEntries())
		{
			//updating Entries in Hybris which has been removed in ECC
			updateRemovedEntriesInHybrisD(orderData, orderModel.getEntries());
		}
		populatingLineItems(orderData, orderModel, orderData.getCompanyCode(), asahiOrderEntryReverseConverter);

		//populating Order Delivery Address
		if (null != orderData.getDeliveryAddress())
		{
			//populating Order Line Items
			if (orderData.getCompanyCode().equalsIgnoreCase("sga"))
			{
				this.fetchingAndPopulatingDeliveryAddress(orderData.getDeliveryAddress(), orderModel, orderData);
			}
			else
			{
				this.populatingDeliveryAddress(orderData.getDeliveryAddress(), orderModel);
			}
		}
	}

	/**
	 * Fetching and populating delivery address.
	 *
	 * @param deliveryAddress
	 *           the delivery address
	 * @param orderModel
	 *           the order model
	 * @param orderData
	 */
	private void fetchingAndPopulatingDeliveryAddress(final AddressData deliveryAddress, final OrderModel orderModel,
			final OrderData orderData)
	{
		final AsahiB2BUnitModel asahiB2BUnitModel = this.apbB2BUnitService.getB2BUnitByAccountNumber(deliveryAddress.getRecordId());
		if (null != asahiB2BUnitModel)
		{
				if (CollectionUtils.isNotEmpty(asahiB2BUnitModel.getAddresses()))
				{
					final AddressModel address = (AddressModel) ((List) asahiB2BUnitModel.getAddresses()).get(0);
					orderModel.setDeliveryAddress(address);
					logger.info("delivery address printing :" + null != address.getPartnerNumber() ? address.getPartnerNumber()
							: "partner number is empty");
				}
		}

	}

	/**
	 * Update picklist cancelled attributes.
	 *
	 * @param orderModel
	 *           the order model
	 */
	private void updatePicklistCancelledAttributes(final OrderModel orderModel)
	{
		orderModel.getEntries().stream().forEach(entry -> entry.setStatus(OrderEntryStatus.CANCELLED));
		modelService.saveAll(orderModel.getEntries());
	}

	/**
	 * Update pick list generated attributes.
	 *
	 * @param orderData
	 *           the order data
	 * @param orderModel
	 *           the order model
	 */
	private void updatePickListGeneratedAttributes(final OrderData orderData, final OrderModel orderModel)
	{
		//Getting Site Date Format
		final SimpleDateFormat format = new SimpleDateFormat(this.asahiConfigurationService.getString(DATE_FORMAT, "dd-MM-yyyy"));

		try
		{
			orderModel.setScheduleDeliveryDate(format.parse(orderData.getScheduleDeliveryDate()));
			orderModel.setScheduleShippingDate(format.parse(orderData.getScheduleShippingDate()));
			if ((!(orderData.getCompanyCode().equalsIgnoreCase(SGA_COMPANY_CODE)))
					|| (orderData.getCompanyCode().equalsIgnoreCase(SGA_COMPANY_CODE) && (STATUS_IN_PROGRESS).equalsIgnoreCase(orderData.getStatusCode())))
			{
				orderModel.setDeliveryRequestDate(format.parse(orderData.getScheduleDeliveryDate()));
			}
		}
		catch (final ParseException exp)
		{
			logger.error("Parse Exception occured" + exp.getMessage());
		}
		orderModel.setShippingWarehouse(orderData.getShippingWarehouse());
		orderModel.setErrorMsg(orderData.getErrorMsg());
		orderModel.setPickingListId(orderData.getPickingListId());

	}

	/**
	 * Populate price totals.
	 *
	 * @param orderData
	 *           the order data
	 * @param orderModel
	 *           the order model
	 */
	private void populatePriceTotals(final OrderData orderData, final OrderModel orderModel)
	{
		orderModel.setInvoiceId(orderData.getInvoiceId()!=null?orderData.getInvoiceId():orderModel.getInvoiceId());
		orderModel.setInvoiceCreatedDate(getCreationDateTime(orderData.getInvoiceCreatedDate()));
		orderModel.setInvoiceAmountWithGST(orderData.getInvoiceAmountWithGST()!=null?orderData.getInvoiceAmountWithGST()
				:orderModel.getInvoiceAmountWithGST());
		orderModel.setTotalPrice(orderData.getInvoiceAmountWithGST()!=null?orderData.getInvoiceAmountWithGST()
				:orderModel.getInvoiceAmountWithGST());
		orderModel.setOrderGST(orderData.getOrderGST()!=null?orderData.getOrderGST():orderModel.getOrderGST());

		orderModel.setDeliverySurChargeGST(null);
		orderModel.setDeliveryCost(null);
		orderModel.setFreight(null);
		orderModel.setFreightGST(null);

	}

	/**
	 * Merge line item.
	 *
	 * @param source the source
	 * @param entries the entries
	 */
	private void mergeLineItem(final OrderEntryData orderEntry1,
			final OrderEntryData orderEntry2) {
		orderEntry1.setQuantity(orderEntry1.getQuantity() + orderEntry2.getQuantity());

		orderEntry1.setInvoicedQty((null != orderEntry1.getInvoicedQty() ? orderEntry1.getInvoicedQty() : 0) + (null != orderEntry2.getInvoicedQty() ? orderEntry2.getInvoicedQty() : 0));
		orderEntry1.setNetUnitPrice((null != orderEntry1.getNetUnitPrice() ? orderEntry1.getNetUnitPrice() : 0) +(null != orderEntry2.getNetUnitPrice() ? orderEntry2.getNetUnitPrice() : 0));
		orderEntry1.setNetLineOrderAmount((null != orderEntry1.getNetLineOrderAmount() ? orderEntry1.getNetLineOrderAmount() : 0) + (null != orderEntry2.getNetLineOrderAmount() ? orderEntry2.getNetLineOrderAmount() : 0));
		orderEntry1.setNetLineInvoiceAmount((null != orderEntry1.getNetLineInvoiceAmount() ? orderEntry1.getNetLineInvoiceAmount() : 0) + (null != orderEntry2.getNetLineInvoiceAmount() ? orderEntry2.getNetLineInvoiceAmount() : 0));
		orderEntry1.setOrderEntryGST((null != orderEntry1.getOrderEntryGST() ? orderEntry1.getOrderEntryGST() : 0) + (null != orderEntry2.getOrderEntryGST() ? orderEntry2.getOrderEntryGST() : 0));
		orderEntry1.setCdl((null != orderEntry1.getCdl() ? orderEntry1.getCdl() : 0) + (null != orderEntry2.getCdl() ? orderEntry2.getCdl() : 0));
		orderEntry1.setIsBonusStock(true);
	}

	/**
	 * Populating line items.
	 *
	 * @param orderData.getEntries()
	 *           the order entry data list
	 * @param orderModel
	 *           the order entry model list
	 * @param companyCode
	 * @param asahiOrderEntryBasicReverseConverter2
	 */
	private void populatingLineItems(final OrderData orderData, final OrderModel orderModel, final String companyCode,
			final Converter<OrderEntryData, AbstractOrderEntryModel> entryConverter)
	{
		final List<AbstractOrderEntryModel> existingEntries = null != orderModel.getEntries()
				? new ArrayList<>(orderModel.getEntries()) : new ArrayList<>();

		if (CollectionUtils.isNotEmpty(orderData.getEntries()))
		{
			orderModel.setDeliverySurChargeGST(0.0);
			orderModel.setDeliveryCost(0.0);
			orderData.getEntries().stream().filter(entry -> null == entry.getWetItem()).collect(Collectors.toList())
					.forEach(entry -> entry.setWetItem(StringUtils.EMPTY));
			orderData.getEntries().sort(Comparator.comparing((final OrderEntryData orderEntry) -> orderEntry.getWetItem()));
			Double totalCDL = 0.0;
			for (final OrderEntryData orderEntry : orderData.getEntries())
			{
				orderEntry.setOrderStatus(orderData.getStatusCode());
				// calculating Order Total CDL
				if(null!=orderEntry && null!=orderEntry.getCdl() && orderEntry.getCdl()>0.0){
					totalCDL = totalCDL + orderEntry.getCdl();
				}

				orderEntry.setOrderId(orderModel.getCode());
				orderEntry.setCurrencyIso(orderData.getCurrencyIso());

				//checking Surcharge and Freight line item if Entry contains, No processing is required
				if (null != orderEntry.getProduct() && orderEntry.getProduct().getCode()
						.contains(this.asahiConfigurationService.getString(PRODUCT_CODE_FOR_DELIVERY_SURCHARGE, "900153")))
				{
					//setting Delivery Surcharge for Order
					orderModel.setDeliverySurChargeGST(orderEntry.getOrderEntryGST());
					orderModel.setDeliveryCost(orderEntry.getNetLineInvoiceAmount());
				}
				else if (null != orderEntry.getProduct() && orderEntry.getProduct().getCode()
						.contains(this.asahiConfigurationService.getString(PRODUCT_CODE_FOR_FREIGHT, "900129")))
				{
					//setting Freight for Order
					orderModel.setFreight(orderEntry.getNetLineInvoiceAmount());
					orderModel.setFreightGST(orderEntry.getOrderEntryGST());
				}
				else
				{
					orderEntry.setCompanyCode(companyCode);
					// Check if this orderEntry already exist in hybris if yes then update otherwise create new.
					Optional<AbstractOrderEntryModel> existingOrderEntry = Optional.empty();

					if (CollectionUtils.isNotEmpty(existingEntries))
					{
						existingOrderEntry = orderModel.getEntries().stream()
								.filter(entry -> entry.getProduct().getCode().equals(orderEntry.getProduct().getCode())
										&& entry.getQuantity().equals(orderEntry.getQuantity())
										&& (null == entry.getLineNum() || entry.getLineNum().equals(orderEntry.getLineNum())))
								.findFirst();
					}

					if (existingOrderEntry.isPresent())
					{
						entryConverter.convert(orderEntry, existingOrderEntry.get());
					}
					else
					{
						if (StringUtils.isNotEmpty(orderEntry.getWetItem()))
						{
							entryConverter.convert(orderEntry, null);
						}
						else
						{
							AbstractOrderEntryModel newOrderEntry = modelService.create(OrderEntryModel.class);
							newOrderEntry.setOrder(orderModel);

							if(STATUS_INVOICE_GENERATED.equalsIgnoreCase(orderData.getStatusCode())){
								newOrderEntry.setStatus(OrderEntryStatus.ADDED);
							}

							newOrderEntry = entryConverter.convert(orderEntry, newOrderEntry);
							if (null != newOrderEntry.getProduct())
							{
								existingEntries.add(newOrderEntry);
							}
						}
					}
				}
			}
			orderModel.setOrderCDL(totalCDL);
		}
		orderModel.setEntries(existingEntries);

		double orderWET = 0.0;
		for (final AbstractOrderEntryModel entry : orderModel.getEntries())
		{
			if(null!= entry.getIsBonusStock() && !entry.getIsBonusStock()){
				final double wetForEntry = null != entry.getOrderEntryWET() ? entry.getOrderEntryWET() : 0D;
				if (wetForEntry > 0.00)
				{
					if(this.asahiConfigurationService.getBoolean("enable.wet.apb", false)){
						entry.setBasePrice(entry.getBasePrice() + (wetForEntry / entry.getInvoicedQty()));
						entry.setNetUnitPrice(entry.getNetUnitPrice() + (wetForEntry / entry.getInvoicedQty()));
					}

					entry.setTotalPrice(entry.getTotalPrice() + wetForEntry);
					orderWET += wetForEntry;
					modelService.save(entry);
				}
			}
		}
		orderModel.setOrderWET(orderWET);
	}

	/**
	 * Update removed entries in hybris D.
	 *
	 * @param orderData
	 *           the order data
	 * @param existingEntries
	 *           the existing entries
	 */
	private void updateRemovedEntriesInHybrisD(final OrderData orderData, final List<AbstractOrderEntryModel> existingEntries)
	{
		List<String> productCodes = null;
		if (CollectionUtils.isNotEmpty(existingEntries) && null != orderData.getEntries())
		{
			productCodes = existingEntries.stream().map(entry -> entry.getProduct().getCode()).distinct()
					.collect(Collectors.toList());
			for (final String product : productCodes)
			{
				final List<AbstractOrderEntryModel> hybrisEntries = existingEntries.stream()
						.filter(entry -> entry.getProduct().getCode().equals(product)).collect(Collectors.toList());
				final List<OrderEntryData> entriesFromDynamics = orderData.getEntries().stream()
						.filter(entry -> entry.getProduct().getCode().equals(product)).collect(Collectors.toList());

				final int max = hybrisEntries.size() > entriesFromDynamics.size() ? hybrisEntries.size() : entriesFromDynamics.size();
				if (CollectionUtils.isEmpty(entriesFromDynamics))
				{
					hybrisEntries.stream().forEach(entry -> updateStatusToNotSupplied(entry));
				}
				else
				{
					IntStream.range(0, max)
							.forEach(i -> mapEntriesAndUpdateStatus(i >= hybrisEntries.size() ? null : hybrisEntries.get(i),
									i >= entriesFromDynamics.size() ? null : entriesFromDynamics.get(i)));
				}
			}
		}

	}

	/**
	 * Map entries and update status.
	 *
	 * @param hybrisEntry
	 *           the hybris entry
	 * @param entryFromDynamics
	 *           the entry from dynamics
	 */
	private void mapEntriesAndUpdateStatus(final AbstractOrderEntryModel hybrisEntry, final OrderEntryData entryFromDynamics)
	{
		if (null != hybrisEntry && null != entryFromDynamics)
		{
			if (!hybrisEntry.getQuantity().equals(entryFromDynamics.getQuantity()))
			{
				hybrisEntry.setQuantity(entryFromDynamics.getQuantity());
				hybrisEntry.setStatus(OrderEntryStatus.UPDATED);
				modelService.save(hybrisEntry);
			}
		}
		else if (null != hybrisEntry)
		{
			updateStatusToNotSupplied(hybrisEntry);
		}
		else if (null != entryFromDynamics)
		{
			entryFromDynamics.setOrderEntryStatus(OrderEntryStatus.ADDED.getCode());
		}

	}

	/**
	 * Update status to not supplied.
	 *
	 * @param entryModel
	 *           the entry model
	 */
	private void updateStatusToNotSupplied(final AbstractOrderEntryModel entryModel)
	{
		entryModel.setStatus(this.enumerationService.getEnumerationValue(OrderEntryStatus.class, "NOTSUPPLIED"));
		entryModel.setInvoicedQty(0);
		entryModel.setTotalPrice(0D);
		entryModel.setOrderEntryWET(0D);
		this.modelService.save(entryModel);
	}

	/**
	 * Update removed entries in hybris.
	 *
	 * @param orderData
	 *           the order data
	 * @param existingEntries
	 *           the existing entries
	 */
	private void updateRemovedEntriesInHybris(final OrderData orderData, final List<AbstractOrderEntryModel> existingEntries)
	{

		final HashMap<String, OrderEntryModel> hybrisEntryMap = new HashMap<>();
		final HashMap<String, String> eccEntryMap = new HashMap<>();
		final HashMap<String, OrderEntryModel> hybrisEntryMapNeedsToBeUpdated = new HashMap<>();
		Boolean isBonusStock;

		for (final AbstractOrderEntryModel entry : existingEntries)
		{
			hybrisEntryMap.put(entry.getProduct().getCode() + entry.getQuantity().toString(), (OrderEntryModel) entry);
			entry.setStatus(null);
		}
		if (CollectionUtils.isNotEmpty(orderData.getEntries()))
		{
			for (final OrderEntryData entry : orderData.getEntries())
			{
				isBonusStock = null != entry.getIsBonusStock() ? entry.getIsBonusStock() : Boolean.FALSE;
				if (!isBonusStock)
				{
					eccEntryMap.put(entry.getProduct().getCode() + entry.getQuantity().toString(), entry.getBackendUid());
				}
			}
		}


		for (final String key : hybrisEntryMap.keySet())
		{
			if (!eccEntryMap.containsKey(key))
			{
				hybrisEntryMapNeedsToBeUpdated.put(key, hybrisEntryMap.get(key));
			}
		}
		for (final Map.Entry entry : hybrisEntryMapNeedsToBeUpdated.entrySet())
		{
			final OrderEntryModel entryModel = (OrderEntryModel) entry.getValue();
			entryModel.setStatus(this.enumerationService.getEnumerationValue(OrderEntryStatus.class, "NOTSUPPLIED"));
			entryModel.setInvoicedQty(0);
			entryModel.setBasePrice(0D);
			entryModel.setTotalPrice(0D);
			entryModel.setOrderEntryWET(0D);
			this.modelService.save(entryModel);
		}
	}

	/**
	 * Populating delivery address.
	 *
	 * @param addressData
	 *           the address data
	 * @param addressModel
	 *           the order model
	 */
	private void populatingDeliveryAddress(final AddressData addressData, final OrderModel orderModel)
	{
		AddressModel addressModel = orderModel.getDeliveryAddress();
		if (null == addressModel)
		{
			addressModel = this.modelService.create(AddressModel.class);
			addressModel.setOwner(orderModel);
		}
		this.apbAddressReverseConverter.convert(addressData, addressModel);
		addressModel.setShippingAddress(Boolean.TRUE);
		addressModel.setBillingAddress(Boolean.FALSE);
		addressModel.setAddressType(this.enumerationService.getEnumerationValue(AddressType.class, "DELIVERY"));

		this.modelService.save(addressModel);
		orderModel.setDeliveryAddress(addressModel);
	}

	/**
	 * Gets the creation date time.
	 *
	 * @param orderTime
	 *           the order time
	 * @return the creation date time
	 */
	private Date getCreationDateTime(final String orderTime)
	{
		if (StringUtils.isNotEmpty(orderTime))
		{
			final String dateTimeFormat = this.asahiConfigurationService.getString(ORDER_DATE_FORMAT, "yyyy-MM-dd HH:mm:ss");
			final SimpleDateFormat format = new SimpleDateFormat(dateTimeFormat);

			try
			{
				final Date formatedDate = format.parse(orderTime); // Create a new Date object
				return formatedDate;
			}
			catch (final ParseException e)
			{
				logger.error("IllegalArgumentException caught in order Date." + e.getMessage());
			}
		}
		return null;
	}

	/**
	 * @return the apbAddressReverseConverter
	 */
	public Converter<AddressData, AddressModel> getApbAddressReverseConverter()
	{
		return apbAddressReverseConverter;
	}

	/**
	 * @param apbAddressReverseConverter
	 *           the apbAddressReverseConverter to set
	 */
	public void setApbAddressReverseConverter(final Converter<AddressData, AddressModel> apbAddressReverseConverter)
	{
		this.apbAddressReverseConverter = apbAddressReverseConverter;
	}

	/**
	 * @return the asahiOrderEntryReverseConverter
	 */
	public Converter<OrderEntryData, AbstractOrderEntryModel> getAsahiOrderEntryReverseConverter()
	{
		return asahiOrderEntryReverseConverter;
	}

	/**
	 * @param asahiOrderEntryReverseConverter
	 *           the asahiOrderEntryReverseConverter to set
	 */
	public void setAsahiOrderEntryReverseConverter(
			final Converter<OrderEntryData, AbstractOrderEntryModel> asahiOrderEntryReverseConverter)
	{
		this.asahiOrderEntryReverseConverter = asahiOrderEntryReverseConverter;
	}

	public Converter<OrderEntryData, AbstractOrderEntryModel> getAsahiOrderEntryBasicReverseConverter()
	{
		return asahiOrderEntryBasicReverseConverter;
	}

	public void setAsahiOrderEntryBasicReverseConverter(
			final Converter<OrderEntryData, AbstractOrderEntryModel> asahiOrderEntryBasicReverseConverter)
	{
		this.asahiOrderEntryBasicReverseConverter = asahiOrderEntryBasicReverseConverter;
	}
}
