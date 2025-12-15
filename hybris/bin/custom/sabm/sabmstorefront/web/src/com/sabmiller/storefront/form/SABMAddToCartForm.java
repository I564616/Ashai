/**
 *
 */
package com.sabmiller.storefront.form;

import de.hybris.platform.acceleratorstorefrontcommons.forms.AddToCartForm;

import java.util.List;


/**
 * Form for validating update field on cart page.
 *
 * @author bonnie.y.wang
 *
 */
public class SABMAddToCartForm extends AddToCartForm
{
	private String unit = "";

	private String productCodePost;

	private String dealCode;

	private List<SABMAddToCartForm> baseProducts;

	private String smartRecommendationModel;

	/**
	 * @return the unit
	 */
	public String getUnit()
	{
		return unit;
	}

	/**
	 * @param unit
	 *           the unit to set
	 */
	public void setUnit(final String unit)
	{
		this.unit = unit;
	}

	/**
	 * @return the productCodePost
	 */
	public String getProductCodePost()
	{
		return productCodePost;
	}

	/**
	 * @param productCodePost
	 *           the productCodePost to set
	 */
	public void setProductCodePost(final String productCodePost)
	{
		this.productCodePost = productCodePost;
	}

	/**
	 * @return the dealCode
	 */
	public String getDealCode()
	{
		return dealCode;
	}

	/**
	 * @param dealCode
	 *           the dealCode to set
	 */
	public void setDealCode(final String dealCode)
	{
		this.dealCode = dealCode;
	}

	/**
	 * @return the baseProducts
	 */
	public List<SABMAddToCartForm> getBaseProducts()
	{
		return baseProducts;
	}

	/**
	 * @param baseProducts
	 *           the baseProducts to set
	 */
	public void setBaseProducts(final List<SABMAddToCartForm> baseProducts)
	{
		this.baseProducts = baseProducts;
	}

	/**
	 * @return recommendation model
	 * */
	public String getSmartRecommendationModel() {
		return smartRecommendationModel;
	}

	/**
	 * @param smartRecommendationModel
	 * 			the smartRecommendationModel to set
	 * */
	public void setSmartRecommendationModel(String smartRecommendationModel) {
		this.smartRecommendationModel = smartRecommendationModel;
	}
}
