package com.sabmiller.facades.order.hook.impl;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.hook.CommerceUpdateCartEntryHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.model.ModelService;

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
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;


/**
 * The Class SABMUpdateCartEntryHook.
 */
public class SABMUpdateCartEntryHook implements CommerceUpdateCartEntryHook
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SABMUpdateCartEntryHook.class);

	/** The model service. */
	private ModelService modelService;

	/** The cart service. */
	@Resource(name = "cartService")
	private CartService cartService;
	
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
	 * Executed after commerce update cart entry.
	 *
	 * @param parameters
	 *           the parameters
	 * @param result
	 *           the result
	 */
	@Override
	public void afterUpdateCartEntry(final CommerceCartParameter parameters, final CommerceCartModification result)
	{
		if(asahiSiteUtil.isCub())
		{
   		// Empty method
   		if (parameters != null)
   		{
   			final CartModel cartModel = parameters.getCart();
   
   			LOG.info("Update OrderSimulationStatus in SABMUpdateCartEntryHook");
   
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
   				LOG.info("Don't need to update the OrderSimulationStatus in cartModel");
   			}
   
   			if (parameters.getQuantity() > 0)
   			{
   				// Set isChange=true in CartEntryModel, Replace subtotal for line item with an em-dash (—)
   				final CartEntryModel cartEntry = cartService.getEntryForNumber(cartModel,
   						Integer.parseInt(String.valueOf(parameters.getEntryNumber())));
   				cartEntry.setIsChange(Boolean.TRUE);
   				getModelService().save(cartEntry);
   			}
   
   			getModelService().save(cartModel);
   		}
		}

	}

	/**
	 * Executed before commerce update cart entry.
	 *
	 * @param parameters
	 *           the parameters
	 */
	@Override
	public void beforeUpdateCartEntry(final CommerceCartParameter parameters)
	{
		if(asahiSiteUtil.isCub())
		{
   		if (parameters != null && parameters.getProduct() != null)
   		{
   			if (parameters.getProduct() instanceof SABMAlcoholVariantProductEANModel && parameters.getUnit() != null
   					&& !parameters.getUnit().equals(parameters.getProduct().getUnit()))
   			{
   				ProductModel eanVariant = null;
   				if (parameters.getProduct() instanceof SABMAlcoholVariantProductMaterialModel)
   				{
   					eanVariant = ((SABMAlcoholVariantProductMaterialModel) parameters.getProduct()).getBaseProduct();
   				}
   				else if (parameters.getProduct() instanceof SABMAlcoholVariantProductEANModel)
   				{
   					eanVariant = parameters.getProduct();
   				}
   
   				if (eanVariant instanceof SABMAlcoholVariantProductEANModel && parameters.getUnit() != null
   						&& !parameters.getUnit().equals(eanVariant.getUnit()))
   				{
   
   					final List<ProductUOMMappingModel> uomMappings = ((SABMAlcoholVariantProductEANModel) eanVariant).getUomMappings();
   
   					if (CollectionUtils.isNotEmpty(uomMappings))
   					{
   						boolean isConversion = false;
   						double calculatedBase = 0d;
   						for (final ProductUOMMappingModel productUOM : uomMappings)
   						{
   							//Find the corresponding base information where  the one selected from the dropdown be equal to  the CommerceCartParameter.unit
   							if (parameters.getUnit().equals(productUOM.getFromUnit()) && productUOM.getToUnit() != null
   									&& productUOM.getToUnit().equals(parameters.getProduct().getUnit())
   									&& productUOM.getQtyConversion() != null)
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
   							LOG.warn(
   									"Unable to find conversion mapping for product:" + parameters.getProduct().getCode() + " and units: "
   											+ parameters.getUnit().getCode() + " - " + parameters.getProduct().getUnit().getCode());
   						}
   					}
   					else
   					{
   						LOG.debug("ProductData.UomMappingList is empty for the product: {}, return old quantity",
   								parameters.getProduct().getCode());
   
   					}
   				}
   			}
   			//after the calculation, put the Product.unit in the CommerceCartParameter.unit
   			parameters.setUnit(parameters.getProduct().getUnit());
   		}
		}
	}

}
