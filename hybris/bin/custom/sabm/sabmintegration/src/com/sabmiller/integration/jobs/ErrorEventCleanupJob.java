package com.sabmiller.integration.jobs;

import com.sabmiller.integration.service.ErrorEventService;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorEventCleanupJob extends AbstractJobPerformable<CronJobModel> {
    private static final Logger LOG = LoggerFactory.getLogger(ErrorEventCleanupJob.class.getName());

    private ErrorEventService errorEventService;

    @Override
    public PerformResult perform(final CronJobModel cronJobModel) {
        try {
            LOG.info("Starting error clean up job.");
            errorEventService.cleanupOldEntries();
        }catch (final Exception e){
            LOG.error("Error running error clean up job", e);
            return new PerformResult(CronJobResult.ERROR, CronJobStatus.FINISHED);
        }

        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    public void setErrorEventService(final ErrorEventService errorEventService) {
        this.errorEventService = errorEventService;
    }
}
