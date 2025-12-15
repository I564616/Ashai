package com.sabmiller.core.jobs;

import de.hybris.platform.retention.job.AfterRetentionCleanupJobPerformable;

public class SabmAfterRetentionCleanupJobPerformable extends AfterRetentionCleanupJobPerformable {

    @Override
    public boolean isAbortable() {
        return true;
    }
}
