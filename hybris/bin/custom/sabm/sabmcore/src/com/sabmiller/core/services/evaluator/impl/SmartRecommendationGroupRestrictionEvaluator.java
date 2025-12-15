package com.sabmiller.core.services.evaluator.impl;

import com.sabm.core.model.cms.restrictions.CMSSmartRecommendationGroupRestrictionModel;
import com.sabmiller.core.enums.RecommendationGroupType;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.evaluator.CMSRestrictionEvaluator;

public class SmartRecommendationGroupRestrictionEvaluator implements CMSRestrictionEvaluator<CMSSmartRecommendationGroupRestrictionModel> {

    private B2BCommerceUnitService b2BCommerceUnitService;

    @Override
    public boolean evaluate(CMSSmartRecommendationGroupRestrictionModel smartRecommendationGroupRestrictionModel, RestrictionData restrictionData) {
        final B2BUnitModel b2BUnit = b2BCommerceUnitService.getParentUnit();
        if(b2BUnit == null){
            return false;
        }

        final RecommendationGroupType smartRecommendationGroup = null != b2BUnit.getRecommendationGroup() ? b2BUnit.getRecommendationGroup() : RecommendationGroupType.A;

        if (smartRecommendationGroupRestrictionModel.getSmartRecommendationGroup() == smartRecommendationGroup) {
            return true;
        }

        return false;
    }

    public B2BCommerceUnitService getB2BCommerceUnitService() {
        return b2BCommerceUnitService;
    }

    public void setB2BCommerceUnitService(B2BCommerceUnitService b2BCommerceUnitService) {
        this.b2BCommerceUnitService = b2BCommerceUnitService;
    }
}
