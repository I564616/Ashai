package com.sabmiller.sfmc.client;

import de.hybris.platform.servicelayer.config.ConfigurationService;

import jakarta.annotation.Resource;
import com.exacttarget.fuelsdk.ETClient;
import com.exacttarget.fuelsdk.ETConfiguration;
import com.exacttarget.fuelsdk.ETSdkException;

/**
 * SFMC Client for SABM using ETClient
 *
 * Setting up configuration from
 * @see "project.properties"
 * @see com.exacttarget.fuelsdk.ETClient
 */
public class SabmSFMCClient {

    private ETConfiguration etConfiguration;

    private static final String SFMC_CLIENT_ID="sabm.sfmc.client.id";

    private static final String SFMC_CLIENT_SECRET="sabm.sfmc.client.secret";

    private static final String DEFAULT_CLIENT_ID="33xp2749gsk90pyb3mvj6fxp";

    private static final String DEFAULT_CLIENT_SECRET="Kli3bEXRkUSLxwKsuTvedlTO";

    @Resource
    ConfigurationService configurationService;

    /**
     * Method to set default client id and client secret
     */
    private void setupConfiguration()
    {
        etConfiguration = new ETConfiguration();
        etConfiguration.setClientId(this.configurationService.getConfiguration().getString(SFMC_CLIENT_ID,DEFAULT_CLIENT_ID));
        etConfiguration.setClientSecret(this.configurationService.getConfiguration().getString(SFMC_CLIENT_SECRET,DEFAULT_CLIENT_SECRET));
    }

    /**
     * Method to get default ET Client
     *
     * @return
     * @throws ETSdkException
     */
    public ETClient getETClient() throws ETSdkException {
        setupConfiguration();
        return new ETClient(etConfiguration);
    }
}
