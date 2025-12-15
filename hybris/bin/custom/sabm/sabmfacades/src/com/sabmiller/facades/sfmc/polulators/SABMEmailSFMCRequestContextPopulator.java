package com.sabmiller.facades.sfmc.polulators;

import com.sabmiller.facades.sfmc.context.SFMCEmailAbstractContextData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import jakarta.annotation.Resource;

public class SABMEmailSFMCRequestContextPopulator<SOURCE extends Object, TARGET extends SFMCEmailAbstractContextData>
        implements Populator<SOURCE, TARGET> {

    @Resource(name = "configurationService")
    private ConfigurationService configurationService;

    @Override
    public void populate(SOURCE source, TARGET target) throws ConversionException {
        target.setHostName(getCurrentHostDomain());
    }

    private String getCurrentHostDomain() {

        return configurationService.getConfiguration().getString("sfmc.email.hostName", "https://online.cub.com.au/");


    }
}
