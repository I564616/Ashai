package com.sabmiller.facades.generic.components.impl;

import com.sabm.core.model.cms.components.FAQComponentModel;
import com.sabmiller.facades.generic.components.SABMGenericComponentsFacade;
import com.sabmiller.facades.generic.data.FAQData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.log4j.Logger;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by philip.c.a.ferma on 5/23/18.
 */
public class SABMGenericComponentsImpl implements SABMGenericComponentsFacade {

    private static final Logger LOG = Logger.getLogger(SABMGenericComponentsImpl.class);

    private Converter<FAQComponentModel, FAQData> sabmFAQConverter;

    public Converter<FAQComponentModel, FAQData> getSabmFAQConverter() {
        return sabmFAQConverter;
    }


    public void setSabmFAQConverter(Converter<FAQComponentModel, FAQData> sabmFAQConverter) {
        this.sabmFAQConverter = sabmFAQConverter;
    }

    @Override
    public String getFaqsComponentJsonFormat(FAQComponentModel faqComponentModel) {
        String jsonFormat = "";
        try {
            ObjectMapper mapper = new ObjectMapper();
            jsonFormat = mapper.writeValueAsString(sabmFAQConverter.convert(faqComponentModel));
        } catch (JsonGenerationException e) {
            LOG.error("JsonGenerationException", e);
        } catch (JsonMappingException e) {
            LOG.error("JsonMappingException", e);
        } catch (Exception e) {
            LOG.error("Exception", e);
        }
        return jsonFormat;
    }
}
