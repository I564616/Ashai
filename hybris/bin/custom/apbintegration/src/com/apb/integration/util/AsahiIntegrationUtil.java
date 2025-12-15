package com.apb.integration.util;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;

import com.apb.integration.constants.ApbintegrationConstants;
import com.apb.integration.service.config.AsahiConfigurationService;


/**
 *
 */
public class AsahiIntegrationUtil
{
	@Resource(name = "asahiIconfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	/**
	 * This method will return the map with configured values related to any particular API key passed as parameter e.g.
	 * API key passed as <b>integration.login.customer.account</b> configuration keys would be
	 * <b>integration.login.customer.account.connection.timeout.sga</b>
	 *
	 * @param apiKey
	 * @param currentSite
	 * @return configuration map
	 */
	public Map<String, String> getAPIConfiguration(final String apiKey, final String currentSite)
	{
		final Map<String, String> config = new HashMap<>();
		config.put(ApbintegrationConstants.URL,
				asahiConfigurationService.getString(apiKey + ".url." + currentSite, StringUtils.EMPTY));
		config.put(ApbintegrationConstants.CONNECTION_TIMEOUT,
				asahiConfigurationService.getString(apiKey + ".connection.timeout." + currentSite, "2000"));
		config.put(ApbintegrationConstants.CONNECTION_REQUEST_TIMEOUT,
				asahiConfigurationService.getString(apiKey + ".connection.request.timeout." + currentSite, "2000"));
		config.put(ApbintegrationConstants.CONNECTION_READ_TIMEOUT,
				asahiConfigurationService.getString(apiKey + ".connection.read.timeout." + currentSite, "2000"));
		config.put(ApbintegrationConstants.REQUEST_CONTENT_TYPE, "xml");
		config.putAll(addRestAPICommonConfig(currentSite));
		return config;
	}

	/**
	 * This method will add API common configurations in the map
	 *
	 * @return map
	 */
	public Map<String, String> addRestAPICommonConfig(final String site)
	{
		final Map<String, String> config = new HashMap<>();
		config.put(ApbintegrationConstants.CLIENT_STORE_PASSWORD,
				asahiConfigurationService.getString("integration.certificate.clientStore.password." + site, "1649"));
		config.put(ApbintegrationConstants.TRUST_STORE_PASSWORD,
				asahiConfigurationService.getString("integration.certificate.trustStore.password." + site, "changeit"));
		config.put(ApbintegrationConstants.CLIENT_STORE_FILE,
				asahiConfigurationService.getString("integration.certificate.clientStore.filename." + site, "hybrisuser.p12"));
		config.put(ApbintegrationConstants.TRUST_STORE_FILE,
				asahiConfigurationService.getString("integration.certificate.trustStore.filename." + site, "keystore_Asahi.jks"));
		config.put(ApbintegrationConstants.CERTIFICATE_FILEPATH,
				asahiConfigurationService.getString("integration.certificate.filePath." + site, "C:/AsahiB2b"));
		return config;
	}
}
