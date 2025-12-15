package com.sabmiller.core.jobs.removal.deals.cartdeals;

import com.sabmiller.core.jalo.DealConditionGroup;
import com.sabmiller.core.jobs.removal.deals.AbstractDealsItemRemovalProvider;
import com.sabmiller.core.model.AbstractDealBenefitModel;
import com.sabmiller.core.model.CartDealConditionModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.OldSABMDealsRemovalCronJobModel;

import java.util.Collections;
import java.util.List;

public class CartDealConditionItemRemovalProvider extends AbstractDealsItemRemovalProvider<CartDealConditionModel> {
    @Override
    public List<CartDealConditionModel> getItemsToRemoved(OldSABMDealsRemovalCronJobModel oldSABMDealsRemovalCronJobModel, DealRemovalContext context) {
        return getDealsService().getCartDealCondition(context.getDate(), context.getBatchSize());
    }

    @Override
    public Class<CartDealConditionModel> getType() {
        return CartDealConditionModel.class;
    }

    @Override
    public List<Class> requiredTypes() {
        return Collections.singletonList(DealModel.class);
    }
}
