package com.sabmiller.merchantsuiteservices.strategy.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import org.apache.commons.configuration2.Configuration;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.sabmiller.merchantsuiteservices.data.SABMMerchantSuiteTokenRequestData;

@UnitTest
public class SABMBankAccountMerchantSuiteStrategyTest {

    @Mock
    private ConfigurationService configurationService;

    @InjectMocks
    private SABMBankAccountMerchantSuiteStrategy sabmBankAccountMerchantSuiteStrategy;

    SABMMerchantSuiteTokenRequestData requestData;

    @Mock
    Configuration configuration;

    /*@Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        strategyToKeyMap = new HashMap();
        strategyToKeyMap.put("TEST","TEST_CODE");
        sabmEmailSFMCStrategy.setStrategyToKeyMap(strategyToKeyMap);
    }

    @Test
    public void testGetToken()
    {
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(this.configurationService.getConfiguration().getString(EMAIL_URL_CONFIG, DEFAULT_EMAIL_DYNAMIC_URL)).thenReturn(DEFAULT_EMAIL_DYNAMIC_URL);

    }*/


}
