package com.sabmiller.core.autopay.jobs;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.io.File;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.autopay.strategy.SabmXlsFileGeneratorStrategy;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.email.service.SabmEmailService;
import com.sabmiller.core.order.SabmB2BOrderService;
import com.sabmiller.integration.salesforce.SabmCSVUtils;

/**
 * @author marc.f.l.bautista
 *
 */
public class ExtractAutoPayCreditCardOrdersJob extends AbstractJobPerformable<CronJobModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(ExtractAutoPayCreditCardOrdersJob.class);
	
	@Resource(name = "b2bOrderService")
	private SabmB2BOrderService b2bOrderService;
	
	@Resource(name = "autoPayExtractCreditCardOrdersStrategy")
	private SabmXlsFileGeneratorStrategy<OrderModel> autoPayExtractCreditCardOrdersStrategy;
	
	@Resource(name = "sabmEmailService")
   private SabmEmailService sabmEmailService;
	
	/**
	 * Performs the following business logic:
	 * 1. An Excel file will be created that contains a list of all credit card orders.
	 * 2. The file will contain the Payer Number, Sold To Number, Order Number and Amount.
	 * 3. The file will only be generated for customers who are currently on the AutoPay Advantage program (i.e. Program Indicator should be P1 or P2).
	 * 4. The file will be automatically generated at a specified time (e.g 2pm) each day as configured in impex or hmc.
	 * 5. The email list that the file is emailed to is configurable (outside of a code deployment).
	 * 6. The file will be emailed to the email address(es) configured in "email.orders.by.credit.card.payment.email.to" config property key.
	 */
	@Override
	public PerformResult perform(CronJobModel arg0)
	{
		final List<OrderModel> orders = b2bOrderService.getOrdersByCreditCardPayment();
		
		if (CollectionUtils.isNotEmpty(orders)) {
   		File file;
   		try {
   			file = autoPayExtractCreditCardOrdersStrategy.generateXlsxFile(orders);
   			sabmEmailService.sendOrdersByCreditCardPaymentEmail(file);
   			SabmCSVUtils.purgeOldFiles(SabmCSVUtils.getFullPath(SabmCoreConstants.AUTOPAY_GENERATED_FILES_HYBRIS_FOLDER_ORDER_EXTRACT).getPath());
   		}
   		catch (Exception e) {
   			LOG.error("Exception encountered while generating/sending the credit card orders file/email", e);
   			return new PerformResult(CronJobResult.ERROR, CronJobStatus.FINISHED);
   		}
		}
		else {
			LOG.info("No credit card payment orders to process");
		}
		
		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}
	
}