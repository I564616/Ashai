package com.sabmiller.core.jobs.concurrency;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.cronjob.jalo.CronJobLogListener;
import de.hybris.platform.cronjob.jalo.Job;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.JaloSystemException;

import java.util.concurrent.ThreadFactory;

public class MultiThreadJobFactory implements ThreadFactory {

    private CronJobLogListener.CronJobLogContext currentContext;
    private Object currentJobLogContainer;
    private Tenant currentTenant;
    private JaloSession currentSession;

    public MultiThreadJobFactory() {
        currentTenant = Registry.getCurrentTenant();
        currentSession = JaloSession.getCurrentSession();
        currentContext = CronJobLogListener.getCurrentContext();
        currentJobLogContainer = JobLogUtils.jobLogContainer();

        if (currentContext == null) {
            throw new IllegalStateException("MultiThreadJobFactory should be called within a cronjobs performable Thread");
        }
    }

    protected void beforeRun() {
        Registry.setCurrentTenant(this.currentTenant);
        currentSession.activate();
        setupLogContext();
    }

    protected void afterRun() {
        try {
            JaloSession.deactivate();
        } finally {
            Registry.unsetCurrentTenant();
            CronJobLogListener.unsetsetCurrentContext();
            JobLogUtils.unset();
        }
    }

    @Override
    public Thread newThread(Runnable runnable) {
        return new Thread(() -> {
            try {
                beforeRun();
                runnable.run();
            } catch (JaloSystemException var4) {
            } finally {
                afterRun();
            }
        });
    }

    public void setupLogContext() {

        if (currentContext == null) {
            return;
        }

        CronJobLogListener.setCurrentContext(currentContext);
        JobLogUtils.setJobLogContainer(currentJobLogContainer);

    }

    /**
     * Somewhat hack to expose logging even in multithread
     */
    private abstract static class JobLogUtils extends Job {
        protected static Job.JobFileLogContainer jobLogContainer() {
            return getCurrentLogContainer();
        }

        protected static void setJobLogContainer(final Object obj) {
            if (obj instanceof Job.JobFileLogContainer) {
                Job.setCurrentLogContainer((JobFileLogContainer) obj);
            }
        }

        protected static void unset(){
            Job.unsetCurrentLogContainer();
        }

    }
}
