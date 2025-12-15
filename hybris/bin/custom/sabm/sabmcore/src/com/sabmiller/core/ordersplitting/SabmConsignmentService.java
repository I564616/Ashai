/**
 *
 */
package com.sabmiller.core.ordersplitting;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.ConsignmentService;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;

import java.util.Date;
import java.util.List;

/**
 * @author joshua.a.antony
 *
 */
public interface SabmConsignmentService extends ConsignmentService
{
	 void attachProductToConsignment(final OrderModel orderModel, final ConsignmentModel consignmentModel,
			final Long quantity, final Long shippedQuantity, final String productCode, String lineNumber, String deliveryItemNumber);

	 ConsignmentModel createConsignment(String code, String trackingId ,final Date beingPickedDate, final Date dispatchedDate, ConsignmentStatus consignmentStatus,
			OrderModel orderModel);

	 void recalculateConsignments(OrderModel orderModel, boolean shuffle);

	 void recalculateConsignments(OrderModel orderModel);

	 void deleteOrphanConsignments(OrderModel orderModel);

	 void setOrderStatusByConsignment(OrderModel orderModel);

	 void mergeConsignments(final OrderModel orderModel, final ConsignmentModel newConsignmentModel);

	 void recalculateCancelledConsignments(final OrderModel orderModel);

	 void addConsignmentToOrder(final OrderModel orderModel, final ConsignmentModel consigmentModel);

	 void removeConsignmentFromOrder(final OrderModel orderModel, final ConsignmentModel consigmentModel);

	 List<ConsignmentModel> lookupConsignmentByCode(final String code);

	 // void updateConsignmentFromRetriver(final ConsignmentModel consignmentModel);
}
