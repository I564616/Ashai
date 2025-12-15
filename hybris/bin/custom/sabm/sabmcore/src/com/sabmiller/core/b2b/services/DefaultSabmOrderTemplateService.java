/**
 *
 */
package com.sabmiller.core.b2b.services;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.SABMOrderTemplateModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelRemovalException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.internal.dao.SortParameters;
import de.hybris.platform.servicelayer.internal.dao.SortParameters.SortOrder;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.Config;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.b2b.dao.SabmOrderTemplateDao;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.order.SabmB2BOrderService;
import com.sabmiller.core.product.SabmProductService;
import com.sabmiller.core.strategy.SABMUpdateOrderTemplateEntryStrategy;


/**
 * The Class DefaultSabmOrderTemplateService.
 */
public class DefaultSabmOrderTemplateService implements SabmOrderTemplateService
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSabmOrderTemplateService.class);

	/** The order template dao. */
	@Resource(name = "sabmOrderTemplateDao")
	private SabmOrderTemplateDao orderTemplateDao;

	/** The b2b commerce unit service. */
	@Resource(name = "b2bCommerceUnitService")
	private B2BCommerceUnitService b2bCommerceUnitService;

	/** The product service. */
	@Resource(name = "productService")
	private SabmProductService productService;

	/** The commerce cart service. */
	@Resource(name = "commerceCartService")
	private CommerceCartService commerceCartService;

	/** The commerce cart service. */
	@Resource(name = "cartService")
	private CartService cartService;

	/** The unit service. */
	@Resource(name = "unitService")
	private UnitService unitService;

	/** The user service. */
	@Resource(name = "userService")
	private UserService userService;

	/** The common i18 n service. */
	@Resource(name = "commonI18NService")
	private CommonI18NService commonI18NService;

	/** The model service. */
	@Resource(name = "modelService")
	private ModelService modelService;

	/** The key generator. */
	@Resource(name = "orderCodeGenerator")
	private KeyGenerator keyGenerator;

	@Resource(name = "baseStoreService")
	private BaseStoreService baseStoreService;

	@Resource(name = "b2bOrderService")
	private SabmB2BOrderService b2bOrderService;

	@Resource(name = "sabmUpdateOrderTemplateEntryStrategy")
	private SABMUpdateOrderTemplateEntryStrategy sabmUpdateOrderTemplateEntryStrategy;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SabmOrderTemplateService#findOrderTemplateByCode(java.lang.String)
	 */
	@Override
	public SABMOrderTemplateModel findOrderTemplateByCode(final String code, final B2BUnitModel b2bUnit)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put("code", code);
		params.put("unit", b2bUnit);

		return findOrderTemplateByMap(params);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SabmOrderTemplateService#findOrderTemplateByName(java.lang.String,
	 * de.hybris.platform.b2b.model.B2BUnitModel)
	 */
	@Override
	public SABMOrderTemplateModel findOrderTemplateByName(final String name, final B2BUnitModel b2bUnit)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put("name", name);
		params.put("unit", b2bUnit);

		return findOrderTemplateByMap(params);
	}

	/**
	 * Find order template by map.
	 *
	 * @param params
	 *           the params
	 * @return the SABM order template model
	 */
	protected SABMOrderTemplateModel findOrderTemplateByMap(final Map<String, Object> params)
	{
		final List<SABMOrderTemplateModel> templateList = orderTemplateDao.find(params);

		LOG.debug("Order Template found: {}, with parameters: {}", templateList, params);

		//Checking if there are elements in the result list. If 0 or more than 1 element throw Exception.
		if (CollectionUtils.size(templateList) > 1)
		{
			throw new AmbiguousIdentifierException("Found more than one OrderTemplate with params: " + params);
		}
		else if (CollectionUtils.isNotEmpty(templateList))
		{
			return templateList.get(0);
		}

		throw new UnknownIdentifierException("There aren't OrderTemplate with params: " + params);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.core.b2b.services.SabmOrderTemplateService#findTemplateByB2BUnitAndSequence(de.hybris.platform.b2b.
	 * model.B2BUnitModel)
	 */
	@Override
	public List<SABMOrderTemplateModel> findTemplateByB2BUnitAndSequence(final B2BUnitModel b2bUnit, final Integer sequence)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put("sequence", sequence);
		params.put("unit", b2bUnit);

		final List<SABMOrderTemplateModel> templateList = orderTemplateDao.find(params);

		LOG.debug("Order Template found: {}, with parameters: {}", templateList, params);

		return ListUtils.emptyIfNull(templateList);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SabmOrderTemplateService#addProductToOrderTemplate(java.lang.String,
	 * java.lang.String, java.lang.String, long)
	 */
	@Override
	public CommerceCartModification addProductToOrderTemplate(final String orderCode, final String productCode,
			final String fromUnit, final Long quantity) throws CommerceCartModificationException
	{
		return addProductToOrderTemplate(orderCode, productCode, fromUnit, quantity, b2bCommerceUnitService.getParentUnit());
	}


	@Override
	public CommerceCartModification addProductToOrderTemplate(final String orderCode, final String productCode,
			final String fromUnit, final Long quantity, final B2BUnitModel b2bUnitModel) throws CommerceCartModificationException
	{
		final SABMOrderTemplateModel templateModel = findOrderTemplateByCode(orderCode, b2bUnitModel);

		return addProductToOrderTemplate(templateModel, productCode, fromUnit, quantity);
	}

	@Override
	public CommerceCartModification addProductToOrderTemplate(final SABMOrderTemplateModel templateModel, final String productCode,
			final String fromUnit, final Long quantity) throws CommerceCartModificationException
	{
		final ProductModel productModel = productService.getProductForCode(productCode);
		LOG.debug("productService.getProductForCode() returned {} for code : {}", productModel,
				productService.getProductForCode(productCode));


		return addProductToOrderTemplate(templateModel, fromUnit, quantity, productModel);

	}

	@Override
	public CommerceCartModification addProductToOrderTemplate(final SABMOrderTemplateModel templateModel, final String fromUnit,
			final Long quantity, final ProductModel productModel) throws CommerceCartModificationException
	{
		CommerceCartModification cartModification = null;

		if (templateModel != null)
		{
			final CommerceCartParameter parameter = new CommerceCartParameter();
			parameter.setEnableHooks(true);
			parameter.setCart(templateModel);
			parameter.setProduct(productModel);
			parameter.setQuantity(quantity != null ? quantity : 1);
			parameter.setCreateNewEntry(false);

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
				//If no unit is found, using the default from the product
				parameter.setUnit(productModel.getUnit());
			}
			cartModification = commerceCartService.addToCart(parameter);
			LOG.debug("Cart modification status : " + cartModification.getStatusCode());
		}


		return cartModification;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SabmOrderTemplateService#createOrderTemplate(java.lang.String)
	 */
	@Override
	public SABMOrderTemplateModel createOrderTemplateFromSessionCart(final String orderName)
	{
		SABMOrderTemplateModel orderTemplate = null;
		if (cartService.hasSessionCart() && CollectionUtils.isNotEmpty(cartService.getSessionCart().getEntries()))
		{


			orderTemplate = createEmptyOrderTemplateForCurrentUnit(orderName);


			cartService.appendToCart(cartService.getSessionCart(), orderTemplate);

			try
			{
				modelService.save(orderTemplate);
			}
			catch (final ModelSavingException e)
			{
				LOG.error("Error persisting order template with name: " + orderName, e);
				return null;
			}
		}
		return orderTemplate;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SabmOrderTemplateService#deleteOrderTemplate(java.lang.String)
	 */
	@Override
	public boolean deleteOrderTemplate(final String orderCode)
	{
		boolean success = false;
		try
		{
			int oldSequence = 0;

			final B2BUnitModel b2bUnitModel = b2bCommerceUnitService.getParentUnit();

			/*
			 * Remove the old template
			 */
			final SABMOrderTemplateModel orderTemplate = findOrderTemplateByCode(orderCode, b2bUnitModel);
			oldSequence = orderTemplate.getSequence();
			modelService.remove(orderTemplate);
			success = true;

			/*
			 * Update the sequence of all the templates that behind this one.
			 */
			do
			{
				final List<SABMOrderTemplateModel> behindOrderTemplates = findTemplateByB2BUnitAndSequence(b2bUnitModel,
						++oldSequence);

				if (behindOrderTemplates != null && behindOrderTemplates.size() > 0)
				{
					for (final SABMOrderTemplateModel behindOrderTemplate : behindOrderTemplates)
					{
						behindOrderTemplate.setSequence(behindOrderTemplate.getSequence() - 1);
						modelService.save(behindOrderTemplate);
					}
				}
				else
				{
					break;
				}
			}
			while (true);
		}
		catch (AmbiguousIdentifierException | UnknownIdentifierException e)
		{
			LOG.warn("Error finding order template with code: " + orderCode, e);
		}
		catch (final ModelRemovalException e)
		{
			LOG.warn("Error removing order template with code: " + orderCode, e);
		}
		return success;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.core.b2b.services.SabmOrderTemplateService#createOrderTempletWithCoreProductRange(de.hybris.platform
	 * .b2b.model.B2BUnitModel)
	 */
	@Override
	public void createOrderTempletWithCoreProductRange(final B2BUnitModel b2bUnitModel) throws CommerceCartModificationException
	{
		//Create order template
		final String templateName = Config.getString("core.product.range.template.name", "CUB Core Range");
		final SABMOrderTemplateModel newOrderTemplate = createOrderTemplate(templateName, b2bUnitModel);

		//Populate template with cub core range products
		final List<SABMAlcoholVariantProductEANModel> productEans = productService.fetchCoreProductRange();

		for (final SABMAlcoholVariantProductEANModel productEanModel : ListUtils.emptyIfNull(productEans))
		{
			LOG.debug("Adding Ean : {} to Template {} ", productEanModel.getCode(), newOrderTemplate.getCode());

			cartService.addNewEntry(newOrderTemplate, productEanModel, 1, productEanModel.getUnit());
		}

		modelService.save(newOrderTemplate);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.core.b2b.services.SabmOrderTemplateService#createEmptyOrderTemplate(de.hybris.platform.b2b.model
	 * .B2BUnitModel)
	 */
	@Override
	public void createEmptyOrderTemplate(final B2BUnitModel b2bUnitModel)
	{
		final String templateName = Config.getString("order.template.empty.name", "Your Template");
		createOrderTemplate(templateName, b2bUnitModel);

	}

	private SABMOrderTemplateModel createOrderTemplate(final String templateName, final B2BUnitModel b2bUnitModel)
	{
		final BaseStoreModel baseStoreModel = baseStoreService
				.getBaseStoreForUid(Config.getString("base.store.default", "sabmStore"));

		final SABMOrderTemplateModel orderTemplate = modelService.create(SABMOrderTemplateModel.class);

		orderTemplate.setCode(String.valueOf(keyGenerator.generate()));
		orderTemplate.setUser(userService.getAnonymousUser());
		orderTemplate.setName(templateName);
		orderTemplate.setUnit(b2bUnitModel);
		orderTemplate.setCurrency(baseStoreModel.getDefaultCurrency());
		orderTemplate.setDate(new Date());
		orderTemplate.setEntries(Collections.<AbstractOrderEntryModel> emptyList());
		orderTemplate.setSequence(Integer.valueOf(1));
		modelService.save(orderTemplate);

		return orderTemplate;
	}


	/**
	 * Create Empty Order Template by name
	 *
	 * @param templateName
	 *           the template code
	 * @return the SABM order template model
	 */
	@Override
	public SABMOrderTemplateModel createEmptyOrderTemplateForCurrentUnit(final String templateName)
	{
		SABMOrderTemplateModel orderTemplate = null;

		if (StringUtils.isNotBlank(templateName))
		{
			final UserModel user = userService.getCurrentUser();
			final B2BUnitModel b2bUnit = b2bCommerceUnitService.getParentUnit();
			final CurrencyModel currency = commonI18NService.getCurrentCurrency();

			Integer sequence = null;

			final SortParameters sortParam = new SortParameters();
			sortParam.addSortParameter(SABMOrderTemplateModel.SEQUENCE, SortOrder.DESCENDING);
			final Map<String, Object> params = new HashMap<>();
			params.put("unit", b2bUnit);

			final List<SABMOrderTemplateModel> sortedOrderTemplate = orderTemplateDao.find(params, sortParam);

			if (CollectionUtils.isNotEmpty(sortedOrderTemplate) && sortedOrderTemplate.get(0).getSequence() != null)
			{
				sequence = sortedOrderTemplate.get(0).getSequence() + 1;
			}

			orderTemplate = modelService.create(SABMOrderTemplateModel.class);

			orderTemplate.setCode(String.valueOf(keyGenerator.generate()));
			orderTemplate.setUser(user);
			orderTemplate.setName(templateName);
			orderTemplate.setUnit(b2bUnit);
			orderTemplate.setCurrency(currency);
			orderTemplate.setDate(new Date());
			orderTemplate.setEntries(Collections.<AbstractOrderEntryModel> emptyList());
			orderTemplate.setSequence(sequence != null ? sequence : 1);

			try
			{
				modelService.save(orderTemplate);
			}
			catch (final ModelSavingException e)
			{
				LOG.error("Error persisting order template with name: " + templateName, e);
				return null;
			}

		}
		return orderTemplate;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SabmOrderTemplateService#createOrderTemplateByOrderCode(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public SABMOrderTemplateModel createOrderTemplateByOrderCode(final String orderName, final String orderCode)
	{
		SABMOrderTemplateModel orderTemplate = null;
		final OrderModel orderModel = b2bOrderService.getOrderForCode(orderCode);
		if (orderModel == null || CollectionUtils.isEmpty(orderModel.getEntries()))
		{
			LOG.error("Error persisting order template with name: " + orderName);
			return null;
		}
		final List<AbstractOrderEntryModel> abstractOrderEntryModels = orderModel.getEntries();
		//Create the order Template
		orderTemplate = this.createEmptyOrderTemplateForCurrentUnit(orderName);

		for (final AbstractOrderEntryModel abstractOrderEntryModel : abstractOrderEntryModels)
		{
			try
			{
				this.addProductToOrderTemplate(orderTemplate, abstractOrderEntryModel.getUnit().getCode(),
						abstractOrderEntryModel.getQuantity(), abstractOrderEntryModel.getProduct());
			}
			catch (final CommerceCartModificationException e)
			{
				LOG.error("Error persisting order template with name: " + orderName, e);
				return null;
			}
		}

		return orderTemplate;
	}

	/**
	 * Update the order template entry by the parameter SABMC-904
	 *
	 * @param parameter
	 *           the parameter
	 * @return the CommerceCartModification
	 */
	@Override
	public CommerceCartModification updateQuantityForOrderTemplateEntry(final CommerceCartParameter parameter)
			throws CommerceCartModificationException
	{
		return sabmUpdateOrderTemplateEntryStrategy.updateQuantityForCartEntry(parameter);
	}

	/**
	 * Service for remove the order template entry SABMC-904
	 *
	 * @param cartModel
	 *           the cart Model
	 * @param entryToUpdate
	 *           the entry To Update
	 * @return CommerceCartModification
	 */
	@Override
	public CommerceCartModification removeEntry(final CartModel cartModel, final AbstractOrderEntryModel entryToUpdate)
	{
		return sabmUpdateOrderTemplateEntryStrategy.removeEntry(cartModel, entryToUpdate);
	}

}
