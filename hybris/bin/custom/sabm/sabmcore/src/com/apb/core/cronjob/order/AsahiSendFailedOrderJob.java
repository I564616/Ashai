package com.apb.core.cronjob.order;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.apb.core.card.payment.AsahiPaymentCaptureRequestService;
import com.sabmiller.core.enums.AsahiProcessObject;
import com.apb.core.model.AsahiSendFailedOrderCronJobModel;
import com.apb.core.model.ProcessingJobLogModel;
import com.apb.core.order.history.service.ApbOrderHistoryService;
import com.apb.core.process.log.service.AsahiProcessLogService;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.services.AsahiOrderService;
import com.apb.integration.data.ApbOrderResponseData;
import com.apb.integration.order.service.AsahiOrderIntegrationService;


/**
 * <p>
 * This class will pickup and send failed orders to Backened system.
 * </p>
 */
public class AsahiSendFailedOrderJob extends AbstractJobPerformable<AsahiSendFailedOrderCronJobModel>
{

	private static final Logger LOGGER = LogManager.getLogger(AsahiSendFailedOrderJob.class);
	private static final String SEND_ORDER_RETRY_COUNT = "send.order.job.retry.count.";
	private static final String PROCESS_FAIL_STATUS = "FAIL";
	private static final String PROCESS_SUCCESS_STATUS = "PROCESSED";
	private static final String STATUS_TO_UPDATE_SEND_ORDER = "asahi.status.to.update.after.order.sent.";
	private static final String STATUS_FAILED_TO_SEND_ORDER = "asahi.status.to.update.after.order.not.sent.";
	private static final String PICKUP_FAILED_ORDER = "send.order.job.send.status.";
	private static final String RESPONSE_SUCCESS_STATUS = "success";
	private static final String STATUS_TO_SEND_PAYMENT = "payment.rest.check.status.";
	private static final String STATUS_TO_UPDATE_PAYMENT_ORDER = "payment.rest.update.status.";
	private static final String STATUS_TO_FAILED_PAYMENT_ORDER = "payment.rest.update.failed.status.";
	private static final String RESPONSE_STATUS_CODE = "status";
	private static final String SUCCESS_RESPONSE = "200";
	private static final String ORDER_STATUS_UPDATED = "Order status set to PAYMENT APPROVED";

	@Resource(name = "asahiOrderIntegrationService")
	private AsahiOrderIntegrationService asahiOrderRestService;

	@Resource(name = "asahiOrderService")
	private AsahiOrderService asahiOrderService;

	@Resource(name = "processLogService")
	private AsahiProcessLogService processLogService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Resource(name = "asahiPaymentCaptureRequestService")
	private AsahiPaymentCaptureRequestService asahiPaymentCaptureRequestService;

	@Resource(name = "orderHistoryService")
	private ApbOrderHistoryService orderHistoryService;




