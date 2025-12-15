package com.sabmiller.core.jobs.removal.deals.benefits;

import com.sabmiller.core.jobs.removal.deals.AbstractDealsItemRemovalProvider;
import com.sabmiller.core.model.*;

import java.util.Collections;
import java.util.List;

public class AbstractDealBenefitItemRemovalProvider extends AbstractDealsItemRemovalProvider<AbstractDealBenefitModel> {

    /**
     * @param oldSABMDealsRemovalCronJobModel
     * @param context
     * @return
     */
    @Override
    public List<AbstractDealBenefitModel> getItemsToRemoved(OldSABMDealsRemovalCronJobModel oldSABMDealsRemovalCronJobModel, DealRemovalContext context) {
        return getCleanupDao().getItemsWithEmptyReference(AbstractDealBenefitModel.class,AbstractDealBenefitModel.DEALCONDITIONGROUP,context.getBatchSize());
    }

    @Override
    public Class<AbstractDealBenefitModel> getType() {
        return AbstractDealBenefitModel.class;
    }

    @Override
    public List<Class> requiredTypes() {
        return Collections.singletonList(DealConditionGroupModel.class);
    }
}
