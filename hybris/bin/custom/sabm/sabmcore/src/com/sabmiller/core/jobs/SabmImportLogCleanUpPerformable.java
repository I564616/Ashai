/**
 *
 */
package com.sabmiller.core.jobs;

import com.sabmiller.core.jobs.service.SabmImportLogCleanUpService;
import com.sabmiller.core.model.SabmImportLogCleanUpCronJobModel;
import com.sabmiller.webservice.model.MasterImportModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.exceptions.ModelRemovalException;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Resource;
import java.util.Calendar;
import java.util.List;


/**
 * @author iqbal.javed
 *
 */
public class SabmImportLogCleanUpPerformable extends AbstractJobPerformable<SabmImportLogCleanUpCronJobModel> {

    /*
     * (non-Javadoc)
     *
     * @see de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable#perform(de.hybris.platform.cronjob.model.
     * CronJobModel)
     */
    private static final Logger LOG = LoggerFactory.getLogger(SabmImportLogCleanUpPerformable.class.getName());

    private static final String STAMP_FORMAT = "MM/dd/yyyy HH:mm:ss";

    private SabmImportLogCleanUpService sabmImportLogCleanUpService;

    @Resource(name = "sabmCronJobStatus")
    private SabmCronJobStatus sabmCronJobStatus;

    /**
     * @return the sabmCronJobStatus
     */
    public SabmCronJobStatus getSabmCronJobStatus() {
        return sabmCronJobStatus;
    }


    /**
     * @param sabmCronJobStatus
     *           the sabmCronJobStatus to set
     */
    public void setSabmCronJobStatus(final SabmCronJobStatus sabmCronJobStatus) {
        this.sabmCronJobStatus = sabmCronJobStatus;
    }


    @Override
    public PerformResult perform(final SabmImportLogCleanUpCronJobModel cronJob) {

        final String timeStamp = DateFormatUtils.format(Calendar.getInstance().getTime(), STAMP_FORMAT);

        CronJobStatus status = CronJobStatus.FINISHED;
        CronJobResult result = CronJobResult.SUCCESS;
        boolean wasGracefulAbort = false;

        try {

            final List<MasterImportModel> importLogs = getSabmImportLogCleanUpService().getMasterImportLogs();

            for (final MasterImportModel masterImport : importLogs) {

                if (clearAbortRequestedIfNeeded(cronJob)) { //check if abort was requested
                    wasGracefulAbort = true;
                    status = CronJobStatus.ABORTED;
                    break;
                }

                try {
                    modelService.remove(masterImport);
                } catch (ModelRemovalException mre) {
                    LOG.error("Unable to remove master import with payloadId [{}]", masterImport.getPayloadId());
                    result = CronJobResult.ERROR;
                }
            }
        } catch (Exception e) {
            LOG.error("An error occurred cleaning up import logs.", e);
            status = CronJobStatus.ABORTED;
            result = CronJobResult.ERROR;
        }

        if ((status == CronJobStatus.ABORTED && !wasGracefulAbort) || result == CronJobResult.ERROR) {
            sabmCronJobStatus.sendJobStatusNotification(cronJob.getCode(), status.getCode() + " with result " + result.getCode(), timeStamp);
        }

        return new PerformResult(result, status);
    }

    /**
     * @return the sabmImportLogCleanUpService
     */
    public SabmImportLogCleanUpService getSabmImportLogCleanUpService() {
        return sabmImportLogCleanUpService;
    }

    /**
     * @param sabmImportLogCleanUpService
     *           the sabmImportLogCleanUpService to set
     */
    public void setSabmImportLogCleanUpService(final SabmImportLogCleanUpService sabmImportLogCleanUpService) {
        this.sabmImportLogCleanUpService = sabmImportLogCleanUpService;
    }

    @Override
    public boolean isAbortable() {
        return true;
    }
}
