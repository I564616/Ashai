/**
 *
 */
package com.sabmiller.core.product;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.commerceservices.product.data.ReferenceData;
import de.hybris.platform.commerceservices.product.impl.DefaultCommerceProductReferenceService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;


/**
 * @author yuxiao.wang
 *
 */
public class SabmDefaultCommerceProductReferenceService extends DefaultCommerceProductReferenceService
{
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	
	@Override
	public List<ReferenceData<ProductReferenceTypeEnum, ProductModel>> getProductReferencesForCode(final String code,
			final List<ProductReferenceTypeEnum> referenceTypes, final Integer limit)
	{
		if(asahiSiteUtil.isCub())
		{
		
   		validateParameterNotNull(code, "Parameter code must not be null");
   		validateParameterNotNull(referenceTypes, "Parameter referenceType must not be null");
   
   		final List<ReferenceData<ProductReferenceTypeEnum, ProductModel>> result = new ArrayList<ReferenceData<ProductReferenceTypeEnum, ProductModel>>();
   
   		final ProductModel product = getProductService().getProductForCode(code);
   		final List<ProductReferenceModel> references = getAllActiveProductReferencesFromSourceOfType(product, referenceTypes);
   		if (references != null && !references.isEmpty())
   		{
   			for (final ProductReferenceModel reference : references)
   			{
   				final ProductModel targetProduct = resolveTarget(product, reference);
   				//Get Variant Product
   				final SABMAlcoholVariantProductEANModel eanProduct = this.getVariantProduct(targetProduct);
   
   				final ReferenceData<ProductReferenceTypeEnum, ProductModel> referenceData = createReferenceData();
   				referenceData.setTarget(eanProduct);
   				referenceData.setDescription(reference.getDescription());
   				referenceData.setQuantity(reference.getQuantity());
   				referenceData.setReferenceType(reference.getReferenceType());
   				result.add(referenceData);
   
   				// Check the limit
   				if (limit != null && result.size() >= limit.intValue())
   				{
   					break;
   				}
   			}
   		}
   
   		return result;
		}
		else
		{
			return super.getProductReferencesForCode(code,referenceTypes,limit);
		}
	}

	/**
	 * @param targetProduct
	 * @return
	 */
	private SABMAlcoholVariantProductEANModel getVariantProduct(final ProductModel targetProduct)
	{
		ProductModel variant = targetProduct;
		SABMAlcoholVariantProductEANModel eanProduct = null;

		//Checking if the source product is instanceof SABMAlcoholVariantProductEANModel because the attribute UomMappings belongs to it.
		while (variant instanceof VariantProductModel)
		{
			if (variant.getClass().equals(SABMAlcoholVariantProductEANModel.class))
			{
				eanProduct = (SABMAlcoholVariantProductEANModel) variant;
				break;
			}

			variant = ((VariantProductModel) variant).getBaseProduct();
		}
		return eanProduct;
	}

}
