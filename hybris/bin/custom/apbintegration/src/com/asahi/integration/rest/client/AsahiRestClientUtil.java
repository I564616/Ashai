package com.asahi.integration.rest.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.*;
import java.util.regex.Pattern;

import jakarta.annotation.Resource;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import com.apb.integration.data.AsahiDirectDebitResponse;
import com.apb.integration.data.AsahiDirectDebitResponseDTO;

import org.apache.commons.codec.binary.Base64;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.apb.integration.constants.ApbintegrationConstants;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;


/**
 * @author Pankaj.Gandhi
 *
 */
public class AsahiRestClientUtil
{

	@Resource
	private RestTemplate asahiRestTemplate;

	private static final Logger LOGGER = Logger.getLogger(AsahiRestClientUtil.class);

	private static final String SUCCESS_STATUS_REGEX = "2\\d{2}.*";
	private static final String BACKEND_NO_RESPONSE_MSG = "No response received from backend";
	private static final String REQUEST_SEND_FAILED_MSG = "Backend sent failed status in response";
	
	 private String plainCredentials;

	/**
	 * This method will be used to execute ajax post request
	 *
	 * @param urlPath
	 * @param requestEntity
	 * @param clazz
	 * @param config
	 * @return - rest response
	 */
	public Object executePOSTRequest(final Object requestEntity, final Class clazz, final Map<String, String> config)
	{
		Object response = null;
		final Gson gson = new Gson();

		final HttpHeaders headers = new HttpHeaders();
		if (plainCredentials != null) {
			headers.add("Authorization", "Basic " + getAuthorization(plainCredentials));
		}
		headers.setContentType(config.get(ApbintegrationConstants.REQUEST_CONTENT_TYPE).equalsIgnoreCase("json")
				? MediaType.APPLICATION_JSON : MediaType.APPLICATION_XML);
		final HttpEntity<Object> entity = new HttpEntity<Object>(requestEntity, headers);

		LOGGER.info("Request for Service URL : " + config.get("URL") + " is  : " + gson.toJson(requestEntity));

		final ResponseEntity<String> responseEntity = executeIntegrationRequest(entity, clazz, config);
		if (responseEntity != null)
		{
			LOGGER.info("Response status for " + config.get("URL") + " is  : " + responseEntity.getStatusCode().toString());
			response = responseEntity.getBody();
		}

		if(clazz.getName().equals(AsahiDirectDebitResponseDTO.class.getName())) {
			if (null == responseEntity) {
				LOGGER.error("No response sent from backend");
				final AsahiDirectDebitResponseDTO asahiDirectDebitResponseDTO = new AsahiDirectDebitResponseDTO();
				final AsahiDirectDebitResponse asahiDirectDebitResponse = new AsahiDirectDebitResponse();
				asahiDirectDebitResponse.setSuccess(false);
				asahiDirectDebitResponse.setReasonOfFailure(BACKEND_NO_RESPONSE_MSG);
				asahiDirectDebitResponseDTO.setDirectDebitResponse(asahiDirectDebitResponse);
				return asahiDirectDebitResponseDTO;
			} else if (!Pattern.matches(SUCCESS_STATUS_REGEX, responseEntity.getStatusCode().toString())) {
				LOGGER.error("Unsuccessful status code received from backend:: status code - " + responseEntity.getStatusCode().toString());
				final AsahiDirectDebitResponseDTO asahiDirectDebitResponseDTO = new AsahiDirectDebitResponseDTO();
				final AsahiDirectDebitResponse asahiDirectDebitResponse = new AsahiDirectDebitResponse();
				asahiDirectDebitResponse.setSuccess(false);
				asahiDirectDebitResponse.setReasonOfFailure(REQUEST_SEND_FAILED_MSG);
                asahiDirectDebitResponseDTO.setDirectDebitResponse(asahiDirectDebitResponse);
				return asahiDirectDebitResponseDTO;
			}
			else{
				LOGGER.info("Successful status code received from backend:: status code - " + responseEntity.getStatusCode().toString() + " <===> " + " Response body - " + gson.toJson(response));
				final AsahiDirectDebitResponseDTO asahiDirectDebitResponseDTO = new AsahiDirectDebitResponseDTO();
				final AsahiDirectDebitResponse asahiDirectDebitResponse = new AsahiDirectDebitResponse();
				asahiDirectDebitResponse.setSuccess(true);
				asahiDirectDebitResponse.setStatusCode(responseEntity.getStatusCode().toString());
                asahiDirectDebitResponseDTO.setDirectDebitResponse(asahiDirectDebitResponse);
				return asahiDirectDebitResponseDTO;
			}
		}

		LOGGER.info("Response from Service URL : " + config.get("URL") + " is  : " + gson.toJson(response));
		return response;
	}

