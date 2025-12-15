/**
 *
 */
package com.sabmiller.core.deals.dao;

import de.hybris.platform.b2b.model.B2BUnitModel;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.sabmiller.core.enums.DealTypeEnum;
import com.sabmiller.core.model.AbstractDealBenefitModel;
import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.AsahiDealModel;
import com.sabmiller.core.model.CartDealConditionModel;
import com.sabmiller.core.model.DealAssigneeModel;
import com.sabmiller.core.model.DealConditionGroupModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.DealScaleModel;



/**
 * The Interface DealsDao.
 *
 * @author joshua.a.antony
 */
public interface DealsDao
{

	/**
	 * Gets the deals.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @param fromDate
	 *           the from date
	 * @param toDate
	 *           the to date
	 * @param brand
	 *           the brand
	 * @param categoryCode
	 *           the category code
	 * @param specificDate
	 *           the specific date
	 * @return the deals
	 */
	List<DealModel> getDeals(B2BUnitModel b2bUnitModel, Date fromDate, Date toDate, List<String> brand, List<String> categoryCode,
			Date specificDate);

	/**
	 * Gets the deal.
	 *
	 * @param dealCode
	 *           the deal code
	 * @return the deal
	 */
	DealModel getDeal(String dealCode);

	/**
	 * Gets the deals.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @param fromDate
	 *           the from date
	 * @param toDate
	 *           the to date
	 * @return the deals
	 */
	List<DealModel> getDeals(B2BUnitModel b2bUnitModel, Date fromDate, Date toDate);

	/**
	 * Gets the deals for product.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @param productCode
	 *           the product code
	 * @param fromDate
	 *           the from date
	 * @param toDate
	 *           the to date
	 * @return the deals for product
	 */
	List<DealModel> getDealsForProduct(B2BUnitModel b2bUnitModel, List<String> productCode, Date fromDate, Date toDate);

	/**
	 * Gets the deals by type.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @param dealType
	 *           the deal type
	 * @return the deals by type
	 */
	List<DealModel> getDealsByType(B2BUnitModel b2bUnitModel, DealTypeEnum dealType);

	/**
	 * Gets the complex deals.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @return the complex deals
	 */
	List<DealModel> getComplexDeals(B2BUnitModel b2bUnitModel);

	/**
	 * Gets all the complex deals.
	 *
	 * @return the complex deals
	 */
	List<DealModel> getComplexDeals();

	/**
	 * Returns all complex deals with validTo dates greater than #toDate
	 * @param toDate the date to test
	 * @return
	 */
	List<DealModel> getComplexDealsToDate(final Date toDate);

	/**
	 * Get Rep-Driven exclusive deals.
	 *
	 * @param b2bUnit
	 *           This is Customer B2BUnit
	 * @param inStore
	 *           This is Rep-Driven deal identifier
	 * @return List<DealModel>
	 */
	List<DealModel> getNonComplexDeals(B2BUnitModel b2bUnit, boolean inStore);

	/**
	 * Get AbstractDealCondition assosciated to month back expired deals
	 *
	 * @param date
	 *           30 days before date
	 *
	 * @return List of AbstractDealCondition
	 */
	List<AbstractDealConditionModel> getAbstractDealCondition(final Date date, final int batchSize);

	/**
	 * Get AbstractDealBenefit assosciated to month back expired deals
	 *
	 * @param date
	 *           30 days before date
	 *
	 * @return List of AbstractDealBenefit
	 */
	List<AbstractDealBenefitModel> getAbstractDealBenefit(final Date date, final int batchSize);

	/**
	 * Get DealScales assosciated to month back expired deals
	 *
	 * @param date
	 *           30 days before date
	 *
	 * @return List of DealScales
	 */
	List<DealScaleModel> getDealsScales(final Date date, final int batchSize);

	/**
	 * Get DealConditionGroup assosciated to month back expired deals
	 *
	 * @param date
	 *           30 days before date
	 *
	 * @return List of DealConditionGroup
	 */
	List<DealConditionGroupModel> getDealConditionGroup(final Date date, final int batchSize);


	/**
	 * Get DealConditionGroup With deals less that or equal validTo base on date
	 * @param date
	 * @param batchSize
	 * @return
	 */
	List<DealConditionGroupModel> getDealConditionGroupForExpiredDeals(final Date date, final int batchSize);

	/**
	 * Get DealAssignee assosciated to month back expired deals
	 *
	 * @param date
	 *           30 days before date
	 *
	 * @return List of DealAssignee
	 */
	List<DealAssigneeModel> getDealAssignee(final Date date, final int batchSize);

	/**
	 * Returns DealAsignee with expired deals based on date
	 * @param date
	 * @param batchSize
	 * @return
	 */
	List<DealAssigneeModel> getDealAssigneeForExpiredDeals(final Date date, int batchSize);

	/**
	 * Get CartDealCondition assosciated to month back expired deals
	 *
	 * @param date
	 *           30 days before date
	 *
	 * @return List of CartDealCondition
	 */
	List<CartDealConditionModel> getCartDealCondition(final Date date, final int batchSize);


	/**
	 * Returns CartDealCondition with deals have expired based on date provided
	 * @param date
	 * @param batchSize
	 * @return
	 */
	List<CartDealConditionModel> getCartDealConditionForExpiredDeals(final Date date, final int batchSize);

	/**
	 * Get Expired deals a month back
	 *
	 * @param date
	 *           30 days before date
	 *
	 * @return List of Deals
	 */
	List<DealModel> getDealsbeforethirtydays(final Date date, final int batchSize);

	/**
	 * @param catalogHierarchies
	 * @return
	 */
	List<AsahiDealModel> getSGASpecificDeals(Collection<String> catalogHierarchies);

	/**
	 * @param dealsToActivate
	 * @return
	 */
	List<AsahiDealModel> getSgaDealsForCode(List<String> dealsToActivate);

	/**
	 * @param dealCode
	 * @return
	 */
	AsahiDealModel getSgaDealByCode(String dealCode);

}
