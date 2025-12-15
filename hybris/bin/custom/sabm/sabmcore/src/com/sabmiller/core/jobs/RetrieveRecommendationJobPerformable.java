package com.sabmiller.core.jobs;

import com.google.common.base.Stopwatch;
import com.sabmiller.core.model.SmartRecommendationsCronJobModel;
import com.sabmiller.core.recommendation.service.RecommendationService;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.NoSuchFileException;

public class RetrieveRecommendationJobPerformable extends AbstractJobPerformable<SmartRecommendationsCronJobModel> {

    private static final Logger LOG = LoggerFactory.getLogger(RetrieveRecommendationJobPerformable.class);

    private RecommendationService recommendationService;
    private SabmCronJobStatus sabmCronJobStatus;

    @Override
    public PerformResult perform(SmartRecommendationsCronJobModel cronJobModel) {
        PerformResult performResult = new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
        final Stopwatch stopwatch = Stopwatch.createUnstarted();
        try {
            stopwatch.start();
            boolean result;
            if (Boolean.TRUE.equals(cronJobModel.getEnableV2())) {
                result = recommendationService.retrieveAndSaveRecommendationsV2(cronJobModel.getCatalogVersion());
            } else {
                result = recommendationService.retrieveAndSaveRecommendations(cronJobModel.getCatalogVersion());
            }
            if (!result) {
                performResult = new PerformResult(CronJobResult.ERROR, CronJobStatus.FINISHED);
            }
        } catch (NoSuchFileException e) {
            if (cronJobModel.getSendNoFileEmail()) {
                LOG.info("Sending no file email notification");
                sabmCronJobStatus.sendNoFileNotification(cronJobModel);
            }
        }
        finally {
            stopwatch.stop();
            LOG.info("Recommendation import finished in [{}]",stopwatch.toString());
        }

        if (cronJobModel.getSendStatusEmail()) {
            LOG.info("Sending job status email notification");
            getSabmCronJobStatus().sendJobStatusNotification(cronJobModel, performResult);
        }
        return performResult;
    }

    protected RecommendationService getRecommendationService() {
        return recommendationService;
    }

    public void setRecommendationService(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    protected SabmCronJobStatus getSabmCronJobStatus() {
        return sabmCronJobStatus;
    }

    public void setSabmCronJobStatus(SabmCronJobStatus sabmCronJobStatus) {
        this.sabmCronJobStatus = sabmCronJobStatus;
    }
}
