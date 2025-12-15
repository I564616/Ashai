package com.apb.core.card.payment.impl;

import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.apb.core.card.payment.AsahiPaymentCaptureRequestService;
import com.sabmiller.core.enums.AsahiProcessObject;
import com.apb.core.model.AsahiPaymentTransactionModel;
import com.sabmiller.core.model.AsahiSAMInvoiceModel;
import com.apb.core.model.ProcessingJobLogModel;
import com.apb.core.payment.fz.AsahiPaymentGatewayContext;
import com.apb.core.payment.fz.exceptions.AsahiPaymentApiException;
import com.apb.core.payment.fz.exceptions.AsahiPaymentNetworkException;
import com.apb.core.payment.fz.models.AsahiPaymentCaptureRequest;
import com.apb.core.payment.fz.models.AsahiPaymentResponse;
import com.apb.core.process.log.service.AsahiProcessLogService;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.ApbXSSEncoderUtil;
import com.apb.facades.sam.data.AsahiCaptureResponseData;



/**
 * The Class AsahiPaymentCaptureRequestServiceImpl.
 */
public class AsahiPaymentCaptureRequestServiceImpl implements AsahiPaymentCaptureRequestService
{

	private final static Logger LOG = LogManager.getLogger("AsahiCreditCardPaymentServiceImpl");
	private static final String PAYMENT_TARGET_ACCOUNT_ID = "asahi.payment.target.account.id.";
	private static final String PAYMENT_TARGET_TRANSACTION_ID = "asahi.payment.target.transaction.token.id.";
	private static final String PAYMENT_TARGET_SANDBOX_ENABLED = "asahi.payment.target.sandbox.url.enabled.";
	
	private static final String SAM_PAYMENT_TARGET_ACCOUNT_ID = "asahi.payment.target.account.id.sam.";
	private static final String SAM_PAYMENT_TARGET_TRANSACTION_ID = "asahi.payment.target.transaction.token.id.sam.";
	
	
	private static final String DEFAULT_ACCOUNT_ID = "TESTAsahi";
	private static final String DEFAULT_TOKEN_ID = "0501434920a18146740843221689a77fa7028f76";
	private static final String RESPONSE_STATUS_CODE = "status";
	private static final String RESPONSE_SUCCESS_STATUS = "success";
	private static final String DEFAULT_RESPONSE_STATUS_CODE = "500";
	private static final String DEFAULT_RESPONSE_SUCCESS_STATUS = "false";
	private static final String STATUS_AFTER_SUCCESSFULL_CAPTURE_REQUEST = "asahi.order.status.after.successfull.capture.request.";
	private static final String STATUS_AFTER_FAILED_CAPTURE_REQUEST = "asahi.order.status.after.failed.capture.request.";
	private static final String PROCESS_FAIL_STATUS = "FAIL";
	private static final String PROCESS_SUCCESS_STATUS = "PROCESSED";
	private static final String SUCCESS_RESPONSE = "200";
	private static final String UPDATE_SUCCESS_CAPTURE_STATUS = "true";
	private static final int SUCCESS_STATUS_CODE = 200;

	@Resource(name = "asahiPaymentGatewayContext")
	AsahiPaymentGatewayContext asahiPaymentGatewayContext;

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Resource(name = "asahiPaymentResponse")
	private AsahiPaymentResponse<AsahiPaymentCaptureRequest> asahiPaymentResponse;

	@Resource(name = "asahiPaymentCaptureRequest")
	private AsahiPaymentCaptureRequest asahiPaymentCaptureRequest;

	@Resource
	private CMSSiteService cmsSiteService;

	@Resource(name = "processLogService")
	private AsahiProcessLogService processLogService;

	@Resource(name = "modelService")
	private ModelService modelService;


