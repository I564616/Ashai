package com.sabmiller.core.report.service;

import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.io.IOException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.openpgp.PGPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.report.strategy.DefaultProductReportExportStrategy;

/**
 * Created by zhuo.a.jiang on 10/01/2018.
 */
public class ProductReportServiceImpl implements ProductReportService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductReportServiceImpl.class);

    private Map<String, DefaultProductReportExportStrategy> strategyMap = new HashMap<>();

    private FlexibleSearchService flexibleSearchService;

    private DefaultProductReportExportStrategy defaultStrategy;

    @Override
    public void generateReport(final String strategy) throws IOException, PGPException, NoSuchProviderException, Exception {
        final DefaultProductReportExportStrategy defaultStrategy = getDefaultProductReportStrategy(strategy);

        defaultStrategy.uploadFileToSFTP();
    }

    private DefaultProductReportExportStrategy getDefaultProductReportStrategy(final String strategy) {

        return strategyMap.getOrDefault(strategy, defaultStrategy);
    }

    public Map<String, DefaultProductReportExportStrategy> getStrategyMap() {
        return strategyMap;
    }

    public void setStrategyMap(final Map<String, DefaultProductReportExportStrategy> strategyMap) {
        this.strategyMap = strategyMap;
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    public DefaultProductReportExportStrategy getDefaultStrategy() {
        return defaultStrategy;
    }

    public void setDefaultStrategy(final DefaultProductReportExportStrategy defaultStrategy) {
        this.defaultStrategy = defaultStrategy;
    }
}
