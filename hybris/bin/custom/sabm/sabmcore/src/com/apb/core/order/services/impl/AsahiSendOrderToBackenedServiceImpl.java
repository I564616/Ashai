package com.apb.core.order.services.impl;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Date;

import jakarta.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sabmiller.core.enums.AsahiProcessObject;
import com.apb.core.model.ProcessingJobLogModel;
import com.apb.core.order.services.AsahiSendOrderToBackenedService;
import com.apb.core.process.log.service.AsahiProcessLogService;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.integration.data.ApbOrderResponseData;
import com.apb.integration.order.service.AsahiOrderIntegrationService;


/**
 * Class which setup and sends the orders to Backened system.
 */
public class AsahiSendOrderToBackenedServiceImpl implements AsahiSendOrderToBackenedService
{

	 private static final String SGA_UID = "sga";
    private static final String APB_UID = "apb";

	@Resource(name = "asahiOrderIntegrationService")
	private AsahiOrderIntegrationService asahiOrderRestService;

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Resource(name = "processLogService")
	private AsahiProcessLogService processLogService;

	@Resource(name = "modelService")
	private ModelService modelService;

	private static final String STATUS_TO_UPDATE_SEND_ORDER = "asahi.status.to.update.after.order.sent.";
	private static final String STATUS_FAILED_TO_SEND_ORDER = "asahi.status.to.update.after.order.not.sent.";

	private static final String PROCESS_FAIL_STATUS = "FAIL";
	private static final String PROCESS_SUCCESS_STATUS = "PROCESSED";
	private static final String RESPONSE_SUCCESS_STATUS = "200";

	private static final Logger LOG = LogManager.getLogger(AsahiSendOrderToBackenedServiceImpl.class);

	/*
	 * This method will setup and send order to Backened System.
	 *
	 * @see
	 * com.apb.core.order.services.AsahiSendOrderToBackenedService#sendOrderToBackendSystem(de.hybris.platform.core.model
	 * .order.OrderModel)
	 */
	@Override
	public void sendOrderToBackendSystem(final OrderModel orderModel)
	{
		LOG.info("Inside send order to backend system");
		final String updateOrderStatus = this.asahiConfigurationService
				.getString(STATUS_TO_UPDATE_SEND_ORDER + orderModel.getSite().getUid(), "SENT_TO_BACKEND");
		final String updateFailedOrderStatus = this.asahiConfigurationService
				.getString(STATUS_FAILED_TO_SEND_ORDER + orderModel.getSite().getUid(), "SENT_TO_BACKEND_FAILED");

		boolean sendSgaOrder = orderModel.getSite().getUid().intern() == SGA_UID &&
            null != orderModel.getStatus() &&
            (orderModel.getStatus().equals(OrderStatus.PAYMENT_APPROVED) || orderModel.getStatus().equals(OrderStatus.PAYMENT_AUTHORIZED));
    boolean sendApbOrder = orderModel.getSite().getUid().intern() == APB_UID &&
            null != orderModel.getStatus() &&
            (orderModel.getStatus().equals(OrderStatus.PAYMENT_APPROVED) || orderModel.getStatus().equals(OrderStatus.PAYMENT_CAPTURED));

    if (sendSgaOrder || sendApbOrder)
		{
			try
			{
				final ProcessingJobLogModel processJobLog = getProcessJobLog(orderModel);

				LOG.info("Before send Order ");
				final ApbOrderResponseData orderResponseData = asahiOrderRestService.sendOrder(orderModel);
				LOG.info("After send Order");
				LOG.info("Order Status code is " + orderResponseData.getStatusCode());

				if (null!=orderResponseData && orderResponseData.getStatusCode() == 200)
				{
					LOG.info("Inside if with order status as 200");
					orderModel.setStatus(OrderStatus.valueOf(updateOrderStatus));
					LOG.info("After setting order status in success scenario--" + orderModel.getStatus().toString());
					processJobLog.setLastStatus(PROCESS_SUCCESS_STATUS);
				}
				else
				{
					LOG.info("Order status not equal to 200");
					orderModel.setErrorMsg(orderResponseData.getStatusCode().toString());
					orderModel.setStatus(OrderStatus.valueOf(updateFailedOrderStatus));
					LOG.info("After setting order status to processing error --" + orderModel.getStatus().toString());
					processJobLog.setLastStatus(PROCESS_FAIL_STATUS);
				}

				processJobLog.setProcessTime(new Date());
				saveProcessJobLog(orderModel, processJobLog);
			}
			catch (final Exception e)
			{
				orderModel.setStatus(OrderStatus.valueOf(updateFailedOrderStatus));
				LOG.error("exception in send order to backened", e);
			}
		}


	}

	/**
	 * This Method will save process job log
	 *
	 * @param orderModel
	 * @param processJobLog
	 */
	private void saveProcessJobLog(final OrderModel orderModel, final ProcessingJobLogModel processJobLog)
	{
		modelService.save(orderModel);
		modelService.save(processJobLog);
	}

	/**
	 * Method which Create and return the process job log.
	 *
	 * @param orderModel
	 * @return
	 */
	private ProcessingJobLogModel getProcessJobLog(final OrderModel orderModel)
	{
		ProcessingJobLogModel processJobLog = processLogService.findProcessLogById(AsahiProcessObject.ORDER, orderModel.getCode());
		if (processJobLog == null)
		{
			processJobLog = modelService.create(ProcessingJobLogModel.class);
			processJobLog.setObjectId(orderModel.getCode());
			processJobLog.setProcessCount(0);
			processJobLog.setObjectType(AsahiProcessObject.ORDER);
		}
		return processJobLog;
	}

}
