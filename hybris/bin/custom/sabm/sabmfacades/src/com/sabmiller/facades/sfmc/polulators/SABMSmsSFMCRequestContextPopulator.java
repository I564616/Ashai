package com.sabmiller.facades.sfmc.polulators;

import com.sabmiller.facades.sfmc.context.SFMCSmsAbstractContextData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import jakarta.annotation.Resource;

public class SABMSmsSFMCRequestContextPopulator<SOURCE extends Object, TARGET extends SFMCSmsAbstractContextData>
        implements Populator<SOURCE, TARGET> {

    @Resource(name = "configurationService")
    private ConfigurationService configurationService;

    @Override
    public void populate(SOURCE source, TARGET target) throws ConversionException {
        target.setEnvType(getEnvironmentType());
    }

    private String getEnvironmentType() {

        return configurationService.getConfiguration().getString("envType", "prod");


    }
}

