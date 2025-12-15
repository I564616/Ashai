package com.sabmiller.facades.sfmc.polulators;

import com.sabmiller.commons.utils.SabmTimeZoneUtils;
import com.sabmiller.facades.sfmc.context.SFMCAbstractContextData;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.b2b.company.impl.DefaultB2BCommerceUnitService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.site.BaseSiteService;

import jakarta.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Default Populator for  Context Objects
 *
 * @param <SOURCE>
 * @param <TARGET>
 */
public class SABMDefaultSFMCRequestContextPopulator<SOURCE extends Object, TARGET extends SFMCAbstractContextData>
        implements Populator<SOURCE, TARGET> {
    @Resource
    DefaultB2BCommerceUnitService defaultB2BCommerceUnitService;

    @Resource
    private SabmTimeZoneUtils sabmTimeZoneUtils;



    @Override
    public void populate(final SOURCE source, final TARGET target) throws ConversionException {
        target.setTransactionDate(getCurrentTimeInBaseStoreTimeZone());

    }

    private String getCurrentTimeInBaseStoreTimeZone() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        String currentTimeZone = sabmTimeZoneUtils.getBaseStoreTimeZone();
        sdf.setTimeZone(TimeZone.getTimeZone(currentTimeZone));
        return sdf.format(new Date());
    }



}
