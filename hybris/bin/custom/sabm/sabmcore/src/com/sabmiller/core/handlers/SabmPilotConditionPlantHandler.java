package com.sabmiller.core.handlers;

import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.sabmiller.core.model.SabmPilotConditionModel;

public class SabmPilotConditionPlantHandler implements DynamicAttributeHandler<String, SabmPilotConditionModel> {
    @Override
    public String get(final SabmPilotConditionModel model) {
        String returnString = StringUtils.EMPTY;
        if (Objects.nonNull(model.getPlant()))
        {
            returnString = model.getPlant().getPlantId();
        }
        return returnString;
    }

    @Override
    public void set(final SabmPilotConditionModel model, final String s) {
        throw new UnsupportedOperationException(
                "Set of dynamic attribute 'plantId' of SabmPilotCondition is disabled!");
    }
}
