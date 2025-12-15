package com.sabmiller.core.report.service;

import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.io.IOException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.Map;

import java.io.File;

import org.bouncycastle.openpgp.PGPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.report.strategy.DefaultNotificationReportExportStrategy;

/**
 * Created by zhuo.a.jiang on 10/01/2018.
 */
public class NotificationReportServiceImpl implements  NotificationReportService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationReportServiceImpl.class);

    private Map<String, DefaultNotificationReportExportStrategy> strategyMap = new HashMap<>();

    private FlexibleSearchService flexibleSearchService;

    private DefaultNotificationReportExportStrategy defaultStrategy;

    @Override
    public void generateReport(final String strategy) throws IOException, PGPException, NoSuchProviderException, Exception {
        final DefaultNotificationReportExportStrategy defaultStrategy = getDefaultNotificationReportStrategy(strategy);

        defaultStrategy.uploadFileToSFTP();
    }

    private DefaultNotificationReportExportStrategy getDefaultNotificationReportStrategy(final String strategy) {

        return strategyMap.getOrDefault(strategy, defaultStrategy);
    }
    
    @Override
 	public File generateNotificationReport(final String strategy)
 			throws IOException, PGPException, NoSuchProviderException, Exception
 	{
 		final DefaultNotificationReportExportStrategy defaultStrategy = getDefaultNotificationReportStrategy(strategy);

 		final File file = defaultStrategy.generatefile();
 		return file;
 	}
    
    public Map<String, DefaultNotificationReportExportStrategy> getStrategyMap() {
        return strategyMap;
    }

    public void setStrategyMap(final Map<String, DefaultNotificationReportExportStrategy> strategyMap) {
        this.strategyMap = strategyMap;
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    public DefaultNotificationReportExportStrategy getDefaultStrategy() {
        return defaultStrategy;
    }

    public void setDefaultStrategy(final DefaultNotificationReportExportStrategy defaultStrategy) {
        this.defaultStrategy = defaultStrategy;
    }
}
