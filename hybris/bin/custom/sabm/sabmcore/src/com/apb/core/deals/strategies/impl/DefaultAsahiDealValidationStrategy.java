/**
 *
 */
package com.apb.core.deals.strategies.impl;


import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.Calendar;
import java.util.Date;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.deals.strategies.AsahiDealValidationStrategy;
import com.apb.core.model.ApbProductModel;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.AsahiDealModel;
import com.sabmiller.core.model.AsahiFreeGoodsDealBenefitModel;
import com.sabmiller.core.model.AsahiProductDealConditionModel;
import com.sabmiller.core.product.SabmPriceRowService;
import com.sabmiller.core.product.SabmProductService;


/**
 * The Class DefaultAsahiDealValidationStrategy.
 */
public class DefaultAsahiDealValidationStrategy implements AsahiDealValidationStrategy
{
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(DefaultAsahiDealValidationStrategy.class);

	/** The product service. */
	@Resource(name = "productService")
	private SabmProductService productService;

	/** The price row service. */
	@Resource(name = "priceRowService")
	private SabmPriceRowService priceRowService;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.core.deals.strategies.SABMDealValidationStrategy#validateDeal(com.sabmiller.core.model.DealModel)
	 */
	@Override
	public boolean validateDeal(final AsahiDealModel deal)
	{
		return validateNoExpired(deal, new Date()) && validateConditions(deal) && validateBenefits(deal, null);
	}

	@Override
	public boolean validateDeal(final AsahiDealModel deal, final AsahiB2BUnitModel b2bUnit)
	{
		return validateNoExpired(deal, new Date()) && validateConditions(deal) && validateBenefits(deal, b2bUnit);
	}


	/**
	 * Validate no Expired.
	 *
	 * @param deal
	 * @param referenceDate
	 * @return true, if no expired
	 */
	protected boolean validateNoExpired(final AsahiDealModel deal, final Date referenceDate)
	{
		final int result = DateUtils.truncatedCompareTo(deal.getValidTo(), referenceDate, Calendar.DAY_OF_MONTH);
		return result < 0 ? false : true;
	}

	/**
	 * Validate Started.
	 *
	 * @param deal
	 * @param referenceDate
	 * @return true if started
	 */
	protected boolean validateStarted(final AsahiDealModel deal, final Date referenceDate)
	{
		final int result = DateUtils.truncatedCompareTo(referenceDate, deal.getValidFrom(), Calendar.DAY_OF_MONTH);
		return result < 0 ? false : true;
	}

	/**
	 * Validate conditions.
	 *
	 * @param deal
	 *           the deal
	 * @return true, if successful
	 */
	protected boolean validateConditions(final AsahiDealModel deal)
	{
		if (deal == null || deal.getDealCondition() == null || !(deal.getDealCondition() instanceof AsahiProductDealConditionModel)
				|| ((AsahiProductDealConditionModel) deal.getDealCondition()).getProductCode() == null
				|| ((AsahiProductDealConditionModel) deal.getDealCondition()).getQuantity() == null
				|| ((AsahiProductDealConditionModel) deal.getDealCondition()).getQuantity() < 1)
		{
			LOG.debug("The deal [{}] doesn't have any conditions. It's invalid", deal);
			return false;
		}
		else {
			final String conditionProductCode = ((AsahiProductDealConditionModel) deal.getDealCondition()).getProductCode();
			final ProductModel conditionProduct = productService.getProductForCodeSafe(conditionProductCode);
			if(!(conditionProduct instanceof ApbProductModel)) {
				LOG.debug("The deal [{}] doesn't have any valid condition product", conditionProductCode);
				return false;
			} else {
				final ApbProductModel apbProductModel = (ApbProductModel)conditionProduct;
				if(!apbProductModel.isActive() || !(apbProductModel.getApprovalStatus().equals(ArticleApprovalStatus.APPROVED))) {
					LOG.debug("The deal [{}] doesn't have any valid benefit product", apbProductModel.getCode());
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Validate benefits.
	 *
	 * @param deal
	 *           the deal
	 * @return true, if successful
	 */
	protected boolean validateBenefits(final AsahiDealModel deal, final B2BUnitModel b2bUnit)
	{
		if (deal == null || deal.getDealBenefit() == null || !(deal.getDealBenefit() instanceof AsahiFreeGoodsDealBenefitModel)
				|| ((AsahiFreeGoodsDealBenefitModel) deal.getDealBenefit()).getProductCode() == null
				|| ((AsahiFreeGoodsDealBenefitModel) deal.getDealBenefit()).getQuantity() == null
				|| ((AsahiFreeGoodsDealBenefitModel) deal.getDealBenefit()).getQuantity() < 1)
		{
			LOG.debug("The deal [{}] doesn't have any benefits. It's invalid", deal);
			return false;
		} else {
			final String benefitProductCode = ((AsahiFreeGoodsDealBenefitModel) deal.getDealBenefit()).getProductCode();
			final ProductModel benefitProduct = productService.getProductForCodeSafe(benefitProductCode);
			if(!(benefitProduct instanceof ApbProductModel)) {
				LOG.debug("The deal [{}] doesn't have any valid benefit product", benefitProductCode);
				return false;
			} else {
				final ApbProductModel apbProductModel = (ApbProductModel)benefitProduct;
				if(!apbProductModel.isActive() || !(apbProductModel.getApprovalStatus().equals(ArticleApprovalStatus.APPROVED))) {
					LOG.debug("The deal [{}] doesn't have any valid benefit product", apbProductModel.getCode());
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public boolean validateDealForCustomer(final AsahiDealModel deal)
	{
		return validateNoExpired(deal, new Date()) && validateStarted(deal, new Date()) && validateConditions(deal)
				&& validateBenefits(deal, null);
	}
}
