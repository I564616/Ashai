package com.sabmiller.core.util;

import com.azure.storage.blob.BlobContainerClient;
import com.sabmiller.core.constants.SabmCoreConstants;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration2.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;

@UnitTest
public class SabmAzureStorageUtilsTest {

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private Configuration configuration;

    private SabmAzureStorageUtils sabmAzureStorageUtils;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);

        given(configurationService.getConfiguration()).willReturn(configuration);
        given(configuration.getString(SabmCoreConstants.RECOMMENDATION_CONTAINER_REFERENCE, "recommendationengine")).willReturn("recommendationengine");
        sabmAzureStorageUtils = new SabmAzureStorageUtils();
        sabmAzureStorageUtils.setConfigurationService(configurationService);
    }

    @Test
    public void invalidAccountKey()  {
        given(configuration.getString(SabmCoreConstants.AZURE_STORAGE_CONNECTION_STRING))
                .willReturn("AccountName=82vrymp29gbhc8kpbvodirc;AccountKey=12345;EndpointSuffix=core.windows.net;DefaultEndpointsProtocol=https;");
        BlobContainerClient container = sabmAzureStorageUtils.getAzureBlobContainer("recommendationengine");
        assertNull(container);
    }

    @Test
    public void invalidAccountName()  {
        given(configuration.getString(SabmCoreConstants.AZURE_STORAGE_CONNECTION_STRING))
                .willReturn("AccountName=abcd;AccountKey=12345;EndpointSuffix=core.windows.net;DefaultEndpointsProtocol=https;");
        BlobContainerClient container = sabmAzureStorageUtils.getAzureBlobContainer("recommendationengine");
        assertNull(container);
    }

    @Test
    public void validAzureCredentials()  {
        given(configuration.getString(SabmCoreConstants.AZURE_STORAGE_CONNECTION_STRING))
                .willReturn("AccountName=82vrymp29gbhc8kpbvodirc;AccountKey=fpdQKY5g5Weo0EcN4c+WoCgFrSPBBRQqG+aA9thYAgHsqew+hdbEQoB8K5zAGSFRxkVpceA2q1RrHkmaB7U8xg==;EndpointSuffix=core.windows.net;DefaultEndpointsProtocol=https;");
        BlobContainerClient container = sabmAzureStorageUtils.getAzureBlobContainer("recommendationengine");
        assertNotNull(container);
    }

}