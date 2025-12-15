/**
 *
 */
package com.sabmiller.facades.b2bunit;

import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2bacceleratorfacades.company.impl.DefaultB2BCommerceUnitFacade;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.SABMOrderTemplateModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.europe1.enums.UserPriceGroup;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.sabmiller.core.b2b.services.SABMDeliveryDateCutOffService;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.b2b.services.SabmOrderTemplateService;
import com.sabmiller.core.comparators.OrderTemplateNameComparator;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.enums.LifecycleStatusType;
import com.sabmiller.core.enums.SAPAvailabilityStatus;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.core.order.SabmB2BOrderService;
import com.sabmiller.facades.cart.SABMCartFacade;
import com.sabmiller.facades.customer.CustomerDataComparator;
import com.sabmiller.facades.customer.CustomerJson;
import com.sabmiller.facades.order.OrderTemplatePopulationOption;
import com.sabmiller.facades.order.data.OrderTemplateData;
import com.sabmiller.facades.smartOrders.json.SmartOrdersJson;
import com.sabmiller.facades.smartOrders.json.SmartOrdersProductsJson;
import com.sabmiller.facades.util.ConfigurableConverter;
import com.sabmiller.facades.util.ConfigurableConverters;


/**
 * The Class DefaultSabmB2BUnitFacade.
 */
