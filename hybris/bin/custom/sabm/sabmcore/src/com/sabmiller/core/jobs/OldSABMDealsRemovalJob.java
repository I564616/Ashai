/**
 *
 */
package com.sabmiller.core.jobs;

import com.sabmiller.core.jobs.concurrency.MultiThreadJobFactory;
import com.sabmiller.core.jobs.removal.deals.AbstractDealsItemRemovalProvider;
import com.sabmiller.core.model.OldSABMDealsRemovalCronJobModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.exceptions.ModelRemovalException;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;


/**
 * @author himabindu.kotari
 *
 */
public class OldSABMDealsRemovalJob extends AbstractJobPerformable<OldSABMDealsRemovalCronJobModel> {
    private static final Logger LOG = LoggerFactory.getLogger(OldSABMDealsRemovalJob.class);

    protected static final String TIMESTAMP_FORMAT = "MM/dd/yyyy HH:mm:ss";
    protected static final String DEALS_NUM_THREAD_PROP = "com.sabmiller.core.jobs.oldsabmdealsremovaljob.threads";
    protected static final String DEALS_WAIT_FOR_HOURS_PROP = "com.sabmiller.core.jobs.oldsabmdealsremovaljob.waitforhours";
    protected static final int DEFAULT_WAIT_FOR_HOURS = 7;

    private SabmCronJobStatus sabmCronJobStatus;

    private List<AbstractDealsItemRemovalProvider<? extends ItemModel>> dealsItemRemovalProviders;

    private ConfigurationService configurationService;

    protected PerformResult performInternal(final OldSABMDealsRemovalCronJobModel job) {

        final String timeStamp = DateFormatUtils.format(Calendar.getInstance().getTime(), TIMESTAMP_FORMAT);

        final int threads = getThreads();

        LOG.info("Starting Deals cleanup with threads {}.",threads);

        //Some of the item providers here are just for initial since there has been a previous deletion using direct database deletion
        //E.G Benefits, we're not supposed to delete those individually since it has been marked as partof="true" which suggest that when dealconditiongroup is deleted
        //it will be removed as well. all the partof="true" models
        final ExecutorService executorService = Executors.newFixedThreadPool(threads, new MultiThreadJobFactory());
        final List<Future<Boolean>> results = new ArrayList<>();
        final AtomicBoolean requestAbortFlag = new AtomicBoolean(false);
        final AtomicBoolean interruptedFlag = new AtomicBoolean(false);
        final Map<Class,CountDownLatch> locks = calculateLocks();

        //loop through all removal provider
        for (final AbstractDealsItemRemovalProvider<? extends ItemModel> itemRemovalProvider : getDealsItemRemovalProviders()) {
            results.add(executorService.submit(new RemovalWorkerThread(itemRemovalProvider, job, requestAbortFlag,interruptedFlag,locks)));
        }

        executorService.shutdown(); // stop from accepting new task

        LOG.info("Waiting for cleanup tasks to complete...");

        final boolean waitForCompletionResult = waitForCompletion(executorService,interruptedFlag); // wait for completion

        LOG.info("Wait for completion result {}.",waitForCompletionResult);

        final PerformResult performResult = collectResult(results, requestAbortFlag,waitForCompletionResult);

        if (!requestAbortFlag.get() && CronJobStatus.ABORTED.equals(performResult.getStatus())) {

            LOG.info("Sending Job Status notification.");
            sabmCronJobStatus.sendJobStatusNotification(job.getCode(), CronJobStatus.ABORTED.getCode(), timeStamp);
        }

        LOG.info("Deals cleanup completed.");
        return performResult;
    }