	@SuppressWarnings("unchecked")
	private ResponseEntity<String> executeIntegrationRequest(final HttpEntity<Object> entity, final Class clazz,
			final Map<String, String> config)
	{
		ResponseEntity<String> responseEntity = null;
		FileInputStream fStream = null;
		try
		{
			//fStream = createIntegrationConfigTemplate(config);
			setHttpRequestFactory(config);
			
			responseEntity = asahiRestTemplate.exchange(config.get("URL"), HttpMethod.POST, entity, clazz);
			
			LOGGER.info("responseEntity value : " + responseEntity);
			
			//closeFIS(fStream);

		}
		catch (final Exception e)
		{
			//closeFIS(fStream);
			LOGGER.error("Exception in post request for url : " + config.get("URL"), e);
		}

		return responseEntity;
	}
	
	
	
	private void closeFIS(final FileInputStream fStream)
	{
		try
		{
			if (null != fStream)
			{
				fStream.close();
			}
		}
		catch (final IOException e)
		{
			LOGGER.error("Unable to close Input Stream", e);
		}
	}

	/**
	 * <p>
	 * This method is used to make order rest request and will return whole responseEntity Object
	 * </p>
	 *
	 * @param urlPath
	 * @param requestEntity
	 * @param clazz
	 * @param config
	 * @return - rest response
	 */
	public void executeRestOrderRequest(final Object requestEntity, final Class clazz, final Map<String, String> config)
	{
		final HttpHeaders headers = new HttpHeaders();
		if (plainCredentials != null) {
			headers.add("Authorization", "Basic " + getAuthorization(plainCredentials));
		}
		headers.setContentType(config.get(ApbintegrationConstants.REQUEST_CONTENT_TYPE).equalsIgnoreCase("json")
				? MediaType.APPLICATION_JSON : MediaType.APPLICATION_XML);
		final HttpEntity<Object> entity = new HttpEntity<Object>(requestEntity, headers);
		executeOrderIntegrationRequest(entity, clazz, config);

	}

	@SuppressWarnings("unchecked")
	private void executeOrderIntegrationRequest(final HttpEntity<Object> entity, final Class clazz,
			final Map<String, String> config)
	{
		FileInputStream fStream = null;
		try
		{
			//fStream = createIntegrationConfigTemplate(config);
			setHttpRequestFactory(config);
			asahiRestTemplate.exchange(config.get("URL"), HttpMethod.POST, entity, clazz);
			//closeFIS(fStream);

		}
		catch (final Exception e)
		{
			//closeFIS(fStream);
			LOGGER.error("Exception in post request for url : " + config.get("URL"), e);
		}

	}
	
	private void setHttpRequestFactory(final Map<String, String> config) throws IOException, NoSuchAlgorithmException, CertificateException, KeyManagementException {
		
		final SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null, null, new SecureRandom());

