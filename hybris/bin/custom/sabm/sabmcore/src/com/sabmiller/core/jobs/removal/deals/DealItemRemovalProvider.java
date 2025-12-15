package com.sabmiller.core.jobs.removal.deals;

import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.OldSABMDealsRemovalCronJobModel;

import java.util.Collections;
import java.util.List;

public class DealItemRemovalProvider extends AbstractDealsItemRemovalProvider<DealModel> {
    @Override
    public List<DealModel> getItemsToRemoved(OldSABMDealsRemovalCronJobModel oldSABMDealsRemovalCronJobModel, DealRemovalContext context) {
        return getDealsService().getDealsbeforethirtydays(context.getDate(), context.getBatchSize());
    }

    @Override
    public Class<DealModel> getType() {
        return DealModel.class;
    }

    @Override
    public List<Class> requiredTypes() {
        return Collections.emptyList();
    }
}
