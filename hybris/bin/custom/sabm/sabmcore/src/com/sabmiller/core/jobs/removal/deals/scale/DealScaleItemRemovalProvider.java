package com.sabmiller.core.jobs.removal.deals.scale;

import com.sabmiller.core.jobs.removal.deals.AbstractDealsItemRemovalProvider;
import com.sabmiller.core.model.DealConditionGroupModel;
import com.sabmiller.core.model.DealScaleModel;
import com.sabmiller.core.model.OldSABMDealsRemovalCronJobModel;

import java.util.Collections;
import java.util.List;

public class DealScaleItemRemovalProvider extends AbstractDealsItemRemovalProvider<DealScaleModel> {
    @Override
    public List<DealScaleModel> getItemsToRemoved(OldSABMDealsRemovalCronJobModel oldSABMDealsRemovalCronJobModel, DealRemovalContext context) {
        return getCleanupDao().getItemsWithEmptyReference(DealScaleModel.class, DealScaleModel.DEALCONDITIONGROUP, context.getBatchSize());
    }

    @Override
    public Class<DealScaleModel> getType() {
        return DealScaleModel.class;
    }

    @Override
    public List<Class> requiredTypes() {
        return Collections.singletonList(DealConditionGroupModel.class);
    }
}