    protected Map<Class,CountDownLatch> calculateLocks(){
        final List<AbstractDealsItemRemovalProvider<? extends ItemModel>> dealsItemRemovalProviders = getDealsItemRemovalProviders();
        final Map<Class,Long> classToCount = dealsItemRemovalProviders.stream().collect(Collectors.groupingBy((e)->e.getType(),Collectors.counting()));
        return classToCount.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,(e)->new CountDownLatch(e.getValue().intValue())));
    }

    protected boolean waitForCompletion(final ExecutorService executorService, final AtomicBoolean interruptedFlag) {
        try {
            final boolean hasCompletedSuccessfully  = executorService.awaitTermination(getWaitForHours(), TimeUnit.HOURS);
            if(!hasCompletedSuccessfully){
                LOG.info("Timed out!. Initiating Graceful shutdown");
                interruptedFlag.set(true); // try giving it 2 minutes for a clean shutdown before force shutdown
                if(!executorService.awaitTermination(2,TimeUnit.MINUTES)){
                    LOG.info("Timed out!. Force shutdown initiated...");
                    executorService.shutdownNow();
                }
            }
            return hasCompletedSuccessfully;
        } catch (InterruptedException ie) {
            LOG.warn("Thread has been interrupted.", ie);
            //force shutdown
            executorService.shutdownNow();
        }
        return false;
    }

    //this  blocks until aborted/interrupted/exception occurs
    protected PerformResult collectResult(final List<Future<Boolean>> results, final AtomicBoolean requestAborted, final boolean waitForCompletionResult) {

        boolean success = true;

        for (final Future<Boolean> result : results) {
            if (!isSuccess(result)) {
                success = false;
                break;
            }
        }

        final boolean abortRequested = requestAborted.get();

        final CronJobStatus jobStatus = abortRequested || !waitForCompletionResult ? CronJobStatus.ABORTED : CronJobStatus.FINISHED;
        final CronJobResult jobResult = waitForCompletionResult && success ? CronJobResult.SUCCESS : CronJobResult.FAILURE ;
        return new PerformResult(jobResult, jobStatus);
    }

    protected boolean isSuccess(final Future<Boolean> result) {
        try {
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            LOG.error("Error retrieving result from task ", e);
        }
        return false;
    }

    @Override
    public PerformResult perform(final OldSABMDealsRemovalCronJobModel job) {
        return performInternal(job);
    }

    protected int getThreads() {
        return getConfigurationService().getConfiguration().getInt(DEALS_NUM_THREAD_PROP, Runtime.getRuntime().availableProcessors());
    }

    protected long getWaitForHours(){
        return getConfigurationService().getConfiguration().getLong(DEALS_WAIT_FOR_HOURS_PROP,DEFAULT_WAIT_FOR_HOURS);
    }
    @Override
    public boolean isAbortable() {
        return true;
    }


    protected void clearAbortRequestedIfNeeded(final OldSABMDealsRemovalCronJobModel cronJob, final AtomicBoolean sharedAbortFlag) {
        final boolean requestAbort = clearAbortRequestedIfNeeded(cronJob);
        if (requestAbort) {
            sharedAbortFlag.getAndSet(true);
        }
    }


    protected List<AbstractDealsItemRemovalProvider<? extends ItemModel>> getDealsItemRemovalProviders() {
        if (CollectionUtils.isEmpty(dealsItemRemovalProviders)) {
            return Collections.emptyList();
        }
        return dealsItemRemovalProviders;
    }

    public void setDealsItemRemovalProviders(List<AbstractDealsItemRemovalProvider<? extends ItemModel>> dealsItemRemovalProviders) {
        this.dealsItemRemovalProviders = dealsItemRemovalProviders;
    }

    protected ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

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


    /**
     * Helper class for performing the actual work
     */
    private class RemovalWorkerThread implements Callable<Boolean> {

        private AbstractDealsItemRemovalProvider<? extends ItemModel> dealsItemRemovalProvider;
        private AtomicBoolean sharedAbortFlag;
        private OldSABMDealsRemovalCronJobModel oldSABMDealsRemovalCronJob;
        private AtomicBoolean interruptedFlag;
        private Map<Class,CountDownLatch> locks;

        private RemovalWorkerThread(AbstractDealsItemRemovalProvider<? extends ItemModel> dealsItemRemovalProvider, final OldSABMDealsRemovalCronJobModel oldSABMDealsRemovalCronJob, final AtomicBoolean sharedAbortFlag,final AtomicBoolean interruptedFlag,final Map<Class,CountDownLatch> locks) {
            this.dealsItemRemovalProvider = dealsItemRemovalProvider;
            this.oldSABMDealsRemovalCronJob = oldSABMDealsRemovalCronJob;
            this.sharedAbortFlag = sharedAbortFlag;
            this.locks = locks;
            this.interruptedFlag = interruptedFlag;
        }

        protected boolean delete(final List<? extends ItemModel> itemModels) {
            boolean success = true;
            for (ItemModel item : itemModels) {

                if(isInterrupted()){
                    LOG.info("Interrupted. Stopping {}.",dealsItemRemovalProvider.getClass().getSimpleName());
                    break;// interrupted. probably timeout expires.
                }

                clearAbortRequestedIfNeeded(oldSABMDealsRemovalCronJob, sharedAbortFlag); //checks if abort has been initiated and update abort flag

                if (sharedAbortFlag.get()) {
                    LOG.info("Abort Requested. Aborting {}.",dealsItemRemovalProvider.getClass().getSimpleName());
                    break;
                }

                if(item == null){
                    LOG.warn("Found null item. Skipping...");
                    continue;
                }

                final String pk = item.getPk().toString();

                try {
                    modelService.remove(item);
                    LOG.debug("Item {} removed", pk);
                } catch (ModelRemovalException e) {
                    success = false;
                    LOG.error("Failed to delete CronJob {}", pk);
                }
            }

            return success;
        }

        protected void complete(){
            locks.get(dealsItemRemovalProvider.getType()).countDown();
        }

        protected boolean waitForDependency(){
            LOG.info("{} is waiting for dependency.",dealsItemRemovalProvider.getClass().getSimpleName());
            for(Class klass: dealsItemRemovalProvider.requiredTypes()){
                final CountDownLatch countDownLatch = locks.get(klass);
                if(countDownLatch == null){
                    continue;
                }
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    LOG.info("Await for {} interrupted.",dealsItemRemovalProvider.getClass().getSimpleName());
                    return false;
                }
            }
            LOG.info("{} dependency waiting complete.",dealsItemRemovalProvider.getClass().getSimpleName());
            return true;
        }

        @Override
        public Boolean call() {
            try {
                if(!waitForDependency()){
                    interruptedFlag.set(true); // we are interrupted
                    return false;
                }

                return callInternal();
            } catch (final Exception e) {
                LOG.error("Error removing records.", e);
            }finally {
                complete();
            }
            return Boolean.FALSE;
        }

        protected boolean isInterrupted(){
            if(interruptedFlag.get()){
                return true;
            }

            if(Thread.interrupted()){
                interruptedFlag.set(true);
            }

            return interruptedFlag.get();
        }

        protected Boolean callInternal() {
            modelService.refresh(oldSABMDealsRemovalCronJob);
            final AbstractDealsItemRemovalProvider.DealRemovalContext dealRemovalContext = dealsItemRemovalProvider.prepare(oldSABMDealsRemovalCronJob);
            boolean success = true;
            while (true) {

                if(isInterrupted()){ //if thread has been interrupted, it's time to settle. bye2x, probably timeout has finished :)
                    LOG.info("Interrupted. Stopping {}.",dealsItemRemovalProvider.getClass().getSimpleName());
                    break;
                }

                clearAbortRequestedIfNeeded(oldSABMDealsRemovalCronJob, sharedAbortFlag);

                if (sharedAbortFlag.get()) {
                    LOG.info("Abort Requested Before Query. Aborting {}.",dealsItemRemovalProvider.getClass().getSimpleName());
                    break;
                }

                final List<? extends ItemModel> items = dealsItemRemovalProvider.getItemsToRemoved(oldSABMDealsRemovalCronJob, dealRemovalContext);

                if (CollectionUtils.isEmpty(items)) {
                    LOG.info("No more entries to remove {}.",dealsItemRemovalProvider.getClass().getSimpleName());
                    success &= true;
                    break;
                }

                success &= delete(items);

            }

            LOG.info("{} for DealsItemRemovalProvider {} exited.",RemovalWorkerThread.class.getSimpleName(),dealsItemRemovalProvider.getClass().getSimpleName());
            return success;

        }
    }
}