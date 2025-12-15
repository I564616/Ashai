/**
 *
 */
package com.apb.facades.populators;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.product.CommerceProductService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.session.SessionService;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.apb.core.model.ApbProductModel;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.facades.deal.data.AsahiDealData;
import com.apb.facades.deal.data.AsahiDealProductData;
import com.sabmiller.core.model.AsahiDealModel;
import com.sabmiller.core.model.AsahiFreeGoodsDealBenefitModel;
import com.sabmiller.core.model.AsahiProductDealConditionModel;
import com.sabmiller.core.product.SabmProductService;
import com.sabmiller.facades.populators.SABMProductUrlPopulator;


/**
 * The Class SABMDealProductPopulator.
 */
public class AsahiDealProductDataPopulator implements Populator<AsahiDealModel, AsahiDealData>
{
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(AsahiDealProductDataPopulator.class);
	/** The commerce product service. */
	@Resource(name = "commerceProductService")
	private CommerceProductService commerceProductService;
	@Resource
	private ApbProductGalleryImagesPopulator<ApbProductModel, ProductData> apbProductGalleryImagesPopulator;
	@Resource
	private SABMProductUrlPopulator sabmProductUrlPopulator;
	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "productService")
	private SabmProductService productService;
	
	@Resource(name = "asahiCoreUtil")
	private AsahiCoreUtil asahiCoreUtil;

	/**
	 * Populate the Deal's Product to DealJson.
	 *
	 * @param source
	 *           the source
	 * @param target
	 *           the target
	 */
	@Override
	public void populate(final AsahiDealModel source, final AsahiDealData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		LOG.debug("Populating deal: [{}]", source);

		final AsahiDealModel deal = source;

		if (deal.getDealCondition() == null)
		{
			LOG.warn("Deal: [{}] is without condition!", deal.getCode());
			return;
		}

		populateCondition(deal, target);
		populateBenefits(source, target);
		populateTitle(source, target);
	}


	/**
	 * @param source
	 * @param target
	 */
	private void populateBenefits(final AsahiDealModel source, final AsahiDealData target)
	{
		if (source.getDealBenefit() instanceof AsahiFreeGoodsDealBenefitModel)
		{
			final AsahiFreeGoodsDealBenefitModel benefitProduct = (AsahiFreeGoodsDealBenefitModel) source.getDealBenefit();
			final AsahiDealProductData asahiDealProductData = new AsahiDealProductData();
			asahiDealProductData.setCode(benefitProduct.getProductCode());
			asahiDealProductData.setQty(benefitProduct.getQuantity());
			target.setBenefitProduct(asahiDealProductData);
		}

	}


	/**
	 * @param deal
	 * @param target
	 */
	private void populateCondition(final AsahiDealModel source, final AsahiDealData target)
	{

		if (source.getDealCondition() instanceof AsahiProductDealConditionModel)
		{
			final AsahiProductDealConditionModel conditionModel = (AsahiProductDealConditionModel) source.getDealCondition();
			final AsahiDealProductData asahiDealProductData = new AsahiDealProductData();
			asahiDealProductData.setCode(conditionModel.getProductCode());
			asahiDealProductData.setQty(conditionModel.getQuantity());
			target.setConditionProduct(asahiDealProductData);
		}

	}

	/**
	 * @param source
	 * @param target
	 */
	private void populateTitle(final AsahiDealModel source, final AsahiDealData target)
	{
		target.setTitle(asahiCoreUtil.getAsahiDealTitle(source));
		final String conditionProductCode = ((AsahiProductDealConditionModel) source.getDealCondition()).getProductCode();
		final ApbProductModel conditionProduct = (ApbProductModel) productService.getProductForCodeSafe(conditionProductCode);
		final ProductData productData = new ProductData();
		apbProductGalleryImagesPopulator.populate(conditionProduct, productData);
		sabmProductUrlPopulator.populate(conditionProduct, productData);
		target.getConditionProduct().setUrl(productData.getUrl());
		target.getConditionProduct().setImages(productData.getImages());
	}
	
	

}
