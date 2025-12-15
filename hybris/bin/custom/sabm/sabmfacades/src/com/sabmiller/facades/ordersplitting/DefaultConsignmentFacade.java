/**
 *
 */
package com.sabmiller.facades.ordersplitting;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.commercefacades.order.data.ConsignmentEntryData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.ConsignmentService;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;

import jakarta.annotation.Resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.notification.service.NotificationService;
import com.sabmiller.core.order.SabmB2BOrderService;
import com.sabmiller.core.ordersplitting.SabmConsignmentService;
import com.sabmiller.core.util.SabmDateUtils;
import com.sabmiller.core.util.SabmStringUtils;
import com.sabmiller.facades.util.SabmFeatureUtil;

/**
 * @author joshua.a.antony
 *
 */
public class DefaultConsignmentFacade implements ConsignmentFacade
{

	private static final Logger LOG = LoggerFactory.getLogger(DefaultConsignmentFacade.class.getName());

	@Resource(name = "b2bOrderService")
	private SabmB2BOrderService orderService;

	@Resource(name = "sabmConsignmentService")
	private SabmConsignmentService consignmentService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "sabmFeatureUtil")
	private SabmFeatureUtil sabmFeatureUtil;

	@Resource(name = "eventService")
	private EventService eventService;

	@Resource(name = "notificationService")
	private NotificationService notificationService;


	@Override
	public void processDeliveryConsignment(final ConsignmentData consignmentData)
	{
		//LOG.debug("In processDeliveryConsignment(). consignmentData : " + ReflectionToStringBuilder.toString(consignmentData));
		boolean needShufflingEntiresAcrossConsignments = true;
		final String deliveryActionCode = consignmentData.getDeliveryActionCode();
		final boolean completionIndicator = consignmentData.getEntries().isEmpty() ? false : consignmentData.getEntries().get(0).isCompletionIndicator(); //TODO : JOSHUA fix this
		final OrderModel orderModel = fetchOrder(consignmentData);

		if (orderModel != null)
		{
			final ConsignmentModel existingConsignment = orderService.lookupConsignment(orderModel, consignmentData.getCode());

			final boolean consignmentExist = existingConsignment != null;

			final Date beingPickedDate = new Date (); // this indicate when the consignment has been picked by SAP (status -> Processing in Hybris)
			/*
			  one consignment should alwasys exists once an order is placed in Hybris with code and status - CREATED
			  SAP will generate new consginment number, e.g 123456790
			 */
			if (!consignmentExist)
			{
				LOG.debug("There is no consignment with id {} in order {}. Creating a new consignment! ", consignmentData.getCode(),
						orderModel.getCode());

				final ConsignmentModel consignmentModel = consignmentService.createConsignment(consignmentData.getCode(),
						consignmentData.getTrackingID(), beingPickedDate, null, consignmentData.getStatus(), orderModel);
				for (final ConsignmentEntryData consignmentEntry : consignmentData.getEntries())
				{
					consignmentService.attachProductToConsignment(orderModel, consignmentModel, consignmentEntry.getQuantity(),
							consignmentEntry.getShippedQuantity(), consignmentEntry.getProductCode(), consignmentEntry.getLineNumber(),
							consignmentEntry.getDeliveryItemNumber());
				}
				modelService.save(consignmentModel);
				consignmentService.addConsignmentToOrder(orderModel, consignmentModel);


			}
			else
			{
				LOG.debug("Found consignment {} in the order!", consignmentData.getCode());

				/** deliveryActionCode = 01, 02 denotes that its an consignment update **/
				if ("02".equals(deliveryActionCode) || "01".equals(deliveryActionCode))
				{
					LOG.debug("deliveryActionCode : 01/02. Updating the consignment entries");
					for (final ConsignmentEntryData consignmentEntry : consignmentData.getEntries())
					{
						consignmentService.attachProductToConsignment(orderModel, existingConsignment, consignmentEntry.getQuantity(),
								consignmentEntry.getShippedQuantity(), consignmentEntry.getProductCode(),
								consignmentEntry.getLineNumber(), consignmentEntry.getDeliveryItemNumber());
					}
					existingConsignment.setConsignmentBeingPickedDate(beingPickedDate);

					modelService.save(existingConsignment);
					modelService.refresh(existingConsignment);
               Set<ConsignmentEntryModel> toBeDeletedConsignmentEntries = new HashSet<ConsignmentEntryModel>();
               Set<ConsignmentEntryModel> updatedConsignmentEntries = new HashSet<ConsignmentEntryModel>();
               for(ConsignmentEntryModel consignmentEntryModel : existingConsignment.getConsignmentEntries()){
                   if(consignmentEntryModel.getShippedQuantity() == 0){
                       toBeDeletedConsignmentEntries.add(consignmentEntryModel);
                   }else{
                       updatedConsignmentEntries.add(consignmentEntryModel);
                   }
               }
               modelService.removeAll(toBeDeletedConsignmentEntries);
               existingConsignment.setConsignmentEntries(updatedConsignmentEntries);
               modelService.save(existingConsignment);
               modelService.refresh(existingConsignment);
				}

				else
				{
					/**
					 * deliveryActionCode = 03 indicates that this is a delivery cancel request. Hence, the consignment state
					 * need to marked as CREATED
					 **/
					final ConsignmentStatus status = deriveConsignmentStatus(deliveryActionCode, completionIndicator);
					if (status != null)
					{
						LOG.debug("deliveryActionCode :{}. Updating the consignment status to {} ", deliveryActionCode, status);
						existingConsignment.setStatus(status);
						modelService.save(existingConsignment);

						if (ConsignmentStatus.CREATED.equals(status))
						{
							consignmentService.mergeConsignments(orderModel, existingConsignment);
						}
					}
					needShufflingEntiresAcrossConsignments = false;
				}
			}
			consignmentService.recalculateConsignments(orderModel, needShufflingEntiresAcrossConsignments);
			modelService.save(orderModel);
		}
		else
		{
			LOG.error("order not found in hybris with Sales order number", consignmentData.getSalesOrderNumber());
		}

	}


	private ConsignmentStatus deriveConsignmentStatus(final String deliveryActionCode, final boolean completionIndicator)
	{
		LOG.debug("In deriveConsignmentStatus(). deliveryActionCode : {} , completionIndicator : {} ", deliveryActionCode,
				completionIndicator);
		if ("03".equals(deliveryActionCode))
		{
			return ConsignmentStatus.CREATED;
		}
		return null;
	}



	/**
	 * The Dispatch cosignment is the follow up step of the delivery consignment, i.e - it is assumed that there is
	 * already a delivery consignment in place before this method invocation. The dispatch consignment acts on the
	 * delivery consignment (based on the delivery number) and is responsible for changing the status of the existing
	 * consignment.
	 *
	 * Also, since the Dispatch consignment is the source of truth, all the entries in the existing consignment are
	 * replaced with the entries that are sent in the request to the Dispatch service.
	 *
	 * Finally, this method invokes the recalculateConsignments() in the {@link ConsignmentService} that performs some
	 * utilites like removing empty consignments etc...
	 */
	@Override
	public void processDispatchConsignment(final ConsignmentData consignmentData)
	{
		final ConsignmentStatus consignmentStatus = consignmentData.getStatus();

		final OrderModel orderModel = orderService.getOrderBySapSalesOrderNumber(consignmentData.getSalesOrderNumber());

		final Date dispatchedDate = new Date (); // this indicate when the consignment has been dispatched by SAP (status -> Shipped in Hybirs

		if (orderModel != null)
		{
			final ConsignmentModel existingConsignment = orderService.lookupConsignment(orderModel, consignmentData.getCode());
			if (existingConsignment != null)
			{
			LOG.debug("processDispatchConsignment Consignment Data status {}",consignmentData.getStatus().getCode());
				existingConsignment.setStatus(consignmentStatus);
				existingConsignment.setConsignmentDispatchedDate(dispatchedDate);
				modelService.save(existingConsignment);
				final Set<ConsignmentEntryModel> validConsignmentEntries = new HashSet<ConsignmentEntryModel>();
				for (final ConsignmentEntryData entryData : consignmentData.getEntries())
				{
					final AbstractOrderEntryModel orderEntryModel = orderService.lookupOrderEntry(orderModel,
							entryData.getProductCode(), entryData.getLineNumber());
					for (final ConsignmentEntryModel existingConsignmentEntry : existingConsignment.getConsignmentEntries())
					{
						if (existingConsignmentEntry.getOrderEntry() != null && orderEntryModel != null
								&& StringUtils.equalsIgnoreCase(
										SabmStringUtils.stripLeadingZeroes(existingConsignmentEntry.getOrderEntry().getSapLineNumber()),
										SabmStringUtils.stripLeadingZeroes(orderEntryModel.getSapLineNumber())))
						{
							validConsignmentEntries.add(existingConsignmentEntry);
							existingConsignmentEntry.setShippedQuantity(entryData.getShippedQuantity());
							modelService.save(existingConsignmentEntry);

							break;
						}
					}
				}


				removeInvalidConsignmentEntries(existingConsignment, validConsignmentEntries);
				existingConsignment.setConsignmentEntries(validConsignmentEntries);

				modelService.save(existingConsignment);
				modelService.refresh(existingConsignment);
				LOG.debug("processDispatchConsignment existingConsignment code {} status {}",existingConsignment.getCode(),existingConsignment.getStatus()!=null?existingConsignment.getStatus().getCode():null);

				modelService.refresh(orderModel);
				consignmentService.recalculateConsignments(orderModel, false);

				modelService.save(orderModel);
			}
			else
			{
				LOG.info("existingConsignment not found in hybris with delivery number- processing as spectrum split order",
						consignmentData.getSalesOrderNumber());
				processSpectrumSplitOrders(orderModel, consignmentData);
			}
		}
		else

		{
			LOG.info("order not found in hybris with Sales order number", consignmentData.getSalesOrderNumber());

		}

	}

	private void removeInvalidConsignmentEntries(final ConsignmentModel existingConsignment,
			final Set<ConsignmentEntryModel> validConsignmentEntries)
	{
		try
		{
			final List<ConsignmentEntryModel> invalidConsignmentEntries = new ArrayList<ConsignmentEntryModel>();
			for (final ConsignmentEntryModel entry : existingConsignment.getConsignmentEntries())
			{
				if (!validConsignmentEntries.contains(entry))
				{
					invalidConsignmentEntries.add(entry);
				}
			}
			modelService.removeAll(invalidConsignmentEntries);
			modelService.refresh(existingConsignment);
		}
		catch (final Exception e)
		{
			LOG.error("Error occured removing invalid consignment entries ", e);
		}
	}


	private OrderModel fetchOrder(final ConsignmentData consignmentData)
	{
		if (!StringUtils.isBlank(consignmentData.getSalesOrderNumber()))
		{
			return orderService.getOrderBySapSalesOrderNumber(consignmentData.getSalesOrderNumber());
		}
		return orderService.getOrderByConsignment(consignmentData.getCode());
	}




	/*
	  below scenario is typically used one new consignment (order split ) happens in SAP which create a second(new) consignment in Hybris.
	 */
	private void processSpectrumSplitOrders(final OrderModel orderModel, final ConsignmentData consignmentData)
	{

		final List<ConsignmentModel> existingConsWithDifferentTrackId = new ArrayList<>();
		// spectrum split order
		if (CollectionUtils.size(orderModel.getConsignments()) > 0)
		{
			for (final ConsignmentModel existingConsignment : orderModel.getConsignments())
			{
				if (ConsignmentStatus.PROCESSING.equals(existingConsignment.getStatus()))
				{
					existingConsWithDifferentTrackId.add(existingConsignment);
				}
			}
		}

		final Date dispatchedDate = new Date () ; // this indicate when the consignment has been dispatched from warehouse by SAP (status -> Shipped in Hybris)

		final ConsignmentModel consignmentModel = consignmentService.createConsignment(consignmentData.getCode(),
				consignmentData.getTrackingID(), null, dispatchedDate, consignmentData.getStatus(), orderModel);

		for (final ConsignmentEntryData consignmentEntry : consignmentData.getEntries())
		{
			consignmentService.attachProductToConsignment(orderModel, consignmentModel, consignmentEntry.getQuantity(),
					consignmentEntry.getShippedQuantity(), consignmentEntry.getProductCode(), consignmentEntry.getLineNumber(),
					consignmentEntry.getDeliveryItemNumber());
			for (final ConsignmentModel existingCon : ListUtils.emptyIfNull(existingConsWithDifferentTrackId))
			{
				final Set<ConsignmentEntryModel> entries = existingCon.getConsignmentEntries();
				final Set<ConsignmentEntryModel> entriesToDelete = new HashSet<>();
				for (final ConsignmentEntryModel entry : entries)
				{
					if (entry.getOrderEntry() != null
							&& StringUtils.equalsIgnoreCase(SabmStringUtils.stripLeadingZeroes(entry.getOrderEntry().getSapLineNumber()),
									SabmStringUtils.stripLeadingZeroes(consignmentEntry.getLineNumber())))
					{
						entriesToDelete.add(entry);
					}
				}
				if (entriesToDelete.size() > 0)
				{
					modelService.removeAll(entriesToDelete);
					modelService.refresh(existingCon);
				}
			}

		}

		final Set<ConsignmentModel> consignmentsToDelete = new HashSet<>();
		for (final ConsignmentModel model : ListUtils.emptyIfNull(existingConsWithDifferentTrackId))
		{
			if (model != null && CollectionUtils.sizeIsEmpty(model.getConsignmentEntries()))
			{
				consignmentsToDelete.add(model);
			}
		}

		if (consignmentsToDelete.size() > 0)
		{
			modelService.removeAll(consignmentsToDelete);
			modelService.refresh(orderModel);
		}

		consignmentService.addConsignmentToOrder(orderModel, consignmentModel);


		consignmentService.recalculateCancelledConsignments(orderModel);

		consignmentService.deleteOrphanConsignments(orderModel);

		consignmentService.setOrderStatusByConsignment(orderModel);


	}


	public void updateConsignmentStatusFromRetriever(final ConsignmentData consignmentData) throws ConsignmentProcessException  {

		ConsignmentModel consignmentModel =null;
		// step 1 , fetch  consignment from system, if the consignment doesn't exit or  status is not in Dispatched, return error back to Retriever



		final List <ConsignmentModel> consignments = consignmentService.lookupConsignmentByCode(consignmentData.getCode());


		/*
		if consignment list contains 0 or more than one consignment, throw exception
		 */
		if(consignments.size() > 1) {
			LOG.error("cosngiment code: " + consignmentData.getCode() + " is not unique ", "");
		}

		if(consignments.size() < 1) {
			LOG.error("cosngiment code: " + consignmentData.getCode() + " has not been found ", "");
		}

		else {

			consignmentModel = consignments.get(0);
			if (ConsignmentStatus.CREATED.equals(consignmentModel.getStatus())||
				ConsignmentStatus.PROCESSING.equals(consignmentModel.getStatus())||
					//	ConsignmentStatus.NOTDELIVERED.equals(consignmentModel.getStatus())||
				ConsignmentStatus.DELIVERED.equals(consignmentModel.getStatus())||
				ConsignmentStatus.CANCELLED.equals(consignmentModel.getStatus())||
				ConsignmentStatus.RETURNED.equals(consignmentModel.getStatus())){
				throw new ConsignmentProcessException("cosngiment: " + consignmentModel.getCode() + " has not been process by SAP and the status is :  "+consignmentModel.getStatus().getCode(),"");
			}
		}



		/*Step 2, if none of errors above been throw, start to update consignment from Retriver request
		*  Retriever return three status : INTRANSIT,DELIVERED,NOTDELIVERED
		 */

		if (consignmentModel != null && consignmentModel.getOrder() != null && consignmentModel.getOrder().getUnit() != null
				&& consignmentData.getStatus() != null)
		{

				if (consignmentData.getStatus().equals(ConsignmentStatus.INTRANSIT)) {

				final boolean isFirstTransitStatusChange = checkIfTransitUpdateIsNew(consignmentModel);
					if (isFirstTransitStatusChange) {
						consignmentModel.setConsignmentInTransitDate(new Date());
					}

				if (BooleanUtils.isTrue(consignmentData.isInTransitArrived()))
				{
					consignmentModel.setInTransitArrived(true);
					consignmentModel.setInTransitNextInQueue(false);
					consignmentModel.setEstimatedArrivedTime(consignmentData.getEstimatedArrivedTime());
					consignmentModel.setConsignmentDeliveredDate(null);
					consignmentModel.setStatus(ConsignmentStatus.INTRANSIT);
					modelService.save(consignmentModel);
					modelService.refresh(consignmentModel);
				}
				else
				{
					final boolean isETAChanged = !isFirstTransitStatusChange && checkIfETAChanged(consignmentModel, consignmentData);

					if (BooleanUtils.isTrue(consignmentData.isInTransitNextInQueue())) {
						final boolean isConsignmentAlreadyNextInQueue = BooleanUtils.isTrue(consignmentModel.getInTransitNextInQueue());
						consignmentModel.setInTransitNextInQueue(true);
						consignmentModel.setEstimatedArrivedTime(consignmentData.getEstimatedArrivedTime());
						consignmentModel.setInTransitArrived(false);
						consignmentModel.setConsignmentDeliveredDate(null);
						consignmentModel.setStatus(ConsignmentStatus.INTRANSIT);
						modelService.save(consignmentModel);
						modelService.refresh(consignmentModel);

						if (!isConsignmentAlreadyNextInQueue)
						{
							notificationService.sendOrderNextInQueueDeliveryNotification(consignmentModel);
						}
					}
					if (BooleanUtils.isFalse(consignmentData.isInTransitNextInQueue())) {

						consignmentModel.setInTransitNextInQueue(false);
						consignmentModel.setInTransitArrived(false);
						consignmentModel.setEstimatedArrivedTime(consignmentData.getEstimatedArrivedTime());
						consignmentModel.setConsignmentDeliveredDate(null);
						consignmentModel.setStatus(ConsignmentStatus.INTRANSIT);

						try{
							modelService.save(consignmentModel);
							modelService.refresh(consignmentModel);
						}

						catch(ModelSavingException ex)
						{
							LOG.error("Error while saving consignment");
						}

						if (isFirstTransitStatusChange)
						{
							notificationService.sendOrderETANotification(consignmentModel);
						}

					}

						if (isETAChanged)
						{
							notificationService.sendOrderETAChangesNotification(consignmentModel);
						}

					}
					consignmentService.setOrderStatusByConsignment((OrderModel) consignmentModel.getOrder());

				}
				if (consignmentData.getStatus().equals(ConsignmentStatus.DELIVERED)) {
                    consignmentModel.setNotDeliveredReason(consignmentData.getNotDeliveredReason());
                    consignmentModel.setNotDeliveredReason(consignmentData.getNotDeliveredReason());
					consignmentModel.setStatus(ConsignmentStatus.DELIVERED);
					consignmentModel.setEstimatedArrivedTime(null);

					consignmentModel.setInTransitNextInQueue(false);
					consignmentModel.setInTransitArrived(false);
					consignmentModel.setConsignmentDeliveredDate(consignmentData.getConsignmentDeliveredDate());
					consignmentModel.setSignature(consignmentData.getSignature());

					modelService.save(consignmentModel);
					modelService.refresh(consignmentModel);
					consignmentService.setOrderStatusByConsignment((OrderModel) consignmentModel.getOrder());

					notificationService.sendOrderDeliveredNotification(consignmentModel);


				}
				if (consignmentData.getStatus().equals(ConsignmentStatus.NOTDELIVERED)) {
                    consignmentModel.setNotDeliveredReason(consignmentData.getNotDeliveredReason());
					consignmentModel.setStatus(ConsignmentStatus.NOTDELIVERED);
					consignmentModel.setEstimatedArrivedTime(null);
					consignmentModel.setInTransitNextInQueue(false);
					consignmentModel.setInTransitArrived(false);
					consignmentModel.setConsignmentDeliveredDate(null);

					modelService.save(consignmentModel);
					modelService.refresh(consignmentModel);
					consignmentService.setOrderStatusByConsignment((OrderModel) consignmentModel.getOrder());
					notificationService.sendOrderUnableToDeliverNotification(consignmentModel);

				}
			//}
		}

	}




	/**
	 * @param consignmentModel
	 * @return
	 */
	private boolean checkIfETAChanged(final ConsignmentModel consignmentModel, final ConsignmentData consignmentData)
	{
		try
		{
		Date previousEstimatedEndTime = null;

		final Date currentEstimatedTime = consignmentData.getEstimatedArrivedTime();


		if (consignmentModel.getEstimatedArrivedTime() != null)
		{
			final Date roundedDate = SabmDateUtils.roundDateToNearestQuarterHour(consignmentModel.getEstimatedArrivedTime());

			if (BooleanUtils.isTrue(consignmentModel.getInTransitNextInQueue()))
			{

				previousEstimatedEndTime = SabmDateUtils.plusMinutes(roundedDate,
						Integer.valueOf(Config.getString("trackorder.ETA.time.window.minnutes.nextDelivery", "")));
			}
			else
			{
				previousEstimatedEndTime = SabmDateUtils.plusMinutes(roundedDate,
						Integer.valueOf(Config.getString("trackorder.ETA.time.window.minnutes", "")));
			}
		}
		LOG.debug("ETA Change Check previous ETA {} current ETA",previousEstimatedEndTime,currentEstimatedTime);

		final long hours = TimeUnit.MILLISECONDS.toHours(currentEstimatedTime.getTime() - previousEstimatedEndTime.getTime());

		LOG.debug("ETA Change Check difference hours {}",hours);
		return hours > 2;
		}
		catch (final Exception e)
		{
			LOG.error("Error while checking if ETA changed", e);
		}
		return false;
	}


	/**
	 * @param consignmentModel
	 * @return
	 */
	private boolean checkIfTransitUpdateIsNew(final ConsignmentModel consignmentModel)
	{
		return !ConsignmentStatus.INTRANSIT.equals(consignmentModel.getStatus());
	}





}