	/**
	 * This method processing send placed order.
	 */
	@Override
	public PerformResult perform(final AsahiSendFailedOrderCronJobModel cronJobModel)
	{

		try
		{
			LOGGER.info("cms site " + " site id " + cronJobModel.getCmsSite().getName() + cronJobModel.getCmsSite().getPk());
			return processOrderPayment(cronJobModel);

		}
		catch (final Exception ex)
		{
			LOGGER.error(" Error in Send order cron job ", ex);
			return new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED);
		}
	}

	private PerformResult processOrderPayment(final AsahiSendFailedOrderCronJobModel cronJobModel)
	{
		final String retryCount = this.asahiConfigurationService
				.getString(SEND_ORDER_RETRY_COUNT + cronJobModel.getCmsSite().getUid(), "5");
		final String orderStatusList = this.asahiConfigurationService
				.getString(STATUS_TO_SEND_PAYMENT + cronJobModel.getCmsSite().getUid(), "");
		final String changeOrderStatus = this.asahiConfigurationService
				.getString(STATUS_TO_UPDATE_PAYMENT_ORDER + cronJobModel.getCmsSite().getUid(), "PAYMENT_CAPTURED");
		final String failedOrderStatus = this.asahiConfigurationService
				.getString(STATUS_TO_FAILED_PAYMENT_ORDER + cronJobModel.getCmsSite().getUid(), "PAYMENT_NOT_CAPTURED");

		final List<OrderModel> orderPayment = asahiOrderService.getOrderList(orderStatusList, cronJobModel.getCmsSite());

		if (CollectionUtils.isNotEmpty(orderPayment))
		{
			LOGGER.info("total order count " + orderPayment.size());
			final int retryCounts = Integer.parseInt(retryCount);
			for (final OrderModel orderModel : orderPayment)
			{
				if (clearAbortRequestedIfNeeded(cronJobModel))
				{
					LOGGER.debug("Cronjob Abort Request from Process Order Payment");
					return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
				}

				try
				{
					ProcessingJobLogModel processJobLog = processLogService.findProcessLogById(AsahiProcessObject.PAYMENT,
							orderModel.getCode());
					if (processJobLog == null)
					{
						processJobLog = modelService.create(ProcessingJobLogModel.class);
						processJobLog.setObjectId(orderModel.getCode());
						processJobLog.setProcessCount(0);
						processJobLog.setObjectType(AsahiProcessObject.PAYMENT);
					}
					final int processCount = processJobLog.getProcessCount();
					if (processCount < retryCounts)
					{

						LOGGER.debug("Befor capture request from cronjob..!!");
						final HashMap<String, String> responseMap = asahiPaymentCaptureRequestService.placeCaptureRequest(orderModel);

						LOGGER.debug("response status code!!! from cronjob " + responseMap.get(RESPONSE_STATUS_CODE));

						if (responseMap.get(RESPONSE_STATUS_CODE).equalsIgnoreCase(SUCCESS_RESPONSE))
						{
							LOGGER.debug("Success Response from fz ");
							if (Boolean.parseBoolean(responseMap.get(RESPONSE_SUCCESS_STATUS)))
							{
								orderModel.setStatus(OrderStatus.valueOf(changeOrderStatus));
								processJobLog.setLastStatus(PROCESS_SUCCESS_STATUS);

								final OrderModel snapshot = null; //orderHistoryService.createHistorySnapshot(orderModel);
								orderHistoryService.createOrderHistoryEntry(orderModel, snapshot, ORDER_STATUS_UPDATED);
							}
						}
						else
						{
							processJobLog.setLastStatus(PROCESS_FAIL_STATUS);
						}
					}
					else if (processCount == retryCounts)
					{
						orderModel.setStatus(OrderStatus.valueOf(failedOrderStatus));
						processJobLog.setLastStatus(PROCESS_SUCCESS_STATUS);
					}

					processJobLog.setProcessTime(new Date());
					processJobLog.setProcessCount(processCount + 1);
					modelService.save(orderModel);
					modelService.save(processJobLog);

				}
				catch (final Exception e)
				{
					LOGGER.error("exception in cron job", e);
				}
			}
		}
		final boolean isJobAborted = processSendOrder(cronJobModel);
		if (isJobAborted)
		{
			LOGGER.debug("Cronjob Abort Request from Process Send Order");
			return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
		}
		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}

	/**
	 * This method processing the send placed order. request from Cron Job.
	 */
	private boolean processSendOrder(final AsahiSendFailedOrderCronJobModel cronJobModel)
	{
		final String retryCount = this.asahiConfigurationService
				.getString(SEND_ORDER_RETRY_COUNT + cronJobModel.getCmsSite().getUid(), "5");
		final String orderStatusList = this.asahiConfigurationService
				.getString(PICKUP_FAILED_ORDER + cronJobModel.getCmsSite().getUid(), "");
		final String changeOrderStatus = this.asahiConfigurationService
				.getString(STATUS_TO_UPDATE_SEND_ORDER + cronJobModel.getCmsSite().getUid(), "SENT_TO_BACKEND");
		final String failedOrderStatus = this.asahiConfigurationService
				.getString(STATUS_FAILED_TO_SEND_ORDER + cronJobModel.getCmsSite().getUid(), "SENT_TO_BACKEND_FAILED");


		final List<OrderModel> placedOrder = asahiOrderService.getOrderList(orderStatusList, cronJobModel.getCmsSite());

		if (CollectionUtils.isNotEmpty(placedOrder))
		{
			final int retryCounts = Integer.parseInt(retryCount);
			for (final OrderModel orderModel : placedOrder)
			{
				if (clearAbortRequestedIfNeeded(cronJobModel))
				{
					LOGGER.debug("Aborting Send Order Job");
					return true;
				}

				try
				{
					ProcessingJobLogModel processJobLog = processLogService.findProcessLogById(AsahiProcessObject.ORDER,
							orderModel.getCode());
					if (processJobLog == null)
					{
						processJobLog = modelService.create(ProcessingJobLogModel.class);
						processJobLog.setObjectId(orderModel.getCode());
						processJobLog.setProcessCount(0);
						processJobLog.setObjectType(AsahiProcessObject.ORDER);
					}
					int processCount = processJobLog.getProcessCount();
					if (processCount < retryCounts)
					{
						final ApbOrderResponseData orderResponseData = asahiOrderRestService.sendOrder(orderModel);
						if (orderResponseData.getStatusCode() != 200)
						{
							processJobLog.setLastStatus(PROCESS_FAIL_STATUS);
						}
						else
						{
							if (SUCCESS_RESPONSE.equalsIgnoreCase(orderResponseData.getOrderStatus()))
							{
								orderModel.setStatus(OrderStatus.valueOf(changeOrderStatus));
								processJobLog.setLastStatus(PROCESS_SUCCESS_STATUS);
							}
							else
							{
								processCount = retryCounts - 1;
								orderModel.setErrorMsg(orderResponseData.getFailureReason());
								orderModel.setStatus(OrderStatus.PROCESSING_ERROR);
								processJobLog.setLastStatus(PROCESS_FAIL_STATUS);
							}
						}
						processJobLog.setProcessTime(new Date());
						processJobLog.setProcessCount(processCount + 1);
					}

					else if (processCount == retryCounts)
					{
						// set the status to SENT_TO_BACKEND_FAILED for maximum retry count.
						orderModel.setStatus(OrderStatus.valueOf(failedOrderStatus));
						processJobLog.setLastStatus(PROCESS_SUCCESS_STATUS);
					}
					modelService.save(orderModel);
					modelService.save(processJobLog);
				}
				catch (final Exception e)
				{
					LOGGER.error("exception in send order cron job", e);
				}
			}
		}
		return false;
	}

	@Override
	public boolean isAbortable()
	{
		return true;
	}
}
