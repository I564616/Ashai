package com.apb.integration.rest.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Collections;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.apache.commons.codec.binary.Base64;

import com.apb.integration.service.config.AsahiConfigurationService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

public class AsahiRestClientImpl implements AsahiRestClient {

	@Resource(name = "asahiRestTemplate")
	private RestTemplate asahiRestTemplate;
	@Resource(name = "asahiIconfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	private HttpClient httpClient;
	private HttpComponentsClientHttpRequestFactory httpRequestFactory;
	private String plainCredentials;

	public static final String INTEGRATION_SERVICE_CONN_TIME_OUT = "integration.service.connection.timeout.apb";
	public static final String INTEGRATION_SERVICE_READ_TIME_OUT = "integration.service.read.timeout.apb";
	public static final String INTEGRATION_SERVICE_CONN_REQUEST_TIME_OUT = "integration.service.connection.request.timeout.apb";
	public static final String INTEGRATION_SERVICE_CONN_MAX_CONNECTION_COUNT = "integration.service.connection.max.connection.count.apb";
	public static final String IINTEGRATION_SERVICE_CONN_MAX_CONNECTION_PER_ROUTE_COUNT = "integration.service.connection.max.connection.per.route.count.apb";	
	public static final String INTEGRATION_CERTIFICATE_CLIENT_STORE_PASSWORD = "integration.certificate.clientStore.password.apb";
	public static final String INTEGRATION_CERTIFICATE_TRUST_STORE_PASSWORD = "integration.certificate.trustStore.password.apb";
	public static final String INTEGRATION_CERTIFICATE_CLIENT_STORE_FILE_NAME = "integration.certificate.clientStore.fileName.apb";
	public static final String INTEGRATION_CERTIFICATE_TRUST_STORE_FILE_NAME = "integration.certificate.trustStore.fileName.apb";
	public static final String INTEGRATION_CERTIFICATE_FILE_PATH = "integration.certificate.filePath.apb";
	private static final Logger LOGGER = LoggerFactory.getLogger(AsahiRestClientImpl.class);


	public void setHttpClientAndRequestFactory() {

		//String clientStorePassword = asahiConfigurationService.getDecryptedPassword(INTEGRATION_CERTIFICATE_CLIENT_STORE_PASSWORD,
		//		"");
		//String trustStorePassword = asahiConfigurationService.getDecryptedPassword(INTEGRATION_CERTIFICATE_TRUST_STORE_PASSWORD,
		//		"");
		
		//String clientStorePassword = asahiConfigurationService.getString(INTEGRATION_CERTIFICATE_CLIENT_STORE_PASSWORD, "Asah!123");
		/*String trustStorePassword = asahiConfigurationService.getString(INTEGRATION_CERTIFICATE_TRUST_STORE_PASSWORD, "Asah!123");
				
		String clientStoreFile = asahiConfigurationService.getString(INTEGRATION_CERTIFICATE_CLIENT_STORE_FILE_NAME,
				"hybrisuser.p12");
//		String trustStoreFile = asahiConfigurationService.getString(INTEGRATION_CERTIFICATE_TRUST_STORE_FILE_NAME,
//				"keystore_Asahi.jks");
		String filePath = asahiConfigurationService.getString(INTEGRATION_CERTIFICATE_FILE_PATH,
				"");*/
			//	File dir = new File(filePath);
		//File file = new File(dir, clientStoreFile);
		//File file1 = new File(dir, trustStoreFile);

		try
		{	
			//	FileInputStream clientStoreInputStream = new FileInputStream(file);
			// FileInputStream	trustStoreInputStream = new FileInputStream(file1);
			/*KeyStore clientStore = KeyStore.getInstance("PKCS12");

			clientStore.load(clientStoreInputStream, clientStorePassword.toCharArray());

			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(clientStore, clientStorePassword.toCharArray());
			KeyManager[] kms = kmf.getKeyManagers();
*/
//			KeyStore trustStore = KeyStore.getInstance("JKS");
//
//			trustStore.load(trustStoreInputStream, trustStorePassword.toCharArray());

//			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//			tmf.init(trustStore);
//			TrustManager[] tms = tmf.getTrustManagers();

			final SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, null, new SecureRandom());

			final String maxConnCount = asahiConfigurationService.getString(INTEGRATION_SERVICE_CONN_MAX_CONNECTION_COUNT, "200");
			final String maxConnPerRoute = asahiConfigurationService.getString(IINTEGRATION_SERVICE_CONN_MAX_CONNECTION_PER_ROUTE_COUNT, "50");

			final SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext);
			final HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
					.setSSLSocketFactory(sslSocketFactory)
					.setMaxConnTotal(Integer.parseInt(maxConnCount))
					//.setDefaultMaxPerRoute(Integer.parseInt(maxConnPerRoute))
					.build();

			httpClient = HttpClients.custom()
					.setConnectionManager(cm)
					.build();

			httpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

		} catch (Exception e) {
			LOGGER.error("exception in Initialization of HttpClient and HttpRequestFactory  .", e);
		} finally {
			LOGGER.info("Finally HttpClient and HttpRequestFactory Initialized ");
		}

	}

	@Override
	public ResponseEntity<String> executeOrderAXRestRequest(String serviceUrl, Object requestEntity, Class clazz,
			String requestType) {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_XML);
		if (plainCredentials != null) {
			headers.add("Authorization", "Basic " + getAuthorization(plainCredentials));
		}
		ResponseEntity<String> responseEntity = executeIntegrationRequest(serviceUrl, requestEntity, clazz, headers,
				"xml");
		return responseEntity;
	}

	@Override
	public Object executePOSTRestRequest(String urlPath, Object requestEntity, Class clazz, String requestType) {
		Object response = null;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if (plainCredentials != null) {
			headers.add("Authorization", "Basic " + getAuthorization(plainCredentials));
		}
		ResponseEntity<String> responseEntity = executeIntegrationRequest(urlPath, requestEntity, clazz, headers,
				"json");
		if (responseEntity != null) {
			response = responseEntity.getBody();
		}
		return response;
	}

	private ResponseEntity<String> executeIntegrationRequest(String urlPath, Object requestEntity, Class clazz,
			HttpHeaders headers, String requestType) {
		ResponseEntity<String> responseEntity = null;
		HttpEntity<Object> entity = new HttpEntity<Object>(requestEntity, headers);
		String strConnTimeout = asahiConfigurationService.getString(INTEGRATION_SERVICE_CONN_TIME_OUT, "5000");
		String strReadTimeout = asahiConfigurationService.getString(INTEGRATION_SERVICE_READ_TIME_OUT, "5000");
		String strConnReqTimeout = asahiConfigurationService.getString(INTEGRATION_SERVICE_CONN_REQUEST_TIME_OUT,
				"5000");

		
		
		if (httpRequestFactory == null) {
			// initializing httprequestfactory if it is null otherwise using the same
			// instance
			LOGGER.info("Asahi HTTPRequestFactory IS Null:"+httpRequestFactory);
			setHttpClientAndRequestFactory();
		}

		httpRequestFactory.setConnectTimeout(Integer.parseInt(strConnTimeout));
		// Manual migration to `SocketConfig.Builder.setSoTimeout(Timeout)` necessary; see: https://docs.spring.io/spring-framework/docs/6.0.0/javadoc-api/org/springframework/http/client/HttpComponentsClientHttpRequestFactory.html#setReadTimeout(int)
		httpRequestFactory.setReadTimeout(Integer.parseInt(strReadTimeout));
		httpRequestFactory.setConnectionRequestTimeout(Integer.parseInt(strConnReqTimeout));
		
		asahiRestTemplate.setRequestFactory(httpRequestFactory);

		if ("json".equalsIgnoreCase(requestType)) {
			ObjectMapper mapper = new ObjectMapper();
			MappingJackson2HttpMessageConverter convertor = new MappingJackson2HttpMessageConverter();
			convertor.setObjectMapper(mapper);
			mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
			mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			asahiRestTemplate.setMessageConverters(Collections.singletonList(convertor));
		} else {
			Jaxb2RootElementHttpMessageConverter convertor = new Jaxb2RootElementHttpMessageConverter();
			asahiRestTemplate.setMessageConverters(Collections.singletonList(convertor));
		}
		try {
			final Gson gson = new Gson();
			LOGGER.info("Asahi HTTPRequestFactory:"+httpRequestFactory.getHttpClient().toString());
			LOGGER.info("Asahi HTTP Entity:"+entity);
			LOGGER.info("Asahi HTTP Entity Headers:"+entity.getHeaders().toString());
			responseEntity = asahiRestTemplate.exchange(urlPath, HttpMethod.POST, entity, clazz);

		} catch (Exception e) {
			LOGGER.error("Exception in Rest call ", e);
		}

		return responseEntity;
	}

	private void closeFIS(FileInputStream fStream) {
		try {
			if (null != fStream) {
				fStream.close();
			}
		} catch (IOException e) {
			LOGGER.error("Unable to close Input Stream", e);
		}
	}

	public void setAsahiRestTemplate(RestTemplate asahiRestTemplate) {
		this.asahiRestTemplate = asahiRestTemplate;
	}

	public void setAsahiConfigurationService(AsahiConfigurationService asahiConfigurationService) {
		this.asahiConfigurationService = asahiConfigurationService;
	}
	
    public String getAuthorization(final String plainCredentials) {

        final byte[] plainCredsBytes = plainCredentials.getBytes();

        final byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);

        return new String(base64CredsBytes);
    }

    /**
     * @param plainCredentials the plainCredentials to set
     */
    public void setPlainCredentials(final String plainCredentials) {
        this.plainCredentials = plainCredentials;
    }

}
