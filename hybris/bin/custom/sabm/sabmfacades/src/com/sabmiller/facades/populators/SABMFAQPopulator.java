package com.sabmiller.facades.populators;

import com.sabm.core.model.cms.components.FAQCategoryItemComponentModel;
import com.sabm.core.model.cms.components.FAQComponentModel;
import com.sabmiller.facades.generic.data.FAQCategoryItemData;
import com.sabmiller.facades.generic.data.FAQData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by philip.c.a.ferma on 5/23/18.
 */
public class SABMFAQPopulator implements Populator<FAQComponentModel, FAQData> {


    @Resource(name = "sabmFAQCategoryItemConverter")
    private Converter<FAQCategoryItemComponentModel, FAQCategoryItemData> faqCategoryItemConverter;

    @Override
    public void populate(FAQComponentModel faqComponentModel, FAQData faqData) throws ConversionException {
        faqData.setPageItemsThreshold(faqComponentModel.getPageItemsThreshold());
        faqData.setTitle(faqComponentModel.getTitle());
        faqData.setSendQuestionText(faqComponentModel.getSendQuestionText());
        faqData.setSendQuestionRenderType(faqComponentModel.getSendQuestionCTA().getRenderType().getCode());
        faqData.setSendQuestionLinkButtonText(faqComponentModel.getSendQuestionCTA().getName());
        faqData.setSendQuestionLinkButtonURL(faqComponentModel.getSendQuestionCTA().getUrl());

        List<FAQCategoryItemData> faqCategoryItemData = new ArrayList<FAQCategoryItemData>();
        for (FAQCategoryItemComponentModel category : faqComponentModel.getCategories()) {
            faqCategoryItemData.add(faqCategoryItemConverter.convert(category));
        }
        faqData.setCategories(faqCategoryItemData);
    }
}
