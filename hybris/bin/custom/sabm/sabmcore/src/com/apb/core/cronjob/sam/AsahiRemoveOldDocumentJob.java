package com.apb.core.cronjob.sam;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jakarta.annotation.Resource;

import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.apb.core.dao.sam.invoice.AsahiSAMInvoiceDao;
import com.sabmiller.core.model.AsahiSAMInvoiceModel;
import com.sabmiller.core.model.AsahiSAMPaymentModel;
import com.apb.core.service.config.AsahiConfigurationService;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;

/**
 * The Class AsahiRemoveOldDocumentJob.
 * 
 * @author Kuldeep.Singh1
 */
public class AsahiRemoveOldDocumentJob extends AbstractJobPerformable<CronJobModel>{
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(AsahiRemoveOldDocumentJob.class);
	
	/** The Constant REMOVE_DOCUMENT_START_DATE. */
	private static final String REMOVE_DOCUMENT_NUMBER_OF_YEAR = "sam.remove.invoice.number.year";
	
	/** The Constant REMOVE_INVOICE_PAYMENT_HISTORY. */
	private static final String REMOVE_INVOICE_PAYMENT_HISTORY = "sam.remove.invoice.payment";
	
	/** The asahi configuration service. */
	@Resource
	private AsahiConfigurationService asahiConfigurationService;
	
	/** The asahi SAM invoice dao. */
	@Resource
	private AsahiSAMInvoiceDao asahiSAMInvoiceDao;
	
	/** The model service. */
	@Resource(name = "modelService")
	private ModelService modelService;

	@Override
	public PerformResult perform(CronJobModel arg0) {
		
		try
		{
			Calendar cal = Calendar.getInstance();
			Date currentDate = cal.getTime();
			
			cal.add(Calendar.YEAR, -this.asahiConfigurationService.getInt(REMOVE_DOCUMENT_NUMBER_OF_YEAR, 2)); // to get previous year add -1
			Date previousYear = cal.getTime();
			
			List<AsahiSAMInvoiceModel> invoiceList = this.asahiSAMInvoiceDao.getInvoiceBasedOnDate(previousYear,currentDate);
			
			//Removing Old Invoice Documents
			if(CollectionUtils.isNotEmpty(invoiceList)){
				this.modelService.removeAll(invoiceList);
			}
			
			//Removing Old Payment History Documents
			if("true".equalsIgnoreCase(this.asahiConfigurationService.getString(REMOVE_INVOICE_PAYMENT_HISTORY, "true"))){
				List<AsahiSAMPaymentModel> paymentList = this.asahiSAMInvoiceDao.getInvoicePaymentBasedOnDate(previousYear,currentDate);
				if(CollectionUtils.isNotEmpty(paymentList)){
					this.modelService.removeAll(paymentList);
				}
			}
			
		}catch (final Exception ex)
		{
			LOGGER.error(" Error in Remove Document cron job ", ex);
			return new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED);
		}
		
		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}

}
