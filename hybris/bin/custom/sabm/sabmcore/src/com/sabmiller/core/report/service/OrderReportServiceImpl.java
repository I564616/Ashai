package com.sabmiller.core.report.service;

import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.io.IOException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.openpgp.PGPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.report.strategy.DefaultOrderReportExportStrategy;

/**
 * Created by zhuo.a.jiang on 10/01/2018.
 */
public class OrderReportServiceImpl implements  OrderReportService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderReportServiceImpl.class);

    private Map<String, DefaultOrderReportExportStrategy> strategyMap = new HashMap<>();

    private FlexibleSearchService flexibleSearchService;

    private DefaultOrderReportExportStrategy defaultStrategy;

    @Override
	public void generateReport(final String strategy, final Integer batchSize, final Integer deltaHours)
			throws IOException, PGPException, NoSuchProviderException, Exception
	{
        final DefaultOrderReportExportStrategy defaultStrategy = getDefaultOrderReportStrategy(strategy);

		defaultStrategy.uploadFileToSFTP(batchSize, deltaHours);
    }

    private DefaultOrderReportExportStrategy getDefaultOrderReportStrategy(final String strategy) {

        return strategyMap.getOrDefault(strategy, defaultStrategy);
    }

    public Map<String, DefaultOrderReportExportStrategy> getStrategyMap() {
        return strategyMap;
    }

    public void setStrategyMap(final Map<String, DefaultOrderReportExportStrategy> strategyMap) {
        this.strategyMap = strategyMap;
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    public DefaultOrderReportExportStrategy getDefaultStrategy() {
        return defaultStrategy;
    }

    public void setDefaultStrategy(final DefaultOrderReportExportStrategy defaultStrategy) {
        this.defaultStrategy = defaultStrategy;
    }

}
