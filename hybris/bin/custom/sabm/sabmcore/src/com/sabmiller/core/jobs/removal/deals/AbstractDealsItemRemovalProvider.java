package com.sabmiller.core.jobs.removal.deals;

import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.core.jobs.removal.ItemRemovalProvider;
import com.sabmiller.core.model.OldSABMDealsRemovalCronJobModel;
import com.sabmiller.core.util.dao.CleanupDao;
import de.hybris.platform.core.model.ItemModel;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public abstract  class AbstractDealsItemRemovalProvider<T extends ItemModel> implements ItemRemovalProvider<T, OldSABMDealsRemovalCronJobModel, AbstractDealsItemRemovalProvider.DealRemovalContext> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDealsItemRemovalProvider.class);

    private DealsService dealsService;

    private CleanupDao cleanupDao;

    private static final int DEFAULT_AGE = 30;

    private static final int DEFAULT_BATCH_SIZE = 1000;

    private static final int DEFAULT_MAX_BATCH_SIZE = 100000;


    @Override
    public DealRemovalContext prepare(OldSABMDealsRemovalCronJobModel job) {

        int age = DEFAULT_AGE;

        int batchSize = DEFAULT_BATCH_SIZE;

        if (job.getAge() != null && job.getAge() > 0) {
            age = job.getAge().intValue();
        } else {
            LOG.warn("Age '" + job.getAge() + "' is invalid, set to default value '" + DEFAULT_AGE + "'");
        }

        if (job.getBatchSize() != null && job.getBatchSize() > 0 && job.getBatchSize() < DEFAULT_MAX_BATCH_SIZE) {
            batchSize = job.getBatchSize().intValue();
        } else {
            LOG.warn("Batch size '" + job.getBatchSize() + "' is invalid, set to default value '" + DEFAULT_BATCH_SIZE + "'");
        }

        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -age);

        return new DealRemovalContext(DateUtils.truncate(calendar.getTime(),Calendar.DATE),batchSize);
    }

    public abstract Class<T> getType();

    public abstract List<Class> requiredTypes();

    protected DealsService getDealsService() {
        return dealsService;
    }

    public void setDealsService(DealsService dealsService) {
        this.dealsService = dealsService;
    }

    protected CleanupDao getCleanupDao() {
        return cleanupDao;
    }

    public void setCleanupDao(CleanupDao cleanupDao) {
        this.cleanupDao = cleanupDao;
    }

    public static class DealRemovalContext{
        private Date date;
        private int batchSize;

        private DealRemovalContext(final Date date, final int batchSize){
            this.date = date;
            this.batchSize = batchSize;
        }

        public Date getDate() {
            return date;
        }

        public int getBatchSize() {
            return batchSize;
        }

    }
}
