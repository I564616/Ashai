package com.apb.core.cronjob.order;

import jakarta.annotation.Resource;

import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;

import com.apb.core.model.AsahiSendFailedOrderCronJobModel;
import com.apb.core.service.config.AsahiConfigurationService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.apb.core.services.AsahiOrderService;

import de.hybris.platform.core.model.order.OrderModel;

public class AsahiRemoveOldOrderJob extends AbstractJobPerformable<AsahiSendFailedOrderCronJobModel>{

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(AsahiRemoveOldOrderJob.class);
	
	/** The Constant REMOVE_DOCUMENT_NUMBER_OF_MONTH_APB. */
	private static final String REMOVE_DOCUMENT_NUMBER_OF_MONTH = "remove.order.number.year.";
	
	/** The Constant SITE_ID. */
	private static final String SITE_ID = "apb";
	
	/** The asahi configuration service. */
	@Resource
	private AsahiConfigurationService asahiConfigurationService;
	
	/** The asahi order service. */
	@Resource
	private AsahiOrderService asahiOrderService;
	
	@Override
	public PerformResult perform(AsahiSendFailedOrderCronJobModel cronJobModel) {
		try
		{
			LOGGER.info("cms site " + " site id " + cronJobModel.getCmsSite().getUid() + cronJobModel.getCmsSite().getPk());
			Calendar cal = Calendar.getInstance();
			Date currentDate = cal.getTime();
			cal.add(Calendar.MONTH, - this.asahiConfigurationService.getInt(REMOVE_DOCUMENT_NUMBER_OF_MONTH + cronJobModel.getCmsSite().getUid(), 2)); // to get previous year add -1
			Date previousYear = cal.getTime();
			
			this.asahiOrderService.removeOrdersBasedOnDateAndSite(cronJobModel.getCmsSite().getUid(),previousYear,currentDate);
		}catch (final Exception ex)
		{
			LOGGER.error(" Error in AsahiRemoveOldOrderJob ", ex);
			return new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED);
		}
		return null;
	}

}
