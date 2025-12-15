package com.sabmiller.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.apache.commons.lang3.StringUtils;

import com.sabm.core.model.cms.components.GenericTextContentItemModel;
import com.sabmiller.facades.generic.data.GenericTextContentItemData;

/**
 * Created by philip.c.a.ferma on 5/23/18.
 */
public class SABMGenericTextContentItemPopulator implements Populator<GenericTextContentItemModel, GenericTextContentItemData> {

    @Override
    public void populate(GenericTextContentItemModel genericTextContentItemModel, GenericTextContentItemData genericTextContentItemData) throws ConversionException {
        genericTextContentItemData.setText(genericTextContentItemModel.getText());
        genericTextContentItemData.setSubText(StringUtils.defaultString(genericTextContentItemModel.getSabmSubText(), ""));
    }
}
