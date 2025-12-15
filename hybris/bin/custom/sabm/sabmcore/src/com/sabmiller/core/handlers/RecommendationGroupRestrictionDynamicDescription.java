package com.sabmiller.core.handlers;


import com.sabm.core.model.cms.restrictions.CMSSmartRecommendationGroupRestrictionModel;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

public class RecommendationGroupRestrictionDynamicDescription extends AbstractDynamicAttributeHandler<String, CMSSmartRecommendationGroupRestrictionModel> {
    @Override
    public String get(CMSSmartRecommendationGroupRestrictionModel model) {
        final StringBuilder result = new StringBuilder();
        result.append("Display for Smart Recommendation Group : ").append(model.getSmartRecommendationGroup().getCode());
        return result.toString();
    }
}
