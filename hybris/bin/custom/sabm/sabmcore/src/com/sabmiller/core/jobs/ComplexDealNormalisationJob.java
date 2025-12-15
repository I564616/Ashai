package com.sabmiller.core.jobs;

import com.sabmiller.core.deals.services.DealsService;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComplexDealNormalisationJob extends AbstractJobPerformable<CronJobModel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComplexDealNormalisationJob.class);

    private DealsService dealsService;

    @Override
    public PerformResult perform(CronJobModel cronJob) {
        try {

            LOGGER.info("Normalising Deals Assignees for Complex Deals");

            getDealsService().normalizeAssigneesDeals();

        } catch (Exception e) {

            LOGGER.error("Error deal assignee normalisation.", e);

            return new PerformResult(CronJobResult.ERROR, CronJobStatus.FINISHED);
        }

        LOGGER.info("Normalising Deals Assignees for Complex Deals complete...");

        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    protected DealsService getDealsService() {
        return dealsService;
    }

    public void setDealsService(DealsService dealsService) {
        this.dealsService = dealsService;
    }
}
