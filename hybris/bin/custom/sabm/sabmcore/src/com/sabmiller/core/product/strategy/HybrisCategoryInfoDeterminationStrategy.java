/**
 *
 */
package com.sabmiller.core.product.strategy;


/**
 * The Interface HybrisCategoryInfoDeterminationStrategy.
 */
public interface HybrisCategoryInfoDeterminationStrategy
{

	/**
	 * Derive category name.
	 *
	 * @param categoryAttribute
	 *           the category attribute
	 * @return the string
	 */
	public String deriveCategoryName(String categoryAttribute);

	/**
	 * Derive category code.
	 *
	 * @param categoryAttribute
	 *           the category attribute
	 * @return the string
	 */
	public String deriveCategoryCode(String categoryAttribute);
}