		final SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext);
		final HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
				.setSSLSocketFactory(sslSocketFactory)
				.build();
		final HttpClient httpClient = HttpClients.custom()
				.setConnectionManager(cm)
				.build();

		final HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory(
				httpClient);
		final String strConnTimeout = config.get(ApbintegrationConstants.CONNECTION_TIMEOUT);
		final String strReadTimeout = config.get(ApbintegrationConstants.CONNECTION_READ_TIMEOUT);
		final String strConnReqTimeout = config.get(ApbintegrationConstants.CONNECTION_REQUEST_TIMEOUT);
		httpRequestFactory.setConnectTimeout(Integer.parseInt(strConnTimeout));
		// Manual migration to `SocketConfig.Builder.setSoTimeout(Timeout)` necessary; see: https://docs.spring.io/spring-framework/docs/6.0.0/javadoc-api/org/springframework/http/client/HttpComponentsClientHttpRequestFactory.html#setReadTimeout(int)
		httpRequestFactory.setReadTimeout(Integer.parseInt(strReadTimeout));
		httpRequestFactory.setConnectionRequestTimeout(Integer.parseInt(strConnReqTimeout));
		asahiRestTemplate.setRequestFactory(httpRequestFactory);

		if ("json".equalsIgnoreCase(config.get(ApbintegrationConstants.REQUEST_CONTENT_TYPE))) {
			final ObjectMapper mapper = new ObjectMapper();
			List<MediaType> mediaTypes = new ArrayList<>();
			mediaTypes.add(MediaType.APPLICATION_JSON);

			final MappingJackson2HttpMessageConverter convertor = new MappingJackson2HttpMessageConverter();
			convertor.setSupportedMediaTypes(mediaTypes);

			convertor.setObjectMapper(mapper);
			mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
			mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
			mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
			asahiRestTemplate.setMessageConverters(Collections.singletonList(convertor));
		} else {
			final Jaxb2RootElementHttpMessageConverter convertor = new Jaxb2RootElementHttpMessageConverter();
			asahiRestTemplate.setMessageConverters(Collections.singletonList(convertor));
		}

	}

	private FileInputStream createIntegrationConfigTemplate(final Map<String, String> config)
			throws KeyStoreException, FileNotFoundException, IOException, NoSuchAlgorithmException,
			CertificateException, UnrecoverableKeyException, KeyManagementException {
		FileInputStream fStream;
		final String strConnTimeout = config.get(ApbintegrationConstants.CONNECTION_TIMEOUT);
		final String strReadTimeout = config.get(ApbintegrationConstants.CONNECTION_READ_TIMEOUT);
		final String strConnReqTimeout = config.get(ApbintegrationConstants.CONNECTION_REQUEST_TIMEOUT);
		final String clientStorePassword = config.get(ApbintegrationConstants.CLIENT_STORE_PASSWORD);
		final String trustStorePassword = config.get(ApbintegrationConstants.TRUST_STORE_PASSWORD);
		final String clientStoreFile = config.get(ApbintegrationConstants.CLIENT_STORE_FILE);
		// final String trustStoreFile =
		// config.get(ApbintegrationConstants.TRUST_STORE_FILE);
		final String filePath = config.get(ApbintegrationConstants.CERTIFICATE_FILEPATH);

		final File dir = new File(filePath);
		final File file = new File(dir, clientStoreFile);
		// final File file1 = new File(dir, trustStoreFile);

		final KeyStore clientStore = KeyStore.getInstance("PKCS12");
		fStream = new FileInputStream(file);
		clientStore.load(fStream, clientStorePassword.toCharArray());

		final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(clientStore, clientStorePassword.toCharArray());
		final KeyManager[] kms = kmf.getKeyManagers();

//    final KeyStore trustStore = KeyStore.getInstance("JKS");
//    fStream = new FileInputStream(file1);
//    trustStore.load(fStream, trustStorePassword.toCharArray());

//    final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//    tmf.init(trustStore);
//    final TrustManager[] tms = tmf.getTrustManagers();

		final SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(kms, null, new SecureRandom());

		final SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext);
		final HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
				.setSSLSocketFactory(sslSocketFactory)
				.build();
		final HttpClient httpClient = HttpClients.custom()
				.setConnectionManager(cm)
				.build();

		final HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory(
				httpClient);
		httpRequestFactory.setConnectTimeout(Integer.parseInt(strConnTimeout));
		// Manual migration to `SocketConfig.Builder.setSoTimeout(Timeout)` necessary; see: https://docs.spring.io/spring-framework/docs/6.0.0/javadoc-api/org/springframework/http/client/HttpComponentsClientHttpRequestFactory.html#setReadTimeout(int)
		httpRequestFactory.setReadTimeout(Integer.parseInt(strReadTimeout));
		httpRequestFactory.setConnectionRequestTimeout(Integer.parseInt(strConnReqTimeout));
		asahiRestTemplate.setRequestFactory(httpRequestFactory);

		if ("json".equalsIgnoreCase(config.get(ApbintegrationConstants.REQUEST_CONTENT_TYPE))) {
			final ObjectMapper mapper = new ObjectMapper();
			List<MediaType> mediaTypes = new ArrayList<>();
			mediaTypes.add(MediaType.APPLICATION_JSON);

			final MappingJackson2HttpMessageConverter convertor = new MappingJackson2HttpMessageConverter();
			convertor.setSupportedMediaTypes(mediaTypes);

			convertor.setObjectMapper(mapper);
			mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
			mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
			mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
			asahiRestTemplate.setMessageConverters(Collections.singletonList(convertor));
		} else {
			final Jaxb2RootElementHttpMessageConverter convertor = new Jaxb2RootElementHttpMessageConverter();
			asahiRestTemplate.setMessageConverters(Collections.singletonList(convertor));
		}
		return fStream;
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
