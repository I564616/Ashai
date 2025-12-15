package com.sabmiller.facades.order.hook.impl;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.hook.CommerceAddToCartMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.variants.model.VariantProductModel;

import java.math.BigDecimal;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.enums.OrderSimulationStatus;
import com.sabmiller.core.model.ProductUOMMappingModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.product.SabmProductService;


/**
 * SAB-574.
 *
 * @author tom.minwen.wang
 */
public class SABMAddToCartHook implements CommerceAddToCartMethodHook
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SABMAddToCartHook.class);

	/** The model service. */
	private ModelService modelService;

	/** The product service. */
	@Resource
	private SabmProductService productService;
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;


	/**
	 * Gets the model service.
	 *
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * Sets the model service.
	 *
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * Executed before commerce add cart entry.
	 *
	 * @param parameters
	 *           the parameters
	 * @throws CommerceCartModificationException
	 *            the commerce cart modification exception
	 */
	@Override
	public void beforeAddToCart(final CommerceCartParameter parameters) throws CommerceCartModificationException
	{
		if (parameters == null)
		{
			LOG.error("CommerceCartParameter should never be null here!");
			return;
		}
		
		if(asahiSiteUtil.isCub())
		{
   		if (parameters.getProduct() instanceof SABMAlcoholVariantProductEANModel && parameters.getUnit() != null
   				&& !parameters.getUnit().equals(parameters.getProduct().getUnit()))
   		{
   			final List<ProductUOMMappingModel> uomMappings = ((SABMAlcoholVariantProductEANModel) parameters.getProduct())
   					.getUomMappings();
   
   			if (CollectionUtils.isNotEmpty(uomMappings))
   			{
   				boolean isConversion = false;
   				double calculatedBase = 0d;
   				for (final ProductUOMMappingModel productUOM : uomMappings)
   				{
   					//Find the corresponding base information where  the one selected from the dropdown be equal to  the CommerceCartParameter.unit
   					if (parameters.getUnit().equals(productUOM.getFromUnit()) && productUOM.getToUnit() != null
   							&& productUOM.getToUnit().equals(parameters.getProduct().getUnit()) && productUOM.getQtyConversion() != null)
   					{
   						calculatedBase = productUOM.getQtyConversion().doubleValue();
   						isConversion = true;
   						break;
   					}
   				}
   				if (isConversion)
   				{
   					// Calculated new value,update the quantity of the CommerceCartParameter.quantity with the new  value
   					parameters.setQuantity(
   							BigDecimal.valueOf(calculatedBase).multiply(BigDecimal.valueOf(parameters.getQuantity())).longValue());
   				}
   				else
   				{
   					LOG.warn("Unable to find conversion mapping for product:" + parameters.getProduct().getCode() + " and units: "
   							+ parameters.getUnit().getCode() + " - " + parameters.getProduct().getUnit().getCode());
   				}
   			}
   			else
   			{
   				if (LOG.isDebugEnabled())
   				{
   					LOG.debug("ProductData.UomMappingList is empty for the product: " + parameters.getProduct().getCode()
   							+ ", return old quantity");
   				}
   
   			}
   		}
   
   		LOG.debug("SABMAddToCartHook.beforeAddToCart product code=" + parameters.getProduct().getCode());
   		
   		//after the calculation, put the Product.unit in the CommerceCartParameter.unit
   		parameters.setUnit(parameters.getProduct().getUnit());
   
   		if (parameters.getProduct().getClass().equals(SABMAlcoholVariantProductEANModel.class))
   		{
   			//Getting the Material From the product to add to cart.
   			try
   			{
   				LOG.debug("SABMAddToCartHook.beforeAddToCart product code BEFORE calling getMaterialFromEan=" + parameters.getProduct().getCode());
   				
   				parameters.setProduct(productService.getMaterialFromEan(parameters.getProduct()));
   				
   				LOG.debug("SABMAddToCartHook.beforeAddToCart product code AFTER calling getMaterialFromEan=" + parameters.getProduct().getCode());
   			}
   			catch (final ModelNotFoundException e)
   			{
   				throw new CommerceCartModificationException("Unable to find Material to add to cart", e);
   			}
   		}
   
   		//In case of MaterialSubstitution use the same one provided by SAP
   		if (parameters.getCart() != null && CollectionUtils.isNotEmpty(parameters.getCart().getEntries()))
   		{
   			for (final AbstractOrderEntryModel entry : parameters.getCart().getEntries())
   			{
   				if (entry.getProduct() instanceof VariantProductModel && parameters.getProduct() instanceof VariantProductModel
   						&& !entry.getProduct().equals(parameters.getProduct()))
   				{
   					if (((VariantProductModel) entry.getProduct()).getBaseProduct()
   							.equals(((VariantProductModel) parameters.getProduct()).getBaseProduct()))
   					{
   						parameters.setProduct(entry.getProduct());
   						break;
   					}
   				}
   			}
   		}
   		
   		LOG.debug("SABMAddToCartHook.beforeAddToCart product code at method end=" + parameters.getProduct().getCode());
		}
	}

	/**
	 * Executed after commerce add cart entry.
	 *
	 * @param parameters
	 *           the parameters
	 * @param result
	 *           the result
	 * @throws CommerceCartModificationException
	 *            the commerce cart modification exception
	 */
	@Override
	public void afterAddToCart(final CommerceCartParameter parameters, final CommerceCartModification result)
			throws CommerceCartModificationException
	{

		if (parameters != null)
		{
			final CartModel cartModel = parameters.getCart();

			LOG.debug("Update OrderSimulationStatus in SABMAddToCartHook for cart: {}", cartModel);

			if (parameters.getCart().getOrderSimulationStatus() == null)
			{
				cartModel.setOrderSimulationStatus(OrderSimulationStatus.NEED_CALCULATION);
			}
			else if (parameters.getCart().getOrderSimulationStatus().equals(OrderSimulationStatus.CALCULATED))
			{
				//end
				cartModel.setOrderSimulationStatus(OrderSimulationStatus.NEED_CALCULATION);
			}
			else if (parameters.getCart().getOrderSimulationStatus().equals(OrderSimulationStatus.CALCULATION_IN_PROGRESS))
			{
				cartModel.setOrderSimulationStatus(OrderSimulationStatus.UPDATE_DURING_CALCULATION);
			}
			else
			{
				LOG.debug("Don't need to update the OrderSimulationStatus in cartModel {}", cartModel);
			}
			getModelService().save(cartModel);
		}

	}
}
