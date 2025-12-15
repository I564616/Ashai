/**
 *
 */
package com.sabmiller.core.b2b.dao;

import de.hybris.platform.b2b.dao.B2BUnitDao;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.order.OrderModel;

import java.util.Date;
import java.util.List;

import com.sabmiller.core.enums.LastUpdatedEntityType;
import com.sabmiller.core.model.LastUpdateTimeEntityModel;


/**
 * The Interface SabmB2BUnitDao.
 */
public interface SabmB2BUnitDao extends B2BUnitDao
{

	/**
	 * Find top level b2 b unit.
	 *
	 * @param payerId
	 *           the payer id
	 * @return the b2 b unit model
	 */
	B2BUnitModel findTopLevelB2BUnit(String payerId);

	/**
	 * Search b2 b unit.
	 *
	 * @param aueryParam
	 *           the auery param
	 * @return the list
	 */
	List<B2BUnitModel> searchB2BUnit(SearchB2BUnitQueryParam aueryParam);

	/**
	 * Find branch.
	 *
	 * @param payerId
	 *           the payer id
	 * @return the b2 b unit model
	 */
	B2BUnitModel findBranch(String payerId);

	/**
	 * Find branches.
	 *
	 * @param payerId
	 *           the payer id
	 * @return the list
	 */
	List<B2BUnitModel> findBranches(String payerId);

	List<B2BUnitModel> searchB2BUnitByAccount(final String accountNumber);

	List<B2BUnitModel> searchB2BUnitByCustomer(final String customerNumber, final String customerName);

	/**
	 * Find old LastUpdateTimeEntityModel while LastUpdateTimeEntityModel.deliveryDate < deliveryBefore.
	 *
	 * @param deliveryBefore
	 *           the delivery before
	 * @param batchSize
	 *           the batch size
	 * @return list of @LastUpdateTimeEntityModel
	 */
	List<LastUpdateTimeEntityModel> findOldLastUpdateTimeEntities(final Date deliveryBefore, final int batchSize);

	/**
	 * @param unit
	 * @param ordersAfterDate
	 * @return
	 */
	List<OrderModel> findB2BunitOrders(B2BUnitModel unit, Date ordersAfterDate);

	/**
	 * Find LastUpdateTimeEntityModel while LastUpdateTimeEntityModel.deliveryDate = deliveryBefore And
	 * LastUpdateTimeEntityModel.entitytype = lastentitytype And LastUpdateTimeEntityModel.b2bunit = b2bunit
	 *
	 * @param deliveryDate
	 *           the delivery date
	 * @param lastUpdatedEntityType
	 *           the entyty type
	 * @param B2BUnit
	 *           the B2BUnit
	 * @return @LastUpdateTimeEntityModel
	 */
	LastUpdateTimeEntityModel findLastUpdateTimeEntities(final Date deliveryDate,
			final LastUpdatedEntityType lastUpdatedEntityType, final B2BUnitModel B2BUnit);

	List<B2BUnitModel> findB2BUnitsByBannerAndPriceGroup(final String primaryGroupKey, final String subGroupKey,
			final String priceGroup);

	B2BUnitModel findB2BUnitbyUID(String uid);
}
