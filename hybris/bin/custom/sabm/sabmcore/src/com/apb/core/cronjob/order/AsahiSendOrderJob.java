package com.apb.core.cronjob.order;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.card.payment.AsahiPaymentCaptureRequestService;
import com.apb.core.constants.ApbCoreConstants;
import com.sabmiller.core.enums.AsahiProcessObject;
import com.apb.core.model.ProcessingJobLogModel;
import com.apb.core.order.history.service.ApbOrderHistoryService;
import com.apb.core.process.log.service.AsahiProcessLogService;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.services.AsahiOrderService;
import com.apb.integration.data.ApbOrderResponseData;
import com.apb.integration.order.service.AsahiOrderIntegrationService;


public class AsahiSendOrderJob extends AbstractJobPerformable<CronJobModel>
{

	private static final Logger LOGGER = LoggerFactory.getLogger(AsahiSendOrderJob.class);

	@Resource(name = "asahiOrderIntegrationService")
	AsahiOrderIntegrationService asahiOrderRestService;

	@Resource(name = "asahiOrderService")
	AsahiOrderService asahiOrderService;

	@Resource(name = "processLogService")
	AsahiProcessLogService processLogService;

	@Resource(name = "catalogVersionService")
	private CatalogVersionService catalogVersionService;

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Resource(name = "asahiPaymentCaptureRequestService")
	private AsahiPaymentCaptureRequestService asahiPaymentCaptureRequestService;

	@Resource(name = "orderHistoryService")
	private ApbOrderHistoryService orderHistoryService;

	private static final String SEND_ORDER_JOB_RETRY_COUNT = "send.order.job.retry.count.apb";
	private static final String SEND_ORDER_JOB_SEND_STATUS = "send.order.job.send.status.apb";
	private static final String SEND_ORDER_JOB_SUCCESS_STATUS = "send.order.job.success.status.apb";
	private static final String SEND_ORDER_JOB_FAIL_STATUS = "send.order.job.failed.status.apb";
	private static final String STATUS_TO_SEND_PAYMENT = "payment.rest.check.status.apb";
	private static final String STATUS_TO_UPDATE_PAYMENT_ORDER = "payment.rest.update.status.apb";
	private static final String STATUS_TO_FAILED_PAYMENT_ORDER = "payment.rest.update.failed.status.apb";
	private static final String RESPONSE_STATUS_CODE = "status";
	private static final String RESPONSE_SUCCESS_STATUS = "success";
	private static final String SUCCESS_RESPONSE = "200";

	private static final String PROCESS_FAIL_STATUS = "FAIL";
	private static final String PROCESS_SUCCESS_STATUS = "PROCESSED";
	private static final String CATALOG_VERSION = "Online";
	private static final String ORDER_STATUS_UPDATED = "Order status set to PAYMENT APPROVED";

	private static final String DISABLE_JOB_FOR_SGA_ORDER = "asahi.block.send.order.job.sga";
	private static final boolean DEFAULT_DISABLE_SEND_ORDER_JOB = false;



	/**
	 * This method processing send placed order.
	 */
	@Override
	public PerformResult perform(final CronJobModel cronJobModel)
	{
		final boolean disableSendOrderJob = this.asahiConfigurationService.getBoolean(DISABLE_JOB_FOR_SGA_ORDER,
				DEFAULT_DISABLE_SEND_ORDER_JOB);
		try
		{
			if (!disableSendOrderJob)
			{
				return processOrderPayment(cronJobModel);
			}
			else
			{
				return new PerformResult(CronJobResult.FAILURE, CronJobStatus.FINISHED);
			}
		}
		catch (final Exception ex)
		{
			LOGGER.error(" Error in Send order cron job ", ex);
			return new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED);
		}
	}

	private PerformResult processOrderPayment(final CronJobModel cronJobModel)
	{
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion("apbContentCatalog", CATALOG_VERSION);
		catalogVersionService.setSessionCatalogVersions(Arrays.asList(catalogVersion));

		final String retryCount = this.asahiConfigurationService.getString(SEND_ORDER_JOB_RETRY_COUNT, "5");
		final String orderStatusList = this.asahiConfigurationService.getString(STATUS_TO_SEND_PAYMENT, "PAYMENT_AUTHORIZED");
		final String changeOrderStatus = this.asahiConfigurationService.getString(STATUS_TO_UPDATE_PAYMENT_ORDER,
				"PAYMENT_CAPTURED");
		final String failedOrderStatus = this.asahiConfigurationService.getString(STATUS_TO_FAILED_PAYMENT_ORDER,
				"PAYMENT_NOT_CAPTURED");

		final List<OrderModel> orderPayment = asahiOrderService.getOrderList(orderStatusList, null);

		if (CollectionUtils.isNotEmpty(orderPayment))
		{
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
					if (orderModel.getSite().getUid().equalsIgnoreCase(ApbCoreConstants.APB_SITE_ID))
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

							LOGGER.debug("Befor capture request..!!");
							final HashMap<String, String> responseMap = asahiPaymentCaptureRequestService
									.placeCaptureRequest(orderModel);

							LOGGER.debug("response status code!!!" + responseMap.get(RESPONSE_STATUS_CODE));

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
	private boolean processSendOrder(final CronJobModel cronJobModel)
	{
		final String retryCount = this.asahiConfigurationService.getString(SEND_ORDER_JOB_RETRY_COUNT, "5");
		final String sendOrderStatusList = this.asahiConfigurationService.getString(SEND_ORDER_JOB_SEND_STATUS, "PAYMENT_APPROVED");
		final String successOrderStatus = this.asahiConfigurationService.getString(SEND_ORDER_JOB_SUCCESS_STATUS,
				"SENT_TO_BACKEND");
		final String failedOrderStatus = this.asahiConfigurationService.getString(SEND_ORDER_JOB_FAIL_STATUS,
				"SENT_TO_BACKEND_FAILED");
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion("apbContentCatalog", CATALOG_VERSION);
		catalogVersionService.setSessionCatalogVersions(Arrays.asList(catalogVersion));
		final List<OrderModel> placedOrder = asahiOrderService.getOrderList(sendOrderStatusList, null);

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
					if (orderModel.getSite().getUid().equalsIgnoreCase(ApbCoreConstants.APB_SITE_ID))
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
								if (RESPONSE_SUCCESS_STATUS.equalsIgnoreCase(orderResponseData.getOrderStatus()))
								{
									orderModel.setStatus(OrderStatus.valueOf(successOrderStatus));
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