	@Override
	public HashMap<String, String> placeCaptureRequest(final OrderModel orderModel)
	{
		final HashMap<String, String> responseMap = new HashMap<>();
		AsahiPaymentResponse<AsahiPaymentCaptureRequest> captureRequestResponse = null;


		asahiPaymentGatewayContext.setUsername(this.asahiConfigurationService
				.getString(PAYMENT_TARGET_ACCOUNT_ID + orderModel.getSite().getUid(), DEFAULT_ACCOUNT_ID));
		asahiPaymentGatewayContext.setToken(this.asahiConfigurationService
				.getString(PAYMENT_TARGET_TRANSACTION_ID + orderModel.getSite().getUid(), DEFAULT_TOKEN_ID));
		asahiPaymentGatewayContext.setSandbox(
				this.asahiConfigurationService.getBoolean(PAYMENT_TARGET_SANDBOX_ENABLED + orderModel.getSite().getUid(), false));

		asahiPaymentGatewayContext.setSiteId(orderModel.getSite().getUid());
        if(asahiConfigurationService.getBoolean("asahi.payment.mode.property.reset", true)) {
            asahiPaymentGatewayContext.setDirectDebit(false);
        }
		final List<PaymentTransactionModel> paymentTransactionList = orderModel.getPaymentTransactions();

		try
		{
			if (CollectionUtils.isNotEmpty(paymentTransactionList)
					&& CollectionUtils.isNotEmpty(paymentTransactionList.get(0).getEntries()))
			{
				final String paymentTransactionId = paymentTransactionList.get(0).getEntries().get(0).getCode();
				final double paymentTransactionAmount = paymentTransactionList.get(0).getEntries().get(0).getAmount().doubleValue();

				LOG.info("Payment Transaction id " + paymentTransactionId);
				LOG.info("Payment Transaction amount " + paymentTransactionAmount);
				captureRequestResponse = asahiPaymentCaptureRequest.create(paymentTransactionAmount, paymentTransactionId,
						asahiPaymentGatewayContext);
				LOG.info("Inside AsahiPaymentCaptureRequestServiceImpl after capture request");

			}
			else
			{
				LOG.info("No payment transaction found for the order.");
			}

		}
		catch (IOException | AsahiPaymentNetworkException | AsahiPaymentApiException e)
		{
			LOG.info("Problem while creating capture request.", e);
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Problem while creating capture request.", e);
			}
		}
		catch (final Exception e)
		{
			LOG.info("Unknow Exception while creating capture request.", e);
			if (LOG.isDebugEnabled())
			{
				LOG.debug("UnKnow Exception while creating capture request.", e);
			}
		}

