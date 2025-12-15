package com.sabmiller.core.report.service;

import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.io.IOException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.openpgp.PGPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.report.strategy.DefaultCustomerReportExportStrategy;

/**
 * Created by zhuo.a.jiang on 14/12/2017.
 */
public class CustomerReportServiceImpl implements CustomerReportService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerReportServiceImpl.class);

    private Map<String, DefaultCustomerReportExportStrategy> strategyMap = new HashMap<>();

    private FlexibleSearchService flexibleSearchService;

    private DefaultCustomerReportExportStrategy defaultStrategy;

    @Override
    public void generateReport(final String strategy) throws IOException, PGPException, NoSuchProviderException,Exception {
        final DefaultCustomerReportExportStrategy defaultStrategy = getDefaultCustomerReportStrategy(strategy);

        defaultStrategy.uploadFileToSFTP();
    }

    private DefaultCustomerReportExportStrategy getDefaultCustomerReportStrategy(final String strategy) {

        return strategyMap.getOrDefault(strategy, defaultStrategy);
    }

    public Map<String, DefaultCustomerReportExportStrategy> getStrategyMap() {
        return strategyMap;
    }
    public void setStrategyMap(final Map<String, DefaultCustomerReportExportStrategy> strategyMap) {
        this.strategyMap = strategyMap;
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    public DefaultCustomerReportExportStrategy getDefaultStrategy() {
        return defaultStrategy;
    }

    public void setDefaultStrategy(final DefaultCustomerReportExportStrategy defaultStrategy) {
        this.defaultStrategy = defaultStrategy;
    }
}
