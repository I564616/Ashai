package com.sabmiller.core.report.service;

import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.io.IOException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.openpgp.PGPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.report.strategy.DefaultVenueReportExportStrategy;

/**
 * Created by zhuo.a.jiang on 15/12/2017.
 */
public class VenueReportServiceImpl implements VenueReportService {

    private static final Logger LOG = LoggerFactory.getLogger(VenueReportServiceImpl.class);

    private Map<String, DefaultVenueReportExportStrategy> strategyMap = new HashMap<>();

    private FlexibleSearchService flexibleSearchService;

    private DefaultVenueReportExportStrategy defaultStrategy;

    @Override
    public void generateReport(final String strategy)  throws IOException, PGPException, NoSuchProviderException, Exception {
        defaultStrategy.uploadFileToSFTP();
    }



    private DefaultVenueReportExportStrategy getDefaultVenueReportExportStrategy(final String strategy) {

        return strategyMap.getOrDefault(strategy, defaultStrategy);
    }

    public Map<String, DefaultVenueReportExportStrategy> getStrategyMap() {
        return strategyMap;
    }
    public void setStrategyMap(final Map<String, DefaultVenueReportExportStrategy> strategyMap) {
        this.strategyMap = strategyMap;
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }
    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    public DefaultVenueReportExportStrategy getDefaultStrategy() {
        return defaultStrategy;
    }

    public void setDefaultStrategy(final DefaultVenueReportExportStrategy defaultStrategy) {
        this.defaultStrategy = defaultStrategy;
    }
}
