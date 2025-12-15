/**
 *
 */
package com.sabmiller.core.ordersplitting;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.impl.DefaultConsignmentService;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.order.SabmB2BOrderService;
import com.sabmiller.core.order.dao.SabmConsignmentDao;
import com.sabmiller.core.product.SabmProductService;


/**
 * Handles the lifecycle of consignments within order. When new order is created, a consignment with CREATED state is
 * created and all the entries from the sales order are referenced from this consignment.On the Delivery creation (SAP
 * to Hybris call), the entries are moved from CREATED to PROCESSING state. On the Delivery Dispatch (SAP to Hybris
 * call), the entries are moved from PROCESSING to DISPATCHED state. On Delivery Deletion, the consignments are moved
 * back to the CREATED state. On Dispatch cancellation, the consignments are moved from the DISPATCHED to PROCESSING
 * state.
 *
 * @author joshua.a.antony
 *
 */
public class DefaultSabmConsignmentService extends DefaultConsignmentService implements SabmConsignmentService
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSabmConsignmentService.class.getName());

	private static final String[] RETURNED_PROCESSING_TYPE_CODE = new String[]
            { "YSR1", "YSFR", "YSRE" };

	@Resource(name = "productService")
	private SabmProductService productService;

	@Resource(name = "b2bOrderService")
	private SabmB2BOrderService b2bOrderService;

	@Resource(name = "modelService")
	private ModelService modelService;


	@Resource(name = "acceleratorConsignmentDao")
	private SabmConsignmentDao consignmentDao;

	/*
	 Add consignmety entry to consignment
	 */
	@Override
	public void attachProductToConsignment(final OrderModel orderModel, final ConsignmentModel consignmentModel,
			final Long quantity, final Long shippedQuantity, final String material, final String lineNumber,
			final String deliveryItemNumber)
	{
		LOG.debug("Attach material : {}  with quantity : {}  , shippedQuantity : {} to Consignment {} ", material, quantity,
				shippedQuantity, consignmentModel.getCode());

		final AbstractOrderEntryModel orderEntryModel = b2bOrderService.lookupOrderEntry(orderModel, material, lineNumber);

		if (orderEntryModel != null)
		{
			final boolean isNewEntry = lookupConsignmentEntry(consignmentModel, orderEntryModel) == null;

			final ConsignmentEntryModel consignmentEntryModel = findOrCreateConsignmentEntry(consignmentModel, orderEntryModel);
			if (isNewEntry)
			{
				consignmentEntryModel.setOrderEntry(orderEntryModel);
				consignmentEntryModel.setQuantity(orderEntryModel.getQuantity());
				consignmentEntryModel.setConsignment(consignmentModel);
				consignmentEntryModel.setDeliveryItemNumber(deliveryItemNumber);

			}
			consignmentEntryModel.setShippedQuantity(shippedQuantity);

			LOG.debug("Saving Consignment Entry : quantity : {} , shippedQuantity : {} , productCode : {} ",
					consignmentEntryModel.getQuantity(), consignmentEntryModel.getShippedQuantity(),
					orderEntryModel.getProduct().getCode());

			if (isNewEntry)
			{
				final Set<ConsignmentEntryModel> entries = new HashSet<ConsignmentEntryModel>(
						consignmentModel.getConsignmentEntries());
				entries.add(consignmentEntryModel);
				consignmentModel.setConsignmentEntries(entries);
			}

			modelService.save(consignmentEntryModel);
			modelService.save(consignmentModel);

			LOG.debug("Consignment : {} has entries => {} ", consignmentModel.getCode(), consignmentModel.getConsignmentEntries());
		}
	}

	@Override
	public ConsignmentModel createConsignment(final String code, final String trackingId,  final Date beingPickedDate, final Date dispatchedDate,
			final ConsignmentStatus consignmentStatus, final OrderModel orderModel)
	{
		LOG.debug("Creating consignment. code : {}  , tracking Id : {}  , status : {}  for order {}", code, trackingId,
				consignmentStatus, orderModel.getCode());

		final ConsignmentModel consignmentModel = modelService.create(ConsignmentModel.class);
		consignmentModel.setCode(code);
		consignmentModel.setTrackingID(trackingId);
		consignmentModel.setStatus(consignmentStatus);
		consignmentModel.setShippingAddress(orderModel.getDeliveryAddress());
		consignmentModel.setWarehouse(orderModel.getStore().getWarehouses().get(0));
		consignmentModel.setConsignmentBeingPickedDate(beingPickedDate);
		consignmentModel.setConsignmentDispatchedDate(dispatchedDate);
		modelService.save(consignmentModel);
		return consignmentModel;
	}

	@Override
	public ConsignmentModel createConsignment(final AbstractOrderModel order, final String code,
            final List<AbstractOrderEntryModel> orderEntries) {
		//By Default, do not add the rejected items to the consignments
		return createConsignment(order, code, orderEntries, false);
	}

	private ConsignmentModel createConsignment(final AbstractOrderModel order, final String code,
			final List<AbstractOrderEntryModel> orderEntries, final boolean addRejectedItems) {
		final ConsignmentModel cons = modelService.create(ConsignmentModel.class);

		cons.setStatus(ConsignmentStatus.CREATED);
        cons.setConsignmentConfirmedDate(new Date());
		cons.setConsignmentEntries(new HashSet<ConsignmentEntryModel>());
		cons.setCode(code);
		if (order != null)
		{
			cons.setShippingAddress(order.getDeliveryAddress());

			for (final AbstractOrderEntryModel orderEntry : orderEntries)
			{
				//Do not add the items in rejected state to the newly created consignment
				if (addRejectedItems || orderEntry.getRejected() == null || !orderEntry.getRejected())
				{
					final ConsignmentEntryModel entry = modelService.create(ConsignmentEntryModel.class);

					entry.setOrderEntry(orderEntry);
					entry.setQuantity(orderEntry.getQuantity());
					entry.setConsignment(cons);
					cons.getConsignmentEntries().add(entry);
					cons.setDeliveryMode(orderEntry.getDeliveryMode());
				}
			}
			cons.setWarehouse(order.getStore().getWarehouses().get(0));
			cons.setOrder(order);
		}

		return cons;
	}


	@Override
	public void addConsignmentToOrder(final OrderModel orderModel, final ConsignmentModel consignmentModel)
	{
		LOG.debug("Adding consignment : {} with status {} to order {} ", consignmentModel.getCode(), consignmentModel.getStatus(),
				orderModel.getCode());
		modelService.refresh(orderModel);
		final Set<ConsignmentModel> consignments = new HashSet<ConsignmentModel>(orderModel.getConsignments());
		consignments.add(consignmentModel);
		orderModel.setConsignments(consignments);
		modelService.save(orderModel);
	}

	@Override
	public void removeConsignmentFromOrder(final OrderModel orderModel, final ConsignmentModel consignmentModel)
	{
		LOG.debug("Removing consignment : {} with status {} from order {} ", consignmentModel.getCode(),
				consignmentModel.getStatus(), orderModel.getCode());

		modelService.remove(consignmentModel);
		modelService.refresh(orderModel);
	}



	@Override
	public void recalculateConsignments(final OrderModel orderModel)
	{
		recalculateConsignments(orderModel, true);
	}

	/**
	 * Merge consignment with similar status into a single consignment
	 */
	@Override
	public void mergeConsignments(final OrderModel orderModel, final ConsignmentModel newConsignmentModel)
	{
		for (final ConsignmentModel existingConsignmentModel : SetUtils.emptyIfNull(orderModel.getConsignments()))
		{
			if (!existingConsignmentModel.equals(newConsignmentModel)
					&& existingConsignmentModel.getStatus().equals(newConsignmentModel.getStatus()))
			{
				mergeConsignments(existingConsignmentModel, newConsignmentModel);
			}
		}
	}

	private void mergeConsignments(final ConsignmentModel existingConsignmentModel, final ConsignmentModel newConsignmentModel)
	{
		final Set<ConsignmentEntryModel> consignmentEntries = new HashSet<ConsignmentEntryModel>();
		//merge the consignment models
		consignmentEntries.addAll(newConsignmentModel.getConsignmentEntries());
		consignmentEntries.addAll(existingConsignmentModel.getConsignmentEntries());
		newConsignmentModel.setConsignmentEntries(consignmentEntries);
		modelService.remove(existingConsignmentModel);
		modelService.save(newConsignmentModel);
	}


	/**
	 * If no consignment exist for the order, a new consignment with CREATED state is created. If the 'shuffle' flag is
	 * set AND consignments with CREATED and PROCESSING state exist - then merge these 2 consignments, which involves
	 * updating/deleting entries in the CREATED consignments based on the entries in the PROCESSING consignments.
	 *
	 * The CANCELLED consignments are also refreshed based on the line items in the order. Any orphan
	 * consignments(without entries) are also removed from the order. Finally the order status is calculated (based on
	 * the consignments) and set in the order.
	 */
	@Override
	public void recalculateConsignments(final OrderModel orderModel, final boolean shuffle)
	{
		LOG.debug("In recalculateConsignments(). Going to recalculate the consignments. shuffle : {} ", shuffle);

		if (onlyCreatedConsignmentExist(orderModel))
		{
			modelService.remove(orderModel.getConsignments().iterator().next());
			orderModel.setConsignments(new HashSet<ConsignmentModel>());
		}

		final boolean emptyConsignements = orderModel.getConsignments() == null || orderModel.getConsignments().isEmpty();
		if (emptyConsignements)
		{
			LOG.debug("No Consignments found in the order. Creating a consignment with status {} with all the items in the order ",
					ConsignmentStatus.CREATED);
            //Create a new consignment and add all the items from the order entry into it
            final ConsignmentModel consignmentModel = createConsignment(orderModel, ConsignmentStatus.CREATED.name(),
                    orderModel.getEntries());

            final Set<ConsignmentModel> consignments = new HashSet<ConsignmentModel>();
            consignments.add(consignmentModel);
            orderModel.setConsignments(consignments);
		}
		else if (shuffle)
		{
			LOG.debug("Going to shuffle the entries across consignments");

			final ConsignmentModel consignmentInCreatedSatus = lookupConsignment(orderModel, ConsignmentStatus.CREATED);
			if (consignmentInCreatedSatus != null)
			{
				for (final ConsignmentModel consignmentModel : orderModel.getConsignments())
				{
					if (ConsignmentStatus.PROCESSING.equals(consignmentModel.getStatus()))
					{
						LOG.debug("This is a delivery creation consignment request. Invoking deduct()");
						mergeCreatedAndProcessingConsignments(consignmentInCreatedSatus, consignmentModel);
					}
				}
			}
		}
		modelService.save(orderModel);
		modelService.refresh(orderModel);

		recalculateCancelledConsignments(orderModel);
		LOG.debug("In recalculateConsignments(). recalculateCancelledConsignments  ");

		deleteOrphanConsignments(orderModel);

		setOrderStatusByConsignment(orderModel);

		LOG.debug("In recalculateConsignments(). setOrderStatusByConsignment  ");



	}

	@Override
	public void setOrderStatusByConsignment(final OrderModel orderModel)
	{
		/**
		 * the sequence of invoke below each considtion is critical,
		 * TODO missing Junit testing class
		 */

		if (consignmentExist(orderModel, ConsignmentStatus.CREATED))
		{
			orderModel.setStatus(OrderStatus.CREATED);
		}
		if (consignmentExist(orderModel, ConsignmentStatus.PROCESSING))
		{
			LOG.debug("setOrderStatusByConsignment - ORDER STATUS to PROCESSING");
			orderModel.setStatus(OrderStatus.PROCESSING);
		}
		if (consignmentExist(orderModel, ConsignmentStatus.SHIPPED))
		{
			LOG.debug("setOrderStatusByConsignment - ORDER STATUS to DISPATCHED");
			orderModel.setStatus(OrderStatus.DISPATCHED);

			consignmentExistToSendDispatchNotification(orderModel);
		}
		if (consignmentExist(orderModel, ConsignmentStatus.INTRANSIT))
		{
			orderModel.setStatus(OrderStatus.INTRANSIT);
		}

		if (ArrayUtils.contains(RETURNED_PROCESSING_TYPE_CODE, orderModel.getProcessingTypeCode())
				|| allConsignmentHaveSameStatus(orderModel, ConsignmentStatus.RETURNED))
		{
			orderModel.setStatus(OrderStatus.RETURNED);
		}
		if (allConsignmentHaveSameStatus(orderModel, ConsignmentStatus.CANCELLED))
		{
			orderModel.setStatus(OrderStatus.CANCELLED);
		}
		if (allConsignmentHaveSameStatus(orderModel, ConsignmentStatus.DELIVERED))
		{
			orderModel.setStatus(OrderStatus.COMPLETED);
		}
		if (allConsignmentHaveSameStatus(orderModel, ConsignmentStatus.NOTDELIVERED))
		{
			orderModel.setStatus(OrderStatus.NOTDELIVERED);
		}
		// finally check if order is partially delivered, it has to be the last condition check
		if (oneConsignmentDelivered_AND_oneConsignmentNotDelivered(orderModel)){

            orderModel.setStatus(OrderStatus.PARTIALDELIVERED);
        }
		else
		{
			LOG.error("Unable to determine Order Status!");
		}

		modelService.save(orderModel);
		LOG.debug("In setOrderStatusByConsignment(). The order status is - " + orderModel.getStatus());
	}

    private List<AbstractOrderEntryModel> cancelledEntries(final OrderModel orderModel) {
        final List<AbstractOrderEntryModel> entries = new ArrayList<AbstractOrderEntryModel>();
        for (final AbstractOrderEntryModel eachEntry : ListUtils.emptyIfNull(orderModel.getEntries())) {
            if (eachEntry.getRejected() != null && eachEntry.getRejected()) {
                entries.add(eachEntry);
            }
        }
        return entries;
    }

    /**
     * The cancelled consignment behaves differently since this is not part of delivery/dispatch interfaces. Rather, this
     * consignment is created as part of the sales order udpate interface invocation from SAP. There is no delivery
     * number tied to the consignment. Hence, during every sales order update, we just throw away the existing cancelled
     * consignment and re-create it again (as the consignments are based on order line items)
     */
    @Override
    public void recalculateCancelledConsignments(final OrderModel orderModel) {
        LOG.debug("In recalculateCancelledConsignments()");
        reCalucateUnRejectedItem(orderModel);
        removeCancelledConsignments(orderModel);
        final List<AbstractOrderEntryModel> cancelledItems = cancelledEntries(orderModel);
        if (!cancelledItems.isEmpty()) {
      	// Start Changes as per order status issue -  :- INC1081239
           if(orderModel.getEntries() != null && cancelledItems.size() == orderModel.getEntries().size()){
               modelService.refresh(orderModel);
               final Set<ConsignmentModel> consignments = new HashSet<ConsignmentModel>(CollectionUtils.emptyIfNull(orderModel.getConsignments()));
               modelService.removeAll(consignments);
               modelService.refresh(orderModel);
           }else if(orderModel.getEntries() != null && cancelledItems.size() != orderModel.getEntries().size()){
				LOG.info("Partial Validation: INC1081239");
               final Set<ConsignmentEntryModel> toBeDeletedConsignmentEntries = new HashSet<ConsignmentEntryModel>();
               final Set<ConsignmentModel> tobeDeletedconsignments = new HashSet<ConsignmentModel>();
               for(final ConsignmentModel consignmentModel : CollectionUtils.emptyIfNull(orderModel.getConsignments())){
               	int count = 0;
                   for(final ConsignmentEntryModel consignmentEntryModel : CollectionUtils.emptyIfNull(consignmentModel.getConsignmentEntries())){ 
                  	 LOG.info("consignmentEntryModel.getOrderEntry(): INC1081239 " +consignmentEntryModel.getOrderEntry());
                       if(consignmentEntryModel.getOrderEntry() == null || cancelledItems.contains(consignmentEntryModel.getOrderEntry())){
							LOG.info("Canelled Validation: INC1081239");
                           toBeDeletedConsignmentEntries.add(consignmentEntryModel);
                           count++;
                       }   
                   }
                   if (consignmentModel.getConsignmentEntries().size() == count ){
                  	 LOG.info("Deleted consignment: INC1081239");
                		tobeDeletedconsignments.add(consignmentModel);
                 	  
                   }                   
               }
               if(! toBeDeletedConsignmentEntries.isEmpty()){
               	 LOG.info("toBeDeletedConsignmentEntries.isEmpty(): INC1081239");
                   modelService.removeAll(toBeDeletedConsignmentEntries);
                   modelService.refresh(orderModel);
               }
               if(! tobeDeletedconsignments.isEmpty()){
               	 modelService.removeAll(tobeDeletedconsignments);
                   modelService.refresh(orderModel);
               }
                         
           }
       // End Changes as per order status issue -  :- INC1081239
            LOG.debug("Moving items {} to Cancelled consignments ", cancelledItems);

            final ConsignmentModel cancelledConsignment = createConsignment(orderModel, ConsignmentStatus.CANCELLED.name(),
                    cancelledItems, true);
            cancelledConsignment.setStatus(ConsignmentStatus.CANCELLED);
            modelService.save(cancelledConsignment);
            addConsignmentToOrder(orderModel, cancelledConsignment);

        }
    }

	private void consignmentExistToSendDispatchNotification(final OrderModel orderModel)
	{
		for (final ConsignmentModel consignmentModel : CollectionUtils.emptyIfNull(orderModel.getConsignments()))
		{
			if (ConsignmentStatus.SHIPPED.equals(consignmentModel.getStatus())
					&& BooleanUtils.isFalse(consignmentModel.getDispatchNotifEmailSent())
					&& !CollectionUtils.sizeIsEmpty(consignmentModel.getConsignmentEntries()))
			{
				orderModel.setDispatchNotifEmailSent(false);
			}
		}

	}

	private boolean consignmentExist(final OrderModel orderModel, final ConsignmentStatus status)
	{
		for (final ConsignmentModel consignmentModel : CollectionUtils.emptyIfNull(orderModel.getConsignments()))
		{
			LOG.debug("consignmentExist consignment code {} status {}",consignmentModel.getCode(),consignmentModel.getStatus()!=null?consignmentModel.getStatus().getCode():null);
			if (status.equals(consignmentModel.getStatus()))
			{
				return true;
			}
		}
		return false;
	}


	private boolean allConsignmentHaveSameStatus(final OrderModel orderModel, final ConsignmentStatus status)
	{
		if (CollectionUtils.isEmpty(orderModel.getConsignments()))
		{
			return false;
		}
		for (final ConsignmentModel consignmentModel : CollectionUtils.emptyIfNull(orderModel.getConsignments()))
		{
			if (!status.equals(consignmentModel.getStatus()))
			{
				return false;
			}
		}
		return true;
	}

	private boolean onlyThisConsignmentExist(final OrderModel orderModel, final ConsignmentStatus status)
	{
		if (orderModel.getConsignments() != null && orderModel.getConsignments().size() == 1)
		{
			final ConsignmentModel consignmentModel = orderModel.getConsignments().iterator().next();
			return status.equals(consignmentModel.getStatus());
		}
		return false;
    }

    @Override
    public List<ConsignmentModel> lookupConsignmentByCode(final String code) {

        validateParameterNotNull(code, "Consignment code cannot be null");

        return consignmentDao.getConsignmentForCode(code);


	}

	@Override
	public void deleteOrphanConsignments(final OrderModel orderModel) {
        try {
            //final Set<ConsignmentModel> nonEmptyConsignments = new HashSet<ConsignmentModel>();
            final List<ConsignmentModel> emptyConsignments = new ArrayList<ConsignmentModel>();
            for (final ConsignmentModel consignment : SetUtils.emptyIfNull(orderModel.getConsignments())) {
                if (consignment.getConsignmentEntries() == null || consignment.getConsignmentEntries().isEmpty()) {
                    LOG.debug(
                            "In deleteOrphanConsignments() Adding Consignment status : {} to the removal list. This will be deleted.",
                            consignment.getStatus());
                    emptyConsignments.add(consignment);
                } else if(processingConsignmentIsRedundant(orderModel,consignment)){
                    emptyConsignments.add(consignment);
                }
            }

            for (final ConsignmentModel eachEmptyConsignmentModel : emptyConsignments) {
                if (!modelService.isRemoved(eachEmptyConsignmentModel)) {
                    modelService.remove(eachEmptyConsignmentModel);
                }
            }

            modelService.refresh(orderModel);
            //orderModel.setConsignments(nonEmptyConsignments);

        } catch (final Exception e) {
            LOG.error("Exception occurred deleting empty consignments. This should not cause rollback, hence swallowing it! ", e);
        }
    }

    /**
     * Order is partially delivered
     *
     * @param orderModel
     * @return
     */
    private boolean oneConsignmentDelivered_AND_oneConsignmentNotDelivered(final OrderModel orderModel) {

        if (orderModel.getConsignments() != null && orderModel.getConsignments().size() > 1) {
            final Optional<ConsignmentModel> consignmentDelivered = orderModel.getConsignments().stream()
                    .filter(c -> c.getStatus().equals(ConsignmentStatus.DELIVERED)).findAny();

            final Optional<ConsignmentModel> consignmentNotDelivered = orderModel.getConsignments().stream()
                    .filter(c -> c.getStatus().equals(ConsignmentStatus.NOTDELIVERED)).findAny();

            return consignmentDelivered.isPresent() && consignmentNotDelivered.isPresent();

        }

        return false;

    }

    private ConsignmentModel lookupConsignment(final OrderModel orderModel, final ConsignmentStatus... consignmentStatuses) {
        final List<ConsignmentStatus> consignmentStatusList = Arrays.asList(consignmentStatuses);
        for (final ConsignmentModel consignmentModel : orderModel.getConsignments()) {
            if (consignmentStatusList.contains(consignmentModel.getStatus())) {
                return consignmentModel;
            }
        }
        return null;
    }

	private boolean processingConsignmentIsRedundant(final OrderModel orderModel, final ConsignmentModel consignment)
	{

		LOG.debug("processingConsignmentIsRedundant order status {}",orderModel.getStatus());

		if (OrderStatus.DISPATCHED.equals(orderModel.getStatus()) && ConsignmentStatus.PROCESSING.equals(consignment.getStatus()))
		{

			for (final ConsignmentEntryModel nonEmptyConsignmentEntry : consignment.getConsignmentEntries())
			{
				if (nonEmptyConsignmentEntry.getQuantity() != null && nonEmptyConsignmentEntry.getShippedQuantity() != null
						&& nonEmptyConsignmentEntry.getQuantity().longValue() == nonEmptyConsignmentEntry.getShippedQuantity()
						.longValue())
				{
					modelService.remove(nonEmptyConsignmentEntry);

				}
			}
			modelService.refresh(consignment);

			return CollectionUtils.isEmpty(consignment.getConsignmentEntries());
		}
		return false;
	}

	/**
	 * Compare the consignment entires in PROCESSING state against the CREATED consignment entires and remove/deduct the
	 * appropriate entires from the CREATED status.
	 *
	 * This is performed due to the lifecycle of the consignments that is explained in this class javadoc.
	 *
	 * @param createdConsignmentModel
	 *           - The consignment whose status is CREATED
	 * @param processingConsignmentModel
	 *           - The consignment whose status is PROCESSING
	 */
	private void mergeCreatedAndProcessingConsignments(final ConsignmentModel createdConsignmentModel,
			final ConsignmentModel processingConsignmentModel)
	{
		LOG.debug("Merging existing consignment model with status {} to new consignment model with status {} ",
				createdConsignmentModel.getStatus(), processingConsignmentModel.getStatus());
		LOG.debug("Existing consignment entries {} . In new consignment {} ", createdConsignmentModel.getConsignmentEntries(),
				processingConsignmentModel.getConsignmentEntries());

        //copying the creation time to processing node
        processingConsignmentModel.setConsignmentConfirmedDate(createdConsignmentModel.getCreationtime());

		final List<ConsignmentEntryModel> entriesToBeRemoved = new ArrayList<ConsignmentEntryModel>();
		for (final ConsignmentEntryModel newConsignmentEntry : processingConsignmentModel.getConsignmentEntries())
		{
			//lookup for the new consignment entry in the existing consignment model
			final ConsignmentEntryModel existingConsignmentEntry = lookupConsignmentEntry(createdConsignmentModel,
					newConsignmentEntry.getOrderEntry(), newConsignmentEntry.getDeliveryNumber(),
					newConsignmentEntry.getDeliveryItemNumber());

			if (existingConsignmentEntry != null)
			{
				final long existingQuantity = existingConsignmentEntry.getQuantity();
				final long quantityInNewlyCreatedConsignment = newConsignmentEntry.getShippedQuantity();

				LOG.debug("Quantity in Existing Consignment {} : {} and in New Consignment {} : {} ",
						createdConsignmentModel.getStatus(), existingQuantity, processingConsignmentModel.getStatus(),
						quantityInNewlyCreatedConsignment);

				if (existingQuantity <= quantityInNewlyCreatedConsignment)
				{
					LOG.debug("Marking the entry {} for removal in existing consignment {} ", existingConsignmentEntry,
							createdConsignmentModel.getStatus());
					entriesToBeRemoved.add(existingConsignmentEntry);
				}
				else
				{
					final long remainingQuantity = existingQuantity - quantityInNewlyCreatedConsignment;
					existingConsignmentEntry.setQuantity(remainingQuantity);
					LOG.debug("Remaining quanitity in the CREATED consignment is {} ", remainingQuantity);
				}
			}
		}

		//Finally, remove the consignment entries from the old consignment model (since they have been moved to the new model)
		modelService.removeAll(entriesToBeRemoved);

		modelService.save(createdConsignmentModel);
		modelService.save(processingConsignmentModel);
	}

	private ConsignmentEntryModel lookupConsignmentEntry(final ConsignmentModel consignmentModel,
			final AbstractOrderEntryModel orderEntryModel, final String deliveryNumber, final String deliveryItemNumber)
	{

		for (final ConsignmentEntryModel entry : consignmentModel.getConsignmentEntries())
		{
			if (match(entry, deliveryNumber, deliveryItemNumber, orderEntryModel, consignmentModel.getStatus()))
			{
				LOG.debug("Found Consignemnt Entry for status:{} delieryNumber : {} , deliveryItemNumber : {} ",
						consignmentModel.getStatus(), deliveryNumber, deliveryItemNumber);
				return entry;
			}
		}
		LOG.warn("Consignment Entry not found for status :{} delieryNumber : {} , deliveryItemNumber : {}. Returning null",
				consignmentModel.getStatus(), deliveryNumber, deliveryItemNumber);
		return null;
	}


	private boolean match(final ConsignmentEntryModel consignmentEntry, final String deliveryNumber,
			final String deliveryItemNumber, final AbstractOrderEntryModel orderEntry, final ConsignmentStatus consignmentStatus)
	{
		if (ConsignmentStatus.CREATED.equals(consignmentStatus))
		{
			return orderEntry.equals(consignmentEntry.getOrderEntry());
		}
		return orderEntry.equals(consignmentEntry.getOrderEntry()) && deliveryNumber.equals(consignmentEntry.getDeliveryNumber())
				&& deliveryItemNumber.equals(consignmentEntry.getDeliveryItemNumber());
	}


	private boolean onlyCreatedConsignmentExist(final OrderModel orderModel)
	{
		if (orderModel.getConsignments() != null && orderModel.getConsignments().size() == 1)
		{
			final ConsignmentModel consignmentModel = orderModel.getConsignments().iterator().next();
			return ConsignmentStatus.CREATED.equals(consignmentModel.getStatus());
		}
		return false;
	}

	private void removeCancelledConsignments(final OrderModel orderModel)
	{
		final ConsignmentModel existingCancelledConsignment = getCancelledConsignment(orderModel);
		if (existingCancelledConsignment != null)
		{
			removeConsignmentFromOrder(orderModel, existingCancelledConsignment);
		}
	}

	private ConsignmentModel getCancelledConsignment(final OrderModel orderModel)
	{
		for (final ConsignmentModel eachConsignment : SetUtils.emptyIfNull(orderModel.getConsignments()))
		{
			if (ConsignmentStatus.CANCELLED.equals(eachConsignment.getStatus()))
			{
				return eachConsignment;
			}
		}

		return null;
	}

	private ConsignmentEntryModel lookupConsignmentEntry(final ConsignmentModel consignmentModel,
			final AbstractOrderEntryModel orderEntryModel)
	{

		for (final ConsignmentEntryModel consignmentEntryModel : SetUtils.emptyIfNull(consignmentModel.getConsignmentEntries()))
		{
			final AbstractOrderEntryModel consOrderEntry = consignmentEntryModel.getOrderEntry();
			if (StringUtils.equalsIgnoreCase(orderEntryModel.getSapLineNumber(), consOrderEntry.getSapLineNumber()))
			{
				return consignmentEntryModel;
			}
		}

		return null;
	}

	private ConsignmentEntryModel findOrCreateConsignmentEntry(final ConsignmentModel consignmentModel,
			final AbstractOrderEntryModel orderEntryModel)
	{
		final ConsignmentEntryModel model = lookupConsignmentEntry(consignmentModel, orderEntryModel);
		return model != null ? model : modelService.create(ConsignmentEntryModel.class);
	}
	
	private void reCalucateUnRejectedItem(final OrderModel orderModel)
	{
		final ConsignmentModel existingCancelledConsignment = getCancelledConsignment(orderModel);
		if (existingCancelledConsignment != null)
		{
			final List<AbstractOrderEntryModel> unRejectedItems = new ArrayList<AbstractOrderEntryModel>();

			for (final ConsignmentEntryModel existingCancelledConsignmentEntryModel : existingCancelledConsignment
					.getConsignmentEntries())
			{
				if (existingCancelledConsignmentEntryModel.getOrderEntry() == null || (existingCancelledConsignmentEntryModel.getOrderEntry() != null && !existingCancelledConsignmentEntryModel.getOrderEntry().getRejected()))
				{
					unRejectedItems.add(existingCancelledConsignmentEntryModel.getOrderEntry());
				}
			}
			if (!unRejectedItems.isEmpty())
			{
				final ConsignmentModel createdStatusconsignment = lookupConsignment(orderModel, ConsignmentStatus.CREATED);
				if (createdStatusconsignment != null)
				{
					modelService.refresh(createdStatusconsignment);
					Set<ConsignmentEntryModel> consignmentEntries = new HashSet<ConsignmentEntryModel>(createdStatusconsignment.getConsignmentEntries());
					for (final AbstractOrderEntryModel orderEntry : unRejectedItems)
					{
						final ConsignmentEntryModel entry = modelService.create(ConsignmentEntryModel.class);
						entry.setOrderEntry(orderEntry);
						entry.setQuantity(orderEntry.getQuantity());
						entry.setConsignment(createdStatusconsignment);
						consignmentEntries.add(entry);
					}
					createdStatusconsignment.setConsignmentEntries(consignmentEntries);
					modelService.save(createdStatusconsignment);
					LOG.info("Creating unrejected consgnment entry for exsting created status consgnment : INC1081239");
				}
				else
				{
					final ConsignmentModel consignmentModel = createConsignment(orderModel, ConsignmentStatus.CREATED.name(),
							unRejectedItems);
					addConsignmentToOrder(orderModel, consignmentModel);
					LOG.info("Creating unrejected consgnment entry for NON-exsting created status consgnment : INC1081239");
				}
				modelService.refresh(orderModel);
			}

		}
	}
}
