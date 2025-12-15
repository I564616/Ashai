package com.sabmiller.core.jobs.removal.deals.conditions;

import com.sabmiller.core.jalo.DealConditionGroup;
import com.sabmiller.core.jobs.removal.deals.AbstractDealsItemRemovalProvider;
import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.DealConditionGroupModel;
import com.sabmiller.core.model.OldSABMDealsRemovalCronJobModel;

import java.util.Collections;
import java.util.List;

public class AbstractDealConditionItemRemovalProvider extends AbstractDealsItemRemovalProvider<AbstractDealConditionModel> {

    @Override
    public List<AbstractDealConditionModel> getItemsToRemoved(OldSABMDealsRemovalCronJobModel oldSABMDealsRemovalCronJobModel, DealRemovalContext context) {
        return getCleanupDao().getItemsWithEmptyReference(AbstractDealConditionModel.class,AbstractDealConditionModel.DEALCONDITIONGROUP,context.getBatchSize());
    }

    @Override
    public Class<AbstractDealConditionModel> getType() {
        return AbstractDealConditionModel.class;
    }

    @Override
    public List<Class> requiredTypes() {
        return Collections.singletonList(DealConditionGroupModel.class);
    }
}