public class DefaultSabmB2BUnitFacade extends DefaultB2BCommerceUnitFacade implements SabmB2BCommerceUnitFacade
{


	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSabmB2BUnitFacade.class);

	private UserService userService;

	/** The b2b unit service. */
	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;

	/** The b2b commerce unit service. */
	@Resource(name = "b2bCommerceUnitService")
	private B2BCommerceUnitService b2bCommerceUnitService;

	/** The model service. */
	@Resource(name = "modelService")
	private ModelService modelService;

	/** The sabm b2 b unit populator. */
	@Resource(name = "sabmBasicB2BUnitConverter")
	private Converter<B2BUnitModel, B2BUnitData> b2bUnitConverter;

	/** The order template converter. */
	@Resource(name = "orderTemplateConverter")
	private Converter<SABMOrderTemplateModel, OrderTemplateData> orderTemplateConverter;

	@Resource(name = "sabmOrderTemplateConfiguredConverter")
	private ConfigurableConverter<SABMOrderTemplateModel, OrderTemplateData, OrderTemplatePopulationOption> sabmOrderTemplateConfiguredConverter;

	/** The B2BUnit converter for Business unit page. */
	@Resource(name = "businessUnitConverter")
	private Converter<B2BUnitModel, B2BUnitData> businessUnitConverter;

	/** The order template converter. */
	@Resource(name = "orderTemplateEntryConverter")
	private Converter<SABMOrderTemplateModel, OrderTemplateData> orderTemplateEntryConverter;

	/** The order template service. */
	@Resource(name = "sabmOrderTemplateService")
	private SabmOrderTemplateService orderTemplateService;

	@Resource(name = "orderTemplateNameComparator")
	private OrderTemplateNameComparator orderTemplateNameComparator;

	@Resource(name = "cartFacade")
	private SABMCartFacade cartFacade;

	/** The commerce cart service. */
	@Resource(name = "cartService")
	private CartService cartService;

	/** The commerce cart service. */
	@Resource(name = "commerceCartService")
	private CommerceCartService commerceCartService;

	/** The b2b order service. */
	@Resource(name = "b2bOrderService")
	private SabmB2BOrderService b2bOrderService;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "sabmDeliveryDateCutOffService")
	private SABMDeliveryDateCutOffService deliveryDateCutOffService;

	@Resource(name = "i18NFacade")
	private I18NFacade i18NFacade;


	@Resource(name = "customerConverter")
	private Converter<B2BCustomerModel, CustomerData> b2BCustomerConverter;

	@Resource(name = "commonI18NService")
	private CommonI18NService commonI18NService;

	@Resource(name = "customerJsonConverter")
	private Converter<CustomerModel, CustomerJson> customerJsonConverter;

	@Resource(name = "defaultSabmUnitService")
	private UnitService unitService;


	@Resource(name = "cartModificationConverter")
	private Converter<CommerceCartModification, CartModificationData> cartModificationConverter;

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Resource
	private B2BCustomerService b2bCustomerService;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SabmB2BUnitService#b2bUnitExist(java.lang.String)
	 */
	@Override
	public boolean b2bUnitExist(final String unitId)
	{
		return b2bUnitService.getUnitForUid(unitId) != null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade#createB2BUnit(de.hybris.platform.b2bacceleratorfacades.
	 * order.data.B2BUnitData)
	 */
	@Override
	public void createB2BUnit(final B2BUnitData b2bUnitData)
	{
		LOG.debug("In createB2BUnit() : Invoking b2bUnitService.persist()");

		final B2BUnitModel b2bUnitModel = modelService.create(B2BUnitModel.class);
		b2bUnitService.persist(b2bUnitModel, b2bUnitData);

		postCreateB2BUnit(b2bUnitModel);
	}

	protected void postCreateB2BUnit(final B2BUnitModel b2bUnitModel)
	{
		try
		{
			final UserPriceGroup userPriceGroup = UserPriceGroup.valueOf(b2bUnitModel.getUid());
			modelService.save(userPriceGroup);

			LOG.debug("Creating order template for b2bunit " + b2bUnitModel.getUid());

			orderTemplateService.createEmptyOrderTemplate(b2bUnitModel);
			orderTemplateService.createOrderTempletWithCoreProductRange(b2bUnitModel);
		}
		catch (final Exception e)
		{
			LOG.error(
					"Exception occurred creating order template. Swallowing this exception as we dont intend to stop creation of ZALB B2BUnit for template failures",
					e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade#updateB2BUnit(de.hybris.platform.b2bacceleratorfacades.
	 * order.data.B2BUnitData)
	 */
	@Override
	public void updateB2BUnit(final B2BUnitData b2bUnitData, final B2BUnitModel b2bUnit)
	{
		LOG.debug("In updateB2BUnit() : Invoking b2bUnitService.persist()");
		b2bUnitService.persist(b2bUnit, b2bUnitData);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade#getB2bUnitData(java.lang.String)
	 */
	/*
	 * SAB-535 Method for get B2bUnit of User
	 *
	 * @see com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade#getB2bUnitData(java.lang.String)
	 */
	@Override
	public de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData getB2bUnitData(final String unitId)
	{
		final B2BUnitModel b2bUnitModel = b2bUnitService.getUnitForUid(unitId);


		final B2BUnitModel sessionB2bUnitModel = (B2BUnitModel) sessionService
				.getAttribute(SabmCoreConstants.SESSION_ATTR_B2B_UNIT);

		sessionService.setAttribute(SabmCoreConstants.SESSION_ATTR_B2B_UNIT, b2bUnitModel);

		final de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData b2bUnitData = b2bUnitConverter.convert(b2bUnitModel);
		final List<CustomerData> customerDate = (List<CustomerData>) b2bUnitData.getCustomers();
		if (CollectionUtils.isNotEmpty(customerDate))
		{
			customerDate.sort(new CustomerDataComparator());
		}
		b2bUnitData.setCustomers(customerDate);
		sessionService.setAttribute(SabmCoreConstants.SESSION_ATTR_B2B_UNIT, sessionB2bUnitModel);

		return b2bUnitData;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade#getB2BUnitOrderTemplates()
	 */
	@Override
	public List<OrderTemplateData> getB2BUnitOrderTemplates()
	{
		final B2BUnitModel unitModel = b2bCommerceUnitService.getParentUnit();
		List<OrderTemplateData> orderTemplateDataList = null;
		if (unitModel != null)
		{
			List<SABMOrderTemplateModel> orderTemplates = unitModel.getOrderTemplates();

			if (CollectionUtils.isNotEmpty(orderTemplates))
			{
				if (Config.getBoolean("enable.core.range.template", true))
				{
					orderTemplates = new ArrayList(orderTemplates);
					orderTemplates
							.removeIf(orderTemplate -> Config.getString("core.product.range.template.name", "CUB Core Range").equals(orderTemplate.getName()));
				}
				orderTemplateDataList = ConfigurableConverters.convertAllForOptions(orderTemplates,
						sabmOrderTemplateConfiguredConverter, Collections.singleton(OrderTemplatePopulationOption.BASIC));
			}
		}

		return ListUtils.emptyIfNull(orderTemplateDataList);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade#getB2BUnitOrderTemplateDetail(java.lang.String)
	 */
	@Override
	public OrderTemplateData getB2BUnitOrderTemplateDetail(final String orderCode)
	{
		final B2BUnitModel b2bUnit = b2bCommerceUnitService.getParentUnit();
		OrderTemplateData orderTemplateData = null;

		if (b2bUnit != null)
		{
			try
			{
				final SABMOrderTemplateModel orderTemplate = orderTemplateService.findOrderTemplateByCode(orderCode, b2bUnit);
				removeSapUnavailabilityEntriesFromTemplate(orderTemplate);

				if (orderTemplate != null)
				{
					orderTemplateData = orderTemplateEntryConverter.convert(orderTemplate);

					getSuggestedQuantity(b2bUnit, orderTemplateData);
				}
			}
			catch (final AmbiguousIdentifierException | UnknownIdentifierException e)
			{
				LOG.error("Error getting Order Template with code: " + orderCode + " for B2BUnit: " + b2bUnit, e);
			}
		}

		return orderTemplateData;
	}

	/**
	 * Gets the suggested quantity for a product using the Smart Order logic
	 *
	 * @param b2bUnit
	 * @param orderTemplateData
	 */
	private void getSuggestedQuantity(final B2BUnitModel b2bUnit, final OrderTemplateData orderTemplateData)
	{
		final HashMap<String, Integer> smartOrderQuantity = new HashMap<String, Integer>();
		final SmartOrdersJson smartOrdersJson = b2bOrderService.getPagedOrdersByB2BUnit(b2bUnit, 0, null, null);
		if (null != smartOrdersJson)
		{
			final List<SmartOrdersProductsJson> products = smartOrdersJson.getProducts();
			for (final SmartOrdersProductsJson product : products)
			{
				smartOrderQuantity.put(product.getTitle(), product.getQty());
			}
		}
		for (final OrderEntryData oeData : orderTemplateData.getEntries())
		{
			if (null != oeData.getProduct().getName() && null != smartOrderQuantity.get(oeData.getProduct().getName()))
			{
				oeData.setSuggestedQty(smartOrderQuantity.get(oeData.getProduct().getName()));
			}
			else
			{
				oeData.setSuggestedQty(0);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade#createOrderTemplateByCart(java.lang.String)
	 */
	@Override
	public boolean createOrderTemplateByCart(final String orderName)
	{
		boolean success = false;

		if (checkTemplateNameUsable(orderName))
		{
			final String trimmedName = orderName.trim();
			final SABMOrderTemplateModel createdOrderTemplate = orderTemplateService.createOrderTemplateFromSessionCart(trimmedName);
			if (createdOrderTemplate != null)
			{
				success = true;
			}

		}
		else
		{
			LOG.error("Impossible to create a new OrderTemplate without name");
		}

		return success;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade#addProductToTemplate(java.lang.String,
	 * java.lang.String, java.lang.String, long)
	 */
	@Override
	public boolean addProductToTemplate(final String orderCode, final String productCode, final String fromUnit,
			final Long quantity)
	{
		try
		{
			final CommerceCartModification cartModification = orderTemplateService.addProductToOrderTemplate(orderCode, productCode,
					fromUnit, quantity);

			if (cartModification != null && CommerceCartModificationStatus.SUCCESS.equals(cartModification.getStatusCode()))
			{
				reArrangeSeqToEntries(orderCode);
				return true;
			}
		}
		catch (final CommerceCartModificationException e)
		{
			LOG.warn("Error adding product: " + productCode + " to Template: " + orderCode, e);
		}

		return false;
	}

	/**
	 * @param orderCode
	 */
	private void reArrangeSeqToEntries(final String orderCode)
	{
		final SABMOrderTemplateModel orderTemplate = orderTemplateService.findOrderTemplateByCode(orderCode,
				b2bCommerceUnitService.getParentUnit());

		int sequence = 1;
		final List<AbstractOrderEntryModel> entries = orderTemplate.getEntries();
		final List<Integer> sequenceNos = new ArrayList<Integer>();
		//Collections.sort(entries, Comparator.comparingInt(AbstractOrderEntryModel::getSequenceNumber));
		for (final AbstractOrderEntryModel entry : entries)
		{
			if (null != entry.getSequenceNumber())
			{
				sequenceNos.add(entry.getSequenceNumber());
			}
			else
			{
				sequenceNos.add(entry.getEntryNumber());
			}
		}
		Collections.sort(sequenceNos);
		for (final int sequenceNo : sequenceNos)
		{
			for (final AbstractOrderEntryModel entry : entries)
			{
				if (null != entry.getSequenceNumber() && entry.getSequenceNumber() == sequenceNo)
				{
					entry.setSequenceNumber(sequence++);
					break;
				}
				else if (null == entry.getSequenceNumber() && entry.getEntryNumber() == sequenceNo)
				{
					entry.setSequenceNumber(sequence++);
					break;
				}
			}
		}
		modelService.saveAll(entries);
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade#addOrderTemplateToCart(java.lang.String)
	 */
	@Override
	public boolean addOrderTemplateToCart(final String orderCode)
	{
		final SABMOrderTemplateModel orderTemplate = orderTemplateService.findOrderTemplateByCode(orderCode,
				b2bCommerceUnitService.getParentUnit());

		try
		{
			final CartModel cartModel = cartService.getSessionCart();

			for (final AbstractOrderEntryModel entry : orderTemplate.getEntries())
			{
				if (entry.getProduct().getPurchasable())
				{

					final CommerceCartParameter parameter = new CommerceCartParameter();
					parameter.setEnableHooks(true);
					parameter.setCart(cartModel);
					parameter.setQuantity(entry.getQuantity());
					parameter.setProduct(entry.getProduct());
					parameter.setCreateNewEntry(false);

					commerceCartService.addToCart(parameter);
				}
			}
		}
		catch (CommerceCartModificationException | IllegalArgumentException | ModelSavingException e)
		{
			LOG.error("Error updating cart from order template: " + orderTemplate, e);

			return false;
		}

		return true;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade#addOrderTemplateToCart(java.lang.String,
	 * java.util.List)
	 */
	@Override
	public List<CartModificationData> addOrderTemplateToCart(final String orderCode,
			final List<OrderEntryData> orderTemplateEntryList)
	{
		final SABMOrderTemplateModel orderTemplate = orderTemplateService.findOrderTemplateByCode(orderCode,
				b2bCommerceUnitService.getParentUnit());
		List<CartModificationData> cartModificationDataList = null;
		try
		{
			cartModificationDataList = new ArrayList<CartModificationData>();

			for (final AbstractOrderEntryModel entry : orderTemplate.getEntries())
			{

				final OrderEntryData templateEntry = getEntryFromTemplate(entry.getEntryNumber(), orderTemplateEntryList);
				if (templateEntry != null)
				{
					final Long quantity = templateEntry.getQuantity();

					if (quantity != null && quantity.longValue() > 0)
					{
						final CartModificationData cartModificationData = cartFacade.addToCart(entry.getProduct().getCode(),
								templateEntry.getUnit().getCode(), quantity);
						cartModificationDataList.add(cartModificationData);
					}
				}
			}
		}
		catch (CommerceCartModificationException | IllegalArgumentException | ModelSavingException e)
		{
			LOG.error("Error updating cart from order template: " + orderTemplate, e);

			return cartModificationDataList;
		}

		return cartModificationDataList;
	}

	private OrderEntryData getEntryFromTemplate(final Integer entryNumber, final List<OrderEntryData> orderTemplateEntryList)
	{
		for (final OrderEntryData entry : orderTemplateEntryList)
		{
			if (entryNumber.equals(entry.getEntryNumber()))
			{
				return entry;
			}
		}
		return null;
	}

	/**
	 * Service for remove the order template entry
	 *
	 * @param orderCode
	 *           the order template code
	 * @param entryNumber
	 *           the number of the entry need to be removed
	 * @return OrderTemplateData
	 */
	@Override
	public OrderTemplateData removeProductOrderTemplate(final String orderCode, final long entryNumber)
	{
		final B2BUnitModel b2bUnit = b2bCommerceUnitService.getParentUnit();
		final SABMOrderTemplateModel orderTemplate = orderTemplateService.findOrderTemplateByCode(orderCode, b2bUnit);
		CommerceCartModification modification = null;

		if (orderTemplate == null)
		{
			LOG.error("Error removing entryNumber: " + entryNumber + " from order template: " + orderCode
					+ "Can not find the order template");
			return null;
		}
		final AbstractOrderEntryModel entryToUpdate = cartService.getEntryForNumber(orderTemplate,
				Long.valueOf(entryNumber).intValue());
		if (entryToUpdate != null)
		{
			modification = orderTemplateService.removeEntry(orderTemplate, entryToUpdate);
			if (modification != null && CommerceCartModificationStatus.SUCCESS.equals(modification.getStatusCode()))
			{
				reArrangeSeqToEntries(orderCode);
				return orderTemplateConverter.convert(orderTemplate);
			}
		}
		else
		{
			LOG.error(
					"Error removing entryNumber: " + entryNumber + " from order template: " + orderCode + "Can not find the entry");
		}
		return null;
	}

	/**
	 * Service for update the quantity of order template entry
	 *
	 * @param orderCode
	 *           the order template code
	 * @param entryNumber
	 *           the number of the entry need to be removed
	 * @param quantity
	 *           the new quantity of the entry
	 * @param fromUnit
	 *           the selected Unit(e.g Cases,Layer)
	 * @return boolean the result of update the entry
	 */
	@Override
	public boolean updateProductToTemplate(final String orderCode, final long entryNumber, final long quantity,
			final String fromUnit)
	{

		final SABMOrderTemplateModel orderTemplate = orderTemplateService.findOrderTemplateByCode(orderCode,
				b2bCommerceUnitService.getParentUnit());

		CartModificationData updateCartEntry = null;
		try
		{
			updateCartEntry = updateOrderTemplateEntry(orderTemplate, entryNumber, quantity, fromUnit);

		}
		catch (final CommerceCartModificationException e)
		{
			LOG.warn("Error updating entry: " + entryNumber + " to Template: " + orderCode, e);
		}

		return updateCartEntry != null && CommerceCartModificationStatus.SUCCESS.equals(updateCartEntry.getStatusCode());
	}

	/**
	 * Service for update the quantity of order template entry
	 *
	 * @param cartModel
	 *           the order template model
	 * @param entryNumber
	 *           the number of the entry need to be removed
	 * @param quantity
	 *           the new quantity of the entry
	 * @param fromUnit
	 *           the selected Unit(e.g Cases,Layer)
	 * @return CartModificationData
	 */
	protected CartModificationData updateOrderTemplateEntry(final CartModel cartModel, final long entryNumber, final long quantity,
			final String fromUnit) throws CommerceCartModificationException
	{
		final CartEntryModel cartEntryModel = cartService.getEntryForNumber(cartModel,
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

		final CommerceCartModification modification = orderTemplateService.updateQuantityForOrderTemplateEntry(parameter);

		return cartModificationConverter.convert(modification);

	}

	/**
	 * Service for update the minimum stock on hand of order template entry
	 *
	 * @param orderCode
	 *           the order template code
	 * @param entryNumber
	 *           the number of the entry need to be updated
	 * @param minimumStock
	 *           the new quantity of the entry
	 * @return
	 */
	public boolean updateMinimumStock(final String orderCode, final long entryNumber, final Integer minimumStock)
	{
		final B2BUnitModel unitModel = b2bCommerceUnitService.getParentUnit();

		try
		{
			final SABMOrderTemplateModel orderTemplate = orderTemplateService.findOrderTemplateByCode(orderCode, unitModel);

			final AbstractOrderEntryModel entryToUpdate = cartService.getEntryForNumber(orderTemplate,
					Long.valueOf(entryNumber).intValue());

			entryToUpdate.setMinimumStockOnHand(minimumStock);
			modelService.save(entryToUpdate);
		}
		catch (AmbiguousIdentifierException | UnknownIdentifierException e)
		{
			LOG.warn("Error fetching order template", e);
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade#updateProductToTemplateName(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public boolean updateProductToTemplateName(final String orderCode, final String orderName)
	{
		boolean success = false;
		final B2BUnitModel b2bUnit = b2bCommerceUnitService.getParentUnit();
		SABMOrderTemplateModel findOrderTemplateByName = null;
		final String trimmedName = StringUtils.trim(orderName);
		try
		{
			findOrderTemplateByName = orderTemplateService.findOrderTemplateByName(trimmedName, b2bUnit);
		}
		catch (final UnknownIdentifierException e)
		{
			LOG.debug("No order template found with name: " + trimmedName + " and b2bUnit: " + b2bUnit, e);
		}
		catch (final AmbiguousIdentifierException e)
		{
			LOG.error("More than one order template found with name : " + trimmedName + " and b2bUnit: " + b2bUnit, e);

			return false;
		}

		if (findOrderTemplateByName == null)
		{
			final SABMOrderTemplateModel orderTemplate = orderTemplateService.findOrderTemplateByCode(orderCode,
					b2bCommerceUnitService.getParentUnit());

			orderTemplate.setName(trimmedName);

			try
			{
				modelService.save(orderTemplate);
				success = true;
			}
			catch (final ModelSavingException e)
			{
				LOG.error("Error updating orderTemplate with code: " + orderCode + ", name: " + trimmedName + ", b2bUnit: " + b2bUnit,
						e);
			}
		}
		else if (findOrderTemplateByName.getCode().equals(orderCode))
		{
			success = true;
		}

		return success;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade#moveOrderTemplates(java.lang.String,
	 * java.lang.Boolean)
	 */
	@Override
	public boolean moveOrderTemplates(final String orderCode, final boolean directionUp)
	{
		final B2BUnitModel unitModel = b2bCommerceUnitService.getParentUnit();

		try
		{
			final SABMOrderTemplateModel orderTemplate = orderTemplateService.findOrderTemplateByCode(orderCode, unitModel);

			if (orderTemplate.getSequence() != null)
			{
				final Integer oldSequence = orderTemplate.getSequence();
				final Integer newSequence = oldSequence + 1 * (directionUp ? 1 : -1);

				final List<SABMOrderTemplateModel> templateList = orderTemplateService.findTemplateByB2BUnitAndSequence(unitModel,
						newSequence);

				for (final SABMOrderTemplateModel sabmOrderTemplateModel : templateList)
				{
					sabmOrderTemplateModel.setSequence(oldSequence);
					modelService.save(sabmOrderTemplateModel);
				}

				orderTemplate.setSequence(newSequence);
				modelService.save(orderTemplate);
			}
		}
		catch (AmbiguousIdentifierException | UnknownIdentifierException e)
		{
			LOG.warn("Error fetching order template", e);
			return false;
		}

		return true;
	}

	@Override
	public boolean moveOrderEntry(final String orderCode, final Integer entryNumber, final Integer newEntryNum)

	{
		final B2BUnitModel unitModel = b2bCommerceUnitService.getParentUnit();

		try
		{
			final SABMOrderTemplateModel orderTemplate = orderTemplateService.findOrderTemplateByCode(orderCode, unitModel);

			final int newSequence = (entryNumber > newEntryNum) ? 1 : -1;
			final List<AbstractOrderEntryModel> entries = orderTemplate.getEntries();

			for (final AbstractOrderEntryModel entry : entries)
			{
				final int indexEntryNo = entry.getSequenceNumber() != null ? entry.getSequenceNumber() : entry.getEntryNumber();

				if (indexEntryNo == entryNumber)
				{
					entry.setSequenceNumber(newEntryNum);
				}
				else if ((indexEntryNo > entryNumber && indexEntryNo <= newEntryNum)
						|| (indexEntryNo >= newEntryNum && indexEntryNo < entryNumber))
				{
					entry.setSequenceNumber(indexEntryNo + newSequence);
				}
			}
			modelService.saveAll(entries);

		}
		catch (AmbiguousIdentifierException | UnknownIdentifierException e)
		{
			LOG.warn("Error fetching order template", e);
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade#deleteTemplate(java.lang.String)
	 */
	@Override
	public boolean deleteTemplate(final String orderCode)
	{
		return orderTemplateService.deleteOrderTemplate(orderCode);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade#getB2BUnits()
	 */

	@Override
	public List<B2BUnitData> getB2BUnits()
	{
		final List<B2BUnitData> b2bunitDataList = new ArrayList();
		final B2BCustomerModel currentUser = (B2BCustomerModel) getUserService().getCurrentUser();
		final List<B2BUnitModel> groups = getUnitsForZADP(currentUser);
		if (groups != null && !groups.isEmpty())
		{
			for (final B2BUnitModel eachB2BUnit : groups)
			{
				b2bunitDataList.add(b2bUnitConverter.convert(eachB2BUnit));
			}
			return b2bunitDataList;
		}


		//Get Parent b2bUnit from the CurrentUse
		final B2BUnitModel b2bParentmodel = b2bCommerceUnitService.getParentUnit();
		if (b2bParentmodel != null)
		{

			b2bunitDataList.add(b2bUnitConverter.convert(b2bParentmodel));
			//Get all the b2bUnits from the CurrentUse
			for (final B2BUnitModel b2bmodel : b2bCommerceUnitService.getBranch())
			{
				//Does not contain the parent b2bUnit
				if (!b2bParentmodel.equals(b2bmodel))
				{
					//add sub b2bUnit to the List
					b2bunitDataList.add(b2bUnitConverter.convert(b2bmodel));
				}
			}
			return b2bunitDataList;
		}
		LOG.warn("Error unable to found the B2BUnitModel");

		return b2bunitDataList;
	}

	private List<B2BUnitModel> getUnitsForZADP(final B2BCustomerModel customer)
	{
		final B2BUnitModel zadpB2BUnit = b2bUnitService.findTopLevelB2BUnit(customer);
		if (zadpB2BUnit != null)
		{
			final List<B2BUnitModel> groups = new ArrayList<>();
			groups.add(zadpB2BUnit);
			for (final PrincipalModel model : SetUtils.emptyIfNull(zadpB2BUnit.getMembers()))
			{
				if (model instanceof B2BUnitModel)
				{
					groups.add((B2BUnitModel) model);
				}
			}
			return groups;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade#getOrderTemplatesNameSorted(boolean)
	 */
	@Override
	public List<OrderTemplateData> getOrderTemplatesNameSorted(final boolean sortAsc)
	{
		final B2BUnitModel unitModel = b2bCommerceUnitService.getParentUnit();
		List<OrderTemplateData> orderTemplateDataList = null;
		if (unitModel != null)
		{
			final List<SABMOrderTemplateModel> orderTemplates = unitModel.getOrderTemplates();

			if (CollectionUtils.isNotEmpty(orderTemplates))
			{
				final List<SABMOrderTemplateModel> orderTemplateList = new ArrayList<>(orderTemplates);
				/*
				 * Collections.sort(orderTemplateList, orderTemplateNameComparator); if (!sortAsc) {
				 * Collections.reverse(orderTemplateList); }
				 *
				 * orderTemplateDataList = Converters.convertAll(orderTemplateList, orderTemplateConverter);
				 */
				orderTemplateDataList = Converters.convertAll(orderTemplateList, orderTemplateConverter);
				final Collator collator = Collator.getInstance(Locale.US);
				collator.setStrength(Collator.PRIMARY);

				if (!sortAsc)
				{
					orderTemplateDataList.sort(Comparator.comparing(OrderTemplateData::getName, collator.reversed()));
				}
				if (sortAsc)
				{
					orderTemplateDataList.sort(Comparator.comparing(OrderTemplateData::getName, collator.reversed()));
					Collections.reverse(orderTemplateDataList);
				}
				//Collections.sort(orderTemplateList, orderTemplateNameComparator);
			}
		}

		return ListUtils.emptyIfNull(orderTemplateDataList);
	}


	@Override
	public String getCurrentB2BUnitId()
	{
		if (b2bCommerceUnitService.getParentUnit() != null)
		{
			return b2bCommerceUnitService.getParentUnit().getUid();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade#isCutOffTimeExceeded(de.hybris.platform.b2b.model.
	 * B2BUnitModel , java.util.Date)
	 */
	@Override
	public boolean isCutOffTimeExceeded(final B2BUnitModel b2bUnitModel)
	{
		return deliveryDateCutOffService.isCutOffTimeExceeded(b2bUnitModel);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade#isCutOffTimeExceeded()
	 */
	@Override
	public boolean isCutOffTimeExceeded()
	{
		final CartModel cart = cartService.getSessionCart();

		if (cart == null)
		{
			return true;
		}

		return !deliveryDateCutOffService.isValidDeliveryDate(cart.getRequestedDeliveryDate());
	}


	/**
	 * Creates the empty order template by Name.
	 *
	 * @param templateName
	 *           the template name
	 * @return true, if successful
	 */
	@Override
	public String createEmptyOrderTemplateByName(final String templateName)
	{
		String templateResultCode = null;

		if (checkTemplateNameUsable(templateName))
		{
			final String trimmedName = templateName.trim();
			final SABMOrderTemplateModel createdOrderTemplate = orderTemplateService
					.createEmptyOrderTemplateForCurrentUnit(trimmedName);
			if (createdOrderTemplate != null)
			{
				templateResultCode = createdOrderTemplate.getCode();
			}

		}
		else
		{
			LOG.error("Impossible to create a new OrderTemplate without name");
		}

		return templateResultCode;
	}

	/**
	 * Check the templateName is existed or not
	 *
	 * @param templateName
	 * @return
	 */
	private boolean checkTemplateNameUsable(final String templateName)
	{
		if (StringUtils.isNotEmpty(templateName))
		{
			final B2BUnitModel b2bUnit = b2bCommerceUnitService.getParentUnit();

			if (b2bUnit != null)
			{
				SABMOrderTemplateModel orderTemplate = null;
				final String trimmedName = templateName.trim();

				try
				{
					orderTemplate = orderTemplateService.findOrderTemplateByName(trimmedName, b2bUnit);
				}
				catch (final AmbiguousIdentifierException e)
				{
					LOG.error("Duplicate Order Template with name: " + trimmedName + " for B2BUnit: " + b2bUnit, e);
				}
				catch (final UnknownIdentifierException e)
				{
					LOG.debug("No order template found with name: " + trimmedName + " for B2BUnit: " + b2bUnit, e);
				}
				if (orderTemplate == null)
				{
					return true;
				}
			}
		}
		else
		{
			LOG.error("Impossible to check the template name without name");
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade#createOrderTemplateByOrder(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public boolean createOrderTemplateByOrder(final String orderName, final String orderCode)
	{
		boolean success = false;

		if (StringUtils.isNotEmpty(orderName))
		{
			final B2BUnitModel b2bUnit = b2bCommerceUnitService.getParentUnit();

			if (b2bUnit != null)
			{
				SABMOrderTemplateModel orderTemplate = null;
				final String trimmedName = orderName.trim();

				try
				{
					orderTemplate = orderTemplateService.findOrderTemplateByName(trimmedName, b2bUnit);
				}
				catch (final AmbiguousIdentifierException e)
				{
					LOG.error("Duplicate Order Template with name: " + trimmedName + " for B2BUnit: " + b2bUnit, e);
				}
				catch (final UnknownIdentifierException e)
				{
					LOG.debug("No order template found with name: " + trimmedName + " for B2BUnit: " + b2bUnit, e);
				}

				if (orderTemplate == null)
				{
					final SABMOrderTemplateModel createdOrderTemplate = orderTemplateService
							.createOrderTemplateByOrderCode(trimmedName, orderCode);
					if (createdOrderTemplate != null)
					{
						success = true;
					}
				}
			}
		}
		else
		{
			LOG.error("Impossible to create a new OrderTemplate without name");
		}

		return success;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade#getSubB2BUnitForZADP()
	 */
	@Override
	public List<RegionData> getSubB2BUnitForZADP()
	{
		final B2BCustomerModel currentCustomer = (B2BCustomerModel) getUserService().getCurrentUser();
		final B2BUnitModel b2bUnit = b2bUnitService.findTopLevelB2BUnit(currentCustomer);
		if (null != b2bUnit)
		{
			final CountryModel country = b2bUnit.getCountry();
			if (null != country)
			{
				return getRegions(b2bUnit, country);
			}
			LOG.warn("This ZADP B2BUnit[{}] not set the country information.", b2bUnit);
		}
		LOG.warn("This current customer[{}] not ZADP B2BUnit", currentCustomer);
		return null;
	}

	/**
	 * @param b2bUnit
	 * @param country
	 */
	private List<RegionData> getRegions(final B2BUnitModel b2bUnit, final CountryModel country)
	{
		final List<RegionData> regions = i18NFacade.getRegionsForCountryIso(country.getIsocode());

		final Set<PrincipalModel> allMembers = b2bUnit.getMembers();
		for (final PrincipalModel principal : SetUtils.emptyIfNull(allMembers))
		{
			if (principal instanceof B2BUnitModel)
			{
				final B2BUnitModel subB2BUnit = (B2BUnitModel) principal;
				final AddressModel address = getAddressFormB2BUnit(subB2BUnit);
				addB2BUnitToRegion(regions, subB2BUnit, address);
			}
		}

		return regions;
	}

	/**
	 * if don't set ContactAddress, the default to the first record from the address attribute
	 *
	 * @param subB2BUnit
	 *           This is B2BUnitModel
	 * @return Address of the B2BUnit
	 */
	private AddressModel getAddressFormB2BUnit(final B2BUnitModel subB2BUnit)
	{
		AddressModel address = subB2BUnit.getContactAddress();
		if (null == address)
		{
			final Collection<AddressModel> addresses = subB2BUnit.getAddresses();
			if (CollectionUtils.isNotEmpty(addresses))
			{
				address = addresses.iterator().next();
			}
			else
			{
				LOG.warn("This B2BUnit[{}] not set the address information.", subB2BUnit);
			}
		}
		return address;
	}

	/**
	 * B2BUnit is added in the Region
	 *
	 * @param regions
	 *           This is Region collection
	 * @param subB2BUnit
	 *           This is B2BUnitModel
	 * @param address
	 *           This is AddressModel
	 */
	private void addB2BUnitToRegion(final List<RegionData> regions, final B2BUnitModel subB2BUnit, final AddressModel address)
	{
		if (null != address && null != address.getRegion())
		{
			for (final RegionData region : ListUtils.emptyIfNull(regions))
			{
				addB2BUnitByIsocode(subB2BUnit, address, region);
			}
		}
		else
		{
			LOG.warn("This B2BUnit[{}] not set the address info or There is not set region content in the address[{}].", subB2BUnit,
					address);
		}
	}

	/**
	 * According to the comparison of Isocode region
	 *
	 * @param b2bUnit
	 *           This is B2BUnitModel
	 * @param address
	 *           This is AddressModel
	 * @param region
	 *           This is RegionData
	 */
	private void addB2BUnitByIsocode(final B2BUnitModel b2bUnit, final AddressModel address, final RegionData region)
	{
		if (region.getIsocode().equals(address.getRegion().getIsocode()))
		{
			if (CollectionUtils.isEmpty(region.getB2bUnits()))
			{
				region.setB2bUnits(Lists.newArrayList());
			}
			region.getB2bUnits().add(b2bUnitConverter.convert(b2bUnit));
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade#getB2BUnitByCustomer(java.lang.String)
	 */
	@Override
	public List<B2BUnitData> getB2BUnitsByCustomer(final String uid)
	{
		if (StringUtils.isBlank(uid))
		{
			LOG.warn("No Customer UID was provided.");
			return null;
		}
		//final CustomerModel customer = b2bCommerceUnitService.getCustomerForUid(uid);
		final CustomerModel customer = (CustomerModel) b2bCustomerService.getUserForUID(uid);
		final Set<PrincipalGroupModel> principalGroups = customer.getGroups();
		final List<B2BUnitData> b2bUnits = Lists.newArrayList();
		for (final PrincipalGroupModel principalGroup : SetUtils.emptyIfNull(principalGroups))
		{

			if (principalGroup instanceof AsahiB2BUnitModel)
			{
				continue;
			}

			if (principalGroup instanceof B2BUnitModel)
			{
				final B2BUnitModel b2bUnit = (B2BUnitModel) principalGroup;
				if (BooleanUtils.isTrue(b2bUnit.getActive()))
				{
					b2bUnits.add(b2bUnitConverter.convert(b2bUnit));
				}
				else
				{
					LOG.warn("This B2BUnit[{}] is not available", b2bUnit);
				}
			}
		}
		return b2bUnits;
	}

	public List<B2BUnitData> getB2BUnitsforBUssinesspage(final String uid)
	{
		if (StringUtils.isBlank(uid))
		{
			LOG.warn("No Customer UID was provided.");
			return null;
		}
		//final CustomerModel customer = b2bCommerceUnitService.getCustomerForUid(uid);
		final CustomerModel customer = (CustomerModel) b2bCustomerService.getUserForUID(uid);
		final Set<PrincipalGroupModel> principalGroups = customer.getGroups();
		final List<B2BUnitData> b2bUnits = Lists.newArrayList();
		for (final PrincipalGroupModel principalGroup : SetUtils.emptyIfNull(principalGroups))
		{
			if (principalGroup instanceof AsahiB2BUnitModel)
			{
				continue;
			}
			if (principalGroup instanceof B2BUnitModel)
			{
				final B2BUnitModel b2bUnitModel = (B2BUnitModel) principalGroup;
				if (BooleanUtils.isTrue(b2bUnitModel.getActive()))
				{
					try
					{
						B2BUnitData branchUnitData = new B2BUnitData();
						//						INC0534163: ALH customers cannot get to the business units page
						branchUnitData = businessUnitConverter.convert(b2bUnitModel);
						b2bUnits.add(branchUnitData);
					}
					catch (final Exception e)
					{
						LOG.error("Exception Occured while converting B2BUnit for business unit page", e);
					}

				}
				else
				{
					LOG.warn("This B2BUnit[{}] is not available", b2bUnitModel);
				}
			}
		}
		return b2bUnits;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade#b2bUnitBelongsToCurrentCustomer(java.lang.String)
	 */
	@Override
	public boolean b2bUnitBelongsToCurrentCustomer(final String b2bUnit)
	{
		//If the parameter is empty return false.
		if (StringUtils.isEmpty(b2bUnit))
		{
			return false;
		}

		//Getting the B2BUnit from the String parameter.
		final B2BUnitModel unitModel = b2bUnitService.getUnitForUid(b2bUnit);

		if (unitModel == null)
		{
			return false;
		}

		//Getting the customer from session.
		final UserModel currentUser = userService.getCurrentUser();

		if (!(currentUser instanceof B2BCustomerModel))
		{
			return false;
		}

		final List<B2BUnitModel> unitsForZADP = getUnitsForZADP((B2BCustomerModel) currentUser);
		final List<PrincipalGroupModel> listToCheck = new ArrayList<>();

		if (unitsForZADP != null)
		{
			listToCheck.addAll(unitsForZADP);
		}

		if (currentUser.getGroups() != null)
		{
			for (final PrincipalGroupModel principal : currentUser.getGroups())
			{
				if (principal instanceof AsahiB2BUnitModel)
				{
					continue;
				}
				listToCheck.add(principal);
			}

		}

		for (final PrincipalGroupModel group : listToCheck)
		{
			//Using instanceof to improve performance avoiding "eauals" calculation of other Model
			if (group instanceof B2BUnitModel && group.equals(unitModel))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public List<B2BUnitData> getZalbB2BUnitsByCustomer(final String uid)
	{
		if (StringUtils.isBlank(uid))
		{
			LOG.warn("No Customer UID was provided.");
			return null;
		}
		//final CustomerModel customer = b2bCommerceUnitService.getCustomerForUid(uid);
		final CustomerModel customer = (CustomerModel) b2bCustomerService.getUserForUID(uid);
		final Set<PrincipalGroupModel> principalGroups = customer.getGroups();
		final List<B2BUnitData> b2bUnits = Lists.newArrayList();
		for (final PrincipalGroupModel principalGroup : SetUtils.emptyIfNull(principalGroups))
		{
			if (principalGroup instanceof AsahiB2BUnitModel)
			{
				continue;
			}

			if (principalGroup instanceof B2BUnitModel)
			{
				final B2BUnitModel b2bUnit = (B2BUnitModel) principalGroup;
				if (BooleanUtils.isTrue(b2bUnit.getActive()))
				{
					if (SabmCoreConstants.ZALB.equals(b2bUnit.getAccountGroup()))
					{
						b2bUnits.add(b2bUnitConverter.convert(b2bUnit));
					}
					else
					{
						LOG.warn("This B2BUnit[{}] is not ZALB", b2bUnit);
					}
				}
				else
				{
					LOG.warn("This B2BUnit[{}] is not available", b2bUnit);
				}
			}
		}
		return b2bUnits;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade#isCurrentB2BUnitExistOfCustomer(java.lang.String)
	 */
	@Override
	public boolean isCurrentB2BUnitExistOfCustomer(final String uid)
	{
		final List<B2BUnitData> b2bUnits = getB2BUnitsByCustomer(uid);
		final List<B2BUnitData> currentB2BUnits = getB2BUnits();
		for (final B2BUnitData b2bUnit : ListUtils.emptyIfNull(b2bUnits))
		{
			for (final B2BUnitData currentB2BUnit : currentB2BUnits)
			{
				if (b2bUnit.isActive() && b2bUnit.getUid().equals(currentB2BUnit.getUid()))
				{
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade#getZADPB2BUnitByCurrentCustomer()
	 */
	@Override
	public de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData getZADPB2BUnitByCurrentCustomer()
	{
		final B2BCustomerModel currentCustomer = (B2BCustomerModel) getUserService().getCurrentUser();
		final B2BUnitModel b2bUnit = b2bUnitService.findTopLevelB2BUnit(currentCustomer);
		if (null != b2bUnit)
		{
			return b2bUnitConverter.convert(b2bUnit);
		}
		LOG.warn("This current customer[{}] not ZADP B2BUnit", currentCustomer);
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade#getB2bUnitDataExceptZADPUser(java.lang.String)
	 */
	@Override
	public B2BUnitData getB2bUnitDataExceptZADPUser(final String unitId)
	{
		final B2BUnitModel b2bUnitModel = b2bUnitService.getUnitForUid(unitId);
		B2BUnitData b2bUnitData = new B2BUnitData();
		if (b2bUnitModel != null)
		{
			//			INC0534163: ALH customers cannot get to the business units page
			try
			{
				b2bUnitData = businessUnitConverter.convert(b2bUnitModel);
			}
			catch (final Exception e)
			{
				LOG.error("Exception Occured while converting B2BUnit for business unit page", e);
			}
			//List Customer except ZADP user
			final List<B2BCustomerModel> customerList = b2bUnitService.getCustmoersExceptZADP(b2bUnitModel);
			final List<CustomerData> customerData = new ArrayList<CustomerData>();
			for (final B2BCustomerModel customer : customerList)
			{
				customerData.add(getB2BCustomerConverter().convert(customer));
			}
			customerData.sort(new CustomerDataComparator());
			b2bUnitData.setCustomers(customerData);
		}
		return b2bUnitData;
	}

	/**
	 * @param customerData
	 * @param b2bUnitModel
	 */
	@Override
	public void setActiveStatus(final Collection<CustomerData> customerData, final String b2bUnitId)
	{
		final B2BUnitModel b2bUnitModel = b2bUnitService.getUnitForUid(b2bUnitId);
		final Collection<String> disabledUsers = b2bUnitModel.getCubDisabledUsers();
		for (final CustomerData customer : customerData)
		{
			if (BooleanUtils.isTrue(customer.isActive()))
			{
				if (disabledUsers.contains(customer.getUid()))
				{
					customer.setActive(Boolean.FALSE);
				}
				else
				{
					customer.setActive(Boolean.TRUE);
				}
			}
			else
			{
				customer.setActive(Boolean.FALSE);
			}
		}


	}

	@Override
	public B2BUnitData getB2bUnitDataOnlyForInvoiceUser(final String unitId, final boolean bdeUser)
	{

		final B2BUnitModel b2bUnitModel = b2bUnitService.getUnitForUid(unitId);

		B2BUnitData b2bUnitData = new B2BUnitData();
		if (b2bUnitModel != null)
		{
			try
			{
				b2bUnitData = businessUnitConverter.convert(b2bUnitModel);
			}
			catch (final Exception e)
			{
				LOG.error("Exception Occured while converting B2BUnit for business unit page", e);
			}
			//List Customer who has permission to see invoice or primary admin
			final List<B2BCustomerModel> customers = b2bUnitService.getCustomersWithInvoicePermission(b2bUnitModel);


			final List<CustomerData> customerData = new ArrayList<CustomerData>();
			for (final B2BCustomerModel customer : customers)
			{
				customerData.add(getB2BCustomerConverter().convert(customer));
			}

			/*
			 * if BDE login, current user will be dummy email like: bde-testsampleb2b@cub.com.au
			 */
			// if (!bdeUser){
			b2bUnitData.setCustomers(
					customerData.stream().filter(customer -> !customer.getUid().equals(getUserService().getCurrentUser().getUid()))
							.sorted(new CustomerDataComparator()).collect(Collectors.toList()));
			// }

			// customerData.sort(new CustomerDataComparator());

		}
		return b2bUnitData;


	}


	/**
	 * @return the userService
	 */
	@Override
	public UserService getUserService()
	{
		return userService;
	}

	/**
	 * @param userService
	 *           the userService to set
	 */
	@Override
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	@Override
	public List<B2BUnitData> getEntireB2bUnits()
	{
		boolean zadpUser = false;
		final UserModel currentUser = userService.getCurrentUser();
		final Set<PrincipalGroupModel> groups = currentUser.getGroups();
		for (final PrincipalGroupModel group : groups)
		{

			if (group instanceof AsahiB2BUnitModel)
			{
				continue;
			}

			if (group instanceof B2BUnitModel)
			{
				final B2BUnitModel businessUnit = (B2BUnitModel) group;
				if (SabmCoreConstants.ZADP.equals(businessUnit.getAccountGroup()))
				{
					zadpUser = true;
					break;
				}
			}
		}
		if (zadpUser)
		{
			final List<B2BUnitData> b2bUnits = new ArrayList<B2BUnitData>();
			final Collection<? extends B2BUnitModel> organization = b2bCommerceUnitService.getOrganization();
			for (final B2BUnitModel b2bUnitModel : organization)
			{
				//				INC0534163: ALH customers cannot get to the business units page
				try
				{
					B2BUnitData branchUnitData = new B2BUnitData();
					branchUnitData = businessUnitConverter.convert(b2bUnitModel);
					b2bUnits.add(branchUnitData);
				}
				catch (final Exception e)
				{
					LOG.error("Exception Occured while converting B2BUnit for business unit page", e);
				}

			}
			return b2bUnits;
		}
		return getB2BUnitsforBUssinesspage(currentUser.getUid());
	}

	@Override
	public de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData getRootB2bUnit()
	{
		return b2bUnitConverter.convert(b2bCommerceUnitService.getRootUnit());
	}

	@Override
	public List<CustomerData> getUsersWithZADP()
	{
		List<CustomerData> returnValue = new ArrayList<CustomerData>();

		boolean zadpUser = false;
		final UserModel currentUser = userService.getCurrentUser();
		final Set<PrincipalGroupModel> groups = currentUser.getGroups();
		final List<B2BUnitModel> businessUnits = new ArrayList<B2BUnitModel>();
		for (final PrincipalGroupModel group : groups)
		{
			if (group instanceof AsahiB2BUnitModel)
			{
				continue;
			}

			if (group instanceof B2BUnitModel)
			{
				final B2BUnitModel businessUnit = (B2BUnitModel) group;

				if (SabmCoreConstants.ZADP.equals(businessUnit.getAccountGroup()))
				{
					zadpUser = true;
				}
				else
				{
					businessUnits.add(businessUnit);
				}
			}
		}
		LOG.info("DefaultSabmB2BUnitFacade getUsersWithZADP zadpUser: " + zadpUser);
		if (zadpUser)
		{
			final List<B2BCustomerModel> customers = b2bUnitService
					.getNoneZADPUsersWithSpecifiedBusinessUnit(b2bCommerceUnitService.getRootUnit(), userService.getCurrentUser());
			LOG.info("DefaultSabmB2BUnitFacade getUsersWithZADP customers: " + customers);
			//PRB0042191 - ALH account issue fix
			//returnValue = Converters.convertAll(customers, getB2BCustomerConverter());
			try
			{
				final List<B2BCustomerModel> zadpCustomers = b2bUnitService
						.getZADPUsersByB2BUnit(b2bCommerceUnitService.getRootUnit());

				if (CollectionUtils.isNotEmpty(zadpCustomers))
				{
					LOG.info(
							"SecondList:" + zadpCustomers.stream().distinct().map(cust -> cust.getUid()).collect(Collectors.toList()));
					customers.addAll(zadpCustomers);
				}
				returnValue = Converters.convertAll(customers, getB2BCustomerConverter());

			}
			catch (final Exception e)
			{
				LOG.error("Error while getting ZADP users", e);
			}
		}
		else
		{
			returnValue = new ArrayList<CustomerData>();
			final HashMap<String, CustomerData> customers = new HashMap<>();

			for (final B2BUnitModel businessUnit : businessUnits)
			{
				final B2BUnitData b2bUnitDataExceptZADPUser = getB2bUnitDataExceptZADPUser(businessUnit.getUid());
				if (b2bUnitDataExceptZADPUser != null && CollectionUtils.isNotEmpty(b2bUnitDataExceptZADPUser.getCustomers()))
				{
					for (final CustomerData customer : b2bUnitDataExceptZADPUser.getCustomers())
					{
						if (!customers.containsKey(customer.getUid()))
						{
							customers.put(customer.getUid(), customer);
						}
					}
				}
			}

			final Iterator<Entry<String, CustomerData>> iterator = customers.entrySet().iterator();
			while (iterator.hasNext())
			{
				returnValue.add(iterator.next().getValue());
			}
		}

		returnValue.sort(new CustomerDataComparator());
		LOG.info("DefaultSabmB2BUnitFacade getUsersWithZADP sorted returnValue: " + returnValue);
		return returnValue;
	}


	@Override
	public List<CustomerData> getNoneZADPUsers()
	{
		List<CustomerData> returnValue = null;

		boolean zadpUser = false;
		final UserModel currentUser = userService.getCurrentUser();
		final Set<PrincipalGroupModel> groups = currentUser.getGroups();
		final List<B2BUnitModel> businessUnits = new ArrayList<B2BUnitModel>();
		for (final PrincipalGroupModel group : groups)
		{
			if (group instanceof AsahiB2BUnitModel)
			{
				continue;
			}

			if (group instanceof B2BUnitModel)
			{
				final B2BUnitModel businessUnit = (B2BUnitModel) group;


				if (SabmCoreConstants.ZADP.equals(businessUnit.getAccountGroup()))
				{
					zadpUser = true;
				}
				else
				{
					businessUnits.add(businessUnit);
				}
			}
		}


		if (zadpUser)
		{
			final List<B2BCustomerModel> customers = b2bUnitService
					.getNoneZADPUsersWithSpecifiedBusinessUnit(b2bCommerceUnitService.getRootUnit(), userService.getCurrentUser());
			returnValue = Converters.convertAll(customers, getB2BCustomerConverter());
		}
		else
		{
			returnValue = new ArrayList<CustomerData>();
			final HashMap<String, CustomerData> customers = new HashMap<>();

			for (final B2BUnitModel businessUnit : businessUnits)
			{
				final B2BUnitData b2bUnitDataExceptZADPUser = getB2bUnitDataExceptZADPUser(businessUnit.getUid());
				if (b2bUnitDataExceptZADPUser != null && CollectionUtils.isNotEmpty(b2bUnitDataExceptZADPUser.getCustomers()))
				{
					for (final CustomerData customer : b2bUnitDataExceptZADPUser.getCustomers())
					{
						if (!customers.containsKey(customer.getUid()))
						{
							customers.put(customer.getUid(), customer);
						}
					}
				}
			}

			final Iterator<Entry<String, CustomerData>> iterator = customers.entrySet().iterator();
			while (iterator.hasNext())
			{
				returnValue.add(iterator.next().getValue());
			}
		}

		returnValue.sort(new CustomerDataComparator());

		return returnValue;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade#isCurrentB2BUnitExistOfCustomer(java.lang.String)
	 */
	@Override
	public boolean isCurrentB2BUnitExistOfUid(final String uid)
	{
		//final CustomerModel customer = b2bCommerceUnitService.getCustomerForUid(uid);
		final CustomerModel customer = (CustomerModel) b2bCustomerService.getUserForUID(uid);
		final Set<PrincipalGroupModel> principalGroups = customer.getGroups();

		final B2BCustomerModel currentCustomer = (B2BCustomerModel) getUserService().getCurrentUser();
		final List<B2BUnitModel> zadpUnitList = b2bUnitService.findCustomerTopLevelUnit(currentCustomer);
		final B2BUnitModel topB2BUnit = (zadpUnitList.isEmpty() ? null : zadpUnitList.get(0));
		if (null != topB2BUnit)
		{
			LOG.info("the top level unit of current customer [{}] is [{}]", currentCustomer, topB2BUnit);
			for (final PrincipalGroupModel principalGroup : SetUtils.emptyIfNull(principalGroups))
			{
				if (principalGroup instanceof AsahiB2BUnitModel)
				{
					continue;
				}
				if (principalGroup instanceof B2BUnitModel)
				{
					final B2BUnitModel b2bUnit = (B2BUnitModel) principalGroup;
					final Set<B2BUnitModel> currentB2BUnits = b2bUnitService.getBranch(topB2BUnit);
					LOG.info("currentB2BUnits:" + Arrays.asList(CollectionUtils.emptyIfNull(currentB2BUnits).stream()
							.map(unit -> unit.getUid()).collect(Collectors.toList())));
					for (final B2BUnitModel currentB2BUnit : CollectionUtils.emptyIfNull(currentB2BUnits))
					{
						if (BooleanUtils.isTrue(b2bUnit.getActive()) && b2bUnit.equals(currentB2BUnit))
						{
							return true;
						}
					}
				}
			}
		}
		else
		{
			LOG.info("Cannot find the top level unit of current customer [{}]", currentCustomer);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade#getTopLevelB2BUnit()
	 */
	@Override
	public de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData getTopLevelB2BUnit()
	{
		final B2BCustomerModel currentCustomer = (B2BCustomerModel) getUserService().getCurrentUser();
		final List<B2BUnitModel> zadpUnitList = b2bUnitService.findCustomerTopLevelUnit(currentCustomer);
		final B2BUnitModel b2bUnit = (zadpUnitList.isEmpty() ? null : zadpUnitList.get(0));
		//final B2BUnitModel b2bUnit = b2bUnitService.findTopLevelB2BUnit(currentCustomer);
		if (null != b2bUnit)
		{
			return b2bUnitConverter.convert(b2bUnit);
		}
		LOG.warn("This current customer[{}] not ZADP B2BUnit", currentCustomer);
		return null;
	}

	//SABMC- 1888
	@Override
	public List<de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData> getBranchesForCustomer(final String uid)
	{

		if (StringUtils.isBlank(uid))
		{
			LOG.warn("No Customer UID was provided.");
			return null;
		}
		//final B2BCustomerModel customer = b2bCommerceUnitService.getCustomerForUid(uid);
		final B2BCustomerModel customer = (B2BCustomerModel) b2bCustomerService.getUserForUID(uid);
		final B2BUnitModel zadpB2BUnit = b2bUnitService.findTopLevelB2BUnit(customer);
		final boolean customerBelongsToZADP = (zadpB2BUnit != null);
		if (customerBelongsToZADP)
		{
			final Set<PrincipalGroupModel> principalGroups = new HashSet<PrincipalGroupModel>();
			for (final PrincipalModel model : SetUtils.emptyIfNull(zadpB2BUnit.getMembers()))
			{
				if (model instanceof B2BUnitModel)
				{
					principalGroups.add((B2BUnitModel) model);
				}
			}
			return populatePrincipalGroups(principalGroups);
		}
		else
		{
			final Set<PrincipalGroupModel> principalGroups = new HashSet<PrincipalGroupModel>();
			for (final PrincipalGroupModel principal : customer.getGroups())
			{
				if (principal instanceof AsahiB2BUnitModel)
				{
					continue;
				}
				principalGroups.add(principal);
			}
			return populatePrincipalGroups(principalGroups);
		}
	}

	private List<de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData> populatePrincipalGroups(
			final Set<PrincipalGroupModel> principalGroups)
	{
		final List<de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData> b2bUnits = Lists.newArrayList();
		for (final PrincipalGroupModel principalGroup : SetUtils.emptyIfNull(principalGroups))
		{
			if (principalGroup instanceof B2BUnitModel)
			{
				final B2BUnitModel b2bUnit = (B2BUnitModel) principalGroup;
				if (BooleanUtils.isTrue(b2bUnit.getActive()))
				{
					if (SabmCoreConstants.ZALB.equals(b2bUnit.getAccountGroup()))
					{
						b2bUnits.add(b2bUnitConverter.convert(b2bUnit));
					}
					else
					{
						LOG.warn("This B2BUnit[{}] is not ZALB", b2bUnit);
					}
				}
				else
				{
					LOG.warn("This B2BUnit[{}] is not available", b2bUnit);
				}
			}
		}
		return b2bUnits;
	}


	/**
	 * @param b2bUnitService
	 *           the b2bUnitService to set
	 */
	public void setB2bUnitService(final SabmB2BUnitService b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

	/**
	 * @param b2bCommerceUnitService
	 *           the b2bCommerceUnitService to set
	 */
	public void setB2bCommerceUnitService(final B2BCommerceUnitService b2bCommerceUnitService)
	{
		this.b2bCommerceUnitService = b2bCommerceUnitService;
	}

	/**
	 * @param b2bUnitConverter
	 *           the b2BUnitConverter to set
	 */
	public void setB2bUnitConverter(final Converter<B2BUnitModel, B2BUnitData> b2bUnitConverter)
	{
		this.b2bUnitConverter = b2bUnitConverter;
	}

	/*
	 *
	 * Remove entries from Template for product sap status as X9
	 *
	 * @see com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade#removeSapUnavailabilityEntriesFromTemplate(de.hybris.
	 * platform.core.model.order.SABMOrderTemplateModel)
	 */
	@Override
	public void removeSapUnavailabilityEntriesFromTemplate(final SABMOrderTemplateModel orderTemplateModel)
	{
		try
		{

			final List<AbstractOrderEntryModel> removalEntries = new ArrayList<AbstractOrderEntryModel>();
			if (CollectionUtils.isNotEmpty(orderTemplateModel.getEntries()))
			{
				final String sapUnAvailabilityList = configurationService.getConfiguration()
						.getString("template.sap.unAvailability.satuses", "");
				for (final AbstractOrderEntryModel entry : orderTemplateModel.getEntries())
				{
					final SABMAlcoholVariantProductMaterialModel product = (SABMAlcoholVariantProductMaterialModel) entry.getProduct();
					if (product != null && product.getBaseProduct() != null)
					{
						final SABMAlcoholVariantProductEANModel eanModel = (SABMAlcoholVariantProductEANModel) product.getBaseProduct();
						final SAPAvailabilityStatus sapAvailabilityStatus = eanModel.getSapAvailabilityStatus();
						if (sapAvailabilityStatus != null && sapUnAvailabilityList.contains(sapAvailabilityStatus.getCode()))
						{
							removalEntries.add(entry);
						}
						else if (!eanModel.getApprovalStatus().equals(ArticleApprovalStatus.APPROVED)
								|| !eanModel.getLifecycleStatus().equals(LifecycleStatusType.LIVE))
						{
							removalEntries.add(entry);
						}
					}
				}
			}
			if (CollectionUtils.isNotEmpty(removalEntries))
			{
				modelService.removeAll(removalEntries);
				modelService.refresh(orderTemplateModel);
			}

		}
		catch (final Exception e)
		{
			LOG.error("Error while removing Invalid sap availability product from template code: " + orderTemplateModel.getCode(),
					e);
		}

	}


}
