package com.sabmiller.sfmc.strategy;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration2.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sabmiller.sfmc.client.SabmSFMCClient;
import com.sabmiller.sfmc.pojo.SFMCRequest;
import com.sabmiller.sfmc.strategy.impl.SABMEmailSFMCStrategy;

@UnitTest
public class SABMEmailSFMCStrategyTest {

    SFMCRequest sfmcRequest;

    @Mock
    private ConfigurationService configurationService;

    @InjectMocks
    private SABMEmailSFMCStrategy sabmEmailSFMCStrategy;

    public Map strategyToKeyMap;

    private static final String DEFAULT_EMAIL_DYNAMIC_URL = "/messaging/v1/messageDefinitionSends/key:{0}/{1}";

    private static final String EMAIL_URL_CONFIG="sfmc.email.dynamic.url";

    @Mock
    private ModelService modelService;

    @Mock
    protected SabmSFMCClient sabmSFMCClient;

    @Mock
    Configuration configuration;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        strategyToKeyMap = new HashMap();
        strategyToKeyMap.put("TEST","TEST_CODE");
        sabmEmailSFMCStrategy.setStrategyToKeyMap(strategyToKeyMap);
    }

    @Test
    public void testCreateUrl()
    {
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(this.configurationService.getConfiguration().getString(EMAIL_URL_CONFIG, DEFAULT_EMAIL_DYNAMIC_URL)).thenReturn(DEFAULT_EMAIL_DYNAMIC_URL);
        Assert.assertNotNull(this.sabmEmailSFMCStrategy.createUrl("TEST",true));
    }

    @Test
    public void testCreateUrl_returnNull()
    {
        when(this.configurationService.getConfiguration()).thenReturn(configuration);
        when(this.configurationService.getConfiguration().getString(EMAIL_URL_CONFIG, DEFAULT_EMAIL_DYNAMIC_URL)).thenReturn(DEFAULT_EMAIL_DYNAMIC_URL);
        Assert.assertNull(this.sabmEmailSFMCStrategy.createUrl("TESTINVALID",true));
    }



}
