package com.apb.core.cronjob.sam;

import com.apb.core.integration.AsahiIntegrationPointsServiceImpl;
import com.apb.core.interceptor.AsahiDirectDebitValidatorInterceptor;
import com.sabmiller.core.model.AsahiSAMDirectDebitModel;
import com.sabmiller.core.model.SIPFailedObjectProcessorCronjobModel;
import com.sabmiller.core.model.SIPFailedPaymentModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by SiddharthaKLet on 4/17/2019.
 */
public class SIPProcessFailedObjectsJob extends AbstractJobPerformable<SIPFailedObjectProcessorCronjobModel> {

    @Resource(name = "asahiDirectDebitValidatorInterceptor")
    private AsahiDirectDebitValidatorInterceptor asahiDirectDebitValidator;

    @Autowired
    private ModelService modelService;

    @Autowired
    private AsahiIntegrationPointsServiceImpl asahiIntegrationPointsService;

    private static final String REMOVE_INDICATOR = "remove";
    private static final Logger logger = Logger.getLogger(SIPProcessFailedObjectsJob.class);

    @Override
    public PerformResult perform(SIPFailedObjectProcessorCronjobModel sipFailedObjectProcessorCronjob) {
        try {
            logger.info("----- Cronjob is invoked to reprocess SIP failed objects -----");
            logger.info("Processing failed direct debits - total objects : " + sipFailedObjectProcessorCronjob.getFailedDirectDebits().size());

            List<AsahiSAMDirectDebitModel> directDebitModels = new ArrayList<>();
            List<SIPFailedPaymentModel> paymentModels = new ArrayList<>();

            sipFailedObjectProcessorCronjob.getFailedDirectDebits().stream().forEach(directDebitModel ->
            {
                logger.info(String.format("Processing direct debit - %s, operation - %s", directDebitModel.getPk().toString(), directDebitModel.getUpdateRemoveIndicator()));
                if (null != directDebitModel.getUpdateRemoveIndicator() && directDebitModel.getUpdateRemoveIndicator().equalsIgnoreCase(REMOVE_INDICATOR)) {
                    modelService.remove(directDebitModel);
                } else {
                    if (!asahiDirectDebitValidator.isSendDirectDebitSuccess(directDebitModel, true, null)) {
                        directDebitModels.add(directDebitModel);
                    }
                }
            });

            logger.info("Processing failed invoice payments - total objects : " + sipFailedObjectProcessorCronjob.getFailedInvoicePayments().size());
            sipFailedObjectProcessorCronjob.getFailedInvoicePayments().stream().forEach(invoicePaymentModel -> {
                logger.info("Processing invoice - " + invoicePaymentModel.getPk().toString());
                if (!asahiIntegrationPointsService.sendInvoicePaymentThruJob(invoicePaymentModel)) {
                    logger.info(String.format("Invoice payment %s was not sent to backend", invoicePaymentModel.getPk().toString()));
                    paymentModels.add(invoicePaymentModel);
                }
            });
            Collections.sort(directDebitModels, (a, b) -> a.getModifiedtime().after(b.getModifiedtime()) ? -1 : 1);
            Collections.sort(paymentModels, (a, b) -> a.getModifiedtime().after(b.getModifiedtime()) ? -1 : 1);
            sipFailedObjectProcessorCronjob.setFailedDirectDebits(directDebitModels);
            sipFailedObjectProcessorCronjob.setFailedInvoicePayments(paymentModels);
            modelService.save(sipFailedObjectProcessorCronjob);
            return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
        } catch (Exception e) {
            logger.error("Exception occurred while reprocessing SIP failed objects. ", e);
            return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
        }
    }
}
