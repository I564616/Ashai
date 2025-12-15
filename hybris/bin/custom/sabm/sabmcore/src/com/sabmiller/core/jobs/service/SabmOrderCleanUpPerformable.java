package com.sabmiller.core.jobs.service;

import com.sabmiller.core.model.EntryOfferInfoModel;
import com.sabmiller.core.model.OldSABMUserAccessHistoryRemovalCronJobModel;
import com.sabmiller.core.order.SabmB2BOrderService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.exceptions.ModelRemovalException;
import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class SabmOrderCleanUpPerformable extends AbstractJobPerformable<OldSABMUserAccessHistoryRemovalCronJobModel> {

    private static final Logger LOG = LoggerFactory.getLogger(SabmOrderCleanUpPerformable.class.getName());

    private static final String TIMEOUT_HOUR = "order.purge.time.hours.after.midnight";

    private SabmB2BOrderService sabmB2BOrderService;
    private ConfigurationService configurationService;

    @Override
    public PerformResult perform(OldSABMUserAccessHistoryRemovalCronJobModel cronJob) {
        TimeoutThread timeoutThread = new TimeoutThread(getTimeout());

        timeoutThread.startAndWaitStarted();
        return timeoutThread.executeWithTimeOut(()->executeCleanup(cronJob,timeoutThread),new PerformResult(CronJobResult.ERROR, CronJobStatus.UNKNOWN));
    }

    private PerformResult executeCleanup(final OldSABMUserAccessHistoryRemovalCronJobModel cronJob, final TimeoutThread timeoutThread) {

        final int limit = cronJob.getBatchSize();
        final LocalDate endLocalDate = LocalDate.now().minusDays(cronJob.getAge() - 1);
        final Date endDate = Date.from(endLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        int counter = 0;
        List<OrderModel> ordersToDelete = sabmB2BOrderService.getOrdersToDate(endDate, limit);
        while (CollectionUtils.isNotEmpty(ordersToDelete)) {
            for (OrderModel orderModel : ordersToDelete) {

                purgeOrder(orderModel);

                counter++;
                if (LOG.isDebugEnabled() && (counter % 100 == 0)) {
                    LOG.debug(counter + " orders purged.");
                }

                if (clearAbortRequestedIfNeeded(cronJob) || timeoutThread.hasTimedOut()) {
                    LOG.info("Total orders purged: " + counter);
                    LOG.info("Order Cleanup Job aborted.");
                    return new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED);
                }
            }

            ordersToDelete = sabmB2BOrderService.getOrdersToDate(endDate, limit);

        }

        LOG.info("Total orders purged: " + counter);

        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    private void purgeOrder(final OrderModel orderModel) {
        try {
            final List<ConsignmentEntryModel> consignmentEntriesToDelete = orderModel.getConsignments().stream()
                    .flatMap(consignmentModel -> consignmentModel.getConsignmentEntries().stream())
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(orderModel.getConsignments())) {
                if (CollectionUtils.isNotEmpty(consignmentEntriesToDelete)) {
                    modelService.removeAll(consignmentEntriesToDelete);
                }
                modelService.removeAll(orderModel.getConsignments());
            }
            final List<EntryOfferInfoModel> offerInfosToDelete = orderModel.getEntries().stream()
                    .flatMap(abstractOrderEntryModel -> abstractOrderEntryModel.getOfferInfo().stream())
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(offerInfosToDelete)) {
                modelService.removeAll(offerInfosToDelete);
            }
            if (CollectionUtils.isNotEmpty(orderModel.getSimulationMessages())) {
                modelService.removeAll(orderModel.getSimulationMessages());
            }
            modelService.remove(orderModel);
        } catch (ModelRemovalException e) {
            LOG.error("Error encountered while purging order. ", e);
        }
    }

    private long getTimeout() {
        final DateTime currentDate = DateTime.now().withZone(DateTimeZone.forID("Australia/Victoria"));
        final int addl_hours = getConfigurationService().getConfiguration().getInt(TIMEOUT_HOUR, 7);

        final DateTime currentDayDateTime = currentDate.withTimeAtStartOfDay().plusHours(addl_hours);
        long timeout = currentDayDateTime.toDate().getTime() - currentDate.toDate().getTime();
        if (timeout > 0) {
            return timeout;
        }

        final DateTime nextDayDateTime = currentDate.plusDays(1).withTimeAtStartOfDay().plusHours(addl_hours);
        timeout = nextDayDateTime.toDate().getTime() - currentDate.toDate().getTime();
        return timeout;
    }

    protected SabmB2BOrderService getSabmB2BOrderService() {
        return sabmB2BOrderService;
    }

    public void setSabmB2BOrderService(SabmB2BOrderService sabmB2BOrderService) {
        this.sabmB2BOrderService = sabmB2BOrderService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    public boolean isAbortable() {
        return true;
    }


    private static class TimeoutThread extends Thread {

        private final long timeout;
        private final Object lock = new Object();
        private final AtomicBoolean finishedOrTimedOut;
        final CountDownLatch threadStartedMarker;

        public TimeoutThread(final long timeout) {
            this.timeout = timeout;
            this.threadStartedMarker = new CountDownLatch(1);
            this.finishedOrTimedOut = new AtomicBoolean(false);
        }

        @Override
        public void run() {
            synchronized (lock) {
                threadStartedMarker.countDown(); // mark as started
                while (!finishedOrTimedOut.get()) {

                    try {
                        lock.wait(timeout);
                    } catch (InterruptedException e) {
                    }
                    finishedOrTimedOut.set(true);
                }
            }

        }

        public void startAndWaitStarted() {
            this.start();
            ensureStarted();
        }

        public <T> T executeWithTimeOut(final Callable<T> executeWithTimeOut, final T defaultValue) {

            try {
                return executeWithTimeOut.call();
            } catch (Exception e) {

                LOG.error("Error executing callable", e);

            } finally {
                notifyComplete(); // notify that we're done
            }

            return defaultValue;
        }

        public boolean hasTimedOut() {
            return finishedOrTimedOut.get();
        }

        private void notifyComplete() {
            synchronized (lock) {
                this.finishedOrTimedOut.set(true);
                lock.notify();
            }
        }

        private void ensureStarted() {
            try {
                threadStartedMarker.await();
            } catch (InterruptedException e) {
                LOG.error("Interrupted while ensuring the thread has started", e);
            }
        }
    }

}
