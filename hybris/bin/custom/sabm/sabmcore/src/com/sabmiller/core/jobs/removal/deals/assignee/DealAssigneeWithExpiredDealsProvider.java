package com.sabmiller.core.jobs.removal.deals.assignee;

import com.sabmiller.core.jobs.removal.deals.AbstractDealsItemRemovalProvider;
import com.sabmiller.core.model.DealAssigneeModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.OldSABMDealsRemovalCronJobModel;

import java.util.Collections;
import java.util.List;

public class DealAssigneeWithExpiredDealsProvider extends AbstractDealsItemRemovalProvider<DealAssigneeModel> {

    @Override
    public List<DealAssigneeModel> getItemsToRemoved(OldSABMDealsRemovalCronJobModel oldSABMDealsRemovalCronJobModel, DealRemovalContext context) {
        return getDealsService().getDealAssigneeForExpiredDeals(context.getDate(),context.getBatchSize());
    }

    @Override
    public Class<DealAssigneeModel> getType() {
        return DealAssigneeModel.class;
    }

    @Override
    public List<Class> requiredTypes() {
        return Collections.singletonList(DealModel.class);
    }
}
