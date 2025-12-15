package com.sabmiller.core.jobs.removal.deals.conditiongroup;

import com.sabmiller.core.jobs.removal.deals.AbstractDealsItemRemovalProvider;
import com.sabmiller.core.model.DealConditionGroupModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.OldSABMDealsRemovalCronJobModel;

import java.util.Collections;
import java.util.List;

public class DealConditionGroupWithExpiredDealsItemProvider extends AbstractDealsItemRemovalProvider<DealConditionGroupModel> {
    @Override
    public List<DealConditionGroupModel> getItemsToRemoved(OldSABMDealsRemovalCronJobModel oldSABMDealsRemovalCronJobModel, DealRemovalContext context) {
        return getDealsService().getDealConditionGroupForExpiredDeals(context.getDate(),context.getBatchSize());
    }

    @Override
    public Class<DealConditionGroupModel> getType() {
        return DealConditionGroupModel.class;
    }

    @Override
    public List<Class> requiredTypes() {
        return Collections.singletonList(DealModel.class);
    }
}