		if (null != captureRequestResponse)
		{
			LOG.info("Inside AsahiPaymentCaptureRequestServiceImpl capture response code : "
					+ captureRequestResponse.getResponseCode());

			responseMap.put(RESPONSE_STATUS_CODE, String.valueOf(captureRequestResponse.getResponseCode()));

			if (null != captureRequestResponse.getResult())
			{
				LOG.info("Inside AsahiPaymentCaptureRequestServiceImpl capture request contains result "
						+ captureRequestResponse.getResult().isSuccessful());
				responseMap.put(RESPONSE_SUCCESS_STATUS, String.valueOf(captureRequestResponse.getResult().isSuccessful()));

				LOG.info("-----------------------------capture request response body----------------------------");
				LOG.info(captureRequestResponse.getResult().toString());
				LOG.info("--------------------------------body end----------------------------------------------");
			}

			else
			{
				responseMap.put(RESPONSE_SUCCESS_STATUS, DEFAULT_RESPONSE_SUCCESS_STATUS);
			}

			LOG.info("-----------------------------capture request response body----------------------------");
			LOG.info(ApbXSSEncoderUtil.encodeValue(captureRequestResponse.getResponseBody()));
			LOG.info("--------------------------------body end----------------------------------------------");

		}
		else
		{
			LOG.info("Inside AsahiPaymentCaptureRequestServiceImpl unsuccessfull capture request since so not updating the order.");
			responseMap.put(RESPONSE_STATUS_CODE, DEFAULT_RESPONSE_STATUS_CODE);
			responseMap.put(RESPONSE_SUCCESS_STATUS, DEFAULT_RESPONSE_SUCCESS_STATUS);
		}
		return responseMap;
	}


	/*
	 * <p> This method is used to setup the capture request for SGA site orders placed via credit cards.</p>
	 *
	 * @param orderModel - order associated with the customer
	 *
	 * @see
	 * com.apb.core.card.payment.AsahiPaymentCaptureRequestService#createCaptureRequest(de.hybris.platform.core.model.
	 * order.OrderModel)
	 */
	@Override
	public void createCaptureRequest(final OrderModel orderModel)
	{
		final String updateCaptureOrderStatus = this.asahiConfigurationService
				.getString(STATUS_AFTER_SUCCESSFULL_CAPTURE_REQUEST + cmsSiteService.getCurrentSite().getUid(), "PAYMENT_CAPTURED");
		final String failedCaptureOrderStatus = this.asahiConfigurationService
				.getString(STATUS_AFTER_FAILED_CAPTURE_REQUEST + cmsSiteService.getCurrentSite().getUid(), "PAYMENT_NOT_CAPTURED");


		if (null != orderModel.getStatus() && orderModel.getStatus().equals(OrderStatus.PAYMENT_AUTHORIZED))
		{

			final ProcessingJobLogModel processJobLog = getProcessJobLog(orderModel);

			final HashMap<String, String> responseMap = this.placeCaptureRequest(orderModel);

			if (responseMap.get(RESPONSE_STATUS_CODE).equalsIgnoreCase(SUCCESS_RESPONSE)
					&& Boolean.parseBoolean(responseMap.get(RESPONSE_SUCCESS_STATUS)))
			{

				orderModel.setStatus(OrderStatus.valueOf(updateCaptureOrderStatus));
				processJobLog.setLastStatus(PROCESS_SUCCESS_STATUS);

			}
			else
			{
				orderModel.setStatus(OrderStatus.valueOf(failedCaptureOrderStatus));
				processJobLog.setLastStatus(PROCESS_FAIL_STATUS);
			}

			saveProcessJobLog(orderModel, processJobLog);

		}

	}




	/**
	 * This method is will save the process Job Log.
	 *
	 * @param orderModel
	 * @param processJobLog
	 * @param processCount
	 */
	private void saveProcessJobLog(final OrderModel orderModel, final ProcessingJobLogModel processJobLog)
	{
		processJobLog.setProcessTime(new Date());
		modelService.save(orderModel);
		modelService.save(processJobLog);
	}


	/**
	 * This method is used to create process job log
	 *
	 * @param orderModel
	 * @return
	 */
	private ProcessingJobLogModel getProcessJobLog(final OrderModel orderModel)
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
		return processJobLog;
	}

	/*
	 * This Method will make capture request for SAM Payment
	 *
	 * @see
	 * com.apb.core.card.payment.AsahiPaymentCaptureRequestService#createSAMPaymentCaptureRequest(com.apb.core.model.
	 * AsahiSAMInvoiceModel)
	 */
	@Override
	public AsahiCaptureResponseData createSAMPaymentCaptureRequest(final AsahiSAMInvoiceModel asahiSAMInvoiceModel)
	{
		AsahiPaymentResponse<AsahiPaymentCaptureRequest> captureRequestResponse = null;
		AsahiCaptureResponseData asahiCaptureResponseData = null;
		asahiPaymentGatewayContext.setUsername(this.asahiConfigurationService
				.getString(SAM_PAYMENT_TARGET_ACCOUNT_ID + cmsSiteService.getCurrentSite().getUid(), DEFAULT_ACCOUNT_ID));
		asahiPaymentGatewayContext.setToken(this.asahiConfigurationService
				.getString(SAM_PAYMENT_TARGET_TRANSACTION_ID + cmsSiteService.getCurrentSite().getUid(), DEFAULT_TOKEN_ID));
		asahiPaymentGatewayContext.setSandbox(this.asahiConfigurationService
				.getBoolean(PAYMENT_TARGET_SANDBOX_ENABLED + cmsSiteService.getCurrentSite().getUid(), false));

		asahiPaymentGatewayContext.setSiteId(cmsSiteService.getCurrentSite().getUid());
        if(asahiConfigurationService.getBoolean("asahi.payment.mode.property.reset", true)) {
            asahiPaymentGatewayContext.setDirectDebit(false);
        }
		final AsahiPaymentTransactionModel paymentTransaction = asahiSAMInvoiceModel.getAsahiPaymentTransaction();

		try
		{
			if (null != paymentTransaction && CollectionUtils.isNotEmpty(paymentTransaction.getEntries()))
			{
				final String paymentTransactionId = paymentTransaction.getEntries().get(0).getCode();
				final double paymentTransactionAmount = paymentTransaction.getEntries().get(0).getAmount().doubleValue();

				captureRequestResponse = asahiPaymentCaptureRequest.create(paymentTransactionAmount, paymentTransactionId,
						asahiPaymentGatewayContext);

			}

		}
		catch (IOException | AsahiPaymentNetworkException | AsahiPaymentApiException e)
		{
			LOG.error("Problem while creating capture request.", e);
		}
		catch (final Exception e)
		{
			LOG.error("UnKnown Exception while creating capture request.", e);
		}

		if (null != captureRequestResponse && captureRequestResponse.getResponseCode() == SUCCESS_STATUS_CODE
				&& null != captureRequestResponse.getResult() && captureRequestResponse.getResult().isSuccessful())
		{
			asahiCaptureResponseData = new AsahiCaptureResponseData();
			paymentTransaction.setCaptureStatus(UPDATE_SUCCESS_CAPTURE_STATUS);
			asahiCaptureResponseData.setPaymentReference(captureRequestResponse.getResult().getId());
			asahiCaptureResponseData.setTotalPaidAmount(String.valueOf(captureRequestResponse.getResult().getAmount()));
			modelService.save(paymentTransaction);

		}
		return asahiCaptureResponseData;

	}
}
